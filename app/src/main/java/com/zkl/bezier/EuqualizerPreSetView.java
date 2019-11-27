package com.zkl.bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class EuqualizerPreSetView extends View {

    private OnModeCheckListener mOnModeCheckListener;

    private int mWidth, mHeight;
    private int heightItem;//每行的高度
    //8个圆的坐标
    private CircleRadiusPoint[] points = new CircleRadiusPoint[8];
    private String[] contents = {"ROCK", "POP", "HIP HOP", "DANCE", "JAZZ", "CLASSICAL", "ACOUSTIC", "FLAT"};

    //正常字体paint
    private Paint normalTextPaint;
    private Paint circlePaint;
    //选中状态字体paint
    private Paint checkedTextPaint;
    //选中状态的圆
    private Paint checkedCirclePaint;

    private int currentMode = 0;
    private int touchIndex = -1;

    public void setOnModeCheckListener(OnModeCheckListener mOnModeCheckListener) {
        this.mOnModeCheckListener = mOnModeCheckListener;
    }

    public EuqualizerPreSetView(Context context) {
        this(context, null);
    }

    public EuqualizerPreSetView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EuqualizerPreSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(DisplayUtils.dp2px(getContext(), 1));
        circlePaint.setColor(Color.parseColor("#80A78E5B"));

        normalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalTextPaint.setStyle(Paint.Style.FILL);
        normalTextPaint.setColor(Color.parseColor("#80A78E5B"));
        normalTextPaint.setTextSize(DisplayUtils.dp2px(getContext(), 18));

        checkedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        checkedTextPaint.setStyle(Paint.Style.FILL);
        checkedTextPaint.setStrokeWidth(DisplayUtils.dp2px(getContext(), 1));
        checkedTextPaint.setColor(Color.parseColor("#0f0f0f"));
        checkedTextPaint.setTextSize(DisplayUtils.dp2px(getContext(), 18));

        checkedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        checkedCirclePaint.setStyle(Paint.Style.FILL);
        checkedCirclePaint.setColor(Color.parseColor("#A78E5B"));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, DisplayUtils.getScreenWidth(getContext())), measureView(heightMeasureSpec, DisplayUtils.dp2px(getContext(), 600)));
    }

    private int measureView(int measureSpec, int defaultSize) {
        int measuredSize;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                measuredSize = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                measuredSize = size;
                break;
            case MeasureSpec.AT_MOST:
                measuredSize = size;
                break;
            default:
                measuredSize = defaultSize;
                break;
        }
        return measuredSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        heightItem = mHeight / 4;

        //计算8个圆的位置
        for (int i = 0; i < points.length; i++) {
            points[i] = new CircleRadiusPoint();
            points[i].pointF = new PointF();
            if (i % 2 == 0) {
                points[i].pointF.x = mWidth / 4;
            } else {
                points[i].pointF.x = mWidth * 3 / 4;
            }
        }
        for (int i = 0; i < points.length / 2; i++) {
            points[i].pointF.y = (heightItem * i) + heightItem / 2;
        }
        points[0].pointF.y = heightItem / 2 + DisplayUtils.dp2px(getContext(), 1);
        points[0].radius = heightItem / 3;
        points[1].pointF.y = heightItem / 2 + DisplayUtils.dp2px(getContext(), 1);
        points[1].radius = heightItem / 2;
        points[2].pointF.y = heightItem + heightItem / 2;
        points[2].radius = heightItem / 2;
        points[3].pointF.y = heightItem + heightItem / 2;
        points[3].radius = heightItem / 3;
        points[4].pointF.y = (heightItem * 2) + heightItem / 2;
        points[4].radius = heightItem / 3;
        points[5].pointF.y = (heightItem * 2) + heightItem / 2;
        points[5].radius = heightItem / 2;
        points[6].pointF.y = (heightItem * 3) + heightItem / 2 - DisplayUtils.dp2px(getContext(), 1);
        points[6].radius = heightItem / 2;
        points[7].pointF.y = (heightItem * 3) + heightItem / 2 - DisplayUtils.dp2px(getContext(), 1);
        points[7].radius = heightItem / 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < points.length; i++) {
            canvas.drawCircle(points[i].pointF.x, points[i].pointF.y, points[i].radius, circlePaint);
            float width = normalTextPaint.measureText(contents[i]);
            Paint.FontMetrics fontMetrics = normalTextPaint.getFontMetrics();
            if (currentMode == i) {
                //选中的
                canvas.drawCircle(points[i].pointF.x, points[i].pointF.y, points[i].radius, checkedCirclePaint);
                canvas.drawText(contents[i], points[i].pointF.x - width / 2, points[i].pointF.y + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, checkedTextPaint);
            } else {
                canvas.drawText(contents[i], points[i].pointF.x - width / 2, points[i].pointF.y + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, normalTextPaint);
            }
        }
    }

    private long currentTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchIndex = getTouchIndex(x, y);
                currentTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                float x1 = event.getX();
                float y1 = event.getY();
                if (Math.abs(x - x1) <= 10 && Math.abs(y - y1) <= 0) {
                    if ((System.currentTimeMillis() - currentTime) < 500) {
                        if (touchIndex != -1 && currentMode != touchIndex) {
                            currentMode = touchIndex;
                            invalidate();
                            if (mOnModeCheckListener != null) {
                                mOnModeCheckListener.onModeChecked(currentMode);
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    private int getTouchIndex(float x, float y) {
        for (int i = 0; i < points.length; i++) {
            PointF pointF = points[i].pointF;
            if (x > (pointF.x - points[i].radius) && x < (pointF.x + points[i].radius) && y > (pointF.y - points[i].radius) && y < (pointF.y + points[i].radius)) {
                return i;
            }
        }
        return -1;
    }

    public interface OnModeCheckListener {
        void onModeChecked(int position);
    }
}
