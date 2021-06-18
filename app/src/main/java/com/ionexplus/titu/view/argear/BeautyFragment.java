package com.ionexplus.titu.view.argear;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexplus.titu.R;
import com.ionexplus.titu.adapter.BeautyListAdapter;
import com.ionexplus.titu.customview.CustomSeekBar;
import com.ionexplus.titu.data.BeautyItemData;
import com.ionexplus.titu.databinding.FragmentBeautyBinding;
import com.ionexplus.titu.utils.AppConfig;
import com.ionexplus.titu.view.recordvideo.CameraActivity;
import com.seerslab.argear.session.ARGContents;
import com.seerslab.argear.session.ARGFrame;

import java.util.Locale;

public class BeautyFragment
        extends Fragment
        implements View.OnClickListener, BeautyListAdapter.Listener {

    public static final String BEAUTY_PARAM1 = "bearuty_param1";
    private static final String TAG = BeautyFragment.class.getSimpleName();
    private View.OnTouchListener BeautyComparisonBtnTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                zeroBeautyParam();
            } else if (1 == motionEvent.getAction()) {
                reloadBeauty();
            }
            return true;
        }
    };
    private SeekBar.OnSeekBarChangeListener BeautySeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                mBeautyItemData.setBeautyValue(mCurrentBeautyType, (float) i);
                ((CameraActivity) getActivity()).setBeauty(mBeautyItemData.getBeautyValues());
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private FragmentBeautyBinding binding;
    private BeautyItemData mBeautyItemData;
    private BeautyListAdapter mBeautyListAdapter;
    private ARGContents.BeautyType mCurrentBeautyType = ARGContents.BeautyType.VLINE;
    private ARGFrame.Ratio mScreenRatio;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mScreenRatio = (ARGFrame.Ratio) getArguments().getSerializable(BEAUTY_PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_beauty, viewGroup, false);
        return binding.getRoot();
    }

    @Override 
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        binding.beautyInitButton.setOnClickListener(this);
        binding.beautyCloseButton.setOnClickListener(this);
        binding.beautyItemsLayout.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.beautyItemsLayout.setLayoutManager(linearLayoutManager);
        mBeautyListAdapter = new BeautyListAdapter(this);
        binding.beautyItemsLayout.setAdapter(mBeautyListAdapter);
        binding.beautySeekbar.setOnSeekBarChangeListener(BeautySeekBarListener);
        binding.beautyComparisonButton.setOnTouchListener(BeautyComparisonBtnTouchListener);
        BeautyItemData beautyItemData = ((CameraActivity) getActivity()).getBeautyItemData();
        mBeautyItemData = beautyItemData;
        mBeautyListAdapter.setData(beautyItemData.getItemInfoData());
        mBeautyListAdapter.selectItem(ARGContents.BeautyType.VLINE);
        updateUIStyle(mScreenRatio);
        onBeautyItemSelected(ARGContents.BeautyType.VLINE);
        reloadBeauty();
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.beauty_close_button:
                getActivity().onBackPressed();
                break;
            case R.id.beauty_init_button:
                ((CameraActivity) getActivity()).setBeauty(AppConfig.BEAUTY_TYPE_INIT_VALUE);
                mBeautyItemData.initBeautyValue();
                binding.beautySeekbar.setProgress((int) mBeautyItemData.getBeautyValue(mCurrentBeautyType));
                break;
        }

    }

    @Override
    public void onBeautyItemSelected(ARGContents.BeautyType beautyType) {
        int[] iArr = ARGContents.BEAUTY_RANGE.get(beautyType);
        if (iArr != null) {
            mCurrentBeautyType = beautyType;
            binding.beautySeekbar.setMax(iArr[1]);
            binding.beautySeekbar.setProgress((int) mBeautyItemData.getBeautyValue(beautyType));
        }
    }


    @Override
    public ARGFrame.Ratio getViewRatio() {
        return mScreenRatio;
    }

    private void zeroBeautyParam() {
        ((CameraActivity) getActivity()).setBeauty(new float[16]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void reloadBeauty() {
        ((CameraActivity) getActivity()).setBeauty(mBeautyItemData.getBeautyValues());
    }

    public void updateUIStyle(ARGFrame.Ratio ratio) {
        mScreenRatio = ratio;
        if (ratio == ARGFrame.Ratio.RATIO_FULL) {
            binding.beautySeekbar.setActivated(false);
        } else {

            binding.beautySeekbar.setActivated(true);
        }
        mBeautyListAdapter.notifyDataSetChanged();
    }

}
