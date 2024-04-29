/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.client;

import org.emile.client.dialog.*;
import org.emile.client.dialog.core.CDefaultMessageDialog;
import org.emile.client.dialog.core.CMenuItem;
import org.emile.client.utils.Utils;

import org.emile.cirilo.fedora.FedoraConnector;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.*;
import voodoosoft.jroots.core.gui.*;
import voodoosoft.jroots.dialog.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.ResourceBundle;

import javax.swing.*;

public class CiriloFrame extends JFrame implements IEventHandler {

	static final long serialVersionUID = 31L;

	private static Logger log = Logger.getLogger(CiriloFrame.class);

	private JDesktopPane moPane;
	private Timer moGarbageTimer;
	private ResourceBundle res;
	private CDefaultAccessManager moAccMan;
	private IGuiComposite loMenu;
	private int iActiveWindow;
	
	private RepositoryDialog[] loMainWindows = {null, null, null, null, null, null};
	private String[] keys = {null, null, null, null, null, null};
 	private int[] Event = {KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5};
	
 	public CiriloFrame(String asTitle, CGuiManager aoGuiManager) throws Exception {

 		super(asTitle);

		try {
			
			JMenuItem loItem;
			
			log.info("Cirilo 6 started");
			
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

			// the garbage timer will ask the business factory every 10 minutes
			// to free unused objects
			
			moGarbageTimer = new Timer(600000, null);
			moGarbageTimer.setRepeats(true);
			new CActionListener(moGarbageTimer, this, "handleGarbageTimer");
			moGarbageTimer.start();

			loMenu = aoGuiManager.getGuiComposite("FrameMenu");
								
			moAccMan = (CDefaultAccessManager) CServiceProvider.getService(ServiceNames.ACCESS_MANAGER);
			moAccMan.setGuiAdapter(aoGuiManager.getAdapter(loMenu));
			CDefaultGuiAdapter adapter = (CDefaultGuiAdapter) aoGuiManager.getAdapter(loMenu);
			adapter.setAccessManager(moAccMan);

			loItem = CMenuItem.get(loMenu,"repository");
			loItem.setText(res.getString(loItem.getText()));
			
			loItem = CMenuItem.get(loMenu,"object");
			loItem.setText(res.getString(loItem.getText()));
			
			loItem = CMenuItem.get(loMenu,"extras");
			loItem.setText(res.getString(loItem.getText()));
			
			loItem = CMenuItem.get(loMenu,"window");
			loItem.setText(res.getString(loItem.getText()));

			loItem = CMenuItem.get(loMenu,"help");
			loItem.setText(res.getString(loItem.getText()));

			loItem = CMenuItem.get(loMenu,"repository.open");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleOpen");
			
			loItem = CMenuItem.get(loMenu,"repository.close");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleClose");

			loItem = CMenuItem.get(loMenu,"repository.search");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleSearch");
			
			loItem = CMenuItem.get(loMenu,"repository.ingest");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleIngest");
			
			loItem = CMenuItem.get(loMenu,"repository.aggregate");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleAggregate");
			
			loItem = CMenuItem.get(loMenu,"repository.import");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleObjectEvent");
				
			loItem = CMenuItem.get(loMenu,"repository.export");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleObjectEvent");
				
			loItem = CMenuItem.get(loMenu,"repository.manageue");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleManageUserEnvironments");
			
			loItem = CMenuItem.get(loMenu,"repository.managepl");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handlePipelines");
	
			loItem = CMenuItem.get(loMenu,"repository.manage");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleObjectEvent");
			
			loItem = CMenuItem.get(loMenu,"repository.publish");
			loItem.setText(res.getString(loItem.getName())+ " ...");
			new CActionListener(loItem, this, "handlePublish");
			
			loItem = CMenuItem.get(loMenu,"repository.publishgroup");
			loItem.setText(res.getString(loItem.getName())+ " ...");
			new CActionListener(loItem, this, "handlePublishGroup");

			loItem = CMenuItem.get(loMenu,"repository.maintain");
			loItem.setText(res.getString(loItem.getName()));

			JMenuItem item;
			JMenu maintain = (JMenu) loMenu.getWidget("repository.maintain");	
			maintain.setEnabled(false);
			
			item = maintain.getItem(0);
			item.setText(res.getString("updateproperties"));
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
           			handleUpdateSystemProperties(ae);     		
        		}
        	});
	  
			item = maintain.getItem(2);
			item.setText(res.getString("reorganizetriplestore"));
			item.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleReorganizeTriplestores(ae);    		
        		}
        	});
			
			loItem = CMenuItem.get(loMenu,"repository.exit");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handleExit");
				
			loItem = CMenuItem.get(loMenu,"object.edit");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
			new CActionListener(loItem, this, "handleObjectEvent");
			
			loItem = CMenuItem.get(loMenu,"object.new");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			new CActionListener(loItem, this, "handleObjectEvent");
			
			loItem = CMenuItem.get(loMenu,"object.replace");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
			new CActionListener(loItem, this, "handleObjectEvent");
						
			loItem = CMenuItem.get(loMenu,"object.delete");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
			new CActionListener(loItem, this, "handleObjectEvent");
			
			loItem = CMenuItem.get(loMenu,"extras.harvest");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleHarvest");
			
			loItem = CMenuItem.get(loMenu,"extras.review");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleReview");
			
			loItem = CMenuItem.get(loMenu,"extras.view");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleView");
			
			loItem = CMenuItem.get(loMenu,"extras.preferences");
			loItem.setText(res.getString(loItem.getName()));
			loItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
			new CActionListener(loItem, this, "handlePreferences");
			
			loItem = CMenuItem.get(loMenu,"help.about");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleAbout");
			
			loItem = CMenuItem.get(loMenu,"help.guide");
			loItem.setText(res.getString(loItem.getName()));
			new CActionListener(loItem, this, "handleGuide");
	
			setJMenuBar((JMenuBar) loMenu.getRootComponent());
			moPane = new JDesktopPane();
			
			this.getContentPane().add(moPane);
			this.pack();
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);	
		}
		
		this.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					close();
				}
				public void windowClosed(WindowEvent we) {}
			});		
	}


	public void setVisible(boolean mode) {
		moAccMan.execRules(getAccessContext());
		super.setVisible(true);
	}

	public void close() {
		try {
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("askexit")), getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {				
				
				moGarbageTimer.stop();
				moGarbageTimer = null;
				
				log.info("Cirilo 6 terminated normally");
				
				super.dispose();
				
				// CApplication.getApp().end();
			}
 		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public IAccessContext getAccessContext() {
		CDefaultAccessContext loCxt = null;
		try {
		}
		catch (Exception ex) {}
		return loCxt;
	}

	public JDesktopPane getDesktopPane() {
		return moPane;
	}
	

	public void handleOpen(ActionEvent qe) {
		int dp = getNextWindow();
		try {
			if (dp != -1) { 
		    	CPropertyService props = (CPropertyService) CServiceProvider.getService( ServiceNames.PROPERTIES );
		    	LoginDialog loLogin = (LoginDialog) CServiceProvider.getService(DialogNames.LOGIN_DIALOG);
				loLogin.open();
				if (loLogin.getServiceName() != null) {
					FedoraConnector connector = (FedoraConnector) CServiceProvider.getService(loLogin.getServiceName());
					CDialogManager dm  = (CDialogManager) CServiceProvider.getService(ServiceNames.DIALOG_MANAGER);
					loMainWindows[dp] = (RepositoryDialog) dm.getDialog(DialogNames.REPOSITORY_DIALOG[dp]);
					loMainWindows[dp].setup(dp+" ▪ "+connector.getCurrentUser()+" ▪ "+connector.getHostname(), this, loLogin.getServiceName());
					loMainWindows[dp].open();	
					keys[dp] = loLogin.getKey();
					iActiveWindow = dp;
					setWindowMenu();
				}
			}
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleClose(ActionEvent ae) {
		try {
			loMainWindows[iActiveWindow].close();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleSearch(ActionEvent ae) {
		try {
			SearchDialog dlg = (SearchDialog) CServiceProvider.getService(DialogNames.SEARCH_DIALOG);
			String header = StringUtils.substringBefore(loMainWindows[iActiveWindow].getTitle(), " ▪") + " ▪ " + res.getString("searchrepo") +StringUtils.substringAfterLast(loMainWindows[iActiveWindow].getTitle(), "▪");
			dlg.setup(header, loMainWindows[iActiveWindow]);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handleIngest(ActionEvent ae) {
		try {
			IngestObjectDialog dlg = (IngestObjectDialog) CServiceProvider.getService(DialogNames.INGESTOBJECT_DIALOG);
			String header = StringUtils.substringBefore(loMainWindows[iActiveWindow].getTitle(), " ▪") + " ▪ " + res.getString("ingestobjects") + " ▪ " + StringUtils.substringAfter(loMainWindows[iActiveWindow].getTitle(), "▪");
			dlg.setup(header, loMainWindows[iActiveWindow]);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleAggregate(ActionEvent ae) {
		try {
			AggregateDialog dlg = (AggregateDialog) CServiceProvider.getService(DialogNames.AGGREGATE_DIALOG);
			String header = StringUtils.substringBefore(loMainWindows[iActiveWindow].getTitle(), " ▪") + " ▪ " + res.getString("maggregate") + " ▪ " + StringUtils.substringAfter(loMainWindows[iActiveWindow].getTitle(), " ▪");
			dlg.setup(header, loMainWindows[iActiveWindow]);
			dlg.open();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
	}


	public void handlePipelines(ActionEvent ae) {
		try {
			XSLDialog dlg = (XSLDialog) CServiceProvider.getService(DialogNames.XSL_DIALOG);
			dlg.setup(loMainWindows[iActiveWindow], null, null);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handleExit(ActionEvent ae) {
			close();
	}
	
	public void handleHarvest(ActionEvent ae) {
		try {
			HarvesterDialog dlg = (HarvesterDialog) CServiceProvider.getService(DialogNames.HARVESTER_DIALOG);
			dlg.setup(loMainWindows[iActiveWindow]); 
			dlg.open();
		} catch (Exception e) {
			log.error(e);
		}
	}
		
	public void setUIFont() {
		try {
					
			CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			
			Utils.setUIFont(new javax.swing.plaf.FontUIResource( props.getProperty("user", "FontName"), 
					 new Integer(props.getProperty("user", "FontStyle")), 
					 new Integer(props.getProperty("user", "FontSize"))));				
						
			for (int i = 0; i<loMainWindows.length; i++) {
				loMainWindows[i].setUIFont();
			}
			
			SwingUtilities.updateComponentTreeUI(this);

		}
		
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleManageUserEnvironments(ActionEvent ae) {
		try {
			UserEnvironmentDialog dlg = (UserEnvironmentDialog) CServiceProvider.getService(DialogNames.USERENVIRONMENT_DIALOG);
			String header = StringUtils.substringBefore(loMainWindows[iActiveWindow].getTitle(), "|") + " | " + res.getString("environmentmanagement") + " | " + StringUtils.substringAfter(loMainWindows[iActiveWindow].getTitle(), "|");
			dlg.setup(header, loMainWindows[iActiveWindow]);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleReview(ActionEvent ae) {
		try {
			ReviewDialog dlg = (ReviewDialog) CServiceProvider.getService(DialogNames.REVIEW_DIALOG);
			dlg.setup(loMainWindows[iActiveWindow].getFedoraConnector());
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleView(ActionEvent ae) {
		try {
			LogDialog dlg = (LogDialog) CServiceProvider.getService(DialogNames.LOG_DIALOG);
			String header = StringUtils.substringBefore(loMainWindows[iActiveWindow].getTitle(), "|") + " | " + res.getString("logview") + " " + StringUtils.substringAfter(loMainWindows[iActiveWindow].getTitle(), "|");
			dlg.setup(header, loMainWindows[iActiveWindow].getFedoraConnector());
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	

	public void handleUpdateSystemProperties(ActionEvent ae) {
		try {
			loMainWindows[iActiveWindow].handleEvent(ae);
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handleReorganizeStorageLayer(ActionEvent ae) {
		try {
			loMainWindows[iActiveWindow].handleEvent(ae);
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleReorganizeTriplestores(ActionEvent ae) {
		try {		
			ReorganizerDialog dlg = (ReorganizerDialog) CServiceProvider.getService(DialogNames.REORGANIZE_DIALOG);
			dlg.setup(keys[iActiveWindow], loMainWindows[iActiveWindow]); 
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}


	public void handlePreferences(ActionEvent ae) {
		try {		
			PreferencesDialog dlg = (PreferencesDialog) CServiceProvider.getService(DialogNames.PREFERENCES_DIALOG);
			dlg.setup(this, getActiveWindows() > 0 ? loMainWindows[iActiveWindow].getHostname() : null, null);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handlePublish(ActionEvent ae) {
		try {		
			PublishDialog dlg = (PublishDialog) CServiceProvider.getService(DialogNames.PUBLISH_DIALOG);
			dlg.setup(keys[iActiveWindow], loMainWindows[iActiveWindow]);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handlePublishGroup(ActionEvent ae) {
		try {		
			PublishGroupDialog dlg = (PublishGroupDialog) CServiceProvider.getService(DialogNames.PUBLISHGROUP_DIALOG);
			dlg.setup(keys[iActiveWindow], loMainWindows[iActiveWindow]);
			dlg.open();
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handleObjectEvent(ActionEvent ae) {
		try {
			loMainWindows[iActiveWindow].handleEvent(ae);
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleAbout(ActionEvent ae) {
		try {
			JOptionPane.showMessageDialog(null, res.getString("aboutbanner"), Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handleGuide(ActionEvent ae) {
    	try {	   	
	    	ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
	    	Desktop desktop = Desktop.getDesktop();

			if(desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(new URI("https://gams.uni-graz.at/doc/"));			
			} else {
				JOptionPane.showMessageDialog(null, Common.msgFormat(res.getString("nodesktop")), Common.MAIN_WINDOW_HEADER, JOptionPane.WARNING_MESSAGE);
			}
	    } catch (Exception e) {
	    	e.printStackTrace();     
	    }	
			
	}


	public void handlerRemoved(CEventListener aoHandler) {
	}
	
	public void handleGarbageTimer(ActionEvent ae) {
		try {
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	public void handleCloseQuestion(ActionEvent ae) {
		close();
	}
	
	public void handleFocusWindow(ActionEvent ae) {
		try {
			Integer dp = new Integer(StringUtils.substringBefore(((ActionEvent)ae).getActionCommand(), " ▪"));
			loMainWindows[dp].toFront(null, null);
			setActiveWindow(dp);
			setMenu();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleFocusWindow(String ae) {
		try {
			Integer dp = new Integer(StringUtils.substringBefore(ae, " ▪"));
			setActiveWindow(dp);
			setMenu();
		}
		catch (Exception e) {
			log.error(e);
		}
	}
		
	public void handleArrangeWindows(ActionEvent ae) {
		try {
			Point loc = null;
			int n = getActiveWindows();
			int x = 5; int y = 5;
					
			Dimension size = new Dimension ((int) getSize().getWidth() - 30*n, (int) getSize().getHeight() - 60*n);	
			int dp = -1;
			
			for (int i = 0; i<loMainWindows.length; i++) {
				if (loMainWindows[i] != null) {
					loc = new Point(x,y);
					loMainWindows[i].getCoreDialog().setLocation(loc);
					loMainWindows[i].getCoreDialog().setSize(size);					
					dp = i; setActiveWindow(dp);
					x += 25; y += 25;
				}
			}
			if (dp > -1) { loMainWindows[dp].toFront(size, loc);}
		} catch (Exception e) {
		  log.error(e);
		}
	}
		
	public void handleSaveWindowPosition(ActionEvent ae) {
		try {
			loMainWindows[iActiveWindow].save();
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void setActiveWindow(int n) {
		iActiveWindow = n;
	}
	
	public void setWindowMenu() {
		try {
			JMenuItem window = CMenuItem.get(loMenu,"window");
			window.removeAll();
			
			if (getActiveWindows() > 0) {
				JMenuItem arrange = new JMenuItem(res.getString("arrange"));
				new CActionListener(arrange, this, "handleArrangeWindows");
				arrange.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
				window.add(arrange);
				
				JMenuItem ensure = new JMenuItem(res.getString("saveposition"));
				new CActionListener(ensure, this, "handleSaveWindowPosition");
				window.add(ensure);
				
				window.add(new JSeparator());
			
				for (int i = 0; i<loMainWindows.length;i++) {
					if (loMainWindows[i] != null) {
						JMenuItem item = new JMenuItem(loMainWindows[i].getTitle());
						item.setAccelerator(KeyStroke.getKeyStroke(Event[i], InputEvent.ALT_MASK));
						new CActionListener(item, this, "handleFocusWindow");
						window.add(item, -1);
					}
				}
			}
			setMenu();
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void removeWindow(String ActionCommand) {
		try {
			Integer dp = new Integer(StringUtils.substringBefore(ActionCommand, " ▪"));
			loMainWindows[dp] = null;
			
			setWindowMenu();
			
			dp = getNextActiveWindow();
			if (dp >-1) ((RepositoryDialog)loMainWindows[dp]).toFront(null, null);
			setActiveWindow(dp);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	
	public void setMenu() {
		
		
		boolean hasEntries = false;
		int n = getActiveWindows(); 
		boolean status = n > 0;
			
		try {
			if (iActiveWindow > -1 && loMainWindows[iActiveWindow]  != null ) hasEntries = loMainWindows[iActiveWindow].hasEntries() ;
			CMenuItem.setEnabled(loMenu, "repository.open", n < 6);
			CMenuItem.setEnabled(loMenu, "repository.close", status);
			CMenuItem.setEnabled(loMenu, "repository.ingest", status && (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "repository.import", status);
			CMenuItem.setEnabled(loMenu, "repository.export", status && hasEntries);
			CMenuItem.setEnabled(loMenu, "repository.aggregate", status && (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "repository.manageue", status  && (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "repository.managepl", status  && (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "repository.manage", status && hasEntries && loMainWindows[iActiveWindow].hasSysopRights()  && loMainWindows[iActiveWindow].isConnectedToHandleSystem());
			CMenuItem.setEnabled(loMenu, "repository.maintain", status  && loMainWindows[iActiveWindow].hasSysopRights());
			CMenuItem.setEnabled(loMenu, "repository.publish", status &&  (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "repository.publishgroup", status &&  (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "repository.search", status);
			CMenuItem.setEnabled(loMenu, "object.edit", status && hasEntries);
			CMenuItem.setEnabled(loMenu, "object.new", status);
			CMenuItem.setEnabled(loMenu, "object.replace", status && hasEntries  && (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "object.delete", status && hasEntries  && (loMainWindows[iActiveWindow].hasAdminRights() || loMainWindows[iActiveWindow].hasSysopRights()));
			CMenuItem.setEnabled(loMenu, "extras.harvest", status && loMainWindows[iActiveWindow].hasSysopRights());
			CMenuItem.setEnabled(loMenu, "extras.review", status);
			CMenuItem.setEnabled(loMenu, "extras.view", status);
		} catch (Exception e) {}
	}

	
	private int getNextWindow() {
		for (int i = 0; i<loMainWindows.length; i++) 
			if (loMainWindows[i] == null) return i;
		return -1;
	}
	
	private int getNextActiveWindow() {
		for (int i = 0; i<loMainWindows.length; i++) 
			if (loMainWindows[i] != null) return i;
		return -1;
	}
	
	private int getActiveWindows() {
		int n = 0;	
		for (int i = 0; i<loMainWindows.length; i++) if (loMainWindows[i] != null) n++;
		return n;
	}

}

