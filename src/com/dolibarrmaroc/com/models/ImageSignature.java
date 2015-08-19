package com.dolibarrmaroc.com.models;

import java.io.Serializable;


public class ImageSignature implements Serializable{

	private int id;
	private String name;
	private String url;
	private GpsTracker gpsImage;

	public ImageSignature() {
	}

	public ImageSignature(String name, String url, GpsTracker gpsImage) {
		super();
		this.name = name;
		this.url = url;
		this.gpsImage = gpsImage;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public GpsTracker getGpsImage() {
		return gpsImage;
	}

	public void setGpsImage(GpsTracker gpsImage) {
		this.gpsImage = gpsImage;
	}

	@Override
	public String toString() {
		return "ImageSignature [id=" + id + ", name=" + name + ", url=" + url
				+ ", gpsImage=" + gpsImage + "]";
	}
	
}
