package com.ionexplus.titu.view.search;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivityHashtagBinding;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.Global;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.viewmodel.HashTagViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

public class HashTagActivity extends BaseActivity {

    ActivityHashtagBinding binding;

    HashTagViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hashtag);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HashTagViewModel()).createFor()).get(HashTagViewModel.class);

        initView();
        initObserve();
        initListeners();

        binding.setViewmodel(viewModel);

    }

    private void initListeners() {
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onLoadMore());
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (getIntent().getStringExtra("hashtag") != null) {
            viewModel.hashtag = getIntent().getStringExtra("hashtag");
        }
        binding.refreshlout.setEnableRefresh(false);
        viewModel.adapter.setHashTag(true);
        viewModel.adapter.setWord(viewModel.hashtag);
        viewModel.fetchHashTagVideos(false);


    }

    private void initObserve() {
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(this);
        viewModel.isloading.observe(this,isloading->{
            if(isloading){
                customDialogBuilder.showLoadingDialog();
            }else{
                customDialogBuilder.hideLoadingDialog();
            }
        });
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
        viewModel.video.observe(this, video -> binding.tvVideoCount.setText(Global.prettyCount(video.getPostCount()).concat(" Videos")));
    }
}