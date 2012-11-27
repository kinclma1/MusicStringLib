package org.herac.tuxguitar.io.midi.base;

public class MidiEvent {
	
	private long tick;
	private MidiMessage message;
	
	public MidiEvent(MidiMessage message, long tick){
		this.message = message;
		this.tick = tick;
	}
	
	public MidiMessage getMessage() {
		return message;
	}
	
	public long getTick() {
		return tick;
	}
}
