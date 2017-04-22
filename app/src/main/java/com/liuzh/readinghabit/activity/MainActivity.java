package com.liuzh.readinghabit.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.dialog.CollectDialog;
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

    private List<BaseFragment> mFragmentList;

    private int mPagePos = 0;

    private HomeMenuPop mMenuPop;

    private ProgressBar mProgressBar;

    private ImageView mIvShowMenu;

    private CollectDialog mLikesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initDialog();

        mMenuPop = new HomeMenuPop(mContext, mLikesDialog);
        mMenuPop.setOutsideTouchable(true);

        initFragment();

        initView();
    }

    private void initDialog() {
        mLikesDialog = new CollectDialog(mContext);
        mLikesDialog.setOnReadClickListener(new CollectDialog.OnReadClickListener() {
            @Override
            public void onReadClick(ReadData read) {
                mLikesDialog.dismiss();
                mVpMain.setCurrentItem(1, true);
                mFragmentList.get(1).setLikeBean(read);
            }
        });
        mLikesDialog.setOnOneClickListener(new CollectDialog.OnOneClickListener() {
            @Override
            public void onOneCLick(OneDay one) {
                mLikesDialog.dismiss();
                mVpMain.setCurrentItem(0, true);
                mFragmentList.get(0).setLikeBean(one);
            }
        });
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

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
                mMenuPop.setCurrContent(mFragmentList.get(position));
                if (position == 0) {
                    mMenuPop.verifyOne(mFragmentList.get(0));
                } else {
                    mMenuPop.verifyRead(mFragmentList.get(1));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mIvShowMenu = (ImageView) findViewById(R.id.bt_showMenu);

        mIvShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuPop.setCurrContent(mFragmentList.get(mPagePos))
                        .showAtLocation(getWindow().getDecorView().findViewById(
                                android.R.id.content), Gravity.BOTTOM, 0, 0);
            }
        });
    }


    private void initFragment() {
        mFragmentList = new ArrayList<>();

        final OneFragment oneFragment = new OneFragment();

        oneFragment.setOnFetchListener(new BaseFragment.OnFetchListener() {
            @Override
            public void onBeginFetch() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFetched() {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        oneFragment.setVerifyLike(new BaseFragment.VerifyLike() {
            @Override
            public void verifyLike() {
                mMenuPop.verifyOne(oneFragment);
            }
        });
        mFragmentList.add(oneFragment);

        final ReadFragment readFragment = new ReadFragment();
        readFragment.setOnFetchListener(new BaseFragment.OnFetchListener() {
            @Override
            public void onBeginFetch() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFetched() {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        readFragment.setVerifyLike(new ReadFragment.VerifyLike() {
            @Override
            public void verifyLike() {
                mMenuPop.verifyRead(readFragment);
            }
        });
        mFragmentList.add(readFragment);
    }


    @Override
    public void onBackPressed() {
        if (mMenuPop.isShowing()) {
            mMenuPop.dismiss();
        } else {
            finish();
        }
    }
}
