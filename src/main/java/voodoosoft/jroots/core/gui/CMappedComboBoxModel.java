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

import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;


/**
 * ComboBoxModel handling two values: one to display and the other as internal key value.
 */
public class CMappedComboBoxModel extends DefaultComboBoxModel
{
   public CMappedComboBoxModel()
   {
      moKeyMap = new HashMap();
      moDisplayMap = new HashMap();
   }

   public void setSelectedItemKey(Object aoKey)
   {
      super.setSelectedItem(moKeyMap.get(aoKey));
   }

   public Object getSelectedItemKey()
   {
      return moDisplayMap.get(super.getSelectedItem());
   }

   public void addMapping(Object aoKey, Object aoDisplayValue)
   {
      moKeyMap.put(aoKey, aoDisplayValue);
      moDisplayMap.put(aoDisplayValue, aoKey);

      super.addElement(aoDisplayValue);
   }

   /*   public static class CKeyLookup
      {
         public Object moKey;
         public String msDisplayValue;
         public int miIndex;

         public boolean equals(Object obj)
         {
            return obj.equals(moKey);
         }
      }
   */
   private HashMap moKeyMap;
   private HashMap moDisplayMap;
}
