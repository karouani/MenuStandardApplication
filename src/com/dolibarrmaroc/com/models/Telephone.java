package com.dolibarrmaroc.com.models;

public class Telephone {

	private int id;
	private String numero;
	private String type;
	
	public Telephone() {
	}

	public Telephone(String numero, String type) {
		super();
		this.numero = numero;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Telephone [id=" + id + ", numero=" + numero + ", type=" + type
				+ "]";
	}
	
}
