/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import junit.framework.TestCase;

import org.json.simple.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.openmobster.core.common.XMLUtilities;
import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Place;
import org.openmobster.cloud.api.location.Address;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestPayloadHandler extends TestCase
{
	private static Logger log = Logger.getLogger(TestPayloadHandler.class);
	
	public void testDeserializeRequest() throws Exception
	{
		//Request payload
		JSONObject requestPayload = new JSONObject();
		for(int i=0; i<5; i++)
		{
			requestPayload.put("param"+i, "value"+i);
		}
		
		//list
		List<String> list = new ArrayList<String>();
		for(int i=0; i<5; i++)
		{
			list.add("listAttribute:"+i);
		}
		requestPayload.put("list", list);
		
		//map
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0; i<5; i++)
		{
			map.put("key"+i, "value"+i);
		}
		requestPayload.put("map", map);
		
		String requestPayloadStr = requestPayload.toJSONString();
		
		//Location payload
		JSONObject locationPayload = new JSONObject();
		
		//Latitude, Longitude
		locationPayload.put("latitude", "-100");
		locationPayload.put("longitude", "-200");
		
		//PlaceTypes
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("restaurant");
		placeTypes.add("airport");
		locationPayload.put("placeTypes", placeTypes);
		for(int i=0; i<5; i++)
		{
			locationPayload.put("name"+i, "value"+i);
		}
		
		String locationPayloadStr = locationPayload.toJSONString();
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("<location-request>\n");
		buffer.append("<service>coupons</service>\n");
		buffer.append("<request-payload>"+XMLUtilities.addCData(requestPayloadStr)+"</request-payload>\n");
		buffer.append("<location-payload>"+XMLUtilities.addCData(locationPayloadStr)+"</location-payload>\n");
		buffer.append("</location-request>\n");
		String xml = buffer.toString();
		
		log.info(xml);
		
		PayloadHandler handler = new PayloadHandler();
		
		LocationContext locationContext = handler.deserializeRequest(xml);
		Request request = (Request)locationContext.getAttribute("request");
		
		log.info("*******************************************");
		log.info("Service: "+request.getService());
		log.info("Latitude: "+locationContext.getLatitude());
		log.info("Longitude: "+locationContext.getLongitude());
		assertEquals(request.getService(),"coupons");
		assertEquals(locationContext.getLatitude(),"-100");
		assertEquals(locationContext.getLongitude(),"-200");
		
		String[] names = request.getNames();
		for(String name:names)
		{
			if(name.equals("list") || name.equals("map"))
			{
				continue;
			}
			
			String value = request.getAttribute(name);
			log.info(name+":"+value);
			assertTrue(name.startsWith("param"));
			assertTrue(value.startsWith("value"));
		}
		
		List<String> myList = request.getListAttribute("list");
		for(String cour:myList)
		{
			log.info(cour);
			assertTrue(cour.startsWith("listAttribute"));
		}
		
		Map<String,String> myMap = request.getMapAttribute("map");
		Set<String> keys = myMap.keySet();
		for(String key:keys)
		{
			String value = myMap.get(key);
			
			log.info(key+":"+value);
			assertTrue(key.startsWith("key"));
			assertTrue(value.startsWith("value"));
		}
		
		//Place Types
		List<String> placeTypesList = locationContext.getPlaceTypes();
		assertTrue(placeTypesList != null && !placeTypesList.isEmpty());
		
		//Rest of the LocationContext
		names = locationContext.getNames();
		for(String name:names)
		{
			Object value = locationContext.getAttribute(name);
			if(value instanceof String)
			{
				log.info(name+":"+value);
			}
		}
	}
	
	public void testSerializeResponse() throws Exception
	{
		LocationContext locationContext = new LocationContext();
		
		//Places
		List<Place> places = new ArrayList<Place>();
		for(int i=0; i<5; i++)
		{
			Place place = new Place();
			place.setAddress("2046 Dogwood Gardens Dr/"+i);
			place.setPhone("867-5309");
			place.setReference("Reference:"+i);
			places.add(place);
		}
		locationContext.setNearbyPlaces(places);
		
		//Address
		Address address = new Address();
		address.setStreet("1782 Stillwind Lane");
		locationContext.setAddress(address);
		
		//Latitude
		locationContext.setLatitude("-100");
		
		//Longitude
		locationContext.setLongitude("-200");
		
		//PlaceTypes
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("restaurant");
		placeTypes.add("airport");
		locationContext.setPlaceTypes(placeTypes);
		
		//rest of the context
		for(int i=0; i<5;i++)
		{
			locationContext.setAttribute("param"+i, "value"+i);
		}
		
		//Response
		Response response = new Response();
		for(int i=0; i<5;i++)
		{
			response.setAttribute("param"+i, "value"+i);
		}
		response.setStatusCode("200");
		response.setStatusMsg("OK");
		locationContext.setAttribute("response", response);
		
		//list
		List<String> list = new ArrayList<String>();
		for(int i=0; i<5; i++)
		{
			list.add("listAttribute:"+i);
		}
		response.setListAttribute("list1", list);
		response.setListAttribute("list2", list);
		
		//map
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0; i<5; i++)
		{
			map.put("key"+i, "value"+i);
		}
		response.setMapAttribute("map1", map);
		response.setMapAttribute("map2", map);
		
		PayloadHandler handler = new PayloadHandler();
		String xml = handler.serializeResponse(locationContext);
		log.info(xml);
		
		Document document = XMLUtilities.parse(xml);
		Element responsePayloadElement = (Element)document.getElementsByTagName("response-payload").item(0);
		Element locationPayloadElement = (Element)document.getElementsByTagName("location-payload").item(0);
		String responsePayload = responsePayloadElement.getTextContent();
		String locationPayload = locationPayloadElement.getTextContent();
		
		log.info(responsePayload);
		log.info(locationPayload);
		assertNotNull(responsePayload);
		assertNotNull(locationPayload);
	}
}
