package com.karouani.cicin.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dolibarrmaroc.com.R;

/*******************************
 * 
 * @author yassinekarouani
 *
 */
public class AutocompleteCustomArrayAdapter extends ArrayAdapter<String> {

	final String TAG = "AutocompleteCustomArrayAdapter.java";

	Activity mContext;
	int layoutResourceId;
	String data[] = null;
	LayoutInflater inflater;
	
	public AutocompleteCustomArrayAdapter(Activity mContext, int layoutResourceId, String[] data) {

		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
		//Log.e("E ",Arrays.toString(data));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		try{
			
			//Log.e("position ","<< "+position+" >>");
			
			/*
			 * The convertView argument is essentially a "ScrapView" as described is Lucas post 
			 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
			 * It will have a non-null value when ListView is asking you recycle the row layout. 
			 * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
			 */
			//if(convertView==null){
				// inflate the layout
				//LayoutInflater inflater = mContext.getLayoutInflater();
				inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(layoutResourceId, null);
			//}

			// object item based on the position
			String objectItem = data[position];
			//Log.e("objectItem ","<< "+objectItem+" >>");
			
			
			// get the TextView and then set the text (item name) and tag (item ID) values
			TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
			textViewItem.setText(objectItem);

			// in case you want to add some style, you can do something like:
			textViewItem.setBackgroundColor(Color.RED);
			textViewItem.setSingleLine(true);
			textViewItem.setCursorVisible(false);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;

	}
}
