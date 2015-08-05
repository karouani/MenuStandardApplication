package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.FactureManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.database.DataErreur.DatabaseHandler;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.FileData;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.MyGpsInvoice;
import com.dolibarrmaroc.com.models.MyProdRemise;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.models.TotauxTicket;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.FactureManagerFactory;
import com.dolibarrmaroc.com.utils.MyLocationListener;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.ServiceDao;
import com.dolibarrmaroc.com.utils.TinyDB;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;

@SuppressLint("NewApi") public class NextEtapeActivity extends Activity implements OnClickListener,OnItemSelectedListener{

	//BD et Synchronisation
	private FactureManager manager;
	private  ProgressDialog dialog = null;
	private TinyDB db;
	private Myinvoice meinvo = new Myinvoice();
	private double remise = 0;
	private Map<String, Remises> allremises = new HashMap<>();
	private Map<String,TotauxTicket> map_totaux = new HashMap<>();
	TotauxTicket total_ticket = new TotauxTicket();
	
	private StockVirtual sv;
	
	//database 
		private DatabaseHandler database;
		private WakeLock wakelock;
		
	//Recuperation
	private List<Produit> produitsFacture;
	private int nmb;
	private String totalttc,totalht;
	private GpsTracker gps;
	private String idclt;
	private Compte compte;

	//Interface
	private TextView du;
	private TextView rendu;
	private EditText encaisse;
	private EditText numchek;
	private Button liquide;
	private Button ticket;
	private Button valider;
	private Spinner mode;

	private ServiceDao daoGps;

	private String reglement;
	private String commentaire;
	private String numChek = "";
	
	private String battery;
	private String imei;
	private String num;
	private String amount = "0";
	private int typeImpriment;
	
	private ProgressDialog dialog2;
	private int type_invoice;
	
	//Spinner Remplissage
	private List<String> listDictionnaire;
	private ArrayList<HashMap<String, String>> dico;
	
	/*********************** Produits ************************/
	ArrayList<Produit> prd;
	HashMap<Integer, ArrayList<Produit>> map ;
	
	private Offlineimpl myofline;
	

	@Override
	public void onBackPressed() {
		NextEtapeActivity.this.finish();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next_etape);
		
		produitsFacture = new ArrayList<Produit>();
		manager = FactureManagerFactory.getFactureManager();
		daoGps = new ServiceDao();
		gps = new GpsTracker();
		
		sv = new StockVirtual(NextEtapeActivity.this);
		
