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

import voodoosoft.jroots.core.gui.*;
import voodoosoft.jroots.dialog.CAbstractTableModel;
import voodoosoft.jroots.exception.CException;

import java.awt.event.MouseEvent;

import javax.swing.JTable;


/**
 * Helper class to implement <code>JTable</code> sorting when table headers are clicked.
 */
public class CTableSortHandler implements IEventHandler
{
   /**
    * Creates and activates a new <code>CTableSortHandler</code> for the given <code>JTable</code>.
    * The table model of the specified table must be of class <code>CAbstractTableModel</code>.
    * To listen to table header events, a <code>CMouseListener</code> is used internally.
    * @see voodoosoft.jroots.dialog.CAbstractTableModel
    */
   public CTableSortHandler(JTable aoTable) //throws NoSuchMethodException
   {
      // @todo Exceptionhandling
      try
      {
         moTable = aoTable;
         new CMouseListener(moTable.getTableHeader(), this, "handleHeaderMouse");
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   public int handleHeaderMouse(MouseEvent e, int aoType)
                         throws Exception
   {
      int liColumn = -1;
      CAbstractTableModel loModel;

      try
      {
         if (aoType == MouseEvent.MOUSE_CLICKED)
         {
            liColumn = CGuiTools.getClickedColumn(moTable, e);
         }

         if (liColumn != -1)
         {
            if (liColumn == miLastSortColumn)
            {
               mbSortAscending = !mbSortAscending;
            }
            else
            {
               mbSortAscending = true;
            }

            miLastSortColumn = liColumn;

            loModel = (CAbstractTableModel) moTable.getModel();
            loModel.sort(liColumn, mbSortAscending);
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return liColumn;
   }

   /**
    * Callback method of <code>CEventListener</code> to disconnect this <code>CTableSortHandler</code>.
    * Releases internal <code>JTable</code> reference.
    * @param aoHandler
    */
   public void handlerRemoved(CEventListener aoHandler)
   {
      moTable = null;
   }

   private JTable moTable;
   private int miLastSortColumn = -1;
   private boolean mbSortAscending;
}
