package com.tbldevelopment.bmny;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.bmny.adapter.ContactListAdaptor;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;

public class FindFriendActivity extends Activity implements OnClickListener{
	private View navigationView;
	private Button addFriendBtn;
	private Button backBtn,doneBtn;
	private Context appContext;
	ListView contactList;
	private ContactListAdaptor appListAdapter;
	private ArrayList<HashMap<String, String>> contactHasList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_friend);
		
		appContext = this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		contactHasList = new ArrayList<HashMap<String, String>>();
		
		TextView titleTxt = (TextView) findViewById(R.id.txtViewTitle);
		contactList=(ListView) findViewById(R.id.friendList);
		
		
		backBtn = (Button) findViewById(R.id.navigationLeftBtn);
		doneBtn = (Button) findViewById(R.id.navigationRightBtn);
		
		titleTxt.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.VISIBLE);
		doneBtn.setVisibility(View.VISIBLE);
		doneBtn.setBackgroundResource(R.drawable.done_btn);
		
		
		titleTxt.setText("Add Friends");
		backBtn.setOnClickListener(this);
		doneBtn.setOnClickListener(this);
		
		appListAdapter = new ContactListAdaptor(appContext,
				R.layout.contact_list, contactHasList);
		contactList.setAdapter(appListAdapter);
		
		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
		while (cursor.moveToNext()) {
			String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			
             phoneNumber = phoneNumber.replaceAll("\\D+","");
             System.out.println("Only Nuber Is  "+phoneNumber);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("contactName",name);
			map.put("phoneNo",phoneNumber);		
			map.put("isSelected", "0");
			contactHasList.add(map);
			map=null;
			System.out.println("name is   " + name);
			System.out.println("no is   " + phoneNumber);
		}
		//appListAdapter.notifyDataSetChanged();

		contactList.setOnItemClickListener(new OnItemClickListener() {
		       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		           HashMap<String, String> hmConcact = contactHasList.get(position);
		           if (hmConcact.get("isSelected").equalsIgnoreCase("0")) {
		        	   hmConcact.put("isSelected", "1");
				   }else{
					   hmConcact.put("isSelected", "0");
				   }
		           contactHasList.set(position, hmConcact);
		           appListAdapter.notifyDataSetChanged();
		    	  
		       }
		   });
		
	
		
	}

	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}else if(v.getId()==R.id.navigationRightBtn){
			new FindFriends().execute();
		}
		
	}
	
	
	
	
	public class FindFriends extends AsyncTask<String, Void, String> {
		
		ProgressDialog applicationDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			applicationDialog = ProgressDialog.show(appContext, "",
					"Please Wait ");
			applicationDialog.setCancelable(true);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String userId = Utility.getUserPrefrence(appContext, "uId",Constant.PREFRENCEFILE);
			String url;
			String response=null;
			String phoneNumbers = "";
			for (int i = 0; i < contactHasList.size(); i++) {
				HashMap<String, String> hmContact = contactHasList.get(i);
				if (hmContact.get("isSelected").equalsIgnoreCase("1")) {
					if( phoneNumbers != ""){
						phoneNumbers = phoneNumbers + ","+hmContact.get("phoneNo");
					}else{
						phoneNumbers = hmContact.get("phoneNo");
					}
							
				}
			}
			url = Constant.serverURL + "FindFriendsByMobile&userid="+userId+"&mobilenumber="+phoneNumbers;
			response = Utility.findJSONFromUrl(url);
			
			return response;
					}

		protected void onPostExecute(String result) {
			applicationDialog.dismiss();
			if (result == null) {
				Toast.makeText(appContext, "Please Check Network",
						Toast.LENGTH_SHORT).show();
				return;
			}else{
				System.out.println("res  "+result);
				Toast.makeText(appContext, "You Added Succesfully",
						Toast.LENGTH_SHORT).show();
			}



		}
	}
	
}
