package org.emile.client.dialog.core;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.emile.cirilo.Namespaces;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.ObjectEditorDialog;
import org.emile.cm4f.models.ObjectListEntry;
import org.jdom.Element;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDialog;

public class CContextWidgets {
	
	private static Logger log = Logger.getLogger(CContextWidgets.class);

	private FedoraConnector connector;
	private CPropertyService props;
	private CDialog dialog;
	private JList<String> jtRels;
	private DefaultListModel mRels; 
	private JList<String> jtNonRels;
	private DefaultListModel mNonRels; 
	private String groups;
	
	public CContextWidgets (FedoraConnector connector, CDialog dialog, String groups) throws Exception {
				
		try {
			
			ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			this.connector = connector;
			this.dialog = dialog;
			this.groups = groups;
		
			jtRels = (JList<String>) this.dialog.getGuiComposite().getWidget( "jtRels" );
			jtRels.getSelectionModel().addListSelectionListener(new DatastreamListSelectionListener());			
			mRels= (DefaultListModel) jtRels.getModel();
			
			jtNonRels = (JList<String>) this.dialog.getGuiComposite().getWidget( "jtNonRels" );
			jtNonRels.getSelectionModel().addListSelectionListener(new DatastreamListSelectionListener());			
			mNonRels = (DefaultListModel) jtNonRels.getModel();
					
			JComboBox jcbRelFind = (JComboBox) this.dialog.getGuiComposite().getWidget( "jcbRelFind" );
			jcbRelFind.setEditable(true);			
						
			jcbRelFind.removeAllItems();
			jcbRelFind.insertItemAt(res.getString("notselected"), 0);	
			jcbRelFind.setEditable(true);

			String searchterm = props.getProperty("user", "Search.Terms");
			if (searchterm != null) {
				String searchterms[] = searchterm.split("[,;]");
				for (int i = searchterms.length - 1; i > -1; i--) {
					if (!searchterms[i].isEmpty()) jcbRelFind.insertItemAt(searchterms[i], 1);
				}
			}		
			
			jcbRelFind.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (jcbRelFind.getSelectedIndex() == 0 ) {
						 JTextComponent editor = (JTextComponent) jcbRelFind.getEditor().getEditorComponent();
						 editor.setText("");
					 }
				 }
			});			
								
			jcbRelFind.setSelectedIndex(0);
			
			refresh(false);

		} catch (Exception e) {
			log.error(e);
		}

	}
	
	public CDialog getDialog() {
		return this.dialog;
	}
	
	public void refresh(boolean mode) {
	
		HashMap<String,String> hContexts = new HashMap<String,String>();
		SortedSet<String> sContexts =  new TreeSet<String>();
		
		try {			
			JComboBox jcbRelFind = (JComboBox) dialog.getGuiComposite().getWidget( "jcbRelFind" );
			ArrayList<ObjectListEntry> ol =  mode ? this.connector.stubGetContextList(this.groups) : null;
					
			mRels.removeAllElements();
			mNonRels.removeAllElements();
		
			if (ol != null) {
		
				for (ObjectListEntry o : ol) {
					hContexts.put(o.getPid(), o.getTitle() + " | " + o.getPid());
					sContexts.add(o.getTitle() + " | " + o.getPid());
				}	
			
				
				if (jcbRelFind.getSelectedIndex() > 0) {
					String searchterm = ((String)jcbRelFind.getSelectedItem()).replace("*","").toLowerCase();
					for (String s : sContexts) {	
						if (s.toLowerCase().contains(searchterm)) {
							mNonRels.addElement(s);
						}		
					}
				} else {
					for (String s : sContexts) {	
						mNonRels.addElement(s);
					}
				}
				
				jtRels.setSelectedIndex( 0 );
				jtNonRels.setSelectedIndex( 0 );		
			}
			
			if (dialog instanceof ObjectEditorDialog) {
				for (Element el: XMLUtils.getChildren("//s:result", XMLUtils.createDocumentFromByteArray(connector.stubGetMembershipsAsRDF(((ObjectEditorDialog)dialog).getPid())))) {
					String id = el.getChildText("pid", Namespaces.xmlns_sparql2001);
					String item = hContexts.get(id);
					mRels.addElement(el.getChildText("title", Namespaces.xmlns_sparql2001)+" | "+id);
					if (item != null) {
						mNonRels.removeElement(item);
					}	
				}	
			}	
			
		} catch (Exception e) {
			log.error(e);
		}
		
	}
	
	public void add() throws Exception  {
		
		ArrayList<String> rm = new ArrayList<String>();
		
		try {
			int[] selected = jtNonRels.getSelectedIndices();

			for ( int i = 0; i < selected.length; i++ ) {
				mRels.addElement( mNonRels.getElementAt( selected[i] ) );
				rm.add( (String) mNonRels.getElementAt( selected[i] ) );
			}
			
			for ( int i = 0; i < rm.size(); i++ ) {
				mNonRels.removeElement( rm.get( i ) );
			}
			
			jtRels.setSelectedIndex( jtRels.getLastVisibleIndex() );
			jtNonRels.setSelectedIndex( jtNonRels.getLastVisibleIndex() );

		} catch (Exception e) {
			log.error(e);
		}	
	}
	
	public void remove() throws Exception  {
		
		ArrayList<String> rm = new ArrayList<String>();
		
		try {
			int[] selected = jtRels.getSelectedIndices();

			for ( int i = 0; i < selected.length; i++ ) {
				mNonRels.addElement( mRels.getElementAt( selected[i] ) );
				rm.add( (String) mRels.getElementAt( selected[i] ) );
			}
			
			for ( int i = 0; i < rm.size(); i++ ) {
				mRels.removeElement( rm.get( i ) );
			}
			
			jtRels.setSelectedIndex( jtRels.getLastVisibleIndex() );
			jtNonRels.setSelectedIndex( jtNonRels.getLastVisibleIndex() );

		} catch (Exception e) {
			log.error(e);
		}

	}

	public void find() throws Exception  {
		CSwingWorker loader = new CSwingWorker(this, null);
		loader.execute();
	}
	
	public void saveterms() {
		
		try { 
			JComboBox jcbRelFind = (JComboBox) this.dialog.getGuiComposite().getWidget( "jcbRelFind" );
			String searchterm = new String();
			for (int i = 1; i <jcbRelFind.getItemCount(); i++) {
				searchterm += jcbRelFind.getItemAt(i) + (i < jcbRelFind.getItemCount()-1 ? ";" : "");  
			}
			props.setProperty("user", "Search.Terms", searchterm );
			props.saveProperties("user");
		} catch (Exception e) {}
	}
	
	public void addterm() {
		try {
			JComboBox<String> jcbRelFind = ((JComboBox<String>) dialog.getGuiComposite().getWidget("jcbRelFind"));		
			ComboBoxModel<String> model = jcbRelFind.getModel();
			for (int i = 0; i < model.getSize(); i++) {
			   if (((String)model.getElementAt(i)).equals((String)jcbRelFind.getSelectedItem())) return;   
			}			
			if (!((String)jcbRelFind.getSelectedItem()).isEmpty()) {
				jcbRelFind.insertItemAt((String)jcbRelFind.getSelectedItem(), 1);
			} else {
				jcbRelFind.setSelectedIndex(0);
			}

		} catch (Exception e) {
			log.error(e);
		}				
	}
	
	public void delterm() {
		try {	
			JComboBox<String> jcbRelFind = ((JComboBox<String>) dialog.getGuiComposite().getWidget("jcbRelFind"));
			if (jcbRelFind.getSelectedIndex() > 0) jcbRelFind.removeItem(jcbRelFind.getSelectedItem());
		} catch (Exception e) {
			log.error(e);
		}		
		
	}
	
	class DatastreamListSelectionListener implements ListSelectionListener {
				
		public void valueChanged(ListSelectionEvent lse) {
			try {
				dialog.getGuiComposite().getWidget( "jbRelRemove" ).setEnabled( mRels.size() > 0);
				dialog.getGuiComposite().getWidget( "jbRelAdd" ).setEnabled( mNonRels.size() > 0 );
			} catch (Exception e) {
			} 
		}
	}

}
