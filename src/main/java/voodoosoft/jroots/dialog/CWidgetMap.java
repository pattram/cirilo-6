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

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.exception.CException;
import voodoosoft.jroots.gui.CInvalidNameException;

import java.text.*;

import java.util.Hashtable;
import java.util.Vector;


/**
 * <code>CWidgetMap</code> simplifies the process of supplying GUI components with data
 * as well as moving user input to the corresponding business object properties.
 * Any class implementing the {@link IPropertyBag} interface may be used as data source,
 * respectively destination.
 * Widget maps consist of several single mappings (<code>CWidgetMapping</code>),
 * where one mapping defines the name of a property serving as data source,
 * the name of a GUI component and an optional data transformer.
 * @see voodoosoft.jroots.core.IPropertyBag
 * @see IGuiAdapter
 * @see CWidgetMapping
 * @see voodoosoft.jroots.core.ITransformer
 */
public class CWidgetMap extends CObject
{
   public CWidgetMap()
   {
      mo_mappings = new Vector();
   }

   public static Format getDisplayFormat(String as_key)
   {
      if (as_key != null)
      {
         return (Format) soDisplayFormats.get(as_key);
      }
      else
      {
         return null;
      }
   }

   public static void addDisplayFormat(String as_key, java.text.Format aoFormat)
   {
      soDisplayFormats.put(as_key, aoFormat);
   }

   public CWidgetMapping getMapping(int index)
   {
      if (index <= mo_mappings.size())
      {
         return (CWidgetMapping) mo_mappings.get(index);
      }
      else
      {
         return null;
      }
   }

   /**
    * Convenient method examining all property classes.
    * The CWidgetMap needs the class of a property during the process of accepting user input
    * in order to supply the IPropertyBag properties with correct typed values.
    * @see voodoosoft.jroots.core.IPropertyBag#getPropertyClass
    */
   public void setMappingTypes(IPropertyBag aoTypeSource)
                        throws CInvalidNameException, CInvalidPropertyException
   {
      CMapIterator loIt;
      CWidgetMapping loMapping;
      CPropertyClass loClass;
      boolean lbCancel = false;

      loIt = (CMapIterator) this.iterator();

      while (loIt.hasNext() && !lbCancel)
      {
         loMapping = (CWidgetMapping) loIt.next();

         try
         {
            loClass = aoTypeSource.getPropertyClass(loMapping.getSource());
         }
         catch (Exception ex)
         {
            CException.record(ex, this);
            loClass = null;
            lbCancel = true;
         }

         if (loMapping.getTypeClass() == null)
         {
            loMapping.setTypeClass(loClass);
         }
      }
   }

   public String getValidation(String as_Widget)
   {
      return null;
   }

   /**
    * Adds specified widget mapping.
    */
   public void addMapping(CWidgetMapping aoMapping)
   {
      mo_mappings.add(aoMapping);
   }

   public void addMapping(String as_Source, String as_Widget)
   {
      addMapping(as_Source, as_Widget, null, null);
   }

   public void addMapping(String as_Source, String as_Widget, ITransformer aiTrans)
   {
      addMapping(as_Source, as_Widget, aiTrans, null);
   }

   public void addMapping(String as_Source, String as_Widget, ITransformer aiTrans,
                          CPropertyClass aoClass)
   {
      CWidgetMapping lo_mapping = new CWidgetMapping(as_Source, as_Widget, aiTrans, aoClass);
      mo_mappings.add(lo_mapping);
   }

   public void addValidation(String as_Widget, String as_rule, String as_message)
   {
   }

   public Object iterator()
   {
      return new CMapIterator(this);
   }

   public int size()
   {
      return mo_mappings.size();
   }

   public boolean validate(String as_widget, String as_error)
   {
      return false;
   }

   protected Object gettingWidgetData(String asWidget, Object aoData)
   {
      return aoData;
   }

   // callback of GuiInterface.showData()
   protected Object settingWidgetData(String asWidget, Object aoData)
   {
      return aoData;
   }

   //   private ? mo_validationRules;
   static Hashtable soDisplayFormats = new Hashtable();
   private Vector mo_mappings;
}
