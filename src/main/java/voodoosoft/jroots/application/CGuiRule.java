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


package voodoosoft.jroots.application;

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.dialog.IGuiAdapter;
import voodoosoft.jroots.exception.CException;

import java.util.*;


/**
 * Access rule for modifying GUIs.
 * One <code>CGuiRule</code> can work on one or several widgets.
 * It can
 * <li> enable or disable widgets
 * <li> show or hide widgets
 *
 * To modify widgets, the execution is delegated to the specified <code>voodoosoft.dialog.IGuiAdapter</code>
 * when <code>exec</code> is called.
 */
public class CGuiRule extends CObject implements IAccessRule
{
   /**
    * Creates a new rule object.
    * To get the widget set to modify, at first the include set is evaluated, afterwards widgets of the exclude set are removed.
    * Note that if the include set equals to the asterix(*), all widgets are affected (minus exclude set).
    * @param aoCxt context for this rule
    * @param asCode instruction what to do, one of: enable, disable, show, hide
    * @param asInclude list of widgets to include for execution, comma separated, the asterix(*) stands for all widgets
    * @param asExclude list of widgets to exclude of execution, comma separated
    */
   public CGuiRule(IAccessContext aoCxt, String asCode, String asInclude, String asExclude)
            throws CInvalidCommandException
   {
      /** @todo check command */
      moCxt = aoCxt;
      msCode = asCode.trim().toLowerCase();

      if (asInclude != null)
      {
         msInclude = asInclude.trim();
      }
      else
      {
         msInclude = "";
      }

      if (asExclude != null)
      {
         msExclude = asExclude.trim();
      }
      else
      {
         msExclude = null;
      }
   }

   /**
    * Returns context.
    */
   public IAccessContext getContext()
   {
      return moCxt;
   }

   /**
    * Executes this rule using the given <code>IAccessManager</code> for widget modification.
    * @param aoManager IAccessManager
    */
   public void exec(IAccessManager aoManager)
   {
      String lsWidget;
      HashSet loInclude;
      HashSet loExclude;
      Map loWidgetTable;
      Iterator loIT;
      IGuiAdapter loGuiAdapter;
      CDefaultAccessManager loAccMan;
      boolean lbEnabled;

      loAccMan = (CDefaultAccessManager) aoManager;
      loGuiAdapter = loAccMan.getGuiAdapter();

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("include [" + msInclude + "] exclude [" + msExclude + "]");
      }

      // create include widget set
      if (msInclude.equals("*"))
      {
         loWidgetTable = loGuiAdapter.getWidgetTable();
         loInclude = new HashSet(loWidgetTable.keySet());
      }
      else
      {
         loInclude = createSet(msInclude);
      }

      // remove exclude widget set from include set
      if (!CTools.isEmpty(msExclude))
      {
         loExclude = createSet(msExclude);
         loInclude.removeAll(loExclude);
      }

      // modify gui
      loIT = loInclude.iterator();

      while (loIT.hasNext())
      {
         lsWidget = ((String) loIT.next()).trim();

         try
         {
            moLogger.debug("code [" + msCode + "] widget [" + lsWidget + "]");

            if (msCode.equals("enable"))
            {
               loAccMan.addWidgetToEnable(lsWidget);
               loGuiAdapter.setEnabled(lsWidget, true);
            }
            else if (msCode.equals("disable"))
            {
               loGuiAdapter.setEnabled(lsWidget, false);
            }
            else if (msCode.equals("show"))
            {
               loGuiAdapter.setVisible(lsWidget, true);
            }
            else if (msCode.equals("hide"))
            {
               loGuiAdapter.setVisible(lsWidget, false);
            }
         }
         catch (Exception ex)
         {
            CException.record(ex, this);
         }
      }
   }

   private HashSet createSet(String asList)
   {
      HashSet loSet;
      String lsElement;
      StringTokenizer loCommaTok;

      loCommaTok = new StringTokenizer(asList, ",");

      loSet = new HashSet();

      while (loCommaTok.hasMoreElements())
      {
         lsElement = ((String) loCommaTok.nextElement()).trim();

         if (!lsElement.equals(""))
         {
            loSet.add(lsElement);
         }
      }

      return loSet;
   }

   protected static Logger moLogger = Logger.getLogger(CDefaultAccessManager.class);
   private IAccessContext moCxt;
   private String msCode;
   private String msInclude;
   private String msExclude;
}
