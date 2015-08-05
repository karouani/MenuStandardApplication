package com.dolibarrmaroc.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;





import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class ImprimerActivity extends Activity {

	private TextView tv_loading;
	private String dest_file_path;
	private int downloadedSize = 0, totalsize;
	private String download_file_url;
	private String file_url = com.dolibarrmaroc.com.utils.URL.URL+"uploads";
	private float per = 0;
	private String fileName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				download_file_url = this.getIntent().getStringExtra("pdf");
				fileName = this.getIntent().getStringExtra("fichier");
			}
			
			
			int i = download_file_url.lastIndexOf("/")+1;
			int j = download_file_url.lastIndexOf(".pdf");
			
			Log.e("down ",download_file_url+"> i, j > "+i+","+j);
			dest_file_path = download_file_url.substring(i,j)+".pdf";
			
			
			//download_file_url = file_url+"/"+fileName;
			//dest_file_path = fileName;
			
			tv_loading = new TextView(this);
			setContentView(tv_loading);
			tv_loading.setGravity(Gravity.CENTER);
			tv_loading.setTypeface(null, Typeface.BOLD);
			downloadAndOpenPDF();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("error pdf ",e.getMessage()+ "  ");
		}


	}

	void downloadAndOpenPDF() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Uri path = Uri.fromFile(downloadFile(download_file_url));
					//Toast.makeText(ImprimerActivity.this, "En cours de telechargement", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(path, "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				} catch (ActivityNotFoundException e) {
					
					tv_loading
					.setText(getResources().getString(R.string.pdf_required));
					
					//Toast.makeText(ImprimerActivity.this, "Lecteur de pdf non installer sur votre device", Toast.LENGTH_LONG).show();
				}
			}
		}).start();

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
            String SDCardRoot = Environment.getExternalStorageDirectory()+ "/pdf/";;
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
			setText("Starting PDF download...");

			// create a buffer...
			byte[] buffer = new byte[1024 * 1024]; 
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				per = ((float) downloadedSize / totalsize) * 100;
				setText("La taille total de fichiers  : "
						+ (totalsize / 1024)
						+ " KB\n\nT�l�chargement du PDF " + (int) per
						+ "% complete");
			}
			// close the output stream when complete //
			fileOutput.close();
			setText("T�l�chargement T�rmin�. Ouvrir PDF par Application installer dans le device.");

		} catch (final MalformedURLException e) {
			setTextError("Quelques erreurs. Cliquez sur retour puis r�ssayer.",
					Color.RED);
		} catch (final IOException e) {
			setTextError("Quelques erreurs. Cliquez sur retour puis r�ssayer.",
					Color.RED);
		} catch (final Exception e) {
			setTextError(
					"Erreur dans le t�l�chargement SVP v�rifiez votre connexion.",
					Color.RED);
		}
		return file;
	}

	void setTextError(final String message, final int color) {
		runOnUiThread(new Runnable() {
			public void run() {
				tv_loading.setTextColor(color);
				tv_loading.setText(message);
			}
		});

	}

	void setText(final String txt) {
		runOnUiThread(new Runnable() {
			public void run() {
				tv_loading.setText(txt);
			}
		});

	}

}
