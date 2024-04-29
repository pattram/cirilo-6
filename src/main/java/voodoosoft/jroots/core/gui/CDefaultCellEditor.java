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
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;

/**
 * Table cell editor for strings, numbers and dates utilizing <code>Format</code> objects. 
 */
public class CDefaultCellEditor extends CAbstractCellEditor 
{
   private JTextField moEditor;
   private Format moFormat;

   /**
    * Creates new editor for the given <code>Format</code>.
    * @param format
    * @throws Exception
    */
   public CDefaultCellEditor(Format format) throws Exception
   {
      this();
      this.moFormat = format;
   }
   
   /**
    * Creates new editor using default <code>Format</code> instances.
    * The following methods are called to get to the <code>Format</code>.
    * <p>{@link java.text.NumberFormat#getInstance} for (sub-)classes of <code>Number</code>.
    * <p>{@link java.text.DateFormat#getDateInstance} for (sub-)classes of <code>java.util.Date</code>.
    * @throws Exception
    */
   public CDefaultCellEditor() throws Exception
   {
      moEditor = new JTextField();      
   }        
   
   public Object getCellEditorValue()
   {     
      Object ret = null;
      
      try
      {
         if (moFormat != null)
            ret = moFormat.parseObject(moEditor.getText());
         else
            ret = moEditor.getText();
      }
      catch (ParseException e)
      {
        ret = null;
      }
         
      setNewValue(ret);
      
      return ret;
   }
   
   
   public Component getTableCellEditorComponent(JTable table, Object value,
                      boolean isSelected,
                      int row, int column)
   {
      
      if (value != null)
      {
         if (moFormat == null)
         {
            Class colClass = table.getModel().getColumnClass(column);
            if (Number.class.isAssignableFrom(colClass))            
               moFormat = NumberFormat.getInstance();
            else if (java.util.Date.class.isAssignableFrom(colClass))
               moFormat = DateFormat.getDateInstance();
         }
         if (moFormat == null)
            moEditor.setText(value.toString());
         else
            moEditor.setText(moFormat.format(value));
      }
      else
      {
         moEditor.setText(null);      
      }
      
      setOldValue(value);
      
      return moEditor;
   }     
  
   
}
