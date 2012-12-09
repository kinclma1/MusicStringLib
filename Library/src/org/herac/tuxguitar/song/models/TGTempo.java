/*
 * Created on 29-nov-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 */
public abstract class TGTempo {
	private static final int SECOND_IN_MILLIS = 1000;
	
	private int value;
	
	public TGTempo(){
        value = 120;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public long getInMillis(){
		double millis = (60.00 / value * SECOND_IN_MILLIS);
		return (long)millis;
	}
	
	public long getInUSQ(){
		double usq = ((60.00 / value * SECOND_IN_MILLIS) * 1000.00);
		return (long)usq;
	}
	
	public static TGTempo fromUSQ(TGFactory factory,int usq){
		double value = ((60.00 * SECOND_IN_MILLIS) / (usq / 1000.00));
		TGTempo tempo = factory.newTempo();
		tempo.setValue((int)value);
		return tempo;
	}
	
	public TGTempo clone(TGFactory factory){
		TGTempo tempo = factory.newTempo();
		copy(tempo);
		return tempo;
	}
	
	public void copy(TGTempo tempo){
		tempo.setValue(value);
	}
	
}
