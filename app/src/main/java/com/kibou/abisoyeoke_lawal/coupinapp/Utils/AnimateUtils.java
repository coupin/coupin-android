package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;

/**
 * Created by abisoyeoke-lawal on 6/11/17.
 */

public class AnimateUtils {
    public static int ANIMATION_DURATION_SHORT = 150;
    public static int ANIMATION_DURATION_MEDIUM = 400;
    public static int ANIMATION_DURATION_LONG = 800;

    public interface AnimationListener {
        boolean onAnimationStart(View view);
        boolean onAnimationEnd(View view);
        boolean onAnimationCancel(View view);
    }


    public static void crossFadeViews(View showView, View hideView) {
        crossFadeViews(showView, hideView, ANIMATION_DURATION_SHORT);
    }

    public static void crossFadeViews(View showView, final View hideView, int duration) {
        fadeInView(showView, duration);
        fadeOutView(hideView, duration);
    }

    public static void fadeInView(View view) {
        fadeInView(view, ANIMATION_DURATION_SHORT);
    }

    public static void fadeInView(View view, int duration) {
        fadeInView(view, duration, null);
    }

    public static void fadeInView(View view, int duration, final AnimationListener listener) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);

        ViewPropertyAnimatorListener viewPropertyAnimatorListener = null;

        if (listener != null) {
            viewPropertyAnimatorListener = new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    if (!listener.onAnimationStart(view)) {
                        view.setDrawingCacheEnabled(true);
                    };
                }

                @Override
                public void onAnimationEnd(View view) {
                    if (!listener.onAnimationEnd(view)) {
                        view.setDrawingCacheEnabled(false);
                    }
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            };
        }

        ViewCompat.animate(view).alpha(1f).setDuration(duration).setListener(viewPropertyAnimatorListener);
    }

    public static void fadeOutView(View view) {
        fadeOutView(view, ANIMATION_DURATION_SHORT);
    }

    public static void fadeOutView(View view, int duration) {
        fadeOutView(view, duration, null);
    }

    public static void fadeOutView(View view, int duration, final AnimationListener listener) {
        ViewCompat.animate(view).alpha(0f).setDuration(duration).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                if (listener == null || !listener.onAnimationEnd(view)) {
                    view.setDrawingCacheEnabled(true);
                }
            }

            @Override
            public void onAnimationEnd(View view) {
                if (listener == null || !listener.onAnimationEnd(view)) {
                    view.setDrawingCacheEnabled(false);
                }
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        });
    }


}
