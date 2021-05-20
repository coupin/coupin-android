package com.kibou.abisoyeoke_lawal.coupinapp.custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 8/12/17.
 */

public class StrikeThroughTextView extends androidx.appcompat.widget.AppCompatTextView {
    public Context context;
    public int dividerColor;
    private Paint paint;

    public StrikeThroughTextView(Context context) {
        super(context);
//        init(context);
        this.context = context;
    }

    public StrikeThroughTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StrikeThroughTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Resources resources = context.getResources();
        //set strikethrough color
        dividerColor = resources.getColor(R.color.text_lighter_grey);
        paint = new Paint();
        paint.setColor(dividerColor);
        paint.setStrikeThruText(true);
        float height = getHeight();
        float width = getWidth();
        canvas.drawLine(width / 10, height /10, (width - width / 10), (height - height / 10), paint);
    }
}
