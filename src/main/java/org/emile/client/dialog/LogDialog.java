package org.emile.client.dialog;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CDatastreamListSelectionListener;
import org.emile.client.dialog.core.CDefaultMessageDialog;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.gui.table.CSortTableModel;
import org.emile.client.utils.Utils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

public class LogDialog extends CDialog {
		
	private CDefaultGuiAdapter moGA;
	private FedoraConnector connector;
	private ResourceBundle res; 
	private JPopupMenu popup;
	private JComboBox<String> jcbLevel;
	private JTextArea jtaErrorMessage;
	private JTextField jtfUsername;
	private JTable jtData; 
	private String title;	
	
	private static Logger log = Logger.getLogger(LogDialog.class);

	public LogDialog() {}
	
	public String getTitle() {
		return title;
	}
		
	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
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
	
	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}		
	}

	
	public void setup(String title, FedoraConnector connector )  {
		try {
			this.connector = connector;
			this.title = title;
		} catch (Exception e) {
		}
	}
	
	public void refresh() {
		
		String[] columnNames = { "uuid", res.getString("date"), res.getString("username"), "PID", "Log ID", res.getString("logger"), res.getString("level"), res.getString("line"), res.getString("message"), res.getString("exception"),  };	
		try {	
						
			jtData.setShowHorizontalLines(false);
			CSortTableModel dm = new CSortTableModel(connector.stubGetLogInfoAsVector((String)jcbLevel.getSelectedItem(), jtfUsername.getText(), null), columnNames);
			jtData.setModel(dm);
			Utils.hideColumnUUID(jtData);
					
			CBoundSerializer.load(this.getCoreDialog(), jtData, null, false);
			
			Utils.setRowSelection(jtData);
			
			TableColumn column=jtData.getColumnModel().getColumn(9);
			column.setMinWidth(0);
			column.setMaxWidth(0);
			column.setWidth(0);
			column.setPreferredWidth(0);
			column.setResizable(false);
       			
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
			
			moGA = (CDefaultGuiAdapter)getGuiAdapter();	
			new CMouseListener(getCoreDialog(), this, "handleMouseClick");
						
			jtData = (JTable) getGuiComposite().getWidget("jtData");
			new CMouseListener(jtData, this, "handleMouseDoubleClick");
			jtData.getSelectionModel().addListSelectionListener(new DatastreamListSelectionListener());			

			jtaErrorMessage = (JTextArea) getGuiComposite().getWidget("jtaErrorMessage");

			jcbLevel = (JComboBox) getGuiComposite().getWidget("jcbLevel");
			jcbLevel.setSelectedItem("ERROR");
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbLevel"), this, "handleItemListener");

			jtfUsername = (JTextField) getGuiComposite().getWidget("jtfUsername");
			jtfUsername.setText(connector.getCurrentUser());
			jtfUsername.setEnabled(connector.getKeycloakRoles().contains("superuser"));

			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");

			jtfUsername.addKeyListener(new KeyAdapter() {
		        @Override
		        public void keyPressed(KeyEvent e) {
		            if(e.getKeyCode() == KeyEvent.VK_ENTER){
		    			CSwingWorker loader = new CSwingWorker(LogDialog.this, null);
		    			loader.execute();
		            }
		        }
		    });
			
			KeyStroke keyClose = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK); 
			Action performClose = new AbstractAction("Close") {  
			    public void actionPerformed(ActionEvent e) {     
			       close();
			    }
			}; 
			jtData.getActionMap().put("performClose", performClose);
			jtData.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyClose, "performClose"); 

			
		} catch (Exception e) {		
			log.error(e);
		}
			
	}
	
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public void close() {
		try {
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

	class DatastreamListSelectionListener implements ListSelectionListener {
			
		public void valueChanged(ListSelectionEvent lse) {
			
			String text = new String();
			
			try {
				int row = jtData.getSelectedRow();
					
				if (row > -1) {
					text = String.format("%s%s | %s | %s | %s\n%s", 
						(String)jtData.getValueAt(jtData.getSelectedRow(), 3)+(!((String)jtData.getValueAt(jtData.getSelectedRow(), 3)).isEmpty() ? " | ": ""),
						(String)jtData.getValueAt(jtData.getSelectedRow(), 6),
						(String)jtData.getValueAt(jtData.getSelectedRow(), 7),
						(String)jtData.getValueAt(jtData.getSelectedRow(), 5),
						(String)jtData.getValueAt(jtData.getSelectedRow(), 8),
						(String)jtData.getValueAt(jtData.getSelectedRow(), 9) 
					);
				}
				
				jtaErrorMessage.setText(text);
				
			} catch (Exception e) {
			} 
		}
	}
}
