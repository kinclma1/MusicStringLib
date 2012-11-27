/*
 * Created on 23-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGBeat {
	
	public static final int MAX_VOICES = 2;
	
	private long start;
	private TGMeasure measure;
	private TGChord chord;
	private TGText text;
	private TGVoice[] voices;
	private TGStroke stroke;
	
	public TGBeat(TGFactory factory) {
        start = TGDuration.QUARTER_TIME;
        stroke = factory.newStroke();
        voices = new TGVoice[ MAX_VOICES ];
		for( int i = 0 ; i < MAX_VOICES ; i ++ ){
            setVoice(i, factory.newVoice(i));
		}
	}
	
	public TGMeasure getMeasure() {
		return measure;
	}
	
	public void setMeasure(TGMeasure measure) {
		this.measure = measure;
	}
	
	public long getStart() {
		return start;
	}
	
	public void setStart(long start) {
		this.start = start;
	}
	
	public void setVoice(int index, TGVoice voice){
		if( index >= 0 && index < voices.length ){
            voices[index] = voice;
            voices[index].setBeat( this );
		}
	}
	
	public TGVoice getVoice(int index){
		if( index >= 0 && index < voices.length ){
			return voices[index];
		}
		return null;
	}
	
	public int countVoices(){
		return voices.length;
	}
	
	public void setChord(TGChord chord) {
		this.chord = chord;
		this.chord.setBeat(this);
	}
	
	public TGChord getChord() {
		return chord;
	}
	
	public void removeChord() {
        chord = null;
	}
	
	public TGText getText() {
		return text;
	}
	
	public void setText(TGText text) {
		this.text = text;
		this.text.setBeat(this);
	}
	
	public void removeText(){
        text = null;
	}
	
	public boolean isChordBeat(){
		return (chord != null );
	}
	
	public boolean isTextBeat(){
		return (text != null );
	}
	
	public TGStroke getStroke() {
		return stroke;
	}
	
	public boolean isRestBeat(){
		for(int v = 0; v < countVoices() ; v ++ ){
			TGVoice voice = getVoice( v );
			if( !voice.isEmpty() && !voice.isRestVoice() ){
				return false;
			}
		}
		return true;
	}
	
	public TGBeat clone(TGFactory factory){
		TGBeat beat = factory.newBeat();
		beat.setStart(start);
        stroke.copy(beat.stroke);
		for( int i = 0 ; i < voices.length ; i ++ ){
			beat.setVoice(i, voices[i].clone(factory));
		}
		if(chord != null){
			beat.setChord(chord.clone(factory));
		}
		if(text != null){
			beat.setText(text.clone(factory));
		}
		return beat;
	}
}