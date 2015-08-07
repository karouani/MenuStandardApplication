package com.dolibarrmaroc.com;





import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class GroupeVendeurActivity extends TabActivity  {

	private static final String TAG_TYERS = "Tab1";
    private static final String TAG_BABES = "Tab2";

    TabHost tabHost; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupe_vendeur);

		try {
			tabHost = getTabHost();

			tabHost.addTab(tabHost.newTabSpec("Tab1")
			    .setIndicator(TAG_TYERS)
			    .setContent(new Intent().setClass(this, VendeurActivity.class)));

			tabHost.addTab(tabHost.newTabSpec("Tab2")
			    .setIndicator(TAG_BABES)
			    .setContent(new Intent().setClass(this, MainActivity.class)));

			tabHost.setCurrentTab(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
