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

package org.emile.client.gui;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.emile.client.ServiceNames;

/**
 *  Description of the Class
 *
 * @author     yoda
 * @created    07. September 2006
 */
public class GuiHarvesterDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiHarvesterDialog.class);

	protected Container container;
	protected JTable jtRepositories;
	protected JLabel message;
	protected JProgressBar jpbProgessBar;
	protected JButton jbHarvest;
	protected JButton jbClose;


	/**
	 *Constructor for the GuiSelectLayoutDialog object
	 */
	public GuiHarvesterDialog() {
		super("GuiHarvesterDialog");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {

		setWidgetName(jtRepositories, "jtRepositories");
		setWidgetName(message, "message");
		setWidgetName(jpbProgessBar, "jpbProgessBar");
		setWidgetName(jbHarvest, "jbHarvest");
		setWidgetName(jbClose, "jbClose");

	}


	/**
	 *  Description of the Method
	 *
	 * @exception  Exception  Description of the Exception
	 */
	private void jbInit() throws Exception {
		
		ResourceBundle res=(ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("insets 10, fillx"));

		jtRepositories = new JTable();
		
		message = new JLabel();

		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);
		jpbProgessBar.setStringPainted(true);
		jpbProgessBar.setPreferredSize(new Dimension(350, jpbProgessBar.getPreferredSize().height));		
		
		jbHarvest = new JButton(res.getString("harvesting"));

		jbClose = new JButton(res.getString("close"));

		container.add( new JScrollPane(jtRepositories), "height 100:500:1500, growx, wrap 5" );
		container.add( message, "wrap 5");
		container.add( jpbProgessBar, "wrap 5");
		Box c0  = Box.createHorizontalBox();
		c0.add( jbHarvest );
		c0.add( new JLabel (" "));
		c0.add( jbClose );
 		container.add( c0, "gapleft push, wrap 10" );
 		
	}

}

