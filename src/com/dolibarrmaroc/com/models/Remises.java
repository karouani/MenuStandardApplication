package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Remises implements Serializable{
	private int id;
	private int qte;
	private int type;
	private int remise = 0;
	
	
	public Remises() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Remises(int id, int qte, int type, int remise) {
		super();
		this.id = id;
		this.qte = qte;
		this.type = type;
		this.remise = remise;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getQte() {
		return qte;
	}


	public void setQte(int qte) {
		this.qte = qte;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public int getRemise() {
		return remise;
	}


	public void setRemise(int remise) {
		this.remise = remise;
	}


	@Override
	public String toString() {
		return "Remises [id=" + id + ", qte=" + qte + ", type=" + type
				+ ", remise=" + remise + "]";
	}
	
}
