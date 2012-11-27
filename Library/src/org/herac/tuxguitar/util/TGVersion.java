package org.herac.tuxguitar.util;

public class TGVersion {
	
	public static final TGVersion CURRENT = new TGVersion();
	
	private int major;
	private int minor;
	private int revision;
	
	public TGVersion(){
		this.major = 1;
		this.minor = 2;
		this.revision = 0;
	}
	
	public int getMajor() {
		return this.major;
	}
	
	public int getMinor() {
		return this.minor;
	}
	
	public int getRevision() {
		return this.revision;
	}
	
	public boolean isSameVersion(TGVersion version){
        return version != null && (version.getMajor() == getMajor() && version.getMinor() == getMinor() && version.getRevision() == getRevision());
    }
	
	public String getVersion(){
		String version = (getMajor() + "." + getMinor());
		if( getRevision() > 0 ){
			version += ("." + getRevision());
		}
		return version;
	}
	
	public String toString(){
		return getVersion();
	}
}
