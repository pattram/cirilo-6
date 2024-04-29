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

import org.apache.log4j.Logger;

import voodoosoft.jroots.core.CTools; 
import voodoosoft.jroots.exception.*;
import voodoosoft.jroots.message.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;

import java.sql.*;

import java.text.SimpleDateFormat;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


//import voodoosoft.jroots.debug.CLogManager;

/**
 * Abstraction for the JDBC <code>Connection</code> and <code>DataSource</code> classes.
*/
public class CConnection extends CCommand implements Serializable
{   
   /**
    * Creates a new (yet disconnected) connection object using <code>DriverManager</code> as connection supplier.
    * @param url
    * @param asCatalog
    * @param user
    * @param password
    * @param abHoldLine - holding connection all the time or reconnect when necessary ?
    */
   public CConnection(String url, String asCatalog, String user, String password, boolean abHoldLine)
   {
      super("");
      moURL = url;
      msCatalog = asCatalog;
      moUser = user;
      moPassword = password;
      mbHoldLineDefault = abHoldLine;

      //      mbAutoCommit = abHoldLine ? false : true;
      msBeginTran = ssBeginTran;
      msCommitTran = ssCommitTran;
   }

   /**
    * Creates a new (yet disconnected) connection object using <code>DriverManager</code> as connection supplier..
    * @param url
    * @param abHoldLine - holding connection all the time or reconnect when necessary ?
    */
   public CConnection(String url, boolean abHoldLine)
   {
      this(url, (String) null, null, null, abHoldLine);
   }

   /**
    * Creates new <code>CConnection</code> using <code>DataSource</code> as connection supplier.
    * @param asDataSourceName
    * @param abHoldLine
    */

   //   public CConnection(String asDataSourceName, String aoUser,boolean abHoldLine)
   //   {
   //      this (asDataSourceName, aoUser, abHoldLine);
   //   }

   /**
    * Creates new <code>CConnection</code> using <code>DataSource</code> as connection supplier.
    * @param asDataSourceName JNDI datasource name
    * @param aoEnvironment environment for the internal <code>InitialContext</code> for JDNI lookup
    * @param abHoldLine
    */
   public CConnection(String asDataSourceName, Hashtable aoEnvironment, String aoUser,
                      String aoPassword, boolean abHoldLine)
               throws NamingException
   {
      InitialContext loCtx;

      mbUsingDataSource = true;

      msDataSourceName = asDataSourceName;
      moUser = aoUser;
      moPassword = aoPassword;
      mbHoldLineDefault = abHoldLine;
      mbAutoCommit = false;

      msBeginTran = ssBeginTran;
      msCommitTran = ssCommitTran;

      loCtx = new InitialContext(aoEnvironment);
      moDataSource = (DataSource) loCtx.lookup(msDataSourceName);
   }

   /**
    * Returns the (opened) connection of the specified key name.
    * The connection must have been previously registered by calling <code>addConnection</code>.
    * @param asKey connection name
    * @return open <code>CConnection</code>
    * @throws CConnectionNotAvailableException
    * @see #addConnection
    */
   public static CConnection getConnection(String asKey)
                                    throws CConnectionNotAvailableException
   {
      return getConnection(asKey, true);
   }

   /**
    * Returns the connection of the specified key name and optionally opens it.
    * The connection must have been previously registered by calling <code>addConnection</code>.
    * @param asKey
    * @return <code>CConnection</code>
    * @throws CConnectionNotAvailableException
    * @see #addConnection
    */
   public static CConnection getConnection(String asKey, boolean abConnect)
                                    throws CConnectionNotAvailableException
   {
      CConnection loConn = null;

      if (!CTools.isEmpty(asKey))
      {
         loConn = (CConnection) soConnectionPool.get(asKey);
      }

      if (loConn == null)
      {
         throw new CConnectionNotAvailableException(asKey);
      }

      try
      {
         if (loConn.isClosed() && abConnect)
         {
            loConn.connect();
         }
      }
      catch (SQLException ex)
      {
         throw new CConnectionNotAvailableException(ex,
                                                    asKey + "/" + loConn.moURL + "/" +
                                                    loConn.msCatalog + "/" + loConn.moUser);
      }

      return loConn;
   }

