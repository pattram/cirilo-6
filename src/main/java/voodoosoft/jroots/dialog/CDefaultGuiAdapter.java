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

import voodoosoft.jroots.application.CDefaultAccessManager;
import voodoosoft.jroots.core.*;
import voodoosoft.jroots.core.gui.CLimitDocument;
import voodoosoft.jroots.exception.*;
import voodoosoft.jroots.gui.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import java.lang.reflect.Constructor;

import java.sql.Timestamp;

import java.text.*;

import java.util.Date;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.*;


/**
 * Default <code>IGuiAdapter</code> implementation for Swing components.
 * Can be connected to a <code>CDefaultAccessManager</code> in order to control
 * widget enabling.
 */
public class CDefaultGuiAdapter extends CAbstractGuiAdapter
{
   /**
    * Creates a new adapter for the specified GUIComposite.
    */
   public CDefaultGuiAdapter(IGuiComposite aoComposite)
   {
      super(aoComposite);

      moDisabledColor = new Color(224, 224, 224);
      moDisabledTextColor = Color.black;
   }

   public void setAccessManager(CDefaultAccessManager aoAccMan)
   {
      moAccMan = aoAccMan;
   }

   public static void setDateFormat(DateFormat aoDateFormat)
   {
      moDateFormat = aoDateFormat;
   }

   /**
    * Sets widget to new (converted, if necessary) value.
    * The widget class decides if the value needs to be converted before set
    * (ancestor class is listed, the same is true of inherited classes):
    * <li> <code>JFormattedTextField</code>: sets value property
    * <li> <code>JTextComponent</code>: sets text property, <code>toString</code> converts given value to string
    * <li> <code>AbstractButton</code>: value selects/deselects button, must be of class <code>Boolean</code>
    * <li> <code>JComboBox</code>: selects item, if combo box model is a <code>CDynamicComboBoxModel</code>, value must be a valid item key,
    * otherwise the item is directly selected in invoking <code>setSelectedItem</code> of <code>JComboBox</code>
    * <li> JList: selects one or several items, value must be a <code>Vector</code> containing items to select. If the widget list model
    * is of class <code>CKeyValueListModel</code>, the items are treated as key values and are
    * decoded before selecting, otherwise the items are selected directly with <code>setSelectedValue</code> of <code>JList</code>.
    *
    * @param asWidget widget name
    * @param aoValue new widget value
    * @throws CInvalidNameException
    * @throws CInvalidDataSourceException
    * @see voodoosoft.jroots.dialog.CKeyValueListModel
    * @see voodoosoft.jroots.dialog.CDynamicComboBoxModel
    */
   public void setData(String asWidget, Object aoValue)
                throws CInvalidNameException, CInvalidDataSourceException
   {
      int liItem;
      JComponent loWidget;
      JTextComponent loText;
      JFormattedTextField loFormField;
      JComboBox loCombo;
      JList loList;
      AbstractButton loButton;
      DefaultComboBoxModel loComboModel;
      CKeyValueListModel loListModel;
      Object[] loValues;
      Vector loVector;

      loWidget = moComposite.getWidget(asWidget);

      if (loWidget instanceof JFormattedTextField)
      {
         loFormField = (JFormattedTextField) loWidget;
         loFormField.setValue(aoValue);
      }
      else if (loWidget instanceof JTextComponent)
      {
         loText = (JTextComponent) loWidget;

         if (aoValue != null)
         {
            loText.setText(aoValue.toString());
         }
         else
         {
            loText.setText(msEmptyString);
         }
      }
      else if (loWidget instanceof JComboBox)
      {
         loCombo = (JComboBox) loWidget;

         if (aoValue == null)
         {
            aoValue = msEmptyString;
         }

         if (loCombo.getModel() instanceof CDynamicComboBoxModel)
         {
            ((CDynamicComboBoxModel) loCombo.getModel()).setSelectedItemKey(aoValue);
         }
         else
         {
            loCombo.setSelectedItem(aoValue);
         }
      }
      else if (loWidget instanceof JList)
      {
         loList = (JList) loWidget;

         if (aoValue == null)
         {
            loList.clearSelection();

            return;
         }

         loValues = loList.getSelectedValues();
         loVector = (Vector) aoValue;

         if (loList.getModel() instanceof CKeyValueListModel)
         {
            loListModel = (CKeyValueListModel) loList.getModel();

            for (int i = 0; i < loVector.size(); i++)
            {
               loList.setSelectedValue(loListModel.getItem(loVector.get(i)), false);
            }
         }
         else
         {
            for (int i = 0; i < loVector.size(); i++)
            {
               loList.setSelectedValue(loVector.get(i), false);
            }
         }
      }
      else if (loWidget instanceof AbstractButton)
      {
         loButton = (AbstractButton) loWidget;

         if (aoValue == null)
         {
            loButton.setSelected(false);
         }
         else
         {
            loButton.setSelected(((Boolean) aoValue).booleanValue());
         }
      }
      else
      {
         // TODO
      }
   }

