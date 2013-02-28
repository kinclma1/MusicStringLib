/*
 * Created on 23-nov-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author julian
 * 
 */
public abstract class TGVoice {
	
	public static final int DIRECTION_NONE = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	
	private TGBeat beat;
	private TGDuration duration;
	private List<TGNote> notes;
	private int index;
	private int direction;
	private boolean empty;
	
	public TGVoice(TGFactory factory, int index) {
        duration = factory.newDuration();
        notes = new ArrayList<TGNote>();
		this.index = index;
        empty = true;
        direction = DIRECTION_NONE;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public TGDuration getDuration() {
		return duration;
	}
	
	public void setDuration(TGDuration duration) {
		this.duration = duration;
	}
	
	public TGBeat getBeat() {
		return beat;
	}

	public void setBeat(TGBeat beat) {
		this.beat = beat;
	}

	public List<TGNote> getNotes() {
		return notes;
	}
	
	public void addNote(TGNote note){
		note.setVoice(this);
        notes.add(note);
        empty = false;
	}
	
	public void moveNote(int index,TGNote note){
        notes.remove(note);
        notes.add(index, note);
	}
	
	public void removeNote(TGNote note){
        notes.remove(note);
	}
	
	public TGNote getNote(int index){
		if(index >= 0 && index < countNotes()){
			return notes.get(index);
		}
		return null;
	}
	
	public int countNotes(){
		return notes.size();
	}
	
	public boolean isRestVoice(){
		return notes.isEmpty();
	}
	
	public TGVoice clone(TGFactory factory){
		TGVoice voice = factory.newVoice(index);
        voice.empty = empty;
        voice.direction = direction;
        duration.copy(voice.duration);
		for(int i = 0;i < countNotes();i++){
			TGNote note = notes.get(i);
			voice.addNote(note.clone(factory));
		}
		return voice;
	}
	
}