   /**
    * Class method to set default SQL commands for beginning and commiting of transactions.
    * New objects of <code>CConnection</code> will get the default settings,
    * but can call <code>setTransactionCommands</code> to override them individually.
    * Default commands are <code>BEGIN TRAN</code> and <code>COMMIT TRAN</code>.
    * @param asBeginTran valid SQL to begin a new transaction
    * @param asCommitTran valid SQL to commit a transaction
    * @see #setTransactionCommands
    * @see #beginTransaction
    * @see #commitTransaction
    */
   public static void setDefaultTransactionCommands(String asBeginTran, String asCommitTran)
   {
      ssBeginTran = asBeginTran;
      ssCommitTran = asCommitTran;
   }

   /**
    * Adds given connection to class connection pool.
    */
   public static void addConnection(CConnection aoConn, String asKey)
   {
      soConnectionPool.put(asKey, aoConn);
      soConRefs.put(new Integer(aoConn.hashCode()), new Hashtable());
   }

   /**
    * Adds JDBC driver to DriverManager.
    */
   public static void addJDBCDriver(String asJDBCDriverClass)
                             throws ClassNotFoundException, IllegalAccessException, 
                                    InstantiationException
   {
      Class.forName(asJDBCDriverClass).newInstance();
   }

   /**
    * Returns true, if this connection is closed.
    * @return connection state
    * @throws SQLException
    */
   public boolean isClosed() throws SQLException
   {
      return ((moConnection == null) || moConnection.isClosed());
   }

   /**
    * Returns the (connected) underlying JDBC <code>Connection</code> object.
    * @return open <code>Connection</code>
    * @throws CConnectionNotAvailableException
    */
   public Connection getConnection() throws CConnectionNotAvailableException
   {
      try
      {
         if (isClosed())
         {
            connect();
         }
      }
      catch (Exception ex)
      {
         throw new CConnectionNotAvailableException(ex, moURL);
      }

      return moConnection;
   }

   public void setDateFormat(String asFormat)
   {
      moDateFormat = new SimpleDateFormat(asFormat);
   }

   /**
    * Returns true if this connection is set to "hold line".
    * Note that <code>isHolding</code> returns the basic setting defined at construction time,
    * invoking <code>holdConnection</code> or <code>releaseConnection</code>
    * takes no influence to the return value of <code>isHolding</code>.
    * @return hold line flag
    */
   public boolean isHolding()
   {
      return mbHoldLineDefault;
   }

   public void setLogStream(PrintStream aoStream)
   {
      DriverManager.setLogWriter(new PrintWriter(aoStream));
   }

   public void setPassword(String asPass)
   {
      moPassword = asPass;
   }

   public void getProperties(Properties loProps)
   {
      DatabaseMetaData loMD;

      try
      {
         if (moConnection != null)
         {
            loMD = moConnection.getMetaData();

            loProps.setProperty("userName", loMD.getUserName());
            loProps.setProperty("driverName", loMD.getDriverName());
            loProps.setProperty("driverVersion", loMD.getDriverVersion());
            loProps.setProperty("url", loMD.getURL());
            loProps.setProperty("databaseProductName", loMD.getDatabaseProductName());
            loProps.setProperty("databaseProductVersion", loMD.getDatabaseProductVersion());
         }
      }
      catch (Exception ex)
      {
      }
   }

   /**
    * Sets SQL statements for beginning and commiting transactions for this connection.
    * The given commands override the default setttings of <code>setDefaultTransactionCommands</code>.
    * @param asBeginTran valid SQL to begin a new transaction
    * @param asCommitTran valid SQL to commit a transaction
    * @see #setTransactionCommands
    * @see #beginTransaction
    * @see #commitTransaction
    */
   public void setTransactionCommands(String asBeginTran, String asCommitTran)
   {
      msBeginTran = asBeginTran;
      msCommitTran = asCommitTran;
   }

