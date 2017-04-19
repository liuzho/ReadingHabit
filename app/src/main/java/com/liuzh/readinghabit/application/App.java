package com.liuzh.readinghabit.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class App extends Application {

    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
