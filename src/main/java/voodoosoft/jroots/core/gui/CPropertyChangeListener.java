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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;


/**
 * Listener for <code>PropertyChangeEvent</code> events of <code>JComponent</code> objects.
 * <p>Handler methods must have the following signature:
 * <li><code>public void handleMethod(PropertyChangeEvent aoEvent)</code>
 */
public class CPropertyChangeListener extends CEventListener implements PropertyChangeListener
{
   /**
    * Installs specified object method as new handler for the given <code>JComponent</code>.
    * @param aoComponent <code>JComponent</code> as event source
    * @param aoEventHandler handler object
    * @param asHandleMethod method to invoke for the specified <code>PropertyChangeEvent</code>
    * @param asProperty property name to listen to
    * @throws CListenerInstallFailedException
    */
   public CPropertyChangeListener(JComponent aoComponent, IEventHandler aoEventHandler,
                                  String asHandleMethod, String asProperty)
                           throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.beans.PropertyChangeEvent");

      install(aoComponent, asProperty);
   }

   /**
    * Installs specified object method as new handler for the given <code>JComponent</code>.
    * @param aoComponent <code>JComponent</code> as event source
    * @param aoEventHandler handler object
    * @param asHandleMethod method to invoke for every <code>PropertyChangeEvent</code>
    * @throws CListenerInstallFailedException
    */
   public CPropertyChangeListener(JComponent aoComponent, IEventHandler aoEventHandler,
                                  String asHandleMethod)
                           throws CListenerInstallFailedException
   {
      this(aoComponent, aoEventHandler, asHandleMethod, null);
   }

   /**
     * Internal method called by the constructor.
     * @param aoComponent
     * @param asProperty
     */
   public void install(JComponent aoComponent, String asProperty)
   {
      if (moComponent != null)
      {
         remove();
      }

      moComponent = aoComponent;

      if (asProperty != null)
      {
         moComponent.addPropertyChangeListener(asProperty, this);
      }
      else
      {
         moComponent.addPropertyChangeListener(this);
      }
   }

   public void propertyChange(PropertyChangeEvent e)
   {
      invokeHandler(e);
   }

   public void remove()
   {
      super.remove();

      if (moComponent != null)
      {
         moComponent.removePropertyChangeListener(this);
      }
   }

   private JComponent moComponent;
}
