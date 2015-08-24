package com.dolibarrmaroc.com.synchronisation;

import java.util.ArrayList;
import java.util.List;

import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.R.id;
import com.dolibarrmaroc.com.R.layout;
import com.dolibarrmaroc.com.R.string;
import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.CategorieDao;
import com.dolibarrmaroc.com.dao.CategorieDaoMysql;
import com.dolibarrmaroc.com.dashboard.HomeActivity;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.os.PowerManager.WakeLock;

public class SynchronisationHomeActivity extends Activity implements OnClickListener{

	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btn5;
	
	//Declaration Objet
			private VendeurManager vendeurManager;
			private StockVirtual sv;
			private List<Produit> products;
			private Compte compte;
			private ProgressDialog dialogSynchronisation;
			//synchro offline
			private Offlineimpl myoffline;
			private Dictionnaire dico;

			private DBHandler mydb ;
			private WakeLock wakelock;
			private ProgressDialog dialog2;

			//type de synchronisation 0 == clt+prod // 1 == comercial // 2 == cmd+catalogue // 3 == payement
			private int type =-1;
			private String msg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronisation_home);

		products = new ArrayList<Produit>();
		dico = new Dictionnaire();

		
		mydb = new DBHandler(this);

		Bundle objetbunble  = this.getIntent().getExtras();
		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
		}
		
		btn1 = (Button)findViewById(R.id.syscbtn1);
		btn2 = (Button)findViewById(R.id.syscbtn2);
		btn3 = (Button)findViewById(R.id.syscbtn3);
		btn4 = (Button)findViewById(R.id.syscbtn4);
		btn5 = (Button)findViewById(R.id.syscbtn5);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		
		if("PRE-VENDEURS".toLowerCase().equals(compte.getProfile().toLowerCase())){
			btn1.setVisibility(View.GONE);
			btn2.setVisibility(View.GONE);
			btn4.setVisibility(View.GONE);
		}else{
			btn3.setVisibility(View.GONE);
		}
		
		
	}

	public void synchronisation() {
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){

			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

						dialogSynchronisation = ProgressDialog.show(SynchronisationHomeActivity.this, getResources().getString(R.string.map_data),
								getResources().getString(R.string.msg_wait), true);
						new ConnexionTask().execute();	

			//new ConnexionTask().execute();
		}else{
			//Alert No network
			alertPrdClt(getResources().getString(R.string.cmdtofc29));
		}
	}


	class ConnexionTask extends AsyncTask<String, Integer, String> {


		int nclt =0;
		int nprod =0;
		int myProgress =0;

		@Override
		protected String doInBackground(String... paramsw) {

			msg = "";
			
			Log.e("Compte Us>>er ",compte.toString());
			sv  = new StockVirtual(SynchronisationHomeActivity.this);
			vendeurManager = VendeurManagerFactory.getClientManager();
			myoffline = new Offlineimpl(SynchronisationHomeActivity.this);
			CommandeManager managercmd =  new CommandeManagerFactory().getManager();
			CommercialManager manager = CommercialManagerFactory.getCommercialManager();
			PayementManager payemn = PayementManagerFactory.getPayementFactory();
			CategorieDao categorie = new CategorieDaoMysql(getApplicationContext());
			
			
			if(myoffline.checkAvailableofflinestorage() > 0){
				myoffline.SendOutData(compte);
			}
			
			List<Produit> products = new ArrayList<>();
			List<Client> clients = new ArrayList<>();
			
			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
			}else{
				switch (type) {
				case 0:

					//msg += "Debut de chargement des produits \n";
					//msgres.setText(msg);
					
					products =  CheckOutSysc.checkOutProducts(vendeurManager, compte);//   vendeurManager.selectAllProduct(compte);
					
						if(products.size() > 0){
							nprod = products.size();
							for (int i = 0; i < products.size(); i++) {
								for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
									if(sv.getAllProduits(-1).get(j).getRef().equals(products.get(i).getId()+"")){
										products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
									}
								}
							}
							CheckOutSysc.checkInProductsPromotion(myoffline, compte, products, vendeurManager.getPromotionProduits());
						} 

						//msg += "Fin de chargement des produits \n \n";
						//msgres.setText(msg);
						
						//msg += "Debut de chargement des clients \n";
						//msgres.setText(msg);
						
						clients = CheckOutSysc.checkOutClient(vendeurManager, compte); //   vendeurManager.selectAllClient(compte);

						
						if(clients.size() > 0){
							nclt =clients.size(); 
							CheckOutSysc.checkInClientsPromotion(myoffline, compte, clients, vendeurManager.getPromotionClients());
						}
						
						
						
						//msg += "Fin de chargement des clients \n \n";
						//msgres.setText(msg);
						
						if(nprod == 0){
							msg += " *** "+getResources().getString(R.string.caus26)+"\n";
						}

						if(nclt == 0){
							msg +=" *** "+ getResources().getString(R.string.caus27)+"\n";
							
						}
						//msgres.setText(msg);
						
						CheckOutSysc.checkInDictionnaire(myoffline, CheckOutSysc.checkOutDictionnaire(vendeurManager, compte));
						
						CheckOutSysc.checkInClientSecteur(myoffline, CheckOutSysc.checkOutClientSecteur(vendeurManager, compte), compte);
						
						if(nclt != 0 && nprod != 0 ){
						//	alertPrdClt(msg);
							msg += getResources().getString(R.string.cnxlab8)+"\n";
							//msgres.setText(msg);
						}
						
					break;
				case 1:

					//msg += "Debut de chargement des Clients et les caract�ristiques des tiers \n";
					//msgres.setText(msg);
					
					CheckOutSysc.checkInCommercialInfo(myoffline, CheckOutSysc.checkOutCommercialInfo(manager, compte), compte);
					
					List<Societe> lsosc = new ArrayList<>();
					lsosc = CheckOutSysc.checkOutAllSociete(manager, compte);
					if(lsosc.size() > 0){
						CheckOutSysc.checkInSocietes(myoffline, lsosc, compte);
					}
					
					//msg += "Fin de chargement \n \n";
					msg += getResources().getString(R.string.cnxlab8)+"\n";
					//msgres.setText(msg);
					//	alertPrdClt(msg);
					
					break;
				case 2:

					//msg += "Debut de chargement de catalogue des produits \n";
					//msgres.setText(msg);
					
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
					
					
					//msg += "Fin de chargement de catalogue des produits \n \n";
				///	msgres.setText(msg);
					
					//	msg += "Debut de chargement des clients \n";
					//msgres.setText(msg);
					
					clients = new ArrayList<>();
					clients = CheckOutSysc.checkOutClient(vendeurManager, compte); //   vendeurManager.selectAllClient(compte);

					
					if(clients.size() > 0){
						nclt =clients.size(); 
						CheckOutSysc.checkInClientsPromotion(myoffline, compte, clients, vendeurManager.getPromotionClients());
					}
					
					
					if(nclt == 0){
						msg += getResources().getString(R.string.caus27)+"\n";
					}
					
					//	msg += "Fin de chargement des clients \n \n";
					//msgres.setText(msg);
					
					//	msg += "Debut de chargement des commandes \n";
					//msgres.setText(msg);
					
					CheckOutSysc.checkInCommandeview(myoffline, CheckOutSysc.checkOutCommandes(managercmd, compte), compte);
					
					//	msg += "Fin de chargement des commandes \n \n";
					//msgres.setText(msg);
					
					//	msg += "Debut de chargement des secteurs clients \n";
					//msgres.setText(msg);
					CheckOutSysc.checkInClientSecteur(myoffline, CheckOutSysc.checkOutClientSecteur(vendeurManager, compte), compte);
					//	msg += "Fin de chargement des secteurs clients \n \n";
					//msgres.setText(msg);
					
					if(lscats.size() > 0){
						CheckOutSysc.checkInCatalogueProduit(myoffline, lscats, compte);
						msg += getResources().getString(R.string.cnxlab8)+"\n";
					//	alertPrdClt(msg);
					}else{
						msg += getResources().getString(R.string.cnxlab9)+"\n";
					//	alertPrdClt(msg);
					}

					//	msg += "Fin de chargement du transaction \n \n";
				//	msgres.setText(msg);
					
					break;
				case 3:
					
					//	msg += "Debut de chargement des payements \n";
				//	msgres.setText(msg);
					List<Payement> pay = CheckOutSysc.checkOutPayement(payemn, compte);
					if(pay.size() > 0){
						CheckOutSysc.checkInPayement(myoffline, pay, compte);	
						msg += getResources().getString(R.string.cnxlab8)+"\n";
					//	alertPrdClt(msg);
					}else{
						msg += getResources().getString(R.string.cmdtofc21)+"\n";
						//alertPrdClt(msg);
					}
					
					//	msg += "Fin de chargement des payements \n \n";
				//	msgres.setText(msg);
					
					break;
				default:
					break;
				}
			}
			return "success";
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialogSynchronisation.isShowing()){
					dialogSynchronisation.dismiss();
					//wakelock.release();
				
					type = -1;
					alertPrdClt(msg);
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

			AlertDialog.Builder dialog =  new AlertDialog.Builder(SynchronisationHomeActivity.this);
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
		AlertDialog.Builder alert = new AlertDialog.Builder(SynchronisationHomeActivity.this);
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		alert.setMessage(msg);
		alert.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
	
	public void onClickHome(View v){
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("user", compte);
		intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity (intent);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.syscbtn1:
			type = 0;
			synchronisation();
			break;

		case R.id.syscbtn2:
			type = 1;
			synchronisation();
			break;
			
		case R.id.syscbtn3:
			type = 2;
			synchronisation();
			break;
			
		case R.id.syscbtn4:
			type = 3;
			synchronisation();
			break;
			
		case R.id.syscbtn5:
			Intent intent6 = new Intent(SynchronisationHomeActivity.this, SettingsynchroActivity.class);
			intent6.putExtra("user", compte);
			startActivity(intent6);
			SynchronisationHomeActivity.this.finish();
			break;
		}
	}

}
