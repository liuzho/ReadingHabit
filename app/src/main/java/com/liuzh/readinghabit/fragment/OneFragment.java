package com.liuzh.readinghabit.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.bean.one.One;
import com.liuzh.readinghabit.bean.one.OneDay;
import com.liuzh.readinghabit.util.DateUtil;
import com.liuzh.readinghabit.util.RetrofitUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by 刘晓彬 on 2017/4/18.
 */

public class OneFragment extends BaseFragment {
    private static final String TAG = "OneFragment";
    private ImageView mIvImg;
    private TextView mTvPicInfo;
    private TextView mTvTextInfo;
    private TextView mTvText;

    private List<OneDay> mDataList = new ArrayList<>();
    private int mCurrentPos = -1;
    private String mCurrentDate;

    @Override
    protected void fetchData() {
        mCurrentDate = DateUtil.getOneYMD();
        fetchOne(mCurrentDate);
    }

    private void fetchOne(String date) {
        RetrofitUtil.getOneCall(date).enqueue(new Callback<One>() {
            @Override
            public void onResponse(Call<One> call, Response<One> response) {
                mDataList.addAll(response.body().data);
                if (mCurrentPos == -1) {
                    mCurrentPos = 0;
                }
                setDate(mDataList.get(mCurrentPos));
            }

            @Override
            public void onFailure(Call<One> call, Throwable t) {
                onFetchFailure(t, "获取ONE失败");
            }
        });
    }

    private void setDate(OneDay date) {
        Picasso.with(getActivity())
                .load(date.hp_img_url)
                .into(mIvImg);
        mTvPicInfo.setText(date.hp_author + " | " + date.image_authors);
        mTvText.setText(date.hp_content);
        mTvTextInfo.setText("《" + date.text_authors + "》");
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_one;
    }

    @Override
    protected void initView(View rootView) {
        mIvImg = (ImageView) rootView.findViewById(R.id.iv_img);
        mTvPicInfo = (TextView) rootView.findViewById(R.id.tv_picInfo);
        mTvTextInfo = (TextView) rootView.findViewById(R.id.tv_textInfo);
        mTvText = (TextView) rootView.findViewById(R.id.tv_text);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void pre() {
        if (mCurrentPos == -1) {
            return;
        } else if (mCurrentPos == mDataList.size() - 1) {
            fetchOne(DateUtil.getOneYMD());
        }
        mCurrentPos = mCurrentPos + 1;

        setDate(mDataList.get(mCurrentPos));
    }

    @Override
    public void next() {
        if (mCurrentPos == -1 || mCurrentPos == 0) {
            return;
        }
        setDate(mDataList.get(--mCurrentPos));
    }

    @Override
    public void curr() {
        mCurrentPos = 0;
        setDate(mDataList.get(mCurrentPos));
    }
}
