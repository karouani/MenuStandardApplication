package com.dolibarrmaroc.com;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.database.DataErreur.DatabaseHandler;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;

@SuppressLint("NewApi")
public class CommercialActivity extends Activity implements OnClickListener,OnItemSelectedListener{
	/**************************************** GPS DATA *****************************************/
	private LocationManager mLocationManager = null;
	private int LOCATION_INTERVAL = 1000;
	private float LOCATION_DISTANCE = 16;
	private double latitude;
	private double longitude;
	
	private ioffline myoffline;
	
	//database 
		private DatabaseHandler database;

	private LocationListener		onLocationChange	= new LocationListener()
	{
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}

		@Override
		public void onProviderEnabled(String provider)
		{
		}

		@Override
		public void onProviderDisabled(String provider)
		{
		}

		@Override
		public void onLocationChanged(Location location)
		{
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	};

	private int testeur;
	/******************************************************************************/
	private CommercialManager manager;
	private Compte compte;
	private GpsTracker gps;
	private WakeLock wakelock;
	private ProspectData data;
	private Prospection client;

	/********************INTERFACES COMPOSANTS *********************************/
	private EditText name;
	private EditText address;
	private EditText zip;
	private EditText tel;
	private EditText fax;
	private EditText email;

	public Spinner etat;
	private Spinner type;
	private Spinner ville;

	private Button btn,suivant;
	private LinearLayout myLayout;

	private List<EditText> maVue;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;
	private ProgressDialog dialog2;
	String resu ;
	
	private StockVirtual sv;
	
	
	public CommercialActivity() {
		// TODO Auto-generated constructor stub
		manager = CommercialManagerFactory.getCommercialManager();
		compte = new Compte();
		gps = new GpsTracker();
		data = new ProspectData();
		client = new Prospection();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commercial);


		
        
