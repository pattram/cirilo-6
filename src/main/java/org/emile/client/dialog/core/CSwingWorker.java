package org.emile.client.dialog.core;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.emile.client.dialog.AggregateDialog;
import org.emile.client.dialog.CreateObjectDialog;
import org.emile.client.dialog.HarvesterDialog;
import org.emile.client.dialog.IngestObjectDialog;
import org.emile.client.dialog.LogDialog;
import org.emile.client.dialog.ObjectEditorDialog;
import org.emile.client.dialog.PublishDialog;
import org.emile.client.dialog.PublishGroupDialog;
import org.emile.client.dialog.ReorganizerDialog;
import org.emile.client.dialog.ReplaceDialog;
import org.emile.client.dialog.RepositoryDialog;
import org.emile.client.dialog.ReviewDialog;
import org.emile.client.dialog.UserEnvironmentDialog;
import org.emile.client.dialog.XSLDialog;

import voodoosoft.jroots.dialog.CDefaultGuiAdapter;

public class CSwingWorker extends SwingWorker<Integer, Integer> {

	private static Logger log = Logger.getLogger(CSwingWorker.class);

	private Object parent;
	private String[] object;
	private Object proxy;
	
	public CSwingWorker(Object parent, String[] object) {
		
		this.parent = parent;
		this.object = object;	
		this.proxy = null;
	}
	
	public CSwingWorker(Object parent, Object object) {
		
		this.parent = parent;
		this.object = null;
		this.proxy = object;	
	}


    @Override
    protected Integer doInBackground() throws Exception {
      	if (proxy != null && proxy instanceof CDatastreamListSelectionListener && parent instanceof ObjectEditorDialog) { 			    
      		ObjectEditorDialog dlg = (ObjectEditorDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
      		dlg.viewDS();
      		pb.setVisible(false);
      	} else if (proxy != null && proxy instanceof String && parent instanceof ObjectEditorDialog) { 			    
          	ObjectEditorDialog dlg = (ObjectEditorDialog) parent;
          	JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
          	pb.setVisible(true);
          	dlg.save((String)proxy);
          	pb.setVisible(false);
      	} else if (proxy != null && proxy instanceof Integer && parent instanceof ObjectEditorDialog) {
       		ObjectEditorDialog dlg = (ObjectEditorDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
     	    dlg.refresh(proxy);
      		pb.setVisible(false);
      	} else if (proxy != null && proxy instanceof byte[] && parent instanceof ObjectEditorDialog) {
       		ObjectEditorDialog dlg = (ObjectEditorDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
     	    dlg.handleAdd((byte[])proxy);
      		pb.setVisible(false);
       	} else if (parent instanceof ObjectEditorDialog) {
       		ObjectEditorDialog dlg = (ObjectEditorDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
     	    dlg.refresh(null);
      		pb.setVisible(false);
     	} else if (proxy instanceof Boolean && parent instanceof RepositoryDialog) { 			    
            RepositoryDialog dlg = (RepositoryDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
      		dlg.doEntomb((Boolean)proxy);
      		pb.setVisible(false);
     	} else if (proxy != null && parent instanceof RepositoryDialog) { 			    
            RepositoryDialog dlg = (RepositoryDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
      		dlg.setEnable(false);
      		dlg.doIt(proxy);
      		dlg.setEnable(true);
      		pb.setVisible(false);
     	} else if (object != null && parent instanceof RepositoryDialog) { 			    
            RepositoryDialog dlg = (RepositoryDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
      		dlg.refresh(object);
      		pb.setVisible(false);
    	} else if (parent instanceof LogDialog) { 			    
    		LogDialog dlg = (LogDialog) parent;
          	JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
          	pb.setVisible(true);
          	dlg.refresh();
          	pb.setVisible(false);
       	} else if (parent instanceof PublishDialog) { 			    
    		PublishDialog dlg = (PublishDialog) parent;
          	JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
          	pb.setVisible(true);
          	dlg.doIt();
         	pb.setVisible(false);
       	} else if (parent instanceof ReorganizerDialog) { 			    
       		ReorganizerDialog dlg = (ReorganizerDialog) parent;
          	JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
          	dlg.doIt();
         	pb.setVisible(false);
       	} else if (parent instanceof HarvesterDialog) { 			    
       		HarvesterDialog dlg = (HarvesterDialog) parent;
          	dlg.doIt();
        } else if (parent instanceof ReviewDialog) { 			    
       		ReviewDialog dlg = (ReviewDialog) parent;
          	JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
          	pb.setVisible(true);
          	dlg.doIt();
        	pb.setVisible(false);
        } else if (parent instanceof PublishGroupDialog) { 			    
       		PublishGroupDialog dlg = (PublishGroupDialog) parent;
          	dlg.doIt();
      	} else if (parent instanceof IngestObjectDialog) {
     		IngestObjectDialog dlg = (IngestObjectDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
      		dlg.setEnable(false);
     	    dlg.doit();
      		dlg.setEnable(true);
      		pb.setVisible(false);
      	} else if (parent instanceof ReplaceDialog) {
      		ReplaceDialog dlg = (ReplaceDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
      		dlg.setEnable(false);
     	    dlg.doit();
      		dlg.setEnable(true);
      		pb.setVisible(false);
    	} else if (parent instanceof AggregateDialog) {
    		AggregateDialog dlg = (AggregateDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		dlg.setEnable(false);
      		pb.setVisible(true);
      	    dlg.doit();
      		dlg.setEnable(true);
      		pb.setVisible(false);
    	} else if (parent instanceof CreateObjectDialog) {
    		CreateObjectDialog dlg = (CreateObjectDialog) parent;
      	    dlg.doit();
     	} else if (parent instanceof UserEnvironmentDialog) {
     		UserEnvironmentDialog dlg = (UserEnvironmentDialog) parent;
       	    dlg.doit(this.proxy);
       	} else if (parent instanceof XSLDialog) {
       		XSLDialog dlg = (XSLDialog) parent;
      		JProgressBar pb = ((JProgressBar) dlg.getGuiComposite().getWidget("jpbProgessBar"));	
      		pb.setVisible(true);
     	    dlg.refresh();
      		pb.setVisible(false);
     	} else if (parent instanceof CContextWidgets) {
      		JProgressBar pb = null;
    		CContextWidgets obj = (CContextWidgets) parent;
       	    if (obj.getDialog() instanceof ObjectEditorDialog) pb = ((ObjectEditorDialog)obj.getDialog()).getProgressBar();
       	    if (obj.getDialog() instanceof ReplaceDialog) pb = ((ReplaceDialog)obj.getDialog()).getProgressBar();
    		pb.setVisible(true);  
    	    obj.refresh(true);
       		pb.setVisible(false);
    	}
    	 	
     	return 200;
    }

    @Override
    protected void done() {
       try {
       } catch (Exception e) {
          log.error(e);
       }
    }
    
}
