package com.tbldevelopment.bmny;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class WebActivity extends Activity{
	
	WebView mWebView;
	String URL;
	
	private Context appContext = this;
	private ProgressDialog applicationDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.web_activity);
		
		
        /*Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
        	URL=bundle.getString("url");
        }*/
		
		URL="https://play.google.com/store/music/artist?id=Azuvjoaksolbzc4fltaf2ok3um4";

		applicationDialog = ProgressDialog.show(appContext, "",
				"Please Wait ");
		applicationDialog.setCancelable(true);
        
        mWebView = (WebView)findViewById( R.id.my_web_view );
        
             WebViewClient yourWebClient = new WebViewClient()
               {
                   // Override page so it's load on my view only
                   @Override
                   public boolean shouldOverrideUrlLoading(WebView  view, String  url)
                   {
                
                    return true;
                   }
                   
                   public void onPageFinished(WebView view, String url) {
   	                //dismiss the indeterminate progress dialog
   	                 applicationDialog.dismiss(); 
   	            }
               };
               
               
              
               mWebView.getSettings().setJavaScriptEnabled(true);   
               mWebView.getSettings().setBuiltInZoomControls(true); 
               mWebView.setWebViewClient(yourWebClient);
              
               // Load URL
               mWebView.loadUrl(URL);
                
           }//End of Method onCreate
        
	public void onResume() {
		super.onResume();
		
	}
        
}