		try {
			 PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		        wakelock.acquire();
		        
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
			
			db = new TinyDB(this);
			Log.e("Table Promotion",db.loadMapRemises("promotion").toString());
			allremises = db.loadMapRemises("promotion");
			
			listDictionnaire = new ArrayList<String>();
			//listDictionnaire.add("--Payement Type---");

			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = this.registerReceiver(null, ifilter);
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

			battery = (level / (float)scale)*100 + "";

			//Getting the Object of TelephonyManager 
			TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			imei = tManager.getDeviceId();
			num = tManager.getLine1Number();

			Log.d("GPS",gps.toString());

			Bundle objetbunble  = this.getIntent().getExtras();
			double totaux_tva = 0;
			double totaux_ht = 0;
			
			if (objetbunble != null) {

				nmb = Integer.parseInt(this.getIntent().getStringExtra("nmbproduct"));
				totalttc = this.getIntent().getStringExtra("total");
				Log.e("Format ttc",totalttc);
				
				//totalht = this.getIntent().getStringExtra("totalht");
				idclt = this.getIntent().getStringExtra("idclt");
				commentaire = this.getIntent().getStringExtra("commentaire");
				compte =  (Compte) getIntent().getSerializableExtra("compte");
				dico = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("dico");
				type_invoice = Integer.parseInt(getIntent().getStringExtra("typeinvoice"));
				
				prd = new ArrayList<>();
				map = new HashMap<>();
				
				
				if(nmb > 0){
					for (int i = 0; i < nmb; i++) {
						Produit p = (Produit) getIntent().getSerializableExtra("produit"+i);
						produitsFacture.add(p);
						//Log.d("Recuperation Depuis Next", produitsFacture.toString()+" Var "+i);
						prd.add(p);
						
						double remise_ht = 0;
						Remises remis = new Remises();
						remis = allremises.get(p.getRef());
						
						double ht = (100 * p.getPrixttc())/(double)(100 + Double.parseDouble(p.getTva_tx())) * p.getQtedemander();
						
						if(remis.getType() == 0){
							remise = remise + remises(p,remis);
							ht = ht - (ht*remis.getRemise()/(double)100);
						}
						
						totaux_ht += ht;
					}
					map.put(0, prd);
				}
			}
			
			Log.e("TTC ",remise+"");
			
			String languageToLoad  = "fr"; // your language
		    Locale locale = new Locale(languageToLoad); 
		    Locale.setDefault(locale);
		    Configuration config = new Configuration();
		    config.locale = locale;
		    getBaseContext().getResources().updateConfiguration(config, 
		      getBaseContext().getResources().getDisplayMetrics());
			
			du 			= (TextView) findViewById(R.id.txtTotal);
			rendu		= (TextView) findViewById(R.id.resteapaye);
			encaisse	= (EditText) findViewById(R.id.apaye);
			numchek		= (EditText) findViewById(R.id.check_number);
			mode 		= (Spinner) findViewById(R.id.modepayementDico);
			mode.setOnItemSelectedListener(this);

			liquide		= (Button) findViewById(R.id.validerfactureNext);
			ticket		= (Button) findViewById(R.id.validation);
			valider = (Button)findViewById(R.id.validation2);
			
			/*
			if(!CheckOutNet.isNetworkConnected(getApplicationContext())){
				liquide.setEnabled(false);
				liquide.setText("PDF not Avilable");
			}
			*/
			liquide.setOnClickListener(this);
			ticket.setOnClickListener(this);
			valider.setOnClickListener(this);
			
			double tt = Double.parseDouble(totalttc) - remise;
			
			totaux_tva = tt -totaux_ht;
			
			du.setText( String.format( "%.2f", tt ).replace(",", "."));
			total_ticket.setTotal_ttc(tt);;
			total_ticket.setTotal_tva(totaux_tva);
			total_ticket.setTotal_ht(totaux_ht);
			
			rendu.setText("0");
			
			encaisse.addTextChangedListener(new TextWatcher() {
				Double m = (double) 0;
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
					if(!s.toString().equals("-") && !s.toString().equals(".")){
						
						if(!"".equals(encaisse.getText().toString())) m = Double.parseDouble(encaisse.getText().toString());
						if(m > Double.parseDouble(du.getText().toString())){
							m = 0D;
							alertmontantdepasser();
							rendu.setText("0");
							amount = "0";
						}else if("".equals(encaisse.getText().toString())) {
							rendu.setText(du.getText().toString());
							amount = "0";
						}else{
							Double h = Double.parseDouble(du.getText().toString()) - m;
							rendu.setText(h+"");
						}
						
						
					}
				
					
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if(!s.toString().equals("-") && !s.toString().equals(".")){
						if(m <= Double.parseDouble(du.getText().toString()) && m!= 0 && !"".equals(encaisse.getText().toString())){
							amount = encaisse.getText().toString();
						}else if("".equals(encaisse.getText().toString())) {
							amount = "0";
						}
					}
					
					/*
					Log.e("amount ",amount);
					
					amount = "-4000";
					Log.e("amount ",amount);
					*/
					
					
				}
			});
			
			
			gps =  getGpsApplication();

			for (int i = 0; i < dico.size(); i++) {
				HashMap<String, String> values = dico.get(i);
				Log.i("Dico GetList"+i,values.toString());

				for (String val : values.values()) {
					listDictionnaire.add(val);
				}

			}
			
			addItemsOnSpinner();
			
			database = new DatabaseHandler(getApplicationContext());
			
			
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		myofline = new Offlineimpl(getApplicationContext());
		 if(!myofline.checkFolderexsiste()){
	        	showmessageOffline();
	        }
		super.onStart();
		
