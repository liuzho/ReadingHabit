package com.liuzh.readinghabit.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.Update;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.db.LikeDBHelper;
import com.liuzh.readinghabit.dialog.CollectDialog;
import com.liuzh.readinghabit.fragment.BaseFragment;
import com.liuzh.readinghabit.fragment.OneFragment;
import com.liuzh.readinghabit.fragment.ReadFragment;
import com.liuzh.readinghabit.popup.HomeMenuPop;
import com.liuzh.readinghabit.task.DeleteLikeInDB;
import com.liuzh.readinghabit.util.PackageUtil;
import com.liuzh.readinghabit.util.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        fetchUpdate();

        mContext = this;

        initDialog();

        mMenuPop = new HomeMenuPop(mContext, mLikesDialog);
        mMenuPop.setOutsideTouchable(true);

        initFragment();

        initView();
    }

    private void fetchUpdate() {

        RetrofitUtil.getUpdateCall().enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {
                int vCode = PackageUtil.getVersionCode(mContext);
                showUpdateDialog(vCode, response.body());
            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {
                Log.i(TAG, "获取更新失败: " + t.getMessage());
            }
        });
    }

    private void showUpdateDialog(int v, Update update) {
        int sv = Integer.valueOf(update.versionCode);
        if (sv > v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("新版本提示")
                    .setMessage(update.info
                            + "\n\n当前版本：" + PackageUtil.getVerionName(mContext)
                            + "\n发布版本：" + update.versionName
                            + "\n发布日期：" + update.publishDate)
                    .setPositiveButton("愉快升级", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri content_url = Uri.parse("https://liuzho.com/readinghabitapp/publish.apk");
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("狠心放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
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

            @Override
            public void onReadDelete(ReadData read) {
                ReadData currBean = (ReadData) mFragmentList.get(1).getCurrBean();
                BaseFragment fragment = null;
                ImageView btLike = null;
                if (read.date.curr.equals(currBean.date.curr)) {
                    fragment = mFragmentList.get(1);
                    if (mVpMain.getCurrentItem() == 1) {
                        btLike = mMenuPop.mBtLike;
                    }
                }
                new DeleteLikeInDB(fragment, btLike, LikeDBHelper.READ_TABLE_NAME)
                        .execute(read.date.curr);
            }
        });
        mLikesDialog.setOnOneClickListener(new CollectDialog.OnOneClickListener() {
            @Override
            public void onOneCLick(OneDay one) {
                mLikesDialog.dismiss();
                mVpMain.setCurrentItem(0, true);
                mFragmentList.get(0).setLikeBean(one);
            }

            @Override
            public void onOneDelete(OneDay one) {
                OneDay currBean = (OneDay) mFragmentList.get(0).getCurrBean();
                BaseFragment fragment = null;
                ImageView btLike = null;
                if (one.curr.equals(currBean.curr)) {
                    fragment = mFragmentList.get(0);
                    if (mVpMain.getCurrentItem() == 0) {
                        btLike = mMenuPop.mBtLike;
                    }
                }
                new DeleteLikeInDB(fragment, btLike, LikeDBHelper.ONE_TABLE_NAME)
                        .execute(one.curr);
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
                Log.i(TAG, "OneFragment onBeginFetch: ");
                mMenuPop.btClickable(false);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFetched() {
                Log.i(TAG, "OneFragment onFetched: ");
                mMenuPop.btClickable(true);
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
                mMenuPop.btClickable(false);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFetched() {
                mMenuPop.btClickable(true);
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
