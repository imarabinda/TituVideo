package com.ionexplus.titu.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.model.user.RestResponse;
import com.ionexplus.titu.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RedeemViewModel extends ViewModel {

    public String coindCount;
    public String coinRate;
    public String requestType;
    public String accountId;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<RestResponse> redeem = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    public void afterPaymentAccountChanged(CharSequence s) {
        accountId = s.toString();
    }

    public void callApiToRedeem() {
        disposable.add(Global.initRetrofit().sendRedeemRequest(Global.ACCESS_TOKEN, coindCount, requestType, accountId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.setValue(true))
                .doOnTerminate(() -> isLoading.setValue(false))
                .subscribe((redeem1, throwable) -> {
                    if (redeem1 != null && redeem1.getStatus() != null) {
                        this.redeem.setValue(redeem1);
                    }
                }));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
