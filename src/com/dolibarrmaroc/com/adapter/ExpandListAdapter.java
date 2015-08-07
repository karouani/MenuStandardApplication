package com.dolibarrmaroc.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dolibarrmaroc.com.R;
import com.dolibarrmaroc.com.models.Categorie;
import com.dolibarrmaroc.com.models.Produit;
import com.dolibarrmaroc.com.utils.UrlImage;

public class ExpandListAdapter extends BaseExpandableListAdapter implements Filterable{

    private Context context;
    private List<Categorie> groups;
    private List<Categorie> filterlist;
    private List<Categorie> facttmp;
    
    private ValueFilter valueFilter;
    
    public ToggleButton ExpCol;
    
    private HashMap<Long, Categorie> hashtmp;

    public ExpandListAdapter(Context context, List<Categorie> groups) {
        this.context = context;
        this.groups = groups;
        this.facttmp = groups;
        
        getFilter();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Produit> chList = groups.get(groupPosition).getProducts();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {

    	Produit child = (Produit) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.lblListItem);
        TextView dsc = (TextView) convertView.findViewById(R.id.listItemInfo);
        TextView prix = (TextView) convertView.findViewById(R.id.prix);
        ImageView iv = (ImageView) convertView.findViewById(R.id.flag);
        
       

        tv.setText(child.getDesig().toString());
        dsc.setText(child.getDesig());
        prix.setText(child.getPrixttc()+" DH");
        if(child.getPhoto().equals("")){
        	iv.setImageResource(R.drawable.nophoto);
        }else{
        	iv.setImageURI(Uri.parse(UrlImage.pathimg+"/produit_img/"+child.getId()+"_"+child.getPhoto()));
        }
      

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Produit> chList = groups.get(groupPosition).getProducts();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        Categorie group = (Categorie) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.lblListItem2);
        tv.setText(group.getLabel());
        
        
        /*
        ImageView iv = (ImageView) convertView.findViewById(R.id.flag2);

        
        if(group.getPhoto().equals("")){
        	iv.setImageResource(R.drawable.nophoto);
        }else{
        	iv.setImageURI(Uri.parse(UrlImage.pathimg+"/categorie_img/"+group.getRowid()+"_"+group.getPhoto()));
        }
        */
        
        
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
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
				hashtmp = new HashMap<>();
				for (int i = 0; i < facttmp.size(); i++) {
					
					
					for (int j = 0; j < facttmp.get(i).getProducts().size(); j++) {
						if(facttmp.get(i).getProducts().get(j).getDesig().toLowerCase().contains(constraint.toString().toLowerCase())){
							hashtmp.put((long)facttmp.get(i).getRowid(), facttmp.get(i));
						}
					}
				}
				
				for (Long c:hashtmp.keySet()) {
					filterlist.add(hashtmp.get(c));
				}
				Log.e("count "+constraint,filterlist.size()+"");
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
			
			groups = new ArrayList<>();
			groups = (List<Categorie>) results.values;
			notifyDataSetChanged();
			
			filterlist = groups;
		}

	}

}