package com.dolibarrmaroc.com.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.FactureGps;
import com.dolibarrmaroc.com.models.FileData;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.models.Remises;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;



public class FactureDaoMysql implements FactureDao {

	private JSONParser jsonParser;
	private static final String LOGIN_URL = URL.URL+"Facturation.class.php";
	private static final String URL_FACT = URL.URL+"getFactureDataGps.php";
	
	private static final String INVO_URL = URL.URL+"synchroclientinvo.php";
	private static final String INVO_CLT_URL = URL.URL+"Facturationinvo.class.php";

	public FactureDaoMysql() {
		jsonParser = new JSONParser();
	}

	@Override
	public FileData insert(List<Produit> prd, String idclt, int nmb,
			String commentaire, Compte compte, String reglement , String amount , String numChek ,int typeImpriment,Map<String, Remises> allremises,int type_invoice) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",compte.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",compte.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("idclt",idclt));
		//Log.e("PRDS ",idclt);
		nameValuePairs.add(new BasicNameValuePair("reglement",reglement));
		//Log.e("PRDS ",reglement);
		nameValuePairs.add(new BasicNameValuePair("commentaire",commentaire));
		//Log.e("PRDS ",commentaire);
		nameValuePairs.add(new BasicNameValuePair("type_invoice",type_invoice+""));
		
		
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
		
		nameValuePairs.add(new BasicNameValuePair("nmbproduct",l3adad+""));
		Log.e("Nombre Produit ",l3adad+"");
		
		nameValuePairs.add(new BasicNameValuePair("amount",amount));
		nameValuePairs.add(new BasicNameValuePair("numchek",numChek ));
		nameValuePairs.add(new BasicNameValuePair("impriment",typeImpriment+""));
		nameValuePairs.add(new BasicNameValuePair("refinvoice",calculIdInvoice_new()));
		
		Log.e("Facture",nameValuePairs.toString());
		
		String jsonString =  jsonParser.makeHttpRequest(LOGIN_URL , "POST", nameValuePairs);
		// Parse les donn�es JSON
		Log.e("JsonString factures", jsonString);
		
		
		try {
			
			String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			
			FileData file = null;
			JSONObject json = new JSONObject(stfomat);
			String in ="";
			
			in = json.getString("erreur");
			if(!in.equals("-100")){
				 file = new FileData();
				file.setErreur(in);
				file.setPath(json.getString("lien"));
				file.setPdf(json.getString("pdf"));
				file.setFileName(json.getString("file"));
				
				/**
				 * {"erreur":"2014-02366",
				 * "pdf":"http:\/\/www.takamaroc.com\/htdocs\/doliDroid\/uploads\/2014-02366.pdf",
				 * "lien":"\/homez.343\/takamaro\/www\/documents\/facture\/2014-02366",
				 * "file":"2014-02366.pdf",
				 * "addresse":"9, Place AL yassir, R\u00e9s Mansouria B, Casablanca",
				 * "tel":"0522400587",
				 * "fax":"0522400588",
				 * "siteWeb":"http:\/\/www.marocgeo.ma",
				 * "nameSte":"GEO.COM",
				 * "desc":"trytry tr ytytr ty trytr rt ytrytr try tytr tryt",
				 * "patente":"32690260",
				 * "IF":"1107096",
				 * "client":"SAADA CAR"}
				 */
				file.setAddresse(json.getString("addresse"));
				file.setClient(json.getString("client"));
				file.setDejaRegler(Double.parseDouble(amount));
				file.setDescription(json.getString("desc"));
				file.setFax(json.getString("fax"));
				file.setIF(json.getString("IF"));
				file.setMsg("Vous pouvez consultez nos offres promotionnelles sur le site :");
				file.setNameSte(json.getString("nameSte"));
				file.setNumFacture(json.getString("erreur"));
				file.setPatente(json.getString("patente"));
				file.setSiteWeb(json.getString("siteWeb"));
				file.setTel(json.getString("tel"));
				
				
			}
			

			return file;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<FactureGps> listFacture(Compte compte) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",compte.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",compte.getPassword()));
		
		String jsonString =  jsonParser.makeHttpRequest(URL_FACT, "POST", nameValuePairs);
		
		List<FactureGps> list = new ArrayList<>();
		
		Log.e("Facture GPS", jsonString );

		
		try{
			JSONArray jArray = new JSONArray(jsonString);

			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);
				
				FactureGps fact = new FactureGps();
				
				fact.setId(json.getInt("id"));
				fact.setLat(json.getString("lat"));
				fact.setLng(json.getString("lng"));
				fact.setNumero(json.getString("facture"));
				
				
				list.add(fact);
				
			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return list;
	}

	@Override
	public FileData insertoff(Prospection ps,List<Produit> prd, String idclt, int nmb,
			String commentaire, Compte compte, String reglement, String amount,
			String numChek, int typeImpriment, Map<String, Remises> allremises,
			HashMap<Integer, Reglement> reg) {
		// TODO Auto-generated method stub
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		
		/**************** begin client   **************************/
		nameValuePairs.add(new BasicNameValuePair("create","create"));
		
		if (ps.getParticulier() == 1) {
			nameValuePairs.add(new BasicNameValuePair("nom",ps.getFirstname()));
		}else{
			nameValuePairs.add(new BasicNameValuePair("nom",ps.getName()));
		}
		
        nameValuePairs.add(new BasicNameValuePair("firstname",ps.getLastname()));
        nameValuePairs.add(new BasicNameValuePair("particulier",ps.getParticulier()+""));
        nameValuePairs.add(new BasicNameValuePair("client",ps.getClient()+""));
        nameValuePairs.add(new BasicNameValuePair("address",ps.getAddress()));
        nameValuePairs.add(new BasicNameValuePair("zip",ps.getZip()));
        nameValuePairs.add(new BasicNameValuePair("town",ps.getTown()));
        nameValuePairs.add(new BasicNameValuePair("phone",ps.getPhone()));
        nameValuePairs.add(new BasicNameValuePair("fax",ps.getFax()));
        nameValuePairs.add(new BasicNameValuePair("email",ps.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("capital",ps.getCapital()));
        nameValuePairs.add(new BasicNameValuePair("idprof1",ps.getIdprof1()));
        nameValuePairs.add(new BasicNameValuePair("idprof2",ps.getIdprof2()));
        nameValuePairs.add(new BasicNameValuePair("idprof3",ps.getIdprof3()));
        nameValuePairs.add(new BasicNameValuePair("idprof4",ps.getIdprof4()));
        nameValuePairs.add(new BasicNameValuePair("typent_id",ps.getTypent_id()));
        nameValuePairs.add(new BasicNameValuePair("effectif_id",ps.getEffectif_id()));
        nameValuePairs.add(new BasicNameValuePair("assujtva_value",ps.getTva_assuj()+""));
        nameValuePairs.add(new BasicNameValuePair("status",ps.getStatus()+""));
		nameValuePairs.add(new BasicNameValuePair("commercial_id",compte.getIduser()));
        nameValuePairs.add(new BasicNameValuePair("country_id",ps.getCountry_id()+""));
        nameValuePairs.add(new BasicNameValuePair("forme_juridique_code",ps.getForme_juridique_code()));
		
        nameValuePairs.add(new BasicNameValuePair("latitude",ps.getLatitude()+""));
        nameValuePairs.add(new BasicNameValuePair("longitude",ps.getLangitude()+""));
        
        /**************  ned client *******************************/
        
		nameValuePairs.add(new BasicNameValuePair("username",compte.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",compte.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("idclt",idclt));
		//Log.e("PRDS ",idclt);
		nameValuePairs.add(new BasicNameValuePair("reglement",reglement));
		//Log.e("PRDS ",reglement);
		nameValuePairs.add(new BasicNameValuePair("commentaire",commentaire));
		//Log.e("PRDS ",commentaire);
		nameValuePairs.add(new BasicNameValuePair("sysoff","1"));
		
		//nbr arg reglements
		nameValuePairs.add(new BasicNameValuePair("nbrreg",reg.size()+""));
		
		
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
		
		nameValuePairs.add(new BasicNameValuePair("nmbproduct",l3adad+""));
		Log.e("Nombre Produit ",l3adad+"");
		
		nameValuePairs.add(new BasicNameValuePair("amount",amount));
		nameValuePairs.add(new BasicNameValuePair("numchek",numChek ));
		nameValuePairs.add(new BasicNameValuePair("impriment",typeImpriment+""));
		nameValuePairs.add(new BasicNameValuePair("refinvoice",calculIdInvoice_new()));
		
		Log.i("Facture",nameValuePairs.toString());
		
		int j=0;
		if(reg.size() > 0){
			SortedSet<Integer> keys = new TreeSet<Integer>(reg.keySet());
			for (Integer key : keys) { 
			   nameValuePairs.add(new BasicNameValuePair("user"+j,reg.get(key).getIdUser()));
				nameValuePairs.add(new BasicNameValuePair("cheque"+j,reg.get(key).getNum_paiement()));
				nameValuePairs.add(new BasicNameValuePair("type"+j,reg.get(key).getPaiementcode()));
				nameValuePairs.add(new BasicNameValuePair("amount"+j,reg.get(key).getAmount()+""));
				nameValuePairs.add(new BasicNameValuePair("facid"+j,reg.get(key).getId()+""));//
				nameValuePairs.add(new BasicNameValuePair("reste"+j,reg.get(key).getFk_facture()));
				j++;
			}
			
			
		}
		
		
		
		
		String jsonString =  jsonParser.makeHttpRequest(LOGIN_URL , "POST", nameValuePairs);
		// Parse les donn�es JSON
		Log.i("JsonString factures", jsonString);
		
		try {
			
			
			FileData file = new FileData();
			JSONObject json = new JSONObject(jsonString);
			file.setErreur(json.getString("erreur"));
			file.setPath(json.getString("lien"));
			file.setPdf(json.getString("pdf"));
			file.setFileName(json.getString("file"));
			
			/**
			 * {"erreur":"2014-02366",
			 * "pdf":"http:\/\/www.takamaroc.com\/htdocs\/doliDroid\/uploads\/2014-02366.pdf",
			 * "lien":"\/homez.343\/takamaro\/www\/documents\/facture\/2014-02366",
			 * "file":"2014-02366.pdf",
			 * "addresse":"9, Place AL yassir, R\u00e9s Mansouria B, Casablanca",
			 * "tel":"0522400587",
			 * "fax":"0522400588",
			 * "siteWeb":"http:\/\/www.marocgeo.ma",
			 * "nameSte":"GEO.COM",
			 * "desc":"trytry tr ytytr ty trytr rt ytrytr try tytr tryt",
			 * "patente":"32690260",
			 * "IF":"1107096",
			 * "client":"SAADA CAR"}
			 */
			file.setAddresse(json.getString("addresse"));
			file.setClient(json.getString("client"));
			file.setDejaRegler(Double.parseDouble(amount));
			file.setDescription(json.getString("desc"));
			file.setFax(json.getString("fax"));
			file.setIF(json.getString("IF"));
			file.setMsg("Vous pouvez consultez nos offres promotionnelles sur le site :");
			file.setNameSte(json.getString("nameSte"));
			file.setNumFacture(json.getString("erreur"));
			file.setPatente(json.getString("patente"));
			file.setSiteWeb(json.getString("siteWeb"));
			file.setTel(json.getString("tel"));

			return file;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	@Override
	public String insertoffline(Prospection ps,List<Produit> prd, String idclt, int nmb,
			String commentaire, Compte compte, String reglement, String amount,
			String numChek, int typeImpriment, Map<String, Remises> allremises,
			HashMap<Integer, Reglement> reg) {
		// TODO Auto-generated method stub
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		Log.e("nom3",ps.toString()+"");
		/**************** begin client   **************************/
		nameValuePairs.add(new BasicNameValuePair("create","create"));
		
		if (ps.getParticulier() == 1) {
			nameValuePairs.add(new BasicNameValuePair("nom",ps.getFirstname()));
			Log.e("nom1",ps.getFirstname());
		}else{
			nameValuePairs.add(new BasicNameValuePair("nom",ps.getName()));
			Log.e("nom2",ps.getName());
		}
		
        nameValuePairs.add(new BasicNameValuePair("firstname",ps.getLastname()));
        nameValuePairs.add(new BasicNameValuePair("particulier",ps.getParticulier()+""));
        nameValuePairs.add(new BasicNameValuePair("client",ps.getClient()+""));
        nameValuePairs.add(new BasicNameValuePair("address",ps.getAddress()));
        nameValuePairs.add(new BasicNameValuePair("zip",ps.getZip()));
        nameValuePairs.add(new BasicNameValuePair("town",ps.getTown()));
        nameValuePairs.add(new BasicNameValuePair("phone",ps.getPhone()));
        nameValuePairs.add(new BasicNameValuePair("fax",ps.getFax()));
        nameValuePairs.add(new BasicNameValuePair("email",ps.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("capital",ps.getCapital()));
        nameValuePairs.add(new BasicNameValuePair("idprof1",ps.getIdprof1()));
        nameValuePairs.add(new BasicNameValuePair("idprof2",ps.getIdprof2()));
        nameValuePairs.add(new BasicNameValuePair("idprof3",ps.getIdprof3()));
        nameValuePairs.add(new BasicNameValuePair("idprof4",ps.getIdprof4()));
        nameValuePairs.add(new BasicNameValuePair("typent_id",ps.getTypent_id()));
        nameValuePairs.add(new BasicNameValuePair("effectif_id",ps.getEffectif_id()));
        nameValuePairs.add(new BasicNameValuePair("assujtva_value",ps.getTva_assuj()+""));
        nameValuePairs.add(new BasicNameValuePair("status",ps.getStatus()+""));
		nameValuePairs.add(new BasicNameValuePair("commercial_id",compte.getIduser()));
        nameValuePairs.add(new BasicNameValuePair("country_id",ps.getCountry_id()+""));
        nameValuePairs.add(new BasicNameValuePair("forme_juridique_code",ps.getForme_juridique_code()));
		
        nameValuePairs.add(new BasicNameValuePair("latitude",ps.getLatitude()+""));
        nameValuePairs.add(new BasicNameValuePair("longitude",ps.getLangitude()+""));
        
        /**************  ned client *******************************/
        
		nameValuePairs.add(new BasicNameValuePair("username",compte.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",compte.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("idclt",idclt));
		//Log.e("PRDS ",idclt);
		nameValuePairs.add(new BasicNameValuePair("reglement",reglement));
		//Log.e("PRDS ",reglement);
		nameValuePairs.add(new BasicNameValuePair("commentaire",commentaire));
		//Log.e("PRDS ",commentaire);
		nameValuePairs.add(new BasicNameValuePair("sysoff","1"));
		
		//nbr arg reglements
		nameValuePairs.add(new BasicNameValuePair("nbrreg",reg.size()+""));
		
		
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
		
		nameValuePairs.add(new BasicNameValuePair("nmbproduct",l3adad+""));
		Log.e("Nombre Produit ",l3adad+"");
		
		nameValuePairs.add(new BasicNameValuePair("amount",amount));
		nameValuePairs.add(new BasicNameValuePair("numchek",numChek ));
		nameValuePairs.add(new BasicNameValuePair("impriment",typeImpriment+""));
		nameValuePairs.add(new BasicNameValuePair("refinvoice",calculIdInvoice_new()));
		
		Log.i("Facture",nameValuePairs.toString());
		
		int j=0;
		if(reg.size() > 0){
			SortedSet<Integer> keys = new TreeSet<Integer>(reg.keySet());
			for (Integer key : keys) { 
			   nameValuePairs.add(new BasicNameValuePair("user"+j,reg.get(key).getIdUser()));
				nameValuePairs.add(new BasicNameValuePair("cheque"+j,reg.get(key).getNum_paiement()));
				nameValuePairs.add(new BasicNameValuePair("type"+j,reg.get(key).getPaiementcode()));
				nameValuePairs.add(new BasicNameValuePair("amount"+j,reg.get(key).getAmount()+""));
				nameValuePairs.add(new BasicNameValuePair("facid"+j,reg.get(key).getId()+""));//
				nameValuePairs.add(new BasicNameValuePair("reste"+j,reg.get(key).getFk_facture()));
				nameValuePairs.add(new BasicNameValuePair("regid"+j,reg.get(key).getIdreg()+""));
				j++;
			}
			
			
		}
		
		
		
		
		return  jsonParser.makeHttpRequest(INVO_URL , "POST", nameValuePairs);
		

	}

	
	@Override
	public String insertcicin(List<Produit> prd, String idclt, int nmb,
			String commentaire, Compte compte, String reglement , String amount , String numChek ,int typeImpriment,Map<String, Remises> allremises,HashMap<Integer, Reglement> reg,String rs,int type_invoice) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",compte.getLogin()));
		//Log.e("PRDS ",compte.getLogin());
		nameValuePairs.add(new BasicNameValuePair("password",compte.getPassword()));
		//Log.e("PRDS ",compte.getPassword());
		nameValuePairs.add(new BasicNameValuePair("idclt",idclt));
		//Log.e("PRDS ",idclt);
		
		nameValuePairs.add(new BasicNameValuePair("reglement",reglement));
		//Log.e("PRDS ",reglement);
		nameValuePairs.add(new BasicNameValuePair("commentaire",commentaire));
		//Log.e("PRDS ",commentaire);
		nameValuePairs.add(new BasicNameValuePair("type_invoice",type_invoice+""));
		//nbr arg reglements
				nameValuePairs.add(new BasicNameValuePair("nbrreg",reg.size()+""));
				
		Log.e("reglo ",reglement+" << ");
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
		
		nameValuePairs.add(new BasicNameValuePair("nmbproduct",l3adad+""));
		Log.e("Nombre Produit ",l3adad+"");
		
		nameValuePairs.add(new BasicNameValuePair("amount",amount));
		nameValuePairs.add(new BasicNameValuePair("numchek",numChek ));
		nameValuePairs.add(new BasicNameValuePair("impriment",typeImpriment+""));
		nameValuePairs.add(new BasicNameValuePair("refinvoice",rs));
		
		Log.e("Facture",nameValuePairs.toString());
		
		
		
		int j=0;
		if(reg.size() > 0){
			SortedSet<Integer> keys = new TreeSet<Integer>(reg.keySet());
			for (Integer key : keys) { 
			   nameValuePairs.add(new BasicNameValuePair("user"+j,reg.get(key).getIdUser()));
				nameValuePairs.add(new BasicNameValuePair("cheque"+j,reg.get(key).getNum_paiement()));
				nameValuePairs.add(new BasicNameValuePair("type"+j,reg.get(key).getPaiementcode()));
				nameValuePairs.add(new BasicNameValuePair("amount"+j,reg.get(key).getAmount()+""));
				nameValuePairs.add(new BasicNameValuePair("facid"+j,reg.get(key).getId()+""));//
				nameValuePairs.add(new BasicNameValuePair("reste"+j,reg.get(key).getFk_facture()));
				nameValuePairs.add(new BasicNameValuePair("regid"+j,reg.get(key).getIdreg()+""));
				j++;
			}
			
			
		}
		
		Log.e("voila send out", nameValuePairs.toString());
		String jsonString =  jsonParser.makeHttpRequest(INVO_CLT_URL , "POST", nameValuePairs);
		
		Log.e("from doli ",jsonString);
		return jsonString;
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
}
