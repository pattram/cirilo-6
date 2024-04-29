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


package voodoosoft.jroots.core;

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CObject;

import java.util.*;


/**
 * Generic object pool/cache/factory.
 * <p><code>CLocalBusinessFactory</code> is thread safe.
 */
public class CObjectPool extends CObject
{
   private static class CPooledObject
   {
      public CPooledObject(Object aoObject, boolean abLocked, Integer aiHashID)
      {
         moObject = aoObject;
         mbLocked = abLocked;
         miHashID = aiHashID;
         miRefCount = 0;
      }

      public boolean equals(Integer aiCheckID)
      {
         return miHashID == aiCheckID;
      }

      public boolean equals(Object aoCheck)
      {
         return moObject.equals(aoCheck);
      }

      public Object moObject;
      public boolean mbLocked;
      public Integer miHashID;
      public int miRefCount;
      public boolean mbSharable;
      public boolean mbCreated;
      public long mlLastAccess;
   }

   public CObjectPool()
   {
      moClasses = new HashMap();
      moObjectPool = new HashMap();
      moReleaseMap = new HashMap();
   }

   public synchronized boolean isLocked(Integer aiObjectHashID)
   {
      CPooledObject loPooled;
      boolean lbLocked = false;

      loPooled = (CPooledObject) moReleaseMap.get(aiObjectHashID);

      if (loPooled != null)
      {
         lbLocked = loPooled.mbLocked;
      }

      return lbLocked;
   }

   public synchronized Object getObject(Integer aiObjectID)
                                 throws CObjectNotAvailable
   {
      Object loFound;

      loFound = moReleaseMap.get(aiObjectID);

      if (loFound == null)
      {
         throw new CObjectNotAvailable(String.valueOf(aiObjectID));
      }

      return ((CPooledObject) loFound).moObject;
   }

   public Object getObject(String asObjectName) throws CObjectNotAvailable
   {
      Object loFree = null;
      Class loClass;
      Vector loSubPool;
      CPooledObject loPooled;

      try
      {
         synchronized (this)
         {
            loSubPool = (Vector) moObjectPool.get(asObjectName);

            if (loSubPool == null)
            {
               throw new CObjectNotAvailable(asObjectName, "registerClass() has not been called");
            }

            for (int i = 0; i < loSubPool.size(); i++)
            {
               loPooled = (CPooledObject) loSubPool.elementAt(i);

               if (loPooled.mbSharable || !loPooled.mbLocked)
               {
                  loPooled.mbLocked = true;
                  loPooled.mlLastAccess = System.currentTimeMillis();
                  loPooled.miRefCount++;
                  loFree = loPooled.moObject;

                  if (moLogger.isDebugEnabled())
                  {
                     moLogger.debug("object found [" + asObjectName + "] ref count [" +
                                    String.valueOf(loPooled.miRefCount) + "]");
                  }

                  break;
               }
            }
         }

         if (loFree == null)
         {
            synchronized (this)
            {
               loClass = (Class) moClasses.get(asObjectName);

               if (loClass == null)
               {
                  throw new CObjectNotAvailable(asObjectName, "requested object not creatable");
               }
            }

            loFree = loClass.newInstance();

            synchronized (this)
            {
               loPooled = new CPooledObject(loFree, true, createObjectHashID(loFree));
               loPooled.mbCreated = true;
               loPooled.mlLastAccess = System.currentTimeMillis();
               loPooled.miRefCount++;
               loSubPool.add(loPooled);
               moReleaseMap.put(loPooled.miHashID, loPooled);

               if (moLogger.isDebugEnabled())
               {
                  moLogger.debug("object created [" + asObjectName + "] ref count [" +
                                 String.valueOf(loPooled.miRefCount) + "]");
               }
            }
         }
      }
      catch (Exception ex)
      {
         throw new CObjectNotAvailable(ex, asObjectName);
      }

      return loFree;
   }

   public synchronized void putObject(Object aoObject, String asObjectName)
                               throws CClassNotRegisteredException
   {
      putObject(aoObject, asObjectName, createObjectHashID(aoObject), false);
   }

   public synchronized void putObject(Object aoObject, String asObjectName, boolean abSharable)
                               throws CClassNotRegisteredException
   {
      putObject(aoObject, asObjectName, createObjectHashID(aoObject), abSharable);
   }

