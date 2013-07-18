package com.tbldevelopment.bmny;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;

public class ProfileSettingActivity extends Activity {

	private static final int DIALOG_SINGLE_CHOICE = 1;
	private TextView txtViewWhoCan,txtViewTitle,txtSetTime;
	private View navigationView;
	Button btnBack,btnLogout,btnSubmit;
	private Context appContext;
	
	int requestFor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile_setting);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(this);
		
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout)findViewById(R.id.topbarProfile)).addView(navigationView);

		((RelativeLayout) findViewById(R.id.layoutWhocan))
				.setOnClickListener(onCliclListener);

		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnLogout=(Button) findViewById(R.id.btnLogout);
		btnSubmit=(Button) findViewById(R.id.btnSubmit);
		txtViewTitle = (TextView)findViewById(R.id.txtViewTitle);
		txtViewWhoCan = (TextView)findViewById(R.id.TextViewWhocan);
		txtSetTime=(TextView) findViewById(R.id.txtTimeSet);
		
		btnBack.setOnClickListener(onCliclListener);
		txtSetTime.setOnClickListener(onCliclListener);
		btnLogout.setOnClickListener(onCliclListener);
		btnSubmit.setOnClickListener(onCliclListener);
		
		txtViewTitle.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		txtViewWhoCan.setText("Everyone");
		txtViewTitle.setText("SETTINGS");
		
		txtSetTime.setText("1 Sec");
		String timeCounter="1";
		Utility.setUserPrefrence(appContext, "timeCounter",timeCounter,Constant.PREFRENCEFILE);
		requestFor=Constant.USER_SETTING;
		
		String userName=Utility.getUserPrefrence(appContext, "userName",Constant.PREFRENCEFILE);
		String userEmail=Utility.getUserPrefrence(appContext, "userEmail",Constant.PREFRENCEFILE);
		String userMobile=Utility.getUserPrefrence(appContext, "userMobile",Constant.PREFRENCEFILE);
		
		((EditText)findViewById(R.id.txtlblUsername)).setText(userName);
		((EditText)findViewById(R.id.TextlblMobile)).setText(userMobile);
		((EditText)findViewById(R.id.TextlblEmail)).setText(userEmail);
		
		
		
		//new GetUserInfo().execute();
		
		SeekBar seekbar = (SeekBar) findViewById(R.id.seek1);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				String timeCounter=String.valueOf(progress);
				txtSetTime.setText(progress+" Sec");
				Utility.setUserPrefrence(appContext, "timeCounter",timeCounter,Constant.PREFRENCEFILE);
			}
		});
	}

	private OnClickListener onCliclListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.navigationLeftBtn:
				finish();
				break;
				
			case R.id.btnLogout:
				Utility.setBooleanPrefrence(appContext, "sign_status",false, Constant.PREFRENCEFILE);
				Intent i=new Intent(appContext,HomeSignInRegisterActivity.class);
				i.putExtra("requestFor", Constant.HOME);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				break;
				
			case R.id.btnSubmit:
				new UpdateProfile().execute();
				break;	
			
			default:
				break;
			}
		}
	};


public class UpdateProfile extends AsyncTask<Void, Void, String> {

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
		url = Constant.serverURL + "UpdateProfile";
		String userId = Utility.getUserPrefrence(appContext, "uId",
				Constant.PREFRENCEFILE);
		
		listNameValuePairs.add(new BasicNameValuePair("userid",userId));
		listNameValuePairs.add(new BasicNameValuePair("mobile",((EditText)findViewById(R.id.TextlblMobile)).getText().toString()));
		listNameValuePairs.add(new BasicNameValuePair("email",((EditText)findViewById(R.id.TextlblEmail)).getText().toString()));
		
		result = Utility.postParamsAndfindJSON(url, listNameValuePairs);
		listNameValuePairs.clear();

		return result;
	}

	protected void onPostExecute(String result) {
		applicationDialog.dismiss();
		System.out.println("result is " + result);
		if (result == null) {
			Utility.ShowAlertWithMessage(appContext, "Alert", "Please check network");
		} else if (result.equals("-1")) {
			Utility.ShowAlertWithMessage(appContext, "Alert", "Email already exist.");
		} else if (result.equals("-2")) {
			Utility.ShowAlertWithMessage(appContext, "Alert", "Moile number already exist.");
		} else if (result.equals("0")) {
			Utility.ShowAlertWithMessage(appContext, "Alert", "Error,Try again");
		} else if (result.equals("1")) {
			Utility.setUserPrefrence(appContext, "userMobile", ((EditText)findViewById(R.id.TextlblMobile)).getText().toString(), Constant.PREFRENCEFILE);
			Utility.setUserPrefrence(appContext, "userEmail",((EditText)findViewById(R.id.TextlblEmail)).getText().toString(), Constant.PREFRENCEFILE);
			Utility.ShowAlertWithMessage(appContext, "Alert", "Your profile update successfully");
		}

	}

}

	
}
