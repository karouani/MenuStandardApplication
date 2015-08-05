package com.dolibarrmaroc.com.offline;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;

import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.FactureManager;
import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.TechnicienManager;
import com.dolibarrmaroc.com.database.DataErreur.DataErreur;
import com.dolibarrmaroc.com.models.BordreauIntervention;
import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Commande;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.FileData;
import com.dolibarrmaroc.com.models.GpsTracker;
import com.dolibarrmaroc.com.models.MouvementGrabage;
import com.dolibarrmaroc.com.models.MyClientPromo;
import com.dolibarrmaroc.com.models.MyDicto;
import com.dolibarrmaroc.com.models.MyGpsInvoice;
import com.dolibarrmaroc.com.models.MyProdRemise;
import com.dolibarrmaroc.com.models.MyProduitPromo;
import com.dolibarrmaroc.com.models.MyTicketBluetooth;
import com.dolibarrmaroc.com.models.MyTicketPayement;
import com.dolibarrmaroc.com.models.MyTicketWitouhtProduct;
import com.dolibarrmaroc.com.models.Myinvoice;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.models.Services;
import com.dolibarrmaroc.com.models.TotauxTicket;
import com.dolibarrmaroc.com.utils.CommandeManagerFactory;
import com.dolibarrmaroc.com.utils.CommercialManagerFactory;
import com.dolibarrmaroc.com.utils.FactureManagerFactory;
import com.dolibarrmaroc.com.utils.GpsTrackingServiceDao;
import com.dolibarrmaroc.com.utils.MouvementManagerFactory;
import com.dolibarrmaroc.com.utils.PayementManagerFactory;
import com.dolibarrmaroc.com.utils.ServiceDao;
import com.dolibarrmaroc.com.utils.TechnicienManagerFactory;
import com.google.gson.Gson;

@SuppressLint("NewApi") public class Offlineimpl implements ioffline {

	private String  filestock;
	private File file;
	private Gson gson;
	private Context context;
	private HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits;
	private HashMap<Integer, List<Integer>> listPromoByClient;
	private Dictionnaire dicot = new Dictionnaire();
	private ConfigGps gpsTracker;
	
	private CommercialManager managerclient;
	private FactureManager managerfacture;
	private PayementManager managerpayement;
	private CommandeManager cmdmanager;
	private MouvementManager stockManager;
	
	private StandardPBEStringEncryptor encryptor;
	
	private static String path = Environment.getExternalStorageDirectory()+"/.datadolicache";//path
	
	private HashMap<Myinvoice, String> myc = new HashMap<>();

