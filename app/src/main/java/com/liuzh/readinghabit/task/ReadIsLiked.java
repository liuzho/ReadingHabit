package com.liuzh.readinghabit.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.db.LikeDBHelper;

/**
 * Created by 刘晓彬 on 2017/4/21.
 */

public class ReadIsLiked extends AsyncTask<String, Void, Boolean> {


    private static final String TAG = "ReadIsLiked";
    private ImageView mBtLike;

    public ReadIsLiked(ImageView btLike){
        mBtLike = btLike;
    }


    @Override
    protected Boolean doInBackground(String... params) {
        String curr = params[0];
        SQLiteDatabase db = new LikeDBHelper(App.getContext()).getWritableDatabase();
        Cursor cursor = db.query(LikeDBHelper.READ_TABLE_NAME,
                new String[]{LikeDBHelper.CURR},
                LikeDBHelper.CURR + "=?",
                new String[]{curr}, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.moveToFirst();
            String string = cursor.getString(cursor.getColumnIndex(LikeDBHelper.CURR));
            Log.i(TAG, "doInBackground: " + string);
            cursor.close();
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean){
            mBtLike.setImageResource(R.drawable.like_);
        }else{
            mBtLike.setImageResource(R.drawable.like);
        }
    }
}
