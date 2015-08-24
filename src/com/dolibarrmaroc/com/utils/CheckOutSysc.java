package com.dolibarrmaroc.com.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dolibarrmaroc.com.business.CommandeManager;
import com.dolibarrmaroc.com.business.CommercialManager;
import com.dolibarrmaroc.com.business.MouvementManager;
import com.dolibarrmaroc.com.business.PayementManager;
import com.dolibarrmaroc.com.business.VendeurManager;
import com.dolibarrmaroc.com.dao.CategorieDao;
import com.dolibarrmaroc.com.dao.CategorieDaoMysql;
import com.dolibarrmaroc.com.dashboard.HomeActivity;
import com.dolibarrmaroc.com.database.StockVirtual;
import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.CategorieCustomer;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Commandeview;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.Dictionnaire;
import com.dolibarrmaroc.com.models.LoadStock;
import com.dolibarrmaroc.com.models.Payement;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.models.Promotion;
import com.dolibarrmaroc.com.models.ProspectData;
import com.dolibarrmaroc.com.models.Societe;
import com.dolibarrmaroc.com.offline.ioffline;

import android.R.integer;
import android.content.Context;
import android.util.Log;

public class CheckOutSysc implements Serializable{

	
	/**************************  get Data from Dolibarr ****************************************/
	public static List<Produit> checkOutProducts(VendeurManager vendeurManager,Compte c){
		return vendeurManager.selectAllProduct(c);
	}
	
	public static Dictionnaire checkOutDictionnaire(VendeurManager vendeurManager,Compte c){
		return vendeurManager.getDictionnaire();
	}
	
	public static List<Client> checkOutClient(VendeurManager vendeurManager,Compte c){
		return  vendeurManager.selectAllClient(c);
	}
	
	public static HashMap<Integer, HashMap<Integer, Promotion>> checkOutPromotionProduits(VendeurManager vendeurManager,Compte c){
		return  vendeurManager.getPromotionProduits();
	}
	
	public static HashMap<Integer, List<Integer>> checkOutPromotionClients(VendeurManager vendeurManager,Compte c){
		return vendeurManager.getPromotionClients();
	}
	
	public static ProspectData checkOutCommercialInfo(CommercialManager comercialmanager,Compte c){
		return comercialmanager.getInfos(c);
	}
	
	public static List<Payement> checkOutPayement(PayementManager payemnmanager,Compte c){
		return payemnmanager.getFactures(c);
	}
	
	public static List<CategorieCustomer> checkOutClientSecteur(VendeurManager vendeurManager,Compte c){
		return  vendeurManager.getAllCategorieCustomer(c);
	}
	
	public static List<Commandeview> checkOutCommandes(CommandeManager comamndemanager,Compte c){
		return comamndemanager.charger_commandes(c);
	}
	
	public static List<Categorie> checkOutCatalogueProduit(CategorieDao categorie,Compte c){
		return categorie.LoadCategories(c);
	}
	
	public static List<Societe> checkOutAllSociete(CommercialManager comercialmanager,Compte c){
		return comercialmanager.getAll(c);
	}
	
	public static LoadStock checkOutStock(MouvementManager stockManager,Compte c){
		return stockManager.laodStock(c);
	}
	
	/**************************  Set Data into cache mobile ****************************************/
	public static long checkInProductsPromotion(ioffline myoffline,Compte c,List<Produit> prod,HashMap<Integer, HashMap<Integer, Promotion>> promo){
		long sysnbr = 0;
		myoffline.CleanProduits();
		myoffline.CleanPromotionProduit();
		sysnbr += myoffline.shynchronizeProduits(prod);
		sysnbr += myoffline.shynchronizePromotion(promo);
		
		return sysnbr;
	}
	
	public static long checkInClientsPromotion(ioffline myoffline,Compte c,List<Client> clts,HashMap<Integer, List<Integer>> clt){
		long sysnbr = 0;
		myoffline.CleanClients();
		myoffline.CleanPromotionClient();
		sysnbr += myoffline.shynchronizeClients(clts);
		sysnbr += myoffline.shynchronizePromotionClient(clt);
		
		return sysnbr;
	}
	
	public static long checkInDictionnaire(ioffline myoffline,Dictionnaire dico){
		myoffline.CleanDico();
		return myoffline.shynchronizeDico(dico);
	}
	
	public static long checkInCommercialInfo(ioffline myoffline,ProspectData data,Compte c){
		myoffline.CleanProspectData();
		return myoffline.shynchronizeProspect(data);
	}
	
	public static long checkInPayement(ioffline myoffline,List<Payement>  data,Compte c){
		if(data.size() > 0){
			myoffline.CleanPayement();
			return myoffline.shynchronizePayement(data);
		}
		return 0;
	}
	
