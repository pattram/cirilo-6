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

import java.awt.Container;


/**
 * Interface for classes working as delegator object for <code>CDialog</code>s to handle low-level GUI tasks
 * like listening to window events or creating the underlying core GUI component.
 * Used by objects of super class <code>CDialog</code>.
 * @see CDialog
 */
public interface IDialogCoreDelegate
{
   /**
    * Returns the core component, e.g. <code>JDialog</code> or <code>JInternalFrame</code>.
    * @return core component
    */
   public Container getCore();

   /**
    * Sets name of the core component.
    * @param name
    */
   public void setName(String name);

   /**
    * Sets root or first component of which the GUI is based, often a <code>JPanel</code>.
    * @param root
    */
   public void setRootComponent(Container root);

   /**
    * Returns true if core component is visible.
    * @return showing flag
    */
   public boolean isShowing();

   /**
    * Sets title of the core component.
    * @param title
    */
   public void setTitle(String title);

   /**
    * Called to clean up ressources.
    *
    */
   public void cleanUp();

   /**
    * Invoked when the core component needs to be created and set up.
    *
    */
   public void createCore();

   /**
    * Makes core component visible.
    *
    */
   public void show();

   /**
    * Brings core component to front.
    *
    */
   public void toFront();
}