		/*
	        if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			dialog2 = ProgressDialog.show(NextEtapeActivity.this, "Synchronisation avec le serveur",
					getResources().getString(R.string.msg_wait_sys), true);
			new ServerSideTask().execute();
		}
		*/
	}
	public void addItemsOnSpinner() {

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listDictionnaire);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mode.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v) {
		myofline = new Offlineimpl(getApplicationContext());
		
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			if(v.getId() == R.id.validerfactureNext){
				
				if(myofline.checkClient_is_Prospect(Integer.parseInt(idclt)) == -1){
					typeImpriment = 1;
					//reglement = "4";
					dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion "+getResources().getString(R.string.facture),
							getResources().getString(R.string.msg_wait), true);
					new FactureTask().execute();
				}else{
					typeImpriment = 2;
					//reglement = "4";
					dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion "+getResources().getString(R.string.facture),
							getResources().getString(R.string.msg_wait), true);
					new offlineTask().execute();
					
					double p = 0;
					if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
					else p = Double.parseDouble(encaisse.getText().toString());
					
					total_ticket.setRegle(p);
					double rci = 0;
					if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
						rci = Double.parseDouble(du.getText().toString());
					}else{
						rci = Double.parseDouble(rendu.getText().toString());
					}
					total_ticket.setRest(rci);
					map_totaux.put("total", total_ticket);
					db.saveMapTotaux("totaux", map_totaux);
				}
				

			}
			if(v.getId() == R.id.validation){
				if(myofline.checkClient_is_Prospect(Integer.parseInt(idclt)) == -1){
					typeImpriment = 2;
					//reglement = "4";
					dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion"+getResources().getString(R.string.facture),
							getResources().getString(R.string.msg_wait), true);
					new FactureTask().execute();
					
					double p = 0;
					if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
					else p = Double.parseDouble(encaisse.getText().toString());
					
					total_ticket.setRegle(p);
					
					double rci = 0;
					if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
						rci = Double.parseDouble(du.getText().toString());
					}else{
						rci = Double.parseDouble(rendu.getText().toString());
					}
					total_ticket.setRest(rci);
					map_totaux.put("total", total_ticket);
					db.saveMapTotaux("totaux", map_totaux);
				}else{
					typeImpriment = 2;
					//reglement = "4";
					dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion"+getResources().getString(R.string.facture),
							getResources().getString(R.string.msg_wait), true);
					new offlineTask().execute();
					
					double p = 0;
					if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
					else p = Double.parseDouble(encaisse.getText().toString());
					
					total_ticket.setRegle(p);


					double rci = 0;
					if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
						rci = Double.parseDouble(du.getText().toString());
					}else{
						rci = Double.parseDouble(rendu.getText().toString());
					}
					total_ticket.setRest(rci);
					map_totaux.put("total", total_ticket);
					db.saveMapTotaux("totaux", map_totaux);
				}
				
			}
			if(v.getId() == R.id.validation2){
				if(myofline.checkClient_is_Prospect(Integer.parseInt(idclt)) == -1){
					typeImpriment = 2;
					//reglement = "4";
					dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion"+getResources().getString(R.string.facture),
							getResources().getString(R.string.msg_wait), true);
					new ValidationTask().execute();
					
					double p = 0;
					if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
					else p = Double.parseDouble(encaisse.getText().toString());
					
					total_ticket.setRegle(p);


					double rci = 0;
					if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
						rci = Double.parseDouble(du.getText().toString());
					}else{
						rci = Double.parseDouble(rendu.getText().toString());
					}
					total_ticket.setRest(rci);
					map_totaux.put("total", total_ticket);
					db.saveMapTotaux("totaux", map_totaux);
				}else{
					typeImpriment = 2;
					//reglement = "4";
					dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion"+getResources().getString(R.string.facture),
							getResources().getString(R.string.msg_wait), true);
					new ValidationOfflineTask().execute();
					
					double p = 0;
					if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
					else p = Double.parseDouble(encaisse.getText().toString());
					
					total_ticket.setRegle(p);


					double rci = 0;
					if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
						rci = Double.parseDouble(du.getText().toString());
					}else{
						rci = Double.parseDouble(rendu.getText().toString());
					}
					total_ticket.setRest(rci);
					map_totaux.put("total", total_ticket);
					db.saveMapTotaux("totaux", map_totaux);
				}
				
			}
		}else{
			if(v.getId() == R.id.validerfactureNext){

				alertpdf();
			}
			if(v.getId() == R.id.validation){
				typeImpriment = 2;
				//reglement = "4";
				dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion"+getResources().getString(R.string.facture),
						getResources().getString(R.string.msg_wait), true);
				new offlineTask().execute();
				
				double p = 0;
				if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
				else p = Double.parseDouble(encaisse.getText().toString());
				
				total_ticket.setRegle(p);

				double rci = 0;
				if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
					rci = Double.parseDouble(du.getText().toString());
				}else{
					rci = Double.parseDouble(rendu.getText().toString());
				}
				total_ticket.setRest(rci);
				map_totaux.put("total", total_ticket);
				db.saveMapTotaux("totaux", map_totaux);
			}
			if(v.getId() == R.id.validation2){
				typeImpriment = 2;
				//reglement = "4";
				dialog = ProgressDialog.show(NextEtapeActivity.this, "Insertion"+getResources().getString(R.string.facture),
						getResources().getString(R.string.msg_wait), true);
				new ValidationOfflineTask().execute();
				
				double p = 0;
				if("".equals(encaisse.getText().toString()) || encaisse.getText().toString() == null)  p = 0;
				else p = Double.parseDouble(encaisse.getText().toString());
				
				total_ticket.setRegle(p);

				double rci = 0;
				if("".equals(rendu.getText().toString()) || "0".equals(rendu.getText().toString()) || rendu.getText().toString() == null)  {
					rci = Double.parseDouble(du.getText().toString());
				}else{
					rci = Double.parseDouble(rendu.getText().toString());
				}
				total_ticket.setRest(rci);
				map_totaux.put("total", total_ticket);
				db.saveMapTotaux("totaux", map_totaux);
			}
		}
		
	}


	class FactureTask extends AsyncTask<Void, Void, String> {

		private FileData data;
		@Override
		protected String doInBackground(Void... arg0) {
			numChek = numchek.getText().toString();
			
			myofline = new Offlineimpl(getApplicationContext());
			
			
			

			/*********************** offline ****************************************/
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(myofline.checkAvailableofflinestorage() > 0){
					myofline.SendOutData(compte);	
				}
				
			}
			
			
			
			data = manager.insert(produitsFacture, idclt, nmb, commentaire, compte, reglement , amount , numChek , 1 ,allremises,type_invoice);
			
			
			if(data != null){
				if(!data.getErreur().equals("-100")){
					daoGps.insertData(gps,imei,num,battery,compte,data.getErreur());
					
					//myofline = new Offlineimpl(getApplicationContext());
					database = new DatabaseHandler(getApplicationContext());

					meinvo = myofline.prepaValideIvoice(data.getNumFacture(), produitsFacture, idclt, nmb, commentaire, compte, reglement , amount , numChek , 1 ,prepaRemise(allremises),gps,imei,num,battery,total_ticket);
					if(meinvo != null){
						//data = meinvo.getData();
						
						//myofline.updateProduits(meinvo);
						
						if(type_invoice == 0){
							myofline.updateProduits(meinvo);
						}else {
							for (int i = 0; i < produitsFacture.size(); i++) {
								sv.addrow("", produitsFacture.get(i).getId(), produitsFacture.get(i).getQtedemander(), type_invoice+"");
							}
						}
					}
				}
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					if(data != null){
						
						if(!data.getErreur().equals("-100")){
							Intent intent =new Intent(NextEtapeActivity.this,EsignatureActivity.class);

						
							intent.putExtra("compte", compte);
							intent.putExtra("path", data.getPath());
							intent.putExtra("pdf", data.getPdf());
							intent.putExtra("fichier", data.getFileName());
							intent.putExtra("objet", data);
							intent.putExtra("type", typeImpriment);				
							intent.putExtra("prds", map);
							
							if(CheckOutNet.isNetworkConnected(getApplicationContext())){
								myofline = new Offlineimpl(getApplicationContext());
								/*
								if(myofline.checkAvailableofflinestorage() > 0){
									dialog2 = ProgressDialog.show(NextEtapeActivity.this, "Synchronisation avec le serveur",
											getResources().getString(R.string.msg_wait_sys), true);
									new ServerSideTask().execute();
								}
								*/
								
							}
							
							startActivity(intent);
							Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.msg_add_invoice), Toast.LENGTH_LONG).show();
							NextEtapeActivity.this.finish();
						}else{
							alert_sw();
						}
						
						
						
						
					}else{
						alert_sw();
					}
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}

	}
	
	class offlineTask extends AsyncTask<Void, Void, String> {

		private FileData data;
		
		@Override
		protected String doInBackground(Void... arg0) {
			myofline = new Offlineimpl(getApplicationContext());
			database = new DatabaseHandler(getApplicationContext());
			
			numChek = numchek.getText().toString();
			
			
			meinvo = myofline.shynchronizeInvoice(database.addrow("fc")+"", produitsFacture, idclt, nmb, commentaire, compte, reglement , amount , numChek , 1 ,prepaRemise(allremises),gps,imei,num,battery,total_ticket,type_invoice);
			if(meinvo != null){
				data = meinvo.getData();
				
				//myofline.updateProduits(meinvo);
				
				if(type_invoice == 0){
					myofline.updateProduits(meinvo);
				}else {
					for (int i = 0; i < produitsFacture.size(); i++) {
						sv.addrow("", produitsFacture.get(i).getId(), produitsFacture.get(i).getQtedemander(), type_invoice+"");
					}
				}
				
				myofline.shynchronizeGpsInvoice(new MyGpsInvoice(gps,imei,num,battery,compte,data.getErreur()));
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					Log.e("data >>> ",data+"");
					if(data != null){
						
						Intent intent =new Intent(NextEtapeActivity.this,TicketActivity.class);//EsignatureActivity

						intent.putExtra("compte", compte);
						intent.putExtra("path", data.getPath());
						intent.putExtra("pdf", data.getPdf());
						intent.putExtra("fichier", data.getFileName());
						intent.putExtra("objet", data);
						intent.putExtra("type", typeImpriment);				
						intent.putExtra("prds", map);
						intent.putExtra("invo", meinvo);
						startActivity(intent);
						Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.msg_add_invoice), Toast.LENGTH_LONG).show();
						NextEtapeActivity.this.finish();
					}else{
						showmessageOffline();
					}
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}

	}
	
	class ValidationTask extends AsyncTask<Void, Void, String> {

		private FileData data;
		@Override
		protected String doInBackground(Void... arg0) {
			numChek = numchek.getText().toString();
			
			
			myofline = new Offlineimpl(getApplicationContext());

			/*********************** offline ****************************************/
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(myofline.checkAvailableofflinestorage() > 0){
					myofline.SendOutData(compte);	
				}
				
			}
			
			data = manager.insert(produitsFacture, idclt, nmb, commentaire, compte, reglement , amount , numChek , 1 ,allremises,type_invoice);
			
			if(data != null){
				if(!data.getErreur().equals("-100")){
					daoGps.insertData(gps,imei,num,battery,compte,data.getErreur());
					
					database = new DatabaseHandler(getApplicationContext());

					numChek = numchek.getText().toString();

					meinvo = myofline.prepaValideIvoice(data.getNumFacture(), produitsFacture, idclt, nmb, commentaire, compte, reglement , amount , numChek , 1 ,prepaRemise(allremises),gps,imei,num,battery,total_ticket);
					if(meinvo != null){
						//data = meinvo.getData();
						if(type_invoice == 0){
							myofline.updateProduits(meinvo);
						}else {
							for (int i = 0; i < produitsFacture.size(); i++) {
								sv.addrow("", produitsFacture.get(i).getId(), produitsFacture.get(i).getQtedemander(), type_invoice+"");
							}
						}
						
						//myofline.shynchronizeGpsInvoice(new MyGpsInvoice(gps,imei,num,battery,compte,data.getErreur()));
					}
				}
				
			}

			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if(dialog.isShowing()){
					dialog.dismiss();
					if(data != null){
						
						if(!data.getErreur().equals("-100")){
							Intent intent =new Intent(NextEtapeActivity.this,ValiderActivity.class);//EsignatureActivity

							intent.putExtra("compte", compte);
							intent.putExtra("path", data.getPath());
							intent.putExtra("pdf", data.getPdf());
							intent.putExtra("fichier", data.getFileName());
							intent.putExtra("objet", data);
							intent.putExtra("type", typeImpriment);				
							intent.putExtra("prds", map);
							intent.putExtra("invo", meinvo);
							intent.putExtra("typeaction", 1);
							startActivity(intent);
							Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.msg_add_invoice), Toast.LENGTH_LONG).show();
							NextEtapeActivity.this.finish();
						}else{
							alert_sw();
						}
						
					}else{
						alert_sw();
					}
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}

	}
	
	class ValidationOfflineTask extends AsyncTask<Void, Void, String> {

		private FileData data;

		@Override
		protected String doInBackground(Void... arg0) {
			myofline = new Offlineimpl(getApplicationContext());
			database = new DatabaseHandler(getApplicationContext());

			numChek = numchek.getText().toString();


			meinvo = myofline.shynchronizeInvoice(database.addrow("fc")+"", produitsFacture, idclt, nmb, commentaire, compte, reglement , amount , numChek , 1 ,prepaRemise(allremises),gps,imei,num,battery,total_ticket,type_invoice);
			
			Log.e("invooo ",myofline.LoadInvoice("").toString());
			
			if(meinvo != null){
				data = meinvo.getData();
				
				//myofline.updateProduits(meinvo);
				
				myofline.shynchronizeGpsInvoice(new MyGpsInvoice(gps,imei,num,battery,compte,data.getErreur()));
				
				if(type_invoice == 0){
					myofline.updateProduits(meinvo);
				}else {
					for (int i = 0; i < produitsFacture.size(); i++) {
						sv.addrow("", produitsFacture.get(i).getId(), produitsFacture.get(i).getQtedemander(), type_invoice+"");
					}
				}
			}


			return null;
		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					Log.e("data >>> ",data+"");
					if(data != null){

						Intent intent =new Intent(NextEtapeActivity.this,ValiderActivity.class);//EsignatureActivity

						intent.putExtra("compte", compte);
						intent.putExtra("path", data.getPath());
						intent.putExtra("pdf", data.getPdf());
						intent.putExtra("fichier", data.getFileName());
						intent.putExtra("objet", data);
						intent.putExtra("type", typeImpriment);				
						intent.putExtra("prds", map);
						intent.putExtra("invo", meinvo);
						intent.putExtra("typeaction", 1);
						startActivity(intent);
						Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.msg_add_invoice), Toast.LENGTH_LONG).show();
						NextEtapeActivity.this.finish();
					}else{
						showmessageOffline();
					}
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(NextEtapeActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}


	}
	
	class ServerSideTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				myofline = new Offlineimpl(getApplicationContext());
				
				List<Produit> products = new ArrayList<>();
				List<Client> clients = new ArrayList<>();
				
				VendeurManager vendeurManager = VendeurManagerFactory.getClientManager();
				
				Dictionnaire dico  = new Dictionnaire();
				
				if(CheckOutNet.isNetworkConnected(getApplicationContext())){
					myofline.SendOutData(compte);	
				}
				

				

				/*********************** offline ****************************************/
				if(!myofline.checkFolderexsiste()){
					showmessageOffline();
				}else{
					Log.e("begin offline from network",">>start load");
					if(CheckOutNet.isNetworkConnected(getApplicationContext())){

						products = vendeurManager.selectAllProduct(compte);
						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits().size(); j++) {
								if(sv.getAllProduits().get(j).getRef().equals(products.get(i).getId())){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits().get(j).getQteDispo());
								}
							}
						}
						
						if(products.size() > 0){
							myofline.CleanProduits();
							myofline.CleanPromotionProduit();
							myofline.shynchronizeProduits(products);
							myofline.shynchronizePromotion(vendeurManager.getPromotionProduits());	
						}
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						dico = vendeurManager.getDictionnaire();
						if(dico.getDico().size() > 0){
							myofline.CleanDico();
							myofline.shynchronizeDico(dico);	
						}
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						clients = vendeurManager.selectAllClient(compte);
						if(clients.size() > 0){
							myofline.CleanClients();
							myofline.CleanPromotionClient();
							myofline.shynchronizeClients(clients);
							myofline.shynchronizePromotionClient(vendeurManager.getPromotionClients());	
						}

					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						CommercialManager manager = CommercialManagerFactory.getCommercialManager();
						myofline.CleanProspectData();
						myofline.shynchronizeProspect(manager.getInfos(compte));	
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){

						PayementManager payemn = PayementManagerFactory.getPayementFactory();
						myofline.CleanPayement();
						myofline.shynchronizePayement(payemn.getFactures(compte));	
					}
				}
				

				
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("erreu synchro",e.getMessage() +" << ");
			}
			
			return null;
		}

		protected void onPostExecute(String sResponse) {
			try {
				if (dialog2.isShowing()){
					dialog2.dismiss();
					
				}
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}
	

	public GpsTracker getGpsApplication(){

		LocationManager mlocManager=null;
		LocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);


		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			gps.setLangitude(""+MyLocationListener.longitude);
			gps.setLatitude(""+MyLocationListener.latitude);
			gps.setAltitude(MyLocationListener.altitude);
			gps.setDateString(MyLocationListener.dateString);
			gps.setDirection(MyLocationListener.direction);
			gps.setSatellite(MyLocationListener.satellite);
			gps.setSpeed(MyLocationListener.speed);
		}
		return gps;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		String selected = parent.getItemAtPosition(position).toString();
		if("--Payement Type---".equals(selected)){
			reglement = "LIQ";
		}else{
			for (int i = 0; i < dico.size(); i++) {
				HashMap<String, String> values = dico.get(i);
				Log.i("Dico GetList"+i,values.toString());

				Iterator<String> keySetIterator = values.keySet().iterator();
				
				while(keySetIterator.hasNext()){
					String key = keySetIterator.next();
					if(selected.equals(values.get(key))){
						reglement = key;
						Log.d("Reglement Selected >>>>>>> ",key);
					}
				}
			}
		}
		

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	public double remises(Produit prd,Remises remis){
		int q = remis.getQte();
		int p = remis.getRemise();
		int qtd = prd.getQtedemander();
		double prix = prd.getPrixttc();
		double retour = 0;
		
		if(prd.getQtedemander() >= q){
			retour = ((prix*qtd) * p) / (double)100;
		}
		
		return retour;
	}
	
	 
	
	public String calculIdInvoice(){
		String res="";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			String sdt1 = "";//calendar.get(Calendar.YEAR)+"";

			int m = calendar.get(Calendar.MONTH)+1;
			int d = calendar.get(Calendar.DAY_OF_MONTH);
			int hr = calendar.get(Calendar.HOUR_OF_DAY);
			int mnt = calendar.get(Calendar.MINUTE);
			int sec = calendar.get(Calendar.SECOND);
			//System.out.println("sdt 1 "+sdt1+" mon "+m+ " d "+d);

			if(m == 0)m=12;
			if(m < 10){
				sdt1+="0"+m+"";
			}else{
				sdt1+=m+"";
			}

			if(d < 10){
				sdt1+="0"+d;
			}else{
				sdt1+=d;
			}
			
			if(hr < 10){
				sdt1+="0"+hr;
			}else{
				sdt1+=""+hr;
			}
			
			if(mnt < 10){
				sdt1+="0"+mnt;
			}else{
				sdt1+=""+mnt;
			}
			
			if(sec < 10){
				sdt1+="0"+sec;
			}else{
				sdt1+=""+sec;
			}
			
			res = sdt1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return res;
	}

	public List<MyProdRemise> prepaRemise(Map<String, Remises> allremises){
		List<MyProdRemise> me = new ArrayList<>();
		
		if(allremises != null){
			if(allremises.size() != 0){
				for(String ref:allremises.keySet()){
					Log.e("prod "+ref," >>> "+ allremises.get(ref));
					me.add(new MyProdRemise(ref, allremises.get(ref)));
				}
			}
		}
		
		
		return me;
		
	}
	
	public void alertmontantdepasser(){
		
			AlertDialog.Builder alert = new AlertDialog.Builder(NextEtapeActivity.this);
			alert.setTitle(getResources().getString(R.string.factlab1));
			alert.setMessage(
					String.format(getResources().getString(R.string.factlab2)
							));
			alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					encaisse.setText("");
					dialog.dismiss();
					return;
				}
			});
			alert.setCancelable(false);
			alert.create().show();
	}
	
	public void showmessageOffline(){
		try {
			 
	         LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.requeststorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(NextEtapeActivity.this);
	         dialog.setView(dialogView);
 	 	     dialog.setTitle(R.string.caus9);
 	 	     dialog.setPositiveButton(R.string.caus8, new DialogInterface.OnClickListener() {
 	 	        public void onClick(DialogInterface dialog, int which) { 
 	 	        	Intent intent =new Intent(NextEtapeActivity.this,ConnexionActivity.class);//EsignatureActivity
					intent.putExtra("user", compte);
					startActivity(intent);
 	 	        	 dialog.cancel();
 	 	        }
 	 	     });
 	 	     dialog.setCancelable(true);
 	 	     dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	public void alertpdf(){
		AlertDialog.Builder alert = new AlertDialog.Builder(NextEtapeActivity.this);
		alert.setTitle(getResources().getString(R.string.caus19));
		alert.setMessage(getResources().getString(R.string.caus20));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
	
	public void alert_sw(){
		AlertDialog.Builder alert = new AlertDialog.Builder(NextEtapeActivity.this);
		alert.setTitle(getResources().getString(R.string.caus22));
		alert.setMessage(getResources().getString(R.string.caus23));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(NextEtapeActivity.this,ConnexionActivity.class);
				intent.putExtra("user", compte);
				startActivity(intent);
				NextEtapeActivity.this.finish();
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
}
