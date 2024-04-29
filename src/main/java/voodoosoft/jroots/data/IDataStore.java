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

import voodoosoft.jroots.core.*;

import java.sql.PreparedStatement;


/**
 * Data storage abstracting the underlying RowSet or ResultSet objects.
 * The data store offers direct row access, no handling of cursor positions is necessary.
 */
public interface IDataStore extends IBasicDataStore
{
   /**
    * Sets a new value to a column. No changes are made in the database until update() is called.
    * @param aiRow row number
    * @param columnName column name
    * @param columnValue new value
    */
   public void setColumn(int aiRow, String columnName, Object columnValue)
                  throws CInvalidColumnException;

   /**
    * Returns the assigned Java class for the specified column.
    * @param asName
    */
   public CPropertyClass getColumnClass(String asName);

   public String getColumnName(int aiColumn);

   /**
    * Deleted rows can be visible or invisible to the user of datastores.
    * @param abHide true means deleted rows are invisible (default)
    */
   public void setHideDeletedRows(boolean abHide);

   public boolean isModified();

   /**
    * Sets parameters of this datastore's PreparedStatement (if used at all)
    * @param aoParam array of parameters, in same order as defined in SQL
    */
   public void setParameters(Object[] aoParam);

   public int getRowState(int aiRow);

   /**
    * Sets this data stores SQL.
    */
   public void setSQL(String asSQL);

   public void setSQL(String asSQL, boolean abPreparedSQL);

   /**
    * Sets this data stores SQL.
    */
   public void setSQL(PreparedStatement aoSQL);

   public void setUpdatableColumns(String[] asInclude);

   /**
    * Sets database table used for updates.
    */
   public void setUpdateTable(String asTable);

   /**
    * Gets database table used for updates.
    */
   public String getUpdateTable();

   /**
    * Deletes the specified row from the datastore. No changes are made in the database until {@link #update} is called.
    * The visibility of deleted rows can be set via {@link #setHideDeletedRows}
    * @param aiRow number of row to delete
    * @return 0 success, -1 failure
   */
   public int deleteRow(int aiRow) throws CInvalidRowNumberException;

   public int findColumn(String asName);

   /**
    * Finds row matching the specified expression.
    * @param asExpression
    * @param aiFromRow row to start search from
    * @return number of found row, otherwise -1
    */
   public long findRow(String asExpression, int aiFromRow);

   /**
    * Inserts a new empty row into this datastore. Row state becomes ROW_NEW.
    */
   public int insertRow();

   /**
    * Clears data and resets internal status.
    */
   public void reset();

   /**
    * Updates underlying database.
    * The destination table will be determined through the "update table".
    * @see #setUpdateTable
    */
   public int update(CConnection aoCon) throws CUpdateFailedException;

   /**
    * Indicates wether this data store uses an prepared SQL object.
    */
   public boolean usesPreparedSQL();
}
