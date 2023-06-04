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


package voodoosoft.jroots.dialog;

import voodoosoft.jroots.data.IDataStore;
import voodoosoft.jroots.data.IDataStoreListener;
import voodoosoft.jroots.exception.CException;

import java.util.HashSet;
import java.util.Vector;

import javax.sql.RowSetEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.table.*;


/**
 * Table model for tables using {@link voodoosoft.jroots.data.IDataStore IDataStore} objects as data source.
 * <li>columns can be excluded from viewing.
 * <li>data can be sorted by any column.
 */
public class CDataStoreTableModel extends AbstractTableModel implements IDataStoreListener
{
   /** Helper class for sorting.
    * @see #sort
    */
   private static class CRow implements Comparable
   {
      public CRow(int aiRow, Object aoData)
      {
         miRow = aiRow;
         moData = (Comparable) aoData;
      }

      public int compareTo(Object o)
      {
         if (mbAscending)
         {
            return moData.compareTo(((CRow) o).moData);
         }
         else
         {
            return ((CRow) o).moData.compareTo(moData);
         }
      }

      public int miRow;
      public Comparable moData;
   }

   /**
    * Creates new model for the specified data store.
    */
   public CDataStoreTableModel(IDataStore aoDataStore)
   {
      int liCount;

      moDataStore = aoDataStore;

      //      moDataStore.addListener(this);
      moExcludeColumns = new HashSet();
      moSortData = new Vector();

      buildColumnMap();
      buildRowMap();
   }

   /**
    * Maps the appropiate Java class to the given SQL column type.
    * @param column (virtual) number
    */
   public Class getColumnClass(int column)
   {
      return moDataStore.getColumnClass(moDataStore.getColumnName(mapColumn(column))).javaClass;
   }

   public int getColumnCount()
   {
      return miColumnCount;
   }

   /**
    * Gets column name by column number.
    * @param aiColumn (virtual) number
    */
   public String getColumnName(int aiColumn)
   {
      String lsName = null;

      lsName = moDataStore.getColumnName(mapColumn(aiColumn));

      return lsName;
   }

   public IDataStore getDataStore()
   {
      return moDataStore;
   }

   public int getRowCount()
   {
      int liCount;

      liCount = moDataStore.getRowCount();

      return liCount;
   }

   /**
    * Returns column value of specified (virtual) column and row.
    */
   public Object getValueAt(int aiRow, int aiColumn)
   {
      Object loValue = null;

      try
      {
         loValue = moDataStore.getColumn(mapRow(aiRow), mapColumn(aiColumn));
      }
      catch (Exception ex)
      {
         CException.record(ex, this, false);
      }

      return loValue;
   }

   public void cleanUp()
   {
      if (moDataStore != null)
      {
         //         moDataStore.removeListener(this);
         moDataStore = null;
         miColumnMap = null;
      }
   }

   public void cursorMoved(RowSetEvent aoEvent)
   {
   }

   /**
    * Allows certain columns to be hidden from the table view.
    * @param asColumns columns to exclude by name
    */
   public void excludeColumns(String[] asColumns)
   {
      // Exclude Columns -> HashSet
      moExcludeColumns.clear();

      for (int i = 0; i < asColumns.length; i++)
      {
         moExcludeColumns.add(asColumns[i].toUpperCase());
      }

      buildColumnMap();
   }

   public void includeColumns(String[] asColumns)
   {
   }

   public void rowChanged(RowSetEvent aoEvent)
   {
      /*
            int liRow;

            // TODO: richtig ?
            try
            {
               liRow = ((RowSet) aoEvent.getSource()).getRow();
               this.fireTableChanged(new TableModelEvent(this, liRow));
            }
            catch (SQLException ex)
            {
               CException.record(ex, this);
            }
      */
   }

   public void rowChanged(int aiRow, int aiEventType)
   {
   }

   public void rowSetChanged(RowSetEvent aoEvent)
   {
      try
      {
         buildColumnMap();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      this.fireTableChanged(new TableModelEvent(this));
   }

   /**
    * Lets the data appear in sorted order.
    * @param aiColumn ordering column
    */
   public boolean sort(int aiColumn, boolean abAscending)
   {
      Class loColumnClass;
      boolean lbSortable = false;

      try
      {
         // is column comparable and thus sortable ?
         loColumnClass = getColumnClass(aiColumn);
         lbSortable = Comparable.class.isAssignableFrom(loColumnClass);

         // sort data if possible
         if (lbSortable)
         {
            // build default map (with specified column as data objects)
            moSortData.clear();
            mbAscending = abAscending;

            for (int i = 0; i < getRowCount(); i++)
            {
               moSortData.add(new CRow(i + 1, moDataStore.getColumn(i + 1, mapColumn(aiColumn))));
            }

            // sort rows
            java.util.Collections.sort(moSortData);

            // notify table
            this.fireTableDataChanged();
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return lbSortable;
   }

   /**
    *
    */
   private void buildColumnMap()
   {
      int liCount;
      int liMapPos;

      liCount = moDataStore.getColumnCount();
      miColumnMap = new int[liCount];
      liMapPos = 0;

      for (int i = 1; i <= liCount; i++)
      {
         if (!moExcludeColumns.contains(moDataStore.getColumnName(i).toUpperCase()))
         {
            miColumnMap[liMapPos] = i;
            liMapPos++;
         }
      }

      miColumnCount = liMapPos;
   }

   private void buildRowMap()
   {
      // default; not sorted
      for (int i = 0; i < getRowCount(); i++)
      {
         moSortData.add(new CRow(i + 1, null));
      }
   }

   private int mapColumn(int aiColumn)
   {
      return miColumnMap[aiColumn];
   }

   /**
    * Maps requested row number to the assigned datastore row.
    * Needed for sorting.
    * @see #sort
    */
   private int mapRow(int aiRow)
   {
      return ((CRow) moSortData.elementAt(aiRow)).miRow;
   }

   private static boolean mbAscending = true;
   private int[] miColumnMap;
   private int miColumnCount;
   private IDataStore moDataStore;
   private HashSet moExcludeColumns;
   private Vector moSortData;
}
