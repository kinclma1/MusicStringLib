package org.herac.tuxguitar.song.models;

public abstract class TGScale {
	private final boolean[] notes = new boolean[12];
	
	private int key;
	
	public TGScale(){
        clear();
	}
	
	public void setKey(int key){
		this.key = key;
	}
	
	public int getKey(){
		return key;
	}
	
	public void setNote(int note,boolean on){
        notes[note] = on;
	}
	
	public boolean getNote(int note){
		return notes[((note + (12 - key)) % 12)];
	}
	
	public void clear(){
        key = 0;
		for(int i = 0; i < notes.length; i++){
            setNote(i,false);
		}
	}
	
}
