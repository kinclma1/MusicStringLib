package org.herac.tuxguitar.io.gpx.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPXDocument {
	
	private GPXScore score;
	private List<GPXTrack> tracks;
	private List<GPXMasterBar> masterBars;
	private List<GPXBar> bars;
	private List<GPXVoice> voices;
	private List<GPXBeat> beats;
	private List<GPXNote> notes;
	private List<GPXRhythm> rhythms;
	private List<GPXAutomation> automations;
	
	public GPXDocument(){
        score = new GPXScore();
        tracks = new ArrayList<GPXTrack>();
        masterBars = new ArrayList<GPXMasterBar>();
        bars = new ArrayList<GPXBar>();
        voices = new ArrayList<GPXVoice>();
        beats = new ArrayList<GPXBeat>();
        notes = new ArrayList<GPXNote>();
        rhythms = new ArrayList<GPXRhythm>();
        automations = new ArrayList<GPXAutomation>();
	}
	
	public GPXScore getScore(){
		return score;
	}
	
	public List<GPXTrack> getTracks() {
		return tracks;
	}
	
	public List<GPXMasterBar> getMasterBars() {
		return masterBars;
	}
	
	public List<GPXBar> getBars() {
		return bars;
	}
	
	public List<GPXVoice> getVoices() {
		return voices;
	}
	
	public List<GPXBeat> getBeats() {
		return beats;
	}
	
	public List<GPXNote> getNotes() {
		return notes;
	}
	
	public List<GPXRhythm> getRhythms() {
		return rhythms;
	}
	
	public List<GPXAutomation> getAutomations() {
		return automations;
	}
	
	public GPXBar getBar( int id ){
        for (GPXBar bar : bars) {
            if (bar.getId() == id) {
                return bar;
            }
        }
		return null;
	}
	
	public GPXVoice getVoice( int id ){
        for (GPXVoice voice : voices) {
            if (voice.getId() == id) {
                return voice;
            }
        }
		return null;
	}
	
	public GPXBeat getBeat( int id ){
        for (GPXBeat beat : beats) {
            if (beat.getId() == id) {
                return beat;
            }
        }
		return null;
	}
	
	public GPXNote getNote( int id ){
        for (GPXNote note : notes) {
            if (note.getId() == id) {
                return note;
            }
        }
		return null;
	}
	
	public GPXRhythm getRhythm( int id ){
        for (GPXRhythm rhythm : rhythms) {
            if (rhythm.getId() == id) {
                return rhythm;
            }
        }
		return null;
	}
	
	public GPXAutomation getAutomation( String type, int untilBarId ){
		GPXAutomation result = null;

        for (GPXAutomation automation : automations) {
            if (automation.getType() != null && automation.getType().equals(type)) {
                if (automation.getBarId() <= untilBarId && (result == null || automation.getBarId() > result.getBarId())) {
                    result = automation;
                }
            }
        }
		return result;
	}
}
