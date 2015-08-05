package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.dao.MouvementDao;
import com.dolibarrmaroc.com.dao.VendeurDao;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.LoadStock;
import com.dolibarrmaroc.com.models.Mouvement;

public class DefaultMouvementManager implements MouvementManager {

	private MouvementDao dao;
	
	public DefaultMouvementManager(MouvementDao dao) {
		super();
		this.dao = dao;
	}
	
	@Override
	public LoadStock laodStock(Compte cp) {
		// TODO Auto-generated method stub
		return dao.laodStock(cp);
	}

	@Override
	public String makemouvement(List<Mouvement> mvs, Compte cp, String label) {
		// TODO Auto-generated method stub
		return dao.makemouvement(mvs, cp, label);
	}

	@Override
	public String makeechange(List<Mouvement> mvs, Compte cp, String label,String clt,int tpmv) {
		// TODO Auto-generated method stub
		return dao.makeechange(mvs, cp, label,clt,tpmv);
	}

}
