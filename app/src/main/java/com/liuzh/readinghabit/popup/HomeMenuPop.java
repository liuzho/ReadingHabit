package com.liuzh.readinghabit.popup;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.fragment.BaseFragment;
import com.liuzh.readinghabit.fragment.OneFragment;
import com.liuzh.readinghabit.fragment.ReadFragment;
import com.liuzh.readinghabit.task.InsertRead2DB;
import com.liuzh.readinghabit.task.ReadIsLiked;
import com.liuzh.readinghabit.util.DensityUtil;

/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class HomeMenuPop extends PopupWindow {

    private ImageView mBtLike;
    private ImageView mBtPre;
    private ImageView mBtNext;
    private ImageView mBtToday;

    private Context mContext;

    private boolean mAddedReadListener = false;


    public HomeMenuPop(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_main_menu, null),
                ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(70));
        this.setAnimationStyle(R.style.MenuPop);
        mContext = context;
        View content = getContentView();
        mBtLike = (ImageView) content.findViewById(R.id.like);
        mBtPre = (ImageView) content.findViewById(R.id.pre);
        mBtNext = (ImageView) content.findViewById(R.id.next);
        mBtToday = (ImageView) content.findViewById(R.id.today);
    }

    public HomeMenuPop setFragment(final Fragment fragment) {

        mBtPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseFragment) fragment).pre();
            }
        });
        mBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseFragment) fragment).next();
            }
        });
        mBtToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseFragment) fragment).curr();
            }
        });
        // ReadFragment、OneFragment不同处理
        if (fragment instanceof ReadFragment) {
            initRead(fragment);
        } else {
            initOne(fragment);
        }
        // 用于链式调用方法，返回自身
        return this;
    }


    private void initRead(final Fragment fragment) {
        if (!mAddedReadListener) {
            // 如果没有添加过监听，则添加，添加过则不添加
            ((ReadFragment) fragment).setOnFetchListener(new BaseFragment.OnFetchListener() {
                @Override
                public void onBeginFetch() {

                }

                @Override
                public void onFetching() {

                }

                @Override
                public void onFetched() {
                    // 检测是否已经收藏过
                    new ReadIsLiked(mBtLike).execute(((
                            ReadFragment) fragment).getCurrBean().date.curr);
                }
            });
            mAddedReadListener = true;
        }

        // 点击收藏，将内容写入数据库
        mBtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertRead2DB insertRead2DB = new InsertRead2DB(mBtLike);
                insertRead2DB.execute(((ReadFragment) fragment).getCurrBean());
            }
        });
    }

    private void initOne(Fragment fragment) {
        final OneDay oneDay = ((OneFragment) fragment).getCurrBean();
        // 点击收藏，将内容写入数据库
        mBtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeDBHelper dbHelper = new LikeDBHelper(mContext);
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
                mBtLike.setImageResource(R.drawable.like_);
            }
        });
    }


}
