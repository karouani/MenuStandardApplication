package com.karouani.cicin.widget;

import java.util.ArrayList;
import java.util.List;


import android.R;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

/*******************
 * 
 * @author yassinekarouani
 *
 */
public class CustomAutoCompleteTextChangedListener{

	public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
	private Activity context;
	private ArrayAdapter<String> myAdapter;
	private List<String> values;
	private int layout;
	
	public CustomAutoCompleteTextChangedListener(Activity context,int layout,List<String> values){
		this.context = context;
		this.values = values;
		//Log.e("Data",values.toString());
		this.layout = layout;
	}

	public ArrayAdapter<String> onTextChanged(CharSequence userInput, int start, int before, int count) {

		try{

			// if you want to see in the logcat what the user types
			//Log.e(TAG, "User input: " + userInput);

			// update the adapater

			// get suggestions from the database
			int k = 0;
			
            ArrayList<String> array = new ArrayList<String>();
            for (int i = 0; i < values.size(); i++) {
				if (values.get(i).toUpperCase().contains(userInput.toString().toUpperCase())) {
					array.add(values.get(i));
				}
			}
			 
			String[] myObjs = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				myObjs[i] = array.get(i);
			}

			// update the adapter
			myAdapter = new AutocompleteCustomArrayAdapter(context,layout, myObjs);
			//myAutoComplete.setAdapter(myAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myAdapter;

	}



}
