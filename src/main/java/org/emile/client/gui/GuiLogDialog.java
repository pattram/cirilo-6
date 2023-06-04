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

import org.emile.client.ServiceNames;

import java.util.ResourceBundle;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiLogDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiLogDialog.class);

	private static String[] LOGLEVEL = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
	
	protected Container container;
	protected JTable jtData;
	protected JTextArea jtaErrorMessage;
	protected JComboBox jcbLevel;
	protected JTextField jtfUsername;
	protected JButton jbClose;

	
	protected JProgressBar jpbProgessBar;
	
	public GuiLogDialog()  {
		
		super("GuiLogDialog");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e);	
		}
	}

	protected void setup() {
		
		setWidgetName(jtData, "jtData");	
		setWidgetName(jtaErrorMessage, "jtaErrorMessage");
		setWidgetName(jpbProgessBar, "jpbProgessBar");
		setWidgetName(jcbLevel, "jcbLevel");
		setWidgetName(jtfUsername, "jtfUsername");
		setWidgetName(jbClose, "jbClose");

	}

	private void jbInit() throws Exception {
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow,fill]","[grow,fill][]"));

		jtData = new JTable();
		
		jtaErrorMessage = new JTextArea();
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);
	    
		jcbLevel = new JComboBox(LOGLEVEL);
		jcbLevel.setMaximumSize(new Dimension(120, jcbLevel.getPreferredSize().height));

		jtfUsername = new JTextField();
		jtfUsername.setMaximumSize(new Dimension(120, jtfUsername.getPreferredSize().height));
	
		jbClose = new JButton( res.getString("close") );

		Box c1 = Box.createHorizontalBox();
		c1.add(jtfUsername);
		c1.add(new JLabel(" "));
		c1.add(jcbLevel);
		c1.add(new JLabel(" "));
		c1.add(jbClose);
		
		container.add(new JScrollPane(jtData), "span 11, wrap 10");
		container.add(new JScrollPane(jtaErrorMessage), "span 11, height 100:100:250, wrap 5");
		container.add(c1, "gapleft push, wrap 5");
		container.add(jpbProgessBar);
		
 	}

} 

