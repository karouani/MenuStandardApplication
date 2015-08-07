package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Produit implements Serializable{

	private int id;
	private String ref;
	private String desig;
	private int qteDispo;
	private String prixUnitaire;
	private int qtedemander;
	private double prixttc;
	
	private String tva_tx;
	private String fk_tva;
	
	
	private String photo;
	
	public Produit() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Produit(String ref, String desig, int qteDispo, String prixUnitaire,
			int qtedemander, double prixttc, String tva_tx, String fk_tva) {
		super();
		this.ref = ref;
		this.desig = desig;
		this.qteDispo = qteDispo;
		this.prixUnitaire = prixUnitaire;
		this.qtedemander = qtedemander;
		this.prixttc = prixttc;
		this.tva_tx = tva_tx;
		this.fk_tva = fk_tva;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getDesig() {
		return desig;
	}

	public void setDesig(String desig) {
		this.desig = desig;
	}

	public int getQteDispo() {
		return qteDispo;
	}

	public void setQteDispo(int qteDispo) {
		this.qteDispo = qteDispo;
	}

	public String getPrixUnitaire() {
		return prixUnitaire;
	}

	public void setPrixUnitaire(String prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}

	public int getQtedemander() {
		return qtedemander;
	}

	public void setQtedemander(int qtedemander) {
		this.qtedemander = qtedemander;
	}

	public double getPrixttc() {
		return prixttc;
	}

	public void setPrixttc(double prixttc) {
		this.prixttc = prixttc;
	}

	public String getTva_tx() {
		return tva_tx;
	}

	public void setTva_tx(String tva_tx) {
		this.tva_tx = tva_tx;
	}

	public String getFk_tva() {
		return fk_tva;
	}

	public void setFk_tva(String fk_tva) {
		this.fk_tva = fk_tva;
	}

	@Override
	public String toString() {
		return "Produit [id=" + id + ", ref=" + ref + ", desig=" + desig
				+ ", qteDispo=" + qteDispo + ", prixUnitaire=" + prixUnitaire
				+ ", qtedemander=" + qtedemander + ", prixttc=" + prixttc
				+ ", tva_tx=" + tva_tx + ", fk_tva=" + fk_tva + "]";
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	
}
