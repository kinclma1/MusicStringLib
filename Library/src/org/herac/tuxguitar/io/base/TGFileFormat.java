package org.herac.tuxguitar.io.base;

public class TGFileFormat {
	
	public static final String EXTENSION_SEPARATOR = ";";
	
	private String name;
	private String supportedFormats;
	
	public TGFileFormat(String name, String supportedFormats) {
		this.name = name;
		this.supportedFormats = supportedFormats;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSupportedFormats() {
		return supportedFormats;
	}

    @Override
    public String toString() {
        return name + " (" + supportedFormats + ")";
    }
}
