package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dolibarrmaroc.com.database.DataErreur.DatabaseHandler;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;

public class SettingsynchroActivity extends Activity {

	private Compte compte;
	private Button btn1,btn2;
	
	private ioffline myoffline;
	
	private WakeLock wakelock;
	
	//database 
	private DatabaseHandler database;
	
	//Asynchrone avec connexion 
		private ProgressDialog dialogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingsynchro);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();

		Bundle objetbunble  = this.getIntent().getExtras();

		if (objetbunble != null) {
			compte =  (Compte) getIntent().getSerializableExtra("user");
		}
		
		btn1 = (Button)findViewById(R.id.setingdelcache);
		btn2 = (Button)findViewById(R.id.setingdelcache2);
		
		
		myoffline = new Offlineimpl(getApplicationContext());
		
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
				alertchachedel(0);
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				alertsynvh(0);
			}
		});
		
		database = new DatabaseHandler(getApplicationContext());
		
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new off1().execute();
	}
	
	class off1 extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			
			Log.e("Start 1","start 1");
			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			Log.e("end 1","start 1");
			new off2().execute();
		}

	}
	
	class off2 extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			Log.e("Start 2","start ");
			
			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			Log.e("end 2","start 2");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settingsynchro, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertchachedel(int n){
		try {
			Builder dialog = new AlertDialog.Builder(SettingsynchroActivity.this);
			if(n == 0){
				dialog.setMessage(R.string.caus13);
			}else{
				dialog.setMessage(R.string.caus14);
			}
			dialog.setTitle("Demande de confirmation");
			
			LayoutInflater inflater = this.getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.inputsetting, null);
	         
	         dialog.setView(dialogView);
	         dialog.setPositiveButton("Lancer votre action", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText txt = (EditText)dialogView.findViewById(R.id.inputpwd);
					if(txt != null){
						if(!txt.equals("")){
							if(compte.getPassword().equals(txt.getText().toString())){
								dialogin = ProgressDialog.show(SettingsynchroActivity.this, getResources().getString(R.string.data_clean),
										getResources().getString(R.string.msg_wait), true);
								myoffline.cleancache();
								if (dialogin.isShowing()){
									dialogin.dismiss();
								}
								SettingsynchroActivity.this.finish();
								Intent intent1 = new Intent(SettingsynchroActivity.this, ConnexionActivity.class);
								intent1.putExtra("user", compte);
								intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent1);
							}else{
								Toast.makeText(getApplicationContext(), "Votre code est invalide", Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "Votre code est invalide", Toast.LENGTH_LONG).show();
						}
					}
				}
			});
	         dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					dialog.cancel();
				}
			});
	         dialog.setCancelable(true);
	         dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("erreur",e.getMessage() +" << ");
		}
	}
	
	@SuppressLint("InlinedApi")
	public void alertsynvh(int n){
		try {
			Builder dialog = new AlertDialog.Builder(SettingsynchroActivity.this);
			dialog.setMessage(R.string.caus14);
			dialog.setTitle("Demande de confirmation");
			
			LayoutInflater inflater = this.getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.inputsetting, null);
	         
	         dialog.setView(dialogView);
	         dialog.setPositiveButton("Lancer votre action", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText txt = (EditText)dialogView.findViewById(R.id.inputpwd);
					if(txt != null){
						if(!txt.equals("")){
							if(compte.getPassword().equals(txt.getText().toString())){
								dialogin = ProgressDialog.show(SettingsynchroActivity.this, getResources().getString(R.string.data_clean),
										getResources().getString(R.string.msg_wait), true);

								myoffline.CleanProspection();
								
								List<Myinvoice> ms  = new ArrayList<>();
								ms = myoffline.LoadInvoice("");
								for (int i = 0; i < ms.size(); i++) {
									myoffline.updateProduitsInv(ms.get(i));
								}
								myoffline.CleanInvoice();
								myoffline.CleanReglement();
								myoffline.CleanPayementTicket();
								myoffline.CleanBluetooth();
								myoffline.CleanGpsInvoice();
								
								/*
								myoffline.CleanProspection();
								
								myoffline.CleanInvoice();
								myoffline.CleanReglement();
								
								myoffline.CleanPayementTicket();
								myoffline.CleanBluetooth();
								
								
								*/
								//myoffline.chargerInvoiceprospect(compte)
								//HashMap<Myinvoice, Prospection> ms = myoffline.chargerInvoiceprospect(compte);
								
								/*
								if(ms.size() > 0){
									for(Myinvoice m:ms.keySet()){
										//myoffline.synchronisationInvoiceOut(m, ms.get(m));
										//Log.e("inv>pros>> ",m.toString());
									}
								}
								*/
								
								/*
								HashMap<Myinvoice, String> msin = myoffline.chargerInvoiceclient();
								if(msin.size() > 0){
									for(Myinvoice m:msin.keySet()){
										//myoffline.testmeinvo(m, msin.get(m));
										//Log.e("inv>clt>> "+msin.get(m),m.toString());
									}
								}
								*/
								/*
								List<Reglement> reg = myoffline.chergerReglementClt(compte);
								for(Reglement r:reg){
									Log.e("reg >me> ",r.toString());
								}
								*/
								
								//myoffline.synchronisationReglementOut(compte);
								
								if (dialogin.isShowing()){
									dialogin.dismiss();
								}
								
								
								
								/*
								SettingsynchroActivity.this.finish();
								Intent intent1 = new Intent(SettingsynchroActivity.this, ConnexionActivity.class);
								intent1.putExtra("user", compte);
								intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent1);
								*/
							}else{
								Toast.makeText(getApplicationContext(), "Votre code est invalide", Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "Votre code est invalide", Toast.LENGTH_LONG).show();
						}
					}
				}
			});
	         dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					/*
					myoffline.CleanProspection();
					myoffline.CleanInvoice();
					myoffline.CleanReglement();
					myoffline.CleanPayementTicket();
					myoffline.CleanBluetooth();
					
					*/
					/*
					for(Reglement r:myoffline.LoadReglement("")){
						Log.e(">>> "+r.getId(),r.toString());
					}
					*/
					
					/*
					HashMap<String, HashMap<Prospection, List<Myinvoice>>> data = myoffline.chargerInvoice_prospect(compte);
					
					HashMap<Prospection, List<Myinvoice>> invo_ps = data.get("ps");
					
					HashMap<Myinvoice,String> invo_cl = myoffline.chargerInvoice_client(data, compte);
					
					Log.e("size ps ",invo_ps.size()+"");
					
					if(invo_ps.size() > 0){
						for(Myinvoice i:invo_cl.keySet()){
							Log.e("invo ps ",i.toString());
						}
					}
					
					
					for(Myinvoice r:myoffline.LoadInvoice("")){
						Log.e("r >> ",r.toString());
					}
					
					if(invo_cl.size() > 0){
						for(Prospection i:invo_ps.keySet()){
							Log.e("invo clt ",invo_ps.get(i).toString());
						}
					}
					*/
					
					//List<Reglement> reg = myoffline.synchronisationReglementOut(compte);
					//Log.e("reg >> ",reg.toString());
					
				//	HashMap<String, HashMap<Prospection, List<Myinvoice>>> data = myoffline.chargerInvoice_prospect(compte);
					
					//HashMap<Prospection, List<Myinvoice>> invo_ps = data.get("ps");
					
					//HashMap<Myinvoice,String> invo_cl = myoffline.chargerInvoice_client(data, compte);
					
					
					
					//Log.e(">>> ",myoffline.LoadGpsInvoice("").toString());
					//myoffline.SendOutData(compte);
					
					/*
					Intent intent1 = new Intent(SettingsynchroActivity.this, ViewcommandeActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					SettingsynchroActivity.this.finish();
					*/
					
					dialog.cancel();
				}
			});
	         dialog.setCancelable(true);
	         dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("erreur",e.getMessage() +" << ");
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SettingsynchroActivity.this.finish();
			Intent intent1 = new Intent(SettingsynchroActivity.this, VendeurActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
		}
		return false;
	}
}
