package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.OutputStream;

public class LilypondSongExporter implements TGLocalFileExporter {
	
	private OutputStream stream;
	
	@Override
    public String getExportName() {
		return "Lilypond";
	}
	
	@Override
    public TGFileFormat getFileFormat() {
		return new TGFileFormat("Lilypond","*.ly");
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
    public void exportSong(TGSong song) {
		if(stream != null){
			new LilypondOutputStream(stream).writeSong(song);
		}
	}
}