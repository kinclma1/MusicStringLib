package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGColor {
	public static final TGColor RED = newColor(255,0,0);
	public static final TGColor GREEN = newColor(0,255,0);
	public static final TGColor BLUE = newColor(0,0,255);
	public static final TGColor WHITE = newColor(255,255,255);
	public static final TGColor BLACK = newColor(0,0,0);
	
	private int r;
	private int g;
	private int b;
	
	public TGColor(){
        r = 0;
        g = 0;
        b = 0;
	}
	
	public int getB() {
		return b;
	}
	
	public void setB(int b) {
		this.b = b;
	}
	
	public int getG() {
		return g;
	}
	
	public void setG(int g) {
		this.g = g;
	}
	
	public int getR() {
		return r;
	}
	
	public void setR(int r) {
		this.r = r;
	}
	
	public boolean isEqual(TGColor color){
		return (r == color.r && g == color.g && b == color.b);
	}
	
	public TGColor clone(TGFactory factory){
		TGColor color = factory.newColor();
		copy(color);
		return color;
	}
	
	public void copy(TGColor color){
        color.r = r;
        color.g = g;
        color.b = b;
	}
	
	public static TGColor newColor(int r,int g,int b){
		TGColor color = new TGFactory().newColor();
        color.r = r;
        color.g = g;
        color.b = b;
		return color;
	}
	
}
