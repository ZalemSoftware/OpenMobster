/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PlaceSPI
{
	private String address;
	private String phone;
	private String internationalPhoneNumber;
	private String url;
	private String website;
	private String icon;
	private String name;
	private String latitude;
	private String longitude;
	private String id;
	private String reference;
	private String rating;
	private List<String> types;
	private String vicinity;
	private String htmlAttribution;
	
	public PlaceSPI()
	{
		this.types = new ArrayList<String>();
	}
	
	public String getHtmlAttribution()
	{
		return htmlAttribution;
	}

	public void setHtmlAttribution(String htmlAttribution)
	{
		this.htmlAttribution = htmlAttribution;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getInternationalPhoneNumber()
	{
		return internationalPhoneNumber;
	}

	public void setInternationalPhoneNumber(String internationalPhoneNumber)
	{
		this.internationalPhoneNumber = internationalPhoneNumber;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getWebsite()
	{
		return website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getReference()
	{
		return reference;
	}

	public void setReference(String reference)
	{
		this.reference = reference;
	}

	public String getRating()
	{
		return rating;
	}

	public void setRating(String rating)
	{
		this.rating = rating;
	}

	public List<String> getTypes()
	{
		return types;
	}

	public void addType(String type)
	{
		this.types.add(type);
	}

	public String getVicinity()
	{
		return vicinity;
	}

	public void setVicinity(String vicinity)
	{
		this.vicinity = vicinity;
	}
}
