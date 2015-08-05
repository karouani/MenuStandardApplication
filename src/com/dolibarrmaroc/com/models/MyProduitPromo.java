package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyProduitPromo implements Serializable{

	private int id;
	private List<Promotion> pro = new ArrayList<>();
	
	public List<Promotion> getPro() {
		return pro;
	}
	public void setPro(List<Promotion> pro) {
		this.pro = pro;
	}
	public MyProduitPromo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MyProduitPromo(int id, List<Promotion> pro) {
		super();
		this.id = id;
		this.pro = pro;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "MyProduitPromo [id=" + id + ", pro=" + pro + "]";
	}
}
