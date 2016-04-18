package com.library;

import android.animation.Animator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by weiguangmeng on 16/4/15.
 */
public class SwiperHelper implements View.OnTouchListener {
    private static final String TAG = "SwiperHelper";
    private SwipePicture mSwipePicture;
    private View mObserverView;

    private int mInitX;
    private int mInitY;

    private int mLastX;
    private int mLastY;
    private int mDownX;
    private int mDownY;


    public SwiperHelper(SwipePicture mSwipePicture) {
        this.mSwipePicture = mSwipePicture;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                mDownX = x;
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - mLastX;
                int dy = y - mLastY;

                Log.d(TAG, "++++++++++++++");
                Log.d(TAG, "X is " + mObserverView.getX() + ", Y is " + mObserverView.getY());
                Log.d(TAG, "translation x is " + mObserverView.getTranslationX() + "translation y is " + mObserverView.getTranslationY());
                Log.d(TAG, "down x is " + mDownX + ", down y is " + mDownY);
                float newX = mObserverView.getX() + dx;
                float newY = mObserverView.getY() + dy;
                Log.d(TAG, "new X is " + newX + ", new y is " + newY);
                Log.d(TAG, "move x is " + x + ", move y is " + y);
                Log.d(TAG, "---------------");

                mObserverView.setX(newX);
                mObserverView.setY(newY);


        /*        dx = x - mLastX;
                dy = y - mLastY;
                int scrollX = -dx;
                int scrollY = -dy;
                mObserverView.scrollBy(scrollX, scrollY);*/
                break;
            case MotionEvent.ACTION_UP:
                v.getParent().requestDisallowInterceptTouchEvent(false);
              /*  int observerViewX = (int) mObserverView.getX() + mObserverView.getWidth() / 2;
                int intervals = mSwipePicture.getWidth() / 3;
                if (observerViewX < intervals) {
                    swipeLeft();
                } else if (observerViewX > intervals * 2) {
                    swipeRight();
                } else {
                    resetView();
                }
                break;*/

                int moveDistanceX = x - mDownX;
                if(Math.abs(moveDistanceX) > 200) {
                    if(moveDistanceX > 0) {
                        swipeRight();
                    } else {
                        swipeLeft();
                    }
                } else {
                    resetView();
                }
        }

        mLastX = x;
        mLastY = y;

        return true;
    }

    public void registerObserverView(View topView, int initX, int initY) {
        mObserverView = topView;
        mObserverView.setOnTouchListener(this);
        mInitX = initX;
        mInitY = initY;
    }

    public void unregisterObserverView() {
        mObserverView = null;
    }

    public void swipeLeft() {
        mObserverView.animate()
                .setDuration(500)
                .x(-mSwipePicture.getWidth() - mObserverView.getWidth())
                .setListener(new AnimatorEndListener() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSwipePicture.removeTop();
                    }

                });
    }

    public void swipeRight() {
        mObserverView.animate()
                .setDuration(500)
                .x(mSwipePicture.getWidth() + mObserverView.getWidth())
                .setListener(new AnimatorEndListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSwipePicture.removeTop();
                    }
                });
    }

    public void resetView() {
        mObserverView.animate()
                .setDuration(500)
                .x(mInitX)
                .y(mInitY);
    }
}
