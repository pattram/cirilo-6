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

import java.util.Vector;


/**
 * Default dialog key implementation.
 */
public class CDefaultDialogKey extends CObject implements IDialogKey
{
   public CDefaultDialogKey()
   {
      moKeys = new Vector();
      moValues = new Vector();
      moGroupKeys = new Vector();
   }

   /**
    * Sets one key attribute to the given value.
    * @param asAttr attribute to set
    * @param aoValue attribute value
    */
   public void setAttribute(String asAttr, Object aoValue)
   {
      int liIdx;

      liIdx = moKeys.indexOf(asAttr);

      if (liIdx == -1)
      {
         throw new RuntimeException("invalid attribute: " + asAttr);
      }

      moValues.set(liIdx, aoValue);
   }

   public Object getAttribute(String asAttr)
   {
      int liIdx;

      liIdx = moKeys.indexOf(asAttr);

      if (liIdx != -1)
      {
         return moValues.elementAt(liIdx);
      }
      else
      {
         return null;
      }
   }

   /**
    * Adds a new attribute to this key.
    * @param aoAttribute new attribute
    * @param abGroupAttribute if true, use attribute to find key groups
    */
   public void addAttrib(Object aoAttribute, boolean abGroupAttribute)
   {
      moKeys.add(aoAttribute);
      moValues.add(null);

      if (abGroupAttribute)
      {
         moGroupKeys.add(aoAttribute);
      }
      else
      {
         moGroupKeys.add(null);
      }
   }

   public boolean equalsGroup(CDefaultDialogKey otherKey)
   {
      boolean lbEqual;

      if (this == otherKey)
      {
         return true;
      }

      if (this.moKeys.size() != otherKey.moKeys.size())
      {
         return false;
      }

      if (this.moGroupKeys.size() != otherKey.moGroupKeys.size())
      {
         return false;
      }

      lbEqual = true;

      for (int i = 0; i < moGroupKeys.size(); i++)
      {
         if (moGroupKeys.elementAt(i) != null)
         {
            if ((!moKeys.elementAt(i).equals(otherKey.moKeys.elementAt(i))) ||
                   (!moValues.elementAt(i).equals(otherKey.moValues.elementAt(i))))
            {
               lbEqual = false;

               break;
            }
         }
      }

      return lbEqual;
   }

   private Vector moKeys;
   private Vector moValues;
   private Vector moGroupKeys;
}
