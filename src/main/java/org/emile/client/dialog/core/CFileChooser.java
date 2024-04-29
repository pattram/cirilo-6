package org.emile.client.dialog.core;

import javax.swing.JFileChooser;

import org.emile.client.Common;

public class CFileChooser {
	
	static public JFileChooser get(String title, String uploadPath, String[] filter, String mimetype, int selectionmode) {
		
		CFileFilter ff = null;
		
		JFileChooser fc = new JFileChooser(uploadPath);
		
		fc.setDialogTitle(title);
		
		if (filter != null) {
			ff = new CFileFilter(filter);
		}
		
		if (selectionmode > -1) {
			fc.setFileSelectionMode(selectionmode);
		}
		
		if (mimetype != null) { 
			for (int i = 1; i < Common.IMAGE_EXTENSIONS.size(); i++) {	
				if (mimetype.equals(Common.IMAGE_EXTENSIONS.get(i))) {
					CImagePreviewPanel preview = new CImagePreviewPanel();
					fc.setAccessory(preview);
					fc.addPropertyChangeListener(preview);
					ff = new CFileFilter(Common.IMAGE_EXTENSIONS.get(0).split("[,]"));
					fc.addChoosableFileFilter(ff);		
					break;
				}
			}
		}	
		
		if (ff != null) fc.setFileFilter(ff);
		
		return fc;
	}

}
