/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.yahoo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openmobster.core.common.XMLUtilities;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Deserializer
{
	public Deserializer()
	{
		
	}
	
	public ResultSet deserialize(String xml) throws Exception
	{
		if(xml == null || xml.trim().length()==0)
		{
			throw new IllegalArgumentException("Invalid Xml data!!");
		}
		
		Document root = XMLUtilities.parse(xml);
		
		Element resultSetElement = (Element)root.getElementsByTagName("ResultSet").item(0);
		ResultSet resultSet = new ResultSet();
		
		//Find hasError
		Element errorElement = (Element)resultSetElement.getElementsByTagName("Error").item(0);
		String error = errorElement.getTextContent();
		if(!error.equals("0"))
		{
			resultSet.setHasError(true);
			return resultSet;
		}
		
		//Result Nodes
		NodeList resultNodes = resultSetElement.getElementsByTagName("Result");
		if(resultNodes != null && resultNodes.getLength()>0)
		{
			int length = resultNodes.getLength();
			for(int i=0; i<length; i++)
			{
				Element resultElement = (Element)resultNodes.item(i);
				Result result = new Result();
				YahooAddress address = new YahooAddress();
				
				//latitude
				String latitude = this.getValue(resultElement, "latitude");
				result.setLatitude(latitude);
				
				//longitude
				String longitude = this.getValue(resultElement, "longitude");
				result.setLongitude(longitude);
				
				//radius
				String radius = this.getValue(resultElement, "radius");
				result.setRadius(radius);
				
				//woeid
				String woeid = this.getValue(resultElement, "woeid");
				result.setWoeid(woeid);
				
				//woetype
				String woetype = this.getValue(resultElement, "woetype");
				result.setWoetype(woetype);
				
				//quality
				String quality = this.getValue(resultElement, "quality");
				address.setQuality(Integer.parseInt(quality));
				
				//line1
				String line1 = this.getValue(resultElement, "line1");
				address.setLine1(line1);
				
				//line2
				String line2 = this.getValue(resultElement, "line2");
				address.setLine2(line2);
				
				//line3
				String line3 = this.getValue(resultElement, "line3");
				address.setLine3(line3);
				
				//line4
				String line4 = this.getValue(resultElement, "line4");
				address.setLine4(line4);
				
				//city
				String city = this.getValue(resultElement, "city");
				address.setCity(city);
				
				//county
				String county = this.getValue(resultElement, "county");
				address.setCounty(county);
				
				//state
				String state = this.getValue(resultElement, "state");
				address.setState(state);
				
				//country
				String country = this.getValue(resultElement, "country");
				address.setCountry(country);
				
				//zipcode
				String zip = this.getValue(resultElement, "uzip");
				address.setZipCode(zip);
				
				//postal
				String postal = this.getValue(resultElement, "postal");
				address.setPostal(postal);
				
				result.setAddress(address);
				resultSet.addResult(result);
			}
			
			resultSet.sort();
		}
		
		return resultSet;
	}
	
	private String getValue(Element element, String nodeName)
	{
		NodeList nodes = element.getElementsByTagName(nodeName);
		if(nodes != null && nodes.getLength()>0)
		{
			String value = nodes.item(0).getTextContent();
			return value;
		}
		return null;
	}
}
