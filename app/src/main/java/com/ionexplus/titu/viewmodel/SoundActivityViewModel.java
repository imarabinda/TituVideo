package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.adapter.SoundVideoAdapter;
import com.ionexplus.titu.model.videos.Video;
import com.ionexplus.titu.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SoundActivityViewModel extends ViewModel {

    public String soundId;
    public int start = 0;
    public int count = 10;
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public SoundVideoAdapter adapter = new SoundVideoAdapter();
    public MutableLiveData<Video.SoundData> soundData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isloading = new MutableLiveData<>(true);
    private CompositeDisposable disposable = new CompositeDisposable();
    public ObservableBoolean isPlaying = new ObservableBoolean(false);
    public String soundUrl;
    public ObservableBoolean isFavourite = new ObservableBoolean(false);

    public void fetchSoundVideos(boolean isLoadMore) {
        if (!disposable.isDisposed()) {
            disposable.clear();
        }
        disposable.add(Global.initRetrofit().getSoundVideos(count, start, soundId, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(dis->isloading.setValue(true))
                .doOnTerminate(() -> {
                    isloading.setValue(false);
                    onLoadMoreComplete.setValue(true);
                })
                .subscribe((video, throwable) -> {
                    if (video != null && video.getData() != null && !video.getData().isEmpty()) {
                        if (isLoadMore) {
                            adapter.loadMore(video.getData());
                        } else {
                            adapter.updateData(video.getData());
                            this.soundData.setValue(video.getSoundData());
                        }
                        isloading.setValue(false);
                        start = start + count;
                    }

                }));
    }

    public void onLoadMore() {
        fetchSoundVideos(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
