package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGMeasure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * A state machine used to unroll repetitions as music string does not support them
 */
class RepetitionTracker {

    private MusicStringTrack track;

    private LinkedList<TGMeasure> repeatBeginning;
    private ArrayList<LinkedList<TGMeasure>> repeatAlternatives;
    private HashSet<Integer> alternativeIndexes;
    private int state;
    private int repetitionAlt;
    private int firstAlt;

    //States
    private final int REPETITION_CLOSED = 0;
    private final int REPETITION_OPEN = 1;
    private final int REPETITION_ALT = 2;
    private final int WAITING_FOR_ALT = 3;

    //Measure types
    private final int MEASURE_ORDINARY = 0;
    private final int MEASURE_REPEAT_OPEN = 1;
    private final int MEASURE_REPEAT_CLOSE = 2;
    private final int MEASURE_REPEAT_ALT = 3;
    private final int MEASURE_REP_ALT_CLOSE = 4;
    private final int MEASURE_REP_OPEN_CLOSE = 5;

    /**
     * Creates a repetition tracker for the given track
     * @param track to unroll repetitions to
     */
    RepetitionTracker(MusicStringTrack track) {
        this.track = track;
        repeatBeginning = new LinkedList<TGMeasure>();
        repeatAlternatives = new ArrayList<LinkedList<TGMeasure>>(8);
        for (int i = 0; i < 8; i ++) {
            repeatAlternatives.add(new LinkedList<TGMeasure>());
        }
        alternativeIndexes = new HashSet<Integer>();
        state = REPETITION_OPEN;
        firstAlt = 0;
    }

    /**
     * Handles the given measure
     * @param measure to be handled
     */
    void processMeasure(TGMeasure measure) {
        int measureType = getMeasureType(measure);
        boolean lastMeasure = measure.getTrack().getMeasure(measure.getTrack().countMeasures() - 1) == measure;
        if (state == REPETITION_CLOSED) {
            if (measureType == MEASURE_REPEAT_OPEN) {
                state = REPETITION_OPEN;
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
            } else if (measureType == MEASURE_REP_OPEN_CLOSE) {
                state = REPETITION_CLOSED;
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
                addBeginningNTimes(measure.getRepeatClose());
            } else {
                state = REPETITION_CLOSED;
                track.addMeasure(measure);
            }
        } else if (state == REPETITION_OPEN) {
            if (measureType == MEASURE_ORDINARY) {
                state = REPETITION_OPEN;
                track.addMeasure(measure);
                addBeginning(measure);
            } else if (measureType == MEASURE_REPEAT_OPEN) {
                state = REPETITION_OPEN;
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
            } else if (measureType == MEASURE_REPEAT_CLOSE) {
                state = REPETITION_CLOSED;
                track.addMeasure(measure);
                addBeginning(measure);
                addBeginningNTimes(measure.getRepeatClose());
            } else if (measureType == MEASURE_REPEAT_ALT) {
                state = REPETITION_ALT;
                addToAlternatives(measure);
            } else if (measureType == MEASURE_REP_ALT_CLOSE) {
                state = WAITING_FOR_ALT;
                addToAlternatives(measure);
                addAlternativeAndBeginning();
            } else if (measureType == MEASURE_REP_OPEN_CLOSE) {
                state = REPETITION_CLOSED;
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
                addBeginningNTimes(measure.getRepeatClose());
            }
        } else if (state == REPETITION_ALT) {
            if (measureType == MEASURE_ORDINARY) {
                state = REPETITION_ALT;
                addToAlternatives(measure);
                if (lastMeasure) {
                    addAlternative();
                }
            } else if (measureType == MEASURE_REPEAT_OPEN) {
                state = REPETITION_OPEN;
                addAlternative();
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
            } else if (measureType == MEASURE_REPEAT_CLOSE) {
                state = WAITING_FOR_ALT;
                addToAlternatives(measure);
                addAlternativeAndBeginning();
            } else if (measureType == MEASURE_REPEAT_ALT) {
                state = REPETITION_CLOSED;
                addAlternative();
                track.addMeasure(measure);
            } else if (measureType == MEASURE_REP_ALT_CLOSE) {
                state = REPETITION_CLOSED;
                addAlternativeAndBeginning();
                track.addMeasure(measure);
            } else if (measureType == MEASURE_REP_OPEN_CLOSE) {
                state = REPETITION_CLOSED;
                addAlternative();
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
                addBeginningNTimes(measure.getRepeatClose());
            }
        } else if (state == WAITING_FOR_ALT) {
            if (measureType == MEASURE_ORDINARY) {
                state = REPETITION_CLOSED;
                addAlternativeAndBeginning();
                track.addMeasure(measure);
            } else if (measureType == MEASURE_REPEAT_OPEN) {
                state = REPETITION_OPEN;
                addAlternativeAndBeginning();
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
            } else if (measureType == MEASURE_REPEAT_CLOSE) {
                state = REPETITION_CLOSED;
                addAlternativeAndBeginning();
                track.addMeasure(measure);
            } else if (measureType == MEASURE_REPEAT_ALT) {
                state = REPETITION_ALT;
                addToAlternatives(measure);
            } else if (measureType == MEASURE_REP_ALT_CLOSE) {
                state = WAITING_FOR_ALT;
                addToAlternatives(measure);
                addAlternativeAndBeginning();
            } else if (measureType == MEASURE_REP_OPEN_CLOSE) {
                state = REPETITION_CLOSED;
                addAlternativeAndBeginning();
                track.addMeasure(measure);
                clear();
                addBeginning(measure);
                addBeginningNTimes(measure.getRepeatClose());
            }
        }

    }

