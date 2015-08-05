package com.dolibarrmaroc.com.dao;

import java.util.List;

import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Compte;

public interface CategorieDao {

	public List<Categorie> LoadCategories(Compte cp);
	
}
