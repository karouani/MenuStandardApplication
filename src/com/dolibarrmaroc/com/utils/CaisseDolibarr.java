package com.dolibarrmaroc.com.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;

public class CaisseDolibarr extends AsyncTask<Void, Void, String> {

	private Compte compte;
	private Client client;
	private Produit produit;
	private String total;
	private Context context;
	private int etat;
	
	private JSONParser json;
	private ProgressDialog  dialog2;
	
	public CaisseDolibarr() {
		super();
		// TODO Auto-generated constructor stub
		dialog2 = ProgressDialog.show(this.context, "Enregistrement des donn�es Donn�es",
				"Attendez SVP...", true);
	}

	public CaisseDolibarr(Compte compte, Client client, Produit produit,
			String total, Context context) {
		super();
		this.compte = compte;
		this.client = client;
		this.produit = produit;
		this.total = total;
		this.context = context;
		
		json = new JSONParser();
		
	}

	@Override
	protected String doInBackground(Void... params) {
		/*
		 * Coter Caisse Connexion
		 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("txtUsername", compte.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("pwdPassword", compte.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("socid", client.getId()+""));
		nameValuePairs.add(new BasicNameValuePair("CASHDESK_ID_BANKACCOUNT_CASH", "1"));

		String url = "http://stratitge.marocgeo.com/cashdesk/index_verif.php";

		json.makeHttpRequest(url, "POST", nameValuePairs);

		/*
		 * Coter Produit choisi
		 */
		String url2 = URL.URL+"cashdesk/facturation_verif.php";
		ArrayList<NameValuePair> nameValue = new ArrayList<NameValuePair>();
		nameValue.add(new BasicNameValuePair("txtRef", produit.getRef()));
		nameValue.add(new BasicNameValuePair("selProduit", produit.getId()+""));

		json.makeHttpRequest(url2, "POST", nameValue);

		/*
		 * Coter ajouter des produits
		 */
		ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("txtQte", produit.getQtedemander()+""));
		nameValuePair.add(new BasicNameValuePair("txtPrixUnit", produit.getPrixUnitaire()));
		nameValuePair.add(new BasicNameValuePair("txtTotal", total));
		nameValuePair.add(new BasicNameValuePair("selTva", "121"));

		String urlFinal = URL.URL+"cashdesk/facturation_verif.php?action=ajout_article";



		json.makeHttpRequest( urlFinal , "POST", nameValuePair);
		
		this.setEtat(1); 
		return "success";
	}

	@Override
	protected void onPostExecute(String sResponse) {
		try {
			if (dialog2.isShowing()){
				dialog2.dismiss();
			
			}

		} catch (Exception e) {
			Toast.makeText(this.context,
					e.getMessage(),
					Toast.LENGTH_LONG).show();
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}

}
