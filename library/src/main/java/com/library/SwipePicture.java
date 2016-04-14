package com.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.Random;

/**
 * Created by weiguangmeng on 16/4/14.
 */
public class SwipePicture extends ViewGroup {
    private static final int DEFAULT_ANIMATION_DURATION = 500;
    private static final int DEFAULT_STACK_SIZE = 3;
    private static final int DEFAULT_SCALE_FACTOR = 1;
    private static final int DEFAULT_SWIPE_ROTATION = 25;
    private static final int DEFAULT_PIC_ROTATION = 15;

    private int mAnimationDuration;
    private int mStackSize;
    private float mScaleFactor;
    private float mSwipeRotation;
    private float mPicRotation;
    private int mPicInterval;

    private Random mRandom;

    private Adapter adapter;
    private DataSetObserver mDataSetObserver;

    private int mCurrentIndex = 0;

    private boolean isNewView;
    private boolean isRefreshSwipe = true;

    public SwipePicture(Context context) {
        this(context, null);
    }

    public SwipePicture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipePicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(attrs);
    }

    private void readAttributes(AttributeSet set) {
        TypedArray attrs = getContext().obtainStyledAttributes(set, R.styleable.SwipePicture);
        try {
            mAnimationDuration = attrs.getInt(R.styleable.SwipePicture_animation_duration, DEFAULT_ANIMATION_DURATION);
            mStackSize = attrs.getInt(R.styleable.SwipePicture_stack_size, DEFAULT_STACK_SIZE);
            mPicRotation = attrs.getFloat(R.styleable.SwipePicture_pic_rotation, DEFAULT_PIC_ROTATION);
            mSwipeRotation = attrs.getFloat(R.styleable.SwipePicture_swipe_rotation, DEFAULT_SWIPE_ROTATION);
            mScaleFactor = attrs.getFloat(R.styleable.SwipePicture_scale_factor, DEFAULT_SCALE_FACTOR);
            mPicInterval = attrs.getDimensionPixelOffset(R.styleable.SwipePicture_pic_interval, getContext().getResources().getDimensionPixelOffset(R.dimen.pic_interval));
        } finally {
            attrs.recycle();
        }
    }

    private void init() {
        mRandom = new Random();
        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                invalidate();
                requestLayout();
            }
        };
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(null == adapter || adapter.isEmpty()) {
            mCurrentIndex = 0;
            removeAllViewsInLayout();  //这个忘了
        }

        for(int i = getChildCount(); i < mStackSize && mCurrentIndex < adapter.getCount(); i ++) {
           addNextView();
        }

        resetItems();

        isRefreshSwipe = false;

    }

    private void addNextView() {
        if(mCurrentIndex < adapter.getCount()) {
            View childView = adapter.getView(mCurrentIndex, null, this);
            childView.setTag(R.id.new_view, true);

            LayoutParams lp = childView.getLayoutParams();
            if (null == lp) {
                lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
            }
            int width = getWidth() - (getPaddingLeft() + getPaddingRight());
            int height = getHeight() - (getPaddingTop() + getPaddingBottom());

            int measureWidth = MeasureSpec.AT_MOST;
            int measureHeight = MeasureSpec.AT_MOST;

            if (lp.width == LayoutParams.MATCH_PARENT) {
                measureWidth = MeasureSpec.EXACTLY;
            }

            if (lp.height == LayoutParams.MATCH_PARENT) {
                measureHeight = MeasureSpec.EXACTLY;
            }
            int measureSpecWidth = MeasureSpec.makeMeasureSpec(width, measureWidth);
            int measureSpecHeight = MeasureSpec.makeMeasureSpec(height, measureHeight);
            childView.measure(measureSpecWidth, measureSpecHeight);

            mCurrentIndex++;
        }
    }

    private void resetItems() {
        int topViewIndex = getChildCount() - 1;
        for(int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            int topViewInstance = (getChildCount() - i) * mPicInterval;
            int viewPosX = (getWidth() - childView.getWidth()) / 2;
            int viewPosY = topViewInstance + getPaddingTop();

            childView.layout(viewPosX, viewPosY, viewPosX + childView.getMeasuredWidth(), viewPosY + childView.getMeasuredHeight());
            if(i == topViewIndex) {

            }

            isNewView = (boolean) childView.getTag(R.id.new_view);
            if(!isRefreshSwipe) {
                if(isNewView) {
                    childView.setTag(R.id.new_view, false);
                    childView.setY(viewPosY);
                    childView.setAlpha(0);
                    childView.setScaleX(mScaleFactor);
                    childView.setScaleY(mScaleFactor);
                }

                childView.animate()
                        .y(viewPosY)
                        .alpha(1)
                        .scaleX(mScaleFactor)
                        .scaleY(mScaleFactor)
                        .setDuration(mAnimationDuration);
            } else {
                childView.setTag(R.id.new_view, false);
                childView.setY(viewPosY);
                childView.setScaleX(mScaleFactor);
                childView.setScaleY(mScaleFactor);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }
}
