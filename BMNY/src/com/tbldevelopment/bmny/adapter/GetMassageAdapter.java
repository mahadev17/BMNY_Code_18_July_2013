package com.tbldevelopment.bmny.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbldevelopment.bmny.R;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;


public class GetMassageAdapter extends ArrayAdapter<JSONObject> {
	private LayoutInflater lflater;
	Context appContext;
	private ArrayList<JSONObject> jsonList;
	private int requestFor;
	HashMap<String, Bitmap> hmImages;
	OnClickListener clickListener;
	String heading;
	public GetMassageAdapter(Context context, int requested,
			ArrayList<JSONObject> list,HashMap<String, Bitmap> _hmImages,String heading,OnClickListener clickListener) {
		super(context,requested, list);
		// TODO Auto-generated constructor stub
		
		jsonList = list;
		hmImages = _hmImages;
		this.heading= heading;
		this.clickListener=clickListener;
		System.out.println("Json List is    in adapter   "+jsonList);
		appContext = context;
		requestFor = requested;
		lflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View holder = convertView;
		//imageLoader = new ImageLoader(appContext);
		if (holder == null) {
			holder = lflater.inflate(R.layout.get_massege_list, null);
			
		} // fif ends
		
		try {
			JSONObject obj;
			obj = jsonList.get(position);
			if (obj != null) {
			
				String senderId = Utility.getUserPrefrence(appContext, "uId",
						Constant.PREFRENCEFILE);
					ImageView imageShared = (ImageView) holder
							.findViewById(R.id.imageGetMsg);
					TextView usernameSendMsg = (TextView) holder
							.findViewById(R.id.usernameGetMsg);
					TextView dateSendMsg = (TextView) holder
							.findViewById(R.id.dateGetMsg);
					TextView pendingTxt = (TextView) holder
							.findViewById(R.id.txtPending);
					Button addFriendBtn = (Button) holder
							.findViewById(R.id.addFriendBtn);
					Button removeFriendBtn = (Button) holder
							.findViewById(R.id.removeFriendBtn);
					
					
					addFriendBtn.setTag(String.valueOf(position));
					removeFriendBtn.setTag(String.valueOf(position));
					
					if(obj.has("photo")){
						obj = obj.getJSONObject("photo");
						dateSendMsg.setText(obj.getString("date"));
						if(obj.getString("senderid").equals(senderId)){
							addFriendBtn.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.GONE);
							pendingTxt.setVisibility(View.GONE);
							usernameSendMsg.setText(obj.getString("receivername"));
							imageShared.setBackgroundResource(R.drawable.send_arrow);
						}else if(obj.getString("receiverid").equals(senderId) && obj.getString("status").equals("10")){
							addFriendBtn.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.GONE);
							pendingTxt.setVisibility(View.GONE);
							usernameSendMsg.setText(obj.getString("sendername"));
							imageShared.setBackgroundResource(R.drawable.box_closed);
						}else if(obj.getString("receiverid").equals(senderId) && obj.getString("status").equals("20")){
							addFriendBtn.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.GONE);
							pendingTxt.setVisibility(View.GONE);
							usernameSendMsg.setText(obj.getString("sendername"));
							imageShared.setBackgroundResource(R.drawable.box_open);
						}
					}else if(obj.has("friend")){
						obj = obj.getJSONObject("friend");
						usernameSendMsg.setText(obj.getString("username"));
						dateSendMsg.setText(obj.getString("date"));
						imageShared.setBackgroundResource(R.drawable.add_freind_icon);
						if(obj.getString("senderid").equals(senderId)&& obj.getString("status").equals("10")){
							pendingTxt.setVisibility(View.GONE);
							addFriendBtn.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.GONE);
						}else if(obj.getString("senderid").equals(senderId)&& obj.getString("status").equals("99")){
							addFriendBtn.setVisibility(View.GONE);
							pendingTxt.setVisibility(View.VISIBLE);
							removeFriendBtn.setVisibility(View.GONE);
						}else if(obj.getString("receiverid").equals(senderId)&& obj.getString("status").equals("99")){
							pendingTxt.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.VISIBLE);
							addFriendBtn.setVisibility(View.VISIBLE);
						}else if(obj.getString("senderid").equals(senderId)&& obj.getString("status").equals("0")){
							addFriendBtn.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.GONE);
							pendingTxt.setVisibility(View.GONE);
						}else if(obj.getString("status").equals("10")){
							addFriendBtn.setVisibility(View.GONE);
							removeFriendBtn.setVisibility(View.GONE);
							pendingTxt.setVisibility(View.GONE);
						}
						
						
					}
					
					
					addFriendBtn.setOnClickListener(clickListener);
					removeFriendBtn.setOnClickListener(clickListener);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;
	}

	
	

}
