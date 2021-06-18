package com.ionexplus.titu.view.recordvideo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.coremedia.iso.boxes.Container;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.ionexplus.titu.api.ContentsResponse;
import com.ionexplus.titu.camera.ReferenceCamera;
import com.ionexplus.titu.camera.ReferenceCamera1;
import com.ionexplus.titu.camera.ReferenceCamera2;
import com.ionexplus.titu.common.PermissionHelper;
import com.ionexplus.titu.data.BeautyItemData;
import com.ionexplus.titu.model.ItemModel;
import com.ionexplus.titu.network.DownloadAsyncResponse;
import com.ionexplus.titu.network.DownloadAsyncTask;
import com.ionexplus.titu.rendering.CameraTexture;
import com.ionexplus.titu.rendering.ScreenRenderer;
import com.ionexplus.titu.utils.AppConfig;
import com.ionexplus.titu.utils.FileDeleteAsyncTask;
import com.ionexplus.titu.utils.Global;
import com.ionexplus.titu.utils.GlobalApi;
import com.ionexplus.titu.utils.MediaStoreUtil;
import com.ionexplus.titu.utils.PreferenceUtil;
import com.ionexplus.titu.view.argear.BeautyFragment;
import com.ionexplus.titu.view.argear.FilterFragment;
import com.ionexplus.titu.view.argear.GLView;
import com.ionexplus.titu.view.argear.StickerFragment;
import com.ionexplus.titu.viewmodel.ContentsViewModel;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.TranscoderOptions;
import com.otaliastudios.transcoder.engine.TrackType;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy;
import com.otaliastudios.transcoder.strategy.size.AtMostResizer;
import com.otaliastudios.transcoder.strategy.size.FractionResizer;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.ActivityCameraBinding;
import com.ionexplus.titu.databinding.DailogProgressBinding;

import com.ionexplus.titu.utils.Const;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.RemoteConfig;
import com.ionexplus.titu.view.base.BaseActivity;
import com.ionexplus.titu.view.media.BottomSheetImagePicker;
import com.ionexplus.titu.view.music.MusicFrameFragment;
import com.ionexplus.titu.view.preview.PreviewActivity;
import com.ionexplus.titu.viewmodel.CameraViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;
import com.seerslab.argear.exceptions.InvalidContentsException;
import com.seerslab.argear.exceptions.NetworkException;
import com.seerslab.argear.exceptions.SignedUrlGenerationException;
import com.seerslab.argear.session.ARGAuth;
import com.seerslab.argear.session.ARGContents;
import com.seerslab.argear.session.ARGFrame;
import com.seerslab.argear.session.ARGMedia;
import com.seerslab.argear.session.ARGSession;
import com.seerslab.argear.session.config.ARGCameraConfig;
import com.seerslab.argear.session.config.ARGConfig;
import com.seerslab.argear.session.config.ARGInferenceConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.reactivex.disposables.CompositeDisposable;

public class CameraActivity extends BaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST = 101;
    private CameraViewModel viewModel;
    private ActivityCameraBinding binding;
    private CustomDialogBuilder customDialogBuilder;
    private Dialog mBuilder;
    private DailogProgressBinding progressBinding;
    private RemoteConfig remoteConfig;
    private CompositeDisposable disposable = new CompositeDisposable();

    private ReferenceCamera mCamera;
    private GLView mGlView;
    private ScreenRenderer mScreenRenderer;
    private CameraTexture mCameraTexture;

    private ARGFrame.Ratio mScreenRatio = ARGFrame.Ratio.RATIO_FULL;

    private String mItemDownloadPath;
    private String mMediaPath;
    private String mInnerMediaPath;
    private boolean mIsShooting = false;

    private boolean mFilterVignette = false;
    private boolean mFilterBlur = false;
    private int mFilterLevel = 100;
    private ItemModel mCurrentStickeritem = null;
    private boolean mHasTrigger = false;
    private boolean mUseARGSessionDestroy = false;

    private int mDeviceWidth = 0;
    private int mDeviceHeight = 0;
    private int mGLViewWidth = 0;
    private int mGLViewHeight = 0;

    private FragmentManager mFragmentManager;

    private StickerFragment mStickerFragment;
    private BeautyFragment mBeautyFragment;
    private FilterFragment mFilterFragment;

    private ContentsViewModel mContentsViewModel;
    private BeautyItemData mBeautyItemData;

    private ARGSession mARGSession;
    private ARGMedia mARGMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new CameraViewModel()).createFor()).get(CameraViewModel.class);
        customDialogBuilder = new CustomDialogBuilder(CameraActivity.this);
        remoteConfig = new RemoteConfig();
        mBeautyItemData = new BeautyItemData();
        mContentsViewModel = (ContentsViewModel) ViewModelProviders.of(this, new ViewModelFactory(new ContentsViewModel()).createFor()).get(ContentsViewModel.class);
        mContentsViewModel.getContents().observe(this, new Observer<ContentsResponse>() {
            @Override
            public void onChanged(ContentsResponse contentsResponse) {
                if (contentsResponse == null) return;
                setLastUpdateAt(CameraActivity.this, contentsResponse.lastUpdatedAt);
            }
        });

        initView();
        initListener();
        initObserve();
        initProgressDialog();
        initRatioUI();
