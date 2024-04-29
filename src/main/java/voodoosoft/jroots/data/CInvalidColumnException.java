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

import java.lang.String;


public class CInvalidColumnException extends CException
{
   public CInvalidColumnException(CObject aoSource, String asColumn, int aiRow)
   {
      init("invalid column or row (column: " + asColumn + " row: " + String.valueOf(aiRow) + ")");
   }

   public CInvalidColumnException(CObject aoSource, int aiRow)
   {
      init("invalid row: " + String.valueOf(aiRow));
   }

   public CInvalidColumnException(CObject aoSource, String asColumn)
   {
      init("invalid column: " + asColumn);
   }

   public CInvalidColumnException(CObject aoSource, int aiColumn, int aiRow)
   {
      init("column: " + String.valueOf(aiColumn) + " row: " + String.valueOf(aiRow));
   }
}