   /**
    * Sets colors for disabled widgets.
    * @param aoDisabledColor background color
    * @param aoDisabledTextColor text color
    */
   public void setDisabledColor(Color aoDisabledColor, Color aoDisabledTextColor)
   {
      moDisabledColor = aoDisabledColor;
      moDisabledTextColor = aoDisabledTextColor;
   }

   /**
    * Not yet implemented.
    * @param asWidget
    * @return always true
    * @throws CInvalidNameException
    */
   public boolean isEmpty(String asWidget) throws CInvalidNameException
   {
      /** @todo isEmpty */
      JComponent loWidget = moComposite.getWidget(asWidget);

      return true;
   }

   /**
    * Enables or disables specified widget.
    * If this <code>CDefaultGuiAdapter</code> is connected to a <code>CDefaultAccessManager</code>
    * enabling widgets is only possible if the <code>CDefaultAccessManager</code> agrees after
    * consulting its access rules.
    * Child widgets will not be processed.
    * @param asWidget widget
    * @param abEnabled true to enable widgets
    * @see #setAccessManager
    */
   public void setEnabled(String asWidget, boolean abEnabled)
                   throws CInvalidNameException
   {
      setEnabled(asWidget, abEnabled, false);
   }

   /**
    * Enables or disables specified widget(s).
    * If this <code>CDefaultGuiAdapter</code> is connected to a <code>CDefaultAccessManager</code>
    * enabling widgets is only possible if the <code>CDefaultAccessManager</code> agrees after
    * consulting its access rules.
    * @param asWidget widget
    * @param abEnabled true to enable widgets
    * @param abProcessChildren true to process child widgets recursively
    * @see #setAccessManager
    */
   public void setEnabled(String asWidget, boolean abEnabled, boolean abProcessChildren)
                   throws CInvalidNameException
   {
      JComponent loWidget = moComposite.getWidget(asWidget);

      //      JTextComponent loText;
      //      if (loWidget instanceof JTextComponent)
      //      {
      //         loText = (JTextComponent) loWidget;
      //         loText.setDisabledTextColor(moDisabledTextColor);
      //         loText.setBackground(abEnabled ? Color.white : moDisabledColor);
      //      }
      if (abProcessChildren)
      {
         setEnabledBranch(loWidget, abEnabled);
      }
      else
      {
         tryEnable(loWidget, abEnabled);
      }
   }

   public boolean isEnabled(String asWidget) throws CInvalidNameException
   {
      JComponent loWidget = moComposite.getWidget(asWidget);

      return loWidget.isEnabled();
   }

