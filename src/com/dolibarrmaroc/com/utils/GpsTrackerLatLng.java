package com.dolibarrmaroc.com.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

import com.dolibarrmaroc.com.models.GpsTracker;

public class GpsTrackerLatLng extends Activity{
	
	private GpsTracker gps;
	public GpsTrackerLatLng() {
		gps = new GpsTracker();
	}
	public GpsTracker getGps(){
		
		LocationManager mlocManager=null;
        android.location.LocationListener mlocListener;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

       if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           if(MyLocationListener.latitude>0)
           {
                gps.setLangitude(""+MyLocationListener.longitude);
                gps.setLatitude(""+MyLocationListener.latitude);
           }
            else
            {
            	gps.setLangitude(""+0);
                gps.setLatitude(""+0);
             }
         } 
       
		return gps;
	}
}
