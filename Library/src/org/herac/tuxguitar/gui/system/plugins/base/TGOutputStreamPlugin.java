package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public abstract class TGOutputStreamPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGOutputStreamBase stream;
	
	protected abstract TGOutputStreamBase getOutputStream();
	
	@Override
    public void init() throws TGPluginException {
        stream = getOutputStream();
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
			TGFileFormatManager.instance().addOutputStream(stream);
            loaded = true;
		}
	}
	
	protected void removePlugin() {
		if(loaded){
			TGFileFormatManager.instance().removeOutputStream(stream);
            loaded = false;
		}
	}
}
