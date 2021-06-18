package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.ionexplus.titu.adapter.BannerAdapter;
import com.ionexplus.titu.adapter.ExploreHashTagAdapter;
import com.ionexplus.titu.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragmentViewModel extends ViewModel {

    public int exploreStart = 0;
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public MutableLiveData<Boolean> bannerLoaded = new MutableLiveData<>();
    public ExploreHashTagAdapter adapter = new ExploreHashTagAdapter();
    public BannerAdapter bannerAdapter = new BannerAdapter();
    public ObservableBoolean isloading = new ObservableBoolean(true);
    private CompositeDisposable disposable = new CompositeDisposable();
    private int count = 10;

    public void fetchExploreItems(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().getExploreVideos(count, exploreStart, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isloading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isloading.set(false);
                })
                .subscribe((explore, throwable) -> {
                    if (explore != null && explore.getData() != null) {
                        if (isLoadMore) {
                            adapter.loadMore(explore.getData());
                        } else {
                            if (!new Gson().toJson(explore.getData()).equals(new Gson().toJson(adapter.getData()))) {
                                adapter.updateData(explore.getData());
                            }
                        }
                        exploreStart = exploreStart + count;
                        isloading.set(false);
                    }
                }));
    }


    public void fetchBannerItems() {
        disposable.add(Global.initRetrofit().getBanners(Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> bannerLoaded.setValue(false))
                .doOnTerminate(() -> {
                    bannerLoaded.setValue(false);
                })
                .subscribe((banner, throwable) -> {
                    if (banner != null && banner.getData() != null && !banner.getData().isEmpty()) {
                        bannerAdapter.updateData(banner.getData());
                        bannerLoaded.setValue(true);
                    }

                    if (throwable != null) {
                        Log.d("TEST","banner error"+throwable.toString());
                    }
                }));
    }

    public void onExploreLoadMore() {

        fetchExploreItems(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
