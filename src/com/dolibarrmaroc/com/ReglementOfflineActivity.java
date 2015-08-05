package com.dolibarrmaroc.com;

import java.util.ArrayList;
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

import com.dolibarrmaroc.com.adapter.MyFactureAdapterView;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.MyTicketPayement;
import com.dolibarrmaroc.com.models.MyfactureAdapter;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.ticket.ReglementTicketActivity;

@SuppressLint("NewApi")
public class ReglementOfflineActivity extends Activity implements OnItemClickListener,OnQueryTextListener {

	private Compte compte;
	private ListView lisview;
	private ListView lisview2;
	
	private Offlineimpl myoffline;
	private Myinvoice meinvo;
	
	private ProgressDialog dialog;
	
	
	private List<MyfactureAdapter> factdata;
	private List<MyfactureAdapter> regdata;
	private List<MyfactureAdapter> factdatafilter;
	private MyFactureAdapterView factadapter;
	private MyFactureAdapterView regldapter;
	
	private WakeLock wakelock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reglement_offline);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Bundle objetbunble  = this.getIntent().getExtras();

		
		
		
		myoffline = new Offlineimpl(getBaseContext());
		
		
		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
		}
		
		lisview = (ListView) findViewById(R.id.reglos);
		lisview.setOnItemClickListener(this);
		
		factdata = new ArrayList<>();
		factdatafilter = new ArrayList<>();
		//new offlineTask().execute();
		
		factadapter = new MyFactureAdapterView();
	}

	public void remplireListview(List<MyfactureAdapter> fc,int n){
		if(n == 0){
		factadapter = new MyFactureAdapterView(this, fc);
		
		factadapter.notifyDataSetChanged();
		lisview.invalidateViews();
		lisview.setAdapter(factadapter);
		lisview.refreshDrawableState();
		
		}else if(n == 1){
			regldapter = new MyFactureAdapterView(this,fc);
			lisview2.setAdapter(regldapter);
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		//new ConnexionTask().execute();
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();
		
		dialog = ProgressDialog.show(ReglementOfflineActivity.this, getResources().getString(R.string.map_data),
				getResources().getString(R.string.msg_wait), true);
		
			new offlineTask().execute();
		super.onStart();
	}
	
	
	class offlineTask extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			
			factdata = new ArrayList<>();
			List<MyTicketPayement> mx = myoffline.LoadTicketPayement("");
			for(MyTicketPayement mm:mx){
				
				//factdata.add(new MyfactureAdapter(mm.getTicket().getClient(), mm.getTicket().getNumFacture(), mm.getMypay().getTotal()+"",mm.getMyreg().getAmount()+"", Integer.parseInt(mm.getMyreg().getPaiementcode())));
				factdata.add(new MyfactureAdapter(mm.getTicket().getClient(), mm.getTicket().getNumFacture(), mm.getMypay().getTotal()+"",mm.getMyreg().getAmount()+"", mm.getMyreg().getIdreg()));
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
					remplireListview(factdata,0);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.reglement_offline, menu);
				
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
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		final MyfactureAdapter selectedfact = factdata.get(position);
		

		Log.e(">>reg ",selectedfact.toString());
		 new AlertDialog.Builder(ReglementOfflineActivity.this)
	 	    .setTitle("Plus d'information")
	 	    .setPositiveButton(R.string.movetoreglscncl, new DialogInterface.OnClickListener() {
	 	        public void onClick(DialogInterface dialog, int which) { 
	 	        	 dialog.cancel();
	 	        }
	 	     })
	 	    .setNegativeButton(R.string.movetoticket, new DialogInterface.OnClickListener() {
	 	        public void onClick(DialogInterface dialog, int which) { 
	 	        	Intent intent1 = new Intent(ReglementOfflineActivity.this, ReglementTicketActivity.class);
	    			intent1.putExtra("user", compte);
	    			intent1.putExtra("refreg", selectedfact.getIdfact());
	    			startActivity(intent1);
	 	        }
	 	     })
	 	     .setCancelable(true)
	 	     .show();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ReglementOfflineActivity.this.finish();
			Intent intent1 = new Intent(ReglementOfflineActivity.this, HomeActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
			
		}
		return false;
	}
}
