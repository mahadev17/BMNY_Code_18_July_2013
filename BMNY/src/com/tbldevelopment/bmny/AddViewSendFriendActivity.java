package com.tbldevelopment.bmny;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tbldevelopment.bmny.adapter.ApplicationCustomAdapter;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.database.DataHelper;

public class AddViewSendFriendActivity extends Activity {

	private LayoutInflater inflater;
	private LinearLayout layoutAdd;
	private Animation anim;
	private View navigationView;
	private DataHelper db;
	private EditText txtFFriendName;
	private Context mContext;
	private ArrayList<HashMap<String, String>> friendList;
	private ApplicationCustomAdapter appListAdapter;
	private ListView listFriend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_feed_history);
		mContext = this;

		db = new DataHelper(mContext);
		listFriend = (ListView) findViewById(R.id.listViewFriends);
		friendList = new ArrayList<HashMap<String, String>>();

		inflater = LayoutInflater.from(this);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		layoutAdd = (LinearLayout) findViewById(R.id.addFriendLayout);
		anim = AnimationUtils.loadAnimation(this, R.anim.show_user_view);
		txtFFriendName = (EditText) findViewById(R.id.editTextName);
		((LinearLayout) findViewById(R.id.feedNavigationView))
				.addView(navigationView);

		
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

				layoutAdd.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				layoutAdd.setVisibility(View.VISIBLE);
			}
		});
		((ImageButton) findViewById(R.id.btnAddFriend))
				.setOnClickListener(onClicllistener);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(friendList.size()==0)
		{
			//new GetFriendlist().execute();	
		}

	}

	private OnClickListener onClicllistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.btnAddFriend:
				if (txtFFriendName.getText().toString().trim().length() == 0) {
					Utility.ShowAlertWithMessage(mContext, "Alert",
							"Please enter friend name to add.");
					return;
				}
				/*boolean isExist = db.isExist(txtFFriendName.getText()
						.toString());
				if (isExist) {
					int count = db.getFriendCount();
					long fid = db.addFriend(
							txtFFriendName.getText().toString(),
							Utility.getUserPrefrence(mContext, "id",
									Constant.PREFRENCEFILE),
							"Active",
							"false",
							"friend",
							""
									+ db.getuserid(txtFFriendName.getText()
											.toString()));
					if(fid>count)
					{
					// new GetFriendlist().execute();
					}
				} else {
					Utility.ShowAlertWithMessage(mContext, "Alert",
							"user not found");
				}
				break;*/
			case 101:

				break;

			default:
				break;
			}

		}
	};

	public class GetFriendlist extends AsyncTask<Void, Void, String> {

		protected void onPreExecute()
		{
			super.onPreExecute();
			friendList.clear();
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			/*friendList = db.getFriend(Utility.getUserPrefrence(mContext, "id",
					Constant.PREFRENCEFILE));*/
			return null;
		}

		protected void onPostExecute(String result) {

			appListAdapter = new ApplicationCustomAdapter(mContext,
					R.layout.application_friend_list_layout, friendList);
			//appListAdapter.notifyDataSetChanged();
			listFriend.setAdapter(appListAdapter);
			
		}
	}

	protected void onStop() {
		super.onStop();
		if (db != null) {
			db.close();
		}
	};
}
