package com.tbldevelopment.bmny;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.database.DataHelper;

public class HomeSignInRegisterActivity extends Activity{

	private Context mContext;
	private int requestFor;
	private View navigationView;
	private String userName;
	private String userEmail;
	private String password;
	ArrayList<NameValuePair> listNameValuePairs;
	boolean sign_status;
	private String serverUserId;
	//Social Login
	SocialAuthAdapter adapter;
	String providerName;
	private String userMobile;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		
		if(Utility.getBooleanPrefrence(this, "sign_status", Constant.PREFRENCEFILE)){
			Intent intent = new Intent(this, CameraMainActivity.class);
			startActivity(intent);
			finish();
		}
		
		listNameValuePairs = new ArrayList<NameValuePair>();
		Bundle bundleValue = this.getIntent().getExtras();
		if (bundleValue != null) {
			requestFor = bundleValue.getInt("requestFor");
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		
		 
		
		if (requestFor == Constant.SIGNIN) {
			setContentView(R.layout.activity_signin);
			((Button) findViewById(R.id.btnSignInSubmit))
					.setOnClickListener(onClickListener);
			((LinearLayout)findViewById(R.id.topbar)).addView(navigationView);
		}
		if (requestFor == Constant.SIGNUP) {
			setContentView(R.layout.activity_register_layout);
			((Button) findViewById(R.id.btnRegisterSubmit))
					.setOnClickListener(onClickListener);
			((LinearLayout)findViewById(R.id.topbar)).addView(navigationView);
		}
		if (requestFor == Constant.HOME) {
			setContentView(R.layout.activity_camera_main);
			
			((Button) findViewById(R.id.buttonRegister))
			.setOnClickListener(onClickListener);
			((Button) findViewById(R.id.buttonSignIn))
			.setOnClickListener(onClickListener);
			
			LinearLayout bar = (LinearLayout) findViewById(R.id.linearbar);
			//bar.setBackgroundResource(R.drawable.bar_gradient);

			// Add Bar to library
			adapter = new SocialAuthAdapter(new ResponseListener());

			// Add providers
			adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook_icon);
			//adapter.addProvider(Provider.TWITTER, R.drawable.twitter_icon);
			adapter.addProvider(Provider.LINKEDIN, R.drawable.in_logo);
			adapter.addProvider(Provider.GOOGLE, R.drawable.googleplus_icon);
			
			adapter.enable(bar);
			
		}
	

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			Intent intent;
			switch (id) {
			case R.id.btnSignInSubmit:
				
				userName= ((EditText)findViewById(R.id.editTextUserName)).getText().toString().trim();
				password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString().trim();
				
				if(userName.length()==0){
					Toast.makeText(mContext, "Username can't be blank", Toast.LENGTH_SHORT).show();
				}
				if(password.length()==0){
					Toast.makeText(mContext, "Password can't be blank", Toast.LENGTH_SHORT).show();
				}
				
				listNameValuePairs.add(new BasicNameValuePair("email",userName));
				listNameValuePairs.add(new BasicNameValuePair("password",password));
				
				new PostDatatoServer().execute();
				break;
			case R.id.btnRegisterSubmit:
				
				userName= ((EditText)findViewById(R.id.editTextUserName)).getText().toString().trim();
				 password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString().trim();
				if(userName.length()==0){
					Toast.makeText(mContext, "Username can't be blank", Toast.LENGTH_SHORT).show();
				}
				if(password.length()==0){
					Toast.makeText(mContext, "Password can't be blank", Toast.LENGTH_SHORT).show();
				}
				
				Intent i=new Intent(mContext,AddUserNameActivity.class);
				i.putExtra("requestFor", Constant.SIGNUP);
				i.putExtra("email", userName);
				i.putExtra("username","");
				i.putExtra("website", "SignUp");
				i.putExtra("password", password);
				startActivity(i);
				
				
				break;
			case R.id.buttonRegister:
				intent = new Intent(mContext, HomeSignInRegisterActivity.class);
				intent.putExtra("requestFor", Constant.SIGNUP);
				startActivity(intent);
				break;
			case R.id.buttonSignIn:
				intent = new Intent(mContext, HomeSignInRegisterActivity.class);
				intent.putExtra("requestFor", Constant.SIGNIN);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};
	
