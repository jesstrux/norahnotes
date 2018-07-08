package akil.co.tz.notetaker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

public class MemoProgressView extends View {
    private final Paint drawPaint;
    private final Paint strokePaint;
    private       float size;
    private       String title;

    public MemoProgressView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor("#DDDDDD"));
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setAntiAlias(true);

        strokePaint = new Paint();
        strokePaint.setColor(Color.parseColor("#FFA500"));
        DashPathEffect dashPath = new DashPathEffect(new float[]{15,15}, (float)1.0);
        strokePaint.setPathEffect(dashPath);
        strokePaint.setStrokeWidth(10);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);

        setOnMeasureCallback();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(size, size, size, drawPaint);
        canvas.drawCircle(size, size, size, strokePaint);
    }

    private void setOnMeasureCallback() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(this);
                size = (getMeasuredWidth() / 2) - 20;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
