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



package voodoosoft.jroots.core.gui;

import voodoosoft.jroots.exception.CException;

import java.awt.*;
import java.awt.event.MouseEvent;

import java.net.URL;

import javax.swing.*;
import javax.swing.table.*;


/**
 * Collection of static functions related to gui purposes.
 */
public class CGuiTools
{
   private CGuiTools()
   {
   }

   /**
    * Returns number of clicked column.
    * @param aoTable
    * @param e mouse click event
    */
   public static int getClickedColumn(JTable aoTable, MouseEvent e)
                               throws Exception
   {
      int liColumn = -1;

      if (e.getClickCount() == 2)
      {
         liColumn = aoTable.columnAtPoint(e.getPoint());
         liColumn = aoTable.convertColumnIndexToModel(liColumn);
      }

      return liColumn;
   }

   public static void setImagePath(String asPath)
   {
      ssImagePath = asPath;
   }

   public static void setLookAndFeel(String asLookAndFeel)
   {
      String lsDefault = null;

      try
      {
         lsDefault = UIManager.getCrossPlatformLookAndFeelClassName();

         if (asLookAndFeel == null)
         {
            UIManager.setLookAndFeel(lsDefault);
         }
         else
         {
            UIManager.setLookAndFeel(asLookAndFeel);
         }
      }
      catch (Exception ex)
      {
         try
         {
            UIManager.setLookAndFeel(lsDefault);
         }
         catch (Exception ex2)
         {
            CException.record(ex2, null);
         }
      }
   }

   /**
    * Centers the specified window relative to the screen size.
    */
   public static void center(Window aoWin)
   {
	  
      Dimension loScreenSize;
      Dimension loLabelSize;
      int liX;
      int liY;

      loScreenSize = aoWin.getToolkit().getScreenSize();
      loLabelSize = aoWin.getSize();

      liX = loScreenSize.width / 2 - loLabelSize.width / 2;
      liY = loScreenSize.height / 2 - loLabelSize.height / 2;
      aoWin.setLocation(liX, liY);
	 
   }
   
      /**
    * Centers the specified window relative to the screen size.
    */
   public static void center(JDialog aoWin)
   {
	  
      Dimension loScreenSize;
      Dimension loLabelSize;
      int liX;
      int liY;

      loScreenSize = aoWin.getToolkit().getScreenSize();
      loLabelSize = aoWin.getSize();

      liX = loScreenSize.width / 2 - loLabelSize.width / 2;
      liY = loScreenSize.height / 2 - loLabelSize.height / 2;
      aoWin.setLocation(liX, liY);
	 
   }

   /**
    * Hides table column.
    * @param aoTable table of column
    * @param asColumn column name to hide
    */
   public static void hideTableColumn(JTable aoTable, String asColumn)
   {
      TableColumn tc;

      tc = aoTable.getColumn(asColumn);
      tc.setMinWidth(0);
      tc.setMaxWidth(0);
      tc.setPreferredWidth(0);
   }

   public static ImageIcon loadIcon(String asFileName)
   {
      ImageIcon loIcon;
      URL loIconURL;

      //      if (ssImagePath != null)
      //         loIconURL = ClassLoader.getSystemResource(ssImagePath + "/" + asFileName);
      //      else
      //         loIconURL = ClassLoader.getSystemResource(asFileName);
      //      loIcon = new ImageIcon(loIconURL);
      loIcon = new ImageIcon(asFileName);

      return loIcon;
   }

   public static void scrollToRow(JTable aoTable, int aiRow, int aiColumn, boolean abSelectRow)
   {
      aoTable.scrollRectToVisible(aoTable.getCellRect(aiRow, aiColumn, true));

      if (abSelectRow)
      {
         aoTable.setRowSelectionInterval(aiRow, aiRow);
      }
   }

   public static void sizeColumns(JTable aoTable)
   {
      JTableHeader loHeader;
      TableCellRenderer loRenderer;
      TableColumn loColumn;
      Component loComp;

      for (int i = 0; i < aoTable.getColumnCount(); i++)
      {
         loHeader = aoTable.getTableHeader();
         loRenderer = loHeader.getDefaultRenderer();
         loColumn = aoTable.getColumn(aoTable.getColumnName(i));

         //         loRenderer = loColumn.getHeaderRenderer();
         loComp = loRenderer.getTableCellRendererComponent(aoTable, loColumn.getHeaderValue(),
                                                           false, false, 0, 0);

         loColumn.setPreferredWidth((int) loComp.getPreferredSize().getWidth());
      }

      aoTable.sizeColumnsToFit(-1);
   }

   private static String ssImagePath = ".";
}
