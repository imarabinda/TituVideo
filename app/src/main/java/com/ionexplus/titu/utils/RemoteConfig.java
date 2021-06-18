package com.ionexplus.titu.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ionexplus.titu.R;

import static android.content.ContentValues.TAG;

public class RemoteConfig {
    public FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    public interface OnConfigsUpdate {
        void onUpdate();
    }
    public void init(OnConfigsUpdate configsUpdate){
      mFirebaseRemoteConfig.setDefaultsAsync(R.xml.configs);

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(mFirebaseRemoteConfig.getBoolean("cache_expire") ? mFirebaseRemoteConfig.getLong("cache_expire"):0)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            configsUpdate.onUpdate();
                        }
                    }
                });
    }

    public String getString(String key){
        return mFirebaseRemoteConfig.getString(key);
    }

    public long getLong(String key){
        return mFirebaseRemoteConfig.getLong(key);
    }

    public boolean getBoolean(String  key){
        return mFirebaseRemoteConfig.getBoolean(key);
    }

}