	public static long checkInClientSecteur(ioffline myoffline,List<CategorieCustomer> lscat,Compte c){
		if(lscat.size() > 0){
			myoffline.CleanCategorieClients();
			for (int i = 0; i < lscat.size(); i++) {
				myoffline.shnchronizeCategorieClients(lscat.get(i), c);
			}
		}
		return 0;
	}
	
	public static long checkInCommandeview(ioffline myoffline,List<Commandeview>  data,Compte c){
		if(data.size() > 0){
			myoffline.shynchronizeCommandeList(data);
		}
		return 0;
	}
	
	public static long checkInCatalogueProduit(ioffline myoffline,List<Categorie> lscats,Compte c){
		if(lscats.size() > 0){
			myoffline.shynchronizeCategoriesList(lscats);
		}
		return 0;
	}
	
	public static long checkInSocietes(ioffline myoffline,List<Societe> lscats,Compte c){
		if(lscats.size() > 0){
			myoffline.shnchronizeSocietesClients(lscats, c);
		}
		return 0;
	}
	
	
	/******************************  Reload data **************************************************************/
	/*
	 * in : 0 == all // 1 == without payement // 2 == only products  // 3 == only clients  // 4 == catalogue // 5 == payement
	 */
	public static HashMap<String, Integer> ReloadProdClt(Context context,ioffline myoffline,Compte compte,VendeurManager vendeurManager,PayementManager payemnmanager,StockVirtual sv,CategorieDao categorie,CommandeManager managercmd,int in,CommercialManager manager){
		
		int nbprod =0,nbclt =0,nbpay =0;
		HashMap<String, Integer> res = new HashMap<>();
		
		List<Client> clients = new ArrayList<>();
		List<Produit> products = new ArrayList<>();
		List<Payement> paym = new ArrayList<>();
		try {
			
			switch (in) {
			case 0:
				products = new ArrayList<>();
				products =  checkOutProducts(vendeurManager, compte);//   vendeurManager.selectAllProduct(compte);
				
				clients = new ArrayList<>();
				clients = checkOutClient(vendeurManager, compte); //   vendeurManager.selectAllClient(compte);

				
					if(products.size() > 0){
						nbprod = products.size();
						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
								if(sv.getAllProduits(-1).get(j).getRef().equals(products.get(i).getId()+"")){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
								}
							}
						}
						checkInProductsPromotion(myoffline, compte, products, vendeurManager.getPromotionProduits());
					} 

					
					
					if(clients.size() > 0){
						nbclt =clients.size(); 
						checkInClientsPromotion(myoffline, compte, clients, vendeurManager.getPromotionClients());
					}
					
					
					if(compte.getPermissionbl() == 1){

						List<Categorie> lscats = checkOutCatalogueProduit(categorie, compte);

						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
								//Log.e(products.get(i).getId()+"",sv.getAllProduits().get(j).getRef());
								if(Integer.parseInt(sv.getAllProduits(-1).get(j).getRef()) == products.get(i).getId()){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
								}
							}
						}

						for (int j = 0; j < lscats.size(); j++) {
							for (int i = 0; i < lscats.get(j).getProducts().size(); i++) {
								for (int k = 0; k < products.size(); k++) {
									if(lscats.get(j).getProducts().get(i).getId() == products.get(k).getId()){
										lscats.get(j).getProducts().get(i).setQteDispo(products.get(k).getQteDispo());
									}
								}
							}
						}

						if(lscats.size() > 0){
							checkInCatalogueProduit(myoffline, lscats, compte);
						}

