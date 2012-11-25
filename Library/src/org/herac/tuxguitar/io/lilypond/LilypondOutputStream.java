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
	//todo more code cleanup
	private String LILYPOND_VERSION;

	private static final String[] LILYPOND_SHARP_NOTES = new String[]{"c","cis","d","dis","e","f","fis","g","gis","a","ais","b"};
	private static final String[] LILYPOND_FLAT_NOTES = new String[]{"c","des","d","ees","e","f","ges","g","aes","a","bes","b"};
	
	private static final String[] LILYPOND_KEY_SIGNATURES = new String[]{ "c","g","d","a","e","b","fis","cis","f","bes","ees","aes", "des", "ges","ces" };

    private static final String[] LILYPOND_DRUMS =
            new String[]{"bda","bd","ss","sna","hc","sne","tomfl","hhc","tomfh","hhp","toml","hho","tomml","tommh",
                    "cymc","tomh","cymr","cymch","rb","tamb","cyms","cb","cymc","vibs","cymr","boh","bol","cghm","cgho",
                    "cgl","timh","timl","agh","agl","cab","mar","whs","whl","guis","guil","cl","wbh","wbl","cuim",
                    "cuio","trim","trio"};

	private static final String INDENT = new String("   ");
	
	// anything over high C should be printed 8vb
	private static final int MAX_PITCH = 72;
	
	private TGSongManager manager;
	
	private PrintWriter writer;
	
	private LilypondSettings settings;

    private boolean divisionTypeOpen;
    private boolean multipleVoices;
	
	public LilypondOutputStream(OutputStream stream,LilypondSettings settings){
		writer = new PrintWriter(stream);
		tempReset();
		this.settings = settings;
		LILYPOND_VERSION = settings.getLilypondVersion();
	}
	
	public void writeSong(TGSong song){
		manager = new TGSongManager();
		manager.setSong(song);

		addVersion();
		addPaper(song);
		addLayout();
		addSongDefinitions(song);
		addSong(song);
		
		writer.flush();
		writer.close();
	}
	
	private void addVersion(){
		writer.println("\\version \"" + LILYPOND_VERSION + "\"");
	}

	private void addPaper(TGSong song){
		writer.println("\\paper {");
		
		writer.println(indent(1) + "indent = #" + (addTrackTitleOnGroup(song) ? 30 : 0));

		if ( LILYPOND_VERSION.compareTo("2.11.60") < 0) {
			writer.println(indent(1) + "printallheaders = #" + getLilypondBoolean(true));
		} else {
			writer.println(indent(1) + "print-all-headers = #" + getLilypondBoolean(true));
		}
		writer.println(indent(1) + "ragged-right = #" + getLilypondBoolean(false));
		writer.println(indent(1) + "ragged-bottom = #" + getLilypondBoolean(true));
		writer.println("}");
	}
	
	private void addHeader(TGSong song, String instrument, boolean drums, int indent){
		writer.println(indent(indent) + "\\header {");
		writer.println(indent(indent + 1) + "title = \"" + song.getName() + "\" ");
		writer.println(indent(indent + 1) + "composer = \"" + song.getAuthor() + "\" ");
		if(settings.isTrackNameEnabled() && !addTrackTitleOnGroup(song) && instrument != null){
			if (!drums) {
                writer.println(indent(indent + 1) + "instrument = \"" + instrument + "\" ");
            } else {
                writer.println(indent(indent + 1) + "instrument = \"DRUMS\" ");
            }
		}
		writer.println(indent(indent) + "}");
	}
	
	private void addLayout(){
		writer.println("\\layout {");
		writer.println(indent(1) + "\\context { \\Score");
		writer.println(indent(2) + "\\override MetronomeMark #'padding = #'5");
		writer.println(indent(1) + "}");
		writer.println(indent(1) + "\\context { \\Staff");
		writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		writer.println(indent(2) + "\\override StringNumber #'transparent = #" + getLilypondBoolean(true));
		writer.println(indent(1) + "}");
		writer.println(indent(1) + "\\context { \\TabStaff");
		writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		writer.println(indent(2) + "\\override Stem #'transparent = #" + getLilypondBoolean(settings.isScoreEnabled()));
		writer.println(indent(2) + "\\override Beam #'transparent = #" + getLilypondBoolean(settings.isScoreEnabled()));
		writer.println(indent(1) + "}");
		if( settings.isScoreEnabled() ){
			writer.println(indent(1) + "\\context { \\TabVoice");
			writer.println(indent(2) + "\\override Tie #'stencil = ##f");
			writer.println(indent(1) + "}");
		}
		writer.println(indent(1) + "\\context { \\StaffGroup");
		writer.println(indent(2) + "\\consists \"Instrument_name_engraver\"");
		writer.println(indent(1) + "}");
		writer.println("}");
	}
	
	private void addSongDefinitions(TGSong song){
		for(int i = 0; i < song.countTracks(); i ++){
			TGTrack track = song.getTrack(i);
			String id = trackID(i, "");
			tempReset();
			addMusic(track, id);
			addScoreStaff(track, id);
			addStaffGroup(track, id);
		}
	}
	
	private void addSong(TGSong song){
		int trackCount = song.countTracks();
		if(settings.isTrackGroupEnabled() && trackCount > 1){
			writer.println("\\score {");
			if(settings.getTrack() == LilypondSettings.ALL_TRACKS){
				writer.println(indent(1) + "<<");
			}
		}
		
		for(int i = 0; i < trackCount; i ++){
			TGTrack track = song.getTrack(i);
			if(settings.getTrack() == LilypondSettings.ALL_TRACKS || settings.getTrack() == track.getNumber()){
				if(!settings.isTrackGroupEnabled() || trackCount == 1){
					writer.println("\\score {");
				}
				writer.println(indent(1) + "\\" + trackID(i, "StaffGroup"));
				if(!settings.isTrackGroupEnabled() || trackCount == 1){
					addHeader(song, track.getName(), track.getChannel().getChannel() == 9, 1);
					writer.println("}");
				}
			}
		}
	}
	
	private void addMusic(TGTrack track,String id){
		int voice = 0;
        writer.println(trackVoiceID(voice,id,"Music") + " = #(define-music-function (parser location inTab) (boolean?)");
        writer.println("#{");
        if( isVoiceAvailable(track, voice) ){
            TGMeasure previous = null;
            int count = track.countMeasures();
            for(int i = 0; i < count; i ++){
                TGMeasure measure = track.getMeasure(i);

                int measureFrom = settings.getMeasureFrom();
                int measureTo = settings.getMeasureTo();
                if((measureFrom <= measure.getNumber() || measureFrom == LilypondSettings.FIRST_MEASURE) && (measureTo >= measure.getNumber() || measureTo == LilypondSettings.LAST_MEASURE )){
                    addMeasure(measure, previous, voice, 1, (i == (count - 1)));
                    previous = measure;
                }
            }
            writer.println(indent(1) + "\\bar \"|.\"");
            writer.println(indent(1) + "\\pageBreak");
        }
        writer.println("#})");
	}
	
	private void addScoreStaff(TGTrack track,String id){
		boolean drums = track.getChannel().getChannel() == 9;
		if(!drums) {
            writer.println(id + "Staff = \\new Staff <<" );
        } else {
            writer.println(id + "Staff = \\new DrumStaff <<");
        }

        String vId =  trackVoiceID(0, id, "Music") ;
        if(!drums) {
            writer.println(indent(1) + "\\context Voice = \"" + vId + "\" {");
        } else {
            writer.println(indent(1) + "\\context DrumVoice = \"" + vId + "\" {");
        }

        writer.println(indent(2) + "\\" + vId + " #" + getLilypondBoolean( false ) );
        writer.println(indent(1) + "}");

		writer.println(">>");
	}

	private void addStaffGroup(TGTrack track,String id){
		writer.println(id + "StaffGroup = \\new StaffGroup <<");
		if(addTrackTitleOnGroup(track.getSong())){
			writer.println(indent(1) + "\\set StaffGroup.instrumentName = #\"" +
                    (track.getChannel().getChannel() != 9 ? track.getName() : "DRUMS")  + "\"");
		}
		if(settings.isScoreEnabled()){
			writer.println(indent(1) + "\\" + id + "Staff");
		}
		writer.println(">>");
	}
	
	private void addMeasure(TGMeasure measure,TGMeasure previous,int voice,int indent,boolean isLast){
		boolean drums = measure.getTrack().getChannel().getChannel() == 9;

        if(previous == null || measure.getTempo().getValue() != previous.getTempo().getValue()){
			addTempo(measure.getTempo(), indent);
		}
		
		if(!drums && (previous == null || measure.getClef() != previous.getClef())){
			addClef(measure.getClef(), indent);
		}
		if(!drums && (previous == null || measure.getKeySignature() != previous.getKeySignature())){
			addKeySignature(measure.getKeySignature(), indent);
		}
		
		if(previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature())){
			addTimeSignature(measure.getTimeSignature(), indent);
		}

		// Set the specific voice
		addMeasureVoice(measure, voice, (previous == null), indent);
        if (drums && previous == null) {
            writer.println(indent(indent) + "\\drummode {");
            indent ++;
        } else if (drums) {
            indent ++;
        }
		addMeasureComponents(measure, voice, indent);
        if(drums && isLast) {
            indent --;
            writer.println(indent(indent) + "}");
        }
	}
	
	private void addTempo(TGTempo tempo,int indent){
		writer.println(indent(indent) + "\\tempo 4=" + tempo.getValue());
	}
	
	private void addTimeSignature(TGTimeSignature ts,int indent){
		writer.println(indent(indent) + "\\time " + ts.getNumerator() + "/" + ts.getDenominator().getValue());
	}
	
	private void addKeySignature(int keySignature,int indent){
		if(keySignature >= 0 && keySignature < LILYPOND_KEY_SIGNATURES.length){
			writer.println(indent(indent) + "\\key " + LILYPOND_KEY_SIGNATURES[keySignature] + " \\major");
		}
	}
	
	private void addClef(int clef,int indent){
		String clefName = null;
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
		if(clefName != null){
			writer.println(indent(indent) + "\\clef \"" + clefName + "_8\"");
		}
	}
	
	private void addMeasureVoice(TGMeasure measure, int voice , boolean force ,int indent){
		boolean multipleVoices = false;
		if ( force || multipleVoices != this.multipleVoices ){
			writer.println( indent(indent) + getLilypondVoice( multipleVoices ? voice : -1 ) );
		}
		this.multipleVoices = multipleVoices;
	}
	
	private void addMeasureComponents(TGMeasure measure,int voice,int indent){
		writer.print(indent(indent));
		addComponents(measure, voice);
		writer.println();
	}
	
	private void addComponents(TGMeasure measure,int vIndex){
		int key = measure.getKeySignature();
		TGBeat previous = null;
		
		for(int i = 0 ; i < measure.countBeats() ; i ++){
			TGBeat beat = measure.getBeat( i );
			TGVoice voice = beat.getVoice( vIndex );
			if( !voice.isEmpty() ){
				TGDivisionType divisionType = voice.getDuration().getDivision();
				
				if(previous != null && divisionTypeOpen && !divisionType.isEqual( previous.getVoice(0).getDuration().getDivision() )){
					writer.print("} ");
					divisionTypeOpen = false;
				}

				if(!divisionTypeOpen && !divisionType.isEqual(TGDivisionType.NORMAL)){
					writer.print("\\times " + divisionType.getTimes() + "/" + divisionType.getEnters() + " {");
					divisionTypeOpen = true;
				}

				addBeat(key, beat, voice);

				previous = beat;
			}
		}
		// It Means that all voice beats are empty 
		if( previous == null ){
			writer.print("\\skip ");
			addDuration(measure.getTimeSignature().getDenominator());
			writer.print("*" + measure.getTimeSignature().getNumerator() + " ");
		}
		
		if(divisionTypeOpen){
			writer.print("} ");
			divisionTypeOpen = false;
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
			writer.print( ( skip ? "\\skip " : "r" ) );
			addDuration(voice.getDuration());
		}
		else{
			int size = voice.countNotes();
            int ottava = 0;
            boolean drums = false;
            if(beat.getMeasure().getTrack().getChannel().getChannel() != 9) {

                for(int i = 0 ; i < size ; i ++){
                    TGNote note = voice.getNote(i);
                    int thisnote = beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue();
                    if (thisnote > MAX_PITCH) {
                        ottava = 1;
                    }
                }
                if (ottava != 0) {
                    addOttava(ottava);
                }
            } else {
                drums = true;
            }


			if(size > 1) {
                writer.print("<");
            }

			for(int i = 0 ; i < size ; i ++){
				TGNote note = voice.getNote(i);

                if (!drums) {
                    addKey(key, (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue()));
                } else {
                    addDrum(note.getValue());
                }

				if(size > 1){
					writer.print(" ");
				}
			}

			if(size > 1) {
                writer.print(">");
            }
			
			addDuration(voice.getDuration());
			if (ottava != 0) {
				addOttava(0);
			}
		}

		writer.print(" ");
	}
	
	private void addKey(int keySignature,int value){
		writer.print( getLilypondKey(keySignature, value) );
	}

    private void addDrum(int value) {
        writer.print(getLilypondDrum(value));
    }
	
	private void addOttava(int ottava){
		writer.print(" \\ottava #" + ottava);
		if (ottava != 0)
			writer.print(" ");
	}
	
	private void addDuration(TGDuration duration){
		writer.print(getLilypondDuration(duration));
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
		if(settings.isTrackNameEnabled() && settings.isTrackGroupEnabled()){
			if(settings.getTrack() == LilypondSettings.ALL_TRACKS && song.countTracks() > 1){
				return true;
			}
		}
		return false;
	}
	
	private String indent(int level){
		StringBuilder indentBuilder = new StringBuilder();
		for(int i = 0; i < level; i ++){
			indentBuilder.append(INDENT);
		}
		return indentBuilder.toString();
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

    private String getLilypondDrum(int value) {
        return LILYPOND_DRUMS[value - 35];
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
		return ("Track" + toBase26(index) + suffix);
	}
	
	private String trackVoiceID(int index, String prefix, String suffix){
		return (prefix + "Voice" + toBase26(index) + suffix);
	}

    private void tempReset() {
        divisionTypeOpen = false;
        multipleVoices = false;
    }
}
