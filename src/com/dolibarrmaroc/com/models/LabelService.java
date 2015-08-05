package com.dolibarrmaroc.com.models;

public class LabelService {

	private int id;
	private String label;
	
	public LabelService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LabelService(String label) {
		super();
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "LabelService [id=" + id + ", label=" + label + "]";
	}
	
}
