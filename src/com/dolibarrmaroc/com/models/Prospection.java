package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Prospection implements Serializable{


	private int id;
	private String name;
	private String firstname;
	private String lastname;
	private int particulier;
	private String civility_id;
	private String address;
	private String zip;
	private String town;
	private int status = 0;   // 0=activity ceased, 1= in activity


	private int country_id = 12;
	private String country_code = "MA";
	private String country="Maroc";

	private String phone;
	private String fax;
	private String email;

	// 4 professional id (usage depend on country)
	private String idprof1;	// IdProf1 (Ex: Siren in France)
	private String idprof2;	// IdProf2 (Ex: Siret in France)
	private String idprof3;	// IdProf3 (Ex: Ape in France)
	private String idprof4;	// IdProf4 (Ex: RCS in France)

	private int tva_assuj;


	private String capital;
	private String typent_id;
	private String typent_code;
	private String effectif_id;

	private String forme_juridique_code;

	private int client = 0;					// 0=no customer, 1=customer, 2=prospect, 3=customer and prospect
	private int prospect = 0;					// 0=no prospect, 1=prospect
	private int fournisseur = 0;				// 0=no supplier, 1=supplier


	private int commercial_id;  // Id of sales representative to link (used for thirdparty creation). Not filled by a fetch, because we can have several sales representatives.
	private int parent;
	
	
	private double latitude;
	private double langitude;
	
	private int idpros;
	
	public Prospection(int id, String name, String firstname, String lastname,
			int particulier, String civility_id, String address, String zip,
			String town, int status, int country_id, String country_code,
			String country, String phone, String fax, String email,
			String idprof1, String idprof2, String idprof3, String idprof4,
			int tva_assuj, String capital, String typent_id,
			String typent_code, String effectif_id,
			String forme_juridique_code, int client, int prospect,
			int fournisseur, int commercial_id, int parent, double latitude,
			double langitude) {
		super();
		this.id = id;
		this.name = name;
		this.firstname = firstname;
		this.lastname = lastname;
		this.particulier = particulier;
		this.civility_id = civility_id;
		this.address = address;
		this.zip = zip;
		this.town = town;
		this.status = status;
		this.country_id = country_id;
		this.country_code = country_code;
		this.country = country;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.idprof1 = idprof1;
		this.idprof2 = idprof2;
		this.idprof3 = idprof3;
		this.idprof4 = idprof4;
		this.tva_assuj = tva_assuj;
		this.capital = capital;
		this.typent_id = typent_id;
		this.typent_code = typent_code;
		this.effectif_id = effectif_id;
		this.forme_juridique_code = forme_juridique_code;
		this.client = client;
		this.prospect = prospect;
		this.fournisseur = fournisseur;
		this.commercial_id = commercial_id;
		this.parent = parent;
		this.latitude = latitude;
		this.langitude = langitude;
	}



	public double getLatitude() {
		return latitude;
	}



	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	public double getLangitude() {
		return langitude;
	}



	public void setLangitude(double langitude) {
		this.langitude = langitude;
	}



	public Prospection() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public Prospection(int id, String name, String firstname, String lastname,
			int particulier, String civility_id, String address, String zip,
			String town, int status, int country_id, String country_code,
			String country, String phone, String fax, String email,
			String idprof1, String idprof2, String idprof3, String idprof4,
			int tva_assuj, String capital, String typent_id,
			String typent_code, String effectif_id,
			String forme_juridique_code, int client, int prospect,
			int fournisseur, int commercial_id, int parent) {
		super();
		this.id = id;
		this.name = name;
		this.firstname = firstname;
		this.lastname = lastname;
		this.particulier = particulier;
		this.civility_id = civility_id;
		this.address = address;
		this.zip = zip;
		this.town = town;
		this.status = status;
		this.country_id = country_id;
		this.country_code = country_code;
		this.country = country;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.idprof1 = idprof1;
		this.idprof2 = idprof2;
		this.idprof3 = idprof3;
		this.idprof4 = idprof4;
		this.tva_assuj = tva_assuj;
		this.capital = capital;
		this.typent_id = typent_id;
		this.typent_code = typent_code;
		this.effectif_id = effectif_id;
		this.forme_juridique_code = forme_juridique_code;
		this.client = client;
		this.prospect = prospect;
		this.fournisseur = fournisseur;
		this.commercial_id = commercial_id;
		this.parent = parent;
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

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getParticulier() {
		return particulier;
	}

	public void setParticulier(int particulier) {
		this.particulier = particulier;
	}

	public String getCivility_id() {
		return civility_id;
	}

	public void setCivility_id(String civility_id) {
		this.civility_id = civility_id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCountry_id() {
		return country_id;
	}

	public void setCountry_id(int country_id) {
		this.country_id = country_id;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getIdprof1() {
		return idprof1;
	}

	public void setIdprof1(String idprof1) {
		this.idprof1 = idprof1;
	}

	public String getIdprof2() {
		return idprof2;
	}

	public void setIdprof2(String idprof2) {
		this.idprof2 = idprof2;
	}

	public String getIdprof3() {
		return idprof3;
	}

	public void setIdprof3(String idprof3) {
		this.idprof3 = idprof3;
	}

	public String getIdprof4() {
		return idprof4;
	}

	public void setIdprof4(String idprof4) {
		this.idprof4 = idprof4;
	}

	public int getTva_assuj() {
		return tva_assuj;
	}

	public void setTva_assuj(int tva_assuj) {
		this.tva_assuj = tva_assuj;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public String getTypent_id() {
		return typent_id;
	}

	public void setTypent_id(String typent_id) {
		this.typent_id = typent_id;
	}

	public String getEffectif_id() {
		return effectif_id;
	}

	public void setEffectif_id(String effectif_id) {
		this.effectif_id = effectif_id;
	}

	public String getForme_juridique_code() {
		return forme_juridique_code;
	}

	public void setForme_juridique_code(String forme_juridique_code) {
		this.forme_juridique_code = forme_juridique_code;
	}

	public int getClient() {
		return client;
	}

	public void setClient(int client) {
		this.client = client;
	}

	public int getProspect() {
		return prospect;
	}

	public void setProspect(int prospect) {
		this.prospect = prospect;
	}

	public int getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(int fournisseur) {
		this.fournisseur = fournisseur;
	}

	
	public String getTypent_code() {
		return typent_code;
	}



	public void setTypent_code(String typent_code) {
		this.typent_code = typent_code;
	}



	public int getCommercial_id() {
		return commercial_id;
	}

	public void setCommercial_id(int commercial_id) {
		this.commercial_id = commercial_id;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "Prospection [id=" + id + ", name=" + name + ", firstname="
				+ firstname + ", lastname=" + lastname + ", particulier="
				+ particulier + ", civility_id=" + civility_id + ", address="
				+ address + ", zip=" + zip + ", town=" + town + ", status="
				+ status + ", country_id=" + country_id + ", country_code="
				+ country_code + ", country=" + country + ", phone=" + phone
				+ ", fax=" + fax + ", email=" + email + ", idprof1=" + idprof1
				+ ", idprof2=" + idprof2 + ", idprof3=" + idprof3
				+ ", idprof4=" + idprof4 + ", tva_assuj=" + tva_assuj
				+ ", capital=" + capital + ", typent_id=" + typent_id
				+ ", typent_code=" + typent_code + ", effectif_id="
				+ effectif_id + ", forme_juridique_code="
				+ forme_juridique_code + ", client=" + client + ", prospect="
				+ prospect + ", fournisseur=" + fournisseur
				+ ", commercial_id=" + commercial_id + ", parent=" + parent
				+ ", latitude=" + latitude + ", langitude=" + langitude
				+ ", idpros=" + idpros + "]";
	}



	public int getIdpros() {
		return idpros;
	}



	public void setIdpros(int idpros) {
		this.idpros = idpros;
	}

	
	
}
