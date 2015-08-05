package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class ConfigGps implements Serializable{
	
	private int id;
	private String iduser;
	private String emei;
	private String step;
	private String stop;
	private String level;
	
	
	public ConfigGps() {
		super();
	}


	public ConfigGps(int id, String iduser, String emei, String step,
			String stop, String level) {
		super();
		this.id = id;
		this.iduser = iduser;
		this.emei = emei;
		this.step = step;
		this.stop = stop;
		this.level = level;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getIduser() {
		return iduser;
	}


	public void setIduser(String iduser) {
		this.iduser = iduser;
	}


	public String getEmei() {
		return emei;
	}


	public void setEmei(String emei) {
		this.emei = emei;
	}


	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}


	public String getStop() {
		return stop;
	}


	public void setStop(String stop) {
		this.stop = stop;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	@Override
	public String toString() {
		return "ConfigGps [id=" + id + ", iduser=" + iduser + ", emei=" + emei
				+ ", step=" + step + ", stop=" + stop + ", level=" + level
				+ "]";
	}

	
}
