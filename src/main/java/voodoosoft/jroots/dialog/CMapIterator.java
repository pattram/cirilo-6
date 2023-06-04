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

import java.util.*;


/**
 * Class to iterate through lists of {@link CWidgetMapping} objects.
 */
public class CMapIterator extends CObject implements Iterator
{
   public CMapIterator(CWidgetMap ao_map)
   {
      mo_map = ao_map;
      mi_next = 0;
   }

   public boolean hasNext()
   {
      if (mi_next < mo_map.size())
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   public Object next() throws NoSuchElementException
   {
      CWidgetMapping nm;

      nm = mo_map.getMapping(mi_next);

      if (nm == null)
      {
         throw new NoSuchElementException();
      }
      else
      {
         mi_next++;

         return nm;
      }
   }

   public void remove() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   private CWidgetMap mo_map;
   private int mi_next;
}
