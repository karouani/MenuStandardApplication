package com.dolibarrmaroc.com.utils;

import com.dolibarrmaroc.com.business.DefaultPayementManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.dao.PayementDao;
import com.dolibarrmaroc.com.dao.PayementDaoMysql;



public class PayementManagerFactory {

	private static PayementManager payementFactory;
	

	
	static{
		PayementDao dao = new PayementDaoMysql();
		payementFactory = new DefaultPayementManager(dao);
	}



	public static PayementManager getPayementFactory() {
		return payementFactory;
	}
	
}

