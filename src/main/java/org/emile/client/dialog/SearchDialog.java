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
import org.emile.cm4f.models.PrototypeListEntry;
import org.jdom.Element;

import java.awt.event.*;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;


public class SearchDialog extends CDialog {

	private static Logger log = Logger.getLogger(SearchDialog.class);
  	
	private IGuiAdapter moGA;
	private ResourceBundle res; 
	private CPropertyService props;
	private RepositoryDialog parent;
	private FedoraConnector connector;
	private String title;
	private String prefix;

	public SearchDialog() { }
	
	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);
	}
	
	public void setup(String title, RepositoryDialog parent) {
		this.title = title;
		this.prefix = parent.getPrefix();
		this.parent = parent;
		this.connector = parent.getFedoraConnector();
	}
	
	public void handleSubmitButton(ActionEvent ae) {
		
		String[] query = Common.QUERY;
		
		try {			
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			JSpinner jcbLimit = (JSpinner) getGuiComposite().getWidget("jcbLimit");		
		    JCheckBox jcbHasAHandle = ((JCheckBox) getGuiComposite().getWidget("jcbHasAHandle"));	
				
			addItem();
			
			query[0] = jcbFulltext.getSelectedIndex() > 0 ? (String)jcbFulltext.getSelectedItem() : null;
			query[1] = jcbContentModel.getSelectedIndex() > 0 ? (String)jcbContentModel.getSelectedItem() : null;
			query[2] = jcbGroup.getSelectedIndex() > 0 ? (String)jcbGroup.getSelectedItem() : null;	
			query[3] = jcbLimit.getValue().toString();
			query[4] = jcbHasAHandle.isSelected() ? "true" : "false";
		
			parent.handleSearch(query);
			
			close(true);
			
		} catch (Exception e) {
			log.error(e);
		}		
 	}
	
	public void handleResetButton(ActionEvent ae) {
		try {			
			props.setProperty("user", prefix+".Search.ContentModel", "");
			props.setProperty("user", prefix+".Search.Group", "");
			props.setProperty("user", prefix+".Search.Limit", "500");
			props.setProperty("user", prefix+".Search.HasHandle", "false");
			props.saveProperties("user");
			
			resetDialogComboBoxes(false);
				
		} catch (Exception e) {
			log.error(e);
		}		
 	}
	
	public void handleAddItem(ActionEvent ae) {
		addItem();
 	}
	
	public void handleDelItem(ActionEvent ae) {
		try {	
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));
			if (jcbFulltext.getSelectedIndex() > 0) jcbFulltext.removeItem(jcbFulltext.getSelectedItem());
		} catch (Exception e) {
			log.error(e);
		}		
 	}

	
	public void handleCloseButton(ActionEvent ae) {
		try {	
			close(true);
		} catch (Exception e) {
			log.error(e);
		}		
 	}

	public void handlerRemoved(CEventListener aoHandler) {
	}

	protected void cleaningUp() {
	}

	
	public void show() throws CShowFailedException {
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, parent.getCoreDialog().getSize(), false);
			this.getCoreDialog().setVisible(true);
			setTitle(title);
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	protected void opened() throws COpenFailedException {

		try {
			
			moGA = (IGuiAdapter) getGuiAdapter();
					
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbReset", "handleResetButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbAdd", "handleAddItem");
			CDialogTools.createButtonListener(this, "jbDel", "handleDelItem");
						
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			       close(false);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 

			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			jcbContentModel.addItem(res.getString("allmodels"));
			for (PrototypeListEntry pt: connector.stubGetPrototypeList(null, true)) {
				jcbContentModel.addItem(StringUtils.substringAfterLast(pt.getModel(), "/"));
 			}

			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			jcbFulltext.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (jcbFulltext.getSelectedIndex() == 0 ) {
						 JTextComponent editor = (JTextComponent) jcbFulltext.getEditor().getEditorComponent();
						 editor.setText("");
					 }
				 }
			});			

		    resetDialogComboBoxes(true);
									
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void close(boolean mode) {
		try {				
			CBoundSerializer.save(this.getCoreDialog(), null, null);

			if (mode) {
				JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
				JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
				JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
				JSpinner jcbLimit = ((JSpinner) getGuiComposite().getWidget("jcbLimit"));
				JCheckBox jcbHasAHandle = ((JCheckBox) getGuiComposite().getWidget("jcbHasAHandle"));
			
				String ft = new String();
				for (int i = 1; i <jcbFulltext.getItemCount(); i++) {
					ft += (i == jcbFulltext.getSelectedIndex() ? "#" : "")+ (String)jcbFulltext.getItemAt(i).trim() + "!";  
				}
			
				props.setProperty("user", prefix+".Search.Fulltext", ft);
				props.setProperty("user", prefix+".Search.ContentModel", (String)jcbContentModel.getSelectedItem());
				props.setProperty("user", prefix+".Search.Group", (String)jcbGroup.getSelectedItem());
				props.setProperty("user", prefix+".Search.Limit", jcbLimit.getValue().toString());
				props.setProperty("user", prefix+".Search.HasHandle", jcbHasAHandle.isSelected() ? "true" : "false");
						
				props.saveProperties("user");
			}
			super.close();
		} catch (Exception e) {	
			e.printStackTrace();
			log.error(e);
		}
	}
	
	private void resetDialogComboBoxes(boolean mode) {
		
		DefaultComboBoxModel<String> model;
		String s;
		
		try {
			
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			jcbFulltext.removeAllItems();
			jcbFulltext.insertItemAt(res.getString("notselected"), 0);
			
		    jcbFulltext.setEditable(true);

			String ft = props.getProperty("user",  prefix+".Search.Fulltext");
			int selected = 0;
			if (ft != null) {
				String fts[] = ft.split("[!]");
				for (int i = fts.length - 1; i > -1; i--) {
					if (fts[i].startsWith("#")) {
						jcbFulltext.insertItemAt(fts[i].substring(1), 1);
						if (mode) selected = i + 1;
					} else {
						jcbFulltext.insertItemAt(fts[i], 1);
					}
				}
				jcbFulltext.setSelectedIndex(selected);
			}
			
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			s = props.getProperty("user",  prefix+".Search.ContentModel"); 			
			if (mode && s != null) jcbContentModel.setSelectedItem(s); else jcbContentModel.setSelectedIndex(0);
			
		    model = new DefaultComboBoxModel<String>();
		    model.addElement(res.getString("allgroups"));
		    JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));	    
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if(!el.getText().isEmpty()) model.addElement(el.getText());
			}
			jcbGroup.setModel(model);
			
 			s = props.getProperty("user",  prefix+".Search.Group");
			if (mode && s != null) jcbGroup.setSelectedItem(s); else jcbGroup.setSelectedIndex(0);
			
			JSpinner jcbLimit = ((JSpinner) getGuiComposite().getWidget("jcbLimit"));	
			s = props.getProperty("user",  prefix+".Search.Limit");
			if (s != null && !s.isEmpty()) jcbLimit.setValue(new Integer(s)); else jcbLimit.setValue(500);	
			
		    JCheckBox jcbHasAHandle = ((JCheckBox) getGuiComposite().getWidget("jcbHasAHandle"));	
			s = props.getProperty("user",  prefix+".Search.HasHandle");
			if (s != null && s.equals("true"))  jcbHasAHandle.setSelected(true);	

		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void addItem() {
		try {
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			ComboBoxModel<String> model = jcbFulltext.getModel();
			for (int i = 0; i < model.getSize(); i++) {
			   if (((String)model.getElementAt(i)).equals((String)jcbFulltext.getSelectedItem())) return;   
			}
			if (!((String)jcbFulltext.getSelectedItem()).isEmpty()) {
				jcbFulltext.insertItemAt((String)jcbFulltext.getSelectedItem(), 1);
			} else {
				jcbFulltext.setSelectedIndex(0);
			}
		} catch (Exception e) {
			log.error(e);
		}	
	}	

}

