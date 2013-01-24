package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;

import java.io.OutputStream;

public interface TGLocalFileExporter extends TGRawExporter {
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(boolean setDefaults);
	
	public void init(TGFactory factory,OutputStream stream);
	
}
