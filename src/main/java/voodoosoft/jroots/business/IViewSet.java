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


package voodoosoft.jroots.business;


/**
 * Business service encapsulation for handling large read-only entity sets in smaller chunks.
 * @see voodoosoft.jroots.dialog.CViewSetTableModel
 */
public interface IViewSet extends IBasicEntitySet
{
   /**
    * Fetches next row chunk, if available.
    * @return true if there are more chunks available
    */
   public boolean fetchNextChunk();

   /**
    * Fetches previous row chunk, if available.
    * @return true if there are more chunks available
    */
   public boolean fetchPreviousChunk();

   /**
    * returns true if there is a following row chunk available.
    * @return true if there are more chunks available
    */
   public boolean hasNextChunk();

   /**
    * returns true if there is a previous row chunk available.
    * @return true if there are more chunks available
    */
   public boolean hasPreviousChunk();
}
