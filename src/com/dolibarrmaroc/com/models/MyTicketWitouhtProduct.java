package com.dolibarrmaroc.com.models;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyTicketWitouhtProduct implements Serializable{
	
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
    
	public String getDateHeur() {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss" );
		return sdf.format(new Date());
	}

	public MyTicketWitouhtProduct() {
	}

	public String getNameSte() {
		return nameSte;
	}

	@Override
	public String toString() {
		return "MyTicket [nameSte=" + nameSte + ", addresse=" + addresse
				+ ", line=" + getLine() + ", client=" + client
				+ ", dejaRegler=" + dejaRegler + ", Description=" + Description
				+ ", tel=" + tel + ", fax=" + fax + ", IF=" + IF + ", patente="
				+ patente + ", msg=" + msg + ", siteWeb=" + siteWeb
				+ ", numFacture=" + numFacture + "]";
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
