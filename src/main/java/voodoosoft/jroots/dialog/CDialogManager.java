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

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.core.CServiceName;

import java.util.HashMap;


/**
 * <code>CDialogManager</code> manages dialog prototypes and helps in opening and creating dialog objects.
 * <p><code>CDialogManager</code> is thread safe.
 */
public class CDialogManager extends CObject
{
   public CDialogManager()
   {
      moModalFlags = new HashMap();
      moPrototypes = new HashMap();
   }

   /**
    * Returns of specified name.
    * If the dialog prototype was registered as modal, the prototype itself is returned,
    * otherwise the prototype is cloned and passed back.
    */
   public CDialog getDialog(CServiceName aoDialogName)
                     throws CDialogNotFoundException
   {
      return getDialog(aoDialogName.toString());
   }

   /**
    * Returns of specified name.
    * If the dialog prototype was registered as modal, the prototype itself is returned,
    * otherwise the prototype is cloned and passed back.
    */
   public CDialog getDialog(String asDialogName) throws CDialogNotFoundException
   {
      Boolean lbModal;
      CDialog loDialog = null;

      try
      {
         synchronized (this)
         {
            lbModal = (Boolean) moModalFlags.get(asDialogName);
            loDialog = (CDialog) moPrototypes.get(asDialogName);
         }

         if (lbModal.equals(Boolean.FALSE))
         {
            loDialog = (CDialog) loDialog.cloneThis();
         }
      }
      catch (Exception ex)
      {
         throw new CDialogNotFoundException(ex, asDialogName);
      }

      if (loDialog == null)
      {
         throw new CDialogNotFoundException(asDialogName);
      }

      return loDialog;
   }

   /**
    * Adds dialog prototype to registry of this <code>CDialogManager</code>.
    * @param aoDialog
    * @param abSingle if true, <code>getDialog</code> will always return the same instance
    */
   public synchronized void registerPrototype(CDialog aoDialog, boolean abSingle)
   {
      moPrototypes.put(aoDialog.getName(), aoDialog);

      if (abSingle)
      {
         moModalFlags.put(aoDialog.getName(), Boolean.TRUE);
      }
      else
      {
         moModalFlags.put(aoDialog.getName(), Boolean.FALSE);
      }
   }

   /**
    * Frees all registered dialog prototypes.
    */
   public synchronized void releasePrototypes()
   {
      moModalFlags.clear();
      moPrototypes.clear();
   }

   private HashMap moPrototypes;
   private HashMap moModalFlags;
}
