package org.emile.client.dialog.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CPropertyStore {

	private HashMap<String,ArrayList<String>> store;
	
	public CPropertyStore() {
		store = new HashMap<String,ArrayList<String>>();
	}
	
	public void add(String key, String name, String value0, String value1) {
		
		ArrayList<String> property = new ArrayList<String>();
		property.add(name);
		property.add(value0);
		property.add(value1);
		store.put(key, property);
		
	}
	
	public void set(String key, String value) {
		
		ArrayList<String> property = store.get(key);
		property.set(2, value);
		store.put(key, property);
	}
	
	public void set(String key, String value0, String value1) {
		
		ArrayList<String> property = store.get(key);
		property.set(1, value0);
		property.set(2, value1);
		store.put(key, property);
	}
	
	public ArrayList<String> get(String key) {
		return store.get(key);
	}
	
	public String getName(String key) {
		return store.get(key).get(0);
	}

	public boolean equals (String key) {
		ArrayList<String> property = store.get(key);	
		return property.get(1).equals(property.get(2));	
	}
	
	public ArrayList<String>getKeyList() {
		return new ArrayList<String>(store.keySet());
	}
	
	public boolean validate() {
	    Iterator it = store.entrySet().iterator();
	    while (it.hasNext()) {
		     Map.Entry pair = (Map.Entry)it.next();
		     ArrayList<String> property = (ArrayList<String>)pair.getValue();
		     if (!property.get(1).equals(property.get(2))) {
		    	 return false;
		     }
		}
	    return true;	
	}
	
}
