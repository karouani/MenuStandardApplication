package com.dolibarrmaroc.com.stocks;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.dolibarrmaroc.com.ConnexionActivity;
import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.R.id;
import com.dolibarrmaroc.com.R.layout;
import com.dolibarrmaroc.com.R.string;
import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Mouvement;
import com.dolibarrmaroc.com.models.MouvementGrabage;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.MouvementManagerFactory;
import com.dolibarrmaroc.com.dashboard.HomeActivity;
import com.dolibarrmaroc.com.database.StockVirtual;
import com.dolibarrmaroc.com.offline.Offlineimpl;

import android.support.v7.app.ActionBarActivity;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TransfertvirtualstockActivity extends Activity {

	private PowerManager.WakeLock wl;	
	private Dialog dialog;
	
	private List<Produit> products,panier;
	private StockVirtual sv;
	private int type_mv;
	
	private SimpleAdapter adapter;
	private List<HashMap<String, String>> fillMaps;
	
	private Button save;
	private ListView listme;
	
	private RadioButton btn1,btn2;
	
	private Compte compte;
	private MouvementManager stockManager;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfertvirtualstock);
		
		

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
		
		try {
			
			Bundle objetbunble  = this.getIntent().getExtras();
			

			if (objetbunble != null) {
				compte =  (Compte) getIntent().getSerializableExtra("user");
			}	
			
			products = new ArrayList<>();
			panier = new ArrayList<>();
			
			type_mv = -1;
			
			save = (Button)findViewById(R.id.savetransfersv);
			save.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.e("mv",type_mv+"");
					if(panier.size() > 0 && type_mv != -1){
						
						if(CheckOutNet.isNetworkConnected(TransfertvirtualstockActivity.this)){
							
							
							
							new AlertDialog.Builder(TransfertvirtualstockActivity.this)
							.setTitle(getResources().getString(R.string.cmdtofc8))
							.setMessage(getResources().getString(R.string.mvm4))
							.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface d, int arg1) {
									//VendeurActivity.super.onBackPressed();
									d.dismiss();

								}

							})
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface d, int arg1) {
									//VendeurActivity.super.onBackPressed();
									 d.dismiss();
										
									 dialog = ProgressDialog.show(TransfertvirtualstockActivity.this, getResources().getString(R.string.cmdtofc10),
												getResources().getString(R.string.msg_wait), true);
									new RecuperationTask().execute();

								}

							}).setCancelable(true).create().show();
						}else{
							alert_response(getString(R.string.cmdtofc29),0);
						}
						
					}else{
						alert_response(getString(R.string.cnxlab6),0);
					}
				}
			});
			
			btn1 = (RadioButton)findViewById(R.id.dch2);
			btn1.setChecked(true);
			btn1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					type_mv = 2;
					panier = new ArrayList<>();
					panier = TrieProducts(sv.getAllProduits(2));
					adapter = getSimple(panier.size());
					adapter.notifyDataSetChanged();
					listme.setAdapter(adapter);
				}
			});
			
			btn2 = (RadioButton)findViewById(R.id.dch3);
			btn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					type_mv = 3;
					panier = new ArrayList<>();
					panier = TrieProducts(sv.getAllProduits(3));
					
					adapter = getSimple(panier.size());
					adapter.notifyDataSetChanged();
					listme.setAdapter(adapter);
				}
			});
			
			listme = (ListView)findViewById(R.id.listviewsv);
			

			dialog = ProgressDialog.show(TransfertvirtualstockActivity.this, getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);
			new ConnexionTask().execute();
			
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfertvirtualstock, menu);
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
	*/
	public SimpleAdapter getSimple(int nmb){
		// create the grid item mapping
		SimpleAdapter adap;
		String[] from = new String[] {"name", "desig","qte","pu"};
		int[] to = new int[] { R.id.item42, R.id.item12, R.id.item32 };

		// prepare the list of all records
		List<HashMap<String, String>>  fillMaps2 = new ArrayList<HashMap<String, String>>();

		
		if(nmb == 0){
			HashMap<String, String> map = new HashMap<String, String>();
		//	map.put("id", "");
			map.put("name", "");
			map.put("desig",getResources().getString(R.string.facture_vide));
			map.put("qte", "");
		//	map.put("pu", "");

			fillMaps2.add(map);
			//save.setEnabled(false);
		}else{
			for (int j = 0; j < panier.size(); j++) {
				HashMap<String, String> map = new HashMap<String, String>();
				Produit p = panier.get(j);
				//Log.e("prod",p.toString());
		//		map.put("id", p.getRef()+"");
				map.put("name", p.getRef());
				map.put("desig",p.getFk_tva());
				map.put("qte", p.getQteDispo()+"");
		//		map.put("pu", p.getPrixUnitaire());

				fillMaps2.add(map);
				//save.setEnabled(true);
			}
		}

		// fill in the grid_item layout
		
		adap = new SimpleAdapter(this, fillMaps2, R.layout.grid_item3, from, to);
		return adap;
	}
	
	
	private class ConnexionTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			products = new ArrayList<>();
			panier = new ArrayList<>();

			sv = new StockVirtual(TransfertvirtualstockActivity.this);
			
			products = sv.getAllProduits(-1);
			
			
			panier = TrieProducts(sv.getAllProduits(2));
			getSimple(panier.size());
			
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
					
					adapter = getSimple(products.size());

					listme.setAdapter(adapter);	

				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage() +" << ",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}
	
	private class RecuperationTask extends AsyncTask<Void, Void, String> {

		private String res="";
		
		@Override
		protected String doInBackground(Void... params) {

			stockManager = MouvementManagerFactory.getManager();
			
			List<Mouvement> lsmv = new ArrayList<>();
			for (int i = 0; i < panier.size(); i++) {
				lsmv.add(new Mouvement(Integer.parseInt(panier.get(i).getRef()), panier.get(i), ""+compte.getIduser(), ""+compte.getIduser(), (double)panier.get(i).getQteDispo()));
			}
			
			
			 res = stockManager.makeechange(lsmv, compte, prepa_label(),0+"",1); 
			
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
						 
						sv = new StockVirtual(TransfertvirtualstockActivity.this);
						
						for (int i = 0; i < panier.size(); i++) {
							sv.deleteProduit(panier.get(i));
						}
						alert_response(getResources().getString(R.string.mvm7),1);
					}else{
						alert_response(getResources().getString(R.string.mvm8),1);
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
			
			st = ts.getTime()+"";
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return st;
	}
	
	public void alert_response(String msg,final int n){
		AlertDialog.Builder alert = new AlertDialog.Builder(TransfertvirtualstockActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm6));
		alert.setMessage(msg);
		alert.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
				
				if(n == 1){
					Intent intent = new Intent(TransfertvirtualstockActivity.this, ConnexionActivity.class);
					intent.putExtra("user", compte);
					startActivity(intent);
					TransfertvirtualstockActivity.this.finish();
				}
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	private List<Produit> TrieProducts(List<Produit> in){
		List<Produit> tmps = new ArrayList<>();
		HashMap<Integer, Produit> tmp = new HashMap<>();
		
		int n =0;
		for (int i = 0; i < in.size(); i++) {
			
			n  = in.get(i).getQteDispo();
			if(tmp.get(Integer.parseInt(in.get(i).getRef())) == null){
				for (int j = 0; j < in.size(); j++) {
					if(in.get(i).getPrixttc() != in.get(j).getPrixttc()){
						if(Integer.parseInt(in.get(i).getRef()) == Integer.parseInt(in.get(j).getRef())){
							n += in.get(j).getQteDispo();
						}
					}
				}
				
				in.get(i).setQteDispo(n);
				
				tmp.put(Integer.parseInt(in.get(i).getRef()), in.get(i));
			}
			
		}
		
		for(Integer i:tmp.keySet()){
			tmps.add(tmp.get(i));
		}
		
		return tmps;
	}
	
	public void onClickHome(View v){
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("user", compte);
		intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity (intent);
		this.finish();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onClickHome(LayoutInflater.from(TransfertvirtualstockActivity.this).inflate(R.layout.activity_transfertvirtualstock, null));
			return true;
		}
		return false;
	}
}
