package com.dolibarrmaroc.com.dao;

import java.util.List;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Prospection;
import com.dolibarrmaroc.com.models.Societe;
import com.dolibarrmaroc.com.models.Societe;

public interface CommercialDao {
	public String insert(Compte c,Prospection prospect);
	public String update(Compte c,Prospection prospect);
	public ProspectData getInfos(Compte c);
	public List<Societe> getAll(Compte c);
}
