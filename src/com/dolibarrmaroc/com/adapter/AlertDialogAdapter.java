package com.dolibarrmaroc.com.adapter;

import java.util.List;

import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.models.AlertDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlertDialogAdapter extends BaseAdapter{

	private Context context;
	private List<AlertDialog> alerts;
	private LayoutInflater inflater;
	
	public AlertDialogAdapter(Context context, List<AlertDialog> alerts) {
		super();
		this.context = context;
		this.alerts = alerts;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return alerts.size();
	}

	@Override
	public AlertDialog getItem(int id) {
		// TODO Auto-generated method stub
		return alerts.get(id);
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		Log.e("view",convertView+"");
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = this.inflater.inflate(R.layout.alert_dialog_adapter,
					parent, false);

			holder.label = (TextView) convertView
					.findViewById(R.id.textView1);
			holder.img = (ImageView) convertView
					.findViewById(R.id.imageView1);
			

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AlertDialog al = alerts.get(position);
		holder.label.setText(al.getLabel());

		Drawable image ;
		try {
			String uri = "drawable/"+ al.getImage();
			int imageResource = context.getApplicationContext().getResources().getIdentifier(uri.toLowerCase(), null, context.getApplicationContext().getPackageName());
			
			image = convertView.getResources().getDrawable(imageResource);
		} catch (Exception e) {
			// TODO: handle exception
			String uri = "drawable/nophoto.png";
			int imageResource = context.getApplicationContext().getResources().getIdentifier(uri, null, context.getApplicationContext().getPackageName());
			Log.e("IMAGES ", imageResource+"");
			image = convertView.getResources().getDrawable(imageResource);
		}
		holder.img.setImageDrawable(image);
		return convertView;
	}

	private class ViewHolder {
		TextView label;
		ImageView img;
	}
}
