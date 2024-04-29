package org.emile.client.dialog.core;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.ObjectEditorDialog;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

public class CDatastreamListSelectionListener implements ListSelectionListener {
	
	private static Logger log = Logger.getLogger(CDatastreamListSelectionListener.class);

	private ObjectEditorDialog parent;
	private ArrayList<String> VersionableDatastreams;
	private boolean showbydoubleclick;	
	private String nondeleteable;
	
	
	public CDatastreamListSelectionListener() {
		
	}
	
	public CDatastreamListSelectionListener(ObjectEditorDialog parent, ArrayList<String> VersionableDatastreams) {
		try {
			CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			this.parent = parent;
			this.VersionableDatastreams = VersionableDatastreams;
			nondeleteable = Common.NONDELETEABLEDATASTREAMS;
			for (int i=0; i<VersionableDatastreams.size(); i++) nondeleteable += VersionableDatastreams.get(i)+"|";
			this.showbydoubleclick = props.getProperty("user", "ShowByDoubleClick").equals("true");			
		} catch (Exception e) {}

	}
	
	public void valueChanged(ListSelectionEvent lse) {
		try {
			if (!lse.getValueIsAdjusting() && !showbydoubleclick) {
								
				CSwingWorker loader = new CSwingWorker(this.parent, this);
				loader.execute();
	
				JButton jbDSVersioning = (JButton) parent.getGuiComposite().getWidget("jbDSVersioning");		
				jbDSVersioning.setEnabled(VersionableDatastreams.contains(parent.getDsid()));
				
				JButton jbDSPurge = (JButton) parent.getGuiComposite().getWidget("jbDSPurge");
				jbDSPurge.setEnabled(!nondeleteable.contains("|"+parent.getDsid()+"|"));

			}
		} catch (Exception e) {}
	}
}
