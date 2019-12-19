package com.zkl.bezier;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * @author Zhangkelu
 * @date 2019/11/29 14:03
 * @Description:
 */
public class EquipartitionTextView extends View {

    private Paint mPaint;
    private int mWidth;
    private String[] texts = {"20HZ", "110HZ", "630HZ", "3.5kHZ", "20kHZ"};
    private PointF[] points;

    public EquipartitionTextView(Context context) {
        this(context, null);
    }

    public EquipartitionTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EquipartitionTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.yellow));
        mPaint.setTextSize(DisplayUtils.dp2px(getContext(), 16));

        points = new PointF[texts.length];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, DisplayUtils.getScreenWidth(getContext())), measureView(heightMeasureSpec, DisplayUtils.dp2px(getContext(), 25)));
    }

    private int measureView(int measureSpec, int defaultSize) {
        int measuredSize;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
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
        for (int i = 0; i < texts.length; i++) {
            points[i] = new PointF(mWidth / 6 * (i+1), h / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < texts.length; i++) {
            float width = mPaint.measureText(texts[i]);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            canvas.drawText(texts[i], points[i].x - width / 2, points[i].y + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, mPaint);
        }
    }
}
