package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;


public class BordereauGps implements Serializable{

	private int id;
	private String numero;
	
	private String lat;
	private String lng;
	
	
	public BordereauGps() {
	}


	public BordereauGps(int id, String numero, String lat, String lng) {
		super();
		this.id = id;
		this.numero = numero;
		this.lat = lat;
		this.lng = lng;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNumero() {
		return numero;
	}


	public void setNumero(String numero) {
		this.numero = numero;
	}


	public String getLat() {
		return lat;
	}


	public void setLat(String lat) {
		this.lat = lat;
	}


	public String getLng() {
		return lng;
	}


	public void setLng(String lng) {
		this.lng = lng;
	}


	@Override
	public String toString() {
		return "FactureGps [id=" + id + ", numero=" + numero + ", lat=" + lat
				+ ", lng=" + lng + "]";
	}

}
