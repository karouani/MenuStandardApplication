package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class MyProdRemise implements Serializable{

	private String ref;
	private Remises remise;
	@Override
	public String toString() {
		return "MyProdRemise [ref=" + ref + ", remise=" + remise + "]";
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public Remises getRemise() {
		return remise;
	}
	public void setRemise(Remises remise) {
		this.remise = remise;
	}
	public MyProdRemise(String ref, Remises remise) {
		super();
		this.ref = ref;
		this.remise = remise;
	}
	public MyProdRemise() {
		super();
		// TODO Auto-generated constructor stub
	}
}
