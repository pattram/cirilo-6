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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;


/**
 * Listener for <code>ItemEvent</code> events of <code>AbstractButton</code> or <code>JComboBox</code> objects.
 * <p>Handler methods must have the following signature:
 * <li><code>public void handleMethod(ItemEvent aoEvent)</code>
 */
public class CItemListener extends CEventListener implements ItemListener
{
   /**
    * Installs new event handler for the given <code>AbstractButton</code>.
    * @param aoButton <code>AbstractButton</code> as event source
    * @param aoEventHandler event handler
    * @param asHandleMethod method to invoke for every <code>ItemEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CItemListener(AbstractButton aoButton, IEventHandler aoEventHandler, String asHandleMethod)
                 throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.ItemEvent");

      install(aoButton);
   }

   /**
    * Installs new event handler for the given <code>JComboBox</code>.
    * @param aoCombo <code>JComboBox</code> as event source
    * @param aoEventHandler event handler
    * @param asHandleMethod method to invoke for every <code>ItemEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CItemListener(JComboBox aoCombo, IEventHandler aoEventHandler, String asHandleMethod)
                 throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.ItemEvent");

      install(aoCombo);
   }

   /**
    * Installs new event handler for the given <code>JComboBox</code> and the specified item state.
    * @param aoCombo
    * @param aoEventHandler
    * @param asHandleMethod
    * @param aiItemState
    * @throws CListenerInstallFailedException
    */
   public CItemListener(JComboBox aoCombo, IEventHandler aoEventHandler, String asHandleMethod,
                        int aiItemState) throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.ItemEvent");

      miItemState = aiItemState;
      install(aoCombo);
   }

   public void install(AbstractButton aoButton)
   {
      if (moButton != null)
      {
         remove();
      }

      moButton = aoButton;
      moButton.addItemListener(this);
   }

   /**
    * Internal method called by the constructor.
    * @param aoCombo
    */
   public void install(JComboBox aoCombo)
   {
      if (moComboBox != null)
      {
         remove();
      }

      moComboBox = aoCombo;
      moComboBox.addItemListener(this);
   }

   public void itemStateChanged(ItemEvent e)
   {
      if (miItemState != -1)
      {
         if (miItemState == e.getStateChange())
         {
            invokeHandler(e);
         }
      }
      else
      {
         invokeHandler(e);
      }
   }

   public void remove()
   {
      super.remove();

      if (moButton != null)
      {
         moButton.removeItemListener(this);
      }

      if (moComboBox != null)
      {
         moComboBox.removeItemListener(this);
      }
   }

   private AbstractButton moButton = null;
   private JComboBox moComboBox = null;
   private int miItemState = -1;
}
