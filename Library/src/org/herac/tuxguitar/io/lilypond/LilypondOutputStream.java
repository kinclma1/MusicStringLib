package org.herac.tuxguitar.io.lilypond;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;

public class LilypondOutputStream {
	//todo drum notes, bass clef
	// private static final String LILYPOND_VERSION = "2.10.5";
	private String LILYPOND_VERSION;

	private static final String[] LILYPOND_SHARP_NOTES = new String[]{"c","cis","d","dis","e","f","fis","g","gis","a","ais","b"};
	private static final String[] LILYPOND_FLAT_NOTES = new String[]{"c","des","d","ees","e","f","ges","g","aes","a","bes","b"};
	
	private static final String[] LILYPOND_KEY_SIGNATURES = new String[]{ "c","g","d","a","e","b","fis","cis","f","bes","ees","aes", "des", "ges","ces" };
	
	private static final String INDENT = new String("   ");
	
	// anything over high C should be printed 8vb
	private static final int MAX_PITCH = 72;
	
	private TGSongManager manager;
	
	private PrintWriter writer;
	
	private LilypondSettings settings;
	
	private LilypondTempData temp;
	
	public LilypondOutputStream(OutputStream stream,LilypondSettings settings){
		this.writer = new PrintWriter(stream);
		this.temp = new LilypondTempData();
		this.settings = settings;
		LILYPOND_VERSION = settings.getLilypondVersion();
	}
	
	public void writeSong(TGSong song){
		this.manager = new TGSongManager();
		this.manager.setSong(song);

		this.addVersion();
		this.addPaper(song);
		this.addLayout();
		this.addSongDefinitions(song);
		this.addSong(song);
		
		this.writer.flush();
		this.writer.close();
	}
	
	private void addVersion(){
		this.writer.println("\\version \"" + LILYPOND_VERSION + "\"");
	}

	private void addPaper(TGSong song){
		this.writer.println("\\paper {");
		
		this.writer.println(indent(1) + "indent = #" + (this.addTrackTitleOnGroup(song) ? 30 : 0));

		if ( LILYPOND_VERSION.compareTo("2.11.60") < 0) {
			this.writer.println(indent(1) + "printallheaders = #" + getLilypondBoolean(true));
		} else {
			this.writer.println(indent(1) + "print-all-headers = #" + getLilypondBoolean(true));
		}
		this.writer.println(indent(1) + "ragged-right = #" + getLilypondBoolean(false));
		this.writer.println(indent(1) + "ragged-bottom = #" + getLilypondBoolean(true));
		this.writer.println("}");
	}
	
	private void addHeader(TGSong song, String instrument, int indent){
		this.writer.println(indent(indent) + "\\header {");
		this.writer.println(indent(indent + 1) + "title = \"" + song.getName() + "\" ");
		this.writer.println(indent(indent + 1) + "composer = \"" + song.getAuthor() + "\" ");
		if(this.settings.isTrackNameEnabled() && !this.addTrackTitleOnGroup(song) && instrument != null){
			this.writer.println(indent(indent + 1) + "instrument = \"" + instrument + "\" ");
		}
		this.writer.println(indent(indent) + "}");
	}
	
	private void addLayout(){
		this.writer.println("\\layout {");
		this.writer.println(indent(1) + "\\context { \\Score");
		this.writer.println(indent(2) + "\\override MetronomeMark #'padding = #'5");
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\Staff");
		this.writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		this.writer.println(indent(2) + "\\override StringNumber #'transparent = #" + getLilypondBoolean(true));
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\TabStaff");
		this.writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		this.writer.println(indent(2) + "\\override Stem #'transparent = #" + getLilypondBoolean(this.settings.isScoreEnabled()));
		this.writer.println(indent(2) + "\\override Beam #'transparent = #" + getLilypondBoolean(this.settings.isScoreEnabled()));
		this.writer.println(indent(1) + "}");
		if( this.settings.isScoreEnabled() ){
			this.writer.println(indent(1) + "\\context { \\TabVoice");
			this.writer.println(indent(2) + "\\override Tie #'stencil = ##f");
			this.writer.println(indent(1) + "}");
		}
		this.writer.println(indent(1) + "\\context { \\StaffGroup");
		this.writer.println(indent(2) + "\\consists \"Instrument_name_engraver\"");
		this.writer.println(indent(1) + "}");
		this.writer.println("}");
	}
	
