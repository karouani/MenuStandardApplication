package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.DefaultVendeurManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.VendeurDao;
import com.dolibarrmaroc.com.dao.VendeurDaoMysql;


public class VendeurManagerFactory {

	private static VendeurManager vendeurManager;
	

	
	static{
		VendeurDao dao = new VendeurDaoMysql();
		vendeurManager = new DefaultVendeurManager(dao);
	}

	public static VendeurManager getClientManager() {
		return vendeurManager;
	}
}
