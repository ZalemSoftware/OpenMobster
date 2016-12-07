/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.spi.ui.framework;

/**
 * @author openmobster@gmail.com
 *
 */
public final class SPIServices 
{
	private static SPIServices singleton;
	
	private NavigationContextSPI navigationContextSPI;
	private EventBusSPI eventBusSPI;
	
	private SPIServices()
	{
		
	}
	
	public static SPIServices getInstance()
	{
		if(SPIServices.singleton == null)
		{
			synchronized(SPIServices.class)
			{
				if(SPIServices.singleton == null)
				{
					SPIServices.singleton = new SPIServices();
				}
			}
		}
		return SPIServices.singleton;
	}
	
	public static void stopSingleton()
	{
		SPIServices.singleton = null;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public NavigationContextSPI getNavigationContextSPI() 
	{
		return navigationContextSPI;
	}

	public void setNavigationContextSPI(NavigationContextSPI navigationContextSPI) 
	{
		this.navigationContextSPI = navigationContextSPI;
	}	
	
	public EventBusSPI getEventBusSPI()
	{
		return this.eventBusSPI;
	}
	
	public void setEventBusSPI(EventBusSPI eventBusSPI)
	{
		this.eventBusSPI = eventBusSPI;
	}
}
