package com.example.workmanagersample.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGen {
    public static String BASE_URL = "https://api.unsplash.com";
    private static String app_key = "PHBvMInLb18uN2E9onm9Zz7FjgqgZfa2GQwJXTL7Sv8";
    private static OkHttpClient httpClient;

    static {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("client_id", app_key).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        };
        httpClient= new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();
    private static RequestApi requestApi = retrofit.create(RequestApi.class);

    public static RequestApi getRequestApi() {
        return requestApi;
    }
}
