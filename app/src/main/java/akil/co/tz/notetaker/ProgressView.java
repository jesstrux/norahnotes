package akil.co.tz.notetaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {

    Paint paint;
    Path path;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();
        path.moveTo(50, 50);
        path.lineTo(50, 500);
        path.lineTo(200, 500);
        path.lineTo(200, 300);
        path.lineTo(350, 300);

        float[] intervals = new float[]{50.0f, 20.0f};
        float phase = 0;

        DashPathEffect dashPathEffect =
                new DashPathEffect(intervals, phase);

        paint.setPathEffect(dashPathEffect);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);

    }

}