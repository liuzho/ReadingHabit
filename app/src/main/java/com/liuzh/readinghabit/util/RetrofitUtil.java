package com.liuzh.readinghabit.util;

import android.os.Build;
import android.webkit.WebSettings;

import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.bean.Update;
import com.liuzh.readinghabit.bean.one.One;
import com.liuzh.readinghabit.bean.read.Read;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private static final String BASE_URL_UPDATE = "https://liuzho.com/readinghabitapp/";

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

    /**
     * 获取Retrofit实例
     *
     * @param baseUrl 请求api头
     * @return retrofit实例
     */
    private static Retrofit getUserAgentRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getUserAgentClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient getUserAgentClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .removeHeader("User-Agent")//移除旧的
                                .addHeader("User-Agent", getUserAgent())//添加真正的头部
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
    }

    private static String getUserAgent() {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(App.getContext());
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /////////////////////////////////////////////////////////////////////////////////
    interface OneService {
        @GET("hp/bymonth/{date}%2000:00:00?channel=wdj&version=4.0.2" +
                "&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android")
        Call<One> getCall(@Path("date") String date);
    }

    public static Call<One> getOneCall(String date) {
        return getRetrofit(BASE_URL_ONE).create(OneService.class).getCall(date);
    }

    //////////////////////////////////////////////////////////////////////////////
    interface ReadService {
        @GET("article/day?dev=1")
        Call<Read> getCall(@Query("date") String date);
    }

    public static Call<Read> getReadCall(String date) {
        return getUserAgentRetrofit(BASE_URL_READ)
                .create(ReadService.class)
                .getCall(date);
    }

    /////////////////////////////////////////////////////////////////////////////
    interface UpdateService {
        @GET("update.json")
        Call<Update> getCall();
    }

    public static Call<Update> getUpdateCall() {
        return getRetrofit(BASE_URL_UPDATE).create(UpdateService.class).getCall();
    }
}
