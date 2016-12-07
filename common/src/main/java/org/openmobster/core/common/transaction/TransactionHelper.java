/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.Status;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

/**
 * @author openmobster@gmail.com
 */
public class TransactionHelper 
{
	private static Logger log = Logger.getLogger(TransactionHelper.class);
	
	public static boolean startTx() 
	{
		if(!isTxAvailable())
		{
			return false;
		}
		
		try
		{
			boolean started = false;
			TransactionManager tm = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");
			
			if(tm.getStatus() == Status.STATUS_NO_TRANSACTION)
			{
				tm.begin();
				started = true;
			}
			
			return started;
		}
		catch(Throwable t)
		{
			log.error(TransactionHelper.class.getName(), t);
			return false;
		}
	}
	
	public static void commitTx()
	{
		if(!isTxAvailable())
		{
			return;
		}
		
		try
		{
			TransactionManager tm = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");
			tm.commit();
		}
		catch(Throwable t)
		{
			log.error(TransactionHelper.class.getName(), t);
			throw new RuntimeException(t);
		}
	}
	
	public static void rollbackTx()
	{
		if(!isTxAvailable())
		{
			return;
		}
		
		try
		{
			TransactionManager tm = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");
			tm.rollback();
		}
		catch(Throwable t)
		{
			log.error(TransactionHelper.class.getName(), t);
			throw new RuntimeException(t);
		}
	}
	
	private static boolean isTxAvailable()
	{
		boolean available = true;
		try
		{
			new InitialContext().lookup("java:/TransactionManager");
		}
		catch(Exception e)
		{
			return false;
		}
		return available;
	}
}
