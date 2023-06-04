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

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.dialog.CDefaultGuiAdapter;
import voodoosoft.jroots.dialog.IGuiAdapter;

import java.awt.*;

import java.util.*;

import javax.swing.*;


/**
 * The CGuiManager manages objects of type {@link IGuiComposite} and {@link voodoosoft.jroots.dialog.IGuiAdapter}
 */
public class CGuiManager extends CObject
{
   /**
    * Class for caching Hashcodes.
    */
   private final class CHashKey extends Object
   {
      CHashKey(Object aoObject)
      {
         miHashKey = aoObject.hashCode();
      }

      public int hashCode()
      {
         return miHashKey;
      }

      private int miHashKey;
   }

   /*
   * P U B L I C
   */

   /**
    * Standard constructor.
    * Calls <code>CGuiManager(false, true)</code>
    * @throws CDuplicateNameException
    */
   public CGuiManager() throws CDuplicateNameException
   {
      this(false, true);
   }

   /**
    * Creates new <code>CGuiManager</code>.
    * Path naming means that widget names are built recursively throughout their component hierarchy.
    * For example, if a button "jbUpdate" is the child of a panel "jpOrder", path naming would
    * name the button "jpOrder.jbUpdate", otherwise it would be simply "jbUpdate".
    * @param abPathNaming true to enable path naming
    * @param abDefaultInterfaces true to create a ready to use <code>CDefaultGuiAdapter</code>
    * @throws CDuplicateNameException
    */
   public CGuiManager(boolean abPathNaming, boolean abDefaultInterfaces)
               throws CDuplicateNameException
   {
      CDefaultGuiAdapter loDefault;

      moComposites = new Hashtable();
      moClassNames = new Hashtable();
      moAdapter = new Hashtable();
      mbPathNaming = abPathNaming;

      if (abDefaultInterfaces)
      {
         loDefault = new CDefaultGuiAdapter(null);
         moDefaultHashKey = new CHashKey(loDefault.getClass().toString());
         addAdapter(loDefault, moDefaultHashKey);
      }
   }

   /**
   * Returns gui adapter for specified gui composite.
   * @return IGuiAdapter
   */
   public IGuiAdapter getAdapter(IGuiComposite aoGuiComposite)
                          throws CInvalidNameException
   {
      IGuiAdapter loFound;

      loFound = getAdapter(moDefaultHashKey, aoGuiComposite);

      if (loFound == null)
      {
         throw new CInvalidNameException(aoGuiComposite.getName());
      }
      else
      {
         return loFound;
      }
   }

   public IGuiAdapter getAdapter(String asName, IGuiComposite aoGuiComposite)
                          throws CInvalidNameException
   {
      IGuiAdapter loFound;

      loFound = getAdapter(new CHashKey(asName), aoGuiComposite);

      if (loFound == null)
      {
         throw new CInvalidNameException(asName);
      }
      else
      {
         return loFound;
      }
   }

   /**
    * Returns gui composite of specified key name.
    * If the key name relates to a registered class, a new <code>IGuiComposite</code> will be created,
    * otherwise the registered pre-created object will be returned.
    * @param asName key name of IGuiComposite
    * @return IGuiComposite
    * @throws CInvalidNameException
    */
   public IGuiComposite getGuiComposite(String asName)
                                 throws CInvalidNameException
   {
      IGuiComposite loFound = null;
      String lsClass;

      try
      {
         loFound = (IGuiComposite) moComposites.get(asName);

         if (loFound == null)
         {
            lsClass = (String) moClassNames.get(asName);

            if (lsClass != null)
            {
               loFound = (IGuiComposite) Class.forName(lsClass).newInstance();
               moComposites.put(asName, loFound);
            }
         }
      }
      catch (Exception ex)
      {
         throw new CInvalidNameException(ex, asName);
      }

      if (loFound == null)
      {
         throw new CInvalidNameException(asName);
      }

      return loFound;
   }

   public static String getWidgetNameKey()
   {
      return msNameKey;
   }

   /**
    * Adds specified IGuiAdapter.
    * @param aoAdapter
    * @param aoKey
    * @throws CDuplicateNameException
    */
   public void addAdapter(IGuiAdapter aoAdapter, CHashKey aoKey)
                   throws CDuplicateNameException
   {
      // Interface-Key schon vorhanden ? Das darf nicht sein
      //      if (moAdapter.containsKey(aoKey))
      //         throw new CDuplicateNameException(asName);
      // Interface registrieren
      moAdapter.put(aoKey, aoAdapter);
   }

