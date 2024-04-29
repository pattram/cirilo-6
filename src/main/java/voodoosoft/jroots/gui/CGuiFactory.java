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



/**
 * Title:        Voodoo Soft Java Framework<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Stefan Wischnewski<p>
 * Company:      Voodoo Soft<p>
 * @author Stefan Wischnewski
 * @version 1.0
 */
package voodoosoft.jroots.gui;

import org.w3c.dom.*;

import org.xml.sax.*;

import voodoosoft.jroots.core.*;

import java.awt.Container;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;

import javax.swing.JComponent;

import javax.xml.parsers.*;


/**
 * The CGuiFactory rebuilds complete Swing GUIs from XML documents.
 */
public class CGuiFactory extends CObject
{
   public CGuiFactory(String asDefaultPackage) //throws ClassNotFoundException, NoSuchMethodException
   {
      moBinder = new CPropertyBinder();
      msDefaultPackage = asDefaultPackage;
      moLazyBuildings = new HashMap();

      //addDefaultBindings();
   }

   public void addDefaultBindings() throws ClassNotFoundException, NoSuchMethodException
   {
      addPropertyBinding("text", "JMenu", "setText", new String[] { "java.lang.String" });
      addPropertyBinding("mnemonic", "JMenu", "setMnemonic", new Class[] { char.class });
      addPropertyBinding("enabled", "JMenu", "setEnabled", new Class[] { boolean.class });

      addPropertyBinding("text", "JMenuItem", "setText", new String[] { "java.lang.String" });
      addPropertyBinding("mnemonic", "JMenuItem", "setMnemonic", new Class[] { char.class });
      addPropertyBinding("enabled", "JMenuItem", "setEnabled", new Class[] { boolean.class });

      addPropertyBinding("text", "JCheckBoxMenuItem", "setText", new String[] { "java.lang.String" });
      addPropertyBinding("mnemonic", "JCheckBoxMenuItem", "setMnemonic", new Class[] { char.class });
      addPropertyBinding("enabled", "JCheckBoxMenuItem", "setEnabled", new Class[] { boolean.class });
      addPropertyBinding("selected", "JCheckBoxMenuItem", "setSelected",
                         new Class[] { boolean.class });
   }

   public void addPropertyBinding(String asProperty, String asClass, String asMethod,
                                  String[] asArgClasses)
                           throws ClassNotFoundException, NoSuchMethodException
   {
      Class[] loArgs;

      loArgs = new Class[asArgClasses.length];

      for (int i = 0; i < asArgClasses.length; i++)
      {
         loArgs[i] = Class.forName(asArgClasses[i]);
      }

      moBinder.addSetBinding(asProperty, Class.forName(msDefaultPackage + "." + asClass), asMethod,
                             loArgs);
   }

   public void addPropertyBinding(String asProperty, String asClass, String asMethod,
                                  Class[] aoArgClasses)
                           throws ClassNotFoundException, NoSuchMethodException
   {
      moBinder.addSetBinding(asProperty, Class.forName(msDefaultPackage + "." + asClass), asMethod,
                             aoArgClasses);
   }

   public Container createContainer(String asClass, boolean abDefaultPackage)
                             throws CInvalidNameException
   {
      Class loClass;
      Container loContainer = null;
      boolean lbThrow = false;

      try
      {
         if (asClass != "")
         {
            if (abDefaultPackage)
            {
               asClass = msDefaultPackage + "." + asClass;
            }

            loClass = Class.forName(asClass);
            loContainer = (Container) loClass.newInstance();
         }
         else
         {
            lbThrow = true;
         }
      }
      catch (ClassNotFoundException ex)
      {
         lbThrow = true;
      }
      catch (InstantiationException ex)
      {
         lbThrow = true;
      }
      catch (IllegalAccessException ex)
      {
         lbThrow = true;
      }

      // TODO
      if (lbThrow)
      {
         throw new CInvalidNameException(asClass);
      }

      return loContainer;
   }

   public CGuiComposite createGuiFromClass(String asClassName)
                                    throws CGuiCreationFailureException, IllegalAccessException, 
                                           InstantiationException
   {
      return createGuiFromClass(asClassName, false);
   }

   /**
    * Builds Gui from a XML-document.
    * @param asXMLDocument XML-file name
    * @return root of created Gui
    */
   public CGuiComposite createGuiFromXML(String asXMLDocument)
                                  throws CGuiCreationFailureException
   {
      return createGuiFromXML(asXMLDocument, false);
   }

   public CGuiComposite createGuiFromXML(String asXMLDocument, boolean abLazyBuild)
                                  throws CGuiCreationFailureException
   {
      CGuiComposite loGui = null;
      Container loRootContainer = null;
      Document loDoc = null;

      try
      {
         // 1. Versuch
         try
         {
            loDoc = readXml(asXMLDocument);
         }
         catch (IOException ex)
         {
         }

         if (loDoc == null)
         {
            // 2. Versuch
            InputStream loDocStream = this.getClass().getClassLoader().getResourceAsStream(asXMLDocument);
            loDoc = readXml(loDocStream);
         }

         if (loDoc != null)
         {
            if (!abLazyBuild)
            {
               loGui = new CGuiComposite(asXMLDocument);
               buildGUI(loGui, loDoc);
            }
            else
            {
               loGui = new CGuiComposite(this, asXMLDocument);

               /** @todo reuse documents */
               moLazyBuildings.put(new Integer(loGui.hashCode()), loDoc);
            }
         }
         else
         {
            throw new CGuiCreationFailureException("File: " + asXMLDocument);
         }
      }
      catch (Exception ex)
      {
         throw new CGuiCreationFailureException(ex, asXMLDocument);
      }

      return loGui;
   }

