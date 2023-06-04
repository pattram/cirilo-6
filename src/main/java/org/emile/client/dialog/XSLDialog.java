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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emile.cirilo.exceptions.FedoraException;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.SSLConnectionFactory;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.gui.table.CSortTableModel;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.PrototypeListEntry;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

public class XSLDialog extends CDialog {
		
	private FedoraConnector connector;
	private ResourceBundle res; 
	private CPropertyService props;
	private RepositoryDialog parent;
	private boolean isChanged;
	private JTable jtData; 
	private String title;	
	private String model;	
	private String category;
	private String defaulttype;
	private String location;
	private HashMap<UUID, Object[]> allPipelines;
	private String[] columnNames;
	private String pid;
	
	private CItemListener ModelItemListener;
	private CItemListener CategoryItemListener;
	private CItemListener GroupItemListener;
	
	private static Logger log = Logger.getLogger(XSLDialog.class);

	private static String SYSTEM_PATH  = "^"+Common.THIS+"((\\/\\w+)([\\w\\-\\.])*\\w+)+$";
	private static String PROJECT_PATH = "^("+Common.THIS+"/)(o:\\w+[\\w\\-\\.]*/)(\\w+[\\w\\-\\.]*\\w+)(::(((\\/[\\w^ ]+)+\\/?([\\w\\-\\.])+\\w)|([a-zA-Z]:[\\\\]([\\w\\-\\.]+\\w[\\\\])*(\\w+[\\w\\-\\.]+\\w))))*$";
	private static String DSNAME = "^("+Common.THIS+"/)(o:\\w+[\\w\\-\\.]*)/(\\w+[\\w\\-\\.])$";
	private static String ISPROJECT = ".*/o:.*pipelines/.*";
	
	public XSLDialog() {

		this.model = null;
		this.category = null;
	}
	
