package cz.cvut.fel.kinclma1;

class TempoTracker {
    private int tempo;

    boolean changed(int newTempo) {
        if(newTempo != tempo) {
            tempo = newTempo;
            return true;
        }
        return false;
    }

    int getTempo() {
        return tempo;
    }
}
