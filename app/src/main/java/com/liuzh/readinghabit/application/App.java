package com.liuzh.readinghabit.application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * @author Created by 刘晓彬.
 * @date on 2017/4/18.
 * <p>
 * 没有写不出来的bug,只有不努力的码农
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
