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

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.dialog.IGuiAdapter;

import java.util.HashSet;
import java.util.Vector;


/**
 * Default access manager implementation, mainly for gui configuring.
 * The <code>CDefaultAccessManager</code> can execute rules of type <code>CGuiRule</code>.
 */
public class CDefaultAccessManager extends CObject implements IAccessManager
{
   /**
    * Creates new access manager using the specified <code>IAccessStorage</code> as rule source.
    * @param aoStorage
    */
   public CDefaultAccessManager(IAccessStorage aoStorage)
   {
      moRules = new Vector();
      moWidgetToEnable = new HashSet();
   }

   /**
    * Sets the internal <code>IGuiAdapter</code> which is used to modify GUIs.
    * If this <code>CDefaultAccessManager</code> has to execute rules of class
    * <code>CGuiRule</code> the internal <code>IGuiAdapter</code> must be a valid object.
    * @param aoAdapter IGuiAdapter to use
    */
   public void setGuiAdapter(IGuiAdapter aoAdapter)
   {
      moGuiAdapter = aoAdapter;
   }

   /**
    * Returns gui adapter of this access manager.
    * <code>getGuiAdapter</code> is called by <code>CGuiRule</code> during execution.
    * @return IGuiAdapter
    */
   public IGuiAdapter getGuiAdapter()
   {
      return moGuiAdapter;
   }

   public void setStorage(IAccessStorage aoStorage)
   {
   }

   /**
    * Adds the specified rule.
    */
   public void addRule(CGuiRule aoRule)
   {
      moRules.add(aoRule);
   }

   /**
    * Internal framework method.
    * @param asWidget
    */
   public void addWidgetToEnable(String asWidget)
   {
      //      CLogManager.log(this, asWidget.toUpperCase(), CLogManager.LOG_DEBUG);
      moWidgetToEnable.add(asWidget);
   }

   public void execRules(IAccessContext aoContext)
   {
      IAccessRule loRule;

      moLogger.debug("execRules");

      moWidgetToEnable.clear();

      for (int i = 0; i < moRules.size(); i++)
      {
         loRule = (IAccessRule) moRules.elementAt(i);

         if (loRule.getContext().matches(aoContext))
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("rule matches [" + loRule.toString() + "]");
            }

            loRule.exec(this);
         }
      }
   }

   public void loadRules()
   {
      moRules.clear();
   }

   /**
    * <code>CDefaultGuiAdapter</code> uses this method whenever <code>setEnabled</code>
    * is called to check whether it is allowed to enable the widget.
    * @param asWidget
    * @return enable flag
    * @see voodoosoft.jroots.dialog.CDefaultGuiAdapter
    */
   public boolean mayEnable(String asWidget)
   {
      boolean lbEnable;

      lbEnable = moWidgetToEnable.contains(asWidget);

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("mayEnable [" + asWidget + "] ? " + String.valueOf(lbEnable));
      }

      return lbEnable;
   }

   public void passivate()
   {
      moGuiAdapter = null;
      moWidgetToEnable.clear();
   }

   protected static Logger moLogger = Logger.getLogger(CDefaultAccessManager.class);
   private Vector moRules;
   private IGuiAdapter moGuiAdapter;
   private HashSet moWidgetToEnable;
}
