package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.view.View;

/**
 * Created by abisoyeoke-lawal on 6/10/17.
 */

public class CustomClickListener {
    /** Interface for Item Click over Recycler View Items **/
    public interface OnItemClickListener {
        void OnClick(View view, int position);
    }
}