	@Override
	public long shynchronizeClients(List<Client> clt) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanClients();
			file = new File(path, "/clientsdata.txt");
			Log.e("filesavz",file.getPath());
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(clt.size() > 0){
					//encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
					for (int i = 0; i < clt.size(); i++) {
						pout.println("["+gson.toJson(clt.get(i),Client.class)+"]");
						ix =1;
						//Log.e(">>> clt ",gson.toJson(clt.get(i),Client.class));
					}
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save clients",e.getMessage()  +" << ");
		}
		return ix;
	}

	public Offlineimpl(Context context) {
		super();
		this.context = context;
		gson = new Gson();
		listPromoByProduits = new HashMap<>();
		listPromoByClient = new HashMap<>();
		
		managerclient = CommercialManagerFactory.getCommercialManager();
		managerfacture = FactureManagerFactory.getFactureManager();
		managerpayement = PayementManagerFactory.getPayementFactory();
		cmdmanager = CommandeManagerFactory.getManager();
		stockManager = MouvementManagerFactory.getManager();
		
		
		file = new File(path+"/");
		
		file.setWritable(true);
		file.setReadable(true);
		if(!file.exists()){
			Log.e("save root","mkdirs "+file.mkdirs());
		}
	}

	public Offlineimpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public long shynchronizeProduits(List<Produit> clt) {
		// TODO Auto-generated method stub
		long ix =-1;
		try {
			CleanProduits();
			file = new File(path, "/produitsdata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}


			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				
				//encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				if(clt.size() > 0){
					for (int i = 0; i < clt.size(); i++) {
						pout.println("["+gson.toJson(clt.get(i),Produit.class)+"]");
						ix =1;
					}
				}
				
				pout.close();
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save produits",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<Client> LoadClients(String fl) {
		// TODO Auto-generated method stub
		List<Client> list = new ArrayList<Client>();

		try{
			int n;

			File file = new File(path, "/clientsdata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				//encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				while ((line = r.readLine()) != null) {
					JSONArray jArray = new JSONArray(line);

					
					for(int i=0;i<jArray.length();i++){
						JSONObject json = jArray.getJSONObject(i);
						Client clt = new Client();

						clt = gson.fromJson(json.toString(), Client.class);
						//Log.e(">>> clt ",clt.toString());
						if(fl.equals("")){
							list.add(clt);
						}else{
							n = clt.getId();
							Log.e("ref clt",fl + " >>> "+n);
							if(n == Integer.parseInt(fl)){
								list.add(clt);
							}
						}
						

					}
					/*
					List<Prospection> mps = this.LoadProspection("");
					if(mps.size() > 0){
						for(Prospection ps:mps){
							if(ps != null){
								Client cl = new Client();
								cl.setAddress(ps.getAddress());
								cl.setEmail(ps.getEmail());
								cl.setId(ps.getId());
								cl.setLatitude(ps.getLatitude());
								cl.setLongitude(ps.getLangitude());
								cl.setName(ps.getName());
								cl.setTel(ps.getPhone());
								cl.setTown(ps.getTown());
								cl.setZip(ps.getZip());
								
								Log.e("hopa >>",cl.toString());
								list.add(cl);
							}
						}
					}
					*/
					
				}
			}

			Log.e("hopa >>",list.size()+"");

		}catch(Exception e){
			Log.e("load clients",e.getMessage()  +" << ");
		}
		return list;
	}


	@Override
	public List<Produit> LoadProduits(String fl) {
		// TODO Auto-generated method stub
		List<Produit> list = new ArrayList<Produit>();
		try {

			String st ="";
			File file = new File(path, "/produitsdata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				//encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				while ((line = r.readLine()) != null) {

					//st += line;
					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						JSONObject json = jArray.getJSONObject(i);
						Produit produit = new Produit();

						produit = gson.fromJson(json.toString(), Produit.class);
						list.add(produit);
					}

				}
				r.close();
				secondInputStream.close();
				
				//Log.e(">>> Produit trouver Successful! ", st);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load produits et promotion",e.getMessage()  +" << ");
		}
		return list;
	}

	public Dictionnaire getDicot() {
		return dicot;
	}

	public void setDicot(Dictionnaire dicot) {
		this.dicot = dicot;
	}

	@Override
	public long  shynchronizeCompte(Compte clt) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			
			CleanCompte();
			file = new File(path, "/comptedata.txt");
			FileOutputStream outputStream;

			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			Log.e("file not exist ",file.getAbsolutePath());
			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				Log.e("file not exist ","wloo wloo");
			}

			//Log.e("compte synchro ",""+gson.toJson(clt));
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(clt != null){
					pout.println(encryptor.encrypt("["+gson.toJson(clt,Compte.class)+"]"));
					ix =1;
				}
				pout.close();
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save compte",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public Compte LoadCompte(String log, String pwd) {
		// TODO Auto-generated method stub
		Compte compte = null;
		gpsTracker = new ConfigGps();
		try {

			File file = new File(path, "/comptedata.txt");
			Log.e("file path",file.getAbsolutePath());
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;

				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");

				while ((line = r.readLine()) != null) {
					//Log.e("load compt data ",line);
					
					JSONArray dico = new JSONArray(encryptor.decrypt(line));
					for (int j = 0; j < dico.length(); j++) {
						JSONObject json = dico.getJSONObject(j);
						Log.e("compte ",line);
						if(json.getString("login").equals(log) && json.getString("password").equals(pwd)){
							compte = new Compte();

							compte = gson.fromJson(json.toString(), Compte.class); 
						}

					}

				}
				r.close();
				secondInputStream.close();


			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load compte",e.getMessage()  +" << ");
		}
		return compte;
	}



	public ConfigGps getGpsTracker() {
		return gpsTracker;
	}

	public void setGpsTracker(ConfigGps gpsTracker) {
		this.gpsTracker = gpsTracker;
	}

	public HashMap<Integer, List<Integer>> getListPromoByClient() {
		return listPromoByClient;
	}

	public void setListPromoByClient(
			HashMap<Integer, List<Integer>> listPromoByClient) {
		this.listPromoByClient = listPromoByClient;
	}

	public HashMap<Integer, HashMap<Integer, Promotion>> getListPromoByProduits() {
		return listPromoByProduits;
	}

	public void setListPromoByProduits(
			HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits) {
		this.listPromoByProduits = listPromoByProduits;
	}

	@Override
	public long shynchronizeDico(Dictionnaire clt) {
		// TODO Auto-generated method stub
		long ix =-1;
		try {
			CleanDico();
			file = new File(path, "/dicodata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);

				if(clt != null)
				{
					int k = clt.getDico().size();
					for(HashMap<String, String> st:clt.getDico()){
						for(String s:st.keySet()){
							MyDicto dc = new MyDicto(s, st.get(s));
							pout.println("["+gson.toJson(dc,MyDicto.class)+"]");
							ix = 1;
						}
					}

					pout.close();
				}

			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save dico",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public Dictionnaire LoadDeco(String fl) {
		// TODO Auto-generated method stub
		List<Produit> list = new ArrayList<Produit>();
		try {

			File file = new File(path, "/dicodata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;

				ArrayList<HashMap<String, String>> mapsss = new ArrayList<>();

				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				while ((line = r.readLine()) != null) {
					JSONArray dico = new JSONArray(line);
					for (int j = 0; j < dico.length(); j++) {
						JSONObject jsone = dico.getJSONObject(j);
						MyDicto dc = new MyDicto();
						HashMap<String, String> dic = new HashMap<>();
						
						dc = gson.fromJson(jsone.toString(), MyDicto.class);
						dic.put(dc.getCode(), dc.getLibelle());
						mapsss.add(dic);
					}

				}
				dicot.setDico(mapsss);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load dictionnaires",e.getMessage()  +" << ");
		}
		return dicot;
	}

	@Override
	public void CleanClients() {
		// TODO Auto-generated method stub
		Log.e("clean *****************","<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		try {
			File file = new File(path, "/clientsdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void CleanProduits() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/produitsdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void CleanDico() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/dicodata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void CleanCompte() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/comptedata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("clean compte",e.getMessage()  +" << ");
		}
	}

	@Override
	public long shynchronizeService(List<Services> clt) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanService();
			file = new File(path, "/servicesdata.txt");
			Log.e("filesavz",file.getPath());
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(clt.size() > 0){
					//encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
					for (int i = 0; i < clt.size(); i++) {
						pout.println("["+gson.toJson(clt.get(i),Services.class)+"]");
						ix =1;
						//Log.e(">>> clt ",gson.toJson(clt.get(i),Client.class));
					}
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save services ",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public Services LoadServices(String fl) {
		// TODO Auto-generated method stub
		Services sr = new Services();
		try {

			File file = new File(path, "/servicesdata.txt");

			List<Services> ls = new ArrayList<>();
			
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						sr = gson.fromJson(json.toString(), Services.class);
						//Log.e("hello >>> ",pd.toString());
						
						ls.add(sr);
					}

				}
				
				//meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load services err ",e.getMessage()  +" << ");
		}
        
		return sr;
	}

	@Override
	public void CleanService() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/servicesdata.txt"); //servicedata
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long shynchronizePromotion(
			HashMap<Integer, HashMap<Integer, Promotion>> clt) {
		// TODO Auto-generated method stub
		long ix =-1;
		try {
			CleanPromotionProduit();
			file = new File(path, "/promodata.txt");
			FileOutputStream outputStream;
			List<Promotion> promo = new ArrayList<>();
			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}


			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);

				if(clt != null)
				{
					pout.print("[");

					int k = clt.size();
					int i=0;
					for(Integer cl:clt.keySet()){
						HashMap<Integer, Promotion> m = clt.get(cl);
						promo = new ArrayList<>();

						for(Integer c:m.keySet()){
							Promotion p = m.get(c);
							promo.add(p);
						}
						MyProduitPromo pp = new MyProduitPromo(cl, promo);

						if( i == (k-1)){
							pout.print(gson.toJson(pp)+"");
						}else{
							pout.print(gson.toJson(pp)+",");
						}
						i++;


					}

					pout.print("]");
					pout.close();
					ix =1;
				}


			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save promotion",e.getMessage()  +" << ");
		}
		
		return ix;
	}

	@Override
	public HashMap<Integer, HashMap<Integer, Promotion>> LoadPromotion(String fl) {
		// TODO Auto-generated method stub
		try {

			File file = new File(path, "/promodata.txt");
			listPromoByProduits = new HashMap<>();

			
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				//String st="";
				//Log.e("file in ",""+file.exists());
				while ((line = r.readLine()) != null) {
					Log.e("file in2 ",""+file.exists());
					//st += line;
					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						JSONObject json = jArray.getJSONObject(i);

						HashMap<Integer, Promotion> map = new HashMap<>();


						JSONArray jpro = new JSONArray(json.getString("pro"));

						//Log.e("ref "+json.getInt("id")," >>> "+json.getString("pro"));
						for (int j = 0; j < jpro.length(); j++) {
							JSONObject jpp = jpro.getJSONObject(j);

							Promotion p = new Promotion(jpp.getInt("id"), 
									Integer.parseInt(jpp.getString("type")), 
									Integer.parseInt(jpp.getString("promos")), 
									Integer.parseInt(jpp.getString("quantite")));
							map.put(p.getId(), p);
						}


						listPromoByProduits.put(json.getInt("id"), map);


					}

				}
				//Log.e("data ",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load promotion",e.getMessage()  +" << ");
		}
		return listPromoByProduits;
	}

	@Override
	public long shynchronizePromotionClient(HashMap<Integer, List<Integer>> clt) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanPromotionClient();
			file = new File(path, "/promoclidata.txt");
			FileOutputStream outputStream;
			List<Promotion> promo = new ArrayList<>();
			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}


			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);

				if(clt != null)
				{
					pout.print("[");

					int k = clt.size();
					int i=0;
					for(Integer cl:clt.keySet()){
						List<Integer> m = clt.get(cl);
						promo = new ArrayList<>();
						for(Integer n:m){
							promo.add(new Promotion(n, -1, -1, -1));
						}

						MyClientPromo me = new MyClientPromo(cl, promo);

						if( i == (k-1)){
							pout.print(gson.toJson(me)+"");
						}else{
							pout.print(gson.toJson(me)+",");
						}
						i++;
					}

					pout.print("]");
					pout.close();
					ix = 1;
				}

			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save promotion client",e.getMessage()  +" << ");
		}
		
		return ix;
	}

	@Override
	public HashMap<Integer, List<Integer>> LoadPromoClient(String ref) {
		// TODO Auto-generated method stub
		try {
			
			List<Client> cl = this.prepaOfflineClient(this.LoadProspection(""));

			File file = new File(path, "/promoclidata.txt");
			listPromoByClient = new HashMap<>();

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				while ((line = r.readLine()) != null) {

					st += line;
					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						JSONObject json = jArray.getJSONObject(i);

						List<Integer> map = new ArrayList<>();


						JSONArray jpro = new JSONArray(json.getString("promo"));

						//Log.e("ref ",json.toString());
						for (int j = 0; j < jpro.length(); j++) {
							JSONObject jpp = jpro.getJSONObject(j);

							map.add(jpp.getInt("id"));
						}

						
						listPromoByClient.put(json.getInt("codecli"), map);


					}

				}
				
				if(cl.size() > 0){
					for (int i = 0; i < cl.size(); i++) {
						List<Integer> map = new ArrayList<>();
						listPromoByClient.put(cl.get(i).getId(), map);
					}
				}
			//	meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load promotion clients ",e.getMessage()  +" << ");
		}
		return listPromoByClient;
	}
	
	public void meme(String st){
		try {
			File file2 = new File(Environment.getExternalStorageDirectory().toString()+"/cicin/promo.txt");
	        if(!file2.exists()){
	    		file2.createNewFile();
	    		file2.mkdir();
	    		//Log.e("file not exist ","wloo wloo");
	    	}
	        file2.setWritable(true, true);
	    	file2.setReadable(true, true);
	        FileWriter fw2 = new FileWriter(file2, false);
	    	PrintWriter pout2 = new PrintWriter(fw2);
	    		pout2.print(st);
	        pout2.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void CleanPromotionProduit() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/promodata.txt");
			FileWriter fw = new FileWriter(file,false);
			PrintWriter pout = new PrintWriter(fw);
			pout.print("");
			pout.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void CleanPromotionClient() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/promoclidata.txt");
			FileWriter fw = new FileWriter(file,false);
			PrintWriter pout = new PrintWriter(fw);
			pout.print("");
			pout.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public List<Promotion> getPromotions(int idclt, int idprd) {
		// TODO Auto-generated method stub
		
		
		List<Integer> list = this.LoadPromoClient("").get(idclt);
		
		
		List<Promotion> lista = new ArrayList<>();
		HashMap<Integer, Promotion> map = this.LoadPromotion("").get(idprd);
		
		
		Log.e("promotion >> ",map.toString()+"client "+list);
		for (int i = 0; i < list.size(); i++) {
			if(map.containsKey(list.get(i))){
				lista.add(map.get(list.get(i)));
			}
		}
		if(lista.size() == 0) lista.add(new Promotion(0, -1, 1, 0));
		
		return lista;
	}

	@Override
	public Myinvoice shynchronizeInvoice(String invoice, List<Produit> prd, String idclt, int nmb,
			String commentaire, Compte compte, String reglement, String amount,
			String numChek, int typeImpriment, List<MyProdRemise> remises,
			GpsTracker gps, String imei, String num, String battery,TotauxTicket tt,int type_invoice) {
		// TODO Auto-generated method stub
		//CleanInvoice();
		FileData mx = null;
		Myinvoice me = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); 
		
		String ins = this.calculIdInvoice_new();
		//String st ="";
		try {
			
			file = new File(path, "/invoicedata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			
			MyTicketWitouhtProduct soc = this.LoadSociete("");
			Client cl = this.seeClient(this.LoadClients(""), idclt);
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(soc != null && cl != null){
					if(this.check_insert_invoice(ins) == -1){
						me = new Myinvoice();
						
						if(invoice != null){
							if(invoice.equals("-1"))invoice = calculIdInvoice();
						}else{
							invoice = calculIdInvoice();
						}
						
						me.setType_invoice(type_invoice);
						me.setIdnew(ins);
//						me.setInvoice("PROV-"+compte.getLogin()+"-"+invoice);
						me.setInvoice("PROV-"+calculIdInvoice());
						me.setInvoid(Integer.parseInt(calculIdInvoice()));
						me.setPrd(prd);
						me.setIdclt(idclt);
						me.setNmb(nmb);
						me.setCommentaire(commentaire);
						me.setCompte(compte);
						me.setReglement(reglement);
						me.setAmount(amount);
						me.setNumChek(numChek);
						me.setTypeImpriment(typeImpriment);
						me.setRemises(remises);
						
						if(gps.getSatellite() == null || gps.getDateString() == null){
							gps.setSatellite("GPS");
							gps.setDateString("0000-00-00 00:00");
						}
						me.setGps(gps);
						me.setImei(imei);
						if(num == null){
							num= "+000000000";
						}
						me.setNum(num);
						me.setBattery(battery);
						mx = new FileData();
						
						mx.setErreur(me.getInvoice());
						
						mx.setAddresse(soc.getAddresse());
						mx.setClient(cl.getName());
						mx.setDejaRegler(Double.parseDouble(me.getAmount()));
						mx.setDescription(soc.getDescription());
						mx.setFax(soc.getFax());
						mx.setIF(soc.getIF());
						mx.setMsg("Vous pouvez consultez nos offres promotionnelles sur le site :");
						mx.setNameSte(soc.getNameSte());
						mx.setNumFacture(me.getInvoice());
						mx.setPatente(soc.getPatente());
						mx.setSiteWeb(soc.getSiteWeb());
						mx.setTel(soc.getTel());
						
						me.setData(mx);
						me.setTotal_ticket(tt);
						
						pout.println("["+gson.toJson(me,Myinvoice.class)+"]");
						
						//st+= gson.toJson(me,Myinvoice.class);
					}
					
				}
				Log.e("my invoice >> ","["+gson.toJson(me,Myinvoice.class)+"]");
				//Log.e("my invoice",me+"");
				pout.close();
			}
			
			/*
			File file2 = new File(Environment.getExternalStorageDirectory().toString()+"/cicin/invoice.txt");
	        if(!file2.exists()){
	    		file2.createNewFile();
	    		file2.mkdir();
	    		//Log.e("file not exist ","wloo wloo");
	    	}
	        if(file2.exists()){
	        	 file2.setWritable(true, true);
	 	    	file2.setReadable(true, true);
	 	        FileWriter fw2 = new FileWriter(file2, false);
	 	    	PrintWriter pout2 = new PrintWriter(fw2);
	 	    		pout2.print("["+gson.toJson(me,Myinvoice.class)+"]");
	 	        pout2.close();
	        }
	       */

	        
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save invoice",e.getMessage()  +" << ");
		}
		return me;
	}

	@Override
	public long shynchronizeSociete(MyTicketWitouhtProduct me) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			
			CleanSociete();
			file = new File(path, "/societedata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			//Log.e("compte synchro ",""+gson.toJson(clt));
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(me != null){
					pout.println("["+gson.toJson(me,MyTicketWitouhtProduct.class)+"]");
					ix = 1;
					//Log.e("societe",gson.toJson(me,MyTicketWitouhtProduct.class));
				}
				pout.close();
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save societe",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public MyTicketWitouhtProduct LoadSociete(String fl) {
		// TODO Auto-generated method stub
		MyTicketWitouhtProduct my = null;
		
		try {

			File file = new File(path, "/societedata.txt");
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json = jArray.getJSONObject(i);
						//Log.e("soc",line);
						my = new MyTicketWitouhtProduct();
						my = gson.fromJson(json.toString(), MyTicketWitouhtProduct.class);
					}
				}

				r.close();
				secondInputStream.close();
				}
				
			}catch(Exception e){
				Log.e("load soceiete ",e.getMessage()  +" << ");
			}
			
		return my;
	}

	@Override
	public Client seeClient(List<Client> ls,String id) {
		// TODO Auto-generated method stub
		List<Client> tmp = this.prepaOfflineClient(this.LoadProspection(""));
		
		if(tmp.size() > 0){
			for (int i = 0; i < tmp.size(); i++) {
				ls.add(tmp.get(i));
			}
		}
		for(Client c:ls){
			if(c.getId() == Integer.parseInt(id)){
				return c;
			}
		}
		return null;
	}

	@Override
	public List<Myinvoice> LoadInvoice(String fl) {
		// TODO Auto-generated method stub
		List<Myinvoice> res = new ArrayList<>();
		
		try {

			File file = new File(path, "/invoicedata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;

				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				res = new ArrayList<>();
				
				while ((line = r.readLine()) != null) {
					
					JSONArray dico = new JSONArray(line);
					
					for (int j = 0; j < dico.length(); j++) {
						JSONObject json = dico.getJSONObject(j);

						 Myinvoice me = new Myinvoice();
						 FileData mx = new FileData();
						
						
						//me.setInvoice(json.getString("invoice"));
						
						/* load produits */
						/*
						JSONArray prod = new JSONArray(json.getString("prd"));
						List<Produit> ps = new ArrayList<>();
						for (int i = 0; i < prod.length(); i++) {
							JSONObject obj = prod.getJSONObject(i);
							Produit p = new Produit(obj.getString("ref"), obj.getString("desig"), obj.getInt("qteDispo"), obj.getString("prixUnitaire"), obj.getInt("qtedemander"), obj.getDouble("prixttc"),obj.getString("tva_tx") ,obj.getString("fk_tva") );
							p.setId(obj.getInt("id"));
							ps.add(p);
						}
						
						me.setPrd(ps);
						
						me.setIdclt(json.getString("idclt"));
						me.setNmb(json.getInt("nmb"));
						me.setCommentaire(json.getString("commentaire"));
						*/
						
						/* load compte */
						/*
						JSONObject cpt = json.getJSONObject("compte");
						Compte compte = new Compte();
						compte.setId(cpt.getInt("id"));
						compte.setActiver(cpt.getInt("activer"));
						compte.setProfile(cpt.getString("profile"));
						compte.setLogin(cpt.getString("login"));
						compte.setPassword(cpt.getString("password"));
						compte.setMessage(cpt.getString("message"));
						compte.setLevel(cpt.getString("level"));
						compte.setEmei(cpt.getString("emei"));
						compte.setIduser(cpt.getString("id"));
						compte.setStep(cpt.getString("step"));
						compte.setStop(cpt.getString("stop"));
						compte.setPermission(cpt.getInt("permission"));
						compte.setPermissionPayement(cpt.getInt("permissionPayement"));
						me.setCompte(compte);
						
						me.setReglement(json.getString("reglement"));
						me.setAmount(json.getString("amount"));
						me.setNumChek(json.getString("numChek"));
						
						me.setTypeImpriment(json.getInt("typeImpriment"));
						*/
						
						/* load remise */
						/*
						JSONArray rm = new JSONArray(json.getString("remises"));
						List<MyProdRemise> mpr = new ArrayList<>();
						for (int i = 0; i < rm.length(); i++) {
							JSONObject obj = rm.getJSONObject(i);
							MyProdRemise m = new MyProdRemise(obj.getString("ref"), new Remises(obj.getJSONObject("remise").getInt("id"), obj.getJSONObject("remise").getInt("qte"), obj.getJSONObject("remise").getInt("type"), obj.getJSONObject("remise").getInt("remise")));
							mpr.add(m);
						}
						me.setRemises(mpr);
						*/
						
						/* load GPS config */
						/*
						JSONObject gps = json.getJSONObject("gps");
						GpsTracker gm = new GpsTracker(gps.getString("latitude"),gps.getString("langitude") ,gps.getString("dateString"),(float)gps.getDouble("speed") ,gps.getDouble("altitude"),(float)gps.getDouble("direction"),gps.getString("satellite"));
						me.setGps(gm);
						
						
						me.setImei(json.getString("imei"));
						me.setNum(json.getString("num"));
						me.setBattery(json.getString("battery"));
						*/
						
						/* load data file */
						/*
						JSONObject jsondata = json.getJSONObject("data");
						mx = new FileData();
						mx.setErreur(json.getString("invoice"));
						mx.setAddresse(jsondata.getString("addresse"));
						mx.setClient(jsondata.getString("client"));
						mx.setDejaRegler(jsondata.getDouble("dejaRegler"));
						mx.setDescription(jsondata.getString("Description"));
						mx.setFax(jsondata.getString("fax"));
						mx.setIF(jsondata.getString("IF"));
						mx.setMsg("Vous pouvez consultez nos offres promotionnelles sur le site :");
						mx.setNameSte(jsondata.getString("nameSte"));
						mx.setNumFacture(jsondata.getString("numFacture"));
						mx.setPatente(jsondata.getString("patente"));
						mx.setSiteWeb(jsondata.getString("siteWeb"));
						mx.setTel(jsondata.getString("tel"));
						
						me.setData(mx);

						*/
						 me = gson.fromJson(json.toString(), Myinvoice.class);
						 //Log.e("load compt data ",me.toString());
						res.add(me);
					}

				}
				r.close();
				secondInputStream.close();


			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load invoice ",e.getMessage()  +" << ");
		}
		
		
		
		return res;
	}

	@Override
	public void CleanInvoice() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/invoicedata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void updateProduits(Myinvoice me) {
		// TODO Auto-generated method stub
		List<Produit> prd ;
		List<MyProdRemise> mex;
		
		if(me != null){
			mex = me.getRemises();
			prd = me.getPrd();
			for (int i = 0; i < prd.size(); i++) {
				
				for (int j = 0; j < mex.size(); j++) {
					MyProdRemise rm = mex.get(j);
					
					if(rm.getRef().equals(prd.get(i).getRef()) && rm.getRemise().getType() == 1){
						Log.e("qnt "+rm.getRemise().getQte(),"fabor "+rm.getRemise().getRemise());
						//int x = 0;
					//	x = prd.get(i).getQtedemander()/rm.getRemise().getQte();
						prd.get(i).setQtedemander(prd.get(i).getQtedemander() + rm.getRemise().getQte());
						break;
					}
				}
			}
			List<Produit> pd = new ArrayList<>();
			
			pd = this.LoadProduits("");
			for (int i = 0; i < pd.size(); i++) {
				for (int j = 0; j < prd.size(); j++) {
					if(pd.get(i).getId() == prd.get(j).getId()){
						pd.get(i).setQteDispo(pd.get(i).getQteDispo() - prd.get(j).getQtedemander());
						Log.e("por "+pd.get(i).getRef(),"qnt "+pd.get(i).getQteDispo()+" >> "+prd.get(j).getQtedemander());
					}
				}
			}
			
			CleanProduits();
			shynchronizeProduits(pd);
		}
		
	}
	
	@Override
	public void updateProduitsInv(Myinvoice me) {
		// TODO Auto-generated method stub
		List<Produit> prd ;
		List<MyProdRemise> mex;
		
		if(me != null){
			mex = me.getRemises();
			prd = me.getPrd();
			for (int i = 0; i < prd.size(); i++) {
				
				for (int j = 0; j < mex.size(); j++) {
					MyProdRemise rm = mex.get(j);
					
					if(rm.getRef().equals(prd.get(i).getRef()) && rm.getRemise().getType() == 1){
						Log.e("qnt "+rm.getRemise().getQte(),"fabor "+rm.getRemise().getRemise());
						//int x = 0;
					//	x = prd.get(i).getQtedemander()/rm.getRemise().getQte();
						prd.get(i).setQtedemander(prd.get(i).getQtedemander() + rm.getRemise().getQte());
						break;
					}
				}
			}
			List<Produit> pd = new ArrayList<>();
			
			pd = this.LoadProduits("");
			for (int i = 0; i < pd.size(); i++) {
				for (int j = 0; j < prd.size(); j++) {
					if(pd.get(i).getId() == prd.get(j).getId()){
						Log.e("por "+pd.get(i).getRef(),"qnt "+pd.get(i).getQteDispo()+" >> "+prd.get(j).getQtedemander());
						pd.get(i).setQteDispo(pd.get(i).getQteDispo() + prd.get(j).getQtedemander());
					}
				}
			}
			
			CleanProduits();
			shynchronizeProduits(pd);
		}
		
	}

	@Override
	public Produit showProduct(List<Produit> pd,int id) {
		// TODO Auto-generated method stub
		for (int i = 0; i < pd.size(); i++) {
			if(pd.get(i).getId() == id){
				return pd.get(i);
			}
		}
		return null;
	}

	@Override
	public long shynchronizeProspect(ProspectData pd) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanProspectData();
			file = new File(path, "/prospectdata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			//CleanClients();
			if(file.exists()){
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(pd != null){
					pout.println("["+gson.toJson(pd, ProspectData.class)+"]");
					ix = 1;
				}
				pout.close();
			}
			
			/*
			File file2 = new File(Environment.getExternalStorageDirectory().toString()+"/cicin/prospectdata.txt");
	        if(!file2.exists()){
	    		file2.createNewFile();
	    		file2.mkdir();
	    		//Log.e("file not exist ","wloo wloo");
	    	}
	        file2.setWritable(true, true);
	    	file2.setReadable(true, true);
	        FileWriter fw2 = new FileWriter(file2, false);
	    	PrintWriter pout2 = new PrintWriter(fw2);
	    		pout2.print("["+gson.toJson(pd, ProspectData.class)+"]");
	        pout2.close();
	        
	        */
	       // this.LoadProspect("");

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save dolidataprospect ",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public ProspectData LoadProspect(String fl) {
		// TODO Auto-generated method stub
		ProspectData pd = new ProspectData();
		try {

			File file = new File(path, "/prospectdata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						JSONObject json = jArray.getJSONObject(i);

						/*
						HashMap<String, String> tc = new HashMap<>();
						tc = (HashMap<String, String> )gson.fromJson(json.getString("typent_code"), tc.getClass());
						
						HashMap<String, String> ti = new HashMap<>();
						ti = (HashMap<String, String> )gson.fromJson(json.getString("typent_id"), ti.getClass());

						HashMap<String, String> jc = new HashMap<>();
						jc = (HashMap<String, String> )gson.fromJson(json.getString("juridique_code"), ti.getClass());
						
						JSONArray jsonjur = json.getJSONArray("juridique");
						List<String> jur = new ArrayList<>();
						for (int j = 0; j < jsonjur.length(); j++) {
							jur.add(jsonjur.getString(j));
						}
						
						JSONArray jsonvl = json.getJSONArray("villes");
						List<String> vl = new ArrayList<>();
						for (int j = 0; j < jsonvl.length(); j++) {
							vl.add(jsonvl.getString(j));
						}
						
						JSONArray jsonent = json.getJSONArray("typent");
						List<String> tent = new ArrayList<>();
						for (int j = 0; j < jsonent.length(); j++) {
							tent.add(jsonent.getString(j));
						}
						
						pd.setJuridique(jur);
						pd.setJuridique_code(jc);
						pd.setTypent(tent);
						pd.setTypent_code(tc);
						pd.setTypent_id(ti);
						pd.setVilles(vl);
						
						*/
						pd = gson.fromJson(json.toString(), ProspectData.class);
					}

				}
				
			//	meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load prospect clients ",e.getMessage()  +" << ");
		}
        
		return pd;
	}

	@Override
	public void CleanProspectData() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/prospectdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void CleanSociete() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/societedata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public String shynchronizeProspection(Prospection ps,Compte cp) {
		// TODO Auto-generated method stub
		String me = "Le Clients n'est pas ajouter r�ssayer plus tard";
		//CleanProspection();
		try {
			file = new File(path, "/prospectiondata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			//CleanClients();
			if(file.exists()){
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(ps != null){
					if(ps.getId() == 0 || ps.getId() == -1){
						ps.setId(Integer.parseInt(this.calculIdInvoice()));
					}
					
					ps.setIdpros(Integer.parseInt(this.calculIdInvoice()));
					
					if(ps.getParticulier() == 1){
						String s = ps.getFirstname();
						ps.setFirstname(cp.getLogin()+"-"+s);
					}
					
					
					String s1 = ps.getName();
					ps.setName(cp.getLogin()+"-"+s1);
					
					Log.e("data "+ps.getName()," "+ps.getFirstname());
					
					pout.println("["+gson.toJson(ps,Prospection.class)+"]");
					if(ps.getClient() == 1){
						me = "Client ajouter avec success";
					}else{
						me = "Prospect ajout� avec success";
					}
					
				}
				pout.close();
			}
			
			
			

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save Prospection ",e.getMessage()  +" << ");
		}
		
		return me;
	}
	
	@Override
	public long shynchronizeProspection_out(Prospection ps,Compte cp) {
		// TODO Auto-generated method stub
		//CleanProspection();
		try {
			file = new File(path, "/prospectiondata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			//CleanClients();
			if(file.exists()){
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(ps != null){
					pout.println("["+gson.toJson(ps,Prospection.class)+"]");
				}
				pout.close();
			}
			
			
			

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save Prospection ",e.getMessage()  +" << ");
		}
		
		return 0;
	}

	@Override
	public List<Prospection> LoadProspection(String fl) {
		// TODO Auto-generated method stub
		List<Prospection> lpd = new ArrayList<>();
		try {

			File file = new File(path, "/prospectiondata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						Prospection pd = new Prospection();
						pd = gson.fromJson(json.toString(), Prospection.class);
						//Log.e("hello >>> ",pd.toString());
						
						lpd.add(pd);
					}

				}
				
				//meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load prospection err ",e.getMessage()  +" << ");
		}
        
		return lpd;
	}

	@Override
	public void CleanProspection() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/prospectiondata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public String calculIdInvoice(){
		String res="";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			String sdt1 = "";// calendar.get(Calendar.YEAR)+"";

			int m = calendar.get(Calendar.MONTH)+1;
			int d = calendar.get(Calendar.DAY_OF_MONTH);
			int hr = calendar.get(Calendar.HOUR_OF_DAY);
			int mnt = calendar.get(Calendar.MINUTE);
			int sec = calendar.get(Calendar.SECOND);
			//System.out.println("sdt 1 "+sdt1+" mon "+m+ " d "+d);

			if(m == 0)m=12;
			if(m < 10){
				sdt1+="0"+m+"";
			}else{
				sdt1+=m+"";
			}

			if(d < 10){
				sdt1+="0"+d;
			}else{
				sdt1+=d;
			}
			
			if(hr < 10){
				sdt1+="0"+hr;
			}else{
				sdt1+=""+hr;
			}
			
			if(mnt < 10){
				sdt1+="0"+mnt;
			}else{
				sdt1+=""+mnt;
			}
			
			if(sec < 10){
				sdt1+="0"+sec;
			}else{
				sdt1+=""+sec;
			}
			
			res = sdt1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return res;
	}

	public String calculIdInvoice_new(){
		String res="";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			String sdt1 = calendar.get(Calendar.YEAR)+"";

			int m = calendar.get(Calendar.MONTH)+1;
			int d = calendar.get(Calendar.DAY_OF_MONTH);
			int hr = calendar.get(Calendar.HOUR_OF_DAY);
			int mnt = calendar.get(Calendar.MINUTE);
			//System.out.println("sdt 1 "+sdt1+" mon "+m+ " d "+d);

			if(m == 0)m=12;
			if(m < 10){
				sdt1+="0"+m+"";
			}else{
				sdt1+=m+"";
			}

			if(d < 10){
				sdt1+="0"+d;
			}else{
				sdt1+=d;
			}
			
			if(hr < 10){
				sdt1+="0"+hr;
			}else{
				sdt1+=""+hr;
			}
			
			if(mnt < 10){
				sdt1+="0"+mnt;
			}else{
				sdt1+=""+mnt;
			}
			
			
			res = sdt1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return res;
	}
	@Override
	public long shynchronizePayement(List<Payement> ps) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			file = new File(path, "/payementdata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}
			
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(ps.size() > 0){
					for (int i = 0; i < ps.size(); i++) {
						pout.println("["+gson.toJson(ps.get(i),Payement.class)+"]");
						ix = 1;
						//Log.e(">>> clt ",gson.toJson(clt.get(i),Client.class));
					}
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save Payement",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<Payement> LoadPayements(String fl) {
		// TODO Auto-generated method stub
		List<Payement> lp = new ArrayList<>();
		try {

			File file = new File(path, "/payementdata.txt");
			int k=0;
			if(file.exists()){
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				while ((line = r.readLine()) != null) {

					
					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						st += line;
						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						Payement pd = new Payement();
						pd = gson.fromJson(json.toString(), Payement.class);
						
						lp.add(pd);
					}

				}
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load payement err ",e.getMessage()  +" << ");
		}
		
		
		
		return lp;
	}

	@Override
	public void CleanPayement() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/payementdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	@Override
	public long checkRefClient(String ref,String eml) {
		// TODO Auto-generated method stub
		long is =-1;
		List<Client> cl = this.LoadClients("");
		List<Client> tmp = this.prepaOfflineClient(this.LoadProspection(""));
		
		if(tmp.size() > 0){
			for (int i = 0; i < tmp.size(); i++) {
				cl.add(tmp.get(i));
				Log.e("clt >>> ",cl.toString());
			}
		}
		
		
		for (int i = 0; i < cl.size(); i++) {
			if(((Client)cl.get(i)).getName() != null){
				
				if(cl.get(i).getEmail() != null){
					if(((Client)cl.get(i)).getName().toLowerCase().equals(ref.toLowerCase()) || ((Client)cl.get(i)).getEmail().toLowerCase().equals(eml.toLowerCase())){
						is = 1;
					}
				}
				
			}
			
		}
		
		//Log.e("client ref "+ref,"val >> "+is);
				
		return is;
	}

	@Override
	public List<Client> prepaOfflineClient(List<Prospection> ps) {
		// TODO Auto-generated method stub
		List<Client> cll = new ArrayList<>();
		if(ps.size() > 0){
				for(Prospection pss:ps){
					if(ps != null){
						Client cl = new Client();
						cl.setAddress(pss.getAddress());
						cl.setEmail(pss.getEmail());
						cl.setId(pss.getIdpros());
						cl.setLatitude(pss.getLatitude());
						cl.setLongitude(pss.getLangitude());
						cl.setName(pss.getName());
						cl.setTel(pss.getPhone());
						cl.setTown(pss.getTown());
						cl.setZip(pss.getZip());
						
						cll.add(cl);
					}
				}
		}
		return cll;
	}

	@Override
	public long shynchronizeReglement(Reglement reg) {
		// TODO Auto-generated method stub
		long ix =-1;
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			file = new File(path, "/reglementdata.txt");
			FileOutputStream outputStream;

			String ins = this.calculIdInvoice_new();
			
			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(reg != null){
					
					if(this.check_insert_reglement(ins) == -1){
						if(reg.getIdreg() == -1){
							reg.setIdreg(Integer.parseInt(calculIdInvoice()));
						}
						reg.setIdnew(ins);
						reg.setIdreg(Integer.parseInt(calculIdInvoice()));
						reg.setDatereg(sdf.format(new Date()));
							pout.println("["+gson.toJson(reg,Reglement.class)+"]");
							//Log.e(">>> reg ","["+gson.toJson(reg,Reglement.class)+"]");
							ix = 1; 
						
					}
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save Reglement",e.getMessage()  +" << ");
		}
		
		return ix;
	}

	@Override
	public List<Reglement> LoadReglement(String fl) {
		// TODO Auto-generated method stub
		List<Reglement> lp = new ArrayList<>();
		try {

			File file = new File(path, "/reglementdata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						Reglement pd = new Reglement();
						pd = gson.fromJson(json.toString(), Reglement.class);
						//Log.e("hello >>> ",pd.toString());
						
						if(fl.equals("")){
							lp.add(pd);
						}else{
							if(pd.getId() == Integer.parseInt(fl)){
								lp.add(pd);
							}
						}
						
					}

				}
				
			//	meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load Reglement err ",e.getMessage()  +" << ");
		}
		
		return lp;
	}

	@Override
	public List<Payement> prepaOfflinePayement(List<Myinvoice> ms) {
		// TODO Auto-generated method stub
		List<Myinvoice> me = this.LoadInvoice("");
		List<Payement> pss = this.LoadPayements("");
		List<Payement> ps = new ArrayList<>();
		List<Reglement> rs ;
		double res =0;
		
		
		if(me.size() > 0){
			for (int i = 0; i < me.size(); i++) {
				rs = new ArrayList<>();
				rs = this.LoadReglement(me.get(i).getInvoid()+"");
			
				Payement p = null;
				if(rs.size() > 0){
					res= 0;
					for (int j = 0; j < rs.size(); j++) {
						res += rs.get(j).getAmount();
					}
				}
				if(rs.size() > 0  && res > 0){
					res += Double.parseDouble(me.get(i).getAmount());
					p = new Payement(me.get(i).getInvoid(), me.get(i).getInvoice(), res, (double)me.get(i).getTotal_ticket().getTotal_ttc());
					p.setSoc(Integer.parseInt(me.get(i).getIdclt()));
					//Log.e("rs "+me.get(i).getInvoice(),res+" *"+p.toString());
				}else{
					p = new Payement(me.get(i).getInvoid(), me.get(i).getInvoice(), Double.parseDouble(me.get(i).getAmount()), (double)me.get(i).getTotal_ticket().getTotal_ttc());
					p.setSoc(Integer.parseInt(me.get(i).getIdclt()));
					//Log.e("rs "+me.get(i).getInvoice(),res+" "+p.toString());
				}
				ps.add(p);
			}
		}
		
		rs = new ArrayList<>();
		res = 0;
		if(pss.size() > 0){
			for (int i = 0; i < pss.size(); i++) {
				rs = new ArrayList<>();
				rs = this.LoadReglement(pss.get(i).getId()+"");
				if(rs.size() > 0){
					res = 0;
					for (int j = 0; j < rs.size(); j++) {
						res += rs.get(j).getAmount();
					}
				}
				if(rs.size() > 0 && res > 0){
					res += pss.get(i).getAmount();
					pss.get(i).setAmount(res);	
				}
				ps.add(pss.get(i));
			}
		}
		Log.e("pay out ",ps.size()+"");
		//Log.e("payement ",st);
		return ps;
	}

	@Override
	public void CleanReglement() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/reglementdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public List<Prospection> synchronisationClientsEnligne(Compte cp) {
		// TODO Auto-generated method stub
		List<Prospection> clx = this.LoadProspection("");
		List<Prospection> ps = new ArrayList<>();
		String s="";
		
		List<Prospection> cl = new ArrayList<>();
		
		
		HashMap<Myinvoice, Prospection> ms = chargerInvoiceprospect(cp);
		
		boolean isit = false;
		
		if(ms.size() > 0){
			
			for (int i = 0; i < clx.size(); i++) {
				isit = false;
				
					for(Myinvoice m:ms.keySet()){
						if(clx.get(i).getIdpros() == Integer.parseInt(m.getIdclt())){
							
						}
					}
			}
				
			}
		if(cl.size() > 0){
			for (int i = 0; i < cl.size(); i++) {
				s = managerclient.insert(cp, cl.get(i));
				if(s != null && s.equals("-1")){
					//if(!s.startsWith("Client ajouter avec success") && !s.startsWith("Prospect ajout� avec success") && !s.equals("")){
						ps.add(cl.get(i));
					//}
				}
			}
			/*
			CleanProspection();
			if(ps.size() > 0){
				for (int i = 0; i < ps.size(); i++) {
					shynchronizeProspection(ps.get(i),cp);
				}
			}
			*/
		}
		
		
		return ps;
	}

	@Override
	public List<Myinvoice> synchronisationFactureEnligne(Compte cp) {
		// TODO Auto-generated method stub
		List<Myinvoice> me = this.LoadInvoice("");
		List<Myinvoice> tmp = new ArrayList<>();
		HashMap<String, Remises> rs = new HashMap<>();
		List<MyProdRemise> mpr = new ArrayList<>();
		String[] st =new String[]{"--- end error code=","Erreur ressayer plus tard !!"};
		FileData ds = new FileData();
		boolean isit = false;
		
		List<Reglement> tmreg;
		if(me.size() > 0){
			for (int i = 0; i < me.size(); i++) {
				tmreg = new ArrayList<>();
				isit = false;
				mpr = me.get(i).getRemises();
				for (int j = 0; j < mpr.size(); j++) {
					rs.put(mpr.get(j).getRef(),mpr.get(j).getRemise());
				}
				
				tmreg = showRegInvo(me.get(i).getInvoid());
				HashMap<Integer, Reglement> hstmp = new HashMap<>();
				for (int k = 0; k < tmreg.size(); k++) {
					hstmp.put(tmreg.get(k).getIdreg(), tmreg.get(k));
				}
				
				//ds = managerfacture.insertoff(me.get(i).getPrd(), me.get(i).getIdclt(), me.get(i).getNmb(), me.get(i).getCommentaire(), cp, me.get(i).getReglement(), me.get(i).getAmount(), me.get(i).getNumChek(), me.get(i).getTypeImpriment(), rs,hstmp);
				
				if(ds.getErreur() != null){
						if(st[0].startsWith(ds.getErreur()) || st[1].startsWith(ds.getErreur())){
							isit = true;
						}
				}
				if(isit){
					tmp.add(me.get(i));
				}
			}
		}
		
		return tmp;
	}

	@Override
	public List<Reglement> synchronisationReglementEnligne(Reglement reg, Compte c) {
		// TODO Auto-generated method stub
		List<Reglement> me = this.showServerside(0); //this.LoadReglement("");
		List<Reglement> tmp = new ArrayList<>();
		String st ="";
		Log.e("szize ",me.size()+"");
		if(me.size() > 0 && c != null){
			for (int i = 0; i < me.size(); i++) {
				st = managerpayement.insertPayement(me.get(i), c);
				Log.e("str >> ",st);
				if(st != null){
					if(!st.startsWith("Paiement ajouter avec success")){
						tmp.add(me.get(i));
					}
				}
			}
			
			List<Reglement> in = LoadReglement("");
			for (int i = 0; i < in.size(); i++) {
				for (int j = 0; j < me.size(); j++) {
					if(in.get(i).getId() != me.get(j).getId()){
						tmp.add(in.get(i));
					}
				}
			}
			
			
			CleanReglement();
			if(tmp.size() > 0){
				for (int i = 0; i < tmp.size(); i++) {
					shynchronizeReglement(tmp.get(i));
				}
			}
			
		}
		
		
		return tmp;
	}

	@Override
	public long shynchronizeBluetooth(MyTicketBluetooth bpd) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			file = new File(path, "/bluetoothdata.txt");

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			Log.e("ticket ",bpd.toString());
			
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(bpd != null){
					bpd.setDt(sdf.format(new Date()));
						pout.println("["+gson.toJson(bpd,MyTicketBluetooth.class)+"]");
						ix = 1;
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save MyTicketBluetooth",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<MyTicketBluetooth> LoadBluetooth(String fl) {
		// TODO Auto-generated method stub
		List<MyTicketBluetooth> lp = new ArrayList<>();
		try {

			File file = new File(path, "/bluetoothdata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						MyTicketBluetooth pd = new MyTicketBluetooth();
						pd = gson.fromJson(json.toString(), MyTicketBluetooth.class);
						//Log.e("hello >>> ",pd.toString());
						
						lp.add(pd);
						
					}

				}
				
			//	meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load MyTicketBluetooth err ",e.getMessage()  +" << ");
		}
		
		return lp;
	}

	@Override
	public void CleanBluetooth() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/bluetoothdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long shynchronizeGpsTracker(GpsTrackingServiceDao gsp) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			file = new File(path, "/gsptrackerdata.txt");

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(gsp != null){
						pout.println("["+gson.toJson(gsp,GpsTrackingServiceDao.class)+"]");
						ix = 1;
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save MyTicketBluetooth",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<GpsTrackingServiceDao> LoadGpsTracker(String fl) {
		// TODO Auto-generated method stub
		List<GpsTrackingServiceDao> lp = new ArrayList<>();
		try {

			File file = new File(path, "/gsptrackerdata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						GpsTrackingServiceDao pd = new GpsTrackingServiceDao();
						pd = gson.fromJson(json.toString(), GpsTrackingServiceDao.class);
						//Log.e("hello >>> ",pd.toString());
						
						lp.add(pd);
						
					}

				}
				
			//	meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load GpsTrackingServiceDao err ",e.getMessage()  +" << ");
		}
		
		return lp;
	}

	@Override
	public void CleanGpsTracker() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/gsptrackerdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long shynchronizePayemntTicket(MyTicketPayement tp) {
		// TODO Auto-generated method stub
		long ix =-1;
		try {
			file = new File(path, "/payementticket.txt");

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(tp != null){
						pout.println("["+gson.toJson(tp,MyTicketPayement.class)+"]");
						Log.e("ticket p2","["+gson.toJson(tp,MyTicketPayement.class)+"]");
						ix = 1;
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save MyTicketPayement",e.getMessage()  +" << ");
		}
		
		return ix;
	}

	@Override
	public List<MyTicketPayement> LoadTicketPayement(String fl) {
		// TODO Auto-generated method stub
		List<MyTicketPayement> lp = new ArrayList<>();
		try {

			File file = new File(path, "/payementticket.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						//Log.e("line >>> ",line);
						JSONObject json = jArray.getJSONObject(i);
						MyTicketPayement pd = new MyTicketPayement();
						pd = gson.fromJson(json.toString(), MyTicketPayement.class);
						//Log.e("hello >>> ",pd.toString());
						
						lp.add(pd);
						
					}

				}
				
			//	meme(st);
				
				//Log.e("file promo",st);
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load payement ticket err ",e.getMessage()  +" << ");
		}
		
		return lp;
	}

	@Override
	public void CleanPayementTicket() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/payementticket.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void synchronisationPositionsGPS() {
		// TODO Auto-generated method stub
		try {
			List<GpsTrackingServiceDao> gps = this.LoadGpsTracker("");
			if(gps.size() > 0){
				for (int i = 0; i < gps.size(); i++) {
					new GpsTrackingServiceDao(gps.get(i).getNum(), gps.get(i).getLevel(), gps.get(i).getLatitude(), gps.get(i).getLongitude(), gps.get(i).getSpeed(), gps.get(i).getAltitude(), gps.get(i).getDirection(), gps.get(i).getSatellite(), gps.get(i).getDateGps(), gps.get(i).getCompte());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public MyTicketBluetooth checkMyFactureticket(int rf) {
		// TODO Auto-generated method stub
		MyTicketBluetooth m = new MyTicketBluetooth();
		List<MyTicketBluetooth> mx = this.LoadBluetooth("");
		
		for (int i = 0; i < mx.size(); i++) {
			if(mx.get(i).getMe().getInvoid() == rf){
				m = mx.get(i);
			}
		}
		return m;
	}

	@Override
	public MyTicketPayement checkMyReglementicket(int ref) {
		// TODO Auto-generated method stub
		MyTicketPayement m = new MyTicketPayement();
		
		List<MyTicketPayement> ls = this.LoadTicketPayement("");
		
		
		for (int i = 0; i < ls.size(); i++) {
			Log.e(">> "+ls.get(i).getMyreg().getPaiementcode(),ref+"");
			if(ls.get(i).getMyreg().getIdreg() == ref){
				m = ls.get(i);
			}
		}
		
		
		return m;
	}

	@Override
	public boolean checkFilexsiste() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkFolderexsiste() {
		// TODO Auto-generated method stub
		boolean isit = false;
		try {
			file = new File(path+"/");
			
			if(file.exists()){
				isit = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return isit;
	}

	@Override
	public long cleancache() {
		// TODO Auto-generated method stub
		long vl =0;
		CleanClients();
		CleanProduits();
		CleanDico();
		CleanPromotionProduit();
		CleanPromotionClient();
		CleanProspectData();
		CleanPayement();
		cleanForUpdate();
		CleanCategorieList();
		return vl;
	}

	@Override
	public List<Reglement> showRegInvo(int id) {
		// TODO Auto-generated method stub
		List<Reglement> lsreg = LoadReglement("");
		List<Reglement> tmp = new ArrayList<>();
        for(Reglement r:lsreg){
       	 if(r.getId() == id){
       		 tmp.add(r);
       	 }
       	 
        }
		return tmp;
	}

	@Override
	public List<Reglement> showServerside(int id) {
		// TODO Auto-generated method stub
		List<Reglement> res = new ArrayList<>();
		
		List<Reglement> in = LoadReglement("");
		List<Myinvoice> in2 = LoadInvoice("");
		
		for (int i = 0; i < in.size(); i++) {
			if(in2.size() > 0){
				for (int j = 0; j < in2.size(); j++) {
					if(in.get(i).getId() != in2.get(j).getInvoid()){
						res.add(in.get(i));
					}
				}
			}else{
				res.add(in.get(i));
			}
		}
		
		return res;
	}

	@Override
	public long checkAvailableofflinestorage() {
		// TODO Auto-generated method stub
		long x=0;
		x += LoadProspection("").size();
		x += LoadInvoice("").size();
		x += LoadReglement("").size();
		x += LoadCmdList("").size();
		x += LoadCmdToFact("").size();
		x += LoadMouvement("").size();
		
		Log.e("nbr >>",x+"");
		
		return x;
	}
	
	@Override
	public long checkAvailableofflinestorage2() {
		// TODO Auto-generated method stub
		long x=0;
		x += LoadInterventions("").size();
		
		Log.e("nbr >>",x+"");
		
		return x;
	}

	@Override
	public long shynchronizeGpsInvoice(MyGpsInvoice ds) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			//CleanClients();
			file = new File(path, "/gpsinvoicedata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(ds != null){
						pout.println("["+gson.toJson(ds,MyGpsInvoice.class)+"]");
						ix =1;
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save GPS INVOICE",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<MyGpsInvoice> LoadGpsInvoice(String fl) {
		// TODO Auto-generated method stub
		List<MyGpsInvoice> ls = new ArrayList<>();
		try {

			File file = new File(path, "/gpsinvoicedata.txt");
			//Log.e("file path",file.getAbsolutePath());
			if(file.exists()){
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;

				encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
				while ((line = r.readLine()) != null) {
					JSONArray dico = new JSONArray(line);
					for (int j = 0; j < dico.length(); j++) {
						JSONObject json = dico.getJSONObject(j);
						MyGpsInvoice sd = new MyGpsInvoice();
							sd = gson.fromJson(json.toString(), MyGpsInvoice.class); 
							
							ls.add(sd);
						}

					}
				r.close();
				secondInputStream.close();
				}
			

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load gps invoice",e.getMessage()  +" << ");
		}
		return ls;
	}

	@Override
	public void CleanGpsInvoice() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/gpsinvoicedata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("clean gps invoice",e.getMessage()  +" << ");
		}
	}

	@Override
	public List<MyGpsInvoice> synchronisationGPSInvoiceEnligne(Compte cp) {
		// TODO Auto-generated method stub
		List<MyGpsInvoice> tmp = new ArrayList<>();
		try {
			List<MyGpsInvoice> rs = LoadGpsInvoice("");
			
			ServiceDao dao = new ServiceDao();
			
			String s ="";
			for (int i = 0; i < rs.size(); i++) {
				s = dao.insertData(rs.get(i).getGps(), rs.get(i).getImei(), rs.get(i).getNum(), rs.get(i).getBattery(), rs.get(i).getC(), rs.get(i).getFact());
				if( s != null ){
					if(s.equals("-1")){
						tmp.add(rs.get(i));
					}
				}
			}
			
			CleanGpsInvoice();
			if(tmp.size() > 0){
				for (int i = 0; i < tmp.size(); i++) {
					shynchronizeGpsInvoice(tmp.get(i));
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return tmp;
	}

	@Override
	public HashMap<Myinvoice, Prospection> chargerInvoiceprospect(
			Compte cp) {
		// TODO Auto-generated method stub
		List<Myinvoice> me = this.LoadInvoice("");
		List<Prospection> tmp = LoadProspection("");
		
		List<Reglement> tmreg;
		
		HashMap<Myinvoice, Prospection> myo = new HashMap<>();
		
		
		Log.e("size invo ",me.size()+"");
		myc = new HashMap<>();
		/*
		HashMap<Prospection, List<Myinvoice>> res = new HashMap<>();
		List<Myinvoice> msinvo;
		
		
		if(tmp != null){
			if(tmp.size() > 0){
				for (int i = 0; i < tmp.size(); i++) {
					msinvo = new ArrayList<>();
					for (int j = 0; j < me.size(); j++) {
						if(me.get(j).getIdclt().equals(tmp.get(i).getId())){
							tmreg = new ArrayList<>();
							
							tmreg = showRegInvo(me.get(j).getInvoid());
							
							me.get(j).setLsregs(tmreg);
							
							msinvo.add(me.get(j));
						}
					}
					
					res.put(tmp.get(i),msinvo);
				}
			}
		}
		*/
		boolean isit = false;
		if(me.size() > 0){
			for (int i = 0; i < me.size(); i++) {
				isit = false;
				Log.e(">>> "+me.get(i).getIdclt(),"<<<");
				for (int j = 0; j < tmp.size(); j++) {
					
					Log.e(">>> "+me.get(i).getIdclt(),"<<<"+tmp.get(j).getIdpros());
					if(me.get(i).getIdclt().equals(tmp.get(j).getIdpros()+"")){
						isit = true;
						
						tmreg = new ArrayList<>();
						
						tmreg = showRegInvo(me.get(i).getInvoid());
						
						me.get(i).setLsregs(tmreg);
						
						myo.put(me.get(i), tmp.get(j));
						break;
					}
				}
				
				if(isit == false){
					tmreg = new ArrayList<>();
					
					tmreg = showRegInvo(me.get(i).getInvoid());
					
					me.get(i).setLsregs(tmreg);
					
					myc.put(me.get(i), me.get(i).getIdclt());
					//break;
				}
			}
		}
		
		
		return myo;
	}

	@Override
	public HashMap<Myinvoice, String>  chargerInvoiceclient() {
		// TODO Auto-generated method stub
		/*
		List<Myinvoice> me = this.LoadInvoice("");
		List<Client> tmp = LoadClients("");
		
		List<Reglement> tmreg;
		
		HashMap<Client, List<Myinvoice>> res = new HashMap<>();
		List<Myinvoice> msinvo;
		
		if(tmp != null){
			if(tmp.size() > 0){
				for (int i = 0; i < tmp.size(); i++) {
					msinvo = new ArrayList<>();
					for (int j = 0; j < me.size(); j++) {
						if(me.get(j).getIdclt().equals(tmp.get(i).getId())){
							tmreg = new ArrayList<>();
							
							tmreg = showRegInvo(me.get(j).getInvoid());
							
							me.get(j).setLsregs(tmreg);
							
							msinvo.add(me.get(j));
						}
					}
					
					res.put(tmp.get(i),msinvo);
				}
			}
		}
		*/
		return myc;
	}

	@Override
	public DataErreur synchronisationInvoiceOut(Compte cp) {
		// TODO Auto-generated method stub
		HashMap<String, Remises> rs = new HashMap<>();
		List<MyProdRemise> mpr = new ArrayList<>();
		
		
		HashMap<Myinvoice, Prospection> out = new HashMap<>();
		out = this.chargerInvoiceprospect(cp);
		
		//new structure 
		List<Prospection> lspros = new ArrayList<>();
		List<Myinvoice> lsinvo = new ArrayList<>();
		List<Reglement> lsreg = new ArrayList<>();
		DataErreur data = new DataErreur();
		
		String vl ="";
		try {
			
			if(out.size() > 0){
				for(Myinvoice invo:out.keySet() ){
					Prospection ps = out.get(invo);
					mpr = invo.getRemises();
					for (int j = 0; j < mpr.size(); j++) {
						rs.put(mpr.get(j).getRef(),mpr.get(j).getRemise());
					}
					
					List<Reglement> tmreg = invo.getLsregs();
					HashMap<Integer, Reglement> hstmp = new HashMap<>();
					for (int k = 0; k < tmreg.size(); k++) {
						hstmp.put(tmreg.get(k).getIdreg(), tmreg.get(k));
					}
					
					 vl = managerfacture.insertoffline(ps, invo.getPrd(), invo.getIdclt(), invo.getNmb(), invo.getCommentaire(), invo.getCompte(), invo.getReglement(), invo.getAmount(), invo.getNumChek(), invo.getTypeImpriment(), rs, hstmp);
					 
					 Log.e("first one ",vl);
					 
					 try {
						 JSONObject json = new JSONObject(vl);
						 
							String clt =  json.getString("feedbackclt");
							String inv =  json.getString("feedbackinvo");
							String reg =  json.getString("feedbackreg");
							
							if(clt.equals("-1")){
								lspros.add(ps);
								lsinvo.add(invo);
								if(invo.getLsregs().size() > 0){lsreg.addAll(invo.getLsregs());}
							}else if(!clt.equals("-1") && inv.equals("-1")){
								//update invo set clt == new clt and del clt
								Myinvoice in = invo;
								in.setIdclt(clt);
								lsinvo.add(in);
								if(in.getLsregs().size() > 0){lsreg.addAll(in.getLsregs());}
							}else if(!clt.equals("-1") && !inv.equals("-1") && !reg.equals("-1")){
								//update reg set fc == new and del clt and invo
								//$idobject."#".$idclient."#".$regid
								JSONArray jarray = json.getJSONArray("reglement_erreur");
								for (int i = 0; i < jarray.length(); i++) {
									String s = jarray.getString(i);
									for (int j = 0; j < invo.getLsregs().size(); j++) {
										if(invo.getLsregs().get(j).getIdreg() == Integer.parseInt(s.split("#")[2])){
											Reglement r = invo.getLsregs().get(j);
											r.setIdUser(s.split("#")[1]);
											r.setId(Integer.parseInt(s.split("#")[0]));
											lsreg.add(r);
											break;
										}
									}
								}
							}
							
							
							
					} catch (Exception e) {
						// TODO: handle exception
						lspros.add(ps);
						lsinvo.add(invo);
						if(invo.getLsregs().size() > 0){lsreg.addAll(invo.getLsregs());}
					}
				}
				
				data = new DataErreur(lspros, lsinvo, lsreg);
			}
			 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return data;
	}


	@Override
	public List<Reglement> chergerReglementClt(Compte cp) {
		// TODO Auto-generated method stub
		List<Reglement> res = new ArrayList<>();
		int n=0;
		try {
			List<Myinvoice> me = new ArrayList<>();
			me = LoadInvoice("");
			List<Reglement> tmp = new ArrayList<>();
			tmp = LoadReglement("");
			
			if(tmp.size() > 0){
				for (int i = 0; i < tmp.size(); i++) {
					n = 0;
					if(me.size() > 0){
						for (int j = 0; j < me.size(); j++) {
							if(tmp.get(i).getId() == me.get(j).getInvoid()){
								n=n+1;
							}
						}
						
						if(n == 0 ){
							res.add(tmp.get(i));
						}
					}else{
						res.add(tmp.get(i));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	@Override
	public List<Reglement> synchronisationReglementOut(Compte cp) {
		// TODO Auto-generated method stub
		List<Reglement> res = new ArrayList<>();
		try {
			String st ="";
			List<Reglement> rs = new ArrayList<>();
			rs = chergerReglementClt(cp);
			if(rs.size() > 0){
				for (int i = 0; i < rs.size(); i++) {
					st = managerpayement.insertPayement(rs.get(i),cp);
					if(st.equals("no")){
						res.add(rs.get(i));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return res;
	}

	@Override
	public DataErreur synchronisationPayementOut(HashMap<Myinvoice, String> in,HashMap<Commande,String> cmd_cl) {
		// TODO Auto-generated method stub
		String st ="";
		
		//new structure 
				List<Prospection> lspros = new ArrayList<>();
				List<Myinvoice> lsinvo = new ArrayList<>();
				List<Reglement> lsreg = new ArrayList<>();
				List<MyGpsInvoice> mygps = new ArrayList<>();
				List<Commande> lscmds = new ArrayList<>();
				DataErreur data = new DataErreur();
		
				ServiceDao daoGps = new ServiceDao();
				
			if(in.size() > 0){
				for(Myinvoice m:in.keySet()){
					HashMap<String, Remises> rs = new HashMap<>();
					List<MyProdRemise> mpr = new ArrayList<>();


						mpr = m.getRemises();
						for (int j = 0; j < mpr.size(); j++) {
							rs.put(mpr.get(j).getRef(),mpr.get(j).getRemise());
						}
						
						List<Reglement> tmreg = m.getLsregs();
						HashMap<Integer, Reglement> hstmp = new HashMap<>();
						for (int k = 0; k < tmreg.size(); k++) {
							hstmp.put(tmreg.get(k).getIdreg(), tmreg.get(k));
						}
						
					
						try {
							
							st = managerfacture.insertcicin(m.getPrd(), m.getIdclt(), m.getNmb(), m.getCommentaire(), m.getCompte(), m.getReglement(), m.getAmount(), m.getNumChek(), m.getTypeImpriment(), rs,hstmp,m.getIdnew(),m.getType_invoice());
							
							Log.e("json cl invo ",st);
							String stfomat = st.substring(st.indexOf("{"),st.lastIndexOf("}")+1);
							 JSONObject json = new JSONObject(stfomat);
							 
								String inv =  json.getString("feedbackinvo");
								String reg =  json.getString("feedbackreg");
								int nbreg = json.getInt("feedbackregnbr");
								
								
								
								 
								if(inv.equals("-1")){
									//update invo set clt == new clt and del clt
									Myinvoice ci = m;
									
									List<Reglement> r = ci.getLsregs();
									
									for (int j = 0; j < r.size(); j++) {
										lsreg.add(r.get(j));
									}
									
									r = new ArrayList<>();
									ci.setLsregs(r);
									
									lsinvo.add(ci);
									
								}else if(!inv.equals("-1") && !reg.equals("-1") || (!inv.equals("-1") && nbreg > 0)){
									//update reg set fc == new and del clt and invo
									//$idobject."#".$idclient."#".$regid
									
									/*
									 JSONArray jarray = json.getJSONArray("reglement_erreur");
									for (int i = 0; i < jarray.length(); i++) {
										String s = jarray.getString(i);
										for (int j = 0; j < m.getLsregs().size(); j++) {
											if(m.getLsregs().get(j).getIdreg() == Integer.parseInt(s.split("#")[2])){
												Reglement r = m.getLsregs().get(j);
												r.setIdUser(s.split("#")[1]);
												r.setId(Integer.parseInt(s.split("#")[0]));
												lsreg.add(r);
												break;
											}
										}
									}
									*/
									//Log.e("nbr reg ",nbreg+"");
									
									 JSONObject jsonreg = new JSONObject(json.getString("outreg"));
									 for (int i = 0; i < nbreg; i++) {
										String s = jsonreg.getString("reglement_erreur"+i);
												for (int j = 0; j < m.getLsregs().size(); j++) {
													if(m.getLsregs().get(j).getIdreg() == Integer.parseInt(s.split("#")[2])){
														Reglement r = m.getLsregs().get(j);
														r.setIdUser(s.split("#")[1]);
														r.setId(Integer.parseInt(s.split("#")[0]));
														Log.e("reg "+s,r.toString());
														//lsreg.add(r);
														break;
													}
												}
									}
								}
								
								
								if(!inv.equals("-1")){
									MyGpsInvoice gp = checkGpsInvoice(m.getInvoice());
									gp.setFact(inv);
									//Log.e("gps ",gp.toString());
									String s = daoGps.insertData(gp.getGps(),gp.getImei(),gp.getNum(),gp.getBattery(),gp.getC(),gp.getFact());
									
									if(s.equals("no")){
										mygps.add(gp);
									}
								}
								
								
						} catch (Exception e) {
							// TODO: handle exception
							Myinvoice ci = m;
							
							List<Reglement> r = ci.getLsregs();
							
							for (int j = 0; j < r.size(); j++) {
								lsreg.add(r.get(j));
							}
							
							r = new ArrayList<>();
							ci.setLsregs(r);
							
							lsinvo.add(ci);
							
							
							MyGpsInvoice gp = checkGpsInvoice(ci.getInvoice());
							mygps.add(gp);
							Log.e("input reglement payement ",e.getMessage()  +" << ");
						}
						
				}
			}
			
			if(cmd_cl.size() > 0){
				for(Commande cm:cmd_cl.keySet()){
					try {
						Log.e("cmd >>> ",cm.toString()+"");
						Commande c = SendCommande(cm, cm.getCompte());
						if(c != null){
							lscmds.add(cm);
						}
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("cmd >> payement ds ",e.getMessage()+"");
						lscmds.add(cm);
					}
				}
			}
			
			List<Prospection> lsps = new ArrayList<>();
			data = new DataErreur(lsps, lsinvo, lsreg);
			data.setMsgps(mygps);
			data.setCmd(lscmds);
		
		return data;
	}

	@Override
	public HashMap<String, HashMap<Prospection, List<Myinvoice>>> chargerInvoice_prospect(
			Compte cp) {
		// TODO Auto-generated method stub
		List<Myinvoice> me = this.LoadInvoice("");
		List<Prospection> tmp = LoadProspection("");
		
		List<Reglement> tmreg;
		
		HashMap<Prospection, List<Myinvoice>> myo = new HashMap<>();
		HashMap<Prospection, List<Myinvoice>> myo2 = new HashMap<>();
		
		HashMap<String, HashMap<Prospection, List<Myinvoice>>>  res = new HashMap<>();
		
		List<Myinvoice> invtmp ;
		
		List<Myinvoice> invps = new ArrayList<>();
		
		try {
			
			
			if(tmp.size() > 0){
				
				for (int i = 0; i < tmp.size(); i++) {
					invtmp = new ArrayList<>();
					
					if(me.size() > 0){
						
						for (int j = 0; j < me.size(); j++) {
							
							if(Integer.parseInt(me.get(j).getIdclt()) == tmp.get(i).getIdpros()){
								
								
								tmreg = new ArrayList<>();
								
								tmreg = showRegInvo(me.get(j).getInvoid());
								
								
								me.get(j).setLsregs(tmreg);
								
								invtmp.add(me.get(j));
								
								invps.add(me.get(j));
							}
						}
						
					}
					
					myo.put(tmp.get(i), invtmp);
				}
				
			}
			myo2.put(new Prospection(), invps);
			
			res.put("ps", myo);
			res.put("all", myo2);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	@Override
	public HashMap<Myinvoice,String> chargerInvoice_client(HashMap<String, HashMap<Prospection, List<Myinvoice>>> da,Compte cp) {
		// TODO Auto-generated method stub
		List<Myinvoice> me = this.LoadInvoice("");
		
		List<Myinvoice> mx = new ArrayList<>();
		
		HashMap<String, HashMap<Prospection, List<Myinvoice>>>  res = new HashMap<>();
		
		HashMap<Prospection, List<Myinvoice>> myo = new HashMap<>();
		
		List<Reglement> tmreg = new ArrayList<>();
		
		
		HashMap<Myinvoice,String> data = new HashMap<>();
		
		boolean isit = false;
		
		try {
			res = da ; //chargerInvoice_prospect(cp);
			
			if(res.size() > 0){
				myo = res.get("all");
				for(Prospection ps:myo.keySet()){
					mx = myo.get(ps);
				}
				
				
				if(me.size() > mx.size()){
					
					for (int i = 0; i < me.size(); i++) {
						isit = false;
						
						for (int j = 0; j < mx.size(); j++) {
							if(me.get(i).getInvoid() == mx.get(j).getInvoid()){
								isit = true;
								break;
							}
						}
						
						if(!isit){
							tmreg = new ArrayList<>();
							
							tmreg = showRegInvo(me.get(i).getInvoid());
							
							me.get(i).setLsregs(tmreg);
							
							
							data.put(me.get(i), me.get(i).getIdclt());
						}
						
					}
					
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return data;
	}

	@Override
	public DataErreur synchronisation_Invoice_Out(
			HashMap<Prospection, List<Myinvoice>> out, Compte cp,HashMap<Prospection, List<Commande>> cmd_ps) {
		// TODO Auto-generated method stub
		
		
		//new structure 
		List<Prospection> lspros = new ArrayList<>();
		List<Myinvoice> lsinvo = new ArrayList<>();
		List<Reglement> lsreg = new ArrayList<>();
		
		List<MyGpsInvoice> mygps = new ArrayList<>();
		
		List<Commande> lscmds = new ArrayList<>();
		
 		DataErreur data = new DataErreur();
		
		ServiceDao daoGps = new ServiceDao();
		
		
		
		String cl ="";
		String fc ="";
		try {
			
			if(out.size() > 0){
				for(Prospection ps:out.keySet() ){
					
					/* liste des factures */
					List<Myinvoice> invo = new ArrayList<>();
					invo = out.get(ps);
					
					/* liste des commandes */
					List<Commande> cmds = new ArrayList<>();
					cmds = cmd_ps.get(ps);
					
					if(cmds != null) cmds = new ArrayList<>();
					
					
					cl = managerclient.insert(cp, ps);
					if(cl != null && !cl.equals("-1")){
							
						for (int i = 0; i < invo.size(); i++) {
							HashMap<String, Remises> rs = new HashMap<>();
							List<MyProdRemise> mpr = new ArrayList<>();
							
								mpr = invo.get(i).getRemises();
								for (int j = 0; j < mpr.size(); j++) {
									rs.put(mpr.get(j).getRef(),mpr.get(j).getRemise());
								}
								
								List<Reglement> tmreg = invo.get(i).getLsregs();
								HashMap<Integer, Reglement> hstmp = new HashMap<>();
								for (int k = 0; k < tmreg.size(); k++) {
									hstmp.put(tmreg.get(k).getIdreg(), tmreg.get(k));
								}
								
							
								try {
									
									fc = managerfacture.insertcicin(invo.get(i).getPrd(), cl, invo.get(i).getNmb(), invo.get(i).getCommentaire(),invo.get(i).getCompte(), invo.get(i).getReglement(), invo.get(i).getAmount(), invo.get(i).getNumChek(), invo.get(i).getTypeImpriment(), rs,hstmp,invo.get(i).getIdnew(),invo.get(i).getType_invoice());
									
									String stfomat = fc.substring(fc.indexOf("{"),fc.lastIndexOf("}")+1);
									 JSONObject json = new JSONObject(stfomat);
									 
									 Log.e("json invo ",fc);
									 
										String inv =  json.getString("feedbackinvo");
										String reg =  json.getString("feedbackreg");
										int nbreg = json.getInt("feedbackregnbr");
										
										
										if(!inv.equals("-100")){
											if(inv.equals("-1")){
												//update invo set clt == new clt and del clt
												Myinvoice ci = invo.get(i);
												ci.setIdclt(cl);
												
												List<Reglement> r = ci.getLsregs();
												
												for (int j = 0; j < r.size(); j++) {
													lsreg.add(r.get(j));
												}
												
												r = new ArrayList<>();
												ci.setLsregs(r);
												
												lsinvo.add(ci);
												
												
											}else if(!inv.equals("-1") && !reg.equals("-1") || (!inv.equals("-1") && nbreg > 0)){
												//update reg set fc == new and del clt and invo
												//$idobject."#".$idclient."#".$regid
												
												//Log.e("nbr reg ",nbreg+"");
												
												if(nbreg > 0){
													 JSONObject jsonreg = new JSONObject(json.getString("outreg"));
													 for (int i1 = 0; i1 < nbreg; i1++) {
														String s = jsonreg.getString("reglement_erreur"+i1);
																for (int j = 0; j < invo.get(i).getLsregs().size(); j++) {
																	if(invo.get(i).getLsregs().get(j).getIdreg() == Integer.parseInt(s.split("#")[2])){
																		Reglement r = invo.get(i).getLsregs().get(j);
																		r.setIdUser(s.split("#")[1]);
																		r.setId(Integer.parseInt(s.split("#")[0]));
																		Log.e("reg "+s,r.toString());
																		//lsreg.add(r);
																		break;
																	}
																}
													}
												}
												
												/*
												JSONArray jarray = json.getJSONArray("reglement_erreur");
												for (int k = 0; k < jarray.length(); k++) {
													String s = jarray.getString(k);
													for (int l = 0; l < invo.get(i).getLsregs().size(); l++) {
														if(invo.get(i).getLsregs().get(l).getIdreg() == Integer.parseInt(s.split("#")[2])){
															Reglement r = invo.get(i).getLsregs().get(l);
															//r.setIdUser(s.split("#")[1]);
															r.setId(Integer.parseInt(s.split("#")[0]));
															lsreg.add(r);
															break;
														}
													}
												}
												*/
											}
											
											if(!inv.equals("-1")){
												MyGpsInvoice gp = checkGpsInvoice(invo.get(i).getInvoice());
												gp.setFact(inv);
												String s = daoGps.insertData(gp.getGps(),gp.getImei(),gp.getNum(),gp.getBattery(),gp.getC(),gp.getFact());
												if(s.equals("no")){
													mygps.add(gp);
												}
											}
											
										
										}
										
								} catch (Exception e) {
									// TODO: handle exception
									Myinvoice ci = invo.get(i);
									ci.setIdclt(cl);
									
									List<Reglement> r = ci.getLsregs();
									
									for (int j = 0; j < r.size(); j++) {
										lsreg.add(r.get(j));
									}
									
									r = new ArrayList<>();
									ci.setLsregs(r);
									
									lsinvo.add(ci);
									

									MyGpsInvoice gp = checkGpsInvoice(ci.getInvoice());
									mygps.add(gp);
									
									Log.e("in invoice add with reglement ",e.getMessage()  +" << ");
									
								}
						}
						
						for (int j = 0; j < cmds.size(); j++) {
							cmds.get(j).setClt(cl);
							try {
								Commande cd = SendCommande(cmds.get(j), cp) ;
								if(cd == null){
									lscmds.add(cd);
								}
							} catch (Exception e) {
								// TODO: handle exception
								lscmds.add(cmds.get(j));
							}
						}
						
						
					}else{
						lspros.add(ps);
						if(invo.size() > 0){
							for (int i = 0; i < invo.size(); i++) {
								
								List<Reglement> r = invo.get(i).getLsregs();
								
								for (int j = 0; j < r.size(); j++) {
									lsreg.add(r.get(j));
								}
								
								r = new ArrayList<>();
								invo.get(i).setLsregs(r);
								
								lsinvo.add(invo.get(i));
								
								MyGpsInvoice gp = checkGpsInvoice(invo.get(i).getInvoice());
								mygps.add(gp);
							}
						}
						if(cmds != null){
							for (int i = 0; i < cmds.size(); i++) {
								lscmds.add(cmds.get(i));
							}
						}
					}
					
				}
				
				data = new DataErreur(lspros, lsinvo, lsreg);
				data.setMsgps(mygps);
				data.setCmd(lscmds);
			}
			 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return data;
	}

	@Override
	public long CleanAlldataOut(DataErreur data,Compte cp) {
		// TODO Auto-generated method stub
		
		long k =0;
		
		CleanProspection();
		CleanInvoice();
		CleanReglement();
		CleanGpsInvoice();
		CleanCmdList();
		CleanCmdToFactList();
		
		for (int i = 0; i < data.getPros().size(); i++) {
			shynchronizeProspection_out(data.getPros().get(i), cp);
		}
		
		for (int j = 0; j < data.getInvo().size(); j++) {
			saveInvoice(data.getInvo().get(j));
		}
		
		for (int i = 0; i < data.getLsreg().size(); i++) {
			shynchronizeReglement(data.getLsreg().get(i));
		}
		
		for (int i = 0; i < data.getMsgps().size(); i++) {
			shynchronizeGpsInvoice(data.getMsgps().get(i));
		}
		
		for (int i = 0; i < data.getCmd().size(); i++) {
			shynchornizeCmd(data.getCmd().get(i));
		}
		
		for (int i = 0; i < data.getCmdview().size(); i++) {
			shynchornizeCmdToFact(data.getCmdview().get(i));
		}
		
		return k;
	}

	@Override
	public long saveInvoice(Myinvoice me) {
		// TODO Auto-generated method stub
		long x = -1;
		try {
			encryptor = new StandardPBEStringEncryptor();encryptor.setPassword("cicinpassword");
			file = new File(path, "/invoicedata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				pout.println("["+gson.toJson(me,Myinvoice.class)+"]");
				pout.close();
				x = 1;
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save invoice new",e.getMessage()  +" << ");
		}

		return x;
	}

	@Override
	public long SendOutData(Compte compte) {
		// TODO Auto-generated method stub
		try {
			DataErreur dataeror1 = new DataErreur();
			DataErreur dataeror2 = new DataErreur();
			DataErreur dataeror3 = new DataErreur();
			
			HashMap<String, HashMap<Prospection, List<Myinvoice>>> data = chargerInvoice_prospect(compte);
			
			HashMap<String, HashMap<Prospection, List<Commande>>> data_cmd = chargerCmd_prospect(compte);
			
			
			// Load factures
			HashMap<Prospection, List<Myinvoice>> invo_ps = data.get("ps");
			
			HashMap<Myinvoice,String> invo_cl = chargerInvoice_client(data, compte);
			
			//Load commandes
			HashMap<Prospection, List<Commande>> cmd_ps = data_cmd.get("ps");
			
			HashMap<Commande,String> cmd_cl = chargerCmd_client(data_cmd, compte);
			
			
			List<Prospection> lspros = new ArrayList<>();
			List<Myinvoice> lsinvo = new ArrayList<>();
			List<Reglement> lsreg = new ArrayList<>();
			List<Commande> lscmd = new ArrayList<>();
			List<Commandeview> lscmdv = new ArrayList<>();
			
			//synchronisation des payements pour des factures d�ja existes
			List<Reglement> ls = synchronisationReglementOut(compte);
			for (int i = 0; i < ls.size(); i++) {
				lsreg.add(ls.get(i));
			}
			
			//synchronisation des factures prospects
			if(invo_ps.size() > 0 || cmd_ps.size() > 0){
				dataeror1 = synchronisation_Invoice_Out(invo_ps, compte,cmd_ps);
			}
			
			
			//synchronisation des fcatures clients
			if(invo_cl.size() > 0 || cmd_cl.size() > 0){
				dataeror2 = synchronisationPayementOut(invo_cl,cmd_cl);
			}
			
			
			if(LoadCmdToFact("").size() > 0){
				lscmdv = synchronisationCmdToFacOut(compte);
			}
			Log.e("***eror1***",invo_ps.size()+"");
			Log.e("***eror2***",invo_cl.size()+"");
			
			Log.e("***cmderor1***",cmd_ps.size()+"");
			Log.e("***cmderor2***",cmd_cl.size()+"");
			
			sendMouvements(compte);
			
			//load prospect
			for (int i = 0; i < dataeror1.getPros().size(); i++) {
				lspros.add(dataeror1.getPros().get(i));
			}
			
			for (int i = 0; i < dataeror2.getPros().size(); i++) {
				lspros.add(dataeror2.getPros().get(i));
			}
			
			//load invoice
			for (int j = 0; j < dataeror1.getInvo().size(); j++) {
				lsinvo.add(dataeror1.getInvo().get(j));
			}
			for (int j = 0; j < dataeror2.getInvo().size(); j++) {
				lsinvo.add(dataeror2.getInvo().get(j));
			}
			
			//load commandes
			for (int j = 0; j < dataeror1.getCmd().size(); j++) {
				lscmd.add(dataeror1.getCmd().get(j));
			}
			for (int j = 0; j < dataeror2.getCmd().size(); j++) {
				lscmd.add(dataeror2.getCmd().get(j));
			}
			
			//Load reglement
			for (int i = 0; i < dataeror1.getLsreg().size(); i++) {
				lsreg.add(dataeror1.getLsreg().get(i));
			}
			for (int i = 0; i < dataeror2.getLsreg().size(); i++) {
				lsreg.add(dataeror2.getLsreg().get(i));
			}
			
			
			
			dataeror3 = new DataErreur(lspros, lsinvo, lsreg);
			dataeror3.setCmd(lscmd);
			dataeror3.setCmdview(lscmdv);
			CleanAlldataOut(dataeror3, compte);
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("send out data bug ",e.getMessage()  +" << ");
		}
		return 0;
	}

	@Override
	public long checkClient_is_Prospect(int ref) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			List<Prospection> ps = new ArrayList<>();
			ps = LoadProspection("");
			
			for (int i = 0; i < ps.size(); i++) {
				if(ps.get(i).getIdpros() == ref){
					ix = 1;
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ix;
	}

	@Override
	public long checkPayement_is_Invoice(int ref) {
		// TODO Auto-generated method stub
		long ix = -1;
		List<Myinvoice> ms = new ArrayList<>();
		ms = LoadInvoice("");
		for (int i = 0; i < ms.size(); i++) {
			if(ms.get(i).getInvoid() == ref){
				ix = 1;
			}
		}
		return ix;
	}

	@Override
	public MyGpsInvoice checkGpsInvoice(String fl) {
		// TODO Auto-generated method stub
		MyGpsInvoice ds = new MyGpsInvoice();
		try {
			List<MyGpsInvoice> ls = new ArrayList<>();
			ls = LoadGpsInvoice("");
			
			for (int i = 0; i < ls.size(); i++) {
				if(ls.get(i).getFact().equals(fl)){
					ds = ls.get(i);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ds;
	}

	@Override
	public Myinvoice prepaValideIvoice(String invoice, List<Produit> prd,
			String idclt, int nmb, String commentaire, Compte compte,
			String reglement, String amount, String numChek, int typeImpriment,
			List<MyProdRemise> remises, GpsTracker gps, String imei,
			String num, String battery, TotauxTicket tt) {
		// TODO Auto-generated method stub
		FileData mx = null;
		Myinvoice me = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); 
		//String st ="";
		try {
			
			
			MyTicketWitouhtProduct soc = this.LoadSociete("");
			Client cl = this.seeClient(this.LoadClients(""), idclt);
				if(soc != null && cl != null){
					me = new Myinvoice();
					
					if(invoice != null){
						if(invoice.equals("-1"))invoice = calculIdInvoice();
					}else{
						invoice = calculIdInvoice();
					}
					
					
//					me.setInvoice("PROV-"+compte.getLogin()+"-"+invoice);
					me.setInvoice(invoice);
					me.setInvoid(Integer.parseInt(calculIdInvoice()));
					me.setPrd(prd);
					me.setIdclt(idclt);
					me.setNmb(nmb);
					me.setCommentaire(commentaire);
					me.setCompte(compte);
					me.setReglement(reglement);
					me.setAmount(amount);
					me.setNumChek(numChek);
					me.setTypeImpriment(typeImpriment);
					me.setRemises(remises);
					
					if(gps.getSatellite() == null || gps.getDateString() == null){
						gps.setSatellite("GPS");
						gps.setDateString("0000-00-00 00:00");
					}
					me.setGps(gps);
					me.setImei(imei);
					if(num == null){
						num= "+000000000";
					}
					me.setNum(num);
					me.setBattery(battery);
					mx = new FileData();
					
					mx.setErreur(me.getInvoice());
					
					mx.setAddresse(soc.getAddresse());
					mx.setClient(cl.getName());
					mx.setDejaRegler(Double.parseDouble(me.getAmount()));
					mx.setDescription(soc.getDescription());
					mx.setFax(soc.getFax());
					mx.setIF(soc.getIF());
					mx.setMsg("Vous pouvez consultez nos offres promotionnelles sur le site :");
					mx.setNameSte(soc.getNameSte());
					mx.setNumFacture(me.getInvoice());
					mx.setPatente(soc.getPatente());
					mx.setSiteWeb(soc.getSiteWeb());
					mx.setTel(soc.getTel());
					
					me.setData(mx);
					me.setTotal_ticket(tt);
					
					
				}

	        
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save invoice",e.getMessage()  +" << ");
		}
		return me;
	}

	@Override
	public long shynchronizeIntervention(BordreauIntervention bi) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			file = new File(path, "/interventionsdata.txt");
			Log.e("filesavz",file.getPath());
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(bi != null){
					bi.setId(Integer.parseInt(calculIdInvoice()));
					pout.println("["+gson.toJson(bi,BordreauIntervention.class)+"]");
					ix =1;
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save intervention ",e.getMessage()  +" << ");
		}
		return ix;
	}
	
	
	@Override
	public long historiqueIntervention(BordreauIntervention bi) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			file = new File(path, "/interventionshistodata.txt");
			Log.e("filesavz",file.getPath());
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
				//Log.e("file not exist ","wloo wloo");
			}

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(bi != null){
					bi.setId(Integer.parseInt(calculIdInvoice()));
					pout.println("["+gson.toJson(bi,BordreauIntervention.class)+"]");
					ix =1;
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save intervention ",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<BordreauIntervention> LoadInterventions(String fl) {
		// TODO Auto-generated method stub
		List<BordreauIntervention> lpd = new ArrayList<>();
		try {

			File file = new File(path, "/interventionsdata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						JSONObject json = jArray.getJSONObject(i);
						BordreauIntervention pd = new BordreauIntervention();
						pd = gson.fromJson(json.toString(), BordreauIntervention.class);
						
						lpd.add(pd);
					}

				}
				
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load Intervention err ",e.getMessage()  +" << ");
		}
        
		return lpd;
	}

	@Override
	public void CleanIntervention() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/interventionsdata.txt"); //servicedata
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long sendOutIntervention(Compte cp) {
		// TODO Auto-generated method stub

		TechnicienManager technicien = TechnicienManagerFactory.getClientManager();

		List<BordreauIntervention> ls = new ArrayList<>();
		ls = LoadInterventions("");

		List<BordreauIntervention> tmp = new ArrayList<>();
		String s ="";

		for (int i = 0; i < ls.size(); i++) {
			try {
				s = technicien.insertBordereauoff(ls.get(i), cp);
				
				if(!s.equals("no")){
					JSONObject json = new JSONObject(s);
					if(!json.getString("feedback").equals("-1")){
						technicien.inesrtImage(ls.get(i).getImgs(), json.getString("lien"));
					}else{
						tmp.add(ls.get(i));
					}
				}else{
					tmp.add(ls.get(i));
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				tmp.add(ls.get(i));
			}
		}
		
		
		CleanIntervention();
		for (int i = 0; i < tmp.size(); i++) {
			shynchronizeIntervention(tmp.get(i));
		}


		return 0;
	}
	
	
	public static String getEncodedString(String str) {
	    String ret = null;
	    try {
	        ret = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return ret;
	}

	@Override
	public void CleanHistoIntervention() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/interventionshistodata.txt"); //servicedata
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public List<BordreauIntervention> LoadHistoInterventions(String fl) {
		// TODO Auto-generated method stub
		List<BordreauIntervention> lpd = new ArrayList<>();
		try {

			File file = new File(path, "/interventionshistodata.txt");

			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				String st ="";
				
				while ((line = r.readLine()) != null) {

					JSONArray jArray = new JSONArray(line);
					for(int i=0;i<jArray.length();i++){

						JSONObject json = jArray.getJSONObject(i);
						BordreauIntervention pd = new BordreauIntervention();
						pd = gson.fromJson(json.toString(), BordreauIntervention.class);
						
						lpd.add(pd);
					}

				}
				
				r.close();
				secondInputStream.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("load Intervention err ",e.getMessage()  +" << ");
		}
        
		return lpd;
	}

	@Override
	public boolean TotalMemory() {
		// TODO Auto-generated method stub
		boolean x =false;
		try {
			Log.e("size total ",getTotalExternalMemorySize());
			if(getTotalExternalMemorySize().contains("MB")){
				x = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return x;
	}

	@Override
	public boolean FreeMemory() {
		// TODO Auto-generated method stub
		boolean x =false;
		Log.e("size available ",getAvailableExternalMemorySize());
		if(getAvailableExternalMemorySize().contains("MB")){
			x = true;
		}
		return x;
	}
	
	public static String getAvailableExternalMemorySize() {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalExternalMemorySize() {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
    }
    
    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, '.');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

	@Override
	public long checkForUpdate() {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			
			file = new File(path, "/checkupdate.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}

			//Log.e("compte synchro ",""+gson.toJson(clt));
			if(file.exists()){
				Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
				int y       = cl.get(Calendar.YEAR);
				int m      = cl.get(Calendar.MONTH)+1; // Jan = 0, dec = 11
				int d = cl.get(Calendar.DAY_OF_MONTH); 
				
				long in = Long.parseLong(y+""+m+""+d);
				
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;

				int me =-1;
				
				while ((line = r.readLine()) != null) {
					Log.e("load update data ",line);
					me =0;
					long out = Long.parseLong(line);
					
					
					
					if(in > out){
						ix = 1;
						
						FileWriter fw = new FileWriter(file);
						PrintWriter pout = new PrintWriter(fw);
						pout.println(in);
						pout.close();
						
					}

				}
				r.close();
				secondInputStream.close();
				
				if(me == -1 ){
					Log.e("load update data ","empty ");

					ix = 1;
					FileWriter fw = new FileWriter(file);
					PrintWriter pout = new PrintWriter(fw);
					pout.println(in);
					pout.close();
					
				}
				
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save update",e.getMessage()  +" << ");
		}
		
		Log.e("save update",ix+" << ");
		return ix;
	}

	@Override
	public long shynchronizeCommandeList(List<Commandeview> cmd) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			
			Log.e("in cmd view","opa");
			CleanCommandeList();
			file = new File(path, "/cmdlistedata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}

			//Log.e("compte synchro ",""+gson.toJson(clt));
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				for (int i = 0; i < cmd.size(); i++) {
					pout.println("["+gson.toJson(cmd.get(i),Commandeview.class)+"]");
					ix =1;
				}
				pout.close();
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save commande liste",e.getMessage()  +" << ");
		}
		return ix;
	}

	@Override
	public List<Commandeview> LoadCommandeList(String fl) {
		// TODO Auto-generated method stub
		List<Commandeview> list = new ArrayList<Commandeview>();

		try{
			int n;

			File file = new File(path, "/cmdlistedata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				while ((line = r.readLine()) != null) {
					
					JSONArray jr = new JSONArray(line);
					for (int i = 0; i < jr.length(); i++) {
						JSONObject json = jr.getJSONObject(i);
						Commandeview c = new Commandeview();

						c = gson.fromJson(json.toString(), Commandeview.class);

						if(c.getRef() != null){
							list.add(c);
						}
					}


					}
				}

		}catch(Exception e){
			Log.e("load commande lise",e.getMessage()  +" << ");
		}
		return list;
	}

	@Override
	public void CleanCommandeList() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/cmdlistedata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long check_insert_invoice(String in) {
		// TODO Auto-generated method stub
		List<Myinvoice> ms = new ArrayList<>();
		ms = this.LoadInvoice("");
		long is =-1;
		for (int i = 0; i < ms.size(); i++) {
			if(ms.get(i).getIdnew().equals(in)){
				is =0;
			}
		}
		return is;
	}

	@Override
	public long check_insert_reglement(String in) {
		// TODO Auto-generated method stub
		List<Reglement> rg = new ArrayList<>();
		rg = this.LoadReglement("");
		
		long is =-1;
		
		for (int i = 0; i < rg.size(); i++) {
			Log.e("insert >> "+in,"check >> "+rg.get(i).getIdnew());
			if(rg.get(i).getIdnew().equals(in)){
				is = 0;
			}
		}
		return is;
	}
	
	@Override
	public long cleanForUpdate() {
		// TODO Auto-generated method stub
		try {

			file = new File(path, "/checkupdate.txt");
			FileOutputStream outputStream;

			//Log.e("compte synchro ",""+gson.toJson(clt));
			if(file.exists()){
				FileWriter fw = new FileWriter(file);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save update",e.getMessage()  +" << ");
		}
		
		return 0;
	}

	@Override
	public long shynchronizeCategoriesList(List<Categorie> cat) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanCategorieList();
			file = new File(path, "/categoriedata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				if(cat.size() > 0){
					for (int i = 0; i < cat.size(); i++) {
						pout.println("["+gson.toJson(cat.get(i),Categorie.class)+"]");
						ix =1;
					}
				}
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save categories",e.getMessage()  +" << ");
			ix =-1;
		}
		return ix;
	}

	@Override
	public List<Categorie> LoadCategorieList(String fl) {
		// TODO Auto-generated method stub
		List<Categorie> list = new ArrayList<Categorie>();

		try{
			int n;

			File file = new File(path, "/categoriedata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				while ((line = r.readLine()) != null) {
					
					JSONArray jr = new JSONArray(line);
					for (int i = 0; i < jr.length(); i++) {
						JSONObject json = jr.getJSONObject(i);
						Categorie c = new Categorie();

						c = gson.fromJson(json.toString(), Categorie.class);

						list.add(c);
					}


					}
				}

		}catch(Exception e){
			Log.e("load commande lise",e.getMessage()  +" << ");
		}
		return list;
	}

	@Override
	public void CleanCategorieList() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/categoriedata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long shynchornizeCmd(Commande cm) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanCmdList();
			file = new File(path, "/cmddata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}

			Log.e(">> cmd ",cm.toString());
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				pout.println("["+gson.toJson(cm,Commande.class)+"]");
				ix =1;
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save cmd ",e.getMessage()  +" << ");
			ix =-1;
		}
		return ix;
	}

	@Override
	public List<Commande> LoadCmdList(String fl) {
		// TODO Auto-generated method stub
		List<Commande> list = new ArrayList<Commande>();

		try{
			int n;

			File file = new File(path, "/cmddata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				while ((line = r.readLine()) != null) {
					
					JSONArray jr = new JSONArray(line);
					for (int i = 0; i < jr.length(); i++) {
						JSONObject json = jr.getJSONObject(i);
						Commande c = new Commande();

						c = gson.fromJson(json.toString(), Commande.class);

						list.add(c);
					}


					}
				}

		}catch(Exception e){
			Log.e("load commande save",e.getMessage()  +" << ");
		}
		return list;
	}

	@Override
	public void CleanCmdList() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/cmddata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public Commande SendCommande(Commande cmd,Compte cp) {
		// TODO Auto-generated method stub
		Commande data = null;
		
		try {
			
			Log.e("cmd cpt" ,cmd.getCompte().toString()+"");
			String res = cmdmanager.insertCommande(cmd.getProds(), cmd.getClt(), cp, cmd.getRemises());
			
			if(!res.equals("ko")){
				 //JSONObject json = new JSONObject(res);

				 //String code =  json.getString("code");
				// String num =  json.getString("cmdnum");
				// int nbln = json.getInt("nbr_error_lines");
				 
				 if((res.equals("10") || Integer.parseInt(res) == 10) ){ //&& nbln  == 0
					 data = null;
				 }else { //if(nbln != 0)
					/*
					 JSONArray js = json.getJSONArray("error_lines");
					
					for (int i = 0; i < js.length(); i++) {
						JSONObject jb = js.getJSONObject(i);
					}
					*/
					data = cmd;
				 }
					
					
			}else{
				data = cmd;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("error send cmd ",e.getMessage()+"");
			data = cmd;
		}
		return data;
	}

	@Override
	public HashMap<String, HashMap<Prospection, List<Commande>>> chargerCmd_prospect(
			Compte cp) {
		// TODO Auto-generated method stub
		List<Commande> me = this.LoadCmdList("");
		List<Prospection> tmp = LoadProspection("");
		
		
		HashMap<Prospection, List<Commande>> myo = new HashMap<>();
		HashMap<Prospection, List<Commande>> myo2 = new HashMap<>();
		
		HashMap<String, HashMap<Prospection, List<Commande>>>  res = new HashMap<>();
		
		List<Commande> invtmp ;
		
		List<Commande> invps = new ArrayList<>();
		
		try {
			
			
			if(tmp.size() > 0){
				
				for (int i = 0; i < tmp.size(); i++) {
					invtmp = new ArrayList<>();
					
					if(me.size() > 0){
						
						for (int j = 0; j < me.size(); j++) {
							
							if(Integer.parseInt(me.get(j).getClt()) == tmp.get(i).getIdpros()){
								
								invtmp.add(me.get(j));
								
								invps.add(me.get(j));
							}
						}
						
					}
					
					myo.put(tmp.get(i), invtmp);
				}
				
			}
			myo2.put(new Prospection(), invps);
			
			res.put("ps", myo);
			res.put("all", myo2);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	@Override
	public HashMap<Commande, String> chargerCmd_client(
			HashMap<String, HashMap<Prospection, List<Commande>>> da, Compte cp) {
		// TODO Auto-generated method stub
		List<Commande> me = this.LoadCmdList("");

		List<Commande> mx = new ArrayList<>();

		HashMap<String, HashMap<Prospection, List<Commande>>>  res = new HashMap<>();

		HashMap<Prospection, List<Commande>> myo = new HashMap<>();


		HashMap<Commande,String> data = new HashMap<>();

		boolean isit = false;

		try {
			res = da ; //chargerInvoice_prospect(cp);

			if(res.size() > 0){
				myo = res.get("all");
				for(Prospection ps:myo.keySet()){
					mx = myo.get(ps);
				}


				if(me.size() > mx.size()){

					for (int i = 0; i < me.size(); i++) {
						isit = false;

						for (int j = 0; j < mx.size(); j++) {
							if(me.get(i).getIdandro() == mx.get(j).getIdandro()){
								isit = true;
								break;
							}
						}

						if(!isit){
							data.put(me.get(i), me.get(i).getClt());
						}

					}

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return data;
	}

	@Override
	public long shynchornizeCmdToFact(Commandeview cm) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			CleanCmdList();
			file = new File(path, "/cmdtofactdata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}

			Log.e(">> cmd ",cm.toString());
			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				pout.println("["+gson.toJson(cm,Commandeview.class)+"]");
				ix =1;
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save cmd to fc ",e.getMessage()  +" << ");
			ix =-1;
		}
		return ix;
	}

	@Override
	public List<Commandeview> LoadCmdToFact(String fl) {
		// TODO Auto-generated method stub
		List<Commandeview> list = new ArrayList<Commandeview>();

		try{
			int n;

			File file = new File(path, "/cmdtofactdata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				while ((line = r.readLine()) != null) {
					
					JSONArray jr = new JSONArray(line);
					for (int i = 0; i < jr.length(); i++) {
						JSONObject json = jr.getJSONObject(i);
						Commandeview c = new Commandeview();

						c = gson.fromJson(json.toString(), Commandeview.class);

						list.add(c);
					}


					}
				}

		}catch(Exception e){
			Log.e("load commande to fc save",e.getMessage()  +" << ");
		}
		return list;
	}

	@Override
	public void CleanCmdToFactList() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/cmdtofactdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public List<Commandeview> synchronisationCmdToFacOut(Compte cp) {
		// TODO Auto-generated method stub
		List<Commandeview> cmd = new ArrayList<>();
		try {
			for (int i = 0; i < this.LoadCmdToFact("").size(); i++) {
				String n = cmdmanager.CmdToFacture(LoadCmdToFact("").get(i), cp);
				
				if(!n.equals("0")){
					cmd.add(LoadCmdToFact("").get(i));
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cmd;
	}

	@Override
	public long shnchronizeMouvement(MouvementGrabage mv, Compte cp) {
		// TODO Auto-generated method stub
		long ix = -1;
		try {
			//CleanCmdList();
			file = new File(path, "/mouvementdata.txt");
			FileOutputStream outputStream;

			if(!file.exists()){
				file.createNewFile();
				file.mkdir();
			}

			if(file.exists()){
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pout = new PrintWriter(fw);
				pout.println("["+gson.toJson(mv,MouvementGrabage.class)+"]");
				ix =1;
				pout.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("save cmd ",e.getMessage()  +" << ");
			ix =-1;
		}
		return ix;
	}

	@Override
	public List<MouvementGrabage> LoadMouvement(String fl) {
		// TODO Auto-generated method stub
		List<MouvementGrabage> list = new ArrayList<MouvementGrabage>();

		try{
			int n;

			File file = new File(path, "/mouvementdata.txt");
			if(file.exists()){
				//Log.e("data loaded exist  ",file.getAbsolutePath());
				File secondInputFile = new File(file.getAbsolutePath());
				InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
				BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
				StringBuilder total = new StringBuilder();
				String line;
				
				while ((line = r.readLine()) != null) {
					
					JSONArray jr = new JSONArray(line);
					for (int i = 0; i < jr.length(); i++) {
						JSONObject json = jr.getJSONObject(i);
						MouvementGrabage c = new MouvementGrabage();

						c = gson.fromJson(json.toString(), MouvementGrabage.class);

						list.add(c);
					}


					}
				}

		}catch(Exception e){
			Log.e("load Mouvement save",e.getMessage()  +" << ");
		}
		return list;
	}

	@Override
	public void CleanMouvement() {
		// TODO Auto-generated method stub
		try {
			File file = new File(path, "/mouvementdata.txt");
			if(file.exists()){
				FileWriter fw = new FileWriter(file,false);
				PrintWriter pout = new PrintWriter(fw);
				pout.print("");
				pout.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public long sendMouvements(Compte cp) {
		// TODO Auto-generated method stub
		try {
			
			Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
			Timestamp ts = new Timestamp(cl.getTimeInMillis());
			
			List<MouvementGrabage> mvs = LoadMouvement("");
			
			List<MouvementGrabage> tmp = new ArrayList<>();
			
			for (int i = 0; i < mvs.size(); i++) {
				String res = stockManager.makeechange(mvs.get(i).getLsmvs(), cp, ts.getTime()+"", mvs.get(i).getClt());
				
				if(!res.equals("100")){
					tmp = mvs;
				}
			}
			
			
			
			CleanMouvement();
			for (int i = 0; i < tmp.size(); i++) {
				shnchronizeMouvement(tmp.get(i), cp);
			}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	
}
