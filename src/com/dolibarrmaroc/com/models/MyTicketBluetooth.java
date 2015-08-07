package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;

import com.dolibarrmaroc.com.utils.MyTicket;

public class MyTicketBluetooth implements Serializable{

	private long id;
	private String dt;
	private Myinvoice me;
	private MyTicket ticket;
	private List<PromoTicket> remises;
	public MyTicketBluetooth() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MyTicketBluetooth(String dt, Myinvoice me, MyTicket ticket,
			List<PromoTicket> remises) {
		super();
		this.dt = dt;
		this.me = me;
		this.ticket = ticket;
		this.remises = remises;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public Myinvoice getMe() {
		return me;
	}
	public void setMe(Myinvoice me) {
		this.me = me;
	}
	public MyTicket getTicket() {
		return ticket;
	}
	public void setTicket(MyTicket ticket) {
		this.ticket = ticket;
	}
	public List<PromoTicket> getRemises() {
		return remises;
	}
	public void setRemises(List<PromoTicket> remises) {
		this.remises = remises;
	}
	@Override
	public String toString() {
		return "MyTicketBluetooth [id=" + id + ", dt=" + dt + ", me=" + me
				+ ", ticket=" + ticket + ", remises=" + remises + "]";
	} 
}
