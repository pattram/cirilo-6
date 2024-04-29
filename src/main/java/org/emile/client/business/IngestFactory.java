package org.emile.client.business;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.emile.cirilo.Common;
import org.emile.cirilo.Namespaces;
import org.emile.cirilo.business.RDFProxy;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.CollectFiles;
import org.emile.cirilo.utils.CollectOntologies;
import org.emile.cirilo.utils.ListDirectories;
import org.emile.cirilo.utils.LogSet;
import org.emile.cirilo.utils.CollectDirectories;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.IngestObjectDialog;
import org.emile.client.utils.AnnulationStack;
import org.emile.cm4f.models.UploadOptions;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import voodoosoft.jroots.core.CServiceProvider;

public class IngestFactory {
	
	private static Logger log = Logger.getLogger(IngestFactory.class);

	private ArrayList<String> dispatches = new ArrayList<String>();
	private ResourceBundle res;
	private FedoraConnector connector;
	private IngestObjectDialog parent;
	private UploadOptions options;
	private AnnulationStack queue; 
	private String key;
	private javax.swing.text.Document logview;
	private LogSet ls;
	private int updated = 0;
	private int created = 0;
	private int err = 0;
	
	private SAXBuilder builder = new SAXBuilder();

	
	public IngestFactory(IngestObjectDialog parent, FedoraConnector connector, String key, String options) {
		try {
			this.connector = connector;
			this.parent = parent;
			this.options = new UploadOptions(options);
			this.key = key;
			this.logview = null;
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		
			dispatches.add(res.getString("createobjok"));
			dispatches.add(res.getString("updateobjok"));
			dispatches.add(res.getString("errcreateobj"));
			dispatches.add(res.getString("errupdateobj"));
			dispatches.add(res.getString("errnopid"));
			dispatches.add(res.getString("errisxsl"));
			dispatches.add(res.getString("errnotvalid"));
			dispatches.add(res.getString("errentombed"));		
			dispatches.add(res.getString("cloneobjok"));		

			queue = new AnnulationStack();
			queue.unset(key);
		} catch (Exception e) {}
		
	}
	
