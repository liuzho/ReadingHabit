package com.liuzh.readinghabit.fragment;

import android.util.Log;
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

    @Override
    protected void fetchData() {
//        fetchOne(DateUtil.getOneYMD());
        fetchOne("2017-2-1");
    }

    /**
     * 请求One的数据
     * 注：不论请求的是具体哪一天，返回的是一整个月的数据
     *
     * @param date 请求url中携带的日期
     */
    private void fetchOne(String date) {
        RetrofitUtil.getOneCall(date).enqueue(new Callback<One>() {
            @Override
            public void onResponse(Call<One> call, Response<One> response) {
//                mDataList.addAll(0, addDate(response.body().data));

                firstAddDate(response.body().data);

                if (mCurrentPos == -1) {
                    mCurrentPos = 0;
                }
                mDataList = response.body().data;
                setDate(mDataList.get(mCurrentPos));

                if (mFetchedListener != null) {
                    mFetchedListener.onFetched();
                }
            }

            @Override
            public void onFailure(Call<One> call, Throwable t) {
                onFetchFailure(t, "获取ONE失败");
            }
        });
    }

    /**
     * 第一次获取并填充数据
     *
     * @param data 第一次获取到的数据
     */
    private void firstAddDate(List<OneDay> data) {
        // maketime的格式：2017-4-20 22:26:23 故以此分割得到日期的数组
        String[] dateStrArr = data.get(0).maketime.split(" ")[0].split("-");
        int year = Integer.valueOf(dateStrArr[0]);
        int mouth = Integer.valueOf(dateStrArr[1]);
        int day = Integer.valueOf(dateStrArr[2]);
        // 字符串中年、月、日的分隔符
        String separator = "-";
        // 为每一天的数据添加上curr、prev、next字段
        for (int i = 0; i < data.size(); i++) {
            OneDay oneDay = data.get(i);
            oneDay.curr = year + separator + mouth + separator + day;
            // 如果是1号，处理月份变动
            if (day == 1) {
                // 因为不论请求具体哪一天，返回的都是整个月的数据
                // 所以如果是1号的数据则直接月份减1，并默认prev字段为该月1号
                // 如果当前月份是1月，则年份减1，并默认prev字段为该年12月1号
                if (mouth == 1) {
                    oneDay.prev = (year - 1) + separator + 12 + separator + 1;
                } else {
                    oneDay.prev = year + separator + (mouth - 1) + separator + 1;
                }
            } else {
                oneDay.prev = year + separator + mouth + separator + (day - 1);
            }
            if (day >= 28) {
                oneDay.next = year + separator + mouth + separator + (day + 1);
                switch (mouth) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                        if (day == 31) {
                            oneDay.next = year + separator + (mouth + 1) + separator + 1;
                        }
                        break;
                    case 12:
                        if (day == 31) {
                            oneDay.next = (year + 1) + separator + 1 + separator + 1;
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        if (day == 30) {
                            oneDay.next = year + separator + (mouth + 1) + separator + 1;
                        }
                        break;
                    case 2:
                        boolean runYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                        if ((day == 29 && runYear) || (day == 28 && !runYear)) {
                            oneDay.next = year + separator + (mouth + 1) + separator + 1;
                        }
                        break;
                    default:
                        break;
                }
            } else {
                oneDay.next = year + separator + mouth + separator + (day + 1);
            }
            day--;
            Log.i(TAG, "firstAddDate: " + oneDay.prev + "<==>" + oneDay.curr + "<==>" + oneDay.next);
        }

    }

    /**
     * 情趣数据完成后设置到界面上
     *
     * @param date 显示在当前界面上的数据
     */
    private void setDate(OneDay date) {
        Picasso.with(getActivity())
                .load(date.hp_img_url)
                .into(mIvImg);
        mTvPicInfo.setText(date.hp_author + " | " + date.image_authors);
        mTvText.setText(date.hp_content);
        mTvTextInfo.setText(date.text_authors);
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


    /**
     * 跳转到上一天的内容
     */
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

    /**
     * 跳转到下一天的内容
     */
    @Override
    public void next() {
        if (mCurrentPos == -1 || mCurrentPos == 0) {
            return;
        }
        setDate(mDataList.get(--mCurrentPos));
    }

    /**
     * 跳转到今天当天的内容
     */
    @Override
    public void curr() {
        mCurrentPos = 0;
        setDate(mDataList.get(mCurrentPos));
    }

    /**
     * 获取当前显示内容所属的日期
     *
     * @return 当前显示内容所属的日期
     */
    @Override
    public String getCurrDate() {
        return mDataList.get(mCurrentPos).curr;
    }
}
