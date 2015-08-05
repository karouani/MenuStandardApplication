package com.dolibarrmaroc.com.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;
import com.dolibarrmaroc.com.utils.UrlImage;

@SuppressLint("NewApi") public class CategorieDaoMysql implements CategorieDao {

	private JSONParser jsonParser;
	private static final String load = URL.URL+"seecategories2.php"; //categorieview.php seecategorie.php
	private static final String save = URL.URL+"createcmd.php";
	
	private Context context;
	
	
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
	
	
	@Override
	public List<Categorie> LoadCategories(Compte cp) {
		// TODO Auto-generated method stub
		List<Categorie> ls = new ArrayList<>();
		jsonParser = new JSONParser();
		boolean isit = false;
		
		List<Long> prod = new ArrayList<>();
		List<Long> child = new ArrayList<>();
		List<Produit> list = new ArrayList<Produit>();
		
		listPromoByProduits = new HashMap<>();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",cp.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",cp.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("imei",cp.getEmei()));
		
		
		try{
			String jsonString =  jsonParser.makeHttpRequest(load, "POST", nameValuePairs);
			
			//Log.e(">>> ",jsonString);
			
			if(jsonString != null && !jsonString.equals("")){
				String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
				JSONObject json = new JSONObject(stfomat);

				
				
				String data = json.getString("data");
				if(data.equals("ok")){
					JSONArray jr = json.getJSONArray("mescategories");
					
					
					for (int i = 0; i < jr.length(); i++) {
						
						
						JSONObject obj = jr.getJSONObject(i);
						
						prod = new ArrayList<>();
						child = new ArrayList<>();
						list = new ArrayList<Produit>();
						
						
						JSONArray p = obj.getJSONArray("products");
						
						//Log.e(obj.getString("label"),">> ");
						
						for (int j = 0; j < p.length(); j++) {
							JSONObject jsonn = p.getJSONObject(j);
							Produit produit = new Produit();
							
							produit.setId(jsonn.getInt("id"));
							produit.setDesig(jsonn.getString("desig"));
							produit.setPrixUnitaire(jsonn.getString("pu"));
							produit.setQteDispo(jsonn.getInt("stock"));
							produit.setRef(jsonn.getString("ref"));
							produit.setPrixttc(jsonn.getDouble("price_ttc"));
							produit.setFk_tva(jsonn.getString("fk_tva"));
							produit.setTva_tx(jsonn.getString("tva_tx"));
							produit.setPhoto(jsonn.getString("photo"));
							
							if(jsonn.getString("imgin").equals("ok")){
								
								String imageURL = UrlImage.urlimgproduit+produit.getId()+"_"+produit.getPhoto();
								//Log.e(">>> img",imageURL+"");
								Bitmap bitmap = null;
								try {
									// Download Image from URL
									InputStream input = new java.net.URL(imageURL).openStream();
									// Decode Bitmap
									bitmap = BitmapFactory.decodeStream(input);
									
									 File dir = new File(UrlImage.pathimg+"/produit_img");
									 if(!dir.exists())  dir.mkdirs();
									 
									     File file = new File(dir, "/"+produit.getId()+"_"+produit.getPhoto());
									     FileOutputStream fOut = new FileOutputStream(file);
									     
									     //Log.e(">>hotos ",produit.getPhoto());
									     
									     if(produit.getPhoto().split("\\.")[1].equals("jpeg") || produit.getPhoto().split("\\.")[1].equals("jpg") || produit.getPhoto().split("\\.")[1].equals("jpe")){
									    	  bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
									     }else{
									    	  bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
									     }
									   
									     fOut.flush();
									     fOut.close();
								} catch (Exception e) {
									e.printStackTrace();
									Log.e(">> ","pic out pd "+e.getMessage());
								}
								
							}
							list.add(produit);
							
							int nombre_promos = jsonn.getInt("nombre_promotion");
							HashMap<Integer, Promotion> map = new HashMap<>();
							
							if(nombre_promos>0){
								for (int j1 = 0; j1 < nombre_promos; j1++) {
									
									Promotion px = new Promotion(jsonn.getInt("id_promos"+j1), 
																Integer.parseInt(jsonn.getString("type_promos"+j1)), 
																Integer.parseInt(jsonn.getString("promos"+j1)), 
																Integer.parseInt(jsonn.getString("qte_promos"+j1)));
									map.put(px.getId(), px);
								}
							}
							else{
									Promotion px = new Promotion(0, 
																-1, 
																0, 
																0);
									map.put(px.getId(), px);
							
							}
							listPromoByProduits.put(jsonn.getInt("id"), map);
						}
						
						
						
						JSONArray c = obj.getJSONArray("childs");
						for (int j = 0; j < c.length(); j++) {
							child.add(c.getLong(j));
						}
						
						if(obj.getString("imgin").equals("ok")){
							
							String imageURL = UrlImage.urlimgcategorie+obj.getInt("id")+"_"+obj.getString("photo");
							
							Bitmap bitmap = null;
							try {
								// Download Image from URL
								InputStream input = new java.net.URL(imageURL).openStream();
								// Decode Bitmap
								bitmap = BitmapFactory.decodeStream(input);
								
								 File dir = new File(UrlImage.pathimg+"/categorie_img");
								 if(!dir.exists())  dir.mkdirs();
								 
								     File file = new File(dir, "/"+obj.getInt("id")+"_"+obj.getString("photo"));
								     FileOutputStream fOut = new FileOutputStream(file);
								   
								     
								     if(obj.getString("photo").split("\\.")[1].equals("jpeg") || obj.getString("photo").split("\\.")[1].equals("jpg")){
								    	  bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
								     }else{
								    	  bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
								     }
								   
								     fOut.flush();
								     fOut.close();
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(">> ","pic out "+e.getMessage());
							}
							
						}
						
						
						
						ls.add(new Categorie(obj.getInt("id"), obj.getInt("parent_id"), obj.getString("label"), obj.getString("description"), obj.getString("fullpath"), obj.getString("photo_vignette"), obj.getString("url"), obj.getString("photo"), list, child));
					}
					
					JSONArray jrnan = json.getJSONArray("productsnan");
					list = new ArrayList<Produit>();
					if(jrnan.length() > 0){
					for (int i = 0; i < jrnan.length(); i++) {
						JSONObject jsonn = jrnan.getJSONObject(i);
						Produit produit = new Produit();
						
						produit.setId(jsonn.getInt("id"));
						produit.setDesig(jsonn.getString("desig"));
						produit.setPrixUnitaire(jsonn.getString("pu"));
						produit.setQteDispo(jsonn.getInt("stock"));
						produit.setRef(jsonn.getString("ref"));
						produit.setPrixttc(jsonn.getDouble("price_ttc"));
						produit.setFk_tva(jsonn.getString("fk_tva"));
						produit.setTva_tx(jsonn.getString("tva_tx"));
						produit.setPhoto(jsonn.getString("photo"));
						
						if(jsonn.getString("imgin").equals("ok")){
							
							String imageURL = UrlImage.urlimgproduit+produit.getId()+"_"+produit.getPhoto();

							Bitmap bitmap = null;
							try {
								// Download Image from URL
								InputStream input = new java.net.URL(imageURL).openStream();
								// Decode Bitmap
								bitmap = BitmapFactory.decodeStream(input);
								
								 File dir = new File(UrlImage.pathimg+"/produit_img");
								 if(!dir.exists())  dir.mkdirs();
								 
								     File file = new File(dir, "/"+produit.getId()+"_"+produit.getPhoto());
								     FileOutputStream fOut = new FileOutputStream(file);
								     
								     //Log.e(">>hotos ",produit.getPhoto());
								     
								     if(produit.getPhoto().split("\\.")[1].equals("jpeg") || produit.getPhoto().split("\\.")[1].equals("jpg")){
								    	  bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
								     }else{
								    	  bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
								     }
								   
								     fOut.flush();
								     fOut.close();
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(">> ","pic out pd "+e.getMessage());
							}
							
						}
						list.add(produit);
						
						int nombre_promos = jsonn.getInt("nombre_promotion");
						HashMap<Integer, Promotion> map = new HashMap<>();
						
						if(nombre_promos>0){
							for (int j1 = 0; j1 < nombre_promos; j1++) {
								
								Promotion px = new Promotion(jsonn.getInt("id_promos"+j1), 
															Integer.parseInt(jsonn.getString("type_promos"+j1)), 
															Integer.parseInt(jsonn.getString("promos"+j1)), 
															Integer.parseInt(jsonn.getString("qte_promos"+j1)));
								map.put(px.getId(), px);
							}
						}
						else{
								Promotion px = new Promotion(0, 
															-1, 
															0, 
															0);
								map.put(px.getId(), px);
						
						}
						listPromoByProduits.put(jsonn.getInt("id"), map);
						}
					
					child.clear();
					
					
					ls.add(new Categorie(0, 0, "Produits non class�s", "Produits non class�s", "", "", "", "", list, child));
					}
					
					
					
				}
			}
			
			
		}catch(JSONException e){
			Log.e("log_tag", "Error laod categories data " + e.toString());
			ls = new ArrayList<>();
		}
		
		return ls;
	}


	public CategorieDaoMysql() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CategorieDaoMysql(Context ctx) {
		// TODO Auto-generated constructor stub
		this.context = ctx;
		try {
			File file = new File(UrlImage.pathimg+"/categorie_img");
			
			file.setWritable(true);
			file.setReadable(true);
			if(!file.exists()){
				Log.e("save cat","mkdirs "+file.mkdirs());
			}
			
			File file2 = new File(UrlImage.pathimg+"/produit_img");
			
			file2.setWritable(true);
			file2.setReadable(true);
			if(!file2.exists()){
				Log.e("save prod","mkdirs "+file2.mkdirs());
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("error ", "eroor images save "+e.getMessage());
		}
	}


	

}
