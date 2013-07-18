/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tbldevelopment.bmny;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbldevelopment.bmny.ColorPickerDialog.OnColorChangedListener;
import com.tbldevelopment.bmny.utility.Constant;
import com.tbldevelopment.bmny.utility.ProgressHUD;
import com.tbldevelopment.bmny.utility.Utility;
import com.tbldevelopment.bmny.utility.UtilityImage;
import com.tbldevelopment.dragview.DragController;
import com.tbldevelopment.dragview.DragLayer;

public class FingerPaint extends GraphicsActivity implements
		ColorPickerDialog.OnColorChangedListener, View.OnTouchListener,View.OnClickListener {

	private MyView myCustomView;
	private ImageView photoView;
	int selectedcolor, requestFor;
	private LinearLayout timePickerLayout;
	private Animation showPicker, hidePicker;
	private static int GETIMAGEGALLARY = 101;
	private Context mContext;
	private EditText txtVDrag;
	private DragController mDragController;
	private DragLayer layoutDrawing;
	private ProgressHUD progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bitmap bm = null;
		setContentView(R.layout.activity_picture_edit);
		mContext = this;
		Bundle bundleValue = getIntent().getExtras();
		if (bundleValue != null) {
			requestFor = bundleValue.getInt("requestFor");
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);// 0xFFFF0000
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		showPicker = AnimationUtils.loadAnimation(this, R.anim.slide_up);
		hidePicker = AnimationUtils.loadAnimation(this, R.anim.slide_down);
		timePickerLayout = (LinearLayout) findViewById(R.id.imageBottomLayout);

		if (requestFor == 1) {
			File f = new File(bundleValue.getString("image"));// bundleValue.getString("image")
			bm = UtilityImage.loadResizedImage(this, f);
			initializingControlls(bm);
		} else {

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("return-data", true);
			startActivityForResult(intent, GETIMAGEGALLARY);
		}

		((Button)findViewById(R.id.btnmyFriend)).setOnClickListener(onCliclLitener);
	}

	AnimationListener animListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			if (animation == showPicker) {
				timePickerLayout.setVisibility(View.VISIBLE);
			}

			if (animation == hidePicker) {
				timePickerLayout.setVisibility(View.GONE);
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
				timePickerLayout.setVisibility(View.VISIBLE);
			}

			if (animation == hidePicker) {
				timePickerLayout.setVisibility(View.GONE);
			}
		}
	};

	private void initializingControlls(Bitmap bm) {
		photoView = (ImageView) findViewById(R.id.imageViewPicture);
		photoView.setImageBitmap(bm);
		layoutDrawing = (DragLayer) findViewById(R.id.layoutDrawing);
		mDragController = new DragController(this);
		layoutDrawing.setDragController(mDragController);
		mDragController.addDropTarget(layoutDrawing);
		txtVDrag = (EditText) findViewById(R.id.textShow);
		
		txtVDrag.setOnClickListener(this);

		myCustomView = new MyView(this);
		((LinearLayout) findViewById(R.id.layoutCustomView))
				.addView(myCustomView);
		showPicker.setAnimationListener(animListener);
		hidePicker.setAnimationListener(animListener);
		
		((Button) findViewById(R.id.btnUndo))
				.setOnClickListener(onCliclLitener);
		((Button) findViewById(R.id.buttonPickColor))
				.setOnClickListener(onCliclLitener);
		((Button) findViewById(R.id.btnCross))
		.setOnClickListener(onCliclLitener);
		
		((Button) findViewById(R.id.buttonSaveImage))
				.setOnClickListener(onCliclLitener);
		((Button) findViewById(R.id.buttonSend))
		.setOnClickListener(onCliclLitener);
		((Button) findViewById(R.id.btnmyFriend))
				.setOnClickListener(onCliclLitener);
	}

	public boolean onTouch(View v, MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean handledHere = false;

		final int action = ev.getAction();

		// In the situation where a long click is not needed to initiate a drag,
		// simply start on the down event.
		if (action == MotionEvent.ACTION_DOWN) {
			handledHere = startDrag(v);
		}

		return handledHere;
	}

	public boolean startDrag(View v) {
		Object dragInfo = v;
		mDragController.startDrag(v, layoutDrawing, dragInfo,
				DragController.DRAG_ACTION_MOVE);
		return true;
	}

	OnClickListener onCliclLitener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.btnUndo:
				myCustomView.undo();
				break;
					
			case R.id.buttonPickColor:
				new ColorPickerDialog(mContext, colorChange, mPaint.getColor())
						.show();
				break;
			case R.id.btnCross:
				finish();
				break;	
			case R.id.btnmyFriend:
			{
				Intent intent = new Intent(mContext, AddViewSendFriendActivity.class);
				startActivity(intent);
			}
			break;
			case R.id.buttonSend:
			{
				Bitmap bm = null;
				layoutDrawing.setDrawingCacheEnabled(true);
				bm = layoutDrawing.getDrawingCache();
				Utility.setBitmap(bm);
				Intent intent = new Intent(mContext, MyFriendActivity.class);
				intent.putExtra("requestFor", Constant.SENDIMAGE);
				startActivity(intent);
					
			}
			break;
			
			case R.id.buttonSaveImage:
				progressDialog = ProgressHUD.show(mContext, "Saving", true, true,
						new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								// TODO Auto-generated method stub
								th.stop();
							}
						});
				saveImage();
				break;
			
			default:
				break;
			}
		}
	};
	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	private Thread th;

	private void saveImage() {

		Runnable runSave = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				File folder = new File(
						Environment.getExternalStorageDirectory() + "/BMNY/");
				Bitmap bm = null;
				layoutDrawing.setDrawingCacheEnabled(true);
				bm = layoutDrawing.getDrawingCache();

				String file = "bamny00" + System.currentTimeMillis() + ".png";
				Utility.saveFile(mContext, folder, file, bm, CompressFormat.PNG, 100);
				bm = null;
				System.gc();
				layoutDrawing.setDrawingCacheEnabled(false);
				
			
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(progressDialog!=null)
						{
							progressDialog.cancel();
							Toast.makeText(mContext, "Saved", Toast.LENGTH_LONG).show();
						}
					}
				});

			}
		};
		th = new Thread(runSave);
		runSave.run();

	}

	OnColorChangedListener colorChange = new OnColorChangedListener() {

		@Override
		public void colorChanged(int color) {
			// TODO Auto-generated method stub
			selectedcolor = color;
			mPaint.setColor(color);
//			((Button) findViewById(R.id.buttonPickColor))
//					.setBackgroundColor(selectedcolor);
		}
	};

	public void colorChanged(int color) {

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if pick imge from Default gallary
		if (requestCode == GETIMAGEGALLARY && resultCode==Activity.RESULT_CANCELED)
		{
			finish();
		}else if (requestCode == GETIMAGEGALLARY && data != null) {

			Uri _uri = data.getData();
			System.out.println("in Gallery ");
			if (_uri != null) {
				System.out.println("uri not null " + requestCode);
				// User had pick an image.
				final Cursor cursor = getContentResolver()
						.query(_uri,
								new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
								null, null, null);
				cursor.moveToFirst();
				File f = new File(cursor.getString(0));
				Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());// Utility.loadResizedImage(this,
																			// f);
				initializingControlls(bm);
				// mbitmap = BitmapFactory.decodeFile(cursor.getString(0));
				cursor.close();

			}

		}
	}

	// Drawing View
	public class MyView extends View {

		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;
		private ArrayList<Path> historyList;
		private HashMap<Path, Integer> selectedColor;
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;

		private int index;
		private boolean isUndo;

		public MyView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public MyView(Context c) {
			super(c);
			// mBitmap = bm.copy(Bitmap.Config.ARGB_8888,
			// true);//Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
			WindowManager windowManager = (WindowManager) c
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			mBitmap = Bitmap.createBitmap(display.getWidth(),
					display.getHeight(), Bitmap.Config.ARGB_8888);

			// Bitmap.createBitmap(bm, 0, 0, display.getWidth(),
			// display.getHeight());
			historyList = new ArrayList<Path>();
			selectedColor = new HashMap<Path, Integer>();
			// bm.copy(Bitmap.Config.ARGB_8888,
			// true);
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		}

		@Override
		public boolean isInEditMode() {
			// TODO Auto-generated method stub
			return true;
		}

		public void refresh(HashMap<String, Path> map) {
			isUndo = true;
			mPath = map.get("path");
			if (!mPath.isEmpty()) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				mCanvas.drawPath(mPath, mPaint);
				invalidate();
			}
			// canvas.drawPath(mPath, mPaint);
		}

		public void undo() {
			if (historyList.size() > 0) {
				historyList.remove(historyList.size() - 1);
				invalidate();

			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			canvas.drawColor(0x00000000);// 0xFFAAAAAA

			// canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			for (Path p : historyList) {
				mPaint.setColor(selectedColor.get(p));
				canvas.drawPath(p, mPaint);
			}
			mPaint.setColor(selectedcolor);
			canvas.drawPath(mPath, mPaint);
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			selectedColor.put(mPath, selectedcolor);
			mCanvas.drawPath(mPath, mPaint);
			historyList.add(mPath);
			// kill this so we don't double draw
			// mPath.reset();
			mPath = new Path();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		// menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		// menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		// menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		// menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

		/****
		 * Is this the mechanism to extend with filter effects? Intent intent =
		 * new Intent(null, getIntent().getData());
		 * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
		 * NotesList.class), null, intent, 0, null);
		 *****/
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case ERASE_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			return true;
		case SRCATOP_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.textShow){
			Toast.makeText(mContext, "Click ",Toast.LENGTH_SHORT).show();
		}
	}
}
