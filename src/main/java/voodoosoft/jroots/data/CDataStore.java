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
import voodoosoft.jroots.exception.*;

import java.io.Serializable;

import java.sql.*;

import java.util.*;


/**
 * Storage class for handling database data.
 * It offers a convenient way to select, update, delete and insert data; all required SQL statements
 * are internally built.
 * <code>CDataStore</code> offers direct row access, no handling of cursor positions is necessary.
 * <p>
 * <li> valid row numbers: 1 to n
 * <li> valid columns numbers: 1 to n
 * <p>
 * Because <code>CDataStore</code> objects caches all data it might not be the best choice
 * for large sets of data.
 */
public class CDataStore extends CObject implements IDataStore, Serializable
{
   /**
    * Standard contructor.
    */
   public CDataStore() throws SQLException
   {
      super("");

      mbRowsCounted = false;
      mbHideDeletedRows = true;
      moPreparedSQL = null;
      msSQLSelect = null;
      mbUpToDate = true;
      miLastRow = 0;
      miLastAbsoluteRow = 0;
      miRowCount = -1;
   }

   /**
    * Creates new datastore using the supplied SQL statement.
    * The SQL will be executed directly, prepared statements are only used for updating
    * but not for data retrieval.
    * Note that before calling {@link #retrieve retrieve} this datastore has no columns.
    * @param asSQLSelect valid SQL SELECT statement
   */
   public CDataStore(String asSQLSelect) throws SQLException
   {
      this(asSQLSelect, false);
   }

   /**
    * Creates new datastore using the supplied SQL statement.
    * Depending on the given parameter, a prepared statement will be created for retrieving
    * data. Updating data always uses prepared statements.
    * Note that before calling {@link #retrieve retrieve} this datastore has no columns.
    * @param asSQLSelect valid SQL SELECT statement
    * @param abPreparedSQL if true, use prepared statement for data retrieval
   */
   public CDataStore(String asSQLSelect, boolean abPreparedSQL)
              throws SQLException
   {
      this();

      setSQL(asSQLSelect);
      mbPreparedSQL = abPreparedSQL;
   }

   /**
    * Creates new data store using the specified prepared statement.
    * Note that before calling {@link #retrieve retrieve} this datastore has no columns.
    * @param aoSQL prepared SQL
    */
   public CDataStore(PreparedStatement aoSQL) throws SQLException
   {
      this();

      setSQL(aoSQL);
   }

   /**
    * Defines size of memory allocated for internal column vectors.
    * Default value is 10.
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
    * Sets column to new value.
    * No changes are made in the database until <code>update</code> is called.
    * <code>setColumn</code> will modify the row state to include <code>ROW_MODIFIED</code>
    * <p>
    * If the given column value is of class <code>java.util.Date</code>, it will be converted
    * to either <code>java.sql.Date</code> or <code>java.sql.Timestamp</code> according to
    * the column type the JDBC driver returned.
    * @param aiRow row number
    * @param columnName column name
    * @param columnValue new value
    * @see #update
    */
   public void setColumn(int aiRow, String columnName, Object columnValue)
                  throws CInvalidColumnException //throws SQLException
   {
      int liRow;
      Vector loColumnRows;
      java.util.Date loUtilDate;
      Class loSQLClass;

      if (mbUpperCaseNaming)
      {
         columnName = columnName.toLowerCase();
      }

      // convert java.util.Date to sql types
      if (columnValue != null && columnValue.getClass() == java.util.Date.class)
      {
         loUtilDate = (java.util.Date) columnValue;
         loSQLClass = getColumnClass(columnName).javaClass;

         if (loSQLClass.equals(java.sql.Date.class))
         {
            columnValue = new java.sql.Date(loUtilDate.getTime());
         }
         else if (loSQLClass.equals(java.sql.Timestamp.class))
         {
            columnValue = new java.sql.Timestamp(loUtilDate.getTime());
         }
      }

      liRow = virtualToAbsolute(aiRow);

      loColumnRows = (Vector) moColumnMap.get(columnName);

      if (loColumnRows == null)
      {
         if (!moColumnMap.containsKey(columnName))
            throw new CInvalidColumnException(this, columnName);
         else
            throw new CInvalidColumnException(this, aiRow);
            
      }

      loColumnRows.set(liRow, columnValue);

      miRowStates[liRow] |= ROW_MODIFIED;

      mbUpToDate = false;
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
            loColumn = ((Vector) moColumnMap.get(columnName.toLowerCase())).get(virtualToAbsolute(aiRow));
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
         loColumn = moColumns[colunmIndex].get(virtualToAbsolute(aiRow));
      }
      catch (Exception ex)
      {
         throw new CInvalidColumnException(this, colunmIndex, aiRow);
      }

