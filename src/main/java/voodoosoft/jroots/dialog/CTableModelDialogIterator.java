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

import voodoosoft.jroots.dialog.CAbstractTableModel;


/**
 * Dialog navigation based on a <code>CAbstractTableModel</code>.
 */
public class CTableModelDialogIterator implements IDialogIterator
{
   public CTableModelDialogIterator(CAbstractTableModel aoModel, String[] asKeyColumns,
                                    int aiStartRow)
   {
      this(aoModel, asKeyColumns, asKeyColumns, aiStartRow);
   }

   public CTableModelDialogIterator(CAbstractTableModel aoModel, String[] asKeyColumns,
                                    String[] asGroupColumns, int aiStartRow)
   {
      moModel = aoModel;
      msKeyColumns = asKeyColumns;
      msGroupColumns = asGroupColumns;
      miRow = aiStartRow;
      moCurrentKey = null;
   }

   public IDialogKey current()
   {
      moCurrentKey = createKey(miRow);

      return moCurrentKey;
   }

   public boolean hasNext()
   {
      return (miRow < moModel.getRowCount() - 1 ? true : false);
   }

   public boolean hasPrevious()
   {
      return (miRow > 0 ? true : false);
   }

   public IDialogKey next()
   {
      CDefaultDialogKey loKey = null;

      while (hasNext())
      {
         loKey = createKey(++miRow);

         if (!loKey.equalsGroup(moCurrentKey))
         {
            break;
         }
      }

      moCurrentKey = loKey;

      return loKey;
   }

   public IDialogKey previous()
   {
      CDefaultDialogKey loKey = null;

      while (hasPrevious())
      {
         loKey = createKey(--miRow);

         if (!loKey.equalsGroup(moCurrentKey))
         {
            break;
         }
      }

      moCurrentKey = loKey;

      return loKey;
   }

   private boolean isGroupColumn(String column)
   {
      for (int i = 0; i < msGroupColumns.length; i++)
      {
         if (msGroupColumns[i].equals(column))
         {
            return true;
         }
      }

      return false;
   }

   private CDefaultDialogKey createKey(int aiRow)
   {
      CDefaultDialogKey loKey = null;
      Object loValue;

      loKey = new CDefaultDialogKey();

      for (int i = 0; i < msKeyColumns.length; i++)
      {
         loValue = moModel.getValueAt(aiRow, msKeyColumns[i]);
         loKey.addAttrib(msKeyColumns[i], isGroupColumn(msKeyColumns[i]));
         loKey.setAttribute(msKeyColumns[i], loValue);
      }

      return loKey;
   }

   private CAbstractTableModel moModel;
   private int miRow;
   private String[] msKeyColumns;
   private String[] msGroupColumns;
   private CDefaultDialogKey moCurrentKey;
}