   /**
    * Changes the default GUI adapter returned by <code>getAdapter</code> methods.
    * @param aoAdapter
    */
   public void setDefaultAdapter(IGuiAdapter aoAdapter)
   {
      moDefaultHashKey = new CHashKey(aoAdapter.getClass().toString());
      moAdapter.put(moDefaultHashKey, aoAdapter);
   }

   /**
    * Adds specified IGuiAdapter.
    * @param aoAdapter
    * @throws CDuplicateNameException
    */
   public void addAdapter(IGuiAdapter aoAdapter) throws CDuplicateNameException
   {
      addAdapter(aoAdapter, new CHashKey(aoAdapter.getClass().toString()));
   }

   /**
    * Registers gui composite class for later (lazy) creation.
    * No object of the specified class will be created before <code>getGuiComposite</code> is called.
    * @param asGuiClass class in full package notation
    * @param asName name to identify gui
    * @throws CDuplicateNameException
    */
   public void addGuiComposite(String asGuiClass, String asName)
                        throws CDuplicateNameException
   {
      if (moComposites.containsKey(asName) || moClassNames.containsKey(asName))
      {
         throw new CDuplicateNameException(asName);
      }

      moClassNames.put(asName, asGuiClass);

      moLogger.debug("gui added: class [" + asGuiClass + "] name [" + asName + "]");
   }

   /**
    * Registers <code>IGuiComposite</code> under specified name.
    * @param aoGuiComposite
    * @param asName
    * @throws CDuplicateNameException
    * @see #releaseGuiComposite
    */
   public void addGuiComposite(IGuiComposite aoGuiComposite, String asName)
                        throws CDuplicateNameException
   {
      // Composite-Key schon vorhanden ? Das darf nicht sein
      if (moComposites.containsKey(asName) || moClassNames.containsKey(asName))
      {
         throw new CDuplicateNameException(asName);
      }

      moComposites.put(asName, aoGuiComposite);

      moLogger.debug("gui added: composite [" + aoGuiComposite.getName() + "] name [" + asName +
                     "]");
   }

   /**
    * Scans all named widgets of specified <code>IGuiComposite</code>.
    * The <code>IGuiComposite</code> must have been registered previously.
    * Widgets are named if <code>JComponent.getName</code> does not return null.
    * Path naming means that widget names are built recursively throughout their component hierarchy.
    * For example, if a button "jbUpdate" is the child of a panel "jpOrder", path naming would
    * name the button "jpOrder.jbUpdate", otherwise it would be simply "jbUpdate".
    * Calling <code>addWidgetTree</code> is a necessity before using {@link voodoosoft.jroots.gui.IGuiComposite#getWidget}.
    * @param asGuiComposite name of registered <code>IGuiComposite</code>
    * @param abSkipRootName if true don't use name of root component for path naming
    * @param abPathNaming true to enable path naming
    * @throws CDuplicateNameException
    * @throws CInvalidNameException
    */
   public void addWidgetTree(String asGuiComposite, boolean abSkipRootName, boolean abPathNaming)
                      throws CDuplicateNameException, CInvalidNameException
   {
      String lsName = null;
      IGuiComposite loComposite;
      Component loRootComponent;

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("addWidgetTree");
      }

      loComposite = (IGuiComposite) getGuiComposite(asGuiComposite);

      if (loComposite == null)
      {
         throw new CInvalidNameException(asGuiComposite);
      }

      loRootComponent = loComposite.getRootComponent();

      if (!abSkipRootName)
      {
         if (loRootComponent instanceof JComponent)
         {
            //            lsName = (String) ((JComponent) loRootComponent).getClientProperty(msNameKey);
            lsName = (String) ((JComponent) loRootComponent).getName();
         }
         else
         {
            lsName = loRootComponent.getName();
         }
      }

