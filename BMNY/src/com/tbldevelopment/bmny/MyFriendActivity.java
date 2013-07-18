package com.tbldevelopment.bmny;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tbldevelopment.bmny.listselection.EntryAdapter;
import com.tbldevelopment.bmny.listselection.EntryItem;
import com.tbldevelopment.bmny.listselection.Item;
import com.tbldevelopment.bmny.listselection.SectionItem;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.database.DataHelper;

public class MyFriendActivity extends Activity implements OnClickListener {

	private View navigationView;
	private Button addFriendBtn;
	private Button backBtn;
	private Context appContext;
	LinearLayout add_view_layout;
	private Button addFriendSubmitBtn;
	//private String friendName;
	private String loginUserId;
	private EditText friendNameEdt;
	private ListView friendListView;
	private ArrayList<JSONObject> arrayJsonList;
	private EntryAdapter adaptorFriendList;
	//private ItemListAdapter itemListAdapter;
	private ArrayList<Item> listFriendRecord;
	private String friendId,currentStatus;
	boolean status;
	private ArrayList<String> receiverlist;
	String timeCounter;
	final CharSequence[] items = { "DELETE FRIEND", "BLOCK FRIEND"};
	final CharSequence[] itemsList = {"DELETE FRIEND","UNBLOCK FRIEND" };
	private static final String[] SECTIONTITLE = { "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };
	int requestFor;
	int lastkRequestFor;
	Bundle bundle;
	private boolean isCheckVisible;
	private Bitmap bitmap;
	private RelativeLayout layoutbottombar;
	private JSONArray arrayJsonDatabase;
	String friendData;
	DataHelper db;
	
	@SuppressWarnings({ "null", "unused" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_friend);

		appContext = this;
		listFriendRecord = new ArrayList<Item>();
		db= new DataHelper(appContext);
		
		
		LayoutInflater inflater = LayoutInflater.from(appContext);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		layoutbottombar = (RelativeLayout) findViewById(R.id.layoutSendBottom);
		arrayJsonList = new ArrayList<JSONObject>();
		receiverlist = new ArrayList<String>();
		add_view_layout = (LinearLayout) findViewById(R.id.add_view_layout);
		addFriendSubmitBtn = (Button) findViewById(R.id.addFriendBtn);
		TextView titleTxt = (TextView) findViewById(R.id.txtViewTitle);
		addFriendBtn = (Button) findViewById(R.id.navigationRightBtn);
		backBtn = (Button) findViewById(R.id.navigationLeftBtn);
		friendListView = (ListView) findViewById(R.id.friendList);
		loginUserId = Utility.getUserPrefrence(appContext, "uId",
				Constant.PREFRENCEFILE);

		addFriendBtn.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.VISIBLE);
		titleTxt.setVisibility(View.VISIBLE);

		bundle = getIntent().getExtras();
		if (bundle != null) {
			requestFor = bundle.getInt("requestFor");
			bitmap = Utility.getBitmap();	
		}
		
		if (requestFor == Constant.SENDIMAGE) {
			titleTxt.setText("Send To..");
			isCheckVisible = true;
			lastkRequestFor=requestFor;
			addFriendBtn.setVisibility(View.GONE);
			layoutbottombar.setVisibility(View.VISIBLE);
		}else{
			titleTxt.setText("My Friend");
		}
		
		
		if(Utility.getBooleanPrefrence(appContext, "databaseCreate", Constant.PREFRENCEFILE)==true){
			new GetFriends().execute();
		}else if(Utility.getBooleanPrefrence(appContext, "databaseCreate", Constant.PREFRENCEFILE)==false){
			
			Cursor cur=db.getFriend(loginUserId);
			
			if(cur==null)
			{
				Toast.makeText(getApplicationContext(), "Not have friend.", Toast.LENGTH_LONG).show();
			}
			else
			{
				if(cur.getCount()!=0)
					
				{
					System.out.println("Count is   "+cur.getCount());
					arrayJsonDatabase = new JSONArray();
					try 
					{
					
					
					String friendInfo = null;
					while(cur.moveToNext()) 
					{
						JSONObject objFriend = new JSONObject();
						JSONObject objFr = new JSONObject();
						objFr.put("username", cur.getString(cur.getColumnIndex("friendName")));
						objFr.put("email", cur.getString(cur.getColumnIndex("friendEmail")));
						objFr.put("password", cur.getString(cur.getColumnIndex("friendPassword")));
						objFr.put("mobile", cur.getString(cur.getColumnIndex("friendMobile")));
						objFr.put("date", cur.getString(cur.getColumnIndex("friendDate")));
						objFr.put("status", cur.getString(cur.getColumnIndex("friendStatus")));
						objFr.put("website", cur.getString(cur.getColumnIndex("website")));
						objFr.put("friendid", cur.getString(cur.getColumnIndex("friendid")));
						objFr.put("senderid", cur.getString(cur.getColumnIndex("senderId")));
						objFr.put("receiverid", cur.getString(cur.getColumnIndex("recieverId")));
						objFr.put("userid", cur.getString(cur.getColumnIndex("recieverId")));
					
					 	objFriend.put("friend", objFr);
					 	arrayJsonDatabase.put(objFriend);
					 	
					 	
					 	System.out.println("objFriend json   "+objFriend);
					}
					
					requestFor=Constant.LOCAL_FRIEND_LIST;
				 	new GetFriends().execute();
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				
				
			}
			db.close();
			
		}
		
		

		addFriendBtn.setOnClickListener(this);
		addFriendSubmitBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);

		friendListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int pos, long id) {
						// TODO Auto-generated method stub
						
						if(requestFor!=Constant.SENDIMAGE){
							if (!listFriendRecord.get(pos).isSection()) {
								EntryItem item = (EntryItem) listFriendRecord
										.get(pos);
								friendId = item.friendid;
								currentStatus=item.currentStatus;
								System.out.println("Current Status  "+currentStatus);
								status=item.isBlock;
							}
							if(currentStatus.equals("99")){
								Utility.ShowAlertWithMessage(appContext," Alert ", "Request is not accept");
							}else if(status){
								showDialogUnblocked();
							}else{
								showDialog();
							}

							return true;
						}else{
							return false;
						}
						
						
					}
				});

		((Button) findViewById(R.id.buttonSendView))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(timeCounter==null){
							timeCounter="1";
						}else{
							timeCounter=Utility.getUserPrefrence(appContext, "timeCounter", Constant.PREFRENCEFILE).trim();
						}
						
						if (receiverlist.size() == 0) {
							Utility.ShowAlertWithMessage(MyFriendActivity.this,
									"Alert",
									"Please select friend to send message.");
							return;
						}
						for (int i = 0; i < receiverlist.size(); i++) {
							if (i == 0) {
								friendId = receiverlist.get(i);
							} else {
								friendId = friendId + "," + receiverlist.get(i);
							}
						}
						
						new SendMessageTask().execute("");
					}
				});
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (view.getId() == R.id.navigationRightBtn) {
			if (add_view_layout.getVisibility() == View.VISIBLE) {
				add_view_layout.setVisibility(View.GONE);
			} else {
				add_view_layout.setVisibility(View.VISIBLE);
			}
		}

		if (view.getId() == R.id.addFriendBtn) {

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(addFriendSubmitBtn.getWindowToken(), 0);

			friendNameEdt = (EditText) findViewById(R.id.addFriendEdt);
			if (friendNameEdt.getText().toString().trim().length() == 0) {
				Utility.ShowAlertWithMessage(appContext, "Alert",
						"Enter User Name");
			}

			new AddFriend().execute();
		}
		if (view.getId() == R.id.navigationLeftBtn) {
			finish();
		}
	}

	public class AddFriend extends AsyncTask<Void, Void, String> {

		String result;
		ProgressDialog applicationDialog;
		String url;

		protected void onPreExecute() {
			super.onPreExecute();
			applicationDialog = ProgressDialog.show(appContext, "",
					"Please Wait ");
			applicationDialog.setCancelable(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> listNameValuePairs = new ArrayList<NameValuePair>();
			url = Constant.serverURL + "AddFriend";

			listNameValuePairs.add(new BasicNameValuePair("senderid",loginUserId));
			listNameValuePairs.add(new BasicNameValuePair("username",friendNameEdt.getText().toString().trim()));
			result = Utility.postParamsAndfindJSON(url, listNameValuePairs);
			listNameValuePairs.clear();

			return result;
		}

		protected void onPostExecute(String result) {
			applicationDialog.dismiss();
			System.out.println("result is " + result);
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, "Alert", "Please check network");
			} else if (result.contains("-1")) {
				Utility.ShowAlertWithMessage(appContext, "Alert", "You can't add yourself");
			} else if (result.contains("0")) {
				Utility.ShowAlertWithMessage(appContext, "Alert", "Error to post on server");
			} else if (result.contains("1")) {
				friendNameEdt.setText("");
				add_view_layout.setVisibility(View.GONE);
				requestFor=Constant.GETFRIEND;
				Utility.setBooleanPrefrence(appContext, "databaseCreate", true, Constant.PREFRENCEFILE);
				new GetFriends().execute();
			}

		}

	}

	public class GetFriends extends AsyncTask<String, Void, String> {
	
		ProgressDialog applicationDialog;
		String loginUserName=Utility.getUserPrefrence(appContext, "userName",Constant.PREFRENCEFILE);
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(requestFor!=Constant.LOCAL_FRIEND_LIST){
				applicationDialog = ProgressDialog.show(appContext, "",
						"Please Wait ");
				applicationDialog.setCancelable(true);
				listFriendRecord.clear();
				arrayJsonList.clear();
			}
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String url;
			String response=null;
			if (requestFor == Constant.GETFRIEND
					|| requestFor == Constant.SENDIMAGE) {
				url = Constant.serverURL + "GetFriends&userid=" +loginUserId;
				response = Utility.findJSONFromUrl(url);
				friendData=response;
				System.out.println("Responce value is  "+response);
			}else if (requestFor == Constant.DELETEFRIEND) {
				url = Constant.serverURL + "DeleteFriend&friendid=" + friendId;
				response = Utility.findJSONFromUrl(url);
			} else if (requestFor == Constant.BLOCKFRIEND) {
				url = Constant.serverURL + "BlockFriend&friendid=" + friendId;
				response = Utility.findJSONFromUrl(url);
			} else if (requestFor == Constant.APPROVE_FRIEND_REQUEST) {
				url = Constant.serverURL + "ApproveFriendRequest&friendid="+friendId;
				response = Utility.findJSONFromUrl(url);
			} else if (requestFor == Constant.REJECT_FRIEND_REQUEST) {
				url = Constant.serverURL + "RejectFriends&friendid="+friendId;
				response = Utility.findJSONFromUrl(url);
			} else if (requestFor == Constant.UNBLOCKFRIEND) {
				ArrayList<NameValuePair> listNameValuePairs = new ArrayList<NameValuePair>();
				listNameValuePairs.add(new BasicNameValuePair("friendid",
						friendId));
				url = Constant.serverURL + "UnBlockFriend";
				response = Utility.postParamsAndfindJSON(url,
						listNameValuePairs);
			}
		
			if (requestFor == Constant.DELETEFRIEND) {
					return response;
				} else if (requestFor == Constant.BLOCKFRIEND) {
					return response;
				} else if (requestFor == Constant.APPROVE_FRIEND_REQUEST) {
					return response;
				} else if (requestFor == Constant.UNBLOCKFRIEND) {
					return response;
				}  else if (requestFor == Constant.REJECT_FRIEND_REQUEST) {
					return response;
				} else if (requestFor == Constant.GETFRIEND
						|| requestFor == Constant.SENDIMAGE||requestFor==Constant.LOCAL_FRIEND_LIST) {
					try {
						arrayJsonList.clear();
						JSONObject jsonObject;
						JSONArray jsonArray;
						if (requestFor == Constant.LOCAL_FRIEND_LIST) {
							jsonArray = arrayJsonDatabase;
						}else
						{
							jsonObject = new JSONObject(response);
							jsonArray = jsonObject.getJSONArray("friends");
							System.out.println("Array  " + jsonArray);
						}
					
						int first = 0, count = 0;
						if (jsonArray != null) {
							for (int j = 0; j < SECTIONTITLE.length; j++) 
							{
								first = j;
								for (int i = 0; i < jsonArray.length(); i++) {
									jsonObject = jsonArray.getJSONObject(i);
									jsonObject = jsonObject.getJSONObject("friend");
									if (Integer.parseInt(jsonObject
											.getString("status")) > 0)
									{
										if(requestFor==Constant.LOCAL_FRIEND_LIST && lastkRequestFor==Constant.SENDIMAGE && jsonObject
												.getString("status").equals("99") ){
											
										}else{
										String compare = getChart(jsonObject
												.getString("username")
												.toUpperCase());
										if (compare.startsWith(SECTIONTITLE[j])) {

											if (first == j) {
												listFriendRecord
														.add(new SectionItem(
																SECTIONTITLE[j]));
												first = -1;
											}
											count += 1;
											
												listFriendRecord
												.add(new EntryItem(
														count,jsonObject.getString("userid"),jsonObject.getString("senderid"),jsonObject.getString("receiverid"),
														jsonObject
																.getString("username"),
														jsonObject
																.getString("username"),
																jsonObject
																.getString("status"),
														false,
														false,
														jsonObject
																.getString("email"),
														jsonObject
																.getString("mobile"),
														jsonObject
																.getString("friendid"),
														jsonObject
																.getString("date"),jsonObject
																.getString("status")));
											}
										
											// arrayJsonList.add(jsonObject);
										}

									} else {
										String blockUserName = jsonObject
												.getString("username").toString();
										if(!isItemSelected(blockUserName)){
											arrayJsonList.add(jsonObject);
										}
											
									}
								}
							}

						if(requestFor!=Constant.SENDIMAGE){
							for (int i = 0; i < arrayJsonList.size(); i++) {
								
								JSONObject json = arrayJsonList.get(i);
								if (i == 0) {
									listFriendRecord.add(new SectionItem(
											"Block"));
								}
								count += 1;
								
									listFriendRecord.add(new EntryItem(count,
											json.getString("userid"), json
													.getString("senderid"),
											json.getString("receiverid"), json
													.getString("username"),
											json.getString("username"),
											json.getString("status"),
											true, false, json
													.getString("email"), json
													.getString("mobile"), json
													.getString("friendid"),
											json.getString("date"), json
													.getString("status")));
							}
						}
					}
						jsonArray = null;
						jsonObject = null;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return response;
				}

			
			return response;
		}

		public boolean isItemSelected(String name){
			boolean isfound =false;
			for (int i = 0; i < arrayJsonList.size(); i++) {
				JSONObject obj = arrayJsonList.get(i);
				  try 
				  {
					if(obj.get("username").toString().equalsIgnoreCase(name))
					  {
						  isfound = true;
						  break;
					  }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return isfound;
		}
		
		protected void onPostExecute(String result) {
			if(requestFor!=Constant.LOCAL_FRIEND_LIST){
				applicationDialog.dismiss();
			}
			if(requestFor==Constant.LOCAL_FRIEND_LIST){
				adaptorFriendList = new EntryAdapter(MyFriendActivity.this,requestFor,
						listFriendRecord, isCheckVisible, listener,clickListener);
				friendListView.setAdapter(adaptorFriendList);
				adaptorFriendList.notifyDataSetChanged();
			}else if (result == null) {
				Toast.makeText(appContext, "Please Check Network",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (requestFor == Constant.DELETEFRIEND) {
				Toast.makeText(appContext, "Data Deleted", Toast.LENGTH_SHORT)
						.show();
				db.deleteFriend(friendId);
				requestFor = Constant.GETFRIEND;
				new GetFriends().execute();
			} else if (requestFor == Constant.BLOCKFRIEND) {
				Toast.makeText(appContext, "User Blocked Succeccfully",
						Toast.LENGTH_SHORT).show();
				db.blockFrind(loginUserId,friendId);
				requestFor = Constant.GETFRIEND;
				new GetFriends().execute();
			} else if (requestFor == Constant.APPROVE_FRIEND_REQUEST) {
				Toast.makeText(appContext, "Friend Add Succeccfully",
						Toast.LENGTH_SHORT).show();
				db.approveFriendRequest(friendId);
				requestFor = Constant.GETFRIEND;
				new GetFriends().execute();
			} else if (requestFor == Constant.REJECT_FRIEND_REQUEST) {
				Toast.makeText(appContext, "Friend Request Canceled Successfully",
						Toast.LENGTH_SHORT).show();
				db.rejectFriendRequest(friendId);
				requestFor = Constant.GETFRIEND;
				new GetFriends().execute();
			}else if (requestFor == Constant.UNBLOCKFRIEND) {
				Toast.makeText(appContext, "User UnBlocked Succeccfully",
						Toast.LENGTH_SHORT).show();
				db.unblockFrind(loginUserId,friendId);
				requestFor = Constant.GETFRIEND;
				new GetFriends().execute();
			} else if (requestFor == Constant.GETFRIEND
					|| requestFor == Constant.SENDIMAGE || requestFor==Constant.LOCAL_FRIEND_LIST) {
				if (arrayJsonList.size() == 0 && listFriendRecord.size() == 0) {
					Toast.makeText(appContext, "Data Not Found",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (requestFor == Constant.SENDIMAGE) {
					isCheckVisible = true;
				}
				
				if(Utility.getBooleanPrefrence(appContext, "databaseCreate", Constant.PREFRENCEFILE)==true){
					db.deleteAllFriend(loginUserId);
					inserFriendInfo();
					Utility.setBooleanPrefrence(appContext, "databaseCreate", false, Constant.PREFRENCEFILE);
				}				
				
				adaptorFriendList = new EntryAdapter(MyFriendActivity.this,requestFor,
						listFriendRecord, isCheckVisible, listener,clickListener);
				friendListView.setAdapter(adaptorFriendList);
				adaptorFriendList.notifyDataSetChanged();
				
			}

		}
	}

	OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//int pos = (Integer)v.getTag();
			int pos = Integer.parseInt(v.getTag().toString());
			if (!listFriendRecord.get(pos).isSection()) {
				EntryItem item = (EntryItem) listFriendRecord.get(pos);
				friendId = item.friendid;
			}
			
			if(v.getId()==R.id.addBtn){
				
				requestFor = Constant.APPROVE_FRIEND_REQUEST;
				new GetFriends().execute();
			}else if(v.getId()==R.id.removeBtn){
				requestFor = Constant.REJECT_FRIEND_REQUEST;
				new GetFriends().execute();
			}
			
			
		}
	};
	
	OnCheckChangeChosenListener listener = new OnCheckChangeChosenListener() {

		@Override
		public void onCheckChangeChosenListener(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			int pos = Integer.parseInt(buttonView.getTag().toString());
			if (!listFriendRecord.get(pos).isSection()) {
				EntryItem item = (EntryItem) listFriendRecord.get(pos);
				if (isChecked) {
					receiverlist.add(item.recieverId);
				} else {
					receiverlist.remove(item.recieverId);
				}
			}
		}
	};

	private String getChart(String character) {
		for (int m = 0; m < character.length(); m++) {
			String c = String.valueOf(character.charAt(m));
			for (int n = 0; n < SECTIONTITLE.length; n++) {
				if (c.startsWith(SECTIONTITLE[n])) {
					m = c.length();
					character = c;

					return character;
				}
			}
		}
		return character;
	}

	public class SendMessageTask extends AsyncTask<String, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();

			applicationDialog = ProgressDialog.show(MyFriendActivity.this, "",
					"Please Wait ");
			applicationDialog.setCancelable(true);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String userId = Utility.getUserPrefrence(appContext, "uId",
					Constant.PREFRENCEFILE);
			
				ArrayList<NameValuePair> listNameValuePairs = new ArrayList<NameValuePair>();
				listNameValuePairs
						.add(new BasicNameValuePair("fids", friendId));
				listNameValuePairs.add(new BasicNameValuePair("photo", Utility
						.encodeTobase64(bitmap)));
				listNameValuePairs.add(new BasicNameValuePair("time",timeCounter));
				listNameValuePairs.add(new BasicNameValuePair("userid", userId));
				String url = Constant.serverURL + "SharePhotoWithFriends";
				String response = Utility.postParamsAndfindJSON(url,
						listNameValuePairs);
				return response;
			
		}

		protected void onPostExecute(String result) {
			applicationDialog.cancel();
			SendMessageTask.this.cancel(true);
			if(result==null){
				Utility.ShowAlertWithMessage(MyFriendActivity.this, "Alert",
						"Please Check Network");
			}else if (result.contains("0")) {
				Utility.ShowAlertWithMessage(MyFriendActivity.this, "Alert",
						"Sending Failed");
				return;
			}else if (result.contains("1")) {
				Intent i=new Intent(appContext,GetMassageActivity.class);
				i.putExtra("requestFor", Constant.GETMSG);
				startActivity(i);
				finish();
			}
		}
	}

	public void showDialog() {

		AlertDialog.Builder builder3 = new AlertDialog.Builder(appContext);
		builder3.setTitle("Pick your choice").setItems(items,
				new DialogInterface.OnClickListener() {

			
			
					@Override
					public void onClick(DialogInterface dialog, int id) {

						
						// TODO Auto-generated method stub
						if (items[id].equals("DELETE FRIEND")) {
							requestFor = Constant.DELETEFRIEND;
							new GetFriends().execute();
						} else if (items[id].equals("BLOCK FRIEND")) {
							requestFor = Constant.BLOCKFRIEND;
							new GetFriends().execute();
						}

					}

				});

		builder3.show();
	}
	
	public void showDialogUnblocked() {

		AlertDialog.Builder builder3 = new AlertDialog.Builder(appContext);
		builder3.setTitle("Pick your choice").setItems(itemsList,
				new DialogInterface.OnClickListener() {

			@Override
					public void onClick(DialogInterface dialog, int id) {

						
						// TODO Auto-generated method stub
						if (itemsList[id].equals("DELETE FRIEND")) {
							requestFor = Constant.DELETEFRIEND;
							new GetFriends().execute();
						} else if (itemsList[id].equals("UNBLOCK FRIEND")) {
							requestFor = Constant.UNBLOCKFRIEND;
							new GetFriends().execute();
						}

					}

				});

		builder3.show();
	}
	
	public interface OnCheckChangeChosenListener {
		void onCheckChangeChosenListener(CompoundButton buttonView,
				boolean isChecked);
	}
	
	 private void inserFriendInfo()
		{
		 JSONObject jsonObject = null;
		 
		try {
			jsonObject = new JSONObject(friendData);
			JSONArray jsonArray = jsonObject.getJSONArray("friends");
			
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				jsonObject = jsonObject.getJSONObject("friend");
				
				
				String friendName=jsonObject.getString("username");
				String friendEmail=jsonObject.getString("email");
				String friendPassword=jsonObject.getString("password");
				String friendMobile=jsonObject.getString("mobile");
				String friendDate=jsonObject.getString("date");
				String friendStatus=jsonObject.getString("status");
				String website=jsonObject.getString("website");
			 	String friendId=jsonObject.getString("friendid");
			 	String senderId=jsonObject.getString("senderid");
			 	String recieverId=jsonObject.getString("receiverid");
			 	
			 	db.addFriend(loginUserId,friendName, friendEmail,friendPassword,friendMobile,friendDate,friendStatus,website,friendId,senderId,recieverId);
			 	
			}
			
			
		 	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
			
			db.close();
		}
	
}
