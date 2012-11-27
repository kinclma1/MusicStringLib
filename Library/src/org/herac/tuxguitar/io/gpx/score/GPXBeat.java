package org.herac.tuxguitar.io.gpx.score;

public class GPXBeat {
	
	private int id;
	private int rhythmId;
	private int[] noteIds;
	private String dynamic;
	
	public GPXBeat(){
    }
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getRhythmId() {
		return rhythmId;
	}
	
	public void setRhythmId(int rhythmId) {
		this.rhythmId = rhythmId;
	}
	
	public int[] getNoteIds() {
		return noteIds;
	}
	
	public void setNoteIds(int[] noteIds) {
		this.noteIds = noteIds;
	}
	
	public String getDynamic() {
		return dynamic;
	}
	
	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}
}
