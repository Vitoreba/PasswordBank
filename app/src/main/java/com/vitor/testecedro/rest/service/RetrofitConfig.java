package com.vitor.testecedro.rest.service;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitConfig {

    private Retrofit retrofit;
    private static RetrofitConfig instance;

    private static final String BASE_URL = "https://dev.people.com.ai/mobile/api/v2/";

    public RetrofitConfig() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static RetrofitConfig getInstance() {
        if (instance == null) {
            instance = new RetrofitConfig();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public interface OnRestResponseListener<T> {

        void onRestSuccess(T response);

        void onRestError(ResponseBody body, Integer code);

    }

}
