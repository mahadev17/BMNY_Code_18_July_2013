package com.tbldevelopment.bmny;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbldevelopment.bmny.sharetweeter.TwitterApp;
import com.tbldevelopment.bmny.sharetweeter.TwitterApp.TwDialogListener;
import com.tbldevelopment.bmny.sharetweeter.TwitterSession;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.bmny.utility.UtilityImage;

public class CameraMainActivity extends Activity implements
		SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	private boolean previewing, isFront;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static String TAG = "Camera Activity";
	private LinearLayout layoutActionSheet;
	private Animation showPicker, hidePicker;
	private View navigationView;
	private LayoutInflater inflater;
	private int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

	Button btnONOFF;
	//Twitter Variable
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };
	private String messageToPost, response;

	public static final String twitter_consumer_key = "DTv8eg3loRGusZdknvgrvQ";
	public static final String twitter_secret_key = "qMBjXJWBSgTz1V642LZg0uGo46PKiy7WjbrC5t8y7A";
	private TwitterApp mTwitter;

	private Handler mHandler;
	public static String PACKAGE_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera_peview);

		if (!UtilityImage.checkCameraHardware(this)) {
			Intent intent = new Intent(getApplicationContext(),
					FingerPaint.class);

			startActivity(intent);
			finish();
		}
		getWindow().setFormat(PixelFormat.UNKNOWN);
		inflater = LayoutInflater.from(this);
		navigationView = inflater.inflate(R.layout.navigation_bar, null);
		// ((LinearLayout)findViewById(R.id.topbar)).addView(navigationView);
		surfaceView = (SurfaceView) findViewById(R.id.surface_camera_preview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		layoutActionSheet = (LinearLayout) findViewById(R.id.cameraBottomLayout);

		AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.0F);
		alpha.setDuration(0); // Make animation instant
		alpha.setFillAfter(true); // Tell it to persist after the animation ends
		layoutActionSheet.startAnimation(alpha);
		showPicker = AnimationUtils.loadAnimation(this, R.anim.slide_up);
		hidePicker = AnimationUtils.loadAnimation(this, R.anim.slide_down);

		showPicker.setAnimationListener(animListener);
		hidePicker.setAnimationListener(animListener);

		
		
		if (UtilityImage.checkFrontCameraHardware(getApplicationContext()))
			((ImageButton) findViewById(R.id.toggleButtonSwitchCamera))
					.setOnClickListener(onclickListener);
		else
			((ImageButton) findViewById(R.id.toggleButtonSwitchCamera))
					.setVisibility(View.INVISIBLE);

		((Button) findViewById(R.id.buttonTakePhoto))
				.setOnClickListener(onclickListener);
		((Button) findViewById(R.id.buttonGallary))
				.setOnClickListener(onclickListener);
		((ImageButton) findViewById(R.id.buttonActionSheet))
				.setOnClickListener(onclickListener);

		((Button) findViewById(R.id.btnProfileSetting))
				.setOnClickListener(onclickListener);
		((ImageView) findViewById(R.id.btnGetMsg))
		.setOnClickListener(onclickListener);
		
		((Button) findViewById(R.id.btnSetting))
		.setOnClickListener(onclickListener);
		((Button) findViewById(R.id.findFriendBtn))
		.setOnClickListener(onclickListener);
		((Button) findViewById(R.id.musicSubscribe))
				.setOnClickListener(onclickListener);
		((Button) findViewById(R.id.myFriendBtn))
				.setOnClickListener(onclickListener);
		((ImageButton) findViewById(R.id.btnFacebook))
		.setOnClickListener(onclickListener);
		((ImageButton) findViewById(R.id.btnTwitter))
		.setOnClickListener(onclickListener);
		btnONOFF=(Button)findViewById(R.id.buttonGallary);
		
		mTwitter = new TwitterApp(CameraMainActivity.this, twitter_consumer_key,
				twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);

	}

	private AnimationListener animListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			if (animation == showPicker) {
				layoutActionSheet.setVisibility(View.VISIBLE);
			}

			if (animation == hidePicker) {
				layoutActionSheet.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if (animation == showPicker) {
				layoutActionSheet.setVisibility(View.VISIBLE);
			}

			if (animation == hidePicker) {
				layoutActionSheet.setVisibility(View.GONE);
			}
		}
	};
	private OnClickListener onclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.buttonTakePhoto:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
					camera.takePicture(myShutterCallback, myPictureCallback_RAW,
						mPicture);
				break;
			case R.id.buttonGallary: {
					/*Intent intent = new Intent(getApplicationContext(),
						FingerPaint.class);
				intent.putExtra("requestFor", 2);
				startActivity(intent);*/
				
				
				/*if(camera!=null){*/
					Parameters params = camera.getParameters();
					if (params.getFlashMode() == Parameters.FLASH_MODE_OFF) {
						params.setFlashMode(Parameters.FLASH_MODE_ON);
						btnONOFF.setBackgroundResource(R.drawable.on_btn);

					} else {
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
						btnONOFF.setBackgroundResource(R.drawable.off_btn);
					}
					camera.setParameters(params);
					/*}else{
					Toast.makeText(getApplicationContext(), "Flash is not available", Toast.LENGTH_SHORT).show();
				}*/
				
				break;
			}
			case R.id.buttonActionSheet: {
				if (layoutActionSheet.getVisibility() == View.GONE) {
					layoutActionSheet.startAnimation(showPicker);
					layoutActionSheet.setVisibility(View.VISIBLE);
				} else {
					layoutActionSheet.startAnimation(hidePicker);
					layoutActionSheet.setVisibility(View.GONE);
				}
			}

				break;

			case R.id.btnProfileSetting:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
				break;
				
			case R.id.btnSetting:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Intent intentsetting = new Intent(getApplicationContext(),
						ProfileSettingActivity.class);
				startActivity(intentsetting);
				break;
				
			case R.id.btnGetMsg:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Intent intentmsg = new Intent(getApplicationContext(),
						GetMassageActivity.class);
				intentmsg.putExtra("requestFor", Constant.GETMSG);
				startActivity(intentmsg);
				break;	
				
			case R.id.findFriendBtn:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Intent intentfindfriend = new Intent(getApplicationContext(),
						FindFriendActivity.class);
				startActivity(intentfindfriend);
				break;
			
			case R.id.btnFacebook:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Intent intentfacebook = new Intent(getApplicationContext(),
						ShareOnFacebook.class);
				intentfacebook.putExtra("requestFor", Constant.FACEBOOK_SHARED);
				startActivity(intentfacebook);
				break;
				
			case R.id.btnTwitter:
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
					onTwitterClick();
				break;	
				
			case R.id.toggleButtonSwitchCamera: {
				if (UtilityImage
						.checkFrontCameraHardware(getApplicationContext())) {
					if (!isFront) {
						isFront = true;
					} else {
						isFront = false;
					}
					switchCamera();
				}
			}
				break;
			case R.id.musicSubscribe: {
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Intent intentmusic = new Intent(getApplicationContext(),
						WebActivity.class);
				startActivity(intentmusic);
			}
				break;

			case R.id.myFriendBtn: {
				if (layoutActionSheet.getVisibility() == View.VISIBLE)
					layoutActionSheet.startAnimation(hidePicker);
				Intent intentmyfriend = new Intent(getApplicationContext(),
						MyFriendActivity.class);
				intentmyfriend.putExtra("requestFor", Constant.GETFRIEND);
				startActivity(intentmyfriend);
			}
				break;
			default:
				break;
			}

		}
	};

	ShutterCallback myShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub

		}
	};

	PictureCallback myPictureCallback_RAW = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub

		}
	};

	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			File pictureFile = new File("/mnt/sdcard/temp.png");// Utility.getOutputMediaFile(MEDIA_TYPE_IMAGE);

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				bm.compress(CompressFormat.PNG, 100, fos);
				// fos.write(data);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			Intent intent = new Intent(getApplicationContext(),
					FingerPaint.class);
			intent.putExtra("image", pictureFile.getAbsolutePath());
			intent.putExtra("requestFor", 1);
			startActivity(intent);
			// camera.startPreview();
		}
	};

	// float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
	// Matrix matrix = new Matrix();
	// Matrix matrixMirrorY = new Matrix();
	// matrixMirrorY.setValues(mirrorY);
	//
	// matrix.postConcat(matrixMirrorY);
	//
	// image = Bitmap.createBitmap(mBitmap, 0, 0, frame.getWidth(),
	// frame.getHeight(), matrix, true)

	private void switchCamera() {
		if (previewing) {
			camera.stopPreview();
		}
		//
		camera.release();

		if (isFront) {
			camId = Camera.CameraInfo.CAMERA_FACING_FRONT;
		} else {
			camId = Camera.CameraInfo.CAMERA_FACING_BACK;
		}
		camera = Camera.open(camId);
		initCamera();
	}

	private void initCamera() {

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				UtilityImage.setCameraDisplayOrientation(this, camId, camera);
				camera.startPreview();
				previewing = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_camera_main, menu);
		return true;
	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (previewing) {
			camera.stopPreview();
		}
		initCamera();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
			camera = getCameraInstance();
			initCamera();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
			previewing = false;
		}

	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			System.out.println("value " + value);
			String username = mTwitter.getUsername();
			if (username != null)
				username = (username.equals("")) ? "No Name" : username;
			if (mTwitter.hasAccessToken()) {
				new TwitterSender().execute();
			} else {
				Toast.makeText(CameraMainActivity.this, "Please Login First",
						Toast.LENGTH_SHORT).show();
			}
		}

		public void onError(String value) {

		}
	};
	
	
	
	private class TwitterSender extends AsyncTask<URL, Integer, Long> {
		private String url;
		ProgressDialog mProgressDialog;

		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(CameraMainActivity.this, "",
					"Posting Details...", true);

			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			TwitterSession twitterSession = new TwitterSession(CameraMainActivity.this);
			AccessToken accessToken = twitterSession.getAccessToken();

			Configuration conf = new ConfigurationBuilder()
					.setOAuthConsumerKey(twitter_consumer_key)
					.setOAuthConsumerSecret(twitter_secret_key)
					.setOAuthAccessToken(accessToken.getToken())
					.setOAuthAccessTokenSecret(accessToken.getTokenSecret())
					.build();

			try {
				
				result = 1;

				Twitter tt = new TwitterFactory(conf).getInstance();
				String currentDateTimeString = DateFormat.getDateTimeInstance()
						.format(new Date());
				twitter4j.Status response = tt
						.updateStatus(Constant.MASSAGE+"  "
								+ currentDateTimeString); // posting
				
				System.out.println("status is : " + response);

			} catch (Exception e) {
				Log.e("", "Failed to send status");
				result = 0;
				e.printStackTrace();
			}

			return result;
		}

		protected void onPostExecute(Long result) {

			if(result==1){
			}else if(result==0){
				Toast.makeText(CameraMainActivity.this, "Failed Post", Toast.LENGTH_SHORT).show();
			}

			if (mProgressDialog != null) {
				mProgressDialog.cancel();
			
			}
		}
	}
	
	private void onTwitterClick() {
		if (mTwitter.hasAccessToken()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					CameraMainActivity.this);

			builder.setMessage("Delete current Twitter connection?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mTwitter.resetAccessToken();
									}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();

			alert.show();
		} else {
			// tweetButton.setChecked(false);

			mTwitter.authorize();
		}
	}
	
	
}
