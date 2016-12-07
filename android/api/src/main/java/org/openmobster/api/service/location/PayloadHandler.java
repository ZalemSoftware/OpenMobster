/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.api.service.location;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openmobster.android.api.location.LocationContext;
import org.openmobster.android.api.location.Request;
import org.openmobster.android.api.location.Response;
import org.openmobster.android.api.location.Place;
import org.openmobster.android.api.location.Address;

import org.openmobster.core.mobileCloud.android.util.XMLUtil;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PayloadHandler
{
	private static PayloadHandler singleton;
	
	private PayloadHandler()
	{
		
	}
	
	public static PayloadHandler getInstance()
	{
		if(PayloadHandler.singleton == null)
		{
			synchronized(PayloadHandler.class)
			{
				if(PayloadHandler.singleton == null)
				{
					PayloadHandler.singleton = new PayloadHandler();
				}
			}
		}
		return PayloadHandler.singleton;
	}
	
	public String serializeRequest(LocationContext locationContext)
	{
		try
		{
			Request request = (Request)locationContext.getAttribute("request");
			
			//Serializing the Request object
			JSONObject requestJSON = new JSONObject();
			String[] names = request.getNames();
			if(names != null && names.length > 0)
			{
				for(String name:names)
				{
					Object value = request.get(name);
					
					if(value instanceof List)
					{
						List<String> list = (List<String>)value;
						if(list.isEmpty())
						{
							continue;
						}
						
						JSONArray listAttribute = new JSONArray();
						for(String local:list)
						{
							listAttribute.put(local);
						}
						
						requestJSON.put(name, listAttribute);
					}
					else if(value instanceof Map)
					{
						Map<String,String> map = (Map<String,String>)value;
						if(map.isEmpty())
						{
							continue;
						}
						
						JSONObject mapAttribute = new JSONObject();
						Set<String> keys = map.keySet();
						for(String key:keys)
						{
							Object local = map.get(key);
							mapAttribute.put(key, local);
						}
						
						requestJSON.put(name, mapAttribute);
					}
					else if(value instanceof String)
					{
						requestJSON.put(name, (String)value);
					}
				}
			}
			
			String requestPayload = requestJSON.toString();
			
			//Serialize the LocationContext
			JSONObject locationContextJSON = new JSONObject();
			
			//Add latitude
			String latitude = locationContext.getLatitude();
			if(latitude != null && latitude.trim().length()>0)
			{
				locationContextJSON.put("latitude", latitude);
			}
			
			String longitude = locationContext.getLongitude();
			if(longitude != null && longitude.trim().length()>0)
			{
				locationContextJSON.put("longitude", longitude);
			}
			
			String placeReference = locationContext.getPlaceReference();
			if(placeReference != null && placeReference.trim().length()>0)
			{
				locationContextJSON.put("placeReference", placeReference);
			}
			
			List<String> placeTypes = locationContext.getPlaceTypes();
			if(placeTypes != null && !placeTypes.isEmpty())
			{
				JSONArray local = new JSONArray();
				for(String placeType:placeTypes)
				{
					local.put(placeType);
				}
				locationContextJSON.put("placeTypes", local);
			}
			
			Address address = locationContext.getAddress();
			if(address != null)
			{
				JSONObject addressJson = this.serializeAddress(address);
				locationContextJSON.put("address", addressJson);
			}
			
			//rest of the context
			names = locationContext.getNames();
			if(names != null && names.length>0)
			{
				for(String name:names)
				{
					Object value = locationContext.getAttribute(name);
					if(value instanceof String)
					{
						locationContextJSON.put(name, (String)value);
					}
				}
			}
			
			String locationPayload = locationContextJSON.toString();
			
			//Set up the xml
			StringBuilder buffer = new StringBuilder();
			buffer.append("<location-request>\n");
			buffer.append("<service>"+request.getService()+"</service>\n");
			buffer.append("<request-payload>"+XMLUtil.addCData(requestPayload)+"</request-payload>\n");
			buffer.append("<location-payload>"+XMLUtil.addCData(locationPayload)+"</location-payload>\n");
			buffer.append("</location-request>\n");
			
			String xml = buffer.toString();
			
			return xml;
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(),"serializeRequest", new Object[]{
				"Exception: "+e.getMessage()
			});
			throw syse;
		}
	}
	
	public LocationContext deserializeResponse(String xml)
	{
		try
		{
			LocationContext locationContext = new LocationContext();
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			Element locationResponse = (Element)root.getElementsByTagName("location-response").item(0);
			Element responsePayloadElement = (Element)locationResponse.getElementsByTagName("response-payload").item(0);
			Element locationPayloadElement = (Element)locationResponse.getElementsByTagName("location-payload").item(0);
			
			String responsePayload = responsePayloadElement.getTextContent();
			String locationPayload = locationPayloadElement.getTextContent();
			
			//Parse the response
			Response response = new Response();
			JSONObject parsedResponse = new JSONObject(responsePayload);
			Iterator keys = parsedResponse.keys();
			if(keys != null)
			{
				while(keys.hasNext())
				{
					String key = (String)keys.next();
					Object value = parsedResponse.get(key);
					if(value instanceof String)
					{
						response.setAttribute(key, (String)value);
					}
					else if(value instanceof JSONArray)
					{
						JSONArray array = (JSONArray)value;
						List<String> list = new ArrayList<String>();
						int length = array.length();
						for(int i=0; i<length; i++)
						{
							list.add((String)array.get(i));
						}
						response.setListAttribute(key, list);
					}
					else if(value instanceof JSONObject)
					{
						JSONObject mapObject = (JSONObject)value;
						Map<String,String> map = new HashMap<String,String>();
						
						Iterator mapKeys = mapObject.keys();
						while(mapKeys.hasNext())
						{
							String mapKey = (String)mapKeys.next();
							String mapValue = mapObject.getString(mapKey);
							map.put(mapKey, mapValue);
						}
						response.setMapAttribute(key, map);
					}
				}
			}
			locationContext.setAttribute("response", response);
			
			//parse the LocationContext
			JSONObject parsedContext = new JSONObject(locationPayload);
			keys = parsedContext.keys();
			if(keys != null)
			{
				while(keys.hasNext())
				{
					String key = (String)keys.next();
					Object value = parsedContext.get(key);
					if(value instanceof String)
					{
						locationContext.setAttribute(key, (String)value);
					}
				}
			}
			
			//Parse the Places
			if(parsedContext.has("places"))
			{
				JSONArray parsedPlaces = parsedContext.getJSONArray("places");
				if(parsedPlaces != null)
				{
					List<Place> places = new ArrayList<Place>();
					int length = parsedPlaces.length();
					for(int i=0; i<length; i++)
					{
						JSONObject parsedPlace = parsedPlaces.getJSONObject(i);
						Place place = this.deserializePlace(parsedPlace);
						places.add(place);
					}
					locationContext.setNearbyPlaces(places);
				}
			}
			
			//Place Details
			if(parsedContext.has("placeDetails"))
			{
				JSONObject placeDetails = parsedContext.getJSONObject("placeDetails");
				if(placeDetails != null)
				{
					Place details = this.deserializePlace(placeDetails);
					locationContext.setPlaceDetails(details);
				}
			}
			
			//Place Types
			if(parsedContext.has("placeTypes"))
			{
				JSONArray parsedPlaceTypes = parsedContext.getJSONArray("placeTypes");
				if(parsedPlaceTypes != null)
				{
					List<String> placeTypes = new ArrayList<String>();
					int length = parsedPlaceTypes.length();
					for(int i=0; i<length; i++)
					{
						String parsedPlaceType = parsedPlaceTypes.getString(i);
						placeTypes.add(parsedPlaceType);
					}
					locationContext.setPlaceTypes(placeTypes);
				}
			}
			
			//Address
			if(parsedContext.has("address"))
			{
				JSONObject parsedAddress = parsedContext.getJSONObject("address");
				if(parsedAddress != null)
				{
					Address address = this.deserializeAddress(parsedAddress);
					locationContext.setAddress(address);
				}
			}
			
			return locationContext;
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(),"deserializeResponse", new Object[]{
				"Exception: "+e.getMessage()
			});
			throw syse;
		}
	}
	//-----------------------------------------------------------------------------------------
	private Place deserializePlace(JSONObject parsedPlace) throws Exception
	{
		Place place = new Place();
		
		if(parsedPlace.has("phone"))
		{
			String phone = parsedPlace.getString("phone");
			if(phone != null && phone.trim().length()>0)
			{
				place.setPhone(phone);
			}
		}
		
		if(parsedPlace.has("address"))
		{
			String value = parsedPlace.getString("address");
			if(value != null && value.trim().length()>0)
			{
				place.setAddress(value);
			}
		}
		
		if(parsedPlace.has("international_phone_number"))
		{
			String value = parsedPlace.getString("international_phone_number");
			if(value != null && value.trim().length()>0)
			{
				place.setInternationalPhoneNumber(value);
			}
		}
		
		if(parsedPlace.has("url"))
		{
			String value = parsedPlace.getString("url");
			if(value != null && value.trim().length()>0)
			{
				place.setUrl(value);
			}
		}
		
		if(parsedPlace.has("website"))
		{
			String value = parsedPlace.getString("website");
			if(value != null && value.trim().length()>0)
			{
				place.setWebsite(value);
			}
		}
		
		if(parsedPlace.has("icon"))
		{
			String value = parsedPlace.getString("icon");
			if(value != null && value.trim().length()>0)
			{
				place.setIcon(value);
			}
		}
		
		if(parsedPlace.has("name"))
		{
			String value = parsedPlace.getString("name");
			if(value != null && value.trim().length()>0)
			{
				place.setName(value);
			}
		}
		
		if(parsedPlace.has("latitude"))
		{
			String value = parsedPlace.getString("latitude");
			if(value != null && value.trim().length()>0)
			{
				place.setLatitude(value);
			}
		}
		
		if(parsedPlace.has("longitude"))
		{
			String value = parsedPlace.getString("longitude");
			if(value != null && value.trim().length()>0)
			{
				place.setLongitude(value);
			}
		}
		
		if(parsedPlace.has("id"))
		{
			String value = parsedPlace.getString("id");
			if(value != null && value.trim().length()>0)
			{
				place.setId(value);
			}
		}
		
		if(parsedPlace.has("reference"))
		{
			String value = parsedPlace.getString("reference");
			if(value != null && value.trim().length()>0)
			{
				place.setReference(value);
			}
		}
		
		if(parsedPlace.has("rating"))
		{
			String value = parsedPlace.getString("rating");
			if(value != null && value.trim().length()>0)
			{
				place.setRating(value);
			}
		}
		
		if(parsedPlace.has("vicinity"))
		{
			String value = parsedPlace.getString("vicinity");
			if(value != null && value.trim().length()>0)
			{
				place.setVicinity(value);
			}
		}
		
		if(parsedPlace.has("html_attribution"))
		{
			String value = parsedPlace.getString("html_attribution");
			if(value != null && value.trim().length()>0)
			{
				place.setHtmlAttribution(value);
			}
		}
		
		if(parsedPlace.has("types"))
		{
			JSONArray value = parsedPlace.getJSONArray("types");
			if(value != null && value.length()>0)
			{
				int length = value.length();
				List<String> types = new ArrayList<String>();
				for(int i=0; i<length; i++)
				{
					String type = value.getString(i);
					types.add(type);
				}
				place.setTypes(types);
			}
		}
		
		return place;
	}
	
	private Address deserializeAddress(JSONObject parsedAddress) throws Exception
	{
		Address address = new Address();
		
		if(parsedAddress.has("street"))
		{
			String street = parsedAddress.getString("street");
			if(street != null && street.trim().length()>0)
			{
				address.setStreet(street);
			}
		}
		
		if(parsedAddress.has("city"))
		{
			String value = parsedAddress.getString("city");
			if(value != null && value.trim().length()>0)
			{
				address.setCity(value);
			}
		}
		
		if(parsedAddress.has("state"))
		{
			String value = parsedAddress.getString("state");
			if(value != null && value.trim().length()>0)
			{
				address.setState(value);
			}
		}
		
		if(parsedAddress.has("country"))
		{
			String value = parsedAddress.getString("country");
			if(value != null && value.trim().length()>0)
			{
				address.setCounty(value);
			}
		}
		
		if(parsedAddress.has("zipcode"))
		{
			String value = parsedAddress.getString("zipcode");
			if(value != null && value.trim().length()>0)
			{
				address.setZipCode(value);
			}
		}
		
		if(parsedAddress.has("county"))
		{
			String value = parsedAddress.getString("county");
			if(value != null && value.trim().length()>0)
			{
				address.setCounty(value);
			}
		}
		
		if(parsedAddress.has("postal"))
		{
			String value = parsedAddress.getString("postal");
			if(value != null && value.trim().length()>0)
			{
				address.setPostal(value);
			}
		}
		
		if(parsedAddress.has("latitude"))
		{
			String value = parsedAddress.getString("latitude");
			if(value != null && value.trim().length()>0)
			{
				address.setLatitude(value);
			}
		}
		
		if(parsedAddress.has("longitude"))
		{
			String value = parsedAddress.getString("longitude");
			if(value != null && value.trim().length()>0)
			{
				address.setLongitude(value);
			}
		}
		
		if(parsedAddress.has("radius"))
		{
			String value = parsedAddress.getString("radius");
			if(value != null && value.trim().length()>0)
			{
				address.setRadius(value);
			}
		}
		
		if(parsedAddress.has("woetype"))
		{
			String value = parsedAddress.getString("woetype");
			if(value != null && value.trim().length()>0)
			{
				address.setWoetype(value);
			}
		}
		
		if(parsedAddress.has("woeid"))
		{
			String value = parsedAddress.getString("woeid");
			if(value != null && value.trim().length()>0)
			{
				address.setWoeid(value);
			}
		}
		
		return address;
	}
	
	private JSONObject serializeAddress(Address address) throws Exception
	{
		JSONObject json = new JSONObject();
		
		String value = address.getStreet();
		if(value != null)
		{
			json.put("street", value);
		}
		
		value = address.getCity();
		if(value != null)
		{
			json.put("city", value);
		}
		
		value = address.getState();
		if(value != null)
		{
			json.put("state", value);
		}
		
		value = address.getCountry();
		if(value != null)
		{
			json.put("country", value);
		}
		
		value = address.getZipCode();
		if(value != null)
		{
			json.put("zipcode", value);
		}
		
		value = address.getCounty();
		if(value != null)
		{
			json.put("county", value);
		}
		
		value = address.getPostal();
		if(value != null)
		{
			json.put("postal", value);
		}
		
		value = address.getLatitude();
		if(value != null)
		{
			json.put("latitude", value);
		}
		
		value = address.getLongitude();
		if(value != null)
		{
			json.put("longitude", value);
		}
		
		value = address.getRadius();
		if(value != null)
		{
			json.put("radius", value);
		}
		
		value = address.getWoetype();
		if(value != null)
		{
			json.put("woetype", value);
		}
		
		value = address.getWoeid();
		if(value != null)
		{
			json.put("woeid", value);
		}
		
		return json;
	}
}
