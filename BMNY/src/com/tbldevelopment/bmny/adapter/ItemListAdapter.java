package com.tbldevelopment.bmny.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbldevelopment.bmny.R;


public class ItemListAdapter extends ArrayAdapter<JSONObject> {
	private LayoutInflater lflater;
	Context appContext;
	private ArrayList<JSONObject> jsonList;
	private int requestFor;

	public ItemListAdapter(Context context, int requested,
			ArrayList<JSONObject> list) {
		super(context, requested, list);
		// TODO Auto-generated constructor stub

		lflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.jsonList = list;

		this.appContext = context;
		requestFor = requested;
		System.out.println("req for ===>>>" + requestFor);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View holder = convertView;

		if (holder == null) {

			switch (requestFor) {

			case 5:
				holder = lflater.inflate(R.layout.friend_list, null);
				break;
			}
		} // fif ends

		try {
			JSONObject obj;
			obj = jsonList.get(position);
			if (obj != null) {

				switch (requestFor) {

				case 5:
					TextView friendNameTxt = (TextView) holder
							.findViewById(R.id.txtFriendName);
					TextView friendNameStatus = (TextView) holder
							.findViewById(R.id.txtFriendStatus);

					ImageView blockImage=(ImageView) holder.findViewById(R.id.blockImageView);
					String friendName = obj.getString("username");
					String status = obj.getString("status");
					if(status.equals("0")){
						blockImage.setVisibility(View.GONE);
					}else if(status.equals("10")){
						blockImage.setVisibility(View.GONE);
					}else if(status.equals("99")){
						friendNameStatus.setVisibility(View.VISIBLE);
						friendNameStatus.setText("Pending...");
					}
					friendNameTxt.setText(friendName);
				}
				// holder.setBackgroundResource(R.drawable.list_button_pressed);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;
	}

}
