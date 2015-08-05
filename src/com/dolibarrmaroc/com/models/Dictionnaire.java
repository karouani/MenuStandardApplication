package com.dolibarrmaroc.com.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dictionnaire {
	private ArrayList<HashMap<String, String>> dico;

	public Dictionnaire() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Dictionnaire(ArrayList<HashMap<String, String>> dico) {
		super();
		this.dico = dico;
	}

	public ArrayList<HashMap<String, String>> getDico() {
		return dico;
	}

	public void setDico(ArrayList<HashMap<String, String>> dico) {
		this.dico = dico;
	}

	@Override
	public String toString() {
		return "Dictionnaire [dico=" + dico + "]";
	}

	
	
}
