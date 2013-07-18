package com.tbldevelopment.bmny;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.database.DataHelper;

public class AddUserNameActivity extends Activity implements OnClickListener{
	private View navigationView;
	private Context appContext;
	Button backBtn;
	int requestFor;
	private String userName,password,userEmail,phoneNo,website;
	private ArrayList<NameValuePair> listNameValuePairs;
	private Button nextBtn;
	private EditText usernameEdt,mobileEditTxt;
	private String serverUserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_username);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
		String number = tm.getLine1Number();
		System.out.println("Mobile no is  "+number);
		
		
		listNameValuePairs = new ArrayList<NameValuePair>();
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			requestFor=bundle.getInt("requestFor");
			userEmail=bundle.getString("email");
			password=bundle.getString("password");
			userName=bundle.getString("username");
			website=bundle.getString("website");
		}
		
		
		
		TextView titleTxt=(TextView)findViewById(R.id.txtViewTitle);
		usernameEdt=(EditText) findViewById(R.id.editTextUserName);
		mobileEditTxt=(EditText) findViewById(R.id.editTextMobileNo);
		nextBtn=(Button) findViewById(R.id.nextBtn);
		
		usernameEdt.setText(userName);
		titleTxt.setVisibility(View.VISIBLE);
		titleTxt.setText("Username");
		
		nextBtn.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.nextBtn){
			
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(nextBtn.getWindowToken(), 0);
			
			userName=usernameEdt.getText().toString().trim();
			phoneNo=mobileEditTxt.getText().toString().trim();
			if(userName.length()==0){
				Utility.ShowAlertWithMessage(appContext, "Alert","Enter Username");
				
			}else if(phoneNo.length()==0){
				Utility.ShowAlertWithMessage(appContext, "Alert","Enter phone no");
			}else{
				
				if(requestFor==Constant.SIGNUP){
					listNameValuePairs.add(new BasicNameValuePair("email",userEmail));
					listNameValuePairs.add(new BasicNameValuePair("username",userName));
					listNameValuePairs.add(new BasicNameValuePair("password",password));
					listNameValuePairs.add(new BasicNameValuePair("mobile",phoneNo));
					listNameValuePairs.add(new BasicNameValuePair("website",website));
				}else if(requestFor==Constant.SOCIAL_LOGIN){
					listNameValuePairs.add(new BasicNameValuePair("email",userEmail));
					listNameValuePairs.add(new BasicNameValuePair("username",userName));
					listNameValuePairs.add(new BasicNameValuePair("pwd",password));
					listNameValuePairs.add(new BasicNameValuePair("mobile",phoneNo));
					listNameValuePairs.add(new BasicNameValuePair("website",website));
				}
				
				new PostDatatoServer().execute();
			}
			
		}
	}
	
	
	private void Register()
	{

		
		DataHelper db = new DataHelper(appContext);
		/*if(!db.isExist(userName))
		{
			return;
		}*/
		
		int count = db.getUserCount();
		long id = db.insertRegister(userEmail,userName, password,serverUserId);
		if(id>count)
		{
			Utility.setUserPrefrence(appContext, "id", ""+id, Constant.PREFRENCEFILE);
			Utility.setUserPrefrence(appContext, "serverUserId", ""+serverUserId, Constant.PREFRENCEFILE);
			Utility.setBooleanPrefrence(appContext, "sign_status", true, Constant.PREFRENCEFILE);
			Utility.setBooleanPrefrence(appContext, "databaseCreate",true, Constant.PREFRENCEFILE);
			Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
			Intent intent = new Intent(appContext, CameraMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else
		{
			Utility.ShowAlertWithMessage(appContext, "Alert", "Registration problem");
		}
		db.close();
	}

	public class PostDatatoServer extends AsyncTask<Void, Void, String> {
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
			
			if(requestFor==Constant.SIGNUP){
				url = Constant.serverURL +"SignUp";
			}else if(requestFor==Constant.SOCIAL_LOGIN){
				url = Constant.serverURL +"facebookSocialSignIn";
			}
				
			result = Utility.postParamsAndfindJSON(url, listNameValuePairs);
			listNameValuePairs.clear();
			return result;
		}

		protected void onPostExecute(String result) {
			applicationDialog.dismiss();
			System.out.println("result is " + result);
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext,"Alert","Please Check Network");
			} else if (result.equals("0")) {
				Utility.ShowAlertWithMessage(appContext, "Alert",
						"Request Sending Fails");
				result = null;
			} else if(result.equals("-1")){
				Utility.ShowAlertWithMessage(appContext, "Alert",
						"Email already Exit");
			} else if(result.equals("-2")){
				Utility.ShowAlertWithMessage(appContext, "Alert",
						"Username already Exit");
			}else{
				
					System.out.println("value of result in sign in" + result);
					try {
						JSONObject json = new JSONObject(result);
						userName=json.getString("username");
						serverUserId=json.getString("userid");
						String userMobile=json.getString("mobile");
						String userEmail=json.getString("email");
						Utility.setUserPrefrence(appContext, "userMobile", userMobile, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(appContext, "userEmail", userEmail, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(appContext, "userName", userName, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(appContext, "uId", serverUserId, Constant.PREFRENCEFILE);
						Utility.setBooleanPrefrence(appContext, "databaseCreate",true, Constant.PREFRENCEFILE);
						Utility.setBooleanPrefrence(appContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					result = null;
					Register();
			}
		}
	} // PostDataToSERver class ends here

	
	
}
