/*
 * Created on 23-nov-2005
 *
 */
package org.herac.tuxguitar.song.managers;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author julian
 *
 */
public class TGSongManager {
	public static final short MAX_CHANNELS = 16;
	
	private TGFactory factory;
	private TGSong song;
	private TGTrackManager trackManager;
	private TGMeasureManager measureManager;
	
	public TGSongManager(){
		this(new TGFactory());
	}
	
	public TGSongManager(TGFactory factory){
		this.factory = factory;
	}
	
	public TGFactory getFactory(){
		return factory;
	}
	
	public void setFactory(TGFactory factory){
		this.factory = factory;
	}
	
	public TGTrackManager getTrackManager(){
		if(trackManager == null){
            trackManager = new TGTrackManager(this);
		}
		return trackManager;
	}
	
	public TGMeasureManager getMeasureManager(){
		if(measureManager == null){
            measureManager = new TGMeasureManager(this);
		}
		return measureManager;
	}
	
	public void setSongName(String name){
        song.setName(name);
	}
	
	public TGSong getSong(){
		return song;
	}
	
	public void clearSong(){
		if(song != null){
            song.clear();
		}
	}
	
	public void setSong(TGSong song){
		if(song != null){
            clearSong();
			this.song = song;
		}
	}
	
	public void setProperties(String name,String artist,String album,String author,String date,String copyright,String writer,String transcriber,String comments){
        song.setName(name);
        song.setArtist(artist);
        song.setAlbum(album);
        song.setAuthor(author);
        song.setDate(date);
        song.setCopyright(copyright);
        song.setWriter(writer);
        song.setTranscriber(transcriber);
        song.setComments(comments);
	}
	
