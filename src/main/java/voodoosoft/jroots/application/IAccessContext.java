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


package voodoosoft.jroots.application;


/**
 * Interface for access contexts.
 * Contexts are more abstract objects than the common user/right relations.
 * They specify under which circumstances which rules are to execute.
 * <p>For example:</p>
 * <li> define a set of user-IDs or -groups </li>
 * <li> define the system version: trial, light or professional </li>
 * <li> define the business client the application is running for </li>
 */
public interface IAccessContext
{
   /**
    *  Checks if the specified context matches or is part of this context.
    *  Whatever that means is indiviual to the implementing class.
    *  @param aoContext context to check
    *  @return true if contexts match
    */
   public boolean matches(IAccessContext aoContext);
}
