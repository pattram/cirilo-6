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

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.exception.CException;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * Default implementation of <code>IDataStoreFactory</code> creating <code>CViewStore</code> objects.
 * @see voodoosoft.jroots.data.CViewStore
 */
public class CViewStoreFactory extends CObject implements IDataStoreFactory
{
   /**
    * Creates new factory.
    */
   public CViewStoreFactory()
   {
      miCapacityIncrement = -1;
      miQueryTimeout = -1;
   }

   /**
    * Adds or removes capacity increment setting for all datastores.
    * @param aiCapacityIncrement capacity increment; -1 removes setting
    * @throws IllegalArgumentException
    */
   public void setCapacityIncrement(int aiCapacityIncrement)
   {
      miCapacityIncrement = aiCapacityIncrement;
   }

   public void setChunkSize(int aiChunkSize)
   {
      miChunkSize = aiChunkSize;
   }

   public void setQueryTimeout(int aiQueryTimeout)
   {
      miQueryTimeout = aiQueryTimeout;
   }

   /**
    *
    * Enables or disabled uppercase naming for this <code>CDataStoreFactory</code>, and consequently for every created datastore.
    * @param abUpperCase
    * @see voodoosoft.jroots.data.CDataStore#setUpperCaseNaming
    */
   public void setUpperCaseNaming(boolean abUpperCase)
   {
      mbUpperCaseNaming = abUpperCase;
   }

   /**
    * Analyses the specified connection to determine if the database uses uppercase identifiers.
    * If uppercase naming is enabled, this property is set to all by <code>createObject</code> constructed datastores.
    * @param aoConn
    * @see voodoosoft.jroots.data.CDataStore#setUpperCaseNaming
    */
   public void setUpperCaseNaming(CConnection aoConn)
   {
      DatabaseMetaData loMData;

      try
      {
         loMData = aoConn.getConnection().getMetaData();
         mbUpperCaseNaming = loMData.storesUpperCaseIdentifiers();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    *  Returns new <code>IDataStore</code> of the concrete class <code>CDataStore</code>.
    *  The new <code>CDataStore</code> gets the default construction properties of this <code>CDataStoreFactory</code>,
    *  if they were specified before.
    *  <p>They are:
    *  <li>smart update
    *  <li>optimistic locking
    *  <li>upper case naming
    *  <li>type mappings
    *  <li>capacity increment
    */
   public IBasicDataStore createObject() throws SQLException
   {
      CViewStore loDS;

      loDS = new CViewStore();

      initDataStore(loDS);

      return loDS;
   }

   /**
    *  Returns new <code>IDataStore</code> of the concrete class <code>CDataStore</code>.
    *  The new <code>CDataStore</code> gets the default construction properties of this <code>CDataStoreFactory</code>,
    *  if they were specified before.
    *  <p>They are:
    *  <li>smart update
    *  <li>optimistic locking
    *  <li>upper case naming
    *  <li>type mappings
    *  <li>capacity increment
    */
   public IBasicDataStore createObject(PreparedStatement aoSQL)
                                throws SQLException
   {
      CViewStore loDS;

      loDS = new CViewStore(aoSQL);

      initDataStore(loDS);

      return loDS;
   }

   /**
    *  Returns new <code>IDataStore</code> of the concrete class <code>CDataStore</code>.
    *  The new <code>CDataStore</code> gets the default construction properties of this <code>CDataStoreFactory</code>,
    *  if they were specified before.
    *  <p>They are:
    *  <li>smart update
    *  <li>optimistic locking
    *  <li>upper case naming
    *  <li>type mappings
    *  <li>capacity increment
    */
   public IBasicDataStore createObject(String asSQL, boolean abPrepared)
                                throws SQLException
   {
      CViewStore loDS;

      loDS = new CViewStore(asSQL, abPrepared);

      initDataStore(loDS);

      return loDS;
   }

   /**
    * Returns true if this <code>CDataStoreFactory</code> sets constructed datastores to uppercase naming.
    * Return value depends on any prior call of <code>setUpperCaseNaming</code>, defaults to <code>false</code>.
    * @return naming state
    */
   public boolean hasUpperCaseNaming()
   {
      return mbUpperCaseNaming;
   }

   private void initDataStore(CViewStore aoDS)
   {
      aoDS.setUpperCaseNaming(mbUpperCaseNaming);

      if (miCapacityIncrement >= 0)
      {
         aoDS.setCapacityIncrement(miCapacityIncrement);
      }

      if (miQueryTimeout >= 0)
      {
         aoDS.setQueryTimeout(miQueryTimeout);
      }

      if (miChunkSize >= 0)
      {
         aoDS.setChunkSize(miChunkSize);
      }
   }

   private boolean mbUpperCaseNaming;
   private int miCapacityIncrement;
   private int miQueryTimeout;
   private int miChunkSize;
}
