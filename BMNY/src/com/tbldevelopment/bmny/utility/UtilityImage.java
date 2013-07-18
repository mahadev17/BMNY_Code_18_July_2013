package com.tbldevelopment.bmny.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class UtilityImage {

	
	static int mMaxWidth, mMaxHeight;
	

	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	 
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static boolean checkFrontCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == 1){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	        
	    } else if(type == 2) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	public static Bitmap loadResizedImage(Context mContext, final File imageFile) {
		WindowManager windowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Runtime.getRuntime().gc();
		mMaxWidth = 1024;// change 2048
		mMaxHeight = 1024;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		
		int scale =calculateInSampleSize(options, mMaxWidth, mMaxHeight);
		while (options.outWidth / scale > mMaxWidth
				|| options.outHeight / scale > mMaxHeight) {
			scale++;
		}
		Bitmap bitmap = null;
		Bitmap scaledBitmap = null;
		System.gc();
		if (scale > 1) {
			try{
			scale--;
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			options.inPurgeable = true;
			options.inTempStorage = new byte[32 * 1024];//change from 32*1024
			bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),
					options);
			if (bitmap == null) {
				return null;
			}
			// resize to desired dimensions
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			double newWidth;
			double newHeight;
			if ((double) width / mMaxWidth < (double) height / mMaxHeight) {
				newHeight = mMaxHeight;
				newWidth = (newHeight / height) * width;
			} else {
				newWidth = mMaxWidth;
				newHeight = (newWidth / width) * height;
			}

			scaledBitmap = Bitmap.createScaledBitmap(bitmap,
					Math.round((float) newWidth),
					Math.round((float) newHeight), true);
			Log.d("", "new height "+scaledBitmap.getHeight() + "new width "+scaledBitmap.getWidth());
			//bitmap.recycle();
			bitmap = scaledBitmap;
			}catch(OutOfMemoryError e){
				e.printStackTrace();
				bitmap=null;
				System.gc();
			}
			System.gc();
		} else {
			bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		}

		return rotateImage(bitmap, imageFile);
	}

	private static Bitmap rotateImage(final Bitmap bitmap,
			final File fileWithExifInfo) {
		if (bitmap == null) {
			return null;
		}
		Bitmap rotatedBitmap = bitmap;
		int orientation = 0;
		try {
			orientation = getImageOrientation(fileWithExifInfo
					.getAbsolutePath());
			if (orientation != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(orientation,(float) bitmap.getWidth() / 2,
						 (float) bitmap.getHeight() / 2);
				rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				//bitmap.recycle();
			}else
			{
				int w = bitmap.getWidth();
		         int h = bitmap.getHeight();
		         Matrix mtx = new Matrix();
		          mtx.preRotate(orientation);
		          rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
							bitmap.getWidth(), bitmap.getHeight(), mtx, true);
		         // bitmap.recycle();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotatedBitmap;
	}

	private static int getImageOrientation(final String file)
			throws IOException {
		ExifInterface exif = new ExifInterface(file);
		int orientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			return 0;
		case ExifInterface.ORIENTATION_ROTATE_90:
			return 90;
		case ExifInterface.ORIENTATION_ROTATE_180:
			return 180;
		case ExifInterface.ORIENTATION_ROTATE_270:
			return 270;
		default:
			return 90;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	public static void saveFile(Context mContext, File folder, String file,
			Bitmap saveBitmap) {
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			try {
				// you can create a new file name "test.jpg" in sdcard folder.
				File f = new File(folder, file);
				Log.d("pathsd", " " + f.getAbsolutePath());
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(f);
				saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fo);
				fo.flush();
				// remember close de FileOutput
				fo.close();
				saveBitmap = null;
				System.gc();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
