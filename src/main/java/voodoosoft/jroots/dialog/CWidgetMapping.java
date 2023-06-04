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


/**
 * Single mapping linking one widget to one property of a property bag.
 * @see voodoosoft.jroots.dialog.CWidgetMap
 */
public class CWidgetMapping extends CObject
{
   public CWidgetMapping(String as_Source, String as_Widget, ITransformer aiTrans,
                         CPropertyClass aoClass)
   {
      msSource = as_Source;
      msWidget = as_Widget;
      moTransformer = aiTrans;
      moClass = aoClass;
   }

   public String getFormat()
   {
      return msFormat;
   }

   public String getSource()
   {
      return msSource;
   }

   public ITransformer getTransformer()
   {
      return moTransformer;
   }

   public void setTypeClass(CPropertyClass aoClass)
   {
      moClass = aoClass;
   }

   public CPropertyClass getTypeClass()
   {
      return moClass;
   }

   public String getWidget()
   {
      return msWidget;
   }

   private String msSource;
   private String msWidget;
   private CPropertyClass moClass;
   private String msFormat;
   private ITransformer moTransformer;
}
