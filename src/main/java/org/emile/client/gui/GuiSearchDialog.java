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

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.awt.*;

import org.apache.log4j.Logger;
import org.emile.client.Common;
import org.emile.client.ServiceNames;


public class GuiSearchDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiSearchDialog.class);
	
	protected Container container;
		
	protected JComboBox<String> jcbFulltext;
	protected JLabel fulltext;
	protected JComboBox<String> jcbContentModel;
	protected JLabel contentmodel;
	protected JComboBox<String> jcbGroup;
	protected JLabel group;
	protected JSpinner jcbLimit;
	protected JCheckBox jcbHasAHandle;
	protected JLabel limit;
	protected JButton jbSubmit;
	protected JButton jbClose;
	protected JButton jbReset;
	protected JButton jbAdd;
	protected JButton jbDel;


	public GuiSearchDialog() {
		
		super("GuiSearchDialog");

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
		
		setWidgetName(jcbFulltext, "jcbFulltext");
		setWidgetName(jcbContentModel, "jcbContentModel");
		setWidgetName(jcbGroup, "jcbGroup");
		setWidgetName(jcbLimit, "jcbLimit");
		setWidgetName(jcbHasAHandle, "jcbHasAHandle");
		setWidgetName(jbSubmit, "jbSubmit");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbReset, "jbReset");
		setWidgetName(jbAdd, "jbAdd");
		setWidgetName(jbDel, "jbDel");
		
	}

	private void jbInit() throws Exception {
		
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		
		fulltext = new JLabel(res.getString("fulltext")+": ");
		fulltext.setHorizontalTextPosition(SwingConstants.LEADING);
		
		jcbFulltext = new JComboBox<String>();
		jcbFulltext.setPreferredSize(new Dimension(1000, jcbFulltext.getPreferredSize().height));
	
		contentmodel = new JLabel(res.getString("contentmodel")+": ");
		contentmodel.setHorizontalTextPosition(SwingConstants.LEADING);
		
		jcbContentModel = new JComboBox();
		jcbContentModel.setPreferredSize(new Dimension(1000, jcbContentModel.getPreferredSize().height));
	
		group = new JLabel(res.getString("group")+": ");
		group.setHorizontalTextPosition(SwingConstants.LEADING);
		
		jcbGroup = new JComboBox<String>();
		jcbGroup.setPreferredSize(new Dimension(1000, jcbGroup.getPreferredSize().height));
	
		limit = new JLabel(res.getString("limit")+": ");
		limit.setHorizontalTextPosition(SwingConstants.LEADING);
		
		Integer value = new Integer(500);
		Integer min = new Integer(0);
		Integer max = new Integer(10000000);
		Integer step = new Integer(100);
		SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);	
		jcbLimit =  new JSpinner(model);
	
		jbSubmit = new JButton(res.getString("submit"));
		jbClose = new JButton(res.getString("close"));
		jbReset = new JButton(res.getString("reset"));
		
		jbAdd = new JButton("+");
		jbAdd.setMaximumSize(new Dimension(jbAdd.getPreferredSize().width-3, jbAdd.getPreferredSize().height-3));

		jbDel = new JButton("-");
		jbDel.setMaximumSize(new Dimension(jbAdd.getPreferredSize().width-3, jbAdd.getPreferredSize().height-3));
	
		jcbHasAHandle = new JCheckBox(res.getString("hashandle"));
			
		container.add(fulltext);	
		container.add(jcbFulltext);
		Box b0 = Box.createHorizontalBox();
		b0.add(jbAdd);	
		b0.add(jbDel);
		container.add(b0, "wrap 10");
		container.add(contentmodel);
		container.add(jcbContentModel, "wrap 10");	
		container.add(group);
		container.add(jcbGroup, "wrap 10");	
		container.add(limit);
		container.add(jcbLimit, "wrap 10");	
		container.add(new JLabel(" "));
		container.add(jcbHasAHandle);	
	    container.add( new JLabel( "" ), "wrap 20" );
	    
		Box c0  = Box.createHorizontalBox();
		c0.add( jbSubmit );
		c0.add( new JLabel( " " ) );
		c0.add( jbClose );
		c0.add( new JLabel( " " ) );
		c0.add( jbReset );
		
		container.add (c0, "gapleft push, wrap 10");		
	
	}
}

