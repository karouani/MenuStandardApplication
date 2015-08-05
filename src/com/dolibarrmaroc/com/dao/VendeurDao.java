package com.dolibarrmaroc.com.dao;

import java.util.HashMap;
import java.util.List;

import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.Facture;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;

public interface VendeurDao {
	
	public int insertFacture(Facture fac);
	
	public List<Produit> selectAllProduct(Compte c);
	public Produit selectProduct(String id,Compte c);
	public Dictionnaire getDictionnaire();
	
	public List<Client> selectAllClient(Compte c);
	public List<Promotion> getPromotions(int idclt,int idprd);
	
	public HashMap<Integer, HashMap<Integer, Promotion>> getPromotionProduits();
	public HashMap<Integer, List<Integer>> getPromotionClients();
}
