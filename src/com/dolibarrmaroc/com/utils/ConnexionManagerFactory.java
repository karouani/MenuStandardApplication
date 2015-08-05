package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.AuthentificationManager;
import com.dolibarrmaroc.com.business.DefaultAuthentificationManager;
import com.dolibarrmaroc.com.dao.ConnexionDao;
import com.dolibarrmaroc.com.dao.ConnexionDaoMysql;





public class ConnexionManagerFactory {

	private  static AuthentificationManager auth;
	private  static ConnexionDao connect;

	
	static{
		connect = new ConnexionDaoMysql();
		auth = new DefaultAuthentificationManager(connect);
	}

	public static AuthentificationManager getCConnexionManager() {
		return auth;
	}
}
