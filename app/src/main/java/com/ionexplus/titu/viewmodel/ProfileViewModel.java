package com.ionexplus.titu.viewmodel;

import android.content.Intent;
import android.net.Uri;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.model.user.RestResponse;
import com.ionexplus.titu.model.user.User;
import com.ionexplus.titu.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileViewModel extends ViewModel {

    public MutableLiveData<User> user = new MutableLiveData<>();
    public MutableLiveData<Integer> onItemClick = new MutableLiveData<>();
    public MutableLiveData<Integer> selectPosition = new MutableLiveData<>();
    public MutableLiveData<Intent> intent = new MutableLiveData<>();
    public MutableLiveData<Boolean> isloading = new MutableLiveData<>(false);
    public ObservableBoolean isFollowLoading = new ObservableBoolean(false);
    public ObservableBoolean isBackBtn = new ObservableBoolean(false);
    public String userId = "";
    public MutableLiveData<RestResponse> followApi = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    public ObservableBoolean isLikedVideos = new ObservableBoolean(false);
    public ObservableInt isMyAccount = new ObservableInt(0);

    public void setOnItemClick(int type) {

        onItemClick.setValue(type);
    }

    public void onSocialClick(int type) {
        String url = "";
        if (user.getValue() != null) {
            switch (type) {
                case 1:
                    url = user.getValue().getData().getFbUrl();
                    break;
                case 2:
                    url = user.getValue().getData().getInstaUrl();
                    break;
                default:
                    url = user.getValue().getData().getYoutubeUrl();
                    break;
            }
        }

        intent.setValue(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void fetchUserById(String userid) {
        disposable.add(Global.initRetrofit().getUserDetails(userid, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isloading.setValue(true))
                .doOnTerminate(() -> isloading.setValue(false))
                .subscribe((user1, throwable) -> {
                    if (user1 != null && user1.getData() != null) {
                        this.user.setValue(user1);
                        if (isMyAccount.get() != 0) {
                            if (user1.getData().getIsFollowing() == 1) {
                                isMyAccount.set(1);
                            } else {
                                isMyAccount.set(2);
                            }
                        }
                    }
                }));
    }

    public void followUnfollow() {

        disposable.add(Global.initRetrofit().followUnFollow(Global.ACCESS_TOKEN, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(obs->{
                    isFollowLoading.set(true);
                }).doOnTerminate(()->{
                    isFollowLoading.set(false);
                }).subscribe((followRequest, throwable) -> {
                    isFollowLoading.set(false);
                    if (followRequest != null && followRequest.getStatus() != null) {
                        if (isMyAccount.get() == 1) {
                            isMyAccount.set(2);
                        } else {
                            isMyAccount.set(1);
                        }
                        followApi.setValue(followRequest);
                    }
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
