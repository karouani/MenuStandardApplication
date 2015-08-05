package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.TechnicienManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.models.BordreauIntervention;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.Services;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.MyLocationListener;
import com.dolibarrmaroc.com.utils.TechnicienManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;

public class TechnicienActivity extends  Activity implements OnClickListener,OnItemSelectedListener{
	//IOC
	private VendeurManager vendeurManager;
	private TechnicienManager technicien;

	//offline
	private ioffline myoffine;
	
	//Ne pas laisser telephone en veille
	private WakeLock wakelock;

	// UI
	private Spinner services;
	private Spinner objets;
	private AutoCompleteTextView clients;
	private Button next;
	private Button deconnecte;

	//Declaration Objet
	private Compte compte;
	private Client client;
	private JSONParser json;
	private GpsTracker gps;

	//Spinner Remplissage
	private List<String> listclt;
	private List<String> listobjet;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;
	private ProgressDialog dialog2;

	//Autre Variable
	private List<Client> allClient;
	private Client clt;
	private Services serv;
	private String objet;

	private int firstexecution;

	//Recuperation Interface de l'utilisateur Par Service
	private String nmb;
	private String serviceName;
	private List<String> labels;

	public TechnicienActivity() {
		firstexecution = 0;

		listclt = new ArrayList<String>();
		listclt.add("--Choisir un Client---");

		listobjet = new ArrayList<String>();
		
		String lan = Locale.getDefault().getLanguage();
		
		if(lan.toLowerCase().equals("ar")){
			listobjet.add("--اختيار الموضوع--");
			listobjet.add("تبديل");
			listobjet.add("تتبيت");
			listobjet.add("معاينة");
			listobjet.add("ازالة");
			listobjet.add("ازالة/تتبيت");
		}else if(lan.toLowerCase().equals("fr")){
			listobjet.add("--Choisir un Objet--");
			listobjet.add("Remplacement");
			listobjet.add("Installation");
			listobjet.add("Vérification");
			listobjet.add("Désinstallation");
			listobjet.add("Désin/Réinstall");
		}
	

		allClient = new ArrayList<Client>();
		labels = new ArrayList<>();

		gps = new GpsTracker();

		json = new JSONParser();

		vendeurManager = VendeurManagerFactory.getClientManager();
		technicien = TechnicienManagerFactory.getClientManager();

		serv = new Services();
		clt = new Client();
		
		
	}

	@Override
	protected void onStart() {
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();

		
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			if(firstexecution == 0){
				dialog = ProgressDialog.show(TechnicienActivity.this, getResources().getString(R.string.map_data),
						getResources().getString(R.string.msg_wait), true);
				new ConnexionTask().execute();
			}
		}else{
			if(firstexecution == 0){
				dialog = ProgressDialog.show(TechnicienActivity.this, getResources().getString(R.string.map_data),
						getResources().getString(R.string.msg_wait), true);
				new OfflineTask().execute();
			}
		}
		
