package com.dolibarrmaroc.com.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.dolibarrmaroc.com.models.Compte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;


@SuppressLint("NewApi")
public class GpsTrackingServiceDao {
	private static final String url = URL.URL+"servicegps.php";
	private String num ;
	private JSONParser jsonParser;
	private int level;
	private double latitude;
	private double longitude;
	private float speed ;
	private double altitude ;
	private float direction ;
	private String satellite;
	private String dateGps;
	private Compte compte;
	
	public GpsTrackingServiceDao() {
		super();
		
	}
		
	public GpsTrackingServiceDao(String num, int level, double latitude,
			double longitude, float speed, double altitude, float direction,
			String satellite, String dateGps, Compte compte) {
		super();
		this.num = num;
		this.level = level;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.altitude = altitude;
		this.direction = direction;
		this.satellite = satellite;
		this.dateGps = dateGps;
		this.compte = compte;
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		insertData();
	}




	public void insertData(){
		jsonParser = new JSONParser();
		
		ArrayList<NameValuePair> nameValue = new ArrayList<NameValuePair>();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		nameValue.add(new BasicNameValuePair("lat",latitude+""));
		nameValue.add(new BasicNameValuePair("lng",longitude+""));
		nameValue.add(new BasicNameValuePair("dateS",format.format( new Date())));
		nameValue.add(new BasicNameValuePair("dateGps",dateGps));
		nameValue.add(new BasicNameValuePair("imei",compte.getEmei()));
		nameValue.add(new BasicNameValuePair("num",num));
		nameValue.add(new BasicNameValuePair("battery",level+""));
		nameValue.add(new BasicNameValuePair("speed",speed+""));
		nameValue.add(new BasicNameValuePair("altitude",altitude+""));
		nameValue.add(new BasicNameValuePair("direction",direction+""));
		nameValue.add(new BasicNameValuePair("sat",satellite));
		nameValue.add(new BasicNameValuePair("iduser",compte.getIduser()));

		nameValue.add(new BasicNameValuePair("username", compte.getLogin()));
		nameValue.add(new BasicNameValuePair("password", compte.getPassword()));

		Log.i("MAPMEDROID DATA SEND",nameValue.toString());

		String res = jsonParser.makeHttpRequest(
				url, "POST", nameValue);

		Log.e("MAPMEDROID return",res);
		try{
		
			JSONObject json = new JSONObject(res);
			//Log.i("Resultat MAPMEDROID", json.getString("ok"));

		}catch(Exception e){
			Log.e("MAPMEDROID ERREUR", "Error in http connection " + e.toString());
		}
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public String getSatellite() {
		return satellite;
	}

	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}

	public String getDateGps() {
		return dateGps;
	}

	public void setDateGps(String dateGps) {
		this.dateGps = dateGps;
	}

	public Compte getCompte() {
		return compte;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	
	
	
}
