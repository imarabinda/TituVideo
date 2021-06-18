package com.ionexplus.titu.view.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivitySoundVideosBinding;
import com.ionexplus.titu.utils.Const;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.Global;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.recordvideo.CameraActivity;
import com.ionexplus.titu.viewmodel.SoundActivityViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

public class SoundVideosActivity extends BaseActivity {


    ActivitySoundVideosBinding binding;
    SoundActivityViewModel viewModel;
    SimpleExoPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sound_videos);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new SoundActivityViewModel()).createFor()).get(SoundActivityViewModel.class);
        initView();
        initListeners();
        initObserve();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        viewModel.soundId = getIntent().getStringExtra("soundid");
        viewModel.soundUrl = getIntent().getStringExtra("sound");
        viewModel.adapter.setSoundId(viewModel.soundId);
        viewModel.fetchSoundVideos(false);

        player = new SimpleExoPlayer.Builder(this).build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "TiTU"));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(Const.ITEM_BASE_URL + viewModel.soundUrl));
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare(videoSource);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        binding.loutShoot.startAnimation(animation);
        binding.tvSoundTitle.setSelected(true);


        if (sessionManager != null && sessionManager.getFavouriteMusic() != null) {
            viewModel.isFavourite.set(sessionManager.getFavouriteMusic().contains(viewModel.soundId));
        }

    }

    private void initListeners() {
        binding.loutFavourite.setOnClickListener(v -> {
            sessionManager.saveFavouriteMusic(viewModel.soundId);
            viewModel.isFavourite.set(!viewModel.isFavourite.get());
        });

        binding.imgPlay.setOnClickListener(v -> {
            if (viewModel.isPlaying.get()) {
                player.setPlayWhenReady(false);
                viewModel.isPlaying.set(false);
            } else {
                player.setPlayWhenReady(true);
                viewModel.isPlaying.set(true);
            }
        });
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onLoadMore());
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.loutShoot.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("music_url", viewModel.soundUrl);
            intent.putExtra("music_title", viewModel.soundData.getValue() != null ? viewModel.soundData.getValue().getSoundTitle() : "Sound_Title");
            intent.putExtra("sound_id", viewModel.soundId);
            startActivity(intent);
        });
    }

    private void initObserve() {
        CustomDialogBuilder customDialogBuilder =  new CustomDialogBuilder(this);
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
        viewModel.soundData.observe(this, soundData -> {
            binding.setSoundData(soundData);
            binding.tvVideoCount.setText(Global.prettyCount(soundData.getPostVideoCount()).concat(" Videos"));

        });

        viewModel.isloading.observe(this,isloading->{
            if(isloading){
                customDialogBuilder.showLoadingDialog();
            }else{
                customDialogBuilder.hideLoadingDialog();
            }
        });
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }
}