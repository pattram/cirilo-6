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

import voodoosoft.jroots.core.data.CDBTools;
import voodoosoft.jroots.data.CConnection;

import java.sql.*;


/**
 * Business service of execution of SQL statements.
 */
public class CStatementService extends CAbstractService implements IBusinessService
{
   public CStatementService()
   {
   }

   public void setKey(IPrimaryKey aoKey)
   {
      moKey = aoKey;
   }

   public IPrimaryKey getKey()
   {
      return moKey;
   }

   public void setStatement(String asSQL)
   {
      msSQL = asSQL;
   }

   public void activate(CConnection aoConn) throws CActivateFailedException
   {
      Object[] loArgs;

      try
      {
         moSQL = aoConn.getConnection().prepareStatement(msSQL);

         loArgs = moKey.getKeyValues();

         CDBTools.setArguments(moSQL, loArgs);
      }
      catch (Exception ex)
      {
         throw new CActivateFailedException(ex);
      }
   }

   public void execute() throws CExecuteFailedException
   {
      try
      {
         moSQL.execute();
      }
      catch (Exception ex)
      {
         throw new CExecuteFailedException(ex, msSQL);
      }
   }

   public void passivate() throws CPassivateFailedException
   {
      moSQL = null;
   }

   public void postCreate()
   {
   }

   private IPrimaryKey moKey;
   String msSQL;
   PreparedStatement moSQL;
}
