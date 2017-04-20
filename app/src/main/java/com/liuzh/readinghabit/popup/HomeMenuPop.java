package com.liuzh.readinghabit.popup;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.fragment.BaseFragment;
import com.liuzh.readinghabit.util.DateUtil;
import com.liuzh.readinghabit.util.DensityUtil;

/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class HomeMenuPop extends PopupWindow {

    private View mBtLike;
    private View mBtPre;
    private View mBtNext;
    private View mBtToday;


    public HomeMenuPop(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_main_menu, null),
                ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(70));
        this.setAnimationStyle(R.style.MenuPop);
        View content = getContentView();
        mBtLike = content.findViewById(R.id.like);
        mBtPre = content.findViewById(R.id.pre);
        mBtNext = content.findViewById(R.id.next);
        mBtToday = content.findViewById(R.id.today);
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

        initBtnVisibility(fragment);

        return this;
    }

    public void initBtnVisibility(Fragment fragment){
        String curr = ((BaseFragment) fragment).getCurrDate();
        String oneYMD = DateUtil.getOneYMD();
        String readYMD = DateUtil.getReadYMD();

        if (curr.equals(oneYMD) || curr.equals(readYMD)) {
            mBtToday.setVisibility(View.GONE);
            mBtNext.setVisibility(View.GONE);
        } else {
            mBtToday.setVisibility(View.VISIBLE);
            mBtNext.setVisibility(View.VISIBLE);
        }
    }


}
