package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Mouvement implements Serializable{

	private long ref;
	private Produit prod;
	private String sw;
	private String dw;
	private double qty;
	public long getRef() {
		return ref;
	}
	public void setRef(long ref) {
		this.ref = ref;
	}
	public Produit getProd() {
		return prod;
	}
	public void setProd(Produit prod) {
		this.prod = prod;
	}
	public String getSw() {
		return sw;
	}
	public void setSw(String sw) {
		this.sw = sw;
	}
	public String getDw() {
		return dw;
	}
	public void setDw(String dw) {
		this.dw = dw;
	}
	public double getQty() {
		return qty;
	}
	public void setQty(double qty) {
		this.qty = qty;
	}
	@Override
	public String toString() {
		return "Mouvement [ref=" + ref + ", prod=" + prod + ", sw=" + sw
				+ ", dw=" + dw + ", qty=" + qty + "]";
	}
	public Mouvement() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Mouvement(long ref, Produit prod, String sw, String dw, double qty) {
		super();
		this.ref = ref;
		this.prod = prod;
		this.sw = sw;
		this.dw = dw;
		this.qty = qty;
	}
	
	
}
