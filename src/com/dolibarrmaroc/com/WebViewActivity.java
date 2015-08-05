package com.dolibarrmaroc.com;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private WebView webView;
	private String url;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);


		try {
			Bundle objetbunble  = this.getIntent().getExtras();

			if (objetbunble != null) {
				url = this.getIntent().getStringExtra("lien");
			}

			webView = (WebView) findViewById(R.id.webView); 
			webView.setWebViewClient(new MyWebViewClient());
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	}

}