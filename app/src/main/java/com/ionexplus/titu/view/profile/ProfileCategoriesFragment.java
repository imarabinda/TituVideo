package com.ionexplus.titu.view.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.util.Log;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.FragmentProfileCategoriesBinding;
import com.ionexplus.titu.viewmodel.EditProfileViewModel;

public class ProfileCategoriesFragment extends BottomSheetDialogFragment {
    FragmentProfileCategoriesBinding binding;
    EditProfileViewModel viewModel;

    private void initView() {
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(bundle);
        bottomSheetDialog.setOnShowListener((show)->{
            bottomSheetDialog.setCanceledOnTouchOutside(false);

        });
        return bottomSheetDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding =  DataBindingUtil.inflate(layoutInflater, R.layout.fragment_profile_categories, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getActivity() != null) {
            viewModel = (EditProfileViewModel) ViewModelProviders.of(getActivity()).get(EditProfileViewModel.class);
        }
        initView();
        initListeners();
        binding.setViewmodel(viewModel);
    }

    private void initListeners() {
        binding.imgClose.setOnClickListener(view->{
            dismiss();
        });
    }

}