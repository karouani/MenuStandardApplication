package com.dolibarrmaroc.com.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.models.Societe;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class CommercialDaoMysql implements CommercialDao{

	private String urlData = URL.URL+"prospection.php";
	private String url = URL.URL+"allclient.php";
	private JSONParser parser ;

	public CommercialDaoMysql() {
		// TODO Auto-generated constructor stub
		parser = new JSONParser();
	}

	@Override
	public String insert(Compte c,Prospection p) {
		Log.e("Appel INSERTION", p.toString());
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("create","create"));
		
		if (p.getParticulier() == 1) {
			nameValuePairs.add(new BasicNameValuePair("nom",p.getFirstname()));
		}else{
			nameValuePairs.add(new BasicNameValuePair("nom",p.getName()));
		}
		
        nameValuePairs.add(new BasicNameValuePair("firstname",p.getLastname()));
        nameValuePairs.add(new BasicNameValuePair("particulier",p.getParticulier()+""));
        nameValuePairs.add(new BasicNameValuePair("client",p.getClient()+""));
        nameValuePairs.add(new BasicNameValuePair("address",p.getAddress()));
        if(p.getZip() != null)
        nameValuePairs.add(new BasicNameValuePair("zip",p.getZip()));
        
        nameValuePairs.add(new BasicNameValuePair("town",p.getTown()));
        nameValuePairs.add(new BasicNameValuePair("phone",p.getPhone()));
        nameValuePairs.add(new BasicNameValuePair("fax",p.getFax()));
        nameValuePairs.add(new BasicNameValuePair("email",p.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("capital",p.getCapital()));
        nameValuePairs.add(new BasicNameValuePair("idprof1",p.getIdprof1()));
        nameValuePairs.add(new BasicNameValuePair("idprof2",p.getIdprof2()));
        nameValuePairs.add(new BasicNameValuePair("idprof3",p.getIdprof3()));
        nameValuePairs.add(new BasicNameValuePair("idprof4",p.getIdprof4()));
        nameValuePairs.add(new BasicNameValuePair("typent_id",p.getTypent_id()));
        nameValuePairs.add(new BasicNameValuePair("effectif_id",p.getEffectif_id()));
        nameValuePairs.add(new BasicNameValuePair("assujtva_value",p.getTva_assuj()+""));
        nameValuePairs.add(new BasicNameValuePair("status",p.getStatus()+""));
		nameValuePairs.add(new BasicNameValuePair("commercial_id",c.getIduser()));
        nameValuePairs.add(new BasicNameValuePair("country_id",p.getCountry_id()+""));
        nameValuePairs.add(new BasicNameValuePair("forme_juridique_code",p.getForme_juridique_code()));
		
        nameValuePairs.add(new BasicNameValuePair("latitude",p.getLatitude()+""));
        nameValuePairs.add(new BasicNameValuePair("longitude",p.getLangitude()+""));
		
		String json = parser.makeHttpRequest(urlData, "POST", nameValuePairs);
		
		Log.d("Insertion Message", json);
		String retour = "";
		
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray arr = obj.getJSONArray("message");
			int k =arr.length() - 1;
			retour = arr.getString(k);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.e("Retour ",retour);
		return retour;
	}

	@Override
	public ProspectData getInfos(Compte c) {
		
		ProspectData data = new ProspectData();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		
		String json = parser.makeHttpRequest(urlData, "POST", nameValuePairs);
		Log.e("RepondreMoi", json);
		
		/*{
			"town":[
			        {"ville":"CASABLANCA"},{"ville":"RABAT"}],
			"formJuridique":[
			                 {"code":"2121","nom":"Soci\u00e9t\u00e9 A R\u00e9sponsabilit\u00e9 Limit\u00e9e"}]
	    }*/
		
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray jarrayTown = obj.getJSONArray("town");
			JSONArray jarrayForm = obj.getJSONArray("formJuridique");
			JSONArray jarrayType = obj.getJSONArray("typent");
			
			List<String> list = new ArrayList<>();
			
			List<String> juridique = new ArrayList<>();
			List<String> typent = new ArrayList<>();
			
			HashMap<String, String> juridique_code= new HashMap<>();
			HashMap<String,String> typent_code= new HashMap<>();
			HashMap<String,String> typent_id= new HashMap<>();
		
			
			for (int i = 0; i < jarrayTown.length(); i++) {
				if(!"".equals(jarrayTown.getJSONObject(i).getString("ville")) || !"null".equals(jarrayTown.getJSONObject(i).getString("ville") )){
					list.add(jarrayTown.getJSONObject(i).getString("ville"));
				}
			}
			
			for (int i = 0; i < jarrayForm.length(); i++) {
				juridique.add(jarrayForm.getJSONObject(i).getString("nom"));
				juridique_code.put(jarrayForm.getJSONObject(i).getString("nom"), jarrayForm.getJSONObject(i).getString("code"));
			}
			
			for (int i = 0; i < jarrayType.length(); i++) {
				typent.add(jarrayType.getJSONObject(i).getString("labelle"));
				typent_code.put(jarrayType.getJSONObject(i).getString("labelle"), jarrayType.getJSONObject(i).getString("code"));
				typent_id.put(jarrayType.getJSONObject(i).getString("labelle"), jarrayType.getJSONObject(i).getString("id"));
			}
			
			data.setJuridique(juridique);
			data.setVilles(list);
			data.setTypent(typent);
			data.setJuridique_code(juridique_code);
			data.setTypent_code(typent_code);
			data.setTypent_id(typent_id);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return data;
	}

	@Override
	public List<Societe> getAll(Compte c) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		List<Societe> list = new ArrayList<>();
		
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		
		String json = parser.makeHttpRequest(url, "POST", nameValuePairs);
		Log.e("RepondreMoi", json);
	
		
		try {
			JSONArray jarray = new JSONArray(json);
			for (int i = 0; i < jarray.length(); i++) {
				JSONObject obj = jarray.getJSONObject(i);
				Societe s = new Societe(obj.getInt("rowid"), 
										obj.getString("name"), 
										obj.getString("address"), 
										obj.getString("town"), 
										obj.getString("phone"), 
										obj.getString("fax"), 
										obj.getString("email"), 
										obj.getInt("type"), 
										obj.getInt("company"), 
										obj.getDouble("latitude"), 
										obj.getDouble("longitude"));
				list.add(s);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	
	@Override
	public String update(Compte c,Prospection p) {
		Log.e("Appel INSERTION", p.toString());
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("create","update"));
		
		if (p.getParticulier() == 1) {
			nameValuePairs.add(new BasicNameValuePair("nom",p.getFirstname()));
		}else{
			nameValuePairs.add(new BasicNameValuePair("nom",p.getName()));
		}
		
        nameValuePairs.add(new BasicNameValuePair("firstname",p.getLastname()));
        nameValuePairs.add(new BasicNameValuePair("rowid",p.getId()+""));
        nameValuePairs.add(new BasicNameValuePair("particulier",p.getParticulier()+""));
        nameValuePairs.add(new BasicNameValuePair("client",p.getClient()+""));
        nameValuePairs.add(new BasicNameValuePair("address",p.getAddress()));
        if(p.getZip() != null)
        nameValuePairs.add(new BasicNameValuePair("zip",p.getZip()));
        
        nameValuePairs.add(new BasicNameValuePair("town",p.getTown()));
        nameValuePairs.add(new BasicNameValuePair("phone",p.getPhone()));
        nameValuePairs.add(new BasicNameValuePair("fax",p.getFax()));
        nameValuePairs.add(new BasicNameValuePair("email",p.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("capital",p.getCapital()));
        nameValuePairs.add(new BasicNameValuePair("idprof1",p.getIdprof1()));
        nameValuePairs.add(new BasicNameValuePair("idprof2",p.getIdprof2()));
        nameValuePairs.add(new BasicNameValuePair("idprof3",p.getIdprof3()));
        nameValuePairs.add(new BasicNameValuePair("idprof4",p.getIdprof4()));
        nameValuePairs.add(new BasicNameValuePair("typent_id",p.getTypent_id()));
        nameValuePairs.add(new BasicNameValuePair("effectif_id",p.getEffectif_id()));
        nameValuePairs.add(new BasicNameValuePair("assujtva_value",p.getTva_assuj()+""));
        nameValuePairs.add(new BasicNameValuePair("status",p.getStatus()+""));
		nameValuePairs.add(new BasicNameValuePair("commercial_id",c.getIduser()));
        nameValuePairs.add(new BasicNameValuePair("country_id",p.getCountry_id()+""));
        nameValuePairs.add(new BasicNameValuePair("forme_juridique_code",p.getForme_juridique_code()));
		
        nameValuePairs.add(new BasicNameValuePair("latitude",p.getLatitude()+""));
        nameValuePairs.add(new BasicNameValuePair("longitude",p.getLangitude()+""));
		
		String json = parser.makeHttpRequest(urlData, "POST", nameValuePairs);
		
		Log.d("Insertion Message", json);
		String retour = "";
		
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray arr = obj.getJSONArray("message");
			int k =arr.length() - 1;
			retour = arr.getString(k);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.e("Retour ",retour);
		return retour;
	}
}
