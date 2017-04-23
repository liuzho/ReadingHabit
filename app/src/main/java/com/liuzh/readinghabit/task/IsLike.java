package com.liuzh.readinghabit.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.fragment.BaseFragment;

/**
 * Created by 刘晓彬 on 2017/4/22.
 */

public class IsLike extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "IsLike";

    private ImageView mBtLike;
    private BaseFragment mFragment;
    private String mTableName;

    public IsLike(BaseFragment fragment, ImageView btLike, String tableName) {
        mFragment = fragment;
        mBtLike = btLike;
        mTableName = tableName;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String curr = params[0];
        if (TextUtils.isEmpty(curr)) {
            return false;
        }
        SQLiteDatabase db = new LikeDBHelper(App.getContext()).getWritableDatabase();
        Cursor cursor = db.query(mTableName,
                new String[]{LikeDBHelper.CURR},
                LikeDBHelper.CURR + "=?",
                new String[]{curr}, null, null, null);
        Log.i(TAG, "doInBackground: "+curr);
        if (cursor.getCount() == 0) {
            cursor.close();
            Log.i(TAG, "doInBackground: nothing");
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
        if (aBoolean) {
            mBtLike.setImageResource(R.drawable.like_);
        } else {
            mBtLike.setImageResource(R.drawable.like);
        }
        mFragment.setIsLiked(aBoolean);
    }
}
