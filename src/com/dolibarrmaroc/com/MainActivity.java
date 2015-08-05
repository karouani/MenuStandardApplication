package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android2ee.formation.librairies.google.map.utils.direction.DCACallBack;
import com.android2ee.formation.librairies.google.map.utils.direction.GDirectionsApiUtils;
import com.android2ee.formation.librairies.google.map.utils.direction.model.GDirection;
import com.dolibarrmaroc.com.business.FactureManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.FactureGps;
import com.dolibarrmaroc.com.utils.FactureManagerFactory;
import com.dolibarrmaroc.com.utils.ForcerActivationGps;
import com.dolibarrmaroc.com.utils.MyLocationListener;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


@SuppressLint("NewApi")
public class MainActivity extends android.support.v4.app.FragmentActivity implements
ConnectionCallbacks,
OnConnectionFailedListener,
LocationListener,
OnMyLocationButtonClickListener,DCACallBack{
	
	//FOrcer Activation GPS
	private ForcerActivationGps forcer;
	
	/*************************** Variables ******************************************************/
	//Declaration Objet
	private VendeurManager vendeurManager;
	private FactureManager factureManager;

	private Compte compte;
	private FactureGps facture;
	private Client client;
	private Client clientLocation;
	
	//INTERFACTE UI
	private Button factbtn;
	private Button clientbtn;
	private Spinner clientspinner,facturespinner;
	private AutoCompleteTextView factcomplete;

	//Spinner Remplissage
	private List<String> listclt;
	private List<String> listfact;
	private List<FactureGps> factures;
	private List<Client> clients;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;

	//Dialogue Button
	private Dialog dialogbtnfact,dialogbtnclt;

	//CE QUI CONCERN MAP
	private LocationClient mLocationClient;

	private GoogleMap map;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private LatLng myPosition;
	private UiSettings mUiSettings;

	private List<MarkerOptions> mesPositions;
	private LatLng myfact,myClient;

	private WakeLock wakelock;
	
	private int zoom = 11;
	
	/*************************** METHOD ******************************************************/

	public MainActivity() {
		vendeurManager = VendeurManagerFactory.getClientManager();
		factureManager = FactureManagerFactory.getFactureManager();

		listclt = new ArrayList<String>();
		listfact = new ArrayList<String>();

		factures = new ArrayList<>();
		clients = new ArrayList<>();

		facture = new FactureGps();
		client = new Client();

		mesPositions = new ArrayList<>();
	}

	@Override
	protected void onStart() {
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();

		dialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.map_data),
				getResources().getString(R.string.msg_wait), true);
		new TrackingMapTask().execute();

		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			dialogbtnfact = new Dialog(this);
			dialogbtnfact.setContentView(R.layout.facturelayout);
			dialogbtnfact.setTitle(getResources().getString(R.string.facture_action));

			dialogbtnclt = new Dialog(this);
			dialogbtnclt.setContentView(R.layout.clientlayout);
			dialogbtnclt.setTitle(getResources().getString(R.string.client_action));

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			if(isNetworkConnected(this)){
				//map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

				//map.setMyLocationEnabled(true);
				//mUiSettings = map.getUiSettings();

				getGpsApplicationAlert();
				
				/*
				if (map == null) {
					Toast.makeText(this, "Google Maps not available", 
							Toast.LENGTH_LONG).show();
				}
				*/
				
				SupportMapFragment mapFragment =
			            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

			    if (savedInstanceState == null) {
			        // First incarnation of this activity.
			        mapFragment.setRetainInstance(true);
			    } else {
			        // Reincarnated activity. The obtained map is the same map instance in the previous
			        // activity life cycle. There is no need to reinitialize it.
			        map = mapFragment.getMap();
			    }
			    setUpMapIfNeeded();
			    
				Bundle objetbunble  = this.getIntent().getExtras();

				if (objetbunble != null) {
					compte = (Compte) getIntent().getSerializableExtra("user");

				}
				
				/*
				clientbtn = (Button) findViewById(R.id.pointerClt);
				factbtn = (Button) findViewById(R.id.pointerFact);
				clientbtn.setOnClickListener(this);
				factbtn.setOnClickListener(this);
				*/

			}else{
				erreurNetwork();
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

	public boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected());
	}


	public void getGpsApplicationAlert(){

		LocationManager mlocManager=null;
		android.location.LocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			createGpsDisabledAlert();
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
				MainActivity.this.showGpsOptions();
				//forcer.turnGPSOn();
			}
		}
				);
		localBuilder.setNegativeButton(getResources().getString(R.string.btn_gps_deactiver),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.cancel();
				MainActivity.this.finish();
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
				MainActivity.this.finish();
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

	private void showGpsOptions() {
		startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				//map.setMyLocationEnabled(true);
				mUiSettings = map.getUiSettings();

				map.setMyLocationEnabled(true);
				map.setOnMyLocationButtonClickListener(this);
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(
					getApplicationContext(),
					this,  // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	/**
	 * Button to get current Location. This demonstrates how to get the current Location as required
	 * without needing to register a LocationListener.
	 */
	public void showMyLocation(View view) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {
		myPosition = new LatLng(location.getLatitude(), location.getLongitude());
		/*if(myfact.longitude != 0 || myClient.latitude != 0 ){
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(myPosition)
			.zoom(zoom)// Sets the zoom
			.build();    // Creates a CameraPosition from the builder
			map.animateCamera(CameraUpdateFactory.newCameraPosition(
					cameraPosition));
		}
		*/
		
	}

	/**
	 * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(
				REQUEST,
				this);  // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onDisconnected() {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	class TrackingMapTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			factures = factureManager.listFacture(compte);

			for (int i = 0; i < factures.size(); i++) {
				FactureGps f = new FactureGps();
				f = factures.get(i);
				listfact.add(f.getNumero());
			}

			clients = vendeurManager.selectAllClient(compte);
			for (int i = 0; i < clients.size(); i++) {
				listclt.add(clients.get(i).getName());
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
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}

	private void hideSoftKeyboard() {
		if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText)
		{
			InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
		}

	}

	/*
	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.pointerClt){
			clientspinner = (Spinner) dialogbtnclt.findViewById(R.id.produitpointer);

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listclt);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			clientspinner.setAdapter(dataAdapter);

			Log.i("Clients ", clients.toString());
			Log.d("List pour spinner ", listclt.toString());
			
			Button annul = (Button) dialogbtnclt.findViewById(R.id.annulershowme); 
			annul.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogbtnclt.dismiss();
				}
			});
			
			dialogbtnclt.show();

		}else if(v.getId() == R.id.pointerFact){
			//facturespinner= (Spinner) dialogbtnfact.findViewById(R.id.facturepointer);
			factcomplete = (AutoCompleteTextView) dialogbtnfact.findViewById(R.id.facturepointer);
			
			if(!factcomplete.hasFocus()){
				hideSoftKeyboard();
			}


			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,

					android.R.layout.simple_spinner_item, listfact);
			dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

			//facturespinner.setAdapter(dataAdapter);
			factcomplete.setAdapter(dataAdapter);
			factcomplete.setThreshold(1);
			factcomplete.setTextColor(Color.RED); 
			factcomplete.setOnItemClickListener(new OnItemClickListener() {


				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					factcomplete.showDropDown();
					String selected = (String) parent.getItemAtPosition(position);
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);

					factcomplete.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});
					//Toast.makeText(MainActivity.this, selected, Toast.LENGTH_LONG).show();

					for (int i = 0; i < factures.size(); i++) {
						if(selected.equals(factures.get(i).getNumero())){
							facture = factures.get(i);
							break;
						}
					}
					//factcomplete.setInputType(InputType.TYPE_NULL);
				}
			});

			Button showme = (Button) dialogbtnfact.findViewById(R.id.factshowme);
			showme.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if(Double.parseDouble(facture.getLat()) > 0){
						myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					}else{
						myfact = myPosition;
					}
					

					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myfact)
					.zoom(10)// Sets the zoom
					.build();    // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(
							cameraPosition));


					MarkerOptions markMe = new MarkerOptions().position(myfact).title("Facture Numero :"+facture.getNumero())
							.icon(BitmapDescriptorFactory.defaultMarker(
									BitmapDescriptorFactory.HUE_YELLOW));
					/*
					 *	map.addMarker(markMe);
					 */

		/*
					mesPositions.add(markMe);
					clearMap(map);

					dialogbtnfact.dismiss();
				}
			});

			Button itinerer = (Button) dialogbtnfact.findViewById(R.id.itenermoifact);
			itinerer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					
					if(Double.parseDouble(facture.getLat()) > 0){
						myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					}else{
						myfact = myPosition;
					}
					
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myfact)
					.zoom(6)// Sets the zoom
					.build();    // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(
							cameraPosition));


					MarkerOptions markMe = new MarkerOptions().position(myfact).title("Facture Numero :"+facture.getNumero())
							.icon(BitmapDescriptorFactory.defaultMarker(
									BitmapDescriptorFactory.HUE_YELLOW));
					/*
					 *	map.addMarker(markMe);
					 */

/*
					mesPositions.add(markMe);
					clearMap(map);

					getDirections(myPosition,myfact);
					dialogbtnfact.dismiss();
				}
			});
			
			Button annul = (Button) dialogbtnfact.findViewById(R.id.annulershowme); 
			annul.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogbtnfact.dismiss();
				}
			});
			dialogbtnfact.show();
		}
	}
*/
	public void clearMap(GoogleMap myMap){
		myMap.clear();

		LatLngBounds newarkBounds = new LatLngBounds(
				new LatLng(21.3381506, -16.9961064),       // South west corner
				new LatLng(28.6727069, -8.6560507));      // North east corner
		GroundOverlayOptions newarkMap = new GroundOverlayOptions()
		.image(BitmapDescriptorFactory.fromResource(R.drawable.sahara))
		.positionFromBounds(newarkBounds);
		myMap.getUiSettings().setCompassEnabled(true);
		//Log.d("z index ", newarkMap.getZIndex());
		myMap.addGroundOverlay(newarkMap);
		mUiSettings.setMyLocationButtonEnabled(true);

		if(mesPositions.size() > 0){
			for (int i = 0; i < mesPositions.size(); i++) {
				map.addMarker(mesPositions.get(i));
			}
		}
	}

	private void getDirections(LatLng mypos,LatLng point) {
		GDirectionsApiUtils.getDirection(this, mypos, point, GDirectionsApiUtils.MODE_DRIVING);
	}

	@Override
	public void onDirectionLoaded(List<GDirection> directions) {
		for(GDirection direction:directions) {
			GDirectionsApiUtils.drawGDirection(direction, map);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/************************MENU Client *****************************************/
		case R.id.pointerClt:
			clientspinner = (Spinner) dialogbtnclt.findViewById(R.id.produitpointer);

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listclt);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			clientspinner.setAdapter(dataAdapter);

			Log.i("Clients ", clients.toString());
			Log.d("List pour spinner ", listclt.toString());
			
			Button annul = (Button) dialogbtnclt.findViewById(R.id.annulershowme); 
			annul.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogbtnclt.dismiss();
				}
			});
			
			clientspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View arg1,int pos, long arg3) {
					String selected = parent.getItemAtPosition(pos).toString();
					
					for (int i = 0; i < clients.size(); i++) {
						if(selected.equals(clients.get(i).getName())){
							clientLocation = clients.get(i);
							Log.e("Client selected",clientLocation.toString());
							break;
						}
					}
					/*
					for (Client clt : clients) {
						if(selected.equals(clt.getName())){
							clientLocation = clt;
							Log.e("Client selected",clt.toString());
							break;
						}
					}
					*/
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			Button showm = (Button) dialogbtnclt.findViewById(R.id.clientshowme);
			showm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Log.e("Client Location",clientLocation.toString());
					if(clientLocation.getLatitude() != 0){
						myClient = new LatLng(clientLocation.getLongitude(),clientLocation.getLatitude());
					}else{
						myClient = myPosition;
					}
					

					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myClient)
					.zoom(zoom)// Sets the zoom
					.build();    // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(
							cameraPosition));


					MarkerOptions markMe = new MarkerOptions().position(myClient).title(clientLocation.getName())
							.icon(BitmapDescriptorFactory.defaultMarker(
									BitmapDescriptorFactory.HUE_GREEN));
					/*
					 *	map.addMarker(markMe);
					 */

	
					mesPositions.add(markMe);
					clearMap(map);

					dialogbtnclt.dismiss();
				}
			});
			
			Button itinere = (Button) dialogbtnclt.findViewById(R.id.itenermoiclient);
			itinere.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					
					if(clientLocation.getLatitude() != 0){
						myClient = new LatLng(clientLocation.getLongitude(),clientLocation.getLatitude());
					}else{
						myClient = myPosition;
					}
					
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myClient)
					.zoom(zoom)// Sets the zoom
					.build();    // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(
							cameraPosition));


					MarkerOptions markMe = new MarkerOptions().position(myClient).title(clientLocation.getName())
							.icon(BitmapDescriptorFactory.defaultMarker(
									BitmapDescriptorFactory.HUE_YELLOW));
					/*
					 *	map.addMarker(markMe);
					 */

					mesPositions.add(markMe);
					clearMap(map);

					getDirections(myPosition,myClient);
					dialogbtnclt.dismiss();
					
					
				}
			});
			
			dialogbtnclt.show();
			break;
			
		/************************MENU FACTURE *****************************************/
		case R.id.pointerFact:
			//facturespinner= (Spinner) dialogbtnfact.findViewById(R.id.facturepointer);
			factcomplete = (AutoCompleteTextView) dialogbtnfact.findViewById(R.id.autocomplate);
			
			if(!factcomplete.hasFocus()){
				hideSoftKeyboard();
			}


			ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,

					android.R.layout.simple_spinner_item, listfact);
			dataAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

			//facturespinner.setAdapter(dataAdapter);
			factcomplete.setAdapter(dataAdapter1);
			factcomplete.setThreshold(1);
			factcomplete.setTextColor(Color.RED); 
			factcomplete.setOnItemClickListener(new OnItemClickListener() {


				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					factcomplete.showDropDown();
					String selected = (String) parent.getItemAtPosition(position);
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);

					factcomplete.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});
					//Toast.makeText(MainActivity.this, selected, Toast.LENGTH_LONG).show();

					for (int i = 0; i < factures.size(); i++) {
						if(selected.equals(factures.get(i).getNumero())){
							facture = factures.get(i);
							break;
						}
					}
					//factcomplete.setInputType(InputType.TYPE_NULL);
				}
			});

			Button showme = (Button) dialogbtnfact.findViewById(R.id.factshowme);
			showme.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if(Double.parseDouble(facture.getLat()) > 0){
						myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					}else{
						myfact = myPosition;
					}
					

					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myfact)
					.zoom(zoom)// Sets the zoom
					.build();    // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(
							cameraPosition));


					MarkerOptions markMe = new MarkerOptions().position(myfact).title(getResources().getString(R.string.facture_num)+facture.getNumero())
							.icon(BitmapDescriptorFactory.defaultMarker(
									BitmapDescriptorFactory.HUE_YELLOW));
					/*
					 *	map.addMarker(markMe);
					 */

	
					mesPositions.add(markMe);
					clearMap(map);

					dialogbtnfact.dismiss();
				}
			});

			Button itinerer = (Button) dialogbtnfact.findViewById(R.id.itenermoifact);
			itinerer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					
					if(Double.parseDouble(facture.getLat()) > 0){
						myfact = new LatLng(Double.parseDouble(facture.getLat()), Double.parseDouble(facture.getLng()));
					}else{
						myfact = myPosition;
					}
					
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(myfact)
					.zoom(10)// Sets the zoom
					.build();    // Creates a CameraPosition from the builder
					map.animateCamera(CameraUpdateFactory.newCameraPosition(
							cameraPosition));


					MarkerOptions markMe = new MarkerOptions().position(myfact).title("Facture Numero :"+facture.getNumero())
							.icon(BitmapDescriptorFactory.defaultMarker(
									BitmapDescriptorFactory.HUE_YELLOW));
					/*
					 *	map.addMarker(markMe);
					 */

					mesPositions.add(markMe);
					clearMap(map);

					getDirections(myPosition,myfact);
					dialogbtnfact.dismiss();
					
					
				}
			});
			
			Button annul1 = (Button) dialogbtnfact.findViewById(R.id.annulershowme); 
			annul1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogbtnfact.dismiss();
				}
			});
			dialogbtnfact.show();
			break;
		}
		return true;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
	}
}
