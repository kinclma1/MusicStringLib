package org.herac.tuxguitar.io.midi;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.player.base.MidiSequenceParser;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiSongExporter implements TGLocalFileExporter {
	
	private OutputStream stream;
	private MidiSettings settings;
	
	@Override
    public String getExportName() {
		return "Midi";
	}
	
	@Override
    public TGFileFormat getFileFormat() {
		return new TGFileFormat("Midi","*.mid;*.midi");
	}
	
	@Override
    public boolean configure(boolean setDefaults) {
        settings = MidiSettings.getDefaults();
		return (settings != null);
	}
	
	@Override
    public void init(TGFactory factory,OutputStream stream){
		this.stream = stream;
	}
	
	@Override
    public void exportSong(TGSong song) {
		if(stream != null && settings != null ){
			TGSongManager manager = new TGSongManager();
			manager.setSong(song);
			MidiSequenceParser parser = new MidiSequenceParser(manager,MidiSequenceParser.DEFAULT_EXPORT_FLAGS,100, settings.getTranspose());
			MidiSequenceHandlerImpl sequence = new MidiSequenceHandlerImpl( (song.countTracks() + 1) , stream);
			parser.parse(sequence);
		}
	}
}
