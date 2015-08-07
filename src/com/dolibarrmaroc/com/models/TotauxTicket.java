package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class TotauxTicket implements Serializable{
	
	private int id;
	private double total_ht;
	private double total_ttc;
	private double rest;
	private double total_tva;
	private double regle;
	
	public TotauxTicket() {
		// TODO Auto-generated constructor stub
	}

	public TotauxTicket(int id, double total_ht, double total_ttc, double rest,
			double total_tva, double regle) {
		super();
		this.id = id;
		this.total_ht = total_ht;
		this.total_ttc = total_ttc;
		this.rest = rest;
		this.total_tva = total_tva;
		this.regle = regle;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTotal_ht() {
		return total_ht;
	}

	public void setTotal_ht(double total_ht) {
		this.total_ht = total_ht;
	}

	public double getTotal_ttc() {
		return total_ttc;
	}

	public void setTotal_ttc(double total_ttc) {
		this.total_ttc = total_ttc;
	}

	public double getRest() {
		return rest;
	}

	public void setRest(double rest) {
		this.rest = rest;
	}

	public double getTotal_tva() {
		return total_tva;
	}

	public void setTotal_tva(double total_tva) {
		this.total_tva = total_tva;
	}

	public double getRegle() {
		return regle;
	}

	public void setRegle(double regle) {
		this.regle = regle;
	}

	@Override
	public String toString() {
		return "TotauxTicket [id=" + id + ", total_ht=" + total_ht
				+ ", total_ttc=" + total_ttc + ", rest=" + rest
				+ ", total_tva=" + total_tva + ", regle=" + regle + "]";
	}

}
