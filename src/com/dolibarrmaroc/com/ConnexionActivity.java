package com.dolibarrmaroc.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.AuthentificationManager;
import com.dolibarrmaroc.com.database.DBHandler;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.Services;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.ConnexionManagerFactory;
import com.dolibarrmaroc.com.utils.ForcerActivationGps;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.MyLocationListener;
import com.dolibarrmaroc.com.utils.TinyDB;
import com.dolibarrmaroc.com.utils.UpdateApp;

@SuppressLint("NewApi")
public class ConnexionActivity extends Activity implements OnClickListener {

	private ProgressDialog mProgressDialog;
	//FOrcer Activation GPS
	private ForcerActivationGps forcer;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;

	private EditText login;
	private EditText password;
	private Button connexion;
	private Button test;

	private Offlineimpl myoffline;

	private AuthentificationManager auth;
  
	private String log;
	private String pass;
	private Compte compte;
	private ConfigGps conf;

	private Services service;

	private JSONParser json;
	private UpdateApp atualizaApp;
	private String ExistingVersionName;

	private String dest_file_path;
	private int downloadedSize = 0, totalsize;
	private String download_file_url;

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

		Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
		cl.add(Calendar.DAY_OF_MONTH, 30);
		Log.e(">>> new date ",cl.get(Calendar.YEAR)+"/"+(cl.get(Calendar.MONTH)+1)+"/"+cl.get(Calendar.DAY_OF_MONTH));
		
		
		try {
			forcer = new ForcerActivationGps(this);

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			login = (EditText) findViewById(R.id.login);
			password = (EditText) findViewById(R.id.password);
			souvenir = (CheckBox) findViewById(R.id.souvenir);

			connexion = (Button) findViewById(R.id.connecter);
			connexion.setOnClickListener(this);

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
			Log.e("Valeur >> ",valeur+"");


			myoffline = new Offlineimpl(ConnexionActivity.this);
			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
				Log.e("here out ","check folder exsite");
			}

			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				/*
				dialogupdate =  ProgressDialog.show(ConnexionActivity.this,getResources().getString(R.string.tecv39),
						getResources().getString(R.string.msg_wait), true);
				 */
				/*
				mProgressDialog = new ProgressDialog(ConnexionActivity.this);
				mProgressDialog.setMessage("Recherche d\'une mise à jour disponible");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setMax(100);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				 */

				if(myoffline.checkForUpdate() == 1){
					new UpdateTask().execute();
				}

