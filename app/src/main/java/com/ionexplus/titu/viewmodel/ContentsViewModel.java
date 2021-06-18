package com.ionexplus.titu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.ionexplus.titu.api.ContentsResponse;

public class ContentsViewModel extends ViewModel {
    private MutableLiveData<ContentsResponse> mutableLiveData = ContentsRepository.getInstance().getContents();

    public LiveData<ContentsResponse> getContents() {
        return this.mutableLiveData;
    }
}