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
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommandeManagerFactory;
import com.dolibarrmaroc.com.utils.URL;

public class ViewcommandeActivity extends Activity {

	private ioffline myoffline;
	private PowerManager.WakeLock wakelock;
	private Compte compte;


	private ListView spinnere;
	private Button pdf,tck;

	private List<Produit> demander,promotion;

	private HashMap<String, Commandeview> mycmd;
	private List<String> myrefs;

	private ProgressDialog dialog;

	private AutoCompleteTextView allcmd;
	private TextView ttc;
	private LinearLayout lc;
	private TextView tc;

	private CommandeManager manager;

	private Commandeview v;

	private SimpleAdapter adapter;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewcommande);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wakelock.acquire();


		myoffline = new Offlineimpl(getApplicationContext());

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}


		manager = new CommandeManagerFactory().getManager();


		Bundle objetbunble  = this.getIntent().getExtras();

		if (objetbunble != null) {
			compte = (Compte) getIntent().getSerializableExtra("user");
		}


		spinnere = (ListView)findViewById(R.id.listview);

		allcmd = (AutoCompleteTextView)findViewById(R.id.autocomplate);

		ttc = (TextView)findViewById(R.id.textView4);

		lc = (LinearLayout)findViewById(R.id.linearLayout1);
		lc.setVisibility(View.GONE);

		pdf = (Button)findViewById(R.id.pdfout);
		tck = (Button)findViewById(R.id.ticketout);

		pdf.setEnabled(false);
		pdf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(v != null){
					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						Log.e(">>> pdf", v.getRef());
						Intent intent = new Intent(ViewcommandeActivity.this,ImprimerActivity.class);
						intent.putExtra("compte", compte);
						intent.putExtra("pdf", URL.URL+"test_uploads/"+v.getRef()+".pdf");
						intent.putExtra("fichier", URL.URL+"test_uploads/"+v.getRef()+".pdf");
						startActivity(intent);
					}else{
						showMsgPDF(0);
					}
				}else{
					showMsgPDF(1);
				}

			}
		});

		tck.setEnabled(false);
		tck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View vx) {
				// TODO Auto-generated method stub
				if(v != null){
					Intent intent = new Intent(ViewcommandeActivity.this,CommandeViewTicketActivity.class);
					intent.putExtra("compte", compte);
					intent.putExtra("cmd", v);

					HashMap<String, List<Produit>> px = new HashMap<>();

					List<Produit> pn = v.getLsprods();
					for (int j = 0; j < pn.size(); j++) {
						if(pn.get(j).getTva_tx() != null){
							if(Double.parseDouble(pn.get(j).getTva_tx()) > 0){
								promotion.add(pn.get(j));
							}else{
								demander.add(pn.get(j));
							}
						}
					}

					Log.e("zen",demander.size()+" # "+promotion.size());

					px.put("prod", demander);
					px.put("promo", promotion);

					intent.putExtra("prod", px);

					startActivity(intent);
				}else{
					showMsgPDF(1);
				}
			}
		});

		mycmd = new HashMap<>();
		demander = new ArrayList<>();
		promotion = new ArrayList<>();
		myrefs = new ArrayList<>();

	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if(!myoffline.checkFolderexsiste()){
			showmessageOffline();
		}

		if(CheckOutNet.isNetworkConnected(getApplicationContext())){

			dialog = ProgressDialog.show(ViewcommandeActivity.this, getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);
			Log.e("begin","start data");
			new ConnexionTask().execute();
		}else{

			dialog = ProgressDialog.show(ViewcommandeActivity.this, getResources().getString(R.string.map_data),
					getResources().getString(R.string.msg_wait), true);
			new OfflineTask().execute();
		}

		super.onStart();
	}

	public void addItemsOnAutoComplate() {


		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, myrefs);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		allcmd.setAdapter(dataAdapter);


		allcmd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				String selected = (String) parent.getItemAtPosition(position);

				allcmd.showDropDown();

				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);

				allcmd.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});


				Log.e("Selected Client Spinner ",selected);


				v = mycmd.get(selected);

				Log.e("Selected ient  ",v.getLsprods().size()+"");
				if(v != null){
					lc.setVisibility(View.VISIBLE);
					adapter = getSimple();

					spinnere.setAdapter(adapter);	

					ttc.setText("Total TTC : "+v.getTtc());

					pdf.setEnabled(true);
					tck.setEnabled(true);
				}
				Log.e(">> data ",v.toString());


			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.viewcommande, menu);
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


 

	public void showmessageOffline(){
		try {

			LayoutInflater inflater = this.getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.msgstorage, null);

			AlertDialog.Builder dialog =  new AlertDialog.Builder(ViewcommandeActivity.this);
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

	class ConnexionTask extends AsyncTask<Void, Void, String> {

		private List<Commandeview> cm;


		@Override
		protected String doInBackground(Void... params) {

			Log.e("data begin","start data ");
			cm = new ArrayList<>();

			
			cm  = manager.charger_commandes(compte);

			myoffline.shynchronizeCommandeList(cm);
			 

			//cm = myoffline.LoadCommandeList("");

			Log.e(">>> ",cm.size()+"");

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
					demander = new ArrayList<>();
					promotion = new ArrayList<>();
					myrefs = new ArrayList<>();


					for (int i = 0; i < cm.size(); i++) {
						mycmd.put(cm.get(i).getRef(), cm.get(i));
						Log.e(">>> ",cm.get(i).getRef()+"");
						myrefs.add(cm.get(i).getRef());


					}


					addItemsOnAutoComplate();

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

	class OfflineTask extends AsyncTask<Void, Void, String> {


		private List<Commandeview> cm;


		@Override
		protected String doInBackground(Void... params) {

			Log.e("data begin","start data ");
			cm = new ArrayList<>();

			cm = myoffline.LoadCommandeList("");

			Log.e(">>> ",cm.size()+"");

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
					demander = new ArrayList<>();
					promotion = new ArrayList<>();
					myrefs = new ArrayList<>();


					for (int i = 0; i < cm.size(); i++) {
						mycmd.put(cm.get(i).getRef(), cm.get(i));
						Log.e(">>> ",cm.get(i).getRef()+"");
						myrefs.add(cm.get(i).getRef());


					}


					addItemsOnAutoComplate();

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


	public SimpleAdapter getSimple(){
		// create the grid item mapping
		SimpleAdapter adap;
		String[] from = new String[] {"desig","qte","pu","rem"};
		int[] to = new int[] { R.id.item12, R.id.item42, R.id.item32 , R.id.item22};

		// prepare the list of all records
		List<HashMap<String, String>>  fillMaps2 = new ArrayList<HashMap<String, String>>();

		if(v.getLsprods().size() == 0){
			HashMap<String, String> map = new HashMap<String, String>();
			//map.put("name", "");
			map.put("desig",getResources().getString(R.string.facture_vide));
			map.put("qte", "");
			map.put("pu", "");
			map.put("rem", "");

			fillMaps2.add(map);
		}else{
			for (int j = 0; j < v.getLsprods().size(); j++) {
				HashMap<String, String> map = new HashMap<String, String>();
				Produit p = v.getLsprods().get(j);
				//map.put("name", p.getRef());
				map.put("desig",p.getDesig());
				map.put("qte", p.getQteDispo()+"");
				map.put("pu", p.getPrixttc()+"");
				if(Double.parseDouble(p.getTva_tx()) == 100){
					map.put("rem", "Offert");
				}else if(Double.parseDouble(p.getTva_tx()) == 0){
					map.put("rem", "--");
				}else{
					map.put("rem", p.getTva_tx());
				}

				fillMaps2.add(map);
			}
		}

		// fill in the grid_item layout
		adap = new SimpleAdapter(this, fillMaps2, R.layout.grid_item2, from, to);
		return adap;
	}
	
	private void showMsgPDF(int n){
		
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(ViewcommandeActivity.this);
		localBuilder
		.setTitle("Message d'information")
		.setCancelable(false)
		.setPositiveButton("Bien compris",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
			}
		});
		
		if(n == 0){
			localBuilder.setMessage("Cette option n'est pas disponible pour le moment. \n Veuillez vous connecter � Internet");
		}else{
			localBuilder.setMessage("Veuillez selectionner un num�ro de commande valide avant de commencer votre traitement");
		}
		
		
		
		localBuilder.show();
	}

	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent1 = new Intent(ViewcommandeActivity.this, CatalogeActivity.class);
			intent1.putExtra("user", compte);
			intent1.putExtra("cmd", "1");
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent1);

			ViewcommandeActivity.this.finish();
		}
		return false;
	}
}
