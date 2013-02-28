/*
 * Created on 26-dic-2005
 *
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.effects.*;

/**
 * @author julian
 *
 */
public abstract class TGNoteEffect {
	private TGEffectBend bend;
	private TGEffectTremoloBar tremoloBar;
	private TGEffectHarmonic harmonic;
	private TGEffectGrace grace;
	private TGEffectTrill trill;
	private TGEffectTremoloPicking tremoloPicking;
	private boolean vibrato;
	private boolean deadNote;
	private boolean slide;
	private boolean hammer;
	private boolean ghostNote;
	private boolean accentuatedNote;
	private boolean heavyAccentuatedNote;
	private boolean palmMute;
	private boolean staccato;
	private boolean tapping;
	private boolean slapping;
	private boolean popping;
	private boolean fadeIn;
	
	public TGNoteEffect(){
        bend = null;
        tremoloBar = null;
        harmonic = null;
        grace = null;
        trill = null;
        tremoloPicking = null;
        vibrato = false;
        deadNote = false;
        slide = false;
        hammer = false;
        ghostNote = false;
        accentuatedNote = false;
        heavyAccentuatedNote = false;
        palmMute = false;
        staccato = false;
        tapping = false;
        slapping = false;
        popping = false;
        fadeIn = false;
	}
	
	public boolean isDeadNote() {
		return deadNote;
	}
	
	public void setDeadNote(boolean deadNote) {
		this.deadNote = deadNote;
		//si es true, quito los efectos incompatibles
		if(this.deadNote){
            tremoloBar = null;
            bend = null;
            trill = null;
            slide = false;
            hammer = false;
		}
	}
	
	public boolean isVibrato() {
		return vibrato;
	}
	public void setVibrato(boolean vibrato) {
		this.vibrato = vibrato;
		//si no es null quito los efectos incompatibles
		if(this.vibrato){
            trill = null;
		}
	}
	
	public TGEffectBend getBend() {
		return bend;
	}
	
	public void setBend(TGEffectBend bend) {
		this.bend = bend;
		//si no es null quito los efectos incompatibles
		if(isBend()){
            tremoloBar = null;
            trill = null;
            deadNote = false;
            slide = false;
            hammer = false;
		}
	}
	
	public boolean isBend() {
		return (bend != null && !bend.getPoints().isEmpty());
	}
	
	public TGEffectTremoloBar getTremoloBar() {
		return tremoloBar;
	}
	
	public void setTremoloBar(TGEffectTremoloBar tremoloBar) {
		this.tremoloBar = tremoloBar;
		//si no es null quito los efectos incompatibles
		if(isTremoloBar()){
            bend = null;
            trill = null;
            deadNote = false;
            slide = false;
            hammer = false;
		}
	}
	
	public boolean isTremoloBar() {
		return (tremoloBar != null);
	}
	
	
	public TGEffectTrill getTrill() {
		return trill;
	}
	
	public void setTrill(TGEffectTrill trill) {
		this.trill = trill;
		//si es true, quito los efectos incompatibles
		if(isTrill()){
            bend = null;
            tremoloBar = null;
            tremoloPicking = null;
            slide = false;
            hammer = false;
            deadNote = false;
            vibrato = false;
		}
	}
	
	public boolean isTrill() {
		return (trill != null);
	}
	
	public TGEffectTremoloPicking getTremoloPicking() {
		return tremoloPicking;
	}
	
	public void setTremoloPicking(TGEffectTremoloPicking tremoloPicking) {
		this.tremoloPicking = tremoloPicking;
		//si es true, quito los efectos incompatibles
		if(isTremoloPicking()){
            trill = null;
            bend = null;
            tremoloBar = null;
            slide = false;
            hammer = false;
            deadNote = false;
            vibrato = false;
		}
	}
	
	public boolean isTremoloPicking() {
		return (tremoloPicking != null);
	}
	
	public boolean isHammer() {
		return hammer;
	}
	
	public void setHammer(boolean hammer) {
		this.hammer = hammer;
		//si es true, quito los efectos incompatibles
		if(this.hammer){
            trill = null;
            tremoloBar = null;
            bend = null;
            deadNote = false;
            slide = false;
		}
	}
	
	public boolean isSlide() {
		return slide;
	}
	
	public void setSlide(boolean slide) {
		this.slide = slide;
		//si es true, quito los efectos incompatibles
		if(this.slide){
            trill = null;
            tremoloBar = null;
            bend = null;
            deadNote = false;
            hammer = false;
		}
	}
	
	public boolean isGhostNote() {
		return ghostNote;
	}
	
