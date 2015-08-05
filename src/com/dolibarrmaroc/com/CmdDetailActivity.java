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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

@SuppressLint("NewApi") public class CmdDetailActivity extends Activity {

	private ioffline myoffline;
	private PowerManager.WakeLock wakelock;
	private Compte compte;


	private ListView spinnere;
	private Button pdf,tck,invo;

	private List<Produit> demander,promotion;

	private HashMap<Integer, Commandeview> mycmd;
	private List<String> myrefs;

	private ProgressDialog dialog;

	private TextView ttc;
	private LinearLayout lc;
	private TextView tc;

	private CommandeManager manager;

	private Commandeview v;

	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmd_detail);

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
			mycmd = (HashMap<Integer, Commandeview>)getIntent().getSerializableExtra("lscmd");
			v = (Commandeview)getIntent().getSerializableExtra("vc"); 
		}


		spinnere = (ListView)findViewById(R.id.listview);

		ttc = (TextView)findViewById(R.id.textView4);

		lc = (LinearLayout)findViewById(R.id.linearLayout1);
		lc.setVisibility(View.GONE);

		pdf = (Button)findViewById(R.id.pdfout);
		tck = (Button)findViewById(R.id.ticketout);
		invo = (Button)findViewById(R.id.invoyceout);

		//pdf.setEnabled(false);
		pdf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(v != null){
					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						Log.e(">>> pdf", v.getRef());
						Intent intent = new Intent(CmdDetailActivity.this,ImprimerActivity.class);
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

		//tck.setEnabled(false);
		tck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View vx) {
				// TODO Auto-generated method stub
				if(v != null){
					Intent intent = new Intent(CmdDetailActivity.this,CommandeViewTicketActivity.class);
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

		//invo.setEnabled(false);
		invo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(v != null){
					if(CheckOutNet.isNetworkConnected(getApplicationContext())){
						
						AlertDialog.Builder alert = new AlertDialog.Builder(CmdDetailActivity.this);
						alert.setTitle(getResources().getString(R.string.cmdtofc8));
						alert.setMessage(getResources().getString(R.string.cmdtofc9));
						alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface d, int arg1) {
								//VendeurActivity.super.onBackPressed();
								d.dismiss();

							}

						});
						alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface d, int arg1) {
								//VendeurActivity.super.onBackPressed();
								 
								 dialog = ProgressDialog.show(CmdDetailActivity.this, getResources().getString(R.string.map_data),
											getResources().getString(R.string.msg_wait), true);
									new ConnexionTask().execute();
									
									 d.dismiss();
									
							}

						});
						alert.create();
						alert.show();
					}else{
						
						
						AlertDialog.Builder alert = new AlertDialog.Builder(CmdDetailActivity.this);
						alert.setTitle(getResources().getString(R.string.cmdtofc8));
						alert.setMessage(getResources().getString(R.string.cmdtofc9));
						alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface d, int arg1) {
								//VendeurActivity.super.onBackPressed();
								d.dismiss();

							}

						});
						alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface d, int arg1) {
								//VendeurActivity.super.onBackPressed();
								 
									 d.dismiss();
									 myoffline = new Offlineimpl(getApplicationContext());
									 myoffline.shynchornizeCmdToFact(v);
										
									 showMsgPDF(3);
										
							}

						});
						alert.create();
						alert.show();
					}
				}else{
					showMsgPDF(1);
				}

			}
		});
		mycmd = new HashMap<>();
		demander = new ArrayList<>();
		promotion = new ArrayList<>();
		myrefs = new ArrayList<>();

		
		if(v != null){
			lc.setVisibility(View.VISIBLE);
			adapter = getSimple();

			spinnere.setAdapter(adapter);	

			ttc.setText("Total TTC : "+v.getTtc());

			pdf.setEnabled(true);
			tck.setEnabled(true);
		}
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if(!myoffline.checkFolderexsiste()){
			showmessageOffline();
		}

		super.onStart();
	}

	 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cmd_detail, menu);
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

			AlertDialog.Builder dialog =  new AlertDialog.Builder(CmdDetailActivity.this);
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
		
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CmdDetailActivity.this);
		localBuilder
		.setTitle(getResources().getString(R.string.cmdtofc10))
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.cmdtofc11),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
			}
		});
		
		if(n == 0){
			localBuilder.setMessage(getResources().getString(R.string.cmdtofc6));
		}else if(n == 3){
			localBuilder.setMessage(getResources().getString(R.string.cmdtofc5));
		}else{
			localBuilder.setMessage(getResources().getString(R.string.cmdtofc7));
		}
		
		
		
		localBuilder.show();
	}

	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent1 = new Intent(CmdDetailActivity.this, CmdViewActivity.class);
			intent1.putExtra("user", compte);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent1);

			CmdDetailActivity.this.finish();
		}
		return false;
	}
	
	
	class ConnexionTask extends AsyncTask<Void, Void, String> {


		private String res;
		@Override
		protected String doInBackground(Void... params) {

			res = manager.CmdToFacture(v, compte);
			
			switch (res) {
			case "0":
				res = getResources().getString(R.string.cmdtofc1);
				break;

			case "1":
				res = getResources().getString(R.string.cmdtofc2);
				break;
			case "2":
				res = getResources().getString(R.string.cmdtofc3);
				break;
			case "3":
				res = getResources().getString(R.string.cmdtofc4);
				break;
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

					new AlertDialog.Builder(CmdDetailActivity.this)
					.setTitle(getResources().getString(R.string.cmdtofc10))
					.setMessage(res)
					.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface d, int arg1) {
							//VendeurActivity.super.onBackPressed();
							d.dismiss();
							Intent intent1 = new Intent(CmdDetailActivity.this, CmdViewActivity.class); //CatalogeActivity.class  //VendeurActivity
							intent1.putExtra("user", compte);
							intent1.putExtra("cmd", "0");
							startActivity(intent1);
							CmdDetailActivity.this.finish();
						}

					})
					.setCancelable(false)
					.create().show();

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