   /**
    * Gets input value of given widget.
    * The value and type returned depends upon the widget class
    * (ancestor class is listed, the same is true for inherited classes):
    * <li> <code>JFormattedTextField</code>: committed field value
    * <li> <code>JTextComponent</code>: String of text property (null for empty strings)
    * <li> <code>AbstractButton</code>: Boolean of selection status
    * <li> <code>JComboBox</code>: if a <code>CDynamicComboBoxModel</code> is used, the selected item key,
    * otherwise the selected item
    * <li> JList: if a <code>CKeyValueListModel</code> is used, a <code>Vector</code> of selected key values
    * otherwise a <code>Vector</code> of selected values
    * @param asWidget
    * @return widget value
    * @throws CInvalidNameException
    * @see voodoosoft.jroots.dialog.CDynamicComboBoxModel
    * @see voodoosoft.jroots.dialog.CKeyValueListModel
    */
   public Object getInput(String asWidget)
                   throws CInvalidNameException //, CInvalidDataSourceException
   {
      int liItem;
      JComponent loWidget;
      JTextComponent loText;
      JFormattedTextField loFormField;
      JComboBox loCombo;
      AbstractButton loButton;
      JList loList;
      Object loValue = null;
      Object[] loValues;
      Vector loVector;
      CKeyValueListModel loListModel;

      loWidget = moComposite.getWidget(asWidget);
      
      if (loWidget instanceof JFormattedTextField)
      {
         loFormField = (JFormattedTextField) loWidget;
         
         try
         {
            if (loFormField.isValid())
            {
               loFormField.commitEdit();
               loValue = loFormField.getValue();
            }            
         }
         catch (ParseException e)
         {
            //throw new RuntimeException(e);
         }
      }
      else if (loWidget instanceof JTextComponent)
      {
         loText = (JTextComponent) loWidget;
         loValue = loText.getText();

         if (loValue.equals(msEmptyString))
         {
            loValue = null;
         }
      }
      else if (loWidget instanceof AbstractButton)
      {
         loButton = (AbstractButton) loWidget;
         loValue = new Boolean(loButton.isSelected());
      }
      else if (loWidget instanceof JComboBox)
      {
         loCombo = (JComboBox) loWidget;

         if (loCombo.getModel() instanceof CDynamicComboBoxModel)
         {
            loValue = ((CDynamicComboBoxModel) loCombo.getModel()).getSelectedItemKey();
         }
         else
         {
            loValue = loCombo.getSelectedItem();
         }
      }
      else if (loWidget instanceof JList)
      {
         loList = (JList) loWidget;
         loValues = loList.getSelectedValues();
         loVector = new Vector();

         if (loList.getModel() instanceof CKeyValueListModel)
         {
            loListModel = (CKeyValueListModel) loList.getModel();

            for (int i = 0; i < loValues.length; i++)
            {
               loVector.add(loListModel.getItemKey(loValues[i]));
            }
         }
         else
         {
            for (int i = 0; i < loValues.length; i++)
            {
               loVector.add(loValues[i]);
            }
         }

         loValue = loVector;
      }
      else
      {
         // TODO
      }

      return loValue;
   }

   /**
    * Not yet implemented.
    * @param asWidget
    * @param aiRow
    * @return null
    * @throws CInvalidNameException
    */
   public Object getInput(String asWidget, int aiRow) throws CInvalidNameException
   {
      int liItem;
      JComponent loWidget;
      JTextComponent loText;
      JComboBox loCombo;
      JTable loTable;
      AbstractButton loButton;
      JList loList;
      Object loValue = null;
      Object[] loValues;
      Vector loVector;

      loWidget = moComposite.getWidget(asWidget);

      //      if (loWidget instanceof JTable)
      //      {
      //         loTable = (JTable) loWidget;
      //         loTable.getModel().fi
      //         loTable.getValueAt()
      //      }
      //      else
      //      {
      //      }
      return loValue;
   }

