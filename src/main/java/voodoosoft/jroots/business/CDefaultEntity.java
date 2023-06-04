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

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.data.*;

import java.sql.SQLException;

import java.util.HashSet;
import java.util.Hashtable;


/**
 * Default <code>IEntity</code> implementation using a <code>IDataStore</code> object as underlying storage.
 * <p>Objects of class <code>CDefaultEntity</code> can be managed by the <code>CBusinessFactory</code>.
 * <p>
 * @see voodoosoft.jroots.data.IDataStore
 * @see CBusinessFactory
 */
public class CDefaultEntity extends CAbstractService implements IEntity
{
   /**
    * Creates new entity without internal <code>IDataStore</code>.
    */
   public CDefaultEntity()
   {
      moPropClasses = new Hashtable();
      moGetProps = new HashSet();
      moSetProps = new HashSet();

      moKey = new CDefaultPKey("");
   }

   /**
    * Creates new entity with internal <code>IDataStore</code> of class <code>CDataStore</code>.
    * <p>
    * @param asSQL SQL for <code>CDataStore</code> construction
    * @param abPrepared if true, use prepared statement
    * @throws Exception
    */
   public CDefaultEntity(String asSQL, boolean abPrepared)
                  throws Exception
   {
      this();

      IDataStore loStore;

      if (soDSFactory == null)
      {
         loStore = new CDataStore();
      }
      else
      {
         loStore = (IDataStore) soDSFactory.createObject();
      }

      loStore.setSQL(asSQL, abPrepared);

      setDataStore(loStore);
   }

   /**
    * Sets internal <code>IDataStoreFactory</code>.
    * @param aoFactory
    */
   public static void setDataStoreFactory(IDataStoreFactory aoFactory)
   {
      soDSFactory = aoFactory;
   }

   /**
    * Sets the internal IDataStore used for persistence.
    */
   public void setDataStore(IDataStore aoDataStore)
   {
      moDataStore = aoDataStore;
   }

   /**
    * Gets the internal IDataStore used for persistence.
    */
   public IDataStore getDataStore()
   {
      return moDataStore;
   }

   /**
    * Returns true if this entity has been marked as deleted.
    * @return deleted flag
    * @see #delete
    */
   public boolean isDeleted()
   {
      return mbDeleted;
   }

   /**
    * Returns true if property equals null.
    * @param asProperty
    * @return empty flag
    * @throws CInvalidPropertyException
    */
   public boolean isEmpty(String asProperty) throws CInvalidPropertyException
   {
      Object loValue = getProperty(asProperty);

      return (loValue == null);
   }

   /**
    * Sets internal key object.
    * <p>This has no effect before <code>activate</code> or <code>retrieve</code> is called.
    * @param aoKey
    */
   public void setKey(IPrimaryKey aoKey)
   {
      moKey = aoKey;
   }

   /**
    * Returns internal key object.
    * @return primary key
    */
   public IPrimaryKey getKey()
   {
      return moKey;
   }

   /**
    * Returns true if this entity has been modified (properties set), but not updated.
    */
   public boolean isModified()
   {
      return moDataStore.isModified();
   }

   /**
    * Sets persist name which corresponds with the datastores "update table".
    * @see voodoosoft.jroots.data.IDataStore#setUpdateTable
    * @see #update
    */
   public void setPersistName(String asName)
   {
      msPersistName = asName;

      if (moDataStore != null)
      {
         moDataStore.setUpdateTable(asName);
      }
   }

   /**
    * Gets persist name which corresponds with the datastores "update table".
    * @see voodoosoft.jroots.data.IDataStore#getUpdateTable
    */
   public String getPersistName()
   {
      return msPersistName;
   }

   /**
    * Sets the specified property to the given value.
    * <p>If there exists a binding for the property, the bound method is called to set the value,
    * otherwise the request is delegated to the internal <code>IDataStore</code>.
    * @see #bindProperty
    */
   public void setProperty(String asProperty, Object aoData)
                    throws CInvalidPropertyException, CPropertyReadonlyException
   {
      Boolean lbBound;
      CPropertyClass loPropClass;

      try
      {
         loPropClass = (CPropertyClass) moPropClasses.get(asProperty);

         if (loPropClass != null && loPropClass.readonly)
         {
            throw new CPropertyReadonlyException(asProperty, this);
         }

         if (moSetProps.contains(asProperty))
         {
            moPropBinder.setProperty(this, asProperty, aoData);
         }
         else
         {
            moDataStore.setColumn(1, asProperty, aoData);
         }
      }
      catch (CInvalidPropertyException ex)
      {
         throw ex;
      }
      catch (Exception ex)
      {
         throw new CInvalidPropertyException(asProperty, this, ex);
      }
   }

   /**
    * Returns property value.
    * <p>If there exists a binding for the property, the bound method is called to get the value,
    * otherwise the request is delegated to the internal <code>IDataStore</code>.
    * @see #bindProperty
    */
   public Object getProperty(String asProperty) throws CInvalidPropertyException
   {
      Object loValue = null;
      Boolean lbBound;

      try
      {
         if (moGetProps.contains(asProperty))
         {
            loValue = moPropBinder.getProperty(this, asProperty);
         }
         else
         {
            loValue = moDataStore.getColumn(1, asProperty);
         }
      }
      catch (Exception ex)
      {
         throw new CInvalidPropertyException(asProperty, this, ex);
      }

      return loValue;
   }

