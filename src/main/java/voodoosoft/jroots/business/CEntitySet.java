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

import java.util.Vector;


/**
 * Class for representing multiple rows of a business entity, instead of the one-to-one relation of
 * {@link CDefaultEntity} objects.
 * <code>CEntitySet</code> uses one object of type <code>IDataStore</code> as data source.
 */
public class CEntitySet extends CAbstractService implements IEntitySet
{
   public CEntitySet()
   {
   }

   /**
    * Creates <code>CEntitySet</code> using the specified <code>IDataStore</code> as underlying data source.
    * @param aoData
    */
   public CEntitySet(IDataStore aoData)
   {
      setDataStore(aoData);
   }

   /**
    * Creates <code>CEntitySet</code> with underlying <code>IDataStore</code> as data source.
    * The internal <code>IDataStore</code> is created by the specified <code>IDataStoreFactory</code>
    * of class <code>CEntitySet</code>.
    * @param asSQL SLQ for data retrieval
    * @param abPrepared if true, SQL contains retrieval argument placeholders
    * @see #setDataStoreFactory
    */
   public CEntitySet(String asSQL, boolean abPrepared)
   {
      IDataStore loDS = null;

      try
      {
         loDS = (IDataStore) soDSFactory.createObject();
         loDS.setSQL(asSQL, abPrepared);

         setDataStore(loDS);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Specifies the <code>IDataStoreFactory</code> used in the <code>CEntitySet</code> constructor.
    * @param aoFactory
    */
   public static void setDataStoreFactory(IDataStoreFactory aoFactory)
   {
      soDSFactory = aoFactory;
   }
   
   public static IDataStoreFactory getDataStoreFactory() 
   {
      return soDSFactory;
   }
      
   public void setColumn(int aiRow, String columnName, Object columnValue)
                  throws CInvalidColumnException
   {
      moData.setColumn(aiRow, columnName, columnValue);
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

   /**
    * Returns underlying <code>IDataStore</code> this <code>CEntitySet</code> is based upon.
    * @return internal datastore
    */
   public IDataStore getDataStore()
   {
      return moData;
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
    * if this <code>CEntitySet</code> has valid <code>IPrimaryKey</code>.
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
    * Adds listener to be notified when rows are deleted or inserted.
    * @param aoListener
    */
   public void addRowListener(IEntitySetListener aoListener)
   {
      if (moRowListener == null)
      {
         moRowListener = new Vector();
      }

      moRowListener.add(aoListener);
   }

   public int deleteRow(int aiRow) throws CInvalidRowNumberException
   {
      int liRet;

      liRet = moData.deleteRow(aiRow);

      if (moRowListener != null)
      {
         for (int i = 0; i < moRowListener.size(); i++)
         {
            ((IEntitySetListener) moRowListener.get(i)).rowChanged(aiRow, ROW_DELETED);
         }
      }

      return liRet;
   }

   public int insertRow()
   {
      int liRow;

      liRow = moData.insertRow();

      if (moRowListener != null)
      {
         for (int i = 0; i < moRowListener.size(); i++)
         {
            ((IEntitySetListener) moRowListener.get(i)).rowChanged(liRow, ROW_INSERTED);
         }
      }

      return liRow;
   }

   /**
    * Resets internal data store
    * @see voodoosoft.jroots.data.IDataStore#reset
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
    * Empties list of row listener.    
    */
   public void removeRowListener()
   {
      if (moRowListener != null)
      {
         moRowListener.clear();
      }
   }

   /**
    * Removes specified listener.
    * @param aoListener
    */
   public void removeRowListener(IEntitySetListener aoListener)
   {
      if (moRowListener != null)
      {
         moRowListener.remove(aoListener);
      }
   }

   public void retrieve(CConnection aoConn) throws CRetrieveFailedException
   {
      try
      {
         /** @todo */
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

   /**
    * Updates internal <code>IDataStore</code>
    * @param aoConn
    * @see voodoosoft.jroots.data.IDataStore#update
    * @throws CUpdateFailedException
    */
   public void update(CConnection aoConn) throws CUpdateFailedException
   {
      try
      {
         moData.update(aoConn);
      }
      catch (voodoosoft.jroots.data.CUpdateFailedException ex)
      {
         throw new voodoosoft.jroots.business.CUpdateFailedException(ex);
      }
   }

   /**
    * Changes underlying <code>IDataStore</code> of this <code>CEntitySet</code>.
    * @param aoData
    */
   protected void setDataStore(IDataStore aoData)
   {
      moData = aoData;
   }

   /**
    * event type for inserted rows.
    */
   public static final int ROW_INSERTED = 1;

   /**
    * event type for deleted rows.
    */
   public static final int ROW_DELETED = 2;

   /**
    * event type for updated rows.
    */
   public static final int ROW_UPDATED = 3;
   
   private static IDataStoreFactory soDSFactory = new CDataStoreFactory();
   private IDataStore moData;
   private IPrimaryKey moKey;
   private Vector moRowListener = null;
}
