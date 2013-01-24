package org.herac.tuxguitar.song.managers;

import org.herac.tuxguitar.song.models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGTrackManager {
	private TGSongManager songManager;
	
	public TGTrackManager(TGSongManager songManager){
		this.songManager = songManager;
	}
	
	public TGSongManager getSongManager(){
		return songManager;
	}
	
	public TGMeasure getFirstMeasure(TGTrack track){
		TGMeasure firstMeasure = null;
		Iterator<TGMeasure> measures = track.getMeasures();
		while(measures.hasNext()){
			TGMeasure currMeasure = (TGMeasure)measures.next();
			if(firstMeasure == null || (currMeasure.getStart() < firstMeasure.getStart())){
				firstMeasure = currMeasure;
			}
		}
		return firstMeasure;
	}
	
	public TGMeasure getLastMeasure(TGTrack track){
		return track.getMeasure(track.countMeasures() - 1);
	}
	
	public TGMeasure getPrevMeasure(TGMeasure measure){
		return measure.getTrack().getMeasure(measure.getNumber() - 2);
	}
	
	public TGMeasure getNextMeasure(TGMeasure measure){
		return measure.getTrack().getMeasure(measure.getNumber());
	}
	
	public TGMeasure getMeasureAt(TGTrack track,long start){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			long measureStart = measure.getStart();
			long measureLength = measure.getLength();
			if(start >= measureStart && start < measureStart + measureLength){
				return measure;
			}
		}
		return null;
	}
	
	public TGMeasure getMeasure(TGTrack track,int number){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			if(measure.getNumber() == number){
				return measure;
			}
		}
		return null;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasure> getMeasuresBeforeEnd(TGTrack track,long fromStart) {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure currMeasure = (TGMeasure)it.next();
			if (currMeasure.getStart() >= fromStart) {
				measures.add(currMeasure);
			}
		}
		return measures;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasure> getMeasuresBetween(TGTrack track,long p1,long p2) {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			if ((measure.getStart() + measure.getLength()) > p1  &&  measure.getStart() < p2) {
				measures.add(measure);
			}
		}
		return measures;
	}
	
	public void addNewMeasureBeforeEnd(TGTrack track,TGMeasureHeader header){
		addNewMeasureAfter(track,header,getLastMeasure(track));
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addNewMeasureAfter(TGTrack track,TGMeasureHeader header,TGMeasure measure){
		TGMeasure newMeasure = songManager.getFactory().newMeasure(header);
		newMeasure.setClef(measure.getClef());
		newMeasure.setKeySignature(measure.getKeySignature());
		addMeasure(track,newMeasure);
	}
	
	public void addNewMeasure(TGTrack track,TGMeasureHeader header){
		TGMeasure previous = getMeasure(track, (header.getNumber() == 1)?(header.getNumber()+1):header.getNumber()-1);
		TGMeasure newMeasure = songManager.getFactory().newMeasure(header);
		newMeasure.setTrack(track);
		newMeasure.setClef(previous.getClef());
		newMeasure.setKeySignature(previous.getKeySignature());
		addMeasure(track,header.getNumber() - 1,newMeasure);
	}
	/*
	public List copyMeasures(TGTrack track,int m1,int m2){
		List measures = new ArrayList();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(track.countMeasures(),m2);
		for(int number = number1; number <= number2;number ++){
			measures.add(getMeasure(track, number));
		}
		return measures;
	}
	*/
	/**
	 * Agrega un Compas
	 *//*
	public void insertMeasures(TGTrack track,List measures){
		if(!measures.isEmpty()){
			Iterator it = measures.iterator();
			while(it.hasNext()){
				TGMeasure measure = (TGMeasure)it.next();
				addMeasure(track,(measure.getNumber() - 1),measure);
			}
		}
	}*/
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasure(TGTrack track,TGMeasure measure){
		track.addMeasure(measure);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasure(TGTrack track,int index,TGMeasure measure){
		track.addMeasure(index,measure);
	}
	
	public void removeLastMeasure(TGTrack track){
		removeMeasure(getLastMeasure(track));
	}
	
	public void removeMeasure(TGTrack track,long start){
		removeMeasure(getMeasureAt(track,start));
	}
	
	public void removeMeasure(TGMeasure measure){
		measure.getTrack().removeMeasure(measure.getNumber() - 1);
	}
	
	public TGMeasure replaceMeasure(TGTrack track,TGMeasure newMeasure){
		TGMeasure measure = getMeasureAt(track,newMeasure.getStart());
		measure.makeEqual(newMeasure);
		return measure;
	}
	
	/**
	 * Mueve el compas
	 */
	public void moveMeasure(TGMeasure measure,long theMove){
		//getSongManager().getMeasureManager().moveAllComponents(measure,theMove);
        songManager.getMeasureManager().moveAllBeats(measure,theMove);
	}
	
	public void moveOutOfBoundsBeatsToNewMeasure(TGTrack track, long start){
		Iterator<TGMeasure> it = getMeasuresBeforeEnd(track,start).iterator();
		while( it.hasNext() ){
			TGMeasure measure = (TGMeasure)it.next();
            songManager.getMeasureManager().moveOutOfBoundsBeatsToNewMeasure(measure);
		}
	}
	
	public void moveTrackBeats(TGTrack track, long measureStart, long moveStart, long theMove ){
		List<TGMeasure> measures = getMeasuresBeforeEnd(track,measureStart);
		for( int i = 0 ; i < measures.size() ; i ++ ){
			TGMeasure measure = (TGMeasure)measures.get(i);
			if( moveStart + theMove < moveStart ){
                songManager.getMeasureManager().removeBeatsBeetween(measure, moveStart, (moveStart + Math.abs(theMove)));
			}
            songManager.getMeasureManager().moveBeats(measure, moveStart, theMove);
		}
		for( int i = 0 ; i < measures.size() ; i ++ ){
			TGMeasure measure = (TGMeasure)measures.get(i);
            songManager.getMeasureManager().moveOutOfBoundsBeatsToNewMeasure(measure,false);
		}
	}
	
	public void changeKeySignature(TGTrack track,long start,int keySignature,boolean toEnd){
		changeKeySignature(track,getMeasureAt(track,start),keySignature,toEnd);
	}
	
	/**
	 * Cambia el Key Signature
	 */
	public void changeKeySignature(TGTrack track,TGMeasure measure,int keySignature,boolean toEnd){
		//asigno el nuevo Key
		measure.setKeySignature(keySignature);
		
		if(toEnd){
			List<TGMeasure> measures = getMeasuresBeforeEnd(track,measure.getStart() + 1);
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure nextMeasure = (TGMeasure)it.next();
				nextMeasure.setKeySignature(keySignature);
			}
		}
	}
	
	public void changeClef(TGTrack track,long start,int clef,boolean toEnd){
		changeClef(track,getMeasureAt(track,start),clef,toEnd);
	}
	
	/**
	 * Cambia el Clef
	 */
	public void changeClef(TGTrack track,TGMeasure measure,int clef,boolean toEnd){
		//asigno el nuevo clef
		measure.setClef(clef);
		
		if(toEnd){
			List<TGMeasure> measures = getMeasuresBeforeEnd(track,measure.getStart() + 1);
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure nextMeasure = (TGMeasure)it.next();
				nextMeasure.setClef(clef);
			}
		}
	}
	
	public void changeSolo(TGTrack track,boolean solo){
		track.setSolo(solo);
		track.setMute(!track.isSolo() && track.isMute());
	}
	
	public void changeMute(TGTrack track,boolean mute){
		track.setMute(mute);
		track.setSolo(!track.isMute() && track.isSolo());
	}
	
	public void changeInfo(TGTrack track,String name,TGColor color,int offset){
		track.setName(name);
		track.setOffset(offset);
		track.getColor().setR(color.getR());
		track.getColor().setG(color.getG());
		track.getColor().setB(color.getB());
	}
	
	public void changeInstrumentStrings(TGTrack track,List<TGString> strings){
		if(strings.size() < track.getStrings().size()){
			removeNotesAfterString(track,strings.size());
		}
		track.setStrings(strings);
	}
	
	public void removeNotesAfterString(TGTrack track,int string){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
            songManager.getMeasureManager().removeNotesAfterString(measure,string);
		}
	}
	
	public void changeInstrument(TGTrack track,int instrument,boolean percussion){
		track.getChannel().setInstrument((short)instrument);
		if(percussion){
			TGChannel.setPercussionChannel(track.getChannel());
			track.setStrings(TGSongManager.createPercussionStrings(songManager.getFactory(),track.getStrings().size()));
		}else{
			if(track.getChannel().isPercussionChannel()){
				TGChannel tempChannel = songManager.getFreeChannel((short) instrument, false);
				track.getChannel().setChannel(tempChannel.getChannel());
				track.getChannel().setEffectChannel(tempChannel.getEffectChannel());
			}
		}
        songManager.updateChannel(track.getChannel());
	}
	
	public void autoCompleteSilences(TGTrack track){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
            songManager.getMeasureManager().autoCompleteSilences(measure);
		}
	}
	
	public void orderBeats(TGTrack track){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
            songManager.getMeasureManager().orderBeats(measure);
		}
	}
	
	public void transposeNotes(TGTrack track, int transposition , boolean tryKeepString, boolean applyToChords, int applyToString){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
            songManager.getMeasureManager().transposeNotes(measure, transposition, tryKeepString, applyToChords, applyToString );
		}
	}
	
	public void transposeNotes(TGTrack track, int[] transpositionStrings , boolean tryKeepString , boolean applyToChords ){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
            songManager.getMeasureManager().transposeNotes(measure, transpositionStrings, tryKeepString , applyToChords);
		}
	}
	
	/**
	 * Retorna true si es el primer compas
	 */
	public boolean isFirstMeasure(TGMeasure measure){
		return (measure.getNumber() == 1);
	}
	
	/**
	 * Retorna true si es el ultimo compas
	 */
	public boolean isLastMeasure(TGMeasure measure){
		return (songManager.getSong().countMeasureHeaders() == measure.getNumber());
	}
}
