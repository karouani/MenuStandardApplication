package com.dolibarrmaroc.com.utils;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckOutNet implements Serializable{

	private static Context ctx;
	public static String url_update ="http://41.142.241.192:8005/android/marocgeo.php";
	public static String type = "alsdroidoffline";
	
	
	public static boolean isNetworkConnected(Context context) {
		
		ctx = context;

		ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()){
			boolean t = isOnline();
			if(t) return true;
			else {
				return false;
			}
		}else{
			return false;
		}
	}
	
	public static boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        try {
	            URL url = new URL("http://www.google.com");
	            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	            urlc.setConnectTimeout(3000);
	            urlc.connect();
	            
	            Log.e("url code ",urlc.getResponseCode()+"");
	            if ((urlc.getResponseCode() == 200) || (urlc.getResponseCode() == 302)) {
	                return new Boolean(true);
	            }
	        } catch (MalformedURLException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    return false;
	}
}
