package com.dolibarrmaroc.com;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dolibarrmaroc.com.business.TechnicienManager;
import com.dolibarrmaroc.com.models.BordreauIntervention;
import com.dolibarrmaroc.com.models.Client;
import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.models.ImageTechnicien;
import com.dolibarrmaroc.com.offline.Offlineimpl;
import com.dolibarrmaroc.com.offline.ioffline;
import com.dolibarrmaroc.com.utils.Base64;
import com.dolibarrmaroc.com.utils.CheckOutNet;
import com.dolibarrmaroc.com.utils.TechnicienManagerFactory;

@SuppressLint("NewApi")
public class CameraActivity extends Activity {
	
	private TechnicienManager technicien;
	private String mYear, mMonth, mDay, mHour, mMinute;
	private static final int PICK_IMAGE = 1;
	private static final int PICK_Camera_IMAGE = 2;
	private ImageView imgView;
	private Button upload,cancel;
	private Bitmap bitmap;
	private ProgressDialog dialog;
	private Uri imageUri;
	private TextView name;
	private int type = 0;

	MediaPlayer mp=new MediaPlayer();

	private List<ImageTechnicien> images;
	private Compte compte;
	private Client clt;
	private String objet;
	private String date;
	private String timeD;
	private String timeF;
	private String fiche;
	private String description,superviseur;
	private String lienSignature;
	private BordreauIntervention br;
	private PowerManager.WakeLock wk;
	
	private ioffline myoffline;
	
