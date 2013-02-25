package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.io.gpx.GPXInputStream;
import org.herac.tuxguitar.io.gtp.*;
import org.herac.tuxguitar.io.lilypond.LilypondSongExporter;
import org.herac.tuxguitar.io.musicxml.MusicXMLSongExporter;
import org.herac.tuxguitar.io.musicxml.MusicXMLSongImporter;
import org.herac.tuxguitar.io.tg.TGInputStream;
import org.herac.tuxguitar.io.tg.TGOutputStream;
import org.herac.tuxguitar.io.tg.TGStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGFileFormatManager {

    public static final String DEFAULT_EXTENSION = TGStream.TG_FORMAT_EXTENSION;

    private static TGFileFormatManager instance;

    private TGSongLoader loader;
    private TGSongWriter writer;
    private List<TGInputStreamBase> inputStreams;
    private List<TGOutputStreamBase> outputStreams;
    private List<TGLocalFileExporter> exporters;
    private List<TGLocalFileImporter> importers;

    private TGFileFormatManager() {
        loader = new TGSongLoader();
        writer = new TGSongWriter();
        inputStreams = new ArrayList<TGInputStreamBase>(7);
        outputStreams = new ArrayList<TGOutputStreamBase>(2);
        exporters = new ArrayList<TGLocalFileExporter>(3);
        importers = new ArrayList<TGLocalFileImporter>(1);
        addDefaultStreams();
        addDefaultImporters();
        addDefaultExporters();
    }

    public static TGFileFormatManager instance() {
        if (instance == null) {
            instance = new TGFileFormatManager();
        }
        return instance;
    }

    public TGSongLoader getLoader() {
        return loader;
    }

    public TGSongWriter getWriter() {
        return writer;
    }

    public void addInputStream(TGInputStreamBase stream) {
        inputStreams.add(stream);
    }

    public void removeInputStream(TGInputStreamBase stream) {
        inputStreams.remove(stream);
    }

    public int countInputStreams() {
        return inputStreams.size();
    }

    public void addOutputStream(TGOutputStreamBase stream) {
        outputStreams.add(stream);
    }

    public void removeOutputStream(TGOutputStreamBase stream) {
        outputStreams.remove(stream);
    }

    public int countOutputStreams() {
        return outputStreams.size();
    }

    public void addImporter(TGLocalFileImporter importer) {
        importers.add(importer);
    }

    public void removeImporter(TGLocalFileImporter importer) {
        importers.remove(importer);
    }

    public int countImporters() {
        return importers.size();
    }

    public void addExporter(TGLocalFileExporter exporter) {
        exporters.add(exporter);
    }

    public void removeExporter(TGRawExporter exporter) {
        exporters.remove(exporter);
    }

    public int countExporters() {
        return exporters.size();
    }

    public Iterator<TGInputStreamBase> getInputStreams() {
        return inputStreams.iterator();
    }

    public Iterator<TGOutputStreamBase> getOutputStreams() {
        return outputStreams.iterator();
    }

    public Iterator<TGLocalFileImporter> getImporters() {
        return importers.iterator();
    }

    public Iterator<TGLocalFileExporter> getExporters() {
        return exporters.iterator();
    }

    public List getInputFormats() {
        List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
        Iterator<TGInputStreamBase> it = getInputStreams();
        while (it.hasNext()) {
            TGInputStreamBase stream = (TGInputStreamBase) it.next();
            TGFileFormat format = stream.getFileFormat();
            if (!existsFormat(format, formats)) {
                formats.add(format);
            }
        }
        return formats;
    }

    public List<TGFileFormat> getOutputFormats() {
        List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
        Iterator<TGOutputStreamBase> it = getOutputStreams();
        while (it.hasNext()) {
            TGOutputStreamBase stream = (TGOutputStreamBase) it.next();
            TGFileFormat format = stream.getFileFormat();
            if (!existsFormat(format, formats)) {
                formats.add(format);
            }
        }
        return formats;
    }

    private boolean existsFormat(TGFileFormat format, List formats) {
        Iterator<TGFileFormat> it = formats.iterator();
        while (it.hasNext()) {
            TGFileFormat comparator = (TGFileFormat) it.next();
            if (comparator.getName().equals(format.getName()) || comparator.getSupportedFormats().equals(format.getSupportedFormats())) {
                return true;
            }
        }
        return false;
    }

    private void addDefaultStreams() {
        addInputStream(new TGInputStream());
        addOutputStream(new TGOutputStream());
        addInputStream(new GP3InputStream(new GTPSettings()));
        addInputStream(new GP4InputStream(new GTPSettings()));
        addInputStream(new GP5InputStream(new GTPSettings()));
        addOutputStream(new GP5OutputStream(new GTPSettings()));
        addInputStream(new GPXInputStream());
    }

    private void addDefaultExporters() {
        addExporter(new MusicXMLSongExporter());
        addExporter(new LilypondSongExporter());
    }

    private void addDefaultImporters() {
        addImporter(new MusicXMLSongImporter());
    }
}
