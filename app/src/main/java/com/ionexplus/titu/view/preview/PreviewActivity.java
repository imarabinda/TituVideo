package com.ionexplus.titu.view.preview;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.appevents.AppEventsConstants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivityPreviewBinding;
import com.ionexplus.titu.databinding.ItemUploadSheetBinding;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.GlobalApi;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.viewmodel.PreviewViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

import java.io.File;
import java.util.Objects;

public class PreviewActivity extends BaseActivity {

    private ActivityPreviewBinding binding;
    private PreviewViewModel viewModel;
    private CustomDialogBuilder customDialogBuilder;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new PreviewViewModel()).createFor()).get(PreviewViewModel.class);
        customDialogBuilder = new CustomDialogBuilder(this);
        viewModel.sessionManager = sessionManager;
        initView();
        playVideo();
        initObserve();
        initListener();
    }


    private void initView() {
        viewModel.videoPath = getIntent().getStringExtra("post_video");
        viewModel.videoThumbnail = getIntent().getStringExtra("post_image");
        viewModel.soundPath = getIntent().getStringExtra("post_sound");
        viewModel.soundImage = getIntent().getStringExtra("sound_image");
        viewModel.soundId = getIntent().getStringExtra("soundId");
    }

    private void playVideo() {

        player = new SimpleExoPlayer.Builder(this).build();

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "TiTu"));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(viewModel.videoPath));

        player.prepare(videoSource);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.setPlaybackParameters(PlaybackParameters.DEFAULT);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        binding.playerView.setPlayer(player);
        player.setPlayWhenReady(true);

    }

    private void initObserve() {
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                customDialogBuilder.showLoadingDialog();
            } else {
                deleteRecursive(getPath());
                customDialogBuilder.hideLoadingDialog();
                setResult(RESULT_OK);
                new GlobalApi().rewardUser("video_upload");
                Toast.makeText(this, "Video Upload SuccessFully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

    }

    private void initListener() {
        binding.setOnBackClick(v -> onBackPressed());
        binding.setOnUploadClick(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            ItemUploadSheetBinding uploadSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.item_upload_sheet, null, false);
            dialog.setContentView(uploadSheetBinding.getRoot());
            dialog.setCanceledOnTouchOutside(false);
            dialog.setDismissWithAnimation(true);
            uploadSheetBinding.setViewModel(viewModel);
            uploadSheetBinding.ivThumb.setImageURI(Uri.parse(viewModel.videoThumbnail));

            uploadSheetBinding.switchComment.setOnClickListener(view->{
                if (uploadSheetBinding.switchComment.isChecked()) {
                    viewModel.canComment = AppEventsConstants.EVENT_PARAM_VALUE_NO;
                }else{
                    viewModel.canComment = AppEventsConstants.EVENT_PARAM_VALUE_YES;
                }
            });

            uploadSheetBinding.switchSave.setOnClickListener(view->{
                if (uploadSheetBinding.switchSave.isChecked()) {
                    viewModel.canSave = AppEventsConstants.EVENT_PARAM_VALUE_NO;
                    Log.d("CAN_SAVE", "initListener: " + viewModel.canSave);
                }else{
                    viewModel.canSave = AppEventsConstants.EVENT_PARAM_VALUE_YES;
                }
            });
            uploadSheetBinding.switchDuet.setOnClickListener(view->{
                if (uploadSheetBinding.switchDuet.isChecked()) {
                    viewModel.canDuet = AppEventsConstants.EVENT_PARAM_VALUE_NO;
                    Log.d("CAN_DUET", "initListener: " + this.viewModel.canDuet);
                }else{
                    viewModel.canDuet = AppEventsConstants.EVENT_PARAM_VALUE_YES;
                }
            });


            viewModel.onClickUpload.observe(this, s -> {
                if (!uploadSheetBinding.edtDes.getHashtags().isEmpty()) {
                    viewModel.hashTag = TextUtils.join(",", uploadSheetBinding.edtDes.getHashtags());
                }
            });

            uploadSheetBinding.imgClose.setOnClickListener(click->{
                dialog.dismiss();
            });
//            uploadSheetBinding.edtDes.setOnHashtagClickListener((view, text) -> {
//
//            });
            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            dialog.show();
        });

    }

    public File getPath() {
        String state = Environment.getExternalStorageState();
        File filesDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            filesDir = getExternalFilesDir(null);
        } else {
            filesDir = getFilesDir();
        }
        return filesDir;
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory != null && fileOrDirectory.isDirectory() && fileOrDirectory.listFiles() != null) {
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles())) {
                deleteRecursive(child);
            }
        }
        if (fileOrDirectory != null) {
            Log.d("TAG", "deleteRecursive: " + fileOrDirectory.delete());
        }
    }

    @Override
    protected void onResume() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }
}