		super.onStart();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_technicien);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}


		try {
			/********* Creation Interfaces *****************/
			objets = (Spinner) findViewById(R.id.objet_tech);
			clients = (AutoCompleteTextView) findViewById(R.id.client_technicien);
			next = (Button) findViewById(R.id.secondStep);
			deconnecte = (Button) findViewById(R.id.deconnecte);

			/********* Declaration Evenement ****************/

			next.setOnClickListener(this);
			deconnecte.setOnClickListener(this);
			objets.setOnItemSelectedListener(this);
 
			clients.setOnItemClickListener(new OnItemClickListener() {


				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String selected = (String) parent.getItemAtPosition(position);

					clients.showDropDown();

					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);

					clients.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});

					for (int i = 0; i < allClient.size(); i++) {
						if(selected.equals(allClient.get(i).getName())){
							clt = allClient.get(i);
							Log.e(">> Client Selected",clt.toString());
							break;
						}
					}
				}
			});


			gps = getGpsApplication();

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				nmb = this.getIntent().getStringExtra("nmbService");
				serviceName = this.getIntent().getStringExtra("service");
				for (int i = 0; i < Integer.parseInt(nmb); i++) {
					labels.add(this.getIntent().getStringExtra("labels"+i));
				}
				Log.e(">> Service Labels",labels.toString());
			}

			addItemsOnSpinner(objets,2);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.secondStep){

			Log.e("clit >> ",clt.toString());
			
			String lan = Locale.getDefault().getLanguage();
			String in1="",in2="";
			
			if(clt.getName() == null){
				Toast.makeText(TechnicienActivity.this, getResources().getString(R.string.tecv45), Toast.LENGTH_LONG).show();
			}else{
				if(lan.toLowerCase().equals("ar")){
					in1 = "--اختيار الزبون--";
					in2 ="--اختيار الموضوع--";
				}else if(lan.toLowerCase().equals("fr")){
					in1 = "--Choisir un Client--";
					in2 ="--Choisir un Objet--";
				}
				if(in2.equals(objet) || in1.equals(clt.getName())){//"--Choisir un Objet---"   --Choisir un Client---
					Toast.makeText(TechnicienActivity.this, getResources().getString(R.string.tecv45), Toast.LENGTH_LONG).show();
				}
				else{
					Intent intent = new Intent(this, SecondStepTechnActivity.class);
					intent.putExtra("user", compte);
					intent.putExtra("service", serviceName);
					intent.putExtra("objet", objet);
					intent.putExtra("client", clt);
					intent.putExtra("nmbService", nmb);
					for (int i = 0; i < Integer.parseInt(nmb); i++) {
						intent.putExtra("labels"+i, labels.get(i).toString());
					}
					startActivity(intent);
				}
			}
			

			

		}else if(v.getId() == R.id.deconnecte){

			
			Intent intent1 = new Intent(this, ConnexionActivity.class);
			intent1.putExtra("user", compte);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent1);
			this.finish();

		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent1 = new Intent(this, ConnexionActivity.class);
		intent1.putExtra("user", compte);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent1);
		this.finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		String selected = parent.getItemAtPosition(position).toString();

		//Spinner Objets
		if(parent.getId() == R.id.objet_tech){

			for (int i = 0; i < listobjet.size(); i++) {
				if(selected.equals(listobjet.get(i))){
					
					
					String lan = Locale.getDefault().getLanguage();
					
					if(lan.toLowerCase().equals("ar")){
						objet = listobjet.get(i);
						
						if(objet.equals("تبديل")){
							objet = "Remplacement";
						}else if(objet.equals("تتبيت")){
							objet = "Installation";
						}else if(objet.equals("معاينة")){
							objet = "V&eacute;rification";
						}else if(objet.equals("ازالة")){
							objet = "D&eacute;sinstallation";
						}else if(objet.equals("ازالة/تتبيت")){
							objet = "D&eacute;sinstallation/R&eacute;installation";
						}
					}else if(lan.toLowerCase().equals("fr")){
						if("Désinstallation".equals(listobjet.get(i))){
							objet = "D&eacute;sinstallation";
						}else if("Désin/Réinstall".equals(listobjet.get(i))){
							objet = "D&eacute;sinstallation/R&eacute;installation";
						}if("Vérification".equals(listobjet.get(i))){
							objet = "V&eacute;rification";
						}else{
							objet = listobjet.get(i);
						}
					}
					
					Log.e(">> Objet Selected Selected", objet);
					break;
				}
			}

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public void addItemsOnSpinner(Spinner s,int type) {

		if(type == 2){
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listobjet);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			s.setAdapter(dataAdapter);

		}

	}

	class ConnexionTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			myoffine = new Offlineimpl(getApplicationContext());
			
			//myoffine.CleanIntervention();
			
			allClient = technicien.selectAllClient(compte);
			for (int i = 0; i < allClient.size(); i++) {
				listclt.add(allClient.get(i).getName());
			}

			
			if(!myoffine.checkFolderexsiste()){
				showmessageOffline();
			}else{
				myoffine.shynchronizeClients(allClient);
			}
			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();

					firstexecution = 1989;
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TechnicienActivity.this, android.R.layout.simple_spinner_item, listclt);
					dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

					//facturespinner.setAdapter(dataAdapter);
					addItemsOnSpinner(services, 3);

					//ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TechnicienActivity.this,android.R.layout.simple_dropdown_item_1line, listclt);				    
					clients.setAdapter(dataAdapter);
					clients.setThreshold(1);
					clients.setTextColor(Color.RED); 
					
					if(myoffine.checkAvailableofflinestorage2() > 0){
						dialog2 = ProgressDialog.show(TechnicienActivity.this,getResources().getString(R.string.caus15),
								getResources().getString(R.string.msg_wait), true);
						new ServerSideTask().execute();
					}
					

					//wakelock.release();
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}
	
	class OfflineTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			myoffine = new Offlineimpl(getApplicationContext());
			
			allClient = myoffine.LoadClients("");
			for (int i = 0; i < allClient.size(); i++) {
				listclt.add(allClient.get(i).getName());
			}

			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();

					firstexecution = 1989;
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TechnicienActivity.this, android.R.layout.simple_spinner_item, listclt);
					dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

					//facturespinner.setAdapter(dataAdapter);
					addItemsOnSpinner(services, 3);

					//ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TechnicienActivity.this,android.R.layout.simple_dropdown_item_1line, listclt);				    
					clients.setAdapter(dataAdapter);
					clients.setThreshold(1);
					clients.setTextColor(Color.RED); 

					
					for (BordreauIntervention b:myoffine.LoadInterventions("")) {
						Log.e(">> interv ",b.toString());
					}
					//wakelock.release();
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}

	public GpsTracker getGpsApplication(){

		LocationManager mlocManager=null;
		MyLocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if(MyLocationListener.latitude>0)
			{
				gps.setLangitude(""+MyLocationListener.longitude);
				gps.setLatitude(""+MyLocationListener.latitude);
				gps.setAltitude(MyLocationListener.altitude);
				gps.setDateString(MyLocationListener.dateString);
				gps.setDirection(MyLocationListener.direction);
				gps.setSatellite(MyLocationListener.satellite);
				gps.setSpeed(MyLocationListener.speed);
			}
			else
			{
				gps.setLangitude(""+0);
				gps.setLatitude(""+0);
			}
		}
		return gps;
	}
	
	 
	
	public void showmessageOffline(){
		try {
			 
	         LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.msgstorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(TechnicienActivity.this);
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

	class ServerSideTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				myoffine = new Offlineimpl(getApplicationContext());
				if(CheckOutNet.isNetworkConnected(getApplicationContext())){
					if(!myoffine.checkFolderexsiste()){
						showmessageOffline();
					}else{
						myoffine.sendOutIntervention(compte);
					}
					
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("erreu synchro",e.getMessage() +" << ");
			}
			
			Log.e("start ","start cnx service");
			
			return null;
		}

		protected void onPostExecute(String sResponse) {
			try {
				if (dialog2.isShowing()){
					dialog2.dismiss();
					
					
					Log.e("end ","load interv");
					/*
					Intent intent2 = new Intent(ConnexionActivity.this, SettingsynchroActivity.class);
					intent2.putExtra("user", compte);
					startActivity(intent2);
					*/
				}
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}
	
	
}
