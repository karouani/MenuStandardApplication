package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Commandeview implements Serializable{

	private int rowid;
	private String ref;
	private Client clt;
	private double ttc;
	private double ht;
	private double tva;
	private String dt;
	private List<Produit> lsprods = new ArrayList<>();
	public Commandeview() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Commandeview(int rowid, String ref, Client clt, double ttc,
			double ht, double tva, String dt, List<Produit> lsprods) {
		super();
		this.rowid = rowid;
		this.ref = ref;
		this.clt = clt;
		this.ttc = ttc;
		this.ht = ht;
		this.tva = tva;
		this.dt = dt;
		this.lsprods = lsprods;
	}
	public int getRowid() {
		return rowid;
	}
	public void setRowid(int rowid) {
		this.rowid = rowid;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public Client getClt() {
		return clt;
	}
	public void setClt(Client clt) {
		this.clt = clt;
	}
	public double getTtc() {
		return ttc;
	}
	public void setTtc(double ttc) {
		this.ttc = ttc;
	}
	public double getHt() {
		return ht;
	}
	public void setHt(double ht) {
		this.ht = ht;
	}
	public double getTva() {
		return tva;
	}
	public void setTva(double tva) {
		this.tva = tva;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public List<Produit> getLsprods() {
		return lsprods;
	}
	public void setLsprods(List<Produit> lsprods) {
		this.lsprods = lsprods;
	}
	@Override
	public String toString() {
		return "Commandeview [rowid=" + rowid + ", ref=" + ref + ", clt=" + clt
				+ ", ttc=" + ttc + ", ht=" + ht + ", tva=" + tva + ", dt=" + dt
				+ ", lsprods=" + lsprods + "]";
	}
	
}
