/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dashboard.DashboardActivity;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.DBHandler;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;
import com.karouani.cicin.widget.alert.AlertDialogList;



/**
 * This is a simple activity that demonstrates the dashboard user interface pattern.
 *
 */

public class HomeActivity extends DashboardActivity 
{

	/**
	 * onCreate - called when the activity is first created.
	 * Called when the activity is first created. 
	 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
	 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
	 * 
	 * Always followed by onStart().
	 *
	 */
	//Declaration Objet
	private VendeurManager vendeurManager;
	private StockVirtual sv;
	private List<Produit> products;
	private Compte compte;
	private ProgressDialog dialogSynchronisation;
	//synchro offline
	private Offlineimpl myoffline;
	private Dictionnaire dico;
	//Spinner Remplissage
	private List<String> listclt;
	private List<String> listprd;
	private List<Client> clients;

	private DBHandler mydb ;
	private WakeLock wakelock;
	
	public HomeActivity() {
		// TODO Auto-generated constructor stub
		vendeurManager = VendeurManagerFactory.getClientManager();
		products = new ArrayList<Produit>();
		dico = new Dictionnaire();

		listclt = new ArrayList<String>();
		listprd = new ArrayList<String>();
		clients = new ArrayList<Client>();
	}

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mydb = new DBHandler(this);