	public void setGhostNote(boolean ghostNote) {
		this.ghostNote = ghostNote;
		//si es true, quito los efectos incompatibles
		if(this.ghostNote){
            accentuatedNote = false;
            heavyAccentuatedNote = false;
		}
	}
	
	public boolean isAccentuatedNote() {
		return accentuatedNote;
	}
	
	public void setAccentuatedNote(boolean accentuatedNote) {
		this.accentuatedNote = accentuatedNote;
		//si es true, quito los efectos incompatibles
		if(this.accentuatedNote){
            ghostNote = false;
            heavyAccentuatedNote = false;
		}
	}
	
	public boolean isHeavyAccentuatedNote() {
		return heavyAccentuatedNote;
	}
	
	public void setHeavyAccentuatedNote(boolean heavyAccentuatedNote) {
		this.heavyAccentuatedNote = heavyAccentuatedNote;
		//si es true, quito los efectos incompatibles
		if(this.heavyAccentuatedNote){
            ghostNote = false;
            accentuatedNote = false;
		}
	}
	
	public void setHarmonic(TGEffectHarmonic harmonic) {
		this.harmonic = harmonic;
	}
	
	public TGEffectHarmonic getHarmonic() {
		return harmonic;
	}
	
	public boolean isHarmonic() {
		return (harmonic != null);
	}
	
	public TGEffectGrace getGrace() {
		return grace;
	}
	
	public void setGrace(TGEffectGrace grace) {
		this.grace = grace;
	}
	
	public boolean isGrace() {
		return (grace != null);
	}
	
	public boolean isPalmMute() {
		return palmMute;
	}
	
	public void setPalmMute(boolean palmMute) {
		this.palmMute = palmMute;
		//si es true, quito los efectos incompatibles
		if(this.palmMute){
            staccato = false;
		}
	}
	
	public boolean isStaccato() {
		return staccato;
	}
	
	public void setStaccato(boolean staccato) {
		this.staccato = staccato;
		//si es true, quito los efectos incompatibles
		if(this.staccato){
            palmMute = false;
		}
	}
	
	public boolean isPopping() {
		return popping;
	}
	
	public void setPopping(boolean popping) {
		this.popping = popping;
		//si es true, quito los efectos incompatibles
		if(this.popping){
            tapping = false;
            slapping = false;
		}
	}
	
	public boolean isSlapping() {
		return slapping;
	}
	
	public void setSlapping(boolean slapping) {
		this.slapping = slapping;
		//si es true, quito los efectos incompatibles
		if(this.slapping){
            tapping = false;
            popping = false;
		}
	}
	
	public boolean isTapping() {
		return tapping;
	}
	
	public void setTapping(boolean tapping) {
		this.tapping = tapping;
		//si es true, quito los efectos incompatibles
		if(this.tapping){
            slapping = false;
            popping = false;
		}
	}
	
	public boolean isFadeIn() {
		return fadeIn;
	}
	
	public void setFadeIn(boolean fadeIn) {
		this.fadeIn = fadeIn;
	}
	
	public boolean hasAnyEffect(){
		return (isBend() ||
				isTremoloBar() ||
				isHarmonic() ||
				isGrace() ||
				isTrill() ||
				isTremoloPicking() ||
                vibrato ||
                deadNote ||
                slide ||
                hammer ||
                ghostNote ||
                accentuatedNote ||
                heavyAccentuatedNote ||
                palmMute ||
                staccato ||
                tapping ||
                slapping ||
                popping ||
                fadeIn);
	}
	
	public TGNoteEffect clone(TGFactory factory){
		TGNoteEffect effect = factory.newEffect();
		effect.setVibrato(vibrato);
		effect.setDeadNote(deadNote);
		effect.setSlide(slide);
		effect.setHammer(hammer);
		effect.setGhostNote(ghostNote);
		effect.setAccentuatedNote(accentuatedNote);
		effect.setHeavyAccentuatedNote(heavyAccentuatedNote);
		effect.setPalmMute(palmMute);
		effect.setStaccato(staccato);
		effect.setTapping(tapping);
		effect.setSlapping(slapping);
		effect.setPopping(popping);
        effect.fadeIn = fadeIn;
		effect.setBend(isBend()? bend.clone(factory) :null);
		effect.setTremoloBar(isTremoloBar()? tremoloBar.clone(factory) :null);
        effect.harmonic = isHarmonic() ? harmonic.clone(factory) : null;
        effect.grace = isGrace() ? grace.clone(factory) : null;
		effect.setTrill(isTrill()? trill.clone(factory) :null);
		effect.setTremoloPicking(isTremoloPicking()? tremoloPicking.clone(factory) :null);
		return effect;
	}
	
}
