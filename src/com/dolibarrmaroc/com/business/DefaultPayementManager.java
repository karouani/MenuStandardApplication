package com.dolibarrmaroc.com.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dolibarrmaroc.com.dao.PayementDao;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Reglement;
import com.dolibarrmaroc.com.utils.JSONParser;
import com.dolibarrmaroc.com.utils.URL;

public class DefaultPayementManager implements PayementManager{
	
	private PayementDao dao;
	
	public DefaultPayementManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public DefaultPayementManager(PayementDao dao) {
		super();
		this.dao = dao;
	}


	public void setDao(PayementDao dao) {
		this.dao = dao;
	}


	@Override
	public List<Payement> getFactures(Compte c) {
		// TODO Auto-generated method stub
		return dao.getFactures(c);
	}

	@Override
	public String insertPayement(Reglement reg, Compte c) {
		// TODO Auto-generated method stub
		return dao.insertPayement(reg, c);
	}

	
	
}
