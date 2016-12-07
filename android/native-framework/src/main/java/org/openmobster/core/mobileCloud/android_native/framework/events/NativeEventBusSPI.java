/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.events;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.spi.ui.framework.EventBusSPI;

/**
 * @author openmobster@gmail.com
 *
 */
public final class NativeEventBusSPI implements EventBusSPI
{
	private List<Subscription> subscriptions;
	
	public NativeEventBusSPI()
	{
		this.subscriptions = new ArrayList<Subscription>();
	}
	
	public void addEventListener(Screen screen, Object eventListener)
	{
		Subscription newSubscription = new Subscription(screen,eventListener);
		this.subscriptions.remove(newSubscription);
		this.subscriptions.add(newSubscription);
	}

	public void sendEvent(Object event)
	{
		Screen currentScreen = NavigationContext.getInstance().getCurrentScreen();
		
		String eventClassName = event.getClass().getSimpleName();
		int indexOfEvent = eventClassName.lastIndexOf("Event");
		String eventId = eventClassName.substring(0, indexOfEvent);
		
		for(Subscription subscription:subscriptions)
		{
			if(subscription.doesItMatchForDelivery(currentScreen, eventId))
			{
				subscription.invokeEvent(event);
				return;
			}
		}
	}
}
