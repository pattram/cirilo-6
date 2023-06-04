package org.emile.client.utils;

import java.util.HashMap;

import org.emile.client.ServiceNames;

import voodoosoft.jroots.core.CServiceProvider;
	
public class AnnulationStack {
		
	public void set(String key) {
		try {
			HashMap<String, Boolean> queue = (HashMap<String, Boolean>) CServiceProvider.getService(ServiceNames.ANNULATION_STACK);
			queue.put(key, true);
			CServiceProvider.addService(queue, ServiceNames.ANNULATION_STACK);
		} catch (Exception e) {}		
	}

	public void unset(String key) {
		try {
			HashMap<String, Boolean> queue = (HashMap<String, Boolean>) CServiceProvider.getService(ServiceNames.ANNULATION_STACK);
			queue.put(key, false);
			CServiceProvider.addService(queue, ServiceNames.ANNULATION_STACK);
		} catch (Exception e) {}		
	}

	public boolean get(String key) {
		boolean value = false;
		try {
			HashMap<String, Boolean> queue = (HashMap<String, Boolean>) CServiceProvider.getService(ServiceNames.ANNULATION_STACK);
			value = queue.get(key);
		} catch (Exception e) {}		
		return value;
	}

}
