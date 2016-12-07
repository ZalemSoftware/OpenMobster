/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.location;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.openmobster.android.api.location.LocationContext;
import org.openmobster.android.api.location.Request;
import org.openmobster.android.api.location.Response;
import org.openmobster.android.api.location.Place;
import org.openmobster.android.api.location.Address;
import org.openmobster.api.service.location.PayloadHandler;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestPayloadHandler extends Test
{
	@Override
	public void runTest()
	{
		try
		{
			this.testSerializeRequest();
			this.testDeserializeReponse();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void testSerializeRequest() throws Exception
	{
		Request request = new Request("coupons");
		
		PayloadHandler payloadHandler = PayloadHandler.getInstance();
		
		LocationContext locationContext = new LocationContext();
		locationContext.setAttribute("request", request);
		
		//Add a list
		List<String> list = new ArrayList<String>();
		list.add("value://0");
		list.add("value://1");
		list.add("value://2");
		request.setListAttribute("mylist", list);
		
		//Add a Map
		Map<String,String> map = new HashMap<String,String>();
		map.put("name1", "value1");
		map.put("name2", "value2");
		map.put("name3", "value3");
		request.setMapAttribute("myMap", map);
		
		for(int i=0; i<5; i++)
		{
			request.setAttribute("param"+i, "value"+i);
		}
		
		//LocationContext
		locationContext.setLatitude("-100");
		locationContext.setLongitude("-200");
		locationContext.setPlaceReference("123456789");
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("food");
		placeTypes.add("grocery");
		placeTypes.add("restaurant");
		locationContext.setPlaceTypes(placeTypes);
		for(int i=0; i<5; i++)
		{
			locationContext.setAttribute("name"+i, "value"+i);
		}
		
		String xml = payloadHandler.serializeRequest(locationContext);
		
		System.out.println(xml);
	}
	
	private void testDeserializeReponse() throws Exception
	{
		String xml = "<location-response>"+
"<response-payload><![CDATA[{\"list2\":[\"listAttribute:0\",\"listAttribute:1\",\"listAttribute:2\",\"listAttribute:3\",\"listAttribute:4\"],\"param0\":\"value0\",\"param1\":\"value1\",\"param2\":\"value2\",\"status\":\"200\",\"list1\":[\"listAttribute:0\",\"listAttribute:1\",\"listAttribute:2\",\"listAttribute:3\",\"listAttribute:4\"],\"param3\":\"value3\",\"param4\":\"value4\",\"map2\":{\"key4\":\"value4\",\"key3\":\"value3\",\"key0\":\"value0\",\"key2\":\"value2\",\"key1\":\"value1\"},\"map1\":{\"key4\":\"value4\",\"key3\":\"value3\",\"key0\":\"value0\",\"key2\":\"value2\",\"key1\":\"value1\"},\"statusMsg\":\"OK\"}]]></response-payload>"+
"<location-payload><![CDATA[{\"param0\":\"value0\",\"param1\":\"value1\",\"param2\":\"value2\",\"address\":{\"street\":\"1782 Stillwind Lane\"},\"param3\":\"value3\",\"param4\":\"value4\",\"placeTypes\":[\"restaurant\",\"airport\"],\"longitude\":\"-200\",\"latitude\":\"-100\",\"places\":[{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:0\",\"reference\":\"Reference:0\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:1\",\"reference\":\"Reference:1\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:2\",\"reference\":\"Reference:2\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:3\",\"reference\":\"Reference:3\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:4\",\"reference\":\"Reference:4\"}]}]]></location-payload>"+
"</location-response>";
		
		PayloadHandler payloadHandler = PayloadHandler.getInstance();
		
		LocationContext locationContext = payloadHandler.deserializeResponse(xml);
		
		Response response = (Response)locationContext.getAttribute("response");
		
		List<String> list1 = response.getListAttribute("list1");
		assertTrue(list1 != null && !list1.isEmpty(), "testDeserializeResponse/list1");
		
		List<String> list2 = response.getListAttribute("list2");
		assertTrue(list2 != null && !list2.isEmpty(), "testDeserializeResponse/list2");
		
		Map<String,String> map1 = response.getMapAttribute("map1");
		assertTrue(map1 != null && !map1.isEmpty(), "testDeserializeResponse/map1");
		
		Map<String,String> map2 = response.getMapAttribute("map2");
		assertTrue(map2 != null && !map2.isEmpty(), "testDeserializeResponse/map2");
		
		List<Place> places = locationContext.getNearbyPlaces();
		assertTrue(places != null && !places.isEmpty(),"testDeserializeResponse/places");
		for(Place place:places)
		{
			System.out.println("Phone: "+place.getPhone());
		}
		
		List<String> placeTypes = locationContext.getPlaceTypes();
		assertTrue(placeTypes != null && !placeTypes.isEmpty(),"testDeserializeResponse/placeTypes");
		for(String placeType:placeTypes)
		{
			System.out.println("PlaceType: "+placeType);
		}
		
		Address address = locationContext.getAddress();
		assertTrue(address != null, "testDeserializeResponse/address");
		System.out.println("Street: "+address.getStreet());
		
		//rest of the context
		assertTrue(locationContext.getAttribute("param0").equals("value0"),"testDeserialize/param0");
		assertTrue(locationContext.getAttribute("param1").equals("value1"),"testDeserialize/param1");
		assertTrue(locationContext.getAttribute("param2").equals("value2"),"testDeserialize/param2");
		assertTrue(locationContext.getAttribute("param3").equals("value3"),"testDeserialize/param3");
		assertTrue(locationContext.getAttribute("param4").equals("value4"),"testDeserialize/param4");
	}
}
