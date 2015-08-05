package com.dolibarrmaroc.com.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dolibarrmaroc.com.models.Compte;

@SuppressLint("NewApi")
public class DBHandler extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "MyDBName.db";
	public static final String CONTACTS_TABLE_NAME = "utilisateurs";
	public static final String CONTACTS_COLUMN_ID = "id";
	public static final String CONTACTS_COLUMN_LOGIN = "login";
	public static final String CONTACTS_COLUMN_PASSWORD = "password";
	public static final String CONTACTS_COLUMN_DATE = "date";
	public static final String CONTACTS_COLUMN_SOUVENIR = "souvenue";
	public static final String CONTACTS_COLUMN_IMEI = "imei";
	public static final String CONTACTS_COLUMN_PROFILE = "profile";
	private static final int DATABASE_VERSION = 5;
	
	//[id=1, login=vendeur, password=1234, profile=vendeur, activer=0, message=null, iduser=null, emei=null, step=null, stop=null, level=null, permission=0, permissionPayement=0, permissionbl=0, souvenue=1, date=1438355302]
			 
	private HashMap hp;

	public DBHandler(Context context)
	{
		super(context, DATABASE_NAME , null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(
				"create table utilisateurs " +
						"(id integer primary key, login text,password text,date INTEGER, souvenue INTEGER, imei text,profile text)"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS utilisateurs");
		onCreate(db);
	}

	public boolean insertUser(String login, String password, long date, int souvenue,String imei,String profile,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("login", login);
		contentValues.put("password", password);
		contentValues.put("date", (int) date);	
		contentValues.put("souvenue", souvenue);
		contentValues.put("imei", imei);
		contentValues.put("profile", profile);
		contentValues.put("id",(int) id);
		
		Log.e("imei ",imei);
		
		db.insert("utilisateurs", null, contentValues);
		return true;
	}
	
	public Compte getConnexion(String imei,long date){
		SQLiteDatabase db = this.getReadableDatabase();
		//Cursor res =  db.rawQuery( "select * from utilisateurs where date <= "+date+" AND imei = '"+imei+"' AND souvenue = 1", null,null );
		int k = numberOfRows();
		
		Cursor res =
				db.query(true,  
						CONTACTS_TABLE_NAME,
						new String[] {
						CONTACTS_COLUMN_ID,
						CONTACTS_COLUMN_LOGIN,
						CONTACTS_COLUMN_PASSWORD,
						CONTACTS_COLUMN_DATE,
						CONTACTS_COLUMN_SOUVENIR,
						CONTACTS_COLUMN_IMEI,
						CONTACTS_COLUMN_PROFILE
	                    }, 
	                    CONTACTS_COLUMN_DATE + "<=" +date+ " AND " + CONTACTS_COLUMN_SOUVENIR + "="+ 1 + " AND " + CONTACTS_COLUMN_IMEI+ "=" +imei, 
	                    null, 
	                    null, 
	                    null, 
	                    null, 
	                    null);
		
		//db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
	    if (res != null) {
	        res.moveToFirst();
	    }
	    
		Log.d("Nombre enregistrement", k+"");
		
		Log.d("id",""+res.getInt(0));
		Log.d("login",""+res.getString(1));
		Log.d("password",""+res.getString(2));
		Log.d("date",""+res.getLong(3));
		Log.d("souvenir",""+res.getInt(4));
		Log.d("imei",""+res.getString(5));
		Log.d("profile",""+res.getString(6));
		
		if(k > 0){
			Compte c =new Compte(
					res.getInt(0), 
					res.getString(1), 
					res.getString(2), 
					res.getInt(4), 
					res.getLong(3),
					res.getString(6)
					);
			c.setIduser(""+c.getId());
			return c;
		}
		
		
		return null;
	}
	
	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
		return numRows;
	}
	

	public Integer deleteUser(String imei)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("utilisateurs", 
				"imei = ? ", 
				new String[] { imei });
	}
	
	public Compte getAll()
	{
		List<Compte> compte = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from utilisateurs", null );
		res.moveToFirst();
		while(res.isAfterLast() == false){
			Compte c =new Compte(
					res.getInt(0), 
					res.getString(1), 
					res.getString(2), 
					res.getInt(4), 
					new Long(res.getInt(3)),
					res.getString(6)
					);
			
			compte.add(c);
			res.moveToNext();
		}
		return compte.get(0);
	}

}
