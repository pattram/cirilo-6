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



/**
 * Title:        Voodoo Soft Java Framework<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Stefan Wischnewski<p>
 * Company:      Voodoo Soft<p>
 * @author Stefan Wischnewski
 * @version 1.0
 */
package voodoosoft.jroots.core;

import java.io.IOException;

import java.util.*;
import java.util.jar.*;


/**
 * Collection of static functions for various purposes.
 */
public class CTools
{
   private CTools()
   {
   }

   /**
    * Returns true if the given <code>String</code> is either null or contains only whitespace characters.
    * @param asValue
    * @return empty flag
    */
   public static boolean isEmpty(String asValue)
   {
      return (asValue == null || asValue.trim().equals(""));
   }

   /**
    * Returns system's line separator.
    * @return line separator
    */
   public static String getLineSeparator()
   {
      String lsNewLine = "\n";

      lsNewLine = System.getProperty("line.separator");

      return lsNewLine;
   }

   public static Collection getManifests() throws IOException
   {
      String lsClassPath;
      String lsJarName;
      StringTokenizer loTok;
      JarFile loJar;
      Manifest loMF;
      String lsManifest;
      Attributes loAtts;
      Collection loMFList;
      Map.Entry loAtt;

      lsClassPath = System.getProperties().getProperty("java.class.path");

      loTok = new StringTokenizer(lsClassPath, ";");
      loMFList = new ArrayList();

      while (loTok.hasMoreTokens())
      {
         lsJarName = loTok.nextToken().toLowerCase();

         if (lsJarName.endsWith("jar"))
         {
            loJar = new JarFile(lsJarName);

            loMF = loJar.getManifest();

            if (loMF != null)
            {
               loMFList.add("");
               loMFList.add(lsJarName);
               loAtts = loMF.getMainAttributes();

               for (Iterator it2 = loAtts.entrySet().iterator(); it2.hasNext();)
               {
                  loAtt = (Map.Entry) it2.next();
                  loMFList.add(loAtt.getKey().toString() + ": " + loAtt.getValue().toString());
               }

               for (Iterator it = loMF.getEntries().entrySet().iterator(); it.hasNext();)
               {
                  loAtt = ((Map.Entry) it.next());
                  loMFList.add("");
                  loMFList.add(loAtt.getKey().toString());
                  loAtts = (Attributes) loMF.getAttributes(loAtt.getKey().toString());

                  for (Iterator it2 = loAtts.entrySet().iterator(); it2.hasNext();)
                  {
                     loAtt = (Map.Entry) it2.next();
                     loMFList.add(loAtt.getKey().toString() + ": " + loAtt.getValue().toString());
                  }
               }
            }
         }
      }

      return loMFList;
   }
}
