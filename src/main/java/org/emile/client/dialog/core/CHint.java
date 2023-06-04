package org.emile.client.dialog.core;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.emile.client.Common;

public class CHint extends Box {
	
	private JLabel hint = new JLabel();
	
	public CHint() {
		
		super(BoxLayout.X_AXIS);
		init();
	}
		
	public CHint(String text) {
		
		super(BoxLayout.X_AXIS);
		init();
		hint.setText(text);
	}

	public void setText(String text) {
		hint.setText(text);
	}
	
	private void init() {
		
		add(new JLabel(Common.GAMS_BUTTON));	
		add (new JLabel(" "));
		add( hint );
	}

}
