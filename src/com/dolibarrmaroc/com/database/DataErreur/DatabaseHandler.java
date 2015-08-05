package com.dolibarrmaroc.com.database.DataErreur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "storagegeocom";
 
    //  tableS name
    private static final String TABLE_FACTURE = "storagefactures";
    private static final String TABLE_REGLEMENT = "storagereglements";
    private static final String TABLE_CLIENT = "storageclients";
    private static final String TABLE_CMD = "storageccmd";
    
 
    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	try {
    		String CREATE_factures_TABLE = "CREATE TABLE " + TABLE_FACTURE + "("
    				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + ")";
    		db.execSQL(CREATE_factures_TABLE);

    		String CREATE_clients_TABLE = "CREATE TABLE " + TABLE_CLIENT + "("
    				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + ")";
    		db.execSQL(CREATE_clients_TABLE);

    		String CREATE_reglements_TABLE = "CREATE TABLE " + TABLE_REGLEMENT + "("
    				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + ")";
    		db.execSQL(CREATE_reglements_TABLE);
    		
    		String CREATE_cmd_TABLE = "CREATE TABLE " + TABLE_CMD + "("
    				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + ")";
    		db.execSQL(CREATE_cmd_TABLE);
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
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACTURE);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGLEMENT);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CMD);

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
    public long addrow(String tb) {
    	long id =-1;
        try {
        	SQLiteDatabase db = this.getWritableDatabase();
        	 
            
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, tb); // Contact Name
     
            // Inserting Row
            if(tb.equals("cmd")){
            	cleantables(tb);
            	id = db.insert(TABLE_CMD, null, values);
            }else if(tb.equals("clt")){
            	cleantables(tb);
            	id = db.insert(TABLE_CLIENT, null, values);
            }else if(tb.equals("fc")){
            	cleantables(tb);
            	 id = db.insert(TABLE_FACTURE, null, values);
            }else{
            	cleantables(tb);
            	 id = db.insert(TABLE_REGLEMENT, null, values);
            }
           
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
    		if(tb.equals("cmd")){
            	db.execSQL("delete from "+TABLE_CMD);
            }else if(tb.equals("clt")){
            	db.execSQL("delete from "+TABLE_CLIENT);
            }else if(tb.equals("fc")){
            	 db.execSQL("delete from "+TABLE_FACTURE);
            }else{
            	 db.execSQL("delete from "+TABLE_REGLEMENT);
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return id;
    }
    
    public int allresults(String tb){
    	int id =-1;
    	try {
    		 String selectQuery = "SELECT  * FROM ";
    		 
    		 if(tb.equals("cmd")){
    			 selectQuery += TABLE_CMD;
             }else if(tb.equals("clt")){
    			 selectQuery += TABLE_CLIENT;
             }else if(tb.equals("fc")){
            	 selectQuery += TABLE_FACTURE;
             }else{
            	 selectQuery += TABLE_REGLEMENT;
             }
    		 
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
 
 
}