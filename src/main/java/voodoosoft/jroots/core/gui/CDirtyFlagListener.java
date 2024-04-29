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

import voodoosoft.jroots.core.*;



/**
 * Event listener for diry-flag events.
 * <p>Handler methods must have the following signature:
 * <li><code>public boolean handleMethod(JComponent source)</code>
 * @see voodoosoft.jroots.core#IDirtyFlagModifier
 */
public class CDirtyFlagListener extends CEventListener implements IDirtyFlagListener
{  
   /**
    * Installs new event handler for the given <code>IDirtyFlagModifier</code>.
    * @param aoCom <code>IDirtyFlagModifier</code> as event source
    * @param aoEventHandler object handling events
    * @param asHandleMethod method to invoke
    * @throws CListenerInstallFailedException
    */
   public CDirtyFlagListener(IDirtyFlagModifier aoCom, IEventHandler aoEventHandler,
                          String asHandleMethod) throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "java.lang.Boolean");

      install(aoCom);
   }

   public void setDirtyFlag(boolean dirtyFlag)
   {
      invokeHandler(new Boolean(dirtyFlag));
   }

   /**
    * Internal method called by the constructor.
    * @param aoCom
    */
   public void install(IDirtyFlagModifier aoCom)
   {
      if (moSource != null)
      {
         remove();
      }

      aoCom.addDirtyFlagListener(this);
      moSource = aoCom;      
   }

   private Object moSource;   
}