	public String run(JEditorPane jLogView, JProgressBar jpbProgessBar, String group, String path, String exts, String prototype, String contextprototype, boolean strict, boolean verbose) {
		
		String pid = null;
		String retval;
		
		try {

			if (jLogView instanceof JEditorPane) {
				jLogView.setText("");
				logview = jLogView.getDocument();
			}
	
			created = 0; updated = 0; err = 0;
			
//			queue.unset(key);
			
  			
			if ( prototype.contains(".tei") || prototype.contains(".lido") ||
				 prototype.contains(".mets") || prototype.contains(".gml") ||
				 prototype.contains(".mei") ) {
				 for (File dir: new CollectDirectories(path).getDirectories()) {
					 CollectFiles cf = new CollectFiles(dir.toString(), exts);
					 ArrayList<File> files = cf.getFiles();		
					 for (File file: files) {
						 ls = null;
						 try {
							 pid  = connector.stubCheckPID(prototype, file, group, strict);
							 if (logLocal(pid, file)) continue;
							 if (queue.get(key)) break;
							 if (jpbProgessBar instanceof JProgressBar) jpbProgessBar.setString(pid);
							 ingest(pid, prototype, contextprototype, group, IOUtils.toByteArray(new FileInputStream(file)), StringUtils.substringBeforeLast(file.getAbsolutePath(), File.separator), null);
						 } catch (Exception e) {}
				 	}
				 }	
			} else if (prototype.contains(".cube") || prototype.contains(".rti") ) {
				CollectDirectories cf = new CollectDirectories(path);
				ArrayList<File> directories = cf.getDirectories();	
				for (File file: directories) {	
					ls = null;
					File lido = Common.exist(file.getAbsolutePath() + File.separator + "LIDO_SOURCE.xml");
			        if (lido != null) {		
						try {
							pid  = connector.stubCheckPID(prototype, lido, group, strict);
							if (logLocal (pid, lido)) continue;
							if (queue.get(key)) break;
							if (jpbProgessBar instanceof JProgressBar) jpbProgessBar.setString(pid);
							ingest(pid, prototype, contextprototype, group, IOUtils.toByteArray(new FileInputStream(lido)), file.getAbsolutePath(), null);
						} catch (Exception e) {}					
					}	
				}				
			} else if (prototype.contains(".spectral")) {					
				ListDirectories ld = new ListDirectories(path);	
				for (String dir: ld.getDirectories()) {	
					ls = null;
					File fp = Common.exist(dir + File.separator + "METS_SOURCE.xml");
			        if (fp != null) {		
						try {
							pid  = connector.stubCheckPID(prototype, fp, group, strict);
							if (logLocal(pid, fp)) continue;
							if (jpbProgessBar != null && queue.get(key)) break;
							if (jpbProgessBar instanceof JProgressBar) jpbProgessBar.setString(pid);
							
							if (jpbProgessBar == null) { 
								if (connector.stubExist(pid, null) == 200) {
									log.info("Update object <" + pid + "> from data in directory " + dir);
								} else {
									log.info("Create object <" + pid + "> from data in directory " + dir);
								}
							}
							
							ingest(pid, (jpbProgessBar == null && verbose ? "batch|" : "" ) + prototype, contextprototype, group, null, dir, fp);	
							
							if (jpbProgessBar == null) { 
								log.info("------ done");
							}
						} catch (Exception e) {}
					}	
				}
			} else if (prototype.contains(".rdo")) {			
				CollectDirectories cf = new CollectDirectories(path);
				for (File dir: cf.getDirectories()) {	
					ls = null;
					File desc = cf.getDescription(dir);
					if (desc != null) {
						try {
							pid  = connector.stubCheckPID(prototype, desc, group, strict);
							if (logLocal (pid, desc)) continue;
							if (queue.get(key)) break;
							if (jpbProgessBar instanceof JProgressBar) jpbProgessBar.setString(pid);
							
							Document rdf = XMLUtils.createDocumentFromFile(desc);	
							ingest(pid, prototype, contextprototype, group, XMLUtils.toByteArray(rdf), dir.getAbsolutePath(), null);
						} catch (Exception e) {}
					}	
				}
								
			} else if (prototype.contains(".skos") || prototype.contains(".rdf") || prototype.contains(".ontology")) {	
				if (jpbProgessBar instanceof JProgressBar) jpbProgessBar.setString(res.getString("collecting"));
				FileInputStream fis;
				for (File dir: new CollectDirectories(path).getDirectories()) {	
					ls = null;
					if (queue.get(key)) break;
					CollectOntologies onto = new CollectOntologies(connector, strict);
					HashMap<String, ArrayList<File>> ontologies = onto.get(new CollectFiles(dir.toString(), exts).getFiles(), group);
					onto = null;
					for (HashMap.Entry<String, ArrayList<File>> entry: ontologies.entrySet()) {
						pid = entry.getKey();
						if (jpbProgessBar instanceof JProgressBar) jpbProgessBar.setString(pid);
						Document doc = null;
						for (File file: entry.getValue()) {		
							Document rdf = null;
							try {
				            	fis = new FileInputStream(file);     	
				            	rdf = builder.build(fis);
				            	fis.close();
				            } catch (Exception q) {
				            	rdf = XMLUtils.createDocumentFromByteArray(RDFProxy.toXML(file));
				            }
							if (entry.getValue().size() > 1 && XMLUtils.getChild("//*[contains(cm4f:useThisMetadataForObject,'true')]", rdf) == null) {
								Element dataset = XMLUtils.getChild("//rdf:Description[contains(rdf:type/@rdf:resource,'http://rdfs.org/ns/void#Dataset')]", rdf);
								if (dataset == null) dataset = XMLUtils.getChild("//void:Dataset", rdf);
								rdf.getRootElement().removeContent(dataset);
							}
				            if (doc == null) { doc = rdf;
				            } else { 
								for (Element child: XMLUtils.getChildren("/rdf:RDF/*", rdf)) {
									doc.getRootElement().addContent((Content)child.clone());
								}		
				            }
						}
						ingest(pid, prototype, contextprototype, group, XMLUtils.toByteArray(doc), dir.getAbsolutePath(), null);
					}
				}
			}
				
		} catch (Exception e) {e.printStackTrace();}
			
		retval = new Integer(err).toString() + ":" + new Integer(created).toString() + ":" + new Integer(updated).toString();

		return retval;	
		
	}

