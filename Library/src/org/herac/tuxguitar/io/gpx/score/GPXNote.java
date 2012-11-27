package org.herac.tuxguitar.io.gpx.score;

public class GPXNote {
	
	private int id;
	private int fret;
	private int string;
	private int tone;
	private int octave;
	private int element;
	private int variation;
	private int midiNumber;
	// bends are measured in % of full step.  100 = full step, not 100 (cents) = half step
	private boolean BendEnabled;
	private int BendOriginValue;
	private int BendMiddleValue;
	private int BendDestinationValue;

	private boolean hammer;
	private boolean slide;
	private int SlideFlags;	// 1, 2, 4, 8, 16 - 2 seems to be up, 4 seems to be down.
	private boolean vibrato;
	private boolean Tapped;
	private boolean tieDestination;
	private boolean mutedEnabled;
	private boolean palmMutedEnabled;

	private boolean Harmonic;
	private int HarmonicFret;
	private String HarmonicType;
	
	public GPXNote(){
        id = -1;
        fret = -1;
        string = -1;
        tone = -1;
        octave = -1;
        element = -1;
        variation = -1;
        midiNumber = -1;
        hammer = false;
        BendEnabled = false;
        BendOriginValue = 0;
        BendMiddleValue = 0;
        BendDestinationValue = 0;
        SlideFlags = 0;
        Harmonic = false;
        HarmonicType = "";
        HarmonicFret = -1;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getFret() {
		return fret;
	}
	
	public void setFret(int fret) {
		this.fret = fret;
	}
	
	public int getString() {
		return string;
	}
	
	public void setString(int string) {
		this.string = string;
	}
	
	public int getTone() {
		return tone;
	}
	
	public void setTone(int tone) {
		this.tone = tone;
	}
	
	public int getOctave() {
		return octave;
	}
	
	public void setOctave(int octave) {
		this.octave = octave;
	}
	
	public boolean isTieDestination() {
		return tieDestination;
	}
	
	public void setTieDestination(boolean tieDestination) {
		this.tieDestination = tieDestination;
	}

	public boolean isMutedEnabled() {
		return mutedEnabled;
	}

	public void setMutedEnabled(boolean mutedEnabled) {
		this.mutedEnabled = mutedEnabled;
	}

	public boolean isPalmMutedEnabled() {
		return palmMutedEnabled;
	}

	public void setPalmMutedEnabled(boolean palmMutedEnabled) {
		this.palmMutedEnabled = palmMutedEnabled;
	}

	public boolean isSlide() {
		return slide;
	}

	public void setSlide(boolean slide) {
		this.slide = slide;
	}

	public int getSlideFlags() {
		return SlideFlags;
	}

	public void setSlideFlags(int slideFlags) {
        SlideFlags = slideFlags;
	}

	public boolean isVibrato() {
		return vibrato;
	}

	public void setVibrato(boolean vibrato) {
		this.vibrato = vibrato;
	}

	public boolean isTapped() {
		return Tapped;
	}

	public void setTapped(boolean Tapped) {
		this.Tapped = Tapped;
	}

	public int getElement() {
		return element;
	}

	public void setElement(int element) {
		this.element = element;
	}

	public int getVariation() {
		return variation;
	}

	public void setVariation(int variation) {
		this.variation = variation;
	}

	public int getMidiNumber() {
		return midiNumber;
	}

	public void setMidiNumber(int midiNumber) {
		this.midiNumber = midiNumber;
	}
	
	public int getBendOriginValue() {
		return BendOriginValue;
	}

	public void setBendOriginValue(int BendOriginValue) {
		this.BendOriginValue = BendOriginValue;
	}

	public int getBendMiddleValue() {
		return BendMiddleValue;
	}

	public void setBendMiddleValue(int BendMiddleValue) {
		this.BendMiddleValue = BendMiddleValue;
	}

	public int getBendDestinationValue() {
		return BendDestinationValue;
	}

	public void setBendDestinationValue(int BendDestinationValue) {
		this.BendDestinationValue = BendDestinationValue;
	}

	public boolean getBendEnabled() {
		return BendEnabled;
	}

	public void setBendEnabled(boolean BendEnabled) {
		this.BendEnabled = BendEnabled;
	}

	public boolean isHarmonic() {
		return Harmonic;
	}
	
	public int getHarmonicFret() {
		return HarmonicFret;
	}

	public void setHarmonicFret(int HarmonicFret) {
        Harmonic = true;
		this.HarmonicFret = HarmonicFret;
	}

	public String getHarmonicType() {
		return HarmonicType;
	}

	public void setHarmonicType(String HarmonicType) {
        Harmonic = true;
		this.HarmonicType = HarmonicType;
	}

	public void setHammer(boolean Hammer) {
        hammer = Hammer;
	}

	public boolean isHammer() {
		return hammer;
	}
}
