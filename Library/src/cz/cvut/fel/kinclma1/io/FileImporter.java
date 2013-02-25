/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.kinclma1.io;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.*;
import java.util.Iterator;

/**
 *
 * @author void
 */
public class FileImporter {

    public TGSong importFile(String fileName) throws IOException, TGFileFormatException {
        return importFile(new File(fileName));
    }

    public TGSong importFile(File file) throws IOException, TGFileFormatException {
        TGSongManager manager = new TGSongManager();
        TGSong song;
            try {
                song = TGFileFormatManager.instance().getLoader().load(manager.getFactory(), new FileInputStream(file));
            } catch (TGFileFormatException e) {
                song = importSong(manager.getFactory(), file.getCanonicalPath());
            }

            if (song != null) {
                manager.setSong(song);
                manager.orderBeats();
            }

        return manager.getSong();
    }

    private TGSong importSong(TGFactory factory, String filename) throws FileNotFoundException, TGFileFormatException {
        Iterator importers = TGFileFormatManager.instance().getImporters();
        while (importers.hasNext()) {
                TGLocalFileImporter currentImporter = (TGLocalFileImporter) importers.next();
                currentImporter.configure(true);
                if (isSupportedExtension(filename, currentImporter)) {
                    InputStream input = new BufferedInputStream(new FileInputStream(filename));
                    currentImporter.init(factory, input);
                    return currentImporter.importSong();
                }
        }
        return null;
    }

    private boolean isSupportedExtension(String filename, TGLocalFileImporter currentImporter) {
        try {
            String extension = filename.substring(filename.lastIndexOf("."), filename.length());
            extension = "*" + extension.toLowerCase();
            String[] formats = currentImporter.getFileFormat().getSupportedFormats().split(";");
            for (String format : formats) {
                if (format.toLowerCase().equals(extension)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }

        return false;
    }
}
