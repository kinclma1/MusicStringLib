/*
 * Created on 29-dic-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 */
public abstract class TGChord {
	private int firstFret;
	private int[] strings;
	private String name;
	private TGBeat beat;
	
	public TGChord(int length){
        strings = new int[length];
		for(int i = 0;i < strings.length;i++){
            strings[i] = -1;
		}
	}
	
	public TGBeat getBeat() {
		return beat;
	}
	
	public void setBeat(TGBeat beat) {
		this.beat = beat;
	}
	
	public void addFretValue(int string,int fret){
		if(string >= 0 && string < strings.length){
            strings[string] = fret;
		}
	}
	
	public int getFretValue(int string){
		if(string >= 0 && string < strings.length){
			return strings[string];
		}
		return -1;
	}
	
	public int getFirstFret() {
		return firstFret;
	}
	
	public void setFirstFret(int firstFret) {
		this.firstFret = firstFret;
	}
	
	public int[] getStrings() {
		return strings;
	}
	
	public int countStrings(){
		return strings.length;
	}
	
	public int countNotes(){
		int count = 0;
		for(int i = 0;i < strings.length;i++){
			if(strings[i] >= 0){
				count ++;
			}
		}
		return count;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public TGChord clone(TGFactory factory){
		TGChord chord = factory.newChord(strings.length);
		chord.setName(name);
		chord.setFirstFret(firstFret);
        System.arraycopy(strings, 0, chord.strings, 0, chord.strings.length);
		return chord;
	}
	
}
