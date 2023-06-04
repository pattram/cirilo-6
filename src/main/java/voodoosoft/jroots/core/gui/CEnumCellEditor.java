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

import java.util.List;

import javax.swing.*;

/**
 * Table cell editor for fixed value lists displayed in combo boxes.
 */
public class CEnumCellEditor extends CAbstractCellEditor 
{
   private JComboBox moEditor;
   

   /**
    * Creates new editor and adds the given value list as items to the internal <code>JComboBox</code>.
    * @param values
    * @throws Exception
    */
   public CEnumCellEditor(List values) throws Exception
   {
      moEditor = new JComboBox(values.toArray());      
   }
        
   /**
    * Creates new editor and adds the given value array as items to the internal <code>JComboBox</code>.
    * @param values
    * @throws Exception
    */
   public CEnumCellEditor(Object[] values) throws Exception
   {
      moEditor = new JComboBox(values);
   }
   
   public Object getCellEditorValue()
   {     
      Object ret = null;
      
      ret = moEditor.getSelectedItem();
      setNewValue(ret);
         
      return ret;
   }
   
   
   public Component getTableCellEditorComponent(JTable table, Object value,
                      boolean isSelected,
                      int row, int column)
   {
      
     moEditor.setSelectedItem(value);
      
      return moEditor;
   }     
  
   
}
