/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.google;

import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.openmobster.core.location.PlaceProvider;
import org.openmobster.core.location.PlaceSPI;
import org.openmobster.core.location.LocationSPIException;

/**
 *
 * @author openmobster@gmail.com
 */
public final class GooglePlaceProvider implements PlaceProvider
{
	private static Logger log = Logger.getLogger(GooglePlaceProvider.class);
	
	private String apiKey;
	private Deserializer deserializer;
	
	public GooglePlaceProvider()
	{
		log.info("**********************************");
		log.info("Google Place Provider started.....");
		log.info("**********************************");
		InputStream is = null;
		try
		{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("location.properties");
			if(is == null)
			{
				return;
			}
			
			Properties properties = new Properties();
			properties.load(is);
			
			String googleApiKey = properties.getProperty("google_api_key");
			if(googleApiKey != null && googleApiKey.trim().length()>0)
			{
				this.apiKey = googleApiKey;
			}
			else
			{
				throw new IllegalStateException("Google API Key Not Found!!");
			}
		}
		catch(IOException ioe)
		{
			log.error(this, ioe);
			throw new RuntimeException(ioe);
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(IOException ioe){}
			}
		}
	}
	
	public String getApiKey()
	{
		return apiKey;
	}



	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}



	public Deserializer getDeserializer()
	{
		return deserializer;
	}


	public void setDeserializer(Deserializer deserializer)
	{
		this.deserializer = deserializer;
	}



	@Override
	public List<PlaceSPI> fetchNearbyPlaces(String latitude, String longitude,
			List<String> types, int radius,String name) throws LocationSPIException
	{
		try
		{
			String location = latitude+","+longitude;
			String typesParameter = null;
			if(types !=null && !types.isEmpty())
			{
				StringBuilder buffer = new StringBuilder();
				for(String type:types)
				{
					buffer.append(type+"|");
				}
				typesParameter = buffer.toString();
			}
			String sensor = "true"; //always invoked from a mobile phone
			
			//Setup the Url
			String url = "https://maps.googleapis.com/maps/api/place/search/xml?location="+location +
			"&radius="+radius+"&sensor="+sensor+"&key="+apiKey;
			if(typesParameter != null)
			{
				typesParameter = URLEncoder.encode(typesParameter, "UTF-8");
				url += "&types="+typesParameter;
			}
			if(name !=null && name.trim().length()>0)
			{
				String nameParameter = URLEncoder.encode(name,"UTF-8");
				url+="&name="+nameParameter;
			}
			
			//setup the request object
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			
			//send the request
			HttpResponse response = client.execute(request);
			
			//read the response
			String result = null;
			HttpEntity entity = response.getEntity();
			if(entity != null)
			{
				result = EntityUtils.toString(entity);
				
				List<PlaceSPI> places = this.deserializer.deserializeNearByPlaces(result);
				
				return places;
			}
			
			
			return null;
		}
		catch(Throwable t)
		{
			log.error(this, t);
			throw new LocationSPIException(t);
		}
	}

	@Override
	public PlaceSPI fetchPlace(String placeReference) throws LocationSPIException
	{
		try
		{
			String url = "https://maps.googleapis.com/maps/api/place/details/xml?reference="+placeReference+"&sensor=true&key="+this.apiKey;
			
			//setup the request object
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			
			//send the request
			HttpResponse response = client.execute(request);
			
			//read the response
			String result = null;
			HttpEntity entity = response.getEntity();
			if(entity != null)
			{
				result = EntityUtils.toString(entity);
				
				PlaceSPI place = this.deserializer.deserializePlace(result);
				
				return place;
			}
			
			return null;
		}
		catch(Throwable t)
		{
			log.error(this, t);
			throw new LocationSPIException(t);
		}
	}
}
