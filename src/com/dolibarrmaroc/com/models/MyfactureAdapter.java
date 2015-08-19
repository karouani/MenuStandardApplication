package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class MyfactureAdapter implements Serializable{

	private String refclient;
	private String reffact;
	private String amount;
	private String payer;
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
	}
	private int idfact;
	public MyfactureAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MyfactureAdapter(String refclient, String reffact, String amount,
			String payer, int idfact) {
		super();
		this.refclient = refclient;
		this.reffact = reffact;
		this.amount = amount;
		this.payer = payer;
		this.idfact = idfact;
	}
	public String getRefclient() {
		return refclient;
	}
	public void setRefclient(String refclient) {
		this.refclient = refclient;
	}
	public String getReffact() {
		return reffact;
	}
	public void setReffact(String reffact) {
		this.reffact = reffact;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public int getIdfact() {
		return idfact;
	}
	public void setIdfact(int idfact) {
		this.idfact = idfact;
	}
	@Override
	public String toString() {
		return "MyfactureAdapter [refclient=" + refclient + ", reffact="
				+ reffact + ", amount=" + amount + ", payer=" + payer
				+ ", idfact=" + idfact + "]";
	}
}
