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


package voodoosoft.jroots.core.gui;

import voodoosoft.jroots.data.*;

import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.*;


/**
 * Helper class to store individual user settings of <code>JTable</code> objects.
 * Required database table structure:
 * <p><code>
 * <br>[UserSetting] [int]          NOT NULL (Primary key)
 * <br>[UserID]      [varchar] (50) NOT NULL
 * <br>[TableName]   [varchar] (50) NOT NULL
 * <br>[ColumnName]  [varchar] (50) NOT NULL
 * <br>[ColumnWidth] [int]          NOT NULL
 * </code>
 * <p>
 * The name of the table is free to choose, but not the column names.
 * A sequence supplier of type <code>ISequence</code> (sequence name is "UserSetting") is required.
 */
public class CTableColumnSerializer
{
   /**
    * Creates new <code>CTablePropertySerializer</code>.
    * @param seq Sequence supplier to get primary keys for the database table, asks for sequence of name "UserSetting"
    * @param dsFactory
    * @param DBtable
    */
   public CTableColumnSerializer(ISequence seq, IDataStoreFactory dsFactory, String DBtable)
   {
      moDSFactory = dsFactory;
      moSeq = seq;
      msDBTable = DBtable;
   }

   /**
    * Restores column widths for the specified table and user.
    * @param asUserID
    * @param aoTable
    * @param asTableID
    * @param aoConn
    * @throws Exception
    */
   public void load(String asUserID, JTable aoTable, String asTableID, CConnection aoConn)
             throws Exception
   {
      TableColumn loColumn;
      String lsColName;
      CDataStore loDS;
      Integer loValue;
      int liRow;
      boolean lbUpdate = false;

      loDS = (CDataStore) moDSFactory.createObject("SELECT * FROM " + msDBTable +
                                                   " WHERE UserID = '" + asUserID +
                                                   "' AND TableName = '" + asTableID + "'", false);
      loDS.setHideDeletedRows(false);
      loDS.retrieve(aoConn);

      for (int i = 1; i <= loDS.getRowCount(); i++)
      {
         lsColName = (String) loDS.getColumn(i, "ColumnName");
         loValue = (Integer) loDS.getColumn(i, "ColumnWidth");
         if (lsColName != null && loValue != null)
         {            
            try
            {
               loColumn = aoTable.getColumn(lsColName);
               loColumn.setPreferredWidth(loValue.intValue());
            }
            catch (Exception ex)
            {
               // invalid column found -> delete it
               loDS.deleteRow(i);
               lbUpdate = true;
            }
         }
      } 

      //      if (lbUpdate)
      //      {
      //         try
      //         {
      //            loDS.setUpdateTable(msDBTable);
      //            loDS.update(aoConn);
      //            aoConn.commit();
      //         }
      //         catch (Exception ex)
      //         {
      //            CException.record(ex, this, false);
      //         }
      //      }
   }

   /**
    * Saves column setting for the given user and table.
    * Connection will not be committed automatically.
    * @param asUserID
    * @param aoTable
    * @param asTableID
    * @param aoConn
    * @throws Exception
    */
   public void save(String asUserID, JTable aoTable, String asTableID, CConnection aoConn)
             throws Exception
   {
      JTableHeader loHeader;
      TableCellRenderer loRenderer;
      TableColumn loColumn;
      String lsColName;
      CDataStore loDS;
      HashMap loColMap;
      Integer loRow;
      Object liID = null, loValue;
      int liRow;
 
      // read existing column settings
      loDS = (CDataStore) moDSFactory.createObject("SELECT * FROM " + msDBTable +
                                                   " WHERE UserID = '" + asUserID +
                                                   "' AND TableName = '" + asTableID + "'", false);
      loDS.setUpdateTable(msDBTable);
      loDS.retrieve(aoConn);

      loColMap = new HashMap();

      // collect saved columns
      for (int i = 1; i <= loDS.getRowCount(); i++)
      {
         loValue = loDS.getColumn(i, "ColumnName");
         if (loValue != null) {        
            loColMap.put(loValue.toString(), new Integer(i));
         }
      }

      // loop through all table columns
      loHeader = aoTable.getTableHeader();
      loRenderer = loHeader.getDefaultRenderer();

      for (int i = 0; i < aoTable.getColumnCount(); i++)
      {
         lsColName = aoTable.getColumnName(i);
         loColumn = aoTable.getColumn(lsColName);

         // column saved before ?
         loRow = (Integer) loColMap.get(lsColName);

         if (loRow == null)
         {
            liID = moSeq.nextObject("UserSetting");
            liRow = loDS.insertRow();
            loDS.setColumn(liRow, "UserSetting", liID);
            loDS.setColumn(liRow, "UserID", asUserID);
            loDS.setColumn(liRow, "TableName", asTableID);
            loDS.setColumn(liRow, "ColumnName", lsColName);
         }
         else
         {
            liRow = loRow.intValue();

         }

         // save column width
         loValue = new Integer(loColumn.getWidth());
         loDS.setColumn(liRow, "ColumnWidth", loValue);

         loDS.update(aoConn);

      }
   }
   
   /**
    * Deletes all settings of specified user and <code>JTable</code>.
    * Connection will not be committed automatically.
    * @param asUserID
    * @param asTableID
    * @param aoConn
    * @throws Exception
    */
   public void resetSettings(String asUserID, String asTableID, CConnection aoConn) throws Exception
   {
      CDataStore loDS;
      
      // read existing settings
      loDS = (CDataStore) moDSFactory.createObject("SELECT * FROM " + msDBTable +
                                                   " WHERE UserID = '" + asUserID +
                                                   "' AND TableName = '" + asTableID + "'", false);      
      loDS.setUpdateTable(msDBTable);
      loDS.retrieve(aoConn);
      loDS.setHideDeletedRows(false);
      for (int i = 1; i <= loDS.getRowCount(); i++)
      {
         loDS.deleteRow(i);
      }
      
      loDS.update(aoConn);
   }
   
   private String msDBTable;
   private ISequence moSeq;
   private IDataStoreFactory moDSFactory;
}
