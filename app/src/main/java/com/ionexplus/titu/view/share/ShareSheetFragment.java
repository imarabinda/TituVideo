package com.ionexplus.titu.view.share;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.ionexplus.titu.R;
import com.ionexplus.titu.databinding.FragmentShareSheetBinding;
import com.ionexplus.titu.model.videos.Video;
import com.ionexplus.titu.utils.Const;
import com.ionexplus.titu.utils.CustomDialogBuilder;
import com.ionexplus.titu.utils.RemoteConfig;
import com.ionexplus.titu.utils.SessionManager;
import com.ionexplus.titu.view.home.ReportSheetFragment;
import com.ionexplus.titu.viewmodel.ShareSheetViewModel;
import com.ionexplus.titu.viewmodelfactory.ViewModelFactory;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.reactivex.annotations.SchedulerSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class ShareSheetFragment extends BottomSheetDialogFragment {
    private static final int MY_PERMISSIONS_REQUEST = 101;
    FragmentShareSheetBinding binding;
    private CustomDialogBuilder customDialogBuilder;
    ShareSheetViewModel viewModel;
    public FirebaseRemoteConfig remoteConfig;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(bundle);
        bottomSheetDialog.setOnShowListener(view->{
        });
        remoteConfig =FirebaseRemoteConfig.getInstance();
        return bottomSheetDialog;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_share_sheet, viewGroup, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ShareSheetViewModel()).createFor()).get(ShareSheetViewModel.class);
        customDialogBuilder = new CustomDialogBuilder(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        initView();
        initListeners();
        initObserve();
    }

    private void initView() {
        binding.setViewModel(viewModel);
        if (!(getArguments() == null || getArguments().getString("video") == null)) {
            viewModel.video = (Video.Data) new Gson().fromJson(getArguments().getString("video"), Video.Data.class);
        }
        createVideoShareLink();
    }

    private void initListeners() {
        binding.btnCopy.setOnClickListener(view->{
            if (getActivity() != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData newPlainText = ClipData.newPlainText("Video link",remoteConfig.getString("share_copy_text").replace("{link}", viewModel.shareUrl));
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(newPlainText);
                    Toast.makeText(getActivity(), "Copied To Clipboard Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnDownload.setOnClickListener(view->{
            if (viewModel.video == null || viewModel.video.getCanSave() == null || !viewModel.video.getCanSave().equals("1")) {
                initPermission();
            } else {
                Toast.makeText(getActivity(), "Download is disabled by the creator", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnReport.setOnClickListener(view->{
            ReportSheetFragment reportSheetFragment = new ReportSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putString("postid", viewModel.video.getPostId());
            bundle.putInt("reporttype", 1);
            reportSheetFragment.setArguments(bundle);
            if (getParentFragment() != null) {
                reportSheetFragment.show(getParentFragment().getChildFragmentManager(), reportSheetFragment.getClass().getSimpleName());
            }
            dismiss();
        });
    }




    private void initObserve() {
        new SessionManager(getActivity()).getUser();
        viewModel.onItemClick.observe(this,num->{
            Intent intent = new Intent("android.intent.action.SEND");
            int intValue = num.intValue();
            if (intValue == 1) {
                intent.setPackage("com.instagram.android");
            } else if (intValue == 2) {
                intent.setPackage("com.facebook.katana");
            } else if (intValue == 3) {
                intent.setPackage("com.whatsapp");
            }
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "Share Video");
            intent.putExtra("android.intent.extra.TEXT",  remoteConfig.getString("share_video_text").replace("{link}",viewModel.shareUrl).replace("{name}",viewModel.video.getFullName()));
            startActivity(Intent.createChooser(intent, "Share Video"));
            dismiss();
        });
    }


    private void initPermission() {
        if (getActivity() == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") == 0 && ActivityCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            startDownload();
        } else {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 101 && iArr[0] == 0) {
            startDownload();
        }
    }

    private void startDownload() {


//        ((RequestBuilder) Glide.with(getActivity()).load(getActivity().getResources().getDrawable(R.drawable.logo)).circleCrop()).into(binding.ivProfile);

        PRDownloader.download(Const.ITEM_BASE_URL + viewModel.video.getPostVideo(), getPath().getPath(), viewModel.video.getPostVideo()).build().setOnStartOrResumeListener(new OnStartOrResumeListener() {

            @Override
            public final void onStartOrResume() {
                customDialogBuilder.showLoadingDialog();
            }
        }).start(new OnDownloadListener() {

            @Override
            public void onDownloadComplete() {
                saveBitMap(getPath().getPath() + "/" + viewModel.video.getPostVideo());
            }

            @Override
            public void onError(Error error) {
                customDialogBuilder.hideLoadingDialog();
                Log.d("DOWNLOAD", "onError: " + error.getConnectionException().getMessage());
            }
        });
    }

    public File getPath() {
        if (getActivity() == null) {
            return new File(Environment.getRootDirectory().getAbsolutePath());
        }
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return getActivity().getExternalFilesDir(null);
        }
        return getActivity().getFilesDir();
    }

    private void createVideoShareLink() {
        String json = new Gson().toJson(viewModel.video);
        String postDescription = viewModel.video.getPostDescription();
        Log.i("ShareJson", "Json Object: " + json);
        BranchUniversalObject title = new BranchUniversalObject().setCanonicalIdentifier("content/12345").setTitle(postDescription);
        BranchUniversalObject contentMetadata = title.setContentImageUrl(Const.ITEM_BASE_URL + viewModel.video.getPostImage()).setContentDescription(viewModel.video.getPostDescription()).setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC).setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC).setContentMetadata(new ContentMetadata().addCustomMetadata("data", json));
        LinkProperties addControlParameter = new LinkProperties().setFeature("sharing").setCampaign("Content launch").setStage("Video").addControlParameter(SchedulerSupport.CUSTOM, "data").addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));
        if (getActivity() != null) {
            contentMetadata.generateShortUrl(getActivity(), addControlParameter, new Branch.BranchLinkCreateListener() {
                @Override
                public final void onLinkCreate(String str, BranchError branchError) {
                    viewModel.shareUrl = str;
                }
            });
        }
    }


    private void saveBitMap(String str) {

        final File watermark = new File(getPath().getPath() + "/"+ System.currentTimeMillis() + ".jpg");
        Bitmap bitmapFromView = getBitmapFromView(binding.watermark);

        try {
            watermark.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(watermark);
            bitmapFromView.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }

        File directory = new File(Environment.getExternalStoragePublicDirectory("/"),  getString(R.string.app_name));

        if (!directory.exists()) {
            directory.mkdir();
        }


//            -loop 1 -framerate 24 -t 10 -i "+file2.getAbsolutePath()+" " +
//            " -i "+str+" " +
//                    " -filter_complex \"[0:v]scale="+width+":"+height+":force_original_aspect_ratio=decrease,pad="+width+":"+height+":(ow-iw)/2:(oh-ih)/2,setsar=1[v0];[v0][1]concat=n=2:v=1:a=0\" " + Environment.getExternalStorageDirectory() + "/Download/" + viewModel.video.getPostVideo()

        FFmpeg.executeAsync("-i " + str + " -i " + watermark.getAbsolutePath() + " -filter_complex \"[1]scale=-1:-1[b];[0][b] overlay=x=10:y=10\" -qscale:v 5 -y -c:a copy \"" + directory.getPath()+"/" + viewModel.video.getPostVideo()+"\"", new ExecuteCallback() {

            @Override
            public void apply(long j, int i) {
                if (i == RETURN_CODE_SUCCESS) {
                    watermark.delete();
                    File downloaded_video = new File(str);
                    if(downloaded_video.exists()){
                        downloaded_video.delete();
                    }

                    customDialogBuilder.hideLoadingDialog();
                    Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                    Log.i(Config.TAG, "Command execution completed successfully.");
                } else if (i == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Command execution cancelled by user.");
                } else {
                    File downloaded_video = new File(str);
                    if(downloaded_video.exists()){
                        downloaded_video.delete();
                    }
                    watermark.delete();
                    customDialogBuilder.hideLoadingDialog();
                    Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                    Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", Integer.valueOf(i)));
                    Config.printLastCommandOutput(4);
                }
            }
        });

    }

    private Bitmap getBitmapFromView(View view) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getPath().getPath() + "/" + viewModel.video.getPostVideo());
        Integer.parseInt(mediaMetadataRetriever.extractMetadata(18));
        Integer.parseInt(mediaMetadataRetriever.extractMetadata(19));
        mediaMetadataRetriever.release();
        Log.i("TAG", "getBitmapFromView: " + view.getWidth() + view.getHeight());
        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(0);
        }
        view.draw(canvas);
        return Bitmap.createScaledBitmap(createBitmap, createBitmap.getWidth() / 2, createBitmap.getHeight() / 2, false);
    }
}