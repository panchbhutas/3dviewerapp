package com.panchbhutas.shambho.ui.home;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.panchbhutas.shambho.HomeActivity;
import com.panchbhutas.shambho.MainActivity;
import com.panchbhutas.shambho.R;

import java.net.InetAddress;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static WebView webView;
    private static final String webViewUrl = "https://panchbhutas.in/shambho/"; //replace url with your own server url (copy files 'web files' folder to your web server  )
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    public RelativeLayout rlSplash;
    public RelativeLayout rlNetworkError;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        webView = (WebView) root.findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "app");
        new CheckInternetConnectionTask().execute();
        rlSplash = root.findViewById(R.id.rlSplash);
        rlNetworkError = root.findViewById(R.id.rlAlertMsg);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                String fileName = url.substring(url.lastIndexOf('/') + 1);
                Toast.makeText(getActivity().getApplicationContext(), "Downloading File " + fileName, Toast.LENGTH_LONG).show();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                request.setDescription("Downloading file...");
                request.setTitle(fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getActivity().getApplicationContext(), "Download complete - " +fileName, Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }

    private void LoadWebViewUrl(String url) {
        //if (isInternetConnected())
        webView.loadUrl(url);

        //else {
        //    refresh.setVisibility(View.VISIBLE);
        //    Toast.makeText(MainActivity.this, "Oops!! There is no internet connection. Please enable your internet connection.", Toast.LENGTH_LONG).show();

        //}
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //refresh.setVisibility(View.GONE);
            //if (!webViewProgressBar.isShown())
            //    webViewProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //rlSplash.setVisibility(View.GONE);
            rlSplash.animate()
                    .alpha(0.0f)
                    .setDuration(500);
            String objFileName=((HomeActivity)getActivity()).selObjFileName;
            webView.loadUrl("javascript:displayObject(\""+objFileName.toString()+"\")");
            //refresh.setVisibility(View.VISIBLE);
            //if (webViewProgressBar.isShown())
            //    webViewProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //refresh.setVisibility(View.VISIBLE);
            //if (webViewProgressBar.isShown())
            //    webViewProgressBar.setVisibility(View.GONE);
            //Toast.makeText(MainActivity.this, "Unexpected error occurred.Reload page again.", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            //refresh.setVisibility(View.VISIBLE);
            //if (webViewProgressBar.isShown())
            //    webViewProgressBar.setVisibility(View.GONE);
            //Toast.makeText(MainActivity.this, "Unexpected error occurred.Reload page again.", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            //refresh.setVisibility(View.VISIBLE);
            //if (webViewProgressBar.isShown())
            //    webViewProgressBar.setVisibility(View.GONE);
            //Toast.makeText(MainActivity.this, "Unexpected SSL error occurred.Reload page again.", Toast.LENGTH_SHORT).show();

        }

    }


    public class CheckInternetConnectionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                InetAddress ipAddr = InetAddress.getByName("google.com");
                if(!ipAddr.equals("")) return "true";
                else return "false";
            } catch (Exception e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // You might want to change "executed" for the returned string
            // passed into onPostExecute(), but that is up to you

            if(result.equals("true"))
                LoadWebViewUrl(webViewUrl);
            else
                rlNetworkError.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), "Error while connecting to internet! Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
        }
    }

    public class CheckInternetConnectionTaskOnResume extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                if(!ipAddr.equals("")) return "true";
                else return "false";
            } catch (Exception e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("false"))
                rlNetworkError.setVisibility(View.VISIBLE);
            else{
                if(rlNetworkError.getVisibility() == View.VISIBLE){
                    rlNetworkError.setVisibility(View.GONE);
                    LoadWebViewUrl(webViewUrl);
                }
            }
        }
    }

    /*
     * JavaScript Interface. Web code can access methods in here
     * (as long as they have the @JavascriptInterface annotation)
     */
    public class WebViewJavaScriptInterface{

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void makeToast(String message, boolean lengthLong){
            //Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }
    }

}