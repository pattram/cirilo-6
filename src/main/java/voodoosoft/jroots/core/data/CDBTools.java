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


package voodoosoft.jroots.core.data;

import voodoosoft.jroots.core.CPropertyClass;

import java.lang.reflect.Constructor;

import java.math.BigDecimal;

import java.sql.*;
import java.sql.Date;
import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Diverse useful database related functions .
 */
public class CDBTools
{
   private CDBTools()
   {
   }

   public static void setArguments(PreparedStatement aoSQL, Object[] aoArgs)
                            throws SQLException
   {
      for (int i = 0; i < aoArgs.length; i++)
      {
         if (aoArgs[i] == null)
         {
            aoSQL.setNull(i + 1, 0);
         }
         else
         {
            aoSQL.setObject(i + 1, aoArgs[i]);
         }
      }
   }

   public static String DateToString(java.util.Date adDate, String asDateFormat)
   {
      return DateToString(adDate, new SimpleDateFormat(asDateFormat));
   }

   public static String DateToString(java.util.Date adDate, SimpleDateFormat aoDateFormat)
   {
      String lsResult;

      if (adDate != null)
      {
         lsResult = aoDateFormat.format(adDate);
      }
      else
      {
         lsResult = "";
      }

      return lsResult;
   }

   public static String DateToString(java.sql.Date adDate, String asDateFormat)
   {
      return DateToString(adDate, new SimpleDateFormat(asDateFormat));
   }

   public static String DateToString(java.sql.Date adDate, SimpleDateFormat aoDateFormat)
   {
      String lsResult;

      if (adDate != null)
      {
         lsResult = aoDateFormat.format(adDate);
      }
      else
      {
         lsResult = "";
      }

      return lsResult;
   }

   public static Class SQLtoJavaType(int aiSQLType)
   {
      switch (aiSQLType)
      {
         case Types.CHAR:
         case Types.VARCHAR:
         case Types.LONGVARCHAR:
            return String.class;

         case Types.BIT:
            return Boolean.class;

         case Types.TINYINT:
         case Types.SMALLINT:
         case Types.INTEGER:
            return Integer.class;

         case Types.BIGINT:
            return Long.class;

         case Types.FLOAT:
         case Types.DOUBLE:
         case Types.REAL:
            return Double.class;

         case Types.NUMERIC:
         case Types.DECIMAL:
            return BigDecimal.class;

         case Types.DATE:
            return java.sql.Date.class;

         case Types.TIME:
            return java.sql.Time.class;

         case Types.TIMESTAMP:
            return java.sql.Timestamp.class;

         default:
            return Object.class;
      }
   }

   /**
    * Creates new object for the given <code>String</code> according to the specified <code>CPropertyClass</code> object.
    * <code>CPropertyClass.javaClass</code> determines class of created object
    * @param asData <code>String</code> to cast
    * @param loClass <code>CPropertyClass</code> describing class of returned object
    * @return created object of given value
    * @throws Exception
    */
   public static Object castDataType2(String asData, CPropertyClass loClass)
                               throws Exception
   {
      Constructor loCons;
      Object loData = null;
      String[] loArgs;
      java.util.Date loUtilDate;

      try
      {
         if (loClass.javaClass.equals(String.class))
         {
            loData = asData;
         }
         else if (loClass.javaClass.equals(Date.class))
         {
            loUtilDate = DateFormat.getDateInstance().parse(asData);
            loData = new java.sql.Date(loUtilDate.getTime());
         }
         else if (loClass.javaClass.equals(Timestamp.class))
         {
            loUtilDate = DateFormat.getDateInstance().parse(asData);
            loData = new java.sql.Timestamp(loUtilDate.getTime());
         }
         else
         {
            loCons = loClass.javaClass.getConstructor(new Class[] { String.class });
            loArgs = new String[1];
            loArgs[0] = asData;
            loData = loCons.newInstance(loArgs);
         }
      }
      catch (Exception ex)
      {
         loData = null;
         throw ex;
      }

      return loData;
   }
}
