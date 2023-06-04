package org.emile.client.dialog;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CItemListener;
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;
import voodoosoft.jroots.dialog.CDialog;
import voodoosoft.jroots.dialog.CDialogTools;
import voodoosoft.jroots.dialog.COpenFailedException;
import voodoosoft.jroots.dialog.CShowFailedException;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.emile.cirilo.Namespaces;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.business.UOPFactory;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CContextWidgets;
import org.emile.client.dialog.core.CDatastreamListSelectionListener;
import org.emile.client.dialog.core.CDefaultMessageDialog;
import org.emile.client.dialog.core.CDocumentFilter;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CHyperlink;
import org.emile.client.dialog.core.CPropertyStore;
import org.emile.client.dialog.core.ImageViewer;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.dialog.core.XMLEditor;
import org.emile.client.gui.table.CSortTableModel;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.UploadOptions;
import org.jdom.Document;
import org.jdom.Element;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ObjectEditorDialog extends CDialog {
	
	private static Logger log = Logger.getLogger(ObjectEditorDialog.class);
	
	private static int MAX_PIPELINES = 19;
		
	private CDefaultGuiAdapter moGA;
	private FedoraConnector connector;
	private ResourceBundle res; 
	private CPropertyService props;
	private RepositoryDialog parent;
	private JTable jtDatastreams; 
	private CPropertyStore pipelineStore; 
	private String relsStore; 
	private HashMap<String,String> Pipelines; 
	private CContextWidgets cwidgets;
	private JPopupMenu popup;
	private String title;
	private String pid;
    private String model;
    private String location;
    
	private XMLEditor xmleditor;
	private ArrayList<String> XMLEditorMimetypes;
	private ImageViewer imageviewer;
	private ArrayList<String> ImageViewerMimetypes;
	private ArrayList<String> VersionableDatastreams;
	
	private String dsid;
	private String mimetype;
	private String fname;
	
	public ObjectEditorDialog() {}
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public String getTitle() {
		return title;
	}
	
	public void setApply(boolean mode) {
		try {
			JButton jbApply = (JButton) getGuiComposite().getWidget("jbApply");
			jbApply.setEnabled(mode);
		} catch (Exception e) {}
	}

	public JProgressBar getProgressBar() {
		JProgressBar pb = null;
		try {
			pb = ((JProgressBar) getGuiComposite().getWidget("jpbProgessBar"));
		} catch (Exception e) {}
		return pb;
	}

	public String getPid() {
		return pid;
	}
	
	public String getDsid() {
		return (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1);
	}
	
	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}
	
	public void handleApplyButton(ActionEvent ae) {
		try {
			JButton jbApply = (JButton) getGuiComposite().getWidget("jbApply");
			jbApply.setEnabled(false);
			
      		JProgressBar pb = ((JProgressBar) getGuiComposite().getWidget("jpbProgessBar"));	
			pb.setStringPainted(true);
			pb.setString(pid);

			JTabbedPane tpPane = (JTabbedPane) getGuiComposite().getWidget("tpPane");
			if (tpPane.getSelectedIndex() == 0) {
				handleUpdated();
			} else if (tpPane.getSelectedIndex() == 1) {
				saveRelations();
			} else if (tpPane.getSelectedIndex() == 2) {
				savePipelines();
			}	
			jbApply.setEnabled(true);
			
		} catch (Exception e) {}
	}

	public void handleConfigureButton(ActionEvent ae) {
		try {	

			PreferencesDialog dlg = (PreferencesDialog) CServiceProvider.getService(DialogNames.PREFERENCES_DIALOG);
			dlg.setup(this, connector.getHostname(), model);
			dlg.open();
						
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	
	public void handleRunButton(ActionEvent ae) {
		try {
			JComboBox<String> jcbRJobs = (JComboBox<String>) getGuiComposite().getWidget("jcbRJobs");
			if (jcbRJobs.getSelectedIndex() > -1) {		
				CSwingWorker loader = new CSwingWorker(this, new Integer(0));
				loader.execute();	
			}
		} catch (Exception e) {}
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

	public void handleDSAddButton(ActionEvent ae) {
		
		JFileChooser chooser = null;
		
		try {
			dsid = (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1);
			mimetype = (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 2);
					
			if (dsid.toLowerCase().contains("ontology") || dsid.toLowerCase().contains("rdf_triples")) chooser = CFileChooser.get(res.getString("fileupload"), props.getProperty("user", "upload.path"), new String[]{".rdf",".ttl",".xml"}, null, -1);
				else if (mimetype.contains("xml")) chooser = CFileChooser.get(res.getString("fileupload"), props.getProperty("user", "upload.path"), new String[]{".rdf",".xml",".xsl"}, mimetype, -1);
					else chooser = CFileChooser.get(res.getString("fileupload"), props.getProperty("user", "upload.path"), null, mimetype, -1);
			
			if (chooser.showDialog(getCoreDialog(), res.getString("choose")) == JFileChooser.APPROVE_OPTION) {
		
				props.setProperty("user", "upload.path", chooser.getCurrentDirectory().getAbsolutePath());	
				props.saveProperties("user");
			
				byte[] stream = FileUtils.readFileToByteArray(chooser.getSelectedFile());
								
				//Todo: Data stream validation
			
				if (checkPID(pid, dsid, stream)) {			
					fname = chooser.getSelectedFile().getAbsolutePath();
					CSwingWorker loader = new CSwingWorker(this, stream);
					loader.execute();
				}
			}			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("uploadnok"), chooser.getSelectedFile().getAbsolutePath()), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);			
		}
	}
	
	public void handleAdd(byte[] stream) {
				
		try {
			
	    	UploadOptions uop = new UOPFactory(model).getUOP();
			if (mimetype.contains("image/tiff") && uop.isPyramidalTIFF()) mimetype += Common.PYRAMIDAL;
			
			int ret = connector.stubAddDatastream(pid, dsid, stream, mimetype, null);
		
			if (ret == 415) { 
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("nosupmediatype"), fname), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);		
			} else if (ret == 500) { 
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("outofmemmory"), fname), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
			} else { 
			     if (uop != null && Common.PRIMARY_SOURCES.contains(dsid)) connector.stubTriggerUploadWorkflow(pid, null, uop.get(), null, null);
			     viewDS();
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("uploadnok"), fname), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);			
		}
	}

	private boolean checkPID(String pid, String dsid, byte[] stream) {
		
		boolean isOK = true;
		String new_pid = null;
		
		try {
			if (dsid.equals("TEI_SOURCE")) {
				Document tei = XMLUtils.createDocumentFromByteArray(stream);
				new_pid= XMLUtils.getChild("//tei:idno[@type='PID']", tei).getText();
				isOK = pid.equals(new_pid);
			} else if (dsid.equals("LIDO_SOURCE")) {
				Document lido = XMLUtils.createDocumentFromByteArray(stream);
				new_pid= XMLUtils.getChild("//lido:lidoRecID[@lido:type='PID']", lido).getText();
				isOK = pid.equals(new_pid);
			} else if (dsid.equals("MEI_SOURCE")) {
				Document mei = XMLUtils.createDocumentFromByteArray(stream);
				new_pid= XMLUtils.getChild("//mei:fileDesc/mei:pubStmt/mei:identifier[@type='PID']", mei).getText();
				isOK = pid.equals(new_pid);
			} else if (dsid.equals("GML_SOURCE")) {
				Document gml = XMLUtils.createDocumentFromByteArray(stream);
				new_pid= XMLUtils.getChild("//gdas:PID", gml).getText();
				isOK = pid.equals(new_pid);
			} else if (dsid.equals("DESCRIPTION")) {
				Document desc = XMLUtils.createDocumentFromByteArray(stream);
				new_pid= XMLUtils.getChild("//void:Dataset/dc:identifier", desc).getText();
				isOK = pid.equals(new_pid);
			} else if (dsid.equals("STORY")) {
				Document story = XMLUtils.createDocumentFromByteArray(stream);
				new_pid= XMLUtils.getChild("/storymap/pid", story).getText();
				isOK = pid.equals(new_pid);
			}
		} catch (Exception e) {}
		
		if (!isOK) {
			if (JOptionPane.showConfirmDialog(null, Common.msgFormat(res.getString("diffpid"), new_pid), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) isOK = true;
		}
		
		return isOK;
	}
	
	public void handleDSNewButton(ActionEvent ae) {
		try {
			Box jbNewDS = ((Box) getGuiComposite().getWidget("jbNewDS"));
			jbNewDS.setVisible(!jbNewDS.isVisible());
			JTextField jtfDSID = ((JTextField) getGuiComposite().getWidget("jtfDSID"));
			jtfDSID.requestFocus();
		} catch (Exception e) {}
	}

	public void handleDSExportButton(ActionEvent ae) {
		try {
			String dsid = (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1);
			
			final JFileChooser chooser = CFileChooser.get(res.getString("exportdatastream"), props.getProperty("user", "save.path"), null, null, -1);			
			
			if ( chooser.showSaveDialog(this.getCoreDialog()) == JFileChooser.APPROVE_OPTION) {
				 File fp = new File(chooser.getSelectedFile().getAbsolutePath());
				 FileOutputStream fos = new FileOutputStream(fp);
				 IOUtils.write(connector.stubGetDatastream(pid,dsid), fos);
				 props.setProperty("user", "save.path", chooser.getSelectedFile().getParent());
				 props.saveProperties("user");
				 fos.close();
				 JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("savedatastream"), dsid, chooser.getSelectedFile().getAbsolutePath()), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);			
			}

		} catch (Exception e) {e.printStackTrace();}
	}

	public void handleDSVersioningButton(ActionEvent ae) {
		try {
			String dsid = (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1);
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcreateversion"), dsid), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
				if (connector.stubVersionDatastream(pid, dsid) == 200) {;
					CSwingWorker loader = new CSwingWorker(this, null);
					loader.execute();
				} else {
					JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("versionexists")), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);			
				}
			}		
		} catch (Exception e) {}
	}
	
	public void handleDSOKButton(ActionEvent ae) {
		try {
			JTextField jtfDSID = ((JTextField) getGuiComposite().getWidget("jtfDSID"));
			String dsid = jtfDSID.getText();
			Box jbNewDS = ((Box) getGuiComposite().getWidget("jbNewDS"));
			String mimetype = (String)((JComboBox) getGuiComposite().getWidget("jcbMimeTypes")).getSelectedItem();
			String datastream = mimetype.contains("xml") ? "<xml/>" : new String();		
			
			for (int i = 0; i < jtDatastreams.getRowCount(); i++) {
				if (dsid.equals((String)jtDatastreams.getValueAt(i, 1))){
					JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("dsidexists"), dsid, pid), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);						
					return;
				};
			}

			connector.stubAddDatastream(pid, jtfDSID.getText(), datastream.getBytes(), mimetype, null);
			jbNewDS.setVisible(false);
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();			
		} catch (Exception e) {}
	}
	
	public void handleDSPurgeButton(ActionEvent ae) {
		try {
			String dsid = (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1);
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askdeleteds"), dsid, pid), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
				if(connector.stubEntombDatastream(pid, dsid) == 200) {
					refresh(null);
				}
			}
		} catch (Exception e) {}
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
		
			JLabel jlXsl = ((JLabel) getGuiComposite().getWidget("jlXsl"+widget));
			XSLDialog dlg = (XSLDialog) CServiceProvider.getService(DialogNames.XSL_DIALOG);
			dlg.setup(parent, model, jlXsl.getText());
			dlg.open();
		
			if (dlg.getLocation() != null) {
				((JTextField) getGuiComposite().getWidget("jtfPipeline"+widget)).setText(dlg.getLocation());
			}
	
		} catch (Exception e) {}		
	}	

	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			setOKButton();
		}
	}
	
	public void validate() {
		try {
			
			JButton jbApply = (JButton) parent.getGuiComposite().getWidget("jbApply");	
			jbApply.setEnabled(!pipelineStore.validate()); 
			
			
		} catch (Exception e) {}
	}
	
	public void setup(String title, RepositoryDialog parent, String model, FedoraConnector connector) {
		try {
			
			this.title = title;
			this.connector = connector;
			this.parent = parent;
			this.pid = StringUtils.substringAfter(title, " - ");
			this.model = StringUtils.substringAfterLast(model, "/");	
			
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	public void refresh(Object object) {
		
		String[] columnNames = { "uuid",  res.getString("dsid"), res.getString("mimetype"), res.getString("sizemb"), res.getString("created"), res.getString("modified")};
		String groups = new String();

		try {
			
			if (this.model.equals("R") && object instanceof Integer) {
				JComboBox<String> jcbRJobs = (JComboBox<String>) getGuiComposite().getWidget("jcbRJobs");
				if (jcbRJobs.getSelectedIndex() > -1) {
					connector.stubComputeRScenario(pid, (String)jcbRJobs.getSelectedItem());
				}
			}
			
			pipelineStore = new CPropertyStore();
			
			jtDatastreams.setShowHorizontalLines(false);

			CSortTableModel dm = new CSortTableModel(connector.stubGetDatastreamListAsVector(pid), columnNames);
			jtDatastreams.setModel(dm);
			Utils.hideColumnUUID(jtDatastreams);

			setOrder();
  
			Pipelines = new HashMap<String,String>();
			for (Element el: XMLUtils.getChildren("//s:result", XMLUtils.createDocumentFromByteArray(connector.stubGetPipelinesAsRDF(pid)))) {
				Pipelines.put(el.getChildText("pipeline", Namespaces.xmlns_sparql2001), el.getChildText("url", Namespaces.xmlns_sparql2001));
			}
		
			setPipelines(parent.getPipelineStore().getIngestPipelines(model),  5, 0);
			setPipelines(parent.getPipelineStore().getDisseminationPipelines(model),  10, 5);
			setPipelines(parent.getPipelineStore().getMetadataPipelines(model),  5, 15);
		
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				groups += el.getText() + ";";
			}

			Utils.setRowSelection(jtDatastreams);
			
			cwidgets = new CContextWidgets(connector, (CDialog)this, groups);	
			
			JList<String> jtRels = (JList<String>) getGuiComposite().getWidget("jtRels");			
			relsStore = new String();
		    for (int i = 0; i< jtRels.getModel().getSize();i++){
		    	relsStore+=StringUtils.substringAfter((String)jtRels.getModel().getElementAt(i), "| ")+";";
		    }			

		    refreshRJobList();
			initPipelineWidgets();
					
						
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void save(String text) {
		
		String dsid = StringUtils.substringBefore(text, "||||");
		String mimetype = StringUtils.substringBefore(StringUtils.substringAfter(text, "||||"), "::::");
		
		try {		
			
			int status = connector.stubModifyDatastream(pid, dsid, StringUtils.substringAfter(text, "::::").getBytes(), mimetype, "WELLFORMED");
		    UploadOptions uop = new UOPFactory(model).getUOP();
		    if (uop != null  && Common.PRIMARY_SOURCES.contains(dsid)) connector.stubTriggerUploadWorkflow(pid, null, uop.get(), null, null);

			getProgressBar().setVisible(false);
			
			if (status == 200) {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("savedsucessfully"), dsid), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
				if (xmleditor != null) xmleditor.resetUpdated();
				refreshRJobList();
			} else {
				JOptionPane.showMessageDialog(null, res.getString("nonvalidxml"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
			};
		} catch (Exception e) {}
	}
	
	public void show() throws CShowFailedException {
		try {
			
			setTitle(title);
			moCore.show();

			CBoundSerializer.load(this.getCoreDialog(), jtDatastreams, parent.getCoreDialog().getSize(), false);
		
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
			CHyperlink JlPath = (CHyperlink) getGuiComposite().getWidget("JlPath");
			
			String path = "https://"+parent.getHostname()+"/"+pid+"/sdef:Object/getrdf";
			JlPath.setURI(new URI(path));
			JlPath.setText("RDF node");
			
			CHyperlink JlContentModel = (CHyperlink) getGuiComposite().getWidget("JlContentModel");
			JlContentModel.setURI(new URI("https://"+parent.getHostname()+"/doc/userguide/rcontentmodels/#"+model.toLowerCase()));
			JlContentModel.setText(model);
	
			((Box) getGuiComposite().getWidget("Location")).setVisible(this.model.contains("OAIRecord") || this.model.contains("Resource"));
			((Box) getGuiComposite().getWidget("Box99")).setVisible(this.model.equals("R"));
			
			location = null;
			
			if (this.model.contains("OAIRecord") || this.model.contains("Resource")) {
				JTextField jtfLocation = ((JTextField) getGuiComposite().getWidget("jtfLocation"));
				location =  connector.stubGetProperty(pid, "edm:isShownAt");
				jtfLocation.setText(location);
			}
			
			validate();
			
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	public void handleMouseDoubleClick(MouseEvent me, int type) {
		try {
			CEventListener.setBlocked(true);
			if (type == MouseEvent.MOUSE_CLICKED) {
				if (me.getClickCount() >= 2) {
					CSwingWorker loader = new CSwingWorker(this, new CDatastreamListSelectionListener());
					loader.execute();
		
					JButton jbDSVersioning = (JButton) parent.getGuiComposite().getWidget("jbDSVersioning");	
		//			jbDSVersioning.setEnabled(VersionableDatastreams.contains(null));
				}
			}	
		} catch (Exception e) {
		} finally {
			CEventListener.setBlocked(false);
		}
	}

	
	public void viewDS() {
		try {

			JPanel panel = (JPanel) getGuiComposite().getWidget("jpDSPanel");
				
			String mimetype = ((String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 2)).trim();			  
		  
			panel.removeAll();

			JButton jbApply = (JButton) getGuiComposite().getWidget("jbApply");
			jbApply.setEnabled(false);

			handleUpdated();

			if (XMLEditorMimetypes.contains(mimetype)) {
			    panel.add(xmleditor.get(pid, (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1), mimetype), BorderLayout.CENTER);
			} else if (ImageViewerMimetypes.contains(mimetype)) {
			    panel.add(imageviewer.get(mimetype, panel.getSize(), pid, (String)jtDatastreams.getValueAt(jtDatastreams.getSelectedRow(), 1)));
			} else {
				panel.add(new JPanel());
			}
 		
		} catch (Exception e) {}

	}

	protected void opened() throws COpenFailedException {
		
		try {
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			moGA = (CDefaultGuiAdapter)getGuiAdapter();	
			
			XMLEditorMimetypes = parent.getPipelineStore().getParameters("TextMimetypes");
			ImageViewerMimetypes = parent.getPipelineStore().getParameters("ImageMimetypes");
			VersionableDatastreams = parent.getPipelineStore().getParameters("VersionableDatastreams");	

			JComboBox jcbMimeTypes = (JComboBox) getGuiComposite().getWidget("jcbMimeTypes");
			for (String s:  parent.getPipelineStore().getParameters("SupportedMimetypes")) {
				jcbMimeTypes.addItem(s);
			}
			
			jtDatastreams = (JTable) getGuiComposite().getWidget("jtDatastreams");
			jtDatastreams.getSelectionModel().addListSelectionListener(new CDatastreamListSelectionListener(this, VersionableDatastreams));			
			new CMouseListener(jtDatastreams, this, "handleMouseDoubleClick");

			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbMimeTypes"), this, "handleItemListener");

			JTextField jtfDSID = (JTextField) getGuiComposite().getWidget("jtfDSID");
			jtfDSID.addKeyListener(
					new KeyAdapter() {
						public void keyTyped(KeyEvent ev) {
								setOKButton();
						}
					});
			
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");
			CDialogTools.createButtonListener(this, "jbRun", "handleRunButton");
			CDialogTools.createButtonListener(this, "jbConfigure", "handleConfigureButton");
		
			JButton jbApply = (JButton) getGuiComposite().getWidget("jbApply");
			jbApply.setEnabled(false);
			
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 		
			
			CDialogTools.createButtonListener(this, "jbRelAdd", "handleRelAddButton");
			CDialogTools.createButtonListener(this, "jbRelRemove", "handleRelRemoveButton");
			CDialogTools.createButtonListener(this, "jbRelFind", "handleRelFindButton");
			CDialogTools.createButtonListener(this, "jbRelFindAdd", "handleRelFindAddButton");
			CDialogTools.createButtonListener(this, "jbRelFindDel", "handleRelFindDelButton");

			CDialogTools.createButtonListener(this, "jbDSAdd", "handleDSAddButton");
			CDialogTools.createButtonListener(this, "jbDSNew", "handleDSNewButton");
			CDialogTools.createButtonListener(this, "jbDSExport", "handleDSExportButton");
			CDialogTools.createButtonListener(this, "jbDSVersioning", "handleDSVersioningButton");
			CDialogTools.createButtonListener(this, "jbDSOK", "handleDSOKButton");
			CDialogTools.createButtonListener(this, "jbDSPurge", "handleDSPurgeButton");
		
			JTabbedPane tpPane = (JTabbedPane) getGuiComposite().getWidget("tpPane");
			tpPane.addChangeListener(new ChangeListener() {
			    public void stateChanged(ChangeEvent e) {
					jbApply.setEnabled(true);
					if (tpPane.getSelectedIndex() == 0 && xmleditor != null) jbApply.setEnabled(xmleditor.isUpdated());
					
				    if (parent != null) jbApply.setEnabled(parent.hasAdminRights() || parent.hasSysopRights());
			    }
			});
			
			JTextField jtfLocation = ((JTextField) getGuiComposite().getWidget("jtfLocation"));
			jtfLocation.addKeyListener(
					new KeyAdapter() {
						public void keyTyped(KeyEvent ev) {
							try {
								JButton jbApply = (JButton) getGuiComposite().getWidget("jbApply");
								jbApply.setEnabled(true);
							} catch (Exception e) {}
						}
					});
			
			xmleditor = new XMLEditor(connector, this);
			imageviewer = new ImageViewer(connector);
			
			createPopupMenu();

		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void close() {
		try {					
			handleUpdated();
            cwidgets.saveterms();
			CBoundSerializer.save(this.getCoreDialog(), jtDatastreams, null);
			super.close();
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	
	public void handleUpdated() {
		Element property;
		try {
			JTextField jtfLocation = ((JTextField) getGuiComposite().getWidget("jtfLocation"));
			if (location != null && !jtfLocation.getText().equals(location)) {
		        property = new Element("isShownAt", Namespaces.xmlns_edm).setAttribute("resource", location, Namespaces.xmlns_rdf);
				connector.stubDelProperty(pid, property);
				location = jtfLocation.getText();			
		        property = new Element("isShownAt", Namespaces.xmlns_edm).setAttribute("resource", location, Namespaces.xmlns_rdf);
		        connector.stubAddProperty(pid, property);
			}
		} catch (Exception e) {}
		
		if (xmleditor != null) {
			if (xmleditor.isUpdated()) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askupdatesave"), xmleditor.getDsid()), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
					xmleditor.handleSave();
					try {
					    UploadOptions uop = new UOPFactory(model).getUOP();
					    if (uop != null && Common.PRIMARY_SOURCES.contains(xmleditor.getDsid())) connector.stubTriggerUploadWorkflow(pid, null, uop.get(), null, null);
					} catch (Exception e) {}

				}
			}
			xmleditor.resetUpdated();
		}		
	}
	
	protected boolean closing() {
		try {
		} catch (Exception e) {		
			log.error(e);
		}
		return true;
	}
	
    private void createPopupMenu() {
	    	
	    JMenuItem item;
	    try {
	       popup = new JPopupMenu();

		   item = new JMenuItem(res.getString("add"));
		   item.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent ae) {
				   handleDSAddButton(ae);
	        	}
	        });
			popup.add(item);
			
			
			item = new JMenuItem(res.getString("new"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					handleDSNewButton(ae);
				}
			});
			popup.add(item);

			item = new JMenuItem(res.getString("export").replaceAll("[.]",  ""));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					handleDSExportButton(ae);
				}
		    });
			popup.add(item);
		
			popup.add(new JSeparator());

			item = new JMenuItem(res.getString("versioning"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					handleDSVersioningButton(ae);
				}
		    });
			popup.add(item);
		
			popup.add(new JSeparator());

			item = new JMenuItem(res.getString("purge"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					handleDSPurgeButton(ae);
				}
		    });
			popup.add(item);
		
			PopupListener popupListener = new PopupListener();
			jtDatastreams.addMouseListener(popupListener);
		
	    } catch (Exception e) {
	    	log.error(e);
	    }
    }
	
	private class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			show(me);
		}
		
		public void mouseReleased(MouseEvent me) {
			show(me);
		}
		
		private void show(MouseEvent me) {
			try {
			 	int row = jtDatastreams.rowAtPoint(me.getPoint());
			 	jtDatastreams.setRowSelectionInterval(row, row);
			 	if (me.isPopupTrigger()) popup.show(me.getComponent(), me.getX(), me.getY());
			} catch (Exception e) {}
		}
	}
	
	private void initPipelineWidgets() {
		try {
			for (int i = 0; i <= MAX_PIPELINES; i++) {
				String num = String.format("%02d", i);
				CDialogTools.createButtonListener(this, "jbChoosePipeline"+num, "handleChoosePipeline"+num);
			}
		} catch (Exception e) {
			log.error(e);
		}
		
	}
	private void setOrder() {
		try {
			TableModel dm = jtDatastreams.getModel();
			jtDatastreams.setAutoCreateRowSorter(true);
			TableRowSorter sorter = new TableRowSorter<TableModel>(dm);
			jtDatastreams.setRowSorter(sorter);
						
			List<RowSorter.SortKey> sortKeys = new ArrayList<>();	
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
			sorter.setSortKeys(sortKeys);
			sorter.sort();
					
		} catch (Exception e) {}
	}
	
	private void setOKButton() {
		try {
			JComboBox jcbMimeTypes = (JComboBox) getGuiComposite().getWidget("jcbMimeTypes");
			JTextField jtfDSID = (JTextField) getGuiComposite().getWidget("jtfDSID");
			JButton jbDSOK = (JButton) getGuiComposite().getWidget("jbDSOK");

			jbDSOK.setEnabled(jcbMimeTypes.getSelectedIndex() > -1 && !(jtfDSID.getText()).isEmpty());	
		} catch (Exception e) {}
	}	
	
	private void setPipelines(ArrayList<String>pipelines, int count, int onset) {
		int xc = 0;
		try {
			CDocumentFilter filter = new CDocumentFilter();
			xc = onset + pipelines.size(); 				
			if (!pipelines.isEmpty()) {
				for (int i = 0; i < pipelines.size() && i < count ; i++) {
					String num = String.format("%02d", onset+i);
					((JLabel) getGuiComposite().getWidget( "jlXsl"+num )).setText(pipelines.get(i));
					String url = Pipelines.get(pipelines.get(i));
					JTextField pipeline = ((JTextField) getGuiComposite().getWidget("jtfPipeline"+num));					
					if (url != null) pipeline.setText(url.replaceAll(Common.APACHE,Common.THIS));
					pipelineStore.add("jtfPipeline"+num, pipelines.get(i), pipeline.getText(), pipeline.getText());
					AbstractDocument doc = ( AbstractDocument) pipeline.getDocument();
					doc.setDocumentFilter(filter);			
				}		
			}
			for (int i = xc; i < onset + count; i++) {
				((Box) getGuiComposite().getWidget( "Box"+String.format("%02d", i) )).setVisible(false);
			}
		} catch (Exception e) {}
	}
	
	private void savePipelines() {
		try {
			for (String key: pipelineStore.getKeyList()) pipelineStore.set(key, ((JTextField) getGuiComposite().getWidget(key)).getText());
			if (!pipelineStore.validate()) {
				for (String key: pipelineStore.getKeyList()) {
					if (!pipelineStore.equals(key)) {
						ArrayList<String> property = pipelineStore.get(key);
						Element el = new Element(StringUtils.substringAfter(property.get(0), ":"), Namespaces.byPrefix.get(StringUtils.substringBefore(property.get(0), ":"))).setAttribute("resource", property.get(1).replaceAll(Common.THIS,Common.APACHE), Namespaces.xmlns_rdf);
						if (!property.get(1).isEmpty()) connector.stubDelProperty(pid, el);
						el.setAttribute("resource", property.get(2).replaceAll(Common.THIS,Common.APACHE), Namespaces.xmlns_rdf);
						if (!property.get(2).isEmpty()) connector.stubAddProperty(pid, el);
						pipelineStore.set(key, property.get(2), property.get(2));
					}
				}
				
				JOptionPane.showMessageDialog(null, res.getString("pipelinessaved"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);

			} else {
				JOptionPane.showMessageDialog(null, res.getString("nopipelinechanged"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (Exception e) {}	
	}

	private void saveRelations() {
		
		String rels = new String();

		try {
							
			JList<String> jtRels = (JList<String>) getGuiComposite().getWidget("jtRels");			
		    for (int i = 0; i< jtRels.getModel().getSize();i++){
		    	rels+=StringUtils.substringAfter((String)jtRels.getModel().getElementAt(i), "| ")+";";
		    }			
		    
		    if (!relsStore.equals(rels)) {
		    	connector.stubDelRelationships(pid, "all");
			    if (!rels.isEmpty()) connector.stubAddRelationships(pid, rels);
				JOptionPane.showMessageDialog(null, res.getString("relssaved"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
		    } else {
				JOptionPane.showMessageDialog(null, res.getString("norelschanged"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
		    }
		    

		} catch (Exception e) {}	
	}
	
	private void refreshRJobList() {

		try {
			if (this.model.equals("R")) {
				JComboBox jcbRJobs = (JComboBox) getGuiComposite().getWidget("jcbRJobs");
				jcbRJobs.removeAllItems();
				Document jobs = XMLUtils.createDocumentFromByteArray(connector.stubGetDatastream(pid, "DATASETS"));
				for(Element job : XMLUtils.getChildren("//dataframe", jobs)) {
					jcbRJobs.addItem(job.getAttributeValue("id", Namespaces.xmlns_xml));
				}	
				jcbRJobs.setSelectedIndex(0);
			}
		} catch (Exception e) {}
	}

}
