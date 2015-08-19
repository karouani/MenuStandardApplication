package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dolibarrmaroc.com.CmdPayActivity.ConnexionTask;
import com.dolibarrmaroc.com.CmdPayActivity.ServerSideTask;
import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.CategorieDao;
import com.dolibarrmaroc.com.dao.CategorieDaoMysql;
import com.dolibarrmaroc.com.database.DBHandler;
import com.dolibarrmaroc.com.database.StockVirtual;
import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Societe;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CheckOutSysc;
import com.dolibarrmaroc.com.utils.CommandeManagerFactory;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class CmdPayActivity extends ActionBarActivity {

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

		//type de synchronisation 0 == clt+prod // 1 == comercial // 2 == cmd+catalogue // 3 == payement
		private int type =-1;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmd_pay);
		
		products = new ArrayList<Produit>();
		dico = new Dictionnaire();

		listclt = new ArrayList<String>();
		listprd = new ArrayList<String>();
		clients = new ArrayList<Client>();
		
		mydb = new DBHandler(this);

		Bundle objetbunble  = this.getIntent().getExtras();
		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
			type= Integer.parseInt(getIntent().getStringExtra("type"));
		}
		
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cmd_pay, menu);
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
	
	public void synchronisation() {
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){

			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			myoffline = new Offlineimpl(getApplicationContext());
			if(myoffline.checkAvailableofflinestorage() > 0){
				dialogSynchronisation = ProgressDialog.show(CmdPayActivity.this, getResources().getString(R.string.caus15),
						getResources().getString(R.string.msg_wait_sys), true);
				new ServerSideTask().execute();
			}else{
				sv  = new StockVirtual(CmdPayActivity.this);
						dialogSynchronisation = ProgressDialog.show(CmdPayActivity.this, getResources().getString(R.string.map_data),
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

					dialogSynchronisation = ProgressDialog.show(CmdPayActivity.this, getResources().getString(R.string.map_data),
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
			sv  = new StockVirtual(CmdPayActivity.this);
			vendeurManager = VendeurManagerFactory.getClientManager();
			myoffline = new Offlineimpl(CmdPayActivity.this);
			CommandeManager managercmd =  new CommandeManagerFactory().getManager();
			CommercialManager manager = CommercialManagerFactory.getCommercialManager();
			PayementManager payemn = PayementManagerFactory.getPayementFactory();
			CategorieDao categorie = new CategorieDaoMysql(getApplicationContext());

			
			
			
			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
			}else{
				if(CheckOutNet.isNetworkConnected(CmdPayActivity.this)){
					
				}else{
					
				}
				
				switch (type) {
				case 0:

					int nbprod,nbclt;
					String msg = "";
					List<Produit> products = new ArrayList<>();
					products =  CheckOutSysc.checkOutProducts(vendeurManager, compte);//   vendeurManager.selectAllProduct(compte);
					
					List<Client> clients = new ArrayList<>();
					clients = CheckOutSysc.checkOutClient(vendeurManager, compte); //   vendeurManager.selectAllClient(compte);

					
						if(products.size() > 0){
							nbprod = products.size();
							for (int i = 0; i < products.size(); i++) {
								for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
									if(sv.getAllProduits(-1).get(j).getRef().equals(products.get(i).getId()+"")){
										products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
									}
								}
							}
							CheckOutSysc.checkInProductsPromotion(myoffline, compte, products, vendeurManager.getPromotionProduits());
						} 


						if(clients.size() > 0){
							nbclt =clients.size(); 
							CheckOutSysc.checkInClientsPromotion(myoffline, compte, clients, vendeurManager.getPromotionClients());
						}
						
						if(nprod == 0){
							msg += getResources().getString(R.string.caus26)+"\n";
						}

						if(nclt == 0){
							msg += getResources().getString(R.string.caus27)+"\n";
						}
						
						if(nclt == 0 || nprod == 0 ){
							alertPrdClt(msg);
						}
						
						CheckOutSysc.checkInDictionnaire(myoffline, CheckOutSysc.checkOutDictionnaire(vendeurManager, compte));
						
						CheckOutSysc.checkInClientSecteur(myoffline, CheckOutSysc.checkOutClientSecteur(vendeurManager, compte), compte);
						
					break;
				case 1:

					CheckOutSysc.checkInCommercialInfo(myoffline, CheckOutSysc.checkOutCommercialInfo(manager, compte), compte);
					
					List<Societe> lsosc = new ArrayList<>();
					lsosc = CheckOutSysc.checkOutAllSociete(manager, compte);
					if(lsosc.size() > 0){
						CheckOutSysc.checkInSocietes(myoffline, lsosc, compte);
					}
					
					break;
				case 2:

					List<Categorie> lscats = CheckOutSysc.checkOutCatalogueProduit(categorie, compte);

					
					products = new ArrayList<>();
					products =  myoffline.LoadProduits("");
					
					for (int i = 0; i < products.size(); i++) {
						for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
							//Log.e(products.get(i).getId()+"",sv.getAllProduits().get(j).getRef());
							if(Integer.parseInt(sv.getAllProduits(-1).get(j).getRef()) == products.get(i).getId()){
								products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
							}
						}
					}

					for (int j = 0; j < lscats.size(); j++) {
						for (int i = 0; i < lscats.get(j).getProducts().size(); i++) {
							for (int k = 0; k < products.size(); k++) {
								if(lscats.get(j).getProducts().get(i).getId() == products.get(k).getId()){
									lscats.get(j).getProducts().get(i).setQteDispo(products.get(k).getQteDispo());
								}
							}
						}
					}

					if(lscats.size() > 0){
						CheckOutSysc.checkInCatalogueProduit(myoffline, lscats, compte);
					}

					CheckOutSysc.checkInCommandeview(myoffline, CheckOutSysc.checkOutCommandes(managercmd, compte), compte);
					
					break;
				case 3:

					break;
				case 4:

					break;

				default:
					break;
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

			AlertDialog.Builder dialog =  new AlertDialog.Builder(CmdPayActivity.this);
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
		AlertDialog.Builder alert = new AlertDialog.Builder(CmdPayActivity.this);
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
}
