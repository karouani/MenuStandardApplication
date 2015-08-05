package com.dolibarrmaroc.com;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.LoadStock;
import com.dolibarrmaroc.com.models.Mouvement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.MouvementManagerFactory;

public class ViewstockActivity extends Activity implements OnItemClickListener,OnClickListener,OnItemSelectedListener{

	private MouvementManager stockManager;
	
	private List<Produit> lsprod;
	private List<Produit> lsmove;
	
	private Button add,save;
	
	private Dialog dialog;
	
	private AutoCompleteTextView proSpinner;
	private ListView myview;
	
	private PowerManager.WakeLock wl;
	
	private Compte compte;
	
	private List<String> nameprod;
	private Produit produit;
	
	private HashMap<String, List<Produit>> data;
	
	private SimpleAdapter adapter;
	
	private Button btn1,btn2;
	private EditText qty;
	
	private LoadStock mystock;
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewstock);
		
		
		
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wl.acquire();
		
		
		
		data = new HashMap<>();
		
		lsprod = new ArrayList<>();
		lsmove = new ArrayList<>();
		
		try {
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				data = (HashMap<String, List<Produit>>) getIntent().getSerializableExtra("data");
				
				lsprod = data.get("prods");
				lsmove = data.get("panier");
				
				
				mystock = (LoadStock) getIntent().getSerializableExtra("stock");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		add = (Button) findViewById(R.id.addnew);
		add.setOnClickListener(this);
		
		save = (Button) findViewById(R.id.savedata);
		save.setOnClickListener(this);
		
		
		
		myview = (ListView) findViewById(R.id.listview);
		
		
		adapter = getSimple(lsmove.size());

		myview.setAdapter(adapter);	

		myview.setOnItemClickListener(this);
		
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.stocklayout);
		dialog.setTitle("Ajouter un autre transfert");
		proSpinner =  (AutoCompleteTextView) dialog.findViewById(R.id.secprod);
		
		qty = (EditText) dialog.findViewById(R.id.qtysec);
		qty.setEnabled(false);

		qty.setText("");

		btn1 = (Button) dialog.findViewById(R.id.addsec);
		btn2 = (Button) dialog.findViewById(R.id.savesec);
		
		//viewpackage();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.viewstock, menu);
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
	
	
	public void addItemsOnSpinner(AutoCompleteTextView s,int type) {

		List<String> d = new ArrayList<>();
		for (int i = 0; i < lsprod.size(); i++) {
			d.add(lsprod.get(i).getDesig());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, d);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId() == R.id.addnew){
			btn2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter = getSimple(lsmove.size());
					adapter.notifyDataSetChanged();
					myview.setAdapter(adapter);
					dialog.dismiss();
				}
			});

			//Button addprd = (Button) dialog.findViewById(R.id.ajouterproduitdialog);
			Log.e("spinner ",proSpinner+" >>");

			addItemsOnSpinner(proSpinner,1);
			
			//////////////**********************************Traitement PoPup************************************************////////////////////

			

			qty.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String qteString = getResources().getString(R.string.field_qte);

					if(!qteString.equals(qty.getText().toString()) && !"".equals(qty.getText().toString()) && produit != null){
						String prix = "";
						
							prix = qty.getText().toString();
							
							Log.e("qty",prix+"  ");
							
							
							Log.e(">>> sum",check_availableQnt(produit)+"");
							
							if (produit.getQteDispo() >= (check_availableQnt(produit) + Integer.parseInt(prix))) {
								btn1.setEnabled(true);
							}else{
								alert();
							}
					}

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String qteString = getResources().getString(R.string.field_qte);
					
				}
			});
			
			btn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.e("me ",qty.getText().toString()+ " **");
					if(produit != null){
						if(produit.getId() != 0 && !qty.getText().toString().equals("")){
							int is = -1;
							
							if(lsmove.size() > 0){
								for (int i = 0; i < lsmove.size(); i++) {
									if(lsmove.get(i).getId() == produit.getId()){
										lsmove.get(i).setQtedemander(lsmove.get(i).getQtedemander()+Integer.parseInt(qty.getText().toString()));
										is = 0;
										break;
									}
								}
								
								if(is == -1){
									produit.setQtedemander(Integer.parseInt(qty.getText().toString()));
									lsmove.add(produit);
								}
							}else{
								produit.setQtedemander(Integer.parseInt(qty.getText().toString()));
								lsmove.add(produit);
							}
						}else{
							alert_produit(getResources().getString(R.string.mvm11));
						}
					}else{
						alert_produit(getResources().getString(R.string.mvm1));
					}
					
					produit = null;
					qty.setText("");
					proSpinner.setText("");
					qty.setEnabled(false);
					
					adapter = getSimple(lsmove.size());
					adapter.notifyDataSetChanged();
					myview.setAdapter(adapter);
					dialog.dismiss();
				}
			});
			
			
			proSpinner.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View arg1, int position,
						long arg3) {
					String selected = (String) parent.getItemAtPosition(position);
					produit = new Produit();
					proSpinner.showDropDown();
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);
					proSpinner.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});

					Log.e("Selected Produit Spinner ",selected);

					for (int i = 0; i < lsprod.size(); i++) {
						if (selected.equals(lsprod.get(i).getDesig())) {
							produit = new Produit();
							produit = lsprod.get(i);
							
							/*
							if(panier.size() > 0){
								if(panier.containsKey(products.get(i).getRef())){
									produit.setQteDispo(panier.get(products.get(i).getRef()));
								}
							}
							*/

							Log.e("Text selectionner ",produit.toString());

							qty.setEnabled(true);

							Log.e("Text cicin ","hellos");
							//selectionner.setEnabled(true);

							break;
						}
					}

				}
			});

			dialog.show();
		}
		
		if(v.getId() == R.id.savedata){
			
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(lsmove != null && mystock != null){
					if(lsmove.size() > 0){
						
						
						AlertDialog.Builder alert = new AlertDialog.Builder(ViewstockActivity.this);
						alert.setTitle(getResources().getString(R.string.cmdtofc8));
						alert.setMessage(getResources().getString(R.string.btn_mvmstckgo));
						alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface d, int arg1) {
								//VendeurActivity.super.onBackPressed();
								d.dismiss();

							}

						});
						alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface d, int arg1) {
								//VendeurActivity.super.onBackPressed();
								 
								dialog =  ProgressDialog.show(ViewstockActivity.this,getResources().getString(R.string.map_data),
										getResources().getString(R.string.msg_wait), true);
								
								new MouvementTask().execute();
								
									
									 d.dismiss();
									
							}

						});
						alert.create();
						alert.show();
					}else{
						alert_panier();
					}
				}else{
					alert_panier();
				}
			}else{
				alert_network();
			}
			
			
		}
		
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		final HashMap<String, String> map = (HashMap<String, String>) myview.getItemAtPosition(position);
		AlertDialog.Builder adb = new AlertDialog.Builder(ViewstockActivity.this);

		//on attribut un titre � notre boite de dialogue
		adb.setTitle(getResources().getString(R.string.facture_action)); 

		//on insére un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
		if(map.size() == 0){
			adb.setMessage(getResources().getString(R.string.facture_vide));
			adb.setNegativeButton(getResources().getString(R.string.btn_cancel),  new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {  
					return;  
				} });  

			//on affiche la boite de dialogue

		}else{
			adb.setMessage(getResources().getString(R.string.facture_choice)+map.get("desig"));


			//on indique que l'on veut le bouton ok à notre boite de dialogue

			adb.setPositiveButton(getResources().getString(R.string.btn_delete), new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) { 
					Log.i(" >> List avant supprission", lsmove.toString());
					
					List<Produit> tmp = new ArrayList<>();

					
					
					for (int i = 0; i < lsmove.size(); i++) {
						if(!map.get("id").equals(""+lsmove.get(i).getId())){
							tmp.add(lsmove.get(i));
						}
					}
					
					
					
					lsmove.clear();
					lsmove = new ArrayList<>();
					
					for (int j = 0; j < tmp.size(); j++) {
						lsmove.add(tmp.get(j));
					}

					adapter = getSimple(lsmove.size());
					adapter.notifyDataSetChanged();
					myview.setAdapter(adapter);
					
					
					
					return;  
				} 
			});   
			adb.setNegativeButton(getResources().getString(R.string.btn_cancel),  new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {  
					return;  
				} });  
		}


		//on affiche la boite de dialogue

		adb.show();
	}
	
	public SimpleAdapter getSimple(int nmb){
		// create the grid item mapping
		SimpleAdapter adap;
		String[] from = new String[] {"id", "desig","qte"}; // this names whill be used to remplire data in hashmap use the same name in the key hashmap
		int[] to = new int[] { R.id.item42, R.id.item12, R.id.item32 };

		// prepare the list of all records
		List<HashMap<String, String>>  fillMaps2 = new ArrayList<HashMap<String, String>>();

		if(nmb == 0){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "");
			map.put("name", "");
			map.put("desig",getResources().getString(R.string.facture_vide));
			map.put("qte", "");

			fillMaps2.add(map);
		}else{
			for (int j = 0; j < lsmove.size(); j++) {
				HashMap<String, String> map = new HashMap<String, String>();
				Produit p = lsmove.get(j);
				
				//Log.e(">>> ",p.getId()+"#"+p.getRef());
				map.put("id", p.getId()+"");
				map.put("name", p.getRef());
				map.put("desig",p.getDesig());
				map.put("qte", p.getQtedemander()+"");

				fillMaps2.add(map);
			}
		}

		// fill in the grid_item layout
		adap = new SimpleAdapter(this, fillMaps2, R.layout.grid_item3, from, to);
		return adap;
	}
	
	
	public void alert(){
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm2));
		alert.setMessage(
				String.format(
						getResources().getString(R.string.mvm2_stock),
						produit.getQteDispo()));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				qty.setText("");
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	public void alert_produit(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm2));
		alert.setMessage(msg);
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				qty.setText("");
				qty.setEnabled(false);
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	public void alert_panier(){
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm2));
		alert.setMessage(getResources().getString(R.string.mvm5));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	/*
	public void viewpackage(){
		try {
			PackageManager pm = getPackageManager();
			List<ApplicationInfo> apps = pm.getInstalledApplications(0);
			
			List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();

			for(ApplicationInfo app : apps) {
			    //checks for flags; if flagged, check if updated system app
			    if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
			        installedApps.add(app);
			    //it's a system app, not interested
			    } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
			        //Discard this one
			    //in this case, it should be a user-installed app
			    } else {
			    	
			        installedApps.add(app);
			    }
			}
			
			for(ApplicationInfo me:installedApps){
				Log.e("cicin", me.name);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	*/
	
	

	class MouvementTask extends AsyncTask<Void, Void, String> {

		private String res;
		
		@Override
		protected String doInBackground(Void... params) {

			stockManager = MouvementManagerFactory.getManager();
			
			List<Mouvement> lsmv = new ArrayList<>();
			for (int i = 0; i < lsmove.size(); i++) {
				lsmv.add(new Mouvement(lsmove.get(i).getId(), lsmove.get(i), ""+mystock.getSw(), ""+mystock.getDw(), (double)lsmove.get(i).getQtedemander()));
			}
			
			res = stockManager.makemouvement(lsmv, compte, prepa_label());
			
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
					
					if(res.equals("100")){
						alert_response(getResources().getString(R.string.mvm7));
					}else{
						alert_response(getResources().getString(R.string.mvm8));
					}
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}
	
	public String prepa_label(){
		String st="";
		try {
			Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
			Timestamp ts = new Timestamp(cl.getTimeInMillis());
			
			st = cl.get(Calendar.YEAR)+"-"+ts.getTime()+"-Effectuer par Telephone";
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return st;
	}
	
	public void alert_response(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm6));
		alert.setMessage(msg);
		alert.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
				
				Intent intent = new Intent(ViewstockActivity.this, TransfertstockActivity.class);
				intent.putExtra("user", compte);
				startActivity(intent);
				ViewstockActivity.this.finish();
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, TransfertstockActivity.class);
		intent.putExtra("user", compte);
		startActivity(intent);
		ViewstockActivity.this.finish();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, TransfertstockActivity.class);
			intent.putExtra("user", compte);
			startActivity(intent);
			ViewstockActivity.this.finish();
			return true;
		}
		return false;
	}
	
	 
	public void alert_network(){
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewstockActivity.this);
		alert.setTitle(getResources().getString(R.string.msg_network));
		alert.setMessage(getResources().getString(R.string.msg_network_alert));
		alert.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
				
				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
	
	public int check_availableQnt(Produit p){
		int n= 0;
		
		for (int i = 0; i < lsmove.size(); i++) {
			if(lsmove.get(i).getId() == p.getId()){
				n = n + lsmove.get(i).getQtedemander();
			}
		}
		return n;
	}
	
}
