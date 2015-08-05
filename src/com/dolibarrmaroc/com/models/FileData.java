package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileData implements Serializable {

	private String pdf;
	private String path;
	private String erreur;
	private String fileName;
	private String nameSte;
	private String addresse;
	private String client;
    private Double dejaRegler;
    private String Description;
    private String tel;
    private String fax;
    private String IF;
    private String patente;
    private String msg;
    private String siteWeb;
    private String numFacture;
    private String produits;
    
	public FileData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public String getProduits() {
		return produits;
	}



	public void setProduits(String produits) {
		this.produits = produits;
	}



	public String getNameSte() {
		return nameSte;
	}



	public String getDateHeur() {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss" );
		return sdf.format(new Date());
	}

	public FileData(String pdf, String path, String erreur) {
		super();
		this.pdf = pdf;
		this.path = path;
		this.erreur = erreur;
	}
	public String getPdf() {
		return pdf;
	}
	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getErreur() {
		return erreur;
	}
	public void setErreur(String erreur) {
		this.erreur = erreur;
	}
	@Override
	public String toString() {
		return "FileData [pdf=" + pdf + ", path=" + path + ", erreur=" + erreur
				+ ", fileName=" + fileName + ", nameSte=" + nameSte
				+ ", addresse=" + addresse + ", client=" + client
				+ ", dejaRegler=" + dejaRegler + ", Description=" + Description
				+ ", tel=" + tel + ", fax=" + fax + ", IF=" + IF + ", patente="
				+ patente + ", msg=" + msg + ", siteWeb=" + siteWeb
				+ ", numFacture=" + numFacture + "]";
	}
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getNumFacture() {
		return numFacture;
	}

	public void setNumFacture(String numFacture) {
		this.numFacture = numFacture;
	}

	public void setNameSte(String nameSte) {
		this.nameSte = nameSte;
	}

	public String getAddresse() {
		return addresse;
	}

	public void setAddresse(String addresse) {
		this.addresse = addresse;
	}

	public String getLine() {
		return "--------------------------------";
	}


	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Double getDejaRegler() {
		return dejaRegler;
	}

	public void setDejaRegler(Double dejaRegler) {
		this.dejaRegler = dejaRegler;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getIF() {
		return IF;
	}

	public void setIF(String iF) {
		IF = iF;
	}

	public String getPatente() {
		return patente;
	}

	public void setPatente(String patente) {
		this.patente = patente;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSiteWeb() {
		return siteWeb;
	}

	public void setSiteWeb(String siteWeb) {
		this.siteWeb = siteWeb;
	}

	
}
