package com.dolibarrmaroc.com.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class CommandeDaoMysql implements CommandeDao {

	private String urlData = URL.URL+"bl.php";
	private static final String save = URL.URL+"createcmd.php";
	
	private static final String cmdtofc = URL.URL+"cmdtofacture.php";
	private JSONParser parser ;

	public CommandeDaoMysql() {
		super();
		parser = new JSONParser();
	}

	@Override
	public List<Commandeview> charger_commandes(Compte c) {
		// TODO Auto-generated method stub
		
		Log.e("start", "json");
		List<Commandeview> cmd = new ArrayList<>();
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));//c.getLogin()
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));


		try {
			String json = parser.makeHttpRequest(urlData, "POST", nameValuePairs);
			String stfomat = json.substring(json.indexOf("["),json.lastIndexOf("]")+1);


			
			Log.e("json",stfomat);
			JSONArray jsr = new JSONArray(stfomat);

			for (int i = 0; i < jsr.length(); i++) {
				JSONObject obj = jsr.getJSONObject(i);

				Commandeview cm = new Commandeview();
				cm.setRowid(obj.getInt("rowid"));
				cm.setRef(obj.getString("ref"));
				
				Client cl = new Client(obj.getJSONObject("socid").getInt("rowid"), obj.getJSONObject("socid").getString("name"), obj.getJSONObject("socid").getString("zip"), obj.getJSONObject("socid").getString("town"), obj.getJSONObject("socid").getString("email"), "", "");
				cm.setClt(cl);
				
				cm.setDt(obj.getString("date_commande"));
				cm.setTtc(obj.getDouble("total_ttc"));
				cm.setHt(obj.getDouble("total_ht"));
				cm.setTva(obj.getDouble("total_tva"));
				
				JSONArray p = obj.getJSONArray("products");
				
				List<Produit> pd = new ArrayList<>();
				for (int j = 0; j < p.length(); j++) {
					JSONObject ob = p.getJSONObject(j);
					
					//						(String ref, String desig, int qteDispo, String prixUnitaire,	int qtedemander, double prixttc, String tva_tx, String fk_tva)
					Produit px = new Produit(ob.getString("ref"), ob.getString("libelle"), ob.getInt("qnt"), "", 0, ob.getDouble("price"), ob.getString("remise_percent"), "");
					
					pd.add(px);
				}
				
				cm.setLsprods(pd);
				
				cmd.add(cm);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("json insert commande",e.getMessage() +" << ");
		}

		return cmd;
	}
	
	
	@Override
	public String insertCommande(List<Produit> prd, String idclt,
			Compte compte, Map<String, Remises> allremises) {
		// TODO Auto-generated method stub
		
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",compte.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",compte.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("socid",idclt));
		
		nameValuePairs.add(new BasicNameValuePair("dtcmd",cl.get(Calendar.YEAR)+"-"+cl.get(Calendar.MONTH)+"-"+cl.get(Calendar.DAY_OF_MONTH)));
		
		Log.e("BRRRRRR",allremises.toString());
		Log.e("PRDS ALL",prd.toString());
		
		List<Produit> prds = new ArrayList<>();
		
		int l3adad = 0;
		
		for (int i = 0; i < prd.size(); i++) {
			Produit pr = prd.get(i);
			Remises r = allremises.get(pr.getRef());
			
			int gratuite = 0;
			
			if((r.getType() == 0) && (pr.getQtedemander() >= r.getQte())){
				nameValuePairs.add(new BasicNameValuePair("remise"+i,r.getRemise()+""));
				nameValuePairs.add(new BasicNameValuePair("fk_article"+i,pr.getId()+""));
				nameValuePairs.add(new BasicNameValuePair("fk_tva"+i,pr.getFk_tva()));
				nameValuePairs.add(new BasicNameValuePair("qte"+i,pr.getQtedemander()+""));
				nameValuePairs.add(new BasicNameValuePair("taux"+i,pr.getTva_tx()+""));
				Log.e("PRDS "+i,pr.toString());
				
			}else if(r.getType() == 1 && (pr.getQtedemander() >= r.getRemise())){
				
				nameValuePairs.add(new BasicNameValuePair("fk_article"+i,pr.getId()+""));
				nameValuePairs.add(new BasicNameValuePair("fk_tva"+i,pr.getFk_tva()));
				nameValuePairs.add(new BasicNameValuePair("qte"+i,pr.getQtedemander()+""));
				nameValuePairs.add(new BasicNameValuePair("taux"+i,pr.getTva_tx()+""));
				Log.e("PRDS "+i,pr.toString());
				
				gratuite = r.getQte();
				
				Log.e("Promos "+i,r.toString());
				Log.e("Remise "+i,r.getRemise()+"");
				Log.e("QTE REMISE "+i,r.getQte()+"");
				Log.e("Gratuite "+i,gratuite+"");
				
				Produit p = new Produit();
				p.setId(pr.getId());
				p.setDesig(pr.getDesig());
				p.setTva_tx(pr.getTva_tx());
				p.setQtedemander(gratuite);
				p.setRef(pr.getRef());
				p.setFk_tva(pr.getFk_tva());
				
				prds.add(p);
				
				
			}else{
				nameValuePairs.add(new BasicNameValuePair("remise"+i,"0"));
				nameValuePairs.add(new BasicNameValuePair("fk_article"+i,pr.getId()+""));
				nameValuePairs.add(new BasicNameValuePair("fk_tva"+i,pr.getFk_tva()));
				nameValuePairs.add(new BasicNameValuePair("qte"+i,pr.getQtedemander()+""));
				nameValuePairs.add(new BasicNameValuePair("taux"+i,pr.getTva_tx()+""));
				Log.e("PRDS "+i,pr.toString());
			}
			
			l3adad++;
		}
		
		int ra9em =0;
		for (int j = 0; j < prds.size(); j++) {
			Produit pr = prds.get(j);
			
			int k =j+l3adad;
			
			nameValuePairs.add(new BasicNameValuePair("remise"+k,100+""));
			nameValuePairs.add(new BasicNameValuePair("fk_article"+k,pr.getId()+""));
			nameValuePairs.add(new BasicNameValuePair("fk_tva"+k,pr.getFk_tva()));
			nameValuePairs.add(new BasicNameValuePair("qte"+k,pr.getQtedemander()+""));
			nameValuePairs.add(new BasicNameValuePair("taux"+k,pr.getTva_tx()+""));
			Log.e("PRDS "+j,pr.toString());
			ra9em++;
			
		}
		
		l3adad = l3adad + ra9em;
		
		nameValuePairs.add(new BasicNameValuePair("nbrprod",l3adad+""));
		
		String stfomat ="ko";
		Log.e("Facture  >>",nameValuePairs.toString());
		
		try {
			
			String jsonString =  parser.makeHttpRequest(save , "POST", nameValuePairs);
			// Parse les donn�es JSON
			Log.e("JsonString factures", jsonString);
			
			stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			
			JSONObject obj = new JSONObject(stfomat);
			
			stfomat = obj.getString("code");

		} catch (Exception e) {
			e.printStackTrace();
			stfomat ="ko";
		}
		
		return stfomat;
	}

	@Override
	public String CmdToFacture(Commandeview cv, Compte c) {
		// TODO Auto-generated method stub
		String res = "ko";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));//c.getLogin()
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("cmd",cv.getRowid()+""));


		try {
			String json = parser.makeHttpRequest(cmdtofc, "POST", nameValuePairs);
			String stfomat = json.substring(json.indexOf("{"),json.lastIndexOf("}")+1);


			
			Log.e("json",stfomat);
			JSONObject obj = new JSONObject(stfomat);
			
			res = obj.getString("msg");
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("json insert commande",e.getMessage() +" << ");
		}

		return res;
	}
	

}