		try {
			
			
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
			
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  LOCATION_INTERVAL, LOCATION_DISTANCE, onLocationChange);
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					onLocationChange);

			if (mLocationManager == null) {
				mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
			}



			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
			}



			//name = (EditText) findViewById(R.id.comm_nom);
			address = (EditText) findViewById(R.id.comm_address);
			zip = (EditText) findViewById(R.id.comm_zip);
			tel = (EditText) findViewById(R.id.comm_phone);
			fax = (EditText) findViewById(R.id.comm_fax);
			email = (EditText) findViewById(R.id.comm_email);

			etat = (Spinner) findViewById(R.id.comm_ste);
			etat.setOnItemSelectedListener(this);
			type = (Spinner) findViewById(R.id.comm_type);
			type.setOnItemSelectedListener(this);
			ville = (Spinner) findViewById(R.id.comm_ville);
			ville.setOnItemSelectedListener(this);

			btn = (Button) findViewById(R.id.comm_etape);
			btn.setOnClickListener(this);

			suivant = (Button) findViewById(R.id.comm_suivant);
			suivant.setOnClickListener(this);

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
			wakelock.acquire();

			
			
			
			
			
			myoffline = new Offlineimpl(getApplicationContext());
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				
				
				
				myoffline = new Offlineimpl(getApplicationContext());
				if(myoffline.checkAvailableofflinestorage() > 0){
					dialog2 = ProgressDialog.show(CommercialActivity.this, getResources().getString(R.string.caus15),
							getResources().getString(R.string.msg_wait_sys), true);
					new ServerSideTask().execute(); 
				}else{
					dialog = ProgressDialog.show(CommercialActivity.this, getResources().getString(R.string.map_data),
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
				}
				
			}else{
				dialog = ProgressDialog.show(CommercialActivity.this, getResources().getString(R.string.comerciallab1),
						getResources().getString(R.string.msg_wait), true);
				new OfflineTask().execute();
			}
			
			/*
			Class squareClass = Class.forName("com.marocgeo.als.models.Prospection");
	        
	        Field[] fields = squareClass.getDeclaredFields(); 
	        for (Field f : fields) {
	            Log.e("field name = " ,f.getName());           
	        }
	        
	        */
			
			
			
			
			CommercialManager manager = CommercialManagerFactory.getCommercialManager();
			// manager.getInfos(compte);
			
			sv  = new StockVirtual(CommercialActivity.this);
			
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	private	class ConnexionTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			CommercialManager manager = CommercialManagerFactory.getCommercialManager();
			data = manager.getInfos(compte);
			Log.e("data ",data+" hopa" );
			//wakelock.acquire();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					addItemsOnSpinner(ville,data.getVilles());
					client.setCommercial_id(Integer.parseInt(compte.getIduser()));
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
	
	private	class OfflineTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			data = myoffline.LoadProspect("");
			//wakelock.acquire();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					addItemsOnSpinner(ville,data.getVilles());
					client.setCommercial_id(Integer.parseInt(compte.getIduser()));
				//	wakelock.release();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}
	}

	public void addItemsOnSpinner(Spinner s,List<String> list) {

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(dataAdapter);
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String selected = parent.getItemAtPosition(position).toString();

		if(parent.getId() == R.id.comm_ste){
			Log.i("Ste", selected+" Positin "+position);
			testeur = position;

			if (position == 0){
				/*
				 * <EditText
                android:id="@+id/comm_rc"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:hint="@string/comm_1"
                android:inputType="textPersonName" />
				 */
				//name.setHint("Nom Compl�te");
				EditText firstname = new EditText(CommercialActivity.this);
				firstname.setHint("Le Nom");
				firstname.setTag("comm_firstname");
				

				firstname.setHeight(35);

				EditText lastname = new EditText(CommercialActivity.this);
				lastname.setHint("Le Pr�nom");
				lastname.setTag("comm_lasttname");
				
				
				lastname.setHeight(35);//LinearLayout.LayoutParams.WRAP_CONTENT

				myLayout = (LinearLayout) findViewById(R.id.comm_interface);
				myLayout.removeAllViews();
				//int k = (myLayout.getWidth() / 2;
				lastname.setWidth(myLayout.getWidth()/2);
				firstname.setWidth(myLayout.getWidth()/2);

				myLayout.addView(firstname);
				myLayout.addView(lastname);

				maVue = new ArrayList<>();
				maVue.clear();
				maVue.add(firstname);
				maVue.add(lastname);

				client.setParticulier(1);

			}else{
				//name.setHint("Nom soci�t�");
				EditText name = new EditText(CommercialActivity.this);
				name.setHint("Nom soci�t�");
				name.setTag("comm_nome");
				name.setHeight(40);

				myLayout = (LinearLayout) findViewById(R.id.comm_interface);
				name.setWidth(myLayout.getWidth());
				myLayout.removeAllViews();
				myLayout.addView(name);

				maVue = new ArrayList<>();
				maVue.clear();
				maVue.add(name);
				client.setParticulier(0);
			}
		}
		if(parent.getId() == R.id.comm_type){
			Log.i("type", selected+" Positin "+position);
			if(position == 0){
				client.setClient(1);
				client.setProspect(0);
			}else{
				client.setClient(2);
				client.setProspect(1);
			}
		}

		if(parent.getId() == R.id.comm_ville){
			Log.i("ville", selected);
			client.setTown(selected);
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		// ||  "".equals(tel.getText().toString())
		if("".equals(maVue != null)	){
			Toast.makeText(CommercialActivity.this, getResources().getString(R.string.comerciallab2), Toast.LENGTH_LONG).show();
		}
		else{

			if (maVue.size() > 1) {
				client.setLastname(maVue.get(1).getText().toString());
				client.setName(maVue.get(0).getText().toString()+" "+maVue.get(1).getText().toString());
				client.setFirstname(maVue.get(0).getText().toString());
			}else{
				client.setName(maVue.get(0).getText().toString());
			}

			client.setLangitude(longitude);
			client.setLatitude(latitude);
			client.setAddress(address.getText().toString());
			client.setZip(zip.getText().toString());
			client.setPhone(tel.getText().toString());
			client.setFax(fax.getText().toString());
			client.setEmail(email.getText().toString());
			client.setStatus(1);

			if (v.getId() == R.id.comm_etape) {
				
				//checkRequiredFields();
				if(email.toString().length() == 0 || !email.toString().equals("")){
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date()); 
					
					client.setEmail(calendar.getTime().getTime()+"_anonyme@gmail.com");
					email.setText(calendar.getTime().getTime()+"_anonyme@gmail.com");
				}
				
				if(checkRequiredFields().size() > 0){
					alertinvonan();
				}else{
					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						
						if(myoffline.checkRefClient(client.getName(),client.getEmail()) == -1){
							dialog = ProgressDialog.show(CommercialActivity.this, getResources().getString(R.string.comerciallab3),
									getResources().getString(R.string.msg_wait), true);
							
							new EnregistrationTask().execute();
						}else{
							
							AlertDialog.Builder localBuilder = new AlertDialog.Builder(CommercialActivity.this);
							localBuilder
							.setTitle(getResources().getString(R.string.cmdtofc10))
							.setMessage(R.string.caus16)
							.setCancelable(false)
							.setPositiveButton("Retour",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface paramDialogInterface, int paramInt) {
									ViewGroup group = (ViewGroup)findViewById(R.id.layoutall);

									for (int i = 0, count = group.getChildCount(); i < count; ++i)
									{
										View view = group.getChildAt(i);
										if (view instanceof EditText) {
											((EditText)view).setText("");
										}

									}
									
									ViewGroup group2 = (ViewGroup)findViewById(R.id.comm_interface);

									for (int i = 0, count = group2.getChildCount(); i < count; ++i)
									{
										View view = group2.getChildAt(i);
										if (view instanceof EditText) {
											((EditText)view).setText("");
										}
									}
								}
							});
							localBuilder.show();
						}
						
					}else{
						Log.e("add clt ",client.toString());
						if(myoffline.checkRefClient(client.getName(),client.getEmail()) == -1){
							dialog = ProgressDialog.show(CommercialActivity.this, getResources().getString(R.string.comerciallab3),
									getResources().getString(R.string.msg_wait), true);
							
							new EnregistrationOfflineTask().execute();
						}else{
							
							AlertDialog.Builder localBuilder = new AlertDialog.Builder(CommercialActivity.this);
							localBuilder
							.setTitle(getResources().getString(R.string.cmdtofc10))
							.setMessage(R.string.caus16)
							.setCancelable(false)
							.setPositiveButton("Retour",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface paramDialogInterface, int paramInt) {
									ViewGroup group = (ViewGroup)findViewById(R.id.layoutall);

									for (int i = 0, count = group.getChildCount(); i < count; ++i)
									{
										View view = group.getChildAt(i);
										if (view instanceof EditText) {
											((EditText)view).setText("");
										}

									}
									
									ViewGroup group2 = (ViewGroup)findViewById(R.id.comm_interface);

									for (int i = 0, count = group2.getChildCount(); i < count; ++i)
									{
										View view = group2.getChildAt(i);
										if (view instanceof EditText) {
											((EditText)view).setText("");
										}
									}
								}
							});
							localBuilder.show();
						}
						
					}
					//String res = manager.insert(compte, client);
					//Log.d("Client",client.toString());
				}
			}else{
				if(checkRequiredFields().size() > 0){
					alertinvonan();
				}else{
					Intent intent = new Intent(CommercialActivity.this,SecondeEtapeCommercialActivity.class);
					intent.putExtra("client", client);
					intent.putExtra("user", compte);
					intent.putStringArrayListExtra("form", (ArrayList<String>) data.getJuridique());
					intent.putStringArrayListExtra("tierce", (ArrayList<String>) data.getTypent());
					intent.putExtra("code_juridique", data.getJuridique_code());
					intent.putExtra("code_type", data.getTypent_code());
					intent.putExtra("id_type", data.getTypent_id());

					startActivity(intent);	
				}
			}

		}
	}




	private	class EnregistrationTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				resu = manager.insert(compte, client);
			}else{
				if(!myoffline.checkFolderexsiste()){
		        	showmessageOffline();
		        	resu =getResources().getString(R.string.comerciallab4);
				}else{
					database = new DatabaseHandler(getApplicationContext());
					client.setId((int)database.addrow("clt"));
					resu = myoffline.shynchronizeProspection(client,compte);
				}
			}
			
			//wakelock.acquire();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}


		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					//Toast.makeText(CommercialActivity.this, resu, Toast.LENGTH_LONG).show();
					if(resu.equals("-1")){
						resu = getResources().getString(R.string.comerciallab5); 
					}else{
						resu =getResources().getString(R.string.comerciallab6); 
					}
					AlertDialog.Builder localBuilder = new AlertDialog.Builder(CommercialActivity.this);
					localBuilder
					.setMessage(resu)
					.setCancelable(false)
					.setPositiveButton("Retour",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							ViewGroup group = (ViewGroup)findViewById(R.id.layoutall);

							for (int i = 0, count = group.getChildCount(); i < count; ++i)
							{
								View view = group.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}

							}
							
							ViewGroup group2 = (ViewGroup)findViewById(R.id.comm_interface);

							for (int i = 0, count = group2.getChildCount(); i < count; ++i)
							{
								View view = group2.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}
							}
						}
					});
					localBuilder.setNegativeButton("Quitter ",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							CommercialActivity.this.finish();
							Intent intent = new Intent(CommercialActivity.this,ConnexionActivity.class);
							startActivity(intent);
						}
					}
							);
					localBuilder.create().show();

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
	
	private	class EnregistrationOfflineTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			
			if(!myoffline.checkFolderexsiste()){
	        	showmessageOffline();
	        	resu =getResources().getString(R.string.comerciallab4);  
			}else{
				database = new DatabaseHandler(getApplicationContext());
				client.setId((int)database.addrow("clt"));
				resu = myoffline.shynchronizeProspection(client,compte);
			}

			
			//wakelock.acquire();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}


		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					//Toast.makeText(CommercialActivity.this, resu, Toast.LENGTH_LONG).show();

					AlertDialog.Builder localBuilder = new AlertDialog.Builder(CommercialActivity.this);
					localBuilder
					.setMessage(resu)
					.setCancelable(false)
					.setPositiveButton("Retour",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							ViewGroup group = (ViewGroup)findViewById(R.id.layoutall);

							for (int i = 0, count = group.getChildCount(); i < count; ++i)
							{
								View view = group.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}

							}
							
							ViewGroup group2 = (ViewGroup)findViewById(R.id.comm_interface);

							for (int i = 0, count = group2.getChildCount(); i < count; ++i)
							{
								View view = group2.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}
							}
						}
					});
					localBuilder.setNegativeButton("Quitter ",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							CommercialActivity.this.finish();
							Intent intent = new Intent(CommercialActivity.this,ConnexionActivity.class);
							startActivity(intent);
						}
					}
							);
					localBuilder.create().show();

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

	 
	
	public void showmessageOffline(){
		try {
			 
	         LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.msgstorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(CommercialActivity.this);
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
				
				List<Produit> products = new ArrayList<>();
				List<Client> clients = new ArrayList<>();
				
				VendeurManager vendeurManager = VendeurManagerFactory.getClientManager();
				
				Dictionnaire dico  = new Dictionnaire();
				
				if(CheckOutNet.isNetworkConnected(getApplicationContext())){
					myoffline.SendOutData(compte);	
				}
				

				
				if(!myoffline.checkFolderexsiste()){
					showmessageOffline();
				}else{
					/*********************** offline ****************************************/
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
							myoffline.CleanProduits();
							myoffline.CleanPromotionProduit();
							myoffline.shynchronizeProduits(products);
							myoffline.shynchronizePromotion(vendeurManager.getPromotionProduits());	
						}
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						dico = vendeurManager.getDictionnaire();
						if(dico.getDico().size() > 0){
							myoffline.CleanDico();
							myoffline.shynchronizeDico(dico);	
						}
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						clients = vendeurManager.selectAllClient(compte);
						if(clients.size() > 0){
							myoffline.CleanClients();
							myoffline.CleanPromotionClient();
							myoffline.shynchronizeClients(clients);
							myoffline.shynchronizePromotionClient(vendeurManager.getPromotionClients());	
						}

					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						CommercialManager manager = CommercialManagerFactory.getCommercialManager();
						myoffline.CleanProspectData();
						myoffline.shynchronizeProspect(manager.getInfos(compte));	
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){

						PayementManager payemn = PayementManagerFactory.getPayementFactory();
						myoffline.CleanPayement();
						myoffline.shynchronizePayement(payemn.getFactures(compte));	
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
					
					dialog = ProgressDialog.show(CommercialActivity.this, getResources().getString(R.string.map_data),
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
				}
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CommercialActivity.this.finish();
			Intent intent1 = new Intent(CommercialActivity.this, VendeurActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
		}
		return false;
	}
	
	public List<String> checkRequiredFields(){
		
		// name;address;zip;tel;fax;email;
		List<String> res = new ArrayList<>();
		try {
			myoffline = new Offlineimpl(getApplicationContext());
			
			List<String> req = myoffline.LoadProspect("").getLsrequired();
		
			if(!req.contains("email"))req.add("email");
			if(!req.contains("name"))req.add("name");
			
	        for (int i = 0; i < req.size(); i++) {
	        	
	        	String st = req.get(i);
				if(st.equals("tel")){
						if(tel.getText().toString().length() == 0){
							res.add(getResources().getString(R.string.comerciallab7));
						}
				}
			
				if(st.equals("fax")){
					if(fax.getText().toString().length() == 0){
						res.add(getResources().getString(R.string.comerciallab8));
					}
				}

				if(st.equals("email")){
					if(email.getText().toString().length() == 0){
						res.add(getResources().getString(R.string.comerciallab9));
					}
				}
				
				/*
				if(st.equals("ville")){
					if(ville.getSelectedItemId() == 0){
						res.add("Ville du client");
					}
				}
				*/

				if(st.equals("address")){
					if(address.getText().toString().length() == 0){
						res.add(getResources().getString(R.string.comerciallab10));
					}
				}

				if(st.equals("zip")){
					if(zip.getText().toString().length() == 0){
						res.add(getResources().getString(R.string.comerciallab12));
					}
				}

				
				if(st.equals("name")){
					if(maVue.size() > 1){
						if(maVue.get(0).getText().toString().length() == 0 || maVue.get(1).getText().toString().length() ==0){
							res.add("Nom et prenom du client");
						}
					}else{
						if(maVue.get(0).getText().toString().length() == 0 ){
							res.add(getResources().getString(R.string.comerciallab11));
						}
					}
					
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("error data ",e.getMessage() +" << ");
		}

		
		return res;
		
	}
	
	
	
	public void alertinvonan(){
		
		List<String> req = checkRequiredFields();

		AlertDialog.Builder alert = new AlertDialog.Builder(CommercialActivity.this);
		alert.setTitle(R.string.caus21);
		
		String st ="";
		for (int i = 0; i < req.size(); i++) {
			st+= req.get(i)+"\n";
		}
		alert.setMessage(
				String.format(st
						));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
}
