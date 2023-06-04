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

public class GuiReviewDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiReviewDialog.class);
	
	protected Container container;
	protected JTextField jtfSource;
	protected JButton jbChooseSource;
	protected JButton jbSubmit;	
	protected JButton jbClose;	
	protected JButton jbCopy;	
	protected JProgressBar jpbProgessBar;
	protected JTextArea jtaReview;

	public GuiReviewDialog()  {
		
		super("GuiReviewDialog");

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
		setWidgetName(jtfSource, "jtfSource");
		setWidgetName(jbChooseSource, "jbChooseSource");
		setWidgetName(jbClose, "jbClose");
		setWidgetName(jbCopy, "jbCopy");
		setWidgetName(jbSubmit, "jbSubmit");
		setWidgetName(jtaReview, "jtaReview");
	}

	private void jbInit() throws Exception {
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

		container = new Container(); 
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow, fill]"));
				
		jtfSource = new JTextField();
		jtfSource.setEditable(false);
	
		int button_width = (int) Math.round(new Integer(props.getProperty("user", "FontSize"))*2.7);
		Dimension button_dim = new Dimension(Math.max(button_width, 32), jtfSource.getPreferredSize().height+2);
		jtfSource.setPreferredSize(new Dimension(1000, jtfSource.getPreferredSize().height));
		jbChooseSource = Utils.createOpenButton(button_dim);

		jbClose = new JButton(res.getString("close"));
		jbSubmit = new JButton(res.getString("submit"));
		jbCopy = new JButton(res.getString("copy"));
		jbSubmit.setEnabled(false);
		jbCopy.setEnabled(false);
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);

		jtaReview = new JTextArea();
	
		JPanel source = new JPanel();
		source.setPreferredSize(new Dimension(1000, source.getPreferredSize().height));
		source.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		source.setBorder(BorderFactory.createTitledBorder(res.getString("localdir")+": "));	
		Box c0 = Box.createHorizontalBox();
		c0.add(jtfSource);
		c0.add(jbChooseSource);
		source.add(c0);
		
		container.add(source, "wrap 5");
		container.add(new JScrollPane(jtaReview), "span 11, height 300:300:550, wrap 5");

		
		Box c1 = Box.createHorizontalBox();
		c1.add(jpbProgessBar);
		c1.add(new JLabel("    "));
		c1.add(jbSubmit);
		c1.add(new JLabel(" "));
		c1.add(jbCopy);
		c1.add(new JLabel(" "));
		c1.add(jbClose);

		container.add(c1, "gapleft push, wrap 10");


 	}

} 

