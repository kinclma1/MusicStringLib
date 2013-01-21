package cz.cvut.fel.kinclma1;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 19.1.13
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
class HarmonyDetector {

    private class TrackShortestNoteFinder implements Callable<MusicStringDuration> {

        private MusicStringTrack track;

        public TrackShortestNoteFinder(MusicStringTrack track) {
            this.track = track;
        }

        @Override
        public MusicStringDuration call() throws Exception {
            return track.getShortestNote();
        }
    }

    private class TrackNoteSplitter implements Callable<FlatTrack> {

        private MusicStringTrack track;
        private FlatTrack flatTrack;

        public TrackNoteSplitter(MusicStringTrack track) {
            this.track = track;
            this.flatTrack = new FlatTrack(shortestNote);
        }

        @Override
        public FlatTrack call() throws Exception {
            Iterator<MusicStringMeasure> measureIterator = track.getMeasures().iterator();

            while (measureIterator.hasNext()) {
                processMeasure(measureIterator.next());
                if (measureIterator.hasNext()) {
                    flatTrack.addMeasureDelimiter();
                }
            }

            return flatTrack;
        }

        private void processMeasure(MusicStringMeasure measure) {
            Iterator<MusicStringBeat> beats;
            int durationCoef = 0;

            beats = measure.getBeats().iterator();

            //while some remaining short notes to write
            while (beats.hasNext()) {
                MusicStringBeat beat = beats.next();
                durationCoef = beat.getDurationDiv128() / shortestNote.toIntegerDiv128();
                HashSet<String> toneSet = beat.getToneSet();
                while (durationCoef > 0) {
                    flatTrack.addToneSet(new HashSet<String>(toneSet));
                    durationCoef --;
                }
            }
        }
    }

    private ArrayList<MusicStringTrack> toneTracks;
    private MusicStringDuration shortestNote;
    private int trackCount;
    private ArrayList<FlatTrack> newTracks;
    private StringBuilder trackString;
    private ExecutorService exec;

    HarmonyDetector(MusicStringSong song) {
        exec = Parallellization.executorService();
        this.toneTracks = getToneTracks(song);
        this.shortestNote = getShortestNote();
        this.trackCount = toneTracks.size();
        trackString = new StringBuilder();
        newTracks = new ArrayList<FlatTrack>(trackCount);
    }

    private ArrayList<MusicStringTrack> getToneTracks(MusicStringSong song) {
        ArrayList<MusicStringTrack> toneTracks = new ArrayList<MusicStringTrack>(song.getTrackIds().size());
        for (String trackId : song.getTrackIds()) {
            if (!trackId.contains("V9")) {
                toneTracks.add(song.getTrack(trackId));
            }
        }
        return toneTracks;
    }

    private MusicStringDuration getShortestNote() {
        MusicStringDuration shortest = new MusicStringDuration(Duration.WHOLE.toString());
        ArrayList<TrackShortestNoteFinder> finders = new ArrayList<TrackShortestNoteFinder>(trackCount);
        for (MusicStringTrack track : toneTracks) {
            finders.add(new TrackShortestNoteFinder(track));
        }
        try {
            List<Future<MusicStringDuration>> results = exec.invokeAll(finders);
            for (Future<MusicStringDuration> result : results) {
                MusicStringDuration trackShortest = result.get();
                if (trackShortest.toInteger() > shortest.toInteger()) {
                    shortest = trackShortest;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return shortest;
    }

    FlatTrack detectHarmony() {
        ArrayList<TrackNoteSplitter> splitters = new ArrayList<TrackNoteSplitter>(trackCount);
        for (MusicStringTrack track : toneTracks) {
            splitters.add(new TrackNoteSplitter(track));
        }
        try {
            List<Future<FlatTrack>> results = exec.invokeAll(splitters);
            for (Future<FlatTrack> result : results) {
                newTracks.add(result.get());
            }
            exec.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //todo merge newTracks to a single FlatTrack
        //todo merge newTracks to stringbuilder
        //todo in musicstringtrack setdefaultoctave if none
        //todo instrument defined notes -- not necessary

        return null;

    }
}
