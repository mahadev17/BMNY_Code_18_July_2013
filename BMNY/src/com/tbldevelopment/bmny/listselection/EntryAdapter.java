package com.tbldevelopment.bmny.listselection;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.bmny.MyFriendActivity.OnCheckChangeChosenListener;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.bmny.R;

public class EntryAdapter extends ArrayAdapter<Item> {

	private Context context;
	private ArrayList<Item> items;
	private LayoutInflater vi;
	private boolean isCheckVisible;
	private ArrayList<Boolean> checkedlist;
	OnCheckChangeChosenListener listener;
	OnClickListener clickListener;
	int requestFor;

	public EntryAdapter(Context context,int requestFor,ArrayList<Item> items,boolean checkVisible, OnCheckChangeChosenListener listener,OnClickListener clickListener) {
		super(context,0, items);
		this.context = context;
		this.items = items;
		isCheckVisible = checkVisible;
		this.listener = listener;
		this.clickListener=clickListener;
		this.requestFor=requestFor;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		checkedlist = new ArrayList<Boolean>(items.size());
		for(int i=0;i<items.size();i++)
		{
			checkedlist.add(false);
		}
	}

	@Override
	public int getCount() {
	    // TODO Auto-generated method stub
	    return items.size();
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final Item i = items.get(position);
		if (i != null) {
			if(i.isSection()){
				SectionItem si = (SectionItem)i;
				v = vi.inflate(R.layout.list_item_section, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());
			}else{
				
				final EntryItem ei = (EntryItem)i;
				v = vi.inflate(R.layout.list_item_entry, null);
				final CheckBox checkbox = (CheckBox)v.findViewById(R.id.checkBox1);
				String userId = Utility.getUserPrefrence(context, "uId",
						Constant.PREFRENCEFILE);
				final TextView title = (TextView)v.findViewById(R.id.list_item_entry_title);
				final TextView subtitle = (TextView)v.findViewById(R.id.list_item_entry_summary);
				final Button addBtn = (Button)v.findViewById(R.id.addBtn);
				final Button removeBtn = (Button)v.findViewById(R.id.removeBtn);
				
				if(!isCheckVisible)
					checkbox.setVisibility(View.INVISIBLE);
				if(checkedlist.size()>0)
				{
					checkbox.setChecked(checkedlist.get(position));
				}
					
				checkbox.setTag(String.valueOf(position));  
				//checkbox.setChecked(checkedlist.get(position));
				addBtn.setTag(String.valueOf(position));
				removeBtn.setTag(String.valueOf(position));
				
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
					
						int pos = Integer.parseInt( buttonView.getTag().toString()) ;
						checkedlist.add(pos,isChecked);
						listener.onCheckChangeChosenListener(buttonView, isChecked);
					}
				});
				
				
					if (title != null) 
					{
						title.setText(ei.title);
					}	
					if(ei.status.equals("0")){
						addBtn.setVisibility(View.GONE);
						subtitle.setVisibility(View.GONE);
						removeBtn.setVisibility(View.GONE);
					}else if(ei.status.equals("99") && ei.senderId.equals(userId)){
						subtitle.setVisibility(View.VISIBLE);
						subtitle.setText("Pending...");
					}else if(ei.status.equals("99")&& ei.recieverId.equals(userId)){
						addBtn.setVisibility(View.VISIBLE);
						subtitle.setVisibility(View.GONE);
						removeBtn.setVisibility(View.VISIBLE);
					}
				
				
				
				addBtn.setOnClickListener(clickListener);
				removeBtn.setOnClickListener(clickListener);
			}
		}
		return v;
	}

}
