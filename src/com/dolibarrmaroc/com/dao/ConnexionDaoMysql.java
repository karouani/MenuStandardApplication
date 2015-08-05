package com.dolibarrmaroc.com.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.LabelService;
import com.dolibarrmaroc.com.models.MyTicketWitouhtProduct;
import com.dolibarrmaroc.com.models.Services;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;



public class ConnexionDaoMysql implements ConnexionDao {

	private JSONParser jsonParser;
	
	private static final String LOGIN_URL = URL.URL+"login.php";
	public static final String url = URL.URL+"services.php";
	
	//JSON element ids from repsonse of php script:
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ID = "id";
	private static final String TAG_PROFILE = "profile";
	private static final String TAG_ACTIVER = "activer";
	
	private int id;
	
	private Compte compte;
	private ConfigGps gpsTracker;
	private String jsonString;
	
	private MyTicketWitouhtProduct my;

	public ConnexionDaoMysql() {
		jsonParser = new JSONParser();
		compte = new Compte();
		gpsTracker = new ConfigGps();
	}
	Context context;
	public Compte login(String login, String password) {
		int success;
		try {
			//Getting the Object of TelephonyManager 
			String login2[] = login.split("/karouaniYassine/");
			login = login2[0];
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", login));
			params.add(new BasicNameValuePair("imei", login2[1]));
			params.add(new BasicNameValuePair("password", password));

			//Log.d("request!", LOGIN_URL);
			Log.e("request!", params.toString());
			// getting product details by making HTTP request
			jsonString =  jsonParser.makeHttpRequest(
					LOGIN_URL, "POST", params);

			String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			
			
			
			Log.e("Login Erreur !", jsonString);

			if ("<!DOCTYP".equals(jsonString.substring(0,8))) {


				compte.setId(0);
				compte.setActiver(0);
				compte.setProfile("");
				compte.setLogin(login);
				compte.setPassword(password);
				compte.setMessage("Login ou password est incorrect !!");


				return compte;
			}else{
				JSONObject json = new JSONObject(jsonString);
				// check your log for json response
				//Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_ACTIVER);
				if(success != 0){
					Log.d("Login Successful!", json.toString());

					compte.setId(json.getInt(TAG_ID));
					compte.setActiver(json.getInt(TAG_ACTIVER));
					compte.setProfile(json.getString(TAG_PROFILE));
					compte.setLogin(login);
					compte.setPassword(password);
					compte.setMessage(json.getString(TAG_MESSAGE));
					compte.setLevel(json.getString("battery"));
					compte.setEmei(json.getString("emei"));
					compte.setIduser(json.getString(TAG_ID));
					compte.setStep(json.getString("step"));
					compte.setStop(json.getString("stop"));
					compte.setPermission(json.getInt("permission"));
					compte.setPermissionbl(json.getInt("blcmd"));
					
					gpsTracker.setEmei(json.getString("emei"));
					gpsTracker.setIduser(json.getString("iduser"));
					gpsTracker.setStep(json.getString("step"));
					gpsTracker.setStop(json.getString("stop"));
					gpsTracker.setLevel(json.getString("battery"));
					
					id = json.getInt("id_service");
					
					
					my = new MyTicketWitouhtProduct();
					if(compte.getProfile().toLowerCase().equals("technicien")){
						my.setNameSte("test societe");
					}else{
						my.setNameSte(json.getString("nameSte"));
						my.setAddresse(json.getString("addresse"));
						my.setDescription(json.getString("desc"));
						my.setFax(json.getString("fax"));
						my.setTel(json.getString("tel"));
						my.setPatente(json.getString("patente"));
						my.setSiteWeb(json.getString("siteWeb"));
						my.setIF(json.getString("IF"));
					}
					
					
					
					return compte;
				}

			}



		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;		
	}

	@Override
	public ConfigGps getGpsConfig() {
	
		return gpsTracker;

	}

	@Override
	public Services getService(String login, String password) {
		Services s =new Services();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",login));
		nameValuePairs.add(new BasicNameValuePair("password",password));
		nameValuePairs.add(new BasicNameValuePair("id_serv",id+""));
		Log.e("nameValuePairs!", nameValuePairs.toString());
		String jsonString =  jsonParser.makeHttpRequest(
				url , "POST", nameValuePairs);

		List<Services> list = new ArrayList<Services>();

		
		Log.e("Services Retournee >> ", jsonString);
		try{
			
			String stfomat = jsonString.substring(jsonString.indexOf("["),jsonString.lastIndexOf("]")+1);
			
			JSONArray jArray = new JSONArray(stfomat);
			// check your log for json response
			//Log.d("Login attempt", jArray.toString());


			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);
				
				Log.d("Produit trouver Successful!", json.toString());

				s.setId(json.getInt(TAG_ID));
				s.setNmb_cmp(json.getInt("nmb"));
				s.setService(json.getString("service"));
				List<LabelService> labels = new ArrayList<>();
				for (int j = 0; j < s.getNmb_cmp(); j++) {
					LabelService lb = new LabelService(json.getString("label"+j));
					labels.add(lb);
				}
				s.setLabels(labels);
			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return s;
	}

	@Override
	public MyTicketWitouhtProduct lodSociete(String st) {
		// TODO Auto-generated method stub
		return my;
	}
}
