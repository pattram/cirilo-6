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

import java.awt.Container;

import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;


/**
 * Class of type <code>IDialogCoreDelegate</code> using <code>JInternalFrame</code> objects.
 */
public class CInternalFrameDelegate implements IDialogCoreDelegate
{
   private static class CWindowAdapter extends InternalFrameAdapter
   {
      public CWindowAdapter(CInternalFrameDelegate ao_Dialog)
      {
         moDialog = ao_Dialog;
      }

      public void cleanUp()
      {
         moDialog = null;
      }

      public void internalFrameActivated(InternalFrameEvent ao_event)
      {
         moDialog.activated();
      }

      public void internalFrameClosing(InternalFrameEvent ao_event)
      {
         moDialog.close();
      }

      public void internalFrameOpened(InternalFrameEvent ao_event)
      {
         moDialog.openCallback();
      }

      private CInternalFrameDelegate moDialog;
   }

   public CInternalFrameDelegate(CDialog wrapper, JDesktopPane parent)
   {
      moWrapper = wrapper;
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
         moCore.removeInternalFrameListener(moWinAdapter);
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
      if (moCore != null)
      {
         moParent.remove(moCore);
      }

      moCore = new JInternalFrame(null, true, true, true, true);
      moParent.add(moCore);
   }

   public void show()
   {
      moCore.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
      moWinAdapter = new CWindowAdapter(this);
      moCore.addInternalFrameListener(moWinAdapter);

      moCore.pack();
      moCore.show();

      // still valid after opening ?
      if (moCore != null)
      {      
         try
         {
            moCore.setSelected(true);
         }
         catch (PropertyVetoException ex)
         {
            CException.record(ex, this, false);
         }
   
         moCore.toFront();
      }
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

   private JDesktopPane moParent;
   private JInternalFrame moCore;
   private CDialog moWrapper;
   private CWindowAdapter moWinAdapter;
}
