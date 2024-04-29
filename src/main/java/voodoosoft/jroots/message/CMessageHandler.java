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

import org.apache.log4j.*;

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.core.container.CPriorityQueue;
import voodoosoft.jroots.exception.CException;

import java.lang.Long;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;


/**
 * <code>CMessageHandler</code> serves as message- or command queue.
 * <p>Objects implementing <code>IObserver</code> can be registered as subscriber for certain messages.
 * <p>Every queue of command objects is runnable in its own thread.
 * @see IMessage
 * @see CCommand
 */
public class CMessageHandler extends CObject
{
   /**
    * Creates new empty <code>CMessageHandler</code>.
    */
   public CMessageHandler()
   {
      mo_Observer = new Vector();
      mo_toDo = new CPriorityQueue();
      mDone = new Stack();
      mo_Running = new HashMap();
   }

   /**
    * Registers observer for the specified message.
    * <p>To inform the observer about message execution of the given type, <code>notify</code> of <code>IObserver</code> is called.
    * <p>The notification includes additional state information:
    * <li><code>MSG_START</code>
    * <li><code>MSG_CANCELED</code>
    * <li><code>MSG_FINISHED</code>
    * <li><code>MSG_FAILED</code>
    * @param message message the observer is interested in; must match <code>getName</code> of <code>IMessage</code>
    * @see IObserver#notify
   */
   public synchronized void attachObserver(IObserver obs, String message)
   {
      CRegisteredObserver reg = new CRegisteredObserver(obs, message);
      mo_Observer.addElement(reg);
   }

   /**
    * Free internal stack of all successful processed messages.
    */
   public synchronized void clearExecuted()
   {
      mDone.clear();
   }

   /**
    * Adds given message with default priority of 0 to the waiting messages and executes all messages.
    * @param ao_message
    * @return ID of created queue
    */
   public synchronized long createQueue(IMessage ao_message)
   {
      pushMessage(ao_message);

      return createQueue(false, false, 0);
   }

   /**
    * Creates new queue for the waiting messages
    * @param ab_runThread if true, create new thread
    * @param ab_infinite if true, queue keeps running after executing all messages
    * @param al_sleepTime sleeping time in milliseconds
    * @return ID of created queue
    */
   public synchronized long createQueue(boolean ab_runThread, boolean ab_infinite, long al_sleepTime)
   {
      long ll_ID;
      CPriorityQueue lo_ToDo;
      CRunnableQueue lo_runQueue;

      // Alle vorhandenen Befehle gehen jetzt an eine neue MessageQueue zur Ausführung
      lo_ToDo = (CPriorityQueue) mo_toDo.clone();
      mo_toDo.clear();

      lo_runQueue = new CRunnableQueue(this, lo_ToDo, mo_Observer, ab_infinite, al_sleepTime);

      // Ausführung als neuer Thread
      if (ab_runThread)
      {
         ll_ID = ml_queueID++;

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("queue created: " + ll_ID);
         }

         mo_Running.put(new Long(ll_ID), lo_runQueue);
         lo_runQueue.start();
      }

      // Ausführung im aktuellen Thread
      else
      {
         ll_ID = 0;

         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("queue created: " + ll_ID);
         }

         lo_runQueue.run();
      }

      return ll_ID;
   }

   /**
    * Callback for successful executed messages.
    * @param message
    */
   public synchronized void done(IMessage message)
   {
      mDone.push(message);
   }

   /**
    * Stops running queue.
    * @param al_queueID
    */
   public synchronized void endQueue(long al_queueID)
   {
      CRunnableQueue lo_queue;

      lo_queue = getQueue(al_queueID);

      if (lo_queue != null)
      {
         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("end queue: " + al_queueID);
         }

         lo_queue.stopQueue();
         lo_queue.interrupt();
      }
   }

   /**
    * Interrupts but not ends running queue.
    * @param al_queueID
    */
   public synchronized void interruptQueue(long al_queueID)
   {
      CRunnableQueue lo_queue;

      lo_queue = getQueue(al_queueID);

      if (lo_queue != null)
      {
         if (moLogger.isDebugEnabled())
         {
            moLogger.debug("interrupt queue: " + al_queueID);
         }

         lo_queue.interrupt();
      }
   }

   /**
    * Adds specified message at the end of the waiting messages.
    * Execution will not start before calling <code>createQueue</code>.
   */
   public synchronized void pushMessage(IMessage ao_message)
   {
      mo_toDo.push(ao_message);
   }

   /**
    * Inserts specified message into the specified (running) queue.
   */
   /**
    * Inserts given message into the specified queue.
    * @param ao_message
    * @param al_queueID either valid ID of running queue or 0 to add to queue of waiting messages
    * @param ai_priority priority of new message, messages of higher values are preferred executed
    * @return
    */
   public synchronized boolean pushMessage(IMessage ao_message, long al_queueID, Integer ai_priority)
   {
      CRunnableQueue lo_queue;
      boolean lb_success = true;

      // eine bestimmte Queue gewünscht ?
      if (al_queueID != 0)
      {
         // Queue heraussuchen
         lo_queue = getQueue(al_queueID);

         if (lo_queue != null)
         {
            lo_queue.pushMessage(ao_message, ai_priority);
         }
         else
         {
            // ToDo
            lb_success = false;
         }
      }
      else
      {
         mo_toDo.push(ao_message, ai_priority);
      }

      return lb_success;
   }

   /**
    * Returns queue of specified ID.
    * @param al_queueID
    * @return
    */
   private synchronized CRunnableQueue getQueue(long al_queueID)
   {
      return (CRunnableQueue) mo_Running.get(new Long(al_queueID));
   }

   /* Notification before messages are executed.*/
   public static final String MSG_START = "MSG_START";

   /* Notification indicating abortion of mesage queues.*/
   public static final String MSG_CANCELED = "MSG_CANCELED";

   /* Notification for succesful executed messages.*/
   public static final String MSG_FINISHED = "MSG_FINISHED";

   /* Notification for failed messages.*/
   public static final String MSG_FAILED = "MSG_FAILED";
   private static long ml_queueID = 1;
   private static Logger moLogger = Logger.getLogger(CMessageHandler.class);

   /** Warteschlange der Befehlsobjekte */
   private CPriorityQueue mo_toDo;

   /** aktuell laufende Befehls-Queues */
   private HashMap mo_Running;

   /** Ausgeführte Befehlsobjekte */
   private Stack mDone;

   /** Registrierte Observer */
   private Vector mo_Observer;
}


