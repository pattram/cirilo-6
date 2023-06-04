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

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.debug.CLogManager;
import voodoosoft.jroots.exception.CException;

import java.awt.Container;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;


/**
 * <code>CGuiComposite</code> encapsulates one hierarchy of Swing components.
 * Each component (or widget) is identified by its unique name.
 * Calling <code>setRootComponent</code> is a necessity for using this <code>CGuiComposite</code>
 * with dialog objects.
 * @see CGuiManager#addWidgetTree
 * @see voodoosoft.jroots.dialog.CDialog
 */
public class CGuiComposite extends CObject implements IGuiComposite
{
   public CGuiComposite(String asName, Container aoRoot)
   {
      setName(asName);
      moWidgets = new Hashtable();
      setRootComponent(aoRoot);
   }

   public CGuiComposite(String asName)
   {
      this(asName, null);
      mbBuilt = false;
   }

   protected CGuiComposite(CGuiFactory aoFactory, String asName)
   {
      this(asName, null);
      mbBuilt = false;
      moFactory = aoFactory;
   }

   /**
    * Sets the root component of this gui.
    * @param aoRoot new root component
    */
   public void setRootComponent(Container aoRoot)
   {
      moRootComponent = aoRoot;
      mbBuilt = true;
   }

   /**
    * Returns root component of this gui.
    * @return root component
    */
   public Container getRootComponent()
   {
      if (!mbBuilt)
      {
         buildGUI();
      }

      return moRootComponent;
   }

   /**
   * Returns a reference to the specified Widget.
   * @param asName name of widget
   * @return the widget as JComponent
   * @exception CInvalidNameException
    */
   public JComponent getWidget(String asName) throws CInvalidNameException
   {
      JComponent lo_found;

      if (!mbBuilt)
      {
         buildGUI();
      }

      //      lo_found = (JComponent) moWidgets.get(asName.toUpperCase());
      lo_found = (JComponent) moWidgets.get(asName);

      if (lo_found == null)
      {
         throw new CInvalidNameException(asName);
      }

      return lo_found;
   }

   /**
    * Gets table of all available widgets.
    * @return widget table
    */
   public Map getWidgetTable()
   {
      if (!mbBuilt)
      {
         buildGUI();
      }

      return moWidgets;
   }

   /**
    * Removes all widgets from this Gui.
    */
   public void clearWidgets()
   {
      moWidgets.clear();
      setRootComponent(null);
   }

   protected void setWidgetName(JComponent aoCom, String asName)
   {
      aoCom.setName(asName);
      aoCom.putClientProperty(CGuiManager.getWidgetNameKey(), asName.toUpperCase());
   }

   protected void setup()
   {
   }

   private void buildGUI()
   {
      try
      {
         CLogManager.log(this, "lazy gui building: " + this.toString());
         moFactory.buildGUI(this, null);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      moFactory = null; // nicht mehr nötig
      mbBuilt = true;
   }

   private boolean mbBuilt;
   private CGuiFactory moFactory;
   private Container moRootComponent;
   private Hashtable moWidgets;
}
