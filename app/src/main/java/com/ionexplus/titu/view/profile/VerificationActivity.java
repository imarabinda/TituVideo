package com.ionexplus.titu.view.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivityVerificationBinding;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.RemoteConfig;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.media.BottomSheetImagePicker;
import com.ionexplus.titu.viewmodel.VerificationViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.ionexplus.titu.utils.BindingAdapters.loadMediaImage;
import static com.ionexplus.titu.utils.BindingAdapters.loadMediaRoundBitmap;

public class VerificationActivity extends BaseActivity {

    ActivityVerificationBinding binding;
    VerificationViewModel viewModel;
    private static int CAPTURE_IMAGE = 100;
    CustomDialogBuilder customDialogBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new VerificationViewModel()).createFor()).get(VerificationViewModel.class);
        customDialogBuilder = new CustomDialogBuilder(this);
        initObserve();
        initListeners();
        binding.setViewModel(viewModel);
    }

    private void initObserve() {
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                customDialogBuilder.showLoadingDialog();
            } else {
                customDialogBuilder.hideLoadingDialog();
                onBackPressed();
                Toast.makeText(this, "Verification request successful.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListeners() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.setOnCaptureClick(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE);
            }
        });
        binding.setOnAttachClick(v -> showPhotoSelectSheet());

    }

    private void showPhotoSelectSheet() {
        BottomSheetImagePicker bottomSheetImagePicker = new BottomSheetImagePicker();
        bottomSheetImagePicker.setOnDismiss(uri -> {
            if (!uri.isEmpty()) {
                loadMediaImage(binding.ivProof, uri, false);
                viewModel.proofUri = uri;
            }
        });
        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE && data != null) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            File thumbFile = new File(getPath(), "verification.jpg");

            try {
                FileOutputStream stream = new FileOutputStream(thumbFile);
                if (photo != null) {
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                }
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            loadMediaRoundBitmap(binding.roundImg, photo);
        }
    }

    public File getPath() {
        String state = Environment.getExternalStorageState();
        File filesDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            filesDir = getExternalFilesDir(null);
        } else {
            // Load another directory, probably local memory
            filesDir = getFilesDir();
        }

        return filesDir;
    }
}