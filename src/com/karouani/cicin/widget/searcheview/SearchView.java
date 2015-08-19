package com.karouani.cicin.widget.searcheview;

import java.lang.reflect.Method;

import com.dolibarrmaroc.com.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class SearchView extends android.widget.SearchView{

	private Context context;

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;

		//SearchView searchView = this;//(SearchView) findViewById(R.id.searchView1);
		int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null); 
		//EditText searchEditText = (EditText) searchView.findViewById(searchSrcTextId);  
		//searchEditText.setTextColor(0xffffffff);  
		//searchEditText.setHintTextColor(Color.LTGRAY);
		//Getting the 'search_icon'

		int searchTextViewId = context.getResources().getIdentifier("android:id/search_src_text", null, null);
		AutoCompleteTextView searchTextView = (AutoCompleteTextView) findViewById(searchTextViewId);
		searchTextView.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
		searchTextView.setTextColor(getResources().getColor(android.R.color.white));
		searchTextView.setTextSize(18.0f);

		int searchIconId = context.getResources().
				getIdentifier("android:id/search_button", null, null);
		ImageView icon = (ImageView) findViewById(searchIconId);
		icon.setImageResource(R.drawable.title_search_default); 

		int searchIconIdClose = context.getResources().
				getIdentifier("android:id/search_close_btn", null, null);
		ImageView iconClose = (ImageView) findViewById(searchIconIdClose);
		iconClose.setImageResource(R.drawable.delete_from_panier);

		int queryTextViewId = getResources().getIdentifier("android:id/search_src_text", null, null);  
		View autoComplete =  findViewById(queryTextViewId);

		Class<?> clazz;
		try {
			clazz = Class.forName("android.widget.SearchView$SearchAutoComplete");
			SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");  
			stopHint.append(context.getResources().getString(R.string.title_recherche));

			Drawable searchIcon = context.getResources().getDrawable(R.drawable.title_search_default);  
			Method textSizeMethod = clazz.getMethod("getTextSize");  
			Float rawTextSize = (Float)textSizeMethod.invoke(autoComplete);  
			int textSize = (int) (rawTextSize * 1.25);  
			searchIcon.setBounds(0, 0, textSize, textSize);  
			stopHint.setSpan(new ImageSpan(searchIcon), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			Method setHintMethod = clazz.getMethod("setHint", CharSequence.class);  
			setHintMethod.invoke(autoComplete, stopHint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	public SearchView(Context context) {
		super(context);
		this.context = context;
	}

}
