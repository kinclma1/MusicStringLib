package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public class TGText{
	
	private String value;
	private TGBeat beat;
	
	public TGText(){
    }
	
	public TGBeat getBeat() {
		return beat;
	}
	
	public void setBeat(TGBeat beat) {
		this.beat = beat;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isEmpty(){
		return (value == null || value.isEmpty());
	}
	
	public void copy(TGText text) {
		text.setValue(value);
	}
	
	public TGText clone(TGFactory factory) {
		TGText text = factory.newText();
		copy(text);
		return text;
	}
	
}
