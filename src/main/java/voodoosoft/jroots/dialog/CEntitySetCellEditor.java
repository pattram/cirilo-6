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

package voodoosoft.jroots.dialog;

import java.awt.Component;

import javax.swing.*;
import javax.swing.JComboBox;


import voodoosoft.jroots.business.IBasicEntitySet;
import voodoosoft.jroots.core.gui.CAbstractCellEditor;

/**
 * Table cell editor using a <code>JComboBox</code> populated with <code>IBasicEntitySet</code> items. 
 */
public class CEntitySetCellEditor extends CAbstractCellEditor 
{
   private CDynamicComboBoxModel moModel;
   private JComboBox moCombo;
   
   /**
    * Creates new <code>CEntitySetCellEditor</code>.
    * @param data combobox item source 
    * @param keyColumn entity set column to use as key
    * @param displayColumn user friendly column to display
    * @param emptyItem if true, add empty item
    * @throws Exception
    */
   public CEntitySetCellEditor(IBasicEntitySet data, String keyColumn, String displayColumn, boolean emptyItem) throws Exception
   {
      moModel = getModel(data, keyColumn, displayColumn, emptyItem);
      moCombo = new JComboBox(moModel);
   }
   
   public Object getCellEditorValue()
   {      
      setNewValue(moModel.getSelectedItemKey());
      return moModel.getSelectedItemKey();
   }
    
   public Component getTableCellEditorComponent(JTable table, Object value,
                      boolean isSelected,
                      int row, int column)
   { 
      moModel.setSelectedItemKey(value);
      moCombo.setSelectedItem(value);      
      return moCombo;
   }     
             
   public CDynamicComboBoxModel getModel()
   {
      return moModel;
   }          
   
   private static CDynamicComboBoxModel getModel(IBasicEntitySet data, String keyColumn, String displayColumn, boolean emptyItem) throws Exception
   {      
      CDynamicComboBoxModel loComboModel = new CDynamicComboBoxModel(data, keyColumn, displayColumn, emptyItem);
      
      return loComboModel;
   }

}
