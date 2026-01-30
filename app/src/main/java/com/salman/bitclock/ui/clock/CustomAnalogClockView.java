
package com.salman.bitclock.ui.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class CustomAnalogClockView extends View {

    private Paint circlePaint;
    private Paint hourHandPaint;
    private Paint minuteHandPaint;
    private Paint secondHandPaint;
    private Paint centerPaint;

    private int centerX;
    private int centerY;
    private int radius;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(this, 1000);
        }
    };

    public CustomAnalogClockView(Context context) {
        super(context);
        init();
    }

    public CustomAnalogClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAnalogClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(16f);

        hourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourHandPaint.setColor(Color.BLACK);
        hourHandPaint.setStrokeWidth(12f);
        hourHandPaint.setStrokeCap(Paint.Cap.ROUND);

        minuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minuteHandPaint.setColor(Color.BLACK);
        minuteHandPaint.setStrokeWidth(8f);
        minuteHandPaint.setStrokeCap(Paint.Cap.ROUND);

        secondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondHandPaint.setColor(Color.RED);
        secondHandPaint.setStrokeWidth(4f);
        secondHandPaint.setStrokeCap(Paint.Cap.ROUND);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.BLACK);
        centerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) / 2 - 40;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw clock face
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // Draw hour hand
        float hourAngle = (hour + minute / 60.0f) * 30.0f - 90;
        drawHand(canvas, hourAngle, radius * 0.5f, hourHandPaint);

        // Draw minute hand
        float minuteAngle = (minute + second / 60.0f) * 6.0f - 90;
        drawHand(canvas, minuteAngle, radius * 0.7f, minuteHandPaint);

        // Draw second hand
        float secondAngle = second * 6.0f - 90;
        drawHand(canvas, secondAngle, radius * 0.9f, secondHandPaint);

        // Draw center circle
        canvas.drawCircle(centerX, centerY, 12, centerPaint);
    }

    private void drawHand(Canvas canvas, float angle, float length, Paint paint) {
        float angleInRad = (float) Math.toRadians(angle);
        float endX = centerX + (float) (Math.cos(angleInRad) * length);
        float endY = centerY + (float) (Math.sin(angleInRad) * length);
        canvas.drawLine(centerX, centerY, endX, endY, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(runnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable);
    }
}
