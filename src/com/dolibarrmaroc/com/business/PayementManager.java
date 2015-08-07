package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Reglement;

public interface PayementManager {
	public List<Payement> getFactures(Compte c);
	public String insertPayement(Reglement reg,Compte c);
}
