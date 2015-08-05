package com.karouani.cicin.widget.alert;

import java.util.List;

import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.adapter.AlertDialogAdapter;
import com.dolibarrmaroc.com.models.AlertDialog;

import android.content.Context;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AlertDialogList extends android.app.AlertDialog.Builder implements OnItemClickListener{

	private Context context;
	private List<AlertDialog> alerts;
	private AlertDialogAdapter adapter;
	private ListView list;
	private LayoutInflater inflater;
	
	public AlertDialogList(Context context, List<AlertDialog> alerts) {
		super(context);
		this.context = context;
		this.alerts = alerts;
		adapter = new AlertDialogAdapter(context, alerts);
		this.inflater = LayoutInflater.from(context);
		View dialogView = inflater.inflate(R.layout.menu_alert_option, null);
		setView(dialogView);
		
		list = (ListView) dialogView.findViewById(R.id.alert_options);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		String selected = (String) adapter.getItem(position).getLabel();
		for (AlertDialog alert : alerts) {
			if (alert.getLabel().equals(selected)) {
				context.startActivity(alert.getIntent());
			}
		}
	}

	
}
