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

import voodoosoft.jroots.business.remote.*;
import voodoosoft.jroots.core.*;
import voodoosoft.jroots.data.CConnection;
import voodoosoft.jroots.data.CConnectionNotAvailableException;
import voodoosoft.jroots.exception.CException;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.sql.*;


/**
 * Factory for business services, e.g. entity objects.
 * <p><code>CBusinessFactory</code> is made of one or two internal factory objects:
 * <li>a local factory and an
 * <li>optional remote factory
 * <p>Some requests are at first delegated to the local factory and, if the specified services
 * are not locally registered, further on to the remote factory.
 * <p><code>CBusinessFactory</code> is thread safe.
 * @see IRMIBusinessFactory
 * @see CLocalBusinessFactory
 */
public class CBusinessFactory extends CObject implements IBusinessFactory
{
   /**
    * Creates factory without remote factory.
    */
   public CBusinessFactory()
   {
      moLocalFactory = new CLocalBusinessFactory();
   }

   public CBusinessFactory(CLocalBusinessFactory aoLocalFactory)
   {
      moLocalFactory = aoLocalFactory;
   }

   /**
    * Creates factory with remote factory using the specified remote server.
    */
   public CBusinessFactory(CRMIServer aoRMIServer)
   {
      this();
      moRMIServer = aoRMIServer;
   }

   /**
    * Creates factory with remote factory using the specified server url.
    * <code>java.rmi.Naming</code> is used to locate the server.
    * @param asRMIServerURL url to locate a valid <code>IRMIServer</code>
    */
   public CBusinessFactory(String asRMIServerURL) throws Exception
   {
      this();

      msServerURL = asRMIServerURL;
      moRMIServer = (IRMIServer) Naming.lookup(asRMIServerURL);
   }

   public void setConnection(String asConnection)
   {
      msConnection = asConnection;
   }
   
   
	public CConnection getConnection() throws CConnectionNotAvailableException
	{
      CConnection loConn = CConnection.getConnection(msConnection, true);
      return loConn;
	}

   /**
    * Gets service object by name.
    * <p><code>getObject</code> forwards the request to the optional remote factory if
    * the specified service is not registered in the local factory.
    * @param asName key name to identify service
    * @param aoConn connection used for activation
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(String asName, CConnection aoConn)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      String lsErr;
      IBusinessService loService = null;

      // 1. Objekt lokal vorhanden ?
      synchronized (this)
      {
         loService = moLocalFactory.getObject(asName, aoConn);
      }

      if (loService == null)
      {
         try
         {
            if (checkRMIFactory())
            {
               loService = moRMIFactory.getObject(asName);
            }
         }
         catch (Exception ex)
         {
            if (moRMIServer != null)
            {
               lsErr = "server url: " + msServerURL;
            }
            else
            {
               lsErr = "";
            }

            throw new CObjectNotAvailable(ex, asName, lsErr);
         }
      }

      // 2. Objekt noch nicht gefunden -> falls vorhanden, RMI-Factory kontaktieren
      if (loService == null)
      {
         if (moRMIServer != null)
         {
            lsErr = "server url: " + msServerURL;
         }
         else
         {
            lsErr = "";
         }

         throw new CObjectNotAvailable(asName, lsErr);
      }

      return loService;
   }

   public IBusinessService getObject(IPrimaryKey aoKey)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      CConnection loConn = null;
      Integer liHandle = null;
      IBusinessService loService = null;

      // 1. Objekt lokal vorhanden ?
      //    (nur möglich bei gesetzter Connection)
      synchronized (this)
      {
         if (msConnection != null)
         {
            try
            {
               loConn = CConnection.getConnection(msConnection);
               liHandle = loConn.lockConnection();

               loService = (IBusinessService) moLocalFactory.getObject(aoKey, loConn);
            }
            catch (Exception ex)
            {
               throw new CObjectNotAvailable(ex, "connection: [" + msConnection + "]");
            }
            finally
            {
               if (loConn != null)
               {
                  loConn.releaseConnection(liHandle);
               }
            }
         }
      }

      // 2. Objekt noch nicht gefunden -> RMI-Factory kontaktieren
      if (loService == null)
      {
         try
         {
            if (checkRMIFactory())
            {
               loService = moRMIFactory.getObject(aoKey);
            }
            else
            {
               throw new CObjectNotAvailable(aoKey.toString());
            }
         }
         catch (Exception ex)
         {
            throw new CObjectNotAvailable(ex, aoKey.toString());
         }
      }

      return loService;
   }

   public IBusinessService getObject(String asName)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      CConnection loConn = null;
      Integer liHandle = null;
      IBusinessService loService = null;

      // 1. Objekt lokal vorhanden ?
      //    (nur möglich bei gesetzter Connection)
      synchronized (this)
      {
         if (msConnection != null)
         {
            try
            {
               loConn = CConnection.getConnection(msConnection);
               liHandle = loConn.lockConnection();

               loService = (IBusinessService) moLocalFactory.getObject(asName, loConn);
            }
            catch (Exception ex)
            {
               throw new CObjectNotAvailable(ex, "connection: [" + msConnection + "]");
            }
            finally
            {
               if (loConn != null)
               {
                  loConn.releaseConnection(liHandle);
               }
            }
         }
      }

      // 2. Objekt noch nicht gefunden -> RMI-Factory kontaktieren
      if (loService == null)
      {
         try
         {
            if (checkRMIFactory())
            {
               loService = moRMIFactory.getObject(asName);
            }
            else
            {
               throw new CObjectNotAvailable(asName);
            }
         }
         catch (Exception ex)
         {
            throw new CObjectNotAvailable(ex, asName);
         }
      }

      return loService;
   }

   /**
    * Gets service object by primary key.
    * <p><code>getObject</code> forwards the request to the optional remote factory if
    * the specified service is not registered in the local factory.
    * @param aoKey key to identify and activate service
    * @param aoConn connection used for activation
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public synchronized IBusinessService getObject(IPrimaryKey aoKey, CConnection aoConn)
                                           throws CObjectNotAvailable, CActivateFailedException
   {
      return getObject(aoKey, aoConn, false);
   }

   /**
    * Gets service object by primary key.
    * <p><code>getObject</code> will return a free unused instance or,
    * if not available and the service was registered with class,
    * a new object will be created using the registered class ({@link #registerServiceClass}).
    * <p><code>getObject</code> forwards the request to the optional remote factory if
    * the specified service is not registered in the local factory.
    * <p>Only services registered by <code>registerServiceClass</code> are available.
    * @param aoKey key to identify service
    * @param aoConn connection used for activation
    * @param abLazyInit service will not be initialized before required, e.g. for limiting unnecessary database access
    * @return wanted service or null if not found
    * @see #registerServiceClass
    */
   public IBusinessService getObject(IPrimaryKey aoKey, CConnection aoConn, boolean abLazyInit)
                              throws CObjectNotAvailable, CActivateFailedException
   {
      IBusinessService loService = null;

      // 1. Objekt lokal vorhanden ?
      synchronized (this)
      {
         loService = (IBusinessService) moLocalFactory.getObject(aoKey, aoConn, abLazyInit);
      }

      // 2. Objekt noch nicht gefunden -> RMI-Factory kontaktieren
      if (loService == null)
      {
         try
         {
            if (checkRMIFactory())
            {
               loService = moRMIFactory.getObject(aoKey, abLazyInit);
            }
            else
            {
               throw new CObjectNotAvailable(aoKey.toString());
            }
         }
         catch (Exception ex)
         {
            throw new CObjectNotAvailable(ex, aoKey.toString());
         }
      }

      return loService;
   }

