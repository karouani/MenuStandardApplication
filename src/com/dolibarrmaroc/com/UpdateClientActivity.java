package com.dolibarrmaroc.com;


import java.util.ArrayList;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.models.Societe;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;

public class UpdateClientActivity extends Activity implements OnClickListener,OnItemSelectedListener,LocationListener{

	/**************************************** GPS DATA *****************************************/
	private LocationManager mLocationManager = null;
	private int LOCATION_INTERVAL = 1000;
	private float LOCATION_DISTANCE = 16;
	private double latitude;
	private double longitude;

	private LocationListener onLocationChange	= new LocationListener()
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

	private Spinner etat;
	private Spinner type;
	private EditText ville;

	private Button btn,suivant;
	private LinearLayout myLayout;

	private List<EditText> maVue;
	//private Spinner clientspinner,proSpinner;
	private AutoCompleteTextView clientspinner;
	private List<String> listclt;
	//Asynchrone avec connexion 
	private ProgressDialog dialog;
	private String resu ;
	private List<Societe> clients;
	private LinearLayout scroll;
	private Societe soc = new Societe();

	public UpdateClientActivity() {
		// TODO Auto-generated constructor stub
		manager = CommercialManagerFactory.getCommercialManager();
		compte = new Compte();
		gps = new GpsTracker();
		data = new ProspectData();
		client = new Prospection();
		listclt = new ArrayList<String>();
		clients = new ArrayList<Societe>();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();

		scroll.setVisibility(LinearLayout.INVISIBLE);

		dialog = ProgressDialog.show(UpdateClientActivity.this, "Récuperation Données",
				"Attendez SVP...", true);
		new ConnexionTask().execute();

		super.onStart();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_client);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		try {


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


			clientspinner =  (AutoCompleteTextView) findViewById(R.id.clientspinner);
			scroll = (LinearLayout) findViewById(R.id.malineaire);

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
			ville = (EditText) findViewById(R.id.comm_ville);

			btn = (Button) findViewById(R.id.comm_etape);
			btn.setOnClickListener(this);


			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}


			//clientspinner.setOnItemSelectedListener(this);
			clientspinner.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long arg3) {
					String selected = (String) parent.getItemAtPosition(position);
					client = new Prospection();
					clientspinner.showDropDown();

					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);

					clientspinner.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});

					for (int i = 0; i < clients.size(); i++) {
						if (selected.equals(clients.get(i).getName())) {
							soc =  clients.get(i);
							scroll.setVisibility(LinearLayout.VISIBLE);
							break;
						}

					}

					Log.e("Selected Client Spinner ",soc.toString());

					//name.setHint("Nom soci�t�");
					EditText name = new EditText(UpdateClientActivity.this);
					name.setText(soc.getName());
					name.setTag("comm_nome");
					name.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

					myLayout = (LinearLayout) findViewById(R.id.comm_interface);
					name.setWidth(myLayout.getWidth());
					myLayout.removeAllViews();
					myLayout.addView(name);

					maVue = new ArrayList<>();
					maVue.clear();
					maVue.add(name);
					client.setParticulier(0);
					

					if(parent.getId() == R.id.comm_type){
						
						Log.i("type", selected+" Positin "+position);
						if(position == 1){
							client.setClient(1);
							client.setProspect(0);
						}else{
							client.setClient(2);
							client.setProspect(1);
						}
					}
					
					if(soc.getCompany() == 1) client.setClient(1);
					
					if(soc.getTown() != null && !"null".equals(soc.getTown()))
					ville.setText(soc.getTown());
					else{
						ville.setHint("Ville");
					}
					if(soc.getAddress() != null && !"null".equals(soc.getAddress()))
					address.setText(soc.getAddress());
					else{
						address.setHint("addresse");
					}
					
					if(soc.getPhone() != null && !"null".equals(soc.getPhone()))
					tel.setText(soc.getPhone());
					else{
						tel.setHint("tel");
					}
					if(soc.getFax() != null && !"null".equals(soc.getFax()))
					fax.setText(soc.getFax());
					else{
						fax.setHint("Fax");
					}
					if(soc.getEmail() != null && !"null".equals(soc.getEmail()))
					email.setText(soc.getEmail());
					else{
						email.setHint("Email");
					}
					
				}
			});


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private	class ConnexionTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			clients = manager.getAll(compte);
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
					client.setCommercial_id(Integer.parseInt(compte.getIduser()));
					wakelock.release();

					for (int i = 0; i < clients.size(); i++) {
						listclt.add(clients.get(i).getName());
					}
					addItemsOnSpinner(clientspinner,listclt);
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	public void addItemsOnSpinner(AutoCompleteTextView clientspinner2,List<String> list) {

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listclt);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clientspinner2.setAdapter(dataAdapter);
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String selected = parent.getItemAtPosition(position).toString();

		if(parent.getId() == R.id.comm_ste){
			Log.i("Ste", selected+" Positin "+position);
			testeur = position;

			if (position == 0){

				//name.setHint("Nom Compléte");
				EditText firstname = new EditText(UpdateClientActivity.this);
				firstname.setHint("Le Nom");
				firstname.setTag("comm_firstname");

				firstname.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

				EditText lastname = new EditText(UpdateClientActivity.this);
				lastname.setHint("Le Prénom");
				lastname.setTag("comm_lasttname");

				lastname.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

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
				EditText name = new EditText(UpdateClientActivity.this);
				name.setHint("Nom société");
				name.setTag("comm_nome");
				name.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

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

		if("".equals(maVue != null) ||
				"".equals(tel.getText().toString())
				){
			Toast.makeText(UpdateClientActivity.this, "Tous les champs sont Obligatoir", Toast.LENGTH_LONG).show();
		}
		else{

			client.setName(maVue.get(0).getText().toString());

			if (soc.getLatitude() == null || soc.getLatitude() == 0D) {
				client.setLangitude(longitude);
				client.setLatitude(latitude);
			}

			client.setAddress(address.getText().toString());
			//client.setZip(zip.getText().toString());
			client.setPhone(tel.getText().toString());
			client.setFax(fax.getText().toString());
			client.setEmail(email.getText().toString());
			client.setStatus(1);
			client.setId(soc.getId());
			
			if (v.getId() == R.id.comm_etape) {
				dialog = ProgressDialog.show(UpdateClientActivity.this, "Enregistration",
						"Attendez SVP...", true);
				new EnregistrationTask().execute();
				//String res = manager.insert(compte, client);
				//Log.d("Client",client.toString());
			}

		}
	}




	private	class EnregistrationTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			resu = manager.update(compte, client);
			resu = "Ce client est mise à jour avec succées";
			wakelock.acquire();
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

					AlertDialog.Builder localBuilder = new AlertDialog.Builder(UpdateClientActivity.this);
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
							UpdateClientActivity.this.finish();
							Intent intent = new Intent(UpdateClientActivity.this,ConnexionActivity.class);
							startActivity(intent);
						}
					}
							);
					localBuilder.create().show();

					wakelock.release();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}


}