//        clearTempMediaFiles();
        binding.setViewModel(viewModel);
    }

    private void clearTempMediaFiles() {
        new FileDeleteAsyncTask(new File(mInnerMediaPath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
            @Override
            public void processFinish(Object result) {
                File dir = new File(mInnerMediaPath);
                if (!dir.exists()) {
                    boolean r = dir.mkdir();
                    Log.e(TAG, "");
                }
            }
        }).execute();
    }

    private void initView() {
        Point realSize = new Point();
        Display display= ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getRealSize(realSize);
        mDeviceWidth = realSize.x;
        mDeviceHeight = realSize.y;
        mGLViewWidth = realSize.x;
        mGLViewHeight = realSize.y;

        mFragmentManager = getSupportFragmentManager();
        mFilterFragment = new FilterFragment();
        mStickerFragment = new StickerFragment();
        mBeautyFragment = new BeautyFragment();
//        mBulgeFragment = new BulgeFragment();


        mItemDownloadPath = getFilesDir().getAbsolutePath();
        mMediaPath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/ARGEAR";
        File dir = new File(mMediaPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mInnerMediaPath =  Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/ARGearMedia";

        String musicUrl = getIntent().getStringExtra("music_url");

        if (musicUrl != null && !musicUrl.isEmpty()) {
            downLoadMusic(getIntent().getStringExtra("music_url"));
            if (getIntent().getStringExtra("music_title") != null) {
                binding.tvSoundTitle.setText(getIntent().getStringExtra("music_title"));
            }
            if (getIntent().getStringExtra("sound_id") != null) {
                viewModel.soundId = getIntent().getStringExtra("sound_id");
            }
        }

        if (viewModel.onDurationUpdate.getValue() != null) {
            binding.progressBar.enableAutoProgressView(viewModel.onDurationUpdate.getValue());
        }

        viewModel.shortTime = (int) remoteConfig.getLong("minimum_camera_video_length");
        viewModel.longTime = (int) remoteConfig.getLong("maximum_camera_video_length");
        binding.progressBar.setDividerColor(Color.WHITE);
        binding.progressBar.setDividerEnabled(true);
        binding.progressBar.setDividerWidth(4);
        binding.progressBar.setListener(mills -> {
            viewModel.isEnabled.set(mills >= (remoteConfig.getLong("minimum_camera_video_length")*1000)-500);
            if (mills == viewModel.onDurationUpdate.getValue()) {
                stopRecording();
            }
        });

        binding.progressBar.setShader(new int[]{ContextCompat.getColor(this, R.color.colorTheme2), ContextCompat.getColor(this, R.color.colorTheme1), ContextCompat.getColor(this, R.color.colorTheme)});

    }




    private void initListener() {

        binding.ivSelect.setOnClickListener(v ->{
            if (viewModel.isRecording.get() || (binding.progressBar.getTimePassed() < (remoteConfig.getLong("minimum_camera_video_length")*1000)-500 && binding.progressBar.getTimePassed() < viewModel.onDurationUpdate.getValue().longValue() - 500)) {
                Toast.makeText(this, "Make sure video is longer than "+getTime((int) remoteConfig.getLong("minimum_camera_video_length"))+"...!", Toast.LENGTH_SHORT).show();
            } else if (viewModel.isRecording.get() || (binding.progressBar.getTimePassed() < (remoteConfig.getLong("maximum_camera_video_length")*1000)-500 && binding.progressBar.getTimePassed() < viewModel.onDurationUpdate.getValue().longValue() - 500)) {
                Toast.makeText(this, "Make sure video is shorter than "+getTime((int) remoteConfig.getLong("maximum_camera_video_length"))+"...!", Toast.LENGTH_SHORT).show();
            } else {
                saveData();
            }
        });

        binding.btnCapture.setOnClickListener(v -> {
            if (!viewModel.isRecording.get()) {
                startReCording();
            } else {
                stopRecording();
            }
        });

        binding.btnFlip.setOnClickListener(v -> {
            viewModel.isFacingFront.set(!viewModel.isFacingFront.get());
            mARGSession.pause();
            mCamera.changeCameraFacing();
            mARGSession.resume();
        });

        binding.ivSticker.setOnClickListener(v->{
            showStickers();
        });
        binding.ivFlash.setOnClickListener(v->{
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if(viewModel.isFacingFront.get()){
                if(!viewModel.isFlashOn.get()){
                    try {
                        if (hasFlashForFrontCamera(cameraManager)) {
                            cameraManager.setTorchMode(getFrontFacingCameraId(cameraManager), true);
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        if (hasFlashForFrontCamera(cameraManager)) {
                            cameraManager.setTorchMode(getFrontFacingCameraId(cameraManager), false);
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                if(!viewModel.isFlashOn.get()){
                    try {
                        cameraManager.setTorchMode(getBackFacingCameraId(cameraManager), true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        cameraManager.setTorchMode(getBackFacingCameraId(cameraManager), false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            viewModel.isFlashOn.set(!viewModel.isFlashOn.get());

        });


        binding.ivBeauty.setOnClickListener(v->{
            showBeauty();
        });

        binding.ivFilter.setOnClickListener(v->{
            showFilters();
        });
        binding.tvSelect.setOnClickListener(v -> {
            BottomSheetImagePicker bottomSheetImagePicker = BottomSheetImagePicker.Companion.getNewInstance(1);
            bottomSheetImagePicker.setOnDismiss(uri -> {
                if (!uri.isEmpty()) {
                    File file = new File(uri);
                    // Get length of file in bytes
                    long fileSizeInBytes = file.length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(this, Uri.fromFile(new File(uri)));
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMilliSec = Long.parseLong(time);
                    if (timeInMilliSec > remoteConfig.getLong("maximum_gallery_video_length")*1000) {
                        customDialogBuilder.showSimpleDialog("Too long video", "This video is longer than "+getTime((int) remoteConfig.getLong("maximum_gallery_video_length"))+".\nPlease select onOther..",
                                "Cancel", "Select another", new CustomDialogBuilder.OnDismissListener() {
                                    @Override
                                    public void onPositiveDismiss() {
                                        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
                                    }

                                    @Override
                                    public void onNegativeDismiss() {
                                        Log.i("", "");
                                    }
                                });
                    }else if (timeInMilliSec  < remoteConfig.getLong("minimum_gallery_video_length")*1000) {
                        customDialogBuilder.showSimpleDialog("Too short video", "This video is shorter than "+ getTime((int) remoteConfig.getLong("minimum_gallery_video_length")) +".\nPlease select onOther..",
                                "Cancel", "Select another", new CustomDialogBuilder.OnDismissListener() {
                                    @Override
                                    public void onPositiveDismiss() {
                                        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
                                    }

                                    @Override
                                    public void onNegativeDismiss() {
                                        Log.i("", "");
                                    }
                                });
                    } else if(fileSizeInMB < remoteConfig.getLong("minimum_gallery_video_size")){
                        customDialogBuilder.showSimpleDialog("Too short video's size", "This video's size is smaller than "+ remoteConfig.getLong("minimum_gallery_video_size") +"Mb.\nPlease select onOther..",
                                "Cancel", "Select onOther", new CustomDialogBuilder.OnDismissListener() {
                                    @Override
                                    public void onPositiveDismiss() {
                                        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
                                    }
                                    @Override
                                    public void onNegativeDismiss() {
                                        Log.i("", "");
                                    }
                                });
                    }else if (fileSizeInMB < remoteConfig.getLong("maximum_gallery_video_size")) {

                        viewModel.videoPaths = new ArrayList<>();
                        viewModel.videoPaths.add(uri);
                        customDialogBuilder.showLoadingDialog();
                        fileSelectUpload();

                    } else if(fileSizeInMB > remoteConfig.getLong("maximum_gallery_video_size")){
                        customDialogBuilder.showSimpleDialog("Too long video's size", "This video's size is grater than "+ remoteConfig.getLong("maximum_gallery_video_size") +"Mb.\nPlease select onOther..",
                                "Cancel", "Select onOther", new CustomDialogBuilder.OnDismissListener() {
                                    @Override
                                    public void onPositiveDismiss() {
                                        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
                                    }
                                    @Override
                                    public void onNegativeDismiss() {
                                        Log.i("", "");
                                    }
                                });
                    }
                    retriever.release();
                }
            });
            bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
        });

        binding.ivClose.setOnClickListener(v -> customDialogBuilder.showSimpleDialog("Are you sure", "Do you really wan to go back ?",
                "No", "Yes", new CustomDialogBuilder.OnDismissListener() {
                    @Override
                    public void onPositiveDismiss() {
                        viewModel.isBack = true;
                        onBackPressed();
                    }

                    @Override
                    public void onNegativeDismiss() {

                    }
                }));
    }


    public boolean hasFlashForFrontCamera(CameraManager cameraManager) throws CameraAccessException {
        for (String str : cameraManager.getCameraIdList()) {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(str);
            if (((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0) {
                return ((Boolean) cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).booleanValue();
            }
        }
        return false;
    }

    public String getBackFacingCameraId(CameraManager cameraManager) throws CameraAccessException {
        String[] cameraIdList = cameraManager.getCameraIdList();
        for (String str : cameraIdList) {
            if (((Integer) cameraManager.getCameraCharacteristics(str).get(CameraCharacteristics.LENS_FACING)).intValue() == 1) {
                return str;
            }
        }
        return null;
    }

    public String getFrontFacingCameraId(CameraManager cameraManager) throws CameraAccessException {
        String[] cameraIdList = cameraManager.getCameraIdList();
        for (String str : cameraIdList) {
            if (((Integer) cameraManager.getCameraCharacteristics(str).get(CameraCharacteristics.LENS_FACING)).intValue() == 0) {
                return str;
            }
        }
        return null;
    }




    private void initObserve() {
        viewModel.parentPath = getPath().getPath();
        viewModel.onItemClick.observe(this, type -> {
            if (type != null) {
                if (type == 1) {
                    MusicFrameFragment frameFragment = new MusicFrameFragment();
                    frameFragment.show(getSupportFragmentManager(), frameFragment.getClass().getSimpleName());
                }
                viewModel.onItemClick.setValue(null);
            }
        });
        viewModel.onSoundSelect.observe(this, sound -> {
            if (sound != null) {
                viewModel.soundId = sound.getSoundId();
                binding.tvSoundTitle.setText(sound.getSoundTitle());
                downLoadMusic(sound.getSound());
            }
        });
        viewModel.onDurationUpdate.observe(this, duration -> binding.progressBar.enableAutoProgressView(duration));
    }


    private void initCamera() {
        if (remoteConfig.getLong("camera_api_level") == 1) {
            mCamera = new ReferenceCamera1(this, cameraListener);
        } else if (remoteConfig.getLong("camera_api_level") == 2) {
            mCamera = new ReferenceCamera2(this, cameraListener, getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void initRatioUI() {
        if (mBeautyFragment != null && mBeautyFragment.isAdded()) {
            mBeautyFragment.updateUIStyle(mScreenRatio);
        }
    }

    private void setGLViewSize(int [] cameraPreviewSize) {
        int previewWidth = cameraPreviewSize[1];
        int previewHeight = cameraPreviewSize[0];

        if (mScreenRatio == ARGFrame.Ratio.RATIO_FULL) {
            mGLViewHeight = mDeviceHeight;
            mGLViewWidth = (int) ((float) mDeviceHeight * previewWidth / previewHeight );
        } else {
            mGLViewWidth = mDeviceWidth;
            mGLViewHeight = (int) ((float) mDeviceWidth * previewHeight / previewWidth);
        }

        if (mGlView != null
                && (mGLViewWidth != mGlView.getViewWidth() || mGLViewHeight != mGlView.getHeight())) {
            binding.cameraLayout.removeView(mGlView);
            mGlView.getHolder().setFixedSize(mGLViewWidth, mGLViewHeight);
            binding.cameraLayout.addView(mGlView);
        }
    }

    private void initGLView() {
        final FrameLayout cameraLayout = findViewById(R.id.camera_layout);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mGlView = new GLView(this, glViewListener);
        mGlView.setZOrderMediaOverlay(true);

        cameraLayout.addView(mGlView, params);
    }




    public void initProgressDialog() {
        mBuilder = new Dialog(this);
        mBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBuilder.setCancelable(false);
        mBuilder.setCanceledOnTouchOutside(false);
        if (mBuilder.getWindow() != null) {
            mBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mBuilder.setCancelable(false);
        mBuilder.setCanceledOnTouchOutside(false);
        progressBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dailog_progress, null, false);
        progressBinding.progressBar.setShader(new int[]{ContextCompat.getColor(this, R.color.colorTheme2), ContextCompat.getColor(this, R.color.colorTheme1), ContextCompat.getColor(this, R.color.colorTheme)});

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        Animation reverseAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse);
        progressBinding.ivParent.startAnimation(rotateAnimation);
        progressBinding.ivChild.startAnimation(reverseAnimation);
        mBuilder.setContentView(progressBinding.getRoot());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showProgressDialog() {
        if (!this.mBuilder.isShowing()) {
            this.mBuilder.show();
        }
    }

    public void hideProgressDialog() {
        try {
            this.mBuilder.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTime(int seconds){
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
        if(day!=0){
            return day+" days "+hours+" hours "+minute+ " minutes "+second +" seconds";
        }
        if(hours!=0){
            return hours+" hours "+minute+ " minutes "+second +" seconds";
        }
        if(minute!=0){
            return minute+ " minutes "+second +" seconds";
        }
        if(second!=0){
            return second +" seconds";
        }

        return seconds+" seconds";
    }

    private void downLoadMusic(String endPoint) {

        PRDownloader.download(Const.ITEM_BASE_URL + endPoint, getPath().getPath(), "recordSound.aac")
                .build()
                .setOnStartOrResumeListener(() -> customDialogBuilder.showLoadingDialog())
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        customDialogBuilder.hideLoadingDialog();
                        viewModel.isStartRecording.set(true);
                        viewModel.createAudioForCamera();
                    }

                    @Override
                    public void onError(Error error) {
                        customDialogBuilder.hideLoadingDialog();
                    }
                });
    }

    private void startReCording() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        binding.btnCapture.startAnimation(animation);
        if (binding.progressBar.getProgressPercent() != 100) {
            if (mCamera == null) {
                return;
            }
            if (viewModel.audio != null) {
                viewModel.audio.start();
            }
            File file = new File(getPath(), "video".concat(String.valueOf(viewModel.count)).concat(".mp4"));
            viewModel.videoPaths.add(getPath().getPath().concat("/video").concat(String.valueOf(viewModel.count)).concat(".mp4"));

            int bitrate = 10 * 1000 * 1000; // 10M

            ARGMedia.Ratio ratio;
            if (mScreenRatio == ARGFrame.Ratio.RATIO_FULL) {
                ratio = ARGMedia.Ratio.RATIO_16_9;
            } else if (mScreenRatio == ARGFrame.Ratio.RATIO_4_3) {
                ratio = ARGMedia.Ratio.RATIO_4_3;
            } else {
                ratio = ARGMedia.Ratio.RATIO_1_1;
            }

            int [] previewSize = mCamera.getPreviewSize();

            mARGMedia.initRecorder(
                    file.getAbsolutePath(),
                    previewSize[0],
                    previewSize[1],
                    bitrate,
                    false,
                    false,
                    false,
                    ratio);
            mARGMedia.startRecording();
            binding.progressBar.resume();
            viewModel.isRecording.set(true);
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
        if (filesDir != null) {
            viewModel.parentPath = filesDir.getPath();
        }
        return filesDir;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mARGSession == null) {
            if (!PermissionHelper.hasPermission(this)) {
                if (PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                    binding.getRoot().setVisibility(View.GONE);
                    Toast.makeText(this, "Please check your permissions!", Toast.LENGTH_SHORT).show();
                    return;
                }
                PermissionHelper.requestPermission(this);
                return;
            }

            ARGConfig config
                    = new ARGConfig(AppConfig.API_URL, AppConfig.API_KEY, AppConfig.SECRET_KEY, AppConfig.AUTH_KEY);
            Set<ARGInferenceConfig.Feature> inferenceConfig
                    = EnumSet.of(ARGInferenceConfig.Feature.FACE_MESH_TRACKING);

            mARGSession = new ARGSession(this, config, inferenceConfig);
            mARGMedia = new ARGMedia(mARGSession);

            mScreenRenderer = new ScreenRenderer();
            mCameraTexture = new CameraTexture();

            setBeauty(mBeautyItemData.getBeautyValues());

            initGLView();
            initCamera();
        }

        mCamera.startCamera();
        mARGSession.resume();

        setGLViewSize(mCamera.getPreviewSize());
    }

    @Override
    protected void onPause() {
        if (viewModel.isRecording.get()) {
            stopRecording();
        }
        if (mARGSession != null) {
            mCamera.stopCamera();
            mARGSession.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (viewModel.isRecording.get()) {
            stopRecording();
        }

//        if (mARGSession != null) {
//            mCamera.destroy();
//            mARGSession.destroy();
//        }
        
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        if (viewModel.isRecording.get()) {
            stopRecording();
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.isBack = true;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        } else if (viewModel.isBack) {
            setResult(RESULT_OK);
            super.onBackPressed();
        }
    }





    public void saveData() {

        final DefaultVideoStrategy build = new DefaultVideoStrategy.Builder().addResizer(new FractionResizer(0.7f)).addResizer(new AtMostResizer(1000)).build();
        TranscoderOptions.Builder into = Transcoder.into(getPath().getPath().concat("/append.mp4"));
        for (int i = 0; i < viewModel.videoPaths.size(); i++) {
            into.addDataSource(viewModel.videoPaths.get(i));
        }
        if (viewModel.audio == null) {
            into.setVideoTrackStrategy(build);
        }
        into.setListener(new TranscoderListener() {

            @Override
            public void onTranscodeProgress(double d) {
                showProgressDialog();
                if (progressBinding != null) {
                    if (viewModel.audio != null) {
                        progressBinding.progressBar.publishProgress(((float) d) / 2.0f);
                    } else {
                        progressBinding.progressBar.publishProgress((float) d);
                    }
                }
            }

            @Override
            public void onTranscodeCompleted(int i) {
                if (viewModel.audio != null) {
                    Transcoder.into(getPath().getPath().concat("/finally.mp4")).addDataSource(TrackType.VIDEO, getPath().getPath().concat("/append.mp4")).addDataSource(TrackType.AUDIO, getPath().getPath().concat("/recordSound.aac")).setVideoTrackStrategy(build).setListener(new TranscoderListener() {

                        @Override
                        public void onTranscodeCanceled() {
                        }

                        @Override
                        public void onTranscodeFailed(Throwable cause) {
                            customDialogBuilder.showSimpleDialog("Hardware Issue", cause.toString(),
                                    "Okay", "No Problem", new CustomDialogBuilder.OnDismissListener() {
                                        @Override
                                        public void onPositiveDismiss() {
                                            viewModel.isBack = true;
                                            onBackPressed();
                                        }

                                        @Override
                                        public void onNegativeDismiss() {
                                            viewModel.isBack = true;
                                            onBackPressed();
                                        }
                                    });
                        }

                        @Override
                        public void onTranscodeProgress(double d) {
                            if (progressBinding != null && viewModel.audio != null) {
                                progressBinding.progressBar.publishProgress(((float) (d + 1.0d)) / 2.0f);
                            }
                        }

                        @Override
                        public void onTranscodeCompleted(int i) {
                            File file = new File(getPath(), "temp.jpg");
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(getPath().getPath().concat("/append.mp4"), 2);
                                if (createVideoThumbnail != null) {
                                    createVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                }
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            hideProgressDialog();
                            Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                            intent.putExtra("post_video", getPath().getPath().concat("/finally.mp4"));
                            intent.putExtra("post_image", file.getPath());
                            if (viewModel.soundId != null && !viewModel.soundId.isEmpty()) {
                                intent.putExtra("soundId", viewModel.soundId);
                            }
                            startActivityForResult(intent, 101);
                        }
                    }).transcode();
                    return;
                }
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            Movie movie = new Movie();
                            movie.addTrack(MovieCreator.build(getPath().getPath().concat("/append.mp4")).getTracks().get(1));
                            Container build = new DefaultMp4Builder().build(movie);
                            FileOutputStream fileOutputStream = new FileOutputStream(getPath().getPath().concat("/originalSound.aac"));
                            build.writeContainer(fileOutputStream.getChannel());
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final File file = new File(getPath(), "temp.jpg");
                        try {
                            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                            Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(getPath().getPath().concat("/append.mp4"), 2);
                            if (createVideoThumbnail != null) {
                                createVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
                            }
                            fileOutputStream2.flush();
                            fileOutputStream2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        RequestBuilder<Bitmap> asBitmap = Glide.with((FragmentActivity) CameraActivity.this).asBitmap();
                        asBitmap.load(Const.ITEM_BASE_URL + sessionManager.getUser().getData().getUserProfile()).into(new CustomTarget<Bitmap>() {

                            @Override
                            public void onLoadCleared(Drawable drawable) {
                            }

                            @Override
                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(new File(getPath(), "soundImage.jpeg"));
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                hideProgressDialog();
                                Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                                intent.putExtra("post_video", getPath().getPath().concat("/append.mp4"));
                                intent.putExtra("post_image", file.getPath());
                                intent.putExtra("post_sound", getPath().getPath().concat("/originalSound.aac"));
                                intent.putExtra("sound_image", getPath().getPath().concat("/soundImage.jpeg"));
                                startActivityForResult(intent, 101);

                            }

                            @Override
                            public void onLoadFailed(Drawable drawable) {
                                super.onLoadFailed(drawable);
                                hideProgressDialog();
                                Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                                intent.putExtra("post_video", getPath().getPath().concat("/append.mp4"));
                                intent.putExtra("post_image", file.getPath());
                                intent.putExtra("post_sound", getPath().getPath().concat("/originalSound.aac"));
                                startActivityForResult(intent, 101);

                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onTranscodeCanceled() {
                Log.d("TAG", "onTranscodeCanceled: ");
            }

            @Override
            public void onTranscodeFailed(Throwable cause) {
                customDialogBuilder.showSimpleDialog("Hardware Issue", cause.toString(),
                        "Okay", "No Problem", new CustomDialogBuilder.OnDismissListener() {
                            @Override
                            public void onPositiveDismiss() {
                                viewModel.isBack = true;
                                onBackPressed();
                            }

                            @Override
                            public void onNegativeDismiss() {
                                viewModel.isBack = true;
                                onBackPressed();
                            }
                        });
            }
        }).transcode();
        return;
    }

    private void fileSelectUpload(){
        //on file picker
        runOnUiThread(new Runnable() {

            public final void run() {
                try {
                    Movie movie = new Movie();
                    movie.addTrack(MovieCreator.build(viewModel.videoPaths.get(0)).getTracks().get(1));
                    Container build = new DefaultMp4Builder().build(movie);
                    FileOutputStream fileOutputStream = new FileOutputStream(getPath().getPath().concat("/originalSound.aac"));
                    build.writeContainer(fileOutputStream.getChannel());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final File file = new File(getPath(), "temp.jpg");
                try {
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                    Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(viewModel.videoPaths.get(0), 2);
                    if (createVideoThumbnail != null) {
                        createVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
                    }
                    fileOutputStream2.flush();
                    fileOutputStream2.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                RequestBuilder<Bitmap> asBitmap = Glide.with((FragmentActivity) CameraActivity.this).asBitmap();
                asBitmap.load(Const.ITEM_BASE_URL + sessionManager.getUser().getData().getUserProfile()).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onLoadCleared(Drawable drawable) {
                    }

                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(getPath(), "soundImage.jpeg"));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        customDialogBuilder.hideLoadingDialog();
                        Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                        intent.putExtra("post_video", viewModel.videoPaths.get(0));
                        intent.putExtra("post_image", file.getPath());
                        intent.putExtra("post_sound", getPath().getPath().concat("/originalSound.aac"));
                        intent.putExtra("sound_image", getPath().getPath().concat("/soundImage.jpeg"));
                        startActivityForResult(intent, 101);
                    }

                    @Override
                    public void onLoadFailed(Drawable drawable) {
                        super.onLoadFailed(drawable);
                        customDialogBuilder.hideLoadingDialog();
                        Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                        intent.putExtra("post_video", viewModel.videoPaths.get(0));
                        intent.putExtra("post_image", file.getPath());
                        intent.putExtra("post_sound", getPath().getPath().concat("/originalSound.aac"));
                        startActivityForResult(intent, 101);
                    }
                });

            }
        });
    }



    private void setLastUpdateAt(Context context, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME, "ContentLastUpdateAt", updateAt);
    }

    private long getLastUpdateAt(Context context) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME, "ContentLastUpdateAt");
    }

    private void setFilterUpdateAt(Context context, String itemId, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME_FILTER, itemId, updateAt);
    }

    private long getFilterUpdateAt(Context context, String itemId) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME_FILTER, itemId);
    }

    private void setStickerUpdateAt(Context context, String itemId, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME_STICKER, itemId, updateAt);
    }

    private long getStickerUpdateAt(Context context, String itemId) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME_STICKER, itemId);
    }

    public void setMeasureSurfaceView(View view) {
        if (view.getParent() instanceof FrameLayout) {
            view.setLayoutParams(new FrameLayout.LayoutParams(mGLViewWidth, mGLViewHeight));
        }else if(view.getParent() instanceof RelativeLayout) {
            view.setLayoutParams(new RelativeLayout.LayoutParams(mGLViewWidth, mGLViewHeight));
        }

        /* to align center */
        if ((mScreenRatio == ARGFrame.Ratio.RATIO_FULL) && (mGLViewWidth > mDeviceWidth)) {
            view.setX((mDeviceWidth - mGLViewWidth) / 2);
        } else {
            view.setX(0);
        }
    }


    public void setSticker(ItemModel item) {
        String filePath = mItemDownloadPath + "/" + item.uuid;
        if (getLastUpdateAt(CameraActivity.this) > getStickerUpdateAt(CameraActivity.this, item.uuid)) {
            new FileDeleteAsyncTask(new File(filePath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
                @Override
                public void processFinish(Object result) {
                    Log.d(TAG, "file delete success!");

                    setStickerUpdateAt(CameraActivity.this, item.uuid, getLastUpdateAt(CameraActivity.this));
                    requestSignedUrl(item, filePath, true);
                }
            }).execute();
        } else {
            if (new File(filePath).exists()) {
                setItem(ARGContents.Type.ARGItem, filePath, item);
            } else {
                requestSignedUrl(item, filePath, true);
            }
        }
    }

    public int getGLViewWidth() {
        return mGLViewWidth;
    }

    public int getGLViewHeight() {
        return mGLViewHeight;
    }

    public BeautyItemData getBeautyItemData() {
        return mBeautyItemData;
    }


    // region - network
    private void requestSignedUrl(ItemModel item, String path, final boolean isArItem) {
        binding.progressBar.setVisibility(View.VISIBLE);
        mARGSession.auth().requestSignedUrl(item.zipFileUrl, item.title, item.type, new ARGAuth.Callback() {
            @Override
            public void onSuccess(String url) {
                requestDownload(path, url, item, isArItem);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SignedUrlGenerationException) {
                    Log.e(TAG, "SignedUrlGenerationException !! ");
                } else if (e instanceof NetworkException) {
                    Log.e(TAG, "NetworkException !!");
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }



    public void setItem(ARGContents.Type type, String path, ItemModel itemModel) {

        mCurrentStickeritem = null;
        mHasTrigger = false;

        mARGSession.contents().setItem(type, path, itemModel.uuid, new ARGContents.Callback() {
            @Override
            public void onSuccess() {
                if (type == ARGContents.Type.ARGItem) {
                    mCurrentStickeritem = itemModel;
                    mHasTrigger = itemModel.hasTrigger;
                }
            }

            @Override
            public void onError(Throwable e) {
                mCurrentStickeritem = null;
                mHasTrigger = false;
                if (e instanceof InvalidContentsException) {
                    Log.e(TAG, "InvalidContentsException");
                }
            }
        });
    }
    private void requestDownload(String targetPath, String url, ItemModel item, boolean isSticker) {
        new DownloadAsyncTask(targetPath, url, new DownloadAsyncResponse() {
            @Override
            public void processFinish(boolean result) {
                binding.progressBar.setVisibility(View.INVISIBLE);
                if (result) {
                    if (isSticker) {
                        setItem(ARGContents.Type.ARGItem, targetPath, item);
                    } else {
                        setItem(ARGContents.Type.FilterItem, targetPath, item);
                    }
                    Log.d(TAG, "download success!");
                } else {
                    Log.d(TAG, "download failed!");
                }
            }
        }).execute();
    }
    // endregion


    // region - GLViewListener
    GLView.GLViewListener glViewListener = new GLView.GLViewListener() {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mScreenRenderer.create(gl, config);
            mCameraTexture.createCameraTexture();
        }

        @Override
        public void onDrawFrame(GL10 gl, int width, int height) {
            if (mCameraTexture.getSurfaceTexture() == null) {
                return;
            }

            if (mCamera != null) {
                mCamera.setCameraTexture(mCameraTexture.getTextureId(), mCameraTexture.getSurfaceTexture());
            }

            ARGFrame frame = mARGSession.drawFrame(gl, mScreenRatio, width, height);
            mScreenRenderer.draw(frame, width, height);

            if (mHasTrigger) updateTriggerStatus(frame.getItemTriggerFlag());

            if (mARGMedia != null) {
                if (mARGMedia.isRecording()) mARGMedia.updateFrame(frame.getTextureId());
               }

            if(mUseARGSessionDestroy)
                mARGSession.destroy();
        }
    };
    // endregion


    // region - CameraListener
    ReferenceCamera.CameraListener cameraListener = new ReferenceCamera.CameraListener() {
        @Override
        public void setConfig(int previewWidth, int previewHeight, float verticalFov, float horizontalFov, int orientation, boolean isFrontFacing, float fps) {
            mARGSession.setCameraConfig(new ARGCameraConfig(previewWidth,
                    previewHeight,
                    verticalFov,
                    horizontalFov,
                    orientation,
                    isFrontFacing,
                    fps));
        }

        // region - for camera api 1
        @Override
        public void feedRawData(byte[] data) {
            mARGSession.feedRawData(data);
        }
        // endregion

        // region - for camera api 2
        @Override
        public void feedRawData(Image data) {
            mARGSession.feedRawData(data);
        }
        // endregion
    };
    // endregion




    public void updateTriggerStatus(final int triggerstatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentStickeritem != null && mHasTrigger) {
                    String strTrigger = null;
                    if ((triggerstatus & 1) != 0) {
                        strTrigger = "Open your mouth.";
                    } else if ((triggerstatus & 2) != 0) {
                        strTrigger = "Move your head side to side.";
                    } else if ((triggerstatus & 8) != 0) {
                        strTrigger = "Blink your eyes.";
                    } else {

                    }

                    if (strTrigger != null) {
                        mHasTrigger = false;
                    }
                }
            }
        });
    }

    private void showSlot(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.slot_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showStickers() {
        showSlot(mStickerFragment);
        clearBulge();
    }


    public void clearBulge() {
        mARGSession.contents().clear(ARGContents.Type.Bulge);
    }

    public void clearFilter() {
        mARGSession.contents().clear(ARGContents.Type.FilterItem);
    }

    private void showFilters(){
        showSlot(mFilterFragment);
    }

    private void showBeauty() {
        clearStickers();
        clearBulge();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BeautyFragment.BEAUTY_PARAM1, mScreenRatio);
        mBeautyFragment.setArguments(bundle);
        showSlot(mBeautyFragment);
    }

    public void clearStickers() {
        mCurrentStickeritem = null;
        mHasTrigger = false;
        mARGSession.contents().clear(ARGContents.Type.ARGItem);
    }

    private void closeBeauty() {

    }

    private void showBulge() {
        clearStickers();
//
//        showSlot(mBulgeFragment);
    }



    public void setFilter(ItemModel item) {

        String filePath = mItemDownloadPath + "/" + item.uuid;
        if (getLastUpdateAt(CameraActivity.this) > getFilterUpdateAt(CameraActivity.this, item.uuid)) {
            new FileDeleteAsyncTask(new File(filePath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
                @Override
                public void processFinish(Object result) {
                    Log.d(TAG, "file delete success!");

                    setFilterUpdateAt(CameraActivity.this, item.uuid, getLastUpdateAt(CameraActivity.this));
                    requestSignedUrl(item, filePath, false);
                }
            }).execute();
        } else {
            if (new File(filePath).exists()) {
                setItem(ARGContents.Type.FilterItem, filePath, item);
            } else {
                requestSignedUrl(item, filePath, false);
            }
        }
    }


    public void setFilterStrength(int strength) {
        mARGSession.contents().setFilterLevel(strength);
    }

    public void setVignette() {
        mFilterVignette = !mFilterVignette;
        mARGSession.contents().setFilterOption(ARGContents.FilterOption.VIGNETTING, mFilterVignette);
    }

    public void setBlurVignette() {
        mFilterBlur = !mFilterBlur;
        mARGSession.contents().setFilterOption(ARGContents.FilterOption.BLUR, mFilterBlur);
    }

    public void setBeauty(float[] params) {
        mARGSession.contents().setBeauty(params);
    }

    public void setBulgeFunType(int type) {
        ARGContents.BulgeType bulgeType = ARGContents.BulgeType.NONE;
        switch (type) {
            case 1:
                bulgeType = ARGContents.BulgeType.FUN1;
                break;
            case 2:
                bulgeType = ARGContents.BulgeType.FUN2;
                break;
            case 3:
                bulgeType = ARGContents.BulgeType.FUN3;
                break;
            case 4:
                bulgeType = ARGContents.BulgeType.FUN4;
                break;
            case 5:
                bulgeType = ARGContents.BulgeType.FUN5;
                break;
            case 6:
                bulgeType = ARGContents.BulgeType.FUN6;
                break;
        }
        mARGSession.contents().setBulge(bulgeType);
    }

    private void setDrawLandmark(boolean landmark, boolean faceRect) {

        EnumSet<ARGInferenceConfig.Debug> set = EnumSet.of(ARGInferenceConfig.Debug.NONE);

        if(landmark){
            set.add(ARGInferenceConfig.Debug.FACE_LANDMARK);
        }

        if(faceRect) {
            set.add(ARGInferenceConfig.Debug.FACE_RECT_HW);
            set.add(ARGInferenceConfig.Debug.FACE_RECT_SW);
            set.add(ARGInferenceConfig.Debug.FACE_AXIES);
        }

        mARGSession.setDebugInference(set);
    }



    private void stopRecording() {
        binding.btnCapture.clearAnimation();
        if (viewModel.audio != null) {
            viewModel.audio.pause();
        }
        viewModel.count++;
        binding.progressBar.pause();
        binding.progressBar.addDivider();
        viewModel.isRecording.set(false);
        mARGMedia.stopRecording();
    }


}
