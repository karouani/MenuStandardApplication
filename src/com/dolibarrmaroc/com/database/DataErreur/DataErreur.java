package com.dolibarrmaroc.com.database.DataErreur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dolibarrmaroc.com.models.*;


public class DataErreur implements Serializable{

	private List<Prospection> pros = new ArrayList<>();
	private List<Myinvoice> invo = new ArrayList<>();
	private List<Reglement> lsreg = new ArrayList<>();
	private List<MyGpsInvoice> msgps = new ArrayList<>();
	private List<Commande> cmd = new ArrayList<>();
	private List<Commandeview> cmdview = new ArrayList<>();
	
	public DataErreur() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DataErreur(List<Prospection> pros, List<Myinvoice> invo,
			List<Reglement> lsreg) {
		super();
		this.pros = pros;
		this.invo = invo;
		this.lsreg = lsreg;
	}
	public List<Prospection> getPros() {
		return pros;
	}
	public void setPros(List<Prospection> pros) {
		this.pros = pros;
	}
	public List<Myinvoice> getInvo() {
		return invo;
	}
	public void setInvo(List<Myinvoice> invo) {
		this.invo = invo;
	}
	public List<Reglement> getLsreg() {
		return lsreg;
	}
	public void setLsreg(List<Reglement> lsreg) {
		this.lsreg = lsreg;
	}
	@Override
	public String toString() {
		return "DataErreur [pros=" + pros + ", invo=" + invo + ", lsreg="
				+ lsreg + "]";
	}
	public List<MyGpsInvoice> getMsgps() {
		return msgps;
	}
	public void setMsgps(List<MyGpsInvoice> msgps) {
		this.msgps = msgps;
	}
	public List<Commande> getCmd() {
		return cmd;
	}
	public void setCmd(List<Commande> cmd) {
		this.cmd = cmd;
	}
	public List<Commandeview> getCmdview() {
		return cmdview;
	}
	public void setCmdview(List<Commandeview> cmdview) {
		this.cmdview = cmdview;
	}
	
	
}
