package org.herac.tuxguitar.song.models.effects;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGEffectHarmonic {
	public static final String KEY_NATURAL = "N.H";
	
	public static final String KEY_ARTIFICIAL = "A.H";
	
	public static final String KEY_TAPPED = "T.H";
	
	public static final String KEY_PINCH = "P.H";
	
	public static final String KEY_SEMI = "S.H";
	
	public static final int TYPE_NATURAL = 1;
	
	public static final int TYPE_ARTIFICIAL = 2;
	
	public static final int TYPE_TAPPED = 3;
	
	public static final int TYPE_PINCH = 4;
	
	public static final int TYPE_SEMI = 5;
	
	public static final int MIN_ARTIFICIAL_OFFSET = -24;
	
	public static final int MAX_ARTIFICIAL_OFFSET = 24;
	
	public static final int MAX_TAPPED_OFFSET = 24;
	
	public static final int NATURAL_FREQUENCIES[][] = {
		{12, 12}, //AH12 (+12 frets)
		{9 , 28}, //AH9 (+28 frets)
		{5 , 24}, //AH5 (+24 frets)
		{7 , 19}, //AH7 (+19 frets)
		{4 , 28}, //AH4 (+28 frets)
		{3 , 31}  //AH3 (+31 frets)
	};
	
	private int type;
	
	private int data;
	
	public TGEffectHarmonic(){
        type = 0;
        data = 0;
	}
	
	public int getData() {
		return data;
	}
	
	public void setData(int data) {
		this.data = data;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isNatural(){
		return (type == TYPE_NATURAL);
	}
	
	public boolean isArtificial(){
		return (type == TYPE_ARTIFICIAL);
	}
	
	public boolean isTapped(){
		return (type == TYPE_TAPPED);
	}
	
	public boolean isPinch(){
		return (type == TYPE_PINCH);
	}
	
	public boolean isSemi(){
		return (type == TYPE_SEMI);
	}
	
	public TGEffectHarmonic clone(TGFactory factory){
		TGEffectHarmonic effect = factory.newEffectHarmonic();
        effect.type = type;
        effect.data = data;
		return effect;
	}
	
}
