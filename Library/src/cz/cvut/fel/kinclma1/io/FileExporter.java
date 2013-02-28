package cz.cvut.fel.kinclma1.io;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 11.11.12
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public class FileExporter {

    public void exportSong(TGSong song, String filename) throws IOException, TGFileFormatException {
        String extension = filename.substring(filename.lastIndexOf('.'));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
        try {
            TGFileFormatManager formatManager = TGFileFormatManager.instance();
            Iterator<TGOutputStreamBase> outputStreams = formatManager.getOutputStreams();
            while (outputStreams.hasNext()) {
                TGOutputStreamBase outputStreamBase = outputStreams.next();
                if (outputStreamBase.getFileFormat().getSupportedFormats().contains(extension)) {
                    outputStreamBase.init(new TGFactory(), out);
                    outputStreamBase.writeSong(song);
                    out.flush();
                    return;
                }
            }
            Iterator<TGLocalFileExporter> exporters = formatManager.getExporters();
            while (exporters.hasNext()) {
                TGLocalFileExporter exporter = exporters.next();
                if (exporter.getFileFormat().getSupportedFormats().contains(extension)) {
                    exporter.init(new TGFactory(), out);
                    exporter.exportSong(song);
                    out.flush();
                    return;
                }
            }
        } finally {
            out.close();
        }
        throw new TGFileFormatException("Unsupported file extension");
    }
}
