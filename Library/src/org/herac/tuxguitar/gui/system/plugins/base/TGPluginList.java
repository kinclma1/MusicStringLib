package org.herac.tuxguitar.gui.system.plugins.base;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.system.plugins.TGPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;

public abstract class TGPluginList extends TGPluginAdapter{
	
	private List plugins;
	
	public TGPluginList(){
    }
	
	@Override
    public void init() throws TGPluginException {
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.init();
		}
	}
	
	@Override
    public void close() throws TGPluginException {
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.close();
		}
	}
	
	@Override
    public void setEnabled(boolean enabled) throws TGPluginException {
		Iterator it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.setEnabled( enabled);
		}
	}
	
	private Iterator getIterator() throws TGPluginException {
		if(plugins == null){
            plugins = getPlugins();
		}
		return plugins.iterator();
	}
	
	protected abstract List getPlugins();
}
