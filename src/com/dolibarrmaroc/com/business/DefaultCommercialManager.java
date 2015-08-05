package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.dao.CommercialDao;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.models.Societe;

public class DefaultCommercialManager implements CommercialManager {

	private CommercialDao dao;

	public DefaultCommercialManager(CommercialDao dao) {
		super();
		this.dao = dao;
	}
	
	@Override
	public String insert(Compte c,Prospection prospect) {
		// TODO Auto-generated method stub
		return dao.insert(c,prospect);
	}
	


	@Override
	public ProspectData getInfos(Compte c) {
		// TODO Auto-generated method stub
		return dao.getInfos(c);
	}

	@Override
	public List<Societe> getAll(Compte c) {
		// TODO Auto-generated method stub
		return dao.getAll(c);
	}

	@Override
	public String update(Compte c, Prospection prospect) {
		// TODO Auto-generated method stub
		return dao.update(c, prospect);
	}

}
