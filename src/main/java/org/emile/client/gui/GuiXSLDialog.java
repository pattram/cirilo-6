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

import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.utils.Utils;

import java.util.ArrayList;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiXSLDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiXSLDialog.class);

	protected Container container;
	protected JTable jtData;
		
	protected JButton jbApply;	
	protected JButton jbClose;	
	protected JButton jbAdd;	
	protected JButton jbDel;	
	protected JButton jbOK;	
	protected JButton jbLoadStylesheet;	
	protected JTextField jtfLocation;	
	protected Box jbManage; 
	protected JComboBox<String> jcbGroups; 
	protected JComboBox<String> jcbModel; 
	protected JComboBox<String> jcbCategory; 
	protected JTextField jtfTitle;
	protected JProgressBar jpbProgessBar;
	
	public GuiXSLDialog()  {
		
		super("GuiXSLDialog");

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
		setWidgetName(jbManage, "jbManage");
		setWidgetName(jcbGroups, "jcbGroups");
		setWidgetName(jcbModel, "jcbModel");
		setWidgetName(jcbCategory, "jcbCategory");
		setWidgetName(jbLoadStylesheet, "jbLoadStylesheet");
		setWidgetName(jpbProgessBar, "jpbProgessBar");
		
		setWidgetName(jtfTitle, "jtfTitle");
		setWidgetName(jtfLocation, "jtfLocation");
		
		setWidgetName(jbApply, "jbApply");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbAdd, "jbAdd");
		setWidgetName(jbDel, "jbDel");
		setWidgetName(jbOK, "jbOK");
	
	}

	private void jbInit() throws Exception {
				
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

		int button_width = (int) Math.round(new Integer(props.getProperty("user", "FontSize"))*2.7);

		jcbCategory = new JComboBox();
		jcbModel = new JComboBox();
		
		jcbGroups = new JComboBox();
		jcbGroups.setPreferredSize(new Dimension(2000, jcbGroups.getPreferredSize().height));
		
		jtfTitle = new JTextField();
		jtfTitle.setText(res.getString("defaultpipeline"));
		jtfTitle.setPreferredSize(new Dimension(150, jtfTitle.getPreferredSize().height));

		jbLoadStylesheet = Utils.createOpenButton(new Dimension(Math.max(button_width,32), jcbCategory.getPreferredSize().height+4));
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);

		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("fillx"));

		jtData = new JTable();
			
		jbApply = new JButton(res.getString("apply"));
		jbApply.setToolTipText(res.getString("TTpipelineapply"));
		jbClose = new JButton(res.getString("close"));
		jbAdd = new JButton("+");
		jbAdd.setToolTipText(res.getString("TTpipelinenew"));
		jbDel = new JButton("-");
		jbDel.setToolTipText(res.getString("TTpipelinedel"));
		jbOK = new JButton(res.getString("save"));
		
		jtfLocation = new JTextField();
		jtfLocation.setPreferredSize(new Dimension(400, jtfLocation.getPreferredSize().height));
		jtfLocation.setText(Common.THIS+"/");
			
		Box c0 = Box.createHorizontalBox();
		c0.add(jcbGroups);
	    
		container.add(c0, "wrap 5");
		container.add(new JScrollPane(jtData), "height 100:500:1000, growx, wrap 5");

		Box b0  = Box.createHorizontalBox();
		b0.add( jbApply );
		b0.add( new JLabel( " " ) );
		b0.add( jbAdd );
		b0.add( new JLabel( " " ) );
		b0.add( jbDel );
		b0.add( new JLabel( "  " ) );
		b0.add( jbClose );
		
		Box c1 = Box.createHorizontalBox();
		c1.add(jpbProgessBar);
		c1.add(new JLabel("   "));
		
		jbManage = Box.createHorizontalBox();
		jbManage.add( jcbModel );
		jbManage.add( new JLabel( " " ) );
		jbManage.add(jcbCategory);
		jbManage.add( new JLabel( " " ) );
		jbManage.add( jtfTitle);	
		jbManage.add( new JLabel( " " ) );
		jbManage.add( jtfLocation);	
		jbManage.add( new JLabel(" "));		
		jbManage.add(jbLoadStylesheet);
		jbManage.add( new JLabel(" "));		
		jbManage.add(jbOK);
		jbManage.add( new JLabel("  "));
		jbManage.setVisible(false);
		
		Box c4 = Box.createHorizontalBox();
		c4.add(c1);
		c4.add(jbManage);
		c4.add(b0);
		
		container.add(c4, "gapleft push, wrap 10");

		
 	}

} 

