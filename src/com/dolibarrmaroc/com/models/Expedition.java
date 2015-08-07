package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.Date;

public class Expedition implements Serializable{

	private String refexpe;
	private Produit prodexpe;
	private double qntexpe;
	private ExpeditionMethode expometh;
	
	private Date datecreation;
	private Date dateliv;
	
	private String idUser;
	private String cmd;
	private String client;
	
	public Expedition() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Expedition(String refexpe, Produit prodexpe, double qntexpe,
			ExpeditionMethode expometh, String idUser, String cmd, String client) {
		super();
		this.refexpe = refexpe;
		this.prodexpe = prodexpe;
		this.qntexpe = qntexpe;
		this.expometh = expometh;
		this.idUser = idUser;
		this.cmd = cmd;
		this.client = client;
	}
	public Expedition(Produit prodexpe, double qntexpe,
			ExpeditionMethode expometh) {
		super();
		this.prodexpe = prodexpe;
		this.qntexpe = qntexpe;
		this.expometh = expometh;
	}
	public String getRefexpe() {
		return refexpe;
	}
	public void setRefexpe(String refexpe) {
		this.refexpe = refexpe;
	}
	public Produit getProdexpe() {
		return prodexpe;
	}
	public void setProdexpe(Produit prodexpe) {
		this.prodexpe = prodexpe;
	}
	public double getQntexpe() {
		return qntexpe;
	}
	public void setQntexpe(double qntexpe) {
		this.qntexpe = qntexpe;
	}
	public ExpeditionMethode getExpometh() {
		return expometh;
	}
	public void setExpometh(ExpeditionMethode expometh) {
		this.expometh = expometh;
	}
	public String getIdUser() {
		return idUser;
	}
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	@Override
	public String toString() {
		return "Expedition [refexpe=" + refexpe + ", prodexpe=" + prodexpe
				+ ", qntexpe=" + qntexpe + ", expometh=" + expometh
				+ ", idUser=" + idUser + ", cmd=" + cmd + ", client=" + client
				+ "]";
	}
	public Date getDatecreation() {
		return datecreation;
	}
	public void setDatecreation(Date datecreation) {
		this.datecreation = datecreation;
	}
	public Date getDateliv() {
		return dateliv;
	}
	public void setDateliv(Date dateliv) {
		this.dateliv = dateliv;
	}
	
}
