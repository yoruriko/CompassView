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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by ricogao on 17/05/2016.
 */
public class CompassView extends View {
    private static final String TAG = "CompassView";

    private float centerX, centerY;
    private Paint circlePaint, pointerPaint, ringPaint, shaderPaint;
    private Shader shader;

    private float strokeWidth;

    private int ringColor;
    private int circleColor;

    private float circleRadius;
    private float offset;
    private boolean showRing;

    private int compassSnapInterval;

    private Bitmap pointerBitmap;

    private int rotation;

    private Animator scanAnimator;
    private int scan;

    private OnRotationChangeListener rotationChanged;


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
            compassSnapInterval = a.getInt(R.styleable.CompassView_compassSnapInterval, 5);
            // Have to do some special validation here...
            if (compassSnapInterval < 0 || compassSnapInterval > 180) compassSnapInterval = 5;
        } finally {
            a.recycle();
        }

        init();

    }

    private float centerRawX, centerRawY;
    private void getRawCenterLocation(float centerX, float centerY) {
        int rawLoc[] = new int[2];
        getLocationOnScreen(rawLoc);
        centerRawX = rawLoc[0] + centerX;
        centerRawY = rawLoc[1] + centerY;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        circleRadius = centerX;

        getRawCenterLocation(centerX, centerY);

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

    public boolean isScanning(){
        return scanAnimator!=null&&scanAnimator.isRunning();
    }

    public void startScan() {
        if (scanAnimator != null && !scanAnimator.isRunning()) {
            scanAnimator.start();
        }
    }

    public void stopScan() {
        if (isScanning()) {
            scanAnimator.end();
        }
    }

    public void setRotationChangedListener(OnRotationChangeListener rotationChanged) {
        this.rotationChanged = rotationChanged;
    }

    public void setRingColor(int color) {
        this.ringColor = color;

        ringPaint.setColor(ringColor);
        pointerPaint.setColor(ringColor);

        shaderPaint.setColor(ringColor);
        shader = new SweepGradient(centerX, centerY, Color.TRANSPARENT, ringColor);
        shaderPaint.setShader(shader);

        invalidate();
//        requestLayout();
    }

    public void setCircleColor(int color) {
        this.circleColor = color;
        circlePaint.setColor(color);
        invalidate();
//        requestLayout();
    }


    public boolean isShowRing() {
        return showRing;
    }

    public void setShowRing(boolean showRing) {
        this.showRing = showRing;
        invalidate();
//        requestLayout();
    }

    public void setPointerDrawable(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        if (bitmap != null) {
            this.pointerBitmap = bitmap;
            invalidate();
//            requestLayout();
        }
    }

    public void setRotation(int rotation) {
        int oldRotation = Math.round(getRotation());

        this.rotation = rotation;

        if (this.rotationChanged != null) this.rotationChanged.rotationChanged(oldRotation, rotation);

        invalidate();
//        requestLayout();
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
            // Account for how the image will be scaled, when deciding where to place it
            matrix.preTranslate(centerX - ((scale * pointerBitmap.getWidth()) / 2), 2 * offset);
            matrix.preScale(scale, scale);
            canvas.drawBitmap(pointerBitmap, matrix, pointerPaint);
        } else {
            canvas.drawCircle(centerX, 3 * offset, offset, pointerPaint);
        }

        canvas.restore();
    }

    /** Detect when view is touched; reposition pointer to corresponding rotation */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float touchX = event.getRawX(), touchY = event.getRawY();
            float adjacent = touchY - centerRawY, opposite = touchX - centerRawX;
            double thetaRad = Math.atan2(adjacent, opposite);
            double thetaDeg = Math.toDegrees(thetaRad);
            float rotationCorr = (float) (thetaDeg + 360 + 90) % 360;
            int rotation = compassSnapInterval * (Math.round(rotationCorr/compassSnapInterval));
            if (rotation == 360) rotation = 0; // No need to have two values for due north
            Log.d(TAG, "touch x: " + event.getRawX() + ", touch y: " + event.getRawY() + ", center x: " + centerRawX + ", center y: " + centerRawY + ", rotation: " + rotation);
            setRotation(rotation);
        }
        return true;
    }

}
