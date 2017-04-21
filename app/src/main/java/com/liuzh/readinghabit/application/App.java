package com.liuzh.readinghabit.application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class App extends Application {

    private static Toast mToast;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }


    public static void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
