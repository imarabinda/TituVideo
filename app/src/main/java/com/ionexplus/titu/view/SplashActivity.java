package com.ionexplus.titu.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.ionexplus.titu.BuildConfig;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivitySplashBinding;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.RemoteConfig;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.home.MainActivity;

import static android.content.ContentValues.TAG;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;
    RemoteConfig mFirebaseRemoteConfig;
    private int updateRequire = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        mFirebaseRemoteConfig = new RemoteConfig();
        mFirebaseRemoteConfig.init(this::hide);
    }

public void hide(){

    updateRequire = compareVersionNames(BuildConfig.VERSION_NAME, !mFirebaseRemoteConfig.getString("minimum_app_version").isEmpty() ? mFirebaseRemoteConfig.getString("minimum_app_version"):BuildConfig.VERSION_NAME);
    new Handler().postDelayed(() -> {
        binding.executePendingBindings();
        if (updateRequire == -1) {
            CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(this);

            customDialogBuilder.showSimpleDialog(mFirebaseRemoteConfig.getString("update_title"), mFirebaseRemoteConfig.getString("update_description"), "Close", "Update", new CustomDialogBuilder.OnDismissListener() {
                @Override
                public void onPositiveDismiss() {
                    final String appPackageName = getPackageName();
                    if (!mFirebaseRemoteConfig.getString("update_url").isEmpty()) {
                            try{
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mFirebaseRemoteConfig.getString("update_url"))));
                            }catch (android.content.ActivityNotFoundException anfe){
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        } else {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    finish();
                }

                @Override
                public void onNegativeDismiss() {
                    finishAndRemoveTask();
                    System.exit(0);
                }
            });
        }else if(mFirebaseRemoteConfig.getBoolean("maintenance_mode")){
            CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(this);
            customDialogBuilder.showSimpleDialog(mFirebaseRemoteConfig.getString("maintenance_title"), mFirebaseRemoteConfig.getString("maintenance_description"), "Close", "Try Again", new CustomDialogBuilder.OnDismissListener() {
                @Override
                public void onPositiveDismiss() {
                    Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onNegativeDismiss() {
                    finishAndRemoveTask();
                    System.exit(0);
                }
            });

        }
        else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }

    }, 500);
}


    public int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i ++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length)? 1 : -1;
        }

        return res;
    }



}
