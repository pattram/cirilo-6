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

import java.util.ArrayList;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.core.IDirtyFlagModifier;


/**
 * Abstract base class for table cell editors capable of firing dirty-flag events. 
 */
public abstract class CAbstractCellEditor extends AbstractCellEditor implements TableCellEditor, IDirtyFlagModifier
{
   public CAbstractCellEditor()
   {
      listeners = new ArrayList();
   }
   
   public boolean stopCellEditing()
    {
       fireEditingStopped();
       
       if (moNewValue != null)
       {
          if(!moNewValue.equals(moOldValue))
           setDirtyFlag(true);
       }
       else if (moOldValue != null)
       {
          if (!moOldValue.equals(moNewValue))
             setDirtyFlag(true);          
       }       
       
       setNewValue(null);
       setOldValue(null);
       return true;    
    }
    
   /**
    * Registers listener to be notified for dirty flag events.
    * @param listener
    * @see #setDirtyFlag
    */
   public void addDirtyFlagListener(IDirtyFlagListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Removes specified listener.
    * @param listener
    */   
   public void removeDirtyFlagListener(IDirtyFlagListener listener)
   {
      listeners.remove(listener);
   }
      
   /**
    * Notifies all registers <code>IDirtyFlagListener</code> of the dirty flag event.
    * @param dirtyFlag
    */
   public void setDirtyFlag(boolean dirtyFlag)
   {
      for (int i = 0; i < listeners.size(); i++)
      {
         ((IDirtyFlagListener)listeners.get(i)).setDirtyFlag(dirtyFlag);
      }
   }

   protected void setOldValue(Object oldValue)
   {
      this.moOldValue = oldValue;
   }

   protected void setNewValue(Object newValue)
   {
      this.moNewValue = newValue;
   }
   
   private ArrayList listeners;
   private Object moOldValue, moNewValue;
}
