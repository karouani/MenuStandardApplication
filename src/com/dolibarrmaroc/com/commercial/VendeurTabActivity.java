package com.dolibarrmaroc.com.commercial;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.R.id;
import com.dolibarrmaroc.com.R.layout;
import com.dolibarrmaroc.com.R.menu;
import com.dolibarrmaroc.com.maps.MainActivity;
import com.dolibarrmaroc.com.models.Compte;


import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.os.Build;
import android.os.PowerManager.WakeLock;

@SuppressWarnings("deprecation")
public class VendeurTabActivity extends Activity{

	private Compte compte;
	private PowerManager.WakeLock wl;


	public VendeurTabActivity() {
		// TODO Auto-generated constructor stub
		compte = new Compte();

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		try {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");

			}
/*
			TabHost tabHost = getTabHost();

			TabSpec admin= tabHost.newTabSpec("Facture");       
			admin.setIndicator("Facture" , getResources().getDrawable(R.drawable.sys_clipboard));
			Intent browserIntent = new Intent(this, VendeurActivity.class);
			browserIntent.putExtra("user", compte);
			admin.setContent(browserIntent);

			// Tab for Photos
			TabSpec photospec = tabHost.newTabSpec("Carte").setIndicator("Carte",getResources().getDrawable(R.drawable.sys_local));
			// setting Title and Icon for the Tab
			//photospec.setIndicator("Photos", getResources().getDrawable(R.drawable.icon_photos_tab));
			Intent photosIntent = new Intent(this, MainActivity.class);
			photosIntent.putExtra("user", compte);
			photospec.setContent(photosIntent);

			// Tab for Songs
			TabSpec songspec = tabHost.newTabSpec("Configuration");       
			songspec.setIndicator("Config", getResources().getDrawable(R.drawable.sys_settings));
			Intent songsIntent = new Intent(this, TrackingActivity.class);
			songsIntent.putExtra("user", compte);
			songspec.setContent(songsIntent);



			//tabHost.addTab(songspec); //COnfiguration
			tabHost.addTab(admin); // Facture
			tabHost.addTab(photospec); // Maps
*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vendeur_tab, menu);

        return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPause() {
		super.onPause();
		wl.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		wl.acquire();
	}

	private void createGpsDisabledAlert() {


		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder
		.setMessage("Le GPS est inactif, voulez-vous l'activer ?")
		.setCancelable(false)
		.setPositiveButton("Activer GPS ",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				VendeurTabActivity.this.showGpsOptions();
			}
		}
				);
		localBuilder.setNegativeButton("Ne pas l'activer ",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.cancel();
				VendeurTabActivity.this.finish();
			}
		}
				);
		localBuilder.create().show();

	}

	private void showGpsOptions() {
		startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/************************MENU Client *****************************************/
		case R.id.pointerClt:
			Intent photosIntent = new Intent(this, MainActivity.class);
			photosIntent.putExtra("user", compte);
			startActivity(photosIntent);
			break;
		/************************MENU FACTURE *****************************************/
		case R.id.pointerFact:
			Intent facture = new Intent(this, VendeurActivity.class);
			facture.putExtra("user", compte);
			startActivity(facture);
			break;
		}
		return true;
		
	}
}