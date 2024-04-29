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

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.exception.CException;
import voodoosoft.jroots.message.*;

import java.text.*;

import java.util.*;


/**
 * <code>CSearchEngine</code> is for data selection with SQL.
 */
public class CSearchEngine extends CObject implements IObserver
{
   /**
    * Creates new <code>CSearchEngine</code> using the specified <code>CSQLEngine</code> as SQL statement source.
    * @param aoEngine
    */
   public CSearchEngine(CSQLEngine aoEngine)
   {
      moSQLEngine = aoEngine;
   }

   /**
    * Creates new <code>CSearchEngine</code> using the specified <code>CSQLEngine</code> as SQL statement source.
    * @param aoEngine
    * @param aoFactory <code>IDataStoreFactory</code> to create returned <code>IDataStore</code> objects.
    */
   public CSearchEngine(CSQLEngine aoEngine, IDataStoreFactory aoFactory)
   {
      moSQLEngine = aoEngine;
      moFactory = aoFactory;
   }

   public void setAsyncCallback(IAsyncCallback aoCallback)
   {
      moCallback = aoCallback;
   }

   public static void setDefaultDateFormat(Format aoFormat)
   {
      soDateFormat = aoFormat;
   }

   public static void setDefaultNumberFormat(Format aoFormat)
   {
      soNumberFormat = aoFormat;
   }

   /**
    * Determines if this <code>CSearchEngine</code> searches in asynchronous mode.
    * @param abAsync
    * @throws IllegalStateException
    */
   public void setAsync(boolean abAsync) throws IllegalStateException
   {
      if (mbAsync != abAsync)
      {
         if (mbRunning)
         {
            throw new IllegalStateException("changing async mode not allowed during running search");
         }

         mbAsync = abAsync;

         if (moQueue == null)
         {
            moQueue = new CMessageHandler();
            moQueue.attachObserver(this, "SearchTask");
         }
      }
   }

   /**
    * Returns true if this <code>CSearchEngine</code> is currently performing a data search.
    * @return active search flag
    */
   public boolean isSearching()
   {
      return mbRunning;
   }

   /**
    * Kills currently running asynchronous search.
    * Invokes <code>canceled</code> on the underlying <code>CAsyncQuery</code> object.
    */
   public void cancelAsyncSearch()
   {
      if (mbAsync && moQueryCommand != null)
      {
         moQueryCommand.canceled();
         mbRunning = false;
      }
   }

   public void cleanUp()
   {
      if (moQueue != null)
      {
         moQueue.clearExecuted();
      }

      moQueue = null;
      moCallback = null;
      moQueryCommand = null;
      moFactory = null;
      moSQLEngine = null;
   }

   /**
    * Callback for the internal <code>CMessageHandler</code> object that does asynchronous searches.
    * @param msg <code>IMessage</code>, object class inherits <code>CAsyncQuery</code>
    * @param asMessageType type as defined in <code>CMessageHandler</code>: <code>MSG_START</code>, <code>MSG_FINISHED</code> or <code>MSG_CANCELED</code>
    * @return always 0 
    */
   public int notify(IMessage msg, String asMessageType)
   {
      if (asMessageType == CMessageHandler.MSG_START)
      {
         moQueryCommand = (CAsyncTask) msg;
      }
      else if (asMessageType == CMessageHandler.MSG_FINISHED ||
                  asMessageType == CMessageHandler.MSG_CANCELED ||
                  asMessageType == CMessageHandler.MSG_FAILED)
      {
         moQueryCommand = null;
         mbRunning = false;
      }

      return 0;
   }

   /**
    * Old deprecated synchronous search method returning <code>CDataStore</code> objects.
    * Use <code>startSearch</code> instead.
    * @deprecated
    * @param aoCriteria
    * @param asSQLName
    * @param aoConn
    * @return search result as <code>CDataStore</code>
    * @throws Exception
    */
   public CDataStore search(CSearchCriteria aoCriteria, String asSQLName, CConnection aoConn)
                     throws Exception
   {
      CDataStore loResultSet = null;
      String lsSearchSQL;
      boolean lbSavedAsync = mbAsync;

      try
      {
         lsSearchSQL = moSQLEngine.getSQL(asSQLName, CSQLEngine.SELECT);
         lsSearchSQL = buildSQL(aoCriteria, lsSearchSQL);

         if (moFactory == null)
         {
            loResultSet = new CDataStore(lsSearchSQL);
         }
         else
         {
            loResultSet = (CDataStore) moFactory.createObject(lsSearchSQL, false);
         }

         lbSavedAsync = mbAsync;
         mbAsync = false;
         doSearch(loResultSet, aoConn, moCallback);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }
      finally
      {
         mbAsync = lbSavedAsync;
      }

      return loResultSet;
   }

