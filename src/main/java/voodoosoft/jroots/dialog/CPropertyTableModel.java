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

import voodoosoft.jroots.exception.CException;

import java.util.*;


/**
 * Table model for <code>Properties</code>.
 */
public class CPropertyTableModel extends CAbstractTableModel
{
   public CPropertyTableModel(boolean abHideGroups)
   {
      moPropNames = new Vector();
      moPropValues = new Vector();
      moPropGroups = new Vector();

      miColFix = abHideGroups ? 1 : 0;
   }

   public CPropertyTableModel()
   {
      this(false);
   }

   /**
    * Always returns true to allow selecting displayed property values.
    */
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return true;
   }

   /**
    * Maps the appropriate Java class to the given SQL column type.
    * @param column (virtual) number
    */
   public Class getColumnClass(int column)
   {
      return String.class;
   }

   /**
    * Returns column number of this model.
    * @return column count
    */
   public int getColumnCount()
   {
      return 3 - miColFix;
   }

   /**
    * Gets column name by column number.
    * @param aiColumn (virtual) column number
    */
   public String getColumnName(int aiColumn)
   {
      switch (aiColumn + miColFix)
      {
         case 0:
            return "Gruppe";

         case 1:
            return "Eigenschaft";

         case 2:
            return "Wert";
      }

      return null;
   }

   /**
    * Returns number of rows this model contains.
    * @return row count
    */
   public int getRowCount()
   {
      int liCount;

      liCount = moPropNames.size();

      return liCount;
   }

   /**
    * Returns column value of specified column and row.
    * @return column value
    */
   public Object getValueAt(int aiRow, int aiColumn)
   {
      switch (aiColumn + miColFix)
      {
         case 0:
            return moPropGroups.get(aiRow);

         case 1:
            return moPropNames.get(aiRow);

         case 2:
            return moPropValues.get(aiRow);
      }

      return null;
   }

   public void addProperties(Properties aoProperties, String asGroup)
   {
      Enumeration loPropEnum;
      String lsProp;
      String[] lsValues;
      int i = 0;

      loPropEnum = aoProperties.keys();

      lsValues = new String[aoProperties.size()];

      while (loPropEnum.hasMoreElements())
      {
         lsProp = loPropEnum.nextElement().toString();
         lsValues[i++] = lsProp;
      }

      Arrays.sort(lsValues);

      for (i = 0; i < lsValues.length; i++)
      {
         moPropNames.add(lsValues[i]);

         if (moHiddenValues != null && moHiddenValues.containsKey(lsValues[i]))
         {
            moPropValues.add(moHiddenValues.get(lsValues[i]));
         }
         else
         {
            moPropValues.add(aoProperties.get(lsValues[i]));
         }

         if (asGroup != null)
         {
            moPropGroups.add(asGroup);
         }
      }
   }

   public void cleanUp()
   {
      moPropNames = null;
      moPropValues = null;
      moPropGroups = null;
   }

   public void hideValues(Map aoHiddenValues)
   {
      moHiddenValues = aoHiddenValues;
   }

   /**
    * Rebuilds internal row and sort mappings; useful if the underlying <code>IViewSet</code> has changed.
    */
   public void redraw()
   {
      try
      {
         fireTableDataChanged();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   private Vector moPropNames;
   private Vector moPropValues;
   private Vector moPropGroups;
   private Map moHiddenValues;
   private int miColFix;
}
