package com.ionexplus.titu.view.argear;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexplus.titu.R;
import com.ionexplus.titu.adapter.FilterListAdapter;
import com.ionexplus.titu.api.ContentsResponse;
import com.ionexplus.titu.databinding.FragmentBeautyBinding;
import com.ionexplus.titu.databinding.FragmentFilterBinding;
import com.ionexplus.titu.model.CategoryModel;
import com.ionexplus.titu.model.ItemModel;
import com.ionexplus.titu.view.recordvideo.CameraActivity;
import com.ionexplus.titu.viewmodel.ContentsViewModel;

import java.util.Locale;

public class FilterFragment
        extends Fragment
        implements View.OnClickListener, FilterListAdapter.Listener {

    private static final String TAG = FilterFragment.class.getSimpleName();

    private FilterListAdapter mFilterListAdapter;
    private ContentsViewModel mContentsViewModel;
    private FragmentFilterBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_filter, viewGroup, false);



        binding.filterCloseButton.setOnClickListener(this);
        binding.filterInitButton.setOnClickListener(this);
        binding.filterSeekbar.setOnSeekBarChangeListener(FilterSeekBarListener);
        binding.filterComparisonButton.setOnTouchListener(FilterComparisonBtnTouchListener);



        binding.recyclerViewFilter.setHasFixedSize(true);
        LinearLayoutManager filterLayoutManager = new LinearLayoutManager(getContext());
        filterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerViewFilter.setLayoutManager(filterLayoutManager);
        mFilterListAdapter = new FilterListAdapter(getContext(), this);
        binding.recyclerViewFilter.setAdapter(mFilterListAdapter);

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            mContentsViewModel = ViewModelProviders.of(getActivity()).get(ContentsViewModel.class);
            mContentsViewModel.getContents().observe(getViewLifecycleOwner(), new Observer<ContentsResponse>() {
                @Override
                public void onChanged(ContentsResponse contentsResponse) {

                    if (contentsResponse == null) return;

                    for (CategoryModel model : contentsResponse.categories) {
                        if (TextUtils.equals(model.title, "Filter")) {
                            mFilterListAdapter.setData(model.items);
                            return;
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void zeroFilterParam() {
        ((CameraActivity) getActivity()).setFilterStrength(0);
    }

    private void reloadFilter() {
        ((CameraActivity) getActivity()).setFilterStrength(slider);
    }

    private View.OnTouchListener FilterComparisonBtnTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                zeroFilterParam();
            } else if (1 == motionEvent.getAction()) {
                reloadFilter();
            }
            return true;
        }
    };

private int slider = 100;
    private SeekBar.OnSeekBarChangeListener FilterSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                ((CameraActivity)getActivity()).setFilterStrength(i);
                slider = i;
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_close_button:
                getActivity().onBackPressed();
                break;
            case R.id.filter_init_button: {
                ((CameraActivity)getActivity()).clearFilter();
                binding.filterSeekbar.setProgress(0);
                break;
            }
//            case R.id.filter_plus_button:
//                ((CameraActivity)getActivity()).setFilterStrength(10);
//                break;
//            case R.id.filter_minus_button:
//                ((CameraActivity)getActivity()).setFilterStrength(-10);
//                break;
//            case R.id.vignett_button:
//                ((CameraActivity)getActivity()).setVignette();
//                break;
//            case R.id.blur_button:
//                ((CameraActivity)getActivity()).setBlurVignette();
//                break;
        }
    }

    @Override
    public void onFilterSelected(int position, ItemModel item) {
        binding.filterSeekbar.setProgress(100);
        ((CameraActivity)getActivity()).setFilter(item);
    }
}