   /**
    * Performs SQL search.
    * <p>The internal <code>CSQLEngine</code> is used to get the basic SQL statement to modify and execute.
    * <p>Depending on the specified <code>CSearchCriteria</code>,
    * the basic SQL is modified by adding <code>WHERE</code>- or <code>FROM</code>-clauses.
    * <p>If this <code>CSearchEngine</code> has an internal <code>IDataStoreFactory</code>,
    * the factory is used to create the returned <code>IBasicDataStore</code>,
    * otherwise a new <code>CDataStore</code> is directly created with <code>new</code>.
    * @param aoCriteria search criteria
    * @param asSQLName name of basic SQL to use
    * @param aoConn connection to use
    * @return null for async searches, otherwise the search result as <code>IDataStore</code>
    */
   public IBasicDataStore startSearch(CSearchCriteria aoCriteria, String asSQLName,
                                      CConnection aoConn)
                               throws Exception
   {
      IBasicDataStore loResultSet = null;
      String lsSearchSQL;

      try
      {
         lsSearchSQL = moSQLEngine.getSQL(asSQLName, CSQLEngine.SELECT);
         lsSearchSQL = buildSQL(aoCriteria, lsSearchSQL);

         if (moFactory == null)
         {
            loResultSet = new CDataStore(lsSearchSQL);
         }
         else
         {
            loResultSet = moFactory.createObject(lsSearchSQL, false);
         }

         doSearch(loResultSet, aoConn, moCallback);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return loResultSet;
   }

   /**
    * Performs SQL search.
    * <p>The internal <code>CSQLEngine</code> is used to get the basic SQL statement to modify and execute.
    * <p>Depending on the specified <code>CSearchCriteria</code>,
    * the basic SQL is modified by adding <code>WHERE</code>- or <code>FROM</code>-clauses.
    * <p>If this <code>CSearchEngine</code> has an internal <code>IDataStoreFactory</code>,
    * the factory is used to create the returned <code>IBasicDataStore</code>,
    * otherwise a new <code>CDataStore</code> is directly created with <code>new</code>.
    * @param aoCriteria search criteria
    * @param asSQLStatement SQL statement to use as basis
    * @param aoConn connection to use
    * @return null for async searches, otherwise the search result as <code>IDataStore</code>
    */
   public IBasicDataStore startSearchSQL(CSearchCriteria aoCriteria, String asSQLStatement,
                                         CConnection aoConn)
                                  throws Exception
   {
      IBasicDataStore loResultSet = null;
      String lsSearchSQL;

      try
      {
         lsSearchSQL = buildSQL(aoCriteria, asSQLStatement);

         if (moFactory == null)
         {
            loResultSet = new CDataStore(lsSearchSQL);
         }
         else
         {
            loResultSet = moFactory.createObject(lsSearchSQL, false);
         }

         doSearch(loResultSet, aoConn, moCallback);
      }
      catch (Exception ex)
      {
         CException.record(ex, this);
      }

      return loResultSet;
   }

   public String buildSQL(CSearchCriteria aoCriteria, String asSQLStatement)
                    throws Exception
   {
      String lsSearchSQL;
      String lsEx;
      String lsAND;
      String lsCritValue;
      Collection loEntries;
      CSearchCriteria.Criterion loCrit;
      int liIdx;
      int liValueIdx = 0;
      StringTokenizer loToks = null;
      List loValues = null;

      if (aoCriteria == null)
      {
         return asSQLStatement;
      }

      lsSearchSQL = asSQLStatement;

      loEntries = aoCriteria.getCriteriaMap().values();

      for (Iterator i = loEntries.iterator(); i.hasNext();)
      {
         loCrit = (CSearchCriteria.Criterion) i.next();

         if (loCrit.Value != null && !loCrit.Value.equals(loCrit.disabledValue))
         {
            if (loCrit.Enumeration)
            {
               // values are in List
               if (loCrit.Value instanceof List)
               {
                  loValues = (List) loCrit.Value;
                  liValueIdx = 0;

                  if (loValues.size() == 0)
                  {
                     continue;
                  }

                  lsCritValue = loValues.get(liValueIdx).toString();
               }

               // values are in String (whitespace separated)
               else
               {
                  loToks = new StringTokenizer(loCrit.Value.toString());

                  /** @todo kontrollierte String-Konvertierung ! */
                  lsCritValue = loToks.nextToken();
               }
            }
            else
            {
               loToks = null;
               loValues = null;

               if (loCrit.ValueFormat == null)
               {
                  if (loCrit.Value instanceof Date)
                  {
                     lsCritValue = soDateFormat.format(loCrit.Value);
                  }
                  else if (loCrit.Value instanceof Number)
                  {
                     lsCritValue = soNumberFormat.format(loCrit.Value);
                  }
                  else
                  {
                     lsCritValue = loCrit.Value.toString();
                  }
               }
               else
               {
                  lsCritValue = loCrit.ValueFormat.format(loCrit.Value);
               }
            }

            lsAND = "";

            // loop through enum values
            do
            {
               if (loCrit.Expression != null)
               {
                  lsEx = loCrit.Expression;
                  liIdx = lsEx.indexOf("@");

                  if (liIdx > -1)
                  {
                     lsAND = lsAND + lsEx.substring(0, liIdx) + lsCritValue +
                             lsEx.substring(liIdx + 1);
                  }
                  else
                  {
                     lsAND = lsAND + lsEx;
                  }
               }
               else
               {
                  if (loCrit.Wildcard)
                  {
                     lsAND = lsAND + loCrit.Name + " LIKE '%" + lsCritValue + "%'";
                  }
                  else if (lsCritValue.indexOf("%") > -1)
                  {
                     lsAND = lsAND + loCrit.Name + " LIKE '" + lsCritValue + "'";
                  }
                  else
                  {
                     lsAND = lsAND + loCrit.Name + " = '" + lsCritValue + "'";
                  }
               }

               // get next enum value
               if (loToks != null && loToks.hasMoreTokens())
               {
                  lsCritValue = loToks.nextToken();
                  lsAND = lsAND + loCrit.EnumOperator;
               }
               else if (loValues != null && ++liValueIdx < loValues.size())
               {
                  lsCritValue = loValues.get(liValueIdx).toString();
                  lsAND = lsAND + loCrit.EnumOperator;
               }
               else
               {
                  lsCritValue = null;
               }
            }
            while (lsCritValue != null);

            // add criterion
            if (!lsAND.equals(""))
            {
               lsAND = "(" + lsAND + ")";
               lsSearchSQL = moSQLEngine.addCondition(lsSearchSQL, lsAND);
            }

            if (loCrit.FROMClause != null)
            {
               lsSearchSQL = moSQLEngine.addJoin(lsSearchSQL, loCrit.FROMClause);
            }
         }
      }

      return lsSearchSQL;
   }

   private void doSearch(IBasicDataStore loResultSet, CConnection aoConn, IAsyncCallback aoCallback)
                  throws Exception
   {
      CAsyncStore loAsyncVS;

      if (mbAsync)
      {
         loAsyncVS = new CAsyncStore(aoCallback, loResultSet, aoConn);
         loAsyncVS.setName("SearchTask");
         moQueue.pushMessage(loAsyncVS);
         mbRunning = true;
         mlQueueID = moQueue.createQueue(true, false, 0);
      }
      else
      {
         mbRunning = true;
         loResultSet.retrieve(aoConn);
         mbRunning = false;
      }
   }

   private static Format soDateFormat = DateFormat.getDateInstance();
   private static Format soNumberFormat = NumberFormat.getNumberInstance();
   private CSQLEngine moSQLEngine;
   private IDataStoreFactory moFactory;
   private CMessageHandler moQueue;
   private CAsyncTask moQueryCommand;
   private IAsyncCallback moCallback;
   private boolean mbAsync;
   private boolean mbRunning;
   private long mlQueueID;
}
