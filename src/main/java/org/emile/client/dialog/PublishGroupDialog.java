package org.emile.client.dialog;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CItemListener;
import voodoosoft.jroots.dialog.CDialog;
import voodoosoft.jroots.dialog.CDialogTools;
import voodoosoft.jroots.dialog.COpenFailedException;
import voodoosoft.jroots.dialog.CShowFailedException;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.Unzipper;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CHint;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.cm4f.models.ObjectListEntry;
import org.emile.cm4f.models.PrototypeListEntry;
import org.jdom.Element;

import net.lingala.zip4j.ZipFile;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class PublishGroupDialog extends CDialog {
		
	private FedoraConnector connector;
	private CPropertyService props;
	private ResourceBundle res; 
	private String key; 
	private String username;

	private javax.swing.text.Document logview;

	private static Logger log = Logger.getLogger(PublishGroupDialog.class);

	public PublishGroupDialog() {}
	
	public void setup(String key, RepositoryDialog parent)  {
		try {
			this.connector = parent.getFedoraConnector();
			this.username = parent.getUsername();
			this.key = key;
		} catch (Exception e) {
		}
	}
	
	public void handleApplyButton(ActionEvent ae) {
		try {

			CSwingWorker loader = new CSwingWorker(this, null);
			loader.execute();
		
		} catch (Exception e) {}
	}


	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}

	public void handleItemListener(ItemEvent e) throws Exception {

		if (e.getStateChange() == 1) {
			JComboBox<String> jcbTargetRepository = ((JComboBox<String>) getGuiComposite().getWidget("jcbTargetRepository"));	
			JButton jbApply = ((JButton) getGuiComposite().getWidget("jbApply"));
			jbApply.setEnabled(jcbTargetRepository.getSelectedIndex() > 0);
		}
	}

	
	public void doIt() {
		int irec = 0;
		try {	
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
			String group = (String)jcbGroups.getSelectedItem();
			JComboBox<String> jcbTargetRepository = ((JComboBox<String>) getGuiComposite().getWidget("jcbTargetRepository"));	
			JCheckBox jcbOnlyAdd = ((JCheckBox) getGuiComposite().getWidget("jcbOnlyAdd"));	
			JCheckBox jcbCpResDir = ((JCheckBox) getGuiComposite().getWidget("jcbCpResDir"));	
			JCheckBox jcbCpPrototypes = ((JCheckBox) getGuiComposite().getWidget("jcbCpPrototypes"));	
			boolean isOnlyAdd = !jcbOnlyAdd.isSelected();
			String onlyadd = jcbOnlyAdd.isSelected() ? res.getString("updateexisting") : "";
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askrepupdate"), (String)jcbGroups.getSelectedItem(), (String)jcbTargetRepository.getSelectedItem(), onlyadd), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {							 
				PasswdDialog dlg = new PasswdDialog(username,key);
				if (key.equals(dlg.getPasswd())) {
					FedoraConnector target = new FedoraConnector();
					if (target.stubOpenConnection("https", (String) jcbTargetRepository.getSelectedItem(), connector.getCurrentUser(), dlg.getPasswd()) == 200) {; 
						JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");			
						jpbProgessBar.setVisible(true);
						jpbProgessBar.setIndeterminate(false);
						jpbProgessBar.setStringPainted(true);
						Vector data = connector.stubGetObjectListAsVector(group, null, -1, null, false); 
						
						if (jcbCpPrototypes.isSelected()){
							ArrayList<PrototypeListEntry>list = connector.stubGetPrototypeList(group, false);
							for (PrototypeListEntry ple : list) {
								Vector<String> row = new Vector<String>();
								row.add(ple.getPid());
								data.add(row);
							}
						}
						
						log(res.getString("startpub"), "");
						try {
							jpbProgessBar.setMaximum(data.size());
							for (int i = 0; i < data.size(); i++) { 
								Vector entry =  (Vector) data.get(i);
								String pid = (String)entry.get(0);
								jpbProgessBar.setValue(i);
								if (!pid.equals("o:cirilo.properties") && !pid.contains("cirilo:") ) {
									jpbProgessBar.setString(pid);
									try {
										byte[] stream = connector.stubGetMETS(pid);
										if (stream != null) {
											int retval = target.stubGetAuthorization(pid, "Ingest");
											if (retval == 410) {
												log(pid,  "... " + res.getString("entomedobj"));
											} else if (retval == 403) {
												log(pid, "... " + res.getString("accessforbidden"));
											} else if (retval == 200 && isOnlyAdd) { 	
												log(pid, "... " + res.getString("existsontarget"));
											} else {
												if (target.stubImportMETS(group, stream) == 200) {
													log(pid, "... " + "ok");
													irec++;
												} else {
													throw new Exception();
												}
											}
										}
									} catch (Exception q) {
										log( pid, "... " + res.getString("outofmemory"));
									}
								}
							}
							if (jcbCpResDir.isSelected()) {
								byte[] stream = connector.stubGetResourceDirectory(group); 
								if (stream != null) { 
									if (target.stubPublishResourceDirectory(group, stream, true) == 200) {
										log( res.getString("cpresdir").toLowerCase(), "... ok");
									}
								}	
							}
							jpbProgessBar.setVisible(false);	
							log(res.getString("endpub"), "");
							JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("migrationok"), (String)jcbTargetRepository.getSelectedItem(), new Integer(irec).toString()), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);		
						} catch (Exception q) {
						} finally {
							jpbProgessBar.setVisible(false);								
						}
			  		} else {
						JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("invalidcredentials"), (String)jcbTargetRepository.getSelectedItem()), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);		
			  		}

				}
			 }
	    	 
		} catch (Exception e) {    
		}		
	
	}
		
	public void show() throws CShowFailedException {
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, new Dimension(700, 450), false);
			setTitle(res.getString("publishgroup"));
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void opened() throws COpenFailedException {
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES); 
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		
			JEditorPane jLogView = (JEditorPane) getGuiComposite().getWidget("jLogView");	
			logview = jLogView.getDocument();

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
			
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));	
			List<String> allGroups = connector.getKeycloakGroups();
			for (String group: allGroups) {
				if(!group.isEmpty()) jcbGroups.addItem(group);
			}
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if (!el.getText().isEmpty() && !allGroups.contains(el.getText()) )jcbGroups.addItem(el.getText());
			}
			
			String s = props.getProperty("user", "General.DefaultGroup");
			if (s != null && !s.isEmpty()) jcbGroups.setSelectedItem(s); else jcbGroups.setSelectedIndex(0);

			JComboBox<String> jcbTargetRepository = ((JComboBox<String>) getGuiComposite().getWidget("jcbTargetRepository"));	
			String repositories = props.getProperty("user", "Fedora.Repositories");
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbTargetRepository"), this, "handleItemListener");
			jcbTargetRepository.addItem(res.getString("notselected"));
			if (repositories != null) {
				for (String r: repositories.split("[;,]")) {
					if (!r.contains(connector.getHostname())) jcbTargetRepository.addItem(r.trim());
				}	
			}

			
		} catch (Exception e) {		
			log.error(e);
		}
			
	}
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	private void log(String pid, String message) {
		try {
			logview.insertString(logview.getLength(), String.format("%s %s %s\n", Common.getDate(), pid, message), null);
		} catch (Exception e) {}
	}
	
	public void close() {
		try {
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

}
