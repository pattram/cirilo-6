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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Pattern;

import org.emile.cirilo.Namespaces;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CDatastreamListSelectionListener;
import org.emile.client.dialog.core.CDefaultMessageDialog;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.gui.table.CSortTableModel;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.PrototypeListEntry;
import org.jdom.Element;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

public class UserEnvironmentDialog extends CDialog {
		
	private CDefaultGuiAdapter moGA;
	private RepositoryDialog parent;
	private FedoraConnector connector;
	private CPropertyService props;
	private ResourceBundle res; 
	private JPopupMenu popup;
	private JTable jtData; 
	private String title;	
	
	private static Logger log = Logger.getLogger(UserEnvironmentDialog.class);

	public UserEnvironmentDialog() {}
	
	public String getTitle() {
		return title;
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
					handleEditButton(null);
				}
			}	
		} catch (Exception e) {
		} finally {
			CEventListener.setBlocked(false);
		}
	}
	
	public void handleCloneButton(ActionEvent ae) {
		try {
			UUID prefix = UUID.randomUUID();
			Box jbClonePrototype = ((Box) getGuiComposite().getWidget("jbClonePrototype"));
			jbClonePrototype.setVisible(!jbClonePrototype.isVisible());
			JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));
			jtfTitle.requestFocus();
		} catch (Exception e) {}
	}

	public void handleDelButton(ActionEvent ae) {
		try {
			String pid = (String) jtData.getValueAt(jtData.getSelectedRow(), 1);
			connector.stubEntombObject(pid, false);

		} catch (Exception e) {}
	}
	
	public void handleOKButton(ActionEvent ae) {
		
		CSwingWorker loader = new CSwingWorker(this, new String());
		loader.execute();
		
	}

	public void handleEditButton(ActionEvent ae) {
		try {
			ObjectEditorDialog dlg = (ObjectEditorDialog) CServiceProvider.getService(DialogNames.OBJECTEDITOR_DIALOG);
			dlg.setup(title + " - " + jtData.getValueAt(jtData.getSelectedRow(), 1), parent, (String)jtData.getValueAt(jtData.getSelectedRow(), 3), connector);
			dlg.open();
		} catch (Exception e) {}
	}

	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}


	public void handleRefreshButton(ActionEvent ae) {
		try {
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
		} catch (Exception e) {}
	}

	public void setup(String title, RepositoryDialog parent )  {
		try {
			this.parent = parent;
			this.connector = parent.getFedoraConnector();
			this.title = title;
		} catch (Exception e) {
		}
	}

	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
		}
	}
	
	public void doit(Object mode) {
		
		
		if (mode != null) {
			
			try {
				
				JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
				JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
				JTextField jtfTitle = ((JTextField) getGuiComposite().getWidget("jtfTitle"));	
				String group = (String)jcbGroups.getSelectedItem();
				String prototype = ((String)jcbContentModel.getSelectedItem());
				String title = jtfTitle.getText();
	     		JProgressBar pb = ((JProgressBar) getGuiComposite().getWidget("jpbProgessBar"));	
	
				if (title == null || title.isEmpty()) {
					title = prototype + " prototype object";
				}
				
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcreateprototype"), prototype, group), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
					pb.setVisible(true);
					pb.setStringPainted(true);
					String pid = connector.stubGetPID(group,"o:prototype.g_"+group+"."+prototype.toLowerCase());	
					pb.setString(pid);
					int retval = connector.stubCloneObject("o:prototype."+prototype.toLowerCase(), pid, group);
		
					if (retval == 200) {;
						Element property = new Element("type", Namespaces.xmlns_rdf).setAttribute("resource", "http://cm4f.org/Prototype", Namespaces.xmlns_rdf);
						connector.stubAddProperty(pid, property);
						property = new Element("type", Namespaces.xmlns_rdf).setAttribute("resource", "http://cm4f.org/Object", Namespaces.xmlns_rdf);
						connector.stubDelProperty(pid, property);
			        
						HashMap<String, String> dcmi = Common.DCMI_MAP;
						dcmi.put("Title", title);
						dcmi.put("Language", "en");
						dcmi.put("Identifier", pid);
						dcmi.put("Rights", "http://creativecommons.org/licenses/by-nc-sa/4.0/");
						dcmi.put("Relation", "http://cm4f.org");
						
						connector.stubUpdateDCMI(pid, Utils.getDcmiXML(dcmi));
						
						connector.waitUntilObjectIsAvailable(pid);
						pb.setVisible(false);
						JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("protocreated"), pid), getTitle(), JOptionPane.INFORMATION_MESSAGE);
					} else {
						pb.setVisible(false);
						JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("internalerror"), pid, Common.VALID_PID_REGEX), getTitle(), JOptionPane.ERROR_MESSAGE);									
						return;
					}
				}

			} catch (Exception e) {}
			
		}	
		
		refresh();
		
	}
	
	public void refresh() {
		
		String[] columnNames = { "uuid", res.getString("pid"), res.getString("title"), res.getString("contentmodel") };	
		
		try {	
		
			CBoundSerializer.load(this.getCoreDialog(), jtData, parent.getCoreDialog().getSize(), false);

			Vector data = new Vector();
			
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
	
			for(PrototypeListEntry pt: connector.stubGetPrototypeList((String)jcbGroups.getSelectedItem(), false)) {
 				Vector row = new Vector();
 				row.add(pt.getPid());
 				row.add(pt.getTitle());
 				row.add(pt.getModel());
 				data.add(row);
 			}
 					 
			jtData.setShowHorizontalLines(false);
			CSortTableModel dm = new CSortTableModel(data, columnNames);
			jtData.setModel(dm);
			Utils.hideColumnUUID(jtData);
			setOrder(1);
			      			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	
	public void show() throws CShowFailedException {
		try {
			
			setTitle(title);

			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();

		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void opened() throws COpenFailedException {
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES); 
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			moGA = (CDefaultGuiAdapter)getGuiAdapter();	
			new CMouseListener(getCoreDialog(), this, "handleMouseClick");
						
			jtData = (JTable) getGuiComposite().getWidget("jtData");
			new CMouseListener(jtData, this, "handleMouseDoubleClick");
			jtData.getSelectionModel().addListSelectionListener(new DatastreamListSelectionListener());			
		
			CDialogTools.createButtonListener(this, "jbClone", "handleCloneButton");
			CDialogTools.createButtonListener(this, "jbEdit", "handleEditButton");
			CDialogTools.createButtonListener(this, "jbDel", "handleDelButton");
			CDialogTools.createButtonListener(this, "jbOK", "handleOKButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbRefresh", "handleRefreshButton");

			JButton jbClose = (JButton) getGuiComposite().getWidget("jbClose");
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			        handleCloseButton(e);
			    }
			}; 
			jbClose.getActionMap().put("performClose", performClose);
			jbClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 
			
			
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
			List<String> allGroups = connector.getKeycloakGroups();
			for (String group: allGroups) {
				if(!group.isEmpty()) jcbGroups.addItem(group);
			}
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if (!el.getText().isEmpty() && !allGroups.contains(el.getText()) )jcbGroups.addItem(el.getText());
			}
			
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			for (PrototypeListEntry pt: connector.stubGetPrototypeList(null, true)) {
				jcbContentModel.addItem(StringUtils.substringAfterLast(pt.getModel(), "/"));
 			}
			
			String s = props.getProperty("user", "General.DefaultGroup");
			if (s != null && !s.isEmpty()) jcbGroups.setSelectedItem(s); else jcbGroups.setSelectedIndex(0);
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbGroups"), this, "handleItemListener");

		} catch (Exception e) {		
			log.error(e);
		}
			
	}
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public void close() {
		try {
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
			props.setProperty("user", "General.DefaultGroup", (String)jcbGroups.getSelectedItem());
			props.saveProperties("user");
			CBoundSerializer.save(this.getCoreDialog(), jtData, null);
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

	public void setOrder(int column) {
		try {
			TableModel dm = jtData.getModel();
			jtData.setAutoCreateRowSorter(true);
			TableRowSorter sorter = new TableRowSorter<TableModel>(dm);
			jtData.setRowSorter(sorter);
						
			List<RowSorter.SortKey> sortKeys = new ArrayList<>();	
			sortKeys.add(new RowSorter.SortKey(column, SortOrder.ASCENDING));
			sorter.setSortKeys(sortKeys);
			sorter.sort();
			Utils.setRowSelection(jtData);
			
		} catch (Exception e) {}
	}
	
	class DatastreamListSelectionListener implements ListSelectionListener {
			
		public void valueChanged(ListSelectionEvent lse) {
						
			try {
				int row = jtData.getSelectedRow();
			} catch (Exception e) {}
		}
	}
}
