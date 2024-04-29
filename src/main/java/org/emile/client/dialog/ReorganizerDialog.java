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

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CSwingWorker;
import org.jdom.Document;
import org.jdom.Element;

import java.awt.event.*;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;


public class ReorganizerDialog extends CDialog {

	private static Logger log = Logger.getLogger(ReorganizerDialog.class);
	
	private int MAX_LOOP = 200;

	private FedoraConnector connector;
	private ResourceBundle res; 
	private String key; 
	private String username; 
	private JCheckBox jcbSemantic;
	private JCheckBox jcbGeospatial;
	
	public ReorganizerDialog() { }

    public void setup(String key, RepositoryDialog dlg) {
    	try {
    		res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
    		this.connector = dlg.getFedoraConnector();
    		this.username = dlg.getUsername();
    		this.key = key;
    		setTitle(res.getString("triplestoreReorg") + " on " + StringUtils.substringAfterLast(dlg.getTitle(), "▪ "));
    	} catch (Exception e) {}
  }
	
			
	public void handleCancelButton(ActionEvent e) {
		close();
	}

	public void handleSubmitButton(ActionEvent ae) {
		try {
		    if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askreorg")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
		    	CSwingWorker loader = new CSwingWorker(this, null);
		    	loader.execute();
		    }
		} catch (Exception e) {		
		}
		
 	}

	public void doIt() {

		  
		  PasswdDialog dlg = new PasswdDialog(username, key);
		  if (key.equals(dlg.getPasswd())) {
			  try {
			
				  JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");
				  jpbProgessBar.setStringPainted(true);

				  JButton jbSubmit = (JButton) getGuiComposite().getWidget("jbSubmit");
				  jbSubmit.setEnabled(false);
			
				  byte[] stream = connector.stubStartReorganizeGraphDatabases();

				  if (stream != null ) {
					  jpbProgessBar.setVisible(true);
					  int obj = 0; int err=0;
					  try {
						  for (Element s: XMLUtils.getChildren("//s:pid", XMLUtils.createDocumentFromByteArray(stream))) {
							  String pid = s.getText();
							  jpbProgessBar.setString(pid);
							  obj++;
							  int lp = 0;
							  while (connector.stubUpdateGraphDatabaseRecord(pid) != 200 && lp++ < MAX_LOOP) { Thread.sleep(2000); };
							  if (lp == MAX_LOOP) err++;
						  }
					  } catch (Exception q) {					
					  } finally {
						  try {
							  connector.stubTermimateReorganizeGraphDatabases();
						  } catch (Exception s) {};
					  }
					  jpbProgessBar.setVisible(false);
					  JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("reorgFinished"), new Integer(obj-err).toString(), new Integer(obj).toString()), getTitle(), JOptionPane.INFORMATION_MESSAGE);
				  } else {
					  jpbProgessBar.setVisible(false);
					  JOptionPane.showMessageDialog(null, res.getString("reorgIsRunning"), getTitle(), JOptionPane.WARNING_MESSAGE);
				  }
				  jbSubmit.setEnabled(true);
			
			  } catch (Exception e) {
				  try {
					  connector.stubTermimateReorganizeGraphDatabases();
				  } catch (Exception s) {};			  
			  }	
		  }
 	}	
	
	public void handlerRemoved(CEventListener aoHandler) {
	}

	protected void cleaningUp() {
	}

	
	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}

	public void show() throws CShowFailedException {
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, null, false);
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	protected void opened() throws COpenFailedException {

		try {
			
			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbCancel", "handleCancelButton");
	
		} catch (Exception e) {
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

