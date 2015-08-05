package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.database.DataErreur.DatabaseHandler;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.FileData;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;
import com.karouani.cicin.widget.AutocompleteCustomArrayAdapter;
import com.karouani.cicin.widget.CustomAutoCompleteTextChangedListener;
import com.karouani.cicin.widget.CustomAutoCompleteView;





public class PayementActivity extends Activity implements OnItemSelectedListener,OnClickListener,TextWatcher,OnItemClickListener{

	
	public CustomAutoCompleteView allfacturesdata;
	// adapter for auto-complete
	public ArrayAdapter<String> myAdapter;
	public String[] values ;
	
	private StockVirtual sv;
	
	private List<String> listfc = new ArrayList<String>();
	
	private WakeLock wakelock;
	//Declaration Objet
	private PayementManager manager;
	private Compte compte;
	private Payement pay;
	private List<Payement> list;
	private List<String> listFact;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;
	private ProgressDialog dialog2;
	
	//database 
		private DatabaseHandler database;

	//private Spinner clientspinner,proSpinner;
	//private AutoCompleteTextView allfacturesdata;
	
	private EditText encaisse;
	private EditText numchek;
	private Spinner mode;
	private TextView rendu;
	private Button valider;
	private Button valider2;
	private String reglement;
	
	private long startout = 0; 

	//Spinner Remplissage
	private List<String> listDictionnaire;
	private ArrayList<HashMap<String, String>> dico;
	
	private ioffline myoffline;

	public PayementActivity() {
		// TODO Auto-generated constructor stub
		manager = PayementManagerFactory.getPayementFactory();
		list = new ArrayList<>();
		listDictionnaire = new ArrayList<String>();
		pay = new Payement();
		listFact = new ArrayList<String>();
		dico = new ArrayList<>();
		
		
	}

	@Override
	protected void onStart() {
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();

		
		myoffline = new Offlineimpl(getApplicationContext());
		
		 if(!myoffline.checkFolderexsiste()){
	        	showmessageOffline();
	        }
		
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			
			if(myoffline.checkAvailableofflinestorage() > 0){
				dialog2 = ProgressDialog.show(PayementActivity.this, getResources().getString(R.string.caus15),
						getResources().getString(R.string.msg_wait_sys), true);
				new ServerSideTask().execute(); 
			}else{
				dialog = ProgressDialog.show(PayementActivity.this, getResources().getString(R.string.map_data),
						getResources().getString(R.string.msg_wait), true);
				new ConnexionTask().execute();
			}
			
			
			
		}else{
			
			dialog = ProgressDialog.show(PayementActivity.this, getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);
			new OfflineTask().execute();
		}

