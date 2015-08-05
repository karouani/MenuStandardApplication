package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.adapter.ExpandListAdapter;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.CategorieDao;
import com.dolibarrmaroc.com.dao.CategorieDaoMysql;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.MyLocationListener;
import com.dolibarrmaroc.com.utils.TinyDB;
import com.dolibarrmaroc.com.utils.UrlImage;
import com.dolibarrmaroc.com.utils.VendeurManagerFactory;
import com.karouani.cicin.widget.AutocompleteCustomArrayAdapter;
import com.karouani.cicin.widget.CustomAutoCompleteTextChangedListener;
import com.karouani.cicin.widget.CustomAutoCompleteView;

@SuppressLint("NewApi") public class CatalogeActivity extends Activity implements OnQueryTextListener,OnItemSelectedListener ,TextWatcher,OnItemClickListener{
	
	private Compte compte;
	private CategorieDao categorie;
	
	private ExpandListAdapter ExpAdapter;
    private List<Categorie> lscats;
    private ExpandableListView ExpandList;
    
    
    private Produit produit;
    private Client client;
    private WakeLock wakelock;
	private TinyDB db;

	//Declaration Objet
	private VendeurManager vendeurManager;
	private JSONParser json;
	private GpsTracker gps;
	private Dictionnaire dico;
	private static double totalTTC = 0;
	private static double totalHT = 0;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;
	private ProgressDialog dialog2;
	
	//Spinner Remplissage
	private List<String> listclt;
	private List<String> listprd;		

	private int firstexecution;
	private HashMap<String, Integer> panier = new HashMap<>();
	
	//private Spinner clientspinner,proSpinner;
	
	
	//private AutoCompleteTextView clientspinner;
	public CustomAutoCompleteView clientspinner;
	// adapter for auto-complete
	public ArrayAdapter<String> myAdapter;
	public String[] values ;
	private List<String> listtmp = new ArrayList<String>();
	
	
	private Button seepanier;
	
	//synchro offline
	private Offlineimpl myoffline;
	private long sysnbr;
	
