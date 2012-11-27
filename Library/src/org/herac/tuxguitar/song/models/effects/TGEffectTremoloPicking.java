package org.herac.tuxguitar.song.models.effects;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;

public abstract class TGEffectTremoloPicking {
	
	private TGDuration duration;
	
	public TGEffectTremoloPicking(TGFactory factory) {
        duration = factory.newDuration();
	}
	
	public TGDuration getDuration() {
		return duration;
	}
	
	public void setDuration(TGDuration duration) {
		this.duration = duration;
	}
	
	public TGEffectTremoloPicking clone(TGFactory factory){
		TGEffectTremoloPicking effect = factory.newEffectTremoloPicking();
        effect.duration.setValue(duration.getValue());
        effect.duration.setDotted(duration.isDotted());
        effect.duration.setDoubleDotted(duration.isDoubleDotted());
        effect.duration.getDivision().setEnters(duration.getDivision().getEnters());
        effect.duration.getDivision().setTimes(duration.getDivision().getTimes());
		return effect;
	}
	
}
