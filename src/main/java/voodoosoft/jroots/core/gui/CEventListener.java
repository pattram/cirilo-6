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

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.exception.CException;

import java.lang.reflect.*;

import java.util.Vector;


/**
 * Listener ancestor for mapping Java events to object methods.
 */
public abstract class CEventListener extends CObject
{
   private class CListenerMapping
   {
      public CListenerMapping(Object aoEventHandler, CEventListener aoListener)
      {
         moEventHandler = aoEventHandler;
         moListener = aoListener;
      }

      public Object moEventHandler;
      public CEventListener moListener;
   }

   /**
    * Creates and installs new event listener.
    * @param aoEventHandler event handler object
    * @param asHandleMethod handler method of event listener
    * @param asParamClass parameter class of handler method
    * @throws CListenerInstallFailedException
    */
   public CEventListener(IEventHandler aoEventHandler, String asHandleMethod, String asParamClass)
                  throws CListenerInstallFailedException
   {
      this(aoEventHandler);

      setupHandleMethod(asHandleMethod, new String[] { asParamClass });

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("registered listener [" + aoEventHandler.getClass() + "#" + asHandleMethod +
                        "] hash [" + Integer.toHexString(aoEventHandler.hashCode()) + "]");
      }
   }

   protected CEventListener(IEventHandler aoEventHandler)
                     throws CListenerInstallFailedException
   {
      moEventHandler = aoEventHandler;

      CListenerMapping loMapping = new CListenerMapping(aoEventHandler, this);
      soListenerMap.add(loMapping);
   }

   /**
    * Disables or enables event handling.
    * @param abBlocked
    */
   public static void setBlocked(boolean abBlocked)
   {
      mbBlocked = abBlocked;
   }

   /**
    * Returns true if event handling is currently disabled.
    * @return blocking state
    */
   public static boolean isBlocked()
   {
      return mbBlocked;
   }

   /**
    * Prints information about the registered event listeners.
    */
   public static void printLog()
   {
      int i = 0;
      CListenerMapping loMapping;
      String lsLog;

      moLogger.debug("registered event listeners");

      while (i < soListenerMap.size())
      {
         loMapping = (CListenerMapping) soListenerMap.elementAt(i);
         lsLog = "[handler] " + loMapping.moListener.moEventHandler.getClass().getName() +
                 " [method] " + loMapping.moListener.moMethodObject.getName();
         moLogger.debug(lsLog);
         i++;
      }
   }

   /**
    * Uninstalls the event handler of this listener.
    * Calls <code>handlerRemoved</code> of the handler object.
    */
   public void remove()
   {
      moEventHandler.handlerRemoved(this);
      moEventHandler = null;
   }

   /**
    * Uninstalls the given event handler.
    * @param aoEventHandler
    */
   public static void removeListener(Object aoEventHandler)
   {
      int i = 0;
      CListenerMapping loMapping;

      if (moLogger.isDebugEnabled())
      {
         if (aoEventHandler != null)
         {
            moLogger.debug("removing listener of [" + aoEventHandler.getClass().getName() +
                           "] hash code [" + Integer.toHexString(aoEventHandler.hashCode()) + "]");
         }
         else
         {
            moLogger.debug("removing [null] listener");
         }
      }

      while (i < soListenerMap.size())
      {
         loMapping = (CListenerMapping) soListenerMap.elementAt(i);

         if (loMapping.moEventHandler == aoEventHandler)
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("removed listener [" + loMapping.moListener.moMethodObject.getName() +
                              "]");
            }

            loMapping.moListener.remove();
            loMapping.moListener = null;
            loMapping.moEventHandler = null;
            soListenerMap.remove(i);
         }
         else 
         {
            i++;
         }
      }
   }

   protected Object invokeHandler(Object e)
   {
      moArgs[0] = e;

      return invokeHandler(moArgs);
   }

   protected Object invokeHandler(Object[] aoArgs)
   {
      if (mbBlocked)
      {
         return null;
      }

      try
      {
         Object ret = moMethodObject.invoke(moEventHandler, aoArgs);
         return ret;
      }
      catch (InvocationTargetException ex)
      {
         CException.record(ex, this);
      }
      catch (IllegalAccessException ex)
      {
         CException.record(ex, this);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
      return null;
   }

   protected void setupHandleMethod(String asHandleMethod, String[] asParamClass)
                             throws CListenerInstallFailedException
   {
      Class[] loParam;
      Method[] m;

      // prepare Parameter
      try
      {
         loParam = new Class[asParamClass.length];

         for (int i = 0; i < asParamClass.length; i++)
         {
            loParam[i] = Class.forName(asParamClass[i]);
         }

         // prepare Argument
         moArgs = new Object[asParamClass.length];

         // find handler method
         m = moEventHandler.getClass().getDeclaredMethods();
         moMethodObject = moEventHandler.getClass().getMethod(asHandleMethod, loParam);
      }
      catch (NullPointerException ex)
      {
         throw new CListenerInstallFailedException(ex);
      }
      catch (NoSuchMethodException ex)
      {
         throw new CListenerInstallFailedException(ex);
      }
      catch (ClassNotFoundException ex)
      {
         throw new CListenerInstallFailedException(ex);
      }
   }

   protected void setupHandleMethod(String asHandleMethod, Class[] aoParamClass)
                             throws CListenerInstallFailedException
   {
      Method[] m;

      // prepare Parameter
      try
      {
         // prepare Argument
         moArgs = new Object[aoParamClass.length];

         // find handler method
         m = moEventHandler.getClass().getDeclaredMethods();
         moMethodObject = moEventHandler.getClass().getMethod(asHandleMethod, aoParamClass);
      }
      catch (NullPointerException ex)
      {
         throw new CListenerInstallFailedException(ex);
      }
      catch (NoSuchMethodException ex)
      {
         throw new CListenerInstallFailedException(ex);
      }
   }

   private static Vector soListenerMap = new Vector();
   private static boolean mbBlocked = false;
   protected static Logger moLogger = Logger.getLogger(CEventListener.class);
   private Method moMethodObject;
   private IEventHandler moEventHandler;
   protected Object[] moArgs;
}
