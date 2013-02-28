package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGMarker {
	private static final TGColor DEFAULT_COLOR = TGColor.RED;
	private static final String DEFAULT_TITLE = "Untitled";
	
	private int measure;
	private String title;
	private TGColor color;
	
	public TGMarker(TGFactory factory) {
        measure = 0;
        title = DEFAULT_TITLE;
        color = DEFAULT_COLOR.clone(factory);
	}
	
	public int getMeasure() {
		return measure;
	}
	
	public void setMeasure(int measure) {
		this.measure = measure;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public TGColor getColor() {
		return color;
	}
	
	public void setColor(TGColor color) {
		this.color = color;
	}
	
	public TGMarker clone(TGFactory factory){
		TGMarker marker = factory.newMarker();
        marker.measure = measure;
        marker.title = title;
        color.copy(marker.color);
		return marker;
	}
	
}
