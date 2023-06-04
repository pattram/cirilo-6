package org.emile.client.dialog.core;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CPIDFieldListener implements KeyListener {
	
	public void keyTyped( KeyEvent e ) {
		char ch = e.getKeyChar();

		if ( ch >= 'A' && ch <= 'Z' ) {
			e.setKeyChar( (char) ( ch + 32 ) );
			return;
		}
		if ( ch >= 'a' && ch <= 'z' ) {
			return;
		}
		if ( ch >= '0' && ch <= '9' ) {
			return;
		}
		if ( ch == (char) 8 ) {
			return;
		}
		if ( ch == '-' ) {
			return;
		}
		if ( ch == '.' ) {
			return;
		}
		Toolkit.getDefaultToolkit().beep();
		e.consume();

	}

	public void keyPressed( KeyEvent e ) {}

	public void keyReleased( KeyEvent e ) {}
}