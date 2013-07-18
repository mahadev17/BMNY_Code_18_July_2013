package com.tbldevelopment.bmny;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tbldevelopment.bmny.utility.Constant;

public class SplashScreen extends Activity {

	protected boolean _active = true, isLogin;
	protected int _splashTime = 2000; // Splash screen time
	Context applicationContex;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_screen);

		applicationContex = this;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
	

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
					//		crateSightings();
						}
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {

				}
				runOnUiThread(endSplashThread);
			}
		};
		splashTread.start();
	}

	private Runnable endSplashThread = new Runnable() {
		public void run() {
			finish();
			Intent intent = new Intent(applicationContex, HomeSignInRegisterActivity.class);
			intent.putExtra("requestFor", Constant.HOME);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			startActivity(intent);
			
		}
	};

	/*private void crateSightings() {

		if (dbAdapter.getUserList().size() == 0)
			dbAdapter.insertRegister("admin", "admin@gmail.com", "admin");

		if (dbAdapter.getPopEnteries().size() == 0) {
			dbAdapter.createPurchaseEntry("1", "Lotus", "Shop",
					"Sapana Sangeeta, Indore", "24/08/2012",
					"An Electroni shop, where get All Electric Appliances ",
					BitmapFactory
							.decodeResource(getResources(), R.drawable.pic));
			dbAdapter.createPurchaseEntry("1", "GuruKripa Hotel", "Eat",
					"Sarwate BusStand, Indore", "20/08/2012",
					"A good Hotel For party and dinner", BitmapFactory
							.decodeResource(getResources(), R.drawable.pic));
			dbAdapter
					.createPurchaseEntry(
							"1",
							"IT Festiwal",
							"Event",
							"Race Corse Road, Indore",
							"24/09/2012",
							"IT Festiwal for all It Companies, to show their it product",
							BitmapFactory.decodeResource(getResources(),
									R.drawable.pic));
			dbAdapter.createPurchaseEntry("1", "IPL 20 - 20", "Play",
					"Neharu Stadium, Indore", "15/10/2012",
					"20-20 Matches india v/s austratlia ", BitmapFactory
							.decodeResource(getResources(), R.drawable.pic));

		}
	}*/

}
