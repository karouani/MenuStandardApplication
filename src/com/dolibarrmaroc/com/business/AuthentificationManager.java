package com.dolibarrmaroc.com.business;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ConfigGps;
import com.dolibarrmaroc.com.models.Services;


public interface AuthentificationManager {

	public Compte login(String login,String password);
	public ConfigGps getGpsConfig();
	public Services getService(String login, String password);
}
