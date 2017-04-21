package com.liuzh.readinghabit.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.db.LikeDBHelper;

/**
 * Created by 刘晓彬 on 2017/4/21.
 */

public class InsertRead2DB extends AsyncTask<ReadData, Void, Void> {

    private static final String TAG = "InsertRead2DB";
    private ImageView mBtLike;

    public InsertRead2DB(ImageView btnLike) {
        mBtLike = btnLike;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(ReadData... params) {
        ReadData readData = params[0];
        LikeDBHelper dbHelper = new LikeDBHelper(App.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(LikeDBHelper.AUTHOR, readData.author);
        values.put(LikeDBHelper.TITLE, readData.title);
        values.put(LikeDBHelper.CONTENT, readData.content);
        values.put(LikeDBHelper.PREV, readData.date.prev);
        values.put(LikeDBHelper.CURR, readData.date.curr);
        values.put(LikeDBHelper.NEXT, readData.date.next);
        db.insert(LikeDBHelper.READ_TABLE_NAME, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG, "onPostExecute: ");
        mBtLike.setImageResource(R.drawable.like_);
    }
}
