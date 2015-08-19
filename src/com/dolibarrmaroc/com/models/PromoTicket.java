package com.dolibarrmaroc.com.models;

public class PromoTicket {
	
	private int id;
	private String desig;
	private int qte;
	private int type;
	private String value;
	private String porcentage;
	
	public PromoTicket() {
		// TODO Auto-generated constructor stub
	}

	public PromoTicket(int id, String desig, int qte, int type, String value) {
		super();
		this.id = id;
		this.desig = desig;
		this.qte = qte;
		this.type = type;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesig() {
		return desig;
	}

	public void setDesig(String desig) {
		this.desig = desig;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "PromoTicket [id=" + id + ", desig=" + desig + ", qte=" + qte
				+ ", type=" + type + ", value=" + value + ", porcentage="
				+ porcentage + "]";
	}

	public String getPorcentage() {
		return porcentage;
	}

	public void setPorcentage(String porcentage) {
		this.porcentage = porcentage;
	}

}
