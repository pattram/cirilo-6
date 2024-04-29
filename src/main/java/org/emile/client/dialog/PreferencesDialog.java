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
import org.emile.client.CiriloFrame;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CHyperlink;
import org.emile.cm4f.models.UploadOptions;

import java.awt.Dimension;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceName;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;


public class PreferencesDialog extends CDialog {

	private static Logger log = Logger.getLogger(PreferencesDialog.class);
    
	
	private static String[] AcceptIDSExclusively = {"jcbCUBEAcceptIDSExclusively", "jcbLIDOAcceptIDSExclusively", "jcbTEIAcceptIDSExclusively"};
	private static String[] ResolvePlacenames = {"jcbCUBEResolvePlacenames", "jcbLIDOResolvePlacenames", "jcbTEIResolvePlacenames"};
	
	private Object parent;
	private IGuiAdapter moGA;
	private ResourceBundle res; 
	private CPropertyService props;
	
	private String fontsize;
    private String hostname;
    
    private String model;
	
	public PreferencesDialog() { }

	public void setDirty(boolean ab_IsDirty) {
		super.setDirty(ab_IsDirty);
	}
			
	public void setup(Object parent, String hostname, String model) {
		try {
			this.parent = parent;
			this.hostname = hostname;
			this.model = model;
		} catch (Exception e) {		
			log.error(e);
		}
	}
	public void handleCloseButton(ActionEvent e) {
		close();
	}
	
	public void handleAcceptIDSExclusively(ActionEvent ae, int widget) {
		try {
			JCheckBox placenames = (JCheckBox) getGuiComposite().getWidget(ResolvePlacenames[widget]);
			JCheckBox IDSExclusively = (JCheckBox) getGuiComposite().getWidget(AcceptIDSExclusively[widget]);
			
			if (IDSExclusively.isSelected()) placenames.setSelected(true);
			if (!IDSExclusively.isSelected()) placenames.setSelected(false);		
		} catch (Exception e) {}
	}


