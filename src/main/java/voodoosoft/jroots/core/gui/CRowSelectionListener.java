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

import voodoosoft.jroots.exception.CException;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Listener for <code>ListSelectionEvent</code> events of <code>JTable</code> objects.
 * <p>Handler methods can have one of the following signatures:
 * <li><code>public void handleMethod(ListSelectionEvent aoEvent)</code>
 * <li><code>public void handleMethod(Integer aiSelectedRow)</code>
 */
public class CRowSelectionListener extends CEventListener implements ListSelectionListener
{
   /**
    * Installs new <code>CRowSelectionListener</code>
    * @param aoTable <code>JTable</code> as event source
    * @param aoEventHandler target object for event handling
    * @param asHandleMethod method to invoke for every <code>ListSelectionEvent</code>
    * @param abEventNotify if true, the handle methods must have a parameter of class <code>ListSelectionEvent</code>,
    * if false the parameter must be <code>Integer</code> and will represent the selected row
    * @throws CListenerInstallFailedException
    */
   public CRowSelectionListener(JTable aoTable, IEventHandler aoEventHandler,
                                String asHandleMethod, boolean abEventNotify)
                         throws CListenerInstallFailedException
   {
      super(aoEventHandler);

      mbEventNotify = abEventNotify;

      if (abEventNotify)
      {
         setupHandleMethod(asHandleMethod,
                           new Class[] { javax.swing.event.ListSelectionEvent.class });
      }
      else
      {
         setupHandleMethod(asHandleMethod, new Class[] { Integer.TYPE, Integer.TYPE });
      }

      install(aoTable);
   }

   /**
    * Internal method called by the constructor.
    * @param aoTable
    */
   public void install(JTable aoTable)
   {
      if (moTable != null)
      {
         remove();
      }

      moTable = aoTable;
      moTable.getSelectionModel().addListSelectionListener(this);
      moTable.setRowSelectionAllowed(true);
      moTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   }

   public void remove()
   {
      super.remove();

      if (moTable != null)
      {
         moTable.getSelectionModel().removeListSelectionListener(this);
      }
   }

   public void valueChanged(ListSelectionEvent e)
   {
      Integer liCurrentRow;

      if (mbEventNotify)
      {
         invokeHandler(e);
      }
      else
      {
         try
         {
            liCurrentRow = new Integer(moTable.getSelectedRow());

            if (liCurrentRow.intValue() != miLastRow.intValue())
            {
               moArgs[0] = miLastRow;
               moArgs[1] = liCurrentRow;
               invokeHandler(moArgs);
               miLastRow = liCurrentRow;
            }
         }
         catch (Exception ex)
         {
            CException.record(ex, this);
         }
      }
   }

   private JTable moTable;
   private boolean mbEventNotify;
   Integer miLastRow = new Integer(-1);
}
