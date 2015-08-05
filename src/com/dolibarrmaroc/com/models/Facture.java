package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;


public class Facture implements Serializable{

	private int id;
	private String numero;
	
	private List<Produit> produits;
	private GpsTracker gpsFactures;
	private String idclient;
	private String total;
	private String idUser;
	
	private String image;
	private String cmd;
	
	public Facture() {
	}

	public Facture(String numero, List<Produit> produits,
			GpsTracker gpsFactures, String idclient, String total,
			String idUser, String image, String cmd) {
		super();
		this.numero = numero;
		this.produits = produits;
		this.gpsFactures = gpsFactures;
		this.idclient = idclient;
		this.total = total;
		this.idUser = idUser;
		this.image = image;
		this.cmd = cmd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public List<Produit> getProduits() {
		return produits;
	}

	public void setProduits(List<Produit> produits) {
		this.produits = produits;
	}

	public GpsTracker getGpsFactures() {
		return gpsFactures;
	}

	public void setGpsFactures(GpsTracker gpsFactures) {
		this.gpsFactures = gpsFactures;
	}

	public String getIdclient() {
		return idclient;
	}

	public void setIdclient(String idclient) {
		this.idclient = idclient;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public String toString() {
		return "Facture [id=" + id + ", numero=" + numero + ", produits="
				+ produits + ", gpsFactures=" + gpsFactures + ", idclient="
				+ idclient + ", total=" + total + ", idUser=" + idUser
				+ ", image=" + image + ", cmd=" + cmd + "]";
	}

	
}
