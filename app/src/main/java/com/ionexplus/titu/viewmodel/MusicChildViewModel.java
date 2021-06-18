package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ionexplus.titu.adapter.MusicsCategoryAdapter;
import com.ionexplus.titu.adapter.MusicsListAdapter;
import com.ionexplus.titu.utils.Global;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MusicChildViewModel extends ViewModel {

    public int type = 0;
    public MusicsCategoryAdapter categoryAdapter = new MusicsCategoryAdapter();
    public MusicsListAdapter musicsListAdapter = new MusicsListAdapter();
    private CompositeDisposable disposable = new CompositeDisposable();
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    public ObservableBoolean noCategory = new ObservableBoolean(false);
    public ObservableBoolean noMusicList = new ObservableBoolean(false);



    public void getMusicList() {
        disposable.add(Global.initRetrofit().getSoundList(Global.ACCESS_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate(()->isLoading.set(false))
                .doOnSubscribe(is->isLoading.set(true))
                .subscribe((soundList, throwable) -> {
                    if (soundList != null && soundList.getStatus() && soundList.getData() != null && !soundList.getData().isEmpty()) {
                        categoryAdapter.updateData(soundList.getData());
                    }
                    noCategory.set(categoryAdapter.getData().isEmpty());
                    isLoading.set(false);

                }));
    }


    public void getFavMusicList(List<String> favouriteMusic) {
        JsonObject jsonObject = new JsonObject();
        JsonArray ids = new JsonArray();
        for (int i = 0; i < favouriteMusic.size(); i++) {
            ids.add(favouriteMusic.get(i));
        }
        jsonObject.add("sound_ids", ids);
        disposable.add(Global.initRetrofit().getFavSoundList(Global.ACCESS_TOKEN, jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate(()->isLoading.set(false))
                .doOnSubscribe(is->isLoading.set(true))
                .subscribe((soundList, throwable) -> {
                    if (soundList != null/* && soundList.getStatus()*/ && soundList.getData() != null && !soundList.getData().isEmpty()) {
                        musicsListAdapter.updateData(soundList.getData());
                    }
                    isLoading.set(false);
                    noMusicList.set(musicsListAdapter.getData().isEmpty());
                   }));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
