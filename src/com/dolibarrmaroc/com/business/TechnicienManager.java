package com.dolibarrmaroc.com.business;

import java.util.List;

import com.dolibarrmaroc.com.models.BordereauGps;
import com.dolibarrmaroc.com.models.BordreauIntervention;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ImageTechnicien;
import com.dolibarrmaroc.com.models.Services;

public interface TechnicienManager {
	
	public String insertBordereau(BordreauIntervention bi,Compte c);
	public String insertBordereauoff(BordreauIntervention bi, Compte c);
	public List<BordereauGps> selectAllBordereau(Compte c);
	
	public List<Services> allServices(Compte c);
	public void inesrtImage(List<ImageTechnicien> imgs,String lien);

	public List<Client> selectAllClient(Compte c);
}