						checkInCommandeview(myoffline, checkOutCommandes(managercmd, compte), compte);

					}
					
					
					checkInDictionnaire(myoffline, checkOutDictionnaire(vendeurManager, compte));
					
					
					paym = new ArrayList<>();
					paym = checkOutPayement(payemnmanager, compte);
					
					checkInPayement(myoffline, paym, compte);
					nbpay = paym.size();
					
				break;

			case 1:
				products = new ArrayList<>();
				products =  checkOutProducts(vendeurManager, compte);//   vendeurManager.selectAllProduct(compte);
				
				clients = new ArrayList<>();
				clients = checkOutClient(vendeurManager, compte); //   vendeurManager.selectAllClient(compte);

				
					if(products.size() > 0){
						nbprod = products.size();
						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
								if(sv.getAllProduits(-1).get(j).getRef().equals(products.get(i).getId()+"")){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
								}
							}
						}
						checkInProductsPromotion(myoffline, compte, products, vendeurManager.getPromotionProduits());
					} 

					
					
					if(clients.size() > 0){
						nbclt =clients.size(); 
						checkInClientsPromotion(myoffline, compte, clients, vendeurManager.getPromotionClients());
					}
					
					
					if(compte.getPermissionbl() == 1){

						List<Categorie> lscats = checkOutCatalogueProduit(categorie, compte);

						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
								//Log.e(products.get(i).getId()+"",sv.getAllProduits().get(j).getRef());
								if(Integer.parseInt(sv.getAllProduits(-1).get(j).getRef()) == products.get(i).getId()){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
								}
							}
						}

						for (int j = 0; j < lscats.size(); j++) {
							for (int i = 0; i < lscats.get(j).getProducts().size(); i++) {
								for (int k = 0; k < products.size(); k++) {
									if(lscats.get(j).getProducts().get(i).getId() == products.get(k).getId()){
										lscats.get(j).getProducts().get(i).setQteDispo(products.get(k).getQteDispo());
									}
								}
							}
						}

						if(lscats.size() > 0){
							checkInCatalogueProduit(myoffline, lscats, compte);
						}

						checkInCommandeview(myoffline, checkOutCommandes(managercmd, compte), compte);

					}
					
					
					checkInDictionnaire(myoffline, checkOutDictionnaire(vendeurManager, compte));
					
				break;
				
				case 2:
					products = new ArrayList<>();
					products =  checkOutProducts(vendeurManager, compte);//   vendeurManager.selectAllProduct(compte);
					
					
						if(products.size() > 0){
							nbprod = products.size();
							for (int i = 0; i < products.size(); i++) {
								for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
									if(sv.getAllProduits(-1).get(j).getRef().equals(products.get(i).getId()+"")){
										products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
									}
								}
							}
							checkInProductsPromotion(myoffline, compte, products, vendeurManager.getPromotionProduits());
						} 
						checkInDictionnaire(myoffline, checkOutDictionnaire(vendeurManager, compte));
					break;
				
				case 3:
					clients = new ArrayList<>();
					clients = checkOutClient(vendeurManager, compte); //   vendeurManager.selectAllClient(compte);
						
						if(clients.size() > 0){
							nbclt =clients.size(); 
							checkInClientsPromotion(myoffline, compte, clients, vendeurManager.getPromotionClients());
						}
						
					break;
				case 4:
					if(compte.getPermissionbl() == 1){

						List<Categorie> lscats = checkOutCatalogueProduit(categorie, compte);

						products = new ArrayList<>();
						products = myoffline.LoadProduits("");
						for (int i = 0; i < products.size(); i++) {
							for (int j = 0; j < sv.getAllProduits(-1).size(); j++) {
								//Log.e(products.get(i).getId()+"",sv.getAllProduits().get(j).getRef());
								if(Integer.parseInt(sv.getAllProduits(-1).get(j).getRef()) == products.get(i).getId()){
									products.get(i).setQteDispo(products.get(i).getQteDispo() - sv.getAllProduits(-1).get(j).getQteDispo());
								}
							}
						}

						for (int j = 0; j < lscats.size(); j++) {
							for (int i = 0; i < lscats.get(j).getProducts().size(); i++) {
								for (int k = 0; k < products.size(); k++) {
									if(lscats.get(j).getProducts().get(i).getId() == products.get(k).getId()){
										lscats.get(j).getProducts().get(i).setQteDispo(products.get(k).getQteDispo());
									}
								}
							}
						}

						if(lscats.size() > 0){
							checkInCatalogueProduit(myoffline, lscats, compte);
						}

						checkInCommandeview(myoffline, checkOutCommandes(managercmd, compte), compte);

					}
					break;
					
				case 5:
					paym = new ArrayList<>();
					paym = checkOutPayement(payemnmanager, compte);
					
					checkInPayement(myoffline, paym, compte);
					nbpay = paym.size();
					break;
			}
			
			
			/*
				List<Societe> lsosc = new ArrayList<>();
				lsosc = checkOutAllSociete(manager, compte);
				if(lsosc.size() > 0){
					checkInSocietes(myoffline, lsosc, compte);
				}
				
				checkInClientSecteur(myoffline, checkOutClientSecteur(vendeurManager, compte), compte);
			*/
			res.put("prod", nbprod);
			res.put("clt", nbclt);
			res.put("pay", nbpay);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.put("prod", nbprod);
			res.put("clt", nbclt);
			res.put("pay", nbpay);
		}
		
		return res;
	}
	
	
	/*********************** Reload basic data ***************************************/
	// in == 0 all else in == 1 only client
	public static int RelaodClientSectInfoCommDicto(Context context,ioffline myoffline,Compte compte,VendeurManager vendeurManager,CommercialManager manager,int in){
		try {
			switch (in) {
			case 0:
				List<Societe> lsosc = new ArrayList<>();
				lsosc = checkOutAllSociete(manager, compte);
				if(lsosc.size() > 0){
					checkInSocietes(myoffline, lsosc, compte);
				}
				
				checkInClientSecteur(myoffline, checkOutClientSecteur(vendeurManager, compte), compte);
				
				checkInDictionnaire(myoffline, checkOutDictionnaire(vendeurManager, compte));
				
				checkInCommercialInfo(myoffline, checkOutCommercialInfo(manager, compte), compte);
				
				
				break;

			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
	}
}
