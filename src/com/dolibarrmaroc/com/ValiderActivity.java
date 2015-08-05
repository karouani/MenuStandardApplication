package com.dolibarrmaroc.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.FileData;
import com.dolibarrmaroc.com.models.MyTicketBluetooth;
import com.dolibarrmaroc.com.models.MyTicketPayement;
import com.dolibarrmaroc.com.models.MyTicketWitouhtProduct;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.PromoTicket;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.models.TotauxTicket;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.MyTicket;
import com.dolibarrmaroc.com.utils.ProduitTicket;
import com.dolibarrmaroc.com.utils.TinyDB;

public class ValiderActivity extends Activity {

	private TinyDB db;
	private Map<String, Remises> allremises = new HashMap<>();
	private List<PromoTicket> remises = new ArrayList<>();
	
	
	private ioffline myoffline;
	private Myinvoice me = new Myinvoice();
	
	
	/** Called when the activity is first created. */
	public static BluetoothAdapter myBluetoothAdapter;
	public String SelectedBDAddress;
	private double ttc_remise = 0;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

	private static final String tab00c0 = "AAAAAAACEEEEIIII" +
			"DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
			"aaaaaaaceeeeiiii" +
			"\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
			"AaAaAaCcCcCcCcDd" +
			"DdEeEeEeEeEeGgGg" +
			"GgGgHhHhIiIiIiIi" +
			"IiJjJjKkkLlLlLlL" +
			"lLlNnNnNnnNnOoOo" +
			"OoOoRrRrRrSsSsSs" +
			"SsTtTtTtUuUuUuUu" +
			"UuUuWwYyYZzZzZzF";

