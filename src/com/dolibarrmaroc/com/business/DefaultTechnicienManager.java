package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.dao.TechnicienDao;
import com.dolibarrmaroc.com.models.BordereauGps;
import com.dolibarrmaroc.com.models.BordreauIntervention;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ImageTechnicien;
import com.dolibarrmaroc.com.models.Services;


public class DefaultTechnicienManager implements TechnicienManager {

	private TechnicienDao dao;
	
	public DefaultTechnicienManager() {
	}

	
	public DefaultTechnicienManager(TechnicienDao dao) {
		super();
		this.dao = dao;
	}

	public TechnicienDao getDao() {
		return dao;
	}


	public void setDao(TechnicienDao dao) {
		this.dao = dao;
	}


	@Override
	public String insertBordereau(BordreauIntervention bi, Compte c) {
		return dao.insertBordereau(bi, c);
	}


	@Override
	public List<BordereauGps> selectAllBordereau(Compte c) {
		return dao.selectAllBordereau(c);
	}


	@Override
	public List<Services> allServices(Compte c) {
		return dao.allServices(c);
	}


	@Override
	public void inesrtImage(List<ImageTechnicien> imgs,String lien) {
		dao.inesrtImage(imgs,lien);
	}


	@Override
	public List<Client> selectAllClient(Compte c) {
		// TODO Auto-generated method stub
		return dao.selectAllClient(c);
	}


	@Override
	public String insertBordereauoff(BordreauIntervention bi, Compte c) {
		// TODO Auto-generated method stub
		return dao.insertBordereauoff(bi, c);
	}



}
