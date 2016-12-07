/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.MobileBean;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestLargeObjectSync extends AbstractLargeObjectTest
{
	public void runTest()
	{		
		try
		{
			StringBuilder messageBuilder = new StringBuilder();
			
			StringBuilder packetBuilder = new StringBuilder();
			for(int i=0; i<1000; i++)
			{
				packetBuilder.append("a");
			}
			
			String packet = packetBuilder.toString();
			for(int i=0; i<100; i++)
			{
				messageBuilder.append(packet);
			}
			
			String largeObjectMessage = messageBuilder.toString();
			
			for(int i=0; i<3; i++)
			{
				MobileBean largeObject = MobileBean.newInstance("large_object_channel");
				largeObject.setValue("message", largeObjectMessage);
				largeObject.saveWithoutSync();
			}
			
			this.startTwoWaySync();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
}
