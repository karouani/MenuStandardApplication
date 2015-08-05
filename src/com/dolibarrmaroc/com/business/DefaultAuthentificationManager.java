package com.dolibarrmaroc.com.business;

import com.dolibarrmaroc.com.dao.ConnexionDao;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.Services;



public class DefaultAuthentificationManager implements AuthentificationManager {

	private ConnexionDao dao;
	
	public DefaultAuthentificationManager() {
	}
	
	public DefaultAuthentificationManager(ConnexionDao dao) {
		super();
		this.dao = dao;
	}

	public ConnexionDao getDao() {
		return dao;
	}

	public void setDao(ConnexionDao dao) {
		this.dao = dao;
	}

	@Override
	public Compte login(String login, String password) {
		return dao.login(login, password);
	}

	@Override
	public ConfigGps getGpsConfig() {
		return dao.getGpsConfig();
	}

	@Override
	public Services getService(String login, String password) {
		return dao.getService(login, password);
	}


}
