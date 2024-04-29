/*
 * VOODOOSOFT SOFTWARE LICENSE
 *
 * Copyright (c) 2002-2003, Stefan Wischnewski, www.voodoosoft.de
 * All rights reserved.
 *
 * You are granted the right to use, modify and redistribute this software
 * provided that one of the following conditions is met:
 *
 * (a) your project is open source licensed under one of the approved licenses of
 *     the Open Source Initiative (www.opensource.org)
 * (b) you did purchase a commercial license from the copyright holder
 * (c) you have any other special agreement with the copyright holder
 *
 * In either case, redistribution and use in source and binary forms, with or
 * without modification, is only permitted provided that:
 *
 * (a) redistributions of source code retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 * (b) neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS IN THE HOPE
 * THAT IT WILL BE USEFUL, BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 */



/**
 * Title:        Voodoo Soft Java Framework<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Stefan Wischnewski<p>
 * Company:      Voodoo Soft<p>
 * @author Stefan Wischnewski
 * @version 1.0
 */
package voodoosoft.jroots.core.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * Document class for limiting the maximal number of allowed input characters.
 */
public class CLimitDocument extends PlainDocument
{
   /**
    * Creates new <code>CLimitDocument</code>
    * <p>Install the document by <code>javax.swing.text.JTextComponent#setDocument</code>.
    * @param aiLimit number of allowed character
    * @param abBeep
    */
   public CLimitDocument(int aiLimit, boolean abBeep)
   {
      miLimit = aiLimit;
      mbBeep = abBeep;
   }

   public CLimitDocument(int aiLimit, boolean abBeep, boolean upperCase)
   {
      miLimit = aiLimit;
      mbUpperCase = upperCase;
   }

   /**
    * Changes character limit to the given value.
    * @param aiLimit
    */
   public void setLimit(int aiLimit)
   {
      miLimit = aiLimit;
   }

   public void insertString(int offset, String str, AttributeSet a)
                     throws BadLocationException
   {
      if (str.length() + getLength() <= miLimit)
      {
         super.insertString(offset, str, a);
      }
      else if (mbBeep)
      {
         // TODO
         //getToolkit().beep();
      }
   }

   private int miLimit;
   private boolean mbBeep, mbUpperCase;
}
