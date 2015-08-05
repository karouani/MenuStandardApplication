package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;

public class Services implements Serializable{

	private int id;
	private String service;
	private int nmb_cmp;
	private List<LabelService> labels;
		
	public Services() {
	}

	public Services(String service, int nmb_cmp, List<LabelService> labels) {
		super();
		this.service = service;
		this.nmb_cmp = nmb_cmp;
		this.labels = labels;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public int getNmb_cmp() {
		return nmb_cmp;
	}

	public void setNmb_cmp(int nmb_cmp) {
		this.nmb_cmp = nmb_cmp;
	}

	public List<LabelService> getLabels() {
		return labels;
	}

	public void setLabels(List<LabelService> labels) {
		this.labels = labels;
	}

	@Override
	public String toString() {
		return "Services [id=" + id + ", service=" + service + ", nmb_cmp="
				+ nmb_cmp + ", labels=" + labels + "]";
	}

}
