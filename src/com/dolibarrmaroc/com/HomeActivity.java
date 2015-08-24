
package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.VendeurActivity.ConnexionTask;
import com.dolibarrmaroc.com.VendeurActivity.OfflineTask;
import com.dolibarrmaroc.com.VendeurActivity.ServerSideTask;
import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.CategorieDao;
import com.dolibarrmaroc.com.dao.CategorieDaoMysql;
import com.dolibarrmaroc.com.dao.DeniedDataDaoMysql;
import com.dolibarrmaroc.com.dashboard.DashboardActivity;
import com.dolibarrmaroc.com.database.DBHandler;
import com.dolibarrmaroc.com.database.StockVirtual;
import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.CategorieCustomer;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Societe;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CheckOutSysc;
import com.dolibarrmaroc.com.utils.CommandeManagerFactory;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;
import com.karouani.cicin.widget.alert.AlertDialogList;



/**
 * This is a simple activity that demonstrates the dashboard user interface pattern.
 *
 */

public class HomeActivity extends Activity 
{
	/* buttons events */
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btn5;
	private Button btn6;
	private Button btn7;
	private Button btn8;
	private Button btn9;

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
	private ProgressDialog dialog2;

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

		Log.e("Compte User ",compte.toString());
		vendeurManager = VendeurManagerFactory.getClientManager();

		if(CheckOutNet.isNetworkConnected(getApplicationContext())){

			myoffline = new Offlineimpl(getApplicationContext());

			/*
			if(myoffline.checkAvailableofflinestorage() > 0){
				dialog2 = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.caus15),
						getResources().getString(R.string.msg_wait_sys), true);
				new ServerSideTask().execute();
			}
			 */
			synchronisation();

			//new ConnexionTask().execute();
		}
		
		/* init btn */
		btn1 = (Button)findViewById(R.id.home_btn_livraison);
		btn2 = (Button)findViewById(R.id.home_btn_tiers);
		btn3 = (Button)findViewById(R.id.home_btn_prise_cmd);
		btn4 = (Button)findViewById(R.id.home_btn_maps);
		btn5 = (Button)findViewById(R.id.home_btn_intervention);
		btn6 = (Button)findViewById(R.id.home_btn_stock);
		btn7 = (Button)findViewById(R.id.home_btn_statistque);
		btn8 = (Button)findViewById(R.id.home_btn_synchronisation);
		btn9 = (Button)findViewById(R.id.home_btn_logout);
		
