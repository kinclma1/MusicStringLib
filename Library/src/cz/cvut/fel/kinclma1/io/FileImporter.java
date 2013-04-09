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
 * Helper class to import files to a TGSong object
 */
public class FileImporter {

    /**
     * Imports file from the given path
     * @param fileName path to the file to be imported
     * @return TGSong object created from the input file
     * @throws IOException
     * @throws TGFileFormatException
     */
    public TGSong importFile(String fileName) throws IOException, TGFileFormatException {
        return importFile(new File(fileName));
    }

    /**
     * Imports file determined by the given File object
     * @param file File object determining the input file
     * @return TGSong object created from the input file
     * @throws IOException
     * @throws TGFileFormatException
     */
    public TGSong importFile(File file) throws IOException, TGFileFormatException {
        TGSongManager manager = new TGSongManager();
        TGSong song;
        FileInputStream fis = new FileInputStream(file);
            try {
                song = TGFileFormatManager.instance().getLoader().load(manager.getFactory(), fis);
            } catch (TGFileFormatException e) {
                song = importSong(manager.getFactory(), file.getCanonicalPath(), fis);
            } finally {
                fis.close();
            }

            if (song != null) {
                manager.setSong(song);
                manager.orderBeats();
            }

        return manager.getSong();
    }

    private TGSong importSong(TGFactory factory, String filename, FileInputStream fis) throws IOException, TGFileFormatException {
        Iterator importers = TGFileFormatManager.instance().getImporters();
        while (importers.hasNext()) {
                TGLocalFileImporter currentImporter = (TGLocalFileImporter) importers.next();
                currentImporter.configure(true);
                if (isSupportedExtension(filename, currentImporter)) {
                    InputStream input = new BufferedInputStream(fis);
                    try {
                        currentImporter.init(factory, input);
                        return currentImporter.importSong();
                    } finally {
                        input.close();
                    }
                }
        }
        return null;
    }

    private boolean isSupportedExtension(String filename, TGLocalFileImporter currentImporter) {
        try {
            String extension = filename.substring(filename.lastIndexOf('.'), filename.length());
            extension = '*' + extension.toLowerCase();
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
