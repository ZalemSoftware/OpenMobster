/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;

/**
 *
 * @author openmobster@gmail.com
 */
public final class LocationReader
{
	private Context context;
	private Location gpsLocation;
	private Location networkLocation;
	private LocationListener gpsListener;
	private LocationListener networkListener;
	private LocationContext locationContext;
	
	public LocationReader(Context context)
	{
		this.context = context.getApplicationContext();
	}
	
	public void start()
	{
		this.locationContext = new LocationContext();
		this.locationContext.start();
		
		//wait for the handler to be ready
		while(this.locationContext.handler==null);
		
		//now start the GPS Reader
		this.locationContext.handler.post(new GPSLocationReader());
		
		//now start the Network Reader
		this.locationContext.handler.post(new NetworkLocationReader());
	}
	
	public void stop()
	{
		LocationManager locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
		
		if(this.gpsListener != null)
		{
			locationManager.removeUpdates(this.gpsListener);
		}
		
		if(this.networkListener != null)
		{
			locationManager.removeUpdates(this.networkListener);
		}
		
		//cleanup
		this.gpsLocation = null;
		this.networkLocation = null;
		
		//now stop the LocationContext
		this.locationContext.handler.getLooper().quit();
	}
	
	public Location getLocation()
	{
		try
		{
			int counter = 6;
			while(counter > 0)
			{
				//Get the GPS Location preferably
				if(this.gpsLocation != null)
				{
					return this.gpsLocation;
				}
				
				Thread.sleep(10000);
				counter--;
			}
			
			//If I get here...fall to the last resort and send back a network location
			//this could be null though
			return this.networkLocation;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private class LocationContext extends Thread
	{
		private Handler handler = null;
		
		public void run()
		{
			Looper.prepare();
			
			this.handler = new Handler();
			
			Looper.loop();
		}
	}
	
	private class GPSLocationReader implements Runnable
	{
		public void run()
		{
			LocationManager locationManager = (LocationManager)context.
			getSystemService(Context.LOCATION_SERVICE);
			
			gpsListener = new GPSLocationListener();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		}
	}
	
	private class NetworkLocationReader implements Runnable
	{
		public void run()
		{
			LocationManager locationManager = (LocationManager)context.
					getSystemService(Context.LOCATION_SERVICE);
					
			networkListener = new NetworkLocationListener();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
		}
	}
	
	private class GPSLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{
			gpsLocation = location;
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			
		}
	}
	
	private class NetworkLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{
			networkLocation = location;
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			
		}
	}
}