	public String getLocation() {
		return location.replaceAll(Common.THIS+"/", "");
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setup(RepositoryDialog parent, String model, String category) {
			
		this.model = model;
		this.category = category;
		this.parent = parent;	
		this.connector = this.parent.getFedoraConnector();
		   
	}
	
	public void handleCloseButton(ActionEvent ae) {
		try {
			location = null;
			close();
		} catch (Exception e) {}
	}
	
	public void handleApplyButton(ActionEvent ae) {
		try {
			location = (String)jtData.getValueAt(jtData.getSelectedRow(), 5);
			category = (String)jtData.getValueAt(jtData.getSelectedRow(), 3);
			close();
		} catch (Exception e) {}
	}

	public void handleAddButton(ActionEvent ae) {
		try {
			Box jbManage = ((Box) getGuiComposite().getWidget("jbManage"));
			jbManage.setVisible(!jbManage.isVisible());
		} catch (Exception e) {}
	}
	
	public void handleDelButton(ActionEvent ae) {
		try {
			((Box) getGuiComposite().getWidget( "jbManage" )).setVisible(false);
			
			CSortTableModel dm = (CSortTableModel)jtData.getModel();
			UUID row = ((UUID)jtData.getValueAt(jtData.getSelectedRow(), 0)); 

			if (!((String)jtData.getValueAt(jtData.getSelectedRow(), 2)).equals(defaulttype)) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askdelpipeline"), (String)jtData.getValueAt(jtData.getSelectedRow(), 3), (String)jtData.getValueAt(jtData.getSelectedRow(), 1) ), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					allPipelines.remove(UUID.nameUUIDFromBytes(((String)jtData.getValueAt(jtData.getSelectedRow(), 1)+(String)jtData.getValueAt(jtData.getSelectedRow(), 2)+(String)jtData.getValueAt(jtData.getSelectedRow(), 3)+(String)jtData.getValueAt(jtData.getSelectedRow(), 4)+(String)jtData.getValueAt(jtData.getSelectedRow(), 5)).getBytes()));
					dm.removeRow(row);
					dm.fireTableDataChanged();
					Utils.setRowSelection(jtData);
					isChanged = true;
				}
			} else {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("cantdeldefpipelines")), getTitle(), JOptionPane.WARNING_MESSAGE);
			}
			
			((JButton) getGuiComposite().getWidget("jbApply")).setEnabled(jtData.getRowCount() > 0);;
			((JButton) getGuiComposite().getWidget("jbDel")).setEnabled(jtData.getRowCount() > 0);;
		
		} catch (Exception e) {}
	}
	
	public void handleLoadStylesheet(ActionEvent ae) {
		try {
			JFileChooser chooser = CFileChooser.get(res.getString("choosepipeline"), props.getProperty("user", "XSLDialog.Pipeline"), new String[]{".xsl",".xml"}, null, -1);
			
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {	
				
				JTextField jtfLocation = (JTextField) getGuiComposite().getWidget( "jtfLocation" );
				
				String location = jtfLocation.getText();
				 
				if (location.contains("::")) {
					location =  StringUtils.substringBefore(location, "::") + "::"+chooser.getSelectedFile().getAbsolutePath();
				} else {
					location += "::"+chooser.getSelectedFile().getAbsolutePath();
				}
					
				jtfLocation.setText(location);

				jtfLocation.requestFocusInWindow();
	
				props.setProperty("user", "XSLDialog.Pipeline", chooser.getSelectedFile().getAbsolutePath());
				props.saveProperties("user");

			}
			
				
		} catch (Exception e) {}
	}

	public void handleOKButton(ActionEvent ae) {
		Matcher matcher;
		String filename;
		String pid;
		String dsid;
		boolean valid;
		
		try {	
			
			JComboBox<String>jcbModel = (JComboBox<String>) getGuiComposite().getWidget( "jcbModel" );
			JComboBox<String>jcbCategory = (JComboBox<String>) getGuiComposite().getWidget( "jcbCategory" );
			JTextField jtfTitle = (JTextField) getGuiComposite().getWidget( "jtfTitle" );
			JTextField jtfLocation = (JTextField) getGuiComposite().getWidget( "jtfLocation" );
			JComboBox<String>jcbGroups = (JComboBox<String>) getGuiComposite().getWidget( "jcbGroups" );
					
			String location = jtfLocation.getText();	
			String group = (String)jcbGroups.getSelectedItem();	
					
			valid = Pattern.compile(SYSTEM_PATH).matcher(location).find();
			
			connector = parent.getFedoraConnector();
			
			if (!valid) {
				matcher = Pattern.compile(DSNAME).matcher(location);
				valid = matcher.find();
				if (valid) {
					if (connector.stubExist(matcher.group(2), matcher.group(3)) != 200) {
						JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("pipelinenotfound"), matcher.group(3), matcher.group(2)), getTitle(), JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else {
					matcher = Pattern.compile(PROJECT_PATH).matcher(location);
					valid = matcher.find();
					if (valid) {
						location = matcher.group(1)+matcher.group(2)+matcher.group(3);
						dsid = matcher.group(3);
						if (matcher.group(4) != null) {
							filename = matcher.group(4).substring(2);
							pid = "o:"+group+".pipelines";
							if (connector.stubExist(pid, dsid) == 200) {
								if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askpipelineexists"), dsid, pid), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0) {
									return;
								}
							}
							try {
								InputStream is = new FileInputStream(new File(filename));
								byte[] datastream = IOUtils.toByteArray(is);
								if (connector.stubAddDatastream(pid, dsid, datastream, "text/xml", "WELLFORMED") != 200) {
									JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("novalidxml"), filename), getTitle(), JOptionPane.WARNING_MESSAGE);				
									is.close();
									return;
								};
								is.close();
								JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("savedsucessfully"), dsid), getTitle(), JOptionPane.INFORMATION_MESSAGE);				
							} catch (Exception e ) {
								log.error(e);
							} 
						}
					}
				}
			} else {
				String cmd = location.replaceAll(Common.THIS,  "https://"+parent.getHostname());
			    try {	
			        try (CloseableHttpClient httpclient = SSLConnectionFactory.createTrustSelfSigned()) {
			           HttpUriRequest request = RequestBuilder.get(cmd).build();
			           CloseableHttpResponse response = httpclient.execute(request);
			           if (response.getStatusLine().getStatusCode() != 200) throw new Exception();
				    }	
				} catch (Exception e) {	
					JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("locationnotfound"), cmd), getTitle(), JOptionPane.WARNING_MESSAGE);	
					return;
				}
			}
						
			if (valid){
				CSortTableModel dm = (CSortTableModel) jtData.getModel();
				String key = (String)jcbModel.getSelectedItem()+(String)jcbGroups.getSelectedItem()+(String)jcbCategory.getSelectedItem()+location;
				String[] entry = { (String)jcbGroups.getSelectedItem(), (String)jcbModel.getSelectedItem(), (String)jcbCategory.getSelectedItem(), jtfTitle.getText(), location };
				if (allPipelines.get(UUID.nameUUIDFromBytes(key.getBytes())) == null) {
					if (dm.getRowCount() == 1 && ((String)dm.getValueAt(0,2)).isEmpty()) dm.removeRow((UUID)dm.getValueAt(0,0));
					dm.addRow(entry);
					allPipelines.put(UUID.nameUUIDFromBytes(key.getBytes()), entry);
					dm.fireTableDataChanged();
					isChanged = true;
					Utils.setRowSelection(jtData);
					savePipelines();
				} else if (Pattern.compile(SYSTEM_PATH).matcher(location).find()) {
					JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("entryexists"), location), getTitle(), JOptionPane.WARNING_MESSAGE);				
				}
				((JButton) getGuiComposite().getWidget("jbApply")).setEnabled(jtData.getRowCount() > 0);;
				((JButton) getGuiComposite().getWidget("jbDel")).setEnabled(jtData.getRowCount() > 0);;
			} else {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("entrynotvalid")), getTitle(), JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleMouseClick(MouseEvent me, int type) {
		try {
			CEventListener.setBlocked(true);
			if (type == MouseEvent.MOUSE_CLICKED) {
				if (me.getClickCount() >= 1) {
				}
			}	
		} catch (Exception e) {
		} finally {
			CEventListener.setBlocked(false);
		}
	}

	public void handleMouseDoubleClick(MouseEvent me, int type) {
		try {
			CEventListener.setBlocked(true);
			if (type == MouseEvent.MOUSE_CLICKED) {
				if (me.getClickCount() >= 2) {
				}
			}	
		} catch (Exception e) {
		} finally {
			CEventListener.setBlocked(false);
		}
	}
	
	public void handleModelItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			JComboBox jcbModel = (JComboBox) getGuiComposite().getWidget("jcbModel");
			JComboBox jcbCategory = (JComboBox) getGuiComposite().getWidget("jcbCategory");
			if (jcbModel.getSelectedIndex() > -1) {
				jcbCategory.removeAllItems();
				ArrayList<String> AllObjects = new ArrayList<String>();
				for (String p :  parent.getPipelineStore().getAll((String)jcbModel.getSelectedItem()) ) {
					jcbCategory.addItem(p);
				}
				generateDSName();
			}			
		}
	}
	
	public void handleCategoryItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			generateDSName();	
		}
	}
	
	public void handleGroupItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			if (isChanged) {
				savePipelines();
			}
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
		}
	}
	
	public void refresh() {
		
		InputStream is = null;
		
		try {	

			ModelItemListener.setBlocked(true);
			CategoryItemListener.setBlocked(true);
			GroupItemListener.setBlocked(true);	

			Vector<Vector> data = new Vector();
						
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
            String group = (String) jcbGroups.getSelectedItem(); 
			
            isChanged = false;
            
            pid = "o:"+group+".pipelines";
            
			if (connector.stubExist(pid, null) != 200) {
				connector.stubCloneObject("o:prototype.envelope", pid, group);
			}
		
			String xmlpath = "//Pipeline["+
					"(@group='"+group+"' or @group='System')"+
					 (model != null ? " and (" : "")+
					 (model != null ? "@model='"+model+"' and "+"@category='"+category+"'" : "") +
					 (model != null ? ")" : "") +
					"]"; 
									
			XPath xpath = XPath.newInstance(xmlpath);
			List pipelines = (List) xpath.selectNodes(parent.getPipelineStore().getDefaultPipelines());
			for (Iterator iter = pipelines.iterator(); iter.hasNext(); ) {
				Element el = (Element) iter.next();
				data.add(new Vector(Arrays.asList(el.getAttributeValue("group"), el.getAttributeValue("model"), el.getAttributeValue("category"), el.getText(), el.getAttributeValue("url") )));
			}
					
			allPipelines = new HashMap<UUID, Object[]>();

			if (connector.stubExist(pid, "PIPELINES") == 200) {
				Document doc = XMLUtils.createDocumentFromByteArray(connector.stubGetDatastream(pid, "PIPELINES"));
				
				if (doc != null) {
					XPath spath = XPath.newInstance("//Pipeline");
					pipelines = (List) spath.selectNodes(doc);
					for (Iterator iter = pipelines.iterator(); iter.hasNext(); ) {
						Element el = (Element) iter.next();
						String key = el.getAttributeValue("group")+el.getAttributeValue("model")+el.getAttributeValue("category")+el.getText()+el.getAttributeValue("url");
						String[] entry = new String[] {el.getAttributeValue("group"),el.getAttributeValue("model"), el.getAttributeValue("category"),el.getText(),el.getAttributeValue("url") };
						allPipelines.put(UUID.nameUUIDFromBytes(key.getBytes()), entry);
						if (model != null) {
							if (!model.equals(entry[1])) continue;
							if (!category.equals(entry[2])) continue;
						}
						data.add(new Vector(Arrays.asList(entry)));
					}
				}
			}

			jtData = (JTable) getGuiComposite().getWidget("jtData");
			CSortTableModel dm = new CSortTableModel(data, columnNames);
			jtData.setModel(dm);			
			Utils.hideColumnUUID(jtData);			
			jtData.setShowHorizontalLines(false);
			setOrder();
			
			boolean isEmpty = ((String)dm.getValueAt(0, 1)).isEmpty();			
			if (jtData.getRowCount() > 0 && !isEmpty) jtData.setRowSelectionInterval(0, 0);


			JComboBox jcbModel = (JComboBox) getGuiComposite().getWidget( "jcbModel" );
			jcbModel.setEnabled(model == null);
			JComboBox jcbCategory = (JComboBox) getGuiComposite().getWidget( "jcbCategory" );
			jcbCategory.setEnabled(model == null);

			if (model != null) jcbModel.setSelectedItem(model); else if( jcbModel.getItemCount() > 0) jcbModel.setSelectedIndex(0);

			jcbCategory.removeAllItems();
			try {
				for (String p : parent.getPipelineStore().getAll((String)jcbModel.getSelectedItem())) {
					jcbCategory.addItem(p);
				}
			} catch (Exception q) {}


			((JButton) getGuiComposite().getWidget("jbApply")).setEnabled(jtData.getRowCount() > 0 && !isEmpty);
			((JButton) getGuiComposite().getWidget("jbDel")).setEnabled(jtData.getRowCount() > 0 && !isEmpty);
	
			CBoundSerializer.load(this.getCoreDialog(), jtData, null, false);
		
			title = res.getString("managepipelines");
			if (model != null) title += (" | " + model);
			if (category != null) title += (" - " + category);
			
			setTitle(title);
			
			if (model != null) jcbModel.setSelectedItem(model); else if( jcbModel.getItemCount() > 0) jcbModel.setSelectedIndex(0);
			if (category != null) jcbCategory.setSelectedItem(category); else if(jcbCategory.getItemCount() > 0)jcbCategory.setSelectedIndex(0);

			generateDSName();
			
			Utils.setRowSelection(jtData);		
			
			if (parent != null) {
				JButton jbAdd = (JButton) getGuiComposite().getWidget("jbAdd");
				jbAdd.setEnabled(parent.hasAdminRights() || parent.hasSysopRights());
				JButton jbDel = (JButton) getGuiComposite().getWidget("jbDel");
				jbDel.setEnabled(jbDel.isEnabled() && (parent.hasAdminRights() || parent.hasSysopRights()));
			}

			
		} catch (Exception e) {
			log.error(e);
		} finally {
			
			ModelItemListener.setBlocked(false);
			CategoryItemListener.setBlocked(false);
			GroupItemListener.setBlocked(false);	
			
			try {
				is.close();
			} catch (Exception e) {}
		}
	}
	
	public void show() throws CShowFailedException {
		try {
			
			
			CBoundSerializer.load(this.getCoreDialog(), null, null, false);

			moCore.show();

			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();

			if (model == null && category == null) {
				((JButton) getGuiComposite().getWidget("jbApply")).setVisible(false);
			}
			
			isChanged = false;
			
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void opened() throws COpenFailedException {
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
	
			defaulttype = res.getString("default");
		
			columnNames = new String[]{
					"uuid", 
					res.getString("group"),
					res.getString("model"),
					res.getString("category"),
					res.getString("description"),
					res.getString("location")
			};
			
			new CMouseListener(getCoreDialog(), this, "handleMouseClick");
						
			jtData = (JTable) getGuiComposite().getWidget("jtData");
			new CMouseListener(jtData, this, "handleMouseDoubleClick");
	
			JComboBox<String> jcbModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbModel"));	
			for (PrototypeListEntry pt: connector.stubGetPrototypeList(null, true)) {
				jcbModel.addItem(StringUtils.substringAfterLast(pt.getModel(), "/"));
 			}

			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if(!el.getText().isEmpty()) jcbGroups.addItem(el.getText());
			}
			String s = props.getProperty("user", "General.DefaultGroup");
			if (s != null && !s.isEmpty()) jcbGroups.setSelectedItem(s); else jcbGroups.setSelectedIndex(0);
			
			ModelItemListener = new CItemListener((JComboBox) getGuiComposite().getWidget("jcbModel"), this, "handleModelItemListener");
			CategoryItemListener = new CItemListener((JComboBox) getGuiComposite().getWidget("jcbCategory"), this, "handleCategoryItemListener");
			GroupItemListener = new CItemListener((JComboBox) getGuiComposite().getWidget("jcbGroups"), this, "handleGroupItemListener");
	
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");
			CDialogTools.createButtonListener(this, "jbAdd", "handleAddButton");
			CDialogTools.createButtonListener(this, "jbDel", "handleDelButton");
			CDialogTools.createButtonListener(this, "jbLoadStylesheet", "handleLoadStylesheet");
			CDialogTools.createButtonListener(this, "jbOK", "handleOKButton");
			
			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 

			JTextField jtfLocation = (JTextField) getGuiComposite().getWidget( "jtfLocation" );
			jtfLocation.addKeyListener( new CTextFieldListener() );
			
						
		} catch (Exception e) {	
			log.error(e);
		}
			
	}
	
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public void close() {
	
		try {
			
			if (isChanged) {
				savePipelines();
			}

			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
			props.setProperty("user", "General.DefaultGroup", (String)jcbGroups.getSelectedItem());
			props.saveProperties("user");

			CBoundSerializer.save(this.getCoreDialog(), jtData, null);
			
		} catch (Exception e) {	
			log.error(e);
		} finally {
			super.close();
		}
	}
	
	protected boolean closing() {
		try {
		} catch (Exception e) {		
			log.error(e);
		}
		return true;
	}
	
	protected void generateDSName() {
		
		try {
			JComboBox jcbModel = (JComboBox) getGuiComposite().getWidget("jcbModel");
			JComboBox jcbCategory = (JComboBox) getGuiComposite().getWidget("jcbCategory");
			JComboBox jcbGroups = (JComboBox) getGuiComposite().getWidget("jcbGroups");
			JTextField jtfLocation = (JTextField) getGuiComposite().getWidget( "jtfLocation" );
					
			String suffix = (String)jcbCategory.getSelectedItem();
			String dsid = (String) jcbModel.getSelectedItem();
			String group = (String) jcbGroups.getSelectedItem();
	
			if (suffix == null) return; 
			
			if (suffix.contains("xsl:hasXsltBibTeX")) {
				if (((String)jcbModel.getSelectedItem()).contains("BibTeX")) {
					suffix =StringUtils.substringAfter(suffix, "xsl:hasXsltBibTeX");
				} else {
					suffix = StringUtils.substringAfter(suffix, "xsl:hasXslt");
					dsid = new String();
				}
			} else if (suffix.contains("xsl:hasXsltFor")) {
				suffix = "_"+StringUtils.substringAfter(suffix, "xsl:hasXsltFor");
			} else if (suffix.contains("xsl:hasXslt")) {
				suffix = StringUtils.substringAfter(suffix, "xsl:hasXslt");
			} else if (suffix.contains("cm4f:hasXslt")) {
				suffix = "_"+StringUtils.substringAfter(suffix, "cm4f:hasXslt");
			} else if (suffix.contains("cm4f:has")) {
				suffix = "_"+StringUtils.substringAfter(suffix, "cm4f:has");
			}
	
			StringUtils.substringAfter((String)jcbCategory.getSelectedItem(), "xsl:hasXslt");
			dsid = (dsid + suffix.replaceAll("To", "2")).toUpperCase();
			String location = jtfLocation.getText();
						
			if (location.contains("::")) {
				location = Common.THIS+"/o:"+group+".pipelines/"+dsid+"::"+StringUtils.substringAfter(location, "::");
			} else {
				location = Common.THIS+"/o:"+group+".pipelines/"+dsid;
			}	
	
			jtfLocation.setText(location);
			((JButton) getGuiComposite().getWidget("jbLoadStylesheet")).setEnabled(true);

		} catch (Exception e) {		
			e.printStackTrace();
			log.error(e);
		}
		
	}
	

	protected void setOrder() {
		try {
			TableModel dm = jtData.getModel();
			TableRowSorter sorter = new TableRowSorter<TableModel>(dm);

			jtData.setRowSorter(sorter);
						
			List<RowSorter.SortKey> sortKeys = new ArrayList<>();	
			sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING ));
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING ));
			sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING ));
			
			sorter.setSortKeys(sortKeys);
			sorter.sort();
		} catch (Exception e) {}
	}
	
	private void savePipelines() {
		
		try {
			
			Document doc = new Document();
			Element pipelines = new Element("Pipelines");
			doc.addContent(pipelines);
				
			for (Map.Entry<UUID, Object[]> entry :allPipelines.entrySet()) {
				Object[] obj = entry.getValue();
				Element pipeline = new Element("Pipeline");
				pipeline.setAttribute("group", (String) obj[0]);
				pipeline.setAttribute("model",  (String) obj[1]);
				pipeline.setAttribute("category",  (String) obj[2]);
				pipeline.setAttribute("url",  (String) obj[4]);
				pipeline.setText( (String) obj[3]);	
				pipelines.addContent(pipeline);
			}    		
			
			connector.stubAddDatastream(pid, "PIPELINES", XMLUtils.toByteArray(doc), "text/xml", null);

		} catch (Exception e) {	
			e.printStackTrace();
			log.error(e);
		}
	}
	
	private class CTextFieldListener implements KeyListener {

		public void keyTyped( KeyEvent ev ) {
			try {
				JTextField jtfLocation = (JTextField) getGuiComposite().getWidget( "jtfLocation" );
				Matcher matcher = Pattern.compile(ISPROJECT).matcher(jtfLocation.getText());		
				((JButton) getGuiComposite().getWidget("jbLoadStylesheet")).setEnabled(matcher.find());
				
				return;
			} catch (Exception e) {}
		}
		
		public void keyPressed( KeyEvent e ) {}
		public void keyReleased( KeyEvent e ) {}
	}

}
