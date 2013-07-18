package com.tbldevelopment.bmny;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.bmny.RefreshableListView.OnRefreshListener;
import com.tbldevelopment.bmny.adapter.GetMassageAdapter;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.database.DataHelper;

public class GetMassageActivity extends Activity implements OnClickListener{
	private String senderId;
	private Context appContext;
	private View navigationView;
	private Button backBtn, clrBtn;

	private ArrayList<JSONObject> arrayJsonList;
	public HashMap<String, Bitmap> hashImages;
	Thread thread;
	private GetMassageAdapter itemListAdapter;
	private RefreshableListView list_view;
	public boolean fromUser;
	Bitmap bitmapPhoto;
	
	// Timer Variable
	private long startTime = 0L;
	private Handler myHandler = new Handler();
	private long timeInMillies = 0L;
	private long timeSwap = 0L;
	private long finalTime = 0L;
	private int timeCounter;
	private int runCounter;

	private LinearLayout imageLayout;
	private ImageView imageMSG;
	private TextView txtCounter;
	int requestFor;
	String sharedPhotoId, friendId;
	String sharedPhotoIdClr, friendIdClr;
	boolean refreshList = false;
	String getMessage;
	DataHelper db;
	ArrayList<NameValuePair> listNameValuePairs;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_get_massage);
		appContext=this;
		
		appContext = this;
		
		arrayJsonList = new ArrayList<JSONObject>();
		hashImages = new HashMap<String, Bitmap>();
		
		
		LayoutInflater inflater = LayoutInflater.from(appContext);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		TextView titleTxt = (TextView) findViewById(R.id.txtViewTitle);
		backBtn = (Button) findViewById(R.id.navigationLeftBtn);
		clrBtn = (Button) findViewById(R.id.navigationRightBtn);
		imageLayout=(LinearLayout) findViewById(R.id.image_popup_layout);
		txtCounter=(TextView) findViewById(R.id.txtTimerSet);
		
		//call dataHelper constructor 
		 db= new DataHelper(appContext);
		
		list_view = (RefreshableListView) findViewById(R.id.getMsgList);
		titleTxt.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.VISIBLE);
		clrBtn.setVisibility(View.VISIBLE);
		clrBtn.setBackgroundResource(R.drawable.clear_btn);
		
		titleTxt.setText("BMNY CHAT");
		backBtn.setOnClickListener(this);
		clrBtn.setOnClickListener(this);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			requestFor=bundle.getInt("requestFor");
		}
		
		senderId = Utility.getUserPrefrence(appContext, "uId",
				Constant.PREFRENCEFILE);
		
		
		if(Utility.getBooleanPrefrence(appContext, "getmsgdatabaseCreate", Constant.PREFRENCEFILE)==true){
			new GetMessage().execute();
		}else if(Utility.getBooleanPrefrence(appContext, "getmsgdatabaseCreate", Constant.PREFRENCEFILE)==false){
			Cursor cur=db.getMessages(senderId);
			
			if(cur==null)
			{
				Toast.makeText(getApplicationContext(), "Data not avalable", Toast.LENGTH_LONG).show();
			}
			else
			{
				cur.moveToFirst();
				if(cur.getCount()!=0)
				{
					getMessage=cur.getString(cur.getColumnIndex("getMessageData"));
					System.out.println("Friend List data is    "+getMessage);
					
				}
				
			}
			db.close();
			requestFor=Constant.LOCAL_FRIEND_LIST;
			new GetMessage().execute();
		}
		
		
		itemListAdapter = new GetMassageAdapter(appContext, 0,arrayJsonList,hashImages,"",clickListener);
		list_view.setAdapter(itemListAdapter);
		list_view.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				// TODO Auto-generated method stub
				
				try {
					
					JSONObject obj=arrayJsonList.get(pos);
					if(obj.has("photo")){
						obj = obj.getJSONObject("photo");
						String status=obj.getString("status");
						String recieverId=obj.getString("receiverid");
						timeCounter=Integer.parseInt(obj.getString("sharetime"));
						runCounter=timeCounter;
						String url=Constant.photoURL+obj.getString("photopath");
						bitmapPhoto = Utility.getBitmap(url);
					    imageMSG=(ImageView) findViewById(R.id.imageGetMsg);
						if(status.equals("10") && recieverId.equals(senderId) )
						{
							sharedPhotoId=obj.getString("sharedphotoid");
							list_view.setVisibility(View.GONE);
							((LinearLayout) findViewById(R.id.topbar)).setVisibility(View.GONE);
							imageLayout.setVisibility(View.VISIBLE);
							startTime = SystemClock.uptimeMillis();
						    myHandler.postDelayed(updateTimerMethod, 1000);
									                     
			                     if (bitmapPhoto == null) {
										imageMSG.setBackgroundResource(R.drawable.bg_3);
									} else {
										//imageMSG.setImageBitmap(bitmapPhoto);
										imageMSG.setBackgroundResource(R.drawable.bg_3);
									}
			                
						}else{
						}
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		
		
		
		});
				
		list_view.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(RefreshableListView listView) {
				// TODO Auto-generated method stub
				//arrayJsonList.clear();
				refreshList=true;
				requestFor=Constant.GETMSG;
				new GetMessage().execute();
			}
		});
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v.getId()==R.id.navigationLeftBtn){
			Intent i=new Intent(appContext,CameraMainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}else if(v.getId()==R.id.navigationRightBtn){
			if(arrayJsonList.size()==0){
				Utility.ShowAlertWithMessage(appContext, "Alert", "Data Already Clear");
			}else{
				listNameValuePairs= new ArrayList<NameValuePair>();
				listNameValuePairs.add(new BasicNameValuePair("userid",senderId));
				listNameValuePairs.add(new BasicNameValuePair("sharedids",sharedPhotoIdClr));
				listNameValuePairs.add(new BasicNameValuePair("friendids",friendIdClr));
				requestFor=Constant.CLEAR_FEED;
				new GetMessage().execute();
			}
			
		}
	}
	
	
	
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			int pos = Integer.parseInt(v.getTag().toString());
			
			try {
				JSONObject obj=arrayJsonList.get(pos);
				if(obj.has("friend")){
					obj = obj.getJSONObject("friend");
					friendId=obj.getString("friendid");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(v.getId()==R.id.addFriendBtn){
				requestFor = Constant.APPROVE_FRIEND_REQUEST;
				new GetMessage().execute();
			}else if(v.getId()==R.id.removeFriendBtn){
				requestFor = Constant.REJECT_FRIEND_REQUEST;
				new GetMessage().execute();
			}
			
		}
	};
	
	
	public class GetMessage extends AsyncTask<String, Void, String> {
		
		ProgressDialog applicationDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			if(requestFor!=Constant.LOCAL_FRIEND_LIST){
				applicationDialog = ProgressDialog.show(appContext, "",
						"Please Wait ");
				applicationDialog.setCancelable(true);
			}
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String url=null;
			String response = null;
			if(requestFor==Constant.GETMSG){
				url = Constant.serverURL + "GetSharedPhotos&userid="+senderId;
				response = Utility.findJSONFromUrl(url);
			}else if(requestFor==Constant.LOCAL_FRIEND_LIST){
				response=getMessage;
			}else if(requestFor==Constant.DELETE_SHARED_PHOTO){
				url = Constant.serverURL +"DeleteSharedPhoto&sharedphotoid="+sharedPhotoId;
				response = Utility.findJSONFromUrl(url);
			}else if(requestFor==Constant.APPROVE_FRIEND_REQUEST){
				url = Constant.serverURL + "ApproveFriendRequest&friendid="+friendId;
				response = Utility.findJSONFromUrl(url);
			}else if(requestFor==Constant.REJECT_FRIEND_REQUEST){
				url = Constant.serverURL + "RejectFriends&friendid="+friendId;
				response = Utility.findJSONFromUrl(url);
			}else if(requestFor==Constant.CLEAR_FEED){
				url = Constant.serverURL +"ClearFeed";
				response = Utility.postParamsAndfindJSON(url, listNameValuePairs);
				listNameValuePairs.clear();
			}

			if (response == null) {
				return response;
			}else if(requestFor==Constant.DELETE_SHARED_PHOTO){
				return response;
			}else if(requestFor==Constant.APPROVE_FRIEND_REQUEST){
				return response;
			}else if(requestFor==Constant.REJECT_FRIEND_REQUEST){
				return response;
			}else if(requestFor==Constant.CLEAR_FEED){
				return response;
			}else if(requestFor==Constant.GETMSG || requestFor==Constant.LOCAL_FRIEND_LIST){

				try {
					
					arrayJsonList.clear();
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("results");
					JSONObject jsonResponse = (JSONObject)jsonArray.get(0);
					JSONArray jsonArray1=jsonResponse.getJSONArray("photos");
					jsonResponse = (JSONObject)jsonArray.get(1);
					JSONArray jsonArray2=jsonResponse.getJSONArray("friends");
					
					getMessage=jsonObject.toString();
					
					if(Utility.getBooleanPrefrence(appContext, "getmsgdatabaseCreate", Constant.PREFRENCEFILE)==true){
						db.deleteAllMessages(senderId);
						inserGetMessage();
						Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", false, Constant.PREFRENCEFILE);
					}else if(Utility.getBooleanPrefrence(appContext, "getmsgdatabaseCreate", Constant.PREFRENCEFILE)==false){
						db.updateGetMessage(senderId,getMessage);
					}
					
					
					
					System.out.println("json Array is  "+jsonArray);
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray1.length(); i++) {
							JSONObject jsonResponse1 = (JSONObject)jsonArray1.get(i);
							arrayJsonList.add(jsonResponse1);
							
							jsonResponse1 = jsonResponse1.getJSONObject("photo");
							if(sharedPhotoIdClr==null){
								sharedPhotoIdClr = jsonResponse1.getString("sharedphotoid");
							}else{
								sharedPhotoIdClr = sharedPhotoIdClr+","+jsonResponse1.getString("sharedphotoid");
								System.out.println("Share Id List is  "+sharedPhotoIdClr);
							}
							
						}
						
						for (int i = 0; i < jsonArray2.length(); i++) {
							JSONObject jsonResponse1 = (JSONObject)jsonArray2.get(i);
							arrayJsonList.add(jsonResponse1);
							
							jsonResponse1 = jsonResponse1.getJSONObject("friend");
							if(friendIdClr==null){
								friendIdClr = jsonResponse1.getString("friendid");
							}else{
								friendIdClr = friendIdClr+","+jsonResponse1.getString("friendid");
								System.out.println("Share Id List is  "+friendIdClr);
							}
						}
						
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Result is  " + response);
				return response;
			}
			
			return response;
		}

		protected void onPostExecute(String result) {
			if(requestFor!=Constant.LOCAL_FRIEND_LIST){
				applicationDialog.dismiss();
			}
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, "Alert", "Please check network");
			}else if(result.equals("0")){
				Utility.ShowAlertWithMessage(appContext, "Alert", "Error");
			}else if(requestFor==Constant.APPROVE_FRIEND_REQUEST){
				db.approveFriendRequest(friendId);
				Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
				requestFor=Constant.GETMSG;
				new GetMessage().execute();
			} else if(requestFor==Constant.DELETE_SHARED_PHOTO){
				Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
				requestFor=Constant.GETMSG;
				new GetMessage().execute();
			} else if(requestFor==Constant.REJECT_FRIEND_REQUEST){
				db.rejectFriendRequest(friendId);
				Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
				requestFor=Constant.GETMSG;
				new GetMessage().execute();
			} else if(requestFor==Constant.CLEAR_FEED){
				Toast.makeText(appContext, "Message deleted successfully", Toast.LENGTH_SHORT).show();
				Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
				requestFor=Constant.GETMSG;
				new GetMessage().execute();
			}else if(requestFor==Constant.GETMSG || requestFor==Constant.LOCAL_FRIEND_LIST){
				if(refreshList==true){
					list_view.completeRefreshing();
					refreshList=false;
				}
				
			itemListAdapter = new GetMassageAdapter(appContext, 0,arrayJsonList,hashImages,"",clickListener);
			list_view.setAdapter(itemListAdapter);
			itemListAdapter.notifyDataSetChanged();
			thread = new Thread(downloadImages);
			thread.start();
			
			}
		}
	}


	public Runnable runOnMain = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			itemListAdapter.notifyDataSetChanged();
		}
	};
	
	Runnable downloadImages = new Runnable() {
		 
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for (int i = 0; i < arrayJsonList.size(); i++) {
				
				try {
					
					JSONObject obj=arrayJsonList.get(i);
					if(obj.has("photo")){
						obj = obj.getJSONObject("photo");
						String photopath=Constant.photoURL+obj.getString("photopath");
						Bitmap bitmapImage = Utility.getBitmap(photopath);
						hashImages.put(obj.getString("sharedphotoid"), bitmapImage);
						bitmapImage = null;
					}
					
					System.gc();
					runOnUiThread(runOnMain);
					
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					runOnUiThread(runOnMain);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
	};


	
	
/*	 public class MalibuCountDownTimer extends CountDownTimer
     {

         public MalibuCountDownTimer(long startTime, long interval)
             {
                 super(startTime, interval);
                 
             }

         @Override
         public void onFinish()
             {
        	 	imageLayout.setVisibility(View.GONE);
                list_view.setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.topbar)).setVisibility(View.VISIBLE);
                countDownTimer.cancel();
                timerHasStarted = false;
                requestFor=Constant.DELETE_SHARED_PHOTO;
                new GetMessage().execute();
             }

         @Override
         public void onTick(long millisUntilFinished)
             {
                 //txtTimer.setText("Time remain:" +millisUntilFinished);
                 timeElapsed = startTime - millisUntilFinished;
                 counterTxt.setText("" +counter);
                 counter--;
             }
     }*/

	 
	 private void inserGetMessage()
		{
			long id = db.createGetMassage(senderId,getMessage);
			System.out.println("UserId is  "+id);
			db.close();
		}
	 
	 private Runnable updateTimerMethod = new Runnable() {

		  public void run() {
		   timeInMillies = SystemClock.uptimeMillis() - startTime;
		   finalTime = timeSwap + timeInMillies;
		   
		   myHandler.postDelayed(this, 1000);
		   txtCounter.setText(""+timeCounter);
		   timeCounter=timeCounter-1;
		   if(finalTime>=runCounter){
			   myHandler.removeCallbacks(updateTimerMethod);
			   imageLayout.setVisibility(View.GONE);
               list_view.setVisibility(View.VISIBLE);
			   ((LinearLayout) findViewById(R.id.topbar)).setVisibility(View.VISIBLE);
               requestFor=Constant.DELETE_SHARED_PHOTO;
               new GetMessage().execute();   
		   }
		  }

		 };
	 
 
}
