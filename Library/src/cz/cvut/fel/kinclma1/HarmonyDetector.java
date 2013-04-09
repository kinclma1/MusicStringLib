package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.scales.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Helper class guessing the scales used in a song
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
        private MusicStringDuration shortestNote;

        public TrackNoteSplitter(MusicStringTrack track, MusicStringDuration shortestNote) {
            this.track = track;
            this.shortestNote = shortestNote;
            flatTrack = new FlatTrack(shortestNote);
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
            int durationCoef;

            beats = measure.getBeats().iterator();

            //while some remaining short notes to write
            while (beats.hasNext()) {
                MusicStringBeat beat = beats.next();
                durationCoef = beat.getDurationDiv128() / shortestNote.toIntegerDiv128();
                HashSet<String> toneSet = beat.getToneSet();
                while (durationCoef > 0) {
                    flatTrack.addToneSet(new HashSet<String>(toneSet));
                    durationCoef--;
                }
            }
        }
    }

    private ArrayList<MusicStringTrack> toneTracks;
    private MusicStringDuration shortestNote;
    private int trackCount;
    private ExecutorService exec;
    private FlatTrack mergedTracks;
    private final Scale[] scales = new Scale[]{
            new MixolydianScale(),
            new LydianScale(),
            new IonianScale(),
            new DorianScale(),
            new PhrygianScale(),
            new AeolianScale(),
            new LocrianScale()};

    HarmonyDetector(MusicStringSong song) {
        exec = Parallellization.executorService();
        toneTracks = getToneTracks(song);
        shortestNote = getShortestNote();
        trackCount = toneTracks.size();
        mergedTracks = null;
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

    private List<FlatTrack> getSplitTracks() {
        ArrayList<Callable<FlatTrack>> splitters = new ArrayList<Callable<FlatTrack>>(trackCount);
        for (MusicStringTrack track : toneTracks) {
            splitters.add(new TrackNoteSplitter(track, shortestNote));
        }
        return Parallellization.runExecutor(exec, splitters);
    }

    /**
     * Returns a flat track with all playable notes to accompany the song processed by the tone filter
     * @param toneFilter Filter to determine which tones can be played using the instrument
     * @return Flat track with all playable notes to accompany the song processed by the tone filter
     */
    FlatTrack detectHarmony(InstrumentTones toneFilter) {
        FlatTrack merged = mergeTracks(getSplitTracks());
        exec.shutdown();
        mergedTracks = merged.clone();
        return toneFilter.filterTones(guessScales(merged));
    }

    /**
     * Returns a flat track with all tones played in the song processed by the tone filter
     * @param toneFilter Filter to determine which tones can be played using the instrument
     * @return Flat track with all tones played in the song processed by the tone filter
     */
    FlatTrack originalTones(InstrumentTones toneFilter) {
        return toneFilter.filterTones(mergedTracks);
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

    private FlatTrack guessScales(FlatTrack track) {
        Iterator<HashSet<String>> iterator = track.getIterator();
        while (iterator.hasNext()) {
            guessScale(iterator.next());
        }
        return track;
    }

    private void guessScale(HashSet<String> beat) {
        if (beat.size() > 2) {
            Set<String> scale = null;
            for (int i = 0; i < scales.length && scale == null; i ++) {
                scale = scales[i].getMatch(beat);
            }
            if (scale != null) {
                beat.addAll(scale);
            }
        }
    }
}
