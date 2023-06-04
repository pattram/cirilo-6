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

import voodoosoft.jroots.core.CPropertyClass;
import voodoosoft.jroots.data.CInvalidColumnException;


/**
 * Base interface for entity sets.
 */
public interface IBasicEntitySet extends IBusinessService
{
   /**
    * Gets value of specified row and column.
    * @param aiRow
    * @param colunmIndex
    * @return column value
    * @throws CInvalidColumnException
    */
   public Object getColumn(int aiRow, int colunmIndex)
                    throws CInvalidColumnException;

   /**
    * Gets value of specified row and column.
    * @param aiRow
    * @param colunmName
    * @return column value
    * @throws CInvalidColumnException
    */
   public Object getColumn(int aiRow, String colunmName)
                    throws CInvalidColumnException;

   /**
    * Returns <code>CPropertyClass</code> identifying the specified column.
    * @param aiColumn
    * @return property info of this <code>CPropertyClass</code>
    */
   public CPropertyClass getColumnClass(int aiColumn);

   /**
    * Gets number of columns of this <code>IBasicEntitySet</code>.
    * @return column count
    */
   public int getColumnCount();

   /**
    * Returns label of specified column.
    * @param aiColumn
    * @return column label
    */
   public String getColumnLabel(int aiColumn);

   /**
    * Gets number of rows of this <code>IBasicEntitySet</code>.
    * @return row number
    */
   public int getRowCount();
}
