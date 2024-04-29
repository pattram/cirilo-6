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

package voodoosoft.jroots.gui;

import java.awt.Container;

import java.util.Map;

import javax.swing.JComponent;


/**
 * CGuiComposite encapsulates one hierarchy of Swing components. Each component (or widget)
 * is identified by its unique name. Widget names represent the path through the
 * hierarchy of components beginning from the root component.
 * e.g. "JPanel1.JTabbedPane1.JTextField3"
 * @see CGuiManager
 * @see CGuiFactory
 */
public interface IGuiComposite
{
   public String getName();

   /**
    * @exception    
    * Returns root component of this Gui.
    * @return root component
    */
   public Container getRootComponent();

   /**
    * @exception CInvalidNameException
    * Returns a reference to the specified Widget.
    * @param asName name of widget
    * @return the widget as JComponent
    */
   public JComponent getWidget(String asName) throws CInvalidNameException;

   /**
    * @exception    
    * Gets table of all available widgets.
    * @return widget table
    */
   public Map getWidgetTable();
}