      return loColumn;
   }

   /**
    * Returns the assigned Java class for the specified column.
    * @param asName
    * @return column class as <code>CPropertyClass</code>
    * @see voodoosoft.jroots.core.CPropertyClass
    */
   public CPropertyClass getColumnClass(String asName)
   {
      CPropertyClass loClass;

      if (mbUpperCaseNaming)
      {
         loClass = moColumnClasses[((Integer) moColumnNumberMap.get(asName.toLowerCase())).intValue()];
      }
      else
      {
         loClass = moColumnClasses[((Integer) moColumnNumberMap.get(asName)).intValue()];
      }

      return loClass;
   }

   /**
    * Returns column number of this <code>CDataStore</code>.
    * @return number of column this datastore has
   */
   public int getColumnCount()
   {
      return miColumnCount;
   }

   public String getColumnName(int aiColumn)
   {
      return msColumnNames[aiColumn];
   }

   /**
    * Determines, if deleted rows are visible or invisible to the user of this datastore.
    * @param abHide true means deleted rows are invisible (default)
    */
   public void setHideDeletedRows(boolean abHide)
   {
      mbHideDeletedRows = abHide;
      miLastRow = 0;
      miLastAbsoluteRow = 0;
      mbRowsCounted = false;
      getRowCount();
   }

   /**
    * Gets MetaData of underlying ResultSet.
    * If this datastore has been serialized, there will be no valid metadata available.
    * @return ResultSetMetaData
    */
   public ResultSetMetaData getMetaData() throws SQLException
   {
      return moMetaData;
   }

   /**
    * Returns modification status of this datastore.
    * @return true if datastore has been modified since last retrieval or update
    */
   public boolean isModified()
   {
      return (!mbUpToDate);
   }

   /**
    * Method to control the generated <code>WHERE</code>-clauses of this <code>CDataStore</code>.
    * If optimistic locking is enabled, <code>CDataStore</code> constructs <code>WHERE</code>-clauses
    * of all updatable columns for <code>UPDATE</code>- and <code>DELETE</code>-statements,
    * otherwise (default setting) only primary key columns are used to find the appropriate
    * database rows to update.
    * @param abLocking
    */
   public void setOptimisticLocking(boolean abLocking)
   {
      mbOptimisticLocking = abLocking;
   }

   /**
    * Sets parameters of this datastore's PreparedStatement (if used at all)
    * @param aoParam array of parameters, in same order as defined in SQL
    */
   public void setParameters(Object[] aoParam)
   {
      moPreparedArgs = aoParam;
   }

   public void setQueryTimeout(int aiQueryTimeout)
   {
      miQueryTimeout = aiQueryTimeout;
   }

   /**
    * Returns number of (visible) rows.
    * If this datastore hides deleted rows, only not deleted rows are counted.
    * @return number of rows
    * @see #setHideDeletedRows
    */
   public int getRowCount()
   {
      if (!mbRowsCounted)
      {
         // RowStates not available ?
         // -> no need to filter rows
         if (miRowStates == null)
         {
            miRowCount = miGrossCount;
         }
         else
         {
            miRowCount = 0;

            for (int i = 1; i <= miGrossCount; i++)
            {
               if ((miRowStates[i] & ROW_DISCARDED) == 0)
               {
                  if (!mbHideDeletedRows)
                  {
                     miRowCount++;
                  }
                  else if ((miRowStates[i] & ROW_DELETED) == 0)
                  {
                     miRowCount++;
                  }
               }
            }

            mbRowsCounted = true;
         }
      }

      return miRowCount;
   }

   /**
    * Returns state information of specified row.
    * Valid row state are bitwise AND-combinations of the following:
    * <li> <code>ROW_UPDATED</code>: row has been updated, this state flag occurs exclusivly
    * <li> <code>ROW_DELETED</code>: row is marked as deleted, but has not yet been updated
    * <li> <code>ROW_MODIFIED</code>: row has been modified
    * <li> <code>ROW_NEW</code>: row was programmatically inserted but not yet updated
    * <li> <code>ROW_DISCARDED</code>: row was deleted and updated, this state flag occurs exclusivly
    * @param aiRow row number
    * @return row state
    */
   public int getRowState(int aiRow)
   {
      int absoluteRow;

      absoluteRow = virtualToAbsolute(aiRow);

      return miRowStates[absoluteRow];
   }

   /**
    * Sets this datastore's SQL to the given select statement.
    * Depending on the given parameter, a prepared statement will be created for retrieving
    * data.
    * Note that calling <code>setSQL</code> immediately clears all rows and columns of this
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
    * Note that calling <code>setSQL</code> immediately clears all rows and columns of this
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
    * Note that calling <code>setSQL</code> immediately clears all rows and columns of this
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

   /**
    * Determines if this datastore updates all or just modified columns.
    * Default setting is false.
    * @param abSmart if true, the datastore updates only modified columns
    */
   public void setSmartUpdate(boolean abSmart)
   {
      mbSmartUpdate = abSmart;
   }

   /**
    * <code>setUpdatableColumns</code> allows to explicitly specify which columns to update.
    * Calling {@link #update update} without prior setting the updatable columns will result
    * in updating all columns.
    * Care should be taken when updatable columns are set because in optimistic locking mode only
    * updatable columns are used to build the where clause.
    * @param asInclude array of column names to update
    */
   public void setUpdatableColumns(String[] asInclude)
   {
      moUpdatableColumns2 = new HashSet();

      for (int i = 0; i < asInclude.length; i++)
      {
         if (mbUpperCaseNaming)
         {
            moUpdatableColumns2.add(asInclude[i].toLowerCase());
         }
         else
         {
            moUpdatableColumns2.add(asInclude[i]);
         }
      }
   }

   /**
    * Sets database table used for updates.
    * An exception will be thrown if this datastore has no update table specified, but
    * <code>update</code> is called.
    * @param asTable database table to send updates to
    * @see #getUpdateTable
    */
   public void setUpdateTable(String asTable)
   {
      String[] lsTableID = new String[5];
      int i = 2;
      int liTableIdx;
      StringTokenizer loTok;

      msFullUpdateTable = asTable;

      for (i = 0; i < 5; i++)
      {
         lsTableID[i] = null;
      }

      i = 2;
      loTok = new StringTokenizer(asTable, ".");

      while (loTok.hasMoreTokens() && i < 5)
      {
         lsTableID[i++] = loTok.nextToken();
      }

      msUpdateTable = lsTableID[--i];
      msUpdateSchema = lsTableID[--i];
      msUpdateCatalog = lsTableID[--i];
   }

   /**
    * Gets database table used for updates.
    * @return update table
    * @see #setUpdateTable
    */
   public String getUpdateTable()
   {
      return msFullUpdateTable;
   }

   /**
    * Enables or disables uppercase identifier mode of this <code>CDataStore</code>.
    * @param abUpperCase if true all columns names are set to uppercase
    */
   public void setUpperCaseNaming(boolean abUpperCase)
   {
      mbUpperCaseNaming = abUpperCase;
   }

   public void cancel() throws CSQLException
   {
      try
      {
         mbCanceled = true;

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

   public boolean checkRowState(int aiRow, int aiStateMask)
   {
      int absoluteRow;

      absoluteRow = virtualToAbsolute(aiRow);

      return (miRowStates[absoluteRow] & aiStateMask) == aiStateMask;
   }

   /**
    * Marks the specified row as deleted.
    * No changes are made to the database until {@link #update update} is called.
    * The visibility of deleted rows can be set via {@link #setHideDeletedRows setHideDeletedRows}.
    * Value of <code>ROW_DELETED</code> will be added to row state.
    * @param aiRow number of row to delete
    * @return 0 success, -1 failure
   */
   public int deleteRow(int aiRow) throws CInvalidRowNumberException
   {
      int absoluteRow;

      absoluteRow = virtualToAbsolute(aiRow);
      miLastRow = 0;
      miLastAbsoluteRow = 0;

      if (aiRow <= 0 || aiRow > miRowStates.length)
      {
         throw new CInvalidRowNumberException(this, aiRow);
      }

      if ((miRowStates[absoluteRow] & ROW_DELETED) == 0)
      {
         miRowStates[absoluteRow] |= ROW_DELETED;

         mbUpToDate = false;
         mbRowsCounted = false;

         if (mbHideDeletedRows)
         {
            miRowCount--;
         }
      }

      return 0;
   }

   /**
    * Returns column number of specified column.
    * @param asName column name
    * @return column number
    * @throws SQLException
    */
   public int findColumn(String asName)
   {
      return ((Integer) moColumnNumberMap.get(asName)).intValue();
   }

   /**
    * Not yet implemented.
    * @param asExpression
    * @param aiFromRow row to start search from
    * @return number of found row, otherwise -1
    */
   public long findRow(String asExpression, int aiFromRow)
   {
      return -1;
   }

   /**
    * Inserts a new empty row into this datastore.
    * The row will be appear as new last row, row state is set to <code>ROW_NEW</code>.
    * @return number of inserted row
    */
   public int insertRow()
   {
      int[] liTemp;

      for (int i = 1; i <= moColumns.length - 1; i++)
      {
         moColumns[i].add(null);
         moOriginalColumns[i].add(null);
      }

      if (miGrossCount == miRowStates.length - 1)
      {
         liTemp = miRowStates;
         miRowStates = new int[miGrossCount + miCapacityIncrement];
         System.arraycopy(liTemp, 0, miRowStates, 0, liTemp.length);
      }

      miGrossCount++;
      miRowStates[miGrossCount] = ROW_NEW;
      miRowCount++;

      mbUpToDate = false;
      miLastRow = 0;
      miLastAbsoluteRow = 0;

      return miRowCount;
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
      miRowCount = 0;
      miLastRow = 0;
      miLastAbsoluteRow = 0;

      mbUpToDate = true;
      mbRowsCounted = false;
      mbPKeySet = false;

      moUpdateSQL = null;
      moInsertSQL = null;
      moDeleteSQL = null;

      miColumnCount = 0;
      msColumnNames = null;
      moColumns = null;
      moOriginalColumns = null;
      moColumnClasses = null;
      miColumnTypes = null;
      miRowStates = null;
      moColumnMap = null;
      moColumnNumberMap = null;
      moMetaData = null;

      try
      {
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
    * Retrieves data using the current SQL and connection.
    * Row states are set to <code>ROW_UPDATED</code>.
    * Automatically connects and closes the connection if needed.
    * @param aoCon <code>CConnection</code> to use
    * @return number of rows retrieved
   */
   public int retrieve(CConnection aoCon) throws CSQLException
   {
      int liRowCount;
      ResultSet loResultSet = null;
      boolean lbDisconnect = false;
      Connection loDriverCon;

      try
      {
         reset();
         mbCanceled = false;

         // connect
         if (aoCon.isClosed())
         {
            aoCon.connect();
            lbDisconnect = true;
         }

         loDriverCon = aoCon.getConnection();

         if (mbPreparedSQL)
         {
            if (moPreparedSQL == null || miRetrieveHash != loDriverCon.hashCode())
            {
               moPreparedSQL = loDriverCon.prepareStatement(msSQLSelect);
            }

            setPreparedArgs();
            moPreparedSQL.setQueryTimeout(miQueryTimeout);
            loResultSet = moPreparedSQL.executeQuery();
         }
         else
         {
            moStatement = aoCon.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                ResultSet.CONCUR_READ_ONLY);
            moStatement.setQueryTimeout(miQueryTimeout);
            loResultSet = moStatement.executeQuery(msSQLSelect);
         }

         miRetrieveHash = loDriverCon.hashCode();

         // grab data
         populate(loResultSet);

         if (mbCanceled)
         {
            reset();
         }

         // init row states
         liRowCount = getRowCount();
      }
      catch (SQLException ex)
      {
         reset();
         throw new CSQLException(ex, msSQLSelect);
      }
      catch (Exception ex)
      {
         reset();
         throw new CSQLException(ex, msSQLSelect);
      }
      finally
      {
         try
         {
            if (loResultSet != null)
            {
               loResultSet.close();
            }

            if (moStatement != null)
            {
               moStatement.close();
               moStatement = null;
            }

            if (lbDisconnect)
            {
               aoCon.close();
            }
         }
         catch (SQLException ex)
         {
            throw new CSQLException(ex);
         }
      }

      return liRowCount;
   }

   /**
    * Updates database with changes since retrieval or last successful update.
    * <p>
    * The destination table is determined through the internal "update table".
    * For each row the row state is checked to determine what kind of SQL statement is needed
    * for updating.
    * If smart update was enabled for this <code>CDataStore</code>, only modified columns are
    * considered for <code>UPDATE</code> commands.
    * If this <code>CDataStore</code> uses optimistic locking, the <code>WHERE</code>-clauses
    * includes all updatable columns, otherwise only primary key colummns.
    * When all rows were successfully updated, the row states will be changed:
    * <li> <code>ROW_DELETED</code> becomes <code>ROW_DISCARDED</code>
    * <li> all states unequal to <code>ROW_DISCARDED</code> become <code>ROW_UPDATED</code>
    * @see #setUpdateTable
    * @see #setUpdatableColumns
    * @see #setOptimisticLocking
    * @see #setSmartUpdate
    */
   public int update(CConnection aoCon) throws CUpdateFailedException
   {
      int liKeyStart;
      int liCol;
      int liAffected = 0;
      int[] liBatchAffected;
      int liDeleteBatchCount = 0;
      int liUpdateBatchCount = 0;
      int liInsertBatchCount = 0;
      String lsWhereSQL;
      String lsUpdateSQL = "";
      String lsColName;
      String lsSQL;
      Object loOriginal;
      Object loNew;
      boolean[] lbUpdateColumns = null;
      Connection loDriverCon;

      try
      {
         if (msFullUpdateTable == null)
         {
            throw new CUpdateFailedException("update table not set");
         }

         // check if prepared statements are still valid (same connection as last time)
         loDriverCon = aoCon.getConnection();

         if (miUpdateHash != loDriverCon.hashCode())
         {
            miUpdateHash = loDriverCon.hashCode();
            moUpdateSQL = null;
            moInsertSQL = null;
            moDeleteSQL = null;
         }

         // get primary key columns
         if (!mbPKeySet)
         {
            getPrimaryKeys(loDriverCon);
            mbPKeySet = true;
         }

         // set updatable columns
         initUpdatableColumns();

         // build UPDATE-trunc
         if (!mbSmartUpdate)
         {
            msUpdateTrunc = buildUpdateTrunc();
         }

         // build WHERE - prepare static UPDATE and DELETE
         if (!mbOptimisticLocking)
         {
            msWhereSQL = buildStaticWhereClause();

            if (!mbSmartUpdate && moUpdateSQL == null)
            {
               moUpdateSQL = loDriverCon.prepareStatement(msUpdateTrunc + msWhereSQL);
            }
         }

         if (mbSmartUpdate)
         {
            lbUpdateColumns = new boolean[miColumnCount + 1];
         }

         // process all rows
         for (int i = 1; i <= miGrossCount; i++)
         {
            // rows to insert
            if (((miRowStates[i] & ROW_NEW) != 0) && ((miRowStates[i] & ROW_DELETED) == 0))
            {
               if (moInsertSQL == null)
               {
                  prepareINSERTStatement(loDriverCon);
               }

               for (int j = 1, k = 1; j <= miColumnCount; j++)
               {
                  if (mbUpdatableColumns[j])
                  {
                     loNew = moColumns[j].get(i);

                     if (loNew == null)
                     {
                        moInsertSQL.setNull(k++, miColumnTypes[j]);
                     }
                     else
                     {
                        moInsertSQL.setObject(k++, loNew);
                     }
                  }
               }

               moInsertSQL.addBatch();
               liInsertBatchCount++;
            }

            // rows to delete
            else if ((miRowStates[i] & ROW_DELETED) != 0 && (miRowStates[i] & ROW_NEW) == 0)
            {
               if (mbOptimisticLocking)
               {
                  lsWhereSQL = buildDynamicWhereClause(i);
                  lsSQL = "DELETE FROM " + getUpdateTable() + lsWhereSQL;
                  moDeleteSQL = loDriverCon.prepareStatement(lsSQL);
               }
               else if (moDeleteSQL == null)
               {
                  lsSQL = "DELETE FROM " + getUpdateTable() + msWhereSQL;
                  moDeleteSQL = loDriverCon.prepareStatement(lsSQL);
               }

               for (int j = 1, k = 0; j <= miColumnCount; j++)
               {
                  if (mbWHEREColumns[j])
                  {
                     loOriginal = moOriginalColumns[j].get(i);

                     if (loOriginal == null)
                     {
                        moDeleteSQL.setNull(++k, miColumnTypes[j]);
                     }
                     else
                     {
                        moDeleteSQL.setObject(++k, loOriginal);
                     }
                  }
               }

               if (mbOptimisticLocking)
               {
                  liAffected = moDeleteSQL.executeUpdate();
                  moDeleteSQL.close();

                  if (liAffected != 1)
                  {
                     throw new CUpdateFailedException("record not found for delete", false);
                  }
               }
               else
               {
                  liDeleteBatchCount++;
                  moDeleteSQL.addBatch();
               }
            }

            // rows to update
            else if ((miRowStates[i] & ROW_MODIFIED) != 0 && (miRowStates[i] & ROW_DELETED) == 0)
            {
               if (mbOptimisticLocking)
               {
                  lsWhereSQL = buildDynamicWhereClause(i);
               }
               else
               {
                  lsWhereSQL = msWhereSQL;
               }

               if (mbSmartUpdate)
               {
                  liKeyStart = 0;
                  lsUpdateSQL = "";

                  for (liCol = 1; liCol <= miColumnCount; liCol++)
                  {
                     lbUpdateColumns[liCol] = false;

                     if (!mbUpdatableColumns[liCol])
                     {
                        continue;
                     }

                     loOriginal = moOriginalColumns[liCol].get(i);
                     loNew = moColumns[liCol].get(i);

                     if (loOriginal == null && loNew == null)
                     {
                        continue;
                     }

                     if (loOriginal != null && loNew != null)
                     {
                        if (loOriginal.equals(loNew))
                        {
                           continue;
                        }
                     }

                     liKeyStart++;
                     lbUpdateColumns[liCol] = true;
                     lsColName = msColumnNames[liCol];
                     lsUpdateSQL = lsUpdateSQL + ", " + lsColName + " = ?";
                  }

                  // any columns changed ?
                  if (lsUpdateSQL.equals(""))
                  {
                     continue;
                  }

                  lsUpdateSQL = "UPDATE " + getUpdateTable() + " SET " + lsUpdateSQL.substring(2) +
                                lsWhereSQL;
                  moUpdateSQL = loDriverCon.prepareStatement(lsUpdateSQL);
                  
                  for (int j = 1, k = 1, l = 0; j <= miColumnCount; j++)
                  {
                     if (lbUpdateColumns[j])
                     {
                        loNew = moColumns[j].get(i);

                        if (loNew == null)
                        {
                           moUpdateSQL.setNull(k++, miColumnTypes[j]);
                        }
                        else
                        {
                           moUpdateSQL.setObject(k++, loNew);
                        }
                     }

                     if (mbWHEREColumns[j])
                     {
                        loOriginal = moOriginalColumns[j].get(i);

                        if (loOriginal == null)
                        {
                           moUpdateSQL.setNull(liKeyStart + (++l), miColumnTypes[j]);
                        }
                        else
                        {
                           moUpdateSQL.setObject(liKeyStart + (++l), loOriginal);
                        }
                     }
                  }
 
                  liAffected = moUpdateSQL.executeUpdate();
                  moUpdateSQL.close();

                  if (liAffected != 1)
                  {
                     throw new CUpdateFailedException("record to update could not be located (affected rows: " +
                                                      String.valueOf(liAffected) + ")", false);
                  }
               }
               else
               {
               	

                  if (mbOptimisticLocking)
                  {
                     moUpdateSQL = loDriverCon.prepareStatement(msUpdateTrunc + lsWhereSQL);
                  }

                  liKeyStart = miUpdatableColumnCount;
                  liCol = 1;

                  for (int j = 1, k = 1; liCol <= miColumnCount; liCol++)
                  {
                     if (mbUpdatableColumns[liCol])
                     {
                        loNew = moColumns[liCol].get(i);

                        if (loNew == null)
                        {
                           moUpdateSQL.setNull(j++, miColumnTypes[liCol]);
                        }
                        else
                        {
                           moUpdateSQL.setObject(j++, loNew);
                        }

                        if (mbWHEREColumns[liCol])
                        {
                           loOriginal = moOriginalColumns[liCol].get(i);

                           if (loOriginal == null)
                           {
                              moUpdateSQL.setNull(liKeyStart + (k++), miColumnTypes[liCol]);
                           }
                           else
                           {
                              moUpdateSQL.setObject(liKeyStart + (k++), loOriginal);
                           }
                        }
                     }
                  }

                  if (mbOptimisticLocking)
                  {
                     liAffected = moUpdateSQL.executeUpdate();
                     moUpdateSQL.close();

                     if (liAffected != 1)
                     {
                        throw new CUpdateFailedException("record to update could not be located (affected rows: " +
                                                         String.valueOf(liAffected) + ")", false);
                     }
                  }
                  else
                  {
                     liUpdateBatchCount++;
                     moUpdateSQL.addBatch();
                  }
               }
            }
         }

         if (moInsertSQL != null && liInsertBatchCount > 0)
         {
            moInsertSQL.executeBatch();
         }

         if (!mbOptimisticLocking)
         {
            if (moDeleteSQL != null && liDeleteBatchCount > 0)
            {
               liBatchAffected = moDeleteSQL.executeBatch();

               for (int i = 0; i < liBatchAffected.length; i++)
               {
                  if (liBatchAffected[i] <= 0)
                  {
                     throw new CUpdateFailedException("record could not be deleted. Batch status: " +
                                                      String.valueOf(liBatchAffected[i]), false);
                  }

                  ;
               }
            }

            if (!mbSmartUpdate && moUpdateSQL != null && liUpdateBatchCount > 0)
            {
               liBatchAffected = moUpdateSQL.executeBatch();

               for (int i = 0; i < liBatchAffected.length; i++)
               {
                  if (liBatchAffected[i] <= 0)
                  {
                     throw new CUpdateFailedException("record could not be updated. Batch status: " +
                                                      String.valueOf(liBatchAffected[i]), false);
                  }
               }
            }
         }

         // update row states
         for (int i = 1; i <= miGrossCount; i++)
         {
            if ((miRowStates[i] & ROW_DELETED) != 0)
            {
               miRowStates[i] = ROW_DISCARDED;
            }
            else if ((miRowStates[i] & ROW_DISCARDED) == 0)
            {
               miRowStates[i] = ROW_UPDATED;
            }
         }

         // set original column values to current column values
         for (int i = 1; i <= miGrossCount; i++)
         {
            for (int j = 1; j <= moOriginalColumns.length - 1; j++)
            {
               moOriginalColumns[j].setElementAt(moColumns[j].elementAt(i), i);
            }
         }

         mbUpToDate = true;
         mbRowsCounted = false;
         miRowCount = getRowCount();
      }
      catch (Exception ex)
      {
         if (ex instanceof CUpdateFailedException)
         {
            throw ((CUpdateFailedException) ex);
         }
         else
         {
            throw new CUpdateFailedException(ex, ex.getMessage());
         }
      }

      miLastRow = 0;
      miLastAbsoluteRow = 0;

      return miRowCount;
   }

   /**
    * Indicates whether this data store uses an prepared SQL object.
    * @return true if this datastore uses prepared statements for data retrieval
    */
   public boolean usesPreparedSQL()
   {
      return mbPreparedSQL;
   }

   private void setDefaultExcludeTypes()
   {
      moExcludeWHERETypes = new HashSet();

      moExcludeWHERETypes.add(new Integer(Types.BINARY));
      moExcludeWHERETypes.add(new Integer(Types.BLOB));
      moExcludeWHERETypes.add(new Integer(Types.CLOB));
      moExcludeWHERETypes.add(new Integer(Types.LONGVARBINARY));
      moExcludeWHERETypes.add(new Integer(Types.LONGVARCHAR));
      moExcludeWHERETypes.add(new Integer(Types.VARBINARY));
   }

   private void setPreparedArgs() throws SQLException
   {
      for (int i = 0; i < moPreparedArgs.length; i++)
      {
         moPreparedSQL.setObject(i + 1, moPreparedArgs[i]);
      }
   }

   private void getPrimaryKeys(Connection aoCon) throws Exception
   {
      DatabaseMetaData loDBMetaData;
      String lsWhereSQL;
      ResultSet loResult;
      short lsSEQ;
      String lsName;
      boolean lbUpperCase;
      int i = 0;
      Integer liColNr;

      loDBMetaData = aoCon.getMetaData();

      lbUpperCase = loDBMetaData.storesUpperCaseIdentifiers();

      if (lbUpperCase)
      {
         if (msUpdateSchema != null)
         {
            msUpdateSchema = msUpdateSchema.toLowerCase();
         }

         msUpdateTable = msUpdateTable.toLowerCase();
      }

      loResult = loDBMetaData.getPrimaryKeys(msUpdateCatalog, msUpdateSchema, msUpdateTable.toLowerCase());

      mbKeyColumn = new boolean[miColumnCount + 1];

      while (loResult.next())
      {
         lsName = loResult.getString("COLUMN_NAME");
         liColNr = (Integer) moColumnNumberMap.get(lsName);
         mbKeyColumn[liColNr.intValue()] = true;
         i++;
      }

      if (i == 0)
      {
         throw new CUpdateFailedException("update table has no primary key");
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

   private String buildDynamicWhereClause(int aiRow) throws SQLException
   {
      String lsWhereSQL = "";
      String lsColName;
      Object loValue;

      for (int i = 1; i <= miColumnCount; i++)
      {
         mbWHEREColumns[i] = false;
      }

      for (int i = 1; i <= miColumnCount; i++)
      {
         if (!moExcludeWHERETypes.contains(new Integer(miColumnTypes[i])))
         {
            if (mbUpdatableColumns[i])
            {
               lsColName = msColumnNames[i];
               loValue = moOriginalColumns[i].get(aiRow);

               if (loValue == null)
               {
                  lsWhereSQL = lsWhereSQL + " AND " + lsColName + " IS NULL";
               }
               else
               {
                  mbWHEREColumns[i] = true;
                  lsWhereSQL = lsWhereSQL + " AND " + lsColName + " = ?";
               }
            }
         }
      }

      lsWhereSQL = " WHERE " + lsWhereSQL.substring(5);

      return lsWhereSQL;
   }

   private String buildStaticWhereClause() throws SQLException
   {
      String lsWhereSQL = "";
      String lsColName;

      for (int i = 1; i <= miColumnCount; i++)
      {
         mbWHEREColumns[i] = false;

         if (mbKeyColumn[i])
         {
            mbWHEREColumns[i] = true;
            lsColName = msColumnNames[i];
            lsWhereSQL = lsWhereSQL + " AND " + lsColName + " = ?";
         }
      }

      lsWhereSQL = " WHERE " + lsWhereSQL.substring(5);

      return lsWhereSQL;
   }

   private String buildUpdateTrunc() throws SQLException
   {
      String lsUpdateSQL = "";
      String lsTable;

      lsTable = getUpdateTable();

      // UPDATE statement
      for (int i = 1; i <= miColumnCount; i++)
      {
         if (mbUpdatableColumns[i])
         {
            lsUpdateSQL = lsUpdateSQL + ", " + msColumnNames[i] + " = ?";
         }
      }

      lsUpdateSQL = "UPDATE " + lsTable + " SET " + lsUpdateSQL.substring(2) + " ";

      return lsUpdateSQL;
   }

   private void initUpdatableColumns() throws SQLException
   {
      int liCounter = 0;

      // Columns, die aufgrund ihres Typs nicht gespeichert werden, setzen
      if (moExcludeWHERETypes == null)
      {
         setDefaultExcludeTypes();
      }

      mbUpdatableColumns = new boolean[miColumnCount + 1];
      miUpdatableColumnCount = 0;

      // updatable columns not defined ? -> take all
      if (moUpdatableColumns2 == null)
      {
         for (int i = 1; i <= miColumnCount; i++)
         {
            mbUpdatableColumns[i] = true;
            miUpdatableColumnCount++;
         }
      }
      else
      {
         for (int i = 1; i <= miColumnCount; i++)
         {
            if (moUpdatableColumns2.contains(msColumnNames[i]))
            {
               mbUpdatableColumns[i] = true;
               miUpdatableColumnCount++;
               liCounter++;
            }
            else
            {
               mbUpdatableColumns[i] = false;
            }
         }

         if (liCounter != moUpdatableColumns2.size())
         {
            throw new IllegalArgumentException("invalid updatable column");
         }
      }
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

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("memColumnClasses");
      }

      moColumnClasses = new CPropertyClass[miColumnCount + 1];
      miColumnTypes = new int[miColumnCount + 1];
      moColumnJavaClasses = new Class[miColumnCount + 1];

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

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("column [" + String.valueOf(liColumn) + "] Java class [" +
                           loJavaClass.getName() + "]" + " precision [" +
                           String.valueOf(liPrecision) + "," + String.valueOf(liScale) +
                           "] SQL type [" + String.valueOf(liSQLType) + "]");
         }
      }
   }

   private void populate(ResultSet aoRS) throws SQLException
   {
      ResultSetMetaData loMetaData;
      Object loValue;

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("populate");
      }

      loMetaData = aoRS.getMetaData();
      moMetaData = loMetaData;
      miColumnCount = loMetaData.getColumnCount();

      // build column vectors
      moColumns = new Vector[miColumnCount + 1];
      moOriginalColumns = new Vector[miColumnCount + 1];
      msColumnNames = new String[miColumnCount + 1];
      mbWHEREColumns = new boolean[miColumnCount + 1];

      // get columns types, precision, etc.
      memColumnClasses(loMetaData);

      // create row storage
      for (int i = 1; i <= miColumnCount; i++)
      {
         moColumns[i] = new Vector(miCapacityIncrement, miCapacityIncrement);
         moColumns[i].add(null);

         moOriginalColumns[i] = new Vector(miCapacityIncrement, miCapacityIncrement);
         moOriginalColumns[i].add(null);
      }

      // get data
      miGrossCount = 0;

      while (!mbCanceled && aoRS.next())
      {
         miGrossCount++;

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

            moColumns[i].add(loValue);
            moOriginalColumns[i].add(loValue);
         }
      }

      if (mbCanceled)
      {
         return;
      }

      // examine columns
      moColumnMap = new HashMap();
      moColumnNumberMap = new HashMap();

      for (int i = 1; i <= miColumnCount; i++)
      {
         if (mbUpperCaseNaming)
         {
            msColumnNames[i] = loMetaData.getColumnName(i).toLowerCase();
         }
         else
         {
            msColumnNames[i] = loMetaData.getColumnName(i);
         }

         moColumnMap.put(msColumnNames[i], moColumns[i]);
         moColumnNumberMap.put(msColumnNames[i], new Integer(i));
      }

      // prepare row states
      miRowStates = new int[miGrossCount + 1];
   }

   private void prepareINSERTStatement(Connection aoCon)
                                throws SQLException
   {
      String lsInsertSQL = "";
      String lsValuesSQL = "";
      String lsTable;

      lsTable = getUpdateTable();

      // INSERT statement
      for (int i = 1; i <= miColumnCount; i++)
      {
         if (mbUpdatableColumns[i])
         {
            lsInsertSQL = lsInsertSQL + ", " + msColumnNames[i];
            lsValuesSQL = lsValuesSQL + ", ?";
         }
      }

      lsInsertSQL = "INSERT INTO " + lsTable + "(" + lsInsertSQL.substring(2) + ") VALUES (" +
                    lsValuesSQL.substring(2) + ")";

      moInsertSQL = aoCon.prepareStatement(lsInsertSQL);


   }

   /**
    * @param aiRow virtual row number
    * @return corresponding Vector row
    */
   private int virtualToAbsolute(int aiVirtualRow)
   {
      int liRow = 0;
      int liVisibleRow = 0;
      boolean lbFound = false;

      if (aiVirtualRow <= 0)
      {
         return -1;
      }

      if (miLastRow == aiVirtualRow)
      {
         return miLastAbsoluteRow;
      }

      while (!lbFound && liRow <= miGrossCount)
      {
         liRow++;

         if ((miRowStates[liRow] & ROW_DISCARDED) == 0)
         {
            if (mbHideDeletedRows)
            {
               if ((miRowStates[liRow] & ROW_DELETED) == 0)
               {
                  liVisibleRow++;

                  if (liVisibleRow >= aiVirtualRow)
                  {
                     lbFound = true;
                  }
               }
            }
            else
            {
               liVisibleRow++;

               if (liVisibleRow >= aiVirtualRow)
               {
                  lbFound = true;
               }
            }
         }
      }

      if (lbFound)
      {
         miLastRow = aiVirtualRow; // cache row number
         miLastAbsoluteRow = liRow;

         return liRow;
      }
      else
      {
         return -1;
      }
   }

   /** row state for updated (or retrieved and not modified) rows */
   public static final int ROW_UPDATED = 0;

   /** row state for deleted but not updated rows */
   public static final int ROW_DELETED = 1;

   /** row state for modified rows */
   public static final int ROW_MODIFIED = 2;

   /** row state for inserted, but not modified rows */
   public static final int ROW_NEW = 4;

   /** row state for deleted and updated rows */
   private static final int ROW_DISCARDED = 8;
   protected static Logger moLogger = Logger.getLogger(CDataStore.class);

   /** SQL as string to populate the datastore */
   private String msSQLSelect;
   private boolean mbPreparedSQL;

   /** SQL as prepared statement to retrieve data */
   private transient PreparedStatement moPreparedSQL;
   private transient Statement moStatement;

   /** Arguments for the prepared statement */
   private Object[] moPreparedArgs;

   /** flag to determine if 'miRowCount' is uptodate */
   private boolean mbRowsCounted;

   /** (virtual) number of rows */
   private int miRowCount;

   /** row count including deleted and discarded rows */
   private int miGrossCount;

   /** Last accessed row (virtual, internal usage) */
   private int miLastRow;

   /** Last accessed row (virtual, internal usage) */
   private int miLastAbsoluteRow;

   /** show or hide deleted rows ? */
   private boolean mbHideDeletedRows;
   private boolean mbUpperCaseNaming;

   /** meta data of this datastore */
   private transient ResultSetMetaData moMetaData;
   private int miCapacityIncrement = 10;

   /** row data */
   private Vector[] moColumns;
   private Vector[] moOriginalColumns;

   /** states of rows */
   private int[] miRowStates;

   /** column names */
   private String[] msColumnNames;

   /** columns used in WHERE-clause */
   private boolean[] mbWHEREColumns;
   private HashSet moExcludeWHERETypes;
   private CPropertyClass[] moColumnClasses;
   private int[] miColumnTypes;
   private Class[] moColumnJavaClasses;
   private HashMap moColumnTypeConversions;

   /** updatable columns */
   private Set moUpdatableColumns2;
   private boolean[] mbUpdatableColumns;
   private int miUpdatableColumnCount;

   /** column count */
   private int miColumnCount;

   /** indicates primary key columns */
   private boolean[] mbKeyColumn;

   /** maps columns names to moColumns[] */
   private HashMap moColumnMap;

   /** maps columns names to column numbers */
   private HashMap moColumnNumberMap;

   /** are there pending updates ? */
   private boolean mbUpToDate;

   /** table to update */
   private String msFullUpdateTable;
   private String msUpdateTable;
   private String msUpdateSchema;
   private String msUpdateCatalog;

   /** prepared statements for updating */
   private transient PreparedStatement moUpdateSQL;

   /** prepared statements for updating */
   private transient PreparedStatement moInsertSQL;

   /** prepared statements for updating */
   private transient PreparedStatement moDeleteSQL;

   /** SQL WHERE-clause and UPDATE-trunc for updating */
   private String msWhereSQL;

   /** SQL WHERE-clause and UPDATE-trunc for updating */
   private String msUpdateTrunc;
   private boolean mbPKeySet;
   private boolean mbCanceled;
   private boolean mbSmartUpdate;
   private boolean mbOptimisticLocking;
   private int miQueryTimeout;
   private int miRetrieveHash;
   private int miUpdateHash;
}
