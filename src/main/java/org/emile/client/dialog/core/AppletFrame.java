package org.emile.client.dialog.core;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Container;
import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JFrame;

public class AppletFrame extends JFrame implements AppletStub, AppletContext {

	static final long serialVersionUID = 4L;

	public AppletFrame(Applet applet) {
		this.applet = applet;
		Container contentPane = getContentPane();
		contentPane.add(this.applet);
		this.applet.setStub(this);
	}

	public void setVisible(boolean mode) {
		applet.init();
		super.setVisible(true);
		applet.start();
		this.dispose();
	}

	public boolean isActive() {
		return true;
	}


	public URL getDocumentBase() {
		return null;
	}


	public URL getCodeBase() {
		return null;
	}

	public String getParameter(String name) {
		return "";
	}

	public AppletContext getAppletContext() {
		return this;
	}

	public void appletResize(int width, int height) { }


	public AudioClip getAudioClip(URL url) {
		return null;
	}

	public Image getImage(URL url) {
		return null;
	}

	public Applet getApplet(String name) {
		return null;
	}

	public Enumeration getApplets() {
		return null;
	}

	public void showDocument(URL url) { }

	public void showDocument(URL url, String target) { }

	public void showStatus(String status) { }

	public void setStream(String key, InputStream stream) { }

	public InputStream getStream(String key) {
		return null;
	}

	public Iterator getStreamKeys() {
		return null;
	}
	
	private Applet applet;

}