package com.dolibarrmaroc.com.business;

import java.util.List;
import java.util.Map;

import com.dolibarrmaroc.com.dao.CommandeDao;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Remises;

public class DefaultCommandeManager implements CommandeManager {

	private CommandeDao dao;
	
	public DefaultCommandeManager(CommandeDao dx){
		this.dao = dx;
	}
	@Override
	public List<Commandeview> charger_commandes(Compte c) {
		// TODO Auto-generated method stub
		return dao.charger_commandes(c);
	}
	@Override
	public String insertCommande(List<Produit> prd, String idclt,
			Compte compte, Map<String, Remises> allremises) {
		// TODO Auto-generated method stub
		return dao.insertCommande(prd, idclt, compte, allremises);
	}
	@Override
	public String CmdToFacture(Commandeview cv, Compte cp) {
		// TODO Auto-generated method stub
		return dao.CmdToFacture(cv,cp);
	}

}
