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
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiPublishGroupDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiPublishGroupDialog.class);
	
	protected Container container;
	protected JComboBox<String> jcbGroups;
	protected JComboBox<String> jcbTargetRepository;
	protected JButton jbApply;	
	protected JButton jbClose;	
	protected JProgressBar jpbProgessBar;
	protected JCheckBox jcbOnlyAdd;
	protected JCheckBox jcbCpResDir;
	protected JCheckBox jcbCpPrototypes;
	protected CHint jbHint0;
	protected CHint jbHint1;
	protected JEditorPane jLogView;

	public GuiPublishGroupDialog()  {
		
		super("GuiPublishGroupDialog");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e);	
		}
	}

	protected void setup() {
		
		setWidgetName(jpbProgessBar, "jpbProgessBar");
		setWidgetName(jLogView, "jLogView");
		setWidgetName(jcbGroups, "jcbGroups");
		setWidgetName(jcbOnlyAdd, "jcbOnlyAdd");
		setWidgetName(jcbCpResDir, "jcbCpResDir");
		setWidgetName(jcbCpPrototypes, "jcbCpPrototypes");
		setWidgetName(jcbTargetRepository, "jcbTargetRepository");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbApply, "jbApply");
	}

	private void jbInit() throws Exception {
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		
		jcbGroups = new JComboBox<String>();
		jcbGroups.setPreferredSize(new Dimension(1000, jcbGroups.getPreferredSize().height));
		
		jcbTargetRepository = new JComboBox<String>();
		jcbTargetRepository.setPreferredSize(new Dimension(1000, jcbTargetRepository.getPreferredSize().height));
		
		jLogView = new JEditorPane();
		JScrollPane scrPane = new JScrollPane(jLogView);
		
		JPanel log = new JPanel();
		log.setPreferredSize(new Dimension(1000, log.getPreferredSize().height));
		log.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		log.setBorder(BorderFactory.createTitledBorder(res.getString("actlog")));	
		log.add( scrPane, "height 250:400:600, growx, wrap 10");

		jbClose = new JButton(res.getString("close"));
		jbApply = new JButton(res.getString("apply"));
		jbApply.setEnabled(false);
		
		jbHint0 = new CHint();
		jbHint0.setText(res.getString("HTupdatetarget"));
		jbHint1 = new CHint();
		jbHint1.setText(res.getString("HTonlyadded"));
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);

		jcbOnlyAdd = new JCheckBox();
		jcbOnlyAdd.setText(res.getString("updateexistingobj"));
				
		jcbCpResDir = new JCheckBox();
		jcbCpResDir.setText(res.getString("cpresdir"));
		
		jcbCpPrototypes = new JCheckBox();
		jcbCpPrototypes.setText(res.getString("cpprototypes"));
				
		
		
		JPanel group = new JPanel();
		group.setPreferredSize(new Dimension(1000, group.getPreferredSize().height));
		group.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		group.setBorder(BorderFactory.createTitledBorder(res.getString("group")+": "));	
		group.add(jcbGroups);
		container.add(group, "wrap 5");
	
		JPanel target = new JPanel();
		target.setPreferredSize(new Dimension(1000, jcbTargetRepository.getPreferredSize().height));
		target.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		target.setBorder(BorderFactory.createTitledBorder(res.getString("target")+": "));	
		target.add(jcbTargetRepository);
		container.add(target, "wrap 5");
		
		container.add(log, "wrap 10");
		
		Box c1 = Box.createHorizontalBox();
		c1.add(jpbProgessBar);
		c1.add(new JLabel(" "));
		c1.add(jbApply);
		c1.add(new JLabel(" "));
		c1.add(jbClose);

		container.add(jcbOnlyAdd, "wrap 10");
		container.add(jcbCpResDir, "wrap 5");
		container.add(jcbCpPrototypes, "wrap 5");

		container.add(c1, "gapleft push, wrap 10");
		
		container.add(jbHint0, "wrap 5");
		container.add(jbHint1, "wrap 15");

 	}

	
} 

