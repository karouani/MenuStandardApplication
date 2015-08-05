package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.database.DataErreur.DatabaseHandler;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;

public class SecondeEtapeCommercialActivity extends Activity implements OnItemSelectedListener,OnClickListener{

	private Prospection client;
	private EditText iF;
	private EditText cnss;
	private EditText rc;
	private EditText patente;
	private Spinner form;
	private Spinner tva;
	private Spinner tierce;
	private Spinner effectif;
	private Button btn;

	private HashMap<String, String> form_code;
	private HashMap<String, String> tierce_code;
	private HashMap<String, String> tierce_id;
	private List<String> juri;
	private List<String> typent;
	
	private ioffline myoffline;

	//database 
			private DatabaseHandler database;
	//Asynchrone avec connexion 
	private ProgressDialog dialog;
	private ProgressDialog dialog2;
	
	
	String resu ;
	private WakeLock wakelock;
	private CommercialManager manager;
	private Compte compte;

	private StockVirtual sv;
	
	public SecondeEtapeCommercialActivity() {
		// TODO Auto-generated constructor stub
		client = new Prospection();
		// TODO Auto-generated constructor stub
		manager = CommercialManagerFactory.getCommercialManager();
		compte = new Compte();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seconde_etape_commercial);

		try {
			Bundle objetbunble  = this.getIntent().getExtras();
			
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
			wakelock.acquire();
			
			if (objetbunble != null) {
				client = (Prospection) getIntent().getSerializableExtra("client");
				compte = (Compte) getIntent().getSerializableExtra("user");
				/*
				 * intent.putStringArrayListExtra("form", (ArrayList<String>) data.getJuridique());
					intent.putStringArrayListExtra("tierce", (ArrayList<String>) data.getTypent());
					intent.putExtra("code_juridique", data.getJuridique_code());
					intent.putExtra("code_type", data.getTypent_code());
					intent.putExtra("id_type", data.getTypent_id());
				 */
				form_code = (HashMap<String, String>)getIntent().getSerializableExtra("code_juridique");
				tierce_code = (HashMap<String, String>)getIntent().getSerializableExtra("code_type");
				tierce_id = (HashMap<String, String>)getIntent().getSerializableExtra("id_type");
				juri = getIntent().getExtras().getStringArrayList("form");
				typent = getIntent().getExtras().getStringArrayList("tierce");

			}

			iF = (EditText) findViewById(R.id.comm_if);
			cnss= (EditText) findViewById(R.id.comm_cnss);
			rc= (EditText) findViewById(R.id.comm_rc);
			patente= (EditText) findViewById(R.id.comm_patente);

			form= (Spinner) findViewById(R.id.comm_form);
			addItemsOnSpinner(form,juri);
			form.setOnItemSelectedListener(this);

			tva= (Spinner) findViewById(R.id.comm_tva);
			tva.setOnItemSelectedListener(this);

			tierce= (Spinner) findViewById(R.id.comm_tierce);
			addItemsOnSpinner(tierce,typent);
			tierce.setOnItemSelectedListener(this);

			effectif= (Spinner) findViewById(R.id.comm_effectif);
			effectif.setOnItemSelectedListener(this);

			btn= (Button) findViewById(R.id.comm_save);
			btn.setOnClickListener(this);
			
			myoffline = new Offlineimpl(getApplicationContext());
			
			sv  = new StockVirtual(SecondeEtapeCommercialActivity.this);
			
			Log.v("HashMapTest", typent.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		myoffline = new Offlineimpl(getApplicationContext());
		if(myoffline.checkAvailableofflinestorage() > 0){
			dialog2 = ProgressDialog.show(SecondeEtapeCommercialActivity.this, getResources().getString(R.string.caus15),
					getResources().getString(R.string.msg_wait_sys), true);
			new ServerSideTask().execute(); 
		}
		
		super.onStart();
	}

	public void addItemsOnSpinner(Spinner s,List<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v) {
		
		//Log.e("tierece ",tierce.getSelectedItem().toString() + "#"+ effectif.getSelectedItem().toString());
		
		if(checkRequiredFields().size() > 0){
			alertinvonan();
		}else{
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				
				if(myoffline.checkRefClient(client.getName(),client.getEmail()) == -1){
					dialog = ProgressDialog.show(SecondeEtapeCommercialActivity.this, getResources().getString(R.string.comerciallab3),
							 getResources().getString(R.string.cnxlab2), true);
					
					new EnregistrationTask().execute();	
				}else{
					
					AlertDialog.Builder localBuilder = new AlertDialog.Builder(SecondeEtapeCommercialActivity.this);
					localBuilder
					.setTitle(getResources().getString(R.string.cmdtofc10))
					.setMessage(getResources().getString(R.string.caus16))
					.setCancelable(false)
					.setPositiveButton("Retour",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							ViewGroup group = (ViewGroup)findViewById(R.id.layoutall);

							for (int i = 0, count = group.getChildCount(); i < count; ++i)
							{
								View view = group.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}

							}
							
							ViewGroup group2 = (ViewGroup)findViewById(R.id.comm_interface);

							for (int i = 0, count = group2.getChildCount(); i < count; ++i)
							{
								View view = group2.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}
							}
						}
					});
					localBuilder.show();
				}
				
			}else{
				
				
				if(myoffline.checkRefClient(client.getName(),client.getEmail()) == -1){
					dialog = ProgressDialog.show(SecondeEtapeCommercialActivity.this, getResources().getString(R.string.comerciallab3),
							getResources().getString(R.string.cnxlab2), true);
					
					Log.e("in clt >> ",client.toString());
					new EnregistrationOfflineTask().execute();
				}else{
					
					AlertDialog.Builder localBuilder = new AlertDialog.Builder(SecondeEtapeCommercialActivity.this);
					localBuilder
					.setTitle(getResources().getString(R.string.cmdtofc10))
					.setMessage(getResources().getString(R.string.caus16))
					.setCancelable(false)
					.setPositiveButton("Retour",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							ViewGroup group = (ViewGroup)findViewById(R.id.layoutall);

							for (int i = 0, count = group.getChildCount(); i < count; ++i)
							{
								View view = group.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}

							}
							
							ViewGroup group2 = (ViewGroup)findViewById(R.id.comm_interface);

							for (int i = 0, count = group2.getChildCount(); i < count; ++i)
							{
								View view = group2.getChildAt(i);
								if (view instanceof EditText) {
									((EditText)view).setText("");
								}
							}
						}
					});
					localBuilder.show();
				}
			}
			
			
		}
		
		
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long idobj) {

		String selected = parent.getItemAtPosition(position).toString();
		if (parent == tva) {
			if(position == 0){
				client.setTva_assuj(1);
			}else{
				client.setTva_assuj(0);
			}
		}else if(parent == tierce){
			Log.e("selected ",selected);
			String code = tierce_code.get(selected);
			String id = tierce_id.get(selected);

			client.setTypent_code(code);
			client.setTypent_id(id);

		}else if(parent == effectif){
			//parent.getAdapter().getItemId(position);
			Log.i("Voila Item Effectif",parent.getAdapter().getItemId(position)+"");
			client.setEffectif_id(parent.getAdapter().getItemId(position)+"");
		}else if(parent == form){
			String code = form_code.get(selected);
			client.setForme_juridique_code(code);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private	class EnregistrationTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				resu = manager.insert(compte, client);
			}else{
				if(!myoffline.checkFolderexsiste()){
		        	showmessageOffline();
		        	resu =getResources().getString(R.string.comerciallab4);
				}else{
					database = new DatabaseHandler(getApplicationContext());
					client.setId((int)database.addrow("clt"));
					resu = myoffline.shynchronizeProspection(client,compte);
				}
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					//Toast.makeText(SecondeEtapeCommercialActivity.this, resu, Toast.LENGTH_LONG).show();
					if(resu.equals("-1")){
						resu = getResources().getString(R.string.comerciallab5);
					}else{
						resu =getResources().getString(R.string.comerciallab6);
					}
					AlertDialog.Builder localBuilder = new AlertDialog.Builder(SecondeEtapeCommercialActivity.this);
					localBuilder
					.setMessage(resu)
					.setCancelable(false)
					.setPositiveButton("Retour",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							//CommercialActivity.this.notifyAll();
							SecondeEtapeCommercialActivity.this.finish();
							Intent intent = new Intent(SecondeEtapeCommercialActivity.this,ConnexionActivity.class);
							intent.putExtra("user", compte);
							startActivity(intent);
						}
					}
							);
					localBuilder.setNegativeButton("Quitter ",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							SecondeEtapeCommercialActivity.this.finish();
							Intent intent = new Intent(SecondeEtapeCommercialActivity.this,ConnexionActivity.class);
							startActivity(intent);
						}
					}
							);
					localBuilder.create().show();
					
					wakelock.release();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}
	}
	
	private	class EnregistrationOfflineTask  extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			if(!myoffline.checkFolderexsiste()){
	        	showmessageOffline();
	        	resu =getResources().getString(R.string.comerciallab4);
			}else{
				database = new DatabaseHandler(getApplicationContext());
				client.setId((int)database.addrow("clt"));
				resu = myoffline.shynchronizeProspection(client,compte);
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					//Toast.makeText(SecondeEtapeCommercialActivity.this, resu, Toast.LENGTH_LONG).show();
					

					AlertDialog.Builder localBuilder = new AlertDialog.Builder(SecondeEtapeCommercialActivity.this);
					localBuilder
					.setMessage(resu)
					.setCancelable(false)
					.setPositiveButton("Retour",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							//CommercialActivity.this.notifyAll();
							SecondeEtapeCommercialActivity.this.finish();
							Intent intent = new Intent(SecondeEtapeCommercialActivity.this,ConnexionActivity.class);
							intent.putExtra("user", compte);
							startActivity(intent);
						}
					}
							);
					localBuilder.setNegativeButton("Quitter ",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							SecondeEtapeCommercialActivity.this.finish();
							Intent intent = new Intent(SecondeEtapeCommercialActivity.this,ConnexionActivity.class);
							startActivity(intent);
						}
					}
							);
					localBuilder.create().show();
					
					wakelock.release();
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
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(SecondeEtapeCommercialActivity.this);
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
	
	class ServerSideTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				myoffline = new Offlineimpl(getApplicationContext());
				
				List<Produit> products = new ArrayList<>();
				List<Client> clients = new ArrayList<>();
				
				VendeurManager vendeurManager = VendeurManagerFactory.getClientManager();
				
				Dictionnaire dico  = new Dictionnaire();
				
				if(CheckOutNet.isNetworkConnected(getApplicationContext())){
					myoffline.SendOutData(compte);	
				}
				

				
				if(!myoffline.checkFolderexsiste()){
					showmessageOffline();
				}else{
					/*********************** offline ****************************************/
					Log.e("begin offline from network",">>start load");
					if(CheckOutNet.isNetworkConnected(getApplicationContext())){

						products = vendeurManager.selectAllProduct(compte);
						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits().size(); j++) {
								if(sv.getAllProduits().get(j).getRef().equals(products.get(i).getId())){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits().get(j).getQteDispo());
								}
							}
						}
						if(products.size() > 0){
							myoffline.CleanProduits();
							myoffline.CleanPromotionProduit();
							myoffline.shynchronizeProduits(products);
							myoffline.shynchronizePromotion(vendeurManager.getPromotionProduits());	
						}
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						dico = vendeurManager.getDictionnaire();
						if(dico.getDico().size() > 0){
							myoffline.CleanDico();
							myoffline.shynchronizeDico(dico);	
						}
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						clients = vendeurManager.selectAllClient(compte);
						if(clients.size() > 0){
							myoffline.CleanClients();
							myoffline.CleanPromotionClient();
							myoffline.shynchronizeClients(clients);
							myoffline.shynchronizePromotionClient(vendeurManager.getPromotionClients());	
						}

					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						CommercialManager manager = CommercialManagerFactory.getCommercialManager();
						myoffline.CleanProspectData();
						myoffline.shynchronizeProspect(manager.getInfos(compte));	
					}


					if(CheckOutNet.isNetworkConnected(getApplicationContext())){

						PayementManager payemn = PayementManagerFactory.getPayementFactory();
						myoffline.CleanPayement();
						myoffline.shynchronizePayement(payemn.getFactures(compte));	
					}
				}
				

				
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("erreu synchro",e.getMessage() +" << ");
			}
			
			return null;
		}

		protected void onPostExecute(String sResponse) {
			try {
				if (dialog2.isShowing()){
					dialog2.dismiss();
					
				}
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SecondeEtapeCommercialActivity.this.finish();
			Intent intent1 = new Intent(SecondeEtapeCommercialActivity.this, VendeurActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
		}
		return false;
	}
	
public List<String> checkRequiredFields(){
		
		// name;address;zip;tel;fax;email;
		List<String> res = new ArrayList<>();
		try {
			myoffline = new Offlineimpl(getApplicationContext());
			
			List<String> req = myoffline.LoadProspect("").getLsrequired();
		
			if(!req.contains("email"))req.add("email");
			
	        for (int i = 0; i < req.size(); i++) {
	        	
	        	String st = req.get(i);
				if(st.equals("iF")){
						if(iF.getText().toString().length() == 0){
							res.add("IF du client");
						}
				}
			
				if(st.equals("cnss")){
					if(cnss.getText().toString().length() == 0){
						res.add("CNSS du client");
					}
				}

				if(st.equals("rc")){
					if(rc.getText().toString().length() == 0){
						res.add("RC du client");
					}
				}
				
				/*
				if(st.equals("ville")){
					if(ville.getSelectedItemId() == 0){
						res.add("Ville du client");
					}
				}
				*/

				if(st.equals("patente")){
					if(patente.getText().toString().length() == 0){
						res.add("Patente du client");
					}
				}

				if(st.equals("tierce")){
					if(tierce.getSelectedItem().toString().equals("-")){
						res.add("Type du tiers");
					}
				}

				
				if(st.equals("effectif")){
					if(effectif.getSelectedItem().toString().equals("0")){
						res.add("Effectif > 0");
					}
					
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("error data ",e.getMessage()  +" << ");
		}

		
		return res;
		
	}
	
	
	
	public void alertinvonan(){
		
		List<String> req = checkRequiredFields();

		AlertDialog.Builder alert = new AlertDialog.Builder(SecondeEtapeCommercialActivity.this);
		alert.setTitle(R.string.caus21);
		
		String st ="";
		for (int i = 0; i < req.size(); i++) {
			st+= req.get(i)+"\n";
		}
		alert.setMessage(
				String.format(st
						));
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
