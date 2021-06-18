package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.model.user.RestResponse;
import com.ionexplus.titu.utils.Global;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ReportViewmodel extends ViewModel {

    public int reportType;
    public String postId;
    public String userId;
    public String reported_by;
    public String reason;
    public String description = "";
    public String contactInfo = "";
    public MutableLiveData<Boolean> isValid = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();

    public void afterUserNameTextChanged(CharSequence s) {
        description = s.toString();
    }

    public void afterContactDetailsChanged(CharSequence s) {
        contactInfo = s.toString();
    }


    public void callApiToReport() {
        if (!description.isEmpty() && !contactInfo.isEmpty()) {
            HashMap<String, Object> hashMap = new HashMap<>();
            if (reportType == 1) {
                hashMap.put("report_type", "report_video");
                hashMap.put("post_id", postId);
            } else {
                hashMap.put("report_type", "report_user");
                hashMap.put("user_id", userId);
            }
            hashMap.put("reason", reason);
            hashMap.put("reported_by", reported_by);
            hashMap.put("description", description);
            hashMap.put("contact_info", contactInfo);

            disposable.add(Global.initRetrofit().reportSomething(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .doOnSubscribe(disposable1 -> isLoading.setValue(true))
                    .doOnTerminate(() -> isLoading.setValue(false))
                    .subscribe(new BiConsumer<RestResponse, Throwable>() {
                        @Override
                        public void accept(RestResponse restResponse, Throwable throwable) throws Exception {
                            if (restResponse != null && restResponse.getStatus().booleanValue()) {
                                isSuccess.setValue(true);
                            }
                        }
                    }));
        } else {
            isValid.setValue(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
