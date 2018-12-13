package com.growatt.shinephone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by Administrator on 2018/9/6.
 */

public class AutoFitTextViewTwo extends AppCompatTextView {
    private float mDefaultTextSize;
    private Paint mTextPaint;
    private float mMinTextSize;

    public AutoFitTextViewTwo(Context context) {
        this(context, null);
    }

    private void initAttr() {
        mTextPaint = new Paint();
        mTextPaint.set(getPaint());
        mDefaultTextSize = getTextSize();
        mMinTextSize = 5;
    }

    public AutoFitTextViewTwo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextViewTwo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
//        refitText(text.toString(),getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refitText(getText().toString(), getWidth(), getHeight());
    }

    public void refitText(String text, int textWidth, int textHeight) {
        if (textWidth > 0) {
            int availableTextWidth = textWidth - getPaddingLeft() - getPaddingRight();
            int availableHeight = textHeight - this.getPaddingBottom() - this.getPaddingTop();
            float mult = 1f;
            float add = 0;
            if (Build.VERSION.SDK_INT > 16) {
                mult = getLineSpacingMultiplier();
                add = getLineSpacingExtra();
            } else {
                //the mult default is 1.0f,if you need change ,you can reflect invoke this field;
            }
            float tsTextSize = mDefaultTextSize;
            mTextPaint.setTextSize(tsTextSize);
            float length = mTextPaint.measureText(text);
            int height = round(mTextPaint.getFontMetricsInt(null) * mult + add);
            while (length > availableTextWidth || height > availableHeight) {
                tsTextSize--;
                mTextPaint.setTextSize(tsTextSize);
                length = mTextPaint.measureText(text);
                height = round(mTextPaint.getFontMetricsInt(null) * mult + add);
                if (tsTextSize < mMinTextSize) break;
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, tsTextSize);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    //FastMath.round()
    public static int round(float value) {
        long lx = (long) (value * (65536 * 256f));
        return (int) ((lx + 0x800000) >> 24);
    }
}
