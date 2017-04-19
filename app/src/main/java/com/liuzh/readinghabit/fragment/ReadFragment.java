package com.liuzh.readinghabit.fragment;

import android.view.View;
import android.widget.TextView;

import com.liuzh.readinghabit.R;
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

    private TextView mTvTitle;
    private TextView mTvAuthor;
    private TextView mTvContent;

    private ReadData mData;

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_read;
    }

    @Override
    protected void fetchData() {
        fetchRead(DateUtil.getReadYMD());
    }

    private void fetchRead(String date) {
        RetrofitUtil.getReadCall(date).enqueue(new Callback<Read>() {
            @Override
            public void onResponse(Call<Read> call, Response<Read> response) {
                mData = response.body().data;
                setData();
            }

            @Override
            public void onFailure(Call<Read> call, Throwable t) {
                onFetchFailure(t, "获取文章失败");
            }
        });
    }

    private void setData() {
        mTvTitle.setText(mData.title);
        mTvAuthor.setText(mData.author);
        String content = mData.content.replace("<p>", "　　");
        content = content.replace("</p>", "\n\n");
        mTvContent.setText(content);
    }

    @Override
    protected void initView(View rootView) {
        mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        mTvAuthor = (TextView) rootView.findViewById(R.id.tv_author);
        mTvContent = (TextView) rootView.findViewById(R.id.tv_content);
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
        fetchRead(mData.date.next);
    }

    @Override
    public void curr() {
        fetchRead(DateUtil.getReadYMD());
    }
}
