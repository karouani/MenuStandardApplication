package com.dolibarrmaroc.com;



import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TrackingActivity extends Activity implements OnClickListener{

	private EditText step,stop,level;
	private TextView imei;
	private Button btn;

	private String imeiText;
	private String stepText;
	private String stopText;
	private String levelText;
	
	//Objet 
	private Compte compte;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		try {
			step = (EditText) findViewById(R.id.step);
			stop = (EditText) findViewById(R.id.stop);
			imei =  (TextView) findViewById(R.id.imei);
			level =  (EditText) findViewById(R.id.level);
			btn =  (Button) findViewById(R.id.btn_enr_track);
			btn.setOnClickListener(this);
			//Getting the Object of TelephonyManager 
			TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			//Getting IMEI Number of Devide
			imei.setText(tManager.getDeviceId());

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				step.setText(compte.getStep());
				stop.setText(compte.getStop());
				level.setText(compte.getLevel());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_enr_track) {
			if("".equals(step.getText().toString()) || "".equals(stop.getText().toString())){
				Toast.makeText(this, "Tous les champs sont renseigner", Toast.LENGTH_LONG).show();
			}else{
				if("".equals(level.getText().toString()))  levelText = "0";
				
				stepText = step.getText().toString() ;
				stopText = stop.getText().toString();
				imeiText = imei.getText().toString();
				levelText = level.getText().toString();

				dialog = ProgressDialog.show(TrackingActivity.this, "Connexion",
						"Attendez SVP...", true);
				new ConnexionTask().execute();
			}

		}
	}
	class ConnexionTask extends AsyncTask<Void, Void, String> {

		JSONParser jsonParser = new JSONParser();
		private int erreur;
		@Override
		protected String doInBackground(Void... params) {
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", compte.getLogin()));
			nameValuePairs.add(new BasicNameValuePair("password", compte.getPassword()));
			nameValuePairs.add(new BasicNameValuePair("iduser", compte.getId()+""));
			nameValuePairs.add(new BasicNameValuePair("imei",imeiText));
			nameValuePairs.add(new BasicNameValuePair("step",stepText));
			nameValuePairs.add(new BasicNameValuePair("stop",stopText));
			nameValuePairs.add(new BasicNameValuePair("level",levelText));
			
			String url = URL.URL+"gpsconfig.php";

			String jsonString =  jsonParser.makeHttpRequest(
					url, "POST", nameValuePairs);

			Log.e("recuperation", jsonString);

			try{

				JSONObject json = new JSONObject(jsonString);
				if("yes".equals(json.getString("ok"))){
					Log.e("ajouter", "Bien Ajouter");
					erreur = 1;
					return "success";
				}else{
					Log.e("ohoy", "Error d'insertion");
					erreur = 0;
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

					if(erreur == 1){
						Toast.makeText(TrackingActivity.this, "Configuration avec success", Toast.LENGTH_LONG).show();
						stopService(new Intent(TrackingActivity.this,  ShowLocationActivity.class));
						
						Intent intentService = new Intent(TrackingActivity.this, ShowLocationActivity.class);
						intentService.putExtra("user", compte);
						startService(intentService);
						
						TrackingActivity.this.finish();
						
						Intent intent = new Intent(TrackingActivity.this, ConnexionActivity.class);
						intent.putExtra("user", compte);
						startService(intent);
					}else{
						Toast.makeText(TrackingActivity.this, "Essayer une autre fois", Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}

	}

}
