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


package voodoosoft.jroots.business.remote;

import voodoosoft.jroots.business.CLocalBusinessFactory;
import voodoosoft.jroots.exception.CException;

import java.net.MalformedURLException;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.HashMap;


public class CRMIServer extends UnicastRemoteObject implements IRMIServer
{
   private CRMIServer(CLocalBusinessFactory aoLocalFactory, String asConnection,
                      String asBindingName) throws RemoteException
   {
      moFactories = new HashMap();
      moLocalFactory = aoLocalFactory;
      msConnection = asConnection;
      msBindingName = asBindingName;
   }

   /**
    * Creates and binds a new server.
    * @param asBindingName
    * @param aoLocalFactory
    * @param asConnection connection to use when objects are requested from the business factory
    * @return rmi server
    * @throws CServerConstructionException
    */
   public static synchronized CRMIServer createServer(String asBindingName,
                                                      CLocalBusinessFactory aoLocalFactory,
                                                      String asConnection)
                                               throws CServerConstructionException
   {
      CRMIServer loServer;

      try
      {
         loServer = new CRMIServer(aoLocalFactory, asConnection, asBindingName);

         if (loServer != null && asBindingName != null)
         {
            Naming.rebind(asBindingName, loServer);
         }
      }
      catch (RemoteException ex)
      {
         throw new CServerConstructionException(ex);
      }
      catch (MalformedURLException ex)
      {
         throw new CServerConstructionException(ex);
      }

      return loServer;
   }

   /**
    * Returns RMI binding name.
    * @return name
    * @throws RemoteException
    */
   public String getBindingName() throws RemoteException
   {
      return msBindingName;
   }

   /**
    * Creates new business factory.
    * @return rmi factory
    * @throws RemoteException
    */
   public synchronized IRMIBusinessFactory getFactory()
                                               throws RemoteException
   {
      IRMIBusinessFactory loFactory;

      loFactory = new CRMIBusinessFactory(moLocalFactory, msConnection);
      moFactories.put(loFactory.getHashID(), loFactory);

      return loFactory;
   }

   /**
    * Frees business factory of the specified hash.
    * @param aiFactoryHash
    * @throws RemoteException
    */
   public synchronized void releaseFactory(Integer aiFactoryHash)
                                    throws RemoteException
   {
      moFactories.remove(aiFactoryHash);
   }

   /**
    * Frees internal business factories and unbinds this server.
    */
   public synchronized void shutdown()
   {
      try
      {
         moFactories.clear();
         Naming.unbind(msBindingName);
      }
      catch (Exception ex)
      {
         CException.record(ex, this, false);
      }
   }

   private CLocalBusinessFactory moLocalFactory;
   private HashMap moFactories;
   private String msConnection;
   private String msBindingName;
}
