package com.dolibarrmaroc.com;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.AuthentificationManager;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.Services;
import com.dolibarrmaroc.com.utils.ConnexionManagerFactory;
import com.dolibarrmaroc.com.utils.DBHandler;
import com.dolibarrmaroc.com.utils.ForcerActivationGps;
import com.dolibarrmaroc.com.utils.MyLocationListener;
import com.dolibarrmaroc.com.utils.TinyDB;

@SuppressLint("NewApi")
public class ConnexionActivity extends Activity implements OnClickListener {

	//FOrcer Activation GPS
	private ForcerActivationGps forcer;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;

	private EditText login;
	private EditText password;
	private Button connexion;
	private Button test;

	private AuthentificationManager auth;

	private String log;
	private String pass;
	private Compte compte;
	private ConfigGps conf;

	private Services service;

	private String imei;

	private TinyDB db;
	private CheckBox souvenir;
	private boolean basedonne = false;
	private DBHandler mydb ;
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, getResources().getString(R.string.msg_retour), Toast.LENGTH_LONG).show();
		stopService(new Intent(this,ShowLocationActivity.class));
		this.finish();
		System.exit(0);
	}


	public ConnexionActivity() {
		auth = ConnexionManagerFactory.getCConnexionManager();
		compte  = new Compte();
		conf = new ConfigGps();
		service = new Services();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connexion);
        db = new TinyDB(this);
        db.clear();
        
		try {
			forcer = new ForcerActivationGps(this);

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}



			//Getting the Object of TelephonyManager 
			TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			//Getting IMEI Number of Devide
			imei=tManager.getDeviceId();
			
			mydb = new DBHandler(this);
			
			GregorianCalendar calendar = new GregorianCalendar();
			//TimeZone.getTimeZone("Africa/Casablanca")
			calendar.setTimeZone(TimeZone.getTimeZone("Africa/Casablanca"));

			calendar.add(Calendar.MONTH,1); 
			//System.out.println(calendar.getTimeInMillis()/1000);
			long timeStamp = calendar.getTimeInMillis()/1000;
			
			int valeur = mydb.numberOfRows();
			Log.d("Valeur >> ",valeur+"");
			
			if(valeur > 0){
				compte = mydb.getConnexion(imei, timeStamp);
				Log.d("Compte Valeur > 0 ",compte.toString());
				if(compte != null){
					basedonne = true;
					
					/*
					dialog = ProgressDialog.show(ConnexionActivity.this, "Connexion",
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
					*/
					Intent intent1 = new Intent(ConnexionActivity.this, HomeActivity.class);
					intent1.putExtra("user", compte);
					startActivity(intent1);
				}else{
					Log.d("Compte null ",compte.toString());

					mydb.deleteUser(imei);
					basedonne = false;
					login = (EditText) findViewById(R.id.login);
					password = (EditText) findViewById(R.id.password);
					souvenir = (CheckBox) findViewById(R.id.souvenir);

					connexion = (Button) findViewById(R.id.connecter);
					connexion.setOnClickListener(this);
				}


			}else{

				Log.d("Compte Valeur == 0 ",compte.toString());

				basedonne = false;

				login = (EditText) findViewById(R.id.login);
				password = (EditText) findViewById(R.id.password);
				souvenir = (CheckBox) findViewById(R.id.souvenir);

				connexion = (Button) findViewById(R.id.connecter);
				connexion.setOnClickListener(this);
			}

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				login.setText(compte.getLogin());
				password.setText(compte.getPassword());
			}

			getGpsApplication();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public boolean isNetworkConnected(Context context) {

		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()){
			boolean t = isOnline();
			if(t) return true;
			else {
				return false;
			}
		}else{
			return false;
		}
	}


	private void createGpsDisabledAlert() {

		forcer.turnGPSOn();


		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder
		.setMessage(getResources().getString(R.string.msg_gps_desactive))
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.btn_gps_activer),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				ConnexionActivity.this.showGpsOptions();
				forcer.turnGPSOn();
			}
		}
				);
		localBuilder.setNegativeButton(getResources().getString(R.string.btn_gps_deactiver),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.cancel();
				ConnexionActivity.this.finish();
			}
		}
				);
		localBuilder.create().show();

	}

	private void erreurNetwork(){
		AlertDialog.Builder local = new AlertDialog.Builder(this);

		local
		.setTitle(getResources().getString(R.string.msg_network))
		.setMessage(getResources().getString(R.string.msg_network_alert))
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.btn_cancel),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				ConnexionActivity.this.finish();
			}
		}
				);
		local.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				return;
			}
		}
				);
		local.create().show();
	}

	private void compteDesactiver(){
		AlertDialog.Builder local = new AlertDialog.Builder(this);

		local
		.setTitle(getResources().getString(R.string.msg_device_unsupported))
		.setMessage(getResources().getString(R.string.msg_deviced))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.btn_cancel),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						return;
					}
				}
						);
		local.setNegativeButton(getResources().getString(R.string.appel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:00212522400587"));
				startActivity(intent);
				return;
			}
		}
				);

		local.setNeutralButton("Email",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String[] TO = {"ykarouani@gmail.com"/*"contact@geocom.ma"*/};
				String[] CC = {"ykarouani@gmail.com"/*"contact@marocgeo.ma"*/};
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setData(Uri.parse("mailto:"));
				emailIntent.setType("text/plain");


				emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
				emailIntent.putExtra(Intent.EXTRA_CC, CC);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_email));
				emailIntent.putExtra(Intent.EXTRA_TEXT, "");

				try {
					startActivity(Intent.createChooser(emailIntent, "Envoyer Email..."));
					finish();
					Log.i("Finished sending email...", "");
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(ConnexionActivity.this, 
							getResources().getString(R.string.no_email), Toast.LENGTH_SHORT).show();
				}
			}
		});
		local.create().show();
	}

	private void showGpsOptions() {
		startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
	}

	public void getGpsApplication(){

		LocationManager mlocManager=null;
		android.location.LocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

		if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			createGpsDisabledAlert();
		}

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.connecter){

			log = login.getText().toString();
			pass = password.getText().toString();

			//stopService(new Intent(ConnexionActivity.this,  ShowLocationActivity.class));

			if("".equals(log) || "".equals(pass)){

				Toast toast = Toast.makeText(ConnexionActivity.this, getResources().getString(R.string.msg_fields), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}else{
				if(isNetworkConnected(this)){
					getGpsApplication();
					//Log.e("Compte",compte.toString());
					
					dialog = ProgressDialog.show(ConnexionActivity.this, "Connexion",
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
				}else{
					erreurNetwork();
				}
			}

		}
	}

	//---you need this to prevent the webview from
	// launching another browser when a url
	// redirection occurs---

	private class Callback extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(
				WebView view, String url) {
			return(false);
		}
	}

	class ConnexionTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				compte = auth.login(log+"/karouaniYassine/"+imei, pass);
				conf = auth.getGpsConfig();
			} catch (Exception e) {
				// TODO: handle exception
			}
				
			
				/*
				if("technicien".equals(compte.getProfile())){
					service = auth.getService(log, pass);
				}
				*/
			
			return null;
		}

		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					
					Log.e("Compte connected",compte.toString());
					
					switch (compte.getActiver()) {
					case 1:
					case 2:
					case 0:
						Toast toast = Toast.makeText(ConnexionActivity.this,compte.getMessage(), Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						break;
					case 3:
						Intent intent = new Intent(ConnexionActivity.this, TrackingActivity.class);
						intent.putExtra("user", compte);
						startActivity(intent);
						break;
					case 8:
						compteDesactiver();
						break;
					case 4 :
					default: 
						String act = compte.getProfile();
						act  = act.toLowerCase();

						Log.i("Voila Profile",act);

						Intent intentService = new Intent(ConnexionActivity.this, ShowLocationActivity.class);
						intentService.putExtra("user", compte);
						startService(intentService);
						
						if (db.getList("login") != null) {
							db.remove("login");
						}
						
						GregorianCalendar calendar = new GregorianCalendar();
						//TimeZone.getTimeZone("Africa/Casablanca")
						calendar.setTimeZone(TimeZone.getTimeZone("Africa/Casablanca"));


						long timeStamp = calendar.getTimeInMillis()/1000;

						if (souvenir.isChecked()) {
							mydb.insertUser(log, pass, timeStamp, 1, imei, act , compte.getId());
						}
						ArrayList<String> auton = new ArrayList<>();
						auton.add(log);
						auton.add(pass);
						db.putList("login", auton);
						
						
						Intent intent1 = new Intent(ConnexionActivity.this, HomeActivity.class);
						intent1.putExtra("user", compte);
						startActivity(intent1);
						
						break;
					}

				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        try {
	            URL url = new URL("http://www.google.com");
	            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	            urlc.setConnectTimeout(3000);
	            urlc.connect();
	            if (urlc.getResponseCode() == 200 || urlc.getResponseCode() == 302) {
	                return new Boolean(true);
	            }
	        } catch (MalformedURLException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    return false;
	}
}


