package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.DefaultCommandeManager;
import com.dolibarrmaroc.com.dao.CommandeDao;
import com.dolibarrmaroc.com.dao.CommandeDaoMysql;

public class CommandeManagerFactory {

	private static CommandeManager manager;
	
	static{
		CommandeDao dao = new CommandeDaoMysql();
		manager = new DefaultCommandeManager(dao);
	}

	public static CommandeManager getManager() {
		return manager;
	}

	
}
