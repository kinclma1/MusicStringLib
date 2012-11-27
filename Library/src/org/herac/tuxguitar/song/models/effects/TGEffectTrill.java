package org.herac.tuxguitar.song.models.effects;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;

public abstract class TGEffectTrill {
	
	private int fret;
	private TGDuration duration;
	
	public TGEffectTrill(TGFactory factory) {
        fret = 0;
        duration = factory.newDuration();
	}
	
	public int getFret() {
		return fret;
	}
	
	public void setFret(int fret) {
		this.fret = fret;
	}
	
	public TGDuration getDuration() {
		return duration;
	}
	
	public void setDuration(TGDuration duration) {
		this.duration = duration;
	}
	
	public TGEffectTrill clone(TGFactory factory){
		TGEffectTrill effect = factory.newEffectTrill();
		effect.setFret(fret);
        effect.duration.setValue(duration.getValue());
        effect.duration.setDotted(duration.isDotted());
        effect.duration.setDoubleDotted(duration.isDoubleDotted());
        effect.duration.getDivision().setEnters(duration.getDivision().getEnters());
        effect.duration.getDivision().setTimes(duration.getDivision().getTimes());
		
		return effect;
	}
	
}