      // jetzt rekursiv Absteigen
      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("path [" + lsName + "]");
      }

      addBranch(loComposite.getWidgetTable(), loRootComponent, lsName, lsName != null, abPathNaming);
   }

   /**
    * Default way to scan all named widgets of specified <code>IGuiComposite</code>.
    * The <code>IGuiComposite</code> must have been registered previously.
    * This method skips the root name, path naming depends upon the setting of this <code>CGuiManager</code>.
    * Calling <code>addWidgetTree</code> is a necessity before using {@link voodoosoft.jroots.gui.IGuiComposite#getWidget}.
    * @throws CDuplicateNameException
    * @throws CInvalidNameException
    * @see #CGuiManager(boolean, boolean)
    */
   public void addWidgetTree(String asGuiComposite)
                      throws CDuplicateNameException, CInvalidNameException
   {
      addWidgetTree(asGuiComposite, true, mbPathNaming);
   }

   /**
    * Removes all previous scanned widgets for the given <code>IGuiComposite</code>.
    * @param asGuiComposite
    * @throws CInvalidNameException
    * @see #addWidgetTree
    */
   public void clearWidgetTree(String asGuiComposite) throws CInvalidNameException
   {
      IGuiComposite loComposite;

      loComposite = (IGuiComposite) moComposites.get(asGuiComposite);

      // jetzt rekursiv Absteigen
      if (loComposite != null)
      {
         loComposite.getWidgetTable().clear();
      }
   }

   /**
    * Removes all created <code>IGuiComposite</code> objects of given key name.
    * @param asGuiComposite key name of IGuiComposite
    * @throws CInvalidNameException
    */
   public void releaseGuiComposite(String asGuiComposite)
                            throws CInvalidNameException
   {
      // von uns erzeugt ? -> dann auch wieder freigeben
      if (moClassNames.containsKey(asGuiComposite))
      {
         moComposites.remove(asGuiComposite);
      }
   }

   private IGuiAdapter getAdapter(CHashKey aoHashKey, IGuiComposite aoGuiComposite)
                           throws CInvalidNameException
   {
      IGuiAdapter loFound;
      IGuiAdapter loClone;

      // Interface-Prototypen finden ...
      loFound = (IGuiAdapter) moAdapter.get(aoHashKey);

      if (loFound == null)
      {
         return null;
      }

      // ... und clonen
      try
      {
         // TODO
         loClone = (IGuiAdapter) loFound.cloneObject();
         loClone.setGuiComposite(aoGuiComposite);
      }
      catch (CloneNotSupportedException ex)
      {
         // TODO
         loClone = new CDefaultGuiAdapter(aoGuiComposite);
         loClone.setGuiComposite(aoGuiComposite);
      }

      return loClone;
   }

   /*
   * P R I V A T E
   */
   private void addBranch(Map aoGuiTable, Component aoParentComponent, String asPath,
                          boolean abNamed, boolean abPathNaming)
                   throws CDuplicateNameException
   {
      Component[] loComponents;
      Container loContainer;
      JMenu loMenu;
      JComponent loJComponent;
      int li;
      Object loClientName;
      String lsName;

      if (aoParentComponent instanceof Container)
      {
         loContainer = (Container) aoParentComponent;

         if (aoParentComponent instanceof JMenu)
         {
            loMenu = (JMenu) aoParentComponent;
            loComponents = loMenu.getMenuComponents();
         }
         else
         {
            loComponents = loContainer.getComponents();
         }

         // durch alle Child-Components
         for (li = 0; li < loComponents.length; li++)
         {
            // Name des Widgets ermitteln
            lsName = loComponents[li].getName();

            // rekursiv absteigen
            if (lsName == null || lsName.equals(""))
            {
               addBranch(aoGuiTable, loComponents[li], asPath, false, abPathNaming);
            }
            else
            {
               if (asPath != null && abPathNaming)
               {
                  addBranch(aoGuiTable, loComponents[li], asPath + "." + lsName, true, abPathNaming);
               }
               else
               {
                  addBranch(aoGuiTable, loComponents[li], lsName, true, abPathNaming);
               }
            }
         }

         // Komponente benannt ?
         // -> dann eintragen (am Ende der Verwurzelung)
         if (abNamed)
         {
            if (aoGuiTable.containsKey(asPath))
            {
               throw new CDuplicateNameException(asPath);
            }
            else
            {
               //               aoGuiTable.put(asPath.toUpperCase(), loContainer);
               aoGuiTable.put(asPath, loContainer);

               if (moLogger.isDebugEnabled())
               {
                  moLogger.debug("container added: path[" + asPath + "]");
               }
            }
         }
      }
   }

   /** Property-Key zur Ermittlung des Widget-Namens*/
   private static String msNameKey = "Name";
   protected static Logger moLogger = Logger.getLogger(CGuiManager.class);

   /** Alle verwalteten GuiComposites */
   private Hashtable moComposites;

   /** GuiComposites-Klassen, Objekterzeugung immer neu bei Anforderung */
   private Hashtable moClassNames;

   /** Alle zur Verfügung stehenden Interfaces */
   private Hashtable moAdapter;
   private CHashKey moDefaultHashKey;
   private boolean mbPathNaming;
}
