package com.dolibarrmaroc.com;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.dao.CategorieDao;
import com.dolibarrmaroc.com.dao.CategorieDaoMysql;
import com.dolibarrmaroc.com.database.DataErreur.DatabaseHandler;
import com.dolibarrmaroc.com.database.DataErreur.StockVirtual;
import com.dolibarrmaroc.com.models.Commande;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.Mouvement;
import com.dolibarrmaroc.com.models.MouvementGrabage;
import com.dolibarrmaroc.com.models.MyProdRemise;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.CommandeManagerFactory;
import com.dolibarrmaroc.com.utils.MouvementManagerFactory;
import com.dolibarrmaroc.com.utils.TinyDB;

public class FactureActivity extends Activity implements OnItemClickListener,OnClickListener{

	private PowerManager.WakeLock wl;
	private TinyDB db;
	private Map<String, Remises> remise= new HashMap<>();
	private MouvementManager stockManager;
	
	private SimpleAdapter adapter;
	private List<HashMap<String, String>> fillMaps;
	private ListView lv;

	private CategorieDao categorie;
	private DatabaseHandler database;
	
	private ioffline myofline;
	//Spinner Remplissage
	private List<String> listprd;

	//Dialog composant
	private EditText qtep = null,pup = null,totalp =null;
	private Produit produit;
	private Dialog dialog;
	private AutoCompleteTextView spinnere;
	private Button facturePop,cancel,addprd;
	private RelativeLayout qntlayout;
	private TextView qntview;

	//Recuperation
	private List<Produit> produitsFacture,prdsvient;
	private int nmb,nmbproducts;
	private String totalttc,totalht;
	private GpsTracker gps;
	private String idclt;
	private Compte compte;

	
	private int cmdTrue=0;
	private int type_invoice;
	private CommandeManager cmdmanager;

	//Interface 
	private EditText commentaire;
	private Button facture;
	private Button retour;
	
	private TextView mtntotal;
	
	private ArrayList<HashMap<String, String>> dico;
	
	private HashMap<String, Integer> panier = new HashMap<>();
	private HashMap<String, Integer> initial = new HashMap<>();
	
	private StockVirtual sv;
	
	public FactureActivity() {
		produitsFacture = new ArrayList<Produit>();
		prdsvient = new ArrayList<Produit>();
		listprd = new ArrayList<String>();
		fillMaps = new ArrayList<HashMap<String, String>>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facture);
		db = new TinyDB(this);
		
		categorie = new CategorieDaoMysql(getApplicationContext());
		
		cmdmanager = CommandeManagerFactory.getManager();
		myofline = new Offlineimpl(getApplicationContext());
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
		
