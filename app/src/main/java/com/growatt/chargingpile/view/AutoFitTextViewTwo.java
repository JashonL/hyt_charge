package com.growatt.chargingpile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    public AutoFitTextViewTwo(Context context) {
        this(context ,null);
    }

    private void initAttr() {
        mTextPaint = new Paint();
        mTextPaint.set(getPaint());
        mDefaultTextSize = getTextSize();
    }

    public AutoFitTextViewTwo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
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
        refitText(getText().toString(),getWidth());
    }

    public void refitText(String text, int textWidth){
        if(textWidth > 0){
            int availableTextWidth = textWidth - getPaddingLeft() - getPaddingRight();
            float tsTextSize = mDefaultTextSize;
            mTextPaint.setTextSize(tsTextSize);
            float length = mTextPaint.measureText(text);
            while (length > availableTextWidth) {
                tsTextSize--;
                mTextPaint.setTextSize(tsTextSize);
                length = mTextPaint.measureText(text);
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX,tsTextSize);
            invalidate();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