	private int cmdTrue=0;
	private StockVirtual sv;
	
	
	//Autre Variable
		private List<Produit> products,produitsFacture;
		private List<Client> clients;
		private int firstinstance;
		private String prix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cataloge);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		Bundle objetbunble  = this.getIntent().getExtras();

		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
			cmdTrue = Integer.parseInt(getIntent().getStringExtra("cmd"));
		}
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();

		
		ExpandList = (ExpandableListView) findViewById(R.id.exp_list);
		//ExpandList.setGroupIndicator(null);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		//this code for adjusting the group indicator into right side of the view
		ExpandList.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));
        
        
		categorie = new CategorieDaoMysql(getApplicationContext());
		lscats = new ArrayList<>();
		listclt = new ArrayList<>();
		
		dialog = ProgressDialog.show(CatalogeActivity.this, getResources().getString(R.string.map_data),
				getResources().getString(R.string.msg_wait), true);
		
		
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			new ConnexionTask().execute();
		}else{
			new OfflineTask().execute();
		}

		
		
        ExpandList.setEnabled(false);
        ExpandList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				produit = (Produit)parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
				alertPutQty();
				
				
				return false;
			}
		});
        
        produit = new Produit();
        client = new Client();
		firstexecution = 0;
		vendeurManager = VendeurManagerFactory.getClientManager();
		dico = new Dictionnaire();

		produitsFacture = new ArrayList<Produit>();

		products = new ArrayList<Produit>();
		clients = new ArrayList<Client>();
		gps = new GpsTracker();
		firstinstance = 0;

		json = new JSONParser();
		
		  db = new TinyDB(this);
	        db.clear();

		myoffline = new Offlineimpl(getBaseContext());
		
		gps = getGpsApplication();
		
		
		clientspinner = (CustomAutoCompleteView) findViewById(R.id.autoclt);
		clientspinner.setOnItemClickListener(this);
		clientspinner.addTextChangedListener(this);
		/*
		clientspinner =  (AutoCompleteTextView) findViewById(R.id.autoclt);
		clientspinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				String selected = (String) parent.getItemAtPosition(position);
				client = new Client();
				clientspinner.showDropDown();

				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);

				clientspinner.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});

				Log.e("Selected Client Spinner ",selected);

				for (int i = 0; i < clients.size(); i++) {
					client = clients.get(i);

					if (client.getName().equals(selected)) {
						client = clients.get(i);
						clientspinner.setEnabled(false);
						Log.e("Text selectionner ",client.toString());
						ExpandList.setEnabled(true);
						break;
					}

				}
			}
		});
		*/
		
		seepanier = (Button)findViewById(R.id.seesala);
		seepanier.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				if(produitsFacture.size() > 0){
					if(products.size() > 0 || products != null ){
						Intent intent = new Intent(CatalogeActivity.this, FactureActivity.class);
						//intent.putExtra"products", produitsFacture);

						Map<String, Promotion> prom = new HashMap<>();
						for (int i = 0; i < products.size(); i++) {
							Promotion pr =chargerMyPromo(client.getId(), products.get(i).getId()).get(0);//vendeurManager.getPromotions(client.getId(), products.get(i).getId()).get(0);
							prom.put( products.get(i).getRef(), pr);
						}

						db.saveMapPromotion("allpromotion", prom);

						for (int i = 0; i < products.size(); i++) {
							Produit p = products.get(i);
							intent.putExtra("products"+i, p);
							Log.d("Product Spinner >> "+i, p.toString());
						}

						for (int i = 0; i < produitsFacture.size(); i++) {
							Produit pm = produitsFacture.get(i);
							if(panier.size() > 0){
								if(panier.containsKey(pm.getRef())){
									pm.setQteDispo(panier.get(pm.getRef()));
								}
							}
							intent.putExtra("produit"+i, pm);
						}
						intent.putExtra("nmbproduct", produitsFacture.size()+"");
						intent.putExtra("nmbproducts", products.size()+"");
						intent.putExtra("total", totalTTC+"");
						intent.putExtra("idclt", client.getId()+"");
						intent.putExtra("gps", gps);
						intent.putExtra("compte", compte);

						intent.putExtra("dico", dico.getDico());

						intent.putExtra("totalht", totalHT+"");

						intent.putExtra("cmd", cmdTrue+"");
						intent.putExtra("typeinvoice", "-1");
						startActivity(intent);
						produitsFacture = new ArrayList<Produit>();
						CatalogeActivity.this.finish();
					}else{
						Toast.makeText(CatalogeActivity.this,getResources().getString(R.string.produit_min2), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(CatalogeActivity.this,getResources().getString(R.string.produit_min2), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		sv  = new StockVirtual(CatalogeActivity.this);
		
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		 
		/*
		switch (id) {
		case R.id.viewcmdpr:
			Intent intent8 = new Intent(CatalogeActivity.this, CmdViewActivity.class);
			intent8.putExtra("user", compte);
			intent8.putExtra("cmd", "1");
			startActivity(intent8);
			break;
		}
		
		*/
		return super.onOptionsItemSelected(item);
	}
	
	public void alertPutQty(){
		try {
			Builder dialog = new AlertDialog.Builder(CatalogeActivity.this);
			//dialog.setMessage(R.string.caus14);
			dialog.setTitle(produit.getDesig());
		    
			LayoutInflater inflater = this.getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.inputquantite, null);
	         
			final EditText txt = (EditText)dialogView.findViewById(R.id.inpqty2);
			final TextView t1 = (TextView)dialogView.findViewById(R.id.textView1w);
			int dx = produit.getQteDispo() - qnt_disponible(produit.getId());
			t1.setText(""+dx);
			
			final TextView t2 = (TextView)dialogView.findViewById(R.id.txw3);
			final TextView t3 = (TextView)dialogView.findViewById(R.id.textView22w);
			
			
			t3.setText(""+produit.getPrixttc());	
			
			final ImageView iv = (ImageView)dialogView.findViewById(R.id.imageView1cat);
			
			if(produit.getPhoto().equals("")){
	        	iv.setImageResource(R.drawable.nophoto);
	        }else{
	        	iv.setImageURI(Uri.parse(UrlImage.pathimg+"/produit_img/"+produit.getId()+"_"+produit.getPhoto()));
	        }
			
			
			 
			 
	         dialog.setView(dialogView);
	         dialog.setPositiveButton(R.string.addpan, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					if("".equals(txt.getText().toString())){
						Toast.makeText(CatalogeActivity.this,getResources().getString(R.string.qte_vide), Toast.LENGTH_SHORT).show();
					}else{
						int t = Integer.parseInt(txt.getText().toString());

						int tr = 0;
						for (int i = 0; i < produitsFacture.size(); i++) {
							if(produitsFacture.get(i).getDesig().equals(produit.getDesig())){
								int k = produitsFacture.get(i).getQtedemander() + t;
								produitsFacture.get(i).setQtedemander(k);
								double prt = produit.getPrixttc()*t;
								totalTTC += prt; 

								tr = 1;
								break;
							}else{
								tr = 0;
							}
						}
						if (tr == 0) {
							produit.setQtedemander(t);
							produitsFacture.add(produit);
							double prt = produit.getPrixttc()*t;
							totalTTC += prt; 
						}
						totalHT += Double.parseDouble(produit.getPrixUnitaire())*t;

						Toast.makeText(CatalogeActivity.this,getResources().getString(R.string.msg_add_product), Toast.LENGTH_SHORT).show();

						Log.e(">>>>>> --------",">>> Firstly "+produit.getQteDispo());

						if(panier.size() == 0){
							int qt = produit.getQteDispo() - Integer.parseInt(txt.getText().toString());
							panier.put(produit.getRef(), qt);
						}
						else{
							if(panier.containsKey(produit.getRef())){
								int qt = panier.get(produit.getRef()) - Integer.parseInt(txt.getText().toString());
								panier.put(produit.getRef(), qt);

							}else{
								int qt = produit.getQteDispo() - Integer.parseInt(txt.getText().toString());
								panier.put(produit.getRef(), qt);
							}

						}

						produit = new Produit();
						
						Log.e(">>>>>> --------",">>> Secondly "+produit.getQteDispo());
						Log.e(">>>>>> --------",">>> Finally "+panier.get(produit.getRef()));
					}
					
				}
			});
	         
	         
	         txt.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {

						if(!s.toString().equals("-") && !s.toString().equals(".")){
							String prix = "";
							double pr = 0;
							if("".equals(txt.getText().toString())){
								t2.setText("0");
							}else {
								prix = txt.getText().toString();
								pr = Double.parseDouble(produit.getPrixUnitaire())* Integer.parseInt(prix);

								if (produit.getQteDispo() >= Integer.parseInt(prix)) {
									t2.setText(pr+"");
								}else{
									if(produit.getId() != 0){
										txt.setText("");
										t2.setText("0");
										alert();
									}

								}
							}
						}

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						
						
						if(!s.toString().equals("-") && !s.toString().equals(".")){
							if(!"".equals(txt.getText().toString()) ){
								List<Promotion> l = chargerMyPromo(client.getId(), produit.getId()); //vendeurManager.getPromotions(client.getId(), produit.getId());
								//for (int i = 0; i < l.size(); i++) {
								Promotion promos = l.get(0);

								if(promos.getType() == 1){
									int p = promos.getPromos();
									int q = promos.getQuantite();
									int n = 0;
									int qd = 0;
									
									if(produitsFacture.contains(produit)){
										for (Produit pr:produitsFacture) {
											if(pr.getRef() == produit.getRef()){
												n = pr.getQtedemander();
												//n = n + Integer.parseInt(qte.getText().toString());
												qd = produit.getQteDispo() + n;
											}
										}
									}else{
										qd = produit.getQteDispo();
									}
									//int qd = produit.getQteDispo() + produit.getQtedemander();
									
									int d = Integer.parseInt(txt.getText().toString());
									 Log.e("Affichage",n+ ">> "+qd);
									 
									if(!checkPromo(qd,n,d,q,p)){
										alertPromos();
									}
								}
							}
						}
						
						
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.searchcatalogue, menu);
		
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(this);

		
		menu.removeItem(R.id.viewcmdpr);
		if(compte != null){
			if(compte.getPermissionbl() == 0){
				menu.removeItem(R.id.viewcmdpr);
			}
		}
		//handleIntent(getIntent());
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		Log.e("search data",""+newText);
		Log.e("util", TextUtils.isEmpty(newText)+"");
		if (TextUtils.isEmpty(newText))
		{
			ExpandList.clearTextFilter();
			remplireListview(lscats);
		}
		else
		{
			ExpandList.setFilterText(newText.toString());

			ExpAdapter.getFilter().filter(newText.toString());
			
			
			//bindingData = new BinderData(WeatherActivity.this, bindingData.getMap());

			ExpAdapter.notifyDataSetChanged();
			ExpandList.invalidateViews();
			ExpandList.setAdapter(ExpAdapter);
			ExpandList.refreshDrawableState();
		}

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void remplireListview(List<Categorie> fc ){
			ExpAdapter = new ExpandListAdapter(CatalogeActivity.this, lscats);
		
			ExpAdapter.notifyDataSetChanged();
		ExpandList.invalidateViews();
		ExpandList.setAdapter(ExpAdapter);
		ExpandList.refreshDrawableState();
	}
	
	public int GetDipsFromPixel(float pixels)
	{
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}
	
	public void alert(){
		AlertDialog.Builder alert = new AlertDialog.Builder(CatalogeActivity.this);
		alert.setTitle(getResources().getString(R.string.stock_limit));
		alert.setMessage(
				String.format(
						getResources().getString(R.string.stock_msg),
						produit.getQteDispo()));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				return;
			}
		});
		alert.create().show();
	}
	
	public GpsTracker getGpsApplication(){

		LocationManager mlocManager=null;
		MyLocationListener mlocListener;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

		if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if(MyLocationListener.latitude>0)
			{
				gps.setLangitude(""+MyLocationListener.longitude);
				gps.setLatitude(""+MyLocationListener.latitude);
				gps.setAltitude(MyLocationListener.altitude);
				gps.setDateString(MyLocationListener.dateString);
				gps.setDirection(MyLocationListener.direction);
				gps.setSatellite(MyLocationListener.satellite);
				gps.setSpeed(MyLocationListener.speed);
			}
			else
			{
				gps.setLangitude(""+0);
				gps.setLatitude(""+0);
			}
		}
		return gps;
	}



	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void addItemsOnSpinner(AutoCompleteTextView s,int type) {

		if(type == 1){
			
			/*
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listclt);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			s.setAdapter(dataAdapter);
			*/
			listtmp = new ArrayList<>();
			listtmp = listclt;
			values= new String[listclt.size()];
			for (int i = 0; i < listclt.size(); i++) {
				values[i] = listclt.get(i);
			}
			myAdapter = new AutocompleteCustomArrayAdapter(CatalogeActivity.this, R.layout.list_view_row, values);
			clientspinner.setAdapter(myAdapter);

		}else{		
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listprd);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			s.setAdapter(dataAdapter);
		}

	}
	
	public List<Promotion> chargerMyPromo(int cl,int pr){
		/*
		if(isNetworkConnected(getApplicationContext()) && (myoffline.checkClient_is_Prospect(cl) == -1)){
			return vendeurManager.getPromotions(cl,pr);
		}
		*/
		return myoffline.getPromotions(cl,pr);
	}
	
	public boolean checkPromo(int qd,int n,int d,int q,int p){
		int x = qd - (n+d);
		int gratuite = ((n+d)/p)*q;
		int  res = x - gratuite;
		if(res >= 0){
			Log.e("Voila Promos","Produits "+produit+" gratuite "+gratuite);
			return true;
		}
		else{
			Log.e("Stock Limite","Stock Limité "+x);
			return false;
		}
	}
	
	public void alertPromos(){
		List<Promotion> l = chargerMyPromo(client.getId(), produit.getId());//vendeurManager.getPromotions(client.getId(), produit.getId());
		//for (int i = 0; i < l.size(); i++) {
		Promotion p = l.get(0);

		AlertDialog.Builder alert = new AlertDialog.Builder(CatalogeActivity.this);
		alert.setTitle(getResources().getString(R.string.promo_alert));
		alert.setMessage(
				String.format(
						getResources().getString(R.string.promo_msg)+" car vous avez "+p.getQuantite()+" Produit Gratuit",
						panier.get(produit.getRef())));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				return;
			}
		});
		alert.create().show();
	}
	
	class ConnexionTask extends AsyncTask<Void, Void, String> {


		int nclt;
		int nprod;
		
		@Override
		protected String doInBackground(Void... params) {

			
			
			//Log.i("Produit ",vendeurManager.selectAllProduct(compte).toString());
			myoffline = new Offlineimpl(getApplicationContext());
			sv = new StockVirtual(CatalogeActivity.this);
			
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			/*
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(myoffline.checkAvailableofflinestorage() > 0){
					for (int i = 0; i < myoffline.LoadCmdList("").size(); i++) {
						Log.e("send cmd ",myoffline.SendCommande(myoffline.LoadCmdList("").get(i), compte)+ "" );
					}
				}
			}
			*/
			
			lscats = categorie.LoadCategories(compte);
			
			products = vendeurManager.selectAllProduct(compte);
			
			for (int i = 0; i < products.size(); i++) {
				for (int j = 0; j < sv.getAllProduits().size(); j++) {
					//Log.e(products.get(i).getId()+"",sv.getAllProduits().get(j).getRef());
					if(Integer.parseInt(sv.getAllProduits().get(j).getRef()) == products.get(i).getId()){
						Log.e("qte1 "+products.get(i).getId(),products.get(i).getQteDispo()+"");
						products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits().get(j).getQteDispo());
						Log.e("qte2 "+products.get(i).getId(),products.get(i).getQteDispo()+"");
					}
				}
				//Log.d("Produit "+i,p.toString());
			}
			nprod = products.size();
		
			
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
				myoffline.shynchronizeCategoriesList(lscats);
			}
			
			
			dico = vendeurManager.getDictionnaire();
			
			


			clients = vendeurManager.selectAllClient(compte);
			nclt = clients.size();
			
			listclt = new ArrayList<>();
			for (int i = 0; i < clients.size(); i++) {
				listclt.add(clients.get(i).getName());
			}

			
			/*********************** offline ****************************************/
			Log.e("begin offline from network",">>start load");
		
			
			
			//myoffline.CleanService();
			
			
			
			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
			}else{
				if(products.size() > 0){
					myoffline.CleanProduits();
					myoffline.CleanPromotionProduit();
					sysnbr += myoffline.shynchronizeProduits(products);
					sysnbr += myoffline.shynchronizePromotion(vendeurManager.getPromotionProduits());
				}
				
				
				if(dico.getDico().size() > 0){
					myoffline.CleanDico();
					sysnbr += myoffline.shynchronizeDico(dico);
				}
				
				if(clients.size() > 0){
					myoffline.CleanClients();
					myoffline.CleanPromotionClient();
					sysnbr += myoffline.shynchronizeClients(clients);
					sysnbr += myoffline.shynchronizePromotionClient(vendeurManager.getPromotionClients());
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
				if (dialog.isShowing()){
					dialog.dismiss();
					
					for (int i = 0; i < lscats.size(); i++) {
						lscats.get(i).setLabel(lscats.get(i).getLabel()+"["+lscats.get(i).getProducts().size()+"]");
					}
					
					ExpAdapter = new ExpandListAdapter(CatalogeActivity.this, lscats);
			        ExpandList.setAdapter(ExpAdapter);
			        
					addItemsOnSpinner(clientspinner,1);
					firstexecution = 1989;
					if(!myoffline.checkFolderexsiste() || (sysnbr == -7)){
						showmessageOffline();
					}
					
					if(myoffline.LoadClients("").size() != nclt || myoffline.LoadProduits("").size() != nprod){
						showmessageOffline();
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
	
	
	class OfflineTask extends AsyncTask<Void, Void, String> {


		int nclt;
		int nprod;
		
		@Override
		protected String doInBackground(Void... params) {

			myoffline = new Offlineimpl(getApplicationContext());
			
			lscats = myoffline.LoadCategorieList("");
			
			
			products = myoffline.LoadProduits("");
			
		
			for (int j = 0; j < lscats.size(); j++) {
				for (int i = 0; i < lscats.get(j).getProducts().size(); i++) {
					for (int k = 0; k < products.size(); k++) {
						if(lscats.get(j).getProducts().get(i).getId() == products.get(k).getId()){
							lscats.get(j).getProducts().get(i).setQteDispo(products.get(k).getQteDispo());
						}
					}
				}
			}
			
			dico = myoffline.LoadDeco("");
			
			

			Log.e("begin offline from offline",">>start load");
			
			clients = myoffline.LoadClients("");
			
			Log.e("star client 1pros ",myoffline.LoadProspection("").size()+"");
			Log.e("star client clt ",clients.size()+"");
			listclt = new ArrayList<>();
			for (int i = 0; i < clients.size(); i++) {
				listclt.add(clients.get(i).getName());
			}

				List<Prospection> pros = myoffline.LoadProspection("");
					for (int i = 0; i < pros.size(); i++) {
						if(pros.get(i).getClient() == 1){
							Client c = new Client(pros.get(i).getIdpros(), pros.get(i).getName(), pros.get(i).getZip(), pros.get(i).getTown(), pros.get(i).getEmail(), pros.get(i).getPhone(), pros.get(i).getAddress());
							clients.add(c);
							listclt.add(pros.get(i).getName());
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
					
					for (int i = 0; i < lscats.size(); i++) {
						lscats.get(i).setLabel(lscats.get(i).getLabel()+"["+lscats.get(i).getProducts().size()+"]");
					}
					
					ExpAdapter = new ExpandListAdapter(CatalogeActivity.this, lscats);
			        ExpandList.setAdapter(ExpAdapter);
			        
					addItemsOnSpinner(clientspinner,1);
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
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(CatalogeActivity.this);
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
	
	private int qnt_disponible(int id){
		int x =0;
		for (int i = 0; i < produitsFacture.size(); i++) {
			if(produitsFacture.get(i).getId() == id){
				x += produitsFacture.get(i).getQtedemander();
			}
		}
		return x;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(clients.size() > 0 && listclt.size() > 0){
			myoffline = new Offlineimpl(getApplicationContext());
			List<Prospection> pros = new ArrayList<>();
			pros = myoffline.LoadProspection("");
			if(!CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(pros.size() > 0) {
					for (int i = 0; i < pros.size(); i++) {
						if(pros.get(i).getClient() == 1){
							if(!listclt.contains(pros.get(i).getName())){
								Client c = new Client(pros.get(i).getIdpros(), pros.get(i).getName(), pros.get(i).getZip(), pros.get(i).getTown(), pros.get(i).getEmail(), pros.get(i).getPhone(), pros.get(i).getAddress());
								clients.add(c);
								listclt.add(pros.get(i).getName());
								addItemsOnSpinner(clientspinner,1);
							}
						}
					}
				}
				
			}
		}
		
		super.onResume();
	}
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.cmdtofc8))
			.setMessage(getResources().getString(R.string.tecv47))
			.setNegativeButton(android.R.string.no, null)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					//VendeurActivity.super.onBackPressed();
					Intent intent1 = new Intent(CatalogeActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);


					Intent intentService = new Intent(CatalogeActivity.this, ShowLocationActivity.class);
					stopService(intentService);

					CatalogeActivity.this.finish();

				}

			}).create().show();
		}
		return false;
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		// TODO Auto-generated method stub
		//String selected = (String) parent.getItemAtPosition(position);
		
		String selected = clientspinner.getSelected(parent, view, position, id);
		client = new Client();
		
		/*	
		clientspinner.showDropDown();
		
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);

		clientspinner.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});
		*/
		Log.e("Selected Client Spinner ",selected);

		for (int i = 0; i < clients.size(); i++) {
			client = clients.get(i);

			if (client.getName().equals(selected)) {
				client = clients.get(i);
				clientspinner.setEnabled(false);
				Log.e("Text selectionner ",client.toString());
				ExpandList.setEnabled(true);
				break;
			}

		}
	}



	/*********************************************************************************************
	 * 							AutoComplate
	*********************************************************************************************/
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		CustomAutoCompleteTextChangedListener txt = new CustomAutoCompleteTextChangedListener(CatalogeActivity.this,R.layout.list_view_row,listclt);

		myAdapter = txt.onTextChanged(s, start, before, count);
		myAdapter.notifyDataSetChanged();
		clientspinner.setAdapter(myAdapter);
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

	
}