	private void SignIn()
	{
		
		DataHelper db = new DataHelper(mContext);
		long id  = db.Login(userName, password);
		if(id>-1)
		{
			///Utility.setUserPrefrence(mContext, "id", ""+id, Constant.PREFRENCEFILE);
			if(requestFor!=Constant.SOCIAL_LOGIN){
				Utility.setUserPrefrence(mContext, "uId", ""+serverUserId, Constant.PREFRENCEFILE);
				Utility.setBooleanPrefrence(mContext, "sign_status", true, Constant.PREFRENCEFILE);
				Utility.setBooleanPrefrence(mContext, "databaseCreate",true, Constant.PREFRENCEFILE);
				Utility.setBooleanPrefrence(mContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
			}
			
			Intent intent = new Intent(mContext, CameraMainActivity.class);
			startActivity(intent);
			finish();
		}else
		{
			signInRegister();
		}
		db.close();
	}
	
	
	
	
	private void signInRegister()
	{

		DataHelper db = new DataHelper(mContext);
		if(db.isExist(userName))
		{
			Utility.ShowAlertWithMessage(mContext, "Alert", "User Already exist");
			return;
		}
		int count = db.getUserCount();
		long id = db.insertRegister(userEmail,userName, password,serverUserId);
		if(id>count)
		{
			Utility.setUserPrefrence(mContext, "id", ""+id, Constant.PREFRENCEFILE);
			Utility.setUserPrefrence(mContext, "serverUserId", ""+serverUserId, Constant.PREFRENCEFILE);
			Utility.setBooleanPrefrence(mContext, "sign_status", true, Constant.PREFRENCEFILE);
			Utility.setBooleanPrefrence(mContext, "databaseCreate",true, Constant.PREFRENCEFILE);
			Utility.setBooleanPrefrence(mContext, "getmsgdatabaseCreate", true, Constant.PREFRENCEFILE);
			Intent intent = new Intent(mContext, CameraMainActivity.class);
			startActivity(intent);
			finish();
		}else
		{
			Utility.ShowAlertWithMessage(mContext, "Alert", "Registration problem");
		}
		db.close();
	}
	
	
	
	public class PostDatatoServer extends AsyncTask<Void, Void, String> {
		String result;
		ProgressDialog applicationDialog;
		String url;
		protected void onPreExecute() {
			super.onPreExecute();
			applicationDialog = ProgressDialog.show(mContext, "",
					"Please Wait ");
			applicationDialog.setCancelable(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			if(requestFor==Constant.SOCIAL_LOGIN){
				url = Constant.serverURL +"facebookSocialSignIn";
			}else{
				url = Constant.serverURL +"SignIn";
			}
			result = Utility.postParamsAndfindJSON(url, listNameValuePairs);
			listNameValuePairs.clear();
			
			return result;
		}

		protected void onPostExecute(String result) {
			applicationDialog.dismiss();
			System.out.println("result is " + result);
			if (result == null) {
				Utility.ShowAlertWithMessage(mContext,"Alert","Please Check Network");
			} else if (result.equals("0")) {
				Utility.ShowAlertWithMessage(mContext, "Alert",
						"Data Not Post On Server");
				result = null;
			} else{
				switch (requestFor) {
				case 3:
					System.out.println("value of result in sign in" + result);
					try {
						JSONObject json = new JSONObject(result);
						userName=json.getString("username");
						userEmail=json.getString("email");
						serverUserId=json.getString("userid");
						userMobile=json.getString("mobile");
						Utility.setUserPrefrence(mContext, "userName", userName, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(mContext, "userMobile", userMobile, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(mContext, "uId", serverUserId, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(mContext, "userEmail", userEmail, Constant.PREFRENCEFILE);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SignIn();
				break;
				
				case 15:
					System.out.println("value of result in sign in" + result);
					try {
						JSONObject json = new JSONObject(result);
						userName=json.getString("username");
						userEmail=json.getString("email");
						serverUserId=json.getString("id");
						Utility.setUserPrefrence(mContext, "userName", userName, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(mContext, "uId", serverUserId, Constant.PREFRENCEFILE);
						Utility.setUserPrefrence(mContext, "userEmail", userEmail, Constant.PREFRENCEFILE);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SignIn();
				break;
				}
				
			}
		}
	} // PostDataToSERver class ends here
	
	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			// Variable to receive message status
			Log.d("Share-Bar", "Authentication Successful");
			
			// Get name of provider after authentication
			providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(mContext, providerName + " connected", Toast.LENGTH_SHORT).show();
			
			Events(0, providerName);

		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}
	}
	
	public void Events(int position, final String provider) {

		switch (position) {
		case 0: // Code to print user profile details for all providers
		{
			
			adapter.getUserProfileAsync(new ProfileDataListener());
			break;
		}
	}
	}
		
	final class ProfileDataListener implements SocialAuthListener<Profile> {

			@Override
			public void onExecute(Profile t) {

				Log.d("Custom-UI", "Receiving Data");
				Profile profileMap = t;
				
				password=profileMap.getValidatedId();
				
				Intent intent=new Intent(mContext,AddUserNameActivity.class);
				intent.putExtra("requestFor", Constant.SOCIAL_LOGIN);
				intent.putExtra("email", profileMap.getEmail());
				intent.putExtra("password", password);
				intent.putExtra("website", providerName);
				intent.putExtra("username", profileMap.getFirstName());
				startActivity(intent);
				finish();
			}

			@Override
			public void onError(SocialAuthError e) {

			}
		}
		
		
		
}
