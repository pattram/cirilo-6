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

import voodoosoft.jroots.core.gui.CEventListener;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;


/**
 * <code>CChangeManager</code> notifies listeners when components have been modified (entered text or selected items for instance)
 * Every object implementing <code>IDirtyListener</code> can act as listener.
 * Components of the following classes are observed:
 * <li><code>JTextComponent</code>
 * <li><code>JToggleButton</code>
 * <li><code>JComboBox</code>
 * <li><code>JList</code>
 */
public class CChangeManager implements DocumentListener, ItemListener,                                        ListSelectionListener
{
   public CChangeManager(IDirtyListener aoListener)
   {
      moListener = aoListener;
   } 

   /**
    * Adds (recursively) listener to the specified component and its children.
    * @param aoParentComponent
    */
   public void addDirtyListener(Component aoParentComponent)
   {
      Component[] loComponents;
      Container loContainer;
      JComponent loJComponent;
      JTextComponent loText;
      int li;

      if (aoParentComponent instanceof JTextComponent)
      {
         loText = (JTextComponent) aoParentComponent;
         loText.getDocument().addDocumentListener(this);
      }
      else if (aoParentComponent instanceof JToggleButton)
      {
         ((JToggleButton) aoParentComponent).addItemListener(this);
      }
      else if (aoParentComponent instanceof JComboBox)
      {
         ((JComboBox) aoParentComponent).addItemListener(this);
      }
      else if (aoParentComponent instanceof JList)
      {
         ((JList) aoParentComponent).addListSelectionListener(this);
      }

      if (aoParentComponent instanceof Container)
      {
         loContainer = (Container) aoParentComponent;
         loComponents = loContainer.getComponents();

         // durch alle Child-Components
         for (li = 0; li < loComponents.length; li++)
         {
            addDirtyListener(loComponents[li]);
         }
      }
   } 


   public void changedUpdate(DocumentEvent e)
   {
      notifyListener();
   }

   public void insertUpdate(DocumentEvent e)
   {
      notifyListener();
   }

   public void itemStateChanged(ItemEvent e)
   {
      notifyListener();
   }

   /**
    * Uninstalls (recursively) listener of the specified component and its children.
    * @param aoParentComponent
    */
   public void remove(Component aoParentComponent)
   {
      removeDirtyListener(aoParentComponent);
      moListener = null;
   }

   public void removeUpdate(DocumentEvent e)
   {
      notifyListener();
   }

   public void valueChanged(ListSelectionEvent e)
   {
      notifyListener();
   }

   private void notifyListener()
   {
      if (!CEventListener.isBlocked())
      {
         moListener.setDirty(true);
      }
   }

   private void removeDirtyListener(Component aoParentComponent)
   {
      Component[] loComponents;
      Container loContainer;
      JComponent loJComponent;
      JTextComponent loText;
      int li; 

      if (aoParentComponent instanceof JTextComponent)
      {
         loText = (JTextComponent) aoParentComponent;
         loText.getDocument().removeDocumentListener(this);
      }
      else if (aoParentComponent instanceof JToggleButton)
      {
         ((JToggleButton) aoParentComponent).removeItemListener(this);
      }
      else if (aoParentComponent instanceof JComboBox)
      {
         ((JComboBox) aoParentComponent).removeItemListener(this);
      }
      else if (aoParentComponent instanceof JList)
      {
         ((JList) aoParentComponent).removeListSelectionListener(this);
      }

      if (aoParentComponent instanceof Container)
      {
         loContainer = (Container) aoParentComponent;
         loComponents = loContainer.getComponents();

         // durch alle Child-Components
         for (li = 0; li < loComponents.length; li++)
         {
            removeDirtyListener(loComponents[li]);
         }
      }
   }

   private IDirtyListener moListener;
}
