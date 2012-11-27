package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGInputStreamBase;

public abstract class TGInputStreamPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGInputStreamBase stream;
	
	protected abstract TGInputStreamBase getInputStream();
	
	@Override
    public void init() throws TGPluginException {
        stream = getInputStream();
	}
	
	@Override
    public void close() throws TGPluginException {
        removePlugin();
	}
	
	@Override
    public void setEnabled(boolean enabled) throws TGPluginException {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}
	
	protected void addPlugin() {
		if(!loaded){
			TGFileFormatManager.instance().addInputStream(stream);
            loaded = true;
		}
	}
	
	protected void removePlugin() {
		if(loaded){
			TGFileFormatManager.instance().removeInputStream(stream);
            loaded = false;
		}
	}
}