	/************************************
	 * SERIALZABLE DATA
	 */
	private HashMap<Integer, ArrayList<Produit>> produits;
	private Compte compte;
	private FileData objet;
	private MyTicket ticket;
	private Button autre,quitter;
	private Map<String,TotauxTicket> map_totaux = new HashMap<>();
	private int okey = 0;
	
	
	/** reglement data ****/
	private MyTicketWitouhtProduct offlineticket;
	private Client clt;
	private Payement mypay;
	private Reglement myreg;
	private List<Reglement> lsreg;
	private ArrayList<HashMap<String, String>> dico;
	private int msrg =0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_valider);
		
		try {
			Bundle objetbunble  = this.getIntent().getExtras();
			db = new TinyDB(this);
			

			ticket = new MyTicket();
			myoffline = new Offlineimpl(getApplicationContext());
			if (objetbunble != null) {
				
				
				okey = (Integer)getIntent().getSerializableExtra("typeaction");
				
				Log.e("okey >>> ",okey+"");
				if(okey == 1){
					
					allremises = db.loadMapRemises("promotion");
					map_totaux = db.loadMapTotaux("totaux");
					
					compte =  (Compte) getIntent().getSerializableExtra("compte");
					produits = (HashMap<Integer, ArrayList<Produit>>) getIntent().getSerializableExtra("prds");
					objet = (FileData) getIntent().getSerializableExtra("objet");
					me = (Myinvoice)getIntent().getSerializableExtra("invo");
					
					/******************* REMPLIR TICKET invoice ************************************/
					ticket.setAddresse(removeDiacritic(objet.getAddresse()));
					ticket.setClient(removeDiacritic(objet.getClient()).toUpperCase());
					ticket.setDejaRegler(objet.getDejaRegler());
					ticket.setDescription(removeDiacritic(objet.getDescription()));
					ticket.setFax(objet.getFax());
					ticket.setIF(objet.getIF());
					ticket.setMsg(getResources().getString(R.string.promotion));
					ticket.setNameSte(removeDiacritic(objet.getNameSte()).toUpperCase());
					ticket.setPatente(objet.getPatente());
					ticket.setSiteWeb(objet.getSiteWeb());
					ticket.setTel(objet.getTel());
					ticket.setNumFacture(objet.getNumFacture());

					List<ProduitTicket> prd = new ArrayList<ProduitTicket>();
					List<Produit> pr = produits.get(0);

					for (int i = 0; i < pr.size(); i++) {
						Produit p = pr.get(i);
						// (int qte, String ref, Double prix, int taxe)
						Double prix = Double.parseDouble(p.getPrixUnitaire());

						int tva = Integer.parseInt(p.getTva_tx());

						ProduitTicket prod = new ProduitTicket(p.getQtedemander(), removeDiacritic(p.getDesig()),prix , tva );
						prd.add(prod);

						Remises r = allremises.get(p.getRef());
						if(r.getType() >= 0 ){
							if (r.getType() == 0) {
								if(p.getQtedemander() >= r.getQte()){
									double t = p.getQtedemander() * p.getPrixttc();
									double h = (t * r.getRemise()) / (double) 100;
									h = t - h;
									ttc_remise = ttc_remise + h;

									PromoTicket promo = new PromoTicket(p.getId(), p.getDesig(), r.getRemise(), r.getType(), h+" DH");
									remises.add(promo);
								}
							}else{
								if(p.getQtedemander() >= r.getRemise()){
									int h = r.getQte();
									PromoTicket promo = new PromoTicket(p.getId(), p.getDesig(), r.getRemise(), r.getType(), h+"");
									remises.add(promo);
								}
							}
						}
					}

					ticket.setPrds(prd);

					Log.e("meinvoice ",me+"");
					if(me != null){
						long ix = myoffline.shynchronizeBluetooth(new MyTicketBluetooth("", me, ticket, remises));
						
						//if(ix > -1){
							Intent intent1 = new Intent(ValiderActivity.this, ConnexionActivity.class);
							intent1.putExtra("user", compte);
							startActivity(intent1);
							ValiderActivity.this.finish();
					//	}else{
					//		Log.e("error tikcet","ticket out");
					//	}
						//myoffline.updateProduits(me);
					}
				}else if(okey == 2){
						compte =  (Compte) getIntent().getSerializableExtra("user");
						offlineticket = (MyTicketWitouhtProduct) getIntent().getSerializableExtra("offlineticket");
						mypay = (Payement) getIntent().getSerializableExtra("mypay");
						myreg = (Reglement)getIntent().getSerializableExtra("myreg");
						clt = (Client)getIntent().getSerializableExtra("clt");
						dico = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("dico");
						lsreg = new ArrayList<>();
						
						
						
						
						msrg = (Integer)getIntent().getSerializableExtra("lsreg");
					
						if(msrg > 0){
							for (int i = 0; i < msrg; i++) {
								Reglement r = (Reglement)getIntent().getSerializableExtra("reg"+i);
								Log.e("reg",r.toString());
								lsreg.add(r);
							}
						}
					

					/******************* REMPLIR TICKET ************************************/
					ticket.setAddresse(removeDiacritic(offlineticket.getAddresse()));
					ticket.setClient(removeDiacritic(clt.getName()));
					ticket.setDejaRegler(myreg.getAmount());
					ticket.setDescription(removeDiacritic(offlineticket.getDescription()));
					ticket.setFax(offlineticket.getFax());
					ticket.setIF(offlineticket.getIF());
					ticket.setMsg(getResources().getString(R.string.promotion));
					ticket.setNameSte(removeDiacritic(offlineticket.getNameSte()).toUpperCase());
					ticket.setPatente(offlineticket.getPatente());
					ticket.setSiteWeb(offlineticket.getSiteWeb());
					ticket.setTel(offlineticket.getTel());
					ticket.setNumFacture(mypay.getNum());  //mypay.getNum()




					long ix = myoffline.shynchronizePayemntTicket(new MyTicketPayement(compte, ticket, offlineticket, clt, mypay, myreg, lsreg));
					Intent intent1 = new Intent(ValiderActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					startActivity(intent1);
					ValiderActivity.this.finish();
				}else{
					Intent intent1 = new Intent(ValiderActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					startActivity(intent1);
					ValiderActivity.this.finish();
				}
			}
			
			
			Intent intent1 = new Intent(ValiderActivity.this, ConnexionActivity.class);
			intent1.putExtra("user", compte);
			startActivity(intent1);
			ValiderActivity.this.finish();

			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("eroor ticket valide ",e.getMessage() +" << ");
		}
	}
	public static String removeDiacritic(String source) {
		char[] vysl = new char[source.length()];
		char one;
		for (int i = 0; i < source.length(); i++) {
			one = source.charAt(i);
			if (one >= '\u00c0' && one <= '\u017f') {
				one = tab00c0.charAt((int) one - '\u00c0');
			}
			vysl[i] = one;
		}
		return new String(vysl);
	}
	
}
