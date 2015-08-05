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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.LoadStock;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.MouvementManagerFactory;

public class TransfertstockActivity extends Activity implements OnClickListener,OnItemSelectedListener{
	
	private WakeLock wakelock;

	//Declaration Objet
	private MouvementManager stockManager;
	private Compte compte;
	private Produit produit;

	//Spinner Remplissage
	private List<String> listprd;

	//Asynchrone avec connexion 
	private ProgressDialog dialog;

	
	//synchro offline
	private Offlineimpl myoffline;

	//Declaration Interface
	private Button selectionner,save,all;
	private TextView sw,dw,vnm,dsqty,nm;
	private EditText qty;

	//private Spinner clientspinner,proSpinner;
	private AutoCompleteTextView proSpinner;

	private HashMap<String, Integer> panier = new HashMap<>();

	//Autre Variable
	private List<Produit> products,prodmv;
	
	private LinearLayout lsqnt;
	
	private LoadStock mystock;
	
	private StockVirtual sv;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();
		
		//Log.e("cpt", compte.toString()+"");
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			dialog =  ProgressDialog.show(TransfertstockActivity.this,getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);

			new ConnexionTask().execute();
		}else{
			alert_network();
		}
		
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfertstock);
		
		try {
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");

			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
				
		stockManager = MouvementManagerFactory.getManager();
		
		mystock = new LoadStock();
		
		listprd = new ArrayList<>();
		panier = new HashMap<>();
		products = new ArrayList<>();
		prodmv  = new ArrayList<>();
		
		sw = (TextView) findViewById(R.id.trns4);
		dw = (TextView) findViewById(R.id.trns6);
		dsqty = (TextView) findViewById(R.id.labelqntdispo4);
		nm = (TextView) findViewById(R.id.trns2);
		
		qty = (EditText) findViewById(R.id.qte);
		qty.setEnabled(false);
		
		selectionner = (Button) findViewById(R.id.gotransfertstck);
		selectionner.setOnClickListener(this);
		selectionner.setEnabled(false);
		
		save = (Button) findViewById(R.id.transfertstck);
		save.setOnClickListener(this);
		save.setEnabled(false);
		
		all = (Button) findViewById(R.id.transfertstckall);
		all.setOnClickListener(this);
		
		proSpinner =  (AutoCompleteTextView) findViewById(R.id.produitspinner);
		
		lsqnt = (LinearLayout) findViewById(R.id.linearLayout4);
		lsqnt.setVisibility(View.GONE);
		
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

				for (int i = 0; i < products.size(); i++) {
					if (selected.equals(products.get(i).getDesig())) {
						produit = new Produit();
						produit = products.get(i);
						
						/*
						if(panier.size() > 0){
							if(panier.containsKey(products.get(i).getRef())){
								produit.setQteDispo(panier.get(products.get(i).getRef()));
							}
						}
						*/

						Log.e("Text selectionner ",produit.toString());

						qty.setEnabled(true);
						
						lsqnt.setVisibility(View.VISIBLE);
						dsqty.setText(""+produit.getQteDispo());
						
						//selectionner.setEnabled(true);

						break;
					}
				}

			}
		});
		
		qty.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String qteString = getResources().getString(R.string.field_qte);

				if(!qteString.equals(qty.getText().toString()) && !"".equals(qty.getText().toString())){
					String prix = "";
					
						prix = qty.getText().toString();

						if (produit.getQteDispo() >= Integer.parseInt(prix)) {
							selectionner.setEnabled(true);
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
		
		sv  = new StockVirtual(TransfertstockActivity.this);
		
	}
	
	class ConnexionTask extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			//Log.i("Produit ",vendeurManager.selectAllProduct(compte).toString());

			
			mystock = stockManager.laodStock(compte);
			
			for (int i = 0; i < mystock.getLsprod().size(); i++) {
				for (int j = 0; j < sv.getAllProduits().size(); j++) {
					if(mystock.getLsprod().get(i).getRef().equals(sv.getAllProduits().get(j).getId())){
						mystock.getLsprod().get(i).setQteDispo(mystock.getLsprod().get(i).getQteDispo() - sv.getAllProduits().get(j).getQteDispo());
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
					if(mystock != null){
						listprd = new ArrayList<>();
						if(mystock.getSw() == -1 || mystock.getDw() == -1){
							alert_error(getResources().getString(R.string.mvm9));
						}else if(mystock.getSw() != -1 && mystock.getDw() != -1 && mystock.getLsprod().size() == 0){
							alert_error(getResources().getString(R.string.mvm12));
						}else{
							products = mystock.getLsprod();
							for (int i = 0; i < products.size(); i++) {
								listprd.add(products.get(i).getDesig());
							}
							
							addItemsOnSpinner(proSpinner,1);
							
							nm.setText(mystock.getName_vend());
							sw.setText(mystock.getVname());
							dw.setText(mystock.getSname());
						}
					}else{
						alert_error(getResources().getString(R.string.mvm10));
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
	
	
	public void addItemsOnSpinner(AutoCompleteTextView s,int type) {

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listprd);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			s.setAdapter(dataAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfertstock, menu);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.gotransfertstck){
			
			if(produit != null){
				if(produit.getId() != 0 && !qty.getText().toString().equals("")){
					int is = -1;
					
					if(prodmv.size() > 0){
						for (int i = 0; i < prodmv.size(); i++) {
							if(prodmv.get(i).getId() == produit.getId()){
								prodmv.get(i).setQtedemander(prodmv.get(i).getQtedemander()+Integer.parseInt(qty.getText().toString()));
								is = 0;
								break;
							}
						}
						
						if(is == -1){
							produit.setQtedemander(Integer.parseInt(qty.getText().toString()));
							prodmv.add(produit);
						}
					}else{
						produit.setQtedemander(Integer.parseInt(qty.getText().toString()));
						prodmv.add(produit);
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
			save.setEnabled(true);
			lsqnt.setVisibility(View.GONE);
			
		}else if(v.getId() == R.id.transfertstck){
			for (int i = 0; i < prodmv.size(); i++) {
				Log.e("panier >> ",prodmv.get(i).toString());
			}
			
			Intent i = new Intent(TransfertstockActivity.this, ViewstockActivity.class);
			HashMap<String, List<Produit>> data = new HashMap<>();
			
			data.put("prods", products);
			data.put("panier", prodmv);
			
			i.putExtra("data", data);
			i.putExtra("user", compte);
			i.putExtra("stock", mystock);
			startActivity(i);
			
		}else if(v.getId() == R.id.transfertstckall){
			prodmv = new ArrayList<>();
			for (int i = 0; i < products.size(); i++) {
				produit = products.get(i);
				produit.setQtedemander(products.get(i).getQteDispo());
				prodmv.add(produit);
			}
			Intent i = new Intent(TransfertstockActivity.this, ViewstockActivity.class);
			HashMap<String, List<Produit>> data = new HashMap<>();
			
			data.put("prods", products);
			data.put("panier", prodmv);
			
			i.putExtra("data", data);
			i.putExtra("user", compte);
			i.putExtra("stock", mystock);
			startActivity(i);
			
		}
		
	}
	
	public void alert(){
		AlertDialog.Builder alert = new AlertDialog.Builder(TransfertstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm2));
		alert.setMessage(
				String.format(
						getResources().getString(R.string.mvm2_stock),
						produit.getQteDispo()));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectionner.setEnabled(false);
				qty.setText("");
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	public void alert_produit(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(TransfertstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm2));
		alert.setMessage(msg);
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectionner.setEnabled(false);
				qty.setText("");
				qty.setEnabled(false);
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, ConnexionActivity.class);
		intent.putExtra("user", compte);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		TransfertstockActivity.this.finish();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, ConnexionActivity.class);
			intent.putExtra("user", compte);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			TransfertstockActivity.this.finish();
			return true;
		}
		return false;
	}
	
	 
	
	public void alert_network(){
		AlertDialog.Builder alert = new AlertDialog.Builder(TransfertstockActivity.this);
		alert.setTitle(getResources().getString(R.string.msg_network));
		alert.setMessage(getResources().getString(R.string.msg_network_alert));
		alert.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
				
				Intent intent = new Intent(TransfertstockActivity.this, ConnexionActivity.class);
				intent.putExtra("user", compte);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				TransfertstockActivity.this.finish();
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	public void alert_error(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(TransfertstockActivity.this);
		alert.setTitle(getResources().getString(R.string.error));
		alert.setMessage(msg);
		alert.setNegativeButton("Lancer � nouveau", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
				
				Intent intent = new Intent(TransfertstockActivity.this, ConnexionActivity.class);
				intent.putExtra("user", compte);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				TransfertstockActivity.this.finish();
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
}
