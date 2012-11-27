package org.herac.tuxguitar.io.gpx.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPXDocument {
	
	private GPXScore score;
	private List tracks;
	private List masterBars;
	private List bars;
	private List voices;
	private List beats;
	private List notes;
	private List rhythms;
	private List automations;
	
	public GPXDocument(){
        score = new GPXScore();
        tracks = new ArrayList();
        masterBars = new ArrayList();
        bars = new ArrayList();
        voices = new ArrayList();
        beats = new ArrayList();
        notes = new ArrayList();
        rhythms = new ArrayList();
        automations = new ArrayList();
	}
	
	public GPXScore getScore(){
		return score;
	}
	
	public List getTracks() {
		return tracks;
	}
	
	public List getMasterBars() {
		return masterBars;
	}
	
	public List getBars() {
		return bars;
	}
	
	public List getVoices() {
		return voices;
	}
	
	public List getBeats() {
		return beats;
	}
	
	public List getNotes() {
		return notes;
	}
	
	public List getRhythms() {
		return rhythms;
	}
	
	public List getAutomations() {
		return automations;
	}
	
	public GPXBar getBar( int id ){
		Iterator it = bars.iterator();
		while( it.hasNext() ){
			GPXBar bar = (GPXBar)it.next();
			if( bar.getId() == id ){
				return bar;
			}
		}
		return null;
	}
	
	public GPXVoice getVoice( int id ){
		Iterator it = voices.iterator();
		while( it.hasNext() ){
			GPXVoice voice = (GPXVoice)it.next();
			if( voice.getId() == id ){
				return voice;
			}
		}
		return null;
	}
	
	public GPXBeat getBeat( int id ){
		Iterator it = beats.iterator();
		while( it.hasNext() ){
			GPXBeat beat = (GPXBeat)it.next();
			if( beat.getId() == id ){
				return beat;
			}
		}
		return null;
	}
	
	public GPXNote getNote( int id ){
		Iterator it = notes.iterator();
		while( it.hasNext() ){
			GPXNote note = (GPXNote)it.next();
			if( note.getId() == id ){
				return note;
			}
		}
		return null;
	}
	
	public GPXRhythm getRhythm( int id ){
		Iterator it = rhythms.iterator();
		while( it.hasNext() ){
			GPXRhythm rhythm = (GPXRhythm)it.next();
			if( rhythm.getId() == id ){
				return rhythm;
			}
		}
		return null;
	}
	
	public GPXAutomation getAutomation( String type, int untilBarId ){
		GPXAutomation result = null;
		
		Iterator it = automations.iterator();
		while( it.hasNext() ){
			GPXAutomation automation = (GPXAutomation)it.next();
			if( automation.getType() != null && automation.getType().equals( type ) ){
				if( automation.getBarId() <= untilBarId  && ( result == null || automation.getBarId() > result.getBarId() )){
					result = automation;
				}
			}
		}
		return result;
	}
}
