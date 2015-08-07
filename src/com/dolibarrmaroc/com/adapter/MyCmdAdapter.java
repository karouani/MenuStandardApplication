package com.dolibarrmaroc.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.models.MyfactureAdapter;

public class MyCmdAdapter extends BaseAdapter implements Filterable{

	private Context context;
	private List<MyfactureAdapter> facts;
	private List<MyfactureAdapter> factsfilter;
	private List<MyfactureAdapter> facttmp;
	private List<MyfactureAdapter> filterlist;
	private LayoutInflater inflater;
	
	private ValueFilter valueFilter;
	
	
	public MyCmdAdapter(Context ctx,List<MyfactureAdapter> fc){
		this.context = ctx;
		this.facts = fc;
		this.inflater = LayoutInflater.from(ctx);
		this.facttmp = fc;
		getFilter();
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return facts.size();
	}

	@Override
	public MyfactureAdapter getItem(int arg0) {
		// TODO Auto-generated method stub
		return facts.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return facts.get(arg0).getIdfact();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = this.inflater.inflate(R.layout.listecmd,
					parent, false);

			holder.clt = (TextView) convertView
					.findViewById(R.id.orefcltcmd);
			holder.fact = (TextView) convertView
					.findViewById(R.id.oreffactcmd);
			holder.mtn = (TextView) convertView
					.findViewById(R.id.omtncmd);
			holder.id = (TextView) convertView.findViewById(R.id.oidcmd);
			
			holder.pay = (TextView) convertView.findViewById(R.id.opayermtncmd);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MyfactureAdapter fc = facts.get(position);
		holder.clt.setText(fc.getRefclient());
		holder.fact.setText(fc.getReffact());
		holder.mtn.setText(fc.getAmount() + " DH");
		holder.pay.setText(fc.getPayer()+ "");
		holder.id.setText(fc.getIdfact()+"");

		return convertView;
	}

	private class ViewHolder {
		TextView clt;
		TextView fact;
		TextView mtn;
		TextView id;
		TextView pay;
	}

	public MyCmdAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	 
	public Filter getFilter() {
		if (valueFilter == null) {
			valueFilter = new ValueFilter();
		}
		return valueFilter;
	}


	private class ValueFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if (constraint != null && constraint.length() > 0) {
				filterlist = new ArrayList<>();
				for (int i = 0; i < facttmp.size(); i++) {
					if ( (facttmp.get(i).getReffact() ).toLowerCase()
							.contains(constraint.toString().toLowerCase()) || (facttmp.get(i).getRefclient().toLowerCase() ).toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						filterlist.add( facttmp.get(i));
					}
				}
				results.count = filterlist.size();
				results.values = filterlist;
			} else {
				results.count = facttmp.size();
				results.values = facttmp;
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			
			facts = new ArrayList<>();
			facts = (List<MyfactureAdapter>) results.values;
			notifyDataSetChanged();
			
			Log.e("LIST",facts.toString());
			filterlist = facts;
		}

	}

	

}
