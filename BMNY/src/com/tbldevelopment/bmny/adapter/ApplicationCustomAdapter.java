package com.tbldevelopment.bmny.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tbldevelopment.bmny.R;

public class ApplicationCustomAdapter extends
		ArrayAdapter<HashMap<String, String>> {

	private ArrayList<HashMap<String, String>> appList;
	private Context mContext;
	private LayoutInflater inflater;

	public ApplicationCustomAdapter(Context context, int textViewResourceId,
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
		TextView txtView = null;
		if (holder == null) {
			holder = inflater.inflate(R.layout.application_friend_list_layout,
					null);
			txtView = (TextView) holder.findViewById(R.id.txtTitle);
		}
		txtView.setText(map.get("userName"));
		return holder;
	}

}
