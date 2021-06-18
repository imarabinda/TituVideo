package com.ionexplus.titu.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.ionexplus.titu.R;
import com.ionexplus.titu.adapter.ProfileCategoryAdapter;
import com.ionexplus.titu.databinding.ActivityEditProfileBinding;
import com.ionexplus.titu.model.user.ProfileCategory;
import com.ionexplus.titu.utils.Const;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.media.BottomSheetImagePicker;
import com.ionexplus.titu.viewmodel.EditProfileViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

import static com.ionexplus.titu.utils.BindingAdapters.loadMediaImage;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private ProfileCategoriesFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        startReceiver();
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new EditProfileViewModel()).createFor()).get(EditProfileViewModel.class);
        initView();
        initObserve();
        initListener();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        viewModel.fetchProfileCategories();
        viewModel.user = sessionManager.getUser();
        viewModel.updateData();
        if (viewModel.user != null) {
            viewModel.curUserName = sessionManager.getUser().getData().getUserName();
            if (viewModel.user.getData().getBio() != null && !viewModel.user.getData().getBio().isEmpty()) {
                viewModel.length.set(viewModel.user.getData().getBio().length());
            }
        }
    }

    private void initObserve() {
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(EditProfileActivity.this);
        viewModel.toast.observe(this, str->{
            if (str != null && !str.isEmpty()) {
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            }
        });

        this.viewModel.isloading.observe(this, new Observer<Boolean>() {
            @Override
            public final void onChanged(Boolean bool) {
                if (bool.booleanValue()) {
                    customDialogBuilder.showLoadingDialog();
                } else {
                    customDialogBuilder.hideLoadingDialog();
                }
            }
        });


        viewModel.updateProfile.observe(this, isUpdate -> {
            if (isUpdate != null && isUpdate) {
                Intent intent = new Intent();
                intent.putExtra("user", new Gson().toJson(viewModel.user));
                sessionManager.saveUser(viewModel.user);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
    }


    private void initListener() {
        binding.setOnChangeClick(view -> showPhotoSelectSheet());

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.imgEditCategory.setOnClickListener(view->{
            fragment = new ProfileCategoriesFragment();
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        });

        this.viewModel.adapter.setOnRecyclerViewItemClick(
                new ProfileCategoryAdapter.OnRecyclerViewItemClick() {
                    @Override
                    public final void onCategoryClick(ProfileCategory.Data data) {
                        viewModel.profile_cat_id = data.getProfileCategoryId();
                        viewModel.profile_cat_name = data.getProfileCategoryName();
                        binding.tvProfileCategory.setText(data.getProfileCategoryName());
                        if (fragment != null) {
                            fragment.dismiss();
                        }

                    }
                });


    }


    private void showPhotoSelectSheet() {
        BottomSheetImagePicker bottomSheetImagePicker = new BottomSheetImagePicker();
        bottomSheetImagePicker.setOnDismiss(uri -> {
            if (!uri.isEmpty()) {
                loadMediaImage(binding.profileImg, uri, true);
                viewModel.imageUri = uri;
            }
        });
        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }


}