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


package voodoosoft.jroots.data;

import org.apache.log4j.Logger;

import voodoosoft.jroots.message.*;


/**
 * Abstract base class for asynchronous command objects.
 */
public abstract class CAsyncTask extends CCommand implements IObserver
{
   public CAsyncTask(IAsyncCallback aoCallback)
   {
      moCallback = aoCallback;
   }

   /**
    * Returns callback object. 
    */
   public IAsyncCallback getCallback()
   {
      return moCallback;
   }

   /**
    * Returns true if cancel flag is set.
    * @return cancel flag
    */
   public boolean isCanceled()
   {
      return mbCanceled;
   }

   /**
    * Gives last thrown exception of this <code>CAsyncTask</code>
    * @return last exception
    */
   public Exception getLastException()
   {
      return moLastException;
   }

   /**
    * Changes cancel flag.
    * @param abCanceled
    */
   protected void setCanceled(boolean abCanceled)
   {
      mbCanceled = abCanceled;
   }

   /**
    * Stores last thrown exception.
    * @param aoEx
    */
   protected void setLastException(Exception aoEx)
   {
      moLastException = aoEx;
   }

   protected static Logger moLogger = Logger.getLogger(CAsyncTask.class);
   private IAsyncCallback moCallback;
   private Exception moLastException;
   private boolean mbCanceled;
}
