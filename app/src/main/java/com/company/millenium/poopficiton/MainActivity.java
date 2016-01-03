package com.company.millenium.poopficiton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    WebView myWebView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) this.findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setUseWideViewPort(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        myWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        if (!DetectConnection.checkInternetConnection(this)) {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            showNoConnectionDialog(this);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        myWebView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals(Constants.HOST) || Uri.parse(url).getHost().equals(Constants.WWWHOST)) {
                    // This is my web site, so do not override; let my WebView load
                    // the page
                    return false;
                }
                // Otherwise, the link is not for a page on my site, so launch
                // another Activity that handles URLs
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        myWebView.loadUrl("http://readpoopfiction.com");


        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity
            //startActivity(new Intent(MainActivity.this, MyIntro.class));
        }
        //if (!isOnline())
        //    showNoConnectionDialog(this);
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

        //Pull-to-refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra("url")) {
            String url = intent.getStringExtra("url");
            myWebView.loadUrl(Constants.SERVER_URL + url);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (DetectConnection.checkInternetConnection(this)) {
            myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            myWebView.reload();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    public static void showNoConnectionDialog(Context ctx1) {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialog);
        builder.setCancelable(true);
        builder.setMessage(R.string.no_connection);
        builder.setTitle(R.string.no_connection_title);
        builder.setPositiveButton(R.string.settings_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
    }

    @Override
    public void onRefresh() {
        myWebView.reload();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


}
