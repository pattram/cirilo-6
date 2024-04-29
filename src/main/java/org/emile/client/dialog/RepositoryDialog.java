package org.emile.client.dialog;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceName;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.CMouseListener;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;
import voodoosoft.jroots.dialog.CDialog;
import voodoosoft.jroots.dialog.CDialogTools;
import voodoosoft.jroots.dialog.COpenFailedException;
import voodoosoft.jroots.dialog.CShowFailedException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.CollectDirectories;
import org.emile.cirilo.utils.CollectFiles;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.CiriloFrame;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CBoundSerializer;
import org.emile.client.dialog.core.CDefaultMessageDialog;
import org.emile.client.dialog.core.CFileChooser;
import org.emile.client.dialog.core.CConfigurationStore;
import org.emile.client.dialog.core.CSwingWorker;
import org.emile.client.gui.table.CSortTableModel;
import org.emile.client.utils.AnnulationStack;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.PrototypeListEntry;
import org.jdom.Element;

import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class RepositoryDialog extends CDialog {
	
	private String[] query;
	
	private CDefaultGuiAdapter moGA;
	private FedoraConnector connector;
	private ResourceBundle res; 
	private CPropertyService props;
	private CConfigurationStore PipelineStore;
	private boolean isConnectedToHandleSystem;
	private AnnulationStack queue; 
	private CiriloFrame parent;
	private String[] columnNames;
	private String[] delOptions;
	private ArrayList<File> filesToImport;
	private JPopupMenu popup;
	private JPanel jbHandleDialog;
	private Box jbInOutDialog;
	private JTable jtData; 
	private String title;	
	private String subtitle;	
	private String username;	
	private String prefix;
	private String host;
	
	
	private static Logger log = Logger.getLogger(RepositoryDialog.class);

	public RepositoryDialog() {}
	
	public FedoraConnector getFedoraConnector() {
		return connector;
	}
	
	public JTable getJtData() {
		return jtData;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getHostname() {
		return host;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public CConfigurationStore getPipelineStore() {
		return PipelineStore;
	}
	
	public boolean hasSysopRights() {
		return connector.isSysop();
	}
	
	public boolean hasAdminRights() {
		return connector.isAdmin();
	}
	
	public void handleEvent(ActionEvent ae) {
		try {
			
			String cmd = ae.getActionCommand().toLowerCase().replaceAll("[. ]", "");
			switch (cmd) {
				case "edit":  
					handleEdit(null); 
					break;
				case "new":  
					handleNew(null); 
					break;
				case "replace":  
					handleReplace(null); 
					break;
				case "managehandles":  
					handleManage(null); 
					break;
				case "export":  
					handleExport(null); 
					break;
				case "import":  
					handleImport(null); 
					break;
				case "delete":  
					handleEntomb(null); 
					break;
				case "updatesystemproperties":  
					handleUpdateSystemProperties(null); 
					break;
				default: 
					CDefaultMessageDialog.notYetImplemented(ae, getCoreDialog().getName()); 
					break;
			}			
		} catch (Exception e) {}
	}

	public void handleEdit(ActionEvent ae) {
		try {
			ObjectEditorDialog dlg = (ObjectEditorDialog) CServiceProvider.getService(DialogNames.OBJECTEDITOR_DIALOG);
			dlg.setup(title + " - " + jtData.getValueAt(jtData.getSelectedRow(), 1), this, (String)jtData.getValueAt(jtData.getSelectedRow(), 3), connector);
			dlg.open();
		} catch (Exception e) {}
	}

	public void handleNew(ActionEvent ae) {
		try {
			CreateObjectDialog dlg = (CreateObjectDialog) CServiceProvider.getService(DialogNames.CREATEOBJECT_DIALOG);
			String header = StringUtils.substringBefore(title, "|") + " | " + res.getString("createobject") + " | " +StringUtils.substringAfter(title, "|");
			dlg.setup(header, this, connector);
			dlg.open();
		} catch (Exception e) {}		
	}

	public void handleReplace(ActionEvent ae) {
		try {
			ReplaceDialog dlg = (ReplaceDialog) CServiceProvider.getService(DialogNames.REPLACE_DIALOG);
			String header = StringUtils.substringBefore(title, "|") + " | " + res.getString("replaceobject") + " | " +StringUtils.substringAfter(title, "|");
			dlg.setup(header, this, connector);
			dlg.open();
		} catch (Exception e) {}
	}

	
	public void handleSort(int order) {
		try {
			setOrder(order);			
		} catch (Exception e) {}
	}
	
	
	public void handleUpdateSystemProperties(ActionEvent ae) {
		try {
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askupdateproperties"), StringUtils.substringAfter(title,"▪ ")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
				CSwingWorker loader = new CSwingWorker(this, new Integer(6));
				loader.execute();
			}			
		} catch (Exception e) {e.printStackTrace();}
		
	}
	
	public void handleHdlCreateButton(ActionEvent ae) {
		try {
			JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");	
			
			if (((JButton)getGuiComposite().getWidget("jbHdlCreate")).getText().equals(res.getString("cancel"))) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcancel"), res.getString("updatehandle")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					jpbProgessBar.setVisible(false);		
					queue.set(title);
				}
				return;
			}

			JTextField jtfPrefix = ((JTextField) getGuiComposite().getWidget("jtfPrefix"));
			JSpinner jtfBeginningWith = ((JSpinner) getGuiComposite().getWidget("jtfBeginningWith"));
			
			String addidum = res.getString("checkdigit");
			if (!jtfPrefix.getText().isEmpty()) {
				addidum = Common.msgFormat(res.getString("projprefix"), jtfPrefix.getText(), jtfBeginningWith.getValue().toString());
			}			
			
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcreatehandle"), new Integer(jtData.getSelectedRows().length).toString(), addidum), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
				CSwingWorker loader = new CSwingWorker(this, new Integer(3));
				loader.execute();
			}
		} catch (Exception e) {}
	}

	public void handleHdlRefreshButton(ActionEvent ae) {
		try {
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askupdatehandle"), new Integer(jtData.getSelectedRows().length).toString(), res.getString("refreshed")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
				CSwingWorker loader = new CSwingWorker(this, new Integer(4));
				loader.execute();
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	public void handleHdlDeleteButton(ActionEvent ae) {
		try {
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askupdatehandle"), new Integer(jtData.getSelectedRows().length).toString(), res.getString("deleted")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
				CSwingWorker loader = new CSwingWorker(this, new Integer(5));
				loader.execute();
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	public void handleHdlCloseButton(ActionEvent ae) {
		try {
			queue.unset(title);
			
			jbHandleDialog.setVisible(false);
		} catch (Exception e) {}
	}

	public void handleExport(ActionEvent ae) {
		try {
			
			JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfInOutDir"));
			tf.setText(props.getProperty("user", "Export.Directory"));

			((JLabel) getGuiComposite().getWidget("jlInOutTitle")).setText(res.getString("exportobj"));
			((JLabel) getGuiComposite().getWidget("jlInOutDir")).setText(res.getString("exportdir"));
			
		    JComboBox<String> jcbInOutGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbInOutGroup"));	    
		    jcbInOutGroup.setVisible(false);

			jbInOutDialog.setVisible(true);
			
		} catch (Exception e) {}
	}
	
	public void handleImport(ActionEvent ae) {
		try {
			
			JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfInOutDir"));
			tf.setText(props.getProperty("user", "Import.Directory"));
			
			((JLabel) getGuiComposite().getWidget("jlInOutTitle")).setText(res.getString("importobj"));
			((JLabel) getGuiComposite().getWidget("jlInOutDir")).setText(res.getString("importdir"));
			
		    JComboBox<String> jcbInOutGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbInOutGroup"));	    
		    jcbInOutGroup.setVisible(false);

			jbInOutDialog.setVisible(true);
			
		} catch (Exception e) {}
	}

	
	public void handleInOutSubmitButton(ActionEvent ae) {
		String header; 

		try {
			JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");	
			String dir = ((JTextField) getGuiComposite().getWidget("jtfInOutDir")).getText();

			header =  ((((JLabel) getGuiComposite().getWidget("jlInOutTitle")).getText().equals(res.getString("importobj"))) ? res.getString("import") : res.getString("export")).toLowerCase().replaceAll("[.]", "");
			
			if (((JButton)getGuiComposite().getWidget("jbInOutSubmit")).getText().equals(res.getString("cancel"))) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askcancel"), header), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					jpbProgessBar.setVisible(false);		
					queue.set(title);
				}
				return;
			}

			if (((JLabel) getGuiComposite().getWidget("jlInOutTitle")).getText().equals(res.getString("importobj"))) {
				filesToImport = new ArrayList<File>();
				for (File sdir: new CollectDirectories(dir).getDirectories()) {	
					 ArrayList<File> files = new CollectFiles(sdir.toString(), "*.xml").getFiles();
					 for (File file: files) filesToImport.add(file);
				}
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askimport"), new Integer(filesToImport.size()).toString(), dir), res.getString("importobj") + " |"+getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
					CSwingWorker loader = new CSwingWorker(this, new Integer(1));
					loader.execute();
				}
			}

			if (((JLabel) getGuiComposite().getWidget("jlInOutTitle")).getText().equals(res.getString("exportobj"))) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askexport"), new Integer(jtData.getSelectedRows().length).toString(), dir), res.getString("importobj") + " |"+getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
					CSwingWorker loader = new CSwingWorker(this, new Integer(2));
					loader.execute();
				}
			}

		} catch (Exception e) {}
	}
	
	public void handleSelectInOutDirButton(ActionEvent ae) {
		String dir;
		
		try {
			if (((JLabel) getGuiComposite().getWidget("jlInOutTitle")).getText().equals(res.getString("importobj"))) {
				dir = props.getProperty("user", "Import.Directory");
			} else {
				dir = props.getProperty("user", "Export.Directory");
			}
			
			final JFileChooser chooser = CFileChooser.get(res.getString("chooseexportdir"), dir, null, null, JFileChooser.DIRECTORIES_ONLY);						
			int returnVal = chooser.showOpenDialog(this.getCoreDialog());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfInOutDir"));
				tf.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		} catch (Exception e) {}	
	}

	public void handleInOutCloseButton(ActionEvent ae) {
		try {
			
			queue.unset(title);
			
			JTextField tf = ((JTextField) getGuiComposite().getWidget("jtfInOutDir"));
			if (((JLabel) getGuiComposite().getWidget("jlInOutTitle")).getText().equals(res.getString("importobj"))) {
				props.setProperty("user", "Import.Directory", tf.getText());
			} else {
				props.setProperty("user", "Export.Directory", tf.getText());
			}
			
			props.saveProperties("user");

			jbInOutDialog.setVisible(false);
			
		} catch (Exception e) {}
	}


	public void handleManage(ActionEvent ae) {
		try {
			jbHandleDialog.setVisible(true);
		} catch (Exception e) {}
	}

	public void handleEntomb(ActionEvent ae) {
		
		int[] rows = jtData.getSelectedRows();
		Boolean entomb = null;
		
		int result = JOptionPane.showOptionDialog(null,Common.msgFormat(res.getString("askdelete"), new Integer(rows.length).toString()), getTitle(), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, delOptions, delOptions[0]);							
		
		if (result > 0) {
			if (result == 2) {
				if (JOptionPane.showConfirmDialog(null, Common.msgFormat(res.getString("askdeleteobj"), new Integer(rows.length).toString()), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
					entomb = false;
				}
			} else if (result == 1) {
				if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askentomb"), new Integer(rows.length).toString()), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
					if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("ask2entomb"), new Integer(rows.length).toString()), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {	
						entomb = true;
					} 
				} 
			} 
			if (entomb != null) {
				CSwingWorker loader = new CSwingWorker(this, entomb);
				loader.execute();
			}
		}
	}

	public void doEntomb(Boolean entomb) {
		int iobj = 0;
		
		try {
			
			int[] rows = jtData.getSelectedRows();

			for (int i = 0; i < rows.length; i++) {
								
				String pid = (String)jtData.getValueAt(rows[i], 1);		
				Matcher m = Pattern.compile("(query|corpus|context|o)[:](.*)").matcher(pid);
			
				if (!m.find() || pid.contains("cirilo") || pid.contains("prototype")) {
					JOptionPane.showMessageDialog(null, res.getString("noentombsyobj"), getTitle(), JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				connector.stubEntombObject(pid, entomb);
				iobj++;
			
			}
			
			if (iobj > 0 ) { 
				refresh(query);
				JOptionPane.showMessageDialog(null, Common.msgFormat(entomb ? res.getString("msgentomb") : res.getString("msgdelete"), new Integer(iobj).toString()) , getTitle(), JOptionPane.INFORMATION_MESSAGE);
			}
			
		} catch (Exception e) {}
	}

	
	public void handleSearch(String[] query) {
		try {
						
			CSwingWorker loader = new CSwingWorker(this, query);
			loader.execute();
	
		} catch (Exception e) {
		}
	}
	
	
	
	public void handleMouseClick(MouseEvent me, int type) {
		try {
			CEventListener.setBlocked(true);
			if (type == MouseEvent.MOUSE_CLICKED) {
				if (me.getClickCount() >= 1) {
					parent.setActiveWindow(new Integer(StringUtils.substringBefore(getTitle(), "|")));
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
					handleEdit(null);
				} else {
					parent.setActiveWindow(new Integer(StringUtils.substringBefore(getTitle(), "|")));
				}
			}	
		} catch (Exception e) {
		} finally {
			CEventListener.setBlocked(false);
		}
	}

	public void handleSubmitButton(ActionEvent ae) {
		
		String[] query = Common.QUERY;
		
		try {			

			addItem();
			pushQuery();
			refresh(query);
					
		} catch (Exception e) {
			log.error(e);
		}		
 	}

	public void handleResetButton(ActionEvent ae) {
		try {			
			resetDialogComboBoxes(false);	
			pushQuery();
			refresh(query);
		} catch (Exception e) {
			log.error(e);
		}		
 	}

	public boolean hasEntries() {
		int rows = 0;
		try {
			jtData = (JTable) getGuiComposite().getWidget("jtData");
			rows = jtData.getModel().getRowCount();
		} catch (Exception e) {}
		
		return  rows > 0;
	}
	

	
	public void refresh(String[]query) {
		
		String fulltext = new String();
		String prop;
			
		try {	
    		String filter = "filter(regex(str(?pid), '^(o:|context:|corpus:|query:)') && !regex(str(?pid), '^(o:prototype|cirilo:)') $fulltext)";
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
	
			if (query == null) {					
				query = Common.QUERY;
				
				prop = props.getProperty("user", prefix+".Search.Fulltext");
				String ft = StringUtils.substringBefore(StringUtils.substringAfter(prop, "#"), "!");				
				query[0] = (ft != null && !ft.startsWith("-")  && !ft.isEmpty() ) ? ft : null;
				prop = props.getProperty("user", prefix+".Search.ContentModel");
				query[1] = (prop != null && !prop.contains("All ") && !prop.isEmpty()) ? prop : null;			
				prop = props.getProperty("user", prefix+".Search.Group");
				query[2] = (prop != null && !prop.contains("All ") && !prop.isEmpty()) ? prop : (String)jcbGroup.getSelectedItem();	
				prop = props.getProperty("user", prefix+".Search.Limit");
				query[3] = (prop != null && !prop.equals("0")) ? prop : "500";
				prop = props.getProperty("user", prefix+".Search.HasHandle");
				query[4] = (prop != null) ? prop : "false";
			} else {
				query[0] = (query[0] != null  && !query[0].startsWith("All ") && !query[0].isEmpty()) ? query[0] : null;
				query[1] = (query[1] != null  && !query[1].startsWith("All ") && !query[1].isEmpty()) ? query[1] : null;	
				query[2] = (query[2] != null  && !query[2].startsWith("All ") && !query[2].isEmpty()) ? query[2] : (String)jcbGroup.getSelectedItem();									
			}
			
			if (query[0] != null) {
				for (String s : query[0].split("[ ]")) {
					fulltext += "&& (regex(lcase(str(?pid)), '"+s.toLowerCase()+"') || regex(lcase(str(?xtitle)), '"+s.toLowerCase()+"'))";
				}
			}
			
			subtitle = new String();
			
			if (query[0] != null) {
				subtitle += res.getString("fulltext") +" = '" + query[0] + "'";
			}
			if (query[1] != null) {
				subtitle += (subtitle.length() > 0 ? " and "  : "") + "Model = " + query[1];
			}
			if (query[2] != null) {
				subtitle += (subtitle.length() > 0 ? " and "  : "") + "Group = " + query[2];
			}
			if (query[4].equals("true")) {
				subtitle += (subtitle.length() > 0 ? " and "  : "") + res.getString("hashandle");
			}	
										
			CSortTableModel dm = new CSortTableModel(connector.stubGetObjectListAsVector(query[2], filter.replaceAll("[$]fulltext", fulltext), new Integer(query[3]), query[1], query[4].equals("true")), columnNames);
			jtData.setModel(dm);
			Utils.hideColumnUUID(jtData);
			setOrder(1);	
						
			setTitle(this.title + (!subtitle.isEmpty() ? " : " + this.subtitle : "") + " : " + dm.getRowCount()+" "+res.getString("of")+" "+connector.stubGetObjectN(query[2], filter.replaceAll("[$]fulltext", fulltext.replaceAll("[?]xtitle", "?title")), query[1], query[4].equals("true")));
			
			parent.setMenu();
			
			jtData.setRowSelectionAllowed(true);
			
			jtData.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			Utils.setRowSelection(jtData);
			
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void doIt(Object proxy) {
		int ok = 0;
		try {
			queue.set(title);
			
			if (proxy instanceof Integer) {
			
			    JComboBox<String> jcbInOutGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbInOutGroup"));	    							
				JProgressBar jpbProgessBar = (JProgressBar) getGuiComposite().getWidget("jpbProgessBar");		
				jpbProgessBar.setStringPainted(true);

				String dir = ((JTextField) getGuiComposite().getWidget("jtfInOutDir")).getText();
				int[] rows = jtData.getSelectedRows();
			
				if (((Integer)proxy).intValue() == 1) {
					jpbProgessBar.setIndeterminate(false);
					jpbProgessBar.setMaximum(filesToImport.size());
					int i = 0;
					for (File fp: filesToImport) {
						jpbProgessBar.setString(fp.getAbsolutePath());
						FileInputStream fis = null;
						jpbProgessBar.setValue(i++);
						try {
				            fis = new FileInputStream(fp);    	     
						    int ret  = connector.stubImportMETS((String)jcbInOutGroup.getSelectedItem(), IOUtils.toByteArray(fis));
				    	    if (ret == 200) ok++;
						} catch (Exception q) {}
						finally {
							try {
								fis.close();
							} catch (Exception u) {}
						}
						if (!queue.get(title)) break;
					}	 
	   			 	jpbProgessBar.setVisible(false);		
	   			 	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("importsummary"), new Integer(ok).toString(), new Integer(filesToImport.size()).toString()), res.getString("importobj") + " |"+getTitle(), JOptionPane.INFORMATION_MESSAGE);										
				} else if (((Integer)proxy).intValue() == 2) {
					jpbProgessBar.setIndeterminate(false);
					jpbProgessBar.setMaximum(rows.length);
					for (int i = 0; i < rows.length; i++) {
						String pid = (String)jtData.getValueAt(rows[i], 1);
						jpbProgessBar.setValue(i);
						jpbProgessBar.setString(pid);
						try {
							byte[] stream = connector.stubGetMETS(pid);
							File fp = new File(dir+File.separator+(pid.replaceAll("[:]", "_"))+".xml");
							FileUtils.writeByteArrayToFile(fp, stream);
							ok++;
						} catch (Exception q) {}
						if (!queue.get(title)) break;
					}
	   			 	jpbProgessBar.setVisible(false);		
	   			 	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("exportsummary"), new Integer(ok).toString(), new Integer(rows.length).toString(), dir), res.getString("exportobj") + " |"+getTitle(), JOptionPane.INFORMATION_MESSAGE);										
				} else if (((Integer)proxy).intValue() == 3) {
					jpbProgessBar.setIndeterminate(false);
					jpbProgessBar.setMaximum(rows.length);
					for (int i = 0; i < rows.length; i++) {
						String pid = (String)jtData.getValueAt(rows[i], 1);
						String hdl = (String)jtData.getValueAt(rows[i], 6);	
						jpbProgessBar.setValue(i);
						jpbProgessBar.setString(pid);
						if (hdl.equals("-")) {
							try { 
							    if (connector.stubHdlCreate(null, pid) != null) ok++;							
							} catch (Exception q) {}
						}
						if (!queue.get(title)) break;
					}
	   			 	jpbProgessBar.setVisible(false);		
	   			 	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("handlesummary"), new Integer(ok).toString(), new Integer(rows.length).toString(), res.getString("hcreated")), getTitle(), JOptionPane.INFORMATION_MESSAGE);										
				} else if (((Integer)proxy).intValue() == 4) {
					jpbProgessBar.setIndeterminate(false);
					jpbProgessBar.setMaximum(rows.length);
					for (int i = 0; i < rows.length; i++) {
						String pid = (String)jtData.getValueAt(rows[i], 1);
						String hdl = (String)jtData.getValueAt(rows[i], 6);	
						jpbProgessBar.setValue(i);
						jpbProgessBar.setString(pid);
						if (hdl.startsWith(Common.HDL_PREFIX)) {
							try { 
								if (connector.stubHdlRefresh(hdl, pid) == 200) ok++;
							} catch (Exception q) {}
						}						
						if (!queue.get(title)) break;
					}
	   			 	jpbProgessBar.setVisible(false);		
	   			 	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("handlesummary"), new Integer(ok).toString(), new Integer(rows.length).toString(), res.getString("refreshed")), getTitle(), JOptionPane.INFORMATION_MESSAGE);										
				} else if (((Integer)proxy).intValue() == 5) {
					jpbProgessBar.setIndeterminate(false);
					jpbProgessBar.setMaximum(rows.length);
					for (int i = 0; i < rows.length; i++) {
						String pid = (String)jtData.getValueAt(rows[i], 1);
						String hdl = (String)jtData.getValueAt(rows[i], 6);						
						jpbProgessBar.setValue(i);
						jpbProgessBar.setString(pid);
						if (hdl.startsWith(Common.HDL_PREFIX)) {
							try { 
								if (connector.stubHdlDelete(hdl, pid) == 200) ok++;
							} catch (Exception q) {}
						}
						if (!queue.get(title)) break;
					}
	   			 	jpbProgessBar.setVisible(false);		
	   			 	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("handlesummary"), new Integer(ok).toString(), new Integer(rows.length).toString(), res.getString("deleted")), getTitle(), JOptionPane.INFORMATION_MESSAGE);										
				} else if (((Integer)proxy).intValue() == 6) {
					connector.stubTriggerCreatePrototypes();
	   			 	jpbProgessBar.setVisible(false);		
	   			 	JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("systempropsummary")), getTitle(), JOptionPane.INFORMATION_MESSAGE);										
			    }
				
				if (((Integer)proxy).intValue() != 2 && ((Integer)proxy).intValue() < 6) {
					CSwingWorker loader = new CSwingWorker(this, query);
					loader.execute();
				}
				jpbProgessBar.setIndeterminate(true);
				jpbProgessBar.setString("");
				jpbProgessBar.setStringPainted(false);
				queue.unset(title);
			}

		} catch (Exception e) {		
			log.error(e);
		}			
	}
	
	public void setup(String title, CiriloFrame parent, CServiceName sn) {
		try {
			this.parent = parent;
			connector = (FedoraConnector) CServiceProvider.getService(sn);
			this.title = title;
			this.subtitle = new String();
			this.prefix = StringUtils.substringAfter(title, ("▪ "));
			this.username = StringUtils.substringBefore(prefix, (" ▪"));
			this.host = StringUtils.substringAfter(prefix, ("▪ "));
		} catch (Exception e) {		
			log.error(e);
		}
	}

	
	public void toFront(Dimension dim, Point loc) {
		try { 
			moCore.show();
			
			if (dim == null) CBoundSerializer.load(this.getCoreDialog(), jtData, parent.getSize(), false);
			else {
				getCoreDialog().setLocation(loc);
				getCoreDialog().setSize(dim);
			}
			parent.handleFocusWindow(title);
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	public void show() throws CShowFailedException {
		try {
			setTitle(title);
					
			query = Common.QUERY;
			
			CBoundSerializer.load(this.getCoreDialog(), jtData, parent.getSize(), false);
			

			CSwingWorker loader = new CSwingWorker(this, query);
			loader.execute();
	
					
		} catch (Exception e) {		
			log.error(e);
		}

	}
	
	protected void opened() throws COpenFailedException {
		try {
		
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			columnNames = new String[] { "uuid", res.getString("pid"), res.getString("title"), res.getString("contentmodel"), res.getString("lastupdate"), res.getString("group"), res.getString("handle"), res.getString("derivedfrom")};
			
			
			delOptions = new String[] {res.getString("cancel"), res.getString("entomb"), res.getString("delete") };

			queue = new AnnulationStack();
			
			moGA = (CDefaultGuiAdapter)getGuiAdapter();	
			new CMouseListener(getCoreDialog(), this, "handleMouseClick");
						
			jtData = (JTable) getGuiComposite().getWidget("jtData");
			new CMouseListener(jtData, this, "handleMouseDoubleClick");

			CDialogTools.createButtonListener(this, "jbSubmit", "handleSubmitButton");
			CDialogTools.createButtonListener(this, "jbReset", "handleResetButton");

			CDialogTools.createButtonListener(this, "jbInOutSubmit", "handleInOutSubmitButton");
			CDialogTools.createButtonListener(this, "jbInOutClose", "handleInOutCloseButton");
			CDialogTools.createButtonListener(this, "jbSelectInOutDir", "handleSelectInOutDirButton");
	
			CDialogTools.createButtonListener(this, "jbHdlCreate", "handleHdlCreateButton");
			CDialogTools.createButtonListener(this, "jbHdlRefresh", "handleHdlRefreshButton");
			CDialogTools.createButtonListener(this, "jbHdlDelete", "handleHdlDeleteButton");
			CDialogTools.createButtonListener(this, "jbHdlClose", "handleHdlCloseButton");

			jbHandleDialog = (JPanel) getGuiComposite().getWidget("jbHandleDialog");
			jbInOutDialog = (Box) getGuiComposite().getWidget("jbInOutDialog");

			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			jcbContentModel.addItem(res.getString("allmodels"));
			for (PrototypeListEntry pt: connector.stubGetPrototypeList(null, true)) {
				jcbContentModel.addItem(StringUtils.substringAfterLast(pt.getModel(), "/"));
 			}
			jcbContentModel.addItemListener(new ItemListener() {
			    @Override
			    public void itemStateChanged(ItemEvent event) {
			    	 if (event.getStateChange() == ItemEvent.SELECTED) {
			    		 pushQuery();
			    		 refresh(query);
			    	 }
			    }

			});
			
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			jcbFulltext.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (jcbFulltext.getSelectedIndex() == 0 ) {
						 JTextComponent editor = (JTextComponent) jcbFulltext.getEditor().getEditorComponent();
						 editor.setText("");
					 }
				 }
			});			
			
			JComboBox jcbGroup = (JComboBox) getGuiComposite().getWidget("jcbGroup");
			jcbGroup.addItemListener(new ItemListener() {
			    @Override
			    public void itemStateChanged(ItemEvent event) {
			    	 if (event.getStateChange() == ItemEvent.SELECTED) {
			    		 pushQuery();
			    		 refresh(query);
			    	 }
			    }

			});

			createPopupMenu();
			resetDialogComboBoxes(true);
			pushQuery();
			
			isConnectedToHandleSystem = connector.stubIsConnectedToHandleSystem();
			
			PipelineStore  = new CConfigurationStore(connector.stubGetDatastream("o:cirilo.properties", "PIPELINE_DESCRIPTION"),
					connector.stubGetDatastream("o:cirilo.properties", "CONFIGURATION_DESCRIPTION"),
					connector.stubGetDatastream("o:cirilo.properties", "DEFAULT_PIPELINES"));
							
		} catch (Exception e) {
			log.error(e);
		}
			
	}
	
 	protected void activated() {
 		try {
 			parent.handleFocusWindow(title);
 		} catch (Exception e) {		
 			log.error(e);
 		}
    }
	
	public void save() {
		try {
			CBoundSerializer.save(this.getCoreDialog(), jtData, connector);
			/*
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		

			String ft = new String();
			for (int i = 1; i <jcbFulltext.getItemCount(); i++) {
				ft += (i == jcbFulltext.getSelectedIndex() ? "#" : "")+ (String)jcbFulltext.getItemAt(i).trim() + "!";  
			}
		
			props.setProperty("user", prefix+".Search.Fulltext", ft);
			props.setProperty("user", prefix+".Search.ContentModel", (String)jcbContentModel.getSelectedItem());
			props.setProperty("user", prefix+".Search.Group", (String)jcbGroup.getSelectedItem());

			props.saveProperties("user");
			*/
		} catch (Exception e) {		
			log.error(e);
		}
	}
	
	
	public boolean isConnectedToHandleSystem() {
		return isConnectedToHandleSystem;
	}
	
	protected void cleaningUp() {}
	
	public void handlerRemoved(CEventListener aoHandler) {}

	public void close() {
		try {
			
			queue.unset(title);
	
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askclose"), host, username), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
				((CiriloFrame) CServiceProvider.getService(ServiceNames.MAIN_WINDOW)).removeWindow(title);
				save();
				super.close();
			}
			

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

    private void createPopupMenu() {
    	
    	JMenuItem item;
    	try {
        	popup = new JPopupMenu();

	       	item = new JMenuItem(res.getString("edit"));
	    	item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleEdit(ae);
        		}
        	});
			popup.add(item);
			
        	item = new JMenuItem(res.getString("new"));
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleNew(ae);
        		}
        	});
			popup.add(item);
								
	       	item = new JMenuItem(res.getString("replace"));
	       	item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleReplace(ae);
        		}
        	});
			popup.add(item);
	
			popup.add(new JSeparator());

			JMenu sort = new JMenu(res.getString("sort"));
	       	item = new JMenuItem(res.getString("pid"));
	       	sort.add(item);
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSort(1);
        		}
        	});
	       	item = new JMenuItem(res.getString("title"));
	       	sort.add(item);
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSort(2);
        		}
        	});
	       	item = new JMenuItem(res.getString("contentmodel"));
	       	sort.add(item);
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSort(3);
        		}
        	});
	       	item = new JMenuItem(res.getString("lastupdate"));
	       	sort.add(item);
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSort(4);
        		}
        	});
	       	item = new JMenuItem(res.getString("group"));
	       	sort.add(item);
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSort(5);
        		}
        	});
	       	item = new JMenuItem(res.getString("handle"));
	       	sort.add(item);
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSort(6);
        		}
        	});
			
			/*
			* popup.add(sort);
			* popup.add(new JSeparator());
			*/
			
	       	item = new JMenuItem(res.getString("delete"));
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleEntomb(ae);
        		}
        	});
	       	item.setEnabled(hasSysopRights() || hasAdminRights());
	       	popup.add(item);
			
			PopupListener popupListener = new PopupListener();
			jtData.addMouseListener(popupListener);

    	} catch (Exception e) {
    		log.error(e);
    	}
    }
    
	private void resetDialogComboBoxes(boolean mode) {
		
		DefaultComboBoxModel<String> model;
		String s;
		
		try {
			
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			jcbFulltext.removeAllItems();
			jcbFulltext.insertItemAt(res.getString("notselected"), 0);
			
		    jcbFulltext.setEditable(true);

			String ft = props.getProperty("user",  prefix+".Search.Fulltext");
			int selected = 0;
			if (ft != null) {
				String fts[] = ft.split("[!]");
				for (int i = fts.length - 1; i > -1; i--) {
					if (fts[i].startsWith("#")) {
						jcbFulltext.insertItemAt(fts[i].substring(1), 1);
						if (mode) selected = i + 1;
					} else {
						jcbFulltext.insertItemAt(fts[i], 1);
					}
				}
				jcbFulltext.setSelectedIndex(mode ? selected : 0);
			}
			
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));	
			s = props.getProperty("user",  prefix+".Search.ContentModel"); 			
 			if (mode && s != null) jcbContentModel.setSelectedItem(s); else jcbContentModel.setSelectedIndex(0);

		    model = new DefaultComboBoxModel<String>();
