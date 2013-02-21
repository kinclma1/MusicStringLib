package cz.cvut.fel.kinclma1;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 19.1.13
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
public class HarmonyDetector {

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

    private static class TrackNoteSplitter implements Callable<FlatTrack> {

        private MusicStringTrack track;
        private FlatTrack flatTrack;
        private MusicStringDuration shortestNote;

        public TrackNoteSplitter(MusicStringTrack track, MusicStringDuration shortestNote) {
            this.track = track;
            this.shortestNote = shortestNote;
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
    private ExecutorService exec;

    public HarmonyDetector(MusicStringSong song) {
        exec = Parallellization.executorService();
        this.toneTracks = getToneTracks(song);
        this.shortestNote = getShortestNote();
        this.trackCount = toneTracks.size();
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
        ArrayList<Callable<MusicStringDuration>> finders = new ArrayList<Callable<MusicStringDuration>>(trackCount);
        for (MusicStringTrack track : toneTracks) {
            finders.add(new TrackShortestNoteFinder(track));
        }
        List<MusicStringDuration> results = Parallellization.runExecutor(exec, finders);

        return Collections.min(results);
    }

    public static FlatTrack splitTrackToShortest(MusicStringTrack track) {
        try {
            return new TrackNoteSplitter(track, track.getShortestNote()).call();
        } catch (Exception e) {
            return null;
        }
    }

    List<FlatTrack> getSplitTracks() {
        ArrayList<Callable<FlatTrack>> splitters = new ArrayList<Callable<FlatTrack>>(trackCount);
        for (MusicStringTrack track : toneTracks) {
            splitters.add(new TrackNoteSplitter(track, shortestNote));
        }
        return Parallellization.runExecutor(exec, splitters);
    }

    FlatTrack detectHarmony() {
        List<FlatTrack> newTracks = getSplitTracks();
        exec.shutdown();
        FlatTrack newTrack = mergeTracks(newTracks);

        //todo instrument defined notes -- not necessary

        return newTrack;

    }

    private FlatTrack mergeTracks(List<FlatTrack> newTracks) {
        FlatTrack track = new FlatTrack(shortestNote);
        ArrayList<Iterator<HashSet<String>>> iterators = new ArrayList<Iterator<HashSet<String>>>(trackCount);
        for (FlatTrack flatTrack : newTracks) {
            iterators.add(flatTrack.getIterator());
        }
        HashSet<String> currentBeat;
        while (iterators.get(0).hasNext()) {
            currentBeat = new HashSet<String>();
            for (Iterator<HashSet<String>> iterator : iterators) {
                currentBeat.addAll(iterator.next());
            }
            if (currentBeat.size() > 1 && currentBeat.contains("R")) {
                currentBeat.remove("R");
            }
            track.addToneSet(currentBeat);
        }

        return track;
    }
}
