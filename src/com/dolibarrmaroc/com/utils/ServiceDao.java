package com.dolibarrmaroc.com.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.GpsTracker;


@SuppressLint("NewApi") public class ServiceDao {

	private JSONParser jsonParser;
	public ServiceDao() {
		super();
		jsonParser = new JSONParser();
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	public String insertData(GpsTracker gps,String imei,String num,String battery,Compte c,String fact){

			String retour ="-1";
			
			String resl = "no";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		String dateString = format.format( new Date());
			
			ArrayList<NameValuePair> nameValue = new ArrayList<NameValuePair>();
			
			
			nameValue.add(new BasicNameValuePair("lat",gps.getLatitude()+""));
			nameValue.add(new BasicNameValuePair("lng",gps.getLangitude()+""));
			nameValue.add(new BasicNameValuePair("dateS",dateString));
			nameValue.add(new BasicNameValuePair("dateGps",gps.getDateString()));
			nameValue.add(new BasicNameValuePair("imei",imei));
			nameValue.add(new BasicNameValuePair("num",num));
			nameValue.add(new BasicNameValuePair("battery",battery));
			nameValue.add(new BasicNameValuePair("speed",gps.getSpeed()+""));
			nameValue.add(new BasicNameValuePair("altitude",gps.getAltitude()+""));
			nameValue.add(new BasicNameValuePair("direction",gps.getDirection()+""));
			nameValue.add(new BasicNameValuePair("sat",gps.getSatellite()));
			nameValue.add(new BasicNameValuePair("username", c.getLogin()));
			nameValue.add(new BasicNameValuePair("password", c.getPassword()));
			nameValue.add(new BasicNameValuePair("facture", fact));
			
			Log.e("data envoyer",nameValue.toString());
			
			String url2 = URL.URL+"gpsservice.php";

			
			
			try{

				String res = jsonParser.makeHttpRequest(
						url2, "POST", nameValue);
				Log.e("result gps ",res);
				
				JSONObject json = new JSONObject(res);
				retour = json.getString("ok");
				resl = json.getString("feedback");
				//Log.i("Resultat ", json.getString("ok"));
				
			}catch(Exception e){
				retour = "-1";
				resl = "no";
				Log.e("log_tag", "Error in http connection " + e.toString());
			}
		
			//return retour;
			
			return resl;
	}
	
}
