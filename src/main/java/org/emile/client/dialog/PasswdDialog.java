package org.emile.client.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;

import org.emile.client.Common;
import org.emile.client.ServiceNames;

import voodoosoft.jroots.core.CServiceProvider;

public class PasswdDialog extends JDialog {
	
	protected JPanel panel = new JPanel();
	protected JLabel jlFieldname;
	protected JPasswordField jpfPasswd = null;
	protected JButton jbOK;
	protected JButton jbCancel;
	protected String passwd;
	protected String key;
	
	public PasswdDialog(String username, String key) {
	
		setTitle(Common.MAIN_WINDOW_HEADER);
		this.key = key;
		
		panel.setLayout(new FlowLayout());
			
		jlFieldname = new JLabel("Reenter password for user "+username+":");
		jpfPasswd = new JPasswordField();
		jpfPasswd.setPreferredSize(new Dimension(150, jpfPasswd.getPreferredSize().height));

		jbOK = new JButton("Submit");		
		jbOK.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			passwd = String.valueOf(jpfPasswd.getPassword());
    			if (passwd != null && passwd.equals(key)) { 
    				PasswdDialog.this.close();
    			} else {
    				try {
    				  ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
					  JOptionPane.showMessageDialog(null, res.getString("invalidconst"), getTitle(), JOptionPane.WARNING_MESSAGE);
    				} catch (Exception e) {}
    			}
    		}
    	});
	
		jbCancel = new JButton("Cancel");		
		jbCancel.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			passwd = null;
    			PasswdDialog.this.close();
    		}
    	});

		panel.add(jlFieldname);
		panel.add(jpfPasswd);
		panel.add(jbOK);
		panel.add(jbCancel);
		
		add(panel);
		
		setSize(580, 100);
		setLocation(350, 350);
		setModal(true);
		setAlwaysOnTop(true);
		setModalityType (ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setVisible(true);
					
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public void close() {
		dispose();
	}
	
}