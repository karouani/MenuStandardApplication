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
import com.dolibarrmaroc.com.models.LoadStock;
import com.dolibarrmaroc.com.models.Mouvement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class MouvementDaoMysql implements MouvementDao {

	private JSONParser jsonParser;
	private static final String mouve = URL.URL+"transfertstock.php";
	private static final String mouve_echange = URL.URL+"echangeproduits.php";
	
	@Override
	public LoadStock laodStock(Compte cp) {
		// TODO Auto-generated method stub
		LoadStock sm =null;
		jsonParser = new JSONParser();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",cp.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",cp.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("imei",cp.getEmei()));
		nameValuePairs.add(new BasicNameValuePair("loadstock","1"));
		
		
		try{
			String jsonString =  jsonParser.makeHttpRequest(mouve, "POST", nameValuePairs);
			
			String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			
			Log.e("stwtc",stfomat);
			JSONObject json = new JSONObject(stfomat);

			
			/* load compte */
			Compte c = new Compte();
			c.setIduser(json.getString("iduser"));
			c.setProfile(json.getString("profile"));
			c.setId(json.getInt("id"));
			
			/* load produits */
			List<Produit> lsprod = new ArrayList<>();
			
			JSONArray ja = json.getJSONArray("myprod");
			
			for (int i = 0; i < ja.length(); i++) {
				JSONObject ob = ja.getJSONObject(i);
				
				Produit p  = new Produit(ob.getString("ref"), ob.getString("desig"), ob.getInt("stock"), "",0, ob.getDouble("price_ttc"), "", "");
				p.setId(ob.getInt("id"));
				lsprod.add(p);
			}
			
			/* load caracteristique */
			long sw = json.getLong("sastock");
			long vw = json.getLong("vstock");
			
			String swn = json.getString("sw_name");
			String vwn = json.getString("vw_name");
			
			sm = new LoadStock(c, lsprod, json.getLong("sastock"), json.getLong("vstock"));
			sm.setSw(sw);
			sm.setDw(vw);
			sm.setSname(swn);
			sm.setVname(vwn);
			sm.setName_vend(json.getString("vendeurname"));
			
		}catch(JSONException e){
			Log.e("log_tag", "Error laod stock data " + e.toString());
			sm = null;
		}

		return sm;
	}

	@Override
	public String makemouvement(List<Mouvement> mvs, Compte cp, String label) {
		// TODO Auto-generated method stub
		String in ="-1";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",cp.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",cp.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("label",label));
		//Log.e("PRDS ",idclt);
		nameValuePairs.add(new BasicNameValuePair("nbrmv",mvs.size()+""));
		
		nameValuePairs.add(new BasicNameValuePair("movementstock", "ok"));
		
		
		
		
		for (int i = 0; i < mvs.size(); i++) {
			Mouvement m = mvs.get(i);
			nameValuePairs.add(new BasicNameValuePair("prod"+i,m.getRef()+""));
			nameValuePairs.add(new BasicNameValuePair("qty"+i,m.getQty()+""));
			nameValuePairs.add(new BasicNameValuePair("dw"+i,m.getSw()));
			nameValuePairs.add(new BasicNameValuePair("sw"+i,m.getDw()));
		}
		
		
		jsonParser = new JSONParser();
		Log.e("mouvements",nameValuePairs.toString());
		
		String jsonString =  "";
		
		
		try {
			
			jsonString = jsonParser.makeHttpRequest(mouve , "POST", nameValuePairs);

			Log.e("JsonString mouvemen responce", jsonString);
			
			String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			
			JSONObject json = new JSONObject(stfomat);
			
			
			in = json.getString("action");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("error ",e.getMessage()+" << ");
			in ="-1";
			return in;
		}
		return in;
	}

	@Override
	public String makeechange(List<Mouvement> mvs, Compte cp, String label,String clt) {
		// TODO Auto-generated method stub
		String in ="-1";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",cp.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",cp.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("label",label));
		//Log.e("PRDS ",idclt);
		nameValuePairs.add(new BasicNameValuePair("nbrmv",mvs.size()+""));
		
		nameValuePairs.add(new BasicNameValuePair("iduser",cp.getId()+""));
		nameValuePairs.add(new BasicNameValuePair("clt",clt+""));
		
		
		
		
		for (int i = 0; i < mvs.size(); i++) {
			Mouvement m = mvs.get(i);
			nameValuePairs.add(new BasicNameValuePair("prod"+i,m.getRef()+""));
			nameValuePairs.add(new BasicNameValuePair("qty"+i,m.getQty()+""));
			nameValuePairs.add(new BasicNameValuePair("dw"+i,m.getSw()));
			nameValuePairs.add(new BasicNameValuePair("sw"+i,m.getDw()));
		}
		
		
		jsonParser = new JSONParser();
		Log.e("mouvements",nameValuePairs.toString());
		
		String jsonString =  "";
		
		
		try {
			
			jsonString = jsonParser.makeHttpRequest(mouve_echange , "POST", nameValuePairs);

			Log.e("JsonString mouvemen responce", jsonString);
			
			String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			
			JSONObject json = new JSONObject(stfomat);
			
			
			in = json.getString("action");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("error ",e.getMessage()+" << ");
			in ="-1";
			return in;
		}
		return in;
	}

}
