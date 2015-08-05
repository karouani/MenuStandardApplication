/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dolibarrmaroc.com;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.dolibarrmaroc.com.dashboard.DashboardActivity;
import com.dolibarrmaroc.com.utils.JSONParser;

import com.rest.client.arc.Arc;
import com.rest.client.arc.Response;

import android.os.Bundle;
import android.widget.TextView;

/**
 * This is the activity for feature 3 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class F3Activity extends DashboardActivity 
{

/**
 * onCreate
 *
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 * @param savedInstanceState Bundle
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    setContentView (R.layout.activity_f3);
    setTitleFromActivityLabel (R.id.title_text);
    
    
    
	try {
		
		/*Arc arc=new Arc("http://takamaroc.com/htdocs/doliDroid/app.gz");
		arc.setBasicAuth("vendeur", "1234");
	    Response resp = arc.request("/htdocs/doliDroid/restWebServices.php")
	            .param("username", "vendeur")
	            .param("password", 1234)
	            .param("id", 45)
	            .get();
	            
		
		Response resp = arc.request("").get();
		*/
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username","vendeur"));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password","1234"));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("id","45"));
		
		JSONParser json = new JSONParser();
		String resp = json.makeHttpRequest("http://takamaroc.com/htdocs/doliDroid/listclient.php", "GET", nameValuePairs);
		TextView txt = (TextView) findViewById(R.id.restWeb);
		txt.setText(resp);
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}

}
    
} // end class
