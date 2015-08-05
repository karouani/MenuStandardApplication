package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class MyGpsInvoice implements Serializable{

	private GpsTracker gps;
	private String imei;
	private String num;
	private String battery;
	private Compte c;
	private String fact;
	public MyGpsInvoice() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MyGpsInvoice(GpsTracker gps, String imei, String num,
			String battery, Compte c, String fact) {
		super();
		this.gps = gps;
		this.imei = imei;
		this.num = num;
		this.battery = battery;
		this.c = c;
		this.fact = fact;
	}
	public GpsTracker getGps() {
		return gps;
	}
	public void setGps(GpsTracker gps) {
		this.gps = gps;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public Compte getC() {
		return c;
	}
	public void setC(Compte c) {
		this.c = c;
	}
	public String getFact() {
		return fact;
	}
	public void setFact(String fact) {
		this.fact = fact;
	}
	@Override
	public String toString() {
		return "MyGpsInvoice [gps=" + gps + ", imei=" + imei + ", num=" + num
				+ ", battery=" + battery + ", c=" + c + ", fact=" + fact + "]";
	}
	
}
