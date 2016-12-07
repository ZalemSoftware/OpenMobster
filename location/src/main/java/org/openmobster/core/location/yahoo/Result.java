/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.yahoo;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Result
{
	private String latitude;
	private String longitude;
	private String radius;
	private String woeid;
	private String woetype;
	
	private YahooAddress address;
	
	public Result()
	{
		
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}

	public String getRadius()
	{
		return radius;
	}

	public void setRadius(String radius)
	{
		this.radius = radius;
	}

	public String getWoeid()
	{
		return woeid;
	}

	public void setWoeid(String woeid)
	{
		this.woeid = woeid;
	}

	public String getWoetype()
	{
		return woetype;
	}

	public void setWoetype(String woetype)
	{
		this.woetype = woetype;
	}

	public YahooAddress getAddress()
	{
		return address;
	}

	public void setAddress(YahooAddress address)
	{
		this.address = address;
	}
}
