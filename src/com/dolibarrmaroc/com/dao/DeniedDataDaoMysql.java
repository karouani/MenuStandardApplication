package com.dolibarrmaroc.com.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class DeniedDataDaoMysql implements DeniedDataDao {

	private String urlData = URL.URL+"dataerror.php";
	private JSONParser parser ;

	@Override
	public int sendMyErrorData(List<String> in, Compte c) {
		// TODO Auto-generated method stub
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("nbr",in.size()+""));



		for (int i = 0; i < in.size(); i++) {
			nameValuePairs.add(new BasicNameValuePair("input"+i,in.get(i)));
		}


		try {
			parser = new JSONParser();
			String json = parser.makeHttpRequest(urlData, "POST", nameValuePairs);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("json insert eroor data ",e.getMessage() +" << ");
		}

		return 0;
	}

}
