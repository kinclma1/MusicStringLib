package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGRawExporter;

public abstract class TGExporterPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGLocalFileExporter exporter;
	
	protected abstract TGLocalFileExporter getExporter();
	
	@Override
    public void init() throws TGPluginException {
        exporter = getExporter();
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
		if(!loaded && exporter != null){
			TGFileFormatManager.instance().addExporter(exporter);
            loaded = true;
		}
	}
	
	protected void removePlugin() {
		if(loaded && exporter != null){
			TGFileFormatManager.instance().removeExporter(exporter);
            loaded = false;
		}
	}	
}