	private static boolean deja_save;
	public CameraActivity() {
		images = new ArrayList<ImageTechnicien>();
		compte = new Compte();
		clt = new Client();
		technicien = TechnicienManagerFactory.getClientManager();
		br = new BordreauIntervention();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wk= powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
		wk.acquire();
		
		deja_save = false;
		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			name = (TextView) findViewById(R.id.typeImage);

			if (objetbunble != null) {
				compte = (Compte) getIntent().getSerializableExtra("user");
				date = this.getIntent().getStringExtra("date");
				timeD = this.getIntent().getStringExtra("timed");
				timeF = this.getIntent().getStringExtra("timef");
				clt = (Client) this.getIntent().getSerializableExtra("client");
				objet =  this.getIntent().getStringExtra("objet");
				description =  this.getIntent().getStringExtra("description");
				superviseur =  this.getIntent().getStringExtra("Superviseur");
				mYear = this.getIntent().getStringExtra("year");
				mMonth = this.getIntent().getStringExtra("month");
				mDay= this.getIntent().getStringExtra("day");
				mHour= this.getIntent().getStringExtra("heurD");
				mMinute= this.getIntent().getStringExtra("minD");
				
				fiche = this.getIntent().getStringExtra("fiche");
				name.setText(getResources().getString(R.string.tecv27)+"_"+fiche);
				int s = Integer.parseInt(timeF) - Integer.parseInt(timeD);
				
				br.setAuthor(compte.getLogin());
				br.setDate_c(date);
				br.setDuree(s+"");
				br.setId_clt(clt.getId()+"");
				br.setStatus(fiche);
				br.setDescription(description);
				br.setObjet(objet);
				br.setHeurD(mHour);
				br.setMinD(mMinute);
				br.setYear(mYear);
				br.setMonth(mMonth);
				br.setDay(mDay);
				br.setNmclt(clt.getName());
				
			}

			imgView = (ImageView) findViewById(R.id.ImageView);
			upload = (Button) findViewById(R.id.imguploadbtn);
			cancel = (Button) findViewById(R.id.imgcancelbtn);

			upload.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					Object tag = imgView.getTag();
					int id = tag == null ? -1 : (int) tag;
					
					if (bitmap == null || id == R.drawable.logo) {
						Toast.makeText(getApplicationContext(),
								"SVP selectionnez une image", Toast.LENGTH_SHORT).show();
					} else {

						InputStream is;
						BitmapFactory.Options bfo;
						Bitmap bitmapOrg;
						ByteArrayOutputStream bao ;

						bfo = new BitmapFactory.Options();
						bfo.inSampleSize = 2;
						//bitmapOrg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + customImage, bfo);

						bao = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
						byte [] ba = bao.toByteArray();
						String ba1 = Base64.encodeBytes(ba);

						if(type == 0){
							Log.i("Nombre Click avant",type+" Size Images : "+images.size());
							//Log.e("name >> ", name.getText().toString().split("_")[0]);
							images.add(new ImageTechnicien(images.size(), "fiche_"+fiche+".jpg", ba1));
							Log.i("Nombre Click aprés",type+" Size Images : "+images.size());

							Toast.makeText(getApplicationContext(),
									"Image de la fiche ajouter avec success ", Toast.LENGTH_SHORT).show();
							imgView.setImageResource(R.drawable.logo);
							name.setText(getResources().getString(R.string.tecv28));
							
							
							br.setImgs(images);

							dialog = ProgressDialog.show(CameraActivity .this, getResources().getString(R.string.caus15),
									getResources().getString(R.string.msg_wait), true);

							if(CheckOutNet.isNetworkConnected(getApplicationContext())){
								new ImageGalleryTask().execute();
							}else{
								new ImageGalleryOfflineTask().execute();
							}


							bitmap = null;
							type++;
						}
						
						/*
						else if(type == 1){
							Log.i("Nombre Click avant",type+" Size Images : "+images.size());
							images.add(new ImageTechnicien(images.size(), "etat_objet_avant"+".jpg", ba1));//name.getText().toString()
							Log.i("Nombre Click aprés",type+" Size Images : "+images.size());

							name.setText(getResources().getString(R.string.tecv29));
							imgView.setImageResource(R.drawable.logo);

							Toast.makeText(getApplicationContext(),
									"Image de l'etat avant "+objet, Toast.LENGTH_SHORT).show();
							upload.setText(getResources().getString(R.string.tecv26));
							bitmap = null;
							type++;
						}else if(type==2){
							Log.i("Nombre Click avant",type+" Size Images : "+images.size());
							images.add(new ImageTechnicien(images.size(), "etat_objet_apres"+".jpg", ba1));
							Log.i("Nombre Click aprés",type+" Size Images : "+images.size());

							Toast.makeText(getApplicationContext(),
									"Image de l'etat apres "+objet, Toast.LENGTH_SHORT).show();
							Log.e("Images","images nombre : "+images.size()+" Image1  : "+images.get(0).getName()+
									" Image2  : "+images.get(1).getName()+
									" Image3  : "+images.get(2).getName());
							Log.e("Envoyer Data ","Execution Asynchrone");
							
							br.setImgs(images);
							
							dialog = ProgressDialog.show(CameraActivity .this, getResources().getString(R.string.caus15),
							 getResources().getString(R.string.msg_wait), true);
							
							if(CheckOutNet.isNetworkConnected(getApplicationContext())){
								new ImageGalleryTask().execute();
							}else{
								new ImageGalleryOfflineTask().execute();
							}
							
						}
						*/

					}
				}
			});

			cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					/*
					Intent intent1 = new Intent(CameraActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					CameraActivity.this.finish();
					*/
					new AlertDialog.Builder(CameraActivity.this)
					.setTitle(getResources().getString(R.string.tecv36))
					.setMessage(getResources().getString(R.string.tecv47))
					.setNegativeButton(R.string.tecv16, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface ds, int arg1) {
							//VendeurActivity.super.onBackPressed();
							ds.dismiss();
							
						}

					})
					.setPositiveButton(R.string.tecv15, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface ds, int arg1) {
							//VendeurActivity.super.onBackPressed();
							Intent intent1 = new Intent(CameraActivity.this, ConnexionActivity.class);
							intent1.putExtra("user", compte);
							intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent1);
							CameraActivity.this.finish();
							
						}

					}).setCancelable(true)
					.create().show();
				}
			});
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_image_gallery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			//define the file-name to save photo taken by Camera activity
			String fileName = "new-photo-name.jpg";
			//create parameters for Intent with filename
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, fileName);
			values.put(MediaStore.Images.Media.DESCRIPTION,"Image capturer par Camera");
			//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
			imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			//create new Intent
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(intent, PICK_Camera_IMAGE);
			return true;

		case R.id.gallery:
			try {
				Intent gintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				gintent.setType("image/*");
				//gintent.setAction(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				//gintent.setAction(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(gintent,PICK_IMAGE);


			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
				
			}
			return true;
		}
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri selectedImageUri = null;
		String filePath = null;
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				selectedImageUri = data.getData();
			}
			break;
		case PICK_Camera_IMAGE:
			if (resultCode == RESULT_OK) {
				//use imageUri here to access the image
				selectedImageUri = imageUri;
				/*Bitmap mPic = (Bitmap) data.getExtras().get("data");
				selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), 
				mPic, getResources().getString(R.string.app_name), Long.toString(System.currentTimeMillis())));*/
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
			}
			break;
		}

		if(selectedImageUri != null){
			try {
				// OI FILE Manager
				String filemanagerstring = selectedImageUri.getPath();

				// MEDIA GALLERY
				String selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null) {
					filePath = selectedImagePath;
				} else if (filemanagerstring != null) {
					filePath = filemanagerstring;
				} else {
					Toast.makeText(getApplicationContext(), "Unknown path",
							Toast.LENGTH_LONG).show();
					Log.e("Bitmap", "Unknown path");
				}

				if (filePath != null) {
					decodeFile(filePath);
					Log.d("Lien Image ", filePath);
				} else {

					bitmap = null;
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Internal error",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}

	class ImageGalleryTask extends AsyncTask<Void, Void, String> {
		@SuppressWarnings("unused")
		@Override
		protected String doInBackground(Void... unsued) {
			//lienSignature = "uploads";
			lienSignature = "";
			
			myoffline = new Offlineimpl(getApplicationContext());
			
			if(!deja_save){
				lienSignature = technicien.insertBordereau(br, compte);
				if(!"".equals(lienSignature)){
					technicien.inesrtImage(images, lienSignature);
				}
				
				if(CheckOutNet.isNetworkConnected(getApplicationContext())){
					if(!myoffline.checkFolderexsiste()){
						showmessageOffline();
					}else{
						myoffline.sendOutIntervention(compte);
					}
					
				}
			}
			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					String msg ="";
					if(!"".equals(lienSignature)){
						msg = getResources().getString(R.string.tecv31);
						deja_save = true;
					}else{
						msg =getResources().getString(R.string.tecv32);
					}
					
					new AlertDialog.Builder(CameraActivity.this)
				    .setTitle(getResources().getString(R.string.tecv30))
				    .setMessage(msg)
				    .setPositiveButton(R.string.tecv33, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	Intent intent = new Intent(CameraActivity.this,ConnexionActivity.class);
							intent.putExtra("user", compte);
							startActivity(intent);
							CameraActivity.this.finish();
				        }
				     })
				     .show();
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}
	
	class ImageGalleryOfflineTask extends AsyncTask<Void, Void, String> {
		
		private long ix =-1;
		@SuppressWarnings("unused")
		@Override
		protected String doInBackground(Void... unsued) {
			//lienSignature = "uploads";
			
			myoffline = new Offlineimpl(getApplicationContext());
			
			if(!myoffline.checkFolderexsiste()){
				showmessageOffline();
			}else{
				br.setCompte(compte);
				br.setImgs(images);
				if(!deja_save){
					ix = myoffline.shynchronizeIntervention(br);
					myoffline.historiqueIntervention(br);
				}
			}
		
			return "success";
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing()){
					dialog.dismiss();
					String msg ="";
					if(ix != -1 ){
						msg = getResources().getString(R.string.tecv31);
						deja_save = true;
					}else{
						msg = getResources().getString(R.string.tecv32);
					}
					
					new AlertDialog.Builder(CameraActivity.this)
				    .setTitle(getResources().getString(R.string.tecv30))
				    .setMessage(msg)
				    .setPositiveButton(R.string.tecv33, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	Intent intent = new Intent(CameraActivity.this,ConnexionActivity.class);
							intent.putExtra("user", compte);
							startActivity(intent);
							CameraActivity.this.finish();
				        }
				     })
				     .show();
					
					
					
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage() +" << ", e);
			}
		}

	}

	/*public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}
	 */

	public String getPath(Uri uri) {
		String[] projection = {  MediaStore.MediaColumns.DATA};
		Cursor cursor;
		try{
			cursor = getContentResolver().query(uri, projection, null, null, null);
		} catch (SecurityException e){
			String path = uri.getPath();
			String result = tryToGetStoragePath(path);
			return  result;
		}
		if(cursor != null) {
			//HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			//THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		}
		else
			return uri.getPath();               // FOR OI/ASTRO/Dropbox etc
	}

	private String tryToGetStoragePath(String path) {
		int actualPathStart = path.indexOf("//storage");
		String result = path;

		if(actualPathStart!= -1 && actualPathStart< path.length())
			result = path.substring(actualPathStart+1 , path.length());

		return result;
	}


	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 2048;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 2;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		imgView.setImageBitmap(bitmap);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.tecv36))
			.setMessage(getResources().getString(R.string.tecv47))
			.setNegativeButton(R.string.tecv16, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					ds.dismiss();
					
				}

			})
			.setPositiveButton(R.string.tecv15, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface ds, int arg1) {
					//VendeurActivity.super.onBackPressed();
					Intent intent1 = new Intent(CameraActivity.this, ConnexionActivity.class);
					intent1.putExtra("user", compte);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					CameraActivity.this.finish();
					
				}

			}).setCancelable(true)
			.create().show();
			return true;
		}
		return false;
	}
	
	 
	
	public void showmessageOffline(){
		try {
			 
	         LayoutInflater inflater = this.getLayoutInflater();
	         View dialogView = inflater.inflate(R.layout.msgstorage, null);
	         
	         AlertDialog.Builder dialog =  new AlertDialog.Builder(CameraActivity.this);
	         dialog.setView(dialogView);
 	 	     dialog.setTitle(R.string.caus1);
 	 	     dialog.setPositiveButton(R.string.caus8, new DialogInterface.OnClickListener() {
 	 	        public void onClick(DialogInterface dialog, int which) { 
 	 	        	 dialog.cancel();
 	 	        }
 	 	     });
 	 	     dialog.setCancelable(true);
 	 	     dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
