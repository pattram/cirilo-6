/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.client.dialog.core;


import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.client.ServiceNames;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

public class CBoundSerializer {
	
	private static Logger log = Logger.getLogger(CBoundSerializer.class);

	public static void load (Container container, Object table, Dimension notused, boolean allwaysusedefault) {
		String prop = null;
	    try {
	    	CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );

	    	Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();						        
			Dimension ws = container.getParent().getSize();

	        String dialog = container.getName();
	    				
			if (dialog.contains("RepositoryDialog")) {
	    		container.setSize(new Dimension((int) ws.getWidth()-10, (int) ws.getHeight() - 45));
				container.setLocation(3, 5);
	    	} else if (dialog.equals("LoginDialog")) {
				container.setLocation((int)(screensize.getWidth()/2-200), (int)(screensize.getHeight()/2-100));	
	    		container.setSize(new Dimension(400, 200));
			} else if (props.getProperty("user", dialog+".dialog.width") != null && !allwaysusedefault) {
	    		int width = new Integer(props.getProperty("user",  dialog+".dialog.width"));
	    		int height = new Integer(props.getProperty("user",  dialog+".dialog.height"));
	    		int x = new Integer(props.getProperty("user",  dialog+".dialog.x"));
	    		int y = new Integer(props.getProperty("user",  dialog+".dialog.y"));	
	    		if (width > ws.getWidth() || height > ws.getHeight()) {
	    			width = (int) ws.getWidth()-35;
	    			height = (int) ws.getHeight() - 110;
	    		    x = 30; y = 100;
	    		}
	    		container.setSize(new Dimension(width, height));
				container.setLocation(x, y);
	    	} else {
				container.setLocation(50,100);
				container.setSize(new Dimension ((int) ws.getWidth()-60, (int) ws.getHeight()-110));
	    	}
			if (table != null) {
       		if (table instanceof JTable) {
        			JTable t = (JTable) table;
        			for (int i = 1; i < t.getColumnCount(); i++) {
           				TableColumn col = t.getColumn(t.getColumnName(i));
           				prop = props.getProperty("user", dialog+".dialog.column."+new Integer(i).toString());
           				if (prop != null) col.setPreferredWidth(new Integer(prop)); 
        			} 
        		}
        	}

	    } catch (Exception e) {
	      log.error(e);	
        }
	}
	       
	public static void save (Container container, Object table, FedoraConnector connector) {			

	    try {
	    	CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
		
	        String dialog = container.getName();
		    
	        if (connector != null) {
	        	props.setProperty("user", dialog+".repository", connector.getHostname());
	        }
	        props.setProperty("user", dialog+".dialog.width", new Integer(container.getWidth()).toString());
	        props.setProperty("user", dialog+".dialog.height", new Integer(container.getHeight()).toString());
	        props.setProperty("user", dialog+".dialog.x", new Integer(container.getX()).toString());
	        props.setProperty("user", dialog+".dialog.y", new Integer(container.getY()).toString());

	        if (table != null) {
	        	if (table instanceof JTable) {
	        		JTable t = (JTable) table;
	        		for (int i=1; i < t.getColumnCount(); i++) {
	        			TableColumn col = t.getColumn(t.getColumnName(i));
	        			props.setProperty("user", dialog+".dialog.column."+new Integer(i).toString(), new Integer(col.getWidth()).toString()); 
	        		} 
		        }
	        }
	        
	        props.saveProperties("user");	
	        	
	     } catch (Exception e) {
	       log.error(e);	
	     }
	}

}
