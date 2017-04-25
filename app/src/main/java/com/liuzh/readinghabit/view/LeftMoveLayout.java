package com.liuzh.readinghabit.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 刘晓彬 on 2017/4/24.
 */

public class LeftMoveLayout extends ViewGroup {

    private static final String TAG = "LeftMoveLayout";

    private ViewGroup mView;

    private View mLeftView;//左边可见的view
    private View mRightView;//滑动后从右边出现的view
    private ViewDragHelper mDragHelper;

    public LeftMoveLayout(Context context) {
        this(context, null);
    }

    public LeftMoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mView = this;
        init();
    }

    public LeftMoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }


    private void init() {
        mDragHelper = ViewDragHelper.create(mView, new ViewDragHelper.Callback() {

            /**
             * 是否捕获当前子view的事件
             * @param child 被触摸的子view
             * @param pointerId 第几个子view
             * @return 是否捕获
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mLeftView || child == mRightView;
            }

            // 水平能拖动的范围
            @Override
            public int getViewHorizontalDragRange(View child) {
                return mRightView.getMeasuredWidth();
            }

            /**
             * 控制子view在水平方向的移动
             * @param child 被触摸的子view
             * @param left left = child.getLeft() + dx
             * @param dx 本次触摸移动的水平距离
             * @return 子view新的left
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (child == mLeftView) {
                    //触摸的是左边的view，控制滑动左边界和右边界
                    if (left < -mRightView.getMeasuredWidth()) {
                        left = -mRightView.getMeasuredWidth();
                    } else if (left > 0) {
                        left = 0;
                    }
                } else if (child == mRightView) {
                    //触摸的是右边的view，控制滑动左边界和右边界
                    int leftMin = mLeftView.getMeasuredWidth() - mRightView.getMeasuredWidth();
                    int leftMax = mLeftView.getMeasuredWidth();
                    if (left < leftMin) {
                        left = leftMin;
                    } else if (left > leftMax) {
                        left = leftMax;
                    }
                }
                return left;
            }

            /**
             * view位置改变的时候回调，让右侧的view跟随左侧的view移动
             * @param changedView 改变了位置的view
             * @param left 新位置left
             * @param top 新位置top
             * @param dx 水平移动距离
             * @param dy 垂直移动距离
             */
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView == mLeftView) {
                    int l = left + mLeftView.getMeasuredWidth();
                    int b = mRightView.getMeasuredHeight();
                    int r = l + mRightView.getMeasuredWidth();
                    mRightView.layout(l, 0, r, b);
                }
                if (changedView == mRightView) {
                    int l = left - mLeftView.getMeasuredWidth();
                    int b = mLeftView.getMeasuredHeight();
                    int r = l + mLeftView.getMeasuredWidth();
                    mLeftView.layout(l, 0, r, b);
                }
            }

            /**
             * 当view被释放的时候回调
             * @param releasedChild 被释放的view
             * @param xvel 水平方向移动的速度
             * @param yvel 垂直方向移动的速度
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.i(TAG, "onViewReleased: xvel:" + xvel);
                Log.i(TAG, "onViewReleased: yvel:" + yvel);
                super.onViewReleased(releasedChild, xvel, yvel);

                if (xvel < -400 || mLeftView.getLeft() < -mRightView.getMeasuredWidth() / 2) {
                    mDragHelper.smoothSlideViewTo(mLeftView, -mRightView.getMeasuredWidth(), 0);
                }
                if (xvel > 400 || mLeftView.getLeft() >= -mRightView.getMeasuredWidth() / 2) {
                    mDragHelper.smoothSlideViewTo(mLeftView, 0, 0);
                }
                ViewCompat.postInvalidateOnAnimation(mView);
            }
        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(mView);
        }
    }

    /**
     * 在读取完XML最后一个节点的时候，判断是不是只有2个子view
     * 不是的话抛出异常
     * 是的话引用这两个子view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            try {
                throw new IllegalAccessException("child must have 2 child");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mLeftView = getChildAt(0);
        mRightView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lW = mLeftView.getMeasuredWidth();
        int lH = mLeftView.getMeasuredHeight();
        mLeftView.layout(0, 0, lW, lH);
        mRightView.layout(lW, 0, lW + mRightView.getMeasuredWidth(), mRightView.getMeasuredHeight());
    }


    //事件交给dragHelper处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }


    //是否拦截事件，由dragHelper决定
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }
}
