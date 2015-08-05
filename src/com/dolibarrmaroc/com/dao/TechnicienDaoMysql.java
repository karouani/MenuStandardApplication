package com.dolibarrmaroc.com.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import com.dolibarrmaroc.com.models.BordereauGps;
import com.dolibarrmaroc.com.models.BordreauIntervention;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ImageTechnicien;
import com.dolibarrmaroc.com.models.LabelService;
import com.dolibarrmaroc.com.models.Services;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;


public class TechnicienDaoMysql implements TechnicienDao{

	private JSONParser jsonParser;
	public static final String url = URL.URL+"services.php";
	public static final String urlInsert = URL.URL+"bordereau.php";
	private String urlclt = URL.URL+"listclient.php";
	private static final String URL_BRD = URL.URL+"getBrdDataGps.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ID = "id";
	

	public TechnicienDaoMysql(){
		jsonParser = new JSONParser();
	}

	@Override
	public List<Services> allServices(Compte c) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));

		String jsonString =  jsonParser.makeHttpRequest(
				url , "POST", nameValuePairs);

		List<Services> list = new ArrayList<Services>();

		Log.d("Json retournee >> ", jsonString);
		try{

			JSONArray jArray = new JSONArray(jsonString);
			// check your log for json response
			//Log.d("Login attempt", jArray.toString());


			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);
				Services s =new Services();
				Log.d("Produit trouver Successful!", json.toString());

				s.setId(json.getInt(TAG_ID));
				s.setNmb_cmp(json.getInt("nmb"));
				s.setService(json.getString("service"));
				List<LabelService> labels = new ArrayList<>();
				for (int j = 0; j < s.getNmb_cmp(); j++) {
					LabelService lb = new LabelService(json.getString("label"+j));
					labels.add(lb);
				}
				s.setLabels(labels);

				list.add(s);

			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return list;
	}



	@Override
	public String insertBordereau(BordreauIntervention bi, Compte c) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("idclt",bi.getId_clt()));
		nameValuePairs.add(new BasicNameValuePair("duree",bi.getDuree()));
		nameValuePairs.add(new BasicNameValuePair("iduser",c.getIduser()));
		nameValuePairs.add(new BasicNameValuePair("desc",bi.getDescription()));
		nameValuePairs.add(new BasicNameValuePair("fiche",bi.getStatus()));
		nameValuePairs.add(new BasicNameValuePair("objet",bi.getObjet()));
		nameValuePairs.add(new BasicNameValuePair("heur",bi.getHeurD()));
		nameValuePairs.add(new BasicNameValuePair("min",bi.getMinD()));
		nameValuePairs.add(new BasicNameValuePair("month",bi.getMonth()));
		nameValuePairs.add(new BasicNameValuePair("day",bi.getDay()));
		nameValuePairs.add(new BasicNameValuePair("year",bi.getYear()));

		Log.e("Bordereau recupere",bi.toString());

		String jsonString = jsonParser.newmakeHttpRequest(urlInsert, "POST", nameValuePairs);

		try {
			Log.e("Bordereau lien ",jsonString);
			String stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
			JSONObject json = new JSONObject(stfomat);
			return json.getString("lien");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	
	@Override
	public String insertBordereauoff(BordreauIntervention bi, Compte c) {
		String stfomat ="no";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("idclt",bi.getId_clt()));
		nameValuePairs.add(new BasicNameValuePair("duree",bi.getDuree()));
		nameValuePairs.add(new BasicNameValuePair("iduser",c.getIduser()));
		nameValuePairs.add(new BasicNameValuePair("desc",bi.getDescription()));
		nameValuePairs.add(new BasicNameValuePair("fiche",bi.getStatus()));
		nameValuePairs.add(new BasicNameValuePair("objet",bi.getObjet()));
		nameValuePairs.add(new BasicNameValuePair("heur",bi.getHeurD()));
		nameValuePairs.add(new BasicNameValuePair("min",bi.getMinD()));
		nameValuePairs.add(new BasicNameValuePair("month",bi.getMonth()));
		nameValuePairs.add(new BasicNameValuePair("day",bi.getDay()));
		nameValuePairs.add(new BasicNameValuePair("year",bi.getYear()));

		

		Log.e("sended params",nameValuePairs.toString());
		
		try {

			String jsonString = jsonParser.newmakeHttpRequest(urlInsert, "POST", nameValuePairs);
			stfomat = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf("}")+1);
		} catch (Exception e) {
			e.printStackTrace();
			return "no";
		}

		Log.e("Bordereau recupere",stfomat);
		return stfomat;
	}


	@Override
	public List<BordereauGps> selectAllBordereau(Compte c) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));

		String jsonString =  jsonParser.makeHttpRequest(URL_BRD, "POST", nameValuePairs);

		List<BordereauGps> list = new ArrayList<>();

		Log.e("Facture GPS", jsonString );


		try{
			JSONArray jArray = new JSONArray(jsonString);

			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);

				BordereauGps fact = new BordereauGps();

				fact.setId(json.getInt("id"));
				fact.setLat(json.getString("lat"));
				fact.setLng(json.getString("lng"));
				fact.setNumero(json.getString("bordereau"));


				list.add(fact);

			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return list;
	}

	@Override
	public void inesrtImage(List<ImageTechnicien> imgs,String lien) {

		Log.d("Lien ",lien);
		String li = URL.URL+"upload_image.php";

		for (int i = 0; i < imgs.size(); i++) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("cmd",imgs.get(i).getName()));
			nameValuePairs.add(new BasicNameValuePair("path",lien));
			nameValuePairs.add(new BasicNameValuePair("image",imgs.get(i).getImageCode()));
			jsonParser.newmakeHttpRequest(li, "POST", nameValuePairs);
		}

	}

	@Override
	public List<Client> selectAllClient(Compte c) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",c.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password",c.getPassword()));

		String jsonString =  jsonParser.makeHttpRequest(
				urlclt, "POST", nameValuePairs);
		// Parse les donn�es JSON

		List<Client> list = new ArrayList<Client>();

		Log.d("Json retourne >> ", jsonString);
		try{

			JSONArray jArray = new JSONArray(jsonString);
			// check your log for json response
			//Log.d("Login attempt", jArray.toString());


			for(int i=0;i<jArray.length();i++){
				JSONObject json = jArray.getJSONObject(i);
				Client clt = new Client();

				//"rowid":"3","name":"karouani","client":"1","zip":"54020","town":null,
				//"stcomm":"Jamais contact\u00e9","prefix_comm":null,"code_client":"14589"

				clt.setId(json.getInt("rowid"));
				clt.setName(json.getString("name"));
				clt.setZip(json.getString("zip"));
				clt.setTown(json.getString("town"));


				list.add(clt);

			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return list;
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
}