	public void addTrack(TGTrack trackToAdd){
        orderTracks();
		int addIndex = -1;
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			if(addIndex == -1 && track.getNumber() == trackToAdd.getNumber()){
				addIndex = i;
			}
			if(addIndex >= 0){
				track.setNumber(track.getNumber() + 1);
			}
		}
		if(addIndex < 0){
			addIndex = song.countTracks();
		}
        song.addTrack(addIndex, trackToAdd);
	}
	
	public void removeTrack(int number){
		int nextNumber = number;
		TGTrack trackToRemove = null;
		orderTracks();
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack currTrack = it.next();
			if(trackToRemove == null && currTrack.getNumber() == nextNumber){
				trackToRemove = currTrack;
			}else if(currTrack.getNumber() == (nextNumber + 1)){
				currTrack.setNumber(nextNumber);
				nextNumber ++;
			}
			
		}
        song.removeTrack(trackToRemove);
	}
	
	private void orderTracks(){
		for(int i = 0;i < song.countTracks();i++){
			TGTrack minTrack = null;
			for(int trackIdx = i;trackIdx < song.countTracks();trackIdx++){
				TGTrack track = song.getTrack(trackIdx);
				if(minTrack == null || track.getNumber() < minTrack.getNumber()){
					minTrack = track;
				}
			}
            song.moveTrack(i, minTrack);
		}
	}
	
	public TGSong newSong(){
		TGSong song = factory.newSong();
		
		TGMeasureHeader header = factory.newHeader();
		header.setNumber(1);
		header.setStart(TGDuration.QUARTER_TIME);
		header.getTimeSignature().setNumerator(4);
		header.getTimeSignature().getDenominator().setValue(TGDuration.QUARTER);
		song.addMeasureHeader(header);
		
		TGMeasure measure = factory.newMeasure(header);
		
		TGTrack track = factory.newTrack();
		track.setNumber(1);
		track.setName("Track 1");
		track.addMeasure(measure);
		track.getChannel().setChannel((short)0);
		track.getChannel().setEffectChannel((short)1);
		track.setStrings(createDefaultInstrumentStrings());
		TGColor.RED.copy(track.getColor());
		song.addTrack(track);
		
		return song;
	}
	
	public int getNextTrackNumber(){
		return (song.countTracks() + 1);
	}
	
	public TGChannel getFreeChannel(short instrument,boolean isPercussion){
		if(isPercussion){
			return TGChannel.newPercussionChannel(factory);
		}
		short normalChannel = -1;
		short effectChannel = -1;
		
		boolean[] usedChannels = getUsedChannels();
		boolean[] usedEffectChannels = getUsedEffectChannels();
		for(short i = 0;i < MAX_CHANNELS;i++){
			if(!TGChannel.isPercussionChannel(i) && !usedChannels[i] && !usedEffectChannels[i]){
				normalChannel = (normalChannel < 0)?i:normalChannel;
				effectChannel = (effectChannel < 0 && i != normalChannel)?i:effectChannel;
			}
		}
		if(normalChannel < 0 || effectChannel < 0){
			if(normalChannel >= 0 ){
				effectChannel = normalChannel;
			}else{
				TGChannel songChannel = getLastTrack().getChannel();
				return songChannel.clone(factory);
			}
		}
		TGChannel channel = factory.newChannel();
		channel.setChannel(normalChannel);
		channel.setEffectChannel(effectChannel);
		channel.setInstrument(instrument);
		return channel;
	}
	
	public boolean[] getUsedEffectChannels(){
		boolean[] channels = new boolean[MAX_CHANNELS];
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			channels[track.getChannel().getEffectChannel()] = true;
		}
		return channels;
	}
	
	public boolean[] getUsedChannels(){
		boolean[] channels = new boolean[MAX_CHANNELS];
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			channels[track.getChannel().getChannel()] = true;
		}
		return channels;
	}
	
	public TGChannel getUsedChannel(int channel){
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			if(channel == track.getChannel().getChannel()){
				return track.getChannel().clone(factory);
			}
		}
		return null;
	}
	
	public int countTracksForChannel(int channel){
		int count = 0;
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			if(channel == track.getChannel().getChannel()){
				count ++;
			}
		}
		return count;
	}
	
	public void updateChannel(TGChannel channel){
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			if(channel.getChannel() == track.getChannel().getChannel()){
				track.setChannel(channel.clone(factory));
			}
		}
	}
	
	public TGTrack getTrack(int number){
		TGTrack track = null;
		for (int i = 0; i < song.countTracks(); i++) {
			TGTrack currTrack = song.getTrack(i);
			if(currTrack.getNumber() == number){
				track = currTrack;
				break;
			}
		}
		return track;
	}
	
	public TGTrack getFirstTrack(){
		TGTrack track = null;
		if(!song.isEmpty()){
			track = song.getTrack(0);
		}
		return track;
	}
	
	public TGTrack getLastTrack(){
		TGTrack track = null;
		if(!song.isEmpty()){
			track = song.getTrack(song.countTracks() - 1);
		}
		return track;
	}
	
	public TGTrack cloneTrack(TGTrack track){
		TGTrack clone = track.clone(factory, song);
		clone.setNumber(getNextTrackNumber());
		addTrack(clone);
		return clone;
	}
	
	public boolean moveTrackUp(TGTrack track){
		if(track.getNumber() > 1){
			TGTrack prevTrack = getTrack(track.getNumber() - 1);
			prevTrack.setNumber(prevTrack.getNumber() + 1);
			track.setNumber(track.getNumber() - 1);
			orderTracks();
			return true;
		}
		return false;
	}
	
	public boolean moveTrackDown(TGTrack track){
		if(track.getNumber() < song.countTracks()){
			TGTrack nextTrack = getTrack(track.getNumber() + 1);
			nextTrack.setNumber(nextTrack.getNumber() - 1);
			track.setNumber(track.getNumber() + 1);
			orderTracks();
			return true;
		}
		return false;
	}
	
	private TGTrack makeNewTrack(){
		TGTrack track = factory.newTrack();
		track.setNumber(getNextTrackNumber());
		track.setName("Track " + track.getNumber());
		//measures
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			TGMeasure measure = factory.newMeasure(header);
			track.addMeasure(measure);
		}
		track.setStrings(createDefaultInstrumentStrings());
		getFreeChannel(TGChannel.DEFAULT_INSTRUMENT,false).copy(track.getChannel());
		TGColor.RED.copy(track.getColor());
		return track;
	}
	
	public TGTrack createTrack(){
		if(song.isEmpty()){
			setSong(newSong());
			return getLastTrack();
		}
		TGTrack track = makeNewTrack();
		addTrack(track);
		return track;
	}
	
	public void removeTrack(TGTrack track){
		removeTrack(track.getNumber());
	}
	
	public void changeTimeSignature(long start,TGTimeSignature timeSignature,boolean toEnd){
		changeTimeSignature(getMeasureHeaderAt(start),timeSignature,toEnd);
	}
	
	public void changeTimeSignature(TGMeasureHeader header,TGTimeSignature timeSignature,boolean toEnd){
		//asigno el nuevo ritmo
		timeSignature.copy(header.getTimeSignature());
		
		long nextStart = header.getStart() + header.getLength();
		List<TGMeasureHeader> measures = getMeasureHeadersBeforeEnd(header.getStart() + 1);
		Iterator<TGMeasureHeader> it = measures.iterator();
		while(it.hasNext()){
			TGMeasureHeader nextHeader = it.next();
			
			long theMove = nextStart - nextHeader.getStart();
			
			//moveMeasureComponents(nextHeader,theMove);
			moveMeasureHeader(nextHeader,theMove,0);
			
			if(toEnd){
				timeSignature.copy(nextHeader.getTimeSignature());
			}
			nextStart = nextHeader.getStart() + nextHeader.getLength();
		}
		moveOutOfBoundsBeatsToNewMeasure(header.getStart());
	}
	
	public void moveOutOfBoundsBeatsToNewMeasure(long start){
		Iterator<TGTrack> it = song.getTracks();
		while( it.hasNext() ){
			TGTrack track = it.next();
			getTrackManager().moveOutOfBoundsBeatsToNewMeasure(track, start);
		}
	}
	
	/*
	public void changeTimeSignature(TGMeasureHeader header,TGTimeSignature timeSignature,boolean toEnd){
		//asigno el nuevo ritmo
		timeSignature.copy(header.getTimeSignature());
		
		long nextStart = header.getStart() + header.getLength();
		List measures = getMeasureHeadersBeforeEnd(header.getStart() + 1);
		Iterator it = measures.iterator();
		while(it.hasNext()){
			TGMeasureHeader nextHeader = (TGMeasureHeader)it.next();
			
			long theMove = nextStart - nextHeader.getStart();
			
			moveMeasureComponents(nextHeader,theMove);
			moveMeasureHeader(nextHeader,theMove,0);
			
			if(toEnd){
				timeSignature.copy(nextHeader.getTimeSignature());
			}
			nextStart = nextHeader.getStart() + nextHeader.getLength();
		}
	}
	*/
	public void changeTripletFeel(long start,int tripletFeel,boolean toEnd){
		changeTripletFeel(getMeasureHeaderAt(start),tripletFeel,toEnd);
	}
	
	public void changeTripletFeel(TGMeasureHeader header,int tripletFeel,boolean toEnd){
		//asigno el nuevo tripletFeel
		header.setTripletFeel(tripletFeel);
		
		if(toEnd){
			List<TGMeasureHeader> measures = getMeasureHeadersBeforeEnd(header.getStart() + 1);
			Iterator<TGMeasureHeader> it = measures.iterator();
			while(it.hasNext()){
				TGMeasureHeader nextHeader = it.next();
				nextHeader.setTripletFeel(tripletFeel); 
			}
		}
	}
	
	public void changeTempos(long start,TGTempo tempo,boolean toEnd){
		changeTempos(getMeasureHeaderAt(start),tempo,toEnd);
	}
	
	public void changeTempos(TGMeasureHeader header,TGTempo tempo,boolean toEnd){
		int oldValue = header.getTempo().getValue();
		Iterator<TGMeasureHeader> it = getMeasureHeadersAfter(header.getNumber() - 1).iterator();
		while(it.hasNext()){
			TGMeasureHeader nextHeader = it.next();
			if(toEnd || nextHeader.getTempo().getValue() == oldValue){
				changeTempo(nextHeader,tempo);
			}else{
				break;
			}
		}
	}
	
	public void changeTempo(TGMeasureHeader header,TGTempo tempo){
		tempo.copy(header.getTempo());
	}
	
	public void changeOpenRepeat(long start){
		TGMeasureHeader header = getMeasureHeaderAt(start);
		header.setRepeatOpen(!header.isRepeatOpen());
	}
	
	public void changeCloseRepeat(long start,int repeatClose){
		TGMeasureHeader header = getMeasureHeaderAt(start);
		header.setRepeatClose(repeatClose);
	}
	
	public void changeAlternativeRepeat(long start,int repeatAlternative){
		TGMeasureHeader header = getMeasureHeaderAt(start);
		header.setRepeatAlternative(repeatAlternative);
	}
	
	public TGMeasureHeader addNewMeasureBeforeEnd(){
		TGMeasureHeader lastHeader = getLastMeasureHeader();
		TGMeasureHeader header = factory.newHeader();
		header.setNumber((lastHeader.getNumber() + 1));
		header.setStart((lastHeader.getStart() + lastHeader.getLength()));
		header.setRepeatOpen(false);
		header.setRepeatClose(0);
		header.setTripletFeel(lastHeader.getTripletFeel());
		lastHeader.getTimeSignature().copy(header.getTimeSignature());
		lastHeader.getTempo().copy(header.getTempo());
        song.addMeasureHeader(header);
		
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().addNewMeasureBeforeEnd(track,header);
		}
		return header;
	}
	
	public void addNewMeasure(int number){
		//Obtengo un clon para el nuevo Header.
		TGMeasureHeader header;
		if(number == 1){
			header = getMeasureHeader(number).clone(factory);
		}else{
			header = getMeasureHeader((number - 1)).clone(factory);
			header.setStart(header.getStart() + header.getLength());
			header.setNumber(header.getNumber() + 1);
		}
		header.setMarker(null);
		header.setRepeatOpen(false);
		header.setRepeatAlternative(0);
		header.setRepeatClose(0);
		
		//Si hay Headers siguientes los muevo
		TGMeasureHeader nextHeader = getMeasureHeader(number);
		if(nextHeader != null){
			moveMeasureHeaders(getMeasureHeadersBeforeEnd(nextHeader.getStart()),header.getLength(),1,true);
		}
		
		//Agrego el header a la lista
		addMeasureHeader( (header.getNumber() - 1) ,header);
		
		//Agrego los compases en todas las pistas
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().addNewMeasure(track,header);
		}
	}
	
	public List<TGMeasure> getMeasures(long start){
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			TGMeasure measure = getTrackManager().getMeasureAt(track,start);
			if(measure != null){
				measures.add(measure);
			}
		}
		return measures;
	}
	
	public TGTrack replaceTrack(TGTrack track){
		TGTrack current = getTrack(track.getNumber());
		if(current != null){
			track.copy(factory, song, current);
		}
		return current;
	}
	/*
	public TGSongSegment copyMeasures(int m1, int m2){
		TGSongSegment segment = new TGSongSegment();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(getSong().countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add( getMeasureHeader(number) );
		}
		Iterator it = getSong().getTracks();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			List measures = getTrackManager().copyMeasures(track,number1,number2);
			segment.addTrack(track.getNumber(),measures);
		}
		return segment.clone(getFactory());
	}
	
	public TGSongSegment copyMeasures(int m1, int m2,TGTrack track){
		TGSongSegment segment = new TGSongSegment();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(getSong().countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add( getMeasureHeader(number) );
		}
		List measures = getTrackManager().copyMeasures(track,number1,number2);
		segment.addTrack(track.getNumber(),measures);
	
		return segment.clone(getFactory());
	}
	
	public void insertMeasures(TGSongSegment segment,int fromNumber,long move){
		List headers = new ArrayList();
		moveMeasureHeaders(segment.getHeaders(),move,0,false);
		
		int headerNumber = fromNumber;
		Iterator it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			header.setNumber(headerNumber);
			headers.add(header);
			headerNumber ++;
		}
		long start = ((TGMeasureHeader)headers.get(0)).getStart();
		long end = ((TGMeasureHeader)headers.get(headers.size() - 1)).getStart() + ((TGMeasureHeader)headers.get(headers.size() - 1)).getLength();
		List headersBeforeEnd = getMeasureHeadersBeforeEnd(start);
		moveMeasureHeaders(headersBeforeEnd,end - start,headers.size(),true);
		
		it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			addMeasureHeader(header.getNumber() - 1,header);
		}
		
		it = getSong().getTracks();
		while (it.hasNext()) {
			TGTrack currTrack = (TGTrack) it.next();
			List measures = null;
			
			Iterator tracks = segment.getTracks().iterator();
			while(tracks.hasNext()){
				TGTrackSegment tSegment = (TGTrackSegment)tracks.next();
				if(tSegment.getTrack() == currTrack.getNumber()){
					measures = tSegment.getMeasures();
					break;
				}
			}
			if(measures == null){
				measures = getEmptyMeasures(((TGTrackSegment)segment.getTracks().get(0)).getMeasures());
			}
			
			for(int i = 0;i < measures.size();i++){
				TGMeasure measure = (TGMeasure)measures.get(i);
				measure.setHeader((TGMeasureHeader)headers.get(i));
				getMeasureManager().moveAllComponents(measure,move);
			}
			getTrackManager().insertMeasures(currTrack,measures);
		}
	}
	
	private List getEmptyMeasures(List measures) {
		List emptyMeasures = new ArrayList();
		
		Iterator it = measures.iterator();
		while (it.hasNext()) {
			TGMeasure measure = (TGMeasure) it.next();
			TGMeasure emptyMeasure = getFactory().newMeasure(null);
			emptyMeasure.setClef(measure.getClef());
			emptyMeasure.setKeySignature(measure.getKeySignature());
			emptyMeasures.add(emptyMeasure);
		}
		return emptyMeasures;
	}
	*/
	/*
	public void replaceMeasures(TGSongSegment tracksMeasures,long move) {
		List measureHeaders = new ArrayList();
		moveMeasureHeaders(tracksMeasures.getHeaders(),move,0,false);
		Iterator it = tracksMeasures.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			TGMeasureHeader replace = replaceMeasureHeader(header);
			
			Iterator nextHeaders = getMeasureHeadersAfter(replace.getNumber()).iterator();
			long nextStart =  (replace.getStart() + replace.getLength());
			while(nextHeaders.hasNext()){
				TGMeasureHeader next = (TGMeasureHeader)nextHeaders.next();
				moveMeasureComponents(next, (nextStart - next.getStart() ));
				moveMeasureHeader(next, (nextStart - next.getStart() ) , 0);
				nextStart = (next.getStart() + next.getLength());
			}
			measureHeaders.add(replace);
		}
		
		it = tracksMeasures.getTracks().iterator();
		while(it.hasNext()){
			TGTrackSegment trackMeasure = (TGTrackSegment)it.next();
			
			TGTrack currTrack = getTrack(trackMeasure.getTrack());
			List measures = trackMeasure.getMeasures();
			for(int i = 0;i < measures.size();i++){
				TGMeasure measure = (TGMeasure)measures.get(i);
				measure.setHeader((TGMeasureHeader)measureHeaders.get(i));
				getMeasureManager().moveAllComponents(measure,move);
				getTrackManager().replaceMeasure(currTrack,measure);
			}
		}
	}
	*/
	public TGMeasureHeader getFirstMeasureHeader(){
		TGMeasureHeader firstHeader = null;
		for(int i = 0;i < song.countMeasureHeaders();i++){
			TGMeasureHeader currHeader = song.getMeasureHeader(i);
			if(firstHeader == null || (currHeader.getStart() < firstHeader.getStart())){
				firstHeader = currHeader;
			}
		}
		return firstHeader;
	}
	
	public TGMeasureHeader getLastMeasureHeader(){
		int lastIndex = song.countMeasureHeaders() - 1;
		return song.getMeasureHeader(lastIndex);
	}
	
	public TGMeasureHeader getPrevMeasureHeader(TGMeasureHeader header){
		int prevIndex = header.getNumber() - 1;
		if(prevIndex > 0){
			return song.getMeasureHeader(prevIndex - 1);
		}
		return null;
	}
	
	public TGMeasureHeader getNextMeasureHeader(TGMeasureHeader header){
		int nextIndex = header.getNumber();
		if(nextIndex < song.countMeasureHeaders()){
			return song.getMeasureHeader(nextIndex);
		}
		return null;
	}
	
	public TGMeasureHeader getMeasureHeaderAt(long start){
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			long measureStart = header.getStart();
			long measureLength = header.getLength();
			if(start >= measureStart && start < measureStart + measureLength){
				return header;
			}
		}
		return null;
	}
	
	public TGMeasureHeader getMeasureHeader(int number){
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			if(header.getNumber() == number){
				return header;
			}
		}
		return null;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasureHeader> getMeasureHeadersBeforeEnd(long fromStart) {
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if (header.getStart() >= fromStart) {
				headers.add(header);
			}
		}
		return headers;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasureHeader> getMeasureHeadersAfter(int number) {
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if (header.getNumber() > number) {
				headers.add(header);
			}
		}
		return headers;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasureHeader> getMeasureHeadersBetween(long p1,long p2) {
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if ((header.getStart() + header.getLength()) > p1  &&  header.getStart() < p2) {
				headers.add(header);
			}
		}
		return headers;
	}
	
	public void removeLastMeasure(){
		removeLastMeasureHeader();
	}
	
	public void removeMeasure(long start){
		removeMeasureHeader(start);
	}
	
	public void removeMeasure(int number){
		removeMeasureHeader(number);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasureHeader(TGMeasureHeader measure){
        song.addMeasureHeader(measure);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasureHeader(int index,TGMeasureHeader measure){
        song.addMeasureHeader(index, measure);
	}
	
	public void removeMeasureHeaders(int n1,int n2){
		for(int i = n1; i <= n2; i ++){
			TGMeasureHeader measure = getMeasureHeader(n1);
			removeMeasureHeader(measure);
		}
		/*
		Iterator it = getMeasureHeadersBetween(p1,p2).iterator();
		while(it.hasNext()){
			TGMeasureHeader measure = (TGMeasureHeader)it.next();
			removeMeasureHeader(measure);
		} */
	}
	
	public void removeLastMeasureHeader(){
		removeMeasureHeader(getLastMeasureHeader());
	}
	
	public void removeMeasureHeader(long start){
		removeMeasureHeader(getMeasureHeaderAt(start));
	}
	
	public void removeMeasureHeader(int number){
		removeMeasureHeader(getMeasureHeader(number));
	}
	
	public void removeMeasureHeader(TGMeasureHeader header){
		long start = header.getStart();
		long length = header.getLength();
		
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().removeMeasure(track,start);
		}
		moveMeasureHeaders(getMeasureHeadersBeforeEnd(start + 1),-length,-1,true);
        song.removeMeasureHeader(header.getNumber() - 1);
	}
	
	public TGMeasureHeader replaceMeasureHeader(TGMeasureHeader newMeasure){
		TGMeasureHeader header = getMeasureHeaderAt(newMeasure.getStart());
		header.makeEqual(newMeasure.clone(factory));
		return header;
	}
	
	public void moveMeasureHeaders(List<TGMeasureHeader> headers,long theMove,int numberMove,boolean moveComponents) {
		if(moveComponents){
			Iterator<TGMeasureHeader> it = headers.iterator();
			while(it.hasNext()){
				TGMeasureHeader header = it.next();
				moveMeasureComponents(header,theMove);
			}
		}
		Iterator<TGMeasureHeader> it = headers.iterator();
		while (it.hasNext()) {
			TGMeasureHeader header = it.next();
			moveMeasureHeader(header,theMove,numberMove);
		}
	}
	
	/**
	 * Mueve el compas
	 */
	public void moveMeasureHeader(TGMeasureHeader header,long theMove,int numberMove){
		header.setNumber(header.getNumber() + numberMove);
		header.setStart(header.getStart() + theMove);
	}
	
	/**
	 * Mueve el compas
	 */
	public void moveMeasureComponents(TGMeasureHeader header,long theMove){
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().moveMeasure(getTrackManager().getMeasure(track,header.getNumber()),theMove);
		}
	}
	
	/** 
	 * Retorna true si el start esta en el rango del compas
	 */
	public boolean isAtPosition(TGMeasureHeader header,long start){
		return (start >= header.getStart() && start < header.getStart() + header.getLength());
	}
	
	public TGMarker updateMarker(int measure,String title,TGColor color){
		TGMeasureHeader header = getMeasureHeader(measure);
		if(header != null){
			if(!header.hasMarker()){
				header.setMarker(factory.newMarker());
			}
			header.getMarker().setMeasure(measure);
			header.getMarker().setTitle(title);
			header.getMarker().getColor().setR(color.getR());
			header.getMarker().getColor().setG(color.getG());
			header.getMarker().getColor().setB(color.getB());
			return header.getMarker(); 
		}
		return null;
	}
	
	public TGMarker updateMarker(TGMarker marker){
		return updateMarker(marker.getMeasure(),marker.getTitle(),marker.getColor());
	}
	
	public void removeMarker(TGMarker marker){
		if(marker != null){
			removeMarker(marker.getMeasure());
		}
	}
	
	public void removeMarker(int number){
		TGMeasureHeader header = getMeasureHeader(number);
		if(header != null && header.hasMarker()){
			header.setMarker(null);
		}
	}
	
	public void removeAllMarkers(){
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				header.setMarker(null);
			}
		}
	}
	
	public List<TGMarker> getMarkers(){
		List<TGMarker> markers = new ArrayList<TGMarker>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				markers.add(header.getMarker());
			}
		}
		return markers;
	}
	
	public TGMarker getMarker(int number){
		TGMeasureHeader header = getMeasureHeader(number);
		if(header != null && header.hasMarker()){
			return header.getMarker();
		}
		return null;
	}
	
	public TGMarker getPreviousMarker(int from){
		TGMeasureHeader previous = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker() && header.getNumber() < from){
				if(previous == null || previous.getNumber() < header.getNumber()){
					previous = header;
				}
			}
		}
		return (previous != null)?previous.getMarker():null;
	}
	
	public TGMarker getNextMarker(int from){
		TGMeasureHeader next = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker() && header.getNumber() > from){
				if(next == null || next.getNumber() > header.getNumber()){
					next = header;
				}
			}
		}
		return (next != null)?next.getMarker():null;
	}
	
	public TGMarker getFirstMarker(){
		TGMeasureHeader first = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				if(first == null || header.getNumber() < first.getNumber()){
					first = header;
				}
			}
		}
		return (first != null)?first.getMarker():null;
	}
	
	public TGMarker getLastMarker(){
		TGMeasureHeader next = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				if(next == null || header.getNumber() > next.getNumber()){
					next = header;
				}
			}
		}
		return (next != null)?next.getMarker():null;
	}
	
	public void autoCompleteSilences(){
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().autoCompleteSilences(track);
		}
	}
	
	public void orderBeats(){
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().orderBeats(track);
		}
	}
	
	public List<TGString> createDefaultInstrumentStrings(){
		List<TGString> strings = new ArrayList(6);
		strings.add(newString(factory,1, 64));
		strings.add(newString(factory,2, 59));
		strings.add(newString(factory,3, 55));
		strings.add(newString(factory,4, 50));
		strings.add(newString(factory,5, 45));
		strings.add(newString(factory,6, 40));
		return strings;
	}
	
	public static List<TGString> createPercussionStrings(TGFactory factory,int stringCount){
		List<TGString> strings = new ArrayList<TGString>(stringCount);
		for(int i = 1;i <= stringCount; i++){
			strings.add(newString(factory,i, 0));
		}
		return strings;
	}
	
	public static TGString newString(TGFactory factory,int number,int value){
		TGString string = factory.newString();
		string.setNumber(number);
		string.setValue(value);
		return string;
	}
	
	public static long getDivisionLength(TGMeasureHeader header){
		long defaultLenght = TGDuration.QUARTER_TIME;
		int denominator = header.getTimeSignature().getDenominator().getValue();
		switch(denominator){
			case TGDuration.EIGHTH:
				if(header.getTimeSignature().getNumerator() % 3 == 0){
					defaultLenght += TGDuration.QUARTER_TIME / 2;
				}
				break;
		}
		return defaultLenght;
	}
}
