package com.liuzh.readinghabit.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.liuzh.readinghabit.R;
import com.liuzh.readinghabit.application.App;
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

    // 字符串中年、月、日的分隔符
    private static final String SEPARATOR = "-";

    private ImageView mIvImg;
    private TextView mTvPicInfo;
    private TextView mTvTextInfo;
    private TextView mTvText;

    private ScrollView mScrollView;

    private Call<One> mCall;


    /**
     * 新获取的数据是否由按next请求得到
     * 如果是，则要调整指针到list的尾部，以确保日期的连贯性
     */
    private boolean mIsNextNewList = false;

    private List<OneDay> mDataList = new ArrayList<>();
    private int mCurrentPos = -1;

    @Override
    protected void fetchData() {
        fetchOne(DateUtil.getOneYMD());
//        fetchOne("2016-11-1");
    }

    /**
     * 请求One的数据
     * 注：不论请求的是具体哪一天，返回的是一整个月的数据
     *
     * @param date 请求url中携带的日期
     */
    private void fetchOne(String date) {
        // 调用获取监听
        if (mFetchListener != null) {
            mFetchListener.onBeginFetch();
        }
        mCall = RetrofitUtil.getOneCall(date);
        mCall.enqueue(new Callback<One>() {
            @Override
            public void onResponse(Call<One> call, Response<One> response) {
                mDataList = response.body().data;
                // 填充curr、prev、next
                feedDate(mDataList);
                if (mIsNextNewList) {
                    // 如果是按后一天获取到的数据，则将指示器定位到1号
                    mCurrentPos = mDataList.size() - 1;
                    mIsNextNewList = false;
                } else {
                    // 否则定位到月末
                    mCurrentPos = 0;
                }
                // 设置界面数据
                setDate(mDataList.get(mCurrentPos));
                // 调用获取结束监听
                if (mFetchListener != null) {
                    mFetchListener.onFetched();
                }
            }

            @Override
            public void onFailure(Call<One> call, Throwable t) {
                // 调用获取结束监听
                if (mFetchListener != null) {
                    mFetchListener.onFetched();
                }
                onFetchFailure(t, "获取ONE失败");
            }
        });
    }

    /**
     * 第一次获取并填充数据
     *
     * @param data 第一次获取到的数据
     */
    private void feedDate(List<OneDay> data) {
        // maketime的格式：2017-04-20 22:26:23 故以此分割得到日期的数组
        String[] dateStrArr = data.get(0).maketime.split(" ")[0].split(SEPARATOR);
        int year = Integer.valueOf(dateStrArr[0]);
        int mouth = Integer.valueOf(dateStrArr[1]);
        int day = Integer.valueOf(dateStrArr[2]);
        // 为每一天的数据添加上curr、prev、next字段
        for (int i = 0; i < data.size(); i++) {
            OneDay oneDay = data.get(i);
            // feedCurr
            oneDay.curr = year + SEPARATOR + add0(mouth) + SEPARATOR + add0(day);
            // feedPrev
            feedPrev(oneDay, year, mouth, day);
            // feedNext
            feedNext(oneDay, year, mouth, day);

            day--;
//            Log.i(TAG, oneDay.prev + "<==>" + oneDay.curr + "<==>" + oneDay.next);
        }
    }

    /**
     * 如果个位数，要在前面加个0
     * @param num 检验的数字
     * @return 加完0后的数字
     */
    private String add0(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }

    /**
     * 填充next字段
     * 月末需要处理月份变动
     * 如果是月末，则月份加1，并默认next字段为该月1号
     * 对12月、2月进行判断处理
     *
     * @param oneDay bean对象
     * @param year   年份
     * @param mouth  月份
     * @param day    号
     */
    private void feedNext(OneDay oneDay, int year, int mouth, int day) {
        if (day >= 28) {
            switch (mouth) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                    if (day == 31) {
                        mouth = mouth + 1;
                        day = 1;
                    }
                    break;
                case 12:
                    if (day == 31) {
                        year = year + 1;
                        mouth = 1;
                        day = 1;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    if (day == 30) {
                        mouth = mouth + 1;
                        day = 1;
                    }
                    break;
                case 2:
                    boolean isLeapYear = DateUtil.isLeapYear(year);
                    if ((day == 29 && isLeapYear) || (day == 28 && !isLeapYear)) {
                        mouth = mouth + 1;
                        day = 1;
                    }
                    break;
                default:
                    break;
            }
        } else {
            day = day + 1;
        }
        oneDay.next = year + SEPARATOR + add0(mouth) + SEPARATOR + add0(day);
    }

    /**
     * 填充prev字段
     * 1号需要处理月份变动
     * 因为不论请求具体哪一天，返回的都是整个月的数据
     * 所以如果是1号的数据则直接月份减1，并默认prev字段为该月1号
     * 如果当前月份是1月，则年份减1，并默认prev字段为该年12月1号
     *
     * @param oneDay bean对象
     * @param year   年份
     * @param mouth  月份
     * @param day    号
     */
    private void feedPrev(OneDay oneDay, int year, int mouth, int day) {
        if (day == 1 && mouth == 1) {
            // 1月1号
            year = year - 1;
            mouth = 12;
            day = 1;
        } else if (day == 1) {
            // 1号但非1月1号
            mouth = mouth - 1;
            day = 1;
        } else {
            // 不是1号
            day = day - 1;
        }
        oneDay.prev = year + SEPARATOR + add0(mouth) + SEPARATOR + add0(day);
    }

    /**
     * 情趣数据完成后设置到界面上
     *
     * @param date 显示在当前界面上的数据
     */
    private void setDate(OneDay date) {
        Log.i(TAG, "setDate: ONE===>" + date.curr);
        Picasso.with(getActivity())
                .load(date.hp_img_url)
                .placeholder(R.drawable.placeholder)
                .into(mIvImg);
        String picInfo;
        if (TextUtils.isEmpty(date.image_authors)) {
            picInfo = date.hp_author;
        } else {
            picInfo = date.hp_author + " | " + date.image_authors;
        }
        mTvPicInfo.setText(picInfo);
        mTvText.setText(date.hp_content);
        mTvTextInfo.setText(date.text_authors);
        // 将界面滑动到顶部
        mScrollView.scrollTo(0, 0);
        mScrollView.smoothScrollTo(0, 0);
        verifyLike();
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
        mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
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
            Log.i(TAG, "pre: new pre list");
            fetchOne(mDataList.get(mCurrentPos).prev);
            return;
        }
        mCurrentPos = mCurrentPos + 1;

        setDate(mDataList.get(mCurrentPos));
    }

    /**
     * 跳转到下一天的内容
     */
    @Override
    public void next() {
        if (mCurrentPos == -1) {
            return;
        }
        OneDay oneDay = mDataList.get(mCurrentPos);
        if (mCurrentPos == 0 && oneDay.next.split(SEPARATOR)[2].equals("1")) {
            fetchOne(oneDay.next);
            mIsNextNewList = true;
            Log.i(TAG, "next: new next list");
            return;
        } else if (mCurrentPos == 0 && !oneDay.next.split(SEPARATOR)[2].equals("1")) {
            App.showToast("no next");
            return;
        }
        setDate(mDataList.get(--mCurrentPos));
    }

    /**
     * 跳转到今天当天的内容
     */
    @Override
    public void curr() {
        String currDate = DateUtil.getOneYMD();

        Log.i(TAG, "curr: " + currDate + "===>" + mDataList.get(mCurrentPos).curr);

        if (mDataList.get(mCurrentPos).curr.equals(currDate)) {
            App.showToast("is today");
            return;
        }
        if (mDataList.get(0).curr.equals(currDate)) {
            mCurrentPos = 0;
            setDate(mDataList.get(mCurrentPos));
        } else {
            Log.i(TAG, "curr: fetch");
            fetchOne(currDate);
        }
    }

    @Override
    public OneDay getCurrBean() {
        return mDataList.get(mCurrentPos);
    }


}
