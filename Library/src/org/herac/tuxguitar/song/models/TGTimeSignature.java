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
public abstract class TGTimeSignature {
	private TGDuration denominator;
	private int numerator;
	
	public TGTimeSignature(TGFactory factory){
        numerator = 4;
        denominator = factory.newDuration();
	}
	
	public int getNumerator() {
		return numerator;
	}
	
	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}
	
	public TGDuration getDenominator() {
		return denominator;
	}
	
	public void setDenominator(TGDuration denominator) {
		this.denominator = denominator;
	}
	
	public TGTimeSignature clone(TGFactory factory){
		TGTimeSignature timeSignature = factory.newTimeSignature();
		copy(timeSignature);
		return timeSignature;
	}
	
	public void copy(TGTimeSignature timeSignature){
        timeSignature.numerator = numerator;
        denominator.copy(timeSignature.denominator);
	}
	
	public boolean isEqual(TGTimeSignature ts){
		return (numerator == ts.numerator && denominator.isEqual(ts.denominator));
	}
}
