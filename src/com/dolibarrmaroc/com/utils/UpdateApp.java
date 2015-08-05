/**
 * 

package com.example.miseajour;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class UpdateApp extends AsyncTask<String,Void,Void>{

	private Context context;
	public void setContext(Context contextf){
		context = contextf;
	}

	@Override
	protected Void doInBackground(String... arg0) {
		try {
			URL url = new URL(arg0[0]);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			//String PATH = context.getCacheDir()+"/signature.png";
			String PATH = Environment.getExternalStorageDirectory()+ "/apk/";
			File file = new File(PATH);
			file.mkdirs();
			File outputFile = new File(file, "update.apk");
			if(outputFile.exists()){
				outputFile.delete();
			}
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(PATH+"/update.apk")), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
			context.startActivity(intent);


		} catch (Exception e) {
			Log.e("UpdateAPP", "Update error! " + e.getMessage());
		}
		return null;
	}

}

 */

package com.dolibarrmaroc.com.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateApp extends AsyncTask<String,Void,Void>{

	private Context context;
	private TextView tv_loading;
	private String dest_file_path;
	private int downloadedSize = 0, totalsize;
	private String download_file_url;
	private float per = 0;
	private String fileName;


	public void setContext(Context contextf){
		context = contextf;
	}

	@Override
	protected Void doInBackground(String... arg0) {
		download_file_url = arg0[0];
		try {

			Uri path = Uri.fromFile(downloadFile(download_file_url));

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
			context.startActivity(intent);



		} catch (Exception e) {
			Log.e("UpdateAPP", "Update error! " + e.getMessage() +" << ");
		}
		return null;
	}

	public File downloadFile(String dwnload_file_path) {
		File file = null;
		try {

			URL url = new URL(dwnload_file_path);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// connect
			urlConnection.connect();

			// set the path where we want to save the file
			//File SDCardRoot = Environment.getExternalStorageDirectory();
			// create a new file, to save the downloaded file
			//file = new File(SDCardRoot, dest_file_path);

			// set the path where we want to save the file
			String SDCardRoot = Environment.getExternalStorageDirectory()+ "/apk/";
			// create a new file, to save the downloaded file
			File outfile = new File(SDCardRoot);
			if(!outfile.exists()) {
				outfile.mkdirs();
			}
			file = new File(outfile, dest_file_path);

			FileOutputStream fileOutput = new FileOutputStream(file);

			// Stream used for reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();
			// this is the total size of the file which we are
			// downloading
			totalsize = urlConnection.getContentLength();
			Toast.makeText(context, "Starting PDF download...", Toast.LENGTH_LONG).show();

			// create a buffer...
			byte[] buffer = new byte[1024 * 1024]; 
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				per = ((float) downloadedSize / totalsize) * 100;

				Toast.makeText(context, "La taille total de fichiers  : "
						+ (totalsize / 1024)+'\n'
						+ " KB\n\nTéléchargement du PDF " + (int) per+"\n"
						+ "% complete", Toast.LENGTH_LONG).show();
			}
			// close the output stream when complete //
			fileOutput.close();
			Toast.makeText(context, "T�l�chargement t�rminer. Ouvrir PDF par Application installer dans le device.", Toast.LENGTH_LONG).show();

		} catch (final Exception e) {
			Toast.makeText(context, "Quelques erreurs. Cliquez sur retour puis r�ssayer.", Toast.LENGTH_LONG).show();

		}
		return file;
	}

}

