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
import org.emile.client.dialog.core.CHint;
import org.emile.client.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiRepositoryDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiRepositoryDialog.class);

	protected Container container;
	protected JTable jtData;
	
	protected JProgressBar jpbProgessBar;
	
	protected Dimension button_dim;

	protected JPanel jbHandleDialog;
	protected JTextField jtfPrefix;
	protected JSpinner jtfBeginningWith;
	protected JButton jbHdlCreate;
	protected JButton jbHdlRefresh;
	protected JButton jbHdlDelete;	
	protected JButton jbHdlClose;	

	protected Box jbInOutDialog;
	protected JTextField jtfInOutDir;
	protected JLabel jlInOutTitle;
	protected JLabel jlInOutDir;
	protected JButton jbSelectInOutDir;	
	protected JComboBox<String> jcbInOutGroup;
	protected JButton jbInOutSubmit;	
	protected JButton jbInOutClose;	
	
	protected Box jbSearchDialog;
	protected JComboBox<String> jcbFulltext;
	protected JLabel fulltext;
	protected JComboBox<String> jcbContentModel;
	protected JLabel contentmodel;
	protected JComboBox<String> jcbGroup;
	protected JLabel group;
	protected JButton jbSubmit;
	protected JButton jbReset;
	
	protected CHint jbHint;

	
	public GuiRepositoryDialog()  {
		
		super("GuiRepositoryDialog");

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
	
		setWidgetName(jbInOutDialog, "jbInOutDialog");
		setWidgetName(jtfInOutDir, "jtfInOutDir");
		setWidgetName(jbSelectInOutDir, "jbSelectInOutDir");
		setWidgetName(jbInOutSubmit, "jbInOutSubmit");
		setWidgetName(jbInOutClose, "jbInOutClose");
		setWidgetName(jlInOutTitle, "jlInOutTitle");
		setWidgetName(jlInOutDir, "jlInOutDir");
		setWidgetName(jcbInOutGroup, "jcbInOutGroup");

		setWidgetName(jbHandleDialog, "jbHandleDialog");
		setWidgetName(jtfPrefix, "jtfPrefix");
		setWidgetName(jtfBeginningWith, "jtfBeginningWith");
		setWidgetName(jbHdlCreate, "jbHdlCreate");
		setWidgetName(jbHdlRefresh, "jbHdlRefresh");
		setWidgetName(jbHdlDelete, "jbHdlDelete");
		setWidgetName(jbHdlClose, "jbHdlClose");

		setWidgetName(jcbFulltext, "jcbFulltext");
		setWidgetName(jcbContentModel, "jcbContentModel");
		setWidgetName(jcbGroup, "jcbGroup");
		setWidgetName(jbSubmit, "jbSubmit");
		setWidgetName(jbReset, "jbReset");

	}

	private void jbInit() throws Exception {

		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

		int button_width = (int) Math.round(new Integer(props.getProperty("user", "FontSize"))*2.7);

		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow,fill]","[grow,fill][]"));

		jtData = new JTable();
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);	
			
		fulltext = new JLabel(res.getString("fulltext")+": ");
		fulltext.setHorizontalTextPosition(SwingConstants.LEADING);
		
		jcbFulltext = new JComboBox<String>();
		jcbFulltext.setPreferredSize(new Dimension(150, jcbFulltext.getPreferredSize().height));
		jcbFulltext.setMaximumSize(new Dimension(150, jcbFulltext.getPreferredSize().height));
	
		contentmodel = new JLabel(res.getString("contentmodel")+": ");
		contentmodel.setHorizontalTextPosition(SwingConstants.LEADING);
		
		jcbContentModel = new JComboBox();
		jcbContentModel.setPreferredSize(new Dimension(100, jcbContentModel.getPreferredSize().height));
		jcbContentModel.setMaximumSize(new Dimension(100, jcbContentModel.getPreferredSize().height));
	
		group = new JLabel(res.getString("group")+": ");
		group.setHorizontalTextPosition(SwingConstants.LEADING);
		
		jcbGroup = new JComboBox<String>();
		jcbGroup.setPreferredSize(new Dimension(100, jcbGroup.getPreferredSize().height));
		jcbGroup.setMaximumSize(new Dimension(100, jcbGroup.getPreferredSize().height));
		
		jbSubmit = new JButton(res.getString("search").replaceAll(" ...",""));
		jbReset = new JButton(res.getString("reset"));
		
		button_dim = new Dimension(Math.max(button_width, 32), jcbGroup.getPreferredSize().height+2);

		jbInOutDialog =  Box.createHorizontalBox();
		jtfInOutDir = new JTextField();
		jtfInOutDir.setPreferredSize(new Dimension(350, jtfInOutDir.getPreferredSize().height));
		jtfInOutDir.setMaximumSize(new Dimension(350, jtfInOutDir.getPreferredSize().height));
		jlInOutTitle = new JLabel();
		jlInOutDir = new JLabel();
		jcbInOutGroup = new JComboBox<String>();
		jcbInOutGroup.setPreferredSize(new Dimension(100, jcbInOutGroup.getPreferredSize().height));
		jcbInOutGroup.setMaximumSize(new Dimension(100, jcbInOutGroup.getPreferredSize().height));

		
		jbSelectInOutDir = Utils.createOpenButton(button_dim);
		jbInOutSubmit = new JButton(res.getString("submit"));
		jbInOutClose = new JButton(res.getString("close"));

		jbHandleDialog =  new JPanel();
		jbHandleDialog.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		jbHandleDialog.setBorder(BorderFactory.createTitledBorder(res.getString("manage").replaceAll("[.]", "")+": "));	

		jtfPrefix = new JTextField(); 
		jtfPrefix.setPreferredSize(new Dimension(90, jtfPrefix.getPreferredSize().height));
		jtfPrefix.setMaximumSize(new Dimension(90, jtfPrefix.getPreferredSize().height));

		SpinnerNumberModel hmodel = new SpinnerNumberModel(new Integer(1), new Integer(0), new Integer(100), new Integer(5));	
		jtfBeginningWith =  new JSpinner(hmodel);
		jtfBeginningWith.setPreferredSize(new Dimension(90, jtfBeginningWith.getPreferredSize().height));
		jtfBeginningWith.setMaximumSize(new Dimension(80, jtfPrefix.getPreferredSize().height));
		
		jbSearchDialog =  Box.createHorizontalBox();
	
		jbSearchDialog.add(fulltext);	
		jbSearchDialog.add(jcbFulltext);	
		
		jbSearchDialog.add(new JLabel("  "));
		jbSearchDialog.add(contentmodel);
		jbSearchDialog.add(jcbContentModel);	
		
		jbSearchDialog.add(new JLabel("  "));
		jbSearchDialog.add(group);
		jbSearchDialog.add(jcbGroup);	

		jbSearchDialog.add(new JLabel(" "));
		jbSearchDialog.add( jbSubmit );
		jbSearchDialog.add( new JLabel( " " ) );
		jbSearchDialog.add( jbReset );
		
		jbHdlCreate = new JButton(res.getString("submit"));
		jbHdlRefresh = new JButton(res.getString("refresh"));
		jbHdlDelete  = new JButton(res.getString("delete"));
		jbHdlClose  = new JButton(res.getString("close"));
			
		jbHint = new CHint(res.getString("HTassignhdl"));

		jbInOutDialog.add(jlInOutTitle);
	    jbInOutDialog.add(new JLabel(" | "));
		jbInOutDialog.add(jlInOutDir);
	    jbInOutDialog.add(new JLabel(": "));
	    jbInOutDialog.add(jtfInOutDir);
	    jbInOutDialog.add(new JLabel(" "));
	    jbInOutDialog.add(jbSelectInOutDir);
	    jbInOutDialog.add(new JLabel(" "));
	    jbInOutDialog.add(jcbInOutGroup);
	    jbInOutDialog.add(new JLabel("  "));
	    jbInOutDialog.add(jbInOutSubmit);
	    jbInOutDialog.add(new JLabel("  "));
	    jbInOutDialog.add(jbInOutClose);	    
	    jbInOutDialog.setVisible(false);
		
	    Box hdl = Box.createHorizontalBox();
	    hdl.add(new JLabel(res.getString("hdlprefix")+": "));
	    hdl.add(jtfPrefix);
	    hdl.add(new JLabel("  "+res.getString("numberconsecutively")+": "));
	    hdl.add(jtfBeginningWith);
	    hdl.add(new JLabel(" "));
	    hdl.add(jbHdlCreate);
	    hdl.add(new JLabel(" "));
	    hdl.add(jbHdlRefresh);
	    hdl.add(new JLabel(" "));
	    hdl.add(jbHdlDelete);
	    hdl.add(new JLabel("  "));
	    hdl.add(jbHdlClose);
	    
	    jbHandleDialog.add(hdl, "wrap 5");
	    jbHandleDialog.add(jbHint);
	    jbHandleDialog.setVisible(false);
	    
		container.add(new JScrollPane(jtData),"span 11, wrap 10");
		
		Box b0 =  Box.createHorizontalBox();
		b0.add(jbSearchDialog);
	
		Box b1 =  Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(jbHandleDialog);
		
		Box b2 =  Box.createHorizontalBox();
		b2.add(jbInOutDialog);
		
		container.add(b0,"wrap 10");
		container.add(b1,"wrap 10");
		container.add(b2,"wrap 10");
		container.add(jpbProgessBar,"wrap 10");
	
 	}

} 

