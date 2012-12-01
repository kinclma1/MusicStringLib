package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGRawImporter;

public abstract class TGImporterPlugin extends TGPluginAdapter{
	
	private boolean loaded;
	private TGLocalFileImporter importer;
	
	protected abstract TGLocalFileImporter getImporter();
	
	@Override
    public void init() throws TGPluginException {
        importer = getImporter();
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
			TGFileFormatManager.instance().addImporter(importer);
            loaded = true;
		}
	}
	
	protected void removePlugin() {
		if(loaded){
			TGFileFormatManager.instance().removeImporter(importer);
            loaded = false;
		}
	}
}
