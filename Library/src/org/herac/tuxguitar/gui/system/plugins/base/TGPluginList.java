package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.system.plugins.TGPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;

import java.util.Iterator;
import java.util.List;

public abstract class TGPluginList extends TGPluginAdapter{
	
	private List<TGPlugin> plugins;
	
	public TGPluginList(){
    }
	
	@Override
    public void init() throws TGPluginException {
		Iterator<TGPlugin> it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.init();
		}
	}
	
	@Override
    public void close() throws TGPluginException {
		Iterator<TGPlugin> it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.close();
		}
	}
	
	@Override
    public void setEnabled(boolean enabled) throws TGPluginException {
		Iterator<TGPlugin> it = getIterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			plugin.setEnabled( enabled);
		}
	}
	
	private Iterator<TGPlugin> getIterator() throws TGPluginException {
		if(plugins == null){
            plugins = getPlugins();
		}
		return plugins.iterator();
	}
	
	protected abstract List<TGPlugin> getPlugins();
}
