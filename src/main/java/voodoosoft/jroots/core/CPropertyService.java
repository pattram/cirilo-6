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
package voodoosoft.jroots.core;

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.debug.CLogManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.*;
import java.util.Hashtable;
import java.util.Properties;


/**
 * Service class for caching property files.
 * Every <code>CPropertyService</code> is able to handle several sets of property files.
 */
public class CPropertyService extends CObject
{
   /**
    * Creates new empty <code>CPropertyService</code>.
    */
   public CPropertyService()
   {
      moProperties = new Hashtable();
      msFiles = new Hashtable();
   }

   public Properties getProperties(String asSet)
   {
      Properties loProps;

      loProps = (Properties) moProperties.get(asSet);

      return loProps;
   }

   /**
    * Changes value of one property.
    * @param asSet keyname of property set
    * @param asKey property to change
    * @param asValue new value
    */
   public void setProperty(String asSet, String asKey, String asValue)
   {
      Properties loProps;
      String loValue;

      loProps = (Properties) moProperties.get(asSet);

      if (loProps != null)
      {
         loProps.put(asKey, asValue);
      }
   }

   /**
    * Returns value of specified property.
    * @param asSet keyname of property set
    * @param asKey keyname of property
    * @return value as String
    */
   public String getProperty(String asSet, String asKey)
   {
      Properties loProps;
      String loValue;

      loProps = (Properties) moProperties.get(asSet);

      if (loProps != null)
      {
         loValue = (String) loProps.get(asKey);
      }
      else
      {
         loValue = null;
      }

      return loValue;
   }

   public void cacheProperties(Properties aoProps, String asKey)
   {
      moProperties.put(asKey, aoProps);
   }

   /**
    * Loads property set of one property file and stores it under the given name.
    * @param asFile filename
    * @param asKey key name for property set
    * @return property set
    */
   public Properties cacheProperties(String asFile, String asKey)
                              throws CPropertySetException
   {
      Properties loProps = null;
      FileInputStream loIS = null;
      boolean lbSuccess = false;

      try
      {
         loProps = new Properties();
         loIS = new FileInputStream(asFile);

         if (loIS != null)
         {
            loProps.load(loIS);
            moProperties.put(asKey, loProps);
            msFiles.put(asKey, asFile);
            lbSuccess = true;
         }
      }
      catch (Exception ex)
      {
         throw new CPropertySetException(ex, "file [" + asFile + "], key [" + asKey + "]");
      }
      finally
      {
         try
         {
            if (loIS != null)
            {
               loIS.close();
            }
         }
         catch (Exception ex)
         {
         }
      }

      if (!lbSuccess)
      {
         throw new CPropertySetException("file [" + asFile + "], key [" + asKey + "]");
      }

      return loProps;
   }

   /**
    * Prints all system properties to <code>CLogManager</code>.
    */
   public void logSystemProperties()
   {
      Properties loProp;
      Enumeration loPropNames;
      String lsKey;

      loProp = System.getProperties();
      loPropNames = loProp.propertyNames();

      while (loPropNames.hasMoreElements())
      {
         lsKey = (String) loPropNames.nextElement();
         CLogManager.log(this, lsKey + " = " + loProp.getProperty(lsKey));
      }
   }

   /**
    * Saves properties of one property set.
    * @param asKey property set
    */
   public void saveProperties(String asKey) throws CPropertySetException
   {
      FileOutputStream loOS = null;
      Properties loProps;
      String lsFile;

      try
      {
         loProps = (Properties) moProperties.get(asKey);

         if (loProps != null)
         {
            loOS = new FileOutputStream((String) msFiles.get(asKey));
            loProps.store(loOS, "header");
         }
      }
      catch (Exception ex)
      {
         lsFile = (String) msFiles.get(asKey);

         if (lsFile == null)
         {
            lsFile = "null";
         }

         throw new CPropertySetException(ex, "file [" + lsFile + "], key [" + asKey + "]");
      }
      finally
      {
         try
         {
            if (loOS != null)
            {
               loOS.close();
            }
         }
         catch (Exception ex)
         {
         }
      }
   }

   private Hashtable msFiles;
   private Hashtable moProperties;
}
