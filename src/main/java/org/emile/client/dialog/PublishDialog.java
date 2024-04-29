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
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.Unzipper;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CHint;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.utils.UserProfile;
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
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class PublishDialog extends CDialog {
		
	private FedoraConnector connector;
	private CPropertyService props;
	private ResourceBundle res; 
	private String key; 
	private String username;

	private static Logger log = Logger.getLogger(PublishDialog.class);

	public PublishDialog() {}
	
	public void setup(String key, RepositoryDialog parent)  {
		try {
			this.connector = parent.getFedoraConnector();
			this.username = parent.getUsername();
			this.key = key;
		} catch (Exception e) {
		}
	}

	public void handleSourceButton(ActionEvent ae) {
		try {
			final JFileChooser chooser = CFileChooser.get(res.getString("choosesourcedir"), props.getProperty("user", "Source.Directory"), null, null, JFileChooser.DIRECTORIES_ONLY);						
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfSource"));
				tf.setText(chooser.getSelectedFile().getAbsolutePath());
				JButton jbApply = ((JButton) getGuiComposite().getWidget("jbApply"));
				jbApply.setEnabled(true);
				CHint jbHint = ((CHint) getGuiComposite().getWidget("jbHint"));
				jbHint.setText(Common.msgFormat(res.getString("HTpublishzip"), "https://"+connector.getHostname()+"/"+jcbGroups.getSelectedItem()) );	
				jbHint.setVisible(true);
			}
		} catch (Exception e) {}
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
			JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));
			JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfSource"));
			tf.setText("");
			JButton jbApply = ((JButton) getGuiComposite().getWidget("jbApply"));
			jbApply.setEnabled(false);
			CHint jbHint = ((CHint) getGuiComposite().getWidget("jbHint"));
			jbHint.setVisible(false);
		}
	}

	
	public void doIt() {
		try {	
			 PasswdDialog dlg = new PasswdDialog(username, key);
			 if (key.equals(dlg.getPasswd())) {
					
				 JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");			
				 jpbProgessBar.setVisible(true);
				 
				 JComboBox<String> jcbGroups = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroups"));
				 JTextField jtfSource = ((JTextField) getGuiComposite().getWidget("jtfSource"));
				 JCheckBox jcbDelTargetDir = ((JCheckBox) getGuiComposite().getWidget("jcbDelTargetDir"));
				
				 UserProfile us = new UserProfile();
				 File tempfile = us.getTempFile();
				 
				 ZipFile archiv = Unzipper.zip(tempfile, new File(jtfSource.getText())); 

				 byte[] stream = FileUtils.readFileToByteArray(archiv.getFile());
				 connector.stubPublishResourceDirectory((String)jcbGroups.getSelectedItem(), stream, jcbDelTargetDir.isSelected());   
				
				 tempfile.delete();

				 jpbProgessBar.setVisible(false);

				 JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("updateresdir"), (String) jcbGroups.getSelectedItem(), connector.getHostname() ), getTitle(), JOptionPane.WARNING_MESSAGE);
				 
				 props.setProperty("user", "General.DefaultGroup", (String)jcbGroups.getSelectedItem());
				 props.setProperty("user", "Source.Directory", jtfSource.getText());
				 props.saveProperties("user");

			 } else {
				 JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("invalidcred")), getTitle(), JOptionPane.WARNING_MESSAGE);
			 }
	    	 
		} catch (Exception e) {    
			e.printStackTrace();
		}		
	
	}
		
	public void show() throws CShowFailedException {
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, new Dimension(700, 450), false);
			setTitle(res.getString("publish") + " on "+connector.getHostname());
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void opened() throws COpenFailedException {
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES); 
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			
			CDialogTools.createButtonListener(this, "jbApply", "handleApplyButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbChooseSource", "handleSourceButton");

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
			new CItemListener((JComboBox) getGuiComposite().getWidget("jcbGroups"), this, "handleItemListener");

			JTextField jtfSource = ((JTextField) getGuiComposite().getWidget("jtfSource"));
			String q = props.getProperty("user","Source.Directory");
			if (q != null && !q.isEmpty()) { 
				jtfSource.setText(q);
				JButton jbApply = ((JButton) getGuiComposite().getWidget("jbApply"));
				jbApply.setEnabled(true);
				CHint jbHint = ((CHint) getGuiComposite().getWidget("jbHint"));
				jbHint.setText(Common.msgFormat(res.getString("HTpublishzip"), "https://"+connector.getHostname()+"/"+jcbGroups.getSelectedItem()) );	
				jbHint.setVisible(true);
			}
		} catch (Exception e) {		
			log.error(e);
		}
			
	}
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

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