   /**
    * Returns widget value directly as string.
    * @param asWidget
    * @return widget value
    * @throws CInvalidNameException
    * @see #getInput
    */
   public String getText(String asWidget) throws CInvalidNameException
   {
      JComponent loWidget;
      JTextComponent loText;
      JComboBox loCombo;
      AbstractButton loButton;
      JList loList;
      String loValue = "";
      Vector loVector;
      Object loItem;

      loWidget = moComposite.getWidget(asWidget);

      if (loWidget instanceof JTextComponent)
      {
         loText = (JTextComponent) loWidget;
         loValue = loText.getText();
      }
      else if (loWidget instanceof AbstractButton)
      {
         loButton = (AbstractButton) loWidget;
         loValue = String.valueOf(loButton.isSelected());
      }
      else if (loWidget instanceof JComboBox)
      {
         loCombo = (JComboBox) loWidget;
         loItem = loCombo.getSelectedItem();

         if (loItem != null)
         {
            loValue = loItem.toString();
         }
         else
         {
            loValue = "";
         }
      }
      else
      {
         // TODO
      }

      return loValue;
   }

   public void setVisible(String asWidget, boolean abVisible)
                   throws CInvalidNameException
   {
      JComponent loWidget = moComposite.getWidget(asWidget);

      loWidget.setVisible(abVisible);
   }

   public void acceptMap(CWidgetMap aoMap, IPropertyBag aoDestination)
                  throws CInvalidNameException, CInvalidPropertyException, CTransformException
   {
      CMapIterator loIt;
      CWidgetMapping loMapping;
      Object loData;
      String lsData;
      ITransformer loTrans;
      CPropertyClass loClass;

      moLogger.debug("acceptMap");

      loIt = (CMapIterator) aoMap.iterator();

      while (loIt.hasNext())
      {
         loMapping = (CWidgetMapping) loIt.next();

         // readonly ?
         loClass = loMapping.getTypeClass();
         loClass = (loClass == null ? aoDestination.getPropertyClass(loMapping.getSource()) : loClass);

         if (loClass != null && loClass.readonly)
         {
            continue;
         }

         // get widget data
         loData = getInput(loMapping.getWidget());

         // format Data
         try
         {
            loTrans = loMapping.getTransformer();

            if (loTrans != null)
            {
               loData = loTrans.transformBack(loData);
            }

            if (loClass != null)
            {
               if (loData != null)
               {
                  if (!loClass.javaClass.equals(String.class) && loData.equals(msEmptyString))
                  {
                     loData = null;
                  }

                  if (loData instanceof String)
                  {
                     loData = castDataType2((String) loData, loClass);
                  }
               }
            }
         }
         catch (CTransformException ex)
         {
            ex.setSource(loMapping.getWidget());
            throw ex;
         }
         catch (Exception ex)
         {
            throw new CInvalidPropertyException(loMapping.getSource(), aoDestination, ex);
         }

         try
         {
            // set destination property
            aoDestination.setProperty(loMapping.getSource(), loData);

            if (moLogger.isDebugEnabled())
            {
               lsData = loData != null ? loData.toString() : "[null]";
               moLogger.debug("widget [" + loMapping.getWidget() + "] property [" +
                              loMapping.getSource() + "] value [" + lsData + "]");
            }
         }
         catch (CPropertyReadonlyException ex)
         {
            // sollte nicht auftreten ...
            CException.record(ex, this);
         }
      }
   }

   public void clearMap(CWidgetMap aoMap)
                 throws CInvalidPropertyException, CInvalidNameException, 
                        CInvalidDataSourceException, CTransformException
   {
      CMapIterator loIt;
      CWidgetMapping loMapping;
      Object loData = null;
      ITransformer loTrans;

      loIt = (CMapIterator) aoMap.iterator();

      while (loIt.hasNext())
      {
         loMapping = (CWidgetMapping) loIt.next();
         loData = null;

         // format Data
         loTrans = loMapping.getTransformer();

         if (loTrans != null)
         {
            try
            {
               loData = loTrans.transform(loData);
            }
            catch (CTransformException ex)
            {
               ex.setSource(loMapping.getWidget());
               throw ex;
            }
         }

         // show Data
         setData(loMapping.getWidget(), loData);
      }
   }