	private void addSongDefinitions(TGSong song){
		for(int i = 0; i < song.countTracks(); i ++){
			TGTrack track = song.getTrack(i);
			String id = this.trackID(i,"");
			this.temp.reset();
			this.addMusic(track,id);
			this.addScoreStaff(track,id);
			this.addStaffGroup(track,id);
		}
	}
	
	private void addSong(TGSong song){
		int trackCount = song.countTracks();
		if(this.settings.isTrackGroupEnabled() && trackCount > 1){
			this.writer.println("\\score {");
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS){
				this.writer.println(indent(1) + "<<");
			}
		}
		
		for(int i = 0; i < trackCount; i ++){
			TGTrack track = song.getTrack(i);
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS || this.settings.getTrack() == track.getNumber()){
				if(!this.settings.isTrackGroupEnabled() || trackCount == 1){
					this.writer.println("\\score {");
				}
				this.writer.println(indent(1) + "\\" + this.trackID(i,"StaffGroup"));
				if(!this.settings.isTrackGroupEnabled() || trackCount == 1){
					this.addHeader(song,track.getName(), 1);
					this.writer.println("}");
				}
			}
		}
	}
	
	private void addMusic(TGTrack track,String id){
		for( int voice = 0 ; voice < TGBeat.MAX_VOICES ; voice ++ ){
			this.writer.println(trackVoiceID(voice,id,"Music") + " = #(define-music-function (parser location inTab) (boolean?)");
			this.writer.println("#{");
			if( this.isVoiceAvailable( track , voice ) ){
				TGMeasure previous = null;
				int count = track.countMeasures();
				for(int i = 0; i < count; i ++){
					TGMeasure measure = track.getMeasure(i);

					int measureFrom = this.settings.getMeasureFrom();
					int measureTo = this.settings.getMeasureTo();
					if((measureFrom <= measure.getNumber() || measureFrom == LilypondSettings.FIRST_MEASURE) && (measureTo >= measure.getNumber() || measureTo == LilypondSettings.LAST_MEASURE )){
						this.addMeasure(measure, previous, voice, 1, (i == (count - 1)));
						previous = measure;
					}
				}
				this.writer.println(indent(1) + "\\bar \"|.\"");
				this.writer.println(indent(1) + "\\pageBreak");
			}
			this.writer.println("#})");
		}
	}
	
	private void addScoreStaff(TGTrack track,String id){
		boolean addLyrics = (this.settings.isLyricsEnabled() && !this.settings.isTablatureEnabled() && !track.getLyrics().isEmpty());
		boolean addChordDiagrams = this.settings.isChordDiagramEnabled();
		boolean addTexts = this.settings.isTextEnabled();
		
		this.writer.println(id + "Staff = \\new Staff <<" );
		
		for( int v = 0 ; v < TGBeat.MAX_VOICES ; v ++ ){
			String vId =  trackVoiceID(v, id, "Music") ;
			this.writer.println(indent(1) + "\\context Voice = \"" + vId + "\" {");
			if(!addChordDiagrams){
				this.writer.println(indent(2) + "\\removeWithTag #'chords");
			}
			if(!addTexts){
				this.writer.println(indent(2) + "\\removeWithTag #'texts");
			}
			this.writer.println(indent(2) + "\\" + vId + " #" + getLilypondBoolean( false ) );
			this.writer.println(indent(1) + "}");
		}
		
		if(addLyrics){
			this.writer.println(indent(1) + "\\new Lyrics \\lyricsto \"" + trackVoiceID(0, id, "Music") + "\" \\" + id + "Lyrics");
		}
		
		this.writer.println(">>");
	}

	private void addStaffGroup(TGTrack track,String id){
		this.writer.println(id + "StaffGroup = \\new StaffGroup <<");
		if(this.addTrackTitleOnGroup(track.getSong())){
			this.writer.println(indent(1) + "\\set StaffGroup.instrumentName = #\"" + track.getName()  + "\"");
		}
		if(this.settings.isScoreEnabled()){
			this.writer.println(indent(1) + "\\" + id + "Staff");
		}
		this.writer.println(">>");
	}
	
	private void addMeasure(TGMeasure measure,TGMeasure previous,int voice,int indent,boolean isLast){
		if(previous == null || measure.getTempo().getValue() != previous.getTempo().getValue()){
			this.addTempo(measure.getTempo(),indent);
		}
		
		if(previous == null || measure.getClef() != previous.getClef()){
			this.addClef(measure.getClef(),indent);
		}
		if(previous == null || measure.getKeySignature() != previous.getKeySignature()){
			this.addKeySignature(measure.getKeySignature(),indent);
		}
		
		if(previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature())){
			this.addTimeSignature(measure.getTimeSignature(),indent);
		}

		// Set the specific voice
		this.addMeasureVoice(measure,voice, (previous == null), indent);

		this.addMeasureComponents(measure, voice, indent);
	}
	
	private void addTempo(TGTempo tempo,int indent){
		this.writer.println(indent(indent) + "\\tempo 4=" + tempo.getValue());
	}
	
	private void addTimeSignature(TGTimeSignature ts,int indent){
		this.writer.println(indent(indent) + "\\time " + ts.getNumerator() + "/" + ts.getDenominator().getValue());
	}
	
	private void addKeySignature(int keySignature,int indent){
		if(keySignature >= 0 && keySignature < LILYPOND_KEY_SIGNATURES.length){
			this.writer.println(indent(indent) + "\\key " + LILYPOND_KEY_SIGNATURES[keySignature] + " \\major");
		}
	}
	
	private void addClef(int clef,int indent){
		String clefName = "";
		if(clef == TGMeasure.CLEF_TREBLE){
			clefName = "treble";
		}
		else if(clef == TGMeasure.CLEF_BASS){
			clefName = "bass";
		}
		else if(clef == TGMeasure.CLEF_ALTO){
			clefName = "alto";
		}
		else if(clef == TGMeasure.CLEF_TENOR){
			clefName = "tenor";
		}
		if(clefName.length() > 0){
			this.writer.println(indent(indent) + "\\clef \"" + clefName + "_8\"");
		}
	}
	
	private void addMeasureVoice(TGMeasure measure, int voice , boolean force ,int indent){
		boolean multipleVoices = hasMultipleVoices(measure);
		if ( force || multipleVoices != this.temp.isMultipleVoices() ){
			this.writer.println( indent(indent) + getLilypondVoice( multipleVoices ? voice : -1 ) );
		}
		this.temp.setMultipleVoices( multipleVoices );
	}
	
	private void addMeasureComponents(TGMeasure measure,int voice,int indent){
		this.writer.print(indent(indent));
		this.addComponents(measure,voice);
		this.writer.println();
	}
	
	private void addComponents(TGMeasure measure,int vIndex){
		int key = measure.getKeySignature();
		TGBeat previous = null;
		
		for(int i = 0 ; i < measure.countBeats() ; i ++){
			TGBeat beat = measure.getBeat( i );
			TGVoice voice = beat.getVoice( vIndex );
			if( !voice.isEmpty() ){
				TGDivisionType divisionType = voice.getDuration().getDivision();
				
				if(previous != null && this.temp.isDivisionTypeOpen() && !divisionType.isEqual( previous.getVoice(0).getDuration().getDivision() )){
					this.writer.print("} ");
					this.temp.setDivisionTypeOpen(false);
				}

				if(!this.temp.isDivisionTypeOpen() && !divisionType.isEqual(TGDivisionType.NORMAL)){
					this.writer.print("\\times " + divisionType.getTimes() + "/" + divisionType.getEnters() + " {");
					this.temp.setDivisionTypeOpen(true);
				}

				this.addBeat(key, beat, voice);

				previous = beat;
			}
		}
		// It Means that all voice beats are empty 
		if( previous == null ){
			this.writer.print("\\skip ");
			this.addDuration( measure.getTimeSignature().getDenominator() );
			this.writer.print("*" + measure.getTimeSignature().getNumerator() + " ");
		}
		
		if(this.temp.isDivisionTypeOpen()){
			this.writer.print("} ");
			this.temp.setDivisionTypeOpen(false);
		}
	}
	
	private void addBeat(int key,TGBeat beat, TGVoice voice){
		if(voice.isRestVoice()){
			boolean skip = false;
			for( int v = 0 ; v < beat.countVoices() ; v ++ ){
				if( !skip && v != voice.getIndex() ){
					TGVoice current = beat.getVoice( v );
					if(!current.isEmpty() && current.getDuration().isEqual( voice.getDuration() )){
						skip = (!current.isRestVoice() || current.getIndex() < voice.getIndex());
					}
				}
			}
			this.writer.print( ( skip ? "\\skip " : "r" ) );
			this.addDuration( voice.getDuration() );
		}
		else{
			this.addEffectsBeforeBeat(voice);

			int size = voice.countNotes();
			
			int ottava = 0;
			for(int i = 0 ; i < size ; i ++){
				TGNote note = voice.getNote(i);
				int thisnote = beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue();
				if (thisnote > MAX_PITCH) {
					ottava = 1;
				}
			}
			if (ottava != 0) {
				this.addOttava(ottava);
			}

			this.writer.print("<");

			for(int i = 0 ; i < size ; i ++){
				TGNote note = voice.getNote(i);

				this.addKey(key, (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue()) );

				this.addString(note.getString());
				
				if(size > 1){
					this.writer.print(" ");
				}
			}

			this.writer.print(">");
			
			this.addDuration( voice.getDuration() );
			if (ottava != 0) {
				this.addOttava(0);
			}
		}
		
		// Add Chord, if was not previously added in another voice
		if( beat.isChordBeat() && !voice.isRestVoice() ){
			boolean skip = false;
			for( int v = 0 ; v < voice.getIndex() ; v ++ ){
				TGVoice current = beat.getVoice( v );
				skip = (skip || ( !current.isEmpty() && !current.isRestVoice() ) );
			}
			if( !skip ){
				this.writer.print("-\\tag #'chords ^\\markup \\fret-diagram #\"");
				TGChord chord = beat.getChord();
				for( int i = 0; i < chord.countStrings(); i ++){
					this.writer.print((i + 1) + "-" + getLilypondChordFret(chord.getFretValue( i )) + ";");
				}
				this.writer.print("\"");
			}
		}
		
		// Add Text, if was not previously added in another voice
		if( beat.isTextBeat() ){
			boolean skip = false;
			for( int v = 0 ; v < voice.getIndex() ; v ++ ){
				skip = (skip || !beat.getVoice( v ).isEmpty() );
			}
			if( !skip ){
				this.writer.print("-\\tag #'texts ^\\markup {\"" + beat.getText().getValue() + "\"}");
			}
		}
		
		this.writer.print(" ");
	}
	
	private void addKey(int keySignature,int value){
		this.writer.print( getLilypondKey(keySignature, value) );
	}
	
	private void addString(int string){
		this.writer.print("\\" + string);
	}
	
	private void addOttava(int ottava){
		this.writer.print(" \\ottava #" + ottava);
		if (ottava != 0)
			this.writer.print(" ");
	}
	
	private void addDuration(TGDuration duration){
		this.writer.print(getLilypondDuration(duration));
	}
	
	private void addEffectsBeforeBeat(TGVoice voice){
		List graceNotes = new ArrayList();
		for( int i = 0 ; i < voice.countNotes() ; i ++ ){
			TGNote note = voice.getNote(i);
			if( note.getEffect().isGrace() ){
				graceNotes.add( note );
			}
		}
		if( !graceNotes.isEmpty() ){
			this.writer.print("\\grace ");
			this.writer.print("<");
			
			int duration = 0;
			for( int i = 0 ; i < graceNotes.size() ; i ++ ){
				TGNote note = (TGNote)graceNotes.get( i );
				TGMeasure measure = voice.getBeat().getMeasure();
				TGString string = measure.getTrack().getString(note.getString());
				TGEffectGrace grace = note.getEffect().getGrace();
				
				if( duration < TGDuration.SIXTY_FOURTH && grace.getDuration() == 1 ){
					duration = TGDuration.SIXTY_FOURTH;
				}else if( duration < TGDuration.THIRTY_SECOND && grace.getDuration() == 2 ){
					duration = TGDuration.THIRTY_SECOND;
				}else if( duration < TGDuration.SIXTEENTH && grace.getDuration() == 3 ){
					duration = TGDuration.SIXTEENTH;
				}
				if( i > 0 ){
					this.writer.print(" ");
				}
				this.addKey(measure.getKeySignature(), (string.getValue() + grace.getFret()) );
				this.addString(note.getString());
			}
			this.writer.print(">");
			this.writer.print(duration);
			this.writer.print(" ");
		}
	}
	
	private boolean hasMultipleVoices( TGMeasure measure ){
		int voiceCount = 0;
		for( int voice = 0 ; voice < TGBeat.MAX_VOICES ; voice ++ ){
			if( isVoiceAvailable(measure, voice) ){
				voiceCount ++;
			}
		}
		return (voiceCount > 1);
	}
	
	private boolean isVoiceAvailable( TGMeasure measure , int voice ){
		for( int i = 0 ; i < measure.countBeats() ; i ++ ){
			TGBeat beat = measure.getBeat( i );
			if( !beat.getVoice( voice ).isEmpty() ){
				return true;
			}
		}
		return false;
	}
	
	private boolean isVoiceAvailable( TGTrack track , int voice ){
		for( int i = 0 ; i < track.countMeasures() ; i ++ ){
			TGMeasure measure = track.getMeasure( i );
			if( isVoiceAvailable(measure, voice) ){
				return true;
			}
		}
		return false;
	}
	
	private boolean addTrackTitleOnGroup(TGSong song){
		if(this.settings.isTrackNameEnabled() && this.settings.isTrackGroupEnabled()){
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS && song.countTracks() > 1){
				return true;
			}
		}
		return false;
	}
	
	private String indent(int level){
		String indent = new String();
		for(int i = 0; i < level; i ++){
			indent += INDENT;
		}
		return indent;
	}
	
	private String getLilypondBoolean(boolean value){
		return (value ? "#t" : "#f");
	}
	
	private String getLilypondKey(int keySignature , int value){
		String[] LILYPOND_NOTES = (keySignature <= 7 ? LILYPOND_SHARP_NOTES : LILYPOND_FLAT_NOTES );
		String key = (LILYPOND_NOTES[ value % 12 ]);
		for(int i = 4; i < (value / 12); i ++){
			key += ("'");
		}
		for(int i = (value / 12); i < 4; i ++){
			key += (",");
		}
		return key;
	}
	
	private String getLilypondDuration(TGDuration value){
		String duration = Integer.toString(value.getValue());
		if(value.isDotted()){
			duration += (".");
		}
		else if(value.isDoubleDotted()){
			duration += ("..");
		}
		return duration;
	}
	
	private String getLilypondChordFret(int value){
		if(value < 0){
			return ("x");
		}
		if(value == 0){
			return ("o");
		}
		return Integer.toString(value);
	}
	
	private String getLilypondVoice(int voice){
		if( voice == -1 ){
			return "\\oneVoice";
		}
		return ( voice == 0 ? "\\voiceOne" : "\\voiceTwo" );
	}
	
	private String toBase26(int value){
		String result = new String();
		int base = value;
		while(base > 25){
			result = ( (char)( (base % 26) + 'A') + result);
			base = base / 26 - 1;
		}
		return ((char)(base + 'A') + result);
	}
	
	private String trackID(int index, String suffix){
		return ("Track" + this.toBase26(index) + suffix);
	}
	
	private String trackVoiceID(int index, String prefix, String suffix){
		return (prefix + "Voice" + this.toBase26(index) + suffix);
	}
	
	protected class LilypondTempData{

		private boolean divisionTypeOpen;
		private boolean multipleVoices;
		
		protected LilypondTempData(){
			this.reset();
		}
		
		public void reset(){
			this.multipleVoices = false;
			this.divisionTypeOpen = false;
		}
		
		public boolean isDivisionTypeOpen() {
			return this.divisionTypeOpen;
		}
		
		public void setDivisionTypeOpen(boolean divisionTypeOpen) {
			this.divisionTypeOpen = divisionTypeOpen;
		}
		
		public void setMultipleVoices( boolean multipleVoices ){
			this.multipleVoices = multipleVoices;
		}
		
		public boolean isMultipleVoices() {
			return this.multipleVoices;
		}
	}
}
