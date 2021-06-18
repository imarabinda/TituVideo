package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.adapter.MusicsListAdapter;
import com.ionexplus.titu.model.music.Musics;
import com.ionexplus.titu.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MusicMainViewModel extends ViewModel {
    public ObservableBoolean isSearch = new ObservableBoolean(false);
    public ObservableBoolean isMore = new ObservableBoolean(false);
    public ObservableBoolean isSearchEmpty = new ObservableBoolean(false);
    public MusicsListAdapter searchMusicAdapter = new MusicsListAdapter();
    public MutableLiveData<Musics.SoundList> music = new MutableLiveData<>();
    public MutableLiveData<Boolean> stopMusic = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private String searchText = "";
    private CompositeDisposable disposable = new CompositeDisposable();

    public void onSearchTextChanged(CharSequence text) {
        isSearchEmpty.set(text.toString().isEmpty());
        searchText = text.toString();
        callApiForSearchMusic();
    }

    private void callApiForSearchMusic() {

        if (!disposable.isDisposed()) {
            disposable.clear();
        }
        disposable.add(Global.initRetrofit().searchSoundList(Global.ACCESS_TOKEN, searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io()).doOnSubscribe(dis->isLoading.setValue(true)).doOnTerminate(()->isLoading.setValue(false))
                .subscribe((musicList, throwable) -> {
                    if (musicList != null && musicList.getData() != null && !musicList.getData().isEmpty()) {
                        searchMusicAdapter.updateData(musicList.getData());
                    }
                    isLoading.setValue(false);
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}

