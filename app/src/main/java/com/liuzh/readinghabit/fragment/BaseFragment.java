package com.liuzh.readinghabit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuzh.readinghabit.application.App;

import java.util.List;


/**
 * Created by 刘晓彬 on 2017/4/18.
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;

    protected OnFetchListener mFetchListener;

    protected VerifyLike mVerifyLike;

    protected boolean mIsLiked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getRootViewId(), null);
            initView(mRootView);
            initData();
        }
        return mRootView;
    }

    protected abstract int getRootViewId();

    protected abstract void initView(View rootView);

    protected abstract void initData();

    protected void fetchData() {
    }

    protected void onFetchFailure(Throwable t, String msg) {
        App.showToast(t.getMessage());
        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(
                android.R.id.content), msg, 2000)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchData();
                    }
                }).show();
    }

    /**
     * 上一天的内容
     */
    public abstract void pre();

    /**
     * 下一天的内容
     */
    public abstract void next();

    /**
     * 回到今天的内容
     */
    public abstract void curr();

    public Object getCurrBean() {
        return null;
    }

    public interface OnFetchListener {
        void onBeginFetch();

        void onFetched();
    }

    public void setOnFetchListener(OnFetchListener listener) {
        this.mFetchListener = (listener);
    }

    protected void verifyLike() {
        if (mVerifyLike != null) {
            mVerifyLike.verifyLike();
        }
    }

    public interface VerifyLike {
        void verifyLike();
    }

    public void setVerifyLike(VerifyLike verifyLike) {
        mVerifyLike = verifyLike;
    }


    public void setIsLiked(boolean isLiked) {
        mIsLiked = isLiked;
    }

    public boolean isLiked() {
        return mIsLiked;
    }


    public void setLikeBean(Object o) {
    }


}

