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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Listener for <code>MouseEvent</code> events of type <code>Component</code>.
 * <p>Handler methods must have the following signature:
 * <li><code>public void handleMethod(MouseEvent aoEvent, Integer aoEventType)</code>
 * <p>The argument <code>aoEventType</code> will be one of the following values:
 * <li><code>MouseEvent.MOUSE_CLICKED</code>
 * <li><code>MouseEvent.MOUSE_PRESSED</code>
 * <li><code>MouseEvent.MOUSE_RELEASED</code>
 * <li><code>MouseEvent.MOUSE_ENTERED</code>
 * <li><code>MouseEvent.MOUSE_EXITED</code>
 *
 */
public class CMouseListener extends CEventListener implements MouseListener
{
   /**
    * Installs new event handler for the given <code>Component</code>.
    * @param aoCom <code>Component</code> as event source
    * @param aoEventHandler event handler
    * @param asHandleMethod method to invoke for every <code>MouseEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CMouseListener(Component aoCom, IEventHandler aoEventHandler, String asHandleMethod)
                  throws CListenerInstallFailedException
   {
      super(aoEventHandler);

      setupHandleMethod(asHandleMethod,
                        new Class[] { java.awt.event.MouseEvent.class, Integer.TYPE });

      // install listener
      moCom = null;
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
      moCom.addMouseListener(this);
   }

   public void mouseClicked(MouseEvent e)
   {
      setTypeArg(MouseEvent.MOUSE_CLICKED);
      invokeHandler(e);
   }

   public void mouseEntered(MouseEvent e)
   {
      setTypeArg(MouseEvent.MOUSE_ENTERED);
      invokeHandler(e);
   }

   public void mouseExited(MouseEvent e)
   {
      setTypeArg(MouseEvent.MOUSE_EXITED);
      invokeHandler(e);
   }

   public void mousePressed(MouseEvent e)
   {
      setTypeArg(MouseEvent.MOUSE_PRESSED);
      invokeHandler(e);
   }

   public void mouseReleased(MouseEvent e)
   {
      setTypeArg(MouseEvent.MOUSE_RELEASED);
      invokeHandler(e);
   }

   public void remove()
   {
      super.remove();

      if (moCom != null)
      {
         moCom.removeMouseListener(this);
      }
   }

   /*
      public void mouseDragged(MouseEvent e)
      {
         setTypeArg(MouseEvent.MOUSE_DRAGGED);
         invokeHandler(e);
      }

      public void mouseMoved(MouseEvent e)
      {
         setTypeArg(MouseEvent.MOUSE_MOVED);
         invokeHandler(e);
      }
   */
   private void setTypeArg(int aoType)
   {
      moArgs[1] = new Integer(aoType);

      //      moArgs[1] = aoType;
   }

   private Component moCom;
}
