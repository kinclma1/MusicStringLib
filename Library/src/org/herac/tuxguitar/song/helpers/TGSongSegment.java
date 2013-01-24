package org.herac.tuxguitar.song.helpers;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

import java.util.ArrayList;
import java.util.List;

public class TGSongSegment {
	private List<TGMeasureHeader> headers;
	private List<TGTrackSegment> tracks;
	
	public TGSongSegment(){
        headers = new ArrayList<TGMeasureHeader>();
        tracks = new ArrayList<TGTrackSegment>();
	}
	
	public List<TGMeasureHeader> getHeaders() {
		return headers;
	}
	
	public List<TGTrackSegment> getTracks() {
		return tracks;
	}
	
	public void addTrack(int track,List<TGMeasure> measures){
        tracks.add(new TGTrackSegment(track, measures));
	}
	
	public boolean isEmpty(){
		return (headers.isEmpty() || tracks.isEmpty());
	}
	
	public TGSongSegment clone(TGFactory factory){
		TGSongSegment segment = new TGSongSegment();
		for(int i = 0;i < headers.size();i++){
			TGMeasureHeader header = (TGMeasureHeader) headers.get(i);
            segment.headers.add(header.clone(factory));
		}
		for(int i = 0;i < tracks.size();i++){
			TGTrackSegment trackMeasure = (TGTrackSegment) tracks.get(i);
            segment.tracks.add(trackMeasure.clone(factory, segment.headers));
		}
		return segment;
	}
}
