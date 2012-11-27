package org.herac.tuxguitar.io.gpx.score;

public class GPXVoice {
	
	private int id;
	private int[] beatIds;
	
	public GPXVoice(){
    }
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int[] getBeatIds() {
		return beatIds;
	}
	
	public void setBeatIds(int[] beatIds) {
		this.beatIds = beatIds;
	}
}
