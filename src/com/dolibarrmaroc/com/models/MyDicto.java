package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class MyDicto implements Serializable{

	private String code;
	private String libelle;
	@Override
	public String toString() {
		return "MyDicto [code=" + code + ", libelle=" + libelle + "]";
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public MyDicto(String code, String libelle) {
		super();
		this.code = code;
		this.libelle = libelle;
	}
	public MyDicto() {
		super();
		// TODO Auto-generated constructor stub
	}
}
