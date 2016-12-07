/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.net.URL;

import org.jboss.kernel.Kernel;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.kernel.spi.dependency.KernelController;

import org.apache.log4j.Logger;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class ServiceManager
{
	public static Logger log = Logger.getLogger(ServiceManager.class);
				
	private static Kernel kernel = null;
	private static ServiceBootstrap bootstrap = null;
	//---------------------------------------------------------------------------------------------------------	
	public ServiceManager()
	{		
	}
		
	public void start()
	{		
	}
		
	public void stop()
	{
		ServiceManager.shutdown();
	}
		
	public Kernel getKernel()
	{
		return ServiceManager.kernel;
	}
	
	public void setKernel(Kernel kernel)
	{
		ServiceManager.kernel = kernel;
	}
	//--------------------------------------------------------------------------------------------------------				
	public synchronized static void bootstrap()
	{
		if(ServiceManager.kernel != null)
		{
			return;
		}
		
		try
		{
			ServiceManager.bootstrap = new ServiceBootstrap(null);
			ServiceManager.bootstrap.bootstrap();
			ServiceManager.kernel = bootstrap.getKernel();
			ServiceManager.bootstrap.bootstrapMobletApps();
		}
		catch(Throwable e)
		{
			log.error(ServiceManager.class, e);
			ServiceManager.shutdown();			
			throw new RuntimeException(e);
		}
	}		
		
	/**
	 * Locates a Service registered with the System Kernel
	 * 
	 * @param name unique identifier for the Service
	 * @return the Service registered with the System Kernel
	 */
	public static Object locate(Object name)
	{
		Object target = null;
		
		/*if(ServiceManager.kernel == null)
		{
			ServiceManager.bootstrap();
		}*/
				
		KernelController controller = ServiceManager.kernel.getController();
		ControllerContext context = controller.getInstalledContext(name);
		if(context != null)
		{
			target = context.getTarget();
		}
		
		return target;
	}
	
	/**
	 * Shuts down the System Kernel and correspondingly the whole Mobile Server Stack
	 *
	 */
	public static void shutdown()
	{
		//Perform proper shutdown and nullify for re-bootstrap
		if(ServiceManager.bootstrap != null)
		{
			ServiceManager.bootstrap.shutdown();			
		}
		
		ServiceManager.bootstrap = null;
		ServiceManager.kernel = null;
	}
	
	public static void redeploy(URL url)
	{
		try
		{
			if(ServiceManager.bootstrap != null)
			{
				ServiceManager.bootstrap.redeploy(url);
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
}
