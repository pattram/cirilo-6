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

import voodoosoft.jroots.business.IBasicEntitySet;

import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;


/**
 * Model class for easy combination and handling of display- and internal key-columns.
 * <code>CDynamicComboBoxModel</code> uses <code>IEntitySet</code> objects as underlying data source.
 * When items of <code>JComboBox</code> objects are stored in database tables they are mostly
 * identified by internal, often numeric, key columns.
 * <code>CDynamicComboBoxModel</code> takes the place of the interpreter between key values and
 * displayed items.
 * @see voodoosoft.jroots.dialog.CKeyValueListModel
 */
public class CDynamicComboBoxModel extends DefaultComboBoxModel
{
   /**
    * Creates new combo box model of the given <code>IEntitySet</code>.
    * @param aoData <code>IEntitySet</code> supplying both the key and display columns
    * @param asKeyColumn column working as unique key to identify combo box items
    * @param asDisplayColumn column to display for each key value
    * @param abEmptyItem if true, an empty item will be inserted
    * @throws Exception
    */
   public CDynamicComboBoxModel(IBasicEntitySet aoData, String asKeyColumn, String asDisplayColumn,
                                boolean abEmptyItem) throws Exception
   {
      msDisplayColumn = asDisplayColumn;
      msKeyColumn = asKeyColumn;
      mbEnabled = true;

      if (!mbRetrieved && !mbRetrieving && mbEnabled)
      {
         retrieve(aoData, abEmptyItem);
      }
   }

   /**
    * Selects corresponding item of the specified key value.
    * @param anObject
    */
   public void setSelectedItemKey(Object anObject)
   {
      super.setSelectedItem(moKeyMap.get(anObject));
   }

   /**
    * Returns key value of the currently selected item.
    * @return item key
    */
   public Object getSelectedItemKey()
   {
      return moDisplayMap.get(getSelectedItem());
   }
   
   public Object getItem(Object key)
   {
      return moKeyMap.get(key);
   }

   /**
    * Removes all items from the combo box and the internal key- and value-maps.
    */
   public void removeAllElements()
   {
      mbRetrieved = false;

      if (moDisplayMap != null)
      {
         moDisplayMap.clear();
      }

      if (moKeyMap != null)
      {
         moKeyMap.clear();
      }

      super.removeAllElements();
   }

   private void retrieve(IBasicEntitySet aoData, boolean abEmptyItem)
                  throws Exception
   {
      Object loKey;
      Object loDisplay;
      mbRetrieving = true;

      removeAllElements();

      if (abEmptyItem)
      {
         addElement("");
      }

      moDisplayMap = new HashMap(aoData.getRowCount());
      moKeyMap = new HashMap(aoData.getRowCount());

      for (int i = 1; i <= aoData.getRowCount(); i++)
      {
         loKey = aoData.getColumn(i, msKeyColumn);
         loDisplay = aoData.getColumn(i, msDisplayColumn);

         this.addElement(loDisplay);
         moDisplayMap.put(loDisplay, loKey);
         moKeyMap.put(loKey, loDisplay);
      }

      mbRetrieved = true;
      mbRetrieving = false;
   }

   private String msKeyColumn;
   private String msDisplayColumn;
   private HashMap moDisplayMap;
   private HashMap moKeyMap;
   private boolean mbRetrieved;
   private boolean mbRetrieving;
   private boolean mbEnabled;
}
