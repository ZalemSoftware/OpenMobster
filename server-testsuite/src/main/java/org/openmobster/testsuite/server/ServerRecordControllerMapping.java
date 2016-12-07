/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.testsuite.server;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.Transaction;


/**
 * 
 * @author openmobster@gmail.com
 */
public class ServerRecordControllerMapping extends ServerRecordController
{
   private static ServerRecordControllerMapping singleton = null;
      
   protected ServerRecordControllerMapping()
   {
	   
   }
         
   public static ServerRecordControllerMapping getInstance()
   {
	   if(ServerRecordControllerMapping.singleton == null)
	   {
		   ServerRecordControllerMapping.singleton = new ServerRecordControllerMapping();
	   }
	   return ServerRecordControllerMapping.singleton;
   }
   //-----------------------------------------------------------------------------------------------------------
   public String create(ServerRecord record)
   {
      Session session = null;
      Transaction tx = null;      
      try
      {
         session = getSessionFactory().openSession();
         tx = session.beginTransaction();
             
         /**
          * Used to show server side creation of an object id different from the device
          */
         Serializable uid = session.save(record);         
         record.setObjectId(uid.toString());         
         
         String recordId = record.getObjectId();
         
         tx.commit();
         
         return recordId;
      }
      catch(Exception e)
      {
    	  if(tx != null)
    	  {
    		  tx.rollback();
    	  }
    	  throw new RuntimeException(e);
      }
      finally
      {         
         if(session != null && session.isOpen())
         {
            session.close();
         }
      }
   }   
}
