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
    private static final String HOME = "https://playlist.market/curator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openCuratorPortal();
    }

    private void openCuratorPortal() {
        Uri portal = Uri.parse(HOME);
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

            tabs.launchUrl(this, portal);
            finish();
        } catch (Exception customTabError) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, portal));
                finish();
            } catch (Exception browserError) {
                Toast.makeText(this,
                        "Please install or update a supported browser, then try again.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
