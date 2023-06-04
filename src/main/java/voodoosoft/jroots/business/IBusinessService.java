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

import voodoosoft.jroots.data.CConnection;

import java.io.Serializable;


/**
 * Base interface for all kinds of business services, e.g. entities, validation rules or business processes.
 * Each business service is identified through its unique primary key.
 * @see IPrimaryKey
 */
public interface IBusinessService extends Serializable
{
   /** Sets primary key */
   public void setKey(IPrimaryKey aoKey);

   /** Gets primary key */
   public IPrimaryKey getKey();

   /** Sets application-unique identifier. */
   public void setObjectID(Integer aiID);

   /** Returns unique identifier of this <code>IBusinessService</code>.*/
   public Integer getObjectID();

   /** Called whenever the business factory activates or creates business service objects.
    *  @see CBusinessFactory#getObject
    */
   public void activate(CConnection aoConn) throws CActivateFailedException;

   /** The business factory calls this method during the process of deactivating service objects.
    *  Place to free memory etc.
    * @see  CBusinessFactory#releaseObject
    */
   public void passivate() throws CPassivateFailedException;

   /** Called after the business factory created this <code>IBusinessService</code>.*/
   public void postCreate();
}
