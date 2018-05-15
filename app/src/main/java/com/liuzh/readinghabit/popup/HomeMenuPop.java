package com.liuzh.readinghabit.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.dialog.CollectDialog;
import com.liuzh.readinghabit.fragment.BaseFragment;
import com.liuzh.readinghabit.fragment.OneFragment;
import com.liuzh.readinghabit.fragment.ReadFragment;
import com.liuzh.readinghabit.task.DeleteLikeInDB;
import com.liuzh.readinghabit.task.InsertOne2DB;
import com.liuzh.readinghabit.task.InsertRead2DB;
import com.liuzh.readinghabit.task.IsLike;
import com.liuzh.readinghabit.task.QueryLikes;
import com.liuzh.readinghabit.util.DensityUtil;
import com.liuzh.readinghabit.util.PackageUtil;

/**
 * @author Created by 刘晓彬.
 * @date on 2017/4/18.
 * <p>
 * 没有写不出来的bug,只有不努力的码农
 */

public class HomeMenuPop extends PopupWindow {

    public ImageView mIvLike;
    private ImageView mIvPre;
    private ImageView mIvNext;
    private ImageView mIvToday;
    private ImageView mIvAbout;
    private ImageView mIvCollect;

    private Context mContext;
    private CollectDialog mDialog;

    public HomeMenuPop(Context context, CollectDialog dialog) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_main_menu, null),
                ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(70));
        this.setAnimationStyle(R.style.MenuPop);
        mContext = context;
        mDialog = dialog;
        findView();
        initView();
    }

    private void findView() {
        View content = getContentView();
        mIvLike = content.findViewById(R.id.iv_like_popup_menu);
        mIvPre = content.findViewById(R.id.iv_pre_popup_menu);
        mIvNext = content.findViewById(R.id.iv_next_popup_menu);
        mIvToday = content.findViewById(R.id.iv_today_popup_menu);
        mIvAbout = content.findViewById(R.id.iv_about_popup_menu);
        mIvCollect = content.findViewById(R.id.iv_collect_popup_menu);
    }

    private void initView() {
        mIvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("关于")
                        .setMessage("每天一个\n" +
                                "每天一文\n" +
                                "满足你的文艺阅读\n\n" +
                                "联系我/反馈：354295878@qq.com\n" +
                                "当前版本：" + PackageUtil.getVersionName(mContext))
                        .setCancelable(false)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        mIvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                new QueryLikes(mDialog).execute();
            }
        });
    }

    /**
     * 设置当前显示的fragment
     * 以正确响应事件
     *
     * @param fragment 当前显示的fragment
     * @return 自身，用于链式调用
     */
    public HomeMenuPop setCurrContent(final BaseFragment fragment) {

        mIvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.pre();
            }
        });
        mIvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.next();
            }
        });
        mIvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.curr();
            }
        });
        // ReadFragment、OneFragment不同处理
        if (fragment instanceof ReadFragment) {
            setReadClick(fragment);
        } else {
            setOneClick(fragment);
        }
        // 用于链式调用方法，返回自身
        return this;
    }

    /**
     * 当前fragment是read时，进行点击事件设定
     *
     * @param fragment readFragment
     */
    private void setReadClick(final BaseFragment fragment) {
        mIvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadData readData = ((ReadFragment) fragment).getCurrBean();
                if (fragment.isLiked()) {
                    // 已收藏，从数据库删除
                    new DeleteLikeInDB(fragment, mIvLike, LikeDBHelper.READ_TABLE_NAME)
                            .execute(readData.date.curr);
                } else {
                    // 未收藏，向数据库添加
                    new InsertRead2DB(fragment, mIvLike).execute(readData);
                }
            }
        });
    }

    /**
     * 当前fragment是one时，进行点击事件设定
     *
     * @param fragment oneFragment
     */
    private void setOneClick(final BaseFragment fragment) {
        mIvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneDay oneDay = ((OneFragment) fragment).getCurrBean();
                if (fragment.isLiked()) {
                    // 已收藏，从数据库删除
                    new DeleteLikeInDB(fragment, mIvLike, LikeDBHelper.ONE_TABLE_NAME)
                            .execute(oneDay.curr);
                } else {
                    // 未收藏，向数据库添加
                    new InsertOne2DB(fragment, mIvLike).execute(oneDay);
                }
            }
        });
    }

    /**
     * 验证read是否已经在数据库中=>是否已经收藏
     *
     * @param fragment readFragment
     */
    public void verifyRead(BaseFragment fragment) {
        ReadData readData = ((ReadFragment) fragment).getCurrBean();
        if (readData == null) {
            return;
        }
        new IsLike(fragment, mIvLike, LikeDBHelper.READ_TABLE_NAME).execute(readData.date.curr);
    }

    /**
     * 验证one是否已经在数据库中=>是否已经收藏
     *
     * @param fragment oneFragment
     */
    public void verifyOne(BaseFragment fragment) {
        OneDay oneDay = ((OneFragment) fragment).getCurrBean();
        if (oneDay == null) {
            return;
        }
        new IsLike(fragment, mIvLike, LikeDBHelper.ONE_TABLE_NAME).execute(oneDay.curr);
    }

    public void btClickable(boolean clickable) {
        mIvPre.setClickable(clickable);
        mIvToday.setClickable(clickable);
        mIvNext.setClickable(clickable);
        mIvLike.setClickable(clickable);
    }

}
