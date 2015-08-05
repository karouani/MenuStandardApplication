package com.dolibarrmaroc.com.database.DataErreur;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dolibarrmaroc.com.models.Produit;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class StockVirtual extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "storagevirtual";
 
    //  tableS name
    private static final String TABLE_PROD = "storageprod";
    
 
    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_REF = "ref";
    private static final String KEY_QTE = "qte";
    private static final String KEY_TYPE = "typeprd";
 
    public StockVirtual(Context context) {
        super(context,Environment.getExternalStorageDirectory()+"/.datadolicache/"+DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	try {
    		String CREATE_factures_TABLE = "CREATE TABLE " + TABLE_PROD + "("
    				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_REF + " INTEGER , "+KEY_QTE+" INTEGER, "+KEY_TYPE+" VARCHAR(30) )";
    		db.execSQL(CREATE_factures_TABLE);

    	} catch (Exception e) {
    		// TODO: handle exception
    		Log.e("erreur ","creation data table");
    	}
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try {
    		// Drop older table if existed
    		// Create tables again
    		onCreate(db);
    	} catch (Exception e) {
    		// TODO: handle exception
    		Log.e("update","upgrade base");
    	}
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new 
    public long addrow(String tb,int pd,int qte,String tp) {
    	long id =-1;
        try {
        	SQLiteDatabase db = this.getWritableDatabase();
        	 
            
            ContentValues values = new ContentValues();
            values.put(KEY_REF, pd);
            values.put(KEY_QTE, qte);
            values.put(KEY_TYPE, tp);
     
            // Inserting Row
            id = db.insert(TABLE_PROD, null, values);
           
            db.close(); // Closing database connection
            
            
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("insert data","insert data");
		}
        return id;
    }
 
    
    public long cleantables(String tb){
    	long id =-1;
    	try {
    		SQLiteDatabase db = this.getWritableDatabase();
    		 db.execSQL("delete from "+TABLE_PROD);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return id;
    }
    
    public int allresults(String tb){
    	int id =-1;
    	try {
    		 String selectQuery = "SELECT  * FROM ";
    		 
    		 // selectQuery += TABLE_REGLEMENT;
    		 
    	        SQLiteDatabase db = this.getWritableDatabase();
    	        Cursor cursor = db.rawQuery(selectQuery, null);
    	 
    	        id = cursor.getCount();
    	        
    	       
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return id;
    }
    /*
    // Getting single 
    Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return 
        return contact;
    }
     
    // Getting All 
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding  to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        // return  list
        return contactList;
    }
 
    // Updating single 
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());
 
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
 
    // Deleting single 
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
    */
    public List<Produit> getAllProduits() {
        List<Produit> contactList = new ArrayList<Produit>();
        // Select All Query
       try {
    	   String selectQuery = "SELECT  * FROM " + TABLE_PROD;
    	   
           SQLiteDatabase db = this.getWritableDatabase();
           Cursor cursor = db.rawQuery(selectQuery, null);
    
           // looping through all rows and adding to list
           if (cursor.moveToFirst()) {
               do {
               	contactList.add(new Produit(""+cursor.getInt(1), cursor.getString(3), cursor.getInt(2), "", -2, 0, "", ""));
               } while (cursor.moveToNext());
           }
	} catch (Exception e) {
		// TODO: handle exception
		contactList = new ArrayList<Produit>();
	}
 
        // return  list
        return contactList;
    }
 
}
