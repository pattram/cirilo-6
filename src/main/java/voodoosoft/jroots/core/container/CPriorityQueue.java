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


package voodoosoft.jroots.core.container;

import java.lang.Integer;

import java.util.Vector;


/**
 * Implementation of a priority queue.
 */
public class CPriorityQueue extends CContainer
{
   //
   // public
   //
   public CPriorityQueue()
   {
      moItems = new Vector();
      moPriorities = new Vector();
   }

   public boolean isEmpty()
   {
      return moItems.isEmpty();
   }

   /** Entfernt alle Objekte aus der Warteschlange */
   public synchronized void clear()
   {
      moItems.removeAllElements();
      moPriorities.removeAllElements();
   }

   /** Liefert einer Kopie der Queue zurück */
   public synchronized Object clone()
   {
      CPriorityQueue lClone = new CPriorityQueue();

      lClone.moItems = (Vector) moItems.clone();
      lClone.moPriorities = (Vector) moPriorities.clone();

      return lClone;
   }

   /** Liefert das vorderste Element (mit der höchsten Priorität) zurück
       und entfernt es aus der Warteschlange */
   public synchronized Object pop()
   {
      int liPos;
      Object lItem;

      liPos = findTop();

      if (liPos >= 0)
      {
         lItem = moItems.elementAt(liPos);
         moItems.removeElementAt(liPos);
         moPriorities.removeElementAt(liPos);
      }
      else
      {
         lItem = null;
      }

      return lItem;
   }

   /** Fgt das Element mit einer Priorität von 0 hinzu */
   public synchronized void push(Object item)
   {
      push(item, new Integer(0));
   }

   /** Fgt ein Element an und ordnet es entsprechend der angegebenen Priorität ein */
   public synchronized void push(Object item, Integer priority)
   {
      moItems.addElement(item);
      moPriorities.addElement(priority);
   }

   /** Liefert das vorderste Element (mit der höchsten Priorität) zurück.
      (Das Element wird nicht entfernt) */
   public synchronized Object top()
   {
      int liPos = findTop();

      if (liPos >= 0)
      {
         return moItems.elementAt(liPos);
      }
      else
      {
         return null;
      }
   }

   //
   // private
   //
   private int findTop()
   {
      Integer lPriority = new Integer(-1);
      int liPos = -1;

      for (int i = 0; i < moPriorities.size(); i++)
      {
         if (lPriority.intValue() < ((Integer) moPriorities.elementAt(i)).intValue())
         {
            lPriority = (Integer) moPriorities.elementAt(i);
            liPos = i;
         }
      }

      return liPos;
   }

   private Vector moItems;
   private Vector moPriorities;
}
