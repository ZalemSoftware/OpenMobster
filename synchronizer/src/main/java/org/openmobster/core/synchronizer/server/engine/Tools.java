/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.beanutils.BeanUtils;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.core.synchronizer.server.SyncContext;

/**
 * 
 * @author openmobster@gmail.com
 */
public class Tools
{
	public static String getOid(MobileBean record)
	{
		try
		{
			String id = "";
			
			Class recordClazz = record.getClass();
			Field[] declaredFields = recordClazz.getDeclaredFields();		
			for(Field field: declaredFields)
			{		
				Annotation[] annotations = field.getAnnotations();			
				for(Annotation annotation: annotations)
				{								
					if(annotation instanceof MobileBeanId)
					{
						return BeanUtils.getProperty(record, field.getName());										
					}
				}			
			}
			
			return id;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static String getDeviceId()
	{
		return SyncContext.getInstance().getDeviceId();
	}
	
	public static String getChannel()
	{
		return SyncContext.getInstance().getServerSource();
	}
}
