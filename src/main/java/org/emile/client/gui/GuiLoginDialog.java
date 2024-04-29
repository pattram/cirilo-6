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


public class GuiLoginDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiLoginDialog.class);
	
	protected Container container;
		
	protected JComboBox<String> jcbRepositories;
	protected JLabel repository;
	protected JTextField jtfUser;
	protected JLabel user;
	protected JPasswordField jpfPasswd;
	protected JLabel passwd;
	protected JButton jbSubmit;
	protected JButton jbCancel;


	public GuiLoginDialog() {
		
		super("GuiLoginDialog");

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
		
		jbSubmit.setDefaultCapable(true);
		jbCancel.setDefaultCapable(false);

		setWidgetName(jcbRepositories, "jcbRepositories");
		setWidgetName(jtfUser, "jtfUser");
		setWidgetName(jpfPasswd, "jpfPasswd");
		setWidgetName(jbSubmit, "jbSubmit");
		setWidgetName(jbCancel, "jbCancel");
	}

	private void jbInit() throws Exception {

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		repository = new JLabel(res.getString("source")+": ");	
		jcbRepositories = new JComboBox<String>();
		jcbRepositories.setPreferredSize(new Dimension(300, jcbRepositories.getPreferredSize().height));
	
		user = new JLabel(res.getString("username")+": ");		
		jtfUser = new JTextField();
		jtfUser.setPreferredSize(new Dimension(300, jtfUser.getPreferredSize().height));

		passwd = new JLabel(res.getString("passwd")+": ");
		jpfPasswd = new JPasswordField();
		jpfPasswd.setPreferredSize(new Dimension(300, jpfPasswd.getPreferredSize().height));

		jbSubmit = new JButton(res.getString("submit"));
		jbCancel = new JButton(res.getString("cancel"));
		
		container.add(user);
		container.add(jtfUser, "grow, wrap 5 ");								
		container.add(passwd);
		container.add(jpfPasswd , "grow, wrap");	
		container.add(repository);
		container.add(jcbRepositories, "grow, wrap 20");	
	
		Box c0  = Box.createHorizontalBox();
		c0.add( jbSubmit );
		c0.add( new JLabel( " " ) );
		c0.add( jbCancel );
		
		container.add (new JLabel(" "));	
		container.add (c0, "gapleft push");		
	
	}
}

