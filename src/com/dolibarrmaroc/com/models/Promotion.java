package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Promotion implements Serializable{
	
	/*
	 * Identificateur Promos
	 */
	private int id;
	
	/*
	 * 0 = Remise
	 * 1 = Gratuit�
	 */
	private int type;
	
	/*
	 * Le promos Valeur si gratuite sur tous "promos" produits
	 * Remise par 'promo'
	 */
	private int promos;
	
	/*
	 * quantite de produit par promos produits
	 * remise sur tous les quantite produits par promos %
	 */
	private int quantite;
	
	
	public Promotion() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Promotion(int id, int type, int promos, int quantite) {
		super();
		this.id = id;
		this.type = type;
		this.promos = promos;
		this.quantite = quantite;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPromos() {
		return promos;
	}

	public void setPromos(int promos) {
		this.promos = promos;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	@Override
	public String toString() {
		return "Promotion [id=" + id + ", type=" + type + ", promos=" + promos
				+ ", quantite=" + quantite + "]";
	}
	
}
