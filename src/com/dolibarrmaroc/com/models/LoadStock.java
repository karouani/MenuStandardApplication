package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;

public class LoadStock implements Serializable{

	private Compte compte;
	private List<Produit> lsprod;
	private long sw;
	private long dw;
	private String sname;
	private String vname;
	private String name_vend;
	private long idvend;
	
	public LoadStock() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LoadStock(Compte compte, List<Produit> lsprod, long sw, long dw) {
		super();
		this.compte = compte;
		this.lsprod = lsprod;
		this.sw = sw;
		this.dw = dw;
	}
	public Compte getCompte() {
		return compte;
	}
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	public List<Produit> getLsprod() {
		return lsprod;
	}
	public void setLsprod(List<Produit> lsprod) {
		this.lsprod = lsprod;
	}
	public long getSw() {
		return sw;
	}
	public void setSw(long sw) {
		this.sw = sw;
	}
	public long getDw() {
		return dw;
	}
	public void setDw(long dw) {
		this.dw = dw;
	}
	@Override
	public String toString() {
		return "LoadStock [compte=" + compte + ", lsprod=" + lsprod + ", sw="
				+ sw + ", dw=" + dw + "]";
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public String getVname() {
		return vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}
	public String getName_vend() {
		return name_vend;
	}
	public void setName_vend(String name_vend) {
		this.name_vend = name_vend;
	}
	public long getIdvend() {
		return idvend;
	}
	public void setIdvend(long idvend) {
		this.idvend = idvend;
	}
	
}
