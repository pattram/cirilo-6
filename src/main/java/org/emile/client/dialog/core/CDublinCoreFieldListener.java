package org.emile.client.dialog.core;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class CDublinCoreFieldListener implements KeyListener {

	public void keyTyped( KeyEvent e ) {
		char ch = e.getKeyChar();

		if (ch == '&' || ch == '<' || ch == '>') {
			Toolkit.getDefaultToolkit().beep();
			e.consume();
		} else {
			return;
		}
	}
	
	public void keyPressed( KeyEvent e ) {}
	public void keyReleased( KeyEvent e ) {}
}
