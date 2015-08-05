package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.dolibarrmaroc.com.adapter.MyCmdAdapter;
import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.MyfactureAdapter;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommandeManagerFactory;

@SuppressLint("NewApi") 
public class CmdViewActivity extends Activity implements OnItemClickListener,OnQueryTextListener{

	private Compte compte;
	private ListView lisview;
	private ListView lisview2;
	
	private Offlineimpl myoffline;
	private Myinvoice meinvo;
	
	private ProgressDialog dialog;
	
	
	private List<MyfactureAdapter> factdata;
	private List<MyfactureAdapter> factdatafilter;
	private MyCmdAdapter factadapter;
	private MyCmdAdapter regldapter;
	
	private WakeLock wakelock;
	
	
	private HashMap<Integer, Commandeview> mycmd;
	private List<Commandeview> cmddata;

	private CommandeManager manager;
	
	private Commandeview cicin;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmd_view);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Bundle objetbunble  = this.getIntent().getExtras();

		manager = new CommandeManagerFactory().getManager();
		
		
		myoffline = new Offlineimpl(getBaseContext());
		
		
		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
			//cicin.setText(compte.getLogin() + compte.getProfile());
		}
		
		lisview = (ListView) findViewById(R.id.lscicincmd);
		lisview.setOnItemClickListener(this);
		
		 
		
		factdata = new ArrayList<>();
		factdatafilter = new ArrayList<>();
		//new offlineTask().execute();
		
		factadapter = new MyCmdAdapter();
		
		mycmd = new HashMap<>();
		cmddata = new ArrayList<>();
		cicin = new Commandeview();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e("on resume","start");
		super.onResume();
	}
	
	
	
	public void remplireListview(List<MyfactureAdapter> fc,int n){
		 
		if(n == 0){
		Log.e("fc siz****e ",factdata.size()+"");
		factadapter = new MyCmdAdapter(this, fc);
		
		factadapter.notifyDataSetChanged();
		lisview.invalidateViews();
		lisview.setAdapter(factadapter);
		lisview.refreshDrawableState();
		
		}else if(n == 1){
			regldapter = new MyCmdAdapter(this,fc);
			lisview2.setAdapter(regldapter);
		}
	}
	
	
	public void FilterSearch(String st){
		for(MyfactureAdapter data : factdata)
        {
            //In this loop, you'll filter through originalData and compare each item to charSequence.
            //If you find a match, add it to your new ArrayList
            //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
            if(data.getReffact().equals(st))
            {
                factdatafilter.add(data);
            }
        }            
		remplireListview(factdatafilter,0);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		//new ConnexionTask().execute();
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();
		
		Log.e("on start","start");
		if(CheckOutNet.isNetworkConnected(getApplicationContext())){
			dialog = ProgressDialog.show(CmdViewActivity.this, getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);
			
				new ConnexionTask().execute();
		}else{
			dialog = ProgressDialog.show(CmdViewActivity.this, getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);
			
				new OfflineTask().execute();
		}
		
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cmd_view, menu);
		
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(this);

		//handleIntent(getIntent());
		return super.onCreateOptionsMenu(menu);
		
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CmdViewActivity.this.finish();
			Intent intent1 = new Intent(CmdViewActivity.this, VendeurActivity.class);
			intent1.putExtra("user", compte);
			intent1.putExtra("cmd", "0");
			startActivity(intent1);
		}
		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		 
		return true;
	}
	
	class ConnexionTask extends AsyncTask<Void, Void, String> {



		@Override
		protected String doInBackground(Void... params) {

			Log.e("data begin","start data ");
			cmddata = new ArrayList<>();

			
			cmddata  = manager.charger_commandes(compte);

			myoffline.shynchronizeCommandeList(cmddata);
			 

			factdata = new ArrayList<>();

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

					mycmd = new HashMap<>();

					for (int i = 0; i < cmddata.size(); i++) {
						mycmd.put(cmddata.get(i).getRowid(), cmddata.get(i));
						factdata.add(new MyfactureAdapter(cmddata.get(i).getClt().getName(), cmddata.get(i).getRef(), cmddata.get(i).getTtc()+"",cmddata.get(i).getDt()+"", cmddata.get(i).getRowid()));
					}

					remplireListview(factdata,0);


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
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
		final MyfactureAdapter selectedfact = factdata.get(position);
		 Log.e("You've selected : ",selectedfact.toString());

		 cicin = mycmd.get(selectedfact.getIdfact());
		
		new AlertDialog.Builder(this)
	    .setTitle(getResources().getString(R.string.cmdtofc12))
	    .setNegativeButton(R.string.tecv4, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	Intent intent1 = new Intent(CmdViewActivity.this, CmdDetailActivity.class);
    			intent1.putExtra("user", compte);
    			intent1.putExtra("vc", cicin);
    			intent1.putExtra("lscmd", mycmd);
    			startActivity(intent1);
	        }
	     })
	     .setCancelable(true)
	     .show();
         
	}
	

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		Log.e("search data",""+newText);
		Log.e("util", TextUtils.isEmpty(newText)+"");
		if (TextUtils.isEmpty(newText))
		{
			lisview.clearTextFilter();
			remplireListview(factdata, 0);
		}
		else
		{
			lisview.setFilterText(newText.toString());
			factadapter.getFilter().filter(newText.toString());
			
			
			//bindingData = new BinderData(WeatherActivity.this, bindingData.getMap());
			factadapter.notifyDataSetChanged();
			lisview.invalidateViews();
			lisview.setAdapter(factadapter);
			lisview.refreshDrawableState();
		}

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		Log.e("search data","is me submit");
		return false;
	}
	
	class OfflineTask extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			Log.e("data begin","start data ");
			cmddata = new ArrayList<>();

			

			//myoffline.shynchronizeCommandeList(cmddata);
			
			cmddata = myoffline.LoadCommandeList("");
			 

			factdata = new ArrayList<>();

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

					mycmd = new HashMap<>();

					for (int i = 0; i < cmddata.size(); i++) {
						mycmd.put(cmddata.get(i).getRowid(), cmddata.get(i));
						factdata.add(new MyfactureAdapter(cmddata.get(i).getClt().getName(), cmddata.get(i).getRef(), cmddata.get(i).getTtc()+"",cmddata.get(i).getDt()+"", cmddata.get(i).getRowid()));
					}

					remplireListview(factdata,0);


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
}	