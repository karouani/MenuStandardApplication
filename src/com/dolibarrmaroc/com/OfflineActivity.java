package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.LayoutInflater;
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
import com.dolibarrmaroc.com.models.MyTicketBluetooth;
import com.dolibarrmaroc.com.models.MyfactureAdapter;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.ticket.FactureTicketActivity;
import com.dolibarrmaroc.com.ticket.ReglementTicketActivity;

@SuppressLint("NewApi")
public class OfflineActivity extends Activity implements OnItemClickListener,OnQueryTextListener{

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
		setContentView(R.layout.activity_offline);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Bundle objetbunble  = this.getIntent().getExtras();

		
		
		
		myoffline = new Offlineimpl(getBaseContext());
		
		
		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
			//cicin.setText(compte.getLogin() + compte.getProfile());
			meinvo = (Myinvoice)getIntent().getSerializableExtra("invo");
		}
		
		lisview = (ListView) findViewById(R.id.invos);
		lisview.setOnItemClickListener(this);
		
		
		
		/*
		ofindfact.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				/*
			
				
				lisview.setFilterText(cs.toString());
				
				factadapter.getFilter().filter(cs);
				
				//bindingData = new BinderData(WeatherActivity.this, bindingData.getMap());
				factadapter.notifyDataSetChanged();
				lisview.invalidateViews();
				lisview.setAdapter(factadapter);
				lisview.refreshDrawableState();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				//Log.e("before","before");
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				//Log.e("after","after");
			}
		});
		
		*/
		
		factdata = new ArrayList<>();
		factdatafilter = new ArrayList<>();
		//new offlineTask().execute();
		
		factadapter = new MyFactureAdapterView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e("on resume","start");
		super.onResume();
	}
	
	
	
	public void remplireListview(List<MyfactureAdapter> fc,int n){
		/*
		List<Myinvoice> m = myoffline.LoadInvoice("");
		List<String> data = new ArrayList<>();
		for(Myinvoice n:m){
			String st = n.getInvoice();
			st+="[ montant = "+n.getData().getDejaRegler()+"]";
			data.add(st);
		}
		myoffline.updateProduits(meinvo);
		ArrayAdapter<String> codeLearnArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
		*/
		if(n == 0){
		Log.e("fc siz****e ",factdata.size()+"");
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
		dialog = ProgressDialog.show(OfflineActivity.this, getResources().getString(R.string.map_data),
				getResources().getString(R.string.msg_wait), true);
		
			new offlineTask().execute();
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.offline, menu);
		
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
			OfflineActivity.this.finish();
			Intent intent1 = new Intent(OfflineActivity.this, HomeActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
		}
		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
		*/
		
		switch (item.getItemId()) {
		/************************MENU Carte *****************************************/
		case R.id.reglofflineticket:
			Intent photosIntent = new Intent(this, ReglementOfflineActivity.class);
			photosIntent.putExtra("user", compte);
			startActivity(photosIntent);
			break;

		}
		return true;
	}
	class offlineTask extends AsyncTask<Void, Void, String> {


		@Override
		protected String doInBackground(Void... params) {

			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			/*
			List<Myinvoice> m = myoffline.LoadInvoice("");
			for(Myinvoice n:m){
				//Log.e("inoffline",n.toString());
				factdata.add(new MyfactureAdapter(n.getData().getClient(), n.getInvoice(), n.getTotal_ticket().getTotal_ttc()+"", n.getAmount(),n.getInvoid()));
			}
			
			*/
			
			factdata = new ArrayList<>();
			List<MyTicketBluetooth> mx = myoffline.LoadBluetooth("");
			for(MyTicketBluetooth mm:mx){
				factdata.add(new MyfactureAdapter(mm.getTicket().getClient(), mm.getTicket().getNumFacture(), mm.getMe().getTotal_ticket().getTotal_ttc()+"", mm.getMe().getAmount(),mm.getMe().getInvoid()));
			}
			
		
			/*
			for(Prospection p:myoffline.LoadProspection("")){
				Log.e(">> prs ",p.toString());
			}
			
			for(Client c:myoffline.LoadClients("")){
				Log.e("client " +c.getName(),""+c);
			}
			
			*/
			/*
			Gson gson = new Gson();
			
			List<Promotion> p = new ArrayList<>();
			p.add(new Promotion(1, 1, 1, 1));
			p.add(new Promotion(2, 2, 2, 2));
			p.add(new Promotion(3, 3, 3,3));
			
			MyProduitPromo pr = new MyProduitPromo(1, p);
			
			Log.e("in gson >> ",gson.toJson(pr));
			
			*/
			
			/*
			HashMap<Integer, HashMap<Integer, Promotion>> clt = myoffline.LoadPromotion("");
			
			for(Integer cl:clt.keySet()){
    			HashMap<Integer, Promotion> m = clt.get(cl);
    			List<Promotion> promo = new ArrayList<>();
    			
    			for(Integer c:m.keySet()){
	    			Log.e("ref "+cl,">>> "+m.get(c));
	    		}
    			
    		}
			
			*/
			/*
			HashMap<Integer, List<Integer>> me = myoffline.LoadPromoClient("");
			for(Integer n:me.keySet()){
				for(Integer nn:me.get(n)){
					Log.e("client >> "+n,"promo "+nn);
				}
			}
			
			*/
			/*
			HashMap<Integer, HashMap<Integer, Promotion>> me = myoffline.LoadPromotion("");
			for(Integer n:me.keySet()){
				for(Integer nn:me.get(n).keySet()){
					Log.e("produit >> "+n,"promo "+me.get(n).get(nn));
				}
			}
			
			*/
			
			/*
			List<Myinvoice> m = myoffline.LoadInvoice("");
			for(Myinvoice n:m){
				Log.e(">> "+n.getInvoice(),n.toString());
			}
			
			*/
			/*
			Log.e("load after products", "");
			myoffline.updateProduits(meinvo);
			myoffline.LoadProduits("");
			*/
			
			
			
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
		final MyfactureAdapter selectedfact = factdata.get(position);
		 Log.e("You've selected : ",selectedfact.toString());

		
		/*
		 Dialog dialog = new Dialog(OfflineActivity.this);
         dialog.setContentView(R.layout.detailfacture);
         dialog.setTitle("Consulter d�tails de facture");
         dialog.setCancelable(true);
         //there are a lot of settings, for dialog, check them all out!

         

         //set up button
         Button button1 = (Button) dialog.findViewById(R.id.oftck);
         button1.setOnClickListener(new OnClickListener() {
         @Override
             public void onClick(View v) {
        	 Toast.makeText(getApplicationContext(), "Imprimer le ticket", Toast.LENGTH_LONG).show();
        		Intent intent1 = new Intent(OfflineActivity.this, FactureTicketActivity.class);
    			intent1.putExtra("user", compte);
    			intent1.putExtra("myticket", selectedfact.getIdfact());
    			startActivity(intent1);
             }
         });
         
         Button button2 = (Button) dialog.findViewById(R.id.ofreg);
         button2.setOnClickListener(new OnClickListener() {
         @Override
             public void onClick(View v) {
        	 showReglements(selectedfact);
             }
         });
         //now that the dialog is set up, it's time to show it    
         dialog.show();
         
         */
		
		new AlertDialog.Builder(this)
	    .setTitle("Consulter d�tails de facture")
	    .setPositiveButton(R.string.movetoregls, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	 showReglements(selectedfact);
	        }
	     })
	    .setNegativeButton(R.string.movetoticket, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	Intent intent1 = new Intent(OfflineActivity.this, FactureTicketActivity.class);
    			intent1.putExtra("user", compte);
    			intent1.putExtra("myticket", selectedfact.getIdfact());
    			startActivity(intent1);
	        }
	     })
	     .setCancelable(true)
	     .show();
         
	}
	
	private void showReglements(MyfactureAdapter selectedfact){
		Dialog dialog = new Dialog(OfflineActivity.this);
		   
		 LayoutInflater li = (LayoutInflater) this.getSystemService(OfflineActivity.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.reglementoffline, null, false);
        dialog.setContentView(v);
        dialog.setCancelable(true);
        
     
        // Include dialog.xml file
       // dialog.setContentView(R.layout.reglementoffline);//listreglementoffline
        // Set dialog title
        dialog.setTitle("1er reglement :"+selectedfact.getPayer() +" DH");

        
        regdata = new ArrayList<>();
        lisview2 = (ListView) dialog.findViewById(R.id.listView1);
       
        List<Reglement> lsreg = myoffline.LoadReglement("");
        for(Reglement r:lsreg){
       	 if(r.getId() == selectedfact.getIdfact()){
       		 regdata.add(new MyfactureAdapter(selectedfact.getRefclient(), selectedfact.getReffact(), selectedfact.getAmount()+"", r.getAmount()+"", r.getIdreg()));
       	 }
       	 
        }
        
       // regdata.add(new MyfactureAdapter(selectedfact.getRefclient(), selectedfact.getReffact(), selectedfact.getAmount()+"", selectedfact.getPayer(), selectedfact.getIdfact()));
        
        remplireListview(regdata, 1);

        
        lisview2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				final MyfactureAdapter selectedfact3 = regdata.get(position);
				Log.e("hopa x ",selectedfact3.toString());
				
    			/*
				final Dialog dialog = new Dialog(OfflineActivity.this);
    	         dialog.setContentView(R.layout.detailfacture);
    	         dialog.setTitle("Plus d'information");
    	         dialog.setCancelable(true);
    	         //there are a lot of settings, for dialog, check them all out!

    	         

    	         //set up button
    	         Button button1 = (Button) dialog.findViewById(R.id.oftck);
    	         button1.setOnClickListener(new OnClickListener() {
    	         @Override
    	             public void onClick(View v) {
    	        	 Intent intent1 = new Intent(OfflineActivity.this, ReglementTicketActivity.class);
    	    			intent1.putExtra("user", compte);
    	    			intent1.putExtra("refreg", selectedfact3.getIdfact());
    	    			startActivity(intent1);
    	             }
    	         });
    	         
    	         Button button2 = (Button) dialog.findViewById(R.id.ofreg);
    	         button2.setText(R.string.movetoreglscncl);
    	         button2.setOnClickListener(new OnClickListener() {
    	         @Override
    	             public void onClick(View v) {
    	        	 	dialog.dismiss();
    	             }
    	         });
    	         //now that the dialog is set up, it's time to show it    
    	         dialog.show();
    	         */
    	         
    	         new AlertDialog.Builder(OfflineActivity.this)
    	 	    .setTitle("Plus d'information")
    	 	    .setPositiveButton(R.string.movetoreglscncl, new DialogInterface.OnClickListener() {
    	 	        public void onClick(DialogInterface dialog, int which) { 
    	 	        	 dialog.cancel();
    	 	        }
    	 	     })
    	 	    .setNegativeButton(R.string.movetoticket, new DialogInterface.OnClickListener() {
    	 	        public void onClick(DialogInterface dialog, int which) { 
    	 	        	Intent intent1 = new Intent(OfflineActivity.this, ReglementTicketActivity.class);
    	    			intent1.putExtra("user", compte);
    	    			intent1.putExtra("refreg", selectedfact3.getIdfact());
    	    			startActivity(intent1);
    	 	        }
    	 	     })
    	 	     .setCancelable(true)
    	 	     .show();
			}
		});
        

        dialog.show();
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
}
