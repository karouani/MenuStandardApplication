package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.DefaultCommercialManager;
import com.dolibarrmaroc.com.dao.CommercialDao;
import com.dolibarrmaroc.com.dao.CommercialDaoMysql;


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
