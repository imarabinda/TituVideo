package com.ionexplus.titu.view.web;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.google.android.exoplayer2.util.Log;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivityWebViewBinding;
import com.ionexplus.titu.utils.RemoteConfig;
import com.ionexplus.titu.view.base.BaseActivity;

public class WebViewActivity extends BaseActivity {
 
    ActivityWebViewBinding binding;
    RemoteConfig remoteConfig;
    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteConfig = new RemoteConfig();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);

        int type = getIntent().getIntExtra("type",0);
        if(type == 0){
            binding.headerText.setText("Terms and Conditions");
            binding.webview.loadUrl(remoteConfig.getString("terms_url"));
        }else if(type == 1){
            binding.headerText.setText("Privacy Policy");
            binding.webview.loadUrl(remoteConfig.getString("privacy_url"));
        }else if(type == 2){
            binding.headerText.setText("Help");
            binding.webview.loadUrl(remoteConfig.getString("help_url"));
        }

        binding.imgBack.setOnClickListener(v -> onBackPressed());

    }
}