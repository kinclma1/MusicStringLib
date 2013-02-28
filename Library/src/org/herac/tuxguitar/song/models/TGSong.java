/*
 * Created on 23-nov-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author julian
 *
 */
public abstract class TGSong {
	
	private String name;
	private String artist;
	private String album;
	private String author;
	private String date;
	private String copyright;
	private String writer;
	private String transcriber;
	private String comments;
	private List<TGTrack> tracks;
	private List<TGMeasureHeader> measureHeaders;
	
	public TGSong() {
        name = "";
        artist = "";
        album = "";
        author = "";
        date = "";
        copyright = "";
        writer = "";
        transcriber = "";
        comments = "";
        tracks = new ArrayList<TGTrack>();
        measureHeaders = new ArrayList<TGMeasureHeader>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	public String getWriter() {
		return writer;
	}
	
	public void setWriter(String writer) {
		this.writer = writer;
	}
	
	public String getTranscriber() {
		return transcriber;
	}
	
	public void setTranscriber(String transcriber) {
		this.transcriber = transcriber;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public int countMeasureHeaders(){
		return measureHeaders.size();
	}
	
	public void addMeasureHeader(TGMeasureHeader measureHeader){
        addMeasureHeader(countMeasureHeaders(),measureHeader);
	}
	
	public void addMeasureHeader(int index,TGMeasureHeader measureHeader){
		measureHeader.setSong(this);
        measureHeaders.add(index, measureHeader);
	}
	
	public void removeMeasureHeader(int index){
        measureHeaders.remove(index);
	}
	
	public void removeMeasureHeader(TGMeasureHeader measureHeader){
        measureHeaders.remove(measureHeader);
	}
	
	public TGMeasureHeader getMeasureHeader(int index){
		return measureHeaders.get(index);
	}
	
	public Iterator<TGMeasureHeader> getMeasureHeaders() {
		return measureHeaders.iterator();
	}
	
	public int countTracks(){
		return tracks.size();
	}
	
	public void addTrack(TGTrack track){
        addTrack(countTracks(),track);
	}
	
	public void addTrack(int index,TGTrack track){
		track.setSong(this);
        tracks.add(index, track);
	}
	
	public void moveTrack(int index,TGTrack track){
        tracks.remove(track);
        tracks.add(index, track);
	}
	
	public void removeTrack(TGTrack track){
        tracks.remove(track);
		track.clear();
	}
	
	public TGTrack getTrack(int index){
		return tracks.get(index);
	}
	
	public Iterator<TGTrack> getTracks() {
		return tracks.iterator();
	}
	
	public boolean isEmpty(){
		return (countMeasureHeaders() == 0 || countTracks() == 0);
	}
	
	public void clear(){
		Iterator<TGTrack> tracks = getTracks();
		while(tracks.hasNext()){
			TGTrack track = tracks.next();
			track.clear();
		}
		this.tracks.clear();
        measureHeaders.clear();
	}
	
	public TGSong clone(TGFactory factory){
		TGSong song = factory.newSong();
		copy(factory,song);
		return song;
	}
	
	public void copy(TGFactory factory,TGSong song){
		song.clear();
        song.name = name;
        song.artist = artist;
        song.album = album;
        song.author = author;
        song.date = date;
        song.copyright = copyright;
        song.writer = writer;
        song.transcriber = transcriber;
        song.comments = comments;
		Iterator<TGMeasureHeader> headers = getMeasureHeaders();
		while(headers.hasNext()){
			TGMeasureHeader header = headers.next();
			song.addMeasureHeader(header.clone(factory));
		}
		Iterator<TGTrack> tracks = getTracks();
		while(tracks.hasNext()){
			TGTrack track = tracks.next();
			song.addTrack(track.clone(factory, song));
		}
	}
}
