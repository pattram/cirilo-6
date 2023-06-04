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

import org.emile.cirilo.Namespaces;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.client.oai.ListRecords;
import org.emile.client.utils.AnnulationStack;
import org.emile.cirilo.utils.ImageTools;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.business.EDM;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CSwingWorker;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.emile.client.models.HarvesterTableModel;

import java.awt.Color;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.*;


public class HarvesterDialog extends CDialog {

	private static Logger log = Logger.getLogger(HarvesterDialog.class);
	
	private FedoraConnector connector;
	private ResourceBundle res; 	
	private  JTable jtRepositories;
	private Document metadata;
	private AnnulationStack queue; 
	private String title;
	private XMLOutputter outputter;
    private int entries;

	public HarvesterDialog() { }

    public void setup(RepositoryDialog dlg) {
    	try {
     		this.connector = dlg.getFedoraConnector();
    		title = res.getString("oaiharvester") + StringUtils.substringAfterLast(dlg.getTitle(), "▪ ");
    		setTitle(title);
    	} catch (Exception e) {}
  }
	
			
	public void handleHarvestButton(ActionEvent ae) {
		try {
		    if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askharvest"), (String) jtRepositories.getValueAt(jtRepositories.getSelectedRow(), 2)), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
		    	CSwingWorker loader = new CSwingWorker(this, null);
		    	loader.execute();
		    }
		} catch (Exception e) {		
		}
		
 	}

	public void doIt() {

		try {
			
			JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");	  
			jpbProgessBar.setStringPainted(true);	  
			jpbProgessBar.setString("");
			jpbProgessBar.setVisible(true);
	  
			JButton jbHarvest = (JButton) getGuiComposite().getWidget("jbHarvest");
			jbHarvest.setEnabled(false);
		    
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			jbClose.setText(res.getString("cancel"));
			
		    queue.set(title);
 			
		    entries = 0;
		    
   		    int sel = jtRepositories.getSelectedRow();
   	    		
   		    String baseURL = (String) jtRepositories.getValueAt(sel, 2);
   		    String metadataPrefix = (String) jtRepositories.getValueAt(sel, 3);
   		    	
   		    try {
   		    		
   		    	if (baseURL.startsWith("http")) {
   		    		if (harvest(metadataPrefix, baseURL, ((HarvesterTableModel) jtRepositories.getModel()).getRow(sel)[6])) {
   		    			addItems(((HarvesterTableModel) jtRepositories.getModel()).getRow(sel));
   		    		}	    		
 		    	} else if (baseURL.startsWith("file:///")) {
   		    		if (collect(metadataPrefix, baseURL, ((HarvesterTableModel) jtRepositories.getModel()).getRow(sel)[6])) {
   		    			addItems(((HarvesterTableModel) jtRepositories.getModel()).getRow(sel));
   		    		}	    		
  		    	}    
   		    	
   		    } catch (Exception ex) {}	
 	    
			jpbProgessBar.setVisible(false);
			
			JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("harveststat"), new Integer(entries).toString(), baseURL, metadataPrefix), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
	
			jbHarvest.setEnabled(true);
			jbClose.setText(res.getString("close"));

		} catch (Exception e) { e.printStackTrace();
	    }	
	}	
	
	public void handlerRemoved(CEventListener aoHandler) {
	}

	protected void cleaningUp() {
	}

	
	public void handleCloseButton(ActionEvent ae) {
		try {
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			if (jbClose.getText().equals(res.getString("cancel"))) {
				queue.unset(title);
				jbClose.setText(res.getString("close"));
			} else {
				close();
			}
		} catch (Exception e) {}
	}

	public void show() throws CShowFailedException {
        
		String[] names = {res.getString("provider"),res.getString("updated"),res.getString("baseurl"), res.getString("prefix"), res.getString("shownat"), res.getString("cmodel"), "Constraints", "Thumbnail", res.getString("owner")}; 
		HarvesterTableModel dm = new HarvesterTableModel(names);
		List<Element> repositories = null;
     	 
        try {
			
			 queue = new AnnulationStack();
			 queue.unset(title);

          	 Document providers= XMLUtils.createDocumentFromByteArray(connector.stubGetDatastream("o:cirilo.properties", "DATAPROVIDERS"));
		     
          	 CBoundSerializer.load(this.getCoreDialog(), null, null, false);

 	         XPath xpath = XPath.newInstance( "/dataproviders/repository[@state='active']" );    
	         repositories = (List<Element>) xpath.selectNodes( providers );

			 if (repositories != null) {
	    		
		    		 for (Iterator iter = repositories.iterator(); iter.hasNext();) {
		    			 try {	    				 
		    				 Element e = (Element) iter.next();
		    				 String[] row = new String[9]; 
		    				 row[0] = e.getAttributeValue("name");
		    				 row[1] = e.getChild("updated").getText();
		    				 row[2] = e.getChild("serviceprovider").getText();
		    				 row[3] = e.getChild("metadataprefix").getText();
		    				 row[4] = e.getChild("url").getText();
		    				 row[5] = e.getChild("model").getText();
		    				 row[6] = e.getChild("constraints").getText();
		    				 row[7] = e.getChild("thumbnail").getText();
		    				 row[8] = e.getChild("owner").getText();
		    				 dm.add(row);
		    				 
		    			 } catch (Exception ex) {}
		    		 }	  
		    	 }
		 	    
			 	jtRepositories.setModel(dm);
			 	jtRepositories.setRowSelectionInterval(0,0);		         
		           
		} catch (Exception e) {		
			log.error(e);
		} finally {
			 try {
				 JButton jbHarvest = (JButton) getGuiComposite().getWidget("jbHarvest");
				 jbHarvest.setEnabled(dm.getRowCount() > 0);
			 } catch (Exception q) {}
		
		}
	}
	
	protected void opened() throws COpenFailedException {

		try {
	   		res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
			CDialogTools.createButtonListener(this, "jbHarvest", "handleHarvestButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
		   
			jtRepositories = (JTable) getGuiComposite().getWidget("jtRepositories");

			Format format = Format.getPrettyFormat().setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			
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

	private boolean harvest(String metadataPrefix, String baseURL, String constraints) {
		
		try {	
			
			JLabel message = (JLabel) getGuiComposite().getWidget("message");	  
			Element oai_pmh = new Element("OAI-PMH");
			metadata = new Document().addContent(oai_pmh);
			List records = null;
			
			ListRecords listRecords = new ListRecords(baseURL, null, null, null, metadataPrefix);
			String resumptionToken = null;
            int i = 0;

            do {
	        	
	        	message.setText(Common.msgFormat(res.getString("fetching"), baseURL) + (resumptionToken != null ? ". " + Common.msgFormat(res.getString("resumption"), new Integer(i).toString(), resumptionToken) : "") );
	        	
				NodeList errors = listRecords.getErrors();
				if (errors != null && errors.getLength() > 0) return false;
				
				if (!queue.get(title)) break;

				Document pass = XMLUtils.createDocumentFromByteArray(listRecords.toString().getBytes());
	
				records = XMLUtils.getChildren("//oai:record"+(!constraints.trim().isEmpty() ? "["+constraints+"]" : ""), pass);
							
				if (records.size() > 0) {
					for (Iterator iter = records.iterator(); iter.hasNext();) {
						Element em = (Element) iter.next();
						oai_pmh.addContent((Element) em.clone());
					}			
				}

				
        		resumptionToken = listRecords.getResumptionToken();
        		
	        	if (!resumptionToken.isEmpty()) {
	        		listRecords = new ListRecords(baseURL, resumptionToken);
		        	i++;		        	
	        	} else {
	        		break;
	        	}
	      
	        } while (true);
            
	        message.setText("");
	        
	        return true;
			
		} catch (Exception e) {
			try {
				log.error(e.getLocalizedMessage(),e);
			} catch (Exception q) {} 	
		}
		
   	    return false;
	}
	
	private boolean collect(String metadataPrefix, String baseURL, String constraints)  {
		try {
			metadata = XMLUtils.createDocumentFromFile(new File(baseURL.substring(8)));
			return true;
		} catch (Exception e) {
			return false;
		}	
	}

	private void addItems(String[] par)  {
			 
		String name = par[0];
		String updated = par[1];
		String serviceprovider = par[2];
		String metadataprefix = par[3];
		String url = par[4];
		String model = par[5];
		String icon = par[7];
		String owner = par[8];

		EDM EDM_Factory = null;
		
		try {
			
			 if (!queue.get(title)) return;
			 
			 JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");	  

		     List records = XMLUtils.getChildren("//oai:record", metadata);
	    	 
		     HashMap<String,String> params = new HashMap<String,String>();
		     params.put("hostname"	, "https://" +connector.getHostname());
		     EDM_Factory = new EDM(connector);
		     
			 for (Iterator iter = records.iterator(); iter.hasNext();) {

				if (!queue.get(title)) break;
				
				Element obj = (Element) iter.next();
				
				String pid =  "o:oai."+  (obj.getChild("header", Namespaces.xmlns_oai).getChildText("identifier",	Namespaces.xmlns_oai)
					    .replaceAll("info:fedora/oai:", "")
						.replaceAll("oai[:]", "") 
						.replaceAll("o:", "")
						.replaceAll("[/:]", ".")
						.replaceAll("hdl[:.]", ""));

				jpbProgessBar.setString(pid);
				
				try {
					connector.stubCloneObject(model, pid, owner);	
					connector.stubModifyDatastream(pid, "RECORD", outputter.outputString(obj).getBytes(), "text/xml", null);	
	//				byte[] rec = XMLUtils.transform(obj, connector.stubGetProperty(pid, "xsl:hasXsltToEDM").replaceAll(Common.APACHE, "https://"+connector.getHostname()+"/"), params);
					System.out.println(outputter.outputString(obj));
					
		//			byte[] rec = XMLUtils.transform(obj, "/Users/yoda/tmp/oai2edm.xsl", params);
		//			EDM_Factory.set(XMLUtils.createDocumentFromByteArray(rec));
					connector.stubModifyDatastream(pid, "EDM_STREAM", EDM_Factory.get(), "text/xml", null);	

					String iconref = null;	
				
					if (icon.startsWith("use:")) {
						iconref = icon.substring(4);
					} else {
						List<Element> lel  = XMLUtils.getChildren(icon, obj);
						if (!lel.isEmpty()) { 
							iconref = lel.get(0).getText();
						} else {
							List<Attribute> lat = XMLUtils.getAttributes(icon, obj);
							if (!lat.isEmpty()) iconref = lat.get(0).getValue();
						}
					}

					if (iconref != null) {
						File thumbnail = File.createTempFile( "temp", ".tmp" );			    
						FileUtils.copyURLToFile(new URL(iconref), thumbnail);		
						connector.stubModifyDatastream(pid, "THUMBNAIL", ImageTools.createThumbnail(thumbnail, 300, 240, Color.lightGray ), "image/jpeg", null);
						thumbnail.delete();
					}
					
					entries++;
					
				} catch (Exception q) {
				}		
			 }
			
		} catch (Exception e) {
		} finally {
			EDM_Factory.save();
		}
	}
}

