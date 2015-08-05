package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.dao.PayementDao;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Reglement;

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
