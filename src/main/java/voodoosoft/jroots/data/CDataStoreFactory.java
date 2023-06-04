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

import java.util.Vector;


/**
 * Default implementation of <code>IDataStoreFactory</code> creating <code>CDataStore</code> objects.
 * @see voodoosoft.jroots.data.CDataStore
 */
public class CDataStoreFactory extends CObject implements IDataStoreFactory
{
   /**
    * Allows to map Java classes to SQL types.
    */
   public static class CTypeMapping
   {
      /**
       * one of the types defined in <code>java.sql.Types</code>.
       */
      public int miType;

      /**
       * number of digits, only valid for numeric types.
       */
      public int miPrecision;

      /**
       * number of digits to right of the decimal point.
       */
      public int miScale;

      /**
       * class of object to create
       */
      public Class moJavaClass;
   }

   /**
    * Creates factory without specifying default construction properties.
    */
   public CDataStoreFactory()
   {
      mbDefaultsSet = false;
      miCapacityIncrement = -1;
      miQueryTimeout = -1;
   }

   /**
    * Creates factory of the specified default construction properties.
    */
   public CDataStoreFactory(boolean abSmartUpdate, boolean abOptimisticLocking)
   {
      setDefaultProperties(abSmartUpdate, abOptimisticLocking);
      miCapacityIncrement = -1;
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

   /**
    * Sets construction properties for all created <code>CDataStore</code> objects.
    * @param abSmartUpdate
    * @param abOptimisticLocking
    */
   public void setDefaultProperties(boolean abSmartUpdate, boolean abOptimisticLocking)
   {
      mbOptimisticLocking = abOptimisticLocking;
      mbSmartUpdate = abSmartUpdate;
      mbDefaultsSet = true;
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
    * Registers mapping between SQL types and Java classes for all created datastores.
    * @param aiType SQL type as defines in <code>java.sql.Types</code>
    * @param aiPrecision number of digits, only valid for numeric types
    * @param aiScale number of digits to right of the decimal point
    * @param aoJavaClass Java class to wrap column values
    * @see voodoosoft.jroots.data.CDataStore#mapColumnType
    */
   public void addTypeMapping(int aiType, int aiPrecision, int aiScale, Class aoJavaClass)
   {
      CTypeMapping loMapping;

      if (moTypeMappings == null)
      {
         moTypeMappings = new Vector();
      }

      loMapping = new CTypeMapping();
      loMapping.miType = aiType;
      loMapping.miScale = aiScale;
      loMapping.miPrecision = aiPrecision;
      loMapping.moJavaClass = aoJavaClass;

      moTypeMappings.add(loMapping);
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
    *  @see #setDefaultProperties
    */
   public IBasicDataStore createObject() throws SQLException
   {
      CDataStore loDS;

      loDS = new CDataStore();

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
    *  @see #setDefaultProperties
    */
   public IBasicDataStore createObject(PreparedStatement aoSQL)
                                throws SQLException
   {
      CDataStore loDS;

      loDS = new CDataStore(aoSQL);

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
    *  @see #setDefaultProperties
    */
   public IBasicDataStore createObject(String asSQL, boolean abPrepared)
                                throws SQLException
   {
      CDataStore loDS;

      loDS = new CDataStore(asSQL, abPrepared);

      initDataStore(loDS);

      return loDS;
   }

   /**
    *  Returns new <code>IDataStore</code> of the concrete class <code>CDataStore</code>.
    *  The new <code>CDataStore</code> gets the specified construction properties.
    *  <p>In addition, the following properties are set:
    *  <li>upper case naming
    *  <li>type mappings
    *  <li>capacity increment
    *  @see #setDefaultProperties
    */
   public IBasicDataStore createObject(String asSQL, boolean abPrepared, boolean abSmartUpdate,
                                       boolean abOptimisticLocking)
                                throws SQLException
   {
      CDataStore loDS;

      loDS = new CDataStore(asSQL, abPrepared);
      loDS.setSmartUpdate(abSmartUpdate);
      loDS.setOptimisticLocking(abOptimisticLocking);
      loDS.setUpperCaseNaming(mbUpperCaseNaming);

      setTypeMappings(loDS);

      if (miCapacityIncrement >= 0)
      {
         loDS.setCapacityIncrement(miCapacityIncrement);
      }

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

   private void setTypeMappings(CDataStore aoDS)
   {
      CTypeMapping loMapping;

      if (moTypeMappings != null)
      {
         for (int i = 0; i < moTypeMappings.size(); i++)
         {
            loMapping = (CTypeMapping) moTypeMappings.get(i);
            aoDS.mapColumnType(loMapping.miType, loMapping.miPrecision, loMapping.miScale,
                               loMapping.moJavaClass);
         }
      }
   }

   private void initDataStore(CDataStore aoDS)
   {
      if (mbDefaultsSet)
      {
         aoDS.setSmartUpdate(mbSmartUpdate);
         aoDS.setOptimisticLocking(mbOptimisticLocking);
      }

      aoDS.setUpperCaseNaming(mbUpperCaseNaming);

      setTypeMappings(aoDS);

      if (miCapacityIncrement >= 0)
      {
         aoDS.setCapacityIncrement(miCapacityIncrement);
      }

      if (miQueryTimeout >= 0)
      {
         aoDS.setQueryTimeout(miQueryTimeout);
      }
   }

   private boolean mbOptimisticLocking;
   private boolean mbSmartUpdate;
   private boolean mbUpperCaseNaming;
   private boolean mbDefaultsSet;
   private int miCapacityIncrement;
   private int miQueryTimeout;
   private Vector moTypeMappings;
}
