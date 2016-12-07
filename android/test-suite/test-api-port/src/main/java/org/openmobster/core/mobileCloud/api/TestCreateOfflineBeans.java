/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.openmobster.android.api.sync.MobileBean;

import org.openmobster.core.mobileCloud.android.module.sync.engine.SyncDataSource;
import org.openmobster.core.mobileCloud.android.module.sync.engine.ChangeLogEntry;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestCreateOfflineBeans extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			StringBuilder builder = new StringBuilder();
			StringBuilder packetBuilder = new StringBuilder();
			for(int i=0; i<1000; i++)
			{
				packetBuilder.append("a");
			}
			String packet = packetBuilder.toString();
			for(int i=0; i<100; i++)
			{
				builder.append(packet);
			}
			
			Set<MobileBean> beans = new HashSet<MobileBean>();
			for(int i=0; i<50; i++)
			{
				MobileBean newBean = MobileBean.newInstance(this.service);
			
				assertTrue(newBean.isInitialized(), this.getInfo()+"://NewBean_should_be_initialized");
				assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
				assertTrue(newBean.getId()==null, this.getInfo()+"://NewBean_id_should_be_null");
				assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
				
				newBean.setValue("from", builder.toString());
			
				beans.add(newBean);
			}
			
			//Should kick off a bulk sync session
			MobileBean.bulkSave(beans);
			
			//Just wait for the sync to be completed
			SyncDataSource syncDataSource = SyncDataSource.getInstance();
			List<ChangeLogEntry> changelog = null;
			do
			{
				Thread.sleep(5000);
				changelog = syncDataSource.readChangeLog();
			}while(!changelog.isEmpty());
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
