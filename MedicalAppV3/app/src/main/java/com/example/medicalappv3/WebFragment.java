package com.example.medicalappv3;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.example.medicalappv3.UserDefinedClasses.WebPos;

public class WebFragment extends Fragment {
    public static WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        String[] url = new String[]{"https://www.1mg.com", "https://www.pharmeasy.in", "https://www.netmeds.com", "https://www.medplusmart.com"};
        webView = view.findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        MyWebview myWebview = new MyWebview(getActivity());
        webView.setWebViewClient(myWebview);
        webView.loadUrl(url[WebPos.position]);
        return view;
    }

    private class MyWebview extends WebViewClient {
        private Activity activity = null;

        public MyWebview(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

    }

}