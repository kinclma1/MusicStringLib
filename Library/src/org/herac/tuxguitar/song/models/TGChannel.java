package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGChannel {
	public static final short DEFAULT_PERCUSSION_CHANNEL = 9;
	
	public static final short DEFAULT_INSTRUMENT = 25;
	public static final short DEFAULT_VOLUME = 127;
	public static final short DEFAULT_BALANCE = 64;
	public static final short DEFAULT_CHORUS = 0;
	public static final short DEFAULT_REVERB = 0;
	public static final short DEFAULT_PHASER = 0;
	public static final short DEFAULT_TREMOLO = 0;
	
	private short channel;
	private short effectChannel;
	private short instrument;
	private short volume;
	private short balance;
	private short chorus;
	private short reverb;
	private short phaser;
	private short tremolo;
	
	public TGChannel() {
        channel = 0;
        effectChannel = 0;
        instrument = DEFAULT_INSTRUMENT;
        volume = DEFAULT_VOLUME;
        balance = DEFAULT_BALANCE;
        chorus = DEFAULT_CHORUS;
        reverb = DEFAULT_REVERB;
        phaser = DEFAULT_PHASER;
        tremolo = DEFAULT_TREMOLO;
	}
	
	public short getBalance() {
		return balance;
	}
	
	public void setBalance(short balance) {
		this.balance = balance;
	}
	
	public short getChannel() {
		return channel;
	}
	
	public void setChannel(short channel) {
		this.channel = channel;
	}
	
	public short getEffectChannel() {
		return effectChannel;
	}
	
	public void setEffectChannel(short effectChannel) {
		this.effectChannel = effectChannel;
	}
	
	public short getChorus() {
		return chorus;
	}
	
	public void setChorus(short chorus) {
		this.chorus = chorus;
	}
	
	public short getInstrument() {
		return (isPercussionChannel() ? 0 : instrument);
	}
	
	public void setInstrument(short instrument) {
		this.instrument = instrument;
	}
	
	public short getPhaser() {
		return phaser;
	}
	
	public void setPhaser(short phaser) {
		this.phaser = phaser;
	}
	
	public short getReverb() {
		return reverb;
	}
	
	public void setReverb(short reverb) {
		this.reverb = reverb;
	}
	
	public short getTremolo() {
		return tremolo;
	}
	
	public void setTremolo(short tremolo) {
		this.tremolo = tremolo;
	}
	
	public short getVolume() {
		return volume;
	}
	
	public void setVolume(short volume) {
		this.volume = volume;
	}
	
	public boolean isPercussionChannel(){
		return TGChannel.isPercussionChannel(channel);
	}
	
	public static boolean isPercussionChannel(int channel){
		return (channel == DEFAULT_PERCUSSION_CHANNEL);
	}
	
	public static void setPercussionChannel(TGChannel channel){
        channel.channel = DEFAULT_PERCUSSION_CHANNEL;
        channel.effectChannel = DEFAULT_PERCUSSION_CHANNEL;
	}
	
	public static TGChannel newPercussionChannel(TGFactory factory){
		TGChannel channel = factory.newChannel();
		TGChannel.setPercussionChannel(channel);
		return channel;
	}
	
	public TGChannel clone(TGFactory factory){
		TGChannel channel = factory.newChannel();
		copy(channel);
		return channel; 
	}
	
	public void copy(TGChannel channel){
        channel.channel = this.channel;
        channel.effectChannel = effectChannel;
        channel.instrument = getInstrument();
        channel.volume = volume;
        channel.balance = balance;
        channel.chorus = chorus;
        channel.reverb = reverb;
        channel.phaser = phaser;
        channel.tremolo = tremolo;
	}
}
