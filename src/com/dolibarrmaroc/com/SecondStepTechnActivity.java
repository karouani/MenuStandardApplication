package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;

public class SecondStepTechnActivity extends Activity implements
OnClickListener{

	// Widget GUI
	private Button btnCalendar, btnTimePickerD,btnTimePickerF,nextstep;
	private EditText txtDate, txtTimeD,txtTimeF,nom;

	// Variable for storing current date and time
	private int mYear, mMonth, mDay, mHour, mMinute;
	private Calendar c ;
	
	private Compte compte;
	private Client clt;
	private String objet;
	
	private boolean hd = false;
	private boolean hf = false;
	
	//Recuperation Interface de l'utilisateur Par Service
	private String nmb;
	private String serviceName;
	private List<String> labels;
	
	private int heurDebutEnSecond,heurFinEnSecond;
	
	/** Called when the activity is first created. */

	public SecondStepTechnActivity() {
		
		c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		compte = new Compte();
		labels = new ArrayList<>();
		
		clt = new Client();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_step_techn);//newtest

		try {
			btnCalendar = (Button) findViewById(R.id.btnCalendar);
			btnTimePickerF = (Button) findViewById(R.id.btnTimePickerF);
			btnTimePickerD = (Button) findViewById(R.id.btnTimePickerD);
			nextstep = (Button) findViewById(R.id.nextStepTech);
			
			txtDate = (EditText) findViewById(R.id.txtDate);
			txtTimeF = (EditText) findViewById(R.id.txtTimeF);
			txtTimeD = (EditText) findViewById(R.id.txtTimeD);
			nom = (EditText) findViewById(R.id.nameOfTechn);

			btnCalendar.setOnClickListener(this);
			btnTimePickerF.setOnClickListener(this);
			btnTimePickerD.setOnClickListener(this);
			nextstep.setOnClickListener(this);
			
			// Remplir Valeur Par defaut
			txtDate.setText(mDay + "-"
					+ (mMonth + 1) + "-" + mYear);
			
			if(mMinute < 10){
				txtTimeD.setText(mHour + ":0" + mMinute);
				txtTimeF.setText(mHour + ":0" + mMinute);
			}else{
				txtTimeD.setText(mHour + ":" + mMinute);
				txtTimeF.setText(mHour + ":" + mMinute);
			}
			
			
			if(mHour == 0) mHour = 24;
			heurFinEnSecond = mHour*3600 + mMinute * 60;
			heurDebutEnSecond = mHour*3600 + mMinute * 60;
			
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				nmb = this.getIntent().getStringExtra("nmbService");
				serviceName = this.getIntent().getStringExtra("service");
				clt = (Client) this.getIntent().getSerializableExtra("client");
				objet =  this.getIntent().getStringExtra("objet");
				
				for (int i = 0; i < Integer.parseInt(nmb); i++) {
					labels.add(this.getIntent().getStringExtra("labels"+i));
				}
				Log.e(">> Service Labels",labels.toString());
				Log.e(">> Selection >>> ","Objet est : "+objet+"\n\r Client : "+clt.toString());
			}
			
			
			String languageToLoad  = "fr"; // your language
		    Locale locale = new Locale(languageToLoad); 
		    Locale.setDefault(locale);
		    Configuration config = new Configuration();
		    config.locale = locale;
		    getBaseContext().getResources().updateConfiguration(config, 
		      getBaseContext().getResources().getDisplayMetrics());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {

		if (v == btnCalendar) {
			// Launch Date Picker Dialog
			DatePickerDialog dpd = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					// Display Selected date in textbox
					txtDate.setText(dayOfMonth + "-"
							+ (monthOfYear + 1) + "-" + year);

				}
			}, mYear, mMonth, mDay);
			dpd.show();
			
		}
		
	
		if (v == btnTimePickerD) {

			// Launch Time Picker Dialog
			TimePickerDialog tpd = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay,
						int minute) {
					// Display Selected time in textbox
					if(minute < 10){
						txtTimeD.setText(hourOfDay + ":0" + minute);
					}else{
						txtTimeD.setText(hourOfDay + ":" + minute);
					}
					
					if(hourOfDay == 0) hourOfDay = 24;
					heurDebutEnSecond = hourOfDay*3600 + minute * 60;
					hd = true;
				}
			}, mHour, mMinute, false);
			tpd.show();
		}
		if (v == btnTimePickerF) {

			// Launch Time Picker Dialog
			TimePickerDialog tpf = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay,
						int minute) {
					// Display Selected time in textbox
					if(minute < 10){
						txtTimeF.setText(hourOfDay + ":0" + minute);
					}else{
						txtTimeF.setText(hourOfDay + ":" + minute);
					}
					

					if(hourOfDay == 0) hourOfDay = 24;
					heurFinEnSecond = hourOfDay*3600 + minute * 60;
					hf = true;
				}
			}, mHour, mMinute, false);
			tpf.show();
		}
		if(v == nextstep){
			
			String chd = txtTimeD.getText().toString();
			String chf = txtTimeF.getText().toString();
			
			if(chd.contains(":") && chf.contains(":") && (chd.length() <= 5) && (chf.length() <= 5)){
				String magana[] = txtTimeD.getText().toString().split(":");
				String tarikh[] = txtDate.getText().toString().split("-");
				
				if(hd && hf){
					Log.e("la date >>> ",txtDate.getText().toString() + " # "+ heurDebutEnSecond+ " # "+heurFinEnSecond + " # "+Arrays.toString(magana) + " # "+ Arrays.toString(tarikh));
				}else{
					
					int h =24;
					
					String hdd[] = txtTimeD.getText().toString().split(":");
					if(Integer.parseInt(hdd[0]) == 0){
						h = 24;
					}else{
						h = Integer.parseInt(hdd[0]);
					}
					heurDebutEnSecond = h*3600 + Integer.parseInt(hdd[1]) * 60;
					
					String hff[] = txtTimeF.getText().toString().split(":");
					if(Integer.parseInt(hff[0]) == 0){
						h = 24;
					}else{
						h = Integer.parseInt(hff[0]);
					}
					heurFinEnSecond = h*3600 + Integer.parseInt(hff[1]) * 60;
					
					Log.e("la date false >>> ",txtDate.getText().toString() + " # "+ txtTimeD.getText().toString()+ " # "+txtTimeF.getText().toString() + " # "+Arrays.toString(magana) + " # "+ Arrays.toString(tarikh)+ " # "+ heurDebutEnSecond+ " # "+heurFinEnSecond);
				}
				
				
				if(heurDebutEnSecond <= heurFinEnSecond){
					Intent intent = new Intent(this, InterfaceTechnicienActivity.class);
					
					intent.putExtra("user", compte);
					intent.putExtra("service", serviceName);
					intent.putExtra("nmbService", nmb);
					intent.putExtra("objet", objet);
					intent.putExtra("client", clt);
					intent.putExtra("Superviseur", nom.getText().toString());
					
					for (int i = 0; i < Integer.parseInt(nmb); i++) {
						intent.putExtra("labels"+i, labels.get(i).toString());
					}
					
					
					intent.putExtra("date", txtDate.getText().toString());
					intent.putExtra("timed", heurDebutEnSecond+"");
					intent.putExtra("timef", heurFinEnSecond+"");
					intent.putExtra("heurD",  magana[0]);
					intent.putExtra("minD",  magana[1]);
					intent.putExtra("year", tarikh[2]);
					intent.putExtra("month",tarikh[1]);
					intent.putExtra("day", tarikh[0]);
						
					startActivity(intent);
				}else{
					Toast.makeText(SecondStepTechnActivity.this, "V�rifi� l'heur de fin", Toast.LENGTH_LONG).show();
				}
				
				
			}else{
				Toast.makeText(SecondStepTechnActivity.this, "Le format de votre temps est incompatible", Toast.LENGTH_LONG).show();
			}
			
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
	
}
