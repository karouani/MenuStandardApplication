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

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.Facture;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class VendeurDaoMysql implements VendeurDao {

	private JSONParser jsonParser;
	private String urlprd = URL.URL+"produit.php";
	private String urlclt = URL.URL+"listclient.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ID = "id";

	private String jsonprd,jsonclt;
	private Dictionnaire dicot = new Dictionnaire();
	
	/*
	 * Integer => id Produit
	 * Integer => id Promotion
	 */
	private HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits;
	
	/*
	 * Integer => id Client
	 * Integer => id Promot
	 */
	private HashMap<Integer, List<Integer>> listPromoByClient;
	
	public VendeurDaoMysql() {
		jsonParser = new JSONParser();
		listPromoByProduits = new HashMap<>();
		listPromoByClient = new HashMap<>();
	}

	@Override
	public int insertFacture(Facture fac) {
		return 0;
	}
	
	public HashMap<Integer, HashMap<Integer, Promotion>> getListPromoByProduits() {
		return listPromoByProduits;
	}

	public void setListPromoByProduits(
			HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits) {
		this.listPromoByProduits = listPromoByProduits;
	}

	public HashMap<Integer, List<Integer>> getListPromoByClient() {
		return listPromoByClient;
	}

	public void setListPromoByClient(
			HashMap<Integer, List<Integer>> listPromoByClient) {
		this.listPromoByClient = listPromoByClient;
	}
	
	@Override
	public List<Produit> selectAllProduct(Compte c) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		
		String jsonString =  jsonParser.makeHttpRequest(
				urlprd , "POST", nameValuePairs);
		// Parse les donn�es JSON
		
		List<Produit> list = new ArrayList<Produit>();
		
		ArrayList<HashMap<String, String>> mapsss = new ArrayList<>();
		
		listPromoByProduits = new HashMap<>();
		
		Log.d("Json retourne >> ", jsonString);
		try{

			JSONArray jArray = new JSONArray(jsonString);
			// check your log for json response
			//Log.d("Dictionnaire", jArray.);
			
			//[{"id":"1","ref":"c00001","desig":"Produit1","stock":"100","pu":"100,00"},
			for(int i=0;i<jArray.length();i++){
				if(i == 0){
					JSONArray dico = jArray.getJSONArray(i);
					for (int j = 0; j < dico.length(); j++) {
						JSONObject jsone = dico.getJSONObject(j);
						HashMap<String, String> dic = new HashMap<>();
						dic.put(jsone.getString("code"), jsone.getString("libelle"));
						mapsss.add(dic);
					}
				}else{
					JSONObject json = jArray.getJSONObject(i);
					Produit produit = new Produit();
					//Log.e(">>> Produit trouver Successful!", json.toString());
					
					/*
					String pu = json.getString("pu");
					String[] parts = pu.split(".");
					
					String mni = parts[1].substring(0, 1);
					
					String part = parts[1]+"."+ mni;
					for (int j = 0; j < parts.length; j++) {
						part += Integer.parseInt(parts[j]);
					}
					*/
					
					produit.setId(json.getInt("id"));
					produit.setDesig(json.getString("desig"));
					produit.setPrixUnitaire(json.getString("pu"));
					produit.setQteDispo(json.getInt("stock"));
					produit.setRef(json.getString("ref"));
					produit.setPrixttc(json.getDouble("price_ttc"));
					produit.setFk_tva(json.getString("fk_tva"));
					produit.setTva_tx(json.getString("tva_tx"));
					list.add(produit);
					
					int nombre_promos = json.getInt("nombre_promotion");
					HashMap<Integer, Promotion> map = new HashMap<>();
					
					if(nombre_promos>0){
						for (int j = 0; j < nombre_promos; j++) {
							
							Promotion p = new Promotion(json.getInt("id_promos"+j), 
														Integer.parseInt(json.getString("type_promos"+j)), 
														Integer.parseInt(json.getString("promos"+j)), 
														Integer.parseInt(json.getString("qte_promos"+j)));
							map.put(p.getId(), p);
						}
					}
					else{
							Promotion p = new Promotion(0, 
														-1, 
														0, 
														0);
							map.put(p.getId(), p);
					
					}
					listPromoByProduits.put(json.getInt("id"), map);
				}
				
			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		
		dicot.setDico(mapsss);
		//Log.i("Dictionnaire >> ",dicot.toString());
		
		return list;
	}
	
	public Produit selectProduct(String id,Compte c) {
		List<Produit> list = selectAllProduct(c);
		
		int k = Integer.parseInt(id);
		
		Produit produit = new Produit();
		
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getId() == k){
				produit = list.get(i);
			}else{
				produit = null;
			}
		}
		return produit;
	}

	@Override
	public List<Client> selectAllClient(Compte c) {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		
		String jsonString =  jsonParser.makeHttpRequest(
				urlclt, "POST", nameValuePairs);
		// Parse les donn�es JSON
		
		List<Client> list = new ArrayList<Client>();
		
		Log.d("Json retourne >> ", jsonString);
		try{

			JSONArray jArray = new JSONArray(jsonString);
			// check your log for json response
			//Log.d("Login attempt", jArray.toString());
			
			
			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);
				Client clt = new Client();
				
				//"rowid":"3","name":"karouani","client":"1","zip":"54020","town":null,
				//"stcomm":"Jamais contact\u00e9","prefix_comm":null,"code_client":"14589"
				
				clt.setId(json.getInt("rowid"));
				clt.setName(json.getString("name"));
				clt.setZip(json.getString("zip"));
				clt.setTown(json.getString("town"));
				clt.setLatitude(Double.parseDouble(json.getString("latitude")));
				clt.setLongitude(Double.parseDouble(json.getString("longitude")));
				if(!c.getProfile().toLowerCase().equals("technicien")){
					clt.setEmail(json.getString("email"));
				}
				
				
				int nombre_promos = json.getInt("nombre_promotion");
				List<Integer> listP = new ArrayList<>();
				
				if(nombre_promos>0){
					for (int j = 0; j < nombre_promos; j++) {
						int idp = Integer.parseInt(json.getString("id_promos"+j));
						listP.add(idp);
					}
				}
				
				listPromoByClient.put(json.getInt("rowid"), listP);
				list.add(clt);
				
			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return list;
	}

	@Override
	public Dictionnaire getDictionnaire() {
		return dicot;
	}

	@Override
	public List<Promotion> getPromotions(int idclt, int idprd) {
		List<Integer> list = this.getListPromoByClient().get(idclt);
		List<Promotion> lista = new ArrayList<>();
		HashMap<Integer, Promotion> map = this.getListPromoByProduits().get(idprd);
		
		for (int i = 0; i < list.size(); i++) {
			if(map.containsKey(list.get(i))){
				lista.add(map.get(list.get(i)));
			}
		}
		if(lista.size() == 0) lista.add(new Promotion(0, -1, 1, 0));
		
		return lista;
	}

	@Override
	public HashMap<Integer, HashMap<Integer, Promotion>> getPromotionProduits() {
		// TODO Auto-generated method stub
		return this.listPromoByProduits;
	}

	@Override
	public HashMap<Integer, List<Integer>> getPromotionClients() {
		// TODO Auto-generated method stub
		return this.listPromoByClient;
	}

	
}
