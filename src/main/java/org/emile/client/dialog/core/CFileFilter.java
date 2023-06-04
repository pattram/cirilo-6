package org.emile.client.dialog.core;

import java.io.File;

public class CFileFilter extends javax.swing.filechooser.FileFilter {
	  
	  private String[] filter;
	  
	  public CFileFilter(String[] f) {
		  this.filter = f;
	  }
	  
	  public boolean accept(File file) {
		
		String filename = file.getName();
	    boolean found = false;
		
		if (file.isDirectory()) {
		    return true;
	    }
		
	    for (int i = 0; i < this.filter.length; i++) {
	    	if (filename.endsWith(this.filter[i])) {
	    		found = true;
	    		break;
	    	};
	    }
	    return found;
	  }
	  
	  public String getDescription() {
		  
		    String desc = "";
		    for (int i = 0; i < this.filter.length; i++) {
		    	desc += "*" + this.filter[i] + (i < this.filter.length -1 ? ";" : "");
		    };
		    return desc;
	  }
}      