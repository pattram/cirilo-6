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

import voodoosoft.jroots.core.gui.*;
import voodoosoft.jroots.exception.*;
import voodoosoft.jroots.gui.*;
import voodoosoft.jroots.message.*;

import java.awt.*;


/**
 * Abstract ancestor class for dialogs.
 * Encapsulates one Swing GUI objects like <code>JDialog</code> or <code>JInternalFrame</code>.
 * The appearance is set by the internal dialog delegate of type <code>IDialogCoreDelegate</code>.
 * To create new <code>CDialog</code> objects, dialog creators are used. 
 * @see IDialogCreator
 * @see IDialogCoreDelegate
 * @see CDefaultDialog
 */
public abstract class CDialog extends CCommand implements IDirtyListener, IEventHandler, Cloneable
{
   /**
    * Constructs new <code>CDialog</code>.
    * The concrete appearance is determined by the specified <code>IDialogCoreDelegate</code> and GUI name.
    * @param delegate internal object for delegating GUI specific tasks
    * @param asGuiName name of GUI as registered by <code>CGuiManager</code>
    * @param asDialogTitle title for this dialog
    * @param asDialogName name for this dialog
    */
   public CDialog(IDialogCoreDelegate delegate, String asGuiName, String asDialogTitle,
                  String asDialogName)
   {
      setDelegate(delegate);
      init(asGuiName, asDialogTitle, asDialogName);
   }

   protected CDialog()
   {
   }

   public Container getCoreDialog()
   {
      return moCore.getCore();
   }

   /**
    *
    * @param delegate
    */
   public void setDelegate(IDialogCoreDelegate delegate)
   {
      if (moCore != null)
      {
         moCore.cleanUp();
      }

      moCore = delegate;
   }

   /**
    * Changes the internal dirty flag.
    * @param ab_IsDirty true to enable flag
    */
   public void setDirty(boolean ab_IsDirty)
   {
      mbIsDirty = ab_IsDirty;
   }

   /**
    * Determines if at least one dialog widget has been touched.
    * For example, entered text or clicking a checkbox would set the dialog's dirty flag.
    * @return true if dialog is dirty
    * @see #setDirty
    */
   public boolean isDirty()
   {
      return mbIsDirty;
   }

   /**
    * Gives the default <code>IGuiAdapter</code> of this <code>CDialog</code>.
    */
   public IGuiAdapter getGuiAdapter() throws CInvalidNameException
   {
      return moGuiManager.getAdapter(moGuiManager.getGuiComposite(msGuiName));
   }

   /**
    * Returns the underlying {@link IGuiComposite} object, which
    * must be registered by the used {@link CGuiManager}.
    */
   public IGuiComposite getGuiComposite() throws CInvalidNameException // TODO exception
   {
      return getGuiManager().getGuiComposite(getGuiName());
   }

   /**
    * Sets <code>CGuiManager</code> for this dialg.
    * Must be called before opening it.
    * @param aoManager
    */
   public void setGuiManager(CGuiManager aoManager)
   {
      moGuiManager = aoManager;
   }

   /**
    * Returns <code>CGuiManager</code> of this dialg.
    * @return <code>CGuiManager</code>
    */
   public CGuiManager getGuiManager()
   {
      return moGuiManager;
   }

   /**
    * Returns name of this dialogs {@link IGuiComposite}.
    */
   public String getGuiName()
   {
      return msGuiName;
   }

   /**
    * Sets title of this dialog
    * @param asTitle title
    */
   public void setTitle(String asTitle)
   {
      msTitle = asTitle;
      moCore.setTitle(msTitle);
   }

   /**
    * Returns dialog title.
    */
   public String getTitle()
   {
      return msTitle;
   }

   /**
    * Adds object to be notified whenever this dialog gets "dirty", meaning the user entered data
    * in one of the textfields for instance.    
    */
   public void addDirtyListener()
   {
      if (moCore != null && moCore.getCore() != null)
      {
         moChangeManager = new CChangeManager(this);
         moChangeManager.addDirtyListener(moCore.getCore());
      }
   }

   /**
    * Creates cloned copy of this dialog.    
    * @throws CloneNotSupportedException
    */
   public CDialog cloneThis() throws CloneNotSupportedException
   {
      return (CDialog) this.clone();
   }

   /**
    * Initiates close process of this dialog.
    * Can be canceled using method <code>closing</code>.
    * Subclasses of <code>CDefaultDialog</code> may implement <code>cleaningUp</code>
    * to release ressources.
    * @see #closing
    * @see #cleaningUp
    */
   public void close()
   {
      boolean lbContinue;

      if (moLogger.isDebugEnabled())
      {
         if (this.getName() != null)
         {
            moLogger.debug(this.getName() + "#close");
         }
      }

      if (moCore != null && moCore.getCore() != null)
         lbContinue = closing();
      else
         lbContinue = true;

      if (lbContinue)
         cleanup();
   }

   public int exec() throws Exception
   {
      open();

      return 0;
   }

   /**
    * Opens this dialog using the specified <code>CGuiManager</code>.
    * @param aoGuiManager <code>CGuiManager</code> to use
    * @throws COpenFailedException    
    * @see #open()
    */
   public void open(CGuiManager aoGuiManager) throws COpenFailedException
   {
      setGuiManager(aoGuiManager);
      open();
   }