   /**
    * Gets class of specified property.
    * @see voodoosoft.jroots.core.CPropertyClass
    */
   public CPropertyClass getPropertyClass(String asProperty)
                                   throws CInvalidPropertyException
   {
      CPropertyClass loClass;

      try
      {
         loClass = (CPropertyClass) moPropClasses.get(asProperty);

         if (loClass == null)
         {
            loClass = moDataStore.getColumnClass(asProperty);
         }

         return loClass;
      }
      catch (Exception ex)
      {
         // TODO
         throw new CInvalidPropertyException(asProperty, this, ex);
      }
   }

   /**
    * Retrieves data for this <code>CDefaultEntity</code> or inserts empty row for new entity.
    * <p><code>isNewEntityKey</code> of the internal <code>IPrimaryKey</code> decides whether
    * this is object is treated as "new" entity.
    * @param aoConn
    * @throws CActivateFailedException
    */
   public void activate(CConnection aoConn) throws CActivateFailedException
   {
      try
      {
         if (moKey.isNewEntityKey())
         {
            mbNewEntity = true;
         }
         else
         {
            mbNewEntity = false;
         }

         retrieve(aoConn);

         if (mbNewEntity)
         {
            moDataStore.insertRow();
         }
      }
      catch (Exception ex)
      {
         throw new CActivateFailedException(ex);
      }
   }

   /**
    * Maps property of this <code>CDefaultEntity</code> to the specified object method.
    * <p>Every call of <code>getProperty</code> or <code>setProperty</code> for bound properties
    * will not delegated to the underlying <code>CDataStore</code> but to the mapped method.
    * @param asProperty name of property
    * @param aoPropClass class of property
    * @param asGetMethod when not null, <code>getProperty</code> is overridden
    * @param asSetMethod when not null, <code>setProperty</code> is overridden
    * @throws CInvalidPropertyException
    */
   public void bindProperty(String asProperty, Class aoPropClass, String asGetMethod,
                            String asSetMethod) throws CInvalidPropertyException
   {
      boolean lbReadonly;
      Class[] loParam = new Class[1];
      loParam[0] = aoPropClass;

      try
      {
         if (moPropBinder == null)
         {
            moPropBinder = new CPropertyBinder();
         }

         if (!CTools.isEmpty(asGetMethod))
         {
            moPropBinder.addGetBinding(asProperty, this.getClass(), asGetMethod);
            moGetProps.add(asProperty);
         }

         if (!CTools.isEmpty(asSetMethod))
         {
            moPropBinder.addSetBinding(asProperty, this.getClass(), asSetMethod, loParam);
            moSetProps.add(asProperty);
            lbReadonly = false;
         }
         else
         {
            lbReadonly = true;
         }

         moPropClasses.put(asProperty, new CPropertyClass(aoPropClass, lbReadonly));
      }
      catch (Exception ex)
      {
         throw new CInvalidPropertyException(asProperty, this, ex);
      }
   }

   /**
    * Marks entity as deleted.
    * <p>Entity is not deleted from database before <code>update</code> is called.
    */
   public void delete(CConnection aoConn) throws CUpdateFailedException
   {
      try
      {
         if (moDataStore != null && moDataStore.getRowCount() > 0)
         {
            moDataStore.deleteRow(1);
            mbDeleted = true;
         }
      }
      catch (Exception ex)
      {
         throw new CUpdateFailedException(ex);
      }
   }

   /**
    * Resets internal <code>IDataStore</code>.
    * @see voodoosoft.jroots.data.IDataStore#reset
    * @throws CPassivateFailedException
    */
   public void passivate() throws CPassivateFailedException
   {
      getDataStore().reset();
   }

   public void postCreate()
   {
   }

   /**
    * Retrieves data using the internal primary key.
    * <p>Calls <code>setParameters</code> and <code>retrieve</code> of <code>IDataStore</code>.
    */
   public void retrieve(CConnection aoConn) throws CRetrieveFailedException
   {
      int liRowCount = 0;
      String lsEntity;

      try
      {
         if (moDataStore.usesPreparedSQL())
         {
            moDataStore.setParameters(moKey.getKeyValues());
         }

         liRowCount = moDataStore.retrieve(aoConn);

         mbDeleted = false;
      }
      catch (CSQLException ex)
      {
         throw new CRetrieveFailedException(ex);
      }

      if (liRowCount != 1)
      {
         if (CTools.isEmpty(getName()))
         {
            lsEntity = this.getKey().getEntityName();
         }
         else
         {
            lsEntity = getName();
         }

         if (liRowCount > 1)
         {
            if (getKey() != null)
            {
               throw new CRetrieveFailedException("for entity [" + lsEntity + "] [" +
                                                  getKey().toString() +
                                                  "] more than one row found: " +
                                                  String.valueOf(liRowCount));
            }
            else
            {
               throw new CRetrieveFailedException("for entity [" + lsEntity +
                                                  "] more than one row found: " +
                                                  String.valueOf(liRowCount));
            }
         }
         else if (liRowCount < 1 && !mbNewEntity)
         {
            if (getKey() != null)
            {
               throw new CRetrieveFailedException("entity [" + lsEntity + "] [" +
                                                  getKey().toString() + " ] not found");
            }
            else
            {
               throw new CRetrieveFailedException("entity [" + lsEntity + "] not found");
            }
         }
      }
   }

