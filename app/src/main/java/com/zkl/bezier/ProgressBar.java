package com.zkl.bezier;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ProgressBar extends View {

    private Paint mPaint;
    private Paint circlePaint;
    private Paint unReachPaint;
    private int mStepNum = 20;//一共有20步
    private int mStepPerSize;//一步的长度
    private int lineHeight;
    private int currentStep = 3;
    private int mWidth;
    private int mHeight;
    private Path mPath;
    private PointF currentPoint;//当前圆的坐标
    private boolean isInner;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public void setCurrentStep(int currentStep) {
        if (currentStep<=0){
            this.currentStep =0;
        }else if (currentStep>20){
            this.currentStep = 20;
        }else {
            this.currentStep = currentStep;
        }
        invalidate();
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.yellow));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        unReachPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unReachPaint.setColor(Color.parseColor("#43403d"));
        unReachPaint.setStyle(Paint.Style.STROKE);
        unReachPaint.setStrokeCap(Paint.Cap.ROUND);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(getResources().getColor(R.color.yellow));
        circlePaint.setStyle(Paint.Style.FILL);
        lineHeight = DisplayUtils.dp2px(getContext(), 4);
        currentPoint = new PointF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, 200), measureView(heightMeasureSpec, 5));
    }

    private int measureView(int measureSpec, int defaultSize) {
        int measureSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.UNSPECIFIED) {
            measureSize = size;
        } else if (mode == MeasureSpec.EXACTLY) {
            measureSize = size;
        } else {
            measureSize = Math.min(size, defaultSize);
        }
        return measureSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mStepPerSize = (mWidth-mHeight) / mStepNum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(lineHeight);
        unReachPaint.setStrokeWidth(lineHeight);
//        mPath.addRect(0,0,mWidth,mHeight, Path.Direction.CW);
        canvas.drawLine(mHeight / 2, mHeight / 2, mWidth - mHeight, mHeight / 2, mPaint);
        float cx = currentStep * mStepPerSize +mHeight/2;
        currentPoint.x = cx;
        currentPoint.y = mHeight / 2;
        if (currentStep!=20) {
            canvas.drawLine(currentPoint.x + mHeight / 2, currentPoint.y, mWidth - mHeight, mHeight / 2, unReachPaint);
        }
        canvas.drawCircle(cx, mHeight / 2, DisplayUtils.dp2px(getContext(), 9), circlePaint);
    }

    private int lastStep = currentStep;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX(), y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //判断是否点击在圆内
                if (findStepByX(x)==currentStep) {
                    Log.d("debug", "触摸在范围内");
                    isInner = true;
                } else {
                    isInner = false;
                    Log.d("debug", "触摸不在范围内");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //根据当前x轴坐标计算出当前属于第几步
                if (isInner) {
                    currentStep = findStepByX(x);
                    invalidate();
                    if (lastStep!=currentStep) {//防止频繁回调
                        Toast.makeText(getContext(), "current" + currentStep, Toast.LENGTH_SHORT).show();
                        lastStep= currentStep;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isInner = false;
                break;
        }
        return true;
    }

    private int findStepByX(int x) {
        if (x < mHeight) {
            Log.d("debug","0");
            return 0;
        } else if (x > mWidth - mHeight) {
            Log.d("debug","20");
            return 20;
        } else {
            Log.d("debug",""+(x - mHeight / 2) / mStepPerSize);
            return (x - mHeight / 2) / mStepPerSize;
        }
    }
}

