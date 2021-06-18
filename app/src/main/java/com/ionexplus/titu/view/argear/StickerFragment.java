package com.ionexplus.titu.view.argear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexplus.titu.R;
import com.ionexplus.titu.adapter.StickerCategoryListAdapter;
import com.ionexplus.titu.adapter.StickerListAdapter;
import com.ionexplus.titu.api.ContentsResponse;
import com.ionexplus.titu.databinding.FragmentStickerBinding;
import com.ionexplus.titu.model.CategoryModel;
import com.ionexplus.titu.model.ItemModel;
import com.ionexplus.titu.view.recordvideo.CameraActivity;
import com.ionexplus.titu.viewmodel.ContentsViewModel;


public class StickerFragment
        extends Fragment
        implements View.OnClickListener, StickerCategoryListAdapter.Listener, StickerListAdapter.Listener {

    private FragmentStickerBinding binding;
    private StickerCategoryListAdapter mStickerCategoryListAdapter;
    private StickerListAdapter mStickerListAdapter;
    private ContentsViewModel mContentsViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } 
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_sticker, viewGroup, false);

        //click listener
        binding.clearStickerButton.setOnClickListener(this);
        binding.closeStickerButton.setOnClickListener(this);

        //sticker category list
        binding.stickerCategoryRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.stickerCategoryRecyclerview.setLayoutManager(linearLayoutManager);
        mStickerCategoryListAdapter = new StickerCategoryListAdapter(this);
        binding.stickerCategoryRecyclerview.setAdapter(mStickerCategoryListAdapter);

        //sticker items
        binding.stickerRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        binding.stickerRecyclerview.setLayoutManager(linearLayoutManager2);
        mStickerListAdapter = new StickerListAdapter(getContext(), this);
        binding.stickerRecyclerview.setAdapter(mStickerListAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            mContentsViewModel = ViewModelProviders.of(getActivity()).get(ContentsViewModel.class);
            mContentsViewModel.getContents().observe(getViewLifecycleOwner(), new Observer<ContentsResponse>() {
                @Override
                public void onChanged(ContentsResponse contentsResponse) {
                    if (contentsResponse != null && contentsResponse.categories != null) {
                        mStickerCategoryListAdapter.setData(contentsResponse.categories);
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

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.close_sticker_button:
                getActivity().onBackPressed();
                break;
            case R.id.clear_sticker_button: {
                ((CameraActivity)getActivity()).clearStickers();
                break;
            }
        }
    }

    @Override
    public void onCategorySelected(CategoryModel category) {
        mStickerListAdapter.setData(category.items);
    }

    @Override
    public void onStickerSelected(int position, ItemModel item) {
        ((CameraActivity)getActivity()).setSticker(item);
    }

}
