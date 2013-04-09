package cz.cvut.fel.kinclma1.scales;

/**
 * Definition of the Aeolian music scale
 */
public class AeolianScale extends Scale {

    public AeolianScale() {
        definingTones = new int[]{0,3,7};
        scaleTones = new int[]{0,2,3,5,7,8,10};
    }
}
