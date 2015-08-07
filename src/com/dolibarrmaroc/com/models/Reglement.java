package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Reglement implements Serializable{
	private int id;
	private String idUser;
	private String paiementcode;
	private String num_paiement;
	private double amount;
	private String fk_facture;
	private String datereg;
	private int idreg;
	private String idnew;
	
	public Reglement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Reglement(int id, String idUser, String paiementcode,
			String num_paiement, double amount) {
		super();
		this.id = id;
		this.idUser = idUser;
		this.paiementcode = paiementcode;
		this.num_paiement = num_paiement;
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getPaiementcode() {
		return paiementcode;
	}

	public void setPaiementcode(String paiementcode) {
		this.paiementcode = paiementcode;
	}

	public String getNum_paiement() {
		return num_paiement;
	}

	public void setNum_paiement(String num_paiement) {
		this.num_paiement = num_paiement;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getFk_facture() {
		return fk_facture;
	}

	public void setFk_facture(String string) {
		this.fk_facture = string;
	}

	@Override
	public String toString() {
		return "Reglement [id=" + id + ", idUser=" + idUser + ", paiementcode="
				+ paiementcode + ", num_paiement=" + num_paiement + ", amount="
				+ amount + ", fk_facture=" + fk_facture + ", datereg="
				+ datereg + ", idreg=" + idreg + "]";
	}

	public String getDatereg() {
		return datereg;
	}

	public void setDatereg(String datereg) {
		this.datereg = datereg;
	}

	public int getIdreg() {
		return idreg;
	}

	public void setIdreg(int idreg) {
		this.idreg = idreg;
	}

	public String getIdnew() {
		return idnew;
	}

	public void setIdnew(String idnew) {
		this.idnew = idnew;
	}
	
}
