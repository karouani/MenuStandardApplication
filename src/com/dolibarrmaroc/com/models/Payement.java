package com.dolibarrmaroc.com.models;

import java.io.Serializable;

public class Payement implements Serializable{
	
	//"rowid":"2095","facnumber":"2015-02436","amount":1000,"total_ttc":"2700.00000000"
	private int id;
	private String num;
	private double amount;
	private double total;
	private int soc;
	
	public Payement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Payement(int id, String num, double amount, double total) {
		super();
		this.id = id;
		this.num = num;
		this.amount = amount;
		this.total = total;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Payement [id=" + id + ", num=" + num + ", amount=" + amount
				+ ", total=" + total + ", soc=" + soc + "]";
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
	}
	
}