	private void ingest(String pid, String prototype, String contextprototype, String group, byte[] stream, String filename, File fp) {
	
		int logm = 0;
		
		String batch = prototype.contains("|") ? StringUtils.substringBefore(prototype, "|") +"|" : "";
		prototype = prototype.contains("|") ? StringUtils.substringAfter(prototype, "|") : prototype;
	
		try {	
			int retcode = connector.stubExist(pid, null);
			if (retcode == 404) {
				if (connector.stubCloneObject(prototype, pid, group) == 200) {
					created++;
					logm = 0;
				} else {
					logm = 2;
					err++;
				}
			} else if (retcode == 410) {
				err++;
				logm = 7;
			} else {
				updated++;
				logm = 1;
			}
			if (logm < 2) {
				if (fp != null ) {
					Document mets = XMLUtils.createDocumentFromFile(fp);	
					stream = XMLUtils.toByteArray(connector.enrichSpectralMETS(filename, connector.getHostname(), batch+pid, options, mets));
				}	
				String buf = connector.stubTriggerUploadWorkflowWithLogProtocol(pid,
						stream, options.get(), contextprototype,
						filename);
				if (buf == null) {
					logm = 2;
					err++;
				} else {
					ls = new LogSet(buf);
					if (!ls.is(5)) {
						updateDC(pid);
					} else {
						logm=8;
					}
				}
			}
			logHost(dispatches.get(logm), pid, filename, prototype);
		} catch (Exception e) {}
	}
	
	private boolean logLocal (String pid, File file) {
		try {
			if (pid == null) {
				if (logview != null) logview.insertString(logview.getLength(), new MessageFormat(dispatches.get(6)).format(new String[]{Common.getDate(), pid, file.getAbsolutePath()}), null);
				err++;
				return true;
			}
			if (pid.equals("nop")) {
				if (logview != null) logview.insertString(logview.getLength(), new MessageFormat(dispatches.get(4)).format(new String[]{Common.getDate(), pid, file.getAbsolutePath()}), null);
				err++;
				return true;
			}
			if (pid.equals("isxsl")) {
				if (logview != null) logview.insertString(logview.getLength(), new MessageFormat(dispatches.get(5)).format(new String[]{Common.getDate(), pid, file.getAbsolutePath()}), null);
				err++;
				return true;
			}
		} catch (Exception e) {}
		
		return false;
	}

	private void logHost(String dispatch, String pid, String filename, String prototype) {
		 try {
			 
			 if (logview != null) { 
				 logview.insertString(logview.getLength(), new MessageFormat(dispatch).format(new String[]{Common.getDate(), pid, filename, prototype}), null);
				 if (ls != null) {
					 for (int i = ls.nextSetBit(0); i >= 0; i = ls.nextSetBit(i+1))
						 logview.insertString(logview.getLength(),  new MessageFormat("{0} {1}\n").format(new String[]{Common.getDate(), ls.get(i)}),  null);
				 }
			 }
		 } catch (Exception e) {}
	}
	
	private void updateDC(String pid) {
		try {
			if (!options.isDCify()) {
				Element oai_dc = new Element("dc", Namespaces.xmlns_oai_dc);
				oai_dc.addNamespaceDeclaration(Namespaces.xmlns_oai_dc);
				oai_dc.addNamespaceDeclaration(Namespaces.xmlns_dc);
				oai_dc.addNamespaceDeclaration(Namespaces.xmlns_dcterms);
				oai_dc.addNamespaceDeclaration(Namespaces.xmlns_dcmitype);
				for (String dc : org.emile.client.Common.DCMI) {
					JTextField tf = ((JTextField) parent.getGuiComposite().getWidget("jtf" + dc));  			
					if (!tf.getText().isEmpty()) {
						Element dcmi = new Element(dc.toLowerCase(), Namespaces.xmlns_dc);
						dcmi.setText(StringUtils.substringAfterLast("#"+tf.getText(), "#"));
						oai_dc.addContent(dcmi);
					} else if (dc.equals("Title")){
						Element dcmi = new Element("title", Namespaces.xmlns_dc);
						dcmi.setText("Unknown");
						oai_dc.addContent(dcmi);
					}
				}
				Element dcmi = new Element("identifier", Namespaces.xmlns_dc);
				dcmi.setText(pid);
				oai_dc.addContent(dcmi);
				connector.stubModifyDatastream(pid, "DC", XMLUtils.toByteArray(oai_dc), "text/xml", null);				
			}
		} catch (Exception e) {}
	}
}
