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


package voodoosoft.jroots.application;

import voodoosoft.jroots.core.CObject;


/**
 * Main abstract class to represent the whole application.
 * It is not a necessity to use <code>CApplication</code>, however it's intention
 * is to have a uniform entry point.
 * Does nothing by default.
 * Place the <code>main</code> method in your derivation of <code>CApplication</code>.
 */
public abstract class CApplication extends CObject
{
   /**
    * Constructor.
    */
   protected CApplication()
   {
   }

   /**
    * Static method to returns the application's main object.
    * @return application object
    * @see #setApp
    */
   public static CApplication getApp()
   {
      return soApp;
   }

   /**
    * Method to place the application's initialization code.
    */
   public void begin()
   {
   }

   /**
    * Method to place code to properly shut down the application.
    */
   public void end()
   {
      System.runFinalization();
   }

   public boolean mayEnd()
   {
      return true;
   }

   /**
    * Static method to store the one application object for easy global access.
    * @param aoApp application object
    * @see #getApp
    */
   protected static void setApp(CApplication aoApp)
   {
      soApp = aoApp;
   }

   private static CApplication soApp;
}
