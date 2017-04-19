package com.liuzh.readinghabit.util;

import com.liuzh.readinghabit.bean.one.One;
import com.liuzh.readinghabit.bean.read.Read;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class RetrofitUtil {
    private static final String BASE_URL_ONE = "http://v3.wufazhuce.com:8000/api/";

    private static final String BASE_URL_READ = "https://interface.meiriyiwen.com/";

    /**
     * 获取Retrofit实例
     *
     * @param baseUrl 请求api头
     * @return retrofit实例
     */
    private static Retrofit getRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    interface OneService {
        @GET("hp/bymonth/{date}%2000:00:00?channel=wdj&version=4.0.2" +
                "&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android")
        Call<One> getCall(@Path("date") String date);
    }

    public static Call<One> getOneCall(String date) {
        return getRetrofit(BASE_URL_ONE).create(OneService.class).getCall(date);
    }


    interface ReadService {
        @GET("article/day?dev=1&date=20170216")
        Call<Read> getCall(@Query("date") String date);
    }

    public static Call<Read> getReadCall(String date) {
        return getRetrofit(BASE_URL_READ).create(ReadService.class).getCall(date);
    }

}
