package com.dolibarrmaroc.com;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dolibarrmaroc.com.models.Compte;
import com.dolibarrmaroc.com.utils.JSONParser;

public class ReadPDFActivity extends Activity {

	private String surl;
	private Compte compte;
	private JSONParser jsonparser ;
	private Button btnPrint;
	
	private Dialog dialog;
	private EditText txtEmail;
	private Button sendPDF;
	
	public ReadPDFActivity() {
		compte = new Compte();
		jsonparser = new JSONParser();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_pdf);

		try {
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.maillayout);
			dialog.setTitle("Envoyer PDF Par Mail");
			txtEmail =  (EditText) dialog.findViewById(R.id.emailto);
			sendPDF = (Button) dialog.findViewById(R.id.sendMail);

			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {

				surl = this.getIntent().getStringExtra("pdf");
				compte =  (Compte) getIntent().getSerializableExtra("compte");

			}

			btnPrint=(Button)findViewById(R.id.annulerfiche);

			btnPrint.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (isNetworkAvailable() == false) {
						Toast.makeText(ReadPDFActivity.this,
								"Network connection not available, Please try later",
								Toast.LENGTH_SHORT).show();
					} else {
						File file = new File(surl);
						Intent printIntent = new Intent(ReadPDFActivity.this, PrintDialogActivity.class);
						printIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
						printIntent.putExtra("title", "Android print demo");
						startActivity(printIntent);
					}
				}
			});




			/*Log.i("Recuperation PDF ",surl);

			WebView webview = (WebView) findViewById(R.id.pdfview);

			webview.getSettings().setJavaScriptEnabled(true); 
			webview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + surl);
			 */
			WebView webView=new WebView(this);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setPluginState(PluginState.ON);
			webView.setWebViewClient(new Callback());

			webView.loadUrl(
					"http://docs.google.com/gview?url=" + surl);

			setContentView(webView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private class Callback extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(
				WebView view, String url) {
			return(false);
		}
	}

	public void onBackPressed() {
		Intent intent = new Intent(this, ConnexionActivity.class);
		startActivity(intent);
		ReadPDFActivity.this.finish();
	}

	public boolean isNetworkAvailable() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.e("Network Testing", "***Available***");
			return true;
		}
		Log.e("Network Testing", "***Not Available***");
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.read_pd, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.telecharger:
			sendPDF.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!"".equals(txtEmail.getText().toString())){
						String[] TO = {txtEmail.getText().toString()};
						
						Intent emailIntent = new Intent(Intent.ACTION_SEND);
	                     emailIntent.setData(Uri.parse("mailto:"));
	                     emailIntent.setType("text/html");

	                     int i = surl.lastIndexOf("/")+1;
	             		int j = surl.lastIndexOf(".pdf");
	             		String g = surl.substring(i,j);

	                     emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
	                     emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Facture "+g);
	                     emailIntent.putExtra(Intent.EXTRA_TEXT, "Voila le lien de la facture � t�l�charger :<br/>"
	                     		+ "<a href="+surl+" >Facture "+g+"</a>");

	                     try {
	                        startActivity(Intent.createChooser(emailIntent, "Envoyer Email..."));
	                        finish();
	                        dialog.dismiss();
	                        Log.i("Finished sending email...", "");
	                     } catch (android.content.ActivityNotFoundException ex) {
	                        Toast.makeText(ReadPDFActivity.this, 
	                        "Pas d'application Email installer", Toast.LENGTH_SHORT).show();
	                     }
					}

					 
				}
				
			});
			
			dialog.show();
			return true;
		}
		return false;
	}
}
