package com.ionexplus.titu.utils;

import com.ionexplus.titu.BuildConfig;
public class AppConfig {

    public static final String API_URL = "https://apis.argear.io";
    public static final String API_KEY = "02435c73a9315e812d7b6aa4";
    public static final String SECRET_KEY = "1aebff8dfb1ccad1f74dd649dfcbbf0ebf572588c854944a453685b27543a31f";
    public static final String AUTH_KEY = "U2FsdGVkX19SxaNENGo8eBl5ZdClMkqHQ5bf4z+Oybq74Vc7Wmu4jLus25Kio20QdUeMTns3t6w94uZz30icMg==";

    // preference

    public static final String USER_PREF_NAME = BuildConfig.APPLICATION_ID + ".Preference";
    public static final String USER_PREF_NAME_FILTER = BuildConfig.APPLICATION_ID + ".ARGearFilter.Preference";
    public static final String USER_PREF_NAME_STICKER = BuildConfig.APPLICATION_ID + ".ARGearItem.Preference";

    // camera
    // 1: CAMERA_API_1, 2: CAMERA_API_2
    public static final int USE_CAMERA_API = 1;

    // region - beauty sample
    public static final float[] BEAUTY_TYPE_INIT_VALUE = {
            10,     //VLINE
            90,     //ACE_SLIM
            55,     //JAW
            -50,    //CHIN
            5,      //EYE
            -10,    //EYE_GAP
            0,      //NOSE_LINE
            35,     //NOSE_SIDE
            30,     //NOSE_LENGTH
            -35,    //MOUTH_SIZE
            0,      //EYE_BACK
            0,      //EYE_CORNER
            0,      //LIP_SIZE
            50,     //SKIN
            0,      //DARK_CIRCLE
            0,      //MOUTH_WRINKLE
    };

    public static final float[] BASIC_BEAUTY_1 = {
            20, 10, 45, 45, 5, -10, 40, 20, 15, 0, 0, 0, 0, 50, 0, 0
    };

    public static final float[] BASIC_BEAUTY_2 = {
            10, 90, 55, -50, 5, -10, 0, 35, 30, -35, 0, 0, 0, 50, 0, 0
    };

    public static final float[] BASIC_BEAUTY_3 = {
            25, 20, 50, -25, 25, -10, 30, 40, 90, 0, 0, 0, 0, 50, 0, 0
    };
    // endregion
}
