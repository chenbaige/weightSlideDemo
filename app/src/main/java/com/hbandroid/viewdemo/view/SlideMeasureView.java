package com.hbandroid.viewdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Title: LevelProgressDemo
 * <p/>
 * Description:薄荷健康的滑动卷尺效果 效果：https://user-gold-cdn.xitu.io/2017/10/13/b9b39e52d632a350baa7c1adbb14cd12
 * <p/>
 * Author:baigege (baigegechen@gmail.com)
 * <p/>
 * Date:2017-10-22
 */
public class SlideMeasureView extends View {

    private Context mContext;

    //默认刻度间的宽度
    public static final int DEFALUT_MEASURE_INTERVAL_WIDTH = 20;
    private int mIntervalWidth = DEFALUT_MEASURE_INTERVAL_WIDTH;

    public static final int DEFALUT_LINE_WIDTH = 6;
    public static final int DEFALUT_CURR_LINE_WIDTH = 15;

    //刻度尺的开始值
    private int mStartValue = 0;
    //刻度尺的结束值
    private int mEndValue = 0;

    //每个单位刻度间的间隔数
    public static final int DEFALU_INTERVAL_YARDSTICK = 10;
    private int mYardstick = DEFALU_INTERVAL_YARDSTICK;

    //默认长刻度线的高度
    public static final int DEFALUT_LONG_LINE_HEIGHT = 60;
    //默认短刻度的高度
    public static final int DEFALUT_SHORT_LINE_HEIGHT = 25;
    //默认长刻度线与刻度值间的间隙高度（和刻度值据View底部间隙值一样）
    public static final int DEFALUT_DIVIDER_LINE_TO_VALUE = 8;
    //刻度尺与当前刻度值间的间隙高度(和当前刻度值与View顶部的间隔高度)
    public static final int DEFALUT_DIVIDER_LINE_TO_CURR_VALUE = 12;

    //当前刻度值的字体大小和字体颜色
    private int mCurrValueTextSize;
    private int mCurrValueTextColor;

    //刻度的字体大小和字体颜色
    private int mValueTextSize;
    private int mValueTextColor;

    //绘制当前刻度值的画笔
    private Paint mCurrValuePaint;
    //绘制长刻度的画笔
    private Paint mLongLinePaint;
    //绘制短刻度的画笔
    private Paint mShortLinePaint;
    //绘制刻度值的画笔
    private Paint mValuePaint;
    //绘制显示当前可的的竖直线画笔
    private Paint mCurrLinePaint;

    //用于测量刻度值文字显示区域的宽度和高度
    private Paint.FontMetricsInt mValueFontMetrics;
    private Rect mValueBound;

    //用于测量当前刻度值文字显示区域的宽度和高度
    private Paint.FontMetricsInt mCurrValueFontMetrics;
    private Rect mCurrValueBound;

    private Scroller mScroller;
    private ViewDragHelper mViewDragHelper;
    private VelocityTracker mVelocityTracker;

    //记录上一次滑动结束的位置
    private int mLastX;
    private int mMove;

    public SlideMeasureView(Context context) {
        this(context, null);
    }

    public SlideMeasureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMeasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        //初始化绘制当前进度值的画笔
        mCurrValueTextSize = 48;
        mCurrValueTextColor = Color.GREEN;
        mCurrValuePaint = new Paint();
        mCurrValuePaint.setColor(mCurrValueTextColor);
        mCurrValuePaint.setTextSize(mCurrValueTextSize);
        mCurrValueFontMetrics = mCurrValuePaint.getFontMetricsInt();
//        mCurrValuePaint.getTextBounds(mProgressText, 0, mProgressText.length(), mTextBound);

        //初始化绘制刻度值的画笔
        mValueTextColor = Color.BLACK;
        mValueTextSize = 38;
        mValuePaint = new Paint();
        mValuePaint.setTextSize(mValueTextSize);
        mValuePaint.setColor(mValueTextColor);
        mValueFontMetrics = mValuePaint.getFontMetricsInt();
//        mTextPaint.getTextBounds(mProgressText, 0, mProgressText.length(), mTextBound);

        //初始化长线的画笔
        mLongLinePaint = new Paint();
        mLongLinePaint.setStrokeWidth(DEFALUT_LINE_WIDTH);
        mLongLinePaint.setColor(Color.BLACK);

        //初始化短线的画笔
        mShortLinePaint = new Paint();
        mShortLinePaint.setStrokeWidth(DEFALUT_LINE_WIDTH);
        mShortLinePaint.setColor(Color.BLACK);

        //初始化长线的画笔
        mCurrLinePaint = new Paint();
        mCurrLinePaint.setStrokeWidth(DEFALUT_CURR_LINE_WIDTH);
        mCurrLinePaint.setColor(Color.GREEN);

        mStartValue = 0;
        mEndValue = 100;
        mValueBound = new Rect();
        mCurrValueBound = new Rect();

