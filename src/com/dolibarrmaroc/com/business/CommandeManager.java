package com.dolibarrmaroc.com.business;

import java.util.List;
import java.util.Map;

import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Remises;

public interface CommandeManager {

	public List<Commandeview> charger_commandes(Compte c);
	public String insertCommande(List<Produit> prd, String idclt,  Compte compte, Map<String, Remises> allremises);
	public String CmdToFacture(Commandeview cv,Compte cp);
}
