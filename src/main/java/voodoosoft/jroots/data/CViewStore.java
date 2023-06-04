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

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.core.data.*;
import voodoosoft.jroots.exception.CException;

import java.io.*;

import java.sql.*;

import java.util.*;


/**
 * The <code>CViewStore</code> class was designed to handle large rowsets in smaller chunks of data.
 * <p><code>CViewStore</code> opens a scrollable <code>ResultSet</code>.
 * An open connection is needed as long as <code>fetchNextChunk</code> or
 * <code>fetchPreviousChunk</code> are called.
 * The internal <code>ResultSet</code> will be created by <code>retrieve</code>,
 * where the first data chunk will be retrieved as well.
 * The chunk size, or number of rows to cache, should be set before data retrieval because
 * changing the chunk size will clear <code>CViewStore</code>.
 * <p>
 * <li>valid row numbers are 1 to chunk size, respectively less if the last
 * chunk happens to have less rows.
 * <li><code>CViewStore</code> is the underlying storage class for {@link voodoosoft.jroots.business.CViewSet CViewSet}.
 * <li><code>CViewStore</code> objects are always read-only storages.
 * @see voodoosoft.jroots.data.CDataStore
 */
public class CViewStore extends CObject implements Serializable, IBasicDataStore
{
   /**
    * Creates <code>CViewStore</code> with default chunk size and capacity increment.
    * <p>chunk size = 100
    * <p>capacity increment = 100
    */
   public CViewStore() throws SQLException
   {
      super("");

      moPreparedSQL = null;
      msSQLSelect = null;

      setChunkSize(100);
      setCapacityIncrement(100);
   }

   /**
    * Creates new datastore using the supplied SQL statement.
    * The SQL will be executed directly without <code>PreparedStatement</code>.
    * <p>Note that before calling {@link #retrieve retrieve} this datastore has no columns.
    * @param asSQLSelect valid SQL SELECT statement
   */
   public CViewStore(String asSQLSelect) throws SQLException
   {
      this(asSQLSelect, false);
   }

   /**
    * Creates new datastore of the supplied SQL statement.
    * Depending on the given parameter, a <code>PreparedStatement</code> will be created for retrieving
    * data.
    * <p>Note that before calling {@link #retrieve retrieve} this datastore has no columns.
    * @param asSQLSelect valid SQL SELECT statement
    * @param abPreparedSQL if true, use prepared statement for data retrieval
   */
   public CViewStore(String asSQLSelect, boolean abPreparedSQL)
              throws SQLException
   {
      this();

      setSQL(asSQLSelect);
      mbPreparedSQL = abPreparedSQL;
   }

   /**
    * Creates new data store using the specified prepared statement.
    * <p>Note that before calling {@link #retrieve retrieve} this datastore has no columns.
    * @param aoSQL prepared SQL
    */
   public CViewStore(PreparedStatement aoSQL) throws SQLException
   {
      this();

      setSQL(aoSQL);
   }

   /**
    * Defines size of memory allocated for every chunk retrieval.
    * Default value is 100.
    * @param aiCapacityIncrement
    * @throws IllegalArgumentException
    */
   public void setCapacityIncrement(int aiCapacityIncrement)
                             throws IllegalArgumentException
   {
      if (aiCapacityIncrement < 0)
      {
         throw new IllegalArgumentException("capacity increment must be >= 0");
      }

      miCapacityIncrement = aiCapacityIncrement;
   }

   /**
    * Changes chunk size and resets this <code>CViewStore</code>.
    * @param aiChunkSize number of rows to cache; 0 means unlimited rows
    */
   public void setChunkSize(int aiChunkSize) throws IllegalArgumentException
   {
      reset();

      if (aiChunkSize < 0)
      {
         throw new IllegalArgumentException("chunk size must be >= 0");
      }

      miChunkSize = aiChunkSize > 0 ? aiChunkSize : Integer.MAX_VALUE;
   }

