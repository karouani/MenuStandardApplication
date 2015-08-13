package com.dolibarrmaroc.com.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "storagevirtual";

	//  tableS name
	private static final String TABLE_PROD = "storageprod";
	private static final String TABLE_SYNCRO = "storagesynchronisation";


	// Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_REF = "ref";
	private static final String KEY_QTE = "qte";
	private static final String KEY_TYPE = "typeprd";
	private static final String KEY_LIB = "libelle";
	private static final String KEY_CLT = "client";
	private static final String KEY_SYS = "sysout";

	private static final String KEY_DT = "dtcheck";
	private static final String KEY_ISIT = "ischeck";

	public StockVirtual(Context context) {
		super(context,Environment.getExternalStorageDirectory()+"/.datadolicachenew/"+DATABASE_NAME, null, DATABASE_VERSION);
	}



	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String CREATE_factures_TABLE = "CREATE TABLE " + TABLE_PROD + "("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_REF + " INTEGER , "+KEY_QTE+" INTEGER, "+KEY_TYPE+" VARCHAR(30), "+KEY_LIB+" VARCHAR(255), "+KEY_CLT+" VARCHAR(255), "+KEY_SYS+" INTEGER )";
			db.execSQL(CREATE_factures_TABLE);


			String cr1 = "CREATE TABLE " + TABLE_SYNCRO + "("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DT + " VARCHAR(30), "+KEY_ISIT+" INTEGER )";
			db.execSQL(cr1);
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
	public long addrow(String tb,int pd,int qte,String tp,String lb,String clt) {
		long id =-1;
		try {
			SQLiteDatabase db = this.getWritableDatabase();


			ContentValues values = new ContentValues();
			values.put(KEY_REF, pd);
			values.put(KEY_QTE, qte);
			values.put(KEY_TYPE, tp);
			values.put(KEY_LIB, lb);
			values.put(KEY_CLT, clt);
			values.put(KEY_SYS, 0);

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

	 */
	// Deleting single 
	public void deleteProduit(Produit contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROD, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getPrixttc()) });
		db.close();
	}


	public List<Produit> getAllProduits(int in) {
		List<Produit> contactList = new ArrayList<Produit>();
		// Select All Query
		try {
			String selectQuery = "";
			String[] nm = null;
			if(in == -1){
				selectQuery = "SELECT  * FROM " + TABLE_PROD;
			}else{
				selectQuery = "SELECT  * FROM " + TABLE_PROD + " WHERE "+KEY_TYPE +" = "+in;
				//nm = new String[] {in+""};
			}


			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, nm);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					contactList.add(new Produit(""+cursor.getInt(1), cursor.getString(3), cursor.getInt(2), "", cursor.getInt(6), cursor.getInt(0), cursor.getString(5), cursor.getString(4)));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			contactList = new ArrayList<Produit>();
		}

		// return  list
		return contactList;
	}


	/*************************************  check update synchronisation ******************************/
	public long addrowcheckout() {
		long id =-1;
		try {

			deleteChechout();

			SQLiteDatabase db = this.getWritableDatabase();


			ContentValues values = new ContentValues();
			
			Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
			int y       = cl.get(Calendar.YEAR);
			int m      = cl.get(Calendar.MONTH)+1; // Jan = 0, dec = 11
			int d = cl.get(Calendar.DAY_OF_MONTH); 
			
			
			values.put(KEY_DT, y+""+m+""+d);
			values.put(KEY_ISIT, 1);

			// Inserting Row
			id = db.insert(TABLE_SYNCRO, null, values);

			db.close(); // Closing database connection


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("insert data","insert data");
		}
		return id;
	}

	public void deleteChechout() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+TABLE_SYNCRO);
		db.close();
	}

	public int getSyc() {
		// Select All Query
		String dt = "";
		try {
			String selectQuery = "";
			String[] nm = null;
			selectQuery = "SELECT  * FROM " + TABLE_SYNCRO;


			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, nm);

			// looping through all rows and adding to list
			Log.e("hello ",cursor.getCount()+"");
			if(cursor.getCount() == 0){
				addrowcheckout();
				return 1;
			}else{
				if (cursor.moveToFirst()) {
					do {
						Log.e(">>> sysc ",cursor.getString(1)+"  "+cursor.getInt(2));
						dt = cursor.getString(1);
						//break;
					} while (cursor.moveToNext());
				}
				
				Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
				int y       = cl.get(Calendar.YEAR);
				int m      = cl.get(Calendar.MONTH)+1; // Jan = 0, dec = 11
				int d = cl.get(Calendar.DAY_OF_MONTH); 
				
				long in = Long.parseLong(y+""+m+""+d);
				if(!"".equals(dt)){
					long out = Long.parseLong(dt);
					
					if(in > out){
						return 1;
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("errror sysc get ",e.getMessage() +"");
			return -1;
		}

		return -1;
	}   

}
