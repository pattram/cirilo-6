package org.emile.client.dialog;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.dialog.CDialog;
import voodoosoft.jroots.dialog.CDialogTools;
import voodoosoft.jroots.dialog.COpenFailedException;
import voodoosoft.jroots.dialog.CShowFailedException;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.CollectDirectories;
import org.emile.cirilo.utils.CollectFiles;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.models.ReviewResult;
import org.emile.client.utils.AnnulationStack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class ReviewDialog extends CDialog {
		
	private FedoraConnector connector;
	private AnnulationStack queue; 
	private CPropertyService props;
	private ResourceBundle res; 
	
	private static Logger log = Logger.getLogger(ReviewDialog.class);

	public ReviewDialog() {}

	public void setup(FedoraConnector connector )  {
		try {
			this.connector = connector;
		} catch (Exception e) {
		}
	}

	public void handleSourceButton(ActionEvent ae) {
		try {
			final JFileChooser chooser = CFileChooser.get(res.getString("choosesourcedir"), props.getProperty("user", "Source.Directory"), null, null, JFileChooser.DIRECTORIES_ONLY);						
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JTextField jtfSource = ((JTextField) getGuiComposite().getWidget("jtfSource"));
				jtfSource.setText(chooser.getSelectedFile().getAbsolutePath());
				JButton jbSubmit = ((JButton) getGuiComposite().getWidget("jbSubmit"));
				jbSubmit.setEnabled(true);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void handleSubmitButton(ActionEvent ae) {
		try {
			JButton jbCopy = ((JButton) getGuiComposite().getWidget("jbCopy"));
			jbCopy.setEnabled(false);
			JButton jbSubmit = ((JButton) getGuiComposite().getWidget("jbSubmit"));
			if (jbSubmit.getText().equals(res.getString("submit"))) {
				CSwingWorker loader = new CSwingWorker(this, null);
				loader.execute();
			} else {
				queue.unset(getTitle());
			}
	
		} catch (Exception e) {}
	}


	public void handleCloseButton(ActionEvent ae) {
		try {
			close();
		} catch (Exception e) {}
	}
	
	public void handleCopyButton(ActionEvent ae) {
		try {
			JTextArea jtaReview = ((JTextArea) getGuiComposite().getWidget("jtaReview"));
			StringSelection stringSelection = new StringSelection(jtaReview.getText());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		} catch (Exception e) {}
	}
		
	public void doIt() {
		
		ArrayList<File> list;
		HashMap<String, ReviewResult> rvs;
		HashMap<String, String> pids;
		 
		try {	
	    	 
			 JButton jbSubmit = ((JButton) getGuiComposite().getWidget("jbSubmit"));
			 JButton jbCopy = ((JButton) getGuiComposite().getWidget("jbCopy"));
			 jbSubmit.setText(res.getString("cancel"));
			 
			 queue.set(getTitle());

			 ArrayList<ReviewResult> results =  new ArrayList<ReviewResult>();
			 
			 JTextField jtfSource = ((JTextField) getGuiComposite().getWidget("jtfSource"));
			 JTextArea jtaReview = ((JTextArea) getGuiComposite().getWidget("jtaReview"));
			 
			list = new ArrayList<File>();
			 
			 for (File dir: new CollectDirectories(jtfSource.getText()).getDirectories()) {	
				 ArrayList<File> files = new CollectFiles(dir.toString(), "*.xml;*.rdf;*.owl").getFiles();	
				 for (File file: files) {
					list.add(file); 
				 }
			 }
			 
			 rvs = new  HashMap<String, ReviewResult>();
			 pids = new HashMap<String,String>();
			 int dups = 0; int errors = 0; int unknown = 0;
			 
			 for (File file: list) {	
				ReviewResult rv = new ReviewResult();	
				rv.set(connector.stubReviewDatastream(FileUtils.readFileToByteArray(file))+"|"+file.getAbsolutePath());
				if (pids.get(rv.getPID()) != null && !rv.getPID().equals("†") && !rv.getPID().contains("is part")) { 
					rv.setRemark(res.getString("dupkey"));
					dups++;
				} else if(rv.getModel().contains("XSLT")) {
					rv.setRemark(res.getString("wrongext"));
				} else if (rv.getModel().contains("unknown")) {
					unknown++;	
				} else  {
					pids.put(rv.getPID(), rv.getPID());
				}
				if(rv.getModel().contains("Invalid")) errors++;
				
			    rvs.put(rv.getPID()+file.getAbsolutePath(),rv);		    
				jtaReview.setText(file.getAbsolutePath()+ " ...\n");
				
				if (!queue.get(getTitle())) break;
			 }

			 jtaReview.setText(new String());
			 SortedSet<String> keys = new TreeSet<>(rvs.keySet());
			 for (String key : keys) { 
				 ReviewResult rv = rvs.get(key);
				 jtaReview.append(rv.getFilename() + ": " + rv.getModel() + print(rv.getValidation())  + print(rv.getPID()) + print(rv.getRemark()) + "\n" );
			 }
			 		 
			 jtaReview.append("\n----\n"+Common.msgFormat(res.getString("summary"), new Integer(rvs.size()).toString(), new Integer(errors).toString(), new Integer(unknown).toString(), new Integer(dups).toString()));
			 
			 jbCopy.setEnabled(true);
			 jbSubmit.setText(res.getString("submit"));

			 props.setProperty("user", "Review.Directory", jtfSource.getText());
			 props.saveProperties("user");

		} catch (Exception e) {    
			e.printStackTrace();
		} finally {
			list = null;
			rvs = null;
			pids = null;
		}
		
		queue.unset(getTitle());
	}
		
	private String print (String s) {
		
		if (!s.isEmpty() && !s.equals("†")) return (" | " + s);
		return new String();
		
	}
	
	public void show() throws CShowFailedException {
		try {
			CBoundSerializer.load(this.getCoreDialog(), null, new Dimension(700, 450), false);
			setTitle(res.getString("review").replaceAll("[.]", ""));
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void opened() throws COpenFailedException {
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES); 
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		
			queue = new AnnulationStack();

			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbClose", "handleCloseButton");
			CDialogTools.createButtonListener(this, "jbCopy", "handleCopyButton");
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
			
			JTextField jtfSource = ((JTextField) getGuiComposite().getWidget("jtfSource"));
			String q = props.getProperty("user","Review.Directory");
			if (q != null && !q.isEmpty()) { 
				jtfSource.setText(q);
				JButton jbApply = ((JButton) getGuiComposite().getWidget("jbSubmit"));
				jbApply.setEnabled(true);
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