   /**
    * Gets column value of specified row  by column name.
    * By default, the JDBC driver determines the class of the returned object -
    * if no explicit type mapping has been specified.
    * @param aiRow valid row (1 to row count)
    * @param columnName
    * @return column value
    * @see #mapColumnType
   */
   public Object getColumn(int aiRow, String columnName)
                    throws CInvalidColumnException
   {
      Object loColumn = null;

      try
      {
         if (mbUpperCaseNaming)
         {
            loColumn = ((Vector) moColumnMap.get(columnName.toUpperCase())).get(virtualToAbsolute(aiRow));
         }
         else
         {
            loColumn = ((Vector) moColumnMap.get(columnName)).get(virtualToAbsolute(aiRow));
         }
      }
      catch (Exception ex)
      {
         throw new CInvalidColumnException(this, columnName, aiRow);
      }

      return loColumn;
   }

   /**
     * Gets column value of specified row by column number.
     * By default, the JDBC driver determines the class of the returned object -
     * if no explicit type mapping has been specified.
     * @param aiRow valid row number (1 to row count)
     * @param colunmIndex valid column number (1 to column count)
     * @return column value
     * @see #mapColumnType
   */
   public Object getColumn(int aiRow, int colunmIndex)
                    throws CInvalidColumnException
   {
      Object loColumn = null;

      try
      {
         loColumn = moOriginalColumns[colunmIndex].get(virtualToAbsolute(aiRow));
      }
      catch (Exception ex)
      {
         throw new CInvalidColumnException(this, colunmIndex, aiRow);
      }

      return loColumn;
   }

   /**
    * Returns a <code>CPropertyClass</code> describing the specified column.
    * @param asName
    * @return column class as <code>CPropertyClass</code>
    * @see voodoosoft.jroots.core.CPropertyClass
    */
   public CPropertyClass getColumnClass(String asName)
   {
      CPropertyClass loClass;

      if (mbUpperCaseNaming)
      {
         loClass = moColumnClasses[((Integer) moColumnNumberMap.get(asName.toUpperCase())).intValue()];
      }
      else
      {
         loClass = moColumnClasses[((Integer) moColumnNumberMap.get(asName)).intValue()];
      }

      return loClass;
   }

   /**
    * Returns column number of this datastore.
    * @return number of column this datastore has
   */
   public int getColumnCount()
   {
      return miColumnCount;
   }

   /**
    * Returns name of given column number.
    * @param aiColumn
    * @return column name
    */
   public String getColumnName(int aiColumn)
   {
      return msColumnNames[aiColumn];
   }

   /**
    * Sets parameters of this datastore's PreparedStatement (if used at all)
    * @param aoParam array of parameters, in same order as defined in SQL
    */
   public void setParameters(Object[] aoParam) throws SQLException
   {
      moPreparedArgs = aoParam;
   }

   public void setQueryTimeout(int aiQueryTimeout)
   {
      miQueryTimeout = aiQueryTimeout;
   }

   /**
    * Returns number of rows (chunk size or less).
    * @return number of rows
    */
   public int getRowCount()
   {
      return miGrossCount;
   }

   /**
    * Sets this datastore's SQL to the given select statement.
    * Depending on the given parameter, a prepared statement will be created for retrieving
    * data.
    * <p>Note that calling <code>setSQL</code> immediately clears all rows and columns of this
    * datastore.
    * @param asSQL valid SQL SELECT statement
    * @param abPreparedSQL if true, use prepared statement for data retrieval
    */
   public void setSQL(String asSQL, boolean abPreparedSQL)
   {
      reset();

      mbPreparedSQL = abPreparedSQL;
      msSQLSelect = asSQL;
      moPreparedSQL = null;
   }

   /**
    * Sets this datastore's SQL to the given select statement.
    * The specified SQL will be executed directly without creating a prepared statement.
    * <p>Note that calling <code>setSQL</code> immediately clears all rows and columns of this
    * datastore.
    * @param asSQL valid SQL SELECT statement
    */
   public void setSQL(String asSQL)
   {
      setSQL(asSQL, false);
   }

