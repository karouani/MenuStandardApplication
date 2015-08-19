package com.dolibarrmaroc.com.models;

import java.util.List;

public class BordreauIntervention {

	private int id;
	private String id_clt;
	private String author;
	private String date_c;
	private String duree;
	private String status;
	private String description;
	private String objet;
	private String heurD;
	private String minD;
	private String year;
	private String month;
	private String day;
	private String nmclt;
	
	private List<ImageTechnicien> imgs;
	private Compte compte;
	
	public BordreauIntervention() {
		super();
	}

	public BordreauIntervention(String id_clt, String author, String date_c,
			String duree, String status, String description) {
		super();
		this.id_clt = id_clt;
		this.author = author;
		this.date_c = date_c;
		this.duree = duree;
		this.status = status;
		this.description = description;
	}

	
	public String getHeurD() {
		return heurD;
	}

	public void setHeurD(String heurD) {
		this.heurD = heurD;
	}

	public String getMinD() {
		return minD;
	}

	public void setMinD(String minD) {
		this.minD = minD;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getObjet() {
		return objet;
	}

	public void setObjet(String objet) {
		this.objet = objet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getId_clt() {
		return id_clt;
	}

	public void setId_clt(String id_clt) {
		this.id_clt = id_clt;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate_c() {
		return date_c;
	}

	public void setDate_c(String date_c) {
		this.date_c = date_c;
	}

	public String getDuree() {
		return duree;
	}

	public void setDuree(String duree) {
		this.duree = duree;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public List<ImageTechnicien> getImgs() {
		return imgs;
	}

	public void setImgs(List<ImageTechnicien> imgs) {
		this.imgs = imgs;
	}

	@Override
	public String toString() {
		return "BordreauIntervention [id=" + id + ", id_clt=" + id_clt
				+ ", author=" + author + ", date_c=" + date_c + ", duree="
				+ duree + ", status=" + status + ", description=" + description
				+ ", objet=" + objet + ", heurD=" + heurD + ", minD=" + minD
				+ ", year=" + year + ", month=" + month + ", day=" + day
				+ ", nmclt=" + nmclt + ", imgs=" + imgs + ", compte=" + compte
				+ "]";
	}

	public Compte getCompte() {
		return compte;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	public String getNmclt() {
		return nmclt;
	}

	public void setNmclt(String nmclt) {
		this.nmclt = nmclt;
	}

	
}
