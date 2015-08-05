package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.DefaultMouvementManager;
import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.dao.MouvementDao;
import com.dolibarrmaroc.com.dao.MouvementDaoMysql;

public class MouvementManagerFactory {

	private static MouvementManager manager;
	
	static{
		MouvementDao dao = new MouvementDaoMysql();
		manager = new DefaultMouvementManager(dao);
	}

	public static MouvementManager getManager() {
		return manager;
	}

	public static void setManager(MouvementManager manager) {
		MouvementManagerFactory.manager = manager;
	}
}
