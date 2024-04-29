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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Timer;


/**
 * Listener for <code>ActionEvent</code> events of <code>AbstractButton</code> or <code>Timer</code> objects.
 * <p>Handler methods must have the following signature:
 * <li><code>public void handleMethod(ActionEvent aoEvent)</code>
 */
public class CActionListener extends CEventListener implements ActionListener
{
   /**
    * Installs new event handler for the given <code>AbstractButton</code>.
    * @param aoButton <code>AbstractButton</code> as event source
    * @param aoEventHandler object handling events
    * @param asHandleMethod method to invoke for every <code>ActionEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CActionListener(AbstractButton aoButton, IEventHandler aoEventHandler,
                          String asHandleMethod) throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.ActionEvent");

      install(aoButton);
   }

   /**
    * Installs new event handler for the given <code>Timer</code>.
    * @param aoTimer <code>Timer</code> as event source
    * @param aoEventHandler object handling events
    * @param asHandleMethod method to invoke for every <code>ActionEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CActionListener(Timer aoTimer, IEventHandler aoEventHandler, String asHandleMethod)
                   throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.ActionEvent");

      install(aoTimer);
   }

   public void actionPerformed(ActionEvent e)
   {
      invokeHandler(e);
   }

   /**
    * Internal method called by the constructor.
    * @param aoButton
    */
   public void install(AbstractButton aoButton)
   {
      if (moButton != null)
      {
         remove();
      }

      moButton = aoButton;
      moButton.addActionListener(this);
   }

   /**
    * Internal method called by the constructor.
    * @param aoTimer
    */
   public void install(Timer aoTimer)
   {
      if (moTimer != null)
      {
         remove();
      }

      moTimer = aoTimer;
      moTimer.addActionListener(this);
   }

   public void remove()
   {
      super.remove();

      if (moButton != null)
      {
         moButton.removeActionListener(this);
      }

      if (moTimer != null)
      {
         moTimer.removeActionListener(this);
      }
   }

   private AbstractButton moButton;
   private Timer moTimer;
}
