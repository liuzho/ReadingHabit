package com.liuzh.readinghabit.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.fragment.BaseFragment;

/**
 * Created by 刘晓彬 on 2017/4/22.
 */

public class DeleteLikeInDB extends AsyncTask<String, Void, Void> {

    private String mTableName;
    private BaseFragment mFragment;
    private ImageView mBtLike;

    public DeleteLikeInDB(BaseFragment fragment, ImageView btLike, String tableName) {
        mFragment = fragment;
        mBtLike = btLike;
        mTableName = tableName;
    }

    @Override
    protected Void doInBackground(String... params) {
        String curr = params[0];
        SQLiteDatabase db = new LikeDBHelper(App.getContext()).getWritableDatabase();
        db.delete(mTableName, LikeDBHelper.CURR + " = ?", new String[]{curr});
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mFragment != null) {
            mFragment.setIsLiked(false);
        }
        if (mBtLike != null) {
            mBtLike.setImageResource(R.drawable.ic_popup_menu_like_nor);
        }
        App.showToast("取消收藏");
    }
}
