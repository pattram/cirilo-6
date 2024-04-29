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


package voodoosoft.jroots.data;


/**
 * <code>IBasicDataStore</code> defines fundamental data store methods.
 */
public interface IBasicDataStore
{
   /**
    * Gets the value of a column in the specified row as Java object.
    * @param aiRow
    * @param columnName
   */
   public Object getColumn(int aiRow, String columnName)
                    throws CInvalidColumnException;

   /**
     * Gets the value of a column in the specified row as Java object.
     * @param aiRow
     * @param colunmIndex
   */
   public Object getColumn(int aiRow, int colunmIndex)
                    throws CInvalidColumnException;

   /**
    * Returns column number of this DataStore.
   */
   public int getColumnCount();

   /**
    * Returns number of rows.
    */
   public int getRowCount();

   /**
    * Asks this <code>IBasicDataStore</code> to stop data retrieval.
    * @throws CSQLException
    */
   public void cancel() throws CSQLException;

   /**
    * Retrieves data using the current SQL and connection.
    * @return number of rows retrieved
   */
   public int retrieve(CConnection aoCon) throws CSQLException;
}
