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

import java.io.Serializable;


/**
 * Interface for objects serving as unique keys for business objects.
 * Every key may consist of several attributes,
 * e.g. identifying columns of a database table.
 */
public interface IPrimaryKey extends Serializable
{
   /** Sets key attribute. */
   public void setAttribute(String asAttr, Object aoValue)
                     throws CInvalidAttributeException;

   /** Gets key attribute */
   public Object getAttribute(String asAttr) throws CInvalidAttributeException;

   /** Sets entity of this key. */
   public void setEntityName(String asName);

   /** Gets entity of this key. */
   public String getEntityName();

   /** Gets values of all attributes. */
   public Object[] getKeyValues();

   /**
    * Marks this key as object for new entities.
    * @param abNewEntityKey
    */
   public void setNewEntityKey(boolean abNewEntityKey);

   /**
    * Returns true if this key describes new entities.
    * @return new flag
    */
   public boolean isNewEntityKey();

   /** Compares this key with the specified key for equality. */
   public boolean equals(Object aoCompare);
}
