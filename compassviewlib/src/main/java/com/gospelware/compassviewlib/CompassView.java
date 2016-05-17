package com.gospelware.compassviewlib;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by ricogao on 17/05/2016.
 */
public class CompassView extends View {

    private float centerX, centerY;
    private Paint circlePaint, pointerPaint, ringPaint, shaderPaint;
    private Shader shader;

    private float strokeWidth;

    private int ringColor;
    private int circleColor;

    private float circleRadius;
    private float offset;
    private boolean showRing;

    private Bitmap pointerBitmap;

    private int rotation;

    private Animator scanAnimator;
    private int scan;


    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompassView);

        float density = getResources().getDisplayMetrics().density;
        strokeWidth = 5 * density;
        offset = 8 * density;

        try {
            circleColor = a.getColor(R.styleable.CompassView_circleColor, Color.BLACK);
            ringColor = a.getColor(R.styleable.CompassView_ringColor, Color.WHITE);
            showRing = a.getBoolean(R.styleable.CompassView_showRing, true);
            rotation = a.getInt(R.styleable.CompassView_pointerRotation, 0);
            int bitmapId = a.getResourceId(R.styleable.CompassView_pointerDrawable, 0);
            pointerBitmap = BitmapFactory.decodeResource(getContext().getResources(), bitmapId);
        } finally {
            a.recycle();
        }

        init();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        circleRadius = centerX;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setColor(ringColor);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(strokeWidth);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);

        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint.setColor(ringColor);


        shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaderPaint.setColor(ringColor);


        initScanAnimator();
    }

    private void initScanAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scan", 0, 360);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        scanAnimator = animator;
    }

    private void setScan(int degree) {
        this.scan = degree;
        invalidate();
    }

    public void startScan() {
        if (scanAnimator != null && !scanAnimator.isRunning()) {
            scanAnimator.start();
        }
    }

    public void stopScan() {
        if (scanAnimator != null && scanAnimator.isRunning()) {
            scanAnimator.end();
        }
    }


    public void setRingColor(int color) {
        this.ringColor = color;

        ringPaint.setColor(ringColor);
        pointerPaint.setColor(ringColor);

        shaderPaint.setColor(ringColor);
        shader = new SweepGradient(centerX, centerY, Color.TRANSPARENT, ringColor);
        shaderPaint.setShader(shader);

        invalidate();
        requestLayout();
    }

    public void setCircleColor(int color) {
        this.circleColor = color;
        circlePaint.setColor(color);
        invalidate();
        requestLayout();
    }


    public boolean isShowRing() {
        return showRing;
    }

    public void setShowRing(boolean showRing) {
        this.showRing = showRing;
        invalidate();
        requestLayout();
    }

    public void setPointerDrawable(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        if (bitmap != null) {
            this.pointerBitmap = bitmap;
            invalidate();
            requestLayout();
        }
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        invalidate();
        requestLayout();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        drawCircle(canvas);

        if (showRing) {
            drawRing(canvas);
        }

        if (scanAnimator != null && scanAnimator.isRunning()) {
            drawScan(canvas);
        } else {
            drawPointer(canvas);
        }

        super.onDraw(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint);
    }

    private void drawRing(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, circleRadius - offset, ringPaint);
    }

    private void drawScan(Canvas canvas) {

        if (shader == null) {
            shader = new SweepGradient(centerX, centerY, Color.TRANSPARENT, ringColor);
            shaderPaint.setShader(shader);
        }

        canvas.save();
        canvas.rotate(scan, centerX, centerY);
        canvas.drawCircle(centerX, centerY, circleRadius, shaderPaint);
        canvas.restore();
    }

    private void drawPointer(Canvas canvas) {

        canvas.save();
        canvas.rotate(rotation, centerX, centerY);

        if (pointerBitmap != null) {
            float scale = 4 * offset / pointerBitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.preTranslate(centerX - (pointerBitmap.getWidth() / 2), 2 * offset);
            matrix.preScale(scale, scale);
            canvas.drawBitmap(pointerBitmap, matrix, pointerPaint);
        } else {
            canvas.drawCircle(centerX, 3 * offset, offset, pointerPaint);
        }

        canvas.restore();
    }


}
