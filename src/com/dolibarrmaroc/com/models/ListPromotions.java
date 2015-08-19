package com.dolibarrmaroc.com.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListPromotions {
	/*
	 * Integer => id Produit
	 * Integer => id Promotion
	 */
	private HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits;
	
	/*
	 * Integer => id Client
	 * Integer => id Promot
	 */
	private HashMap<Integer, List<Integer>> listPromoByClient;
	

	
	public ListPromotions() {
	}
	
	public ListPromotions(
			HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits,
			HashMap<Integer, List<Integer>> listPromoByClient) {
		super();
		this.listPromoByProduits = listPromoByProduits;
		this.listPromoByClient = listPromoByClient;
	}

	

	public HashMap<Integer, HashMap<Integer, Promotion>> getListPromoByProduits() {
		return listPromoByProduits;
	}

	public void setListPromoByProduits(
			HashMap<Integer, HashMap<Integer, Promotion>> listPromoByProduits) {
		this.listPromoByProduits = listPromoByProduits;
	}

	public HashMap<Integer, List<Integer>> getListPromoByClient() {
		return listPromoByClient;
	}

	public void setListPromoByClient(
			HashMap<Integer, List<Integer>> listPromoByClient) {
		this.listPromoByClient = listPromoByClient;
	}

	public List<Promotion> getPromotions(int idclt,int idprd) {
		
		List<Integer> list = listPromoByClient.get(idclt);
		List<Promotion> lista = new ArrayList<>();
		HashMap<Integer, Promotion> map = listPromoByProduits.get(idprd);
		
		for (int i = 0; i < list.size(); i++) {
			if(map.containsKey(list.get(i))){
				lista.add(map.get(list.get(i)));
			}
		}
		return lista;
	}

	

}
