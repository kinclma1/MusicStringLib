package cz.cvut.fel.kinclma1;

class TempoTracker {
    private int tempo = 0;

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
