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

import voodoosoft.jroots.business.CEntitySet;
import voodoosoft.jroots.business.IEntitySet;
import voodoosoft.jroots.business.IEntitySetListener;
import voodoosoft.jroots.core.CPropertyClass;
import voodoosoft.jroots.data.CInvalidColumnException;
import voodoosoft.jroots.data.CInvalidRowNumberException;
import voodoosoft.jroots.exception.CException;

import java.util.*;


/**
 * TableModel for tables using {@link voodoosoft.jroots.data.IDataStore IDataStore} objects as data source.
 * <li>columns can be hidden from view
 * <li>editing can be disabled for certain columns
 * <li>data can appear in sorted order
 * <li>column labels are changable
 * <p>
 * <p>Valid row numbers: 0 to (row count -1)
 */
public class CEntitySetTableModel extends CAbstractTableModel implements IEntitySetListener
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
         int liResult;
         CRow loRow = (CRow) o;

         if (moData == null && loRow.moData == null)
         {
            liResult = 0;
         }
         else if (moData == null)
         {
            liResult = mbAscending ? -1 : 1;
         }
         else if (loRow.moData == null)
         {
            liResult = mbAscending ? 1 : -1;
         }
         else if (mbAscending)
         {
            liResult = moData.compareTo(((CRow) o).moData);
         }
         else
         {
            liResult = ((CRow) o).moData.compareTo(moData);
         }

         return liResult;
      }

      public int miRow;
      public Comparable moData;
   }

   /**
    * Creates new read only model for the specified entity set.
    */
   public CEntitySetTableModel(IEntitySet aoDataStore)
   {
      this(aoDataStore, false);
   }

   /**
    * Creates new model for the specified entity set.
    * @param abEditable if true, the model will be editable
    */
   public CEntitySetTableModel(IEntitySet aoDataStore, boolean abEditable)
   {
      int liCount;

      moEntitySet = aoDataStore;
      mbEditable = abEditable;
      moExcludeColumns = new HashSet();
      moLockedColumns = new HashSet();
      moColumnNumbers = new HashMap();
      moSortData = new Vector();

      buildColumnMap();
      buildRowMap();
   }

   /**
    * Determines if a column is editable.
    * This depends upon the setting of <code>lockColumns</code> and the intitial construction.
    * @param rowIndex
    * @param columnIndex
    * @return true if editable
    * @see #CEntitySetTableModel
    * @see #lockColumns
    */
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      if (mbEditable && !moLockedColumns.contains(getColumnName(columnIndex).toUpperCase()))
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Maps the appropriate Java class to the given SQL column type.
    * @param column (virtual) number
    */
   public Class getColumnClass(int column)
   {
      CPropertyClass loPropClass;

      loPropClass = moEntitySet.getColumnClass(mapColumn(column));

      return loPropClass.javaClass;
   }

   /**
    * Returns column number of this model.
    * @return column count
    */
   public int getColumnCount()
   {
      return miColumnCount;
   }

   /**
    * Changes the default column labels to the given column/label map
    * @param aoLabels <code>Map</code> of column/label pairs (key is column name)
    */
   public void setColumnLabels(Map aoLabels)
   {
      moLabelMap = aoLabels;
      buildColumnMap();
   }

   /**
    * Gets column name by column number.
    * @param aiColumn (virtual) number
    */
   public String getColumnName(int aiColumn)
   {
      return moLabels.get(aiColumn).toString();
   }


   /**
    * Returns the corresponding column number for the given column name.
    * @param asColumn
    * @return column number
    */
   public int getColumnNumber(String asColumn)
   {
      return ((Integer) moColumnNumbers.get(asColumn.toUpperCase())).intValue();
   }

   /**
    * Changes underlying entity set and rebuilds internal column structure.
    * @param aoSet
    */
   public void setEntitySet(IEntitySet aoSet)
   {
      setEntitySet(aoSet, true);
   }

   /**
    * Changes underlying entity set.
    * @param aoSet
    * @param abStructureChanged if true, rebuild internal column structure
    */
   public void setEntitySet(IEntitySet aoSet, boolean abStructureChanged)
   {
      try
      {         
         moEntitySet = aoSet;

         if (abStructureChanged)
         {            
            miSortOrder = -1;
            buildColumnMap();
         }

         buildRowMap();

         if (abStructureChanged)
         {
            fireTableStructureChanged();
         }

         fireTableDataChanged();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Returns underlying <code>IEntitySet</code>.
    * @return IEntitySet
    */
   public IEntitySet getEntitySet()
   {
      return moEntitySet;
   }

   /**
    * Returns number of rows this model contains.
    * @return row count
    */
   public int getRowCount()
   {
      int liCount;

      liCount = moEntitySet.getRowCount();

      return liCount;
   }

   /**
    * Sets column value of specified column and row.
    */
   public void setValueAt(Object aoValue, int aiRow, String asColumn)
   {
      try
      {
         moEntitySet.setColumn(mapRow(aiRow), asColumn, aoValue);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Sets column value of specified column and row.
    */
   public void setValueAt(Object aoValue, int aiRow, int aiColumn)
   {
      String lsColName;

      try
      {
         lsColName = getColumnName(aiColumn);
         moEntitySet.setColumn(mapRow(aiRow), lsColName, aoValue);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Returns column value of specified column and row.
    * @return column value
    */
   public Object getValueAt(int aiRow, int aiColumn)
   {
      Object loValue = null;

      try
      {
         loValue = moEntitySet.getColumn(mapRow(aiRow), mapColumn(aiColumn));
      }
      catch (Exception ex)
      {
         if (!mbExceptionCatched)
         {
            mbExceptionCatched = true;
            CException.record(ex, this, true);
         }
      }

      return loValue;
   }

   /**
    * Returns column value of specified column and row.
    * @return column value
    */
   public Object getValueAt(int aiRow, String asColumn)
   {
      Object loValue = null;

      try
      {
         loValue = moEntitySet.getColumn(mapRow(aiRow), asColumn);
      }
      catch (Exception ex)
      {
         if (!mbExceptionCatched)
         {
            mbExceptionCatched = true;
            CException.record(ex, this, true);
         }
      }

      return loValue;
   }

   public void cleanUp()
   {
      if (moEntitySet != null)
      {
         //         moEntitySet.removeListener(this);
         moEntitySet = null;
         miColumnMap = null;
      }
   }

   /**
    * Allows certain columns to be hidden from the table view.
    * @param asColumns columns to exclude by name
    */
   public void excludeColumns(String[] asColumns) // throws SQLException
   {
      // Exclude Columns -> HashSet
      moExcludeColumns.clear();

      for (int i = 0; i < asColumns.length; i++)
      {
         moExcludeColumns.add(asColumns[i].toUpperCase());
      }

      buildColumnMap();
   }

   
   /**
    * Removes specified row from this table model and the underlying entity set.
    * Data will not be resorted automatically.
    * @param aiRow
    */
   public void deleteRow(int aiRow) throws CInvalidRowNumberException
   {
      int liMappedRow = mapRow(aiRow);
      mbProcessRowEvent = false;
      moEntitySet.deleteRow(liMappedRow);
      if (!mbProcessRowEvent)
      {      
         buildRowMap();
         this.fireTableRowsDeleted(aiRow-1, aiRow-1);
      }      
   }

   /**
    * Inserts new row to this model and the underlying entity set.
    * Data will not be resorted automatically.
    * @param aiAfterRow not in use yet, row will be appended
    * @return number of inserted row
    */
   public int insertRow(int aiAfterRow)
   {
      int liInserted = -1;

      mbProcessRowEvent = false;
      liInserted = moEntitySet.insertRow();
      if (!mbProcessRowEvent)
      {           
         moSortData.add(new CRow(moSortData.size() + 1, null));
         this.fireTableRowsInserted(liInserted, liInserted);
      }
      
      liInserted--;

      return liInserted;
   }

   /**
    * Allows to prevent columns from being edited.
    * @param asColumns column names to lock
    */
   public void lockColumns(String[] asColumns)
   {
      moLockedColumns.clear();

      for (int i = 0; i < asColumns.length; i++)
      {
         moLockedColumns.add(asColumns[i].toUpperCase());
      }
   }

   public void rowChanged(int aiRow, int aiEventType)
   {
      try
      {         
         switch (aiEventType)
         {
            case CEntitySet.ROW_INSERTED:
               moSortData.add(new CRow(moSortData.size() + 1, null));
               aiRow--; // covert range (0..n)
               this.fireTableRowsInserted(aiRow, aiRow);
               mbProcessRowEvent = true;

               break;

            case CEntitySet.ROW_DELETED:
               buildRowMap();
               aiRow--; // covert range (0..n)
               this.fireTableRowsDeleted(aiRow, aiRow);
               mbProcessRowEvent = true;
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Rebuilds a sorted internal row map.
    * Sort order is the column given to method <code>sort</code>. 
    * @throws CInvalidColumnException
    */
   public void resort() throws CInvalidColumnException
   {
      if (miSortOrder != -1)
      {
         moSortData.clear();
         for (int i = 0; i < getRowCount(); i++)
         {
            moSortData.add(new CRow(i + 1, moEntitySet.getColumn(i + 1, mapColumn(miSortOrder))));
         }
            
         java.util.Collections.sort(moSortData);
      }
      else
      {
         buildRowMap();         
      }
            
      // notify table
      this.fireTableDataChanged();      
   }
   
   /**
    * Removes current sort order of this table model and rebuilds the internal row map.
    * @throws CInvalidColumnException
    */
   public void clearSortOrder() throws CInvalidColumnException
   {
      miSortOrder = -1;      
      resort();
   }

   /**
    * Lets the data appear in sorted order using the specified column as sort criterium.
    * @param aiColumn ordering column
    * @param abAscending if true, sort in ascending order, otherwise descending
    */ 
   public boolean sort(int aiColumn, boolean abAscending) throws CInvalidColumnException
   {  
      Class loColumnClass;
      boolean lbSortable = false;
    
      // is column comparable and thus sortable ?
      loColumnClass = getColumnClass(aiColumn);
      lbSortable = Comparable.class.isAssignableFrom(loColumnClass);

      // sort data if possible
      if (lbSortable)
      {
         mbAscending = abAscending;
         miSortOrder = aiColumn;            
         resort();
      }

      return lbSortable;
   }

   private void buildColumnMap()
   {
      int liCount = 0;
      int liMapPos;
      String lsLabel;
      String lsCol;
      String lsLookup;
      Object loMappedLabel;

      if (moEntitySet != null)
      {
         liCount = moEntitySet.getColumnCount();
      }

      miColumnMap = new int[liCount];
      moLabels = new Vector(liCount);
      liMapPos = 0;

      for (int i = 1; i <= liCount; i++)
      {
         if (moLabelMap == null)
         {
            lsLabel = moEntitySet.getColumnLabel(i);
         }
         else
         {
            lsCol = moEntitySet.getColumnLabel(i);
            loMappedLabel = moLabelMap.get(lsCol);

            if (loMappedLabel == null)
            {
               lsLabel = lsCol;
            }
            else
            {
               lsLabel = loMappedLabel.toString();
            }
         }

         lsLookup = (lsLabel == null ? "" : lsLabel.toUpperCase());

         if (!moExcludeColumns.contains(lsLookup))
         {
            moLabels.add(lsLabel);
            moColumnNumbers.put(lsLookup, new Integer(liMapPos));
            miColumnMap[liMapPos] = i;
            liMapPos++;
         }
      }

      miColumnCount = liMapPos;
   }

   private void buildRowMap()
   {
      int liRows = getRowCount();

      // default; not sorted
      moSortData.clear();
      for (int i = 0; i < liRows; i++)
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
   private HashMap moColumnNumbers;
   private int miColumnCount;
   private Map moLabelMap;
   private Vector moLabels;
   private HashSet moExcludeColumns;
   private HashSet moLockedColumns;
   private IEntitySet moEntitySet;
   private Vector moSortData;
   private int miSortOrder = -1;
   private boolean mbEditable;
   private boolean mbExceptionCatched = false;
   private boolean mbProcessRowEvent;
}
