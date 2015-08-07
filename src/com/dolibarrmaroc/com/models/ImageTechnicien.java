package com.dolibarrmaroc.com.models;

public class ImageTechnicien {
	
	private int id;
	private String name;
	private String imageCode;
	
	public ImageTechnicien() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ImageTechnicien(int id, String name, String imageCode) {
		super();
		this.id = id;
		this.name = name;
		this.imageCode = imageCode;
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

	public String getImageCode() {
		return imageCode;
	}

	public void setImageCode(String imageCode) {
		this.imageCode = imageCode;
	}


	@Override
	public String toString() {
		return "ImageTechnicien [id=" + id + ", name=" + name +  "]";
	}
}
