package com.dolibarrmaroc.com.models;

public class Societe {
	
	/*
	 * {"rowid":"590","name":"com Commercial","address":"","town":null,"phone":null,"fax":null,"email":null,"type":"3","company":0,"latitude":"0","longitude":"0"}
	 */
	private int id;
	private String name;
	private String address;
	private String town;
	private String phone;
	private String fax;
	private String email;
	private int type;
	private int company;
	private Double latitude;
	private Double longitude;
	
	public Societe() {
		// TODO Auto-generated constructor stub
	}

	public Societe(int id, String name, String address, String town,
			String phone, String fax, String email, int type, int company,
			Double latitude, Double longitude) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.town = town;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.type = type;
		this.company = company;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCompany() {
		return company;
	}

	public void setCompany(int company) {
		this.company = company;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Societe [id=" + id + ", name=" + name + ", address=" + address
				+ ", town=" + town + ", phone=" + phone + ", fax=" + fax
				+ ", email=" + email + ", type=" + type + ", company="
				+ company + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}

}
