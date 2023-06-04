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


package voodoosoft.jroots.message;

import java.lang.String;


/**
 * Interface for objects being used as messages or commands.
 * @see CCommand
*/
public interface IMessage
{
   /**
    * Returns key name of message.
    * <p>Will be used to filter observers to notify.
    * @return
    */
   public String getName();

   /**
    * Callback if execution has been canceled.
    */
   public void canceled();

   /**
    * Callback to indicate successful execution.
    */
   public void done();

   /**
    * Starts execution of messge.
    * @return -1 if execution failed
    * @throws Exception
    */
   public int exec() throws Exception;

   /**
    * Callback if execution has failed.
    */
   public void failed();

   /**
    * Takes back action performed in <code>exec</code>.
    * @return -1 if failed
    * @throws Exception
    */
   public int undo() throws Exception;
}
