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

import voodoosoft.jroots.core.*;
import voodoosoft.jroots.gui.*;

import java.util.Map;


/**
 * Interface for objects working as gui adapter.
 * <li> the <code>IGuiAdapter</code> offers a standarized and comfortable approach to the most
 * frequently used widget functions.
 * <li> the <code>CWidgetMap</code> defines the relations between properties and widgets.
 * <li> values of a complete widget set can be read or set invoking <code>acceptMap</code> respectively <code>showMap</code>
 * <li> all classes implementing <code>IPropertyBag</code> can act as data source and destination.
 * <li> Every <code>IGuiAdapter</code> works for exactly one <code>IGuiComposite</code>.
 * @see CWidgetMap
 * @see voodoosoft.jroots.core.IPropertyBag
 * @see voodoosoft.jroots.gui.IGuiComposite
 */
public interface IGuiAdapter
{
   /** Sets value of given widget. */
   public void setData(String asWidget, Object aoValue)
                throws CInvalidNameException, CInvalidDataSourceException;

   /** Returns true if given widget is empty. */
   public boolean isEmpty(String asWidget) throws CInvalidNameException;

   /** Enables or disables specified widget */
   public void setEnabled(String asWidget, boolean abEnabled)
                   throws CInvalidNameException;

   /** Enables or disables widget and its child widgets if desired */
   public void setEnabled(String asWidget, boolean abEnabled, boolean abProcessChildren)
                   throws CInvalidNameException;

   /** Returns true if widget is enabled (editable) */
   public boolean isEnabled(String asWidget) throws CInvalidNameException;

   /** Specifies the underlying gui this adapter is working for */
   public void setGuiComposite(IGuiComposite aoComposite);

   /** Gets input value of given widget. */
   public Object getInput(String asWidget) throws CInvalidNameException;

   /** Gets input value of given widget and row. */
   public Object getInput(String asWidget, int aiRow) throws CInvalidNameException;

   /** Shows or hides specified widget */
   public void setVisible(String asWidget, boolean abVisible)
                   throws CInvalidNameException;

   /** Returns widget of the specified name */
   public Object getWidget(String asName) throws CInvalidNameException;

   /** Returns table of widgetname/widget pairs for this adapter. */
   public Map getWidgetTable();

   /** Sets all destination properties specified in the widget map to current input values */
   public void acceptMap(CWidgetMap aoMap, IPropertyBag aoDestination)
                  throws CInvalidPropertyException, CInvalidNameException, CTransformException;

   /** Clears properties of specified widget map (to null or empty string). */
   public void clearMap(CWidgetMap aoMap)
                 throws CInvalidPropertyException, CInvalidNameException, 
                        CInvalidDataSourceException, CTransformException;

   public Object cloneObject() throws CloneNotSupportedException;

   /** Sets focus to given widget. */
   public void requestFocus(String asWidget) throws CInvalidNameException;

   /** Prepares widget for usage, e.g. sets alignment and edit limit. */
   public void setupWidgets(IPropertyBag aoDataSource, CWidgetMap aoMap)
                     throws CInvalidPropertyException, CInvalidNameException;

   /** Supplies all widgets of the given widget map with data of the specified property bag. */
   public void showMap(IPropertyBag aoDataSource, CWidgetMap aoMap)
                throws CInvalidPropertyException, CInvalidNameException, 
                       CInvalidDataSourceException, CTransformException;
}
