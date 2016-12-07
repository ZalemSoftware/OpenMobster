/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;


import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.core.services.MobileObjectMonitor;


/**
 * @author openmobster@gmail.com
 */
public class GetAllMobileBeans implements ContainerService 
{
	private static Logger log = Logger.getLogger(GetAllMobileBeans.class);
	
	private String id;
	private MobileObjectMonitor monitor;
	
	public GetAllMobileBeans()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
		
	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}
	
	
	public MobileObjectMonitor getMonitor() 
	{
		return monitor;
	}

	public void setMonitor(MobileObjectMonitor monitor) 
	{
		this.monitor = monitor;
	}
	//---------------------------------------------------------------------------------------------------
	public InvocationResponse execute(Invocation invocation) throws InvocationException
	{
		try
		{
			InvocationResponse response = InvocationResponse.getInstance();
			
			List<MobileBean> allBeans = new ArrayList<MobileBean>();
						
			String connectorId = invocation.getConnectorId();
			
			//Retrieve the correct MobileObjectConnector		
			Channel connector = monitor.lookup(connectorId);
			if(connector == null)
			{
				response.setStatus(InvocationResponse.STATUS_NOT_FOUND);
				return response;
			}
			
			
			String bootup = (String)invocation.getAttribute("bootup");
			
			List<? extends MobileBean> all = null;
			
			if(bootup == null || bootup.trim().length() == 0)
			{
				all = connector.readAll();
			}
			else
			{
				all = connector.bootup();
				if(all == null)
				{
					all = new ArrayList();
				}
				
				List<? extends MobileBean> others = connector.readAll();
				
				if(others != null)
				{
					List buffer = new ArrayList();
					for(MobileBean otherBean: others)
					{
						ChannelInfo connectorInfo = (ChannelInfo)connector.getClass().
						getAnnotation(ChannelInfo.class);
						Class objectClazz = Thread.currentThread().
						getContextClassLoader().
						loadClass(connectorInfo.mobileBeanClass());
						
						Field objectIdField = null;
						Field[] declaredFields = objectClazz.getDeclaredFields();			
						for(Field field: declaredFields)
						{		
							Annotation id = field.getAnnotation(MobileBeanId.class);	
							if(id != null)
							{
								objectIdField = field;
								break;
							}
						}
						
						String otherBeanObjectId = BeanUtils.getProperty(otherBean, objectIdField.getName());
						
						boolean objectFound = false;
						for(MobileBean allBean: all)
						{							
							String objectId = BeanUtils.getProperty(allBean, objectIdField.getName());
							if(objectId.equals(otherBeanObjectId))
							{
								objectFound = true;
								break;
							}
						}
						
						if(!objectFound)
						{
							//then send meta data about the bean back to be synced up later by the device's sync engine
							MobileBean metaData = (MobileBean)objectClazz.newInstance();							
							BeanUtils.setProperty(metaData, objectIdField.getName(), "proxy[["+otherBeanObjectId+"]]");
							buffer.add(metaData);
						}
					}
					
					all.addAll(buffer);
				}
			}
			
			response.setAllBeans(allBeans);
			if(all != null && !all.isEmpty())
			{
				for(MobileBean local: all)
				{
					allBeans.add(local);
				}				
			}			
															
			return response;
		}
		catch(Throwable t)
		{
			throw new InvocationException(t.getMessage(), t);
		}
	}
}
