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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;

import org.emile.cirilo.Namespaces;
import org.emile.cirilo.exceptions.FedoraException;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.LogSet;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.business.UOPFactory;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CContextWidgets;
import org.emile.client.dialog.core.CDocumentFilter;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CPropertyStore;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.utils.AnnulationStack;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.PrototypeListEntry;
import org.jdom.Document;
import org.jdom.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ReplaceDialog extends CDialog {
		
	private static Logger log = Logger.getLogger(ReplaceDialog.class);

	private static int MAX_PIPELINES = 19;
	
	private javax.swing.text.Document logview;

	private FedoraConnector connector;
	private ResourceBundle res; 
	private CPropertyStore pipelineStore; 
	private AnnulationStack queue; 
	private CPropertyService props;
	private RepositoryDialog parent;
	private CContextWidgets cwidgets;
	private String title;	
	
	public ReplaceDialog() {}
		
	public String getTitle() {
		return title;
	}
	
	public JProgressBar getProgressBar() {
		JProgressBar pb = null;
		try {
			pb = ((JProgressBar) getGuiComposite().getWidget("jpbProgessBar"));
		} catch (Exception e) {}
		return pb;
	}

	public void handleRelAddButton(ActionEvent ae) {
		try {
			cwidgets.add();		
		} catch (Exception e) {}
	}

	public void handleRelRemoveButton(ActionEvent ae) {
		try {
			cwidgets.remove();
		} catch (Exception e) {}
	}

	public void handleRelFindButton(ActionEvent ae) {
		try {
			cwidgets.addterm();
			cwidgets.find();
		} catch (Exception e) {}
	}
	
	public void handleRelFindAddButton(ActionEvent ae) {
		try {
			cwidgets.addterm();
		} catch (Exception e) {}
	}
	
	public void handleRelFindDelButton(ActionEvent ae) {
		try {
			cwidgets.delterm();
		} catch (Exception e) {}	
	}

	public void handleApplyButton(ActionEvent ae) {
		boolean valid = false;
		try {
			
			if (((JButton)getGuiComposite().getWidget("jbApply")).getText().equals(res.getString("cancel"))) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcancel"), res.getString("replacement").toLowerCase() ), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					((JProgressBar) getGuiComposite().getWidget("jpbProgessBar")).setVisible(false);		
					queue.set(title);
				}
				return;
			}
			
			JTabbedPane jtpPane = (JTabbedPane) getGuiComposite().getWidget("jtpPane");
			int sel = jtpPane.getSelectedIndex();
			JTable jtData = parent.getJtData();
			int[] rows = jtData.getSelectedRows();
			
			switch (sel) {
   				case 0: valid = validateGenerals(rows.length); break;
   				case 1: valid = validateTransformations(rows.length); break;
   				case 2: valid = validatePipelines(rows.length); break;
   				case 3: valid = validateMemberships(rows.length); break;
   				case 4: valid = validateQueries(rows.length); break;
			}
			if (valid) {
				CSwingWorker loader = new CSwingWorker(this, null);
				loader.execute();
			}
		} catch (Exception e) {}		
	}
	
	public void handleConfigureButton(ActionEvent ae) {
		try {	

			PreferencesDialog dlg = (PreferencesDialog) CServiceProvider.getService(DialogNames.PREFERENCES_DIALOG);
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels"));	
			dlg.setup(this, connector.getHostname(), StringUtils.substringBefore((String) jcbContentModel.getSelectedItem(), " "));
			dlg.open();
						
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	
	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			setPipelineWidgets();
		}
	}
	
	public void handleChoosePipeline00(ActionEvent ae) {
		try {			
			handleChoosePipeline("00");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline01(ActionEvent ae) {
		try {			
			handleChoosePipeline("01");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline02(ActionEvent ae) {
		try {			
			handleChoosePipeline("02");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline03(ActionEvent ae) {
		try {			
			handleChoosePipeline("03");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline04(ActionEvent ae) {
		try {			
			handleChoosePipeline("04");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline05(ActionEvent ae) {
		try {			
			handleChoosePipeline("05");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline06(ActionEvent ae) {
		try {			
			handleChoosePipeline("06");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline07(ActionEvent ae) {
		try {			
			handleChoosePipeline("07");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline08(ActionEvent ae) {
		try {			
			handleChoosePipeline("08");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline09(ActionEvent ae) {
		try {			
			handleChoosePipeline("09");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline10(ActionEvent ae) {
		try {			
			handleChoosePipeline("10");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline11(ActionEvent ae) {
		try {			
			handleChoosePipeline("11");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline12(ActionEvent ae) {
		try {			
			handleChoosePipeline("12");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline13(ActionEvent ae) {
		try {			
			handleChoosePipeline("13");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline14(ActionEvent ae) {
		try {			
			handleChoosePipeline("14");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline15(ActionEvent ae) {
		try {			
			handleChoosePipeline("15");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline16(ActionEvent ae) {
		try {			
			handleChoosePipeline("16");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline17(ActionEvent ae) {
		try {			
			handleChoosePipeline("17");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline18(ActionEvent ae) {
		try {			
			handleChoosePipeline("18");
		} catch (Exception e) {}		
	}	
	public void handleChoosePipeline19(ActionEvent ae) {
		try {			
			handleChoosePipeline("19");
		} catch (Exception e) {}		
	}	

	public void handleChoosePipeline(String widget) {
		try {			
			JComboBox<String> jcbContentModels = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels"));	

			JLabel jlXsl = ((JLabel) getGuiComposite().getWidget("jlXsl"+widget));
			XSLDialog dlg = (XSLDialog) CServiceProvider.getService(DialogNames.XSL_DIALOG);
			dlg.setup(parent, (String)jcbContentModels.getSelectedItem(), jlXsl.getText());
			dlg.open();
		
			if (dlg.getLocation() != null) {
				((JTextField) getGuiComposite().getWidget("jtfPipeline"+widget)).setText(dlg.getLocation());
			}
	
		} catch (Exception e) {}		
	}	

	public void handleChoosePipelineFile(ActionEvent ae) {
		try {
			JFileChooser chooser = CFileChooser.get(res.getString("choosestylesheet"), props.getProperty("user", "ReplaceDialog.Stylesheet"), new String[]{".xsl"}, null, -1);
			
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfStylesheet"));
				tf.setText(chooser.getSelectedFile().getAbsolutePath());
				props.setProperty("user", "ReplaceDialog.Stylesheet", tf.getText());
			}
		} catch (Exception e) {}		
	}	

	
	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}		
	}

	public void setup(String title, RepositoryDialog parent, FedoraConnector connector) {
		try {
			this.connector = connector;
			this.title = title;
			this.parent = parent;
		} catch (Exception e) {		
			log.error(e);
		}
	}
		
	public void doit() {
		int no = 0;
		int ok = 0;
		try {			
			JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");			
			JTabbedPane jtpPane = (JTabbedPane) getGuiComposite().getWidget("jtpPane");
			String model  = (String) ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels")).getSelectedItem();	
			
			int sel = jtpPane.getSelectedIndex();
			JTable jtData = parent.getJtData();
			int[] rows = jtData.getSelectedRows();
			
			queue.unset(title);
						
			for (int i=0; i <rows.length; i++) {
				   try {
					   String pid = (String)jtData.getValueAt(rows[i], 1);
					   jpbProgessBar.setString(pid);
					   
					   switch (sel) {
			   				case 0: 
			   					doGenerals(pid, (String)jtData.getValueAt(rows[i], 3));
			   					ok++;
			   					break;
			   				case 1: 
			   					doTransformations(pid); 
			   					ok++;
			   					break;
			   				case 2:  
			   					if (StringUtils.substringAfterLast((String)jtData.getValueAt(rows[i], 3), "/").equals(model)) {	
			   						doPipelines(pid);
				   					ok++;
			   					}
			   					break;
			   				case 3: 
			   					doMemberships(pid); 
			   					ok++;
			   					break;
			   				case 4: 
			   					doQueries(pid); 
			   					ok++;
			   					break;
			   				default:
					   }
					   connector.stubSetLastModfied(pid, null);
					   if (queue.get(title)) break;
				   } catch (Exception q) {}
				   no++;
			   }
	    	jpbProgessBar.setVisible(false);		
	     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("replacesummary"), new Integer(ok).toString(), new Integer(no).toString()), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
		}		
	}
	
	private boolean validateGenerals(int no) throws FedoraException{
		boolean valid = false;
		try {

			String message = Common.msgFormat(res.getString("forall"), new Integer(no).toString());

		    String jcbsGroup = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsGroup")).getSelectedItem();	    
		    String jcbsMarkAsLanguageResource = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsMarkAsLanguageResource")).getSelectedItem();	    
		    String jcbsRefreshObjectFromSource = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsRefreshObjectFromSource")).getSelectedItem();	    
		    String jcbsVersionableDatastreams = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsVersionableDatastreams")).getSelectedItem();	    

		    
		    if (!jcbsMarkAsLanguageResource.equals("-") || !jcbsRefreshObjectFromSource.equals("-") ||
		    	!jcbsGroup.equals("-") || !jcbsVersionableDatastreams.equals("-")) { 
		    	
		    	char ad = 97;
		    	
		    	if (!jcbsGroup.equals("-")) {
		    		message +=Common.msgFormat(res.getString("replowner"), Character.toString(ad++), (String)((JComboBox<String>) getGuiComposite().getWidget("jcbGroup")).getSelectedItem());
		    	} 
		    	if (!jcbsMarkAsLanguageResource.equals("-")) {
		    		message +=Common.msgFormat(res.getString("replsetlr"),  Character.toString(ad++), jcbsMarkAsLanguageResource.toLowerCase());
		    	}    	
		    	if (!jcbsRefreshObjectFromSource.equals("-")) {
		    		message +=Common.msgFormat(res.getString("replsetrefresh"), Character.toString(ad++));
		    	} 
		    	if (!jcbsVersionableDatastreams.equals("-")) {
		    		message +=Common.msgFormat(res.getString("replversion"),  Character.toString(ad++), (String)((JComboBox<String>) getGuiComposite().getWidget("jcbVersionableDatastreams")).getSelectedItem());
			    } 
				if (JOptionPane.showConfirmDialog(null,message +  res.getString("askcontinue"), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
					valid = true;
				}
 		    } else {	    	
		     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noreplaceop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   	
		    }		    


		} catch (Exception e ) {
			e.printStackTrace();
			throw new FedoraException();
		}
		return valid;
	}

	private boolean validateTransformations(int no) throws FedoraException{
		boolean valid = false;
		try {
			
		    JComboBox<String> jcbsTransformations = ((JComboBox<String>) getGuiComposite().getWidget("jcbsTransformations"));	    
		    String  dsid = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbDatastreams")).getSelectedItem();	    
		    String stylesheet = ((JTextField) getGuiComposite().getWidget("jtfStylesheet")).getText();
					
		    if (((String)jcbsTransformations.getSelectedItem()).equals(res.getString("apply"))) {   	
		    	if(!stylesheet.isEmpty()) {
					if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("asktransf"), new Integer(no).toString(), dsid, stylesheet ), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
						valid = true;
					}
		    	} else {
			     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noxslt")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   	
			    	} 	
		    } else {	    	
		     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noreplaceop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   	
		    }		    
		} catch (Exception e ) {
			throw new FedoraException();
		}
		return valid;
	}

	private boolean validatePipelines(int no) throws FedoraException{
		boolean noop = true;
		boolean valid = false;
		try {		    			
		    String  model = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels")).getSelectedItem();	    
		    for (String s: pipelineStore.getKeyList()) {
		    	JComboBox<String> jcbsPipeline = ((JComboBox<String>) getGuiComposite().getWidget(pipelineStore.getName(s)));
		        if (!((String)jcbsPipeline.getSelectedItem()).equals("-")) {  
		        	noop = false;
		        	break;
		        }
		    }		     
		    if (!noop) {   	
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askpipes"), new Integer(no).toString(), model), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
					valid = true;
				}
		    } else {	    	
		     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noreplaceop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   	
		    }		    
			
		} catch (Exception e ) {
			e.printStackTrace();
			throw new FedoraException();
		}
		return valid;
	}

	private boolean validateMemberships(int no) throws FedoraException{
		boolean valid = false;
		try {
			
		    JComboBox<String> jcbsRels = ((JComboBox<String>) getGuiComposite().getWidget("jcbsRels"));	    
		    JList<String> jtRels = (JList<String>) getGuiComposite().getWidget("jtRels");	    
		    
		    if (!((String)jcbsRels.getSelectedItem()).equals("-")) {   	
		    	if(jtRels.getModel().getSize() > 0) {
					if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askrels"), new Integer(no).toString()), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
						valid = true;
					}
		    	} else {
			     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("norels")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   		    		
		    	}
		    } else {	    	
		     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noreplaceop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   	
		    }		    

	
		} catch (Exception e ) {
			throw new FedoraException();
		}
		return valid;
	}

	private boolean validateQueries(int no) throws FedoraException{
		boolean valid = false;
		try {
		    JComboBox<String> jcbsQuery = ((JComboBox<String>) getGuiComposite().getWidget("jcbsQuery"));	    
		    JTextArea jtaQuery = (JTextArea) getGuiComposite().getWidget("jtaQuery");	    
				
		    if (((String)jcbsQuery.getSelectedItem()).equals(res.getString("apply"))) {   	
		    	if(jtaQuery.getText().toLowerCase().contains("select")) {
					if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askquery"), new Integer(no).toString()), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
						valid = true;
					}
		    	} else {
			     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noquery")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   		    		
		    	}
		    } else {	    	
		     	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("noreplaceop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);   	
		    }		    
		} catch (Exception e ) {
			throw new FedoraException();
		}
		return valid;
	}
	
	private void doGenerals(String pid, String model) throws FedoraException{
	
		CServiceName service_name = null;	
		LogSet ls = null;
		
		try {
		    String jcbsGroup = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsGroup")).getSelectedItem();	    
		    String jcbsMarkAsLanguageResource = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsMarkAsLanguageResource")).getSelectedItem();	    
		    String jcbsRefreshObjectFromSource = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsRefreshObjectFromSource")).getSelectedItem();	    
		    String jcbsVersionableDatastreams = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsVersionableDatastreams")).getSelectedItem();	    
	    		    
		    if (!jcbsGroup.equals("-")) {
	    		JComboBox<String> jcbGroup = (JComboBox<String>) getGuiComposite().getWidget("jcbGroup");	    
	    		String group = (String)jcbGroup.getSelectedItem();
	    		connector.stubSetOwner(pid, group);
		    } 
	    	if (!jcbsMarkAsLanguageResource.equals("-")) {
	    		if (!model.contains("/TEI")) throw new FedoraException();
				String sel = jcbsMarkAsLanguageResource.equals(res.getString("set")) ? "set" : "unset";	
				Element cpm = new Element("hasComponentMetadata", Namespaces.xmlns_cm4f).setAttribute("resource", Common.COMPONENT_METADATA, Namespaces.xmlns_rdf);
				Document rels_ext = XMLUtils.createDocumentFromByteArray(connector.stubGetDatastream(pid, "RELS-EXT"));
				switch (sel) {
					case "set" : 
						connector.stubAddProperty(pid, cpm);
						if (XMLUtils.getChild("//cm4f:hasComponentMetadata", rels_ext) == null) {
							rels_ext.getRootElement().getChild("Description", Namespaces.xmlns_rdf).addContent(cpm);
							connector.stubModifyDatastream(pid, "RELS-EXT", XMLUtils.toByteArray(rels_ext), "text/xml", null);
						}
						break;
					case "unset" : 
						connector.stubDelProperty(pid, cpm);
						if (XMLUtils.getChild("//cm4f:hasComponentMetadata", rels_ext) != null) {
							rels_ext.getRootElement().getChild("Description", Namespaces.xmlns_rdf).removeChild("hasComponentMetadata", Namespaces.xmlns_cm4f);
							connector.stubModifyDatastream(pid, "RELS-EXT", XMLUtils.toByteArray(rels_ext), "text/xml", null);
						}
						break;
				}
	    	}   
	    	
	    	if (!jcbsRefreshObjectFromSource.equals("-")) {
				UOPFactory uop = new UOPFactory(StringUtils.substringAfterLast(model,"/"));
				String buf = connector.stubTriggerUploadWorkflowWithLogProtocol(pid, null, uop.getUOP().get(), null, "");
				ls = new LogSet(buf);

	    	} 
	    	
	    	if (!jcbsVersionableDatastreams.equals("-")) {
				JComboBox<String> jcbVersionableDatastreams = ((JComboBox<String>) getGuiComposite().getWidget("jcbVersionableDatastreams"));	
	    		String dsid = (String)jcbVersionableDatastreams.getSelectedItem();
				connector.stubVersionDatastream(pid, dsid);
	    	} 
	    	log(pid, res.getString("changed"));
		} catch (Exception e ) {
	    	log(pid, res.getString("faulty"));

			throw new FedoraException();
		}
		
		if (ls != null) {
			 for (int i = ls.nextSetBit(0); i >= 0; i = ls.nextSetBit(i+1))
				log(ls.get(i), "");
		}

	}

	private void doTransformations(String pid) throws FedoraException{
		try {
			
		    String dsid = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbDatastreams")).getSelectedItem();	    
		    String stylesheet = ((JTextField) getGuiComposite().getWidget("jtfStylesheet")).getText();
		    
		    Document doc = XMLUtils.createDocumentFromByteArray(connector.stubGetDatastream(pid, dsid));
		    byte[] stream = XMLUtils.transform(doc, stylesheet, null);
		    connector.stubModifyDatastream(pid, dsid, stream, "text/xml", null);
	    	log(pid, res.getString("changed"));

		} catch (Exception e ) {
	    	log(pid, res.getString("faulty"));
			throw new FedoraException();
		}
	}

	private void doPipelines(String pid) throws FedoraException{
		try {
			Document pipelines = XMLUtils.createDocumentFromByteArray(connector.stubGetPipelinesAsRDF(pid));
			for (String key: pipelineStore.getKeyList()) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget(pipelineStore.getName(key).replaceAll("jcbs", "jtf")));
				Element pipe = XMLUtils.getChild("//s:result[s:pipeline = '"+key+"']", pipelines);
				pipelineStore.set(key, pipe != null ? pipe.getChildText("url", Namespaces.xmlns_sparql2001) : "", tf.getText());
				String sel = (String)((JComboBox<String>) getGuiComposite().getWidget(pipelineStore.getName(key))).getSelectedItem();	
				if (!sel.equals("-")) {
					if (!pipelineStore.equals(key)) {
						String action = sel.equals(res.getString("replace")) ? "replace" : "remove";
						ArrayList<String> property = pipelineStore.get(key);
						switch (action) {
							case "remove": 
								if (!property.get(1).isEmpty())  {
									Element del = new Element(StringUtils.substringAfter(key, ":"), Namespaces.byPrefix.get(StringUtils.substringBefore(key, ":"))).setAttribute("resource", property.get(1).replaceAll(Common.THIS,Common.APACHE), Namespaces.xmlns_rdf);
									connector.stubDelProperty(pid, del);
								}
						 		break;
							case "replace" : 
								if (!property.get(2).isEmpty()) {
									Element rep = new Element(StringUtils.substringAfter(key, ":"), Namespaces.byPrefix.get(StringUtils.substringBefore(key, ":"))).setAttribute("resource", property.get(1).replaceAll(Common.THIS,Common.APACHE), Namespaces.xmlns_rdf);
									if (!property.get(1).isEmpty()) connector.stubDelProperty(pid, rep);
									rep.setAttribute("resource", property.get(2).replaceAll(Common.THIS,Common.APACHE), Namespaces.xmlns_rdf);
									connector.stubAddProperty(pid, rep);
								}
								break;
						}
				    	log(pid, res.getString("changed"));
					}
				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
	    	log(pid, res.getString("faulty"));
			throw new FedoraException();
		}
	}

	private void doMemberships(String pid) throws FedoraException{
		String rels = new String();
		String action = null;

		try {
			String sel = (String)((JComboBox<String>) getGuiComposite().getWidget("jcbsRels")).getSelectedItem();	
			if (!sel.equals("-")) {
				if (sel.equals(res.getString("add"))) action = "add";
				else if (sel.equals(res.getString("replace"))) action = "replace";
				else if (sel.equals(res.getString("remove"))) action = "remove";
				JList<String> jtRels = (JList<String>) getGuiComposite().getWidget("jtRels");			
			    for (int i = 0; i< jtRels.getModel().getSize();i++){
			    	rels+=StringUtils.substringAfter((String)jtRels.getModel().getElementAt(i), "| ")+";";
			    }			
				switch (action) {
					case "add":
					    String memberships = new String();
						for (Element el: XMLUtils.getChildren("//s:result", XMLUtils.createDocumentFromByteArray(connector.stubGetMembershipsAsRDF(pid)))) {
							memberships += el.getChildText("pid", Namespaces.xmlns_sparql2001)+";";
						}	
						String nrels = new String();
						for (String s: rels.split("[;]")) {
							if (!memberships.contains(s+";")) {
								nrels += s+";";
							}
						}
						connector.stubAddRelationships(pid, nrels);
						break;
					case "replace":
						connector.stubDelRelationships(pid, "all");
						if (!rels.isEmpty()) connector.stubAddRelationships(pid.trim(), rels);
						break;
					case "remove": 
						connector.stubDelRelationships(pid, rels);
						break;
					default:
				}
		    	log(pid, res.getString("changed"));
			}	    

		} catch (Exception e) {
			e.printStackTrace();
	    	log(pid, res.getString("faulty"));
			throw new FedoraException();		
		}	
	}

	private void doQueries(String pid) throws FedoraException{
		try {
		    JTextArea jtaQuery = (JTextArea) getGuiComposite().getWidget("jtaQuery");	    
		    connector.stubModifyDatastream(pid, "QUERY", jtaQuery.getText().replaceAll("<[$]self>", "<"+Utils.rename(pid)+">").replaceAll("[$]self", pid).getBytes(), "text/plain", null);
	    	log(pid, res.getString("changed"));
		} catch (Exception e ) {
	    	log(pid, res.getString("faulty"));
			throw new FedoraException();
		}
	}

	public void setEnable(boolean mode) {
		
		try {
			((JButton)getGuiComposite().getWidget("jbApply")).setText(mode ? res.getString("apply") : res.getString("cancel"));
		} catch (Exception e) {	
		}

	}
	
	public void show() throws CShowFailedException {
		try {
				
			setTitle(title);			
			refresh();
			CBoundSerializer.load(this.getCoreDialog(), null, parent.getCoreDialog().getSize(), false);
		
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void refresh() { 
		try {
		} catch (Exception e) {		
			log.error(e);
		}
	}

	protected void opened() throws COpenFailedException {
		
		String groups = new String();
		String ds;
		
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		
			queue = new AnnulationStack();
			queue.unset(title);
			
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbRelAdd", "handleRelAddButton");
			CDialogTools.createButtonListener(this, "jbRelRemove", "handleRelRemoveButton");
			CDialogTools.createButtonListener(this, "jbRelFind", "handleRelFindButton");
			CDialogTools.createButtonListener(this, "jbRelFindAdd", "handleRelFindAddButton");
			CDialogTools.createButtonListener(this, "jbRelFindDel", "handleRelFindDelButton");
			CDialogTools.createButtonListener(this, "jbStylesheet", "handleChoosePipelineFile");
			CDialogTools.createButtonListener(this, "jbConfigure", "handleConfigureButton");


			JEditorPane jLogView = (JEditorPane) getGuiComposite().getWidget("jLogView");	
			logview = jLogView.getDocument();

			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));	
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				jcbGroup.addItem(el.getText());
				groups += el.getText() + ";";
			}

			JComboBox<String> jcbContentModels = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels"));	
			for (PrototypeListEntry pt: connector.stubGetPrototypeList(null, true)) {
				jcbContentModels.addItem(StringUtils.substringAfterLast(pt.getModel(), "/"));
 			}
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbContentModels"), this, "handleItemListener");
			ds = props.getProperty("user", "ReplaceDialog.ContentModel");
			if (ds != null) jcbContentModels.setSelectedItem(ds);
		
			JComboBox<String> jcbDatastreams = ((JComboBox<String>) getGuiComposite().getWidget("jcbDatastreams"));	
			for (String s: parent.getPipelineStore().getParameters("TransformableDatastreams")) {
				jcbDatastreams.addItem(s);
 			}
			ds = props.getProperty("user", "ReplaceDialog.TransformableDatastream");
			if (ds != null) jcbDatastreams.setSelectedItem(ds);
	
			JComboBox<String> jcbVersionableDatastreams = ((JComboBox<String>) getGuiComposite().getWidget("jcbVersionableDatastreams"));	
			for (String s: parent.getPipelineStore().getParameters("VersionableDatastreams")) {
				jcbVersionableDatastreams.addItem(s);
			}
			ds = props.getProperty("user", "ReplaceDialog.VersionableDatastream");
			if (ds != null) jcbVersionableDatastreams.setSelectedItem(ds);

		    JTextArea jtaQuery = (JTextArea) getGuiComposite().getWidget("jtaQuery");	    
			ds = props.getProperty("user", "ReplaceDialog.Query");
			if (ds != null)  jtaQuery.setText(ds);
		    	
			JTextField jtfStylesheet = (JTextField) getGuiComposite().getWidget( "jtfStylesheet" );
			String stylesheet = props.getProperty("user", "ReplaceDialog.Stylesheet");
			if (stylesheet != null) jtfStylesheet.setText(stylesheet);
			
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 		

			
			cwidgets = new CContextWidgets(connector, (CDialog)this, groups);		
			initPipelineWidgets();
			
		} catch (Exception e) {		
			log.error(e);
		}
			
	}
			
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public void close() {
		try {
			queue.unset(title);
			
			cwidgets.saveterms();
			
			JComboBox<String> jcbContentModels = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels"));
			props.setProperty("user", "ReplaceDialog.ContentModel", (String)jcbContentModels.getSelectedItem() );
			JComboBox<String> jcbDatastreams = ((JComboBox<String>) getGuiComposite().getWidget("jcbDatastreams"));
			props.setProperty("user", "ReplaceDialog.TransformableDatastream", (String)jcbDatastreams.getSelectedItem() );
			JComboBox<String> jcbVersionableDatastreams = ((JComboBox<String>) getGuiComposite().getWidget("jcbVersionableDatastreams"));
			props.setProperty("user", "ReplaceDialog.VersionableDatastream", (String)jcbVersionableDatastreams.getSelectedItem() );
			JTextArea jtaQuery = (JTextArea) getGuiComposite().getWidget("jtaQuery");
			props.setProperty("user", "ReplaceDialog.Query", jtaQuery.getText() );
			props.saveProperties("user");
			
			CBoundSerializer.save(this.getCoreDialog(), null, null);
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
	
	private void initPipelineWidgets() {
		try {
			for (int i = 0; i <= MAX_PIPELINES; i++) {
				String num = String.format("%02d", i);
				CDialogTools.createButtonListener(this, "jbChoosePipeline"+num, "handleChoosePipeline"+num);
			}
			setPipelineWidgets();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}		
	}
	
	private void setPipelineWidgets() {
		try {
			JComboBox<String> jcbContentModels = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModels"));	

			String model = (String)jcbContentModels.getSelectedItem();
			
			pipelineStore = new CPropertyStore();
						
			setPipelines(parent.getPipelineStore().getIngestPipelines(model),  5, 0);
			setPipelines(parent.getPipelineStore().getDisseminationPipelines(model),  10, 5);
			setPipelines(parent.getPipelineStore().getMetadataPipelines(model),  5, 15);
			
		} catch (Exception e) {e.printStackTrace();}

	}
	
	private void setPipelines(ArrayList<String>pipes, int count, int onset) {
		int xc = 0;
		try {
			CDocumentFilter filter = new CDocumentFilter();
			xc = onset + pipes.size(); 
			if (!pipes.isEmpty()) {
				for (int i = 0; i < pipes.size() && i < count ; i++) {
					String num = String.format("%02d", onset+i);
					((JLabel) getGuiComposite().getWidget( "jlXsl"+num )).setText(pipes.get(i));
					((Box) getGuiComposite().getWidget( "Box"+num )).setVisible(true);
					JTextField pipeline = ((JTextField) getGuiComposite().getWidget("jtfPipeline"+num));	
					pipelineStore.add(pipes.get(i), "jcbsPipeline"+num, null, null);
					AbstractDocument doc = ( AbstractDocument) pipeline.getDocument();
					doc.setDocumentFilter(filter);			
				}				
			}
			for (int i = xc; i < onset + count; i++) {
				((Box) getGuiComposite().getWidget( "Box"+String.format("%02d", i) )).setVisible(false);
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private void log(String pid, String message) {
		try {
			logview.insertString(logview.getLength(), String.format("%s %s %s\n", Common.getDate(), pid, message), null);
		} catch (Exception e) {}
	}

}
