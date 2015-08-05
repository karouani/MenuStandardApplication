package com.dolibarrmaroc.com.models;

import java.io.Serializable;

import android.content.Intent;

public class AlertDialog implements Serializable{
	
	private Intent intent;
	private String label;
	private String image;
	
	public AlertDialog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AlertDialog(Intent intent, String label, String image) {
		super();
		this.intent = intent;
		this.label = label;
		this.image = image;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "AlertDialog [intent=" + intent + ", label=" + label
				+ ", image=" + image + "]";
	}
}
