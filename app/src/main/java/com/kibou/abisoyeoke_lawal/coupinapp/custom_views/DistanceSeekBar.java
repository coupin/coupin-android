package com.kibou.abisoyeoke_lawal.coupinapp.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 11/2/17.
 */

public class DistanceSeekBar extends AppCompatSeekBar {
    private int mThumbSize;
    private TextPaint mTextPaint;

    public DistanceSeekBar(Context context) {
        super(context);

        init();
    }

    public DistanceSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.seekBarStyle);

        init();
    }

    public DistanceSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumbSize);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(getResources().getColor(android.R.color.white));
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.small_text));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        String progressText = getProgress() / 10 + "Km";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
        float thumbY = getHeight() / 2f + bounds.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
    }
}
