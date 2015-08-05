package com.dolibarrmaroc.com;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.utils.JSONParser;

public class GpsTrackerActivity extends Activity implements OnClickListener{

	//Objet 
	private Compte compte;

	private EditText serveur,bd,pass,table,lat,lng,login;
	private Button ok;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_tracker);

		try {
			serveur = (EditText) findViewById(R.id.serveurbd);
			bd = (EditText) findViewById(R.id.namebd);
			login = (EditText) findViewById(R.id.loginbd);
			pass = (EditText) findViewById(R.id.passbd);
			table = (EditText) findViewById(R.id.tablebd);
			lat = (EditText) findViewById(R.id.latfieldbd);
			lng = (EditText) findViewById(R.id.lngfieldbd);

			ok = (Button) findViewById(R.id.pointerClt);
			ok.setOnClickListener(this);

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pointerClt) {
			dialog = ProgressDialog.show(GpsTrackerActivity.this, "Connexion",
					"Attendez SVP...", true);
			new ConnexionTask().execute();
		}
	}

	class ConnexionTask extends AsyncTask<Void, Void, String> {

		JSONParser jsonParser = new JSONParser();
		@Override
		protected String doInBackground(Void... params) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", compte.getLogin()));
			nameValuePairs.add(new BasicNameValuePair("password", compte.getPassword()));
			nameValuePairs.add(new BasicNameValuePair("user", compte.getId()+""));
			nameValuePairs.add(new BasicNameValuePair("serveur",serveur.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("bd",bd.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("login",login.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("pass",pass.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("table",table.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("lat",lat.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("lng",lng.getText().toString()));

			String url = "http://41.142.241.192/crm1/htdocs/android/gps.php";

			String jsonString =  jsonParser.makeHttpRequest(
					url, "POST", nameValuePairs);

			Log.e("recuperation", jsonString);
			
			try{

				JSONObject json = new JSONObject(jsonString);
				if("yes".equals(json.getString("ok"))){
					Log.e("ajouter", "Bien Ajouter");
					return "success";
				}else{
					Log.e("ohoy", "Error d'insertion");
					return "error";
				}
			}catch(Exception e){
				Log.e("log_tag", "Error in http connection " + e.toString());
				return "0";
			}


		}

		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();

					//startService(GpsTrackerActivity.this,MyService.class);
					startService(new Intent(GpsTrackerActivity.this,MyService.class));
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}
}
