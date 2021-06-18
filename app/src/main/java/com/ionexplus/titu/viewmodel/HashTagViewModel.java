package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.adapter.VideoListAdapter;
import com.ionexplus.titu.model.videos.Video;
import com.ionexplus.titu.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HashTagViewModel extends ViewModel {

    public VideoListAdapter adapter = new VideoListAdapter();
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public MutableLiveData<Boolean> isloading = new MutableLiveData<>(true);
    public int start = 0;
    private int count = 10;
    private CompositeDisposable disposable = new CompositeDisposable();
    public String hashtag;
    public MutableLiveData<Video> video = new MutableLiveData<>();

    public void fetchHashTagVideos(boolean isLoadMore) {
        if (!disposable.isDisposed()) {
            disposable.clear();
        }
        disposable.add(Global.initRetrofit().fetchHasTagVideo(hashtag, count, start, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io()).doOnSubscribe(dis->isloading.setValue(true))
                .doOnTerminate(() -> {
                    isloading.setValue(false);
                    onLoadMoreComplete.setValue(true);
                })
                .subscribe((video1, throwable) -> {
                    if (video1 != null && video1.getData() != null && !video1.getData().isEmpty()) {
                        this.video.setValue(video1);
                        if (isLoadMore) {
                            adapter.loadMore(video1.getData());
                        } else {
                            adapter.updateData(video1.getData());
                        }
                        start = start + count;
                    }

                }));
    }

    public void onLoadMore() {
        fetchHashTagVideos(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}

