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

import voodoosoft.jroots.core.gui.CGuiTools;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;


/**
 * Class of type <code>IDialogCoreDelegate</code> using <code>JDialog</code> objects.
 */
public class CJDialogDelegate implements IDialogCoreDelegate
{
   private static class CWindowAdapter extends WindowAdapter
   {
      public CWindowAdapter(CJDialogDelegate ao_Dialog)
      {
         moDialog = ao_Dialog;
      }

      public void cleanUp()
      {
         moDialog = null;
      }

      public void windowActivated(WindowEvent ao_event)
      {
         moDialog.activated();
      }

      public void windowClosing(WindowEvent ao_event)
      {
         moDialog.close();
      }

      public void windowOpened(WindowEvent ao_event)
      {
         moDialog.openCallback();
      }

      private CJDialogDelegate moDialog;
   }

   public CJDialogDelegate(CDialog wrapper, Frame parent, boolean modal)
   {
      moWrapper = wrapper;
      mbModal = modal;
      moParent = parent;
   }

   public Container getCore()
   {
      return moCore;
   }

   public void setName(String name)
   {
      moCore.setName(name);
   }

   public void setRootComponent(Container root)
   {
      Container pane = moCore.getContentPane();
      pane.add(root);
   }

   public boolean isShowing()
   {
      return moCore == null ? false : moCore.isShowing();
   }

   public void setTitle(String title)
   {
      moCore.setTitle(title);
   }

   public void cleanUp()
   {
      if (moCore != null)
      {
         moCore.removeWindowListener(moWinAdapter);
         moCore.dispose();
         moCore = null;
      }

      if (moWinAdapter != null)
      {
         moWinAdapter.cleanUp();
         moWinAdapter = null;
      }
   }

   public void createCore()
   {
      moCore = new JDialog(moParent, null, mbModal);
   }

   public void show()
   {
      moCore.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      moWinAdapter = new CWindowAdapter(this);
      moCore.addWindowListener(moWinAdapter);

      //new CKeyListener(moCore, this, "handleDialogKeys");
      moCore.pack();
      if (System.getProperty( "os.name" ).indexOf("Windows")  > -1)  CGuiTools.center(moCore);
      moCore.setVisible(true);
   }

   public void toFront()
   {
      moCore.toFront();
   }

   private void activated()
   {
      moWrapper.activated();
   }

   private void close()
   {
      moWrapper.close();
   }

   private void openCallback()
   {
      moWrapper.openCallback();
   }

   private boolean mbModal;
   private JDialog moCore;
   private Frame moParent;
   private CDialog moWrapper;
   private CWindowAdapter moWinAdapter;
}
