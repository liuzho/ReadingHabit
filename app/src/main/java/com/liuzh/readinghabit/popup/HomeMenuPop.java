package com.liuzh.readinghabit.popup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.fragment.BaseFragment;
import com.liuzh.readinghabit.fragment.OneFragment;
import com.liuzh.readinghabit.fragment.ReadFragment;
import com.liuzh.readinghabit.task.DeleteLikeFromDB;
import com.liuzh.readinghabit.task.InsertOne2DB;
import com.liuzh.readinghabit.task.InsertRead2DB;
import com.liuzh.readinghabit.task.IsLike;
import com.liuzh.readinghabit.util.DensityUtil;

/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class HomeMenuPop extends PopupWindow {

    private static final String TAG = "HomeMenuPop";
    private ImageView mBtLike;
    private ImageView mBtPre;
    private ImageView mBtNext;
    private ImageView mBtToday;

    private Context mContext;

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

    public HomeMenuPop setFragment(final BaseFragment fragment) {

        mBtPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (fragment).pre();
            }
        });
        mBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (fragment).next();
            }
        });
        mBtToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (fragment).curr();
            }
        });
        // ReadFragment、OneFragment不同处理
        if (fragment instanceof ReadFragment) {
            mBtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReadData readData = ((ReadFragment) fragment).getCurrBean();
                    if ((fragment).isLiked()) {
                        // 已收藏，从数据库删除
                        new DeleteLikeFromDB(fragment, mBtLike, LikeDBHelper.READ_TABLE_NAME)
                                .execute(readData.date.curr);
                    } else {
                        // 未收藏，向数据库添加
                        new InsertRead2DB(fragment, mBtLike).execute(readData);
                    }
                }
            });
        } else {
            // 点击收藏，将内容写入数据库
            mBtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OneDay oneDay = ((OneFragment) fragment).getCurrBean();
                    if (fragment.isLiked()) {
                        // 已收藏，从数据库删除
                        new DeleteLikeFromDB(fragment, mBtLike, LikeDBHelper.ONE_TABLE_NAME)
                                .execute(oneDay.curr);
                    } else {
                        // 未收藏，向数据库添加
                        new InsertOne2DB(fragment, mBtLike).execute(oneDay);
                    }
                }
            });
        }
        // 用于链式调用方法，返回自身
        return this;
    }


    public void verifyRead(BaseFragment fragment) {
        ReadData readData = ((ReadFragment) fragment).getCurrBean();
        if (readData == null) {
            return;
        }
        new IsLike(fragment, mBtLike, LikeDBHelper.READ_TABLE_NAME).execute(readData.date.curr);
    }

    public void verifyOne(BaseFragment fragment) {
        OneDay oneDay = ((OneFragment) fragment).getCurrBean();
        if (oneDay == null) {
            return;
        }
        new IsLike(fragment, mBtLike, LikeDBHelper.ONE_TABLE_NAME).execute(oneDay.curr);
    }

}
