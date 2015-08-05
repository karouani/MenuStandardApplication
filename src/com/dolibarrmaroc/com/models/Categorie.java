package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Categorie implements Serializable{

	private int rowid;
	private int fkparent;
	private String label;
	private String description;
	private String fullpath;
	private String vignette;
	private String url;
	private String photo;
	private List<Long> lsprods = new ArrayList<>();
	private List<Long> child = new ArrayList<>();
	private List<Produit> products = new ArrayList<>();
	public Categorie() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Categorie(int rowid, int fkparent, String label, String description,
			String fullpath, String vignette, String url, String photo,List<Long> child) {
		super();
		this.rowid = rowid;
		this.fkparent = fkparent;
		this.label = label;
		this.description = description;
		this.fullpath = fullpath;
		this.vignette = vignette;
		this.url = url;
		this.photo = photo;
		this.lsprods = lsprods;
		this.child = child;
	}
	
	public Categorie(int rowid, int fkparent, String label, String description,
			String fullpath, String vignette, String url, String photo,
			List<Produit> lsprods,List<Long> child) {
		super();
		this.rowid = rowid;
		this.fkparent = fkparent;
		this.label = label;
		this.description = description;
		this.fullpath = fullpath;
		this.vignette = vignette;
		this.url = url;
		this.photo = photo;
		this.products = lsprods;
		this.child = child;
	}
	public int getRowid() {
		return rowid;
	}
	public void setRowid(int rowid) {
		this.rowid = rowid;
	}
	public int getFkparent() {
		return fkparent;
	}
	public void setFkparent(int fkparent) {
		this.fkparent = fkparent;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFullpath() {
		return fullpath;
	}
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}
	public String getVignette() {
		return vignette;
	}
	public void setVignette(String vignette) {
		this.vignette = vignette;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public List<Long> getLsprods() {
		return lsprods;
	}
	public void setLsprods(List<Long> lsprods) {
		this.lsprods = lsprods;
	}
	public List<Long> getChild() {
		return child;
	}
	public void setChild(List<Long> child) {
		this.child = child;
	}
	@Override
	public String toString() {
		return "Categorie [rowid=" + rowid + ", fkparent=" + fkparent
				+ ", label=" + label + ", description=" + description
				+ ", fullpath=" + fullpath + ", vignette=" + vignette
				+ ", url=" + url + ", photo=" + photo + ", lsprods=" + lsprods
				+ ", child=" + child + "]";
	}
	public List<Produit> getProducts() {
		return products;
	}
	public void setProducts(List<Produit> products) {
		this.products = products;
	}
	
	
}
