package com.tbldevelopment.bmny;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class MusicSubscribeActivity extends Activity {

	private View navigationView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_subscribe_music);
		LayoutInflater inflater = LayoutInflater.from(this);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
	}

}