   public CGuiComposite createGuiFromXML(InputStream loXMLStream, boolean abLazyBuild)
                                  throws CGuiCreationFailureException
   {
      CGuiComposite loGui = null;
      Container loRootContainer = null;
      Document loDoc = null;

      try
      {
         try
         {
            loDoc = readXml(loXMLStream);
         }
         catch (IOException ex)
         {
         }

         if (loDoc != null)
         {
            if (!abLazyBuild)
            {
               loGui = new CGuiComposite(loXMLStream.toString());
               buildGUI(loGui, loDoc);
            }
            else
            {
               loGui = new CGuiComposite(this, loXMLStream.toString());

               /** @todo reuse documents */
               moLazyBuildings.put(new Integer(loGui.hashCode()), loDoc);
            }
         }
         else
         {
            throw new CGuiCreationFailureException("File: " + loXMLStream.toString());
         }
      }
      catch (Exception ex)
      {
         throw new CGuiCreationFailureException(ex, loXMLStream.toString());
      }

      return loGui;
   }
   
   public Document readXml(String asXMLDocument)
                    throws SAXParseException, SAXException, IOException, 
                           ParserConfigurationException
   {
      Document loDoc = null;
      DocumentBuilderFactory loDocBuilderFactory;
      DocumentBuilder loDocBuilder;
      File loXMLFile;

      loDocBuilderFactory = DocumentBuilderFactory.newInstance();
      loDocBuilder = loDocBuilderFactory.newDocumentBuilder();

      loXMLFile = new File(asXMLDocument);

      if (loXMLFile.exists())
      {
         loDoc = loDocBuilder.parse(loXMLFile);
      }

      return loDoc;
   }

   public Document readXml(InputStream aoXMLDocument)
                    throws SAXParseException, SAXException, IOException, 
                           ParserConfigurationException
   {
      Document loDoc = null;
      DocumentBuilderFactory loDocBuilderFactory;
      DocumentBuilder loDocBuilder;

      loDocBuilderFactory = DocumentBuilderFactory.newInstance();
      loDocBuilder = loDocBuilderFactory.newDocumentBuilder();

      loDoc = loDocBuilder.parse(aoXMLDocument);

      return loDoc;
   }

   protected void buildGUI(CGuiComposite aoComposite, Document aoXML)
                    throws CGuiCreationFailureException
   {
      Container loRootContainer = null;

      try
      {
         if (aoXML == null)
         {
            aoXML = (Document) moLazyBuildings.remove(new Integer(aoComposite.hashCode()));
         }

         loRootContainer = addChilds(aoXML.getDocumentElement(), null);
         aoComposite.setRootComponent(loRootContainer);
      }
      catch (Exception ex)
      {
         throw new CGuiCreationFailureException(ex, aoXML.toString());
      }
   }

   private Container addChilds(Element aoParent, Container aoGuiParent)
                        throws CInvalidPropertyArgsException, CInvalidPropertyException
   {
      NodeList loChilds;
      NamedNodeMap loAttributes;
      Element loChild;
      Container loContainer;
      Container loGuiParent = aoGuiParent;
      JComponent loJComp;
      String lsName;
      String lsClass = "";
      short loNodeType;
      Object[] loArgs;

      // create container (from class attribute)
      //      lsName = aoParent.getNodeName();
      lsClass = aoParent.getAttribute(CLASS_TAG);

      try
      {
         loContainer = createContainer(lsClass, true);
      }
      catch (CInvalidNameException ex)
      {
         throw new CInvalidPropertyArgsException(null, CLASS_TAG, ex.getMessage());
      }

      if (loContainer != null)
      {
         if (aoGuiParent != null)
         {
            aoGuiParent.add(loContainer);
         }
         else
         {
            loGuiParent = loContainer;
         }
      }

      // set attributes
      loAttributes = aoParent.getAttributes();
      loArgs = new Object[1];

      for (int i = 0; i < loAttributes.getLength(); i++)
      {
         lsName = loAttributes.item(i).getNodeName();

         if (lsName.equals(NAME_TAG))
         {
            //            if (loContainer instanceof JComponent)
            //            {
            //               loJComp = (JComponent) loContainer;
            loContainer.setName(loAttributes.item(i).getNodeValue());

            //               loJComp.putClientProperty(CGuiManager.getWidgetNameKey(), loAttributes.item(i).getNodeValue());
            //            }
            // TODO
            //          else
            //               throw new CGuiCreationFailureException("Only
         }
         else if (!lsName.equals(CLASS_TAG))
         {
            loArgs[0] = loAttributes.item(i).getNodeValue();
            moBinder.setProperty(loContainer, lsName, loArgs);
         }
      }

      // process child elements
      loChilds = aoParent.getChildNodes();

      for (int i = 0; i < loChilds.getLength(); i++)
      {
         loNodeType = loChilds.item(i).getNodeType();

         if (loNodeType == Node.ELEMENT_NODE)
         {
            loChild = (Element) loChilds.item(i);
            addChilds(loChild, loContainer);
         }
      }

      return loGuiParent;
   }

   private CGuiComposite createGuiFromClass(String asClassName, boolean abLazyBuild)
                                     throws CGuiCreationFailureException, IllegalAccessException, 
                                            InstantiationException
   {
      CGuiComposite loComposite;

      try
      {
         loComposite = (CGuiComposite) Class.forName(asClassName).newInstance();
      }
      catch (Exception ex)
      {
         throw new CGuiCreationFailureException(ex, asClassName);
      }

      return loComposite;
   }

   private final String CLASS_TAG = "class";
   private final String NAME_TAG = "name";
   private CPropertyBinder moBinder;
   private String msDefaultPackage;
   private HashMap moLazyBuildings;
}
