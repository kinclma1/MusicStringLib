/*
 * Created on 30-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGString {
	private int number;
	private int value;
	
	public TGString(){
        number = 0;
        value = 0;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isEqual(TGString string){
		return (number == string.number && value == string.value);
	}
	
	public TGString clone(TGFactory factory){
		TGString string = factory.newString();
		copy(string);
		return string;
	}
	
	public void copy(TGString string){
		string.setNumber(number);
		string.setValue(value);
	}
	
}
