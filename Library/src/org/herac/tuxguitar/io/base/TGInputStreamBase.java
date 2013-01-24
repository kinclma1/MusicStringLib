package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.IOException;
import java.io.InputStream;

public interface TGInputStreamBase {
	
	public void init(TGFactory factory,InputStream stream);
	
	public boolean isSupportedVersion();
	
	public TGFileFormat getFileFormat();
	
	public TGSong readSong() throws TGFileFormatException,IOException;
}
