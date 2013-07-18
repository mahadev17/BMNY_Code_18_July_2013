package com.tbldevelopment.bmny;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.tbldevelopment.bmny.utility.Constant;

public class ShareOnFacebook extends Activity {

	private static final String APP_ID = "489742777757288";
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };
	String response;
	Context appContext;

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;
	private int requestFor;
	

	private com.facebook.android.AsyncFacebookRunner mAsyncRunner;
	
	public boolean saveCredentials(Facebook facebook) {
		Editor editor = this.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		SharedPreferences facebookSession = this.getPreferences(MODE_PRIVATE); // this.getSharedPreferences(KEY,
																				// Context.MODE_PRIVATE);
		facebook.setAccessToken(facebookSession.getString(TOKEN, null));
		facebook.setAccessExpires(facebookSession.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
		mAsyncRunner = new com.facebook.android.AsyncFacebookRunner(facebook);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.facebook_dialog);

		appContext = this;
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			requestFor=bundle.getInt("requestFor");
		}
		String facebookMessage = getIntent().getStringExtra("facebookMessage");
		if (facebookMessage == null) {
			facebookMessage = Constant.MASSAGE;
		}
		messageToPost = facebookMessage;
		
		
		
	}

	public void doNotShare(View button) {
		finish();
	}

	public void share(View button) {

		try {
			if (!facebook.isSessionValid()) {
				loginAndPostToWall();
			} else {
				if(requestFor==Constant.FACEBOOK_LOGIN){
					getProfileInformation();
				}else if(requestFor==Constant.FACEBOOK_SHARED){
					postToWall(messageToPost);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void loginAndPostToWall() {
		// facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new
		// LoginDialogListener());
		facebook.authorize(this, PERMISSIONS, new LoginDialogListener());
	}

	@SuppressWarnings("deprecation")
	public void postToWall(String message) {

		new PostStatus(message).execute();
		finish();
		final Bundle parameters = new Bundle();
		parameters.putString("message", message);
		
		parameters.putString("description", "topic share");
		Thread postStatusThread = new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub

				try {

					facebook.request("me");

					response = facebook.request("me/feed", parameters, "POST");
					Log.d("Tests", "got response: " + response);
					finish();

				} catch (Exception e) {
					response = "1";
					e.printStackTrace();
					finish();
				}
			}
		});
		
	}

	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			
			if(requestFor==Constant.FACEBOOK_LOGIN){
				getProfileInformation();
			}else if(requestFor==Constant.FACEBOOK_SHARED){
				postToWall(messageToPost);
			}
		}

		public void onFacebookError(FacebookError error) {
			showToast("Authentication with Facebook failed!");
			finish();
		}

		public void onError(DialogError error) {
			showToast("Authentication with Facebook failed!");
			finish();
		}

		public void onCancel() {
			showToast("Authentication with Facebook cancelled!");
			finish();
		}
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class PostStatus extends AsyncTask<Void, Void, String> {
		String message;

		public PostStatus(String msg) {
			message = msg;
		}

		public void onPrexecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			final Bundle parameters = new Bundle();
			parameters.putString("message", message);
			parameters.putString("description", "topic share");

			try {

				facebook.request("me");
				response = facebook.request("me/feed", parameters, "POST");
				Log.d("Tests", "got response: " + response);
			} catch (Exception e) {
				response = "1";
				e.printStackTrace();
			}
			return response;
		}

		public void onPostExecute(String response) {

			if (response == null || response.equals("")
					|| response.equals("false")) {
				System.out.println("response is : " + response);
				showToast("Blank response.");
			} else if (response == "1") {
				System.out.println("response is : " + response);
				showToast("Failed to post to wall");
			} else {
				System.out.println("response is : " + response);
				if(requestFor==Constant.FACEBOOK_LOGIN){
					showToast("FAcebook Login Successfully");
				}else if(requestFor==Constant.FACEBOOK_SHARED){
					showToast("Message posted to your facebook wall!");
				}
				
			}

		}
	}
	
	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(json);
					System.out.println("json responce is  "+response);
					
					// getting name of the user
					final String username = profile.getString("name");
					
					// getting email of the user
					final String id = profile.getString("id");
					String imageUrl="http://graph.facebook.com/"+id+"/picture?type=small";
					
					
					//Utility.setSharedPreference(appContext, "FbUserName", username);
					//Utility.setSharedPreference(appContext, "FbUserImage", imageUrl);
					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							Intent intent =new Intent(appContext,AddUserNameActivity.class);
							intent.putExtra("username",Constant.SIGNUP);
							intent.putExtra("username", username);
							intent.putExtra("USerId", id);
							startActivity(intent);
							finish();
						}

					});

					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}
}
