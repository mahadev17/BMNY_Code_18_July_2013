package com.tbldevelopment.bmny.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbldevelopment.bmny.R;

public class ContactListAdaptor extends ArrayAdapter<HashMap<String, String>> {

	private ArrayList<HashMap<String, String>> appList;
	private Context mContext;
	private LayoutInflater inflater;

	public ContactListAdaptor(Context context, int textViewResourceId,
			ArrayList<HashMap<String, String>> list) {
		super(context, textViewResourceId, list);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.appList = list;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appList.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return appList.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View holder;
		holder = convertView;
		HashMap<String, String> map = getItem(position);
		
		if (convertView == null) {
			holder = inflater.inflate(R.layout.contact_list,null);
			((TextView)holder.findViewById(R.id.phoneNoTxt)).setVisibility(View.VISIBLE);
			
			
		}
		if(map.get("isSelected").equalsIgnoreCase("0"))
		{
			((ImageView)holder.findViewById(R.id.checkImageView)).setBackgroundResource(R.drawable.dot);
		}else{
			((ImageView)holder.findViewById(R.id.checkImageView)).setBackgroundResource(R.drawable.right_icon_green);
		}
		((TextView)holder.findViewById(R.id.userNameTxt)).setText(map.get("contactName"));
		((TextView)holder.findViewById(R.id.phoneNoTxt)).setText(map.get("phoneNo"));
		return holder;
	}



}
