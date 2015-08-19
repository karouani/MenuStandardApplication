package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Myinvoice implements Serializable {

	private String invoice;
	private int invoid;
	public String idnew;
	private List<Produit> prd;
	private String idclt;
	private int nmb;
	private String commentaire;
	private Compte compte;
	private String reglement;
	private String amount;
	private String numChek;
	private int typeImpriment;
	private List<MyProdRemise> remises;
	
	private GpsTracker gps;
	private String imei;
	private String num;
	private String battery;
	private FileData data;
	private int type_invoice;
	
	private TotauxTicket total_ticket; 
	
	private List<Reglement> lsregs = new ArrayList<>();
	
	public Myinvoice() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<Produit> getPrd() {
		return prd;
	}
	public void setPrd(List<Produit> prd) {
		this.prd = prd;
	}
	public String getIdclt() {
		return idclt;
	}
	public void setIdclt(String idclt) {
		this.idclt = idclt;
	}
	public int getNmb() {
		return nmb;
	}
	public void setNmb(int nmb) {
		this.nmb = nmb;
	}
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public Compte getCompte() {
		return compte;
	}
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	public String getReglement() {
		return reglement;
	}
	public void setReglement(String reglement) {
		this.reglement = reglement;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getNumChek() {
		return numChek;
	}
	public void setNumChek(String numChek) {
		this.numChek = numChek;
	}
	public int getTypeImpriment() {
		return typeImpriment;
	}
	public void setTypeImpriment(int typeImpriment) {
		this.typeImpriment = typeImpriment;
	}
	public List<MyProdRemise> getRemises() {
		return remises;
	}
	public void setRemises(List<MyProdRemise> remises) {
		this.remises = remises;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	public Myinvoice(String invoice, List<Produit> prd, String idclt, int nmb,
			String commentaire, Compte compte, String reglement, String amount,
			String numChek, int typeImpriment, List<MyProdRemise> remises,
			GpsTracker gps, String imei, String num, String battery) {
		super();
		this.invoice = invoice;
		this.prd = prd;
		this.idclt = idclt;
		this.nmb = nmb;
		this.commentaire = commentaire;
		this.compte = compte;
		this.reglement = reglement;
		this.amount = amount;
		this.numChek = numChek;
		this.typeImpriment = typeImpriment;
		this.remises = remises;
		this.gps = gps;
		this.imei = imei;
		this.num = num;
		this.battery = battery;
	}
	public GpsTracker getGps() {
		return gps;
	}
	public void setGps(GpsTracker gps) {
		this.gps = gps;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	
	public FileData getData() {
		return data;
	}
	public void setData(FileData data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Myinvoice [invoice=" + invoice + ", invoid=" + invoid
				+ ", prd=" + prd + ", idclt=" + idclt + ", nmb=" + nmb
				+ ", commentaire=" + commentaire + ", compte=" + compte
				+ ", reglement=" + reglement + ", amount=" + amount
				+ ", numChek=" + numChek + ", typeImpriment=" + typeImpriment
				+ ", remises=" + remises + ", gps=" + gps + ", imei=" + imei
				+ ", num=" + num + ", battery=" + battery + ", data=" + data
				+ ", total_ticket=" + total_ticket + ", lsregs=" + lsregs + "]";
	}
	public TotauxTicket getTotal_ticket() {
		return total_ticket;
	}
	public void setTotal_ticket(TotauxTicket total_ticket) {
		this.total_ticket = total_ticket;
	}
	public int getInvoid() {
		return invoid;
	}
	public void setInvoid(int invoid) {
		this.invoid = invoid;
	}
	public List<Reglement> getLsregs() {
		return lsregs;
	}
	public void setLsregs(List<Reglement> lsregs) {
		this.lsregs = lsregs;
	}
	public String getIdnew() {
		return idnew;
	}
	public void setIdnew(String idnew) {
		this.idnew = idnew;
	}
	public int getType_invoice() {
		return type_invoice;
	}
	public void setType_invoice(int type_invoice) {
		this.type_invoice = type_invoice;
	}
}
