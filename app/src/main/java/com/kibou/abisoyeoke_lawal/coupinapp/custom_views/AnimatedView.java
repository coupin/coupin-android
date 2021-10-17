package com.kibou.abisoyeoke_lawal.coupinapp.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 5/20/17.
 */

public class AnimatedView extends androidx.appcompat.widget.AppCompatImageView {

    private final Context mContext;
    int x = -1;
    int y = -1;
    private final int xVelocity = 10;
    private  int yVelocity = 5;
    private final Handler h;
    private final int FRAME_RATE = 30;

    public AnimatedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mContext = context;
        h = new Handler();
    }

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    protected void onDraw(Canvas c) {
        BitmapDrawable ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_clogo);

        if ( x < 0 && y < 0) {
            x = this.getWidth()/2;
            y = this.getHeight()/2;
        } else {
//            x += xVelocity;
            y += yVelocity;

//            if ((x > this.getWidth() - ball.getBitmap().getWidth()) || x < 0) {
//                xVelocity = xVelocity *  -1;
//            }

            if ((y > this.getHeight() - ball.getBitmap().getHeight() + 20) || y < 0) {
                yVelocity = yVelocity *  -1;
            }
        }

        c.drawBitmap(ball.getBitmap(), x, y, null);
        h.postDelayed(r, FRAME_RATE);
    }

}
