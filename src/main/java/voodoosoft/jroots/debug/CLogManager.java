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

import voodoosoft.jroots.core.CObject;

import java.io.PrintStream;


/**
 * Class for logging information at runtime.
 */
public class CLogManager extends CObject
{
   public static void setLogLevel(int aiLevel)
   {
      siLogLevel = aiLevel;
   }

   public static void setStream(PrintStream aoStream)
   {
      soStream = aoStream;
   }

   public static PrintStream getStream()
   {
      return soStream;
   }

   public static void log(Object aoSource, String asInfo)
   {
      log(aoSource, asInfo, 0);
   }

   public static void log(Object aoSource, String asInfo, int aiLevel)
   {
      if (aiLevel >= siLogLevel)
      {
         soStream.println("[log source] " + aoSource.getClass().getName() + " [log] " + asInfo);
      }
   }

   public static void log(Class aoSourceClass, String asInfo, int aiLevel)
   {
      if (aiLevel >= siLogLevel)
      {
         soStream.println("[log source] " + aoSourceClass.getName() + " [log] " + asInfo);
      }
   }

   public static void log(Class aoSourceClass, String asInfo)
   {
      log(aoSourceClass, asInfo, 0);
   }

   public static final int LOG_DEBUG = 0;
   public static final int LOG_DETAILINFO = 1;
   public static final int LOG_INFO = 2;
   public static final int LOG_STANDARDINFO = 3;
   public static final int LOG_WARNING = 4;
   public static final int LOG_ERROR = 5;
   private static PrintStream soStream = new PrintStream(System.out);
   private static int siLogLevel = 100;
}
