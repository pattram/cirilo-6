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


import javax.swing.*;



/**
 * <p>Handler methods must have the following signature:
 * <li><code>public boolean handleMethod(JComponent source)</code>
 */
public class CInputListener extends CEventListener
{
   private class CVerifier extends InputVerifier
   {
      public boolean verify(JComponent e)
      {
         Object ret = invokeHandler(e);
         return ((Boolean)ret).booleanValue();
      }
   }
   
   /**
    * Installs new event handler for the given <code>JComponent</code>.
    * @param aoCom <code>JComponent</code> as event source
    * @param aoEventHandler object handling events
    * @param asHandleMethod method to invoke
    * @throws CListenerInstallFailedException
    */
   public CInputListener(JComponent aoCom, IEventHandler aoEventHandler,
                          String asHandleMethod) throws CListenerInstallFailedException
   {
      super(aoEventHandler, asHandleMethod, "javax.swing.JComponent");

      install(aoCom);
   }

 

   /**
    * Internal method called by the constructor.
    * @param aoCom
    */
   public void install(JComponent aoCom)
   {
      if (moSource != null)
      {
         remove();
      }

      moSource = aoCom;
      moSource.setInputVerifier(new CVerifier());
   }

   public void remove()
   {
      super.remove();

      if (moSource != null)
      {
         moSource.setInputVerifier(null);
      }    
   }

   private JComponent moSource;   
}
