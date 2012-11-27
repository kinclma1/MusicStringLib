package org.herac.tuxguitar.io.gpx;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class GPXInputStream implements TGInputStreamBase {
	
	private int gpxHeader;
	private InputStream gpxStream;
	private GPXFileSystem gpxFileSystem;
	private TGFactory factory;
	
	@Override
    public TGFileFormat getFileFormat() {
		return new TGFileFormat("Guitar Pro 6","*.gpx");
	}
	
	@Override
    public void init(TGFactory factory, InputStream stream) {
		this.factory = factory;
        gpxStream = stream;
        gpxHeader = 0;
        gpxFileSystem = new GPXFileSystem();
	}
	
	@Override
    public boolean isSupportedVersion() {
		try {
            gpxHeader = gpxFileSystem.getHeader(gpxStream);
			
			return gpxFileSystem.isSupportedHeader(gpxHeader);
		} catch (Throwable throwable) {
			return false;
		}
	}
	
	@Override
    public TGSong readSong() throws TGFileFormatException, IOException {
		try {
            gpxFileSystem.load(gpxHeader, gpxStream);
			
			GPXDocumentReader gpxReader = new GPXDocumentReader(gpxFileSystem.getFileContentsAsStream("score.gpif"));
			GPXDocumentParser gpxParser = new GPXDocumentParser(factory, gpxReader.read() );
			
			return gpxParser.parse();
		} catch (Throwable throwable) {
			throw new TGFileFormatException( throwable );
		}
	}
}
