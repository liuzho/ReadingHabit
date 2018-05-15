package com.liuzh.readinghabit.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
import com.liuzh.readinghabit.bean.read.Read;
import com.liuzh.readinghabit.bean.read.ReadData;
import com.liuzh.readinghabit.util.DateUtil;
import com.liuzh.readinghabit.util.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Created by 刘晓彬.
 * @date on 2017/4/18.
 * <p>
 * 没有写不出来的bug,只有不努力的码农
 */

public class ReadFragment extends BaseFragment {

    private static final String TAG = "ReadFragment";

    private TextView mTvTitle;
    private TextView mTvAuthor;
    private TextView mTvContent;

    private ReadData mData;

    private ScrollView mScrollView;

    private boolean mIsFirstEnter = true;

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_read;
    }

    @Override
    protected void fetchData() {
        fetchRead(DateUtil.getReadYMD());
    }

    private void fetchRead(String date) {

        if (mFetchListener != null) {
            mFetchListener.onBeginFetch();
        }
        RetrofitUtil.getReadCall(date)
                .enqueue(new Callback<Read>() {
                    @Override
                    public void onResponse(Call<Read> call, Response<Read> response) {
                        if (mFetchListener != null) {
                            mFetchListener.onFetched();
                        }
                        Read read = response.body();
                        if (read == null) {
                            App.showToast("get read error : no data");
                            return;
                        }
                        mData = read.data;
                        setData();
                    }


                    @Override
                    public void onFailure(Call<Read> call, Throwable t) {
                        if (mFetchListener != null) {
                            mFetchListener.onFetched();
                        }
                        onFetchFailure(t, "获取文章失败");
                    }
                });
    }

    private void setData() {
        Log.i(TAG, "setData: READ===>" + mData.date.curr);
        mTvTitle.setText(mData.title);
        mTvAuthor.setText(mData.author);
        String content = mData.content.replace("<p>", "　　");
        content = content.replace("</p>", "\n\n");
        mTvContent.setText(content);
        // 将界面滑动到顶部
        mScrollView.scrollTo(0, 0);
        mScrollView.smoothScrollTo(0, 0);
        // 如果是刚刚启动应用进来预加载的本页，则不进行收藏检验，因为One也在检验就会导致收藏按钮图片异常
        if (mIsFirstEnter) {
            mIsFirstEnter = false;
            return;
        }
        verifyLike();
    }

    @Override
    protected void initView(View rootView) {
        mTvTitle = rootView.findViewById(R.id.tv_title);
        mTvAuthor = rootView.findViewById(R.id.tv_author);
        mTvContent = rootView.findViewById(R.id.tv_content);
        mScrollView = rootView.findViewById(R.id.scrollView);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void pre() {
        if (mData == null || mData.date == null) {
            App.showToast("no data");
            return;
        }
        fetchRead(mData.date.prev);
    }

    @Override
    public void next() {
        if (mData == null || mData.date == null) {
            App.showToast("no data");
            return;
        }

        fetchRead(mData.date.next);
    }

    @Override
    public void curr() {
        String currDate = DateUtil.getReadYMD();
        if (mData.date.curr.equals(currDate)) {
            App.showToast("is ic_popup_menu_today");
            return;
        }
        fetchRead(currDate);
    }

    @Override
    public ReadData getCurrBean() {
        return mData;
    }

    @Override
    public void setLikeBean(Object o) {
        mData = (ReadData) o;
        setData();
    }
}