/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.location;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Address
{
	private String street;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private String county;
	private String postal;
	
	private String latitude;
	private String longitude;
	private String radius;
	private String woeid;
	private String woetype;
	
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

	public Address()
	{
		
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCounty()
	{
		return county;
	}

	public void setCounty(String county)
	{
		this.county = county;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getZipCode()
	{
		return zipCode;
	}

	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	public String getPostal()
	{
		return postal;
	}

	public void setPostal(String postal)
	{
		this.postal = postal;
	}
}
