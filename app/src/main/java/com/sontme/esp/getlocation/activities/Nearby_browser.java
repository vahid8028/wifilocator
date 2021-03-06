package com.sontme.esp.getlocation.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sontme.esp.getlocation.R;

public class Nearby_browser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_nearby_browser);

        WebView webmap = findViewById(R.id.webmap);
        webmap.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        webmap.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webmap.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webmap.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //webmap.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webmap.getSettings().setAppCacheEnabled(true);
        webmap.getSettings().setDatabaseEnabled(true);
        webmap.getSettings().setDomStorageEnabled(true);
        webmap.getSettings().setJavaScriptEnabled(true);

        webmap.loadUrl("https://sont.sytes.net/wifilocator/map.php");
    }
}
