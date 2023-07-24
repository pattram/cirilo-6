package org.emile.client;

import java.awt.Container;
import java.awt.Font;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.emile.client.Setup;
import org.emile.client.dialog.SearchDialog;
import org.emile.client.dialog.core.AppletFrame;
import org.emile.client.gui.GuiSplashDialog;
import org.emile.client.utils.UserProfile;
import org.emile.client.utils.Utils;
import org.emile.cm4f.models.UploadOptions;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;


import org.emile.client.business.SpingerFactory;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.emile.cirilo.utils.LogSet;

import voodoosoft.jroots.application.CApplication;
import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.core.gui.CEventListener;

public class Cirilo extends CiriloApplet {

	static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(Cirilo.class);

	
	public static void main(String[] args) {
	
		
		PropertyConfigurator.configure(Cirilo.class.getResource("log4j.properties"));

		if (args.length > 0 &&  args.length < 10) {
			
			log.info("Usage: Spinger -u <username> -p <passwd> -o <owner> -r <repository hostname> -l <path to object store> [-v] [-t <default: o:prototype.spectral>] [-c <default: o:prototype.context>]"); 
			return;	
			
		} else if ( args.length >= 10) {
			try {

				
				
				String host = null;
				String path = null; 
				String user = null;
				String passwd = null;
				String group = null;
				String prototype = null;
				String context = null;
				boolean verbose = false;
				
				// Hack to set default file encoding on runtime
				
				System.setProperty("file.encoding", "UTF-8");
				
				try {
					Field charset = Charset.class.getDeclaredField("defaultCharset");
					charset.setAccessible(true);
					charset.set(null,null);
				} catch (Exception f) {}
				
				log.info("Spinger 1.0.0.1 - (c) 2022 by Johannes Stigler - Batch Ingester for Spectral Objects");					
				
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("-u")) user = args[i+1];
					if (args[i].equals("-p")) passwd = args[i+1];
					if (args[i].equals("-o")) group = args[i+1];
					if (args[i].equals("-r")) host = args[i+1];
					if (args[i].equals("-l")) path = args[i+1];
					if (args[i].equals("-t")) prototype = args[i+1];
					if (args[i].equals("-c")) context = args[i+1];
					if (args[i].equals("-v")) verbose = true;
				}
			
				SpingerFactory repo = new SpingerFactory(verbose);
				
				if (repo.connect(host, user, passwd, group, prototype, context)) {
				
					CPropertyService props = new CPropertyService();					
					Properties cirilo = new Properties();
					UserProfile profile = new UserProfile();
	
					cirilo.load(Cirilo.class.getResourceAsStream("cirilo.properties"));
					
					props.cacheProperties(cirilo, "system");	
					props.cacheProperties(profile.get(), "user");

					props.saveProperties("user");

					CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.SPECTRAL_UPLOADOPTIONS.toString())), ServiceNames.SPECTRAL_UPLOADOPTIONS);
					CServiceProvider.addService(new HashMap<String, Boolean>(), ServiceNames.ANNULATION_STACK);

					log.info("Connection to Fedora repository on <"+host+"> with user <" +  user + "> ok");
					repo.ingest(path);			
				} else {
					log.info("Invalid credentials for authentication on <"+host+">");
				}
				
				log.info("Spinger terminated normally");
				
			} catch (Exception e) {}
			
		} else {
			AppletFrame frame = new AppletFrame(new CiriloApplet());
			frame.setSize(0, 0);
			frame.setVisible(true);
		}
	
	}
	

}

class CiriloApplet extends JApplet {

	static final long serialVersionUID = 2L;
	private CiriloWindow oWnd;
	
	public void init() {
		Container contentPane = getContentPane();
		oWnd = new CiriloWindow(getParameter("Applet"));
		contentPane.add(oWnd);
	}

	public void stop() {}

}

class CiriloWindow extends JPanel {

    private static Logger log = Logger.getLogger(CiriloWindow.class);
	private static final long serialVersionUID = 3L;

	public CiriloWindow(String applet) {
		CiriloApp loCiriloApp;

		loCiriloApp = new CiriloApp();
		loCiriloApp.begin();
	}


	public void stop() {}

	public class CiriloApp extends CApplication {

		public CiriloApp() {
			setApp(this);
		}
		
