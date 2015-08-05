package com.karouani.cicin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

public class CustomAutoCompleteView extends AutoCompleteTextView{
	private Context mContext;
	
	public CustomAutoCompleteView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }
     
    public CustomAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }
 
    public CustomAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }
 
    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }

	
	
	public String getSelected(AdapterView<?> parent, View arg1, int pos, long id) {
		RelativeLayout rl = (RelativeLayout) arg1;
        TextView tv = (TextView) rl.getChildAt(0);
        this.setText(tv.getText().toString());
         
     // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindowToken(), 
                                  InputMethodManager.RESULT_UNCHANGED_SHOWN);
        this.setSelection(this.getText().length());
		return this.getText().toString();
	}

	
}
