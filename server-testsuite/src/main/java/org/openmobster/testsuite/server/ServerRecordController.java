/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.testsuite.server;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.database.HibernateManager;


/**
 * 
 * @author openmobster@gmail.com
 */
public class ServerRecordController
{
   private static ServerRecordController singleton = null;
   
   protected SessionFactory sessionFactory = null;
      
   protected ServerRecordController()
   {
	   
   }
      
   protected SessionFactory getSessionFactory()
   {
	   if(this.sessionFactory == null)
	   {
		   HibernateManager hibernateManager = (HibernateManager)ServiceManager.
			locate("server-testsuite://HibernateManager");
		   this.sessionFactory = hibernateManager.getSessionFactory();
	   }
	   return this.sessionFactory;
   }
      
   public static ServerRecordController getInstance()
   {
	   if(ServerRecordController.singleton == null)
	   {
		   ServerRecordController.singleton = new ServerRecordController();
	   }
	   return ServerRecordController.singleton;
   }
   //--------------------------------------------------------------------------------------------------------
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
         //Serializable uid = session.save(record);         
         //record.setObjectId(uid.toString());
         
         session.save(record);
         
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

   
   public void save(ServerRecord record)
   {
      Session session = null;
      Transaction tx = null;      
      try
      {
         session = getSessionFactory().openSession();
         tx = session.beginTransaction();
         
         session.saveOrUpdate(record);
         
         tx.commit();
      }
      catch(Exception e)
      {
    	  if(tx != null)
    	  {
    		  tx.rollback();
    	  }
      }
      finally
      {         
         if(session != null && session.isOpen())
         {
            session.close();
         }
      }
   }
   
   
   public List readAll()
   {      
      Session session = null;
      Transaction tx = null;      
      try
      {
         session = getSessionFactory().openSession();
         tx = session.beginTransaction();
         
         List all = session.createQuery("from ServerRecord").list();
         
         tx.commit();
         
         return all;
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
   
   
   public ServerRecord readServerRecord(String recordId)
   {      
      Session session = null;
      Transaction tx = null;      
      try
      {
         session = getSessionFactory().openSession();
         tx = session.beginTransaction();
         
         ServerRecord serverRecord = (ServerRecord)session.createQuery("from ServerRecord where objectId='"+recordId+"'").
         uniqueResult();
         
         tx.commit();
         
         return serverRecord;
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
   
   
   public void delete(ServerRecord record)
   {
      Session session = null;
      Transaction tx = null;      
      try
      {
         session = getSessionFactory().openSession();
         tx = session.beginTransaction();
         
         session.delete(record);
         
         tx.commit();
      }
      catch(Exception e)
      {
    	  if(tx != null)
    	  {
    		  tx.rollback();
    	  }
      }
      finally
      {         
         if(session != null && session.isOpen())
         {
            session.close();
         }
      }
   }
   
   
   public void deleteAll()
   {
	   List all = this.readAll();
	   if(all != null)
	   {
		   for(int i=0; i<all.size(); i++)
		   {
			   this.delete((ServerRecord)all.get(i));
		   }
	   }
   }
}
