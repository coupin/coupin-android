package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatSeekBar;
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
        super(context, attrs);

        init();
    }

    public DistanceSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumbSize);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(getResources().getColor(R.color.colorAccent));
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.small_text));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        String progressText = String.valueOf(getProgress()/10) + "Km";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding * 2 + thumbOffset;
        float thumbY = getHeight() / 2f + bounds.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint);

        setThumbOffset(getThumbOffset() + leftPadding);
    }
}