/**
 * Message queue for executing messages.
 * <p>Created by <code>CMessageHandler</code>.
 */
class CRunnableQueue extends Thread //implements Runnable
{
   public CRunnableQueue(CMessageHandler evMan, CPriorityQueue toDo, Vector observer,
                         boolean ab_infinite, long sleepTime)
   {
      mo_MessageHandler = evMan;
      mo_toDo = toDo;
      ml_sleepTime = sleepTime;
      mo_Observer = observer;
      mb_infinite = ab_infinite;
   }

   /**
    * Appends or inserts given message to this <code>CRunnableQueue</code>.
    * @param ao_message
    * @param ai_priority
    */
   public void pushMessage(IMessage ao_message, Integer ai_priority)
   {
      mo_toDo.push(ao_message, ai_priority);
   }

   /**
    * Starts execution of <code>CRunnableQueue</code>.
    */
   public void run()
   {
      int lErr = 0;
      IMessage lMessage = null;
      CPriorityQueue lToDo;

      mb_stopped = false;

      while (true)
      {
         // nichts mehr zu tun oder Abbruch gewünscht ?
         if ((mo_toDo.isEmpty() && !mb_infinite) || mb_stopped)
         {
            break;
         }

         lMessage = (IMessage) mo_toDo.pop();

         notifyObserver(lMessage, CMessageHandler.MSG_START);

         try
         {
            if (lMessage != null)
            {
               lErr = lMessage.exec();
            }
         }
         catch (Exception e)
         {
            CException.record(e, this, false);
            lErr = -1;
         }

         if (lErr == -1)
         {
            moLogger.error("message failed [" + Integer.toHexString(lMessage.hashCode()) + "]");
            lMessage.failed();
            notifyObserver(lMessage, CMessageHandler.MSG_FAILED);
         }

         if (lErr != -1 && !mb_stopped)
         {
            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("message done [" + Integer.toHexString(lMessage.hashCode()) + "]");
            }

            lMessage.done();
            notifyObserver(lMessage, CMessageHandler.MSG_FINISHED);
            mo_MessageHandler.done(lMessage);
         }

         if (ml_sleepTime > 0)
         {
            try
            {
               Thread.sleep(ml_sleepTime);
            }
            catch (InterruptedException e)
            {
               if (moLogger.isDebugEnabled())
               {
                  moLogger.debug("queue interrupted [" + Integer.toHexString(this.hashCode()) +
                                 "]");
               }
            }
         }
      }

      if (mb_stopped)
      {
         if (lMessage != null)
         {
            lMessage.canceled();
            notifyObserver(lMessage, CMessageHandler.MSG_CANCELED);
         }
      }

      mo_toDo.clear();
   }

   /**
    * Sets cancel signal to this <code>CRunnableQueue</code>.
    */
   public void stopQueue()
   {
      mb_stopped = true;

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("queue stopped");
      }
   }

   private int notifyObserver(IMessage message, String asType)
   {
      CRegisteredObserver lReg;
      IObserver lObs;
      int lErr = 0;

      // toDo
      // mo_Observer lokal clonen !
      //
      for (int i = 0; i < mo_Observer.size(); i++)
      {
         lReg = (CRegisteredObserver) mo_Observer.elementAt(i);

         if (lReg.getEvent() == null || lReg.getEvent().equals(message.getName()))
         {
            lObs = lReg.getObserver();

            if (moLogger.isDebugEnabled())
            {
               moLogger.debug("notifying observer [" + lObs + "] message type [" + asType + "]");
            }

            lObs.notify(message, asType);
         }
      }

      return lErr;
   }

   private static Logger moLogger = Logger.getLogger(CMessageHandler.class);

   /** MessageHandler als Parent-Objekt */
   private CMessageHandler mo_MessageHandler;

   /** Warteschlange von Befehlen */
   private CPriorityQueue mo_toDo;

   /** alle registrierten Observer */
   private Vector mo_Observer;

   /** erzwungene Ruhepausen */
   private long ml_sleepTime;

   /** läuft die Queue endlos ? */
   private boolean mb_infinite;

   /** Abbruch der Verarbeitung ? */
   private boolean mb_stopped;
}


/**
 * Observer class for <code>CMessageHandler</code> and <code>CRunnableQueue</code>.
 */
class CRegisteredObserver
{
   public CRegisteredObserver(IObserver regObserver, String regEvent)
   {
      mo_Observer = regObserver;
      mEvent = regEvent;
   }

   public String getEvent()
   {
      return mEvent;
   }

   public IObserver getObserver()
   {
      return mo_Observer;
   }

   private IObserver mo_Observer;
   private String mEvent;
}