   /**
    * Returns current URL of this connection.
    * If the internal <code>Connection</code> is open, <code>getURL</code> will return
    * the connection's metadata url setting, otherwise the URL formerly specified as
    * constructor parameter.
    * @return connection URL
    */
   public String getURL()
   {
      String lsURL = "";

      try
      {
         if (moConnection != null && !moConnection.isClosed())
         {
            lsURL = moConnection.getMetaData().getURL();
         }
         else
         {
            lsURL = moURL;
         }
      }
      catch (Exception ex)
      {
      }

      return lsURL;
   }

   public void setUserName(String asUser)
   {
      moUser = asUser;
   }

   /**
    * Returns current user name of this connection.
    * If the internal <code>Connection</code> is open, <code>getUserName</code> will return
    * the connection's metadata user name, otherwise the name formerly specified as
    * constructor parameter.
    * @return connection user
    */
   public String getUserName()
   {
      String lsURL = "";

      try
      {
         if (moConnection != null && !moConnection.isClosed())
         {
            lsURL = moConnection.getMetaData().getUserName();
         }
         else
         {
            lsURL = moUser;
         }
      }
      catch (Exception ex)
      {
      }

      return lsURL;
   }

   /**
    * Starts a new transaction in executing the internal "Begin Transaction"-SQL as <code>CallableStatement</code>.
    * @throws SQLException
    * @see #setTransactionCommands
    * @see #setDefaultTransactionCommands
    */
   public void beginTransaction() throws SQLException
   {
      CallableStatement loSQL;

      loSQL = moConnection.prepareCall(msBeginTran);
      loSQL.execute();
   }

