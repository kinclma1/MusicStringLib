package org.herac.tuxguitar.song.helpers;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGSongSegmentHelper {
	
	private TGSongManager sm;
	
	public TGSongSegmentHelper(TGSongManager sm){
		this.sm = sm;
	}
	
	public TGSongSegment copyMeasures(int m1, int m2){
		TGSongSegment segment = new TGSongSegment();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(sm.getSong().countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add(sm.getMeasureHeader(number) );
		}
		Iterator<TGTrack> it = sm.getSong().getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			List<TGMeasure> measures = new ArrayList<TGMeasure>();
			for(int number = number1; number <= number2;number ++){
				measures.add(sm.getTrackManager().getMeasure(track, number));
			}
			segment.addTrack(track.getNumber(),measures);
		}
		return segment.clone(sm.getFactory());
	}
	
	public TGSongSegment copyMeasures(int m1, int m2,TGTrack track){
		TGSongSegment segment = new TGSongSegment();
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(sm.getSong().countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add(sm.getMeasureHeader(number) );
			measures.add(sm.getTrackManager().getMeasure(track, number));
		}
		segment.addTrack(track.getNumber(),measures);
		return segment.clone(sm.getFactory());
	}
	
	public void insertMeasures(TGSongSegment segment,int fromNumber,long move, int track){
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
        sm.moveMeasureHeaders(segment.getHeaders(), move, 0, false);
		
		int headerNumber = fromNumber;
		Iterator<TGMeasureHeader> it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			header.setNumber(headerNumber);
			headers.add(header);
			headerNumber ++;
		}
		long start = headers.get(0).getStart();
		long end = headers.get(headers.size() - 1).getStart() + headers.get(headers.size() - 1).getLength();
		List<TGMeasureHeader> headersBeforeEnd = sm.getMeasureHeadersBeforeEnd(start);
        sm.moveMeasureHeaders(headersBeforeEnd, end - start, headers.size(), true);
		
		it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
            sm.addMeasureHeader(header.getNumber() - 1, header);
		}
		Iterator<TGTrack> it1;
		it1 = sm.getSong().getTracks();
		while (it.hasNext()) {
			TGTrack currTrack = it1.next();
			List<TGMeasure> measures = null;
			
			Iterator<TGTrackSegment> tracks = segment.getTracks().iterator();
			while(tracks.hasNext()){
				TGTrackSegment tSegment = tracks.next();
				
				if(  ((track > 0 && segment.getTracks().size() == 1)?track:tSegment.getTrack()) == currTrack.getNumber()){
					measures = tSegment.getMeasures();
					break;
				}
			}
			if(measures == null){
				TGTrackManager tm = sm.getTrackManager();
				TGMeasure measure = (fromNumber > 1 ? tm.getMeasure(currTrack , (fromNumber - 1) ) : tm.getMeasure(currTrack, headerNumber ));
				int clef = ( measure != null ? measure.getClef() : TGMeasure.DEFAULT_CLEF );
				int keySignature = ( measure != null ? measure.getKeySignature() : TGMeasure.DEFAULT_KEY_SIGNATURE );
				measures = getEmptyMeasures(segment.getHeaders().size(), clef, keySignature);
			}
			
			for(int i = 0;i < measures.size();i++){
				TGMeasure measure = measures.get(i);
				measure.setHeader(headers.get(i));
                sm.getMeasureManager().moveAllBeats(measure,move);
			}
			
			insertMeasures(currTrack,measures);
		}
	}
	
	public void insertMeasures(TGTrack track,List<TGMeasure> measures){
		if(!measures.isEmpty()){
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure measure = it.next();
                sm.getMeasureManager().removeNotesAfterString(measure, track.stringCount());
                sm.getTrackManager().addMeasure(track,(measure.getNumber() - 1),measure);
			}
		}
	}
	
	public void replaceMeasures(TGSongSegment segment,long move, int track) {
		boolean replaceHeader = ( track == 0 || sm.getSong().countTracks() == 1 );
		
		List<TGMeasureHeader> measureHeaders = new ArrayList<TGMeasureHeader>();
        sm.moveMeasureHeaders(segment.getHeaders(), move, 0, false);
		Iterator<TGMeasureHeader> sHeaders = segment.getHeaders().iterator();
		while(sHeaders.hasNext()){
			TGMeasureHeader header = sHeaders.next();
			TGMeasureHeader replace = (replaceHeader ? sm.replaceMeasureHeader(header) : sm.getMeasureHeaderAt(header.getStart()));
			
			Iterator<TGMeasureHeader> nextHeaders = sm.getMeasureHeadersAfter(replace.getNumber()).iterator();
			long nextStart =  (replace.getStart() + replace.getLength());
			while(nextHeaders.hasNext()){
				TGMeasureHeader next = nextHeaders.next();
                sm.moveMeasureComponents(next, (nextStart - next.getStart()));
                sm.moveMeasureHeader(next, (nextStart - next.getStart()), 0);
				nextStart = (next.getStart() + next.getLength());
			}
			measureHeaders.add(replace);
		}
		
		Iterator<TGTrackSegment> sTracks = segment.getTracks().iterator();
		while(sTracks.hasNext()){
			TGTrackSegment tSegment = sTracks.next();
			TGTrack currTrack = sm.getTrack((track > 0 && segment.getTracks().size() == 1) ? track : tSegment.getTrack());
			if(currTrack != null){
				for(int i = 0;i < tSegment.getMeasures().size();i++){
					TGMeasure measure = tSegment.getMeasures().get(i);
					measure.setHeader(measureHeaders.get(i));
					//this.sm.getMeasureManager().removeNotesAfterString(measure, currTrack.stringCount());
					//this.sm.getMeasureManager().moveAllBeats(measure,move);
                    sm.getMeasureManager().moveAllBeats(measure,move);
                    sm.getMeasureManager().removeVoicesOutOfTime(measure);
                    sm.getMeasureManager().removeNotesAfterString(measure, currTrack.stringCount());
                    sm.getTrackManager().replaceMeasure(currTrack,measure);
				}
			}
		}
	}
	
	private List<TGMeasure> getEmptyMeasures(int count, int clef, int keySignature) {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		for(int i = 0 ; i < count ; i ++ ){
			TGMeasure measure = sm.getFactory().newMeasure(null);
			measure.setClef(clef);
			measure.setKeySignature(keySignature);
			measures.add(measure);
		}
		return measures;
	}
	
	public TGSongSegment createSegmentCopies( TGSongSegment srcSegment , int count ){
		TGSongSegment segment = srcSegment.clone(sm.getFactory() );
		
		int mCount = segment.getHeaders().size();
		int tCount = segment.getTracks().size();
		
		TGMeasureHeader fMeasure = segment.getHeaders().get( 0 );
		TGMeasureHeader lMeasure = segment.getHeaders().get( mCount - 1 );
		
		long mMove = ( ( lMeasure.getStart() + lMeasure.getLength() ) - fMeasure.getStart() );
		for( int i = 1 ; i < count ; i ++ ){
			for(int m = 0; m < mCount; m++ ){
				TGMeasureHeader header = segment.getHeaders().get(m).clone(sm.getFactory());
				segment.getHeaders().add( header );
                sm.moveMeasureHeader(header, (mMove * i), (mCount * i));
				for( int t = 0 ; t < tCount ; t ++ ){
					TGTrackSegment track = segment.getTracks().get( t );
					TGMeasure measure = track.getMeasures().get( m ).clone(sm.getFactory(), header);
					track.getMeasures().add( measure );
                    sm.getMeasureManager().moveAllBeats(measure, (mMove * i));
				}
			}
		}
		
		return segment;
	}
}
