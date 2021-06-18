package com.ionexplus.titu.utils;

import com.google.android.exoplayer2.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class GlobalApi {

    private CompositeDisposable disposable = new CompositeDisposable();
    private String time;
    public void rewardUser(String rewardActionId) {
        disposable.add(Global.initRetrofit().rewardUser(Global.ACCESS_TOKEN, rewardActionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((reward, throwable) -> {
                    Log.d("TEST","runned reward");
                    if(reward !=null && !reward.getMessage().isEmpty()){
                        time = reward.getMessage().toString();
                        Log.d("TEST",time);
                    }
                    if(throwable!=null){
                        Log.d("TEST",throwable.toString());

                    }
                }));
    }

    public void increaseView(String postId) {
        disposable.add(Global.initRetrofit().increaseView(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((reward, throwable) -> {

                }));
    }
}
