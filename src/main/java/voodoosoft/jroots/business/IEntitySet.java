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

import voodoosoft.jroots.data.CInvalidColumnException;
import voodoosoft.jroots.data.CInvalidRowNumberException;


/**
 * Interface for entity sets (object containings 0 - n rows).
 */
public interface IEntitySet extends IBasicEntitySet
{
   /**
    * Changes column value.
    * @param aiRow
    * @param columnName
    * @param columnValue
    * @throws CInvalidColumnException
    */
   public void setColumn(int aiRow, String columnName, Object columnValue)
                  throws CInvalidColumnException;

   /**
    * Deletes row of this <code>IEntitySet</code>.
    * @param aiRow
    * @throws CInvalidRowNumberException
    */
   public int deleteRow(int aiRow) throws CInvalidRowNumberException;

   /**
    * Inserts empty row into this <code>IEntitySet</code>.
    * @return number of inserted row.
    */
   public int insertRow();
}
