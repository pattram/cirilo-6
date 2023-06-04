package org.emile.client.dialog.core;

import javax.swing.JMenuItem;

import voodoosoft.jroots.gui.IGuiComposite;

public class CMenuItem {
	
	
	public static JMenuItem get (IGuiComposite menu, String widget) {
		
		JMenuItem item = null;
		
		try {
			item = (JMenuItem) menu.getWidget(widget);
		} catch (Exception e) {}
		
		return item;
	}
	
	public static void setEnabled (IGuiComposite menu, String widget, boolean status) {
				
		try {
			((JMenuItem) menu.getWidget(widget)).setEnabled(status);
		} catch (Exception e) {}
	
	}

}
