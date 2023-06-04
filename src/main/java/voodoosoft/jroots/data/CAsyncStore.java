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

import voodoosoft.jroots.exception.CException;


/**
 * Wrapper for asynchronous retrieval of data stores (<code>IBasicDataStore</code>) implemented as <code>CCommand</code>.
 * <p>Typically executed in <code>CMessageHandler</code>.
 */
public class CAsyncStore extends CAsyncTask
{
   /**
    * Creates new <code>CAsyncStore</code>.
    * @param aoCallback object to notify data retrieval has been successful, has failed or has been canceled
    * @param aoViewStore <code>IBasicDataStore</code>, ready for retrieval
    * @param aoConn <code>CConnection</code> to use
    */
   public CAsyncStore(IAsyncCallback aoCallback, IBasicDataStore aoViewStore, CConnection aoConn)
   {
      super(aoCallback);

      moViewStore = aoViewStore;
      moConn = aoConn;
   }

   /**
    * Callback if data retrieval has been canceled.
    * Will invoke <code>asyncFinished</code> on the previously specified <code>IAsyncCallback</code>.
    */
   public void canceled()
   {
      try
      {
         setCanceled(true);

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("canceled [" + Integer.toHexString(hashCode()) + "]");
         }

         if (moViewStore != null)
         {
            moViewStore.cancel();
            getCallback().asyncFinished(moViewStore, true);
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
      finally
      {
         moViewStore = null;
         moConn = null;
      }
   }

   /**
    * Callback if data retrieval of this <code>CAsyncStore</code> was successful.
    * Will invoke <code>asyncFinished</code> on the previously specified <code>IAsyncCallback</code>.
    */
   public void done()
   {
      try
      {
         if (!isCanceled())
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("finished [" + Integer.toHexString(hashCode()) + "]");
            }

            getCallback().asyncFinished(moViewStore, false);
         }
      }
      finally
      {
         moViewStore = null;
         moConn = null;
      }
   }

   /**
    * Starts retrieval of internal <code>IBasicDataStore</code>.
    * <p>Before beginning, the last thrown exception and the canceled flag are resetted.
    * <p>Next step will be either <code>done</code>, <code>failed</code> or <code>canceled</code>.
    * <p>Any thrown exception is stored with <code>setLastException</code>.
    * @return 0 for success, -1 if failed
    * @throws Exception
    */
   public int exec() throws Exception
   {
      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("start [" + Integer.toHexString(hashCode()) + "]");
      }

      try
      {
         setLastException(null);
         setCanceled(false);
         moViewStore.retrieve(moConn);
      }
      catch (Exception ex)
      {
         setLastException(ex);

         //throw ex;
         return -1;
      }

      return 0;
   }

   /**
    * Callback method if data retrieval failed.
    * Will invoke <code>asyncFailed</code> on the previously specified <code>IAsyncCallback</code>.
    */
   public void failed()
   {
      try
      {
         if (!isCanceled())
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("failed [" + Integer.toHexString(hashCode()) + "]");
            }

            getCallback().asyncFailed(getLastException());
         }
         else
         {
            if (getLastException() == null)
            {
               moLogger.warn("canceled query failed ");
            }
            else if (getLastException() instanceof CSQLException)
            {
               moLogger.warn("canceled query failed " +
                             ((CSQLException) getLastException()).getNested().getMessage());
            }
            else
            {
               moLogger.warn("canceled query failed " + getLastException().getMessage());
            }
         }
      }
      finally
      {
         moViewStore = null;
         moConn = null;
      }
   }

   /**
    * Empty implementation.
    * @return 0
    * @throws Exception
    */
   public int undo() throws Exception
   {
      return 0;
   }

   private IBasicDataStore moViewStore;
   private CConnection moConn;
}