   /**
    * Disconnects this connection, regardless if it was constructed in "hold line" mode or not.
    * @throws SQLException
    * @see #connect
    */
   public void close() throws SQLException
   {
      Hashtable loHandles;

      if (!isClosed())
      {
         moConnection.close();
         loHandles = (Hashtable) soConRefs.get(new Integer(this.hashCode()));

         if (loHandles != null)
         {
            loHandles.clear();
         }

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("closed [" + Integer.toHexString(this.hashCode()) + "] " + moURL);
         }

         //CLogManager.log(this, "closed " + moURL, CLogManager.LOG_INFO);
      }
   }

   /**
    * Delegates the commit request to <code>commit</code> of the internal <code>Connection</code> object.
    * @throws SQLException
    * @see #rollback
    */
   public void commit() throws SQLException
   {
      moConnection.commit();
   }

   /**
    * Commits transactions in executing the internal "Commit Transaction"-SQL as <code>CallableStatement</code>.
    * Another possible way to commit transactions is to call <code>commit</code>.
    * @throws SQLException
    * @see #setTransactionCommands
    * @see #setDefaultTransactionCommands
    * @see #commit
    */
   public void commitTransaction() throws SQLException
   {
      CallableStatement loSQL;

      loSQL = moConnection.prepareCall(msCommitTran);
      loSQL.execute();
   }

   /**
    * Connects this connection using the formerly set parameters.
    * @return connect success flag
    * @throws SQLException
    * @see #close
    */
   public boolean connect() throws SQLException
   {
      return connect(moPassword);
   }

   /**
    * Connects this connection using the formerly set parameters and the specified password.
    * @return connect success flag
    * @throws SQLException
    * @see #close
    */
   public boolean connect(String asPassword) throws SQLException
   {
      boolean lbSuccess = false;

      if (isClosed())
      {
         if (mbUsingDataSource)
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("connecting [" + Integer.toHexString(this.hashCode()) +
                              "] datasource " + msDataSourceName + " ...");
            }

            moConnection = moDataSource.getConnection(moUser, asPassword);
         }
         else
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("connecting [" + Integer.toHexString(this.hashCode()) + "] url [" +
                              moURL + "] ...");
            }

            moConnection = DriverManager.getConnection(moURL, moUser, asPassword);
         }

         if (!isClosed())
         {
            moConnection.setCatalog(msCatalog);
            moConnection.setAutoCommit(mbAutoCommit);
            lbSuccess = true;
         }
         else
         {
            // TODO
         }
      }
      else
      {
         lbSuccess = true;
      }

      return lbSuccess;
   }

   /**
      Aufbauen der Datenbank-Verbindung anhand der Objekt-Attribute.
   */
   public int exec() throws SQLException, Exception
   {
      super.exec();

      connect();

      return 0;
   }

   /**
    * Executes the given SQL statement as <code>PreparedStatement</code>.
    * If the connection is closed and is not set to "hold line", it will be
    * opened and closed after execution automatically.
    * <code>executeUpdate</code> of <code>PreparedStatement</code> will be invoked.
    * @param asSQL valid SQL <code>INSERT</code>, <code>UPDATE</code> or <code>DELETE</code> statement
    * @return number of rows affected
    * @throws SQLException
    */
   public int execSQL(String asSQL) throws SQLException
   {
      int liRows = 0;
      PreparedStatement loSQL;
      boolean lbConnected = false;
      boolean lbDisconnect = false;

      if (isClosed())
      {
         if (!mbHoldLineDefault)
         {
            lbConnected = connect();
            lbDisconnect = lbConnected ? true : false;
         }
      }
      else
      {
         lbConnected = true;
      }

      if (lbConnected)
      {
         loSQL = moConnection.prepareStatement(asSQL);
         liRows = loSQL.executeUpdate();

         if (lbDisconnect)
         {
            moConnection.close();
         }
      }
      else
      {
         // TODO
      }

      return liRows;
   }

   /**
    * Executes the given SQL statement as <code>PreparedStatement</code> and returns the query results.
    * If the connection is closed and is not set to "hold line", it will be
    * opened and closed after execution automatically.
    * <code>executeQuery</code> of <code>PreparedStatement</code> will be invoked.
    * @param asSQL valid SQL <code>SELECT</code> statement
    * @param aiType result set type; see <code>ResultSet.TYPE_XXX</code>
    * @param aiConcurrency concurrency type; see <code>ResultSet.CONCUR_XXX</code>
    * @return <code>ResultSet</code> of executed SQL
    * @throws SQLException
    */
   public ResultSet execSQL(String asSQL, int aiType, int aiConcurrency)
                     throws SQLException
   {
      ResultSet loResultSet = null;
      PreparedStatement loSQL;
      boolean lbConnected = false;
      boolean lbDisconnect = false;

      if (isClosed())
      {
         if (!mbHoldLineDefault)
         {
            lbConnected = connect();
            lbDisconnect = lbConnected ? true : false;
         }
      }
      else
      {
         lbConnected = true;
      }

      if (lbConnected)
      {
         loSQL = moConnection.prepareStatement(asSQL, aiType, aiConcurrency);
         loResultSet = loSQL.executeQuery();

         if (lbDisconnect)
         {
            moConnection.close();
         }
      }
      else
      {
         // TODO
      }

      return loResultSet;
   }

   /**
    * Makes sure this <code>CConnection</code> is connected.
    * Does nothing if connection is already open.
    *
    * @return true if succeeded
    * @throws SQLException
    */
   public boolean holdConnection() throws SQLException
   {
      boolean lbSuccess;
      Hashtable loHandles;

      lbSuccess = connect();

      if (lbSuccess)
      {
         loHandles = (Hashtable) soConRefs.get(new Integer(this.hashCode()));

         if (loHandles != null)
         {
            loHandles.put(siDefaultHandle, siDefaultHandle);

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("holdConnection [" + Integer.toHexString(this.hashCode()) +
                              "] handle [" + Integer.toHexString(siDefaultHandle.intValue()) +
                              "] ref Count[" + String.valueOf(loHandles.size()) + "]");
            }
         }
      }

      mbHolding = lbSuccess;

      return lbSuccess;
   }

   public Integer lockConnection() throws SQLException
   {
      boolean lbSuccess;
      Integer liHandle = null;
      Hashtable loHandles;

      lbSuccess = connect();

      if (lbSuccess)
      {
         loHandles = (Hashtable) soConRefs.get(new Integer(this.hashCode()));

         if (loHandles != null)
         {
            liHandle = new Integer(++siNextHandle);
            loHandles.put(liHandle, liHandle);

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("lockConnection [" + Integer.toHexString(this.hashCode()) +
                              "] handle [" + Integer.toHexString(liHandle.intValue()) +
                              "] ref Count[" + String.valueOf(loHandles.size()) + "]");
            }

            //            CLogManager.log(this, "connection " + String.valueOf(this.hashCode()) + ", handles: "  + String.valueOf(loHandles.size()), CLogManager.LOG_DETAILINFO);
         }
      }

      mbHolding = lbSuccess;

      return liHandle;
   }

   public void logMetaData()
   {
      DatabaseMetaData loMD;

      try
      {
         if (moConnection != null)
         {
            loMD = moConnection.getMetaData();

            moLogger.info(loMD.getDriverName() + " " + loMD.getDriverVersion());
            moLogger.info(loMD.getDatabaseProductName() + " " + loMD.getDatabaseProductVersion());
            moLogger.info(loMD.getURL());
            moLogger.info(loMD.getUserName());
         }
      }
      catch (Exception ex)
      {
      }
   }

   public void releaseConnection()
   {
      releaseConnection(siDefaultHandle);
   }

   /**
    * Releases this connection and, if not in "hold line" mode, closes it.
    */
   public void releaseConnection(Integer aiHandle) //throws SQLException
   {
      boolean lbClose = true;
      Hashtable loHandles;

      if (aiHandle == null)
      {
         return;
      }

      loHandles = (Hashtable) soConRefs.get(new Integer(this.hashCode()));

      if (loHandles != null)
      {
         if (loHandles.contains(aiHandle))
         {
            loHandles.remove(aiHandle);

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("releaseConnection [" + Integer.toHexString(this.hashCode()) +
                              "] handle [" + Integer.toHexString(aiHandle.intValue()) +
                              "] ref count[" + String.valueOf(loHandles.size()) + "]");
            }
         }

         if (!loHandles.isEmpty())
         {
            lbClose = false;
         }
      }

      if (!mbHoldLineDefault && lbClose)
      {
         try
         {
            close();
         }
         catch (Exception ex)
         {
            CException.record(ex, this);
         }
      }

      mbHolding = false;
   }

   /**
    * Delegates the rollback request to <code>rollback</code> of the internal <code>Connection</code> object.    *
    * @see #commit
    */
   public void rollback()
   {
      try
      {
         moConnection.rollback();
      }
      catch (SQLException ex)
      {
         CException.record(ex, this);
      }
   }

   /**
    * Disconnects this connection.
   */
   public int undo() throws SQLException
   {
      boolean lbConnected;

      if (moConnection != null)
      {
         // TODO: isClosed
         if (!moConnection.isClosed())
         {
            moConnection.rollback();
            moConnection.close();
         }
      }

      return 0;
   }

   protected void finalize() throws Throwable
   {
      undo();
   }

   private static Hashtable soConnectionPool = new Hashtable();
   private static Hashtable soConRefs = new Hashtable();
   private static int siNextHandle = 1;
   private static Integer siDefaultHandle = new Integer(1);
   private static String ssBeginTran = "BEGIN TRAN";
   private static String ssCommitTran = "COMMIT TRAN";
   protected static Logger moLogger = Logger.getLogger(CConnection.class);
   private String msDataSourceName;
   private DataSource moDataSource;
   private boolean mbUsingDataSource = false;
   private String moURL;
   private String msCatalog;
   private String moUser;
   private String moPassword;
   private boolean mbHoldLineDefault;
   private boolean mbHolding;
   private boolean mbAutoCommit;
   private SimpleDateFormat moDateFormat;
   private Connection moConnection;
   private String msBeginTran;
   private String msCommitTran;
}
