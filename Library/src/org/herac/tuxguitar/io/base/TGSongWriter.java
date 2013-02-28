/*
 * Created on 19-dic-2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.*;
import java.util.Iterator;

/**
 * @author julian
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGSongWriter {
	
	public TGSongWriter(){
    }
	
	public void write(TGFactory factory,TGSong song,String path) throws TGFileFormatException, IOException {
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(path)));
        try {
			Iterator it = TGFileFormatManager.instance().getOutputStreams();
			while(it.hasNext()){
				TGOutputStreamBase writer = (TGOutputStreamBase)it.next();
				if(isSupportedExtension(writer,path)){
					writer.init(factory, out);
					writer.writeSong(song);
					return;
				}
			}
		} catch (Throwable t) {
			throw new TGFileFormatException(t);
		} finally {
            out.flush();
            out.close();
        }
		throw new TGFileFormatException("Unsupported file format");
	}
	
	private boolean isSupportedExtension(TGOutputStreamBase writer,String path){
		int index = path.lastIndexOf('.');
        return index > 0 && writer.isSupportedExtension(path.substring(index));
    }
	
}