   public void requestFocus(String asWidget) throws CInvalidNameException
   {
      JComponent loWidget = moComposite.getWidget(asWidget);

      loWidget.requestFocus();
   }

   /**
    * Prepares widgets of specified <code>CWidgetMap</code> for usage.
    * <p>
    * <code>setupWidgets</code> will loop through the <code>CWidgetMap</code>
    * and setup all widgets of type <code>JTextField</code>:
    * The <code>CPropertyClass</code> of every single <code>CWidgetMapping</code> decides, what to do:
    * <li> alignment is set, for String properties to left, for all others to right
    * <li> the number of characters the user can enter will be limited depending on the properties'
    * <code>precision</code> and <code>scale</code>.
    * @param aoDataSource
    * @param aoMap
    * @throws CInvalidPropertyException
    * @throws CInvalidNameException
    * @see voodoosoft.jroots.core.CPropertyClass
    * @see voodoosoft.jroots.core.gui.CLimitDocument
    * @see CWidgetMap#setMappingTypes
    * @see CWidgetMapping
    */
   public void setupWidgets(IPropertyBag aoDataSource, CWidgetMap aoMap)
                     throws CInvalidPropertyException, CInvalidNameException
   {
      CMapIterator loIt;
      CWidgetMapping loMapping;
      CPropertyClass loPropClass;
      int liSize;
      JComponent loWidget;
      JTextField loTextWidget;

      loIt = (CMapIterator) aoMap.iterator();

      while (loIt.hasNext())
      {
         loMapping = (CWidgetMapping) loIt.next();

         loWidget = (JComponent) getWidget(loMapping.getWidget());

         if (loWidget instanceof JTextField)
         {
            loTextWidget = (JTextField) (loWidget);
            loPropClass = loMapping.getTypeClass();

            if (loPropClass != null)
            {
               // alignment
               if (Number.class.isAssignableFrom(loPropClass.javaClass))
               {
                  loTextWidget.setHorizontalAlignment(JTextField.RIGHT);
               }
               else
               {
                  loTextWidget.setHorizontalAlignment(JTextField.LEFT);
               }

               // edit limit
               if (loPropClass.displaySize > 0)
               {
                  liSize = loPropClass.displaySize;
               }
               else
               {
                  liSize = loPropClass.scale > 0 ? loPropClass.precision + 1 : loPropClass.precision;

                  if (liSize == 0)
                  {
                     liSize = Integer.MAX_VALUE;
                  }
               }

               if (loTextWidget.getDocument() instanceof CLimitDocument)
               {
                  CLimitDocument doc = (CLimitDocument) loTextWidget.getDocument();
                  doc.setLimit(liSize); 
               }
               else
               {
                  loTextWidget.setDocument(new CLimitDocument(liSize, true));
               }
                  
            }
         }
      }
   }

   public void showMap(IPropertyBag aoDataSource, CWidgetMap aoMap)
                throws CInvalidPropertyException, CInvalidNameException, 
                       CInvalidDataSourceException, CTransformException
   {
      CMapIterator loIt;
      CWidgetMapping loMapping;
      Object loData;
      String lsData;
      ITransformer loTrans;

      if (moLogger.isDebugEnabled())
      {
         moLogger.debug("showMap");
      }

      loIt = (CMapIterator) aoMap.iterator();

      while (loIt.hasNext())
      {
         loMapping = (CWidgetMapping) loIt.next();

         // get Data
         loData = aoDataSource.getProperty(loMapping.getSource());

         // format Data
         loTrans = loMapping.getTransformer();

         if (loTrans != null)
         {
            try
            {
               loData = loTrans.transform(loData);
            }
            catch (CTransformException ex)
            {
               ex.setSource(loMapping.getWidget());
               throw ex;
            }
         }

         // show Data
         setData(loMapping.getWidget(), loData);

         if (moLogger.isDebugEnabled())
         {
            lsData = loData != null ? loData.toString() : "[null]";
            moLogger.debug("widget [" + loMapping.getWidget() + "] property [" +
                           loMapping.getSource() + "] value [" + lsData + "]");
         }
      }
   }

