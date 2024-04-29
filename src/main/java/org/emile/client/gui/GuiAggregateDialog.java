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


public class GuiAggregateDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiAggregateDialog.class);
	
	protected Container container;
	
	protected JProgressBar jpbProgessBar;

	protected JRadioButton jrbKML;
	protected JRadioButton jrbPELAGIOS;
	protected JRadioButton jrbCMIF;
	protected JRadioButton jrbCMDI;
	protected JButton jbApply;
	protected JButton jbClose;
	protected JEditorPane jLogView;


	public GuiAggregateDialog() {
		
		super("GuiAggregateDialog");

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
		
		setWidgetName( jpbProgessBar, "jpbProgessBar" );
		setWidgetName( jrbKML, "jrbKML" );
		setWidgetName( jrbPELAGIOS, "jrbPELAGIOS" );
		setWidgetName( jrbCMIF, "jrbCMIF" );
		setWidgetName( jrbCMDI, "jrbCMDI" );
		setWidgetName( jLogView, "jLogView" );
		
		setWidgetName( jbApply, "jbApply" );
		setWidgetName( jbClose, "jbClose" );

	}

	
	private void jbInit() throws Exception {

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);	
		jpbProgessBar.setStringPainted(true);
	
		jbApply = new JButton( res.getString("apply") );
		jbClose = new JButton( res.getString("close") );

		ButtonGroup bg = new ButtonGroup();
		jrbKML = new JRadioButton(res.getString("kml"));
		jrbPELAGIOS = new JRadioButton(res.getString("pelagios"));
		jrbCMIF = new JRadioButton(res.getString("cmif"));
		jrbCMDI = new JRadioButton(res.getString("cmdi"));
		bg.add(jrbKML);
		bg.add(jrbPELAGIOS);
		bg.add(jrbCMIF);
		bg.add(jrbCMDI);
		
		jLogView = new JEditorPane();
		JScrollPane scrPane = new JScrollPane(jLogView);
	      
		jbApply = new JButton( res.getString("apply") );
		jbClose = new JButton( res.getString("close") );

		container.add(jrbKML, "wrap 5");
		container.add(jrbPELAGIOS, "wrap 5");
		container.add(jrbCMIF, "wrap 5");
		container.add(jrbCMDI, "wrap 25");
		
		container.add (new JLabel(res.getString("actlog")), "wrap 1");		
	   	container.add( scrPane, "height 100:1000:3000, growx, wrap 10");
		
		Box c0  = Box.createHorizontalBox();
		c0.add(jpbProgessBar);
		c0.add(new JLabel ("  "));
		c0.add(jbApply);
		c0.add(new JLabel (" "));
		c0.add(jbClose);
		
		container.add(c0, "gapleft push, wrap 10");

		
	}
}

