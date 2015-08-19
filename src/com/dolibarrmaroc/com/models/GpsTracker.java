package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsTracker implements Serializable{

	private int id;
	private String latitude;
	private String langitude;
	private String dateString ;
	
	private float speed ;
	private double altitude;
	private float direction;
	private String satellite;
	
	public GpsTracker() {
	}

	public GpsTracker(int id, String latitude, String langitude, String dateString, float speed,
			double altitude, float direction, String satellite) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.langitude = langitude;
		this.dateString = dateString;
		this.speed = speed;
		this.altitude = altitude;
		this.direction = direction;
		this.satellite = satellite;
	}

	public GpsTracker(String latitude, String langitude,String dateString, float speed,
			double altitude, float direction, String satellite) {
		super();
		this.latitude = latitude;
		this.langitude = langitude;
		this.dateString = dateString;
		this.speed = speed;
		this.altitude = altitude;
		this.direction = direction;
		this.satellite = satellite;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLangitude() {
		return langitude;
	}

	public void setLangitude(String langitude) {
		this.langitude = langitude;
	}

	

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public String getSatellite() {
		return satellite;
	}

	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}

	@Override
	public String toString() {
		return "GpsTracker [id=" + id + ", latitude=" + latitude
				+ ", langitude=" + langitude + ", dateString=" + dateString + ", speed=" + speed
				+ ", altitude=" + altitude + ", direction=" + direction
				+ ", satellite=" + satellite + "]";
	}

	
}
