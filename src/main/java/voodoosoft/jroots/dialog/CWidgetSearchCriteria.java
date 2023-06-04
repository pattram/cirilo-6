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
import voodoosoft.jroots.data.CSearchCriteria;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Allows to directly feed <code>CSearchEngine</code> with search criteria out of <code>CWidgetMap</code> objects.
 */
public class CWidgetSearchCriteria extends CSearchCriteria implements IPropertyBag
{
   public CWidgetSearchCriteria(CWidgetMap aoMap)
   {
      CMapIterator loIt;
      CWidgetMapping loMapping;

      loWidgets = new Vector(aoMap.size());
      loCrit = new Hashtable(aoMap.size());
      loIt = (CMapIterator) aoMap.iterator();

      while (loIt.hasNext())
      {
         loMapping = (CWidgetMapping) loIt.next();
         addCriterion(loMapping.getSource());

         loWidgets.add(loMapping.getWidget());
         loCrit.put(loMapping.getWidget(), loMapping.getSource());
      }
   }

   public String getCritName(String asWidget)
   {
      return (String) loCrit.get(asWidget);
   }

   public boolean isEmpty(String asProperty) throws CInvalidPropertyException
   {
      return false;
   }

   public void setProperty(String asProperty, Object aoData)
                    throws CInvalidPropertyException, CPropertyReadonlyException
   {
      if (aoData != null)
      {
         setValue(asProperty, aoData);
      }
      else
      {
         setValue(asProperty, null);
      }
   }

   public Object getProperty(String asProperty) throws CInvalidPropertyException
   {
      return getValue(asProperty);
   }

   public CPropertyClass getPropertyClass(String asProperty)
                                   throws CInvalidPropertyException
   {
      Object loProp;

      loProp = getValue(asProperty);

      if (loProp != null)
      {
         return new CPropertyClass(loProp.getClass());
      }
      else
      {
         return null;
      }
   }

   public Vector getWidgets()
   {
      return loWidgets;
   }

   private Vector loWidgets;
   private Hashtable loCrit;
}
