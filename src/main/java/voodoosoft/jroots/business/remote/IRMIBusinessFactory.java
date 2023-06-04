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
import voodoosoft.jroots.core.CClassNotRegisteredException;
import voodoosoft.jroots.core.CObjectNotAvailable;
import voodoosoft.jroots.data.CConnectionNotAvailableException;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for remote business factories.
 */
public interface IRMIBusinessFactory extends Remote
{
   public Integer getHashID() throws RemoteException;

   /**
    * Gets service object by name.
    * @param asName service name
    * @return wanted service or null if not found
    */

   //   public IBusinessService getObject(String asName) throws CObjectNotAvailable, RemoteException;
   public IBusinessService getObject(String asName)
                              throws CObjectNotAvailable, CActivateFailedException, RemoteException, 
                                     CConnectionNotAvailableException;

   /**
    * Gets service object by primary key.
    * @param aoKey key to identify and activate service
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(IPrimaryKey aoKey)
                              throws CObjectNotAvailable, CActivateFailedException, RemoteException, 
                                     CConnectionNotAvailableException;

   /**
    * Gets service object by primary key.
    * @param aoKey key to identify and activate service
    * @param abLazyInit service will not be initialized before required, e.g. for limiting unnecessary database access
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(IPrimaryKey aoKey, boolean abLazyInit)
                              throws CObjectNotAvailable, CActivateFailedException, RemoteException, 
                                     CConnectionNotAvailableException;

   /**
    * Creates primary key object for the given service name.
    * Name must be previously registered.
    * @param asName service to create key for
    * @return new created key object
    */
   public IPrimaryKey createPrimaryKey(String asName) throws CKeyCreationException, RemoteException;

   public void executeProcess(IBusinessProcess aoProcess)
                       throws RemoteException, CExecuteFailedException;

   /**
    * Stores service object in factory for later usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    */
   public void putObject(Object aoObject, String asObjectName)
                  throws RemoteException, CClassNotRegisteredException;

   /**
    * Reads all database primary keys from a given connection.
    * @param asSchema database schema to analyze
    * @param lbRegisterCatalog complete key entries with database catalog ?
    */
   public void readPKeyColumns(String asSchema, boolean lbRegisterCatalog)
                        throws RemoteException, CConnectionNotAvailableException;

   public void registerPrimaryKey(String asEntityName, String asPersistName)
                           throws RemoteException;

   /**
    * Registers service class.
    * @param asEntityName unique service name
    * @param aoClass service class
    */
   public void registerServiceClass(String asEntityName, Class aoClass, int aiLimit)
                             throws RemoteException;

   public void registerServiceClass(String asEntityName)
                             throws RemoteException;

   /**
    * Sets usage flag of all services to free.
    * @param abRemoveObjects if true, service object references are
    */
   public void releaseAll(boolean abRemoveObjects) throws RemoteException;

   /**
    * Sets usage flag of the given service to free.
    * @param aiObjectHash object to release
    * @see IBusinessService#passivate
    */
   public void releaseObject(Integer aiObjectHash)
                      throws CPassivateFailedException, RemoteException;
}
