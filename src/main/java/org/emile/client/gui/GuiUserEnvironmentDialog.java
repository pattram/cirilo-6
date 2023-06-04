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

import java.util.ArrayList;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiUserEnvironmentDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiUserEnvironmentDialog.class);
	
	protected Container container;
	protected JTable jtData;
	protected JComboBox<String> jcbGroups;
	protected JComboBox<String> jcbContentModel;
	protected JTextField jtfTitle;	
	protected JButton jbClone;	
	protected JButton jbRefresh;	
	protected JButton jbClose;	
	protected JButton jbEdit;	
	protected JButton jbDel;	
	protected JButton jbOK;	
	protected Box jbClonePrototype;
	protected JProgressBar jpbProgessBar;
	
	public GuiUserEnvironmentDialog()  {
		
		super("GuiUserEnvironmentDialog");

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
		setWidgetName(jpbProgessBar, "jpbProgessBar");
		setWidgetName(jcbGroups, "jcbGroups");
		setWidgetName(jbClonePrototype, "jbClonePrototype");
		setWidgetName(jtfTitle, "jtfTitle");
		setWidgetName(jbClone, "jbClone");
		setWidgetName(jbEdit, "jbEdit");
		setWidgetName(jbDel, "jbDel");
		setWidgetName(jbOK, "jbOK");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbRefresh, "jbRefresh");
		setWidgetName(jcbContentModel, "jcbContentModel");
	}

	private void jbInit() throws Exception {
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

		ArrayList<String> AllObjects = new ArrayList<String>();
				
		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("fillx"));

		jtData = new JTable();
		
		jcbGroups = new JComboBox();
		jcbGroups.setPreferredSize(new Dimension(2000, jtData.getPreferredSize().height));

		jtfTitle = new JTextField();
		jtfTitle.setToolTipText(res.getString("TTprototypetitle"));
		jtfTitle.setPreferredSize(new Dimension(250, jtfTitle.getPreferredSize().height));

		jcbContentModel = new JComboBox();
		jcbContentModel.setToolTipText(res.getString("TTprototypetype"));
		jcbContentModel.setPreferredSize(new Dimension(100, jcbContentModel.getPreferredSize().height));

		jbClone = new JButton("+");
		jbClone.setToolTipText(res.getString("TTprototypeadd"));
		jbClose = new JButton(res.getString("close"));
		jbRefresh = new JButton(res.getString("refresh"));
		jbDel = new JButton("-");
		jbDel.setToolTipText(res.getString("TTprototypedel"));
		jbEdit = new JButton(res.getString("edit"));
		jbEdit.setToolTipText(res.getString("TTprototypeedit"));
		jbOK = new JButton(res.getString("create"));
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);

		Box c0 = Box.createHorizontalBox();
		c0.add(jcbGroups);
	    
		container.add(c0, "wrap 5");
		container.add(new JScrollPane(jtData), "height 100:500:1000, growx, wrap 5");
		
		Box c1 = Box.createHorizontalBox();
		c1.add(jpbProgessBar);
		c1.add(new JLabel("   "));
		jbClonePrototype = Box.createHorizontalBox();
		jbClonePrototype.add(c1);
		jbClonePrototype.add(jcbContentModel);
		jbClonePrototype.add(new JLabel(" "));
		jbClonePrototype.add(jtfTitle);
		jbClonePrototype.add(new JLabel(" "));
		jbClonePrototype.add(jbOK);
		jbClonePrototype.add(new JLabel("  "));
		Box c3 = Box.createHorizontalBox();
		c3.add(jbClone);
		c3.add(new JLabel(" "));
		c3.add(jbEdit);
		c3.add(new JLabel(" "));
		c3.add(jbDel);
		c3.add(new JLabel(" "));
		c3.add(jbRefresh);
		c3.add(new JLabel(" "));
		c3.add(jbClose);
		Box c4 = Box.createHorizontalBox();
		c4.add(c1);
		c4.add(jbClonePrototype);
		c4.add(c3);
		
		container.add(c4, "gapleft push, wrap 10");
	
		jbClonePrototype.setVisible(false);
 	}

} 

