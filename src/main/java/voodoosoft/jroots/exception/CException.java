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


package voodoosoft.jroots.exception;

import voodoosoft.jroots.debug.IDebugger;

import java.io.*;

import java.util.*;


/**
 * Ancestor for all exceptions of JRoots as well as wrapper of <code>Exception</code> objects.
 */
public class CException extends Exception
{
   public static class CExceptionRecord
   {
      CExceptionRecord(Exception aoEx, Object aoOrigin)
      {
         moEx = aoEx;
         moOrigin = aoOrigin;
      }

      public Exception getException()
      {
         return moEx;
      }

      public Object getOrigin()
      {
         return moOrigin;
      }

      private Exception moEx;
      private Object moOrigin;
   }

   public CException(Exception nested, String message)
   {
      init(nested, message);
   }

   protected CException()
   {
   }

   /**
    * Returns timestamp of <code>init</code> method.
    * @return timestamp of this <code>CException</code>
    */
   public Date getDate()
   {
      return md_date;
   }

   /**
    * Sets debugger called in <code>record</code>.
    * @param aoDebugger
    */
   public static void setDebugger(IDebugger aoDebugger)
   {
      soDebugger = aoDebugger;
   }

   /**
    * @deprecated    
    */
   public String getException()
   {
      return msExceptionKey;
   }

   /**
    * Returns <code>init</code>-message of this <code>CException</code>.
    * @return exception message
    */
   public String getMessage()
   {
      return msMessage;
   }

   //   public static void getResources(Locale ao_locale)
   //   {
   //      System.out.println(ao_locale);
   //
   //      so_resources = (CExceptionResources) ResourceBundle.getBundle(ss_basisClass, ao_locale);
   //
   //      System.out.println(so_resources.getLocale());
   //
   //   }

   /**
    * Returns nested exception of this <code>CException</code>.
    * @return nested exception 
    */
   public Exception getNested()
   {
      return moNested;
   }

   /**
    * Returns stack trace of specified Exception.
    */
   public static String getStackTrace(Exception aoEx)
   {
      PrintWriter loPW;
      StringWriter loSW = new StringWriter();

      loPW = new PrintWriter(loSW);
      aoEx.printStackTrace(loPW);

      return loSW.toString();
   }

   /**
    * Sets internal exception and message of this <code>CException</code> to the specified values.
    * <code>init</code> should be called in the constructor of descended exception classes.
    * @param aoNested
    * @param asMessage
    */
   public void init(Exception aoNested, String asMessage)
   {
      moNested = aoNested;

      init(asMessage);
   }

   /**
    * Sets internal message of this <code>CException</code> to the specified value.
    * <code>init</code> should be called in the constructor of descended exception classes.
    * @param asMessage
    */
   public void init(String asMessage)
   {
      md_date = new Date();
      msMessage = asMessage;
   }

   /**
    * @deprecated
    * @param as_exception
    * @param asMessage
    */
   public void init(String as_exception, String asMessage)
   {
      String lsResourceMessage;

      //      // falls noch keine Resourcen geladen wurden, Default-Werte verwenden
      //      if (so_resources == null)
      //         so_resources = (CExceptionResources) ResourceBundle.getBundle(ss_basisClass);
      // Zeitpunkt festhalten
      md_date = new Date();

      // Schl\uFFFDsselwort merken
      msExceptionKey = as_exception;

      // Beschreibung holen
      msMessage = asMessage;
   }

   /**
    * Standard method for exception handling.
    * If there is an <code>IDebugger</code> registered,
    * <code>record</code> of the debugger will be called.
    * @param aoEx exception to record
    * @param aoOrigin exception source
    * @param abShow if true, show message box
    * @see #setDebugger
    */
   public static void record(Exception aoEx, Object aoOrigin, boolean abShow)
   {
      if (aoEx == null)
      {
         return;
      }

      if (soDebugger != null)
      {
         soDebugger.record(aoEx, aoOrigin, abShow);
      }
      else
      {
         System.out.println(aoEx.getClass().toString());
         aoEx.printStackTrace();
      }
   }

   /**
    * Standard method for exception handling.
    * If there is an <code>IDebugger</code> registered,
    * <code>record</code> of the debugger will be called.
    * @param aoEx exception to record
    * @param aoOrigin exception source
    * @see #setDebugger
    */
   public static void record(Exception aoEx, Object aoOrigin)
   {
      if (aoEx == null)
      {
         return;
      }

      if (soDebugger != null)
      {
         soDebugger.record(aoEx, aoOrigin);
      }
      else
      {
         System.out.println(aoEx.getClass().toString());
         aoEx.printStackTrace();
      }
   }

   public void printStackTrace()
   {
      super.printStackTrace();

      if (moNested != null)
      {
         System.err.println("nested exception:");
         System.err.println(moNested.getClass().getName());
         moNested.printStackTrace();
      }
   }

   /**
    * Returns <code>init</code>-message of this <code>CException</code>.
    * @return exception message
    */
   public String toString()
   {
      if (msMessage != null)
      {
         return msMessage;
      }
      else
      {
         return this.getClass().toString() + " <no message>";
      }
   }

   private static IDebugger soDebugger = null;

   /** Kurzbezeichnung */
   private String msExceptionKey = new String("DEFAULT");

   /** (Individuelle) Beschreibung der Exception, zus\uFFFDtzlich des Resourcentextes */
   private String msMessage;

   /** Uhrzeit der Ausl\uFFFDsung */
   private Date md_date;
   private Exception moNested;
}
