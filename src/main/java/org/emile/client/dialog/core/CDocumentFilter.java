package org.emile.client.dialog.core;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.emile.client.Common;

public class CDocumentFilter extends DocumentFilter {
	
	  @Override
	  public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

		  if (text.length() > 1) {
			  
			  if (!text.startsWith(Common.THIS+"/")) {
				  super.remove(fb, 0, fb.getDocument().getLength());
				  super.insertString(fb, 0, Common.THIS+"/"+text, attrs);
			  }
			  
		  } else {
			  
			if (!fb.getDocument().getText(0, fb.getDocument().getLength()).startsWith(Common.THIS+"/")) {
				super.remove(fb, 0, fb.getDocument().getLength());
				super.insertString(fb, 0, Common.THIS+"/", attrs);
				offset += Common.THIS.length()+1;
			}
			super.replace(fb, offset, length, text, attrs);
		  }

	  
	  }

	  @Override
	  public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException {
		  super.insertString(fb, offset, text, attrs);
		  if (!fb.getDocument().getText(0, fb.getDocument().getLength()).startsWith(Common.THIS+"/")) {
				super.remove(fb, 0, fb.getDocument().getLength());
				super.insertString(fb, 0, Common.THIS+"/", attrs);
		 } 
	  }

	  @Override
	  public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
	  }

	}