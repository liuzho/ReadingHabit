package com.liuzh.readinghabit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Created by 刘晓彬.
 * @date on 2017/4/21.
 * <p>
 * 没有写不出来的bug,只有不努力的码农
 */

public class LikeDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Like.db";
    private static final int DB_VERSION = 1;

    public static final String READ_TABLE_NAME = "readLike";
    public static final String ID = "_id";
    public static final String AUTHOR = "author";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    public static final String PREV = "prev";
    public static final String CURR = "curr";
    public static final String NEXT = "next";

    public static final String ONE_TABLE_NAME = "oneLike";
    public static final String HP_IMG_URL = "hp_img_url";
    public static final String HP_AUTHOR = "hp_author";
    public static final String HP_CONTENT = "hp_content";
    public static final String IMAGE_AUTHORS = "image_authors";
    public static final String TEXT_AUTHORS = "text_authors";
    public static final String MAKE_TIME = "maketime";


    private static final String CREATE_READ = "create table " +
            READ_TABLE_NAME + " (" +
            ID + " integer primary key autoincrement," +
            AUTHOR + " text," +
            TITLE + " text," +
            CONTENT + " text," +
            PREV + " text," +
            CURR + " text," +
            NEXT + " text)";

    private static final String CREATE_ONE = "create table " +
            ONE_TABLE_NAME + " (" +
            ID + " integer primary key autoincrement," +
            HP_IMG_URL + " text," +
            HP_AUTHOR + " text," +
            HP_CONTENT + " text," +
            IMAGE_AUTHORS + " text," +
            TEXT_AUTHORS + " text," +
            MAKE_TIME + " text," +
            PREV + " text," +
            CURR + " text," +
            NEXT + " text)";

    public LikeDBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    private LikeDBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_READ);
        db.execSQL(CREATE_ONE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