		ShowMyHome();
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
		ShowMyHome();
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
	public void onClickFeature(View v) {
		// TODO Auto-generated method stub

		myoffline = new Offlineimpl(HomeActivity.this);

		int id = v.getId ();
		switch (id) {
		case R.id.home_btn_logout :
			deconnecter(this).create().show();
			break;
		case R.id.home_btn_synchronisation :
			//startActivity (new Intent(getApplicationContext(), F1Activity.class));
			//PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			//wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
			//wakelock.acquire();

			//synchronisation();
			/*
			List<com.dolibarrmaroc.com.models.AlertDialog> alertfc22 = new ArrayList<>();
			Intent intentfc12 = new Intent(getApplicationContext(), CmdPayActivity.class); //CatalogeActivity.class  //CmdViewActivity
			intentfc12.putExtra("type", "0");
			intentfc12.putExtra("user", compte);
			com.dolibarrmaroc.com.models.AlertDialog createfc12 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc12, getString(R.string.syscl1), "invoice_see");


			Intent intentfc22 = new Intent(getApplicationContext(), CmdPayActivity.class);
			intentfc22.putExtra("type", "1");
			intentfc22.putExtra("user", compte);
			com.dolibarrmaroc.com.models.AlertDialog updatefc22 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc22, getString(R.string.syscl2), "invoice");


			Intent intentfc32 = new Intent(getApplicationContext(), CmdPayActivity.class);
			intentfc32.putExtra("type", "2");
			intentfc32.putExtra("user", compte);
			com.dolibarrmaroc.com.models.AlertDialog updatefc32 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc32, getString(R.string.syscl3), "invoice_lock");

			Intent intentfc42 = new Intent(getApplicationContext(), CmdPayActivity.class);
			intentfc42.putExtra("type", "3");
			intentfc42.putExtra("user", compte);
			com.dolibarrmaroc.com.models.AlertDialog updatefc24 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc42, getString(R.string.syscl4), "invoice_pay");

			Intent intentfc52 = new Intent(getApplicationContext(), CmdPayActivity.class);
			com.dolibarrmaroc.com.models.AlertDialog updatefc25 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc52, getString(R.string.syscl5), "invoice_pay");

			alertfc22.add(createfc12);
			alertfc22.add(updatefc22);
			alertfc22.add(updatefc32);
			alertfc22.add(updatefc24);
			alertfc22.add(updatefc25);
			new AlertDialogList(HomeActivity.this, alertfc22).show();
			 */
			Intent intentsys = new Intent(HomeActivity.this, SynchronisationHomeActivity.class);
			intentsys.putExtra("user", compte);
			startActivity(intentsys);

			break;
		case R.id.home_btn_statistque :
			//startActivity (new Intent(getApplicationContext(), F3Activity.class));
			alertPrdClt(getString(R.string.syscl9));
			break;
		case R.id.home_btn_livraison :
			//startActivity (new Intent(getApplicationContext(), VendeurActivity.class));
			if(compte.getPermissionbl() == 0){
				List<com.dolibarrmaroc.com.models.AlertDialog> alertfc2 = new ArrayList<>();
				Intent intentfc1 = new Intent(getApplicationContext(), VendeurActivity.class); //CatalogeActivity.class  //CmdViewActivity
				intentfc1.putExtra("user", compte);
				intentfc1.putExtra("cmd", "0");
				com.dolibarrmaroc.com.models.AlertDialog createfc1 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc1, getString(R.string.title_activity_vendeur), "invoice_see");


				Intent intentfc2 = new Intent(getApplicationContext(), PayementActivity.class);
				intentfc2.putExtra("user", compte);
				intentfc2.putExtra("dico", myoffline.LoadDeco("").getDico());
				com.dolibarrmaroc.com.models.AlertDialog updatefc2 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc2, getString(R.string.title_activity_payement), "invoice");


				Intent intentfc3 = new Intent(getApplicationContext(), OfflineActivity.class);
				intentfc3.putExtra("user", compte);
				com.dolibarrmaroc.com.models.AlertDialog updatefc3 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc3, getString(R.string.title_activity_offline), "invoice_lock");

				Intent intentfc4 = new Intent(getApplicationContext(), ReglementOfflineActivity.class);
				intentfc4.putExtra("user", compte);
				com.dolibarrmaroc.com.models.AlertDialog updatefc4 = new com.dolibarrmaroc.com.models.AlertDialog(intentfc4, getString(R.string.title_activity_reglement_offline), "invoice_pay");

