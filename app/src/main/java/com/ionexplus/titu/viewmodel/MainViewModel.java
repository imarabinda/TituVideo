package com.ionexplus.titu.viewmodel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public boolean isBack = false;
    public MutableLiveData<Boolean> isShowLoaderInHome = new MutableLiveData<>();
    public ObservableInt loadingVisibility = new ObservableInt(View.GONE);
    public MutableLiveData<Boolean> onStop = new MutableLiveData<>();
    public MutableLiveData<Integer> scroll = new MutableLiveData<>(0);
    public MutableLiveData<Integer> selectedPosition = new MutableLiveData<>(0);

}
