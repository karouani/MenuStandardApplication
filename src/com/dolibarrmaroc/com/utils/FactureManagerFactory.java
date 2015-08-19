package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.DefaultFactureManager;
import com.dolibarrmaroc.com.business.FactureManager;
import com.dolibarrmaroc.com.dao.FactureDao;
import com.dolibarrmaroc.com.dao.FactureDaoMysql;



public class FactureManagerFactory {

	private static FactureManager factureManager;
	

	
	static{
		FactureDao dao = new FactureDaoMysql();
		factureManager = new DefaultFactureManager(dao);
	}

	public static FactureManager getFactureManager() {
		return factureManager;
	}
}

