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

import voodoosoft.jroots.business.IBasicEntitySet;
import voodoosoft.jroots.core.*;

/**
 * Title:        Voodoo Soft Java Framework
 * Description:
 * Copyright:    Copyright (c) Stefan Wischnewski
 * Company:      Voodoo Soft
 * @author Stefan Wischnewski
 * @version 1.0
 */
import java.util.HashMap;


public class CMapTransformer extends CObject implements ITransformer
{
   public CMapTransformer()
   {
      moKeyMap = new HashMap();
      moDisplayMap = new HashMap();
   }

   public CMapTransformer(IBasicEntitySet aoMapSet, String asKeyColumn, String asDisplayColumn)
                   throws Exception
   {
      this();
      addMappings(aoMapSet, asKeyColumn, asDisplayColumn);
   }

   public void addMapping(Object aoKey, Object aoDisplayValue)
   {
      moKeyMap.put(aoKey, aoDisplayValue);
      moDisplayMap.put(aoDisplayValue, aoKey);
   }

   public void addMappings(IBasicEntitySet aoMapSet, String asKeyColumn, String asDisplayColumn)
                    throws Exception
   {
      for (int i = 1; i <= aoMapSet.getRowCount(); i++)
      {
         addMapping(aoMapSet.getColumn(i, asKeyColumn),
                    aoMapSet.getColumn(i, asDisplayColumn).toString());
      }

      ;
   }

   public void clear()
   {
      moKeyMap.clear();
      moDisplayMap.clear();
   }

   public Object transform(Object aoInput)
   {
      return moKeyMap.get(aoInput);
   }

   public Object transformBack(Object asInput)
   {
      return moDisplayMap.get(asInput);
   }

   private HashMap moKeyMap;
   private HashMap moDisplayMap;
}
