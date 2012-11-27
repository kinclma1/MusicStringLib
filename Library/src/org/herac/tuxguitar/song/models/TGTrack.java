/*
 * Created on 23-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGTrack {
	public static final int MAX_OFFSET = 24;
	public static final int MIN_OFFSET = -24;
	
	private int number;
	private int offset;
	private boolean solo;
	private boolean mute;
	private String name;
	private List<TGMeasure> measures;
	private List<TGString> strings;
	private TGChannel channel;
	private TGColor color;
	private TGLyric lyrics;
	private TGSong song;
	
	public TGTrack(TGFactory factory) {
        number = 0;
        offset = 0;
        solo = false;
        mute = false;
        name = "";
        measures = new ArrayList<TGMeasure>();
        strings = new ArrayList<TGString>();
        channel = factory.newChannel();
        color = factory.newColor();
        lyrics = factory.newLyric();
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public Iterator<TGMeasure> getMeasures() {
		return measures.iterator();
	}
	
	public void addMeasure(TGMeasure measure){
		measure.setTrack(this);
        measures.add(measure);
	}
	
	public void addMeasure(int index,TGMeasure measure){
		measure.setTrack(this);
        measures.add(index, measure);
	}
	
	public TGMeasure getMeasure(int index){
		if(index >= 0 && index < countMeasures()){
			return measures.get(index);
		}
		return null;
	}
	
	public void removeMeasure(int index){
        measures.remove(index);
	}
	
	public int countMeasures(){
		return measures.size();
	}
	
	public TGChannel getChannel() {
		return channel;
	}
	
	public void setChannel(TGChannel channel) {
		this.channel = channel;
	}
	
	public List<TGString> getStrings() {
		return strings;
	}
	
	public void setStrings(List<TGString> strings) {
		this.strings = strings;
	}
	
	public TGColor getColor() {
		return color;
	}
	
	public void setColor(TGColor color) {
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public boolean isSolo() {
		return solo;
	}
	
	public void setSolo(boolean solo) {
		this.solo = solo;
	}
	
	public boolean isMute() {
		return mute;
	}
	
	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	public TGLyric getLyrics() {
		return lyrics;
	}
	
	public void setLyrics(TGLyric lyrics) {
		this.lyrics = lyrics;
	}
	
	public TGString getString(int number){
		return strings.get(number - 1);
	}
	
	public int stringCount(){
		return strings.size();
	}
	
	public boolean isPercussionTrack(){
		return (channel.isPercussionChannel());
	}
	
	public TGSong getSong() {
		return song;
	}
	
	public void setSong(TGSong song) {
		this.song = song;
	}
	
	public void clear(){
        strings.clear();
        measures.clear();
	}
	
	public TGTrack clone(TGFactory factory,TGSong song){
		TGTrack track = factory.newTrack();
		copy(factory, song, track);
		return track;
	}
	
	public void copy(TGFactory factory,TGSong song,TGTrack track){
		track.clear();
		track.setNumber(number);
		track.setName(name);
		track.setOffset(offset);
        channel.copy(track.channel);
        color.copy(track.color);
        lyrics.copy(track.lyrics);
		for (int i = 0; i < strings.size(); i++) {
			TGString string = strings.get(i);
            track.strings.add(string.clone(factory));
		}
		for (int i = 0; i < countMeasures(); i++) {
			TGMeasure measure = getMeasure(i);
			track.addMeasure(measure.clone(factory,song.getMeasureHeader(i)));
		}
	}
	
}
