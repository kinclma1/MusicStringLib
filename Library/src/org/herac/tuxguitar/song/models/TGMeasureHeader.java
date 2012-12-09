/*
 * Created on 26-nov-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 */
public abstract class TGMeasureHeader {
	public static final int TRIPLET_FEEL_NONE = 1;
	public static final int TRIPLET_FEEL_EIGHTH = 2;
	public static final int TRIPLET_FEEL_SIXTEENTH = 3;
	
	private int number;
	private long start;
	private TGTimeSignature timeSignature;
	private TGTempo tempo;
	private TGMarker marker;
	private boolean repeatOpen;
	private int repeatAlternative;
	private int repeatClose;
	private int tripletFeel;
	private TGSong song;
	
	public TGMeasureHeader(TGFactory factory){
        number = 0;
        start = TGDuration.QUARTER_TIME;
        timeSignature = factory.newTimeSignature();
        tempo = factory.newTempo();
        marker = null;
        tripletFeel = TRIPLET_FEEL_NONE;
        repeatOpen = false;
        repeatClose = 0;
        repeatAlternative = 0;
        checkMarker();
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
        checkMarker();
	}
	
	public int getRepeatClose() {
		return repeatClose;
	}
	
	public void setRepeatClose(int repeatClose) {
		this.repeatClose = repeatClose;
	}
	
	public int getRepeatAlternative() {
		return repeatAlternative;
	}
	
	/**
	 * bitwise value 1 TO 8.
	 * (1 << AlternativeNumber)
	 */
	public void setRepeatAlternative(int repeatAlternative) {
		this.repeatAlternative = repeatAlternative;
	}
	
	public boolean isRepeatOpen() {
		return repeatOpen;
	}
	
	public void setRepeatOpen(boolean repeatOpen) {
		this.repeatOpen = repeatOpen;
	}
	
	public long getStart() {
		return start;
	}
	
	public void setStart(long start) {
		this.start = start;
	}
	
	public int getTripletFeel() {
		return tripletFeel;
	}
	
	public void setTripletFeel(int tripletFeel) {
		this.tripletFeel = tripletFeel;
	}
	
	public TGTempo getTempo() {
		return tempo;
	}
	
	public void setTempo(TGTempo tempo) {
		this.tempo = tempo;
	}
	
	public TGTimeSignature getTimeSignature() {
		return timeSignature;
	}
	
	public void setTimeSignature(TGTimeSignature timeSignature) {
		this.timeSignature = timeSignature;
	}
	
	public TGMarker getMarker() {
		return marker;
	}
	
	public void setMarker(TGMarker marker) {
		this.marker = marker;
	}
	
	public boolean hasMarker(){
		return (marker != null);
	}
	
	private void checkMarker(){
		if(hasMarker()){
            marker.setMeasure(number);
		}
	}
	
	public long getLength(){
		return timeSignature.getNumerator() * timeSignature.getDenominator().getTime();
	}
	
	public TGSong getSong() {
		return song;
	}
	
	public void setSong(TGSong song) {
		this.song = song;
	}
	
	public void makeEqual(TGMeasureHeader measure){
        start = measure.start;
        timeSignature = measure.timeSignature;
        tempo = measure.tempo;
        marker = measure.marker;
        repeatOpen = measure.repeatOpen;
        repeatClose = measure.repeatClose;
        repeatAlternative = measure.repeatAlternative;
        checkMarker();
	}
	
	public TGMeasureHeader clone(TGFactory factory){
		TGMeasureHeader header = factory.newHeader();
		header.setNumber(number);
		header.setStart(start);
		header.setRepeatOpen(repeatOpen);
		header.setRepeatAlternative(repeatAlternative);
		header.setRepeatClose(repeatClose);
		header.setTripletFeel(tripletFeel);
        timeSignature.copy(header.timeSignature);
        tempo.copy(header.tempo);
		header.setMarker(hasMarker()? marker.clone(factory) :null);
		return header;
	}
}
