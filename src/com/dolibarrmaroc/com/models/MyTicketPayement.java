package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;

import com.dolibarrmaroc.com.utils.MyTicket;

public class MyTicketPayement implements Serializable{

	private Compte compte;
	private MyTicket ticket;
	private MyTicketWitouhtProduct offlineticket;
	private Client clt;
	private Payement mypay;
	private Reglement myreg;
	private List<Reglement> lsreg;
	private Myinvoice facture;
	public MyTicketPayement() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MyTicketPayement(Compte compte, MyTicket ticket,
			MyTicketWitouhtProduct offlineticket, Client clt, Payement mypay,
			Reglement myreg, List<Reglement> lsreg) {
		super();
		this.compte = compte;
		this.ticket = ticket;
		this.offlineticket = offlineticket;
		this.clt = clt;
		this.mypay = mypay;
		this.myreg = myreg;
		this.lsreg = lsreg;
	}
	public Compte getCompte() {
		return compte;
	}
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	public MyTicket getTicket() {
		return ticket;
	}
	public void setTicket(MyTicket ticket) {
		this.ticket = ticket;
	}
	public MyTicketWitouhtProduct getOfflineticket() {
		return offlineticket;
	}
	public void setOfflineticket(MyTicketWitouhtProduct offlineticket) {
		this.offlineticket = offlineticket;
	}
	public Client getClt() {
		return clt;
	}
	public void setClt(Client clt) {
		this.clt = clt;
	}
	public Payement getMypay() {
		return mypay;
	}
	public void setMypay(Payement mypay) {
		this.mypay = mypay;
	}
	public Reglement getMyreg() {
		return myreg;
	}
	public void setMyreg(Reglement myreg) {
		this.myreg = myreg;
	}
	public List<Reglement> getLsreg() {
		return lsreg;
	}
	public void setLsreg(List<Reglement> lsreg) {
		this.lsreg = lsreg;
	}
	public Myinvoice getFacture() {
		return facture;
	}
	public void setFacture(Myinvoice facture) {
		this.facture = facture;
	}
	@Override
	public String toString() {
		return "MyTicketPayement [compte=" + compte + ", ticket=" + ticket
				+ ", offlineticket=" + offlineticket + ", clt=" + clt
				+ ", mypay=" + mypay + ", myreg=" + myreg + ", lsreg=" + lsreg
				+ ", facture=" + facture + "]";
	}
}
