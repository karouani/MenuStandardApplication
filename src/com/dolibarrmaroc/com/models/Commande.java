package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commande implements Serializable{

	
	private long idbo;
	private long idandro;
	private String ref;
	private String dt;
	private String clt;
	private List<Produit> prods;
	private Map<String, Remises> remises;
	private Compte compte;
	public Commande() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getIdbo() {
		return idbo;
	}
	public void setIdbo(long idbo) {
		this.idbo = idbo;
	}
	public long getIdandro() {
		return idandro;
	}
	public void setIdandro(long idandro) {
		this.idandro = idandro;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public String getClt() {
		return clt;
	}
	public void setClt(String clt) {
		this.clt = clt;
	}
	public List<Produit> getProds() {
		return prods;
	}
	public void setProds(List<Produit> prods) {
		this.prods = prods;
	}
	public Map<String, Remises> getRemises() {
		return remises;
	}
	public void setRemises(HashMap<String, Remises> remises) {
		this.remises = remises;
	}
	public Commande(long idbo, long idandro, String ref, String dt,
			String clt, List<Produit> prods, Map<String, Remises> remises) {
		super();
		this.idbo = idbo;
		this.idandro = idandro;
		this.ref = ref;
		this.dt = dt;
		this.clt = clt;
		this.prods = prods;
		this.remises = remises;
	}
	@Override
	public String toString() {
		return "Commande [idbo=" + idbo + ", idandro=" + idandro + ", ref="
				+ ref + ", dt=" + dt + ", clt=" + clt + ", prods=" + prods
				+ ", remises=" + remises + "]";
	}
	public Compte getCompte() {
		return compte;
	}
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	
	
}
