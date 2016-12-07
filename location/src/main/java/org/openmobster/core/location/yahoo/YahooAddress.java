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
public final class YahooAddress
{
	private int quality;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private String city;
	private String county;
	private String state;
	private String country;
	private String zipCode;
	private String postal;
	
	
	public YahooAddress()
	{
		
	}


	public String getLine1()
	{
		return line1;
	}


	public void setLine1(String line1)
	{
		this.line1 = line1;
	}


	public String getLine2()
	{
		return line2;
	}


	public void setLine2(String line2)
	{
		this.line2 = line2;
	}


	public String getLine3()
	{
		return line3;
	}


	public void setLine3(String line3)
	{
		this.line3 = line3;
	}


	public String getLine4()
	{
		return line4;
	}


	public void setLine4(String line4)
	{
		this.line4 = line4;
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


	public int getQuality()
	{
		return quality;
	}


	public void setQuality(int quality)
	{
		this.quality = quality;
	}
}
