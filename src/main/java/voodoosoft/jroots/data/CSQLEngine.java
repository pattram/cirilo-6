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


/**
 * <code>CSQLEngine</code> loads and modifies SQL commands.
 * Note that the current version cannot handle SQL statements that include
 * nested Sub-<code>SELECT</code>s or <code>UNION</code> constructs.
 * <p><code>CSQLEngine</code> will not check if the given SQL statements are valid,
 * what it does, is merely text insertion.
 * <p>SQL statements containing one of the keywords <code>FROM</code>, <code>WHERE</code>, <code>HAVING</code>
 * as part of column name or constant expression will most likely not handled
 * correctly.
 * <p>For SQL clauses <code>ORDER BY</code> and <code>GROUP BY</code>,
 * it is important to place exactly one space before the <code>BY</code>-keyword
 *
 * @see voodoosoft.jroots.data.ISQLStorage
 * @see voodoosoft.jroots.data.CXmlSqlStorage
 *
 */
public class CSQLEngine extends CObject
{
   /**
    * Creates SQLEngine using the specified storage object as SQL source.
    * @param aoStorage storage as SQL source
    * @see #getSQL
    */
   public CSQLEngine(ISQLStorage aoStorage)
   {
      moStorage = aoStorage;
   }

   /**
    * Standard constructor for creation without <code>ISQLStorage</code>.
    */
   public CSQLEngine()
   {
   }

   /**
    * Returns specified SQL from the internal <code>ISQLStorage</code>.
    * @param asName name of SQL to return
    * @param aiType one of the predefined SQL types like <code>SELECT</code> or <code>INSERT</code>,
    * @return requested SQL
    */
   public String getSQL(String asName, int aiType) throws CSQLNotFoundException
   {
      return moStorage.getSQL(asName, aiType);
   }

   /**
    * Gets specified SQL type as <code>String</code>.
    * @param aiType type
    * @return String type
    */
   public static String getSQLType(int aiType)
   {
      return ssTypes[aiType];
   }

   /**
    * Adds condition to WHERE clause.
    * @param asSQL condition to add; must not contain 'WHERE' or 'AND' at the beginning
    * Valid expressions are for example:
    * <li> "city = 'Hamburg'"
    * <li> "(city = 'Hamburg')"
    * <li> "(city = 'Hamburg') AND (country = 'Germany')"
    * @param asQuery query to modify
    * @return modified query
    */
   public String addCondition(String asQuery, String asSQL)
   {
      int liPos;
      int liWHERE;
      int liLength;
      String lsUpperSQL;
      String lsModified;
      String lsWHERE;

      // find keywords
      lsUpperSQL = asQuery.toUpperCase();

      liWHERE = lsUpperSQL.indexOf("WHERE");

      liPos = lsUpperSQL.indexOf("GROUP BY");

      if (liPos == -1)
      {
         liPos = lsUpperSQL.indexOf("ORDER BY");
      }

      // build new SQL
      if (liWHERE == -1)
      {
         lsWHERE = " WHERE " + asSQL;
      }
      else
      {
         lsWHERE = " AND " + asSQL;
      }

      if (liPos == -1)
      {
         lsModified = asQuery + lsWHERE;
      }
      else
      {
         lsModified = asQuery.substring(0, liPos - 1) + lsWHERE + " " + asQuery.substring(liPos);
      }

      return lsModified;
   }

