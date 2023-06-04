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
 * <code>ISQLStorage</code> defines the interface for SQL storage classes.
 * @see CSQLEngine
 */
public interface ISQLStorage
{
   /**
    * Changes specified SQL in the storage.
    * @param asSQL
    * @param asName
    * @param aiType
    */
   public void setSQL(String asSQL, String asName, int aiType);

   /**
    *
    * @param asName name to uniqely identify SQL statement
    * @param aiType type of statement like <code>SELECT</code>, <code>INSERT</code> or <code>UPDATE</code>
    * @return SQL statement
    * @throws CSQLNotFoundException
    */
   public String getSQL(String asName, int aiType) throws CSQLNotFoundException;

   /**
    * Makes storage content accessible.
    */
   public void load() throws CSQLNotFoundException;

   /**
    * Persists storage.
    */
   public void save();
}
