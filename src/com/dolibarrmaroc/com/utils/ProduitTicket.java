package com.dolibarrmaroc.com.utils;


public class ProduitTicket {
	
	private int qte;
	private String ref;
	private Double prix;
	private int taxe;
	
	public ProduitTicket() {
		// TODO Auto-generated constructor stub
	}

	
	public ProduitTicket(int qte, String ref, Double prix, int taxe) {
		super();
		this.qte = qte;
		this.ref = ref;
		this.prix = prix;
		this.taxe = taxe;
	}

	public int getQte() {
		return qte;
	}

	public void setQte(int qte) {
		this.qte = qte;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public Double getPrix() {
		return prix;
	}

	public void setPrix(Double prix) {
		this.prix = prix;
	}


	public int getTaxe() {
		return taxe;
	}


	public void setTaxe(int taxe) {
		this.taxe = taxe;
	}


	@Override
	public String toString() {
		return "Produit [qte=" + qte + ", ref=" + ref + ", prix=" + prix
				+ ", taxe=" + taxe + "]";
	}

}
