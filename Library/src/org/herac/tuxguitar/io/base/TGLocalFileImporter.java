package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;

import java.io.InputStream;

public interface TGLocalFileImporter extends TGRawImporter {
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(boolean setDefaults);
	
	public void init(TGFactory factory,InputStream stream);
	
}
