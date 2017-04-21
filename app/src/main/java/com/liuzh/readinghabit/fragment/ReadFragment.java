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
 * Created by 刘晓彬 on 2017/4/18.
 */

public class ReadFragment extends BaseFragment {

    private static final String TAG = "ReadFragment";

    private TextView mTvTitle;
    private TextView mTvAuthor;
    private TextView mTvContent;

    private ReadData mData;

    private ScrollView mScrollView;

    private Call<Read> mCall;

    private VerifyLike mVerifyLike;

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
            mFetchListener.onFetching();
        }
        mCall = RetrofitUtil.getReadCall(date);
        mCall.enqueue(new Callback<Read>() {
            @Override
            public void onResponse(Call<Read> call, Response<Read> response) {
                mData = response.body().data;
                setData();
                if (mFetchListener != null) {
                    mFetchListener.onFetched();
                }
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
        mScrollView.smoothScrollTo(0, 0);
        verifyLike();
    }

    @Override
    protected void initView(View rootView) {
        mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        mTvAuthor = (TextView) rootView.findViewById(R.id.tv_author);
        mTvContent = (TextView) rootView.findViewById(R.id.tv_content);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void pre() {
        fetchRead(mData.date.prev);
    }

    @Override
    public void next() {
        if (mData.date.curr.equals(DateUtil.getReadYMD())) {
            App.showToast("no next");
            return;
        }
        fetchRead(mData.date.next);
    }

    @Override
    public void curr() {
        String currDate = DateUtil.getReadYMD();
        if (mData.date.curr.equals(currDate)) {
            App.showToast("is today");
            return;
        }
        fetchRead(currDate);
    }

    @Override
    public ReadData getCurrBean() {
        return mData;
    }

    public void verifyLike() {
        if (mVerifyLike != null) {
            mVerifyLike.verifyLike();
        }
    }

    public interface VerifyLike {
        void verifyLike();
    }

    public void setVerfyLike(VerifyLike verifyLike) {
        mVerifyLike = verifyLike;
    }
}