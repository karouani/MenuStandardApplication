package com.dolibarrmaroc.com.dao;

import java.util.List;

import com.dolibarrmaroc.com.models.Compte;

public interface DeniedDataDao {

	public int sendMyErrorData(List<String> in,Compte cp);
}