		super.onStart();
	}
	/*
	
	@Override
	protected void onDestroy() {
		
		Log.e("hello","destroy action");
		  if (dialog != null) {
			  dialog.dismiss();
			  dialog = null;
	        }
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		
		Log.e("hello","resume action");
		  if (dialog != null) {
			  dialog.dismiss();
			 // dialog = null;
	        }
		super.onResume();
	}
	
	*/
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payement);

		try {
			try {
				
				if (android.os.Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
				}
				
				Log.e("All 1 Dico", "hello is me");
				compte = (Compte) getIntent().getSerializableExtra("user");
				dico = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("dico");
				Log.e("All 11 Dico", "hello is me");
			} catch (Exception e) {}

			rendu		= (TextView) findViewById(R.id.resteapaye);
			encaisse	= (EditText) findViewById(R.id.apaye);
			numchek		= (EditText) findViewById(R.id.check_number);
			valider 	= (Button) findViewById(R.id.validation);
			valider.setOnClickListener(this);
			
			valider2 	= (Button) findViewById(R.id.validation2tc);
			valider2.setOnClickListener(this);
			
			mode 		= (Spinner) findViewById(R.id.spinner1);
			allfacturesdata = (CustomAutoCompleteView) findViewById(R.id.autocomplate);
			allfacturesdata.setOnItemClickListener(this);
			allfacturesdata.addTextChangedListener(this);

			for (int i = 0; i < dico.size(); i++) {
				HashMap<String, String> values = dico.get(i);
				//Log.i("Dico GetList"+i,values.toString());

				for (String val : values.values()) {
					listDictionnaire.add(val);
				}

			}


			addItemsOnSpinner();


			encaisse.addTextChangedListener(new TextWatcher() {

				Double m = (double) 0;
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(!"".equals(encaisse.getText().toString()) && (!".".equals(encaisse.getText().toString())) ) m = Double.parseDouble(encaisse.getText().toString());
					double v = pay.getTotal() - pay.getAmount();
					Double h = v - m;

					if(h<0){
						alert();

					}
					/*else if(h == 0){
						alert2();
					}
					*/else{
						rendu.setText(h+"");
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			mode.setOnItemSelectedListener(this);
			
			sv  = new StockVirtual(PayementActivity.this);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	public void addItemsOnSpinner() {

		Log.e("All data Dico", listDictionnaire.toString());

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listDictionnaire);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mode.setAdapter(dataAdapter);
	}

	public void addItemsOnAutoComplate() {

		Log.e("All data", listFact.size()+"");
/*
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listFact);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		allfacturesdata.setAdapter(dataAdapter);
		*/

		listfc = listFact;
		values= new String[listFact.size()];
		for (int i = 0; i < listFact.size(); i++) {
			values[i] = listFact.get(i);
		}
		myAdapter = new AutocompleteCustomArrayAdapter(PayementActivity.this, R.layout.list_view_row, values);
		allfacturesdata.setAdapter(myAdapter);

		 
		/*
		allfacturesdata.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				String selected = (String) parent.getItemAtPosition(position);

				allfacturesdata.showDropDown();

				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);

				allfacturesdata.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});


				Log.e("Selected Client Spinner ",selected);
				int trouve = 0;

				for (int i = 0; i < list.size(); i++) {

					
					if (list.get(i).getNum().equals(selected)) {
						pay = list.get(i);
						//allfacturesdata.setEnabled(false);
						Log.e("Text selectionner ",pay.toString());
						double v = pay.getTotal() - pay.getAmount();
						rendu.setText(v+"");
						trouve =1;
						break;
					}

				}
				if(trouve == 0) rendu.setText("0");

			}
		});
		*/
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			/*
			new AlertDialog.Builder(this)
			.setTitle("Vraiment d�connecter?")
			.setMessage("Vous voulez vraiment d�connecter?")
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent1 = new Intent(PayementActivity.this, VendeurActivity.class);
					intent1.putExtra("user", compte);
					startActivity(intent1);
				}
				
			})
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					PayementActivity.super.onBackPressed();
					
					Intent intentService = new Intent(PayementActivity.this, ShowLocationActivity.class);
					stopService(intentService);
					
					Intent intent1 = new Intent(PayementActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					startActivity(intent1);
				}

			}).create().show();
			
			*/
			Intent intent1 = new Intent(PayementActivity.this, VendeurActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
			PayementActivity.this.finish();
			return true;
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payement, menu);
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


	class ConnexionTask extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			Log.e("start ","cnx");
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			list = manager.getFactures(compte);
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(list.size() > 0){
					if(!myoffline.checkFolderexsiste()){
						showmessageOffline();
					}else{
						myoffline.CleanPayement();
						myoffline.shynchronizePayement(list);
					}
					
				}
			}
			
			

			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();

					Log.e("List",list.size()+"");
					listFact = new ArrayList<>();
					for (int i = 0; i < list.size(); i++) {
						listFact.add(list.get(i).getNum());
					}
					addItemsOnAutoComplate();
					
					if(list.size() == 0){
						alertFact(getResources().getString(R.string.cmdtofc21));
					}

				}
				Log.e("end ","cnx");
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
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
								if(sv.getAllProduits().get(j).getRef().equals(products.get(i).getRef())){
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
					
					dialog = ProgressDialog.show(PayementActivity.this, getResources().getString(R.string.map_data),
							getResources().getString(R.string.msg_wait), true);
					new ConnexionTask().execute();
				}
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fatal_error),
						Toast.LENGTH_LONG).show();
				Log.e("Error","");
			}
		}

	}
	
	class OfflineTask extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {
			
			

			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			/*
			list = myoffline.LoadPayements("");
			
			
			List<Payement> ps = myoffline.prepaOfflinePayement(myoffline.LoadInvoice(""));
			if(ps.size() > 0){
				for (int i = 0; i < ps.size(); i++) {
					list.add(ps.get(i));
				}
			}
			*/
			list = new ArrayList<>();
			list = myoffline.prepaOfflinePayement(null);
			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();

					Log.e("List",list.size()+"");
					listFact = new ArrayList<>();
					for (int i = 0; i < list.size(); i++) {
						
						listFact.add(list.get(i).getNum());
					}
					
					if(list.size() == 0 ){
						alertFact(getResources().getString(R.string.cmdtofc21));
					}
					addItemsOnAutoComplate();

				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}


	class FactureTask extends AsyncTask<Void, Void, String> {
		private long ix;
		private FileData data;
		Reglement reg;
		String vl ="";
		@Override
		protected String doInBackground(Void... arg0) {
			
			database = new DatabaseHandler(getApplicationContext());
			reg = new Reglement(pay.getId(), compte.getIduser(), reglement , numchek.getText().toString(), Double.parseDouble(encaisse.getText().toString()));
			reg.setFk_facture(rendu.getText().toString());
			vl = manager.insertPayement(reg, compte);
			Log.e("value insert ",vl);
			
			reg.setIdreg((int)database.addrow("reg"));
			//ix = myoffline.shynchronizeReglement(reg);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					
					if(vl.equals("ok")){
						Toast.makeText(PayementActivity.this, getResources().getString(R.string.msg_add_reglement), Toast.LENGTH_LONG).show();
						
						List<Reglement> lsreg = myoffline.LoadReglement(pay.getId()+"");
						Intent intent4 = new Intent(PayementActivity.this, TicketOfflineActivity.class); //PayementActivity
						intent4.putExtra("user", compte);
						intent4.putExtra("dico", dico);
						
						intent4.putExtra("offlineticket",myoffline.LoadSociete(""));
						intent4.putExtra("mypay", pay);
						intent4.putExtra("myreg", reg);
						intent4.putExtra("clt", loadClient(pay.getSoc()) );//myoffline.LoadClients(pay.getSoc()+"").get(0)
						intent4.putExtra("lsreg", lsreg.size());
						if(lsreg.size() > 0){
							
							for (int i = 0; i < lsreg.size(); i++) {
								intent4.putExtra("reg"+i, lsreg.get(i));
							}
						}
						
						/*
						dialog2 = ProgressDialog.show(PayementActivity.this, "Synchronisation avec le serveur",
								getResources().getString(R.string.msg_wait_sys), true);
						new ServerSideTask().execute(); 
						
						*/
						
						startActivity(intent4);
						
						
						
					}else{
						alert4();
					}
					
				}
			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(PayementActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}
	
	class FactureOfflineTask extends AsyncTask<Void, Void, String> {

		private FileData data;
		private Reglement reg;
		private long ix = -1;
		@Override
		protected String doInBackground(Void... arg0) {
			
			if(!myoffline.checkFolderexsiste()){
	        	showmessageOffline();
			}else{
				database = new DatabaseHandler(getApplicationContext());
				reg = new Reglement(pay.getId(), compte.getIduser(), reglement , numchek.getText().toString(), Double.parseDouble(encaisse.getText().toString()));
				reg.setFk_facture(rendu.getText().toString());
				reg.setIdreg((int)database.addrow("reg"));
				ix = myoffline.shynchronizeReglement(reg);
			}
			
			
			
			return null;
		}
		@Override

		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();

					if(ix != -1){
						Toast.makeText(PayementActivity.this, getResources().getString(R.string.msg_add_reglement), Toast.LENGTH_LONG).show();
						
						List<Reglement> lsreg = myoffline.LoadReglement(pay.getId()+"");
						Intent intent4 = new Intent(PayementActivity.this, TicketOfflineActivity.class); //PayementActivity
						intent4.putExtra("user", compte);
						intent4.putExtra("dico", dico);
						
						intent4.putExtra("offlineticket",myoffline.LoadSociete(""));
						intent4.putExtra("mypay", pay);
						intent4.putExtra("myreg", reg);
						intent4.putExtra("clt", loadClient(pay.getSoc()) );//myoffline.LoadClients(pay.getSoc()+"").get(0)
						intent4.putExtra("lsreg", lsreg.size());
						if(lsreg.size() > 0){
							for (int i = 0; i < lsreg.size(); i++) {
								intent4.putExtra("reg"+i, lsreg.get(i));
							}
						}
						
						startActivity(intent4);
					}else{
						alert4();
						
					}
					
					
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(PayementActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}

	}

	public void alert(){

		AlertDialog.Builder alert = new AlertDialog.Builder(PayementActivity.this);
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		alert.setMessage(
				String.format(getResources().getString(R.string.cnxlab3)
						));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				double v = pay.getTotal() - pay.getAmount();
				rendu.setText(v+"");

				encaisse.setText("0");
				return;
			}
		});
		alert.create().show();
		encaisse.setText("0");
	}
	
	public void alert2(){

		AlertDialog.Builder alert = new AlertDialog.Builder(PayementActivity.this);
		alert.setTitle("Information !!");
		alert.setMessage(
				String.format("Cette facture est d�ja cloturer"
						));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				encaisse.setText("0");
				return;
			}
		});
		alert.create().show();
	}
	
	public void alert3(){

		AlertDialog.Builder alert = new AlertDialog.Builder(PayementActivity.this);
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		alert.setMessage(
				String.format(getResources().getString(R.string.cnxlab4)
						));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				encaisse.setText("");
				return;
			}
		});
		alert.create().show();
	}
	
	public void alertinvonan(){

		AlertDialog.Builder alert = new AlertDialog.Builder(PayementActivity.this);
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		alert.setMessage(
				String.format(getResources().getString(R.string.cnxlab5)
						));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				encaisse.setText("");
				return;
			}
		});
		alert.create().show();
	}
	
	public void alert4(){
/*
		AlertDialog.Builder alert = new AlertDialog.Builder(PayementActivity.this);
		alert.setTitle("Erreur d'enregistrement");
		alert.setMessage(
				String.format("Veuillez v�rifier votre enregistrement"
						));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				return;
			}
		});
		alert.create().show();
		*/
		try {
			LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.requeststorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(PayementActivity.this);
	         dialog.setView(dialogView);
	 	     dialog.setTitle(R.string.caus9);
	 	     dialog.setPositiveButton(R.string.caus8, new DialogInterface.OnClickListener() {
	 	        public void onClick(DialogInterface dialog, int which) { 
	 	        	encaisse.setText("");
	 	        	Intent intent4 = new Intent(PayementActivity.this, PayementActivity.class); 
					intent4.putExtra("user", compte);
					intent4.putExtra("dico", dico);
					startActivity(intent4);
	 	        	 dialog.cancel();
	 	        }
	 	     });
	 	     dialog.setCancelable(true);
	 	     dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		
		String selected = parent.getItemAtPosition(position).toString();
		Log.e("hopa ",selected);
		if("--Payement Type---".equals(selected)){
			reglement = "LIQ";
		}else{
			for (int i = 0; i < dico.size(); i++) {
				HashMap<String, String> values = dico.get(i);
				Log.i("Dico GetList"+i,values.toString());

				Iterator<String> keySetIterator = values.keySet().iterator();

				while(keySetIterator.hasNext()){
					String key = keySetIterator.next();
					if(selected.equals(values.get(key))){
						reglement = key;
						Log.d("Reglement Selected >>>>>>> ",key);
					}
				}
			}
		}

		Log.e("reglement ",reglement);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//reglement = "4";
		
		if(v.getId() == R.id.validation){
			
			if(encaisse.getText().toString().equals("") || encaisse.getText().toString().equals("0")){
				alert3();
			}else{
				
				Payement pay2 = new Payement();
				
				for (int i = 0; i < list.size(); i++) {

					if (list.get(i).getNum().equals(allfacturesdata.getText().toString())) {
						pay2 = list.get(i);
						break;
					}

				}
				
				if(pay.getId() > 0 && pay2.getId() > 0 && (pay.getId() == pay2.getId())){
					double rs = pay.getTotal() - pay.getAmount();
					double ms = rs - Double.parseDouble(encaisse.getText().toString());
					Log.e("rs>> ",+rs+"");
					Log.e("ms>> ",+ms+"");
					Log.e("rendu",rendu.getText().toString());
					if( ms >= 0 && (Double.parseDouble(rendu.getText().toString()) == ms) ){
						
						
						if(CheckOutNet.isNetworkConnected(getApplicationContext())){
							if(myoffline.checkPayement_is_Invoice(pay.getId()) == -1){
								dialog = ProgressDialog.show(PayementActivity.this, "Insertion"+getResources().getString(R.string.facture),
										getResources().getString(R.string.msg_wait), true);
								new ValidationTask().execute();
							}else{
								dialog = ProgressDialog.show(PayementActivity.this, "Insertion"+getResources().getString(R.string.facture),
										getResources().getString(R.string.msg_wait), true);
								new ValidationOfflineTask().execute();
							}
						}else{
							dialog = ProgressDialog.show(PayementActivity.this, "Insertion"+getResources().getString(R.string.facture),
									getResources().getString(R.string.msg_wait), true);
							new ValidationOfflineTask().execute();
						}
					}else{
						alertinvonan();
					}
				}else{
					alertinvonan();
				}
				
			}
			
			Log.e("hona vl","not tcc");
		}

		if(v.getId() == R.id.validation2tc){
			
			if(encaisse.getText().toString().equals("") || encaisse.getText().toString().equals("0")){
				alert3();
			}else{
				
				Payement pay2 = new Payement();
				
				for (int i = 0; i < list.size(); i++) {

					if (list.get(i).getNum().equals(allfacturesdata.getText().toString())) {
						pay2 = list.get(i);
						break;
					}

				}
				
				if(pay.getId() > 0 && pay2.getId() > 0 && (pay.getId() == pay2.getId())){
					double rs = pay.getTotal() - pay.getAmount();
					double ms = rs - Double.parseDouble(encaisse.getText().toString());
					Log.e("rs>> ",+rs+"");
					Log.e("ms>> ",+ms+"");
					Log.e("rendu",rendu.getText().toString());
					if( ms >= 0 && (Double.parseDouble(rendu.getText().toString()) == ms) ){
						
						if(CheckOutNet.isNetworkConnected(getApplicationContext())){
							if(myoffline.checkPayement_is_Invoice(pay.getId()) == -1){
								dialog = ProgressDialog.show(PayementActivity.this, "Insertion"+getResources().getString(R.string.facture),
										getResources().getString(R.string.msg_wait), true);
								new FactureTask().execute();
							}else{
								dialog = ProgressDialog.show(PayementActivity.this, "Insertion"+getResources().getString(R.string.facture),
										getResources().getString(R.string.msg_wait), true);
								new FactureOfflineTask().execute();
							}
						}else{
							dialog = ProgressDialog.show(PayementActivity.this, "Insertion"+getResources().getString(R.string.facture),
									getResources().getString(R.string.msg_wait), true);
							new FactureOfflineTask().execute();
						}
					}else{
						alertinvonan();
					}
				}else{
					alertinvonan();
				}
				
			}
			
			Log.e("hona tc","tcc");
		}
		
	}
	 
	
	private Client loadClient(int id){
		Client cl = new Client();//new Client();
		
		List<Client> ls = myoffline.LoadClients("");
		Log.e("soc >9bal> ",""+ls.size());
		
		List<Client> pss =new ArrayList<>();
		pss = myoffline.prepaOfflineClient(myoffline.LoadProspection(""));
		
		if(pss.size() > 0){
			ls.addAll(pss);
		}
		
		
		Log.e("soc >> ",""+id);
		
		Log.e("soc all ",ls.toString());
		for (int i = 0; i < ls.size(); i++) {
			if(ls.get(i).getId() == id){
				cl = ls.get(i);
			}
		}
		return cl;
	}
	
	public String calculIdInvoice(){
		String res="";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			String sdt1 = "";//calendar.get(Calendar.YEAR)+"";

			int m = calendar.get(Calendar.MONTH)+1;
			int d = calendar.get(Calendar.DAY_OF_MONTH);
			int hr = calendar.get(Calendar.HOUR_OF_DAY);
			int mnt = calendar.get(Calendar.MINUTE);
			int sec = calendar.get(Calendar.SECOND);
			//System.out.println("sdt 1 "+sdt1+" mon "+m+ " d "+d);

			if(m == 0)m=12;
			if(m < 10){
				sdt1+="0"+m+"";
			}else{
				sdt1+=m+"";
			}

			if(d < 10){
				sdt1+="0"+d;
			}else{
				sdt1+=d;
			}
			
			if(hr < 10){
				sdt1+="0"+hr;
			}else{
				sdt1+=""+hr;
			}
			
			if(mnt < 10){
				sdt1+="0"+mnt;
			}else{
				sdt1+=""+mnt;
			}
			
			if(sec < 10){
				sdt1+="0"+sec;
			}else{
				sdt1+=""+sec;
			}
			
			res = sdt1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return res;
	}
	
	public void showmessageOffline(){
		try {
			 
	         LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.msgstorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(PayementActivity.this);
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
	
	class ValidationOfflineTask extends AsyncTask<Void, Void, String> {

		private long ix = -1;
		private FileData data;
		Reglement reg;
		String vl ="";
		@Override
		protected String doInBackground(Void... arg0) {
			
			
				if(!myoffline.checkFolderexsiste()){
		        	showmessageOffline();
				}else{
					database = new DatabaseHandler(getApplicationContext());
					reg = new Reglement(pay.getId(), compte.getIduser(), reglement , numchek.getText().toString(), Double.parseDouble(encaisse.getText().toString()));
					reg.setFk_facture(rendu.getText().toString());
					reg.setIdreg((int)database.addrow("reg"));
					ix = myoffline.shynchronizeReglement(reg);
				}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					
					if(ix != -1){
						Toast.makeText(PayementActivity.this, getResources().getString(R.string.msg_add_reglement), Toast.LENGTH_LONG).show();
						
						List<Reglement> lsreg = myoffline.LoadReglement(pay.getId()+"");
						Intent intent4 = new Intent(PayementActivity.this, ValiderActivity.class); //PayementActivity
						intent4.putExtra("user", compte);
						intent4.putExtra("dico", dico);
						
						intent4.putExtra("offlineticket",myoffline.LoadSociete(""));
						intent4.putExtra("mypay", pay);
						intent4.putExtra("myreg", reg);
						intent4.putExtra("typeaction", 2);
						intent4.putExtra("clt", loadClient(pay.getSoc()) );//myoffline.LoadClients(pay.getSoc()+"").get(0)
						intent4.putExtra("lsreg", lsreg.size());
						if(lsreg.size() > 0){
							
							for (int i = 0; i < lsreg.size(); i++) {
								intent4.putExtra("reg"+i, lsreg.get(i));
							}
						}
						
						/*
						dialog2 = ProgressDialog.show(PayementActivity.this, "Synchronisation avec le serveur",
								getResources().getString(R.string.msg_wait_sys), true);
						new ServerSideTask().execute(); 
						
						*/
						
						startActivity(intent4);
						
						
						
					}else{
						alert4();
					}
					
					
				}
			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(PayementActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}


	}
	
	class ValidationTask extends AsyncTask<Void, Void, String> {

		private long ix;
		private FileData data;
		Reglement reg;
		String vl ="";
		@Override
		protected String doInBackground(Void... arg0) {
			
			database = new DatabaseHandler(getApplicationContext());
			reg = new Reglement(pay.getId(), compte.getIduser(), reglement , numchek.getText().toString(), Double.parseDouble(encaisse.getText().toString()));
			reg.setFk_facture(rendu.getText().toString());
			vl = manager.insertPayement(reg, compte);
			Log.e("value insert ",vl);
			
			reg.setIdreg((int)database.addrow("reg"));
			//ix = myoffline.shynchronizeReglement(reg);
			
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
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					
					if(vl.equals("ok")){
						
						Intent intent1 = new Intent(PayementActivity.this, ConnexionActivity.class);
						intent1.putExtra("user", compte);
						startActivity(intent1);
						PayementActivity.this.finish();
						
						
						
					}else{
						//myoffline.shynchronizeReglement(reg);
						alert4();
					}
					
				}
			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				Toast.makeText(PayementActivity.this, getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}


	/*********************************************************************************************
	 * 							AutoComplate
	*********************************************************************************************/
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		CustomAutoCompleteTextChangedListener txt = new CustomAutoCompleteTextChangedListener(PayementActivity.this,R.layout.list_view_row,listfc);

		myAdapter = txt.onTextChanged(s, start, before, count);
		myAdapter.notifyDataSetChanged();
		allfacturesdata.setAdapter(myAdapter);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		//final TextView txt = (TextView) findViewById(R.id.cicin);
		String selected = allfacturesdata.getSelected(parent, view, position, id);
		//txt.setText(selected);
		
		Log.e(">>>> cinoss ",selected+ "");
		
		int trouve = 0;

		for (int i = 0; i < list.size(); i++) {

			
			if (list.get(i).getNum().equals(selected)) {
				pay = list.get(i);
				//allfacturesdata.setEnabled(false);
				Log.e("Text selectionner ",pay.toString());
				double v = pay.getTotal() - pay.getAmount();
				rendu.setText(v+"");
				trouve =1;
				break;
			}

		}
		if(trouve == 0) rendu.setText("0");
	}
	
	/*********************************************************************************************
	 * 							AutoComplate
	*********************************************************************************************/

	public void alertFact(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(PayementActivity.this);
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
