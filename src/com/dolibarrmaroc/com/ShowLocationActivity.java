package com.dolibarrmaroc.com;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.utils.GpsTrackingServiceDao;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;


public class ShowLocationActivity extends Service{

	private static final String url = URL.URL+"servicegps.php";

	private int step = 0;
	private int stop = 0;
	private float batteryPct = 0;
	private JSONParser jsonParser;
	private int level = 0;
	private String imei ;
	private String num ;
	private String jsonString;
	private Compte compte;


	private  double latitude;
	private  double longitude;
	private  SimpleDateFormat format ;
	private  String dateString;

	private  float speed ;
	private  double altitude;
	private  float direction ;
	private  String satellite ;

	private GpsTrackingServiceDao dao;
	private Criteria criteria;

	private static final String TAG = "BOOMBOOMTESTGPS";
	private LocationManager mLocationManager = null;
	private int LOCATION_INTERVAL = 1000;
	private float LOCATION_DISTANCE = 16;

	private int firstExecute = 0;

	private LocationListener		onLocationChange	= new LocationListener()
	{
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}

		@Override
		public void onProviderEnabled(String provider)
		{
		}

		@Override
		public void onProviderDisabled(String provider)
		{
		}

		@Override
		public void onLocationChanged(Location location)
		{

			if(Float.parseFloat(compte.getLevel()) <= batteryPct){
				//Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();

				Log.d("Je suis dans onLocationChanged()",location.toString());
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
				String dateString = format.format( new Date(location.getTime()));
				
				Double latitude = location.getLatitude();
				Double longitude = location.getLongitude();
				float speed = location.getSpeed();
				double altitude = location.getAltitude();
				float direction = location.getBearing();
				String satellite = location.getProvider()+"/"+dateString;


				new GpsTrackingServiceDao(num, (int) batteryPct, latitude, longitude, speed, altitude, direction, satellite, dateString, compte);
			}
			else{
				Log.e(TAG, "onDestroy");
				if (mLocationManager != null) {
						try {
							mLocationManager.removeUpdates(this);
							Log.i(TAG, "Remove location listners,");
						} catch (Exception ex) {
							Log.i(TAG, "fail to remove location listners, ignore", ex);
						}
				}
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(intent == null) {
			stopService(new Intent(this,ShowLocationActivity.class));
		}else{
			if(firstExecute == 0){
				Bundle extras = intent.getExtras(); 
				if(extras == null){
					Log.d("Service onStart()","null");
				}else{

					compte = (Compte) intent.getSerializableExtra("user");
					Log.d("Service Instanciation onStartCommand() ","not null "+compte.toString());
					step = Integer.parseInt(compte.getStep());
					LOCATION_DISTANCE = Integer.parseInt(compte.getStop());
					level = Integer.parseInt(compte.getLevel());
					LOCATION_INTERVAL = (int) (step * 1000);
					onCreate();
					firstExecute = 123456789;
				}
			}
			
			Log.e(TAG, "onStartCommand");
			super.onStartCommand(intent, flags, startId);   
		}
		    
		return START_STICKY;
	}
	@Override
	public void onCreate()
	{
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		batteryPct = (level / (float)scale)*100;

		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		num = tManager.getLine1Number();
		num+="-serie: "+tManager.getSimSerialNumber();

		Log.e(TAG, "onCreate");
		initializeLocationManager();

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  LOCATION_INTERVAL, LOCATION_DISTANCE, onLocationChange);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
				onLocationChange);
	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}
}