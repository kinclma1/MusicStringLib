package org.herac.tuxguitar.util;

public class TGVersion {
	
	public static final TGVersion CURRENT = new TGVersion();
	
	private int major;
	private int minor;
	private int revision;
	
	public TGVersion(){
        major = 1;
        minor = 2;
        revision = 0;
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public int getRevision() {
		return revision;
	}
	
	public boolean isSameVersion(TGVersion version){
        return version != null && (version.major == major && version.minor == minor && version.revision == revision);
    }
	
	public String getVersion(){
		String version = (major + "." + minor);
		if(revision > 0 ){
			version += ("." + revision);
		}
		return version;
	}
	
	public String toString(){
		return getVersion();
	}
}
