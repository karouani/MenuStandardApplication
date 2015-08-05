package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.LoadStock;
import com.dolibarrmaroc.com.models.Mouvement;

public interface MouvementManager {
	public LoadStock laodStock(Compte cp);
	public String makemouvement(List<Mouvement> mvs,Compte cp,String label);
	public String makeechange(List<Mouvement> mvs, Compte cp, String label,String clt);
}
