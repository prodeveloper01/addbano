package com.example.newfestivalpost.payment.Network;

import com.example.newfestivalpost.payment.Config;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String API_URL_EXTENSION = "/v100/";
    public static final String API_USER_NAME = "admin";
    public static final String API_PASSWORD = "1234";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(API_USER_NAME, API_PASSWORD)).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.API_SERVER_URL + API_URL_EXTENSION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
