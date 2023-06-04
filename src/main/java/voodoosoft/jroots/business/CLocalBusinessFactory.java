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


package voodoosoft.jroots.business;

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.data.CConnection;
import voodoosoft.jroots.exception.CException;

import java.sql.*;

import java.util.HashMap;
import java.util.Vector;


/**
 * Local (internal) factory for business services used by <code>CBusinessFactory</code>
 * and <code>CRMIBusinessFactory</code>.
 * Framework clients will most likely use <code>CBusinessFactory</code> or <code>CRMIBusinessFactory</code>.
 * <p><code>CLocalBusinessFactory</code> is thread safe.
 * @see voodoosoft.jroots.business.CBusinessFactory
 * @see voodoosoft.jroots.business.remote.CRMIBusinessFactory
 */
public class CLocalBusinessFactory extends CObject //implements IBusinessFactory
{
   public CLocalBusinessFactory()
   {
      moPKeyColumns = new HashMap();
      moPKeys = new HashMap();
      moPool = new CObjectPool();
      soActiveMap = new HashMap();
   }

   public IBusinessService getObject(String asName, CConnection aoConn)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      return getObject(new CDefaultPKey(asName), aoConn);
   }

   /**
    * Gets service object by primary key.
    * @param aoKey key to identify and activate service
    * @param aoConn connection used for activation
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(IPrimaryKey aoKey, CConnection aoConn)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      return getObject(aoKey, aoConn, false);
   }

   /**
    * Gets service object by primary key.
    * If there is no free object available in the factory's object pool, it will be created using
    * the registered class ({@link #registerServiceClass}).
    * @param aoKey key to identify and activate service
    * @param aoConn connection used for activation
    * @param abLazyInit service will not be initialized before required, e.g. for limiting unnecessary database access
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(IPrimaryKey aoKey, CConnection aoConn, boolean abLazyInit)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      IBusinessService loService = null;
      String lsEntity;
      Integer liID;
      Boolean lbActive;
      boolean lbWasCreated;

      try
      {
         synchronized (this)
         {
            lsEntity = aoKey.getEntityName();
            loService = (IBusinessService) moPool.getObject(lsEntity);

            liID = loService.getObjectID();
            lbActive = (Boolean) soActiveMap.get(liID);
            lbWasCreated = moPool.wasCreated(liID);

            if (lbActive == null || lbActive.equals(Boolean.FALSE))
            {
               soActiveMap.put(liID, Boolean.TRUE);
            }
         }

         synchronized (loService)
         {
            if (lbActive == null && lbWasCreated)
            {
               loService.postCreate();

               if (moLogger.isDebugEnabled())
               {
                  moLogger.debug("created [" + lsEntity + "] id [" +
                                 Integer.toHexString(liID.intValue()) + "]");
               }
            }

            if (lbActive == null || lbActive.equals(Boolean.FALSE))
            {
               if (moLogger.isDebugEnabled())
               {
                  moLogger.debug("activating [" + lsEntity + "] id [" +
                                 Integer.toHexString(liID.intValue()) + "]");
               }

               loService.setKey(aoKey);
               loService.activate(aoConn);
            }
         }
      }
      catch (CActivateFailedException ex)
      {
         moPool.releaseObject(loService.getObjectID());
         throw ex;
      }

      return loService;
   }

   public synchronized boolean isPrimaryKeyRegistered(String asEntityName)
   {
      return moPKeys.containsKey(asEntityName);
   }

   /**
    * Adds set of primary key columns.
    * @param asCatalog database catalog
    * @param asSchema database schema
    * @param asTable database table
    * @param aoColumns column names in ordinal order
    */
   public synchronized void addPKeyColumns(String asCatalog, String asSchema, String asTable,
                                           String[] aoColumns)
   {
      moPKeyColumns.put(asCatalog + "." + asSchema + "." + asTable, aoColumns);

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("primary key read: " + asCatalog + "." + asSchema + "." + asTable);
      }
   }

   /**
    * Adds set of primary key columns.
    * @param asSchema database schema
    * @param asTable database table
    * @param aoColumns column names in ordinal order
    */
   public synchronized void addPKeyColumns(String asSchema, String asTable, String[] aoColumns)
   {
      if (asSchema != null)
         moPKeyColumns.put((asSchema + "." + asTable).toUpperCase(), aoColumns);
      else
         moPKeyColumns.put(asTable.toUpperCase(), aoColumns);
      
      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("primary key read: " + (asSchema + "." + asTable).toUpperCase());
      }
   }

   /**
    * Creates primary key object for the given service name.
    * Name must be previously registered.
    * @param asName service to create key for
    * @return new created key object
    */
   public synchronized IPrimaryKey createPrimaryKey(String asName)
                                             throws CKeyCreationException
   {
      String[] loPKNames;
      CDefaultPKey loNewKey = null;
      String lsPersistName;

      lsPersistName = (String) moPKeys.get(asName);

      if (lsPersistName != null)
      {
         loPKNames = (String[]) moPKeyColumns.get(lsPersistName);

         if (loPKNames == null)
         {
            throw new CKeyCreationException("key columns not found for [" + asName + "]");
         }

         /** @todo Key-Erzeugung auslagern */
         loNewKey = new CDefaultPKey(asName, loPKNames);
      }
      else
      {
         throw new CKeyCreationException("key [" + asName + "] not registered");
      }

      return loNewKey;
   }

   /**
    * Executes specified <code>IBusinessProcess</code> using the given connection.
    * @param aoProcess
    * @param aoConn 
    */
   public void executeProcess(IBusinessProcess aoProcess, CConnection aoConn) throws CExecuteFailedException
   {
      aoProcess.execute(aoConn);
   }

   /**
    * Stores service object in factory for later usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    */
   public synchronized void putObject(Object aoObject, String asObjectName)
                               throws CClassNotRegisteredException
   {
      moPool.putObject(aoObject, asObjectName);
   }

   public synchronized void putObject(Object aoObject, String asObjectName, boolean abSharable)
                               throws CClassNotRegisteredException
   {
      moPool.putObject(aoObject, asObjectName, abSharable);
   }

   /**
    * Reads all database primary keys from a given connection
    * @param aoConn source connection
    * @param asSchema database schema to analyze
    * @param lbRegisterCatalog complete key entries with database catalog ?
    */
   public void readPKeyColumns(Connection aoConn, String asSchema, boolean lbRegisterCatalog)
   {
      DatabaseMetaData loMData;
      ResultSet loTables;
      ResultSet loKeys;
      String lsTableName;
      String lsCatalog;
      String[] lsTemp;
      String lsDebug = null;
      Vector loPKTemp;
      boolean lbUpperCase;
      boolean lbFound = false;

      try
      {
         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("readPKeyColumns");
         }

         loPKTemp = new Vector();
         loMData = aoConn.getMetaData();

         lbUpperCase = loMData.storesUpperCaseIdentifiers();

         if (lbUpperCase && asSchema != null)
         {
            asSchema = asSchema.toUpperCase();
         }

         lsCatalog = aoConn.getCatalog();

         if (lsCatalog != null && lsCatalog.equals(""))
         {
            lsCatalog = null;
         }

         loTables = loMData.getTables(lsCatalog, asSchema, "%", new String[] { "TABLE" });

         synchronized (this)
         {
            moPKeyColumns.clear();

            while (loTables.next())
            {
               loPKTemp.clear();

               lsTableName = loTables.getString("TABLE_NAME");
               loKeys = loMData.getPrimaryKeys(lsCatalog, asSchema, lsTableName);

               while (loKeys.next())
               {
                  lbFound = true;
                  loPKTemp.add(loKeys.getString("COLUMN_NAME"));
               }

               if (lbFound)
               {
                  lsTemp = new String[loPKTemp.size()];
                  lsDebug = "";

                  for (int i = 0; i < loPKTemp.size(); i++)
                  {
                     lsTemp[i] = (String) loPKTemp.get(i);

                     if (moLogger.isDebugEnabled())
                     {
                        lsDebug = lsDebug + " " + lsTemp[i];
                     }
                  }

                  if (lbRegisterCatalog)
                  {
                     addPKeyColumns(lsCatalog, asSchema, lsTableName, lsTemp);
                  }
                  else
                  {
                     addPKeyColumns(asSchema, lsTableName, lsTemp);
                  }

                  if (moLogger.isDebugEnabled())
                  {
                     moLogger.debug("columns: " + lsDebug);
                  }

                  lbFound = false;
               }

               loKeys.close();
            }

            loTables.close();
         }
      }
      catch (SQLException ex)
      {
         CException.record(ex, this);
      }
   }

   public synchronized void registerPrimaryKey(String asEntityName, String asPersistName)
   {
      moPKeys.put(asEntityName, asPersistName.toUpperCase());
   }

   public synchronized void registerServiceClass(String asObjectName, Class aoClass, int aiLimit,
                                                 int aiInitialCapacity, int aiCapacityIncrement)
   {
      moPool.registerClass(asObjectName, aoClass, aiLimit, aiInitialCapacity, aiCapacityIncrement);
   }

   /**
    * Registers service class.
    * @param asEntityName unique service name
    * @param aoClass service class
    */
   public synchronized void registerServiceClass(String asEntityName, Class aoClass, int aiLimit)
   {
      moPool.registerClass(asEntityName, aoClass, aiLimit);
   }

   public synchronized void registerServiceClass(String asEntityName)
   {
      moPool.registerClass(asEntityName);
   }

   public void releaseAll(boolean abRemoveObjects)
   {
      moPool.releaseAll(abRemoveObjects);
   }

   /**
    * Releases object for further reuse.
    * Object will be passivated.
    * @param aiObjectID object to release
    * @see IBusinessService#passivate
    */
   public synchronized boolean releaseObject(Integer aiObjectID) //throws CPassivateFailedException
   {
      boolean lbFound = false;
      IBusinessService loService;

      try
      {
         lbFound = moPool.isLocked(aiObjectID);

         if (lbFound)
         {
            loService = (IBusinessService) moPool.getObject(aiObjectID);

            moPool.releaseObject(aiObjectID);

            if (!moPool.isLocked(aiObjectID))
            {
               if (moLogger.isDebugEnabled())
               {
                  moLogger.debug("passivating " + Integer.toHexString(aiObjectID.intValue()));
               }

               loService.passivate();
               soActiveMap.put(aiObjectID, Boolean.FALSE);
            }
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return lbFound;
   }

   /**
    * Removes objects that have been unused for the given time period.
    * @param alSleepingPeriod period in seconds
    */
   public void removeExpiredObjects(long alSleepingPeriod)
   {
      moPool.removeExpiredObjects(alSleepingPeriod);
   }

   private static HashMap soActiveMap;
   private static Logger moLogger = Logger.getLogger(CLocalBusinessFactory.class);
   private CObjectPool moPool;
   private HashMap moPKeyColumns;
   private HashMap moPKeys;
}
