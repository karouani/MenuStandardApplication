package com.dolibarrmaroc.com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

	public static double latitude;
	public static double longitude;
	public static SimpleDateFormat format ;
	public static String dateString;
	
	public static float speed ;
	public static double altitude;
	public static float direction ;
	public static String satellite ;
		
	public MyLocationListener() {
		super();
	}


	@Override
	public void onLocationChanged(Location location)
	{
		format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		latitude=location.getLatitude();
		longitude=location.getLongitude();
		dateString = format.format( new Date(location.getTime()));
		speed = location.getSpeed();
		altitude = location.getAltitude();
		direction = location.getBearing();
		satellite = location.getProvider();
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		//print "Currently GPS is Disabled";
	}
	@Override
	public void onProviderEnabled(String provider)
	{
		//print "GPS got Enabled";
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}
	
}