        mScroller = new Scroller(mContext);
//        mViewDragHelper = ViewDragHelper.create((ViewGroup) this.getParent(), mCallback);
    }

//    public ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
//        @Override
//        public boolean tryCaptureView(View child, int pointerId) {
//            return false;
//        }
//    };


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //当前其实值到结束值所需要的宽度
        int width = (mEndValue - mStartValue) * mYardstick * mIntervalWidth;
        int height = DEFALUT_DIVIDER_LINE_TO_CURR_VALUE * 2 + mCurrValueFontMetrics.bottom - mCurrValueFontMetrics.top + DEFALUT_LONG_LINE_HEIGHT + mValueFontMetrics.bottom - mValueFontMetrics.top + DEFALUT_DIVIDER_LINE_TO_VALUE * 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //刻度线开始时的y坐标
        int startLineLocationY = DEFALUT_DIVIDER_LINE_TO_CURR_VALUE * 2 + mCurrValueFontMetrics.bottom - mCurrValueFontMetrics.top;

        //刻度值的开始x坐标
        mValuePaint.getTextBounds(String.valueOf(mStartValue), 0, String.valueOf(mStartValue).trim().length(), mValueBound);
        int startLineLocationX = (mValueBound.right - mValueBound.left) / 2;

        //画水平横线
        canvas.drawLine(startLineLocationX, startLineLocationY - DEFALUT_LINE_WIDTH / 2, (mEndValue - mStartValue) * mIntervalWidth * DEFALU_INTERVAL_YARDSTICK, startLineLocationY - DEFALUT_LINE_WIDTH / 2, mShortLinePaint);

        //画刻度线
        int longLineNum = mEndValue - mStartValue;
        //绘制长刻度线
        for (int i = 0; i < longLineNum; i++) {
            for (int j = 0; j < mYardstick; j++) {
                if (j != 0 || j != mYardstick - 1) {
                    canvas.drawLine((i * mYardstick + j) * mIntervalWidth + startLineLocationX, startLineLocationY, (i * mYardstick + j) * mIntervalWidth + startLineLocationX, startLineLocationY + DEFALUT_SHORT_LINE_HEIGHT, mShortLinePaint);
                }
            }
            //画长刻度线
            canvas.drawLine((i * mYardstick) * mIntervalWidth + startLineLocationX, startLineLocationY, startLineLocationX + (i * mYardstick) * mIntervalWidth, startLineLocationY + DEFALUT_LONG_LINE_HEIGHT, mLongLinePaint);

            //画刻度值
            String strValue = String.valueOf(mStartValue + i);
            mValuePaint.getTextBounds(strValue, 0, strValue.length(), mValueBound);
            canvas.drawText(strValue, (i * mYardstick) * mIntervalWidth - (mValueBound.right - mValueBound.left) / 2, startLineLocationY + DEFALUT_LONG_LINE_HEIGHT + DEFALUT_SHORT_LINE_HEIGHT - mValueFontMetrics.top, mValuePaint);
        }

        //画当前刻度值
        double bili = (getScrollX() + getScreenWidth() / 2) / (double) getMeasuredWidth();
        double floCurrValue = bili * (mEndValue - mStartValue) + mStartValue;
        String strCurrValue = format1(floCurrValue);
        mCurrValuePaint.getTextBounds(strCurrValue, 0, strCurrValue.length(), mCurrValueBound);
        canvas.drawText(strCurrValue, (getScreenWidth() - (mCurrValueBound.right - mCurrValueBound.left)) / 2 + mScroller.getFinalX(), DEFALUT_DIVIDER_LINE_TO_CURR_VALUE - mCurrValueFontMetrics.top, mCurrValuePaint);

        //画显示当前进度的竖直线
        canvas.drawLine(getScreenWidth() / 2 + mScroller.getFinalX(), startLineLocationY, getScreenWidth() / 2 + mScroller.getFinalX(), startLineLocationY + DEFALUT_LONG_LINE_HEIGHT, mCurrValuePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mLastX - x;
//                if (mCountScale - mTempScale < 0) { //向右边滑动
//                    if (mCountScale <= mStartValue && dataX <= 0) //禁止继续向右滑动
//                        return super.onTouchEvent(event);
//                } else if (mCountScale - mTempScale > 0) { //向左边滑动
//                    if (mCountScale >= mMax && dataX >= 0) //禁止继续向左滑动
//                        return super.onTouchEvent(event);
//                }
                smoothScrollBy(dataX, 0);
                mLastX = x;
                postInvalidate();
//                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
//                if (mCountScale < mMin) mCountScale = mMin;
//                if (mCountScale > mMax) mCountScale = mMax;
//                int finalX = (mCountScale - mMidCountScale) * mScaleMargin;
                int finalX = getScreenWidth() / 2 + getScrollX();
                mScroller.setFinalX(finalX); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    //保留几位小数的算法(这里保留两位小数)
    public static String format1(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }
}
