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

import org.emile.cirilo.exceptions.FedoraException;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.CollectDirectories;
import org.emile.cirilo.utils.CollectFiles;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.business.IngestFactory;
import org.emile.client.business.UOPFactory;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CDefaultMessageDialog;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CFileFilter;
import org.emile.client.dialog.core.CHint;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.utils.AnnulationStack;
import org.emile.cm4f.models.PrototypeListEntry;
import org.emile.cm4f.models.UploadOptions;
import org.jdom.Element;

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class IngestObjectDialog extends CDialog {

	private static Logger log = Logger.getLogger(IngestObjectDialog.class);

	private CDefaultGuiAdapter moGA;
	private ResourceBundle res; 
	private CPropertyService props;
	private AnnulationStack queue; 
	
	private RepositoryDialog parent;
	private FedoraConnector connector;
	private String title;
	private UploadOptions UOP;	
	private String EXTENSION;	
	
	public IngestObjectDialog() {}

	public String getHostname() {
		return connector.getHostname();
	}
	
	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}
		    

	public void handleApplyButton(ActionEvent ae) {
		try {
			
			if (((JButton)getGuiComposite().getWidget("jbApply")).getText().equals(res.getString("cancel"))) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcancel"), res.getString("ingest").toLowerCase().replaceAll("[.]", "") ), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					((JProgressBar) getGuiComposite().getWidget("jpbProgessBar")).setVisible(false);		
					queue.set(title);
				}
				return;
			}

	    	JRadioButton jrbFilesystem = ((JRadioButton) getGuiComposite().getWidget("jrbFilesystem"));
	    	JRadioButton jrbSpreadsheet = ((JRadioButton) getGuiComposite().getWidget("jrbSpreadsheet"));
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));			
    		JTextField jtfIngestDir = ((JTextField) getGuiComposite().getWidget("jtfIngestDir"));	    		
    		
			String prototype = StringUtils.substringAfter((String)jcbContentModel.getSelectedItem(), "| ");
			String model = StringUtils.substringBefore((String)jcbContentModel.getSelectedItem(), " -");
			
	    	if (jrbFilesystem.isSelected()) {    		
	    		int objects_found = countObjectFiles(jtfIngestDir.getText(), EXTENSION, prototype); 		    		
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askingest"), new Integer(objects_found).toString(), jtfIngestDir.getText(), model), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0) {
					return;
				}
	    	} else if (jrbSpreadsheet.isSelected()) {
	    		JTextField jtfSpreadsheet = ((JTextField) getGuiComposite().getWidget("jtfSpreadsheet"));
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("asksheetingest"), jtfSpreadsheet.getText(), model), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0) {
					return;
				}
	    	}
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
  
		} catch (Exception e) {e.printStackTrace();}
	}

	public void handleSpreadsheetButton(ActionEvent ae) {
		try {
						
			final JFileChooser chooser = CFileChooser.get(res.getString("choosespreadsheet"), props.getProperty("user",  "Ingest.Spreadsheet"), new String[]{".xlsx",".ods"}, null, -1);			
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfSpreadsheet"));
				tf.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		} catch (Exception e) {}
	}
	
	public void handleTemplateButton(ActionEvent ae) {
		try {
	
			
			final JFileChooser chooser = CFileChooser.get(res.getString("choosetemplate"), props.getProperty("user",  "Ingest.Template"), new String[]{".xml"}, null, -1);			
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfTemplate"));
				tf.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		} catch (Exception e) {}
	}

	public void handleIngestDirButton(ActionEvent ae) {
		try {
			final JFileChooser chooser = CFileChooser.get(res.getString("chooseingestdir"), props.getProperty("user", "Ingest.Directory"), null, null, JFileChooser.DIRECTORIES_ONLY);						
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfIngestDir"));
				tf.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		} catch (Exception e) {}
	}
	
	public void handleResetButton(ActionEvent ae) {
		try {
			queue.set(title);

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

	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			handleFilesystemRadioButton(null);
			enableDC();
		}
	}

	public void handleGroupItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			fetchPrototypes();
		}
	}

	public void handleFilesystemRadioButton(ActionEvent ae) {
		try {
			JRadioButton jrbFilesystem = ((JRadioButton) getGuiComposite().getWidget("jrbFilesystem"));
			Box jbxFilesystem = ((Box) getGuiComposite().getWidget("jbxFilesystem"));
			Box jbxSpreadsheet = ((Box) getGuiComposite().getWidget("jbxSpreadsheet"));
			JEditorPane jLogView = ((JEditorPane) getGuiComposite().getWidget("jLogView"));
			jrbFilesystem.setSelected(true);
			jbxFilesystem.setVisible(true);
			jbxSpreadsheet.setVisible(false);
			jLogView.setEnabled(true);
		} catch (Exception e) {}
	}
	
	public void handleSpreadsheetRadioButton(ActionEvent ae) {
		try {
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));			
			JRadioButton jrbSpreadsheet = ((JRadioButton) getGuiComposite().getWidget("jrbSpreadsheet"));
			Box jbxFilesystem = ((Box) getGuiComposite().getWidget("jbxFilesystem"));
			Box jbxSpreadsheet = ((Box) getGuiComposite().getWidget("jbxSpreadsheet"));
			JEditorPane jLogView = ((JEditorPane) getGuiComposite().getWidget("jLogView"));
	
			handleFilesystemRadioButton(null);
			        
			String cm = (String) jcbContentModel.getSelectedItem();
			for (String q :  parent.getPipelineStore().getParameters("SpreadsheetableModels")) {
  				if (cm.contains("."+q.toLowerCase())) {
  					jbxFilesystem.setVisible(false);
  					jbxSpreadsheet.setVisible(true);
  					jrbSpreadsheet.setSelected(true);
   					jLogView.setEnabled(false);
				return;
  				}					
  			}
			JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("notavailable")), getTitle(), JOptionPane.WARNING_MESSAGE);									

		} catch (Exception e) {}
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


	public void setup(String title, RepositoryDialog parent) {
		try {
			this.title = StringUtils.substringBefore(title, ":");
			this.parent = parent;
			this.connector = parent.getFedoraConnector();
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	
	public void show() throws CShowFailedException {
		try {			
			setTitle(title);
			CBoundSerializer.load(this.getCoreDialog(), null, parent.getCoreDialog().getSize(), false);
			
			for (String dc : Common.DCMI) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));   		
	    		if (!dc.equals("Identifier")) {
    				String s = props.getProperty("user", "DC." + dc);
    				tf.setText(s != null ? s : "");
 	    		}
	    	}

			enableDC();

		} catch (Exception e) {
		}
	}
		
	public void doit() {
		
		InputStream is = null;
		InputStream it = null;
		
	    try {
	    	JRadioButton jrbFilesystem = ((JRadioButton) getGuiComposite().getWidget("jrbFilesystem"));
			JRadioButton jrbSpreadsheet = ((JRadioButton) getGuiComposite().getWidget("jrbSpreadsheet"));
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));			
			JComboBox<String> jcbContextPrototype = ((JComboBox<String>) getGuiComposite().getWidget("jcbContextPrototype"));			
			JEditorPane jLogView = (JEditorPane) getGuiComposite().getWidget("jLogView");
			JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");			
			JCheckBox jcbStrict = (JCheckBox) getGuiComposite().getWidget("jcbStrict");			

			String prototype = StringUtils.substringAfter((String)jcbContentModel.getSelectedItem(), "| ");
			String group = (String) jcbGroup.getSelectedItem();
			
			
	    	if (jrbFilesystem.isSelected()) {
	    		    		
	    		JTextField jtfIngestDir = ((JTextField) getGuiComposite().getWidget("jtfIngestDir"));	    
	    
	 	    	String retval =  new IngestFactory(this, this.connector, title, UOP.get()).run(jLogView, jpbProgessBar, group, jtfIngestDir.getText(), EXTENSION, prototype, (String)jcbContextPrototype.getSelectedItem(), jcbStrict.isSelected(), false);
	    		
    			String err = StringUtils.substringBefore(retval, ":");
    			String created = StringUtils.substringBetween(retval, ":", ":");
    			String updated = StringUtils.substringAfterLast(retval, ":");

    			jpbProgessBar.setVisible(false);		

    			if (Integer.parseInt(err) == 0) {
      				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("ingestsummary"), created, updated), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
      				JOptionPane.showMessageDialog(null, res.getString("ingestnote"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
    			} else {	
      				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("ingesterror"), created, updated, err), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
    			}
	    		
	    	} else if (jrbSpreadsheet.isSelected()) {
	    		 		
	    		JTextField jtfSpreadsheet = ((JTextField) getGuiComposite().getWidget("jtfSpreadsheet"));
	    		JTextField jtfTemplate = ((JTextField) getGuiComposite().getWidget("jtfTemplate"));
	    		   
	    		is = new FileInputStream(new File(jtfSpreadsheet.getText()));
	            byte[] spreadsheet = IOUtils.toByteArray(is);
	            
	    		it = new FileInputStream(new File(jtfTemplate.getText()));
	            byte[] template = IOUtils.toByteArray(it);

	        	jpbProgessBar.setString(StringUtils.substringAfterLast(jtfSpreadsheet.getText(),File.separator));
	        	
	    		int ret = connector.stubTriggerFromSpreadsheetIngest(group, prototype, UOP.get(), (String)jcbContextPrototype.getSelectedItem(), spreadsheet, template);
	  		
	    		jpbProgessBar.setVisible(false);		
	  			 		
    			if (ret == 200) {
      				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("sheetingestsummary"), jtfSpreadsheet.getText()), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
      				JOptionPane.showMessageDialog(null, res.getString("ingestnote"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
    			} else {	
      				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("shettingesterror"), jtfSpreadsheet.getText(), new Integer(ret).toString()), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
    			}
	    	}
	    	
		} catch (Exception e) {e.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
				if (it != null) it.close();
			} catch (Exception q) {}
		}

	}
	
	protected void opened() throws COpenFailedException {

		String s;
		
		try {
					    
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
			queue = new AnnulationStack();
			queue.unset(title);

			moGA = (CDefaultGuiAdapter)getGuiAdapter();		
			
			CDialogTools.createButtonListener(this, "jbConfigure", "handleConfigureButton");			
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");			
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbReset", "handleResetButton");
			
			CDialogTools.createButtonListener(this, "jbIngestDir", "handleIngestDirButton");
			CDialogTools.createButtonListener(this, "jbSpreadsheet", "handleSpreadsheetButton");
			CDialogTools.createButtonListener(this, "jbTemplate", "handleTemplateButton");
	         
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if(!el.getText().isEmpty()) jcbGroup.addItem(el.getText());
			}

			s = props.getProperty("user", "General.DefaultGroup");
			if (s != null && !s.isEmpty()) jcbGroup.setSelectedItem(s); else jcbGroup.setSelectedIndex(0);
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbGroup"), this, "handleGroupItemListener");
		
			fetchPrototypes();
			
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));			
            s =  props.getProperty("user", "General.DefaultContentModel");
            if (s != null) jcbContentModel.setSelectedItem(s); 
    		new CItemListener((JComboBox) getGuiComposite().getWidget("jcbContentModel"), this, "handleItemListener");
    		                    
			JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));
			jtfTitle.requestFocus(true);
			 			
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 

			JRadioButton jrbFilesystem = ((JRadioButton) getGuiComposite().getWidget("jrbFilesystem"));
			jrbFilesystem.setSelected(true);
					
			jrbFilesystem.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleFilesystemRadioButton(ae);
        		}
        	});
			
			JRadioButton jrbSpreadsheet = ((JRadioButton) getGuiComposite().getWidget("jrbSpreadsheet"));
			jrbSpreadsheet.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSpreadsheetRadioButton(ae);
        		}
        	});

			s = props.getProperty("user", "Ingest.Directory");
			if (s != null) (((JTextField) getGuiComposite().getWidget("jtfIngestDir"))).setText(s);
			s = props.getProperty("user", "Ingest.Spreadsheet");
			if (s != null) (((JTextField) getGuiComposite().getWidget("jtfSpreadsheet"))).setText(s);
			s = props.getProperty("user", "Ingest.Template");
			if (s != null) (((JTextField) getGuiComposite().getWidget("jtfTemplate"))).setText(s);
			
			
			JComboBox<String> jcbContextPrototype = ((JComboBox<String>) getGuiComposite().getWidget("jcbContextPrototype"));			
			jcbContextPrototype.removeAllItems();
			for (PrototypeListEntry  ple: connector.stubGetPrototypeList((String)jcbGroup.getSelectedItem(), "http://cm4f.org/Context")) {
	    		jcbContextPrototype.addItem(ple.getPid());
	    	}
			s = props.getProperty("user", "General.ContextPrototype");
			if (s != null && !s.isEmpty()) jcbContextPrototype.setSelectedItem(s); else jcbContextPrototype.setSelectedIndex(0);


		} catch (Exception e) {
			log.error(e);	
		}
	}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public void setEnable(boolean mode) {
		
		try {
			((JButton)getGuiComposite().getWidget("jbApply")).setText(mode ? res.getString("apply") : res.getString("cancel"));
			((JButton)getGuiComposite().getWidget("jbReset")).setEnabled(mode);
			((JButton)getGuiComposite().getWidget("jbConfigure")).setEnabled(mode);
		} catch (Exception e) {	
		}

	}
	
	protected void cleaningUp() {}

	public void close() {
		JTextField tf;
		try {	
			queue.set(title);

			CBoundSerializer.save(this.getCoreDialog(), null, null);
			
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			props.setProperty("user", "General.DefaultContentModel", (String)jcbContentModel.getSelectedItem());
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			props.setProperty("user", "General.DefaultGroup", (String)jcbGroup.getSelectedItem());
			JComboBox<String> jcbContextPrototype = ((JComboBox<String>) getGuiComposite().getWidget("jcbContextPrototype"));		
			props.setProperty("user", "General.ContextPrototype", (String)jcbContextPrototype.getSelectedItem());


			for (String dc : Common.DCMI) {
				tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));
				props.setProperty("user", "DC." + dc, tf.getText());
			}	
			
			tf = ((JTextField) getGuiComposite().getWidget("jtfSpreadsheet"));
			props.setProperty("user", "Ingest.Spreadsheet", tf.getText());
			tf = ((JTextField) getGuiComposite().getWidget("jtfTemplate"));
			props.setProperty("user", "Ingest.Template", tf.getText());
			tf = ((JTextField) getGuiComposite().getWidget("jtfIngestDir"));
			props.setProperty("user", "Ingest.Directory", tf.getText());
			
			
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
	
	private boolean enableDC() {

		CServiceName service_name = null;	
		
		boolean mode = false;
		
		try {

			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			String model = StringUtils.substringBefore((String)jcbContentModel.getSelectedItem(), " ");
			
			UOPFactory uop = new UOPFactory(model);
			UOP = uop.getUOP();
			EXTENSION = uop.getExtension();
			mode = !UOP.isDCify();

			CHint jbHint = (CHint) getGuiComposite().getWidget("jbHint");
			jbHint.setText(res.getString("HTuncheckdc"));
			jbHint.setVisible(!mode);
			
			for (String dc : Common.DCMI) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtf" + dc));   		
	    		if (!dc.equals("Identifier")) {
     				if (mode) { 
			    		tf.setBackground(Color.white);
	    			} else {
	    				tf.setBackground(new Color(239, 239, 239));
	    			}
	    			tf.setEnabled(mode);
	    		}
	    	}
		} catch (Exception e) {	
		}
		
		return mode;
		
	}
	
	private void fetchPrototypes() {
		try {
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
			jcbContentModel.removeAllItems();
			
			for(PrototypeListEntry pt: connector.stubGetPrototypeList((String)jcbGroup.getSelectedItem(), false)) {
				for (String s : parent.getPipelineStore().getParameters("IngestibleModels")) {
					if (Pattern.matches("^o:prototype.g_[a-z0-9.]*", pt.getPid()) && pt.getPid().contains("."+s.toLowerCase())) {
						jcbContentModel.addItem(StringUtils.substringAfterLast(pt.getModel(), "/") + " - " + pt.getTitle() + " | "+pt.getPid());	
						break;
					}					
				}	
			}
			if (jcbContentModel.getItemCount() == 0) jcbContentModel.addItem ("TEI - Default model | o:prototype.tei");
			
			JComboBox<String> jcbContextPrototype = ((JComboBox<String>) getGuiComposite().getWidget("jcbContextPrototype"));	
			jcbContextPrototype.removeAllItems();
			for (PrototypeListEntry  ple: connector.stubGetPrototypeList((String)jcbGroup.getSelectedItem(), "http://cm4f.org/Context")) {
	    		jcbContextPrototype.addItem(ple.getPid());
	    	}

		} catch (Exception e) {		
			log.error(e);
		}	
	}
	
	public int countObjectFiles(String path, String exts, String prototype) {
		int objects_found = 0;
		try {
			if ( prototype.contains(".tei") || prototype.contains(".lido") ||
				 prototype.contains(".mets") || prototype.contains(".gml") ||
				 prototype.contains(".mei") || prototype.contains(".ontology") ||
				 prototype.contains(".rdf") ||  prototype.contains(".spectral") ||
				 prototype.contains(".skos")) {
				 for (File dir: new CollectDirectories(path).getDirectories()) {
					 CollectFiles cf = new CollectFiles(dir.toString(), exts);
					 objects_found += cf.getFiles().size();
				 }
			} else if (prototype.contains(".cube") || prototype.contains(".rti")) {
				CollectDirectories cf = new CollectDirectories(path);
				ArrayList<File> directories = cf.getDirectories();	
				for (File dir: directories) {			
					File lido = cf.getLidoSource(dir);
					if (lido != null && lido.exists()) {
						objects_found++;
					}	
				}
			} else if (prototype.contains(".rdo")) {
				CollectDirectories cf = new CollectDirectories(path);
				objects_found = cf.getDirectories().size();
			}
		 } catch (Exception e) { 
			log.error(e);
		 }
		
		return objects_found;	
	}


}