   protected Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }

   private void setEnabledBranch(Component aoParentComponent, boolean abEnabled)
   {
      Component[] loComponents;
      Container loContainer;
      JMenu loMenu;
      JComponent loJComponent;
      int li;
      Object loClientName;
      String lsName;
      boolean lbMayEnable;

      if (aoParentComponent instanceof Container)
      {
         loContainer = (Container) aoParentComponent;

         if (aoParentComponent instanceof JMenu)
         {
            loMenu = (JMenu) aoParentComponent;
            loComponents = loMenu.getMenuComponents();
         }
         else
         {
            loComponents = loContainer.getComponents();
         }

         // durch alle Child-Components
         for (li = 0; li < loComponents.length; li++)
         {
            setEnabledBranch(loComponents[li], abEnabled);
         }

         if (aoParentComponent instanceof JComponent)
         {
            if (!(aoParentComponent instanceof JLabel))
            {
               tryEnable(aoParentComponent, abEnabled);
            }
         }
      }
   }

   private Object castDataType2(String asData, CPropertyClass loClass)
                         throws Exception
   {
      Constructor loCons;
      Object loData = null;
      String[] loArgs;
      java.util.Date loUtilDate;

      if (loClass.javaClass.equals(String.class))
      {
         loData = asData;
      }
      else if (loClass.javaClass.equals(Date.class))
      {
         loUtilDate = moDateFormat.parse(asData);
         loData = new java.sql.Date(loUtilDate.getTime());
      }
      else if (loClass.javaClass.equals(Timestamp.class))
      {
         loUtilDate = moDateFormat.parse(asData);
         loData = new java.sql.Timestamp(loUtilDate.getTime());
      }
      else if (loClass.javaClass.equals(java.sql.Date.class))
      {
         loUtilDate = moDateFormat.parse(asData);
         loData = new java.sql.Date(loUtilDate.getTime());
      }      

      //      else if (Number.class.isAssignableFrom(loClass.javaClass))
      //      {
      //         loData = moNumberFormat.parse(asData);
      //      }
      else
      {
         loCons = loClass.javaClass.getConstructor(new Class[] { String.class });
         loArgs = new String[1];
         loArgs[0] = asData;
         loData = loCons.newInstance(loArgs);
      }

      return loData;
   }

   private void tryEnable(Component loWidget, boolean abEnable)
   {
      boolean lbMayEnable;
      JTabbedPane loTabs;
      int liIdx;

      if (abEnable)
      {
         if (moAccMan != null && loWidget.getName() != null)
         {
            lbMayEnable = moAccMan.mayEnable(loWidget.getName());
         }
         else
         {
            lbMayEnable = true;
         }

         if (lbMayEnable)
         {
            if (loWidget.getParent() instanceof JTabbedPane)
            {
               loTabs = (JTabbedPane) loWidget.getParent();
               liIdx = loTabs.indexOfComponent(loWidget);
               loTabs.setEnabledAt(liIdx, true);
            }
            else
            {
               loWidget.setEnabled(true);
            }
         }
      }
      else
      {
         if (loWidget.getParent() instanceof JTabbedPane)
         {
            loTabs = (JTabbedPane) loWidget.getParent();
            liIdx = loTabs.indexOfComponent(loWidget);
            loTabs.setEnabledAt(liIdx, false);
         }
         else
         {
            loWidget.setEnabled(false);
         }
      }
   }

   private static DateFormat moDateFormat = DateFormat.getDateInstance();

   //   private static NumberFormat moNumberFormat = NumberFormat.getNumberInstance();
   protected static Logger moLogger = Logger.getLogger(CDefaultGuiAdapter.class);
   private String msEmptyString = new String("");
   private Color moDisabledColor;
   private Color moDisabledTextColor;
   private CDefaultAccessManager moAccMan;
}