		Bundle objetbunble  = this.getIntent().getExtras();

		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
		}
	}

	/**
	 * onDestroy
	 * The final call you receive before your activity is destroyed. 
	 * This can happen either because the activity is finishing (someone called finish() on it, 
	 * or because the system is temporarily destroying this instance of the activity to save space. 
	 * You can distinguish between these two scenarios with the isFinishing() method.
	 *
	 */

	protected void onDestroy ()
	{
		super.onDestroy ();
	}

	/**
	 * onPause
	 * Called when the system is about to start resuming a previous activity. 
	 * This is typically used to commit unsaved changes to persistent data, stop animations 
	 * and other things that may be consuming CPU, etc. 
	 * Implementations of this method must be very quick because the next activity will not be resumed 
	 * until this method returns.
	 * Followed by either onResume() if the activity returns back to the front, 
	 * or onStop() if it becomes invisible to the user.
	 *
	 */

	protected void onPause ()
	{
		super.onPause ();
	}

	/**
	 * onRestart
	 * Called after your activity has been stopped, prior to it being started again.
	 * Always followed by onStart().
	 *
	 */

	protected void onRestart ()
	{
		super.onRestart ();
	}

	/**
	 * onResume
	 * Called when the activity will start interacting with the user. 
	 * At this point your activity is at the top of the activity stack, with user input going to it.
	 * Always followed by onPause().
	 *
	 */

	protected void onResume ()
	{
		super.onResume ();
	}

	/**
	 * onStart
	 * Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
	 *
	 */

	protected void onStart ()
	{
		super.onStart ();
	}

	/**
	 * onStop
	 * Called when the activity is no longer visible to the user
	 * because another activity has been resumed and is covering this one. 
	 * This may happen either because a new activity is being started, an existing one 
	 * is being brought in front of this one, or this one is being destroyed.
	 *
	 * Followed by either onRestart() if this activity is coming back to interact with the user, 
	 * or onDestroy() if this activity is going away.
	 */

	protected void onStop ()
	{
		super.onStop ();
	}

	/**
	 */
	// Click Methods
	@Override
	public void onClickFeature(View v) {
		// TODO Auto-generated method stub
		super.onClickFeature(v);

		int id = v.getId ();
		switch (id) {
		case R.id.home_btn_synchronisation :
			//startActivity (new Intent(getApplicationContext(), F1Activity.class));
			//PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			//wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
			//wakelock.acquire();
			
			synchronisation();
			break;
		case R.id.home_btn_logout :
			deconnecter();
			break;
		case R.id.home_btn_statistque :
			startActivity (new Intent(getApplicationContext(), F3Activity.class));
			break;
		case R.id.home_btn_livraison :
			//startActivity (new Intent(getApplicationContext(), VendeurActivity.class));
			Intent intent1 = new Intent(getApplicationContext(), VendeurActivity.class); //CatalogeActivity.class  //CmdViewActivity
			intent1.putExtra("user", compte);
			intent1.putExtra("cmd", "0");
			startActivity(intent1);
			break;
		case R.id.home_btn_tiers :
			//startActivity (new Intent(getApplicationContext(), F5Activity.class));
			List<com.dolibarrmaroc.com.models.AlertDialog> alerts = new ArrayList<>();
			Intent intentX = new Intent(getApplicationContext(), CommercialActivity.class); //CatalogeActivity.class  //CmdViewActivity
			intentX.putExtra("user", compte);
			com.dolibarrmaroc.com.models.AlertDialog create = new com.dolibarrmaroc.com.models.AlertDialog(intentX, "Nouveau", "user_add");
			
			Intent intentY = new Intent(getApplicationContext(), UpdateClientActivity.class);
			intentY.putExtra("user", compte);
			com.dolibarrmaroc.com.models.AlertDialog update = new com.dolibarrmaroc.com.models.AlertDialog(intentY, "Mise à jour", "user_add");
			
			alerts.add(create);
			alerts.add(update);
			new AlertDialogList(HomeActivity.this, alerts).show();
			break;
		case R.id.home_btn_feature2 :
			startActivity (new Intent(getApplicationContext(), F6Activity.class));
			break;
		default: 
			break;
		}
	}

	/**
	 */
	// More Methods
	/****************************************************
	 * Methode de synchronisation
	 */

	public void synchronisation() {
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			myoffline = new Offlineimpl(getApplicationContext());
			if(myoffline.checkAvailableofflinestorage() > 0){
				dialogSynchronisation = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.caus15),
						getResources().getString(R.string.msg_wait_sys), true);
				new ServerSideTask().execute();
			}else{
				dialogSynchronisation = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.map_data),
						getResources().getString(R.string.msg_wait), true);
				new ConnexionTask().execute();	
			}

			//new ConnexionTask().execute();
		}else{
			//Alert No network
		}
	}

	class ServerSideTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			try {
				myoffline = new Offlineimpl(getApplicationContext());
				if(CheckOutNet.isNetworkConnected(getApplicationContext())){
					myoffline.SendOutData(compte);
				}

			} catch (Exception e) {
				// TODO: handle exception
				Log.e("erreu synchro",e.getMessage() +" << ");
			}

			Log.e("start ","start cnx service");

			return null;
		}

		protected void onPostExecute(String sResponse) {
			try {
				if (dialogSynchronisation.isShowing()){
					dialogSynchronisation.dismiss();

					dialogSynchronisation = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.map_data),
							getResources().getString(R.string.msg_wait), true);

					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						new ConnexionTask().execute();	
					}

					Log.e("end ","start cnx service");
					/*
				Intent intent2 = new Intent(ConnexionActivity.this, SettingsynchroActivity.class);
				intent2.putExtra("user", compte);
				startActivity(intent2);
					 */
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}

	class ConnexionTask extends AsyncTask<Void, Void, String> {


		int nclt;
		int nprod;

		@Override
		protected String doInBackground(Void... params) {

			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			Log.i("Compte User ",compte.toString());


			products = vendeurManager.selectAllProduct(compte);
			sv  = new StockVirtual(HomeActivity.this);
			dico = vendeurManager.getDictionnaire();
			clients = vendeurManager.selectAllClient(compte);


			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
			}else{
				if(products.size() > 0){
					myoffline.CleanProduits();
					myoffline.CleanPromotionProduit();
					myoffline.shynchronizeProduits(products);
					myoffline.shynchronizePromotion(vendeurManager.getPromotionProduits());
				}


				if(dico.getDico().size() > 0){
					myoffline.CleanDico();
					myoffline.shynchronizeDico(dico);
				}

				if(clients.size() > 0){
					myoffline.CleanClients();
					myoffline.CleanPromotionClient();
					myoffline.shynchronizeClients(clients);
					myoffline.shynchronizePromotionClient(vendeurManager.getPromotionClients());
				}

				CommercialManager manager = CommercialManagerFactory.getCommercialManager();
				myoffline.CleanProspectData();
				myoffline.shynchronizeProspect(manager.getInfos(compte));

				myoffline.CleanPayement();
				PayementManager payemn = PayementManagerFactory.getPayementFactory();
				myoffline.shynchronizePayement(payemn.getFactures(compte));
			}

			Log.e("start ","start cnx task");




			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialogSynchronisation.isShowing()){
					dialogSynchronisation.dismiss();
					String msg ="";
					//wakelock.release();
					
					if(products.size() == 0){
						msg += getResources().getString(R.string.caus26)+"\n";
					}

					if(clients.size() == 0){
						msg += getResources().getString(R.string.caus27)+"\n";
					}

					int k =0;
					if(clients.size() == 0 || products.size() == 0 ){
						alertPrdClt(msg);
						k=-1;
					}

					if(k == 0) {
						if(myoffline.LoadClients("").size() != clients.size() || myoffline.LoadProduits("").size() != products.size()){
							showmessageOffline();
						}
					}


					Log.e("end ","end cnx task");
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

			AlertDialog.Builder dialog =  new AlertDialog.Builder(HomeActivity.this);
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

	public void alertPrdClt(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		alert.setMessage(msg);
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}

	public void deconnecter(){
		AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
		alert.setTitle(getResources().getString(R.string.btn_decon));
		alert.setMessage(getResources().getString(R.string.tecv47));
		alert.setNegativeButton(getResources().getString(R.string.description_logout), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//startActivity (new Intent(getApplicationContext(), F2Activity.class));
				TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String imei = tManager.getDeviceId();
				if(mydb.numberOfRows() > 0){
					Log.e(">>"+imei," >> "+mydb.numberOfRows());
					mydb.deleteUser(imei);
					//Log.e("All Compte",mydb.getAll().toString());
				}

				Intent intent = new Intent(HomeActivity.this,ConnexionActivity.class);
				startActivity(intent);
				HomeActivity.this.finish();
				return;
			}
		});

		alert.setPositiveButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
} // end class
