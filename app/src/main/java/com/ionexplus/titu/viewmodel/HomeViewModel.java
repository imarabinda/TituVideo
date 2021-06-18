package com.ionexplus.titu.viewmodel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    public MutableLiveData<Boolean> isShowLoaderInHome = new MutableLiveData<>();
    public ObservableInt loadingVisibility = new ObservableInt(View.GONE);
    public MutableLiveData<Integer> onPageSelect = new MutableLiveData<>();
    public MutableLiveData<Boolean> onRefresh = new MutableLiveData<>();
    public MutableLiveData<Boolean> onStop = new MutableLiveData<>();
    public MutableLiveData<Integer> scroll = new MutableLiveData<>();

}
