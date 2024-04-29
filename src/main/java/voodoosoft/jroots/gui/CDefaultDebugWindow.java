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


package voodoosoft.jroots.gui;

import voodoosoft.jroots.debug.IDebugWindow;
import voodoosoft.jroots.exception.CException;

import java.awt.*;

import java.text.DateFormat;

import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


/**
 * Under development.
 */
public class CDefaultDebugWindow extends CGuiComposite implements IDebugWindow
{
   public CDefaultDebugWindow()
   {
      super("CDefaultDebugWindow");

      try
      {
         jbInit();
         setup();
         setRootComponent(jPanel1);
      }
      catch (Exception e)
      {
         CException.record(e, this);
      }
   }

   public void exceptionThrown(Object aoExSource, Class aoExClass, String asExMessage,
                               String asStack, Date aoThrownAt)
   {
      String[] lsRow = { "<empty>", "<empty>", "<empty>", "<empty>" };

      if (aoThrownAt != null)
      {
         lsRow[0] = DateFormat.getDateTimeInstance().format(aoThrownAt);
      }

      if (aoExSource != null)
      {
         lsRow[1] = aoExSource.getClass().getName();
      }

      if (aoExClass != null)
      {
         lsRow[2] = aoExClass.getName();
      }

      lsRow[3] = asExMessage;

      moModel.addRow(lsRow);

      jtaStackTrace.setText(asStack);
   }

   protected void setup()
   {
      jSplitPane1.setDividerLocation(100);
      jbClose.setMnemonic('C');
      jbPrint.setMnemonic('P');
      jbEMail.setMnemonic('E');

      //      setWidgetName(jtaException, "jtaException");
      setWidgetName(jtaStackTrace, "jtaStackTrace");
      setWidgetName(jbClose, "close");

      String[] lsColumns = { "date", "origin", "class", "message" };
      moModel = new DefaultTableModel(lsColumns, 0);
      jTable1.setModel(moModel);
      ;
   }

   private void jbInit() throws Exception
   {
      jPanel1.setLayout(gridBagLayout1);
      jbClose.setText("Close");
      jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
      jtaStackTrace.setWrapStyleWord(true);
      jtaStackTrace.setBorder(null);
      jScrollPane2.setBorder(BorderFactory.createLoweredBevelBorder());
      jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jbPrint.setText("Print");
      jbEMail.setText("EMail");
      jPanel1.add(jSplitPane1,
                  new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                         GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 186));
      jSplitPane1.add(jScrollPane2, JSplitPane.BOTTOM);
      jScrollPane2.getViewport().add(jtaStackTrace, null);
      jSplitPane1.add(jScrollPane1, JSplitPane.TOP);
      jScrollPane1.getViewport().add(jTable1, null);
      jPanel1.add(jbPrint,
                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                         GridBagConstraints.NONE, new Insets(16, 16, 9, 0), 49, 3));
      jPanel1.add(jbEMail,
                  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                         GridBagConstraints.NONE, new Insets(16, 25, 9, 0), 43, 3));
      jPanel1.add(jbClose,
                  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                         GridBagConstraints.NONE, new Insets(16, 25, 9, 10), 41, 3));
   }

   DefaultTableModel moModel;
   JPanel jPanel1 = new JPanel();
   JButton jbClose = new JButton();
   JScrollPane jScrollPane1 = new JScrollPane();
   JTextArea jtaStackTrace = new JTextArea();
   JScrollPane jScrollPane2 = new JScrollPane();
   JSplitPane jSplitPane1 = new JSplitPane();
   JButton jbPrint = new JButton();
   JButton jbEMail = new JButton();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JTable jTable1 = new JTable();
}
