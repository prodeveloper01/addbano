package com.example.newfestivalpost.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Base_Url1 {


    public static final String BASE_URL11 = "https://epsilonitservice.com/envatomarket/postermaker/rest-api/";
//    public static final String BASE_URL11 = "http://app.myadbazaar.in/digitalpostV8/rest-api/";
//    public static final String API_KEY11 = "sa361uhiee3oioar50hb4j5v";
//    public static final String BASE_URL11 = "https://epsilonitservice.com/envatomarket/festivalpostermaker/rest-api/";
    public static final String API_KEY11 = "8e90d71140a94bb";

    public static final String BASE_URL = BASE_URL11 + "/v100/";
    public static final String API_USER_NAME = "admin";
    public static final String API_PASSWORD = "1234";
    private static Retrofit retrofit = null;


    public static Retrofit getRetrofitInstance() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(API_USER_NAME, API_PASSWORD)).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}
