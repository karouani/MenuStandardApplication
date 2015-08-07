package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.DefaultTechnicienManager;
import com.dolibarrmaroc.com.business.TechnicienManager;
import com.dolibarrmaroc.com.dao.TechnicienDao;
import com.dolibarrmaroc.com.dao.TechnicienDaoMysql;




public class TechnicienManagerFactory {

	private static TechnicienManager technicienManager;
	

	
	static{
		TechnicienDao dao = new TechnicienDaoMysql();
		technicienManager = new DefaultTechnicienManager(dao);
	}

	public static TechnicienManager getClientManager() {
		return technicienManager;
	}
}
