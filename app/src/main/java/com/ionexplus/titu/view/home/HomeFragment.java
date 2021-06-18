package com.ionexplus.titu.view.home;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.ionexplus.titu.R;
import com.ionexplus.titu.adapter.HomeViewPagerAdapter;
import com.ionexplus.titu.databinding.FragmentMainBinding;
import com.ionexplus.titu.view.base.BaseFragment;
import com.ionexplus.titu.viewmodel.HomeViewModel;
import com.ionexplus.titu.viewmodel.MainViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {


    private FragmentMainBinding binding;
    private MainViewModel parentViewModel;
    private HomeViewModel viewModel;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        viewModel = ViewModelProviders.of(requireActivity(), new ViewModelFactory(new HomeViewModel()).createFor()).get(HomeViewModel.class);
        initView();
        initViewPager();
        initObserve();
        initListener();
    }

    private void initView() {
        binding.swipeRefresh.setProgressViewOffset(true, 0, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.napier_green), ContextCompat.getColor(getActivity(), R.color.india_red), ContextCompat.getColor(getActivity(), R.color.kellygreen), ContextCompat.getColor(getActivity(), R.color.tufs_blue), ContextCompat.getColor(getActivity(), R.color.tiffanyblue), ContextCompat.getColor(getActivity(), R.color.Sanddtorm), ContextCompat.getColor(getActivity(), R.color.salmonpink_1));
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public final void onRefresh() {
                viewModel.onRefresh.setValue(true);
                MediaPlayer create = MediaPlayer.create(getActivity(), (int) R.raw.refresh_);
                mediaPlayer = create;
                create.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    public final void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    public final void onCompletion(MediaPlayer mediaPlayer) {
                        viewModel.onRefresh.setValue(false);
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                });
            }
        });

        binding.tvFollowing.setTextColor(getResources().getColor(R.color.grey));
    }

    private void initViewPager() {
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(homeViewPagerAdapter);
        binding.viewPager.setCurrentItem(1);
        viewModel.onPageSelect.postValue(1);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
          
            }

            @Override
            public void onPageSelected(int i) {
                viewModel.onPageSelect.postValue(Integer.valueOf(i));
                boolean z = true;
                viewModel.onStop.setValue(true);
                SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefresh;
                if (i != 1) {
                    z = false;
                }
                swipeRefreshLayout.setEnabled(z);
                if (i == 0) {
                    binding.tvForu.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
                    binding.tvFollowing.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    return;
                }
                binding.tvFollowing.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
                binding.tvForu.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
      
            }
        });
    }

    private void initObserve() {
        parentViewModel.onStop.observe(getViewLifecycleOwner(), onStop -> {
            viewModel.onPageSelect.setValue(binding.viewPager.getCurrentItem());
            viewModel.onStop.postValue(onStop);
        });


        viewModel.loadingVisibility = parentViewModel.loadingVisibility;
        viewModel.isShowLoaderInHome = parentViewModel.isShowLoaderInHome;
        viewModel.scroll = parentViewModel.scroll;
        viewModel.onRefresh.observe(getViewLifecycleOwner(), onRefresh-> {
            if (onRefresh != null && !onRefresh.booleanValue()) {
                binding.swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void initListener() {
        binding.tvFollowing.setOnClickListener(v -> {
            binding.tvForu.setTextColor(getResources().getColor(R.color.grey));
            binding.tvFollowing.setTextColor(getResources().getColor(R.color.white));
            binding.viewPager.setCurrentItem(0);
        });
        binding.tvForu.setOnClickListener(v -> {
            binding.tvFollowing.setTextColor(getResources().getColor(R.color.grey));
            binding.tvForu.setTextColor(getResources().getColor(R.color.white));
            binding.viewPager.setCurrentItem(1);
        });
    }

}
