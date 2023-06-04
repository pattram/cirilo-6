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


package voodoosoft.jroots.debug;

import org.apache.log4j.*;

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.core.CTools;
import voodoosoft.jroots.exception.*;

import java.io.*;

import java.text.DateFormat;

import java.util.Date;
import java.util.Vector;

import javax.swing.*;


/**
 * Default debugger implementation running as separate thread.
 */
public class CDebugger extends CObject implements IDebugger, Runnable
{
   public CDebugger(String asLogFile, IDebugWindow aoDebugWin, long alSleepTime)
   {
      this(aoDebugWin, alSleepTime);
      msLogFile = asLogFile;
   }

   public CDebugger(String asLogFile)
   {
      this(null, 0);
      msLogFile = asLogFile;
   }

   public CDebugger(IDebugWindow aoDebugWin, long alSleepTime)
   {
      moFrame = null;
      moWin = aoDebugWin;
      mlSleepTime = alSleepTime;
      moRecordings = new Vector();
   }

   public void setShowWindow(boolean abShow)
   {
      mbShowWindow = abShow;
   }

   /**
    * Checks if debugger gui is currently visible.
    */
   public boolean isShowing()
   {
      return (moFrame == null ? false : moFrame.isShowing());
   }

   /**
    * Hides debugger gui.
    */
   public void hide()
   {
      moFrame.setVisible(false);
   }

   public void record(Exception aoEx, Object aoOrigin)
   {
      record(aoEx, aoOrigin, mbShowWindow);
   }

   /**
    * Records specified exception.
    * Adds exception to recordings, opens and shows exception information.
    */
   public void record(Exception aoEx, Object aoOrigin, boolean abShowWindow)
   {
      String lsMsg;
      String lsStack;
      String lsOrigin;
      String lsDate;
      Date loDate;
      Exception loNested;
      PrintStream loPrinter = null;
      boolean lbNestedFound;

      try
      {
         //         Toolkit.getDefaultToolkit().beep();
         if (moDebugStream == null)
         {
            if (msLogFile != null)
            {
               loPrinter = new PrintStream(new FileOutputStream(msLogFile, true));
            }
            else
            {
               loPrinter = new PrintStream(System.err);
            }
         }
         else
         {
            loPrinter = new PrintStream(moDebugStream);
         }

         loDate = new Date();
         lsDate = DateFormat.getDateTimeInstance().format(loDate);
         loPrinter.println(CTools.getLineSeparator() + "[exception record date] " + lsDate);

         if (aoOrigin != null)
         {
            lsOrigin = aoOrigin.getClass().toString();
         }
         else
         {
            lsOrigin = NULL_STRING;
         }

         loPrinter.println("[origin] " + lsOrigin);

         loNested = aoEx;
         lbNestedFound = true;

         do
         {
            lsStack = CException.getStackTrace(loNested);

            lsMsg = loNested.getMessage();

            if (lsMsg == null)
            {
               lsMsg = NULL_STRING;
            }

            moRecordings.add(lsMsg);

            loPrinter.println("[class] " + loNested.getClass().getName());
            loPrinter.println("[description] " + lsMsg);

            if (loNested instanceof CException)
            {
               if (((CException) loNested).getNested() != null)
               {
                  loNested = ((CException) loNested).getNested();
               }
               else
               {
                  lbNestedFound = false;
               }
            }
            else
            {
               lbNestedFound = false;
            }
         }
         while (lbNestedFound);

         if (lsStack == null)
         {
            lsStack = NULL_STRING;
         }

         loPrinter.println(this.NULL_STRING);
         loPrinter.println("[stack trace]");
         loPrinter.println(lsStack);

         // Debug-Window versorgen
         //         if (!isShowing() && abShowWindow)
         if (abShowWindow)
         {
            JOptionPane.showMessageDialog(moFrame,
                                          loNested.getClass() + "\n" + loNested.getMessage(),
                                          "Internal Error", JOptionPane.ERROR_MESSAGE);

            //            show();
            //            moWin.exceptionThrown(aoOrigin, aoEx.getClass(), lsMsg, lsStack, loDate);
            //            moFrame.toFront();
         }

         moLogger.error("exception thrown:  " + loNested.getClass().getName());
         moLogger.error("exception message: " + loNested.getMessage());
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      finally
      {
         if (loPrinter != null)
         {
            loPrinter.close();
         }
      }
   }

   /**
    * Debugger thread run method.
    */
   public void run()
   {
      boolean lbInterrupted = false;

      while (!lbInterrupted)
      {
         try
         {
            Thread.sleep(mlSleepTime);
         }
         catch (InterruptedException e)
         {
            lbInterrupted = true;
         }
      }
   }

   /**
    * Opens debugger gui.
    * @see IDebugWindow
    */
   public void show()
   {
      if (moFrame == null)
      {
         moFrame = new JFrame("Debugger");
         moFrame.getContentPane().add(moWin.getRootComponent());

         moFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
         moFrame.pack();
      }

      if (!moFrame.isShowing())
      {
         moFrame.setVisible(true);
      }
   }

   /**
    * Starts new debugger thread.
    */
   public void startDebugger()
   {
      moRecordings.clear();
      moDebug = new Thread(this);
      moDebug.start();
   }

   /**
    * Stops debugger, thread and closes gui.
    */
   public void stopDebugger()
   {
      if (moDebug != null)
      {
         moDebug.interrupt();
      }

      moDebug = null;
      moWin = null;

      if (moFrame != null)
      {
         moFrame.dispose();
      }

      moFrame = null;
   }

   protected static Logger moLogger = Logger.getLogger(CDebugger.class);
   private JFrame moFrame;
   private Thread moDebug;
   private IDebugWindow moWin;
   private OutputStream moDebugStream;
   private String msLogFile;
   private long mlSleepTime;
   private Vector moRecordings;
   private boolean mbShowWindow;
   private final String NULL_STRING = new String("<null>");
}
