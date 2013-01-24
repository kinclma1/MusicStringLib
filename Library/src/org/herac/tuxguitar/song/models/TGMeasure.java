/*
 * Created on 26-nov-2005
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
public abstract class TGMeasure {
	
	public static final int CLEF_TREBLE = 1;
	public static final int CLEF_BASS = 2;
	public static final int CLEF_TENOR = 3;
	public static final int CLEF_ALTO = 4;
	
	public static final int DEFAULT_CLEF = CLEF_TREBLE;
	public static final int DEFAULT_KEY_SIGNATURE= 0;
	
	private TGMeasureHeader header;
	private TGTrack track;
	private int clef;
	private int keySignature;
	
	private List<TGBeat> beats;
	
	public TGMeasure(TGMeasureHeader header){
		this.header = header;
        clef = DEFAULT_CLEF;
        keySignature = DEFAULT_KEY_SIGNATURE;
        beats = new ArrayList<TGBeat>();
	}
	
	public TGTrack getTrack() {
		return track;
	}
	
	public void setTrack(TGTrack track) {
		this.track = track;
	}
	
	public int getClef() {
		return clef;
	}
	
	public void setClef(int clef) {
		this.clef = clef;
	}
	
	public int getKeySignature() {
		return keySignature;
	}
	
	public void setKeySignature(int keySignature) {
		this.keySignature = keySignature;
	}
	
	public List<TGBeat> getBeats() {
		return beats;
	}
	
	public void addBeat(TGBeat beat){
		beat.setMeasure(this);
        beats.add(beat);
	}
	
	public void moveBeat(int index,TGBeat beat){
        beats.remove(beat);
        beats.add(index, beat);
	}
	
	public void removeBeat(TGBeat beat){
        beats.remove(beat);
	}
	
	public TGBeat getBeat(int index){
		if(index >= 0 && index < countBeats()){
			return beats.get(index);
		}
		return null;
	}
	
	public int countBeats(){
		return beats.size();
	}
	
	public TGMeasureHeader getHeader() {
		return header;
	}
	
	public void setHeader(TGMeasureHeader header) {
		this.header = header;
	}
	
	public int getNumber() {
		return header.getNumber();
	}
	
	public int getRepeatClose() {
		return header.getRepeatClose();
	}
	
	public long getStart() {
		return header.getStart();
	}
	
	public TGTempo getTempo() {
		return header.getTempo();
	}
	
	public TGTimeSignature getTimeSignature() {
		return header.getTimeSignature();
	}
	
	public boolean isRepeatOpen() {
		return header.isRepeatOpen();
	}
	
	public int getTripletFeel() {
		return header.getTripletFeel();
	}
	
	public long getLength() {
		return header.getLength();
	}
	
	public boolean hasMarker() {
		return header.hasMarker();
	}
	
	public TGMarker getMarker(){
		return header.getMarker();
	}
	
	public void makeEqual(TGMeasure measure){
        clef = measure.clef;
        keySignature = measure.keySignature;
        beats.clear();
		for(int i = 0; i < measure.countBeats(); i ++){
			TGBeat beat = measure.getBeat(i);
            addBeat(beat);
		}
	}
	
	public TGMeasure clone(TGFactory factory,TGMeasureHeader header){
		TGMeasure measure = factory.newMeasure(header);
		measure.setClef(clef);
		measure.setKeySignature(keySignature);
		for(int i = 0; i < countBeats(); i ++){
			TGBeat beat = beats.get(i);
			measure.addBeat(beat.clone(factory));
		}
		return measure;
	}
}
