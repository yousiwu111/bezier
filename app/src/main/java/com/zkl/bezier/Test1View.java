package com.zkl.bezier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Test1View extends View {
    public Test1View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private Path mPath;
    private Paint mPaint;
    private Paint nodePaint;
    private Paint connectPaint;
    private int mWidth, mHeight;
    private PointF[] pointsArray;
    private final int STATE_NONE = 0;
    private final int STATE_TOUCH_DOWN = 1;
    private final int STATE_TOUCH_MOVE = 2;
    private final int STATE_TOUCH_UP = 3;
    private int STATE_NOW = STATE_NONE;

    private int[] decibelArray;
    private float mRadius;
    private float stepVertical;
    private updateDecibelListener listener;
    private PathMeasure mPathMeasure;
    private Rect rect, rectP;
    private Bitmap srcBit, dstBit;
    private int normalBitmapWidth, selectedBitmapWidth;
    private int STEP_VERTICAL_TWENTY = 20;
    private Paint linePaint = new Paint();
    private List<PathMeasure> pathMeasures = new ArrayList<>();
    private float centerPointY;//中间Y坐标
    public interface updateDecibelListener {
        void updateDecibel(int[] decibels);

    }


    public void setUpdateDecibelListener(updateDecibelListener listener) {
        this.listener = listener;
    }

    public int[] getDecibelArray() {
        return decibelArray;
    }

    public void setDecibelArray(int[] decibelArray) {
        this.decibelArray = decibelArray;
        invalidate();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        nodePaint = new Paint();
        nodePaint.setAntiAlias(true);
        nodePaint.setColor(ContextCompat.getColor(getContext(), R.color.yellow));
        nodePaint.setStrokeWidth(DisplayUtils.dp2px(getContext(), 3));
        nodePaint.setStyle(Paint.Style.STROKE);
        connectPaint = new Paint();
        connectPaint.setAntiAlias(true);
        connectPaint.setStrokeWidth(50);
        connectPaint.setStyle(Paint.Style.FILL);
        connectPaint.setColor(ContextCompat.getColor(getContext(), R.color.yellow));

        pointsArray = new PointF[7];
        decibelArray = new int[5];
        rect = new Rect();
        rectP = new Rect();
        srcBit = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_euqualizer_unselected);
        dstBit = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_euqualizer_selected);
        normalBitmapWidth = srcBit.getWidth();
        selectedBitmapWidth = dstBit.getWidth();
        //线
        linePaint.setColor(getResources().getColor(R.color.yellow));
        linePaint.setStrokeWidth(DisplayUtils.dp2px(getContext(), 1));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    private int measureView(int measureSpec, int defaultSize) {
        int measureSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            measureSize = size;
        } else {
            measureSize = defaultSize;
            if (mode == MeasureSpec.AT_MOST) {
                measureSize = Math.min(measureSize, defaultSize);
            }
        }
        return measureSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, 400),
                measureView(heightMeasureSpec, 200));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        stepVertical = mHeight / STEP_VERTICAL_TWENTY;    //-10到10共20份
        centerPointY = stepVertical * STEP_VERTICAL_TWENTY / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int stepSize = mWidth / 6;//6段
        pointsArray[0] = new PointF(0, centerPointY);
        pointsArray[pointsArray.length - 1] = new PointF(mWidth, centerPointY);

//        if ((STATE_NOW == STATE_NONE)) {
        for (int i = 1; i <= pointsArray.length - 2; i++) {
            float cx = stepSize * i, cy = stepVertical * (decibelArray[i - 1] + STEP_VERTICAL_TWENTY / 2);
            pointsArray[i] = new PointF(cx, cy);
        }
