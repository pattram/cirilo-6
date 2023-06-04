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

package voodoosoft.jroots.core.gui;

import java.awt.Component;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 * Table cell editor utilizing {@link javax.swing.text.MaskFormatter} objects.
 */
public class CMaskCellEditor extends CAbstractCellEditor 
{
   private JFormattedTextField moField;
   
   /**
    * Creates new masked editor.
    * @param cellMask valid edit mask to pass to the internal <code>MaskFormatter</code>.
    * @throws Exception
    */
   public CMaskCellEditor(String cellMask) throws Exception
   {
      MaskFormatter mf = new MaskFormatter(cellMask);
      DefaultFormatterFactory ff = new DefaultFormatterFactory(mf);
      moField = new JFormattedTextField(ff);
   }
   
   public void selectAll()
   {
      moField.selectAll();
   }
   
   public Object getCellEditorValue()
   {     
      try
      {
         moField.commitEdit(); 
      }
      catch (ParseException e)
      {
         return null;
         //throw new RuntimeException(e);
      }

      setNewValue(moField.getValue());
      
      return moField.getValue();
   }
   
   
   public Component getTableCellEditorComponent(JTable table, Object value,
                      boolean isSelected,
                      int row, int column)
   {       
      moField.setValue(value);
      moField.selectAll();
      return moField;
   }     
  
   
}
