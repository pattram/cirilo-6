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


package voodoosoft.jroots.business.remote;

import voodoosoft.jroots.business.*;
import voodoosoft.jroots.core.*;
import voodoosoft.jroots.data.CConnection;
import voodoosoft.jroots.data.CConnectionNotAvailableException;
import voodoosoft.jroots.exception.CException;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import java.sql.*;


/**
 * Remote factory for business services.
 * <p><code>CRMIBusinessFactory</code> is thread safe.
 */
public class CRMIBusinessFactory extends UnicastRemoteObject implements IRMIBusinessFactory
{
   public CRMIBusinessFactory(CLocalBusinessFactory aoLocalFactory, String asConnection)
                       throws RemoteException
   {
      moLocalFactory = aoLocalFactory;
      msConnection = asConnection;
   }

   //   public void setLocalFactory(CLocalBusinessFactory aoLocalFactory)
   //   {
   //      moLocalFactory = aoLocalFactory;
   //   }
   public Integer getHashID() throws RemoteException
   {
      return new Integer(this.hashCode());
   }

   /**
    * Gets service object by name.
    * @param asName service name
    * @return wanted service or null if not found
    */
   public synchronized IBusinessService getObject(String asName)
                                           throws CObjectNotAvailable, CActivateFailedException, 
                                                  CConnectionNotAvailableException
   {
      CConnection loConn = null;
      IBusinessService loService = null;
      Integer liHandle = null;

      try
      {
         loConn = CConnection.getConnection(msConnection);
         liHandle = loConn.lockConnection();

         loService = moLocalFactory.getObject(asName, loConn);
      }
      catch (SQLException ex)
      {
         throw new CObjectNotAvailable(ex, asName);
      }
      finally
      {
         if (loConn != null)
         {
            loConn.releaseConnection(liHandle);
         }
      }

      return loService;
   }

   /**
    * Gets service object by primary key.
    * @param aoKey key to identify and activate service
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public synchronized IBusinessService getObject(IPrimaryKey aoKey)
                                           throws CObjectNotAvailable, CActivateFailedException, 
                                                  CConnectionNotAvailableException
   {
      return getObject(aoKey, false);
   }

   /**
    * Gets service object by primary key.
    * If there is no free object available in the factory's object pool, it will be created using
    * the registered class ({@link #registerServiceClass}).
    * @param aoKey key to identify and activate service
    * @param abLazyInit service will not be initialized before required, e.g. for limiting unnecessary database access
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public synchronized IBusinessService getObject(IPrimaryKey aoKey, boolean abLazyInit)
                                           throws CObjectNotAvailable, CActivateFailedException, 
                                                  CConnectionNotAvailableException
   {
      CConnection loConn;
      IBusinessService loService;
      Integer liHandle;

      try
      {
         loConn = CConnection.getConnection(msConnection);

         liHandle = loConn.lockConnection();

         loService = moLocalFactory.getObject(aoKey, loConn, abLazyInit);

         loConn.releaseConnection(liHandle);
      }
      catch (Exception ex)
      {
         CException.record(ex, this, false);
         throw new CObjectNotAvailable(ex, aoKey.getEntityName());
      }

      return loService;
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
      return moLocalFactory.createPrimaryKey(asName);
   }

   public void executeProcess(IBusinessProcess aoProcess) throws CExecuteFailedException
   {
      CConnection loConn = null;

      try
      {
         loConn = CConnection.getConnection(msConnection, true);
      }
      catch (CConnectionNotAvailableException ex)
      {
         CException.record(ex, this, false);
      }
      moLocalFactory.executeProcess(aoProcess, loConn);      
   }

   /**
    * Stores service object in factory for later usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    */
   public synchronized void putObject(Object aoObject, String asObjectName)
                               throws CClassNotRegisteredException
   {
      moLocalFactory.putObject(aoObject, asObjectName);
   }

   /**
    * Reads all database primary keys from a given connection
    * @param asSchema database schema to analyze
    * @param lbRegisterCatalog complete key entries with database catalog ?
    */
   public synchronized void readPKeyColumns(String asSchema, boolean lbRegisterCatalog)
                                     throws CConnectionNotAvailableException
   {
      CConnection loConn;
      Integer liHandle;

      try
      {
         loConn = CConnection.getConnection(msConnection);

         liHandle = loConn.lockConnection();

         moLocalFactory.readPKeyColumns(loConn.getConnection(), asSchema, lbRegisterCatalog);

         loConn.releaseConnection(liHandle);
      }
      catch (Exception ex)
      {
         throw new CConnectionNotAvailableException(ex, msConnection);
      }
   }

   public synchronized void registerPrimaryKey(String asEntityName, String asPersistName)
   {
      moLocalFactory.registerPrimaryKey(asEntityName, asPersistName);
   }

   public synchronized void registerServiceClass(String asObjectName, Class aoClass, int aiLimit,
                                                 int aiInitialCapacity, int aiCapacityIncrement)
   {
      moLocalFactory.registerServiceClass(asObjectName, aoClass, aiLimit, aiInitialCapacity,
                                          aiCapacityIncrement);
   }

   /**
    * Registers service class.
    * @param asEntityName unique service name
    * @param aoClass service class
    */
   public synchronized void registerServiceClass(String asEntityName, Class aoClass, int aiLimit)
   {
      moLocalFactory.registerServiceClass(asEntityName, aoClass, aiLimit);
   }

   public synchronized void registerServiceClass(String asEntityName)
   {
      moLocalFactory.registerServiceClass(asEntityName);
   }

   public synchronized void releaseAll(boolean abRemoveObjects)
   {
      moLocalFactory.releaseAll(abRemoveObjects);
   }

   /**
    * Releases object for further reuse.
    * Object will be passivated.
    * @see IBusinessService#passivate
    */
   public synchronized void releaseObject(Integer aiObjectHash) // throws CPassivateFailedException, RemoteException
   {
      moLocalFactory.releaseObject(aiObjectHash);
   }

   private CLocalBusinessFactory moLocalFactory;
   private String msConnection;
}
