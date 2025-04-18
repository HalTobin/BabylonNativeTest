package com.moineaufactory.babylonnativetest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.library.babylonnative.BabylonView;

public class PlaygroundActivity extends Activity implements BabylonView.ViewDelegate {
    BabylonView mView;
    String modelPath;

    // Activity life
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        modelPath = getIntent().getStringExtra("path");

        mView = new BabylonView(getApplication(), this);
        setContentView(mView);
    }

    @Override
    protected void onPause() {
        mView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        mView.onRequestPermissionsResult(requestCode, permissions, results);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mView.getVisibility() == View.GONE) {
            mView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewReady() {
        // Define JS variable
        String injectedJs = "globalThis.modelPath = '" + modelPath + "';";
        String sourceUrl = "runtime-injected.js";

        Log.i("PlaygroundActivity", "load: " + modelPath);
        mView.eval(injectedJs, sourceUrl);
        mView.loadScript("app:///Scripts/experience_variant.js");
    }
}
