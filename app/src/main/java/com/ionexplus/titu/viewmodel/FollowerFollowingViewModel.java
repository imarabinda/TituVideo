package com.ionexplus.titu.viewmodel;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import com.ionexplus.titu.model.user.User;

public class FollowerFollowingViewModel extends ViewModel {

    public ObservableInt itemType = new ObservableInt(0);
    public User user;


}
