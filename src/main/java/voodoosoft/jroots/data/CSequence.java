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

import java.sql.*;


/**
 * Default <code>ISequence</code> implementation using one database table as sequence storage.
 * <p><code>CSequence</code> is thread safe.
 */
public class CSequence implements ISequence
{
   public CSequence(String asTable, String asNameColumn, String asValueColumn)
             throws CConnectionNotAvailableException, CSQLException
   {
      this(asTable, asNameColumn, asValueColumn, false);
   }

   /**
    * Creates sequence supplier for the specified database table.
    * <p>Before the construction of <code>CSequence</code> objects, it ist necessary
    * to register a valid connection at <code>CConnection</code> of name <code>SEQUENCE_CONNECTION</code>.
    * This connection will be used for every later sequence request as well.
    * @param asTable table where sequence values are stored
    * @param asNameColumn column holding all available sequence names
    * @param asValueColumn column of all sequence values
    * @throws CConnectionNotAvailableException
    * @throws CSQLException
    * @see voodoosoft.jroots.data.CConnection#addConnection
    */
   public CSequence(String asTable, String asNameColumn, String asValueColumn, boolean abAutoCommit)
             throws CConnectionNotAvailableException, CSQLException
   {
      CConnection loConn;
      Integer liHandle;

      mbAutoCommit = abAutoCommit;

      msSQL = "SELECT " + asValueColumn + " FROM " + asTable + " WHERE " + asNameColumn + " = ?";

      msUpdateSQL = "UPDATE " + asTable + " SET " + asValueColumn + " = " + asValueColumn + " + 1" +
                    " WHERE " + asNameColumn + " = ?";

      loConn = CConnection.getConnection(SEQUENCE_CONNECTION);

      if (loConn.isHolding())
      {
         setupSQL(loConn);
      }
   }

   /**
    * Gets next sequence value without commiting.
    * @param asSequence
    * @return sequence value
    * @throws CSequenceLookupException
    */
   public int nextNo(String asSequence) throws CSequenceLookupException
   {
      return nextNo(asSequence, mbAutoCommit);
   }

   /**
    * Gets next value from the sequence database table, increments and returns it.
    * <code>nextNo</code> will use the <code>CConnection</code> object
    * registered by name <code>SEQUENCE_CONNECTION</code>.
    *
    * @param asSequence name of sequence
    * @param abCommit if true, the <code>SEQUENCE_CONNECTION</code> will be commited
    * @return sequence value
    * @throws CSequenceLookupException
    */
   public int nextNo(String asSequence, boolean abCommit)
              throws CSequenceLookupException
   {
      ResultSet loResult;
      boolean lbFound;
      CConnection loConn = null;
      int loNextNo = -1;
      Integer liHandle = null;

      try
      {
         loConn = CConnection.getConnection(SEQUENCE_CONNECTION);

         if (!loConn.isHolding())
         {
            setupSQL(loConn);
         }

         liHandle = loConn.lockConnection();

         synchronized (this)
         {
            moSQL.setString(1, asSequence);
            loResult = moSQL.executeQuery();

            lbFound = loResult.next();

            if (lbFound)
            {
               moUpdateSQL.setString(1, asSequence);
               moUpdateSQL.executeUpdate();

               loNextNo = loResult.getInt(1);
               loNextNo++;
            }
            else
            {
               loNextNo = -1;
            }

            loResult.close();

            if (abCommit)
            {
               loConn.commit();
            }
         }
      }
      catch (Exception ex)
      {
         throw new CSequenceLookupException(ex, asSequence);
      }
      finally
      {
         if (loConn != null)
         {
            loConn.releaseConnection(liHandle);
         }
      }

      if (loNextNo < 0)
      {
         throw new CSequenceLookupException(asSequence);
      }

      return loNextNo;
   }

   public Object nextObject(String asSequence) throws CSequenceLookupException
   {
      return new Integer(nextNo(asSequence));
   }

   public Object nextObject(String asSequence, boolean abCommit)
                     throws CSequenceLookupException
   {
      return new Integer(nextNo(asSequence, abCommit));
   }

   private void setupSQL(CConnection aoConn) throws CSQLException, CConnectionNotAvailableException
   {
      String lsSQL = null;

      try
      {
         lsSQL = msSQL;
         moSQL = aoConn.getConnection().prepareStatement(lsSQL);
         lsSQL = msUpdateSQL;
         moUpdateSQL = aoConn.getConnection().prepareStatement(lsSQL);
      }
      catch (SQLException ex)
      {
         throw new CSQLException(ex, lsSQL);
      }
   }

   /**
    * Name of the used <code>CConnection</code>.
    */
   public static final String SEQUENCE_CONNECTION = "Sequences";
   private PreparedStatement moSQL;
   private PreparedStatement moUpdateSQL;
   private String msSQL;
   private String msUpdateSQL;
   private boolean mbAutoCommit;
}
