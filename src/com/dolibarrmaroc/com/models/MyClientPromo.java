package com.dolibarrmaroc.com.models;

import java.io.Serializable;
import java.util.List;

public class MyClientPromo implements Serializable{

	private int codecli;
	private List<Promotion> promo;
	public MyClientPromo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MyClientPromo(int codecli, List<Promotion> promo) {
		super();
		this.codecli = codecli;
		this.promo = promo;
	}
	public int getCodecli() {
		return codecli;
	}
	public void setCodecli(int codecli) {
		this.codecli = codecli;
	}
	public List<Promotion> getPromo() {
		return promo;
	}
	public void setPromo(List<Promotion> promo) {
		this.promo = promo;
	}
	@Override
	public String toString() {
		return "MyClientPromo [codecli=" + codecli + ", promo=" + promo + "]";
	}
}
