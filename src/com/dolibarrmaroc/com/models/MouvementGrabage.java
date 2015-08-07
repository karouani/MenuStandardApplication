package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;

public class MouvementGrabage implements Serializable{

	private String clt;
	private List<Mouvement> lsmvs;
	private Compte cpt;
	public MouvementGrabage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MouvementGrabage(String clt, List<Mouvement> lsmvs, Compte cpt) {
		super();
		this.clt = clt;
		this.lsmvs = lsmvs;
		this.cpt = cpt;
	}
	public String getClt() {
		return clt;
	}
	public void setClt(String clt) {
		this.clt = clt;
	}
	public List<Mouvement> getLsmvs() {
		return lsmvs;
	}
	public void setLsmvs(List<Mouvement> lsmvs) {
		this.lsmvs = lsmvs;
	}
	public Compte getCpt() {
		return cpt;
	}
	public void setCpt(Compte cpt) {
		this.cpt = cpt;
	}
	@Override
	public String toString() {
		return "MouvementGrabage [clt=" + clt + ", lsmvs=" + lsmvs + ", cpt="
				+ cpt + "]";
	}
}
