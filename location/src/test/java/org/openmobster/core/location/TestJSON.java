/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import junit.framework.TestCase;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


/**
 *
 * @author openmobster@gmail.com
 */
public class TestJSON extends TestCase
{

	/* 
	 * 
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/* 
	 * 
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/*public void testSerialization() throws Exception
	{
		AddressSPI address = new AddressSPI();
		address.setStreet("2046 Dogwood Gardens Dr");
		address.setCity("Germantown");
		
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        
        String addressJson = xstream.toXML(address);
        System.out.println(addressJson);
        
        String test = "{\"org.openmobster.core.location.AddressSPI\":{\"street\":\"2046 Dogwood Gardens Dr\",\"city\":\"Germantown\"}}";
        AddressSPI deserialized = (AddressSPI)xstream.fromXML(test);
        
        System.out.println("-----------------------------------");
        System.out.println("Street: "+deserialized.getStreet());
        System.out.println("City: "+deserialized.getCity());
        System.out.println("Zip: "+deserialized.getZipCode());
        
        assertEquals("2046 Dogwood Gardens Dr",deserialized.getStreet());
        assertEquals("Germantown",deserialized.getCity());
	}
	
	public void testMapSerialization() throws Exception
	{
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
		//XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
		//    public HierarchicalStreamWriter createWriter(Writer writer) {
		//        return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		//    }
		//});
        
        Map<String,String> map = new HashMap<String,String>();
        for(int i=0; i<5; i++)
        {
        	map.put("key"+i, "value"+i);
        }
        
        String json = xstream.toXML(map);
        System.out.println(json);
        
        //XStream jettison = new XStream(new JettisonMappedXmlDriver());
        //jettison.setMode(XStream.NO_REFERENCES);
        
        //Map<String,String> back = (Map<String,String>)jettison.fromXML(json);
	}
	
	public void testArraySerialization() throws Exception
	{
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        
        AddressSPI[] array = new AddressSPI[5];
        for(int i=0; i<5; i++)
        {
        	AddressSPI address = new AddressSPI();
    		address.setStreet("2046 Dogwood Gardens Dr/"+i);
    		address.setCity("Germantown/"+i);
    		array[i] = address;
        }
        
        String json = xstream.toXML(array);
        
        System.out.println(json);
	}*/
	
	/*public void testPlaceSerialization() throws Exception
	{
		PlaceSPI place = new PlaceSPI();
		place.setAddress("2046 Dogwood Gardens Dr");
		place.setName("My Home");
		place.addType("home");
		place.addType("house");
		
		//Encode
		JSONObject encoder = new JSONObject();
		encoder.put("address", place.getAddress());
		encoder.put("name", place.getName());
		encoder.put("types", place.getTypes());
		
		System.out.println(encoder.toJSONString());
		
		String parseMe = "{\"types\":[\"home\",\"house\"],\"address\":\"2046 Dogwood Gardens Dr\",\"name\":\"My Home\"}";

		JSONParser parser = new JSONParser();
		
		JSONObject parsed = (JSONObject)parser.parse(parseMe);
		String address = (String)parsed.get("address");
		String name = (String)parsed.get("name");
		JSONArray types = (JSONArray)parsed.get("types");
		
		System.out.println("Address: "+address);
		System.out.println("Name: "+name);
		int length = types.size();
		for(int i=0; i<length; i++)
		{
			String type = (String)types.get(i);
			System.out.println("Type: "+type);
		}
		
		Set keys = parsed.keySet();
		for(Object key:keys)
		{
			System.out.println("Key: "+key);
		}
	}*/
	
	public void testMapSerialization() throws Exception
	{
		//Encode
		JSONObject encoder = new JSONObject();
		encoder.put("name", "openmobster");
		encoder.put("email", "openmobster@gmail.com");
		Map<String,String> coupons = new HashMap<String,String>();
		coupons.put("key1", "coupon1");
		coupons.put("key2", "coupon2");
		encoder.put("coupons", coupons);
		
		System.out.println(encoder.toJSONString());
		
		String parseMe = "{\"coupons\":{\"key2\":\"coupon2\",\"key1\":\"coupon1\"},\"email\":\"openmobster@gmail.com\",\"name\":\"openmobster\"}";
		
		JSONParser parser = new JSONParser();
		JSONObject parsed = (JSONObject)parser.parse(parseMe);
		JSONObject parsedCoupons = (JSONObject)parsed.get("coupons");
		Set<String> keys = parsedCoupons.keySet();
		for(String key:keys)
		{
			String value = (String)parsedCoupons.get(key);
			
			System.out.println(key+":"+value);
		}
	}
}