//        }

        measurePath();

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        Path dst = new Path();
        dst.rLineTo(0, 0);
        float distance = mPathMeasure.getLength();
        if (mPathMeasure.getSegment(0, distance, dst, true)) {
            //绘制线
            canvas.drawPath(dst, paint);
            //绘制阴影
            //绘制点
        }
        refreshView(canvas, stepSize);
    }

    private void refreshView(Canvas canvas, int stepSize) {
//        float[] points = new float[]{pointsArray[0].x, pointsArray[0].y, pointsArray[1].x, pointsArray[1].y,
//                pointsArray[1].x, pointsArray[1].y, pointsArray[2].x, pointsArray[2].y,
//                pointsArray[2].x, pointsArray[2].y, pointsArray[3].x, pointsArray[3].y,
//                pointsArray[3].x, pointsArray[3].y, pointsArray[4].x, pointsArray[4].y,
//                pointsArray[4].x, pointsArray[4].y, pointsArray[5].x, pointsArray[5].y,
//                pointsArray[5].x, pointsArray[5].y, pointsArray[6].x, pointsArray[6].y,
//                pointsArray[6].x, pointsArray[6].y, pointsArray[7].x, pointsArray[7].y,
//                pointsArray[7].x, pointsArray[7].y, pointsArray[8].x, pointsArray[8].y,
//                pointsArray[8].x, pointsArray[8].y, pointsArray[9].x, pointsArray[9].y,
//                pointsArray[9].x, pointsArray[9].y, pointsArray[10].x, pointsArray[10].y,
//                pointsArray[10].x, pointsArray[10].y, pointsArray[11].x, pointsArray[11].y};
//        canvas.drawLines(points, connectPaint);
        measurePath();
        for (int i = 1; i <= pointsArray.length - 2; i++) {
            float cx = stepSize * i, cy = pointsArray[i].y;
            if (i == index && STATE_NOW != STATE_TOUCH_UP) {
                mRadius = DisplayUtils.dp2px(getContext(), 19);
                //触摸的时候
                canvas.drawBitmap(dstBit, cx - selectedBitmapWidth / 2, cy - selectedBitmapWidth / 2, nodePaint);
            } else {
                //非触摸情况
                mRadius = DisplayUtils.dp2px(getContext(), 10);
                ;
                canvas.drawBitmap(srcBit, cx - normalBitmapWidth / 2, cy - normalBitmapWidth / 2, nodePaint);
            }

//            canvas.drawCircle(cx, cy, mRadius, nodePaint);
//            canvas.drawCircle(cx, cy, mRadius - 6, connectPaint);
//            mPaint.setColor(ContextCompat.getColor(mContext, R.color.equalizer_node_over_line));
//            mPaint.setStrokeWidth(6);
//            canvas.drawLine(cx, cy + mRadius + 3, stepSize * i, mHeight, mPaint);
//            mPaint.setColor(ContextCompat.getColor(mContext, R.color.equalizer_node_last_line));
//            canvas.drawLine(cx, cy - mRadius - 3, stepSize * i, 0, mPaint);
        }
//        for (int i = 1; i <= 10; i++) {
//            float cx = stepSize * i, cy = pointsArray[i].y;
//            if (i == index && STATE_NOW != STATE_TOUCH_UP) {
//                mRadius = 50;
//            } else {
//                mRadius = 40;
//            }
//            canvas.drawCircle(cx, cy, mRadius, nodePaint);
//            canvas.drawCircle(cx, cy, mRadius - 6, connectPaint);
//            mPaint.setColor(ContextCompat.getColor(mContext, R.color.equalizer_node_over_line));
//            mPaint.setStrokeWidth(6);
//            canvas.drawLine(cx, cy + mRadius + 3, stepSize * i, mHeight, mPaint);
//            mPaint.setColor(ContextCompat.getColor(mContext, R.color.equalizer_node_last_line));
//            canvas.drawLine(cx, cy - mRadius - 3, stepSize * i, 0, mPaint);
//        }
    }

    private int mLastY = 0;
    private int index = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX(), y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                index = findTheIndex(x, y);
                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_DOWN;
                    invalidate();

                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float deltaY = y - mLastY;

                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_MOVE;
                    pointsArray[index].y += deltaY;
                    if (y <= normalBitmapWidth)
                        pointsArray[index].y = normalBitmapWidth;
                    if (y >= mHeight - normalBitmapWidth)
                        pointsArray[index].y = mHeight - normalBitmapWidth;
                    decibelArray[index - 1] = getTheDecibel(pointsArray[index].y);
                    invalidate();
                    listener.updateDecibel(decibelArray);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_UP;
                    if (decibelArray[index - 1] != 0 && decibelArray[index - 1] != -10 &&
                            decibelArray[index - 1] != 10) {
                        float lastY = stepVertical * (-decibelArray[index - 1] + 10);
                        pointsArray[index].y = lastY;
                    } else if (decibelArray[index - 1] == 0)
                        pointsArray[index].y = stepVertical * 10;
                    invalidate();
                }
                break;
            }
            default:
                break;
        }
        mLastY = y;
        return true;
    }

    /**
     * 查出当前正在操作的是哪个结点
     *
     * @param x
     * @param y
     * @return
     */
    private int findTheIndex(float x, float y) {
        int result = 0;
        for (int i = 1; i < pointsArray.length; i++) {
            if (pointsArray[i].x - mRadius * 1.5 < x && pointsArray[i].x + mRadius * 1.5 > x &&
                    pointsArray[i].y - mRadius * 1.5 < y && pointsArray[i].y + mRadius * 1.5 > y) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 将坐标转换为-12到12之间的数字
     *
     * @param y
     * @return
     */
    private int getTheDecibel(float y) {
        if (y == getHeight() - normalBitmapWidth)
            return -10;
        else if (y == normalBitmapWidth)
            return 10;
        else
            return 10 - Math.round(y / stepVertical);
    }



    private void measurePath() {
        mPath = new Path();
//        mAssistPath = new Path();
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        final int lineSize = pointsArray.length;
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                PointF point = pointsArray[valueIndex];
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    PointF point = pointsArray[valueIndex - 1];
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    PointF point = pointsArray[valueIndex - 2];
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < lineSize - 1) {
                PointF point = pointsArray[valueIndex + 1];
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                mPath.moveTo(currentPointX, currentPointY);
//                mAssistPath.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (0.2f * firstDiffX);
                final float firstControlPointY = previousPointY + (0.2f * firstDiffY);
                final float secondControlPointX = currentPointX - (0.2f * secondDiffX);
                final float secondControlPointY = currentPointY - (0.2f * secondDiffY);
                //画出曲线
                mPath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
                //将控制点保存到辅助路径上
//                mAssistPath.lineTo(firstControlPointX, firstControlPointY);
//                mAssistPath.lineTo(secondControlPointX, secondControlPointY);
//                mAssistPath.lineTo(currentPointX, currentPointY);
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }
        mPathMeasure = new PathMeasure(mPath, false);
        Log.d("debug","prePreviousPointX:"+prePreviousPointX);
    }
}
