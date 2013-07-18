package com.tbldevelopment.bmny.sharetweeter;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CustomeWebView extends WebView{

	public CustomeWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public CustomeWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public CustomeWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		try{
			super.onWindowFocusChanged(hasWindowFocus);
		}catch(NullPointerException e){
			// Catch null pointer exception 
			e.printStackTrace();
		}
	}

}
