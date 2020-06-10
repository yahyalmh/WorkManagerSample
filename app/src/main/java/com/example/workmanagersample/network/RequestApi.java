package com.example.workmanagersample.network;

import com.example.workmanagersample.models.Picture;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RequestApi {
    @GET("/photos/random?count=1")
    public Observable<ArrayList<Picture>> getPhoto();

    @GET
    public Call<ResponseBody> downloadImage(@Url String url);
}
