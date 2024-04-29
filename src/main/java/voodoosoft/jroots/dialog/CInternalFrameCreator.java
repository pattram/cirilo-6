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

import voodoosoft.jroots.core.CCreateFailedException;
import voodoosoft.jroots.core.CServiceName;

import javax.swing.JDesktopPane;


/**
 * Helper class for creating <code>CDialog</code>s based on <code>JInternalFrame</code> core objects.
 * @see CDialogCreator
 * @see CDialog
 */
public class CInternalFrameCreator implements IDialogCreator
{
   public CInternalFrameCreator(JDesktopPane defaultParent)
   {
      moDefaultParent = defaultParent;
   }

   public CDialog createDialog(Class aoDialogClass, String asGuiName, String asDialogTitle,
                               CServiceName asDialogName)
                        throws CCreateFailedException
   {
      return createDialog(aoDialogClass, asGuiName, asDialogTitle, asDialogName.toString());
   }

   public CDialog createDialog(Class aoDialogClass, String asGuiName, String asDialogTitle,
                               String asDialogName) throws CCreateFailedException
   {
      CDialog loNewDialog = null;

      try
      {
         loNewDialog = (CDialog) aoDialogClass.newInstance();
         loNewDialog.setDelegate(new CInternalFrameDelegate(loNewDialog, moDefaultParent));
         loNewDialog.init(asGuiName, asDialogTitle, asDialogName);
      }
      catch (Exception ex)
      {
         throw new CCreateFailedException(aoDialogClass, ex);
      }

      return loNewDialog;
   }

   private JDesktopPane moDefaultParent;
}
