package com.ionexplus.titu.view.search;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.exoplayer2.util.Log;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.FragmentSearchBinding;
import com.ionexplus.titu.viewmodel.MainViewModel;
import com.ionexplus.titu.viewmodel.SearchFragmentViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private ImageView[] dots;
    SearchFragmentViewModel viewModel;
    FragmentSearchBinding binding;
    private MainViewModel parentViewModel;
    private static final int MY_PERMISSIONS_REQUEST = 101;
    LinearLayout sliderDotspanel;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new SearchFragmentViewModel()).createFor()).get(SearchFragmentViewModel.class);
        initView();
        initObserve();
        initListeners();
        binding.setViewmodel(viewModel);
    }

    private void initView() {

        sliderDotspanel = (LinearLayout) getView().findViewById(R.id.sliderDots);
        viewModel.bannerLoaded.observe(this,loaded->{

            if(loaded && dots == null){
                dots = new ImageView[viewModel.bannerAdapter.getItemCount()];
                for (int i = 0; i < viewModel.bannerAdapter.getItemCount(); i++) {
                    dots[i] = new ImageView(getContext());
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);
                    sliderDotspanel.addView(dots[i], params);
                }
                dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
            }else if(loaded && dots.length != viewModel.bannerAdapter.getItemCount()){
                int x =  Math.abs(viewModel.bannerAdapter.getItemCount() - dots.length);
                dots = new ImageView[x];
                for (int i = 0; i < x; i++) {
                    dots[i] = new ImageView(getContext());
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);
                    sliderDotspanel.addView(dots[i], params);
                }
            }


        });



        binding.banner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int pos = ((LinearLayoutManager)(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                btnAction(pos,viewModel.bannerAdapter.getItemCount());
            }
        });
        binding.refreshlout.setEnableRefresh(false);

    }

    private void btnAction(int position, int bannerListSize) {
        for (int i = 0; i < bannerListSize; i++) {
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
            }
        }
    }

    private void initObserve() {
        parentViewModel.selectedPosition.observe(this, position -> {
            if (position != null && position == 1) {
                viewModel.exploreStart = 0;
                viewModel.fetchExploreItems(false);
                viewModel.fetchBannerItems();
                parentViewModel.selectedPosition.setValue(null);
            }
        });
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());

    }

    private void initListeners() {

        binding.imgSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra("search", binding.etSearch.getText().toString());
            startActivity(intent);
        });


        binding.imgQr.setOnClickListener(v -> initPermission());

        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onExploreLoadMore());

    }

    private void initPermission() {
        if (getActivity() != null && getActivity().getPackageManager() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST);
            } else {
                startActivity(new Intent(getContext(), QRScanActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED && getActivity() != null && getActivity().getPackageManager() != null) {
            startActivity(new Intent(getContext(), QRScanActivity.class));
        }
    }

}
