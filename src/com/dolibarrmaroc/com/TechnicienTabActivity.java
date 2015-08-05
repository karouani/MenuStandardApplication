package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;

public class TechnicienTabActivity extends TabActivity {

	private Compte compte;
	private PowerManager.WakeLock wl;
	//Recuperation Interface de l'utilisateur Par Service
	private String nmb;
	private String serviceName;
	private List<String> labels;
	
	private ProgressDialog dialogIN;
	//offline
		private ioffline myoffine;
		
	public TechnicienTabActivity() {
		compte = new Compte();
		labels = new ArrayList<>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				nmb = this.getIntent().getStringExtra("nmbService");
				serviceName = this.getIntent().getStringExtra("service");
				for (int i = 0; i < Integer.parseInt(nmb); i++) {
					labels.add(this.getIntent().getStringExtra("labels"+i));
				}
			}

			
			for(String l:labels){
				Log.e(">> ",l);
			}
			final TabHost tabHost = getTabHost();

			TabSpec boredereau= tabHost.newTabSpec(getResources().getString(R.string.tecboredereau));       
			boredereau.setIndicator(getResources().getString(R.string.tecboredereau) , getResources().getDrawable(R.drawable.sys_clipboard));
			Intent browserIntent = new Intent(this, TechnicienActivity.class);
			browserIntent.putExtra("user", compte);
			browserIntent.putExtra("service", serviceName);
			browserIntent.putExtra("nmbService", nmb);
			for (int i = 0; i < Integer.parseInt(nmb); i++) {
				browserIntent.putExtra("labels"+i, labels.get(i).toString());
			}
			boredereau.setContent(browserIntent);

			// Tab for  Maps
			TabSpec carte = tabHost.newTabSpec(getResources().getString(R.string.teccarte)).setIndicator(getResources().getString(R.string.teccarte),getResources().getDrawable(R.drawable.sys_local));
			// setting Title and Icon for the Tab
			//photospec.setIndicator("Photos", getResources().getDrawable(R.drawable.icon_photos_tab));
			Intent photosIntent = new Intent(this, MainActivity.class);
			photosIntent.putExtra("user", compte);
			carte.setContent(photosIntent);

			// Tab for Configuration GPS
			TabSpec conf = tabHost.newTabSpec("Configuration");       
			conf.setIndicator("Config", getResources().getDrawable(R.drawable.sys_settings));
			Intent songsIntent = new Intent(this, TrackingActivity.class);
			songsIntent.putExtra("user", compte);
			conf.setContent(songsIntent);



			//tabHost.addTab(conf); //COnfiguration
			tabHost.addTab(boredereau); // Boredereau
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				tabHost.addTab(carte); // Maps
			}
			/*
			tabHost.setOnTabChangedListener(new OnTabChangeListener() {
				
				@Override
				public void onTabChanged(String tabId) {
					// TODO Auto-generated method stub
					
					tabHost.getCurrentTabView().setOnTouchListener(
	                        new OnTouchListener()
	                        {
	                            @Override
	                            public boolean onTouch(View v, MotionEvent event)
	                            {
	                                if (event.getAction() == MotionEvent.ACTION_DOWN)
	                                {
	                                	
	                                    if (getTabHost().getCurrentTabTag().equals("tab0")
	                                    {
	                                        getTabHost().setCurrentTabByTag("tab1");
	                                        getTabHost().setCurrentTabByTag("tab0");
	                                    }
	                                    
	                                	
	                                	Log.e("tab >> ",getTabHost().getCurrentTabTag() +" >> ");
	                                    return false;
	                                }
	                                return false;
	                            }
	                        });
				}
			});
			*/
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	

	@Override
	protected void onPause() {
		super.onPause();
		wl.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		wl.acquire();
	}

	private void createGpsDisabledAlert() {


		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder
		.setMessage("Le GPS est inactif, voulez-vous l'activer ?")
		.setCancelable(false)
		.setPositiveButton("Activer GPS ",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				TechnicienTabActivity.this.showGpsOptions();
			}
		}
				);
		localBuilder.setNegativeButton("Ne pas l'activer ",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.cancel();
				TechnicienTabActivity.this.finish();
			}
		}
				);
		localBuilder.create().show();

	}

	private void showGpsOptions() {
		startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.technicien_tab, menu);
		
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/************************MENU Carte *****************************************/
		case R.id.cleanitervention:
			alertchachedel();
			break;

		case R.id.hisinterv:
			Intent intent = new Intent(this, InterventionhistoActivity.class);
			intent.putExtra("user", compte);
			startActivity(intent);
			break;
		}
		return true;

	}
	
	public void alertchachedel(){
		try {
			
			myoffine = new Offlineimpl(getApplicationContext());
			
			Builder dialog = new AlertDialog.Builder(TechnicienTabActivity.this);
			
			String vl = getResources().getString(R.string.tecv49)+"["+myoffine.LoadInterventions("").size()+"] "+getResources().getString(R.string.tecv50)+" \n";
				dialog.setMessage(vl+getResources().getString(R.string.tecv35));

				dialog.setTitle(getResources().getString(R.string.tecv36));
			
			LayoutInflater inflater = this.getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.inputsetting, null);
	         
	         dialog.setView(dialogView);
	         dialog.setPositiveButton(getResources().getString(R.string.tecv37), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText txt = (EditText)dialogView.findViewById(R.id.inputpwd);
					if(txt != null){
						if(!txt.equals("")){
							if(compte.getPassword().equals(txt.getText().toString())){
								dialogIN = ProgressDialog.show(TechnicienTabActivity.this, getResources().getString(R.string.tecv37),
										getResources().getString(R.string.msg_wait), true);
								
								myoffine.CleanIntervention();
								if (dialogIN.isShowing()){
									dialogIN.dismiss();
								}
								
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.tecv38), Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(getApplicationContext(),getResources().getString(R.string.tecv38), Toast.LENGTH_LONG).show();
						}
					}
				}
			});
	         dialog.setNegativeButton(getResources().getString(R.string.tecv16), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					dialog.cancel();
				}
			});
	         dialog.setCancelable(true);
	         dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("erreur",e.getMessage() +" << ");
		}
	}
}