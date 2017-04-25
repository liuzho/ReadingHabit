package com.liuzh.readinghabit.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.dialog.CollectDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘晓彬 on 2017/4/22.
 */

public class QueryLikes extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "QueryLikes";
    private List<OneDay> mOneList = new ArrayList<>();
    private List<ReadData> mReadList = new ArrayList<>();

    private CollectDialog mDialog;

    private boolean mReadNoMsg;
    private boolean mOneNoMsg;

    public QueryLikes(CollectDialog dialog) {
        mDialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.showProgress(true);
    }

    @Override
    protected Void doInBackground(Void... params) {
        SQLiteDatabase db = new LikeDBHelper(App.getContext()).getWritableDatabase();
        Cursor readCursor = db.query(LikeDBHelper.READ_TABLE_NAME,
                null, null, null, null, null, null);
        if (readCursor.moveToFirst()) {
            mReadNoMsg = false;
            do {
                ReadData read = new ReadData();
                read.title = dbGetString(readCursor, LikeDBHelper.TITLE);
                read.author = dbGetString(readCursor, LikeDBHelper.AUTHOR);
                read.content = dbGetString(readCursor, LikeDBHelper.CONTENT);
                read.date.prev = dbGetString(readCursor, LikeDBHelper.PREV);
                read.date.curr = dbGetString(readCursor, LikeDBHelper.CURR);
                read.date.next = dbGetString(readCursor, LikeDBHelper.NEXT);
                mReadList.add(read);
            } while (readCursor.moveToNext());
        } else {
            mReadNoMsg = true;
        }
        readCursor.close();
        Cursor oneCursor = db.query(LikeDBHelper.ONE_TABLE_NAME,
                null, null, null, null, null, null);
        if (oneCursor.moveToFirst()) {
            mOneNoMsg = false;
            do {
                OneDay one = new OneDay();
                one.hp_author = dbGetString(oneCursor, LikeDBHelper.HP_AUTHOR);
                one.hp_content = dbGetString(oneCursor, LikeDBHelper.HP_CONTENT);
                one.hp_img_url = dbGetString(oneCursor, LikeDBHelper.HP_IMG_URL);
                one.text_authors = dbGetString(oneCursor, LikeDBHelper.TEXT_AUTHORS);
                one.image_authors = dbGetString(oneCursor, LikeDBHelper.IMAGE_AUTHORS);
                one.maketime = dbGetString(oneCursor, LikeDBHelper.MAKE_TIME);
                one.prev = dbGetString(oneCursor, LikeDBHelper.PREV);
                one.curr = dbGetString(oneCursor, LikeDBHelper.CURR);
                one.next = dbGetString(oneCursor, LikeDBHelper.NEXT);
                mOneList.add(one);
            } while (oneCursor.moveToNext());
        }else{
            mOneNoMsg = true;
        }
        oneCursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDialog.setData(mOneList, mReadList);
        mDialog.showProgress(false);
        mDialog.readNoMsg(mReadNoMsg);
        mDialog.oneNoMsg(mOneNoMsg);
    }


    /**
     * 从数据库获取String
     *
     * @param cursor     游标
     * @param columnName 列名
     * @return 结果
     */
    private String dbGetString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }
}
