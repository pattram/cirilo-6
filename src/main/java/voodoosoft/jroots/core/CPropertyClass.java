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


package voodoosoft.jroots.core;

import java.io.Serializable;


/**
 * CPropertyClass describes the type of a property using a Java class, precision and scale.
 */
public class CPropertyClass implements Serializable
{
   public CPropertyClass(Class aoClass)
   {
      javaClass = aoClass;
      precision = 0;
      scale = 0;
   }

   public CPropertyClass(Class aoClass, boolean abReadonly)
   {
      this(aoClass);
      readonly = abReadonly;
   }

   public CPropertyClass(Class aoClass, boolean abReadonly, int aiPrecision, int aiScale)
   {
      javaClass = aoClass;
      precision = aiPrecision;
      scale = aiScale;
      readonly = abReadonly;
   }

   public CPropertyClass(Class aoClass, boolean abReadonly, int aiPrecision, int aiScale,
                         int aiDisplaySize)
   {
      javaClass = aoClass;
      precision = aiPrecision;
      scale = aiScale;
      readonly = abReadonly;
      displaySize = aiDisplaySize;
   }

   public String toString()
   {
      return "CPropertyClass class[" + javaClass.getName() + "] precision[" +
             String.valueOf(precision) + ", " + String.valueOf(scale) + "] displaySize[" +
             String.valueOf(displaySize) + "] readonly[" + String.valueOf(readonly) + "]";
   }

   public Class javaClass;
   public int precision;
   public int scale;
   public int displaySize;
   public boolean readonly;
}
