package com.dolibarrmaroc.com;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;

public class ObservationTechActivity extends Activity implements OnClickListener{
	
	private Compte compte;
	private Client clt;
	private String objet;

	//Recuperation Interface de l'utilisateur Par Service
	private String description,superviseur;
	private String date;
	private String timeD;
	private String timeF;

	//Interface User
	private LinearLayout myLayout;

	private EditText numFiche;
	private EditText observation;
	private Button valider,annuler;
	private String mYear, mMonth, mDay, mHour, mMinute;
	
	public ObservationTechActivity() {
		compte = new Compte();
		clt = new Client();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_observation_tech);

		try {
			numFiche = (EditText) findViewById(R.id.ficheNum);
			observation = (EditText) findViewById(R.id.observationtech);
			
			valider = (Button) findViewById(R.id.validerfiche);
			annuler = (Button) findViewById(R.id.annulerfiche);
			
			valider.setOnClickListener(this);
			annuler.setOnClickListener(this);
			
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				date = this.getIntent().getStringExtra("date");
				timeD = this.getIntent().getStringExtra("timed");
				timeF = this.getIntent().getStringExtra("timef");
				
				mYear = this.getIntent().getStringExtra("year");
				mMonth = this.getIntent().getStringExtra("month");
				mDay= this.getIntent().getStringExtra("day");
				mHour= this.getIntent().getStringExtra("heurD");
				mMinute= this.getIntent().getStringExtra("minD");
				
				superviseur =  this.getIntent().getStringExtra("Superviseur");
				
				clt = (Client) this.getIntent().getSerializableExtra("client");
				objet =  this.getIntent().getStringExtra("objet");
				description =  this.getIntent().getStringExtra("description");
			}

			myLayout = (LinearLayout) findViewById(R.id.recapitule);
			myLayout.removeAllViews();
			myLayout.setPadding(15, 5, 15, 5);

			TextView client = new TextView(this);
			TextView dateEf = new TextView(this);
			TextView duree = new TextView(this);
			TextView operation = new TextView(this);

			int s = Integer.parseInt(timeF) - Integer.parseInt(timeD);

			//int annee   = s / 60 / 60 / 24 / 365;
			//int jour    = s / 60 / 60 / 24 % 365;
			int heure   = s / 60 / 60 % 24;
			int minute  = s / 60 % 60;
			//int seconde = s % 60;
			
			String wa9t ;
			if(minute < 10){
				if(heure <10){
					wa9t = "0"+heure+" : 0"+minute;
				}
				wa9t = heure+" : 0"+minute;
			}else{
				if(heure <10){
					wa9t = "0"+heure+" : 0"+minute;
				}
				wa9t = heure+" : "+minute;
			}
			
			String lan = Locale.getDefault().getLanguage();
			
			
			
			String cl = getResources().getString(R.string.tecv21);
			String dt=getResources().getString(R.string.tecv22);
			String dr=getResources().getString(R.string.tecv23);
			
			
			client.setText(cl+clt.getName());
			dateEf.setText(dt+date);
			duree.setText(dr+wa9t);
			operation.setText(description);

			myLayout.addView(client);
			myLayout.addView(dateEf);
			myLayout.addView(duree);
			myLayout.addView(operation);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if(v == valider){
			if(!"".equals(numFiche.getText().toString())){
				Intent intent = new Intent(this, CameraActivity.class);

				intent.putExtra("user", compte);
				intent.putExtra("objet", objet);
				intent.putExtra("client", clt);

				intent.putExtra("date", date);
				intent.putExtra("timed", timeD);
				intent.putExtra("timef", timeF);
				
				intent.putExtra("heurD",  mHour);
				intent.putExtra("minD",  mMinute);
				intent.putExtra("year", mYear);
				intent.putExtra("month",mMonth);
				intent.putExtra("day", mDay);
				
				intent.putExtra("Superviseur", superviseur);
				
				intent.putExtra("fiche", numFiche.getText().toString());
				
				intent.putExtra("description", description+" \n\r "+" Constat : "+observation.getText()
						.toString());

				startActivity(intent);
				ObservationTechActivity.this.finish();
			}else{
				Toast.makeText(this, "Il faut renseigner Numero de la Fiche ", Toast.LENGTH_LONG).show();
			}
		}else if(v == annuler){
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.tecv36))
			.setMessage(getResources().getString(R.string.tecv47))
			.setNegativeButton(R.string.tecv16, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					ds.dismiss();
					
				}

			})
			.setPositiveButton(R.string.tecv15, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					Intent intent1 = new Intent(ObservationTechActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					ObservationTechActivity.this.finish();
					
				}

			}).setCancelable(true)
			.create().show();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.tecv36))
			.setMessage(getResources().getString(R.string.tecv47))
			.setNegativeButton(R.string.tecv16, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					ds.dismiss();
					
				}

			})
			.setPositiveButton(R.string.tecv15, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					Intent intent1 = new Intent(ObservationTechActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					ObservationTechActivity.this.finish();
					
				}

			}).setCancelable(true)
			.create().show();
			return true;
		}
		return false;
	}
}
