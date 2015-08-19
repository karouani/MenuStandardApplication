package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategorieCustomer implements Serializable{

	private long id;
	private String libelle;
	private String description;
	private List<Integer> lsclt = new ArrayList<>();
	public CategorieCustomer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CategorieCustomer(long id, String libelle, String description,
			List<Integer> lsclt) {
		super();
		this.id = id;
		this.libelle = libelle;
		this.description = description;
		this.lsclt = lsclt;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Integer> getLsclt() {
		return lsclt;
	}
	public void setLsclt(List<Integer> lsclt) {
		this.lsclt = lsclt;
	}
	@Override
	public String toString() {
		return "CategorieCustomer [id=" + id + ", libelle=" + libelle
				+ ", description=" + description + ", lsclt=" + lsclt + "]";
	}
}