   /**
    * Creates primary key object for the given service name.
    * <p>In order for <code>createPrimaryKey</code> to work, it is necessary to
    * <li>register services for the automatic key creation (<code>registerPrimaryKey</code>)
    * <li>analyse the database and read key metadata (<code>readPKeyColumns</code>)
    * @param asName service to create key for
    * @return new created key object
    * @see #registerPrimaryKey
    * @see #readPKeyColumns
    */
   public IPrimaryKey createPrimaryKey(String asName) throws CKeyCreationException
   {
      boolean lbFound;
      IPrimaryKey loNewKey = null;

      synchronized (this)
      {
         lbFound = moLocalFactory.isPrimaryKeyRegistered(asName);

         if (lbFound)
         {
            loNewKey = moLocalFactory.createPrimaryKey(asName);
         }
      }

      if (!lbFound)
      {
         try
         {
            lbFound = checkRMIFactory();

            if (lbFound)
            {
               loNewKey = moRMIFactory.createPrimaryKey(asName);
            }
         }
         catch (RemoteException ex)
         {
            throw new CKeyCreationException(ex);
         }
      }

      if (loNewKey == null)
      {
         throw new CKeyCreationException("key [" + asName + "] not registered");
      }

      return loNewKey;
   }

   public synchronized void executeProcess(IBusinessProcess aoProcess) throws CExecuteFailedException
   {
      boolean remote = false;
      
      try
      {
         remote = checkRMIFactory();
         if (remote)      
            moRMIFactory.executeProcess(aoProcess);
      }
      catch (RemoteException e)
      {
         throw new CExecuteFailedException(e, "executeProcess() failed");
      }
               
      if (!remote)
      {            
         CConnection loConn;
         try
         {
            loConn = CConnection.getConnection(msConnection, true);
         }
         catch (Exception e)
         {
            throw new CExecuteFailedException(e, "executeProcess() failed");
         }
         moLocalFactory.executeProcess(aoProcess, loConn);
      }
   }
   

