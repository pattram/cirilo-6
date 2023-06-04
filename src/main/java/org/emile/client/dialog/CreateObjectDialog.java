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

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceName;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CItemListener;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;
import voodoosoft.jroots.dialog.CDialog;
import voodoosoft.jroots.dialog.CDialogTools;
import voodoosoft.jroots.dialog.COpenFailedException;
import voodoosoft.jroots.dialog.CShowFailedException;

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.business.UOPFactory;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CHint;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.PrototypeListEntry;
import org.emile.cm4f.models.UploadOptions;
import org.jdom.Element;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class CreateObjectDialog extends CDialog {

	private static Logger log = Logger.getLogger(CreateObjectDialog.class);

	private ResourceBundle res; 
	private CPropertyService props;
	private FedoraConnector connector;
	private RepositoryDialog parent;
	private String title;
	private boolean isAdmin;
	
	private UploadOptions UOP;	
	private boolean dcmimode;

	private String pid;
	private String group;
	private String cm;
		
	public CreateObjectDialog() {}

	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}
		    
	public void handleResetButton(ActionEvent ae) {
		try {
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askdeldc")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
				for (String dc : Common.DCMI) {
					JTextField tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));
					if (!tf.getText().startsWith("#"))tf.setText("");
					props.setProperty("user", "DC." + dc, "");
				}			
				props.saveProperties("user");
			}
		} catch (Exception e) {}
	}

	public void handleApplyButton(ActionEvent ae) {
		
		CSwingWorker loader = new CSwingWorker(this, null);
		loader.execute();

	}
	
	public void handleConfigureButton(ActionEvent ae) {
		try {	

			PreferencesDialog dlg = (PreferencesDialog) CServiceProvider.getService(DialogNames.PREFERENCES_DIALOG);
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			dlg.setup(this, connector.getHostname(), StringUtils.substringBefore((String) jcbContentModel.getSelectedItem(), " "));
			dlg.open();
			
			enableDC();
			
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	private boolean enableDC() {

		
		try {

			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			String model = StringUtils.substringBefore((String)jcbContentModel.getSelectedItem(), " ");
			
			UOPFactory uop = new UOPFactory(model);
			UOP = uop.getUOP();
			
			JButton jbConfigure = (JButton) getGuiComposite().getWidget("jbConfigure");		
			jbConfigure.setEnabled(parent.getPipelineStore().getParameters("IngestibleModels").contains(model));
			
			dcmimode =  !jbConfigure.isEnabled() || !UOP.isDCify();

			CHint jbHint = (CHint) getGuiComposite().getWidget("jbHint");
			jbHint.setText(res.getString("HTuncheckdc"));
			jbHint.setVisible(!dcmimode);
			
			for (String dc : Common.DCMI) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));   		
	    		if (!dc.equals("Identifier")) {
     				if (dcmimode) { 
			    		tf.setBackground(Color.white);
	    			} else {
	    				tf.setBackground(new Color(239, 239, 239));
	    			}
	    			tf.setEnabled(dcmimode);
	    		}
	    	}
		} catch (Exception e) {	
		}
		
		return dcmimode;
		
	}
	
	public void doit() {
		try {
			
			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
			JComboBox jcbNamespace = ((JComboBox) getGuiComposite().getWidget("jcbNamespace"));
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));		
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
      		JProgressBar pb = ((JProgressBar) getGuiComposite().getWidget("jpbProgessBar"));	

			pid = jcbNamespace.getSelectedItem()+ jtfPID.getText();
			group = (String)jcbGroup.getSelectedItem();
			cm = (String)jcbContentModel.getSelectedItem();
			JCheckBox jcbAllowEditingPIDField = ((JCheckBox) getGuiComposite().getWidget("jcbAllowEditingPIDField"));
			if (!jcbAllowEditingPIDField.isSelected()) pid = connector.stubGetPID(group, null);
			
			if (Common.VALID_PID_PATTERN.matcher(pid).find()) {
				if (connector.stubExist(pid, null) == 404) {
					if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcreateobject"), pid, cm), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
						pb.setVisible(true);
						pb.setStringPainted(true);
						pb.setString(pid);
						if (doClone() == 200) {
							
							connector.waitUntilObjectIsAvailable(pid);

							JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("objcreated"), pid, cm, group), getTitle(), JOptionPane.INFORMATION_MESSAGE);
						
							String[] query = Common.QUERY;
							query[0] = pid;
							query[1] = null;
							query[2] = group;
							query[3] = "500";
							query[4] = "false";
							
							parent.handleSearch(query);
							pb.setVisible(false);
						} else {
							pb.setVisible(false);
							JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("useentombpiderr"), pid), getTitle(), JOptionPane.ERROR_MESSAGE);									
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("objexists"), pid), getTitle(), JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("invalidpid"), pid, Common.VALID_PID_REGEX), getTitle(), JOptionPane.ERROR_MESSAGE);			
			}
		
		} catch (Exception e) {e.printStackTrace();}	
	}
	
	private int doClone() {	
		int retval = -1;
		try {
			retval = connector.stubCloneObject(StringUtils.substringAfter(cm, "| "), pid, group);
			if (dcmimode) connector.stubModifyDatastream(pid, "DC",  Utils.getDcmi(pid, this), "text/xml", null);			
		} catch (Exception e) {
			log.error(e);
		}
		return retval;
	}

	public void handleAllowEditingPIDField(ActionEvent ae) {
		try {
			JComboBox jcbNamespace = ((JComboBox) getGuiComposite().getWidget("jcbNamespace"));
			JComboBox jcbContentModel = ((JComboBox) getGuiComposite().getWidget("jcbContentModel"));		
			JCheckBox jcbAllowEditingPIDField = ((JCheckBox) getGuiComposite().getWidget("jcbAllowEditingPIDField"));
			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
			JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));
			
			jcbNamespace.setEnabled(false);
		
            String cm = jcbContentModel.getSelectedItem().toString().toLowerCase();
			if (cm.startsWith("context")) {
				jcbNamespace.setSelectedIndex(1);
				enableTextField(true, jtfPID);
				jcbAllowEditingPIDField.setSelected(true);
			} else if (cm.startsWith("corpus")) {
				jcbNamespace.setSelectedIndex(2);
				enableTextField(true, jtfPID);
				jcbAllowEditingPIDField.setSelected(true);
			} else if (cm.startsWith("query")) {
				jcbNamespace.setSelectedIndex(3);
				enableTextField(true, jtfPID);
				jcbAllowEditingPIDField.setSelected(true);
			} else {
				jcbNamespace.setSelectedIndex(0);	
				jcbAllowEditingPIDField.setSelected(ae != null && isAdmin && !jtfPID.isEnabled());
				enableTextField(ae != null && isAdmin && jcbAllowEditingPIDField.isSelected() , jtfPID);
				if (!isAdmin) jtfTitle.requestFocus();
			}
			
		} catch (Exception e) {}
	}
	
	private void enableTextField(boolean mode, JTextField jtf) {
		try {
			jtf.setBackground( mode ? Color.YELLOW : new Color (238,238,238) );
			jtf.setEnabled(mode);
			jtf.requestFocus(mode);
			jtf.setText("");
		} catch (Exception e) {		
			log.error(e);
		}
	}
		
	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			handleAllowEditingPIDField(null);
			enableDC();
		}
	}
	
	public void handleGroupItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			fetchPrototypes();
		}
	}

	public void setup(String title, RepositoryDialog parent, FedoraConnector connector) {
		try {
			this.title = StringUtils.substringBefore(title, ":");
			this.parent = parent;
			this.connector = connector;
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	public void show() throws CShowFailedException {
		try {			
			setTitle(title);
			CBoundSerializer.load(this.getCoreDialog(), null, this.getCoreDialog().getSize(), false);
			
			for (String dc : Common.DCMI) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));
				String s = props.getProperty("user", "DC." + dc);
				if (s != null) tf.setText(s);
			}	
			enableDC();
			
		} catch (Exception e) {
		}
	}
		
	protected void opened() throws COpenFailedException {
		String s;
		
		try {
					    
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			
			isAdmin = 	connector.isSysop() || connector.isAdmin();
			
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");			
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbConfigure", "handleConfigureButton");			
			CDialogTools.createButtonListener(this, "jbReset", "handleResetButton");
			CDialogTools.createButtonListener(this, "jcbAllowEditingPIDField", "handleAllowEditingPIDField");
					
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 

			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if (!el.getText().isEmpty()) jcbGroup.addItem(el.getText());
			}
            s =  props.getProperty("user", "General.DefaultGroup");
            if (s != null && !s.isEmpty()) jcbGroup.setSelectedItem(s); else jcbGroup.setSelectedIndex(0);
    		new CItemListener((JComboBox) getGuiComposite().getWidget("jcbGroup"), this, "handleGroupItemListener");

            fetchPrototypes();
            
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));			
  	        s =  props.getProperty("user", "General.DefaultContentModel");
            if (s != null) jcbContentModel.setSelectedItem(s); 
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbContentModel"), this, "handleItemListener");
 
                   
			JCheckBox jcbAllowEditingPIDField = ((JCheckBox) getGuiComposite().getWidget("jcbAllowEditingPIDField"));
			jcbAllowEditingPIDField.setEnabled(isAdmin);

			JTextField jtfPID = ((JTextField) getGuiComposite().getWidget("jtfPID"));
    		jtfPID.setEnabled(jcbAllowEditingPIDField.isEnabled());
    		
			JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));
			jtfTitle.requestFocus(true);
			
    		handleAllowEditingPIDField(null);
    		
		} catch (Exception e) {
			log.error(e);	
		}
	}
	
	
	public void handlerRemoved(CEventListener aoHandler) {}

	protected void cleaningUp() {}

	public void close() {
		try {	
			CBoundSerializer.save(this.getCoreDialog(), null, null);
			
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			props.setProperty("user", "General.DefaultContentModel", (String)jcbContentModel.getSelectedItem());
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			props.setProperty("user", "General.DefaultGroup", (String)jcbGroup.getSelectedItem());

			for (String dc : Common.DCMI) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));
				props.setProperty("user", "DC." + dc, tf.getText());
			}	
						
			props.saveProperties("user");
			super.close();
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	protected boolean closing() {
		try {
		} catch (Exception e) {		
			log.error(e);
		}
		return true;
	}
	
	private void fetchPrototypes() {
		try {
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			jcbContentModel.removeAllItems();
			
			for(PrototypeListEntry pt: connector.stubGetPrototypeList((String)jcbGroup.getSelectedItem(), false)) {
				if (Pattern.matches("^o:prototype.g_[a-z0-9.]*", pt.getPid())) {
					jcbContentModel.addItem(StringUtils.substringAfterLast(pt.getModel(), "/") + " - " + pt.getTitle() + " | "+pt.getPid());	
				}							
			}
			if (jcbContentModel.getItemCount() == 0) jcbContentModel.addItem ("TEI | o:prototype.tei");
		} catch (Exception e) {		
			log.error(e);
		}
		
	}
	
}

