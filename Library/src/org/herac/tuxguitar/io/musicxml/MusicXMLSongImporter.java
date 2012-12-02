package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 1.12.12
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class MusicXMLSongImporter implements TGLocalFileImporter {
    private InputStream stream;
    private TGFactory factory;

    @Override
    public TGFileFormat getFileFormat() {
        return new TGFileFormat("MusicXML","*.xml");
    }

    @Override
    public boolean configure(boolean setDefaults) {
        return true;
    }

    @Override
    public void init(TGFactory factory, InputStream stream) {
        this.factory = factory;
        this.stream = stream;
    }

    @Override
    public String getImportName() {
        return "MusicXML";
    }

    @Override
    public TGSong importSong() throws TGFileFormatException {
        if (factory != null && stream != null) {
            return new MusicXMLReader(factory, stream).readSong();
        }
        return null;
    }
}
