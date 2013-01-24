/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.herac.tuxguitar.io.musicstring;

import cz.cvut.fel.kinclma1.MusicStringSong;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author void
 */
public class MusicStringExporter implements TGLocalFileExporter {
    
    private OutputStream stream;

    @Override
    public TGFileFormat getFileFormat() {
        return new TGFileFormat("musicstring", "*.musicstring");
    }

    @Override
    public boolean configure(boolean setDefaults) {
        return true;
    }

    @Override
    public void init(TGFactory factory, OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public String getExportName() {
        return "MusicString";
    }

    @Override
    public void exportSong(TGSong song) throws TGFileFormatException {
        String music = new MusicStringSong(song).toString();
        if(stream != null) {
            try {
                stream.write(music.getBytes());
                stream.flush();
            } catch (IOException ex) {
                throw new TGFileFormatException("Unable to export song");
            }
        }
    }
    
}
