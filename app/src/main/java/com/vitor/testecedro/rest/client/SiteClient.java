package com.vitor.testecedro.rest.client;

import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.model.ApiResponse;
import com.vitor.testecedro.rest.service.RetrofitConfig;
import com.vitor.testecedro.rest.service.SitesService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiteClient {

    private static final String TAG = "CEPClient";

    private static SiteClient instance;
    private static SitesService service;
    private boolean logJson = true;

    public SiteClient() {
        instance = this;
        service = RetrofitConfig.getInstance().getRetrofit().create(SitesService.class);
    }

    public static SiteClient getInstance() {
        if (instance == null) {
            instance = new SiteClient();
        }

        return instance;
    }

    public void doRegisterPerson(Person person
            , final RetrofitConfig.OnRestResponseListener<ApiResponse> listener) {
        service.doRegisterPerson(person).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    listener.onRestSuccess(response.body());
                } else {
                    listener.onRestError(response.errorBody(), response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                listener.onRestError(null, null);
            }
        });
    }

    public void doLogin(Person person
            , final RetrofitConfig.OnRestResponseListener<ApiResponse> listener) {
        service.doLogin(person).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    listener.onRestSuccess(response.body());
                } else {
                    listener.onRestError(response.errorBody(), response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                listener.onRestError(null, null);
            }
        });
    }

    public void doGetSiteLogo(String token, String urlSite
            , final RetrofitConfig.OnRestResponseListener<ResponseBody> listener) {
        service.doGetSiteLogo(token, urlSite).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onRestSuccess(response.body());
                } else {
                    listener.onRestError(response.errorBody(), response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onRestError(null, null);
            }
        });
    }
}
