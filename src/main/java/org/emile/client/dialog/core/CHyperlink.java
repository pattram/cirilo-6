package org.emile.client.dialog.core;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.emile.client.Common;
import org.emile.client.ServiceNames;

import voodoosoft.jroots.core.CServiceProvider;

public class CHyperlink extends JLabel {
	
	private URI uri;
	
	public CHyperlink() {
		
		this.setForeground(Color.BLUE.darker());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		this.addMouseListener(new MouseAdapter() {
			 
			@Override
			public void mouseClicked(MouseEvent me) {
		    	try {	   	
			    	ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			    	Desktop desktop = Desktop.getDesktop();
	
					if(desktop.isSupported(Desktop.Action.BROWSE)) {
						desktop.browse(uri);			
					} else {
						JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("nodesktop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
					}
			    } catch (Exception e) {
			    	e.printStackTrace();     
			    }
			}		    
			
			@Override
		    public void mouseEntered(MouseEvent me) {
		        // the mouse has entered the label
		    }
		 
		    @Override
		    public void mouseExited(MouseEvent me) {
		        // the mouse has exited the label
		    }
		});
		
	}
	
	public void setURI(URI uri) {
		this.uri = uri;
	}	
	
	public void setText(String text) {
		super.setText(" "+text+" ");
	}

}
