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

import java.text.Format;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Container for a group of <code>Criterion</code> objects.
 * @see voodoosoft.jroots.data.CSearchEngine
 */
public class CSearchCriteria
{
   /**
    * Definition of <code>Criterion</code> to append to a given SQL <code>SELECT</code> statement.
    */
   public static class Criterion
   {
      public Criterion(String asName, String asExpression, boolean abEnumeration, boolean abWildcard)
      {
         Name = asName;
         Expression = asExpression;
         Value = null;
         disabledValue = EMPTY_STRING;
         Enumeration = abEnumeration;
         Wildcard = abWildcard;
         EnumOperator = ENUM_OP_AND;
      }

      public static final String EMPTY_STRING = "";

      /** name to identify this criterion */
      public String Name;

      /** value of this criterion */
      public Object Value;
      public String TextValue;
      public Object disabledValue;

      /** optional expression to insert */
      public String Expression;

      /** if true treat value as list of values */
      public boolean Enumeration;

      /** operator to link enum values */
      public String EnumOperator;

      /** if true, use <code>LIKE</code> operator otherwise =*/
      public boolean Wildcard;

      /** optional join expression to append to SQL*/
      public String FROMClause;
      public Format ValueFormat;
   }

   public CSearchCriteria()
   {
      moCriteria = new Hashtable();
   }

   public void setCriterionExpression(String asCrit, String asExpression)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.Expression = asExpression;
   }

   public void setDisabledValue(String asCrit, Object aoValue)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.disabledValue = aoValue;
   }

   public void setEnumeratedCriterion(String asCrit, String asEnumOperator)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.Enumeration = true;
      loCrit.EnumOperator = asEnumOperator;
   }

   public void setFROMClause(String asCrit, String asFROMClause)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.FROMClause = asFROMClause;
   }

   public void setTextValue(String asCrit, String aoValue)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.TextValue = aoValue;
   }

   public String getTextValue(String asCrit)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);

      return loCrit.TextValue;
   }

   public void setValue(String asCrit, Object aoValue)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.Value = aoValue;
   }

   public Object getValue(String asCrit)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);

      return loCrit.Value;
   }

   public void setValueFormat(String asCrit, Format aoFormat)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.ValueFormat = aoFormat;
   }

   public void setWildcardUsage(String asCrit, boolean abUseWildcard)
   {
      Criterion loCrit = (Criterion) moCriteria.get(asCrit);
      loCrit.Wildcard = abUseWildcard;
   }

   public void addCriterion(String asCrit, String asExpression, boolean abEnumeration,
                            boolean abWildcard)
   {
      Criterion loCrit = new Criterion(asCrit, asExpression, abEnumeration, abWildcard);

      moCriteria.put(asCrit, loCrit);
   }

   public void addCriterion(String asCrit)
   {
      addCriterion(asCrit, null, false, false);
   }

   public void clearCriteriaValues()
   {
      Iterator loIt;
      Criterion loCrit;
      Enumeration loCritEnum;

      loCritEnum = moCriteria.elements();

      while (loCritEnum.hasMoreElements())
      {
         loCrit = (Criterion) loCritEnum.nextElement();
         loCrit.Value = null;
         loCrit.TextValue = null;
      }
   }

   public boolean isDirty()
   {
      Iterator loIt;
      Criterion loCrit;
      Enumeration loCritEnum;
      boolean lbDirty = false;

      loCritEnum = moCriteria.elements();

      while (loCritEnum.hasMoreElements())
      {
         loCrit = (Criterion) loCritEnum.nextElement();
         if (loCrit.Value != null || loCrit.TextValue != null)
         {
            lbDirty = true;
         }
      }
      
      return lbDirty;
   }

   protected Hashtable getCriteriaMap()
   {
      return moCriteria;
   }

   /** operator for combining enumerated values with <code>AND</code>*/
   public static final String ENUM_OP_AND = " AND ";

   /** operator for combining enumerated values with <code>OR</code>*/
   public static final String ENUM_OP_OR = " OR ";
   private Hashtable moCriteria;
}
