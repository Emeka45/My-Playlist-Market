package com.emeka45.myspotify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends Activity {
    private static final String HOME = "https://open.spotify.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Spotify's web player requires a supported full browser for protected audio.
        // Android WebView does not expose the same reliable DRM/browser environment.
        openSpotifyInBrowser();
    }

    private void openSpotifyInBrowser() {
        Uri spotify = Uri.parse(HOME);
        try {
            CustomTabColorSchemeParams colors = new CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(Color.rgb(25, 20, 20))
                    .setNavigationBarColor(Color.rgb(25, 20, 20))
                    .setSecondaryToolbarColor(Color.rgb(29, 185, 84))
                    .build();

            CustomTabsIntent tabs = new CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(colors)
                    .setShowTitle(true)
                    .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                    .build();

            tabs.launchUrl(this, spotify);
            finish();
        } catch (Exception customTabError) {
            try {
                Intent browser = new Intent(Intent.ACTION_VIEW, spotify);
                startActivity(browser);
                finish();
            } catch (Exception browserError) {
                Toast.makeText(this,
                        "Please install or update Chrome, Firefox, or another Spotify-supported browser, then try again.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