		public void begin() {

			try {
			
				
				GuiSplashDialog loSplash = new GuiSplashDialog();
				loSplash.setVisible(true);

				// Hack to set default file encoding on runtime
				
				System.setProperty("file.encoding", "UTF-8");

				try {
					Field charset = Charset.class.getDeclaredField("defaultCharset");
					charset.setAccessible(true);
					charset.set(null,null);
				} catch (Exception f) {}
				
				PropertyConfigurator.configure(Cirilo.class.getResource("log4j.properties"));
	        
				Properties cirilo = new Properties();
				UserProfile profile = new UserProfile();
				CPropertyService props = new CPropertyService();
			
				cirilo.load(Cirilo.class.getResourceAsStream("cirilo.properties"));
									
				props.cacheProperties(cirilo, "system");	
				props.cacheProperties(profile.get(), "user");

				props.saveProperties("user");
				
				setProperties(props);
									
				CServiceProvider.addService(props, ServiceNames.PROPERTIES);

				ResourceBundle res = Common.getResourceBundle("en");	
				CServiceProvider.addService(res, ServiceNames.RESOURCES);
							
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.CUBE_UPLOADOPTIONS.toString())), ServiceNames.CUBE_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.GML_UPLOADOPTIONS.toString())), ServiceNames.GML_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.LIDO_UPLOADOPTIONS.toString())), ServiceNames.LIDO_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.MEI_UPLOADOPTIONS.toString())), ServiceNames.MEI_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.METS_UPLOADOPTIONS.toString())), ServiceNames.METS_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.ONTOLOGY_UPLOADOPTIONS.toString())), ServiceNames.ONTOLOGY_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.RDF_UPLOADOPTIONS.toString())), ServiceNames.RDF_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.RDO_UPLOADOPTIONS.toString())), ServiceNames.RDO_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.RTI_UPLOADOPTIONS.toString())), ServiceNames.RTI_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.SKOS_UPLOADOPTIONS.toString())), ServiceNames.SKOS_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.SPECTRAL_UPLOADOPTIONS.toString())), ServiceNames.SPECTRAL_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.STORY_UPLOADOPTIONS.toString())), ServiceNames.STORY_UPLOADOPTIONS);
				CServiceProvider.addService(new UploadOptions(props.getProperty("user", ServiceNames.TEI_UPLOADOPTIONS.toString())), ServiceNames.TEI_UPLOADOPTIONS);			
				
				CServiceProvider.addService(new HashMap<String, Boolean>(), ServiceNames.ANNULATION_STACK);

				System.setProperty("file.encoding", "UTF-8");
				
				CEventListener.setBlocked(true);

				Setup.AccessManager();
				Setup.Dialogs(Setup.GUI());

				Thread.sleep(5000);
				
				loSplash.dispose();

				JFrame loFrame = (JFrame) CServiceProvider.getService(ServiceNames.MAIN_WINDOW);
				loFrame.setVisible(true);
				loFrame.toFront();	

				CEventListener.setBlocked(false);
			
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				exit();
			}
		}
		
		public void end() {
			JFrame loFrame;
			try {
				loFrame = (JFrame) CServiceProvider.getService(ServiceNames.MAIN_WINDOW);
				CEventListener.removeListener(loFrame);
				loFrame.dispose();
				System.exit(0);
			}
			catch (Exception e) {
				log.error(e);
			}
			finally {
				CEventListener.printLog();
				CServiceProvider.removeAllServices();
			}
		}

		private void exit() {
			end();
			System.exit(0);
		}
		
		private void setProperties(CPropertyService props) {
			
			try {
				
				for (String key: Common.PROPERTIES)  {				
					if (props.getProperty("user", key) == null) {
						props.setProperty("user", key, props.getProperty("system", key));
					}
				}
				
				if (props.getProperty("user", "FontName") == null) props.setProperty("user", "FontName", "LucidaGrande");
				if (props.getProperty("user", "FontStyle") == null) props.setProperty("user", "FontStyle", "0");
				if (props.getProperty("user", "FontSize") == null) props.setProperty("user", "FontSize", "12");
				
				Utils.setUIFont(new javax.swing.plaf.FontUIResource( props.getProperty("user", "FontName"), 
																	 new Integer(props.getProperty("user", "FontStyle")), 
																	 new Integer(props.getProperty("user", "FontSize"))));	
				
				props.saveProperties("user");
				
				String thecure = props.getProperty("user", "IReallyLikeTheCure");	
				javax.swing.UIManager.setLookAndFeel(thecure == null || !thecure.equals("true") ? new FlatLightLaf() : new FlatDarkLaf() );		

					
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
}
	


