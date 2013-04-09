package cz.cvut.fel.kinclma1;

/**
 * Tracks tempo along a track, used to signalize tempo changes
 */
class TempoTracker {
    private int tempo;

    /**
     * Returns information whether the given tempo is different from the previous
     * @param newTempo Current measure tempo
     * @return information whether the given tempo is different from the previous
     */
    boolean changed(int newTempo) {
        if(newTempo != tempo) {
            tempo = newTempo;
            return true;
        }
        return false;
    }

    /**
     * Returns current tempo
     * @return current tempo
     */
    int getTempo() {
        return tempo;
    }
}
