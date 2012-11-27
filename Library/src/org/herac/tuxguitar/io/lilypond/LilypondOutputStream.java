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
	private static final String LILYPOND_VERSION = "2.14.0";

	private static final String[] LILYPOND_SHARP_NOTES = new String[]{"c","cis","d","dis","e","f","fis","g","gis","a","ais","b"};
	private static final String[] LILYPOND_FLAT_NOTES = new String[]{"c","des","d","ees","e","f","ges","g","aes","a","bes","b"};
	
	private static final String[] LILYPOND_KEY_SIGNATURES = new String[]{ "c","g","d","a","e","b","fis","cis","f","bes","ees","aes", "des", "ges","ces" };

    private static final String[] LILYPOND_DRUMS =
            new String[]{"bda","bd","ss","sna","hc","sne","tomfl","hhc","tomfh","hhp","toml","hho","tomml","tommh",
                    "cymc","tomh","cymr","cymch","rb","tamb","cyms","cb","cymc","vibs","cymr","boh","bol","cghm","cgho",
                    "cgl","timh","timl","agh","agl","cab","mar","whs","whl","guis","guil","cl","wbh","wbl","cuim",
                    "cuio","trim","trio"};

	private static final String INDENT = "   ";
	
	// anything over high C should be printed 8vb
	private static final int MAX_PITCH = 72;

    private PrintWriter writer;
	
	public LilypondOutputStream(OutputStream stream){
		writer = new PrintWriter(stream);
	}
	
	public void writeSong(TGSong song){
        TGSongManager manager = new TGSongManager();
		manager.setSong(song);

		addVersion();
		addPaper();
		addLayout();
		addSongDefinitions(song);
		addSong(song);
		
		writer.flush();
		writer.close();
	}
	
	private void addVersion(){
		writer.println("\\version \"" + LILYPOND_VERSION + "\"");
	}

	private void addPaper(){
		writer.println("\\paper {");
		writer.println(indent(1) + "indent = #" + 0);
        writer.println(indent(1) + "print-all-headers = #" + getLilypondBoolean(true));
		writer.println(indent(1) + "ragged-right = #" + getLilypondBoolean(false));
		writer.println(indent(1) + "ragged-bottom = #" + getLilypondBoolean(true));
		writer.println("}");
	}
	
	private void addHeader(String instrument, boolean drums, int indent){
		writer.println(indent(indent) + "\\header {");
        writer.println(indent(indent + 1) + "instrument = \"" + (drums ? "DRUMS" : instrument) + "\" ");
		writer.println(indent(indent) + "}");
	}
	
	private void addLayout(){
		writer.println("\\layout {");
		writer.println(indent(1) + "\\context { \\Score");
		writer.println(indent(2) + "\\override MetronomeMark #'padding = #'5");
		writer.println(indent(1) + "}");
		writer.println(indent(1) + "\\context { \\Staff");
		writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		writer.println(indent(1) + "}");
		writer.println("}");
	}
	
	private void addSongDefinitions(TGSong song){
		for(int i = 0; i < song.countTracks(); i ++){
			TGTrack track = song.getTrack(i);
			String id = trackID(i, "");
			addMusic(track, id);
			addScoreStaff(track, id);
		}
	}
	
	private void addSong(TGSong song){
		int trackCount = song.countTracks();
		
		for(int i = 0; i < trackCount; i ++){
			TGTrack track = song.getTrack(i);
            boolean drums = track.getChannel().getChannel() == 9;
            writer.println("\\score {");
            writer.println(indent(1) + "\\" + trackID(i, "Staff"));
            addHeader(track.getName(), drums, 1);
            writer.println("}");
		}
	}
	
	private void addMusic(TGTrack track,String id){
        boolean drums = track.getChannel().getChannel() == 9;
        writer.println(id + "Music" + " = \\new " + (!drums ? "Voice" : "DrumVoice"));
        writer.println("{");
        TGMeasure previous = null;
        int count = track.countMeasures();
        int indent = 1;
        if (drums) {
            writer.println(indent(indent ++) + "\\drummode {");
        }
        for(int i = 0; i < count; i ++){
            TGMeasure measure = track.getMeasure(i);
            addMeasure(measure, previous, indent, (i == (count - 1)));
            previous = measure;
        }
        writer.println(indent(indent) + "\\bar \"|.\"");
        writer.println(indent(indent) + "\\pageBreak");
        if (drums) {
            writer.println(indent(-- indent) + "}");
        }
        writer.println("}");
	}
	
	private void addScoreStaff(TGTrack track,String id){
        writer.println(id + "Staff = \\new " + (track.getChannel().getChannel() != 9 ? "Staff" : "DrumStaff") + " {" );
        writer.println(indent(1) + "\\" + id + "Music");
		writer.println("}");
	}
	
	private void addMeasure(TGMeasure measure, TGMeasure previous, int indent, boolean isLast){
		boolean drums = measure.getTrack().getChannel().getChannel() == 9;

        if(previous == null || measure.getTempo().getValue() != previous.getTempo().getValue()){
			addTempo(measure.getTempo(), indent);
		}

        if(!drums) {
            if(previous == null || measure.getClef() != previous.getClef()){
                addClef(measure.getClef(), indent);
            }
            if(previous == null || measure.getKeySignature() != previous.getKeySignature()){
                addKeySignature(measure.getKeySignature(), indent);
            }
        }
		
		if(previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature())){
			addTimeSignature(measure.getTimeSignature(), indent);
		}

		if (previous == null) {
            writer.println( indent(indent) + "\\oneVoice" );
        }

		addMeasureComponents(measure, indent);
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
		String clefName = clef == TGMeasure.CLEF_TREBLE ? "treble" : clef == TGMeasure.CLEF_BASS ? "bass" : null;
		if(clefName != null){
			writer.println(indent(indent) + "\\clef \"" + clefName + "_8\"");
		}
	}
	
	private void addMeasureComponents(TGMeasure measure, int indent){
		writer.print(indent(indent));
		addComponents(measure);
		writer.println();
	}
	
	private void addComponents(TGMeasure measure){
		int key = measure.getKeySignature();
		
		for(int i = 0 ; i < measure.countBeats() ; i ++){
			TGBeat beat = measure.getBeat(i);
			TGVoice voice = beat.getVoice(0);
            addBeat(key, beat, voice);
		}
	}
	
	private void addBeat(int key,TGBeat beat, TGVoice voice){
		if(voice.isRestVoice()){
            writer.print("r");
			addDuration(voice.getDuration());
		}
		else{
			int size = voice.countNotes();
            int ottava = 0;
            boolean drums = beat.getMeasure().getTrack().getChannel().getChannel() == 9;
            if(!drums) {
                ottava = getOttava(voice,beat,size);
                if(ottava > 0) {
                    addOttava(ottava);
                }
            }

            if (size == 1) {
                addNote(key, beat, voice.getNote(0), drums);
            } else {
                addChord(key, beat, voice, drums, size);
            }
			
			addDuration(voice.getDuration());

			if (ottava != 0) {
				addOttava(0);
			}
		}

		writer.print(" ");
	}

    private int getOttava(TGVoice voice, TGBeat beat, int size) {
        for(int i = 0 ; i < size ; i ++){
            TGNote note = voice.getNote(i);
            int thisnote = beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue();
            if (thisnote > MAX_PITCH) {
                return 1;
            }
        }
        return 0;
    }

    private void addNote(int key, TGBeat beat, TGNote note, boolean drums) {
        if (!drums) {
            addKey(key, (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue()));
        } else {
            addDrum(note.getValue());
        }
    }

    private void addChord(int key, TGBeat beat, TGVoice voice, boolean drums, int size) {
        writer.print("<");
        for(int i = 0 ; i < size ; i ++){
            addNote(key, beat, voice.getNote(i), drums);
            writer.print(" ");
        }
        writer.print(">");
    }
	
	private void addKey(int keySignature,int value){
		writer.print( getLilypondKey(keySignature, value) );
	}

    private void addDrum(int value) {
        writer.print(LILYPOND_DRUMS[value - 35]);
    }
	
	private void addOttava(int ottava){
		writer.print(" \\ottava #" + ottava);
		if (ottava != 0)
			writer.print(" ");
	}
	
	private void addDuration(TGDuration duration){
		writer.print(getLilypondDuration(duration));
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
	
	private String toBase26(int value){
		String result = "";
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
}
