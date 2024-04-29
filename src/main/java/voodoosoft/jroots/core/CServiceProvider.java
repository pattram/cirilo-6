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

import voodoosoft.jroots.exception.CException;

import java.rmi.Naming;

import java.util.HashMap;


/**
 * <code>CServiceProvider</code> works as object registry,
 * allowing global access to any kind of local or remote objects.
 * <p><code>CRMIBusinessFactory</code> is thread safe.
 */
public class CServiceProvider extends CObject
{
   protected CServiceProvider()
   {
   }

   /**
    * Returns reference to service of specified name.
    * To find the service, at first the local registry is searched,
    * if not successful, <code>java.rmi.Naming#lookup</code> is used to locate
    * the requested object.
    * @param asKey name of previously registered service
    * @return service requested
    * @throws CServiceLookupException
    */
   public static Object getService(CServiceName asKey)
                            throws CServiceLookupException
   {
      Object loLookUp;
      Object loService = null;
      String lsURL;

      synchronized (CServiceProvider.class)
      {
         loLookUp = soServices.get(asKey);
      }

      if (loLookUp == null)
      {
         throw new CServiceLookupException(asKey.toString());
      }
      else
      {
         if (loLookUp instanceof String)
         {
            try
            {
               lsURL = (String) loLookUp;
               loService = Naming.lookup(lsURL);
            }
            catch (Exception ex)
            {
               throw new CServiceLookupException(ex);
            }
         }
         else
         {
            loService = loLookUp;
         }
      }

      return loService;
   }

   /**
    * Registers local service object.
    * @param aoService service to add
    * @param asKey unique service name
    * @throws CDuplicateNameException
    */
   public static synchronized void addService(Object aoService, CServiceName asKey)
                                       throws CDuplicateNameException
   {
      if (soServices.containsKey(asKey))
      {
         throw new CDuplicateNameException(asKey.toString());
      }

      soServices.put(asKey, aoService);
   }

   /**
    * Registers link to remote service.
    * @param asKey unique service name
    * @param asURL RMI url to find the service
    * @throws CDuplicateNameException
    */
   public static synchronized void addService(CServiceName asKey, String asURL)
                                       throws CDuplicateNameException
   {
      if (soServices.containsKey(asKey))
      {
         throw new CDuplicateNameException(asKey.toString());
      }

      soServices.put(asKey, asURL);
   }

   /**
    * Returns true if given service has been registered.
    * @param asKey    
    */
   public static boolean hasService(CServiceName asKey)
   {
      return soServices.containsKey(asKey);
   }

   /**
    * Lists all bound remote objects of the specified url.
    * @param asURL
    */
   public static void printBindings(String asURL)
   {
      String[] lsBindings;

      try
      {
         lsBindings = Naming.list(asURL);

         for (int i = 0; i < lsBindings.length; i++)
         {
            System.out.println(lsBindings);
         }
      }
      catch (Exception ex)
      {
         CException.record(ex, null);
      }
   }

   /**
    * Removes all registered services of this <code>CServiceProvider</code>.
    */
   public static synchronized void removeAllServices()
   {
      soServices.clear();
   }

   /**
    * Removes reference, respectively url of specified service.
    * @param asKey
    */
   public static synchronized void removeService(CServiceName asKey)
   {
      soServices.remove(asKey);
   }

   private static HashMap soServices = new HashMap();
}
