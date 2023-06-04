package org.emile.client.dialog.core;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import org.emile.client.Common;

public class CDefaultMessageDialog {


	public static void notYetImplemented() {
		
		JOptionPane.showMessageDialog(null, "Developers hint: This module isn't yet implemented.", Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void notYetImplemented(ActionEvent ev) {
		
		JOptionPane.showMessageDialog(null, "Developers hint: The module <" + ((ActionEvent) ev).getActionCommand().toLowerCase() + "> isn't yet implemented.", Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void notYetImplemented(ActionEvent ev, String dialog) {
		
		JOptionPane.showMessageDialog(null, "Developers hint: The module <" + ((ActionEvent) ev).getActionCommand().toLowerCase() + "> for dialog <" + dialog + "> isn't yet implemented.", Common.MAIN_WINDOW_HEADER, JOptionPane.INFORMATION_MESSAGE);
	}


	
}
	