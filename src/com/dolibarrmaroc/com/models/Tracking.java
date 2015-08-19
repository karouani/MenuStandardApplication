package com.dolibarrmaroc.com.models;

public class Tracking {

	private int id;
	private String url;
	private String stop;
	private String step;
	
	private Compte compte;
	
	public Tracking() {
	}

	public Tracking(String url, String stop, String step,
			Compte compte) {
		super();
		this.url = url;
		this.stop = stop;
		this.step = step;
		this.compte = compte;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	

	public Compte getCompte() {
		return compte;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	@Override
	public String toString() {
		return "Tracking [id=" + id + ", url=" + url + ", stop=" + stop
				+ ", step=" + step + ", compte="
				+ compte + "]";
	}

}
