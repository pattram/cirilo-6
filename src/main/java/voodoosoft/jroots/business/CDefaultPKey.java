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


package voodoosoft.jroots.business;

import voodoosoft.jroots.core.CObject;
import voodoosoft.jroots.exception.CException;

import java.util.Vector;


/**
 * Default primary key implementation.
 */
public class CDefaultPKey extends CObject implements IPrimaryKey
{
   /**
    * Creates new <code>CDefaultPKey</code>.
    * @param asEntityName entity name
    * @param aoAttributes attributes for this <code>CDefaultPKey</code>
    */
   public CDefaultPKey(String asEntityName, String[] aoAttributes)
   {
      this();

      try
      {
         setAttribs(aoAttributes);
      }
      catch (CInvalidAttributeException ex)
      {
         // should never occur
         CException.record(ex, this);
      }

      setEntityName(asEntityName);
      mbLockAttribs = true;
   }

   /**
    * Constructor for the specified entity and new-entity flag.
    * @param asEntityName
    * @param abNewEntityKey
    */
   public CDefaultPKey(String asEntityName, boolean abNewEntityKey)
   {
      this();

      setEntityName(asEntityName);
      setNewEntityKey(abNewEntityKey);
   }

   /**
    * Creates <code>CDefaultPKey</code> without set new-entity flag.
    * @param asEntityName
    */
   public CDefaultPKey(String asEntityName)
   {
      this(asEntityName, false);
   }

   /**
    * Creates <code>CDefaultPKey</code> initialised by the specified attributes and values.
    * @param aoAttributes
    * @param aoValues
    */
   public CDefaultPKey(String[] aoAttributes, Object[] aoValues)
   {
      this();

      try
      {
         setAttribs(aoAttributes);
      }
      catch (CInvalidAttributeException ex)
      {
         // should never occur
         CException.record(ex, this);
      }

      for (int i = 0; i < aoValues.length; i++)
      {
         moValues.add(aoValues[i]);
      }

      mbLockAttribs = true;
   }

   /**
    * Constructor for empty <code>CDefaultPKey</code> without attributes.
    */
   private CDefaultPKey()
   {
      moKeys = new Vector();
      moValues = new Vector();
   }

   /**
    * Sets attributes of this key to the given values.
    * @param aoAttributes new key attribute values
    */
   public void setAttribs(String[] aoAttributes) throws CInvalidAttributeException
   {
      if (mbLockAttribs)
      {
         throw new CInvalidAttributeException(null, "attributes are locked");
      }

      moKeys.removeAllElements();
      moValues.removeAllElements();

      for (int i = 0; i < aoAttributes.length; i++)
      {
         if (aoAttributes[i] == null)
         {
            throw new CInvalidAttributeException(aoAttributes[i], "[null] is not allowed");
         }

         moKeys.add(aoAttributes[i].toUpperCase());
         moValues.add(null);
      }
   }

   /**
    * Sets one key attribute to the given value.
    * @param asAttr attribute to set
    * @param aoValue attribute value
    */
   public void setAttribute(String asAttr, Object aoValue)
                     throws CInvalidAttributeException
   {
      int liIdx;

      liIdx = moKeys.indexOf(asAttr.toUpperCase());

      if (liIdx == -1)
      {
         if (mbLockAttribs)
         {
            throw new CInvalidAttributeException(asAttr, "attributes are locked");
         }

         addAttrib(asAttr);
         liIdx = moKeys.size() - 1;
      }

      moValues.set(liIdx, aoValue);
   }

   /**
    * Returns value of specified attribute.
    * @param asAttr
    * @return attribute value
    */
   public Object getAttribute(String asAttr) throws CInvalidAttributeException
   {
      int liIdx;

      liIdx = moKeys.indexOf(asAttr.toUpperCase());

      if (liIdx != -1)
      {
         return moValues.elementAt(liIdx);
      }
      else
      {
         throw new CInvalidAttributeException(asAttr, "attribute not found");
      }
   }

   /**
    * Sets entity of this <code>CDefaultPKey</code>.
    * @param asName
    */
   public void setEntityName(String asName)
   {
      msEntity = asName;
   }

   /**
    * Returns entity name of this <code>CDefaultPKey</code>.
    * @return entity name 
    */
   public String getEntityName()
   {
      return msEntity;
   }

