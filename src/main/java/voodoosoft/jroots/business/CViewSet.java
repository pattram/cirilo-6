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
import voodoosoft.jroots.data.*;
import voodoosoft.jroots.exception.CException;


/**
 * Class for handling large rowsets of business entities in smaller chunks of data.
 * <p>Default implementation of <code>IViewSet</code>.
 */
public class CViewSet extends CAbstractService implements IViewSet
{
   /**
    * Creates new <code>CViewSet</code> with a default chunk size of 100 rows.
    * @param asSQL valid SQL
    * @param abPrepared if true, use <code>PreparedStatement</code>
    */
   public CViewSet(String asSQL, boolean abPrepared)
   {
      this(asSQL, abPrepared, 100);
   }

   /**
    * Creates new <code>CViewSet</code>.
    * @param asSQL valid SQL
    * @param abPrepared if true, use <code>PreparedStatement</code>
    * @param aiChunkSize number of rows to hold
    */
   public CViewSet(String asSQL, boolean abPrepared, int aiChunkSize)
   {
      this(asSQL, abPrepared, aiChunkSize, false);
   }

   public CViewSet(CViewStore aoStore)
   {
      moData = aoStore;
   }

   /**
    * Creates new <code>CViewSet</code>.
    * @param asSQL valid SQL
    * @param abPrepared if true, use <code>PreparedStatement</code>
    * @param aiChunkSize number of rows to hold
    * @param abUpperCaseNaming if true, use uppercase column names
    */
   public CViewSet(String asSQL, boolean abPrepared, int aiChunkSize, boolean abUpperCaseNaming)
   {
      CViewStore loDS = null;

      try
      {
         loDS = new CViewStore(asSQL, abPrepared);
         loDS.setChunkSize(aiChunkSize);
         loDS.setUpperCaseNaming(abUpperCaseNaming);
         moData = loDS;
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Sets number of rows the underlying <code>IViewSet</code> caches.
    * @param aiChunkSize
    */
   public void setChunkSize(int aiChunkSize)
   {
      if (moData != null)
      {
         moData.setChunkSize(aiChunkSize);
      }
   }

   public Object getColumn(int aiRow, String colunmName)
                    throws CInvalidColumnException
   {
      return moData.getColumn(aiRow, colunmName);
   }

   public Object getColumn(int aiRow, int colunmIndex)
                    throws CInvalidColumnException
   {
      return moData.getColumn(aiRow, colunmIndex);
   }

   public CPropertyClass getColumnClass(int aiColumn)
   {
      return moData.getColumnClass(moData.getColumnName(aiColumn));
   }

   public int getColumnCount()
   {
      return moData.getColumnCount();
   }

   public String getColumnLabel(int aiColumn)
   {
      return moData.getColumnName(aiColumn);
   }

   public void setKey(IPrimaryKey aoKey)
   {
      moKey = aoKey;
   }

   public IPrimaryKey getKey()
   {
      return moKey;
   }

   public int getRowCount()
   {
      return moData.getRowCount();
   }

   /**
    * <code>activate</code> will initiate data retrieval by calling <code>retrieve</code> -
    * if this <code>CViewSet</code> has valid <code>IPrimaryKey</code>.
    * @param aoConn
    * @throws CActivateFailedException
    */
   public void activate(CConnection aoConn) throws CActivateFailedException
   {
      try
      {
         if (getKey() != null)
         {
            retrieve(aoConn);
         }
      }
      catch (CRetrieveFailedException ex)
      {
         throw new CActivateFailedException(ex);
      }
   }

   /**
    * Fetches the next row chunk.
    * @return true if there are more following chunks to get.
    * @see #setChunkSize
    */
   public boolean fetchNextChunk()
   {
      boolean lbMoreChunks = false;

      try
      {
         lbMoreChunks = moData.fetchNextChunk();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return lbMoreChunks;
   }

   /**
    * Fetches the previous row chunk.
    * @return true if there are more preceding chunks to get.
    * @see #setChunkSize
    */
   public boolean fetchPreviousChunk()
   {
      boolean lbMoreChunks = false;

      try
      {
         lbMoreChunks = moData.fetchPreviousChunk();
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return lbMoreChunks;
   }

   /**
    * Returns true if there are more rows to fetch with <code>fetchNextChunk</code>.
    * @return chunk state
    */
   public boolean hasNextChunk()
   {
      return moData.hasNextChunk();
   }

   /**
    * Returns true if there is a previous chunk of data available via <code>fetchPreviousChunk</code>.
    * @return chunk state
    */
   public boolean hasPreviousChunk()
   {
      return moData.hasPreviousChunk();
   }

   /**
    * Resets internal view store.
    * @see voodoosoft.jroots.data.CViewStore#reset
    * @throws CPassivateFailedException
    */
   public void passivate() throws CPassivateFailedException
   {
      moData.reset();
   }

   public void postCreate()
   {
   }

   /**
    * Retrieves data using the internal primary key.
    * <p>Called every time this <code>CViewSet</code> gets activated
    * <p><code>retrieve</code> invokes <code>setParameters</code> and <code>retrieve</code>
    * of the internal <code>IViewStore</code>.
    * @see #activate
    */
   public void retrieve(CConnection aoConn) throws CRetrieveFailedException
   {
      try
      {
         if (moData.usesPreparedSQL())
         {
            moData.setParameters(moKey.getKeyValues());
         }

         moData.retrieve(aoConn);
      }
      catch (Exception ex)
      {
         throw new CRetrieveFailedException(ex);
      }
   }

   private CViewStore moData;
   private IPrimaryKey moKey;
}
