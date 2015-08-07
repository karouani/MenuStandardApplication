package com.dolibarrmaroc.com.dao;

import java.util.List;
import java.util.Map;

import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Remises;

public interface CategorieDao {

	public List<Categorie> LoadCategories(Compte cp);
	
}
