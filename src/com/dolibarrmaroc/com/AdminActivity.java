package com.dolibarrmaroc.com;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.dolibarrmaroc.com.models.Compte;

public class AdminActivity extends TabActivity{

	private String lien;
	private Compte compte;
	private PowerManager.WakeLock wl;
	
	public AdminActivity() {
		// TODO Auto-generated constructor stub
		compte = new Compte();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
		
			

		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				lien = this.getIntent().getStringExtra("url");
				compte = (Compte) getIntent().getSerializableExtra("user");
			}

			TabHost tabHost = getTabHost();

			TabSpec admin= tabHost.newTabSpec("Login");       
			admin.setIndicator("Back Office" , getResources().getDrawable(R.drawable.sys_home));
			Intent browserIntent = new Intent(this, WebViewActivity.class);
			browserIntent.putExtra("lien", lien);
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



			// Adding all TabSpec to TabHost
			tabHost.addTab(songspec); // Adding songs tab
			tabHost.addTab(photospec); // Adding photos tab
			tabHost.addTab(admin);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

}
