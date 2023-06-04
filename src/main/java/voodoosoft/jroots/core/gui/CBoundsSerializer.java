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

import voodoosoft.jroots.data.*;

import java.awt.Container;
import java.awt.Dimension;



/**
 * Helper class to store individual user settings of <code>Container</code> objects.
 * Required database table structure:
 * <p><code>
 * <br>[UserSetting]  [int]          NOT NULL (Primary key)
 * <br>[UserID]       [varchar] (50) NOT NULL
 * <br>[ContainerName][varchar] (50) NOT NULL
 * <br>[x]            [int]          NOT NULL
 * <br>[y]            [int]          NOT NULL
 * <br>[width]        [int]          NOT NULL
 * <br>[height]       [int]          NOT NULL
 * </code>
 * <p>
 * <p>The name of the table is free to choose, but not the column names.
 * A sequence supplier of type <code>ISequence</code> (sequence name is "UserSetting") is required.
 */
public class CBoundsSerializer
{
   /**
    * Creates new <code>CBoundsSerializer</code>.
    * @param seq Sequence supplier to get primary keys for the database table, asks for sequence of name "UserSetting"
    * @param dsFactory
    * @param DBtable
    */
   public CBoundsSerializer(ISequence seq, IDataStoreFactory dsFactory, String DBtable)
   {
      moDSFactory = dsFactory;
      moSeq = seq;
      msDBTable = DBtable;
   }


   /**
    * Restores bounds for the specified container and user.
    * 
    * @param asUserID
    * @param aoContainer
    * @param asName
    * @param aoConn
    * @throws Exception
    */
   public boolean load(String asUserID, Container aoContainer, String asName, CConnection aoConn)
             throws Exception
   {
      CDataStore loDS;      
      int liRow;
      Integer liX, liY, liWidth, liHeight;

      loDS = (CDataStore) moDSFactory.createObject("SELECT * FROM " + msDBTable +
                                                   " WHERE UserID = '" + asUserID +
                                                   "' AND ContainerName = '" + asName + "'", false);
      loDS.retrieve(aoConn);

      if (loDS.getRowCount() == 1)
      {
         liX = (Integer) loDS.getColumn(1, "x");
         liY = (Integer) loDS.getColumn(1, "y");
         liWidth = (Integer) loDS.getColumn(1, "width");
         liHeight = (Integer) loDS.getColumn(1, "height");
         
         aoContainer.setSize(new Dimension(liWidth.intValue(), liHeight.intValue()));
         aoContainer.setLocation(liX.intValue(), liY.intValue());

         return true;
      }      
      return false;         
   }
   
   /**
    * Saves bounds user and container.
    * 
    * @param asUserID
    * @param aoContainer
    * @param asName
    * @param aoConn
    * @throws Exception
    */   
   public void save(String asUserID, Container aoContainer, String asName, CConnection aoConn)
             throws Exception
   {
      CDataStore loDS;
      Integer loRow;
      Integer loValue;
      Object liID = null;
      int liRow;

      // read existing settings
      loDS = (CDataStore) moDSFactory.createObject("SELECT * FROM " + msDBTable +
                                                   " WHERE UserID = '" + asUserID +
                                                   "' AND ContainerName = '" + asName + "'", false);
      loDS.setUpdateTable(msDBTable);
      loDS.retrieve(aoConn);

      // too many rows found ? -> delete
      if (loDS.getRowCount() > 1)
      {
         while (loDS.getRowCount() > 0)         
            loDS.deleteRow(1);
      }
       
      if (loDS.getRowCount() == 0)
      {
         liID = moSeq.nextObject("UserSetting");

         liRow = loDS.insertRow();
         loDS.setColumn(liRow, "UserSetting", liID);
         loDS.setColumn(liRow, "UserID", asUserID);
         loDS.setColumn(liRow, "ContainerName", asName);
      }

      loValue = new Integer(aoContainer.getX());
      loDS.setColumn(1, "x", loValue);
         
      loValue = new Integer(aoContainer.getY());
      loDS.setColumn(1, "y", loValue);

      loValue = new Integer(aoContainer.getWidth());
      loDS.setColumn(1, "width", loValue);

      loValue = new Integer(aoContainer.getHeight());
      loDS.setColumn(1, "height", loValue);

      loDS.update(aoConn);
   }

   public void resetSettings(String asUserID, String asName, CConnection aoConn) throws Exception
   {
      CDataStore loDS;
      
      // read existing settings
      loDS = (CDataStore) moDSFactory.createObject("SELECT * FROM " + msDBTable +
                                                   " WHERE UserID = '" + asUserID +
                                                   "' AND ContainerName = '" + asName + "'", false);      
      loDS.retrieve(aoConn);
      loDS.setHideDeletedRows(false);
      for (int i = 1; i <= loDS.getRowCount(); i++)
      {
         loDS.deleteRow(i);
      }
      
      loDS.update(aoConn);
   }

   private String msDBTable;
   private ISequence moSeq;
   private IDataStoreFactory moDSFactory;
}