		try {
			listprd.add(getResources().getString(R.string.product_spinner));

			commentaire = (EditText) findViewById(R.id.commentaire);

			

			retour = (Button) findViewById(R.id.retour);
			retour.setOnClickListener(this);
			
			mtntotal = (TextView) findViewById(R.id.labelqntdispo44);

			Bundle objetbunble  = this.getIntent().getExtras();
			

			if (objetbunble != null) {

				nmb = Integer.parseInt(this.getIntent().getStringExtra("nmbproduct"));
				nmbproducts = Integer.parseInt(this.getIntent().getStringExtra("nmbproducts"));
				totalttc = this.getIntent().getStringExtra("total");
				totalht = this.getIntent().getStringExtra("totalht");
				idclt = this.getIntent().getStringExtra("idclt");
				compte =  (Compte) getIntent().getSerializableExtra("compte");
				gps =  (GpsTracker) getIntent().getSerializableExtra("gps");
				dico = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("dico");
				
				cmdTrue = Integer.parseInt(getIntent().getStringExtra("cmd"));
				type_invoice = Integer.parseInt(getIntent().getStringExtra("typeinvoice"));
				
				mtntotal.setText(totalttc+ "DH");
				Log.v("<<<<<<<< DICO Facture ", dico.toString());
				
				if(nmb > 0){
					for (int i = 0; i < nmb; i++) {
						Produit prd = (Produit) getIntent().getSerializableExtra("produit"+i);
						produitsFacture.add(prd);
						Log.d("Recuperation Depuis Next", produitsFacture.toString()+" Var "+i);
						Log.v("---------> ",prd.toString());
						int qtin = prd.getQtedemander() + prd.getQteDispo();
						initial.put(prd.getRef(), qtin);
					}
					for (int i = 0; i < nmbproducts; i++) {
						Produit pst = (Produit) getIntent().getSerializableExtra("products"+i);
						
						/*if(pst.getQteDispo() > 0){
							prdsvient.add(pst);
							listprd.add(pst.getDesig());
						}
						*/
						prdsvient.add(pst);
						listprd.add(pst.getDesig());
						
						Log.i("listprd ", listprd.toString()+" Var "+i);
						Log.i("prdsvient ", prdsvient.toString()+" Var "+i);
					}
				}


			}
			
			facture = (Button) findViewById(R.id.validerfacture);
			facture.setOnClickListener(this);
			if(cmdTrue == 0 && type_invoice == 3){
				facture.setText(getResources().getString(R.string.cmdtofc26));
			}else if(cmdTrue == 1 && type_invoice == -1){
				facture.setText(getResources().getString(R.string.btn_validete_order));
			}else if(cmdTrue == 0 && type_invoice == 2){
				facture.setText(getResources().getString(R.string.cmdtofc27));
			}else{
				facture.setText(getResources().getString(R.string.btn_validete_invoice));
			}
			
			
			Log.w("Produit initial ",initial.toString());
			
			Log.e("Produit initial ",produitsFacture.size()+"");
			
			if(produitsFacture.size() == 0){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", "");
				map.put("name", "");
				map.put("desig",getResources().getString(R.string.facture_vide));
				map.put("qte", "");
				map.put("pu", "");

				fillMaps.add(map);
				facture.setEnabled(false);
			}

			adapter = getSimple(produitsFacture.size());

			lv = (ListView)findViewById(R.id.listview);

			lv.setAdapter(adapter);	

			if(produitsFacture.size() >0){
				lv.setOnItemClickListener(this);
			}

			dialog = new Dialog(this);
			dialog.setContentView(R.layout.prodcatlayout);
			dialog.setTitle("Ajouter un autre Produit");

			facturePop = (Button) dialog.findViewById(R.id.itenermoifact);
			cancel = (Button) dialog.findViewById(R.id.annulershowme);
			addprd = (Button) dialog.findViewById(R.id.factshowme);
			
			
			
			if(cmdTrue == 1){
				facturePop.setText(getResources().getString(R.string.facture2));
			}
			
			//Toast.makeText(FactureActivity.this, type_invoice+"", Toast.LENGTH_LONG).show();
			
			cancel.setVisibility(View.GONE);
			
			spinnere =  (AutoCompleteTextView) dialog.findViewById(R.id.produitpointer);
			spinnere.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					
					String selected = (String) parent.getItemAtPosition(position);
					produit = new Produit();
					spinnere.showDropDown();
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromInputMethod(parent.getWindowToken(), 0);
					spinnere.setFilters(new InputFilter[] {new InputFilter.LengthFilter(selected.length())});

					Log.e("Selected Produit Spinner ",selected);

					for (int i = 0; i < prdsvient.size(); i++) {
						if (selected.equals(prdsvient.get(i).getDesig())) {
							produit = new Produit();
							produit = prdsvient.get(i);
							
							/*
							if(panier.size() > 0){
								if(panier.containsKey(products.get(i).getRef())){
									produit.setQteDispo(panier.get(products.get(i).getRef()));
								}
							}
							*/

							Log.e("Text selectionner ",produit.toString());
							pup.setText(produit.getPrixUnitaire());
							qtep.setEnabled(true);
							
							qntlayout.setVisibility(View.VISIBLE);
							
							int d = produit.getQteDispo() - qnt_disponible(produit.getId());
							
							qntview.setText(d+"");
							//selectionner.setEnabled(true);

							break;
						}
					}
				}
			});
			
			qntlayout = (RelativeLayout)dialog.findViewById(R.id.l1w);
			qntlayout.setVisibility(View.GONE);
			
			qntview = (TextView)dialog.findViewById(R.id.textView1ww);
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public SimpleAdapter getSimple(int nmb){
		// create the grid item mapping
		SimpleAdapter adap;
		String[] from = new String[] {"name", "desig","qte","pu"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item4 , R.id.item3};

		// prepare the list of all records
		List<HashMap<String, String>>  fillMaps2 = new ArrayList<HashMap<String, String>>();

		Log.e("nmb ",nmb +" >> ");
		
		if(nmb == 0){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "");
			map.put("name", "");
			map.put("desig",getResources().getString(R.string.facture_vide));
			map.put("qte", "");
			map.put("pu", "");

			fillMaps2.add(map);
			facture.setEnabled(false);
		}else{
			for (int j = 0; j < produitsFacture.size(); j++) {
				HashMap<String, String> map = new HashMap<String, String>();
				Produit p = produitsFacture.get(j);
				map.put("id", p.getId()+"");
				map.put("name", p.getRef());
				map.put("desig",p.getDesig());
				map.put("qte", p.getQtedemander()+"");
				map.put("pu", p.getPrixUnitaire());

				fillMaps2.add(map);
				facture.setEnabled(true);
			}
		}

		// fill in the grid_item layout
		adap = new SimpleAdapter(this, fillMaps2, R.layout.grid_item, from, to);
		return adap;
	}
	
	public void addElementToSpinner(){
		//Spinner spinnere = (Spinner) dialog.findViewById(R.id.produitspinnerdialog);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listprd);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnere.setAdapter(dataAdapter);

		//spinnere.setOnItemSelectedListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.retour){

			
			
			//Button facture = (Button) dialog.findViewById(R.id.facturedialog);
			facturePop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter = getSimple(produitsFacture.size());
					adapter.notifyDataSetChanged();
					lv.setAdapter(adapter);
					
					qntlayout.setVisibility(View.GONE);
					qtep.setEnabled(false);
					qtep.setText("");
					qtep.setHint(getResources().getString(R.string.field_qte));
					totalp.setText("0");
					spinnere.setText("");
					pup.setText("0");
					produit = new Produit();
					
					dialog.dismiss();
				}
			});
			//Button cancel = (Button) dialog.findViewById(R.id.annulerFact);
			cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter = getSimple(produitsFacture.size());
					adapter.notifyDataSetChanged();
					lv.setAdapter(adapter);
					
					
					qntlayout.setVisibility(View.GONE);
					qtep.setEnabled(false);
					qtep.setText("");
					qtep.setHint(getResources().getString(R.string.field_qte));
					totalp.setText("0");
					spinnere.setText("");
					pup.setText("0");
					produit = new Produit();
					
					dialog.dismiss();
				}
			});

			//Button addprd = (Button) dialog.findViewById(R.id.ajouterproduitdialog);

			addElementToSpinner();
			
			//////////////**********************************Traitement PoPup************************************************////////////////////
			totalp = (EditText) dialog
					.findViewById(R.id.totaltextdialog);
			totalp.setFocusable(false);
			totalp.setEnabled(false);

			qtep = (EditText) dialog
					.findViewById(R.id.qtedialog);
			qtep.setEnabled(false);
			pup = (EditText) dialog
					.findViewById(R.id.pudialog);

			pup.setFocusable(false);
			pup.setEnabled(false);

			qtep.setText("");

			qtep.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String qteString = getResources().getString(R.string.field_qte);
					if(!s.toString().equals("-") && !s.toString().equals(".")){
						if(!qteString.equals(qtep.getText().toString())){
							String prix = "";
							double pr = 0;
							if("".equals(qtep.getText().toString())){
								totalp.setText("0");
							}
							else {
								int pri = 0;
								prix = qtep.getText().toString();
								if(!"".equals(prix) || prix != null){
									pri = Integer.parseInt(prix);
								}
								
								pr = Double.parseDouble(produit.getPrixUnitaire())* pri;

								if (produit.getQteDispo() >= Integer.parseInt(prix)) {
									totalp.setText(pr+"");
								}else{
									if(produit.getId() != 0){
										//Toast.makeText(FactureActivity.this, "La quantit� demand� est supperieur � notre stock "+produit.getQteDispo(), Toast.LENGTH_SHORT).show();
										alert(addprd,produit);
										totalp.setText(pr+"");
									}
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
					String qteString = getResources().getString(R.string.field_qte);
					
					if(!s.toString().equals("-") && !s.toString().equals(".")){
						if(!qteString.equals(qtep.getText().toString()) && !"".equals(qtep.getText().toString()) ){
							
							Promotion promos = new Promotion();
							promos = db.loadMapPromotion("allpromotion").get(produit.getRef());

							if(promos.getType() == 1){
								int p = promos.getPromos();
								int q = promos.getQuantite();
								int n = 0;
								int qd = 0;
								
								int d = Integer.parseInt(qtep.getText().toString());
								Log.e("Panier",produitsFacture.toString());
								int tr = 0;
								for (int i = 0; i < produitsFacture.size(); i++) {
									if(produitsFacture.get(i).getDesig().equals(produit.getDesig())){
										//qd = produitsFacture.get(i).getQtedemander();
										n = produitsFacture.get(i).getQtedemander();
										//n = n + Integer.parseInt(qte.getText().toString());
										qd = produit.getQteDispo() + n;
										
										tr = 1;
										break;
									}else{
										tr = 0;
									}
								}
								if (tr == 0) {
									qd = produit.getQteDispo();
								}

								Log.e("Affichage N",n+ ">> ");
								Log.e("Affichage QD",">> "+qd);
								
								if(!checkPromo(qd,n,d,q,p)){
									alertPromos();
									qtep.setText("0");
								}
							}
						}
					}
					
				}
			});

			addprd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String qteString = getResources().getString(R.string.field_qte);
					if(produit.getId() == 0){
						
					}else if(qteString.equals(qtep.getText().toString()) || "".equals(qtep.getText().toString())){
						Toast.makeText(FactureActivity.this,getResources().getString(R.string.qte_vide), Toast.LENGTH_SHORT).show();
					}else if(Integer.parseInt(qtep.getText().toString()) != 0  && produit.getId() != 0){
						int t = Integer.parseInt(qtep.getText().toString());
						Double toteaux = (double) 0;
						Double tote = (double) 0;

						int tr = 0;
						for (int i = 0; i < produitsFacture.size(); i++) {
							if(produitsFacture.get(i).getDesig().equals(produit.getDesig())){
								int k = produitsFacture.get(i).getQtedemander() + t;
								produitsFacture.get(i).setQtedemander(k);
								double prt = produit.getPrixttc()*t;

								toteaux += prt; 
								tote += Double.parseDouble(produit.getPrixUnitaire()) * t;

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

							toteaux += prt; 
							tote += Double.parseDouble(produit.getPrixUnitaire()) * t;
						}

						totalttc = toteaux + Double.parseDouble(totalttc)+"";
						mtntotal.setText(totalttc+ "DH");
						
						totalht = tote + Double.parseDouble(totalht)+"";

						Toast.makeText(FactureActivity.this,getResources().getString(R.string.msg_add_product), Toast.LENGTH_SHORT).show();
						Log.i("Produits List Popup", produitsFacture.toString());
						
						if(panier.size() == 0){
							int qt = produit.getQteDispo() - Integer.parseInt(qtep.getText().toString());
							panier.put(produit.getRef(), qt);
						}
						else{
							if(panier.containsKey(produit.getRef())){
								int qt = panier.get(produit.getRef()) - Integer.parseInt(qtep.getText().toString());
								panier.put(produit.getRef(), qt);
								
							}else{
								int qt = produit.getQteDispo() - Integer.parseInt(qtep.getText().toString());
								panier.put(produit.getRef(), qt);
							}

						}
						
						adapter = getSimple(produitsFacture.size());
						adapter.notifyDataSetChanged();
						lv.setAdapter(adapter);
						
						
						
						qntlayout.setVisibility(View.GONE);
						qtep.setEnabled(false);
						qtep.setText("");
						qtep.setHint(getResources().getString(R.string.field_qte));
						totalp.setText("0");
						spinnere.setText("");
						pup.setText("0");
						produit = new Produit();
						//facture.setEnabled(true);
					}else{
						AlertDialog.Builder alert = new AlertDialog.Builder(FactureActivity.this);
						alert.setTitle(getResources().getString(R.string.qte_zero));
						alert.setMessage(getResources().getString(R.string.qte_size));
						alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								//ajouterproduit.setEnabled(false);
								qtep.setText("");
								qtep.setHint(getResources().getString(R.string.field_qte));
								pup.setText("0");
								totalp.setText("0");
								return;
							}
						});
						alert.create().show();
					}
				}
			});

			
			qntlayout.setVisibility(View.GONE);
			qtep.setEnabled(false);
			qtep.setText("");
			qtep.setHint(getResources().getString(R.string.field_qte));
			totalp.setText("0");
			spinnere.setText("");
			pup.setText("0");
			produit = new Produit();
			
			dialog.show();
		}else if(v.getId() == R.id.validerfacture){
			
			if(cmdTrue == 1){
				
				
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.cmdtofc8))
				.setMessage(getResources().getString(R.string.cmdtofc13))
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
						 Log.e("goo","goo");
						 
						 dialog = ProgressDialog.show(FactureActivity.this, getResources().getString(R.string.map_data),
									getResources().getString(R.string.msg_wait), true);
							
							for (int i = 0; i < produitsFacture.size(); i++) {
								Produit pm = produitsFacture.get(i);
								Remises remi = setRemise(pm);
								remise.put(pm.getRef(), remi);
							}
							
							
							if(CheckOutNet.isNetworkConnected(getApplicationContext())){
								if(myofline.checkClient_is_Prospect(Integer.parseInt(idclt)) == -1){
									new ValidationTask().execute();
									
								}else{
									new ValidationOfflineTask().execute();
								}
							}else{
								new ValidationOfflineTask().execute();
							}
							
							

					}

				}).create().show();
				
				
			}else if(type_invoice == 0 || type_invoice == 2){
				
				int nan_number =0;
				
				for (int i = 0; i < produitsFacture.size(); i++) {
					if(produitsFacture.get(i).getQtedemander() == 0){
						nan_number++;
					}
				}
				
				if(nan_number != 0){
					makeMsgNanZero();
				}else{
					
					Intent intent = new Intent(FactureActivity.this, NextEtapeActivity.class);
					//intent.putExtra"products", produitsFacture);
					
					for (int i = 0; i < produitsFacture.size(); i++) {
						Produit pm = produitsFacture.get(i);
						intent.putExtra("produit"+i, pm);
						
						Remises remi = setRemise(pm);
						remise.put(pm.getRef(), remi);
						
						if(produitsFacture.get(i).getQtedemander() == 0){
							nan_number++;
						}
					}
					
					intent.putExtra("nmbproduct", produitsFacture.size()+"");
					intent.putExtra("total", totalttc);
					intent.putExtra("idclt", idclt);
					intent.putExtra("gps", gps);
					intent.putExtra("compte", compte);
					intent.putExtra("totalht", totalht);
					intent.putExtra("commentaire", commentaire.getText().toString());
					intent.putExtra("dico", dico);
					intent.putExtra("typeinvoice", type_invoice+"");
					
					db.saveMapRemises("promotion", remise);
					
					startActivity(intent);
					this.finish();
				}
				
				
			}else{
				//transfert de stock
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.cmdtofc8))
				.setMessage(getResources().getString(R.string.cmdtofc28))
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
							
							for (int i = 0; i < produitsFacture.size(); i++) {
								Produit pm = produitsFacture.get(i);
								Remises remi = setRemise(pm);
								remise.put(pm.getRef(), remi);
							}
							
							
							if(CheckOutNet.isNetworkConnected(getApplicationContext())){
								if(myofline.checkClient_is_Prospect(Integer.parseInt(idclt)) == -1){
									
									 dialog = ProgressDialog.show(FactureActivity.this, getResources().getString(R.string.map_data),
												getResources().getString(R.string.msg_wait), true);
									new EchangeTask().execute();
									
								}else{
									alertCnx_out(1);
								}
							}else{
								//alertCnx_out(0);
								dialog = ProgressDialog.show(FactureActivity.this, getResources().getString(R.string.map_data),
										getResources().getString(R.string.msg_wait), true);
							new EchangeOfflineTask().execute();
								 
							}
							
							

					}

				}).create().show();
			}
			
		}
	}
	
	private void makeMsgNanZero(){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(FactureActivity.this);
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		alert.setMessage(getResources().getString(R.string.cmdtofc14));
		alert.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface d, int arg1) {
				//VendeurActivity.super.onBackPressed();
				d.dismiss();

			}

		});
		alert.create();
		alert.show();
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, ConnexionActivity.class);
		startActivity(intent);
		FactureActivity.this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(position);
		AlertDialog.Builder adb = new AlertDialog.Builder(FactureActivity.this);

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
			adb.setMessage(getResources().getString(R.string.facture_choice)+map.get("name"));


			//on indique que l'on veut le bouton ok à notre boite de dialogue

			adb.setPositiveButton(getResources().getString(R.string.btn_delete), new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) { 
					Log.i(" >> List avant supprission", produitsFacture.toString());

					Double prttc = (double) 0;
					Double prht = (double) 0;

					List<Produit> toRemove = new ArrayList<>();
					for(Produit a: produitsFacture){
						if(a.getRef().equals(map.get("name"))){
							toRemove.add(a);
							//Double totalSup = a.getPrixttc() * a.getQtedemander();
							Log.i("a.getPrixttc()",a.getPrixttc()+"");
							Log.i("totalttc",totalttc);
							Log.i("a.getPrixttc()",a.getPrixUnitaire()+"");

							prttc += a.getPrixttc() * a.getQtedemander();
							prht  += Double.parseDouble(a.getPrixUnitaire()) * a.getQtedemander();
							
							
							
							
							for (int i = 0; i <prdsvient.size(); i++) {
								if(a.getRef().equals(prdsvient.get(i).getRef())){
									prdsvient.get(i).setQtedemander(0);
									//prdsvient.get(i).setQteDispo(a.getQtedemander());//initial.get(prdsvient.get(i).getRef())
									panier.remove(prdsvient.get(i).getRef());
								}
							}
							break;
						}
					}
					produitsFacture.removeAll(toRemove);

					adapter = getSimple(produitsFacture.size());
					adapter.notifyDataSetChanged();
					lv.setAdapter(adapter);
					
					
					totalttc = Double.parseDouble(totalttc)-prttc +"";
					totalht = Double.parseDouble(totalht)-prht + "";
					
					mtntotal.setText(totalttc+ "DH");

					Log.i(">>> List apres supprission", produitsFacture.toString());
					
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

	public void alert(final Button ajouterproduit,Produit prdouit){
		AlertDialog.Builder alert = new AlertDialog.Builder(FactureActivity.this);
		

		int d = produit.getQteDispo() - qnt_disponible(produit.getId());
		
		
		alert.setTitle(getResources().getString(R.string.stock_limit));
		alert.setMessage(
				String.format(
					    getResources().getString(R.string.stock_msg),
					    d));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//ajouterproduit.setEnabled(false);
				qtep.setText("0");
				pup.setText("0");
				totalp.setText("0");
				
				
				return;
			}
		});
		alert.create().show();
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
			Log.e("Stock Limite","Stock Limit� "+x);
			return false;
		}
	}

	public void alertPromos(){
		//List<Promotion> l = vendeurManager.getPromotions(client.getId(), produit.getId());
		//for (int i = 0; i < l.size(); i++) {
		Promotion p = new Promotion();
		p = db.loadMapPromotion("allpromotion").get(produit.getRef());
		
		AlertDialog.Builder alert = new AlertDialog.Builder(FactureActivity.this);
		alert.setTitle(getResources().getString(R.string.promo_alert));
		alert.setMessage(
				String.format(
						getResources().getString(R.string.promo_msg)+" car vous avez "+p.getQuantite()+" Produit Gratuit",
						panier.get(produit.getRef())));
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alert.create().show();
	}
	
	public Remises setRemise(Produit p){
		Remises r = new Remises();
		Promotion pro = new Promotion();
		pro = db.loadMapPromotion("allpromotion").get(p.getRef());
		
		if(pro.getType() == 0){
			r.setId(p.getId());
			r.setQte(pro.getQuantite());
			r.setRemise(pro.getPromos());
			r.setType(pro.getType());
		}else{
			int gratuite = (p.getQtedemander() / pro.getPromos()) * pro.getQuantite();
			r.setId(p.getId());
			r.setQte(gratuite);
			r.setRemise(pro.getPromos());
			r.setType(pro.getType());
		}
		return r;
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
	
	/******************************* synchronisation des commandes ****************************/
	
	class ValidationTask extends AsyncTask<Void, Void, String> {

		private String data="";
		@Override
		protected String doInBackground(Void... arg0) {
			myofline = new Offlineimpl(getApplicationContext());

			
			
			
			
			/*********************** offline ****************************************/
			if(CheckOutNet.isNetworkConnected(getApplicationContext())){
				if(myofline.checkAvailableofflinestorage() > 0){
					myofline.SendOutData(compte);	
				}
				
				data = cmdmanager.insertCommande(produitsFacture, idclt, compte, remise);
			}
			
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if(dialog.isShowing()){
					dialog.dismiss();
					String res ="";
					
					
					switch (data) {
					case "10":
						res = getResources().getString(R.string.cmdtofc18);
						break;
					case "-2":
						res = getResources().getString(R.string.cmdtofc17);
						break;
					case "-1":
					case "-4":
					case "-5":
						res = getResources().getString(R.string.cmdtofc15);
						break;
					
					default:
						break;
					}
					
					if(data.equals("10")){
						//res =getResources().getString(R.string.cmdtofc18);
						new AlertDialog.Builder(FactureActivity.this)
						.setTitle(getResources().getString(R.string.cmdtofc10))
						.setMessage(res)
						.setNegativeButton(getResources().getString(R.string.cmdtofc19), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface ds, int arg1) {
								//VendeurActivity.super.onBackPressed();
								ds.dismiss();
								Intent intent = new Intent(FactureActivity.this, CatalogeActivity.class);
								intent.putExtra("user", compte);
								intent.putExtra("cmd", "1");
								startActivity(intent);
								FactureActivity.this.finish();
							}

						})
						.setCancelable(false)
						.create().show();
					}else{
						//res = getResources().getString(R.string.cmdtofc18);
						new AlertDialog.Builder(FactureActivity.this)
						.setTitle(getResources().getString(R.string.cmdtofc10))
						.setMessage(res)
						.setNegativeButton(getResources().getString(R.string.cmdtofc20), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface ds, int arg1) {
								//VendeurActivity.super.onBackPressed();
								ds.dismiss();
							}

						})
						.setCancelable(false)
						.create().show();
					}
				
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}
	
	class ValidationOfflineTask extends AsyncTask<Void, Void, String> {

		private long data;

		@Override
		protected String doInBackground(Void... arg0) {
			myofline = new Offlineimpl(getApplicationContext());
			database = new DatabaseHandler(getApplicationContext());

			
			Calendar cl = Calendar.getInstance();
			cl.setTime(new Date());
			
			long s = database.addrow("cmd");
			Commande cmd = new Commande(s, s, s+"", cl.get(Calendar.YEAR)+"-"+cl.get(Calendar.MONTH)+"-"+cl.get(Calendar.DAY_OF_MONTH), idclt, produitsFacture, remise);
			cmd.setCompte(compte);
			
			data = myofline.shynchornizeCmd(cmd);
			
			/*
			HashMap<String, HashMap<Prospection, List<Commande>>> data_cmd = myofline.chargerCmd_prospect(compte);
			
			//Load commandes
			HashMap<Prospection, List<Commande>> cmd_ps = data_cmd.get("ps");
			
			HashMap<Commande,String> cmd_cl = myofline.chargerCmd_client(data_cmd, compte);
			
			Log.e("cmd_ps",cmd_ps.toString()+"");
			Log.e("cmd_cl",cmd_cl.toString()+"");
			*/
			
			if(data != -1){
				Myinvoice me = new Myinvoice(cmd.getIdandro()+"", produitsFacture, idclt, 0, "", compte, "", "", "", 0, prepaRemise(remise), null, "", "", "");
				//Log.e("invo ",me.toString());
				myofline.updateProduits(me);
			}


			return null;
		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					String res ="";
					if(data != -1){
						res =getResources().getString(R.string.cmdtofc18);
						new AlertDialog.Builder(FactureActivity.this)
						.setTitle(getResources().getString(R.string.cmdtofc10))
						.setMessage(res)
						.setNegativeButton(getResources().getString(R.string.cmdtofc19), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface ds, int arg1) {
								//VendeurActivity.super.onBackPressed();
								ds.dismiss();
								Intent intent = new Intent(FactureActivity.this, CatalogeActivity.class);
								intent.putExtra("user", compte);
								intent.putExtra("cmd", "1");
								startActivity(intent);
								FactureActivity.this.finish();
							}

						})
						.setCancelable(false)
						.create().show();
					}else{
						res = getResources().getString(R.string.cmdtofc17); //"Une erreur de communication avec le server distant. Veuillez r�essayer plus tard";
						new AlertDialog.Builder(FactureActivity.this)
						.setTitle(getResources().getString(R.string.cmdtofc10))
						.setMessage(res)
						.setNegativeButton(getResources().getString(R.string.cmdtofc20), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface ds, int arg1) {
								//VendeurActivity.super.onBackPressed();
								ds.dismiss();
								showmessageOffline();
							}

						})
						.setCancelable(false)
						.create().show();
					}
					
					
				}

			} catch (Exception e) {
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}


	}
	
	public void showmessageOffline(){
		try {
			 
	         LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.requeststorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(FactureActivity.this);
	         dialog.setView(dialogView);
 	 	     dialog.setTitle(R.string.caus9);
 	 	     dialog.setPositiveButton(R.string.caus8, new DialogInterface.OnClickListener() {
 	 	        public void onClick(DialogInterface dialog, int which) { 
 	 	        	Intent intent =new Intent(FactureActivity.this,ConnexionActivity.class);//EsignatureActivity
					intent.putExtra("user", compte);
					startActivity(intent);
 	 	        	 dialog.cancel();
 	 	        }
 	 	     });
 	 	     dialog.setCancelable(true);
 	 	     dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public List<MyProdRemise> prepaRemise(Map<String, Remises> allremises){
		List<MyProdRemise> me = new ArrayList<>();
		
		if(allremises != null){
			if(allremises.size() != 0){
				for(String ref:allremises.keySet()){
					Log.e("prod "+ref," >>> "+ allremises.get(ref));
					me.add(new MyProdRemise(ref, allremises.get(ref)));
				}
			}
		}
		
		
		return me;
		
	}
	
	public void alertCnx_out(int n){
		AlertDialog.Builder alert = new AlertDialog.Builder(FactureActivity.this);
		
		if(n == 0){
			alert.setMessage(getResources().getString(R.string.cmdtofc29));
		}else{
			alert.setMessage(getResources().getString(R.string.cmdtofc30));
		}
		alert.setTitle(getResources().getString(R.string.cmdtofc10));
		
		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				return;
			}
		});
		alert.setCancelable(true);
		alert.create().show();
	}
	
	private class EchangeTask extends AsyncTask<Void, Void, String> {

		private String res;
		
		@Override
		protected String doInBackground(Void... params) {

			stockManager = MouvementManagerFactory.getManager();
			
			List<Mouvement> lsmv = new ArrayList<>();
			for (int i = 0; i < produitsFacture.size(); i++) {
				lsmv.add(new Mouvement(produitsFacture.get(i).getId(), produitsFacture.get(i), ""+compte.getIduser(), ""+compte.getIduser(), (double)produitsFacture.get(i).getQtedemander()));
			}
			
			res = stockManager.makeechange(lsmv, compte, prepa_label(),idclt+"");  // stockManager.makemouvement(lsmv, compte, prepa_label());
			
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
						
						sv = new StockVirtual(FactureActivity.this);
						for (int i = 0; i < produitsFacture.size(); i++) {
							sv.addrow("", produitsFacture.get(i).getId(), produitsFacture.get(i).getQtedemander(), type_invoice+"");
						}
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
	
	
	private class EchangeOfflineTask extends AsyncTask<Void, Void, String> {

		private long res;
		private 	List<Mouvement> lsmv;
		@Override
		protected String doInBackground(Void... params) {

			lsmv = new ArrayList<>();
			for (int i = 0; i < produitsFacture.size(); i++) {
				lsmv.add(new Mouvement(produitsFacture.get(i).getId(), produitsFacture.get(i), ""+compte.getIduser(), ""+idclt, (double)produitsFacture.get(i).getQtedemander()));
			}
			
			
			myofline = new Offlineimpl(FactureActivity.this);
			
			res =0;
			
		
			res = myofline.shnchronizeMouvement(new MouvementGrabage(idclt, lsmv, compte), compte);
			
			
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
					
					if(res == 1){
						
						sv = new StockVirtual(FactureActivity.this);
						for (int i = 0; i < produitsFacture.size(); i++) {
							sv.addrow("", produitsFacture.get(i).getId(), produitsFacture.get(i).getQtedemander(), type_invoice+"");
						}
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
			
			st = ts.getTime()+"";
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return st;
	}
	
	public void alert_response(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(FactureActivity.this);
		alert.setTitle(getResources().getString(R.string.mvm6));
		alert.setMessage(msg);
		alert.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
				
				Intent intent = new Intent(FactureActivity.this, ConnexionActivity.class);
				intent.putExtra("user", compte);
				startActivity(intent);
				FactureActivity.this.finish();
				return;
			}
		});
		alert.setCancelable(false);
		alert.create().show();
	}
	
	
}

