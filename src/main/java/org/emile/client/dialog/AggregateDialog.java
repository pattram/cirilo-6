package org.emile.client.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.SSLConnectionFactory;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.utils.AnnulationStack;
import org.emile.client.utils.Utils;
import org.jdom.Document;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;
import voodoosoft.jroots.dialog.CDialog;
import voodoosoft.jroots.dialog.CDialogTools;
import voodoosoft.jroots.dialog.COpenFailedException;
import voodoosoft.jroots.dialog.CShowFailedException;

public class AggregateDialog extends CDialog {
	
	private static Logger log = Logger.getLogger(AggregateDialog.class);

	private CDefaultGuiAdapter moGA;
	private ResourceBundle res; 
	private CPropertyService props;
	private AnnulationStack queue; 
	
	private FedoraConnector connector;
	private RepositoryDialog parent;
	private String title;
	private String hostname;

	private ArrayList<String> entries;

	
	public void setup(String title, RepositoryDialog parent) {
		try {
			String[] buf = title.split("▪ "); 
			this.title = title;
			this.connector = parent.getFedoraConnector();
			this.parent = parent;
			this.hostname = buf[buf.length-1];
		} catch (Exception e) {		
			log.error(e);
		}
	}

	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}
	
	public void doit() {
		try {
			JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");
			JEditorPane jLogView = (JEditorPane) getGuiComposite().getWidget("jLogView");
			String format = getSelectedFormat();
			for (int i=0; i < entries.size(); i++) {
				jpbProgessBar.setString(entries.get(i));
				int status = connector.stubUpdateAggregationDatastream(entries.get(i), format);
				if (status == 200) {
					jLogView.setText(jLogView.getText()+Common.msgFormat(res.getString("okwriteds"), Utils.getDate(),  format, entries.get(i)));
				} else {
					jLogView.setText(jLogView.getText()+Common.msgFormat(res.getString("errwriteds"), Utils.getDate(),  format, entries.get(i)));
				}
				
				if (queue.get(title)) return;
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleApplyButton(ActionEvent ae) {
	
		HttpResponse response = null;

		try {
					
			
			if (((JButton)getGuiComposite().getWidget("jbApply")).getText().equals(res.getString("cancel"))) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcancel"), res.getString("aggregation").toLowerCase() ), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					((JProgressBar) getGuiComposite().getWidget("jpbProgessBar")).setVisible(false);		
					queue.set(title);
				}
				return;
			}
			
			JEditorPane jLogView = ((JEditorPane) getGuiComposite().getWidget("jLogView"));
			jLogView.setText("");
			javax.swing.text.Document logview = jLogView.getDocument();
				
			JTable jtData = parent.getJtData();			
			int[] rows = jtData.getSelectedRows();
			String format  = getSelectedFormat();
			String model = null;
			
			entries = new ArrayList<String>();
			
			if (format != null) {
				
				if ("KML|PELAGIOS|CMIF".contains(format)) {
					model = "Context";
					logview.insertString(logview.getLength(),Common.msgFormat(res.getString("selobjects"), Utils.getDate() , model), null);
					for (int i=0; i <rows.length; i++) {
						String pid = (String)jtData.getValueAt(rows[i], 1);
						Document rdf = XMLUtils.createDocumentFromByteArray(connector.stubGetRDF(pid));
						if (((String)jtData.getValueAt(rows[i], 3)).contains("/"+model)) {
							String property = null;
							String xsl = null;
							String template = null;
							switch (format) {	
								case "KML": property = "xsl:hasXsltToKML";
									break;
								case "PELAGIOS": property = "xsl:hasXsltToPELAGIOS";
									break;
								case "CMIF": property = "xsl:hasXsltToCMIF";
									break;
							}
							xsl = XMLUtils.getResourceAttribute("//"+property, rdf);
							if (xsl != null) {
								if (checkXSLT(pid, xsl, logview)) {
									entries.add((String)jtData.getValueAt(rows[i], 1));
								}
							} else {
								logview.insertString(logview.getLength(),Common.msgFormat(res.getString("propertynotf"), Utils.getDate() , model, pid, property), null);
							}
						}
					}
				} else if ("CMDI".contains(format)) {
					model = "Corpus";
					logview.insertString(logview.getLength(),Common.msgFormat(res.getString("selobjects"), Utils.getDate() , model), null);
					for (int i=0; i <rows.length; i++) {
						String pid = (String)jtData.getValueAt(rows[i], 1);
						Document rdf = XMLUtils.createDocumentFromByteArray(connector.stubGetRDF(pid));
						if (((String)jtData.getValueAt(rows[i], 3)).contains("/"+model)) {
							String property = null;
							String xsl = null;
							switch (format) {	
								case "CMDI": property = "xsl:hasXsltToCMDI";
									break;
							}
							xsl = XMLUtils.getResourceAttribute("//"+property, rdf);
							if (xsl != null) {
						//		if (checkXSLT(pid, xsl, logview)) {
									entries.add((String)jtData.getValueAt(rows[i], 1));
						//		}
							} else {
								logview.insertString(logview.getLength(),Common.msgFormat(res.getString("propertynotf"), Utils.getDate() , model, pid, property), null);
							}
						}
					}
				}

				if (entries.size() > 0) {
					if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askaggregate"), new Integer(entries.size()).toString(), model, format), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
						CSwingWorker loader = new CSwingWorker(this, null);
						loader.execute();		
					}
				} else {
					logview.insertString(logview.getLength(),Common.msgFormat(res.getString("objnotf"), Utils.getDate() , model), null);
				}
			}		
			 
		} catch (Exception e) {
			log.error(e);
		}
	}


	
	public void show() throws CShowFailedException {
		try {	
			setTitle(title);
			CBoundSerializer.load(this.getCoreDialog(), null, parent.getCoreDialog().getSize(), false);
		} catch (Exception e) {
		}
	}
	
	protected void opened() throws COpenFailedException {
		
		try {
					    
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			
			queue = new AnnulationStack();
			queue.unset(title);
			
			moGA = (CDefaultGuiAdapter)getGuiAdapter();	
			
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");

			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 		

			JRadioButton jrbKML = ((JRadioButton) getGuiComposite().getWidget("jrbKML"));
			jrbKML.setSelected(true);

		} catch (Exception e) {
			log.error(e);
		}
	}

	public void handlerRemoved(CEventListener aoHandler) {}

	public void setEnable(boolean mode) {
		
		try {
			((JButton)getGuiComposite().getWidget("jbApply")).setText(mode ? res.getString("apply") : res.getString("cancel"));;
		} catch (Exception e) {	
		}

	}

	protected void cleaningUp() {}

	public void close() {
		try {
			queue.set(title);
			CBoundSerializer.save(this.getCoreDialog(), null, null);
			super.close();

		} catch (Exception e) {
			log.error(e);
		}

	}
	
	protected boolean checkXSLT(String pid, String xsl, javax.swing.text.Document logview) {

		xsl = "https://" + hostname + xsl.substring(16);
		String template = xsl.replaceAll("\\.xsl", ".xml").replaceAll("\\.XSL", ".XML");
		HttpResponse response = null;
		boolean isOK = false;
		
		try {
			
			try (CloseableHttpClient httpsclient = SSLConnectionFactory.createTrustSelfSigned()) {
				response = connector.stubGetRequest(httpsclient, xsl, null);
				isOK = response.getStatusLine().getStatusCode() == 200;
				if (!isOK) {
					logview.insertString(logview.getLength(),Common.msgFormat(res.getString("dsnotf"), Utils.getDate() , "XSLT stylesheet "+xsl, pid), null);
				}	
			}
			try (CloseableHttpClient httpsclient = SSLConnectionFactory.createTrustSelfSigned()) {
				response = connector.stubGetRequest(httpsclient, template, null);
				isOK = isOK && response.getStatusLine().getStatusCode() == 200;
				if (response.getStatusLine().getStatusCode() != 200) {
					logview.insertString(logview.getLength(),Common.msgFormat(res.getString("dsnotf"), Utils.getDate() , "Template "+template, pid), null);
				}	
			}
		} catch (Exception e) {}
		
		return isOK;
		
	}
	
	protected String getSelectedFormat() {
		String selected = null;
		try {
			if (((JRadioButton) getGuiComposite().getWidget("jrbKML")).isSelected()) selected = "KML";
			else if (((JRadioButton) getGuiComposite().getWidget("jrbPELAGIOS")).isSelected()) selected = "PELAGIOS";
			else if (((JRadioButton) getGuiComposite().getWidget("jrbCMIF")).isSelected()) selected = "CMIF";
			else if (((JRadioButton) getGuiComposite().getWidget("jrbCMDI")).isSelected()) selected = "CMDI";
		} catch (Exception e) {}
		
		return selected;
	}


}