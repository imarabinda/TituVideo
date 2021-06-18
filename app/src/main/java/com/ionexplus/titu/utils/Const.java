package com.ionexplus.titu.utils;

import com.ionexplus.titu.BuildConfig;

public final class Const {


    public static final String BASE_URL = BuildConfig.DEBUG ? "https://dev.titu.live/api/" : "https://tituadmin.titu.live/api/";
    public static final String ITEM_BASE_URL = BuildConfig.DEBUG ? "https://dev.titu.live/uploads/" : "https://tituadmin.titu.live/uploads/";

    public static final String IS_LOGIN = "is_login";
    public static final String PREF_NAME = "com.ionexplus.titu";
    public static final String GOOGLE_LOGIN = "google";
    public static final String FACEBOOK_LOGIN = "facebook";
    public static final String USER = "user";
    public static final String FAV = "fav";
}
