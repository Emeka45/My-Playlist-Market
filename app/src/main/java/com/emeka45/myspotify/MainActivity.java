package com.emeka45.myspotify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    private static final String HOME = "https://open.spotify.com/";
    private static final int FILE_PICKER = 1001;

    private WebView webView;
    private ProgressBar progressBar;
    private android.webkit.ValueCallback<Uri[]> fileCallback;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        FrameLayout root = new FrameLayout(this);
        webView = new WebView(this);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);

        root.addView(webView, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(-1, 5, Gravity.TOP);
        root.addView(progressBar, progressParams);

        ImageButton menu = new ImageButton(this);
        menu.setImageResource(android.R.drawable.ic_menu_more);
        menu.setColorFilter(Color.WHITE);
        menu.setBackgroundColor(Color.TRANSPARENT);
        menu.setContentDescription("My Spotify options");
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(52, 52, Gravity.TOP | Gravity.END);
        menuParams.topMargin = 8;
        menuParams.rightMargin = 6;
        root.addView(menu, menuParams);
        menu.setOnClickListener(v -> showMenu(v));

        setContentView(root);
        configureWebView();
        if (savedInstanceState == null) webView.loadUrl(HOME);
        else webView.restoreState(savedInstanceState);
    }

    private void configureWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setLoadWithOverviewMode(false);
        s.setUseWideViewPort(false);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Mobile Safari/537.36");

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                if (scheme == null || scheme.equals("http") || scheme.equals("https")) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch (Exception ignored) {}
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }

            @Override public boolean onShowFileChooser(WebView view, android.webkit.ValueCallback<Uri[]> callback, FileChooserParams params) {
                fileCallback = callback;
                try {
                    startActivityForResult(params.createIntent(), FILE_PICKER);
                } catch (Exception e) {
                    callback.onReceiveValue(null);
                    fileCallback = null;
                }
                return true;
            }
        });
    }

    private void showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Reload Spotify");
        popup.getMenu().add("Clear web cache");
        popup.getMenu().add("Open Spotify in browser");
        popup.getMenu().add("About My Spotify");
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Reload Spotify")) {
                webView.reload();
                return true;
            }
            if (title.equals("Clear web cache")) {
                webView.clearCache(true);
                webView.reload();
                return true;
            }
            if (title.equals("Open Spotify in browser")) {
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(HOME))); } catch (Exception ignored) {}
                return true;
            }
            if (title.equals("About My Spotify")) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("My Spotify")
                        .setMessage("A lightweight Spotify web container. It keeps Spotify's service online while avoiding the full Spotify Android app. Music and Spotify content are not downloaded by this app.")
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER && fileCallback != null) {
            Uri[] result = resultCode == RESULT_OK && data != null && data.getData() != null
                    ? new Uri[]{data.getData()} : null;
            fileCallback.onReceiveValue(result);
            fileCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        if (webView != null) {
            CookieManager.getInstance().flush();
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