   /**
    * Sets this datastore's SQL to the given select statement.
    * The specified SQL will be executed directly without creating a prepared statement.
    * <p>Note that calling <code>setSQL</code> immediately clears all rows and columns of this
    * datastore.
    * @param aoSQL valid SQL SELECT <code>PreparedStatement</code>
    */
   public void setSQL(PreparedStatement aoSQL)
   {
      reset();

      mbPreparedSQL = true;
      msSQLSelect = null;
      moPreparedSQL = aoSQL;
   }

   public void setUpperCaseNaming(boolean abUpperCase)
   {
      mbUpperCaseNaming = abUpperCase;
   }

   /**
    * Tries to cancel data retrieval.
    * <p>Note that canceling queries is database and driver dependent.
    * @throws CSQLException
    */
   public void cancel() throws CSQLException
   {
      try
      {
         mbCanceled = true;

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("cancel requested ...");
         }

         if (mbPreparedSQL)
         {
            if (moPreparedSQL != null)
            {
               moPreparedSQL.cancel();
            }
         }
         else
         {
            if (moStatement != null)
            {
               moStatement.cancel();
            }
         }
      }
      catch (SQLException ex)
      {
         throw new CSQLException(ex);
      }
   }

   /**
    * Replaces the internal data cache with data beginning at the specified row number.
    * For <code>fetchChunk</code> to work, the internal scrollable <code>ResultSet</code>
    * requires that the connection specified when <code>retrieve</code> was invoked is still open.
    * @param aiStartRow
    * @return true if there are more chunks available
    * @throws SQLException
    */
   public boolean fetchChunk(int aiStartRow) throws SQLException
   {
      boolean lbFound = false;

      miStartRow = aiStartRow > 0 ? aiStartRow : 1;
      miEndRow = miStartRow + miChunkSize - 1;
      populate(moResultSet, miStartRow, miEndRow, false);
      lbFound = mbHasMoreRows;

      return lbFound;
   }

   /**
    * Replaces the internal data cache with data of next chunk.
    * For <code>fetchNextChunk</code> to work, the internal scrollable <code>ResultSet</code>
    * requires that the connection specified when <code>retrieve</code> was invoked is still open.
    * @return true if there are more chunks available
    * @throws SQLException
    */
   public boolean fetchNextChunk() throws SQLException
   {
      return fetchChunk(miEndRow + 1);
   }

   /**
    * Replaces the internal data cache with data of previous chunk (beginning at current-start-row minus chunk-size).
    * For <code>fetchPreviousChunk</code> to work, the internal scrollable <code>ResultSet</code>
    * requires that the connection specified when <code>retrieve</code> was invoked is still open.
    * @return true if there are more chunks available 
    * @throws SQLException
    */
   public boolean fetchPreviousChunk() throws SQLException
   {
      fetchChunk(miStartRow - miChunkSize);

      if (miStartRow == 1)
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   /**
    * Returns true if this <code>CViewStore</code> can fetch at least one more chunk forwardly.
    * @return true if there are more chunks available
    */
   public boolean hasNextChunk()
   {
      return mbHasMoreRows;
   }

   /**
    * Returns true if this <code>CViewStore</code> can fetch at least one more chunk backwardly.
    * @return true if there are more chunks available
    */
   public boolean hasPreviousChunk()
   {
      return (miStartRow > 1 ? true : false);
   }

   /**
    * Maps JDBC types to Java classes for data access with <code>getColumn</code>.
    * Supported classes are
    * <li> java.lang.Integer
    * <li> java.lang.Long
    * <li> java.lang.String
    * @param aiType SQL type as defines in <code>java.sql.Types</code>
    * @param aiPrecision number of digits, only valid for numeric types
    * @param aiScale number of digits to right of the decimal point
    * @param aoJavaClass Java class to wrap column values when <code>getColumn</code> is called
    */
   public void mapColumnType(int aiType, int aiPrecision, int aiScale, Class aoJavaClass)
   {
      String lsTypeLookUp;

      if (moColumnTypeConversions == null)
      {
         moColumnTypeConversions = new HashMap();
      }

      lsTypeLookUp = String.valueOf(aiType) + "," + String.valueOf(aiPrecision) + "," +
                     String.valueOf(aiScale);

      moColumnTypeConversions.put(lsTypeLookUp, aoJavaClass);
   }

   /**
    * Clears data and resets internal status.
    */
   public void reset()
   {
      miGrossCount = 0;

      miStartRow = 0;
      miEndRow = 0;

      miColumnCount = 0;
      msColumnNames = null;
      moOriginalColumns = null;
      moColumnClasses = null;
      moColumnMap = null;
      moColumnNumberMap = null;
      mbHasMoreRows = false;

      try
      {
         if (moResultSet != null)
         {
            moResultSet.close();
         }

         if (moStatement != null)
         {
            moStatement.close();
            moStatement = null;
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this, false);
      }
   }

   /**
    * Sends SQL to the database and fills the internal data cache with the first row chunk.
    * @param aoCon <code>CConnection</code> to use
    * @return current number of retrieved rows; max. chunk size
   */
   public int retrieve(CConnection aoCon) throws CSQLException
   {
      int liRowCount;
      Connection loDriverCon;

      try
      {
         reset();
         mbCanceled = false;

         loDriverCon = aoCon.getConnection();

         if (mbPreparedSQL)
         {
            if (moPreparedSQL == null || miRetrieveHash != loDriverCon.hashCode())
            {
               moPreparedSQL = loDriverCon.prepareStatement(msSQLSelect,
                                                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                            ResultSet.CONCUR_READ_ONLY);
            }

            setPreparedArgs();
            moPreparedSQL.setQueryTimeout(miQueryTimeout);

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("executing query (" + new Timestamp(System.currentTimeMillis()) +
                              ")");
            }

            moResultSet = moPreparedSQL.executeQuery();
         }
         else
         {
            moStatement = aoCon.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                ResultSet.CONCUR_READ_ONLY);
            moStatement.setQueryTimeout(miQueryTimeout);

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("executing query (" + new Timestamp(System.currentTimeMillis()) +
                              ")");
            }

            moResultSet = moStatement.executeQuery(msSQLSelect);
         }

         miRetrieveHash = loDriverCon.hashCode();

         // grab data
         miStartRow = 1;
         miEndRow = miChunkSize;
         populate(moResultSet, miStartRow, miEndRow, true);

         if (mbCanceled)
         {
            reset();

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("canceled");
            }
         }

         // init row states
         liRowCount = getRowCount();
      }
      catch (Exception ex)
      {
         reset();

         //         ex.printStackTrace();
         throw new CSQLException(ex, msSQLSelect);
      }
      finally
      {
         moStatement = null;
      }

      return liRowCount;
   }

   /**
    * Indicates whether this data store uses a <code>PreparedStatement</code>.
    * @return true if this datastore uses prepared statements for data retrieval
    */
   public boolean usesPreparedSQL()
   {
      return mbPreparedSQL;
   }

   /**
    * Returns true if this <code>CViewStore</code> was asked to cancel data retrieval.
    * <p><code>wasCanceled</code>
    * @return cancel flag
    */
   public boolean wasCanceled()
   {
      return mbCanceled;
   }

   private void setPreparedArgs() throws SQLException
   {
      for (int i = 0; i < moPreparedArgs.length; i++)
      {
         moPreparedSQL.setObject(i + 1, moPreparedArgs[i]);
      }
   }

   private Object getValue(ResultSet aoRS, int aiColumn)
                    throws SQLException
   {
      int liValue;
      long llValue;
      Object loValue = null;
      Clob loClob;

      if (moColumnJavaClasses[aiColumn] == Integer.class)
      {
         liValue = aoRS.getInt(aiColumn);

         if (!aoRS.wasNull())
         {
            loValue = new Integer(liValue);
         }
      }
      else if (moColumnJavaClasses[aiColumn] == Long.class)
      {
         llValue = aoRS.getLong(aiColumn);

         if (!aoRS.wasNull())
         {
            loValue = new Long(llValue);
         }
      }
      else if (moColumnJavaClasses[aiColumn] == String.class)
      {
         if (miColumnTypes[aiColumn] == Types.CLOB)
         {
            loClob = aoRS.getClob(aiColumn);

            if (loClob != null)
            {
               loValue = loClob.getSubString(1, (int) loClob.length());
            }
         }
         else
         {
            loValue = aoRS.getString(aiColumn);
         }
      }
      else
      {
         loValue = aoRS.getObject(aiColumn);
      }

      return loValue;
   }

   private void memColumnClasses(ResultSetMetaData aoMetaData)
                          throws SQLException
   {
      int liColumn;
      int liSQLType;
      int liPrecision;
      int liScale;
      int liDisplaySize;
      CPropertyClass loType;
      Class loJavaClass;
      String lsTypeLookUp;

      moColumnClasses = new CPropertyClass[miColumnCount + 1];
      moColumnJavaClasses = new Class[miColumnCount + 1];
      miColumnTypes = new int[miColumnCount + 1];

      for (liColumn = 1; liColumn <= miColumnCount; liColumn++)
      {
         liScale = aoMetaData.getScale(liColumn);
         liSQLType = aoMetaData.getColumnType(liColumn);
         liDisplaySize = aoMetaData.getColumnDisplaySize(liColumn);

         switch (liSQLType)
         {
            case Types.BLOB:
            case Types.CLOB:
            case Types.BINARY:
            case Types.VARBINARY:
               liPrecision = 0;

               break;

            default:
               liPrecision = aoMetaData.getPrecision(liColumn);
         }

         loJavaClass = null;

         if (moColumnTypeConversions != null)
         {
            lsTypeLookUp = String.valueOf(liSQLType) + "," + String.valueOf(liPrecision) + "," +
                           String.valueOf(liScale);
            loJavaClass = (Class) moColumnTypeConversions.get(lsTypeLookUp);
            moColumnJavaClasses[liColumn] = loJavaClass;
         }

         if (loJavaClass == null)
         {
            loJavaClass = CDBTools.SQLtoJavaType(liSQLType);
         }

         loType = new CPropertyClass(loJavaClass, false, liPrecision, liScale, liDisplaySize);

         moColumnClasses[liColumn] = loType;
         miColumnTypes[liColumn] = liSQLType;
      }
   }

   private void populate(ResultSet aoRS, int aiStartRow, int aiEndRow, boolean abClear)
                  throws SQLException
   {
      boolean lbSuccess;
      boolean lbFirstRow = false;
      int liMaxRows;
      int liRowsFound;
      ResultSetMetaData loMetaData;
      Vector[] loTempColumns;
      Object loValue;

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("populating " + String.valueOf(aiStartRow) + " - " +
                        String.valueOf(aiEndRow) + " (" +
                        new Timestamp(System.currentTimeMillis()) + ")");
      }

      if (abClear)
      {
         loMetaData = aoRS.getMetaData();
         miColumnCount = loMetaData.getColumnCount();

         msColumnNames = new String[miColumnCount + 1];
         moColumnNumberMap = new HashMap();

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("scanning columns names");
         }

         for (int i = 1; i <= miColumnCount; i++)
         {
            if (mbUpperCaseNaming)
            {
               msColumnNames[i] = loMetaData.getColumnName(i).toUpperCase();
            }
            else
            {
               msColumnNames[i] = loMetaData.getColumnName(i);
            }

            moColumnNumberMap.put(msColumnNames[i], new Integer(i));
         }

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("scanning columns types");
         }

         memColumnClasses(loMetaData);

         if (!mbCanceled)
         {
            lbFirstRow = aoRS.next();
         }
      }
      else
      {
         lbFirstRow = true;
      }

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("getting data (" + new Timestamp(System.currentTimeMillis()) + ")");
      }

      loTempColumns = new Vector[miColumnCount + 1];

      if (!lbFirstRow || mbCanceled)
      {
         mbHasMoreRows = false;
         moOriginalColumns = loTempColumns;

         return;
      }

      // allocate row memory
      for (int i = 1; i <= miColumnCount; i++)
      {
         loTempColumns[i] = new Vector(miCapacityIncrement + 1, miCapacityIncrement);
         loTempColumns[i].add(null);
      }

      // copy resultset data
      liMaxRows = aiEndRow - aiStartRow + 1;
      liRowsFound = 0;
      lbSuccess = aoRS.absolute(aiStartRow);

      while (liRowsFound < liMaxRows && lbSuccess)
      {
         liRowsFound++;

         for (int i = 1; i <= miColumnCount; i++)
         {
            if (moColumnJavaClasses[i] == null)
            {
               loValue = aoRS.getObject(i);
            }
            else
            {
               loValue = getValue(aoRS, i);
            }

            loTempColumns[i].add(loValue);
         }

         lbSuccess = mbCanceled ? false : aoRS.next();
      }

      if (mbCanceled)
      {
         return;
      }

      if (liRowsFound > 0)
      {
         miGrossCount = liRowsFound;
         moOriginalColumns = loTempColumns;
         moColumnMap = new HashMap(miColumnCount + 1);

         for (int i = 1; i <= miColumnCount; i++)
         {
            moColumnMap.put(msColumnNames[i], moOriginalColumns[i]);
         }
      }

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("populated " + String.valueOf(miGrossCount) + " rows (" +
                        new Timestamp(System.currentTimeMillis()) + ")");
      }

      mbHasMoreRows = !aoRS.isAfterLast();
   }

   /**
    * @param aiRow virtual row number
    * @return corresponding Vector row
    */
   private int virtualToAbsolute(int aiVirtualRow) throws Exception
   {
      if (aiVirtualRow <= 0 || aiVirtualRow > miGrossCount)
      {
         throw new IllegalArgumentException("invalid row number: " + String.valueOf(aiVirtualRow));
      }

      return aiVirtualRow;
   }

   protected static Logger moLogger = Logger.getLogger(CViewStore.class);

   /** SQL as string to populate the datastore */
   private String msSQLSelect;
   private boolean mbPreparedSQL;

   /** SQL as prepared statement to retrieve data */
   private transient PreparedStatement moPreparedSQL;
   private transient ResultSet moResultSet;
   private transient Statement moStatement;

   /** Arguments for the prepared statement */
   private Object[] moPreparedArgs;

   /** row count including deleted and discarded rows */
   private int miGrossCount;
   private boolean mbHasMoreRows;

   /** First and last row to cache (numbers relate to the ResultSet */
   private int miStartRow;

   /** First and last row to cache (numbers relate to the ResultSet */
   private int miEndRow;
   private int miChunkSize;
   private int miCapacityIncrement;

   /** row data */
   private Vector[] moOriginalColumns;

   /** column names */
   private String[] msColumnNames;
   private CPropertyClass[] moColumnClasses;
   private Class[] moColumnJavaClasses;
   private int[] miColumnTypes;
   private HashMap moColumnTypeConversions;

   /** column count */
   private int miColumnCount;

   /** maps columns names to moColumns[] */
   private HashMap moColumnMap;

   /** maps columns names to column numbers */
   private HashMap moColumnNumberMap;
   private int miRetrieveHash;
   private boolean mbUpperCaseNaming;
   private int miQueryTimeout;
   private boolean mbCanceled;
}
