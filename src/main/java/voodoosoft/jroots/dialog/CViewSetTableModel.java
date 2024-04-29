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

import voodoosoft.jroots.business.IViewSet;
import voodoosoft.jroots.core.CPropertyClass;
import voodoosoft.jroots.data.CInvalidColumnException;
import voodoosoft.jroots.exception.CException;

import java.util.*;


/**
 * Read-only table model for tables using <code>IViewSet</code> objects as underlying data source.
 * <li>columns can be hidden from viewing
 * <li>data can appear in sorted order
 * <li>column labels are changable
 * <p>
 * <p>row numbers: 0 to (row count -1)
 * @see voodoosoft.jroots.business.IViewSet
 */
public class CViewSetTableModel extends CAbstractTableModel
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

   public CViewSetTableModel(IViewSet aoDataStore, Map aoLabelMap)
   {
      moLabelMap = aoLabelMap;

      moEntitySet = aoDataStore;
      moExcludeColumns = new HashSet();
      moColumnNumbers = new HashMap();
      moSortData = new Vector();

      buildColumnMap();
      buildRowMap();
   }

   /**
   * Creates new model for the specified view set.
   */
   public CViewSetTableModel(IViewSet aoDataStore)
   {
      moEntitySet = aoDataStore;
      moExcludeColumns = new HashSet();
      moColumnNumbers = new HashMap();
      moSortData = new Vector();

      buildColumnMap();
      buildRowMap();
   }

   /**
    * <code>CViewSetTableModel</code> is never editable. Always returns false.
    */
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return false;
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
    * @param aiColumn (virtual) column number
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
    * Sets underlying view set and rebuilds internal column, row and sort mappings.
    * @param aoSet new data source for this <code>CViewSetTableModel</code>
    */
   public void setEntitySet(IViewSet aoSet)
   {
      setEntitySet(aoSet, true);
   }

   /**
    * Sets underlying view set and rebuilds internal row and sort mappings.
    * @param aoSet new data source for this <code>CViewSetTableModel</code>
    * @param abStructureChanged if true, the internal column mappings are rebuild
    */
   public void setEntitySet(IViewSet aoSet, boolean abStructureChanged)
   {
      try
      {
         moEntitySet = aoSet;

         if (abStructureChanged)
         {            
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
    * Returns underlying <code>IViewSet</code>.
    * @return IViewSet
    */
   public IViewSet getEntitySet()
   {
      return moEntitySet;
   }

   /**
    * Returns number of rows this model contains.
    * @return row number
    */
   public int getRowCount()
   {
      int liCount;

      liCount = moEntitySet.getRowCount();

      return liCount;
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
    * Rebuilds internal row and sort mappings; useful if the underlying <code>IViewSet</code> has changed.
    */
   public void redraw()
   {
      try
      {         
         buildRowMap();
         fireTableDataChanged();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Lets data appear in sorted order using the specified column as sort criterium.
    * @param aiColumn ordering column
    * @param abAscending if true, sort in ascending order, otherwise descending
    */
   public boolean sort(int aiColumn, boolean abAscending) throws CInvalidColumnException
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
               moSortData.add(new CRow(i + 1, moEntitySet.getColumn(i + 1, mapColumn(aiColumn))));
            }

            // sort rows
            java.util.Collections.sort(moSortData);
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
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
   private Map moLabelMap;
   private Vector moLabels;
   private HashMap moColumnNumbers;
   private int miColumnCount;
   private HashSet moExcludeColumns;
   private IViewSet moEntitySet;
   private Vector moSortData;
   private boolean mbExceptionCatched = false;
}
