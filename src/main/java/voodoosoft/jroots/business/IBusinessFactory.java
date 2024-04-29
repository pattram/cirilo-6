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

import voodoosoft.jroots.core.CClassNotRegisteredException;
import voodoosoft.jroots.core.CObjectNotAvailable;
import voodoosoft.jroots.core.CObjectPutFailedException;
import voodoosoft.jroots.data.CConnection;

import java.sql.*;


/**
 * Interface for standard (non-remote) business factories.
 */
public interface IBusinessFactory
{
   /**
    * Gets service object by name.
    * @param asName service name
    * @param aoConn <code>CConnection</code> to use
    * @return wanted service or null if not found
    */
   public IBusinessService getObject(String asName, CConnection aoConn)
                              throws CObjectNotAvailable, CActivateFailedException;

   public IBusinessService getObject(String asName)
                              throws CObjectNotAvailable, CActivateFailedException;

   /**
    * Gets service object by primary key.
    * @param aoKey key to identify and activate service
    * @param aoConn connection used for activation
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(IPrimaryKey aoKey, CConnection aoConn)
                              throws CObjectNotAvailable, CActivateFailedException;

   public IBusinessService getObject(IPrimaryKey aoKey)
                              throws CObjectNotAvailable, CActivateFailedException;

   /**
    * Gets service object by primary key.
    * @param aoKey key to identify and activate service
    * @param aoConn connection used for activation
    * @param abLazyInit service will not be initialized before required, e.g. for limiting unnecessary database access
    * @return wanted service or null if not found
    * @see IBusinessService#activate
    */
   public IBusinessService getObject(IPrimaryKey aoKey, CConnection aoConn, boolean abLazyInit)
                              throws CObjectNotAvailable, CActivateFailedException;

   /**
    * Creates primary key object for the given service name.
    * @param asName service to create key for
    * @return new created key object
    */
   public IPrimaryKey createPrimaryKey(String asName) throws CKeyCreationException;

   /**
    * Executes specified process.
    * @param aoProcess
    */
   public void executeProcess(IBusinessProcess aoProcess) throws CExecuteFailedException;

   /**
    * Stores service object in factory for later (exclusive) usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    */
   public void putObject(Object aoObject, String asObjectName)
                  throws CObjectPutFailedException, CClassNotRegisteredException;

   /**
    * Stores service object in factory for later usage.
    * @param aoObject object to store
    * @param asObjectName unique name to identify
    * @param abSharable true indicates that the given service may be shared among several clients
    */
   public void putObject(Object aoObject, String asObjectName, boolean abSharable)
                  throws CObjectPutFailedException, CClassNotRegisteredException;

   /**
    * Reads all database primary keys from a given connection.
    * @param aoConn source connection
    * @param asSchema database schema to analyze
    * @param lbRegisterCatalog complete key entries with database catalog ?
    */
   public void readPKeyColumns(Connection aoConn, String asSchema, boolean lbRegisterCatalog);

   /**
    * Maps business service to the specified persist name.
    * @param asEntityName business service
    * @param asPersistName typically database table as key source
    * @throws CKeyRegisterException
    */
   public void registerPrimaryKey(String asEntityName, String asPersistName)
                           throws CKeyRegisterException;

   /**
    * Registers business service with specified Java class.
    * Before business service requests are possible, they must previously be registered.
    * @param asEntityName service name, unique for this <code>IBusinessFactory</code>
    * @param aoClass service class in case the factory needs to create a new object
    */
   public void registerServiceClass(String asEntityName, Class aoClass, int aiLimit);

   /**
    * Registers business service.
    * Before business service requests are possible, they must previously be registered.
    * @param asEntityName service name, unique for this <code>IBusinessFactory</code>
    */
   public void registerServiceClass(String asEntityName);

   /**
    * Sets usage flag of all services to free.
    * @param abRemoveObjects if true, service object references are
    */
   public void releaseAll(boolean abRemoveObjects);

   /**
    * Sets usage flag of the given service to free.
    * @param aoObject object to release
    * @see IBusinessService#passivate
    */
   public void releaseObject(IBusinessService aoObject); //throws CReleaseObjectFailedException;
}