//		    model.addElement(res.getString("allgroups"));
		    JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));	    
		    JComboBox<String> jcbInOutGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbInOutGroup"));	    
			for (Element el: XMLUtils.getChildren("//group", XMLUtils.createDocumentFromByteArray(connector.stubGetUserInfoAsRDF()))) {
				if(!el.getText().isEmpty()) model.addElement(el.getText());
				if(!el.getText().isEmpty()) jcbInOutGroup.addItem(el.getText());
			}
			jcbGroup.setModel(model);
			
			s = props.getProperty("user", "General.DefaultGroup");
			if (s != null && !s.isEmpty()) jcbInOutGroup.setSelectedItem(s);
			
 			s = props.getProperty("user",  prefix+".Search.Group");
			if (s != null) jcbGroup.setSelectedItem(s); else jcbGroup.setSelectedIndex(0);
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void addItem() {
		try {
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			ComboBoxModel<String> model = jcbFulltext.getModel();
			for (int i = 0; i < model.getSize(); i++) {
			   if (((String)model.getElementAt(i)).equals((String)jcbFulltext.getSelectedItem())) return;   
			}
			if (!((String)jcbFulltext.getSelectedItem()).isEmpty()) {
				jcbFulltext.insertItemAt((String)jcbFulltext.getSelectedItem(), 1);
			} else {
				jcbFulltext.setSelectedIndex(0);
			}
		} catch (Exception e) {
			// log.error(e);
		}	
	}	
	
	public void setEnable(boolean mode) {
		
		try {
			((JButton)getGuiComposite().getWidget("jbInOutSubmit")).setText(mode ? res.getString("submit") : res.getString("cancel"));;
			((JButton)getGuiComposite().getWidget("jbHdlCreate")).setText(mode ? res.getString("submit") : res.getString("cancel"));;
			((JButton)getGuiComposite().getWidget("jbHdlRefresh")).setEnabled(mode);
			((JButton)getGuiComposite().getWidget("jbHdlDelete")).setEnabled(mode);
		} catch (Exception e) {	
		}

	}

	public void setOrder(int column) {
		try {
			TableModel dm = jtData.getModel();
			jtData.setAutoCreateRowSorter(true);
			TableRowSorter sorter = new TableRowSorter<TableModel>(dm);
			jtData.setRowSorter(sorter);
						
			List<RowSorter.SortKey> sortKeys = new ArrayList<>();	
			sortKeys.add(new RowSorter.SortKey(column, column != 6 && column != 4  ? SortOrder.ASCENDING : SortOrder.DESCENDING ));
			sorter.setSortKeys(sortKeys);
			sorter.sort();
		} catch (Exception e) {}
	}
	
	private void pushQuery() {
		try {
			JComboBox<String> jcbFulltext = ((JComboBox<String>) getGuiComposite().getWidget("jcbFulltext"));		
			JComboBox<String> jcbContentModel = ((JComboBox<String>) getGuiComposite().getWidget("jcbContentModel"));
			JComboBox<String> jcbGroup = ((JComboBox<String>) getGuiComposite().getWidget("jcbGroup"));		
	
			query[0] = jcbFulltext.getSelectedIndex() > 0 ? (String)jcbFulltext.getSelectedItem() : null;
			query[1] = jcbContentModel.getSelectedIndex() > 0 ? (String)jcbContentModel.getSelectedItem() : null;
			query[2] = jcbGroup.getSelectedIndex() > 0 ? (String)jcbGroup.getSelectedItem() : null;	
			query[3] = "10000000";
			query[4] = "false";
		} catch (Exception e) {}
	}

	public void setUIFont() {
		Utils.setRowSelection(jtData);
	}

	private class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			if (me.getButton() == me.BUTTON3) {
				show(me);
			}
		}
		
		public void mouseReleased(MouseEvent me) {
			if (me.getButton() == me.BUTTON3) {
				show(me);
			}
		}
		
		private void show(MouseEvent me) {
			try {
			 	int row = jtData.rowAtPoint(me.getPoint());
			 	if (me.isPopupTrigger()) popup.show(me.getComponent(), me.getX(), me.getY());
			} catch (Exception e) {}
		}
	}

}
