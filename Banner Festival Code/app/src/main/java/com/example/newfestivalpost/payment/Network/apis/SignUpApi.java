package com.example.newfestivalpost.payment.Network.apis;



import com.example.newfestivalpost.payment.Network.models.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

    public interface SignUpApi {
    @FormUrlEncoded
    @POST("signup")
    Call<User> signUp(@Header("API-KEY") String apiKey,
                      @Field("email") String email,
                      @Field("password") String password,
                      @Field("name") String name);

}
