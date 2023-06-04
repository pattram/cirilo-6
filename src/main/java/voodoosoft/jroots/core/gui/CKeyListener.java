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

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * Listener for <code>KeyEvent</code> events of <code>Component</code> objects.
 * <p>Handler methods must have the following signature:
 * <li><code>public void handleMethod(KeyEvent aoEvent)</code>
 */
public class CKeyListener extends CEventListener implements KeyListener
{
   /**
    * Installs new key event handler for the given <code>Component</code>.
    * Events of type <code>keyTyped</code> will be processed, not <code>keyPressed</code>
    * @param aoCom <code>Component</code> as event source
    * @param aoEventHandler event handler object
    * @param asHandleMethod method to invoke for every <code>KeyEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CKeyListener(Component aoCom, IEventHandler aoEventHandler, String asHandleMethod)
                throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.KeyEvent");

      mbListenKeyPressed = false;

      install(aoCom);
   }

   /**
    * Installs new key event handler for the given <code>Component</code>.
    * @param aoCom <code>Component</code> as event source
    * @param aoEventHandler event handler object
    * @param asHandleMethod method to invoke for every <code>KeyEvent</code>
    * @param abListenToKeyPressed if true, events of type <code>keyPressed</code> will be processed, otherwise <code>keyTyped</code>
    * @throws CListenerInstallFailedException
    */
   public CKeyListener(Component aoCom, IEventHandler aoEventHandler, String asHandleMethod,
                       boolean abListenToKeyPressed) throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.awt.event.KeyEvent");

      mbListenKeyPressed = abListenToKeyPressed;

      install(aoCom);
   }

   /**
    * Internal method called by the constructor.
    * @param aoCom
    */
   public void install(Component aoCom)
   {
      if (moCom != null)
      {
         remove();
      }

      moCom = aoCom;
      moCom.addKeyListener(this);
   }

   public void keyPressed(KeyEvent e)
   {
      if (mbListenKeyPressed)
      {
         invokeHandler(e);
      }
   }

   public void keyReleased(KeyEvent e)
   {
      //TODO: Implement this java.awt.event.KeyListener method
   }

   public void keyTyped(KeyEvent e)
   {
      if (!mbListenKeyPressed)
      {
         invokeHandler(e);
      }
   }

   public void remove()
   {
      super.remove();

      if (moCom != null)
      {
         moCom.removeKeyListener(this);
      }
   }

   private Component moCom;
   private boolean mbListenKeyPressed;
}
