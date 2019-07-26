package com.growatt.chargingpile.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

/**
 * Created by Administrator on 2019/7/1.
 */

public class SpanableStringUtils {

    private SpanableStringUtils() {

    }


    public static Builder getSpanableBuilder(String text) {
        return new Builder(text);
    }

    public static class Builder {
        private String text;
        //颜色
        private int mTextColor = -1;
        //大小
        private int mTextSize = -1;
        //是否加粗
        private boolean isBold = false;
        private SpannableStringBuilder mBuilder;
        private int flag;
        //设置比例
        private float proportion = -1;

        public Builder(String text) {
            this.text = text;
            mBuilder = new SpannableStringBuilder();
            flag = SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE;
        }


        public Builder setmTextColor(int textColor) {
            this.mTextColor = textColor;
            return this;
        }

        public Builder setmTextSize(int textSize) {
            this.mTextSize = textSize;
            return this;
        }

        public Builder append(String text) {
            setSpan();
            this.text = text;
            return this;
        }

        public SpannableStringBuilder create() {
            setSpan();
            return mBuilder;
        }

        public Builder setBold(boolean bold) {
            this.isBold = bold;
            return this;
        }

        public Builder setProportion(float proportion) {
            this.proportion = proportion;
            return this;
        }

        private void setSpan() {
            int start = mBuilder.length();
            mBuilder.append(this.text);
            int end = mBuilder.length();

            if (mTextColor != -1) {
                mBuilder.setSpan(new ForegroundColorSpan(mTextColor), start, end, flag);
                mTextColor = -1;
            }

            if (mTextSize != -1) {
                mBuilder.setSpan(new AbsoluteSizeSpan(mTextSize), start, end, flag);
                mTextSize = -1;
            }

            if (isBold) {
                mBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, flag);
                isBold = false;
            }
            if (proportion != -1) {
                mBuilder.setSpan(new RelativeSizeSpan(proportion), start, end, flag);
                proportion = -1;
            }
        }

    }
}