   /**
    * Opens this dialog using the previous set <code>CGuiManager</code>.
    * When <code>open</code> is called
    * <li> the internal <code>IDialogCoreDelegate</code> sets up the dialog component
    * <li> a new core GUI component will be created
    * <li> the dialog's <code>CGuiManager</code> is used to get the gui by calling {@link voodoosoft.jroots.gui.CGuiManager#getGuiComposite getGuiComposite}
    * <li> the <code>CGuiManager</code> will recursively add all named widgets to it's widget registry with {@link voodoosoft.jroots.gui.CGuiManager#addWidgetTree addWidgetTree}
    * <li> <code>opened</code> is called (for descendant classes to overwrite)
    * <li> <code>show</code> is called (for descendant classes to overwrite)
    * @throws COpenFailedException
    * @see #setGuiManager
    * @see #opened
    * @see #show
    */
   public void open() throws COpenFailedException
   {
      Exception loOpenException;

      try
      {
         if (moCore != null && moCore.isShowing())
         {
            CEventListener.setBlocked(true);

            moCore.toFront();
            show();

            CEventListener.setBlocked(false);

            return;
         }

         if (moGuiManager == null)
         {
            throw new COpenFailedException("Call 'setGuiManager()' before opening the dialog");
         }

         moCore.createCore();
         moCore.setRootComponent(getGuiComposite().getRootComponent());
         moCore.setName(getName());
         moCore.setTitle(msTitle);

         moGuiManager.clearWidgetTree(getGuiName());
         moGuiManager.addWidgetTree(getGuiName());

         moCore.show();
      }
      catch (Exception ex)
      {
         throw new COpenFailedException(ex);
      }

      /** @todo exception handling */
      if (mbExceptionThrown)
      {
         loOpenException = moOpenException;
         clearOpenException();

         if (loOpenException instanceof COpenFailedException)
         {
            throw ((COpenFailedException) loOpenException);
         }
         else
         {
            throw new COpenFailedException(loOpenException);
         }
      }
   }

   /**
    * Internal method.
    * Should not be overwritten.
    */
   protected void setOpenException(Exception ex)
   {
      mbExceptionThrown = true;
      moOpenException = ex;
   }

   /**
    * Called each time the window is activated.
    */
   protected void activated()
   {
   }

   /**
    * Method for descendant classes to release ressources.
    * @see #closing
    */
   protected void cleaningUp()
   {
   }

   /**
    * Method to clean up this dialog properly.
    * Normally, this should not be overwritten, use {@link #closing} instead.
    */
   protected void cleanup()
   {
      if (moLogger.isDebugEnabled())
      {
         if (this.getName() != null)
         {
            moLogger.debug(this.getName() + "#cleanup");
         }
      }

      CEventListener.removeListener(this);

      if (moCore != null && moCore.getCore() != null)
         cleaningUp();

      if (moGuiManager != null)
      {
         try
         {
            moGuiManager.clearWidgetTree(getGuiName());
            moGuiManager.releaseGuiComposite(getGuiName());
         }
         catch (Exception ex)
         {
            CException.record(ex, this);
         }
      }

      if (moChangeManager != null)
      {
         if (moCore != null && moCore.getCore() != null)
         {
            moChangeManager.remove(moCore.getCore());
            moChangeManager = null;
         }
      }

      if (moCore != null)
      {
         moCore.cleanUp();
      }

      moOpenException = null;
   }

   /**
    * Internal method.
    * Should not be overwritten.
    */
   protected void clearOpenException()
   {
      mbExceptionThrown = false;
      moOpenException = null;
   }

   /**
    * Dialog is being closed.
    * <code>closing</code> is the place to check whether closing the dialog
    * is allowed and cancel the process if needed.
    * @return if false, closing will be canceled
    */
   protected boolean closing()
   {
      return true;
   }

   /**
    * Initializes this dialog.
    * Called from constructor{@link #CDialog}.
    * @param asGuiName gui name
    * @param asDialogTitle dialog title
    * @param asDialogName dialog name
    */
   protected void init(String asGuiName, String asDialogTitle, String asDialogName)
   {
      msTitle = asDialogTitle;
      msGuiName = asGuiName;
      setName(asDialogName);
      mbIsDirty = false;      
   }

   /**
    * Internal callback of the dialogs <code> WindowAdapter</code>.
    * Descendant classes should use {@link #opened} and {@link #show}.
    */
   protected void openCallback()
   {
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

   /**
    * Method invoked after the dialog has been opened (one time).
    * Place for descendant classes to initialize the dialog
    */
   protected void opened() throws COpenFailedException
   {
   }

   /**
    * Method to set up dialog.
    *
    */
   protected void show() throws CShowFailedException
   {
   }

   protected static Logger moLogger = Logger.getLogger(CDefaultDialog.class);

   /** name of used {@link IGuiComposite} */
   private String msGuiName;

   /** underlying Swing Dialog */
   protected IDialogCoreDelegate moCore;

   /** {@link CGuiManager} to work with */
   private CGuiManager moGuiManager;
   private CChangeManager moChangeManager;

   /** text of titlebar */
   private String msTitle;

   /** flag indicating any (user) changes */
   private boolean mbIsDirty;
   private boolean mbExceptionThrown = false;
   private Exception moOpenException;
}
