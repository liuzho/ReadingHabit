package com.liuzh.readinghabit.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.fragment.BaseFragment;
import com.liuzh.readinghabit.fragment.OneFragment;
import com.liuzh.readinghabit.fragment.ReadFragment;
import com.liuzh.readinghabit.popup.HomeMenuPop;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;

    private ViewPager mVpMain;

    private List<Fragment> mFragmentList;

    private int mPagePos = 0;

    private HomeMenuPop mMenuPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initFragment();

        mVpMain = (ViewPager) findViewById(R.id.vp_main);
        mVpMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }
        });

        mVpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagePos = position;
                if (mMenuPop != null) {
                    mMenuPop.setFragment(mFragmentList.get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.bt_showMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenuPop == null) {
                    mMenuPop = new HomeMenuPop(mContext);
                    mMenuPop.setOutsideTouchable(true);
                }
                mMenuPop.setFragment(mFragmentList.get(mPagePos))
                        .showAtLocation(getWindow().getDecorView().findViewById(
                                android.R.id.content), Gravity.BOTTOM, 0, 0);
            }
        });
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();

        final OneFragment oneFragment = new OneFragment();
        oneFragment.setOnFetchedListener(new BaseFragment.OnFetchedListener() {
            @Override
            public void onFetched() {
                if (mMenuPop != null) {
                    mMenuPop.initBtnVisibility(oneFragment);
                }
            }
        });
        mFragmentList.add(oneFragment);

        final ReadFragment readFragment = new ReadFragment();
        readFragment.setOnFetchedListener(new BaseFragment.OnFetchedListener() {
            @Override
            public void onFetched() {
                if (mMenuPop != null) {
                    mMenuPop.initBtnVisibility(readFragment);
                }
            }
        });
        mFragmentList.add(readFragment);

    }


}
