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


package voodoosoft.jroots.data;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.*;

import java.io.*;

import javax.xml.parsers.*;


/**
 * Implementation of ISQLStorage using XML for persisting SQL commands.
 */
public class CXmlSqlStorage implements ISQLStorage
{
   public CXmlSqlStorage(InputStream aoXMLFile)
   {
      moXMLFile = aoXMLFile;           
   }

   public CXmlSqlStorage(String asXMLFile)
   {
      msXMLFile = asXMLFile;
   }


   public void setSQL(String asSQL, String asName, int aiType)
   {
      /**@todo: Implement this voodoosoft.data.ISQLStorage method*/
      throw new java.lang.UnsupportedOperationException("Method setSQL() not yet implemented.");
   }

   public String getSQL(String asName, int aiType) throws CSQLNotFoundException
   {
      String lsPath;
      String lsType = "";
      Element loDocRoot;
      Node loNode = null;
      NodeList loList;

      try
      {
         lsType = CSQLEngine.getSQLType(aiType);
         loDocRoot = moSQLDoc.getDocumentElement();
         lsPath = "//SQL/" + lsType + "[@name='" + asName + "']";
         loNode = XPathAPI.selectSingleNode(loDocRoot, lsPath);
      }
      catch (Exception ex)
      {
         throw new CSQLNotFoundException(ex, msXMLFile, asName, lsType);
      }

      if (loNode != null)
      {
         return ((Element) loNode).getAttribute("command");
      }
      else
      {
         throw new CSQLNotFoundException(msXMLFile, asName, lsType);
      }
   }

   public void load() throws CSQLNotFoundException
   {
      DocumentBuilderFactory loDocBuilderFactory;
      DocumentBuilder loDocBuilder;
      File loXMLFile;

      try
      {
         loDocBuilderFactory = DocumentBuilderFactory.newInstance();
         loDocBuilder = loDocBuilderFactory.newDocumentBuilder();
  
         if ( moXMLFile != null )
         {
             moSQLDoc = loDocBuilder.parse(moXMLFile);
         }
         else
         {        
             loXMLFile = new File(msXMLFile);
             moSQLDoc = loDocBuilder.parse(loXMLFile);
         }
      }
      catch (Exception ex)
      {
         throw new CSQLNotFoundException(ex, msXMLFile, null, null);
      }
   }

   public void save()
   {
      /**@todo: Implement this voodoosoft.data.ISQLStorage method*/
      throw new java.lang.UnsupportedOperationException("Method save() not yet implemented.");
   }

   private String msXMLFile;
   private InputStream moXMLFile;
   private Document moSQLDoc;
}