   /**
    * Adds column list to the GROUP BY clause as well as to the list of selected columns.
    * The expression can consist of several comma separated elements.
    * Valid expressions are for example:
    * <li> "city"
    * <li> "city, customer"
    * @param asSQL group to add
    * @param asQuery query to modify
    * @return modified query
    */
   public String addGroup(String asQuery, String asSQL)
   {
      int liGroupPos;
      int liOrderPos;
      int liHavingPos;
      int liFROMPos;
      String lsUpperSQL;
      String lsModified;

      lsUpperSQL = asQuery.toUpperCase();

      // 1. extend grouped column list
      liHavingPos = lsUpperSQL.indexOf("HAVING");

      if (liHavingPos != -1)
      {
         lsModified = asQuery.substring(0, liHavingPos - 1) + ", " + asSQL + " " +
                      asQuery.substring(liHavingPos);
      }
      else
      {
         liGroupPos = lsUpperSQL.indexOf("GROUP BY");
         liOrderPos = lsUpperSQL.indexOf("ORDER BY");

         if (liGroupPos == -1)
         {
            if (liOrderPos == -1)
            {
               lsModified = asQuery + " GROUP BY " + asSQL;
            }
            else
            {
               lsModified = asQuery.substring(0, liOrderPos - 1) + " GROUP BY " + asSQL + " " +
                            asQuery.substring(liOrderPos);
            }
         }
         else
         {
            if (liOrderPos == -1)
            {
               lsModified = asQuery + ", " + asSQL;
            }
            else
            {
               lsModified = asQuery.substring(0, liOrderPos - 1) + ", " + asSQL + " " +
                            asQuery.substring(liOrderPos);
            }
         }
      }

      // 2. extend selected column list
      liFROMPos = lsUpperSQL.indexOf("FROM");
      lsModified = lsModified.substring(0, liFROMPos - 1) + ", " + asSQL + " " +
                   lsModified.substring(liFROMPos);

      return lsModified;
   }

   /**
    * Not yet implemented.
    * Adds condition to HAVING clause.
    * @param asSQL condition to add
    * @param asQuery query to modify
    * @return modified query
    */
   public String addHavingCondition(String asQuery, String asSQL)
   {
      return null;
   }

   /**
    * Adds join expression to FROM clause.
    * Example:
    *
    * <code>
    * String lsSQL, lsNewJoin;
    * CSQLEngine loSQLEngine;
    *
    * lsSQL = "SELECT * FROM dbo.Tasks"
    * lsNewJoin = "INNER JOIN dbo.OpenTasks ON dbo.OpenTasks.TaskID = dbo.Tasks.TaskID"
    * loSQLEngine = new CSQLEngine();
    * loSQLEngine.addJoin(lsSQL, lsNewJoin);
    * </code>
    *
    * The resulting statement looks like this:
    * <code>
    * SELECT *
    * FROM dbo.Tasks INNER JOIN dbo.OpenTasks ON dbo.OpenTasks.TaskID = dbo.Tasks.TaskID
    * </code>
    *
    * @param asSQL join to add
    * @param asQuery query to modify
    * @return modified query
    */
   public String addJoin(String asQuery, String asSQL)
   {
      int liPos;
      String lsUpperSQL;
      String lsModified;

      // find keywords
      lsUpperSQL = asQuery.toUpperCase();

      liPos = lsUpperSQL.indexOf("WHERE");

      if (liPos == -1)
      {
         liPos = lsUpperSQL.indexOf("GROUP BY");

         if (liPos == -1)
         {
            liPos = lsUpperSQL.indexOf("ORDER BY");
         }
      }

      /** @todo Index-Prüfung */
      lsModified = asQuery.substring(0, liPos - 1) + " " + asSQL + " " + asQuery.substring(liPos);

      return lsModified;
   }

   /**
    * Adds new expression to the ORDER BY clause.
    * The expression may be a comma separated list of several order elements.
    * Valid expressions are for example:
    * <li> "city"
    * <li> "city, customer"
    * @param asSQL expression to add, without comma at the beginning
    * @param asQuery query to modify
    * @return modified query
    */
   public String addOrder(String asQuery, String asSQL)
   {
      int liPos;
      String lsUpperSQL;
      String lsModified;

      lsUpperSQL = asQuery.toUpperCase();

      liPos = lsUpperSQL.indexOf("ORDER BY");

      if (liPos == -1)
      {
         lsModified = asQuery + " ORDER BY " + asSQL;
      }
      else
      {
         lsModified = asQuery + ", " + asSQL;
      }

      return lsModified;
   }

   /**
    * Not yet implemented.
    * Adds union to SQL statement.
    * @param asSQL union to add
    * @param asQuery query to modify
    */
   public void addUnion(String asQuery, String asSQL)
   {
   }

   /**
    * Loads SQL statements of the internal <code>ISQLStorage</code> into this engine.
    */
   public void load() throws CSQLNotFoundException
   {
      moStorage.load();
   }

   public static final int SELECT = 0;
   public static final int INSERT = 1;
   public static final int DELETE = 2;
   public static final int UPDATE = 3;
   private static String[] ssTypes = { "SELECT", "INSERT", "DELETE", "UPDATE", "AND" };
   ISQLStorage moStorage;
}
