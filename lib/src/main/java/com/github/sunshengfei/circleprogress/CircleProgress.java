package com.github.sunshengfei.circleprogress;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fred on 2016/9/9.
 */
public class CircleProgress extends View {

    /**
     * the foreground progress
     */
    private float progress = 0;

    /**
     * the foreground progress of Loading
     */
    private int progressLoading = 0;//0~100

    /**
     * the foreground progress stroke line-width
     */
    private float strokeWidth = 3f;

    /**
     * the background progress stroke line-width
     */
    private float backgroundStrokeWidth = 2f;

    /**
     * the foreground progress stroke line-color
     */
    private int color = Color.BLACK;
    private int fillcolor = Color.BLACK;
    /**
     * the background progress stroke line-color
     */
    private int backgroundColor = Color.GRAY;

    /**
     * default start angle
     */
    private final static int START_ANGLE = -90;
    private int gultter = 1;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;

    private Paint LoadingPaint;
    private RectF rectFLoading;

    public boolean fill,outline;

    private ObjectAnimator objectAnimator;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CirclePlayBar, 0, 0);
        //Reading values from the XML layout
        try {
            // Value
            progress = typedArray.getFloat(R.styleable.CirclePlayBar_cpb_progress, progress);
            gultter = typedArray.getDimensionPixelSize(R.styleable.CirclePlayBar_cpb_background_cpb_gutter, gultter);
            progressLoading = typedArray.getInt(R.styleable.CirclePlayBar_cpb_progress_loading, progressLoading);
            fill = typedArray.getBoolean(R.styleable.CirclePlayBar_cpb_fill, false);
            outline = typedArray.getBoolean(R.styleable.CirclePlayBar_cpb_outline, true);
            // StrokeWidth
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CirclePlayBar_cpb_progressbar_width, (int) strokeWidth);
            backgroundStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CirclePlayBar_cpb_background_progressbar_width, (int) backgroundStrokeWidth);
            // Color
            fillcolor = typedArray.getInt(R.styleable.CirclePlayBar_cpb_progressbar_fillcolor, color);
            color = typedArray.getInt(R.styleable.CirclePlayBar_cpb_progressbar_color, color);
            backgroundColor = typedArray.getInt(R.styleable.CirclePlayBar_cpb_background_progressbar_color, backgroundColor);
        } finally {
            typedArray.recycle();
        }

        // Init Background
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);

        // Init Foreground
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);

        // Loading Foreground
        LoadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LoadingPaint.setColor(fillcolor);
        LoadingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        LoadingPaint.setStrokeWidth(backgroundStrokeWidth);

        rectFLoading = new RectF();
        setProgressLoading(progressLoading);
    }

    private void setFill(boolean fill) {
        this.fill = fill;
        invalidate();
    }
    //endregion

    //region Draw Method
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (outline){
            canvas.drawOval(rectF, backgroundPaint);
            float angle = 360 * progress / 100;
            canvas.drawArc(rectF, START_ANGLE, angle, false, foregroundPaint);
        }
        float loadingSweepAngle = progressLoading * 360F / 100F;
        rectFLoading.set(rectF);
        rectFLoading.inset(gultter, gultter);
        if (fill && loadingSweepAngle != 0)
            canvas.drawArc(rectFLoading, START_ANGLE, loadingSweepAngle, true, LoadingPaint);
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = Math.max(strokeWidth, backgroundStrokeWidth);
        rectF.set(0, 0, min, min);
        rectF.inset(highStroke, highStroke);
    }
    //endregion

    //region Method Get/Set
    public void setProgress(float progress) {
        this.progress = progress < 0 ? 0 : (Math.min(progress, 100));
        invalidate();
    }

    /**
     * @param progress 0 ~ 360
     */
    public void setProgressLoading(int progress) {
        this.progressLoading = progress < 0 ? 0 : (Math.min(progress, 100));
        invalidate();
    }

    private void removeAnimation() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
            objectAnimator = null;
        }
    }


    public float getProgressBarWidth() {
        return strokeWidth;
    }

    public void setProgressBarWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        foregroundPaint.setStrokeWidth(strokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        foregroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }
    //endregion

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAnimation();
    }
}