   /**
    * Gets key values of this <code>CDefaultPKey</code>.
    */
   public Object[] getKeyValues()
   {
      return moValues.toArray();
   }

   /**
    * Determines if this <code>CDefaultPKey</code> is used to get new business objects (no database equivalent).
    * @param abNewEntityKey if true, all attribute values will be set to the new-key-value of <code>CDefaultPKey</code>
    * @see #setNewKeyValue
    */
   public void setNewEntityKey(boolean abNewEntityKey)
   {
      mbNewEntityKey = abNewEntityKey;

      if (mbNewEntityKey)
      {
         for (int i = 0; i < moKeys.size(); i++)
         {
            moValues.set(i, CDefaultPKey.moNullValue);
         }
      }
   }

   /**
    * Specifies value assigned to all attributes of new-entity <code>CDefaultPKey</code> objects.
    * The purpose is to initialize newly created objects of class <code>CDefaultEntity</code> or <code>CEntitySet</code>
    * with the proper database table structure like column names and types.
    * <p>To achieve this, retrieval arguments of the underlying SQL statements are set to the new-key-value.
    * As result, no database record will be found - but the business object received the complete
    * column structure and is ready to use.
    *
    * <p>Default new-key-value is of class <code>Integer</code>, initialized by -1.
    * @param aoValue value that will allow
    * @see voodoosoft.jroots.business.CDefaultEntity#retrieve
    * @see voodoosoft.jroots.business.CEntitySet#retrieve
    */
   public static void setNewKeyValue(Object aoValue)
   {
      CDefaultPKey.moNullValue = aoValue;
   }

   /**
    * Returns true if this <code>CDefaultPKey</code> is for retrieving new business objects (no database equivalent).
    * @return #setNewEntityKey
    */
   public boolean isNewEntityKey()
   {
      return mbNewEntityKey;
   }

   /**
    * Adds a new attribute to this key.
    * @param aoAttribute new attribute
    */
   public void addAttrib(String aoAttribute) throws CInvalidAttributeException
   {
      if (mbLockAttribs)
      {
         throw new CInvalidAttributeException(aoAttribute, "attributes are locked");
      }

      if (aoAttribute == null)
      {
         throw new CInvalidAttributeException(aoAttribute, "[null] is not allowed");
      }

      moKeys.add(aoAttribute.toUpperCase());
      moValues.add(null);
   }

   /**
    * Compares two <code>CDefaultPKey</code> objects in comparing their attribute names and values.
    * @param aoCompare
    * @return true if all attribute names and -values are equal
    */
   public boolean equals(Object aoCompare)
   {
      CDefaultPKey loPK;
      boolean lbEqual;

      if (this == aoCompare)
      {
         return true;
      }

      if (!(aoCompare instanceof CDefaultPKey))
      {
         return false;
      }

      loPK = (CDefaultPKey) aoCompare;

      if (this.moKeys.size() != loPK.moKeys.size())
      {
         return false;
      }

      lbEqual = true;

      for (int i = 0; i < moKeys.size(); i++)
      {
         if ((!moKeys.elementAt(i).equals(loPK.moKeys.elementAt(i))) ||
                (!moValues.elementAt(i).equals(loPK.moValues.elementAt(i))))
         {
            lbEqual = false;

            break;
         }
      }

      return lbEqual;
   }

   public int hashCode()
   {
      int liHash = 17;

      for (int i = 0; i < moKeys.size(); i++)
      {
         if (moKeys.get(i) != null)
         {
            liHash = liHash * 37 + moKeys.get(i).hashCode();
         }

         if (moValues.get(i) != null)
         {
            liHash = liHash * 37 + moValues.get(i).hashCode();
         }
      }

      return liHash;
   }

   public String toString()
   {
      StringBuffer lsBuf;

      lsBuf = new StringBuffer(128);

      for (int i = 0; i < moKeys.size(); i++)
      {
         lsBuf.append(moKeys.get(i));
         lsBuf.append("=");

         if (moValues.get(i) != null)
         {
            lsBuf.append(moValues.get(i).toString());
         }
         else
         {
            lsBuf.append("[null]");
         }

         lsBuf.append(" ");
      }

      return lsBuf.toString().trim();
   }

   private static Object moNullValue = new Integer(-1);
   private Vector moKeys;
   private Vector moValues;
   private String msEntity;
   private boolean mbNewEntityKey;
   private boolean mbLockAttribs;
}
