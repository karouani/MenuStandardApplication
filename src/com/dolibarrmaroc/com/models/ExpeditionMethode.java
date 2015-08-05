package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class ExpeditionMethode implements Serializable{

	private long idref;
	private String ref;
	private String libelle;
	public ExpeditionMethode() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ExpeditionMethode(long idref, String ref, String libelle) {
		super();
		this.idref = idref;
		this.ref = ref;
		this.libelle = libelle;
	}
	public long getIdref() {
		return idref;
	}
	public void setIdref(long idref) {
		this.idref = idref;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	@Override
	public String toString() {
		return "ExpeditionMethode [idref=" + idref + ", ref=" + ref
				+ ", libelle=" + libelle + "]";
	}
	
}
