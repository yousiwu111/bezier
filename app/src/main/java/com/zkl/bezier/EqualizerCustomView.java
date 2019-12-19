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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class EqualizerCustomView extends View {

    private Paint mPaint;
    private Paint nodePaint;
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
    private Bitmap srcBit, dstBit;
    private int normalBitmapWidth, selectedBitmapWidth;
    private int STEP_VERTICAL_TWENTY = 20;
    private Paint linePaint = new Paint();
    private List<PathMeasure> pathMeasures = new ArrayList<>();
    private float centerPointY;//中间Y坐标
    private Paint mFillPaint;
    private List<Path> paths = new ArrayList<>();

    private int[] colors = {Color.rgb(166, 141, 91), Color.rgb(135, 115, 75), Color.rgb(120, 103, 67), Color.rgb(105, 90, 60), Color.rgb(90, 78, 52), Color.rgb(75, 65, 45), Color.rgb(60, 52, 37), Color.rgb(45, 40, 30), Color.rgb(30, 27, 22), Color.rgb(15, 15, 15)};

    public EqualizerCustomView(Context context) {
        this(context, null);
    }

    public EqualizerCustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqualizerCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

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
        STATE_NOW = STATE_NONE;
        index = 0;
        invalidate();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //画触控点画笔
        nodePaint = new Paint();
        nodePaint.setAntiAlias(true);

        //填充画笔
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.yellow));

        pointsArray = new PointF[7];
        decibelArray = new int[5];

        srcBit = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_euqualizer_unselected);
        dstBit = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_euqualizer_selected);
        normalBitmapWidth = srcBit.getWidth();
        selectedBitmapWidth = dstBit.getWidth();
        //线
        linePaint.setColor(getResources().getColor(R.color.yellow));
        linePaint.setStrokeWidth(DisplayUtils.dp2px(getContext(), 1));
        linePaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < STEP_VERTICAL_TWENTY / 2; i++) {
            paths.add(new Path());
        }
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
        setMeasuredDimension(measureView(widthMeasureSpec, DisplayUtils.getScreenWidth(getContext())),
                measureView(heightMeasureSpec, DisplayUtils.dp2px(getContext(), 396)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        stepVertical = (mHeight-selectedBitmapWidth) / STEP_VERTICAL_TWENTY;    //-10到10共20份
        centerPointY = mHeight / 2;
        pointsArray[0] = new PointF(0, centerPointY);
        pointsArray[pointsArray.length - 1] = new PointF(mWidth, centerPointY);
        Log.d("debug", "宽度：" + mWidth + ",高度：" + mHeight + ",中间坐标：" + centerPointY + ",垂直的高度均分20份每份：" + stepVertical);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int stepSize = mWidth / 6;//6段


        if ((STATE_NOW == STATE_NONE)) {
            for (int i = 1; i <= pointsArray.length - 2; i++) {
                float cx = stepSize * i, cy = stepVertical * (-decibelArray[i - 1] + STEP_VERTICAL_TWENTY / 2)+selectedBitmapWidth/2;
                pointsArray[i] = new PointF(cx, cy);
            }
        }

        measurePath2();

        for (int i = 0; i < pathMeasures.size(); i++) {
            Path path = paths.get(i);
            path.reset();
            path.moveTo(-2, -2);
            path.rLineTo(-2, centerPointY);
            if (pathMeasures.get(i).getSegment(0, pathMeasures.get(i).getLength(), path, true)) {
                //绘制线
                mFillPaint.setColor(colors[i]);
                canvas.drawPath(path, mFillPaint);
            }
            if (i == 0) {
                //画中间线，否则一开始没有横线
                canvas.drawPath(path, linePaint);
            }
        }
        refreshView(canvas, stepSize);
    }

    private void refreshView(Canvas canvas, int stepSize) {
        for (int i = 1; i <= pointsArray.length - 2; i++) {
            float cx = stepSize * i, cy = pointsArray[i].y;
            if (i == index && STATE_NOW != STATE_TOUCH_UP) {
                mRadius = DisplayUtils.dp2px(getContext(), 19);
                //触摸的时候
                canvas.drawBitmap(dstBit, cx - selectedBitmapWidth / 2, cy - selectedBitmapWidth / 2, nodePaint);
            } else {
                //非触摸情况
                mRadius = DisplayUtils.dp2px(getContext(), 10);
                canvas.drawBitmap(srcBit, cx - normalBitmapWidth / 2, cy - normalBitmapWidth / 2, nodePaint);
            }
        }
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
                    if (y <= stepVertical / 2)
                        pointsArray[index].y = stepVertical / 2;
                    if (y >= mHeight - stepVertical / 2)
                        pointsArray[index].y = mHeight - stepVertical / 2;
                    int theDecibel = getTheDecibel(pointsArray[index].y);
                    if (listener != null && theDecibel != decibelArray[index - 1]) {
                        decibelArray[index - 1] = theDecibel;
                        listener.updateDecibel(decibelArray);
                    }
                    Log.d("debug", "y:" + pointsArray[index].y + ",index：" + theDecibel + ",小圆长度：" + normalBitmapWidth);
                    invalidate();//宽度：720,高度：760,中间坐标：380.0,垂直的高度均分20份每份：38.0
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
     * 将坐标转换为-10到10之间的数字
     *
     * @param y
     * @return
     */
    private int getTheDecibel(float y) {
        if (y >= getHeight() - stepVertical / 2-selectedBitmapWidth/2)
            return -10;
        else if (y <= stepVertical+selectedBitmapWidth/2)
            return 10;
        else
            return 10 - Math.round(y-selectedBitmapWidth/2 / stepVertical);
    }


    /**
     * 根据y坐标获取10等份的长度
     *
     * @param y
     * @return
     */
    private float getOffsetByY(float y) {
        return (centerPointY - y) / 10;
    }

    private void measurePath2() {
        for (int i = 0; i < 10; i++) {
            Path mPath = new Path();
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
                    currentPointY = point.y + getOffsetByY(point.y) * (i);
                }
                if (Float.isNaN(previousPointX)) {
                    //是否是第一个点
                    if (valueIndex > 0) {
                        PointF point = pointsArray[valueIndex - 1];
                        previousPointX = point.x;
                        previousPointY = point.y + getOffsetByY(point.y) * (i);
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
                        prePreviousPointY = point.y + getOffsetByY(point.y) * (i);
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
                    nextPointY = point.y + getOffsetByY(point.y) * (i);
                } else {
                    //是的话就用当前点表示下一个点
                    nextPointX = currentPointX;
                    nextPointY = currentPointY;
                }

                if (valueIndex == 0) {
                    // 将Path移动到开始点
                    mPath.moveTo(currentPointX, currentPointY);
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
                }

                // 更新值,
                prePreviousPointX = previousPointX;
                prePreviousPointY = previousPointY;
                previousPointX = currentPointX;
                previousPointY = currentPointY;
                currentPointX = nextPointX;
                currentPointY = nextPointY;
            }
            if (pathMeasures.size() == 10) {
                pathMeasures.get(i).setPath(mPath, false);
            } else {
                pathMeasures.add(new PathMeasure(mPath, false));
            }
        }
    }
}
