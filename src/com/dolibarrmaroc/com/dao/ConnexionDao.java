package com.dolibarrmaroc.com.dao;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.MyTicketWitouhtProduct;
import com.dolibarrmaroc.com.models.Services;


public interface ConnexionDao {
	
	public Compte login(String login,String password);
	public ConfigGps getGpsConfig();
	public Services getService(String login, String password);
	public MyTicketWitouhtProduct lodSociete(String st);
}
