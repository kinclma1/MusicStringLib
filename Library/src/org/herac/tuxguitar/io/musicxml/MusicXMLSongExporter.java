package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.OutputStream;

public class MusicXMLSongExporter implements TGLocalFileExporter {
	
	private OutputStream stream;
	
	@Override
    public String getExportName() {
		return "MusicXML";
	}
	
	@Override
    public TGFileFormat getFileFormat() {
		return new TGFileFormat("MusicXML","*.xml");
	}
	
	@Override
    public boolean configure(boolean setDefaults) {
		return true;
	}
	
	@Override
    public void init(TGFactory factory,OutputStream stream){
		this.stream = stream;
	}
	
	@Override
    public void exportSong(TGSong song) throws TGFileFormatException {
		if(stream != null ){
			new MusicXMLWriter(stream).writeSong(song);
		}
	}
}