   /**
    * Initiates the update process.
    * <p>Nothing happens if this entity is a new object as well as marked as deleted.
    * Otherwise, it does the following:
    * <p>
    * <li>supplies the internal <code>IDataStore</code> with the update table</li>
    * <li>if this is a new entity, <code>beforeInsert</code> is called</li>
    * <li>if this entity is marked as deleted, <code>beforeDelete is invoked</li>
    * <li>updates IDataStore</li>
    * <li>if this is (was) a new entity, <code>afterInsert</code> is executed</li>
    * <li>if this entity is marked as deleted, <code>afterDelete</code> is invoked</li>
    * <p>
    * @see #setPersistName
    * @see #getPersistName
    * @see voodoosoft.jroots.data.IDataStore#setUpdateTable
    * @see voodoosoft.jroots.data.IDataStore#update
    */
   public void update(CConnection aoConn) throws CUpdateFailedException
   {
      try
      {
         moDataStore.setUpdateTable(getPersistName());

         if (mbNewEntity && mbDeleted)
         {
            return;
         }

         if (mbNewEntity)
         {
            beforeInsert(aoConn);
         }
         else if (mbDeleted)
         {
            beforeDelete(aoConn);
         }

         moDataStore.update(aoConn);

         if (mbNewEntity)
         {
            afterInsert(aoConn);
         }
         else if (mbDeleted)
         {
            afterDelete(aoConn);
         }

         mbNewEntity = false;
      }
      catch (voodoosoft.jroots.data.CUpdateFailedException ex)
      {
         throw new CUpdateFailedException((voodoosoft.jroots.data.CUpdateFailedException) ex);
      }
      catch (voodoosoft.jroots.business.CUpdateFailedException ex)
      {
         throw new CUpdateFailedException((voodoosoft.jroots.business.CUpdateFailedException) ex);
      }
      catch (Exception ex)
      {
         throw new CUpdateFailedException(ex);
      }
   }

   /**
    * Called automatically after this entity has been deleted from the database.
    * Invoked during execution of <code>update</code>.
    * Does nothing by default.
    */
   protected void afterDelete(CConnection aoConn) throws Exception
   {
   }

   /**
    * Called automatically after this entity has been added to the database.
    * Invoked during execution of <code>update</code>.
    * Does nothing by default.
    */
   protected void afterInsert(CConnection aoConn) throws Exception
   {
   }

   /**
    * Called automatically before this entity is being deleted from the database.
    * Invoked during execution of <code>update</code>.
    * Does nothing by default.
    */
   protected void beforeDelete(CConnection aoConn) throws Exception
   {
   }

   /**
    * Called automatically before this entity is being added to the database.
    * Useful for setting id columns or inserting other needed entities.
    * Invoked during execution of <code>update</code>.
    * Does nothing by default.
    */
   protected void beforeInsert(CConnection aoConn) throws Exception
   {
   }

   /**
    * @deprecated
    * @param asSQL
    * @param aoConn
    * @throws CActivateFailedException
    */
   protected void createDataStore(String asSQL, CConnection aoConn)
                           throws CActivateFailedException
   {
      createDataStore(asSQL);
   }

   /**
    * Sets internal <code>IDataStore</code> to a new created instance.
    * <p>The specified SQL must be valid to use as prepared statement.
    * <p>Internal convenient method for usage in <code>activate</code> for inherited classes.
    * <p>To use <code>createDataStore</code> the datastore factory must have been set.
    * @param asSQL SQL used to create internal <code>IDataStore</code>
    * @see IPrimaryKey#getKeyValues
    * @see #setDataStoreFactory
    */
   protected void createDataStore(String asSQL) throws CActivateFailedException
   {
      IDataStore loStore;

      try
      {
         if (soDSFactory == null)
         {
            loStore = new CDataStore();
         }
         else
         {
            loStore = (IDataStore) soDSFactory.createObject();
         }

         loStore.setSQL(asSQL, true);
         setDataStore(loStore);
      }
      catch (SQLException ex)
      {
         throw new CActivateFailedException(ex);
      }
   }

   private static IDataStoreFactory soDSFactory;
   private String msPersistName;
   private IDataStore moDataStore = null;
   private IPrimaryKey moKey;
   private CPropertyBinder moPropBinder;
   private Hashtable moPropClasses;
   private HashSet moGetProps;
   private HashSet moSetProps;
   private boolean mbNewEntity;
   private boolean mbDeleted = false;
}