   /**
    * Stores non-sharable service object in factory for later usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    */
   public synchronized void putObject(Object aoObject, String asObjectName)
                               throws CObjectPutFailedException, CClassNotRegisteredException
   {
      moLocalFactory.putObject(aoObject, asObjectName, false);
   }

   /**
    * Stores service object in factory for later usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    * @param abSharable if true, the given service may be shared among several clients.
    */
   public synchronized void putObject(Object aoObject, String asObjectName, boolean abSharable)
                               throws CObjectPutFailedException, CClassNotRegisteredException
   {
      moLocalFactory.putObject(aoObject, asObjectName, abSharable);
   }

   /**
    * Reads all database primary keys of the specified schema.
    * @param aoConn connection to use
    * @param asSchema database schema to analyze
    * @param lbRegisterCatalog if true key entries will be completed with database catalog
    * @see #createPrimaryKey
    */
   public void readPKeyColumns(Connection aoConn, String asSchema, boolean lbRegisterCatalog) //throws CNoRemoteFactoryException
   {
      moLocalFactory.readPKeyColumns(aoConn, asSchema, lbRegisterCatalog);
   }

   /**
    * Maps business service to the specified persist name.
    * <p><code>registerPrimaryKey</code> is necessary to use the automatic creation of <code>IPrimaryKey</code> objects
    * offered by <code>createPrimaryKey</code>.
    * @param asEntityName entity to register for
    * @param asPersistName database table that will be analysed to get the key columns
    * @throws CKeyRegisterException
    * @see #createPrimaryKey
    */
   public synchronized void registerPrimaryKey(String asEntityName, String asPersistName)
                                        throws CKeyRegisterException
   {
      moLocalFactory.registerPrimaryKey(asEntityName, asPersistName);
   }

   /**
    * Registers singleton business service.
    * Every kind of business service needs to be registered before the business factory can supply appropriate objects.
    * <p>This version of <code>registerServiceClass</code> does not require to specify the business service's Java class,
    * thus it is only useful for objects submitted by <code>putObject(Object, String)</code>.
    * <p>If the service requested by <code>getObject</code> is already in use, no new object will be created.
    * @param asEntityName unique service name
    * @see #putObject(Object, String)
    * @see #registerServiceClass(String, Class, int)
    */
   public synchronized void registerServiceClass(String asEntityName)
   {
      moLocalFactory.registerServiceClass(asEntityName);
   }

   public synchronized void registerServiceClass(String asObjectName, Class aoClass, int aiLimit,
                                                 int aiInitialCapacity, int aiCapacityIncrement)
   {
      moLocalFactory.registerServiceClass(asObjectName, aoClass, aiLimit, aiInitialCapacity,
                                          aiCapacityIncrement);
   }

   /**
    * Registers creatable business service.
    * Every kind of business service needs to be registered before the business factory can supply appropriate objects.
    * <p>If the service requested by <code>getObject</code> is already in use, a new instance of the
    * given Java class will be created and returned.
    * @param asEntityName unique service name
    * @param aoClass service class
    */
   public synchronized void registerServiceClass(String asEntityName, Class aoClass, int aiLimit)
   {
      moLocalFactory.registerServiceClass(asEntityName, aoClass, aiLimit);
   }

   public synchronized void releaseAll(boolean abRemoveObjects)
   {
      moLocalFactory.releaseAll(abRemoveObjects);
   }

   /**
    * Releases object for further reuse.
    * <code>releaseObject</code> forwards the request to the optional remote factory if
    * the specified object could not be found in the local factory.
    * <p>Object will be passivated.
    * @param aoObject object to release
    * @see IBusinessService#passivate
    */
   public void releaseObject(IBusinessService aoObject) //throws CReleaseObjectFailedException
   {
      boolean lbFound;

      if (aoObject == null)
      {
         return;
      }

      try
      {
         synchronized (this)
         {
            lbFound = moLocalFactory.releaseObject(aoObject.getObjectID());
         }

         if (!lbFound)
         {
            if (checkRMIFactory())
            {
               moRMIFactory.releaseObject(aoObject.getObjectID());
            }
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this, false);
      }
   }

   /**
    * Removes objects that have been unused for the given time period.
    * @param alSleepingPeriod period in seconds
    */
   public void removeExpiredObjects(long alSleepingPeriod)
   {
      moLocalFactory.removeExpiredObjects(alSleepingPeriod);
   }

   private boolean checkRMIFactory() throws RemoteException
   {
      boolean lbOK = false;

      if (moRMIServer != null)
      {
         if (moRMIFactory == null)
         {
            moRMIFactory = moRMIServer.getFactory();
         }

         if (moRMIFactory != null)
         {
            lbOK = true;
         }
      }

      return lbOK;
   }

   private IRMIServer moRMIServer;
   private String msServerURL;
   private IRMIBusinessFactory moRMIFactory;
   private CLocalBusinessFactory moLocalFactory;
   private String msConnection;
}
