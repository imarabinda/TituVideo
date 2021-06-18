package com.ionexplus.titu.view.share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.ionexplus.titu.R;
import com.ionexplus.titu.model.user.User;
import com.ionexplus.titu.model.videos.Video;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.home.MainActivity;
import com.ionexplus.titu.view.search.FetchUserActivity;
import com.ionexplus.titu.view.video.PlayerActivity;

import org.json.JSONException;

import java.util.ArrayList;

import io.branch.referral.Branch;

public class ShareHandleActivity extends BaseActivity {

    private CustomDialogBuilder customDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_handle);

        getDataFromBranch();
        customDialogBuilder = new CustomDialogBuilder(this);
        customDialogBuilder.showLoadingDialog();
    }

    private void getDataFromBranch() {
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession((referringParams, error) -> {
            if (error == null) {
                if (referringParams != null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                }

                try {
                    if (referringParams != null && referringParams.has("data")) {
                        customDialogBuilder.hideLoadingDialog();

                        String data = referringParams.getString("data");

                        User user = new Gson().fromJson(data, User.class);
                        Video.Data video = new Gson().fromJson(data, Video.Data.class);
                        if (user != null && user.getData() != null) {
                            Intent intent = new Intent(ShareHandleActivity.this, FetchUserActivity.class);
                            intent.putExtra("userid", user.getData().getUserId());
                            startActivity(intent);
                        } else if (video != null) {
                            Intent intent = new Intent(ShareHandleActivity.this, PlayerActivity.class);
                            ArrayList<Video.Data> mList = new ArrayList<>();
                            mList.add(video);
                            intent.putExtra("video_list", new Gson().toJson(mList));
                            intent.putExtra("position", 0);
                            intent.putExtra("type", 5);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(ShareHandleActivity.this, MainActivity.class));
                            finish();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.i("BRANCH SDK", error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }
}