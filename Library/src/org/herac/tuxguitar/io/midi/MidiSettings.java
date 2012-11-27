package org.herac.tuxguitar.io.midi;

public class MidiSettings {
	
	private int transpose;
	
	public MidiSettings(){
        transpose = 0;
	}
	
	public int getTranspose() {
		return transpose;
	}
	
	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}
	
	public static MidiSettings getDefaults(){
		return new MidiSettings();
	}
}
