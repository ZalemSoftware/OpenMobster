/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail.com
 *
 */
public final class MobilePushInvocation extends Invocation 
{	
	private List<MobilePushMetaData> mobilePushMetaData;
	
	public MobilePushInvocation(String target)
	{
		super(target);
	}
	
	public List<MobilePushMetaData> getMobilePushMetaData()
	{
		if(this.mobilePushMetaData == null)
		{
			this.mobilePushMetaData = new ArrayList<MobilePushMetaData>();
		}
		return this.mobilePushMetaData;
	}
	
	public void addMobilePushMetaData(MobilePushMetaData pushMetaData)
	{
		this.getMobilePushMetaData().add(pushMetaData);
	}

	public Map<String,Object> getShared()
	{
		Map<String,Object> shared = super.getShared();
		
		if(!this.mobilePushMetaData.isEmpty())
		{
			int size = this.mobilePushMetaData.size();
			shared.put("pushMetaDataSize", ""+size);
			for(int i=0; i<size; i++)
			{
				MobilePushMetaData metadata = this.mobilePushMetaData.get(i);
				shared.put("pushMetaData["+i+"].service", metadata.getService());
				shared.put("pushMetaData["+i+"].id", metadata.getId());
				shared.put("pushMetaData["+i+"].isDeleted", 
				metadata.isDeleted()?Boolean.TRUE:Boolean.FALSE);
				shared.put("pushMetaData["+i+"].isAdded", 
				metadata.isAdded()?Boolean.TRUE:Boolean.FALSE);
				shared.put("pushMetaData["+i+"].isUpdated", 
				metadata.isUpdated()?Boolean.TRUE:Boolean.FALSE);
			}
		}
		
		return shared;
	}
}
