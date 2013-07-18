package com.tbldevelopment.bmny.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

public class Utility {
	private static int MAX_IMAGE_DIMENSION = 720;
	private static Bitmap bm;
	public static void ShowAlertWithMessage(Context context, String title,
			String msg) {
		// Assign the alert builder , this can not be assign in Click events
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setMessage(msg);
		// builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle(title);
		// Set behavior of negative button
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Cancel the dialog
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public static void setBitmap(Bitmap bitmap)
	{
		bm = bitmap;
	}
	public static Bitmap getBitmap()
	{
		return bm;
	}
	public static void ShowAlertWithMessageAndListener(Context context,
			String title, String msg, OnClickListener listener) {
		// Assign the alert builder , this can not be assign in Click events
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setMessage(msg);
		// builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle(title);
		// Set behavior of negative button
		builder.setNegativeButton("OK", listener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	// check for Valid email Address
	public static boolean isValidEmail(String emailAddress) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(emailAddress);

		return matcher.matches();
	}

	public static void setUserPrefrence(Context context, String key,
			String value, String prefFile) {
		SharedPreferences settings = context.getSharedPreferences(prefFile, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);

		// Commit the edits!
		editor.commit();

	}

	public static String getUserPrefrence(Context context, String key,
			String prefFile) {
		SharedPreferences settings = context.getSharedPreferences(prefFile, 0);
		return settings.getString(key, null);
	}

	public static void setBooleanPrefrence(Context context, String key,
			boolean value, String prefFile) {
		SharedPreferences settings = context.getSharedPreferences(prefFile, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);

		// Commit the edits!
		editor.commit();

	}

	public static boolean getBooleanPrefrence(Context context, String key,
			String prefFile) {
		SharedPreferences settings = context.getSharedPreferences(prefFile, 0);
		return settings.getBoolean(key, false);
	}

	public static String fileToBase64(String path) throws IOException {
		byte[] bytes = fileToByteArray(path);
		return Base64.encodeToString(bytes, 0);
	}

	public static byte[] fileToByteArray(String path) throws IOException {
		File imagefile = new File(path);
		byte[] data = new byte[(int) imagefile.length()];
		FileInputStream fis = new FileInputStream(imagefile);
		fis.read(data);
		fis.close();
		return data;
	}

	public static String encodeTobase64(Bitmap image) {
		//Bitmap immagex = image;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		// String imageEncoded = Base64Coder.encodeTobase64(image);

		// Log.d("LOOK", imageEncoded);
		return imageEncoded;
	}
	
	public static byte[] scaleImage(Context context, Uri photoUri,int Height, int Width,CompressFormat format)
			throws IOException {
		InputStream is = context.getContentResolver().openInputStream(photoUri);
		BitmapFactory.Options dbo = new BitmapFactory.Options();
		dbo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, dbo);
		is.close();

		int rotatedWidth, rotatedHeight;
		int orientation = 0;// getOrientation(context, photoUri);

		if (orientation == 90 || orientation == 270) {
			rotatedWidth = dbo.outHeight;
			rotatedHeight = dbo.outWidth;
		} else {
			rotatedWidth = dbo.outWidth;
			rotatedHeight = dbo.outHeight;
		}

		Bitmap srcBitmap;
		is = context.getContentResolver().openInputStream(photoUri);
		if (rotatedWidth > MAX_IMAGE_DIMENSION
				|| rotatedHeight > MAX_IMAGE_DIMENSION) {
			float widthRatio = ((float) rotatedWidth)
					/ ((float) MAX_IMAGE_DIMENSION);
			float heightRatio = ((float) rotatedHeight)
					/ ((float) MAX_IMAGE_DIMENSION);
			float maxRatio = Math.max(widthRatio, heightRatio);

			// Create the bitmap from file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = calculateInSampleSize(dbo, Width, Height);
			srcBitmap = BitmapFactory.decodeStream(is, null, options);
		} else {
			srcBitmap = BitmapFactory.decodeStream(is);
		}
		is.close();

		/*
		 * if the orientation is not 0 (or -1, which means we don't know), we
		 * have to do a rotation.
		 */
		if (orientation > 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(orientation);

			srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
					srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		}

		String type = context.getContentResolver().getType(photoUri);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		srcBitmap.compress(format, 100, baos);

		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return bMapArray;
	}

	public static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
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
			Bitmap saveBitmap, CompressFormat format, int Quality) {
		
		
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			try {
				// you can create a new file name "test.jpg" in sdcard folder.
				File f = new File(folder, file);
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(f);
				saveBitmap.compress(format, 100, fo);
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

	static int mMaxWidth, mMaxHeight;

	public static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}
	
	
	public static String postParamsAndfindJSON(String url,
			ArrayList<NameValuePair> params) {
		// TODO Auto-generated method stub
		JSONObject jObj = new JSONObject();
		String result = "";

		System.out.println("URL comes in jsonparser class is:  " + url);
		try {
			int TIMEOUT_MILLISEC = 100000; // = 10 seconds
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			// httpGet.setURI(new URI(url));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();

			InputStream is = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");

			}

			is.close();
			result = sb.toString();

		} catch (Exception e) {
			System.out.println("exception in jsonparser class ........");
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	public static String findJSONFromUrl(String url) {
		// TODO Auto-generated method stub
		JSONObject jObj = new JSONObject();
		String result = "";

		System.out.println("URL comes in jsonparser class is:  " + url);
		try {
			int TIMEOUT_MILLISEC = 100000; // = 10 seconds
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			// httpGet.setURI(new URI(url));

			HttpResponse httpResponse = httpClient.execute(httpGet);
			int status = httpResponse.getStatusLine().getStatusCode();

			InputStream is = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");

			}

			is.close();
			result = sb.toString();
			System.out.println("result  in jsonparser class ........" + result);

		} catch (Exception e) {
			System.out.println("exception in jsonparser class ........");
			e.printStackTrace();
			return null;
		}
		return result;
	}

	
	public static Bitmap getBitmap(String url) {
		Bitmap imageBitmap = null;
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			try {
				imageBitmap = BitmapFactory.decodeStream(new FlushedInputStream(is));
			} catch (OutOfMemoryError error) {
				error.printStackTrace();
				System.out.println("exception in get bitma putility");
			}

			bis.close();
			is.close();
			final int IMAGE_MAX_SIZE = 50;
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				// b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = imageBitmap.getHeight();
				int width = imageBitmap.getWidth();

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) x,
						(int) y, true);
				imageBitmap.recycle();
				imageBitmap= scaledBitmap;

				System.gc();
			} else {
				// b = BitmapFactory.decodeStream(in);
			}

		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			System.out.println("exception in get bitma putility");
		} catch (Exception e) {
			System.out.println("exception in get bitma putility");
			e.printStackTrace();
		}
		return imageBitmap;
	}
	
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}
	}
}
