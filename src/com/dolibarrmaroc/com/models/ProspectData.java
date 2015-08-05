package com.dolibarrmaroc.com.models;

import java.util.HashMap;
import java.util.List;

public class ProspectData {
	
	private List<String> villes;
	private List<String> juridique;
	private List<String> typent;
	
	private HashMap<String, String> juridique_code;
	private HashMap<String,String> typent_code;
	private HashMap<String,String> typent_id;
	
	private List<String> lsrequired;
	
	public ProspectData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProspectData(List<String> villes, List<String> juridique,
			List<String> typent, HashMap<String, String> juridique_code,
			HashMap<String, String> typent_code,
			HashMap<String, String> typent_id) {
		super();
		this.villes = villes;
		this.juridique = juridique;
		this.typent = typent;
		this.juridique_code = juridique_code;
		this.typent_code = typent_code;
		this.typent_id = typent_id;
	}

	public List<String> getVilles() {
		return villes;
	}

	public void setVilles(List<String> villes) {
		this.villes = villes;
	}

	public List<String> getJuridique() {
		return juridique;
	}

	public void setJuridique(List<String> juridique) {
		this.juridique = juridique;
	}

	public List<String> getTypent() {
		return typent;
	}

	public void setTypent(List<String> typent) {
		this.typent = typent;
	}

	public HashMap<String, String> getJuridique_code() {
		return juridique_code;
	}

	public void setJuridique_code(HashMap<String, String> juridique_code) {
		this.juridique_code = juridique_code;
	}

	public HashMap<String, String> getTypent_code() {
		return typent_code;
	}

	public void setTypent_code(HashMap<String, String> typent_code) {
		this.typent_code = typent_code;
	}

	public HashMap<String, String> getTypent_id() {
		return typent_id;
	}

	public void setTypent_id(HashMap<String, String> typent_id) {
		this.typent_id = typent_id;
	}

	@Override
	public String toString() {
		return "ProspectData [villes=" + villes + ", juridique=" + juridique
				+ ", typent=" + typent + ", juridique_code=" + juridique_code
				+ ", typent_code=" + typent_code + ", typent_id=" + typent_id
				+ "]";
	}

	public List<String> getLsrequired() {
		return lsrequired;
	}

	public void setLsrequired(List<String> lsrequired) {
		this.lsrequired = lsrequired;
	}

	
}