   public synchronized void putObject(Object aoObject, String asObjectName, Integer aiObjectHashID,
                                      boolean abSharable)
                               throws CClassNotRegisteredException
   {
      Vector loSubPool;
      CPooledObject loPooled;

      loSubPool = (Vector) moObjectPool.get(asObjectName);

      if (loSubPool != null)
      {
         loPooled = new CPooledObject(aoObject, false, aiObjectHashID);
         loPooled.mbSharable = abSharable;
         loPooled.mbCreated = false;
         loSubPool.add(loPooled);
         moReleaseMap.put(aiObjectHashID, loPooled);

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("object put [" + asObjectName + "] id [" +
                           Integer.toHexString(aiObjectHashID.intValue()) + "]");
         }
      }
      else
      {
         throw new CClassNotRegisteredException(asObjectName);
      }
   }

   public synchronized void registerClass(String asObjectName, Class aoClass, int aiLimit,
                                          int aiInitialCapacity, int aiCapacityIncrement)
   {
      moClasses.put(asObjectName, aoClass);
      moObjectPool.put(asObjectName, new Vector(aiInitialCapacity, aiCapacityIncrement));
   }

   
   public synchronized void registerClass(String asObjectName, Class aoClass, int aiLimit)
   {
      /** @todo object limit */
      moClasses.put(asObjectName, aoClass);
      moObjectPool.put(asObjectName, new Vector());
   }

   public synchronized void registerClass(String asObjectName)
   {
      moObjectPool.put(asObjectName, new Vector());
   }

   public synchronized void releaseAll(boolean abRemoveObjects)
   {
      CPooledObject loPooled;
      Collection loObjects;
      Iterator loIt;

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("releaseAll, object count [" + moReleaseMap.size() + "]");
      }

      loObjects = moReleaseMap.values();
      loIt = loObjects.iterator();

      while (loIt.hasNext())
      {
         loPooled = (CPooledObject) loIt.next();
         loPooled.mbLocked = false;
         loPooled.miRefCount = 0;

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("object released, class [" + loPooled.moObject.getClass().getName() +
                           "] id [" + Integer.toHexString(loPooled.miHashID.intValue()) + "]");
         }

         if (abRemoveObjects)
         {
            loPooled.moObject = null;
         }
      }

      if (abRemoveObjects)
      {
         moReleaseMap.clear();
      }
   }

   public synchronized void releaseObject(Integer aiObjectHashID)
   {
      CPooledObject loPooled;

      loPooled = (CPooledObject) moReleaseMap.get(aiObjectHashID);

      if (loPooled != null)
      {
         if (--loPooled.miRefCount == 0)
         {
            loPooled.mbLocked = false;

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("object released, class [" + loPooled.moObject.getClass().getName() +
                              "] id [" + Integer.toHexString(aiObjectHashID.intValue()) + "]");
            }
         }
      }
      else
      {
         moLogger.error("object release failed, id [" +
                        Integer.toHexString(aiObjectHashID.intValue()) + "]");
      }
   }

   /**
    * Removes objects that have been unused for the given time period.
    * @param alSleepingPeriod period in seconds
    */
   public synchronized void removeExpiredObjects(long alSleepingPeriod)
   {
      CPooledObject loPooled;
      Collection loObjects;
      Iterator loIt;
      long llCurrentTime = System.currentTimeMillis();

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("looking for expired objects, object count [" + moReleaseMap.size() + "]");
      }

      alSleepingPeriod = alSleepingPeriod * 1000;
      loObjects = moReleaseMap.values();
      loIt = loObjects.iterator();

      while (loIt.hasNext())
      {
         loPooled = (CPooledObject) loIt.next();

         if (loPooled.mbCreated && !loPooled.mbLocked &&
                (llCurrentTime - loPooled.mlLastAccess > alSleepingPeriod))
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("object removed, class [" + loPooled.moObject.getClass().getName() +
                              "] id [" + Integer.toHexString(loPooled.miHashID.intValue()) + "]");
            }

            loPooled.moObject = null;
            loIt.remove();
         }
      }
   }

   public synchronized boolean wasCreated(Integer aiObjectHashID)
   {
      CPooledObject loPooled;
      boolean lbCreated = false;

      loPooled = (CPooledObject) moReleaseMap.get(aiObjectHashID);

      if (loPooled != null)
      {
         lbCreated = loPooled.mbCreated;
      }

      return lbCreated;
   }

   protected Integer createObjectHashID(Object aoObject)
   {
      return new Integer(aoObject.hashCode());
   }

   protected static Logger moLogger = Logger.getLogger(CObjectPool.class);
   private HashMap moClasses;
   private HashMap moObjectPool;
   private HashMap moReleaseMap;
}
