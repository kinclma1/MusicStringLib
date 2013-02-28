/*
 * Created on 05-dic-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 */
public abstract class TGDivisionType {
	public static final TGDivisionType NORMAL = newDivisionType(1,1);
	
	/**
	 * Cantidad de Duraciones que entran en los tiempos
	 */
	private int enters;
	/**
	 * Tiempos
	 */
	private int times;
	
	public TGDivisionType(){
        enters = 1;
        times = 1;
	}
	
	public int getEnters() {
		return enters;
	}
	
	public void setEnters(int enters) {
		this.enters = enters;
	}
	
	public int getTimes() {
		return times;
	}
	
	public void setTimes(int times) {
		this.times = times;
	}
	
	public long convertTime(long time){
		return time * times / enters;
	}
	
	public boolean isEqual(TGDivisionType divisionType){
		return (divisionType.enters == enters && divisionType.times == times);
	}
	
	public TGDivisionType clone(TGFactory factory){
		TGDivisionType divisionType = factory.newDivisionType();
		copy(divisionType);
		return divisionType;
	}
	
	public void copy(TGDivisionType divisionType){
        divisionType.enters = enters;
        divisionType.times = times;
	}
	
	private static TGDivisionType newDivisionType(int enters,int times){
		TGDivisionType divisionType = new TGFactory().newDivisionType();
        divisionType.enters = enters;
        divisionType.times = times;
		return divisionType;
	}
	
}
