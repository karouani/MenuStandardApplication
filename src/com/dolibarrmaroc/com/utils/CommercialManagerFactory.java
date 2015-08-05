package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.DefaultCommercialManager;
import com.dolibarrmaroc.com.business.DefaultVendeurManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.CommercialDao;
import com.dolibarrmaroc.com.dao.CommercialDaoMysql;
import com.dolibarrmaroc.com.dao.VendeurDao;
import com.dolibarrmaroc.com.dao.VendeurDaoMysql;


public class CommercialManagerFactory {

	private static CommercialManager commercial;
	

	
	static{
		CommercialDao dao = new CommercialDaoMysql();
		commercial = new DefaultCommercialManager(dao);
	}

	public static CommercialManager getCommercialManager() {
		return commercial;
	}
}
