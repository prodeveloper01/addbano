package com.example.newfestivalpost.payment;

import static com.example.newfestivalpost.Retrofit.Base_Url1.API_KEY11;
import static com.example.newfestivalpost.Retrofit.Base_Url1.BASE_URL11;

public class Config {

    public static final String API_SERVER_URL = BASE_URL11;
    public static final String API_KEY = API_KEY11;

    public static final String TERMS_URL = API_SERVER_URL.replace("rest-api/","terms/");

    public static final String ENVATO_PURCHASE_CODE = "xxxxxxxxxxxxxx-xxxxxxxxxx-xxxxxxxxxx-xxxxxx-xxxxxxxxxxxxx";

    public static final boolean ENABLE_DOWNLOAD_TO_ALL = false;

    public static boolean ENABLE_RTL = false;

    public static boolean YOUTUBE_VIDEO_AUTO_PLAY = false;

    public static final boolean ENABLE_EXTERNAL_PLAYER = false;

    public static boolean DEFAULT_DARK_THEME_ENABLE = true;

    public static final boolean ENABLE_FACEBOOK_LOGIN = false;

    public static final boolean ENABLE_PHONE_LOGIN = true;

    public static final boolean ENABLE_GOOGLE_LOGIN = false;
}
