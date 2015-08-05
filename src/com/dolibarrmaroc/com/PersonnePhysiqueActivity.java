package com.dolibarrmaroc.com;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.dolibarrmaroc.com.models.Prospection;

public class PersonnePhysiqueActivity extends Activity {
	
	private Prospection client;
	
	public PersonnePhysiqueActivity() {
		// TODO Auto-generated constructor stub
		client = new Prospection();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personne_physique);
		
		Bundle objetbunble  = this.getIntent().getExtras();

		if (objetbunble != null) {
			client = (Prospection) getIntent().getSerializableExtra("client");
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personne_physique, menu);
		return true;
	}


}