				alertfc2.add(createfc1);
				alertfc2.add(updatefc2);
				alertfc2.add(updatefc3);
				alertfc2.add(updatefc4);
				new AlertDialogList(HomeActivity.this, alertfc2).show();

			}else{
				alertPrdClt(getString(R.string.syscl9));
			}

			break;
		case R.id.home_btn_tiers :
			/*
			myoffline = new Offlineimpl(getApplicationContext());
			CommercialManager manager = CommercialManagerFactory.getCommercialManager();
			List<Societe> lsosc = new ArrayList<>();
			lsosc = CheckOutSysc.checkOutAllSociete(manager, compte);
			if(lsosc.size() > 0){
				CheckOutSysc.checkInSocietes(myoffline, lsosc, compte);
			}
			 */

			//startActivity (new Intent(getApplicationContext(), F5Activity.class));


			if(compte.getPermission() != 0 && compte.getPermissionbl() == 0){
				List<com.dolibarrmaroc.com.models.AlertDialog> alerts = new ArrayList<>();
				Intent intentX = new Intent(getApplicationContext(), CommercialActivity.class); //CatalogeActivity.class  //CmdViewActivity
				intentX.putExtra("user", compte);
				com.dolibarrmaroc.com.models.AlertDialog create = new com.dolibarrmaroc.com.models.AlertDialog(intentX, getString(R.string.comm_new_head), "user_yellow_add");

				Intent intentY = new Intent(getApplicationContext(), UpdateClientActivity.class);
				intentY.putExtra("user", compte);
				com.dolibarrmaroc.com.models.AlertDialog update = new com.dolibarrmaroc.com.models.AlertDialog(intentY,  getString(R.string.comm_title_head), "user_yellow_edit");

				alerts.add(create);
				alerts.add(update);
				new AlertDialogList(HomeActivity.this, alerts).show();
			}else{
				alertPrdClt(getString(R.string.syscl7));
			}

			break;
		case R.id.home_btn_stock :
			List<com.dolibarrmaroc.com.models.AlertDialog> alerts2 = new ArrayList<>();

			if("Administrateur magasinier".toLowerCase().equals(compte.getProfile().toLowerCase())){

				Intent intents1 = new Intent(getApplicationContext(), TransfertstockActivity.class); //CatalogeActivity.class  //CmdViewActivity
				intents1.putExtra("user", compte);
				com.dolibarrmaroc.com.models.AlertDialog creates1 = new com.dolibarrmaroc.com.models.AlertDialog(intents1, getString(R.string.title_activity_transfertstock), "warehouse_worker");
				alerts2.add(creates1);
			}
			Intent intents2 = new Intent(getApplicationContext(), TransfertvirtualstockActivity.class);
			intents2.putExtra("user", compte);
			intents2.putExtra("cmd", "0");
			com.dolibarrmaroc.com.models.AlertDialog updates2 = new com.dolibarrmaroc.com.models.AlertDialog(intents2, getString(R.string.title_activity_transfertvirtualstock), "warehouse_put");


			alerts2.add(updates2);
			new AlertDialogList(HomeActivity.this, alerts2).show();



			break;
		case R.id.home_btn_prise_cmd : 

			if(compte.getPermissionbl() != 0){
				List<com.dolibarrmaroc.com.models.AlertDialog> alertc2 = new ArrayList<>();
				Intent intentc1 = new Intent(getApplicationContext(), CatalogeActivity.class); //CatalogeActivity.class  //CmdViewActivity
				intentc1.putExtra("user", compte);
				intentc1.putExtra("cmd", "1");
				com.dolibarrmaroc.com.models.AlertDialog createc1 = new com.dolibarrmaroc.com.models.AlertDialog(intentc1, getString(R.string.title_activity_cataloge), "buy");

				Intent intentc2 = new Intent(getApplicationContext(), CmdViewActivity.class);
				intentc2.putExtra("user", compte);
				intentc2.putExtra("cmd", "1");
				com.dolibarrmaroc.com.models.AlertDialog updatec2 = new com.dolibarrmaroc.com.models.AlertDialog(intentc2, getString(R.string.title_activity_cmd_view), "catalog");

				alertc2.add(createc1);
				alertc2.add(updatec2);
				new AlertDialogList(HomeActivity.this, alertc2).show();
			}else{
				alertPrdClt(getString(R.string.syscl8));
			}
			break;
		case R.id.home_btn_maps : 

			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				Intent photosIntent = new Intent(this, MainActivity.class);
				photosIntent.putExtra("user", compte);
				startActivity(photosIntent);
			}else{
				alertmaps();
			}

			break;
		default: 
			alertPrdClt(getString(R.string.syscl9));
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
				/*	
				sv  = new StockVirtual(HomeActivity.this);
					if(sv.getSyc() == 1){

						dialogSynchronisation = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.map_data),
								getResources().getString(R.string.msg_wait), true);
						new ConnexionTask().execute();	

					}
				 */
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

			Log.e("Compte User ",compte.toString());
			sv  = new StockVirtual(HomeActivity.this);
			vendeurManager = VendeurManagerFactory.getClientManager();
			myoffline = new Offlineimpl(HomeActivity.this);
			CommandeManager managercmd =  new CommandeManagerFactory().getManager();
			CommercialManager manager = CommercialManagerFactory.getCommercialManager();
			PayementManager payemn = PayementManagerFactory.getPayementFactory();
			CategorieDao categorie = new CategorieDaoMysql(getApplicationContext());




			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
			}else{

				int vsv = sv.getSyc();
				Log.e("is alreadey sysc ",vsv+"");
				if(vsv == 1){
					if(CheckOutNet.isNetworkConnected(HomeActivity.this)){
						HashMap<String, Integer> res = new HashMap<>();
						res = CheckOutSysc.ReloadProdClt(HomeActivity.this, myoffline, compte, vendeurManager, payemn, sv, categorie, managercmd, 0,manager);

						CheckOutSysc.RelaodClientSectInfoCommDicto(HomeActivity.this, myoffline, compte, vendeurManager, manager, 0);

						
						nprod = res.get("prod");
						nclt = res.get("clt");
						
						
						new DeniedDataDaoMysql().sendMyErrorData(myoffline.LoadDenided(""), compte);
					}
				}else{
					nprod = myoffline.LoadClients("").size();
					nclt = myoffline.LoadProduits("").size();
				}
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

					if(nprod == 0){
						msg += getResources().getString(R.string.caus26)+"\n";
					}

					if(nclt == 0){
						msg += getResources().getString(R.string.caus27)+"\n";
					}

					int k =0;
					if(nclt == 0 || nprod == 0 ){
						alertPrdClt(msg);
						k=-1;
					}

					if(k == 0) {
						if(nclt == 0 || nprod == 0 ){
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			deconnecter(this).create().show();
			return true;
		}
		return false;
	}

	public void alertmaps(){
		AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
		alert.setTitle(getResources().getString(R.string.caus17));
		alert.setMessage(getResources().getString(R.string.caus18));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}

	public void goHome(Context context) 
	{
		final Intent intent = new Intent(context, HomeActivity.class);
		intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity (intent);
	}

	/**
	 * Use the activity label to set the text in the activity's title text view.
	 * The argument gives the name of the view.
	 *
	 * <p> This method is needed because we have a custom title bar rather than the default Android title bar.
	 * See the theme definitons in styles.xml.
	 * 
	 * @param textViewId int
	 * @return void
	 */

	public void setTitleFromActivityLabel (int textViewId)
	{
		TextView tv = (TextView) findViewById (textViewId);
		if (tv != null) tv.setText (getTitle ());
	} // end setTitleText

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg String
	 * @return void
	 */

	public void toast (String msg)
	{
		Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast

	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	public void trace (String msg) 
	{
		Log.d("Demo", msg);
		toast (msg);
	}

	public AlertDialog.Builder deconnecter(final Activity context){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(context.getResources().getString(R.string.btn_decon));
		alert.setMessage(context.getResources().getString(R.string.tecv47));
		alert.setNegativeButton(context.getResources().getString(R.string.description_logout), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//startActivity (new Intent(getApplicationContext(), F2Activity.class));
				TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String imei = tManager.getDeviceId();
				if(mydb.numberOfRows() > 0){
					Log.e(">>"+imei," >> "+mydb.numberOfRows());
					mydb.deleteUser(imei);
					//Log.e("All Compte",mydb.getAll().toString());
				}

				Intent intentService = new Intent(HomeActivity.this, ShowLocationActivity.class);
				stopService(intentService);


				Intent intent = new Intent(context,ConnexionActivity.class);
				startActivity(intent);
				context.finish();
				return;
			}
		});

		alert.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}
		});
		alert.setCancelable(true);
		return alert;
	}

	private void ShowMyHome(){
		if(compte.getProfile().toLowerCase().equals("PRE-VENDEURS".toLowerCase())){
			btn1.setEnabled(false);
			btn2.setEnabled(false);
			btn4.setEnabled(false);
			btn5.setEnabled(false);
			btn6.setEnabled(false);
		}else if(compte.getProfile().toLowerCase().equals("Administrateur magasinier".toLowerCase())){
			btn1.setEnabled(false);
			btn2.setEnabled(false);
			btn3.setEnabled(false);
			btn4.setEnabled(false);
			btn5.setEnabled(false);
			btn8.setEnabled(false);
		}
	}
} // end class
