package com.dolibarrmaroc.com.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.utils.Base64;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class PayementDaoMysql implements PayementDao{

	private JSONParser jsonParser;
	private String url = URL.URL+"factclt.php";
	private String pay = URL.URL+"paiement.php";

	public PayementDaoMysql() {
		super();
		jsonParser = new JSONParser();
	}

	@Override
	public List<Payement> getFactures(Compte c) {
		
		Log.e("Data avant >>>",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		Log.e(">>Connecteur ", c.toString());
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("id",c.getIduser()));

		String jsonString =  jsonParser.makeHttpRequest(
				url , "POST", nameValuePairs);

		List<Payement> list = new ArrayList<Payement>();
		
		Log.e(">>Crypted JSON", jsonString);
		
		try{
			String stfomat = jsonString.substring(jsonString.indexOf("["),jsonString.lastIndexOf("]")+1);
			
			JSONArray jArray = new JSONArray(stfomat);
			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);
				Payement pay = new Payement();
				pay.setId(json.getInt("rowid"));
				pay.setNum(json.getString("facnumber"));
				pay.setTotal(json.getDouble("total_ttc"));
				pay.setAmount(json.getDouble("amount"));
				pay.setSoc(json.getInt("soc"));

				//{"rowid":"2095","facnumber":"2015-02436","amount":1000,"total_ttc":"2700.00000000","soc":"67"}]
				list.add(pay);
			}
		}catch(Exception e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		Log.e("List DAta",list.toString()+"");
		Log.e("Data apres >>>",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		return list;
	}

	@Override
	public String insertPayement(Reglement reg,Compte c) {
		//Log.e("nsertion Payement",reg+"");
		String res ="no";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("create","create"));
		
		nameValuePairs.add(new BasicNameValuePair("user",reg.getIdUser()));
		nameValuePairs.add(new BasicNameValuePair("cheque",reg.getNum_paiement()));
		nameValuePairs.add(new BasicNameValuePair("type",reg.getPaiementcode()));
		nameValuePairs.add(new BasicNameValuePair("amount",reg.getAmount()+""));
		nameValuePairs.add(new BasicNameValuePair("facid",reg.getId()+""));//
		nameValuePairs.add(new BasicNameValuePair("reste",reg.getFk_facture()));
		
		
		
		Log.e("Reponse Paiement insert ",nameValuePairs.toString());
		JSONObject obj = new JSONObject();
		try {
			String jsonString =  jsonParser.makeHttpRequest(
					pay , "POST", nameValuePairs);
			
			Log.e("Reponse Paiement insert ",jsonString);
			 obj = new JSONObject(jsonString);
			 if(obj != null){
				 if(obj.has("cicin")){
					 res = obj.getString("cicin");
				 }
			 }
			// Log.e("json>> ",obj.getString("code"));
			
		} catch (Exception e) {
			// TODO: handle exception
			res ="no";
			Log.e("error parseing "+obj,e.getMessage() +" << ");
		}
		Log.e("Reponse Paiement 2 ",res);
		return res;
	}

}
