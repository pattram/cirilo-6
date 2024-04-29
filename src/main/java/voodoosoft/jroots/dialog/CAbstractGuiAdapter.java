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
import voodoosoft.jroots.gui.CInvalidNameException;
import voodoosoft.jroots.gui.IGuiComposite;

import java.lang.Cloneable;

import java.util.Map;


/**
 * Abstract adapter class implementing the handling of the {@link IGuiComposite}.
 */
public abstract class CAbstractGuiAdapter extends CObject implements Cloneable, IGuiAdapter
{
   public CAbstractGuiAdapter()
   {
      setName(getClass().getName());
   }

   public CAbstractGuiAdapter(IGuiComposite aoComposite)
   {
      setName(getClass().getName());

      moComposite = aoComposite;
   }

   public void setGuiComposite(IGuiComposite aoComposite)
   {
      moComposite = aoComposite;
   }

   /**
    * Returns reference to the specified widget of this adapters {@link IGuiComposite}.
    * @param asName widget name
    * @return JComponent reference
    */
   public Object getWidget(String asName) throws CInvalidNameException
   {
      return moComposite.getWidget(asName);
   }

   public Map getWidgetTable()
   {
      return moComposite.getWidgetTable();
   }

   public Object cloneObject() throws CloneNotSupportedException
   {
      return clone();
   }

   protected Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }

   protected IGuiComposite moComposite;
}
