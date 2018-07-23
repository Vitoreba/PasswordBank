package com.vitor.testecedro.rest.service;

import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.model.ApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SitesService {

    @POST("register")
    Call<ApiResponse> doRegisterPerson(@Body Person person);

    @POST("login")
    Call<ApiResponse> doLogin(@Body Person person);

    @GET("logo/{nomedosite}")
    Call<ResponseBody> doGetSiteLogo(@Header("authorization") String token,
                                     @Path("nomedosite") String nomeDoSite);

}
