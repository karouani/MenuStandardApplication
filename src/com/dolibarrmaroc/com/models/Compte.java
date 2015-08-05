package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Compte implements Serializable{

	private int id;
	private String login;
	private String password;
	private String profile;
	private int activer;
	private String message;
	
	private String iduser;
	private String emei;
	private String step;
	private String stop;
	
	private String level;
	private int permission;
	private int permissionPayement;
	private int permissionbl;
	
	private int souvenue;
	private long date;
	
	public Compte() {
	}

	public Compte(int id, String login, String password, String profile,
			int activer, String message, String iduser, String emei,
			String step, String stop, String level) {
		super();
		this.id = id;
		this.login = login;
		this.password = password;
		this.profile = profile;
		this.activer = activer;
		this.message = message;
		this.iduser = iduser;
		this.emei = emei;
		this.step = step;
		this.stop = stop;
		this.level = level;
	}
	
	public Compte(int id, String login, String password, int souvenue, long date,String profile) {
		super();
		this.id = id;
		this.login = login;
		this.password = password;
		this.souvenue = souvenue;
		this.date = date;
		this.profile = profile;
	}


	public int getId() {
		return id;
	}

	
	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public int getActiver() {
		return activer;
	}

	public void setActiver(int activer) {
		this.activer = activer;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	
	public int getPermissionPayement() {
		return permissionPayement;
	}

	public void setPermissionPayement(int permissionPayement) {
		this.permissionPayement = permissionPayement;
	}

	
	
	
	public int getSouvenue() {
		return souvenue;
	}

	public void setSouvenue(int souvenue) {
		this.souvenue = souvenue;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Compte [id=" + id + ", login=" + login + ", password="
				+ password + ", profile=" + profile + ", activer=" + activer
				+ ", message=" + message + ", iduser=" + iduser + ", emei="
				+ emei + ", step=" + step + ", stop=" + stop + ", level="
				+ level + ", permission=" + permission
				+ ", permissionPayement=" + permissionPayement
				+ ", permissionbl=" + permissionbl + ", souvenue=" + souvenue
				+ ", date=" + date + "]";
	}

	public int getPermissionbl() {
		return permissionbl;
	}

	public void setPermissionbl(int permissionbl) {
		this.permissionbl = permissionbl;
	}

	
}
