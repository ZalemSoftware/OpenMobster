/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin;

import java.util.Set;
import java.util.HashSet;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.android.api.sync.CommitException;
import org.openmobster.android.api.sync.MobileBean;

/**
 *
 * @author openmobster@gmail.com
 */
final class SyncSession 
{
	private static SyncSession singleton;
	
	private GenericAttributeManager cache;
	
	private SyncSession()
	{
		this.cache = new GenericAttributeManager();
	}
	
	public static SyncSession getInstance()
	{
		if(SyncSession.singleton == null)
		{
			synchronized(SyncSession.class)
			{
				if(SyncSession.singleton == null)
				{
					SyncSession.singleton = new SyncSession();
				}
			}
		}
		return SyncSession.singleton;
	}
	
	public synchronized void cacheBean(MobileBean mobileBean)
	{
		if(this.readBean(mobileBean.getId()) != null)
		{
			return;
		}
		this.cache.setAttribute(mobileBean.getId(), mobileBean);
	}
	
	public synchronized void cacheBean(String oid,MobileBean newBean)
	{
		this.cache.setAttribute(oid, newBean);
	}
	
	public synchronized MobileBean readBean(String oid)
	{
		return (MobileBean)this.cache.getAttribute(oid);
	}
	
	public synchronized void removeBean(String oid)
	{
		this.cache.removeAttribute(oid);
	}
	
	public synchronized void commit() throws Exception
	{
		boolean syncScheduled = false;
		try
		{
			Object[] beans = this.cache.getValues();
			if(beans == null)
			{
				return;
			}
			
			Set<MobileBean> beansToBeCommited = new HashSet<MobileBean>();
			for(Object local:beans)
			{
				MobileBean bean = (MobileBean)local;
				beansToBeCommited.add(bean);
			}
			
			if(!beansToBeCommited.isEmpty())
			{
				MobileBean.bulkSave(beansToBeCommited);
				syncScheduled = true;
			}
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			if(!syncScheduled)
			{
				//schedule a sync session and then return
				try
				{
					SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
							SyncInvocation.scheduleSync);		
					Bus.getInstance().invokeService(syncInvocation);
				}
				catch(Exception e)
				{
					SystemException sys = new SystemException(this.getClass().getName(), "commit", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					});
					ErrorHandler.getInstance().handle(sys);
					throw new CommitException(sys);
				}
			}
			//this must execute regardless
			this.cache.clear();
		}
	}
	
	public synchronized void rollback()
	{
		this.cache.clear();
	}
}
