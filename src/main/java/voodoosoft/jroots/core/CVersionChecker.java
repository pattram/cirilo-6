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


package voodoosoft.jroots.core;

import java.util.StringTokenizer;


/**
 * Service class for comparing version numbers.
 */
public class CVersionChecker
{
   private CVersionChecker()
   {
   }

   /**
    * Checks if the given version number is equal, higher or lower than the specified reference number.
    * <p>
    * <p>Valid version numbers consist of several numeric sub-numbers, devided by dots, for example:
    * <p>"1.2.5" or "0.3"
    * @param asCheckVersion version to compare with reference version
    * @param asReferenceVersion reference version
    * @return <code>VERSIONS_ARE_EQUAL</code>, <code>VERSION_IS_LOWER</code> or <code>VERSION_IS_HIGHER</code>
    */
   public static int compareVersions(String asCheckVersion, String asReferenceVersion)
   {
      int liRet = VERSIONS_ARE_EQUAL;
      StringTokenizer loTok1;
      StringTokenizer loTok2;
      String[] lsV1;
      String[] lsV2;
      int liN1;
      int liN2;

      loTok1 = new StringTokenizer(asCheckVersion, ".");
      lsV1 = new String[loTok1.countTokens()];

      for (int i = 0; i < lsV1.length; i++)
      {
         lsV1[i] = loTok1.nextToken();
      }

      loTok2 = new StringTokenizer(asReferenceVersion, ".");
      lsV2 = new String[loTok2.countTokens()];

      for (int i = 0; i < lsV2.length; i++)
      {
         lsV2[i] = loTok2.nextToken();
      }

      for (int i = 0; i < lsV1.length; i++)
      {
         if (lsV2.length < i + 1)
         {
            liRet = VERSION_IS_HIGHER;

            break;
         }

         liN1 = Integer.parseInt(lsV1[i]);
         liN2 = Integer.parseInt(lsV2[i]);

         if (liN1 < liN2)
         {
            liRet = VERSION_IS_LOWER;

            break;
         }
         else if (liN1 > liN2)
         {
            liRet = VERSION_IS_HIGHER;

            break;
         }
      }

      if (lsV2.length > lsV1.length)
      {
         liRet = VERSION_IS_LOWER;
      }

      return liRet;
   }

   public static final int VERSIONS_ARE_EQUAL = 0;
   public static final int VERSION_IS_LOWER = 1;
   public static final int VERSION_IS_HIGHER = 2;
}
