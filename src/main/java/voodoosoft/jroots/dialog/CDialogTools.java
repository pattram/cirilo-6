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
import voodoosoft.jroots.core.gui.CActionListener;
import voodoosoft.jroots.core.gui.CItemListener;
import voodoosoft.jroots.core.gui.CMappedComboBoxModel;
import voodoosoft.jroots.dialog.CDynamicComboBoxModel;
import voodoosoft.jroots.dialog.CKeyValueListModel;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;


/**
 * Various useful methods for the dialog layer.
 */
public class CDialogTools
{
   private CDialogTools()
   {
   }

   /**
    * Supplies specified combo box with data of given entity set.
    * @param aoBox JComboBox to feed
    * @param aoData data source
    * @param asColumn column to show
    */
   public static void setItems(JComboBox aoBox, IBasicEntitySet aoData, String asColumn,
                               boolean abEmptyItem) throws Exception
   {
      aoBox.removeAllItems();

      if (abEmptyItem)
      {
         aoBox.addItem("");
      }

      for (int i = 1; i <= aoData.getRowCount(); i++)
      {
         aoBox.addItem(aoData.getColumn(i, asColumn));
      }
   }
 
   public static CActionListener createButtonListener(CDialog loDialog, String lsButton,
                                                      String asHandlerMethod)
                                               throws Exception
   {
      AbstractButton loButton;
      CActionListener loListener;

      loButton = (AbstractButton) loDialog.getGuiComposite().getWidget(lsButton);
      loListener = new CActionListener(loButton, loDialog, asHandlerMethod);

      return loListener;
   }
   
   public static CItemListener createItemListener(CDialog loDialog, String lsButton,
                                                         String asHandlerMethod)
                                                  throws Exception
   {
      AbstractButton loButton;
      CItemListener loListener;

      loButton = (AbstractButton) loDialog.getGuiComposite().getWidget(lsButton);
      loListener = new CItemListener(loButton, loDialog, asHandlerMethod);

      return loListener;
   }
      
   public static CMappedComboBoxModel createComboBoxModel(IBasicEntitySet aoData,
                                                          String asKeyColumn, String asDisplayColumn)
                                                   throws Exception
   {
      CMappedComboBoxModel loModel = new CMappedComboBoxModel();

      for (int i = 1; i <= aoData.getRowCount(); i++)
      {
         loModel.addMapping(aoData.getColumn(i, asKeyColumn), aoData.getColumn(i, asDisplayColumn));
      }

      ;

      return loModel;
   }

   public static void setupComboBox(CDialog aoDialog, IBasicEntitySet aoData, String asComboBox,
                                    String asKeyColumn, String asDisplayColumn,
                                    CMapTransformer aoTrans, String asListenerMethod,
                                    boolean abEmptyItem)
                             throws Exception
   {
      JComboBox loCombo;

      aoTrans.clear();
      aoTrans.addMappings(aoData, asKeyColumn, asDisplayColumn);
      loCombo = (JComboBox) aoDialog.getGuiComposite().getWidget(asComboBox);
      CDialogTools.setItems(loCombo, aoData, asDisplayColumn, abEmptyItem);

      if (asListenerMethod != null && !asListenerMethod.equals(""))
      {
         new CItemListener(loCombo, aoDialog, asListenerMethod);
      }
   }

   public static void setupComboBox(CDialog aoDialog, IBasicEntitySet aoData, String asComboBox,
                                    String asKeyColumn, String asDisplayColumn,
                                    String asListenerMethod, boolean abEmptyItem)
                             throws Exception
   {
      JComboBox loCombo;
      CDynamicComboBoxModel loComboModel;

      loCombo = (JComboBox) aoDialog.getGuiComposite().getWidget(asComboBox);
      loComboModel = new CDynamicComboBoxModel(aoData, asKeyColumn, asDisplayColumn, abEmptyItem);
      loCombo.setModel(loComboModel);

      if (asListenerMethod != null && !asListenerMethod.equals(""))
      {
         new CItemListener(loCombo, aoDialog, asListenerMethod);
      }
   }

   public static void setupList(CDialog aoDialog, IBasicEntitySet aoData, String asList,
                                String asKeyColumn, String asDisplayColumn)
                         throws Exception
   {
      JList loList;
      CKeyValueListModel loListModel;

      loList = (JList) aoDialog.getGuiComposite().getWidget(asList);
      loListModel = new CKeyValueListModel(aoData, asKeyColumn, asDisplayColumn);
      loList.setModel(loListModel);
   }
}
