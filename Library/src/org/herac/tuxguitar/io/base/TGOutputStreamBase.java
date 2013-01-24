package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.IOException;
import java.io.OutputStream;

public interface TGOutputStreamBase {
	
	public void init(TGFactory factory,OutputStream stream);
	
	public boolean isSupportedExtension(String extension);
	
	public TGFileFormat getFileFormat();
	
	public void writeSong(TGSong song) throws IOException;
}