	public void handleApplyButton(ActionEvent ae) {
		
		UploadOptions uop;
		JTextField tf;
		JSpinner sp;
		
		try {
			
			sp = (JSpinner) getGuiComposite().getWidget("jtfFontSize");			
			if (parent instanceof CiriloFrame && !sp.getValue().toString().equals(fontsize)) {
				props.setProperty("user", "FontSize", sp.getValue().toString());
				((CiriloFrame)parent).setUIFont();
			}
			
	  		JCheckBox checkbox = (JCheckBox) getGuiComposite().getWidget("jcbShowByDoubleClick");
    		props.setProperty("user", "ShowByDoubleClick", checkbox.isSelected() ? "true" : "false");
    		
	  		JCheckBox thecure = (JCheckBox) getGuiComposite().getWidget("jcbTheCure");
    		props.setProperty("user", "IReallyLikeTheCure", thecure.isSelected() ? "true" : "false");

			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.CUBE_UPLOADOPTIONS);
			set("CUBE", "ExtractDCMI", 2, uop);
			set("CUBE", "ApplySemanticPolicy", 3, uop);
			set("CUBE", "ExecuteCustomization", 1, uop);
			set("CUBE", "ExecuteValidation", 0, uop);
			set("CUBE", "TriggerPipelines", 10, uop);
			set("CUBE", "ResolveSKOS", 4, uop);
			set("CUBE", "ResolvePlacenames", 5, uop);
			set("CUBE", "AcceptIDSExclusively", 6, uop);
			props.setProperty("user", ServiceNames.CUBE_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.GML_UPLOADOPTIONS);
			set("GML", "ExtractDCMI", 2, uop);
			set("GML", "ApplySemanticPolicy", 3, uop);
			set("GML", "ExecuteCustomization", 1, uop);
			set("GML", "ExecuteValidation", 0, uop);
			set("GML", "TriggerPipelines", 10, uop);
			props.setProperty("user", ServiceNames.GML_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.LIDO_UPLOADOPTIONS);
			set("LIDO", "ExtractDCMI", 2, uop);
			set("LIDO", "ApplySemanticPolicy", 3, uop);
			set("LIDO", "ExecuteCustomization", 1, uop);
			set("LIDO", "ExecuteValidation", 0, uop);
			set("LIDO", "TriggerPipelines", 10, uop);
			set("LIDO", "ResolveSKOS", 4, uop);
			set("LIDO", "ResolvePlacenames", 5, uop);
			set("LIDO", "AcceptIDSExclusively", 6, uop);
			set("LIDO", "UploadImages", 9, uop);
			set("LIDO", "GeneratePyramidalTiff", 12, uop);
			props.setProperty("user", ServiceNames.LIDO_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.MEI_UPLOADOPTIONS);
			set("MEI", "ExtractDCMI", 2, uop);
			set("MEI", "ApplySemanticPolicy", 3, uop);
			set("MEI", "ExecuteCustomization", 1, uop);
			set("MEI", "ExecuteValidation", 0, uop);
			set("MEI", "TriggerPipelines", 10, uop);
			props.setProperty("user", ServiceNames.MEI_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.METS_UPLOADOPTIONS);
			set("METS", "ExtractDCMI", 2, uop);
			set("METS", "ExecuteValidation", 0, uop);
			props.setProperty("user", ServiceNames.METS_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.ONTOLOGY_UPLOADOPTIONS);
			set("Ontology", "ExtractDCMI", 2, uop);
			set("Ontology", "UploadImages", 9, uop);
			set("Ontology", "GeneratePyramidalTiff", 12, uop);
			props.setProperty("user", ServiceNames.ONTOLOGY_UPLOADOPTIONS.toString(), uop.get());
		
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.RDF_UPLOADOPTIONS);
			set("RDF", "ExtractDCMI", 2, uop);
			set("RDF", "UploadImages", 9, uop);
			set("RDF", "GeneratePyramidalTiff", 12, uop);
			props.setProperty("user", ServiceNames.RDF_UPLOADOPTIONS.toString(), uop.get());
	
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.RDO_UPLOADOPTIONS);
			set("RDO", "ExtractDCMI", 2, uop);
			props.setProperty("user", ServiceNames.RDO_UPLOADOPTIONS.toString(), uop.get());
	
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.RTI_UPLOADOPTIONS);
			set("RTI", "ExtractDCMI", 2, uop);
			set("RTI", "ApplySemanticPolicy", 3, uop);
			set("RTI", "ExecuteCustomization", 1, uop);
			set("RTI", "TriggerPipelines", 10, uop);
			props.setProperty("user", ServiceNames.RTI_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.SKOS_UPLOADOPTIONS);
			set("SKOS", "ExtractDCMI", 2, uop);
			set("SKOS", "Skosify", 4, uop);
			props.setProperty("user", ServiceNames.SKOS_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.SPECTRAL_UPLOADOPTIONS);
			set("Spectral", "ExtractDCMI", 2, uop);
			set("Spectral", "ApplySemanticPolicy", 3, uop);
			set("Spectral", "ExecuteValidation", 0, uop);
			set("Spectral", "GeneratePyramidalTiff", 12, uop);
			props.setProperty("user", ServiceNames.SPECTRAL_UPLOADOPTIONS.toString(), uop.get());
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.STORY_UPLOADOPTIONS);
			set("Story", "ExtractDCMI", 2, uop);
			props.setProperty("user", ServiceNames.STORY_UPLOADOPTIONS.toString(), uop.get());
	
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.TEI_UPLOADOPTIONS);
			set("TEI", "ExtractDCMI", 2, uop);
			set("TEI", "ApplySemanticPolicy", 3, uop);
			set("TEI", "ExecuteCustomization", 1, uop);
			set("TEI", "ExecuteValidation", 0, uop);
			set("TEI", "TriggerPipelines", 10, uop);
			set("TEI", "ResolveSKOS", 4, uop);
			set("TEI", "ResolvePlacenames", 5, uop);
			set("TEI", "AcceptIDSExclusively", 6, uop);
			set("TEI", "UploadImages", 9, uop);
			set("TEI", "GeneratePyramidalTiff", 12, uop);
			set("TEI", "CreateMETS", 7, uop);
			props.setProperty("user", ServiceNames.TEI_UPLOADOPTIONS.toString(), uop.get());
		
			props.saveProperties("user");
			
			JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("changessaved")), getTitle(), JOptionPane.INFORMATION_MESSAGE);			
	
			close();		
		} catch (Exception e) {	
			e.printStackTrace();
			log.error(e);
		}	
 	}

	public void handlerRemoved(CEventListener aoHandler) {
	}

	protected void cleaningUp() {
	}

	
	public void show() throws CShowFailedException {
	
		UploadOptions uop;
		JTextField tf;
		JSpinner sp;
		int item;
		
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, new Dimension(600, 600), false);
			
			this.getCoreDialog().setVisible(true);
			setDirty(false);
			
			setTitle(res.getString("preferences"));
			
			fontsize = props.getProperty("user", "FontSize");

			JTabbedPane tp = (JTabbedPane) getGuiComposite().getWidget("tp");

			Integer value = new Integer(fontsize);
			Integer min = new Integer(8);
			Integer max = new Integer(18);
			Integer step = new Integer(1);
			SpinnerNumberModel smodel = new SpinnerNumberModel(value, min, max, step);	
			
			sp = (JSpinner) getGuiComposite().getWidget("jtfFontSize");
			sp.setModel(smodel);
			sp.setEnabled(parent instanceof CiriloFrame);

			JCheckBox checkbox = (JCheckBox) getGuiComposite().getWidget("jcbShowByDoubleClick");
    		checkbox.setSelected(props.getProperty("user", "ShowByDoubleClick").equals("true"));
    		
       		JCheckBox thecure = (JCheckBox) getGuiComposite().getWidget("jcbTheCure");
       		thecure.setSelected(props.getProperty("user", "IReallyLikeTheCure").equals("true"));
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.CUBE_UPLOADOPTIONS);
			get("CUBE", "ExtractDCMI", 2, uop);
			get("CUBE", "ApplySemanticPolicy", 3, uop);
			get("CUBE", "ExecuteCustomization", 1, uop);
			get("CUBE", "ExecuteValidation", 0, uop);
			get("CUBE", "TriggerPipelines", 10, uop);
			get("CUBE", "ResolveSKOS", 4, uop);
			get("CUBE", "ResolvePlacenames", 5, uop);
			get("CUBE", "AcceptIDSExclusively", 6, uop);
			setSeeAlso(hostname, "CUBE");
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.GML_UPLOADOPTIONS);
			get("GML", "ExtractDCMI", 2, uop);
			get("GML", "ApplySemanticPolicy", 3, uop);
			get("GML", "ExecuteCustomization", 1, uop);
			get("GML", "ExecuteValidation", 0, uop);
			get("GML", "TriggerPipelines", 10, uop);
			setSeeAlso(hostname, "GML");
		
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.LIDO_UPLOADOPTIONS);
			get("LIDO", "ExtractDCMI", 2, uop);
			get("LIDO", "ApplySemanticPolicy", 3, uop);
			get("LIDO", "ExecuteCustomization", 1, uop);
			get("LIDO", "ExecuteValidation", 0, uop);
			get("LIDO", "TriggerPipelines", 10, uop);
			get("LIDO", "ResolveSKOS", 4, uop);
			get("LIDO", "ResolvePlacenames", 5, uop);
			get("LIDO", "AcceptIDSExclusively", 6, uop);
			get("LIDO", "UploadImages", 9, uop);
			get("LIDO", "GeneratePyramidalTiff", 12, uop);
			setSeeAlso(hostname, "LIDO");
		
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.MEI_UPLOADOPTIONS);
			get("MEI", "ExtractDCMI", 2, uop);
			get("MEI", "ApplySemanticPolicy", 3, uop);
			get("MEI", "ExecuteCustomization", 1, uop);
			get("MEI", "ExecuteValidation", 0, uop);
			get("MEI", "TriggerPipelines", 10, uop);
			setSeeAlso(hostname, "MEI");
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.METS_UPLOADOPTIONS);
			get("METS", "ExtractDCMI", 2, uop);
			get("METS", "ExecuteValidation", 0, uop);
			setSeeAlso(hostname, "METS");
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.ONTOLOGY_UPLOADOPTIONS);
			get("Ontology", "ExtractDCMI", 2, uop);
			get("Ontology", "UploadImages", 9, uop);
			get("Ontology", "GeneratePyramidalTiff", 12, uop);
			setSeeAlso(hostname, "Ontology");

			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.RDF_UPLOADOPTIONS);
			get("RDF", "ExtractDCMI", 2, uop);
			get("RDF", "UploadImages", 9, uop);
			get("RDF", "GeneratePyramidalTiff", 12, uop);
			setSeeAlso(hostname, "RDF");
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.RDO_UPLOADOPTIONS);
			get("RDO", "ExtractDCMI", 2, uop);
			setSeeAlso(hostname, "RDO");
			
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.RTI_UPLOADOPTIONS);
			get("RTI", "ExtractDCMI", 2, uop);
			get("RTI", "ApplySemanticPolicy", 3, uop);
			get("RTI", "ExecuteCustomization", 1, uop);
			get("RTI", "TriggerPipelines", 10, uop);
			setSeeAlso(hostname, "RTI");
		
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.SKOS_UPLOADOPTIONS);
			get("SKOS", "ExtractDCMI", 2, uop);
			get("SKOS", "Skosify", 4, uop);
			setSeeAlso(hostname, "SKOS");
		
			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.SPECTRAL_UPLOADOPTIONS);
			get("Spectral", "ExtractDCMI", 2, uop);
			get("Spectral", "ApplySemanticPolicy", 3, uop);
			get("Spectral", "ExecuteValidation", 0, uop);
			get("Spectral", "GeneratePyramidalTiff", 12, uop);
			setSeeAlso(hostname, "Spectral");

			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.STORY_UPLOADOPTIONS);
			get("Story", "ExtractDCMI", 2, uop);
			setSeeAlso(hostname, "Story");

			uop = (UploadOptions) CServiceProvider.getService(ServiceNames.TEI_UPLOADOPTIONS);
			get("TEI", "ExtractDCMI", 2, uop);
			get("TEI", "ApplySemanticPolicy", 3, uop);
			get("TEI", "ExecuteCustomization", 1, uop);
			get("TEI", "ExecuteValidation", 0, uop);
			get("TEI", "TriggerPipelines", 10, uop);
			get("TEI", "ResolveSKOS", 4, uop);
			get("TEI", "ResolvePlacenames", 5, uop);
			get("TEI", "AcceptIDSExclusively", 6, uop);
			get("TEI", "UploadImages", 9, uop);
			get("TEI", "GeneratePyramidalTiff", 12, uop);
			get("TEI", "CreateMETS", 7, uop);
			setSeeAlso(hostname, "TEI");
					
			model = model == null ? "" : model;
			
			switch (model) {
				case "CUBE": item = 1; break;
				case "GML": item = 2; break;
				case "LIDO": item = 3; break;
				case "MEI": item = 4; break;
				case "METS": item = 5; break;
				case "Ontology": item = 6; break;
				case "RDF": item = 7; break;
				case "RDO": item = 8; break;
				case "RTI": item = 9; break;
				case "SKOS": item = 10; break;
				case "Spectral": item = 11; break;
				case "Story": item = 12; break;
				case "TEI": item = 13; break;
				default: item = 0;
			}
			tp.setSelectedIndex(item);

		
		} catch (Exception e) {	
			e.printStackTrace();
			log.error(e);
		}
	}
	
	protected void opened() throws COpenFailedException {

		try {
			
			moGA = (IGuiAdapter) getGuiAdapter();
				
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 

			
			((JCheckBox) getGuiComposite().getWidget(AcceptIDSExclusively[0])).addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent ae) {
	        		handleAcceptIDSExclusively(ae, 0);
	        	}
	        });

			((JCheckBox) getGuiComposite().getWidget(AcceptIDSExclusively[1])).addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent ae) {
	        		handleAcceptIDSExclusively(ae, 1);
	        	}
	        });

			((JCheckBox) getGuiComposite().getWidget(AcceptIDSExclusively[2])).addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent ae) {
	        		handleAcceptIDSExclusively(ae, 2);
	        	}
	        });

		} catch (Exception e) {
			log.error(e);
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
	
	private boolean set(String object, String widget, int bitIndex, UploadOptions uop) {
		boolean checked = false;
	    
		try {
	    	JCheckBox checkbox = (JCheckBox) getGuiComposite().getWidget("jcb"+object+widget);
	    	uop.set(checkbox.isSelected(), bitIndex);
	    } catch (Exception e) {}
	    
	    return checked;
	}

	private void get(String object, String widget, int bitIndex, UploadOptions uop) {
    	try {
    		JCheckBox checkbox = (JCheckBox) getGuiComposite().getWidget("jcb"+object+widget);
    		checkbox.setSelected(uop.is(bitIndex));
    	} catch (Exception e) {}
    }

	private void setSeeAlso(String hostname, String model) {
    	try {

    		if (hostname != null) {
    			CHyperlink hl = (CHyperlink) getGuiComposite().getWidget("jhl"+model+"SeeAlso");
			    hl.setURI(new URI("https://"+hostname+"/doc/userguide/rcontentmodels/#"+model.toLowerCase()));
    		} else {
    			CHyperlink hl = (CHyperlink) getGuiComposite().getWidget("jhl"+model+"SeeAlso");
    			hl.setVisible(false);
    		}
    	} catch (Exception e) {}

	}
	
}


