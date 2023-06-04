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


package voodoosoft.jroots.dialog;

import org.apache.log4j.Logger;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.gui.CEventListener;
import voodoosoft.jroots.core.gui.IEventHandler;
import voodoosoft.jroots.exception.CException;

import javax.swing.JOptionPane;


/**
 * Dialog class offering standard handling of access managing, validating and updating.
 */
public class CDefaultDialog extends CDialog implements IEventHandler
{
   protected CDefaultDialog()
   {
      setUpdateQuestion("Save changes ?");
   }

   public IAccessContext getAccessContext()
   {
      return null;
   }

   public void setAccessManager(IAccessManager aoAccessManager)
   {
      moAccMan = aoAccessManager;
   }

   public void setUpdateQuestion(String asQuestion)
   {
      msUpdateQuestion = asQuestion;
   }

   /**
    * Method starting close process of dialog.
    * Overrides {@link CDialog#close} for calling validate and update logic.
    * Close process can be canceled using method <code>closing</code>.
    * Subclasses of <code>CDefaultDialog</code> may implement <code>cleaningUp</code>
    * to release ressources.
    * @see CDialog#cleaningUp
    */
   public void close()
   {
      boolean lbClose;

      if (moLogger.isDebugEnabled())
      {
         if (this.getName() != null)
         {
            moLogger.debug(this.getName() + "#close");
         }
      }

      // only delegate to descendants if dialog is open
      if (moCore.getCore() != null)
      {      
         lbClose = closing();

         if (lbClose)
         {
            lbClose = optionalUpdate();
         }
      }
      else
      {
         lbClose = true;
      }
      
      if (lbClose)
      {
         cleanup();
      }
   }

   public void handlerRemoved(CEventListener aoHandler)
   {
   }

   /**
    *  Method to start update process of dialog.
    */
   public boolean update()
   {
      boolean lbOK;
      StringBuffer lsMsg = new StringBuffer();

      lbOK = validating(lsMsg);

      if (lbOK)
      {
         lbOK = updating();

         if (lbOK)
         {
            setDirty(false);
         }
      }
      else
      {
         if (lsMsg.length() == 0)
         {
            lsMsg.append("Nicht spezifizierter Fehler aufgetreten !");
         }

         recordMessage(lsMsg.toString());
      }

      return lbOK;
   }

   protected void cleanup()
   {
      if (moLogger.isDebugEnabled())
      {
         if (this.getName() != null)
         {
            moLogger.debug(this.getName() + "#cleanup");
         }
      }

      if (moAccMan != null)
      {
         moAccMan.passivate();
      }

      super.cleanup();
   }

   /**
    * Internal method initiating dialog opening.
    * Calls {@link #opened}, {@link #show} and {@link voodoosoft.jroots.application.IAccessManager#execRules}.
    */
   protected void openCallback()
   {
      /** @todo restore position and size */
      IAccessContext loCurrentContext;

      try
      {
         if (moLogger.isDebugEnabled())
         {
            if (this.getName() != null)
            {
               moLogger.debug(this.getName() + "#openCallback");
            }
         }

         clearOpenException();

         CEventListener.setBlocked(true);

         opened();

         // call access manager
         if (moAccMan != null)
         {
            loCurrentContext = getAccessContext();

            if (loCurrentContext != null)
            {
               moAccMan.execRules(loCurrentContext);
            }
         }

         show();
         addDirtyListener();
      }
      catch (Exception ex)
      {
         /** @todo exception handling */
         cleanup();
         CException.record(ex, this, true);

         //         setOpenException(ex);
      }
      finally
      {
         CEventListener.setBlocked(false);
      }
   }

   protected boolean optionalUpdate()
   {
      int liAnswer;
      boolean lbOK;
      boolean lbGoOn = true;

      if (isDirty())
      {
         liAnswer = shallUpdate();

         if (liAnswer == JOptionPane.CANCEL_OPTION)
         {
            lbGoOn = false;
         }
         else if (liAnswer == JOptionPane.YES_OPTION)
         {
            lbOK = update();

            if (!lbOK)
            {
               lbGoOn = false;
            }
         }
         else
         {
            // Änderungen verwerfen
            setDirty(false);
         }
      }

      return lbGoOn;
   }

   /**
    * Default implementation of recording validation messages.
    * Shows MessageDialog.
    */
   protected void recordMessage(String asMsg)
   {
      JOptionPane.showMessageDialog(getCoreDialog(), asMsg, getTitle(),
                                    JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Confirms updating when dialog is being closed.
    */
   protected int shallUpdate()
   {
      int liAnswer;

      liAnswer = JOptionPane.showConfirmDialog(getCoreDialog(), msUpdateQuestion, getTitle(),
                                               JOptionPane.YES_NO_CANCEL_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);

      return liAnswer;
   }

   /**
    * Method to do dialog specific updating.
    */
   protected boolean updating()
   {
      return true;
   }

   /**
    * Method to place individual validate logic.
    */
   protected boolean validating(StringBuffer asMsg)
   {
      return true;
   }

   protected static Logger moLogger = Logger.getLogger(CDefaultDialog.class);
   private String msUpdateQuestion;
   private IAccessManager moAccMan = null;
}