				//new UpdateTask().execute();
			}

			if(valeur > 0){
				Log.e("Compte Valeur > 0 ",imei+" "+timeStamp);
				compte = mydb.getConnexion(imei, timeStamp);
				
				if(compte != null){
					basedonne = true;
					
					compte = myoffline.LoadCompte(compte.getLogin(), compte.getPassword());  

					/*
					dialog = ProgressDialog.show(ConnexionActivity.this, "Connexion",
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
					 */
					
					Intent intent1 = new Intent(ConnexionActivity.this, HomeActivity.class);
					intent1.putExtra("user", compte);
					startActivity(intent1);
					
				}else{

					mydb.deleteUser(imei);
					basedonne = false;
					
				}


			}else{

				Log.d("Compte Valeur == 0 ",compte.toString());

				basedonne = false;

				
			}

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				if(compte != null){
					login.setText(compte.getLogin());
					password.setText(compte.getPassword());
				}
				
			}



			myoffline.TotalMemory();
			myoffline.FreeMemory();

			getGpsApplication();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

			Log.e("in click ","hello");
			log = login.getText().toString();
			pass = password.getText().toString();

			//stopService(new Intent(ConnexionActivity.this,  ShowLocationActivity.class));

			if("".equals(log) || "".equals(pass)){

				Toast toast = Toast.makeText(ConnexionActivity.this, getResources().getString(R.string.msg_fields), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}else{
				if(CheckOutNet.isNetworkConnected(this)){
					getGpsApplication();
					//Log.e("Compte",compte.toString());

					dialog = ProgressDialog.show(ConnexionActivity.this, getResources().getString(R.string.connexion),
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
				}else{
					//erreurNetwork();

					dialog = ProgressDialog.show(ConnexionActivity.this,  getResources().getString(R.string.connexion),
							getResources().getString(R.string.msg_wait), true);
					new OfflineTask().execute();
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


				mydb = new DBHandler(ConnexionActivity.this);

				myoffline = new Offlineimpl(ConnexionActivity.this);

				if(!myoffline.checkFolderexsiste()){
					showmessageOffline();
				}else if(compte != null){
					myoffline.shynchronizeCompte(compte);
					myoffline.shynchronizeSociete(auth.lodSociete(""));


					if("technicien".equals(compte.getProfile())){
						service = auth.getService(log, pass);

						List<Services> ls = new ArrayList<>();
						ls.add(service);
						myoffline.shynchronizeService(ls);
					}
				}

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

					if(compte != null){
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
							ConnexionActivity.this.finish();
							break;
						}
					}else{
						Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.fatal_error),
								Toast.LENGTH_LONG).show();
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

	
	class OfflineTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				Log.e("in offline cpt","star");
				mydb = new DBHandler(ConnexionActivity.this);

				
				if(!myoffline.checkFolderexsiste()){
					showmessageOffline();
				}else{
					compte = myoffline.LoadCompte(login.getText().toString(), password.getText().toString());
					conf = myoffline.getGpsTracker();
					
					
					if("technicien".equals(compte.getProfile())){
						service = myoffline.LoadServices("");
					}	
				}
				
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("in offline",e.getMessage()  +" << ");
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
					
					//Log.e("Compte connected",compte.toString());
					if(compte != null){
						
						
						
						switch (compte.getActiver()) {
						case 1:
						case 2:
						case 0:
							Toast toast = Toast.makeText(ConnexionActivity.this,compte.getMessage()  +" << ", Toast.LENGTH_SHORT);
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
							ConnexionActivity.this.finish();
							break;
						}
					}else{
						
						//pas de fichier de configuration
						Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.fatal_error),
								Toast.LENGTH_LONG).show();
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
	

	public void showmessageOffline(){
		try {

			LayoutInflater inflater = this.getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.msgstorage, null);

			AlertDialog.Builder dialog =  new AlertDialog.Builder(ConnexionActivity.this);
			dialog.setView(dialogView);
			dialog.setTitle(R.string.caus1);
			dialog.setPositiveButton(R.string.caus8, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					dialog.cancel();
				}
			});
			dialog.setCancelable(true);
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	class UpdateTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(ConnexionActivity.this);
			// Set your ProgressBar Title 
			mProgressDialog.setTitle(getResources().getString(R.string.cnxlab1));
			//mProgressDialog.setIcon(R.drawable.ic_launcher);
			// Set your ProgressBar Message
			mProgressDialog.setMessage(getResources().getString(R.string.cnxlab2));
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// Show ProgressBar
			mProgressDialog.setCancelable(false);
			//  mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... paramsw) {

			try {

				//Getting the Object of TelephonyManager 
				TelephonyManager tManager2 = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				//Getting IMEI Number of Devide
				imei=tManager2.getDeviceId();

				atualizaApp = new UpdateApp();
				atualizaApp.setContext(getApplicationContext());

				json = new JSONParser();

				try {
					ExistingVersionName = getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0 ).versionName;
				} catch (NameNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


				// TODO Auto-generated method stub
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("imei", imei));
				params.add(new BasicNameValuePair("date", new Date().toGMTString()));
				params.add(new BasicNameValuePair("type", CheckOutNet.type));
				params.add(new BasicNameValuePair("version", ExistingVersionName));

				String jsonString = json.makeHttpRequest(CheckOutNet.url_update, "POST", params);
				Log.e("Json",jsonString);

				try {
					JSONObject obj = new JSONObject(jsonString);
					String update = obj.getString("update");
					String version = obj.getString("version");

					//Log.e("Version",ExistingVersionName);
					if("yes".equals(update)){


						if(!ExistingVersionName.equals(version)){
							String apk = obj.getString("apk");

							if(!apk.contains("http://")) apk = "http://"+apk;

							download_file_url = apk;
							dest_file_path = obj.getString("name")+".apk";

							//downloadAndOpenPDF(download_file_url,dest_file_path);

							String apkurl = download_file_url;
							String outputFileName = dest_file_path;
							try {
								URL url = new URL(apkurl);
								HttpURLConnection c = (HttpURLConnection) url.openConnection();
								c.setRequestMethod("GET");
								c.setDoOutput(true);
								c.connect();

								int fileLength = c.getContentLength();

								String PATH = Environment.getExternalStorageDirectory()
										+ "/apk/";
								File file = new File(PATH);
								file.mkdirs();
								File outputFile = new File(file, outputFileName);
								FileOutputStream fos = new FileOutputStream(outputFile);

								InputStream is = c.getInputStream();

								byte[] buffer = new byte[1024];
								int len1 = 0;
								long total = 0;

								while ((len1 = is.read(buffer)) != -1) {
									fos.write(buffer, 0, len1);
									total += len1;
									// Publish the progress
									publishProgress((int) (total * 100 / fileLength));
								}
								fos.close();
								is.close();// .apk is download to sdcard in download file

								// install the .apk
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(new File(Environment
										.getExternalStorageDirectory()
										+ "/apk/"
										+ outputFileName)),
										"application/vnd.android.package-archive");
								ConnexionActivity.this.startActivity(intent);
							} catch (IOException e) {
								Toast.makeText(ConnexionActivity.this.getApplicationContext(), "Update error!",
										Toast.LENGTH_LONG).show();
							}

						}else{
							// TODO Auto-generated method stub
							List<NameValuePair> params2 = new ArrayList<NameValuePair>();
							params2.add(new BasicNameValuePair("imei", imei));
							params2.add(new BasicNameValuePair("date", new Date().toGMTString()));
							params2.add(new BasicNameValuePair("type", CheckOutNet.type));
							params2.add(new BasicNameValuePair("version", ExistingVersionName));
							params2.add(new BasicNameValuePair("update", "yes"));

							json.makeHttpRequest(CheckOutNet.url_update, "POST", params2);
						}
					}else{
						// TODO Auto-generated method stub
						List<NameValuePair> params2 = new ArrayList<NameValuePair>();
						params2.add(new BasicNameValuePair("imei", imei));
						params2.add(new BasicNameValuePair("date", new Date().toGMTString()));
						params2.add(new BasicNameValuePair("type", CheckOutNet.type));
						params2.add(new BasicNameValuePair("version", ExistingVersionName));
						params2.add(new BasicNameValuePair("update", "yes"));

						json.makeHttpRequest(CheckOutNet.url_update, "POST", params2);
						if(mProgressDialog.isShowing()){
							mProgressDialog.dismiss();
						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			} catch (Exception e) {
				// TODO: handle exception
				Log.e("erreu synchro",e.getMessage()  +" << ");
			}

			return null;
		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				/*
				if(dialogupdate.isShowing()){
					dialogupdate.dismiss();
				}
				 */
				//dialogupdate.dismiss();
				mProgressDialog.dismiss();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}


		protected void onProgressUpdate(Integer... progress) {
			// TODO Auto-generated method stub
			mProgressDialog.setProgress(progress[0]);
		}

	}

	public void alertmaps(){


		AlertDialog.Builder alert = new AlertDialog.Builder(ConnexionActivity.this);
		alert.setTitle(getResources().getString(R.string.caus25));
		alert.setMessage(getResources().getString(R.string.caus18));
		alert.setNegativeButton("Bien compris", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}

	public void alertmaps2(){

		String ExistingVersionName2="";
		try {
			ExistingVersionName2 = "\n [ + "+getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0 ).versionName+"]";
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(ConnexionActivity.this);
		alert.setTitle(getResources().getString(R.string.caus25)+ExistingVersionName2);
		alert.setMessage(getResources().getString(R.string.caus24));
		alert.setNegativeButton("Lancer la recherche", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new UpdateTask().execute();
				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
	
	public void onClickHome(View v){
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			alertmaps2();
		}else{
			alertmaps();
		}
	}

}


