/*
 * Created on 23-nov-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 * 
 */
public abstract class TGNote {
	private int value;
	private int velocity;
	private int string;
	private boolean tiedNote;
	private TGNoteEffect effect;
	private TGVoice voice;
	
	public TGNote(TGFactory factory) {
        value = 0;
        velocity = TGVelocities.DEFAULT;
        string = 1;
        tiedNote = false;
        effect = factory.newEffect();
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getVelocity() {
		return velocity;
	}
	
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	
	public int getString() {
		return string;
	}
	
	public void setString(int string) {
		this.string = string;
	}
	
	public boolean isTiedNote() {
		return tiedNote;
	}
	
	public void setTiedNote(boolean tiedNote) {
		this.tiedNote = tiedNote;
	}
	
	public TGNoteEffect getEffect() {
		return effect;
	}
	
	public void setEffect(TGNoteEffect effect) {
		this.effect = effect;
	}
	
	public TGVoice getVoice() {
		return voice;
	}
	
	public void setVoice(TGVoice voice) {
		this.voice = voice;
	}
	
	public TGNote clone(TGFactory factory){
		TGNote note = factory.newNote();
        note.value = value;
        note.velocity = velocity;
        note.string = string;
        note.tiedNote = tiedNote;
        note.effect = effect.clone(factory);
		return note;
	}
}