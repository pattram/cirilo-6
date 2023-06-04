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


package voodoosoft.jroots.core;

import java.io.Serializable;

import java.lang.reflect.*;

import java.util.HashMap;


/**
 * <code>CPropertyBinder</code> maps property names to any object method.
 * <p>To every mapped property belongs a get and/or set method.
 * <p>When properties are get or set, the corresponding method is dynamically invoked
 * with the aid of the Java reflection package.
 * <p><code>CPropertyBinder</code> is thread safe.
 *
 */
public class CPropertyBinder extends CObject implements Serializable
{
   /**
    * Creates new property binder.
    */
   public CPropertyBinder()
   {
      moSetMethods = new HashMap();
      moGetMethods = new HashMap();
   }

   /**
    * Sets value of a bound property by dynamic invokation of the mapped set-method.
    * @param aoObject target object
    * @param asProperty property name
    * @param aoArg value to set
    * @throws CInvalidPropertyException
    * @throws CInvalidPropertyArgsException
    * @see #addSetBinding
    */
   public void setProperty(Object aoObject, String asProperty, Object aoArg)
                    throws CInvalidPropertyException, CInvalidPropertyArgsException
   {
      Object[] aoArgs = new Object[1];
      aoArgs[0] = aoArg;
      setProperty(aoObject, asProperty, aoArgs);
   }

   /**
    * Sets array value of a bound property by dynamic invokation of the mapped set-method.
    * @param aoObject target object
    * @param asProperty property name
    * @param aoArgs values to set
    * @throws CInvalidPropertyException
    * @throws CInvalidPropertyArgsException
    * @see #addSetBinding
    */
   public void setProperty(Object aoObject, String asProperty, Object[] aoArgs)
                    throws CInvalidPropertyException, CInvalidPropertyArgsException
   {
      Class[] loParam;
      Method loMethodObject = null;

      synchronized (this)
      {
         loMethodObject = getSetMethod(aoObject, asProperty);

         if (loMethodObject == null)
         {
            throw new CInvalidPropertyException(asProperty, aoObject, "set-method not found");
         }

         // Convert Args where required
         convertArgs(loMethodObject, aoArgs);
      }

      try
      {
         loMethodObject.invoke(aoObject, aoArgs);
      }
      catch (IllegalArgumentException ex)
      {
         /** @todo null-argument */
         throw new CInvalidPropertyArgsException(aoObject, asProperty, null);
      }
      catch (InvocationTargetException ex)
      {
         throw new CInvalidPropertyException(asProperty, aoObject, ex);
      }
      catch (IllegalAccessException ex)
      {
         throw new CInvalidPropertyException(asProperty, aoObject, ex);
      }
   }

   /**
    * Gets value of a bound property by dynamic invokation of the mapped get-method.
    * @param aoObject target object
    * @param asProperty property name
    * @return property value
    * @see #addGetBinding
    */
   public Object getProperty(Object aoObject, String asProperty)
                      throws CInvalidPropertyException
   {
      Method loMethodObject;
      Class[] lParam;
      Object[] lArgs = null;
      Object loPropValue = null;
      boolean lbThrow = false;
      Exception ex = null;

      try
      {
         synchronized (this)
         {
            loMethodObject = getGetMethod(aoObject, asProperty);

            if (loMethodObject == null)
            {
               throw new CInvalidPropertyException(asProperty, aoObject, "get-method not found");
            }
         }

         // Get property
         loPropValue = (Object) loMethodObject.invoke(aoObject, lArgs);
      }
      catch (Exception e)
      {
         lbThrow = true;
         ex = e;
      }

      if (lbThrow)
      {
         throw new CInvalidPropertyException(asProperty, aoObject, ex);
      }

      return loPropValue;
   }

   /**
    * Binds get-property to the specified method.
    * @param asProperty property name, must be unique for the given class
    * @param aoClass class of the get-method
    * @param asMethod name of get-method
    * @throws NoSuchMethodException
    */
   public synchronized void addGetBinding(String asProperty, Class aoClass, String asMethod)
                                   throws NoSuchMethodException
   {
      Method loMethodObject;
      Class[] loParam = null;

      loMethodObject = aoClass.getMethod(asMethod, loParam);

      moGetMethods.put(aoClass.getName() + "." + asProperty, loMethodObject);
   }

   /**
    * Binds set-property to the specified method.
    * @param asProperty property name, must be unique for the given class
    * @param aoClass class of the set-method
    * @param asMethod name of set-method
    * @param aoParam method parameter classes of set-method
    * @throws NoSuchMethodException
    */
   public synchronized void addSetBinding(String asProperty, Class aoClass, String asMethod,
                                          Class[] aoParam)
                                   throws NoSuchMethodException
   {
      Method loMethodObject;

      loMethodObject = aoClass.getMethod(asMethod, aoParam);

      moSetMethods.put(aoClass.getName() + "." + asProperty, loMethodObject);
   }

   /**
    * Replaces <code>String</code>-values of the given argument array to the expected parameter types.
    * <p>The following conversions take place:
    * <li>Character
    * <li>Boolean
    * <li>Integer
    * <p>Used by <code>setProperty</code>.
    * @param aoMethodObject method describing the expected argument types
    * @param aoArgs array of arguments to replace
    */
   public void convertArgs(Method aoMethodObject, Object[] aoArgs)
   {
      Class[] loParam;

      loParam = aoMethodObject.getParameterTypes();

      for (int i = 0; i < loParam.length; i++)
      {
         if (aoArgs[i] == null)
         {
            continue;
         }

         // String -> Character
         if (loParam[i].getName().equals(char.class.getName()))
         {
            aoArgs[i] = new Character(aoArgs[i].toString().charAt(0));
         }

         // String -> Boolean
         else if (loParam[i].getName().equals(boolean.class.getName()))
         {
            aoArgs[i] = new Boolean(aoArgs[i].toString());
         }

         // String -> Integer
         else if (loParam[i].getName().equals(int.class.getName()))
         {
            if (aoArgs[i].getClass().equals(String.class))
            {
               aoArgs[i] = Integer.valueOf(aoArgs[i].toString());
            }
         }
      }
   }

   private Method getGetMethod(Object aoObject, String asProperty)
   {
      Method loMethodObject = null;
      Class loSuperClass;

      // get property method, ascending class tree
      loSuperClass = aoObject.getClass();

      while (loSuperClass != null)
      {
         loMethodObject = (Method) moGetMethods.get(loSuperClass.getName() + "." + asProperty);

         if (loMethodObject != null)
         {
            break;
         }
         else
         {
            loSuperClass = loSuperClass.getSuperclass();
         }
      }

      return loMethodObject;
   }

   private Method getSetMethod(Object aoObject, String asProperty)
   {
      Method loMethodObject = null;
      Class loSuperClass;

      // get property method, ascending class tree
      loSuperClass = aoObject.getClass();

      while (loSuperClass != null)
      {
         loMethodObject = (Method) moSetMethods.get(loSuperClass.getName() + "." + asProperty);

         if (loMethodObject != null)
         {
            break;
         }
         else
         {
            loSuperClass = loSuperClass.getSuperclass();
         }
      }

      return loMethodObject;
   }

   private HashMap moSetMethods;
   private HashMap moGetMethods;
}
