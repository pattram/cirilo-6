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

import javax.swing.*;

import java.util.ResourceBundle;
import java.awt.*;

import org.apache.log4j.Logger;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CHint;


public class GuiReorganizerDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiReorganizerDialog.class);
	
	protected Container container;
		
	protected JButton jbSubmit;
	protected JButton jbCancel;
	protected JProgressBar jpbProgessBar;


	public GuiReorganizerDialog() {
		
		super("GuiReorganizerDialog");

		try {
			jbInit();						
			setRootComponent(container);
			setup();
		}
		catch (Exception e) {
			log.error(e);	
		}
	}

	protected void setup() {
		
		setWidgetName(jbSubmit, "jbSubmit");
		setWidgetName(jbCancel, "jbCancel");
		setWidgetName(jpbProgessBar, "jpbProgessBar");
	}

	private void jbInit() throws Exception {

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		jbSubmit = new JButton(res.getString("submit"));
		jbCancel = new JButton(res.getString("cancel"));
		
		CHint info1 = new CHint (res.getString("reorgInfo1"));
		JLabel info2 = new JLabel(res.getString("reorgInfo2"));
	
			
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);	

		container.add(info1, "wrap 5");
		container.add(info2, "wrap 15");
		container.add( jpbProgessBar, "wrap 5" );
		
		Box c0  = Box.createHorizontalBox();
		c0.add( jbSubmit );
		c0.add( new JLabel( " " ) );
		c0.add( jbCancel );
		
		container.add (c0, "gapleft push, wrap 10");		
		
	
	}
}

