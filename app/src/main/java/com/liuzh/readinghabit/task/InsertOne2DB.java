package com.liuzh.readinghabit.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.fragment.BaseFragment;

/**
 * Created by 刘晓彬 on 2017/4/21.
 */

public class InsertOne2DB extends AsyncTask<OneDay, Void, Void> {

    private ImageView mBtLike;
    private BaseFragment mFragment;

    public InsertOne2DB(BaseFragment fragment,ImageView btLike) {
        mFragment = fragment;
        mBtLike = btLike;
    }

    @Override
    protected Void doInBackground(OneDay... params) {
        OneDay oneDay = params[0];
        LikeDBHelper dbHelper = new LikeDBHelper(App.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LikeDBHelper.HP_AUTHOR, oneDay.hp_author);
        values.put(LikeDBHelper.HP_CONTENT, oneDay.hp_content);
        values.put(LikeDBHelper.HP_IMG_URL, oneDay.hp_img_url);
        values.put(LikeDBHelper.IMAGE_AUTHORS, oneDay.image_authors);
        values.put(LikeDBHelper.TEXT_AUTHORS, oneDay.text_authors);
        values.put(LikeDBHelper.MAKE_TIME, oneDay.maketime);
        values.put(LikeDBHelper.PREV, oneDay.prev);
        values.put(LikeDBHelper.CURR, oneDay.curr);
        values.put(LikeDBHelper.NEXT, oneDay.next);
        db.insert(LikeDBHelper.ONE_TABLE_NAME, null, values);
        db.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mBtLike.setImageResource(R.drawable.like_);
        mFragment.setIsLiked(true);
        App.showToast("收藏成功");
    }
}
