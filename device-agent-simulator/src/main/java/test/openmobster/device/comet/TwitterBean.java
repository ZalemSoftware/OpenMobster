/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * @author openmobster@gmail.com
 */
public class TwitterBean implements MobileBean 
{
	@MobileBeanId
	private String feedId;
	
	private String latestTweet;
	
	public TwitterBean()
	{
		
	}

	public String getFeedId() 
	{
		return feedId;
	}

	public void setFeedId(String feedId) 
	{
		this.feedId = feedId;
	}

	public String getLatestTweet() 
	{
		return latestTweet;
	}

	public void setLatestTweet(String latestTweet) 
	{
		this.latestTweet = latestTweet;
	}	
}