    private int getMeasureType(TGMeasure measure) {
        if (measure.isRepeatOpen()) {
            if (measure.getRepeatClose() > 0) {
                return MEASURE_REP_OPEN_CLOSE;
            } else {
                return MEASURE_REPEAT_OPEN;
            }
        }

        if (measure.getHeader().getRepeatAlternative() > 0) {
            if (measure.getRepeatClose() > 0) {
                return MEASURE_REP_ALT_CLOSE;
            } else {
                return MEASURE_REPEAT_ALT;
            }
        }

        if (measure.getRepeatClose() > 0) {
            return MEASURE_REPEAT_CLOSE;
        }

        return MEASURE_ORDINARY;
    }

    private void clear() {
        repeatBeginning.clear();
        for (int i = 0; i < repeatAlternatives.size(); i ++) {
            repeatAlternatives.get(i).clear();
        }
        alternativeIndexes.clear();
        firstAlt = 0;
    }

    private void addBeginning(TGMeasure measure) {
        repeatBeginning.add(measure);
    }

    private void addToAlternatives(TGMeasure measure) {
        int repAlt = measure.getHeader().getRepeatAlternative();
        if (repAlt > 0) {
            repetitionAlt = repAlt;
            alternativeIndexes.clear();
        }
        for (int i = 0; i < 8; i ++) {
            if ((repetitionAlt & 1 << i) > 0) {
                repeatAlternatives.get(i).add(measure);
                alternativeIndexes.add(i);
            }
        }
    }

    private void addBeginningNTimes(int n) {
        for (int i = 0; i < n; i ++) {
            track.addMeasures(repeatBeginning);
        }
    }

    private void addAlternativeAndBeginning() {
        for (; firstAlt < 8 && !repeatAlternatives.get(firstAlt).isEmpty(); firstAlt ++) {
            track.addMeasures(repeatAlternatives.get(firstAlt));
            track.addMeasures(repeatBeginning);
        }
    }

    private void addAlternative() {
        for (; firstAlt < 8 && !repeatAlternatives.get(firstAlt).isEmpty(); firstAlt ++) {
            track.addMeasures(repeatAlternatives.get(firstAlt));
        }
    }
}
