package com.dolibarrmaroc.com;


import android.app.Activity;
import android.os.Bundle;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.utils.ServiceDao;

public class GpsFactureActivity extends Activity {

	private GpsTracker gps;
	private ServiceDao daoGps;
	private Compte compte;
	
	public GpsFactureActivity() {
		 daoGps = new ServiceDao();
		 compte = new Compte();
		 gps = new GpsTracker();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_facture);

		
	}

}
