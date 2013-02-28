/*
 * Created on 19-dic-2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author julian
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGSongLoader {
	
	public TGSongLoader(){
    }
	
	/**
	 * @return TGSong
	 * @throws TGFileFormatException
	 */
	public TGSong load(TGFactory factory,InputStream is) throws TGFileFormatException, IOException {
        BufferedInputStream stream = new BufferedInputStream(is);
        try{
			stream.mark(1);
			Iterator<TGInputStreamBase> it = TGFileFormatManager.instance().getInputStreams();
			while(it.hasNext()){
				TGInputStreamBase reader = it.next();
				reader.init(factory,stream);
				if(reader.isSupportedVersion()){
					return reader.readSong();
				}
				stream.reset();
			}
		}catch(Throwable t){
			throw new TGFileFormatException(t);
		} finally {
            stream.close();
        }
		throw new TGFileFormatException("Unsupported file format");
	}
}