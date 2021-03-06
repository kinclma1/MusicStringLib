package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGVelocities;

public class TGStream {
	
	public static final String TG_FORMAT_NAME = "TuxGuitar File Format";

    public static final String TG_VERSION = "1.2";
	
	public static final String TG_FORMAT_VERSION = (TG_FORMAT_NAME + " - " + TG_VERSION);
	
	public static final String TG_FORMAT_EXTENSION = (".tg");
	
	protected static final int TRACK_SOLO = 0x01;
	
	protected static final int TRACK_MUTE = 0x02;
	
	protected static final int TRACK_LYRICS = 0x04;
	
	protected static final int MEASURE_HEADER_TIMESIGNATURE = 0x01;
	
	protected static final int MEASURE_HEADER_TEMPO = 0x02;
	
	protected static final int MEASURE_HEADER_REPEAT_OPEN = 0x04;
	
	protected static final int MEASURE_HEADER_REPEAT_CLOSE = 0x08;
	
	protected static final int MEASURE_HEADER_REPEAT_ALTERNATIVE = 0x10;
	
	protected static final int MEASURE_HEADER_MARKER = 0x20;
	
	protected static final int MEASURE_HEADER_TRIPLET_FEEL = 0x40;
	
	protected static final int MEASURE_CLEF = 0x01;
	
	protected static final int MEASURE_KEYSIGNATURE = 0x02;
	
	protected static final int BEAT_HAS_NEXT = 0x01;
	
	protected static final int BEAT_HAS_STROKE = 0x02;
	
	protected static final int BEAT_HAS_CHORD = 0x04;
	
	protected static final int BEAT_HAS_TEXT = 0x08;
	
	protected static final int BEAT_HAS_VOICE = 0x10;
	
	protected static final int BEAT_HAS_VOICE_CHANGES = 0x20;
	
	protected static final int VOICE_HAS_NOTES = 0x01;
	
	protected static final int VOICE_NEXT_DURATION = 0x02;
	
	protected static final int VOICE_DIRECTION_UP = 0x04;
	
	protected static final int VOICE_DIRECTION_DOWN = 0x08;
	
	protected static final int NOTE_HAS_NEXT = 0x01;
	
	protected static final int NOTE_TIED = 0x02;
	
	protected static final int NOTE_EFFECT = 0x04;
	
	protected static final int NOTE_VELOCITY = 0x08;
	
	protected static final int DURATION_DOTTED = 0x01;
	
	protected static final int DURATION_DOUBLE_DOTTED = 0x02;
	
	protected static final int DURATION_NO_TUPLET = 0x04;
	
	protected static final int EFFECT_BEND = 0x01;
	
	protected static final int EFFECT_TREMOLO_BAR = 0x02;
	
	protected static final int EFFECT_HARMONIC = 0x04;
	
	protected static final int EFFECT_GRACE = 0x08;
	
	protected static final int EFFECT_TRILL = 0x010;
	
	protected static final int EFFECT_TREMOLO_PICKING = 0x020;
	
	protected static final int EFFECT_VIBRATO = 0x040;
	
	protected static final int EFFECT_DEAD = 0x080;
	
	protected static final int EFFECT_SLIDE = 0x0100;
	
	protected static final int EFFECT_HAMMER = 0x0200;
	
	protected static final int EFFECT_GHOST = 0x0400;
	
	protected static final int EFFECT_ACCENTUATED = 0x0800;
	
	protected static final int EFFECT_HEAVY_ACCENTUATED = 0x01000;
	
	protected static final int EFFECT_PALM_MUTE = 0x02000;
	
	protected static final int EFFECT_STACCATO = 0x04000;
	
	protected static final int EFFECT_TAPPING = 0x08000;
	
	protected static final int EFFECT_SLAPPING = 0x010000;
	
	protected static final int EFFECT_POPPING = 0x020000;
	
	protected static final int EFFECT_FADE_IN = 0x040000;
	
	protected static final int GRACE_FLAG_DEAD = 0x01;
	
	protected static final int GRACE_FLAG_ON_BEAT = 0x02;
	
	protected class TGBeatData {
		private long currentStart;
		private TGVoiceData[] voices;
		
		protected TGBeatData(TGMeasure measure){
            init(measure);
		}
		
		private void init(TGMeasure measure){
            currentStart = measure.getStart();
            voices = new TGVoiceData[TGBeat.MAX_VOICES];
			for(int i = 0 ; i < voices.length ; i ++ ){
                voices[i] = new TGVoiceData(measure);
			}
		}
		
		protected TGVoiceData getVoice(int index){
			return voices[index];
		}
		
		public long getCurrentStart(){
			long minimumStart = -1;
			for(int i = 0 ; i < voices.length ; i ++ ){
				if(voices[i].getStart() > currentStart){
					if( minimumStart < 0 || voices[i].getStart() < minimumStart ){
						minimumStart = voices[i].getStart();
					}
				}
			}
			if( minimumStart > currentStart){
                currentStart = minimumStart;
			}
			return currentStart;
		}
	}
	
	protected class TGVoiceData {
		private long start;
		private int velocity;
		private int flags;
		private TGDuration duration;
		
		protected TGVoiceData(TGMeasure measure){
            init(measure);
		}
		
		private void init(TGMeasure measure){
            flags = 0;
            start = measure.getStart();
            velocity = TGVelocities.DEFAULT;
            duration = new TGFactory().newDuration();
		}
		
		public TGDuration getDuration() {
			return duration;
		}
		
		public void setDuration(TGDuration duration) {
			this.duration = duration;
		}
		
		public long getStart() {
			return start;
		}
		
		public void setStart(long start) {
			this.start = start;
		}
		
		public int getVelocity() {
			return velocity;
		}
		
		public void setVelocity(int velocity) {
			this.velocity = velocity;
		}
		
		public int getFlags() {
			return flags;
		}
		
		public void setFlags(int flags) {
			this.flags = flags;
		}
	}
}
