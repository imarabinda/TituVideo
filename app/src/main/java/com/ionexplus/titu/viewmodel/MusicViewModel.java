package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.adapter.MusicsListAdapter;
import com.ionexplus.titu.model.music.Musics;

public class MusicViewModel extends ViewModel {
    public ObservableInt selectPosition = new ObservableInt(0);

    public ObservableBoolean isMore = new ObservableBoolean(false);
    public MusicsListAdapter searchMusicAdapter = new MusicsListAdapter();
    public MutableLiveData<Musics.SoundList> music = new MutableLiveData<>();
    public MutableLiveData<Boolean> stopMusic = new MutableLiveData<>();
}
