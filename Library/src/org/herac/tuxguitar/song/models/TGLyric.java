package org.herac.tuxguitar.song.models;

public abstract class TGLyric {
	private static final String REGEX = " ";
	
	private int from;
	private String lyrics;
	
	public TGLyric(){
        from = 1;
        lyrics = "";
	}
	
	public int getFrom() {
		return from;
	}
	
	public void setFrom(int from) {
		this.from = from;
	}
	
	public String getLyrics() {
		return lyrics;
	}
	
	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
	
	public String[] getLyricBeats(){
		String lyrics = this.lyrics;
		lyrics = lyrics.replaceAll("\n",REGEX);
		lyrics = lyrics.replaceAll("\r",REGEX);
		return lyrics.split(REGEX);
	}
	
	public boolean isEmpty(){
		return (lyrics.isEmpty());
	}
	
	public void copy(TGLyric lyric){
        lyric.from = from;
        lyric.lyrics = lyrics;
	}
	
}
