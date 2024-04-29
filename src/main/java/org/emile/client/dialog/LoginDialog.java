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

package org.emile.client.dialog;

import org.emile.client.Common;
import org.emile.cirilo.exceptions.FedoraConnectionException;
import org.emile.cirilo.exceptions.KeycloakServerNotFoundException;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.swing.*;

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceName;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;


public class LoginDialog extends CDialog {

	private static Logger log = Logger.getLogger(LoginDialog.class);
    
	private IGuiAdapter moGA;
	private ResourceBundle res; 
	private CPropertyService props;
	private JComboBox<String> jcbRepositories;
	private CServiceName sn;
	private String key;

	public LoginDialog() { }

	public CServiceName getServiceName() {
		return sn;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);
		try {
			if (((JTextField)getGuiComposite().getWidget("jtfUser")).getText().isEmpty() || 
				((JPasswordField)getGuiComposite().getWidget("jpfPasswd")).getPassword().length < 3) {
				getGuiComposite().getWidget("jbSubmit").setEnabled(false);
			} else {
				getGuiComposite().getWidget("jbSubmit").setEnabled(true);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
			
	public void handleCancelButton(ActionEvent e) {
		close();
	}

	public void handleSubmitButton(ActionEvent ae) {
		
		try {
			FedoraConnector connector = new FedoraConnector();
			
			try {		
				FedoraConnector.stubGetAuthorizationServer("https", (String) jcbRepositories.getSelectedItem());
			} catch (KeycloakServerNotFoundException e) {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("nokeycloak"), (String)jcbRepositories.getSelectedItem()), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
				return;
			} catch (FedoraConnectionException e) {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noconnection"), (String)jcbRepositories.getSelectedItem()), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			connector.stubOpenConnection("https", (String) jcbRepositories.getSelectedItem(), 
						((JTextField)getGuiComposite().getWidget("jtfUser")).getText(), 
						String.valueOf(((JPasswordField)getGuiComposite().getWidget("jpfPasswd")).getPassword())); 
				
			key = String.valueOf(((JPasswordField)getGuiComposite().getWidget("jpfPasswd")).getPassword());
			
			props.setProperty("user", "Default.Source",  (String) jcbRepositories.getSelectedItem());
			props.setProperty("user", "Default.Account", ((JTextField)getGuiComposite().getWidget("jtfUser")).getText());
			
			props.saveProperties("user");
						
			sn =  new CServiceName(UUID.randomUUID().toString());
			CServiceProvider.addService(connector, sn);

			close();
			
			
		} catch (Exception e) {		
			JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("invalidcredentials"), (String)jcbRepositories.getSelectedItem()), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
		}
		
 	}

	public void handlerRemoved(CEventListener aoHandler) {
	}

	protected void cleaningUp() {
	}

	
	public void show() throws CShowFailedException {
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, null, false);
			
			Point pt = jcbRepositories.getLocation();
			jcbRepositories.setLocation((int)pt.getX()+30, (int)pt.getY()+20);

			this.getCoreDialog().setVisible(true);
			setDirty(false);
			sn = null;
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	protected void opened() throws COpenFailedException {

		try {
			
			moGA = (IGuiAdapter) getGuiAdapter();
	
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			jcbRepositories = (JComboBox<String>)getGuiComposite().getWidget("jcbRepositories");
			
			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");

			try {
						
				String repositories = props.getProperty("user", "Fedora.Repositories");
				if (repositories != null) {
					for (String s: repositories.split("[;,]")) {
						jcbRepositories.addItem(s.trim());
					}	
				}
				
				String username = props.getProperty("user", "Default.Account");
				if (username != null && !username.isEmpty()) {
					((JTextField)getGuiComposite().getWidget("jtfUser")).setText(username);
					moGA.requestFocus("jpfPasswd");
				} else {
					moGA.requestFocus("jtfUser");
				}
				
				String hostname = props.getProperty("user", "Default.Source");
				if (hostname != null && !hostname.isEmpty()) {
					jcbRepositories.setSelectedItem(hostname);
				} 
					
			} catch (Exception e) {
				log.error(e);
			}
							
			setDirty(false);
			this.getCoreDialog().setVisible(true);
			
			JPasswordField pf = (JPasswordField)getGuiComposite().getWidget("jpfPasswd");
			pf.addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent ev) {
						if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
							ev.consume();
							handleSubmitButton(null);
						}
					}
				});
			
		} catch (Exception e) {
			log.error(e);
			throw new COpenFailedException(e);
		}
	}
	
	public void close() {
		try {	
			CBoundSerializer.save(this.getCoreDialog(), null, null);
			super.close();
		} catch (Exception e) {		
			log.error(e);
		}
	}


}

