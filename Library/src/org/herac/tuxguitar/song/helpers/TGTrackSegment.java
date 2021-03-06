package org.herac.tuxguitar.song.helpers;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

import java.util.ArrayList;
import java.util.List;

public class TGTrackSegment {
	private final int track;
	private final List<TGMeasure> measures;
	
	public TGTrackSegment(int track,List<TGMeasure> measures){
		this.track = track;
		this.measures = measures;
	}
	
	public List<TGMeasure> getMeasures() {
		return measures;
	}
	
	public int getTrack() {
		return track;
	}
	
	public TGTrackSegment clone(TGFactory factory,List<TGMeasureHeader> headers){
		List<TGMeasure> measures = new ArrayList<TGMeasure>(this.measures.size());
		for(int i = 0;i < this.measures.size();i++){
			TGMeasure measure = this.measures.get(i);
			measures.add(measure.clone(factory, headers.get(i)));
		}
		return new TGTrackSegment(track,measures);
	}
}
