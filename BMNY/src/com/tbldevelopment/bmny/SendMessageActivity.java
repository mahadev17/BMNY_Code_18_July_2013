package com.tbldevelopment.bmny;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.tbldevelopment.bmny.listselection.EntryAdapter;
import com.tbldevelopment.bmny.listselection.Item;

public class SendMessageActivity extends Activity {

	private static final String[] SECTIONTITLE = { "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };
	private ArrayList<Item> listFriendRecord;
	private boolean isCheckVisible;
	private ListView friendListView;
	private EntryAdapter adaptorFriendList;
	int requestFor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_friend);

		listFriendRecord = new ArrayList<Item>();
		//adaptorFriendList = new EntryAdapter(this, listFriendRecord,isCheckVisible);
		friendListView = (ListView) findViewById(R.id.friendList);
	}

}
