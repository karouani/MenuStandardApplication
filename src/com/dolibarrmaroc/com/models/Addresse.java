package com.dolibarrmaroc.com.models;

public class Addresse {

	private int id;
	private String adress;
	
	public Addresse() {
	}

	public Addresse(String adress) {
		super();
		this.adress = adress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	@Override
	public String toString() {
		return "Addresse [id=" + id + ", adress=" + adress + "]";
	}
	
}
