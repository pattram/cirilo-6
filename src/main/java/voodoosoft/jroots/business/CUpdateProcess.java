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

import java.util.ArrayList;

/**
 * <code>CUpdateProcess</code> updates several <code>IEntity</code> objects in one database transaction. 
 */
public class CUpdateProcess implements IBusinessProcess
{
   /**
    * Creates new empty <code>CUpdateProcess</code>.    
    */
   public CUpdateProcess()
   {
      moUpdates = new ArrayList();
   }

   /**
    * Adds entity for updating by this <code>CUpdateProcess</code>.
    * @param aoUpdate
    */
   public void addUpdate(IEntity aoUpdate)
   {
      moUpdates.add(aoUpdate);
   }

   /**
    * Updates all entities of this <code>CUpdateProcess</code> and eventually commits the given connection.
    */
   public void execute(CConnection aoConn) throws CExecuteFailedException
   {
      IEntity loUpdate;

      try
      {
         //         aoConn.beginTransaction();
         for (int i = 0; i < moUpdates.size(); i++)
         {
            loUpdate = (IEntity) moUpdates.get(i);
            loUpdate.update(aoConn);
         }

         //         aoConn.commitTransaction();
         aoConn.commit();
      }
      catch (Exception ex)
      {
         if (aoConn != null)
         {
            aoConn.rollback();
         }
         throw new CExecuteFailedException(ex, "execute process failed");
      }
   }

   private ArrayList moUpdates;
}
